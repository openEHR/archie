package org.openehr.bmm.v2.persistence;

import com.esotericsoftware.kryo.Kryo;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.util.KryoUtil;

public class PBmmBase extends OpenEHRBase {

    public PBmmBase clone() {
        Kryo kryo = null;
        try {
            kryo = KryoUtil.getPool().borrow();
            return kryo.copy(this);
        }
        finally {
            KryoUtil.getPool().release(kryo);
        }
    }
}
