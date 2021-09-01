package org.openehr.adl;

import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rminfo.MetaModels;

public class CComplexObjectParserV2 {
    private final MetaModels metaModels;

    public CComplexObjectParserV2(MetaModels metaModels) {
        this.metaModels = metaModels;
    }

    public CComplexObject parseComplexObject(String text, ArchieErrorListener listener) {
        return null;
    }

    public RulesSection parseRules(String text, ArchieErrorListener errorListenerFor) {
        return null;
    }
}
