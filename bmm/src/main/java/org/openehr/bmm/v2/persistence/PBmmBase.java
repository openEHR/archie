package org.openehr.bmm.v2.persistence;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.util.KryoUtil;

public class PBmmBase extends OpenEHRBase {

    public PBmmBase clone() {
        Kryo kryo = null;
        try {
            kryo = KryoUtil.getPool().obtain();
            return kryo.copy(this);
        }
        finally {
            KryoUtil.getPool().free(kryo);
        }
    }

    protected boolean nullToFalse(Boolean value) {
        return value == null ? false: value;
    }
}
