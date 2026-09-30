package com.nedap.archie.serializer.adl.constraints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Joiner;
import com.nedap.archie.aom.*;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;
import org.openehr.odin.jackson.ODINMapper;
import org.openehr.odin.jackson.ODINPrettyPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nedap.archie.serializer.adl.ArchetypeSerializeUtils.buildOccurrences;


/**
 * @author Marko Pipan
 */
public class CComplexObjectSerializer<T extends CComplexObject> extends ConstraintSerializer<T> {
    public CComplexObjectSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(T cobj) {
        builder.indent().newline();
        appendSiblingOrder(cobj);
        builder.append(cobj.getRmTypeName());
        if (cobj.getNodeId() != null) {
            builder.append("[").append(cobj.getNodeId()).append("]");
        }
        appendOccurrences(cobj);
        if (cobj.getAttributes().isEmpty() && cobj.getAttributeTuples().isEmpty() && cobj.getDefaultValue() == null) {
            builder.lineComment(serializer.getTermText(cobj));
        } else {
            builder.ensureSpace().append("matches {");
            builder.lineComment(serializer.getTermText(cobj));
            buildAttributesAndTuples(cobj);

            builder.append("}");
        }
        builder.unindent();
    }

    protected void buildAttributesAndTuples(T cobj) {
        builder.indent().newline();
        Set<String> tupleAttributes = getTupleAttributeNames(cobj);

        cobj.getAttributes().stream()
                .filter(a -> !tupleAttributes.contains(a.getRmAttributeName()))
                .forEach(this::buildAttribute);

        cobj.getAttributeTuples().forEach(this::buildTuple);
        buildDefaultValue(cobj);

        builder.unindent().newline();
    }

    protected void buildDefaultValue(T cobj) {
        if(cobj.getDefaultValue() != null) {
            if(cobj.getDefaultValue() instanceof DefaultValueContainer) {
                serializeDefaultValueContainer((DefaultValueContainer) cobj.getDefaultValue());
            } else {
                RMObjectMapperProvider rmObjectMapperProvider = serializer.getRmObjectMapperProvider();
                if (rmObjectMapperProvider == null ||
                        (rmObjectMapperProvider.getOutputOdinObjectMapper() == null && rmObjectMapperProvider.getJsonObjectMapper() == null)) {
                    //fallback: serialize generic ODIN. This will likely be non-standard!
                    builder.append("_default = ");
                    builder.newIndentedLine();
                    builder.odin(cobj.getDefaultValue());
                    builder.newUnindentedLine();
                } else {
                    try {
                        if (rmObjectMapperProvider.getJsonObjectMapper() != null) {
                            //the writerFor here makes sure type info gets output even for the root type
                            String content = rmObjectMapperProvider.getJsonObjectMapper().writerFor(OpenEHRBase.class).writeValueAsString(cobj.getDefaultValue());
                            serializeDefaultValueJson(content);
                        } else {
                            ObjectMapper objectMapper = rmObjectMapperProvider.getOutputOdinObjectMapper();
                            ObjectWriter objectWriter;
                            //the writerFor here makes sure type info gets output even for the root type
                            if(objectMapper instanceof ODINMapper) {
                                // If the mapper is a ODINMapper, use the ODINPrettyPrinter to apply the correct indentation depth.
                                objectWriter = objectMapper.writerFor(OpenEHRBase.class).with(new ODINPrettyPrinter(builder.getIndentDepth()));
                            } else {
                                objectWriter = objectMapper.writerFor(OpenEHRBase.class);
                            }
                            String content = objectWriter.writeValueAsString(cobj.getDefaultValue());
                            serializeDefaultValueOdin(content);
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    protected void serializeDefaultValueContainer(DefaultValueContainer container) {
        // This method doesn't apply indentation to keep the original indentation in the container.
        builder.tryNewLine();
        if(container.getFormat() == null || container.getFormat().equalsIgnoreCase(DefaultValueContainer.ODIN)) {
            builder.append("_default = ");
            builder.append(container.getContent());
        } else {
            builder.append("_default = (");
            builder.append(container.getFormat());
            builder.append(") <#");
            builder.append(container.getContent());
            builder.append("#>");
        }
    }

    protected void serializeDefaultValueJson(String jsonContent) {
        builder.tryNewLine();
        builder.append("_default = (");
        builder.append(DefaultValueContainer.JSON);
        builder.append(") <#");
        builder.newIndentedLine();
        // This will indent the JSON content to the correct level.
        builder.appendMultipleLines(jsonContent);
        builder.unindent();
        builder.append("#>");
    }

    protected void serializeDefaultValueOdin(String odinContent) {
        // This method doesn't apply indentation as it is unsafe to add extra indentation to ODIN.
        // The ODIN might already be indented correctly by the ODINPrettyPrinter.
        builder.tryNewLine();
        builder.append("_default = ");
        builder.append(odinContent);
    }

    private Set<String> getTupleAttributeNames(T cobj) {
        return cobj.getAttributeTuples().stream()
                    .flatMap(cat -> cat.getMembers().stream())
                    .map(CAttribute::getRmAttributeName)
                    .collect(Collectors.toSet());
    }


    private void buildAttribute(CAttribute cattr) {
        builder.tryNewLine();
        if (cattr.getDifferentialPath() == null) {
            builder.append(cattr.getRmAttributeName());
        } else {
            builder.append(cattr.getDifferentialPath());
        }
        if (cattr.getExistence() != null) {
            builder.append(" existence matches {");
            buildOccurrences(builder, cattr.getExistence());
            builder.append("}");
        }
        if (cattr.getCardinality() != null) {
            builder.append(" cardinality matches {");
            appendCardinality(cattr.getCardinality());
            builder.append("}");
        }
        if (!cattr.getChildren().isEmpty()) {
            buildAttributeChildConstraints(cattr);
        }
    }


    private void buildTuple(CAttributeTuple cAttributeTuple) {
        builder.tryNewLine();
        builder.append("[");
        List<String> members = cAttributeTuple.getMembers().stream()
                .map(CAttribute::getRmAttributeName)
                .collect(Collectors.toList());
        builder.append(Joiner.on(", ").join(members));
        builder.append("] matches {");
        builder.indent();
        for (int i = 0; i < cAttributeTuple.getTuples().size(); i++) {
            CPrimitiveTuple cObjectTuple = cAttributeTuple.getTuples().get(i);
            builder.newline();
            builder.append("[");
            for (int j = 0; j < cObjectTuple.getMembers().size(); j++) {
                builder.append("{");
                serializer.appendCObject(cObjectTuple.getMembers().get(j));
                builder.append("}");
                if (j < cObjectTuple.getMembers().size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
            if (i < cAttributeTuple.getTuples().size() - 1) {
                builder.append(",");
            }

            cObjectTuple.getMembers().stream()
                    .map(serializer::getSimpleCommentText)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresent(builder::lineComment);
        }
        builder.unindent().newline();
        builder.append("}");
    }

    private void buildAttributeChildConstraints(CAttribute cattr) {
        List<CObject> children = filterNonEmptyChildren(cattr.getChildren());
        if(children.isEmpty()) {
            return;
        }

        builder.append(" matches ");
        boolean indent = !children.isEmpty() &&
                (children.size() > 1 || !(children.get(0) instanceof CPrimitiveObject));
        builder.append("{");
        children.forEach(serializer::appendCObject);

        if (indent) {
            builder.newline();
        }

        builder.append("}");

        if (!indent && !children.isEmpty()) {
            String commentText = serializer.getSimpleCommentText(children.get(0));
            if (commentText != null) {
                builder.lineComment(commentText);
            }
        }
    }

    private List<CObject> filterNonEmptyChildren(List<CObject> children) {
        return children.stream()
                .filter(child -> !serializer.isEmpty(child))
                .collect(Collectors.toList());
    }

    private void appendCardinality(Cardinality card) {
        buildOccurrences(builder, card.getInterval());
        List<String> tags = new ArrayList<>();
        //TODO: this should compare against the RM and only serialize if different, OR we should make ordered nullable
        //and add a preprocessor that removes all default values
        if (!card.isOrdered()) {
            tags.add("unordered");
        }
        //TODO: this should compare against the RM and only serialize if different, OR we should make unique nullable
        //and add a preprocessor that removes all default values
        if (card.isUnique()) {
            tags.add("unique");
        }
        if (!tags.isEmpty()) {
            builder.append("; ").append(Joiner.on("; ").join(tags));
        }
    }
}
