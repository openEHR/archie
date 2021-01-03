package com.nedap.archie.opt14;

import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.Template;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.util.LinkedHashMap;
import java.util.Map;

public class DescriptionConverter {

    public static void convert(Template template, OPERATIONALTEMPLATE opt14) {
        RESOURCEDESCRIPTION description14 = opt14.getDescription();
        ResourceDescription description = new ResourceDescription();
        description.setLifecycleState(TerminologyCode.createFromString("openehr", null, description14.lifecycleState));
        Map<String, String> author = new LinkedHashMap<>();
        if(description14.getOriginalAuthor() != null) {
            for(StringDictionaryItem item:description14.getOriginalAuthor()) {
                author.put(item.getId(), item.getValue());
            }
        }
        description.setOriginalAuthor(author);
        template.setDescription(description);

        template.setOriginalLanguage(
                TerminologyCode.createFromString(
                        opt14.getLanguage().getTerminologyId().getValue(),
                        null,
                        opt14.getLanguage().getCodeString()));
        //TODO: implement me further
    }
}
