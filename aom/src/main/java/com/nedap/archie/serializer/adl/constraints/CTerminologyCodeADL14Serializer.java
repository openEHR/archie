package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.primitives.CTerminologyCodeADL14;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

import java.util.List;

/**
 * Serializer for {@link CTerminologyCodeADL14}, the ADL 1.4 flavour of a terminology code constraint
 * whose constraint is a {@code List<String>} (one or more inline codes) rather than the single
 * {@code String} of the ADL 2 {@link com.nedap.archie.aom.primitives.CTerminologyCode}.
 * <p>
 * This mirrors {@link CTerminologyCodeSerializer} but reconstructs the ADL 1.4 syntax from the list
 * the parser produced. The parser stores codes in one of these shapes:
 * <ul>
 *   <li>a single local code, e.g. {@code ["at0001"]} or {@code ["ac0001"]} → {@code [at0001]} / {@code [ac0001]};</li>
 *   <li>a single external term code already in full form, e.g. {@code ["[snomed-ct::12345]"]} → appended as-is;</li>
 *   <li>multiple local codes, e.g. {@code ["at0001", "at0002"]} → {@code [local::at0001, at0002]};</li>
 *   <li>an external terminology with multiple codes, with the terminology id first, e.g.
 *       {@code ["openehr", "271", "272"]} → {@code [openehr::271, 272]}.</li>
 * </ul>
 */
public class CTerminologyCodeADL14Serializer extends ConstraintSerializer<CTerminologyCodeADL14> {

    public CTerminologyCodeADL14Serializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CTerminologyCodeADL14 cobj) {
        List<String> constraints = cobj.getConstraint();
        if (constraints == null || constraints.isEmpty()) {
            return;
        }
        appendConstraintStatus(cobj);

        String assumedValue = cobj.getAssumedValue() != null ? cobj.getAssumedValue().getCodeString() : null;

        if (constraints.size() == 1 && constraints.get(0).startsWith("[")) {
            // already a full external term code ref like [snomed-ct::12345]
            appendFullTermCodeRef(constraints.get(0), assumedValue);
            return;
        }

        builder.append("[");
        if (constraints.size() == 1) {
            // single local at- or ac-code
            builder.append(constraints.get(0));
        } else if (AOMUtils.isValueCode(constraints.get(0)) || AOMUtils.isValueSetCode(constraints.get(0))) {
            // multiple local codes are an implicit local value set
            builder.append("local::").append(String.join(", ", constraints));
        } else {
            // external terminology: first entry is the terminology id, the rest are codes
            builder.append(constraints.get(0)).append("::")
                    .append(String.join(", ", constraints.subList(1, constraints.size())));
        }
        if (assumedValue != null) {
            builder.append("; ").append(assumedValue);
        }
        builder.append("]");
    }

    private void appendFullTermCodeRef(String fullRef, String assumedValue) {
        if (assumedValue != null && fullRef.endsWith("]")) {
            builder.append(fullRef.substring(0, fullRef.length() - 1)).append("; ").append(assumedValue).append("]");
        } else {
            builder.append(fullRef);
        }
    }

    private void appendConstraintStatus(CTerminologyCodeADL14 cobj) {
        if (cobj.getConstraintStatus() == null) {
            return;
        }
        String constraintStatusString;
        switch (cobj.getConstraintStatus()) {
            case REQUIRED:
                constraintStatusString = "required";
                break;
            case EXTENSIBLE:
                constraintStatusString = "extensible";
                break;
            case PREFERRED:
                constraintStatusString = "preferred";
                break;
            case EXAMPLE:
                constraintStatusString = "example";
                break;
            default:
                throw new RuntimeException("constraint status " + cobj.getConstraintStatus() + " unknown, cannot be serialized");
        }
        builder.append(constraintStatusString);
        builder.append(" ");
    }

    @Override
    public String getSimpleCommentText(CTerminologyCodeADL14 cobj) {
        List<String> constraints = cobj.getConstraint();
        if (constraints != null && constraints.size() == 1) {
            String constraint = constraints.get(0);
            if (AOMUtils.isValueSetCode(constraint) || AOMUtils.isValueCode(constraint)) {
                return serializer.getTermText(cobj, constraint);
            }
        }
        return null;
    }
}
