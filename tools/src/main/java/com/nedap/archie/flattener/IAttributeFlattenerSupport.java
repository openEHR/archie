package com.nedap.archie.flattener;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.rminfo.MetaModels;

public interface IAttributeFlattenerSupport {

    CObject createSpecializeCObject(CAttribute attribute, CObject parent, CObject specialized);
    public MetaModels getMetaModels();

    public FlattenerConfiguration getConfig();
}
