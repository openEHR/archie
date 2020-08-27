package com.nedap.archie.adl14;

import com.nedap.archie.adl14.terms.TerminologyUriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ADL14ConversionConfiguration {

    private List<TerminologyUriTemplate> terminologyConversionTemplates = new ArrayList<>();

    /**
     * Set to true in case you want to accept duplicate fields in ODIN syntax. This can for example be duplicate at-codes
     * in the terminology.
     */
    private boolean allowDuplicateFieldNames;

    /**
     * Set to false to skip applying the differential format. Use only when you know what you are doing, very nonstandard!
     */
    private boolean applyDiff = true;


    public List<TerminologyUriTemplate> getTerminologyConversionTemplates() {
        return terminologyConversionTemplates;
    }

    public void setTerminologyConversionTemplates(List<TerminologyUriTemplate> terminologyConversionTemplates) {
        this.terminologyConversionTemplates = terminologyConversionTemplates;
    }

    public TerminologyUriTemplate getTerminologyUriTemplate(String terminologyId, String version) {
        Optional<TerminologyUriTemplate> result = terminologyConversionTemplates.stream().filter(template ->
                template.getTerminologyId().equalsIgnoreCase(terminologyId) &&
                        (version == null || version.equalsIgnoreCase(template.getTerminologyVersion()))).findFirst();
        if(result.isPresent() || version == null) {
            return result.orElse(null);
        }
        return terminologyConversionTemplates.stream().filter(template ->
                template.getTerminologyId().equalsIgnoreCase(terminologyId) &&
                        (template.getTerminologyVersion() == null)).findFirst().orElse(null);
    }

    public boolean isAllowDuplicateFieldNames() {
        return allowDuplicateFieldNames;
    }

    public void setAllowDuplicateFieldNames(boolean allowDuplicateFieldNames) {
        this.allowDuplicateFieldNames = allowDuplicateFieldNames;
    }

    public boolean isApplyDiff() {
        return applyDiff;
    }

    public void setApplyDiff(boolean applyDiff) {
        this.applyDiff = applyDiff;
    }
}
