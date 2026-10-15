package com.nedap.archie.flattener;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.rminfo.MetaModel;

public interface IAttributeFlattenerSupport {

    CObject createSpecializeCObject(CAttribute attribute, CObject parent, CObject specialized);

    public MetaModel getMetaModel();

    public FlattenerConfiguration getConfig();
}
