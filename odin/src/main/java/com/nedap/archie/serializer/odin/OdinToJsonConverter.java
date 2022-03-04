package com.nedap.archie.serializer.odin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.nedap.archie.adlparser.antlr.odinParser.*;
import org.apache.commons.text.StringEscapeUtils;


import java.util.List;

/**
 * A simple Odin to JSON-converter, not bound to ADL syntax and lexer rules.
 *
 * We need different lexer modes, but this is very hard to do in the current version of ADL!
 *
 * Perhaps better would be to create a jackson-databinding for ODIN, but this is quite some work and I don't consider
 * ODIN as having considerable benefits over JSON or YAML.
 *
 * If anyone wants to do this - go ahead.
 *
 * Created by pieter.bos on 01/11/15.
 */
public class OdinToJsonConverter {

    public static final String TYPE_PROPERTY_NAME = "_type";
    private static ObjectMapper objectMapper = new ObjectMapper();
    private     StringBuilder output = new StringBuilder();

    static {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //keywords = <"value"> is indistinguishable from keywords = <"value1", "value2">
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }


    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String convert(Odin_textContext context) {
        if(context == null) {
            return "{}";
        }
        if (context.attr_vals() != null) {
            output(context.attr_vals().attr_val(), null /* no type id here */);
        } else if(context.object_value_block() != null){
            output(context.object_value_block());
        } else if (context.keyed_object() != null && context.keyed_object().size() > 0) {
            outputKeyedObjects(context.keyed_object(), null /* no type id here */);
        } else {
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
        outputEscaped(TYPE_PROPERTY_NAME);
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
        } else if (valueBlockContext.EMBEDDED_URI() != null) {
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
                            output.append(",\"lower\": " + interval.integer_value().get(0).getText());
                            if(relopText.contains("=")) {
                                output.append(",\"lower_included\": \"true\"");
                            } else {
                                output.append(",\"lower_included\": \"false\"");
                            }
                        } else if(relopText.contains("<")) {
                            output.append(",\"lower_unbounded\": \"true\"");
                            output.append(",\"upper_unbounded\": \"false\"");
                            output.append(",\"upper\": " + interval.integer_value().get(0).getText());
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
                        output.append(",\"lower\": " + interval.integer_value().get(0).getText());
                        output.append(",\"upper\": " + interval.integer_value().get(1).getText());

                    }

                } else if (intervalCtx.real_interval_value() != null) {
                    Real_interval_valueContext interval = intervalCtx.real_interval_value();
                    if(interval.relop() != null) {
                        String relopText = interval.relop().getText();
                        if(relopText.contains(">")) {
                            output.append(",\"lower_unbounded\": \"false\"");
                            output.append(",\"upper_unbounded\": \"true\"");
                            output.append(",\"lower\": " + interval.real_value().get(0).getText());
                            if(relopText.contains("=")) {
                                output.append(",\"lower_included\": \"true\"");
                            } else {
                                output.append(",\"lower_included\": \"false\"");
                            }
                        } else if(relopText.contains("<")) {
                            output.append(",\"lower_unbounded\": \"true\"");
                            output.append(",\"upper_unbounded\": \"false\"");
                            output.append(",\"upper\": " + interval.real_value().get(0).getText());
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
                        output.append(",\"lower\": " + interval.real_value().get(0).getText());
                        output.append(",\"upper\": " + interval.real_value().get(1).getText());

                    }
                } else if(intervalCtx.date_time_interval_value() != null) {

                } else if(intervalCtx.time_interval_value() != null) {

                }
                output.append("}");
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
            //jackson expects true and false, not TruE or trUe like valid in odin
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
        } catch (JsonProcessingException e) {
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOutput() {
        return output.toString();
    }
}
