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


    /**
     * The rm_release for converted archetypes that do not state a valid one themselves, in other_details["rm_release"]
     * or in the rm_release header item. Defaults to 1.1.0
     */
    private String rmRelease = "1.1.0";

    /**
     * Set to false to always use {@link #getRmRelease()}, ignoring the rm_release the ADL 1.4 archetype states.
     * The stated rm_release also selects the reference model the conversion uses.
     */
    private boolean useArchetypeRmRelease = true;


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

    public String getRmRelease() {
        return rmRelease;
    }

    public void setRmRelease(String rmRelease) {
        this.rmRelease = rmRelease;
    }

    public boolean isUseArchetypeRmRelease() {
        return useArchetypeRmRelease;
    }

    public void setUseArchetypeRmRelease(boolean useArchetypeRmRelease) {
        this.useArchetypeRmRelease = useArchetypeRmRelease;
    }
}
