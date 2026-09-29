package com.nedap.archie.serializer.odin;

import tools.jackson.core.JsonParser;
import com.nedap.archie.adlparser.antlr.AdlParser.*;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.DeserializationProblemHandler;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.core.StreamReadFeature;

/** Converts ODIN in ADL to JSON with Jackson 3. */
public class AdlOdinToJsonConverter3 {

    public static final String TYPE_PROPERTY_NAME = "_type";
    private static final ObjectMapper objectMapper;
    private final StringBuilder output = new StringBuilder();

    static {
        JsonMapper.Builder builder = JsonMapper.builder();
        configureBuilder(builder, false);
        objectMapper = builder.build();
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static void configureBuilder(MapperBuilder<?, ?> objectMapper, boolean allowDuplicates) {
        objectMapper.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //keywords = <"value"> is indistinguishable from keywords = <"value1", "value2">
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        //odin sometimes does <> where it can mean either an empty array OR a null object. Nastyness
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        if(!allowDuplicates) {
            objectMapper.enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION);
        } else {
            objectMapper.disable(StreamReadFeature.STRICT_DUPLICATE_DETECTION);
        }

        //ignore the type field when not needed
        objectMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, ValueDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
                if (propertyName.equalsIgnoreCase(TYPE_PROPERTY_NAME)) {
                    return true;
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });

    }

    public String convert(Odin_textContext context) {
        if(context == null) {
            return "{}";
        }
        if (context.attr_vals() != null) {
            output(context.attr_vals().attr_val(), null /* no type id here */);
        } else if(context.object_value_block() != null){
            output(context.object_value_block());
        } else if (context.keyed_object() != null && !context.keyed_object().isEmpty()) {
            outputKeyedObjects(context.keyed_object(), null /* no type id here */);
        } else{
            //empty
            return "{}";
        }
        return output.toString();

    }

    private void output(List<Attr_valContext> context, Type_idContext type_idContext) {
        output.append("{");
        boolean first = true;
        if(type_idContext != null) {
            first = false;
            outputTypeId(type_idContext);
        }
        for (Attr_valContext attrValContext : context) {
            if(!first) {
                output.append(',');
            }
            first = false;
            output.append('"');
            output.append(attrValContext.odin_object_key().getText());
            output.append('"');
            output.append(':');
            output(attrValContext.object_block());
        }
        output.append("}");
    }

    private void outputTypeId(Type_idContext type_idContext) {
        outputEscaped("_type");
        output.append(":");
        outputEscaped(type_idContext.getText());//we might need to remove the generics from the type id if present
    }

    private void output(Object_blockContext context) {
        Object_value_blockContext valueBlockContext = context.object_value_block();
        if (context.object_reference_block() != null) {
            //WARN: not supported. not needed for adls?
        } else if (valueBlockContext != null) {
            output(valueBlockContext);
        } else {
            output.append("{}");
        }
    }

    private void output(Object_value_blockContext valueBlockContext) {
        List<Keyed_objectContext> keyedObjectContexts = valueBlockContext.keyed_object();
        Primitive_objectContext primitiveObjectContext = valueBlockContext.primitive_object();
        if (valueBlockContext.attr_vals() != null) {
            output(valueBlockContext.attr_vals().attr_val(), valueBlockContext.type_id());
        } else if (keyedObjectContexts != null && !keyedObjectContexts.isEmpty()) {
            outputKeyedObjects(keyedObjectContexts, valueBlockContext.type_id());
        }  else if (valueBlockContext.EMBEDDED_URI() != null) {
            output.append("\"");
            output.append(OdinEmbeddedUriParser.parseEmbeddedUri(valueBlockContext.EMBEDDED_URI().getText()));
            output.append("\"");
        } else if (primitiveObjectContext != null) {
            if(primitiveObjectContext.primitive_value() != null) {
                output(primitiveObjectContext.primitive_value());
            } else if (primitiveObjectContext.primitive_list_value() != null) {
                //json array
                Primitive_list_valueContext listContext = primitiveObjectContext.primitive_list_value();
                output(listContext);

            } else {
                output.append("{ \"_type\": \"INTERVAL\" ");
                Primitive_interval_valueContext intervalCtx = primitiveObjectContext.primitive_interval_value();

                if(intervalCtx.date_interval_value() != null) {

                } else if(intervalCtx.duration_interval_value() != null) {

                } else if (intervalCtx.integer_interval_value() != null) {
                    Integer_interval_valueContext interval = intervalCtx.integer_interval_value();
                    if(interval.relop() != null) {
                        String relopText = interval.relop().getText();
                        if(relopText.contains(">")) {
                            output.append(",\"lower_unbounded\": \"false\"");
                            output.append(",\"upper_unbounded\": \"true\"");
                            output.append(",\"lower\": ").append(interval.integer_value().get(0).getText());
                            if(relopText.contains("=")) {
                                output.append(",\"lower_included\": \"true\"");
                            } else {
                                output.append(",\"lower_included\": \"false\"");
                            }
                        } else if(relopText.contains("<")) {
                            output.append(",\"lower_unbounded\": \"true\"");
                            output.append(",\"upper_unbounded\": \"false\"");
                            output.append(",\"upper\": ").append(interval.integer_value().get(0).getText());
                            if(relopText.contains("=")) {
                                output.append(",\"upper_included\": \"true\"");
                            } else {
                                output.append(",\"upper_included\": \"false\"");
                            }
                        }
                    } else {
                        output.append(",\"lower_unbounded\": \"false\"");
                        output.append(",\"upper_unbounded\": \"false\"");
                        if(interval.SYM_GT() != null) {
                            output.append(",\"lower_included\": \"false\"");
                        } else {
                            output.append(",\"lower_included\": \"true\"");
                        }
                        if(interval.SYM_LT() != null) {
                            output.append(",\"upper_included\": \"false\"");
                        } else {
                            output.append(",\"upper_included\": \"true\"");
                        }
                        output.append(",\"lower\": ").append(interval.integer_value().get(0).getText());
                        if(interval.integer_value().size() > 1) {
                            output.append(",\"upper\": ").append(interval.integer_value().get(1).getText());
                        } else {
                            output.append(",\"upper\": ").append(interval.integer_value().get(0).getText());
                        }

                    }

                } else if (intervalCtx.real_interval_value() != null) {
                    Real_interval_valueContext interval = intervalCtx.real_interval_value();
                    if(interval.relop() != null) {
                        String relopText = interval.relop().getText();
                        if(relopText.contains(">")) {
                            output.append(",\"lower_unbounded\": \"false\"");
                            output.append(",\"upper_unbounded\": \"true\"");
                            output.append(",\"lower\": ").append(interval.real_value().get(0).getText());
                            if(relopText.contains("=")) {
                                output.append(",\"lower_included\": \"true\"");
                            } else {
                                output.append(",\"lower_included\": \"false\"");
                            }
                        } else if(relopText.contains("<")) {
                            output.append(",\"lower_unbounded\": \"true\"");
                            output.append(",\"upper_unbounded\": \"false\"");
                            output.append(",\"upper\": ").append(interval.real_value().get(0).getText());
                            if(relopText.contains("=")) {
                                output.append(",\"upper_included\": \"true\"");
                            } else {
                                output.append(",\"upper_included\": \"false\"");
                            }
                        }
                    } else {
                        output.append(",\"lower_unbounded\": \"false\"");
                        output.append(",\"upper_unbounded\": \"false\"");
                        if(interval.SYM_GT() != null) {
                            output.append(",\"lower_included\": \"false\"");
                        } else {
                            output.append(",\"lower_included\": \"true\"");
                        }
                        if(interval.SYM_LT() != null) {
                            output.append(",\"upper_included\": \"false\"");
                        } else {
                            output.append(",\"upper_included\": \"true\"");
                        }
                        output.append(",\"lower\": ").append(interval.real_value().get(0).getText());

                        if(interval.real_value().size() > 1) {
                            output.append(",\"upper\": ").append(interval.real_value().get(1).getText());
                        } else {
                            output.append(",\"upper\": ").append(interval.real_value().get(0).getText());
                        }

                    }
                } else if(intervalCtx.date_time_interval_value() != null) {

                } else if(intervalCtx.time_interval_value() != null) {

                }
                output.append("}");
                //interval. TODO: implement interval-object notation in json :)
                //interval. TODO: implement interval-object notation in json :)
            }
        } else {
            output.append("[]");
        }
    }

    private void outputKeyedObjects(List<Keyed_objectContext> keyedObjectContexts, Type_idContext type_idContext) {
        output.append("{");
        boolean first = true;
        if(type_idContext != null) {
            first = false;
            outputTypeId(type_idContext);
        }
        for (Keyed_objectContext keyedObjectContext : keyedObjectContexts) {
            if(!first) {
                output.append(',');
            }
            first = false;
            //output.append('"');
            output(keyedObjectContext.primitive_value());
            //output.append('"');
            output.append(':');
            output(keyedObjectContext.object_block());

        }
        output.append("}");
    }

    private void output(Primitive_list_valueContext listContext) {
        List<Primitive_valueContext> primitiveValueContexts = listContext.primitive_value();
        output.append("[");
        boolean first = true;
        for(Primitive_valueContext valueContext:primitiveValueContexts) {
            if (!first) {
                output.append(',');
            }
            first = false;
            output(valueContext);
        }
        output.append("]");
    }

    private void output(Primitive_valueContext context) {
        if (context.date_time_value() != null) {
            outputString(context.getText());
        } else if (context.date_value()!= null) {
            outputString(context.getText());
        } else if (context.duration_value() != null) {
            outputString(context.getText());
        } else if (context.time_value() != null) {
            outputString(context.getText());
        } else if (context.term_code_value() != null) {
            outputString(context.getText());
        } else if (context.boolean_value() != null) {
            //Must be unquoted case insensitive for jackson to not just parse this as false
            if(context.boolean_value().getText().equalsIgnoreCase("true")) {
                output.append("true");
            } else {
                output.append("false");
            }
        } else {
            //json-compatible anyway
            outputEscaped(context.getText());
        }

    }

    private void outputString(String text) {
        try {
            output.append(objectMapper.writeValueAsString(text));
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    private void outputEscaped(String text) {
        try {
            //strip " if present, all the other "-characters will have to be escaped
            if(text.startsWith("\"") && text.endsWith("\"")) {
                String textWithoutQuotationMarks = text.substring(1, text.length()-1);

                String textQuotesReplaced = StringEscapeUtils.unescapeJson(textWithoutQuotationMarks);
                output.append(objectMapper.writeValueAsString(textQuotesReplaced));
            } else {
                output.append(objectMapper.writeValueAsString(text));
            }
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOutput() {
        return output.toString();
    }
}
