package com.nedap.archie.flattener;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModels;

public interface IAttributeFlattenerSupport {

    CObject createSpecializeCObject(CAttribute attribute, CObject parent, CObject specialized);

    /**
     * @deprecated Use {@link #getMetaModel()} instead.
     */
    @Deprecated
    public MetaModels getMetaModels();

    public MetaModel getMetaModel();

    public FlattenerConfiguration getConfig();
}
