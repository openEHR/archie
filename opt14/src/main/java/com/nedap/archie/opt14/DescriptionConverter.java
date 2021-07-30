package com.nedap.archie.opt14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.Template;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nedap.archie.opt14.schema.*;

class DescriptionConverter {

    public static void convert(OPERATIONALTEMPLATE opt14, Archetype template) {
        RESOURCEDESCRIPTION description14 = opt14.getDescription();
        ResourceDescription description = new ResourceDescription();
        description.setLifecycleState(TerminologyCode.createFromString("openehr", null, description14.getLifecycleState()));
        Map<String, String> author = new LinkedHashMap<>();
        if(description14.getOriginalAuthor() != null) {
            for(StringDictionaryItem item:description14.getOriginalAuthor()) {
                author.put(item.getId(), item.getValue());
            }
        }
        if(description14.getOtherDetails() != null) {
            for(StringDictionaryItem item:description14.getOtherDetails()) {
                description.getOtherDetails().put(item.getId(), item.getValue());
            }
        }

        if(description14.getParentResource() != null) {
            //TODO: this seems to contain the parent archetype.
            // however only the top level archetype used, not any included archetype roots. Very odd
            //probably can be ignored?

        }
        //resource uri
        if(description14.getResourcePackageUri() != null) {
            description.setResourcePackageUri(description14.getResourcePackageUri());
        }
        if(description14.getDetails() != null) {
            convertDetails(description14, description);
        }
        description.setOtherContributors(description14.getOtherContributors());

        description.setOriginalAuthor(author);
        template.setDescription(description);

        template.setOriginalLanguage(BaseTypesConverter.convert(opt14.getLanguage()));
    }

    private static void convertDetails(RESOURCEDESCRIPTION description14, ResourceDescription description) {
        Map<String, ResourceDescriptionItem> detailsMap = new LinkedHashMap<>();
        for(RESOURCEDESCRIPTIONITEM details14:description14.getDetails()) {
            if(details14.getLanguage() == null || details14.getLanguage().getCodeString() == null) {
                throw new RuntimeException("Cannot convert resource description details without a language");
            }
            ResourceDescriptionItem details = new ResourceDescriptionItem();
            details.setCopyright(details14.getCopyright());
            details.setKeywords(details14.getKeywords());
            details.setLanguage(BaseTypesConverter.convert(details14.getLanguage()));
            details.setMisuse(details14.getMisuse());
            details.setUse(details14.getUse());
            details.setPurpose(details14.getPurpose());
            if(details14.getOriginalResourceUri() != null) {
                Map<String, URI> uris = new LinkedHashMap<>();
                for(StringDictionaryItem item:details14.getOriginalResourceUri()) {
                    uris.put(item.getId(), URI.create(item.getValue()));
                }
                details.setOriginalResourceUri(uris);
            }
            if(details14.getOtherDetails() != null) {
                Map<String, String> otherDetails = new LinkedHashMap<>();
                for(StringDictionaryItem item:details14.getOtherDetails()) {
                    otherDetails.put(item.getId(), item.getValue());
                }
                details.setOtherDetails(otherDetails);
            }
            detailsMap.put(details.getLanguage().getCodeString(), details);
        }
        description.setDetails(detailsMap);
    }
}
