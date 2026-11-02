package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CArchetypeRootSerializer extends CComplexObjectSerializer<CArchetypeRoot> {
    public CArchetypeRootSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CArchetypeRoot cobj) {
        builder.indent().newline();
        appendSiblingOrder(cobj);
        builder.append("use_archetype");
        builder.append(" ").append(cobj.getRmTypeName());
        builder.append("[");
        boolean nodeIdAppended = false;
        if (cobj.getNodeId() != null) {
            nodeIdAppended = true;
            builder.append(cobj.getNodeId());
        }
        if(cobj.getArchetypeRef() != null) {
            if(nodeIdAppended) {
                builder.append(", ");
            }
            builder.append(cobj.getArchetypeRef());
        }
        builder.append("]");

        appendOccurrences(cobj);
        //this is not according to the grammar, won't parse and not according to the AOM, but according to specs.
        //it only occurs when you flatten a use_archetype node. You can change it into complex objects with an option in the flattener
        //See https://openehr.atlassian.net/browse/SPECPR-217 and https://openehr.atlassian.net/browse/SPECPR-218
        //on why this is a good idea
        if(cobj.getAttributes() != null && !cobj.getAttributes().isEmpty()) {
            builder.ensureSpace();
            builder.append("matches {");
            builder.lineComment(serializer.getTermText(cobj));
            buildAttributesAndTuples(cobj);
            builder.append("}");
        } else {
            builder.lineComment(serializer.getTermText(cobj));
        }


        builder.unindent();
    }
}
