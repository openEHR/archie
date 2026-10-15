package com.nedap.archie.serializer.adl.constraints;

import com.google.common.base.Joiner;
import com.nedap.archie.aom.primitives.CBoolean;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marko Pipan
 */
public class CBooleanSerializer extends ConstraintSerializer<CBoolean> {
    public CBooleanSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public void serialize(CBoolean cobj) {

        boolean constrained = false;

        List<String> valids = new ArrayList<>();
        if (!cobj.getConstraint().isEmpty()) {
            valids.addAll(cobj.getConstraint().stream()
                    .map(aBoolean -> capitalize(aBoolean.toString().toLowerCase())).collect(Collectors.toList()));
        }


        if (!valids.isEmpty()) {
            builder.append(Joiner.on(", ").join(valids));
            constrained = true;
        }

        if (cobj.getAssumedValue() != null) {
            builder.append("; ").append(capitalize(cobj.getAssumedValue().toString()));
            constrained = true;
        }

        if (!constrained) {
            builder.append("*");
        }


    }

    @Override
    public boolean isEmpty(CBoolean cobj) {
        return cobj.getConstraint() == null || cobj.getConstraint().isEmpty();
    }

}
