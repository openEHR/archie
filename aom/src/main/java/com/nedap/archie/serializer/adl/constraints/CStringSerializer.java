package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CStringSerializer extends ConstraintSerializer<CString> {
    public CStringSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CString cobj) {
        boolean constrained = false;

        if (!cobj.getConstraint().isEmpty()) {
            for (int i = 0; i < cobj.getConstraint().size(); i++) {
                String item = cobj.getConstraint().get(i);
                if (isRegex(item)) {
                    builder.append(item);
                } else {
                    builder.text(item);
                }
                if (i < cobj.getConstraint().size() - 1) {
                    builder.append(", ");
                }
            }
            constrained = true;
        }
        if (cobj.getAssumedValue() != null) {
            builder.append("; ").text(cobj.getAssumedValue());
            constrained = true;
        }
        if (!constrained) {
            builder.append("*");
        }
    }

    private boolean isRegex(String str) {
        if (str.length()<=1) return false;
        char c = str.charAt(0);

        if (str.charAt(str.length()-1)!=c) return false;
        return c=='/' || c=='^';
    }

    @Override
    public boolean isEmpty(CString cobj) {
        return cobj.getConstraint() == null || cobj.getConstraint().isEmpty();
    }
}
