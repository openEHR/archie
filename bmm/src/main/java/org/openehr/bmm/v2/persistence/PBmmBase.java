package org.openehr.bmm.v2.persistence;

import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.util.CloneUtil;

public class PBmmBase extends OpenEHRBase {

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public PBmmBase clone() {
        return CloneUtil.clone(this);
    }

    protected boolean nullToFalse(Boolean value) {
        return value == null ? false: value;
    }
}
