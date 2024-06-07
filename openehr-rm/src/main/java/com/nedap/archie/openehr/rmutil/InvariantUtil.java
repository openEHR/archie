package com.nedap.archie.openehr.rmutil;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import com.nedap.archie.terminology.OpenEHRTerminologyAccess;
import com.nedap.archie.terminology.TermCode;

import java.util.Collection;
import java.util.List;

public class InvariantUtil {

    public static final String ENGLISH = "en";

    public static boolean nullOrNotEmpty(Collection<?> collection) {
        if(collection != null) {
            return !collection.isEmpty();
        }
        return true;
    }

    public static boolean nullOrNotEmpty(String string) {
        if(string != null) {
            return !string.isEmpty();
        }
        return true;
    }

    public static boolean belongsToTerminologyByOpenEHRId(CodePhrase value, String openEHRId) {
        if(value != null && value.getCodeString() != null) {
            TermCode termCode = OpenEHRTerminologyAccess.getInstance().getTermByOpenEhrId(openEHRId, value.getCodeString(), ENGLISH);
            return termCode != null && termCode != null &&
                    (value.getTerminologyId() == null || value.getTerminologyId().getValue().equalsIgnoreCase(termCode.getTerminologyId()));
        }
        return true;
    }

    public static boolean belongsToTerminologyByGroupId(CodePhrase value, String groupId) {
        if(value != null && value.getCodeString() != null) {
            TermCode termCode = OpenEHRTerminologyAccess.getInstance().getTermByOpenEHRGroup(groupId, ENGLISH, value.getCodeString());
            return termCode != null && termCode != null &&
                    (value.getTerminologyId() == null || value.getTerminologyId().getValue().equalsIgnoreCase(termCode.getTerminologyId()));
        }
        return true;
    }

    public static boolean belongsToTerminologyByOpenEHRId(DvCodedText value, String openEHRId) {
        if(value != null) {
            return belongsToTerminologyByOpenEHRId(value.getDefiningCode(), openEHRId);
        }
        return true;

    }

    public static boolean belongsToTerminologyByGroupId(DvCodedText value, String groupId) {
        if(value != null) {
            return belongsToTerminologyByGroupId(value.getDefiningCode(), groupId);
        }
        return true;
    }

    public static boolean objectRefTypeEquals(List<ObjectRef<? extends ObjectId>> refs, String type) {
        if(refs == null) {
            return true;
        }
        for(ObjectRef ref:refs) {
            if (!objectRefTypeEquals(ref, type)) return false;
        }
        return true;
    }

    public static boolean objectRefTypeEquals(ObjectRef<?> ref, String type) {
        return ref == null || ref.getType() == null || ref.getType().equals(type);

    }
}
