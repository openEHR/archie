package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CTerminologyCodeSerializer extends ConstraintSerializer<CTerminologyCode> {

    public CTerminologyCodeSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CTerminologyCode cobj) {
        if (!cobj.getConstraint().isEmpty()) {
            builder.append("[");
            String constraint = cobj.getConstraint().get(0);
            builder.append(constraint);
            if (cobj.getAssumedValue() != null && cobj.getAssumedValue().getCodeString()!=null) {
                builder.append("; ").append(cobj.getAssumedValue().getCodeString());
            }
            builder.append("]");
        }
    }

    public String getSimpleCommentText(CTerminologyCode cobj) {
        if (!cobj.getConstraint().isEmpty()) {
            String constraint = cobj.getConstraint().get(0);
            if(AOMUtils.isValueSetCode(constraint) || AOMUtils.isValueCode(constraint)) {
                return serializer.getTermText(cobj, constraint);
            }
        }

        return null;
    }

}
