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
     * ADL 1.4 contains no rm release version, 2 does. So one needs to be added. Set to the desired rm_release. Defaults to 1.1.0
     */
    private String rmRelease = "1.1.0";

    /**
     * Current adlVersion
     */
    private String adlVersion = "2.3.0";

    /**
     * Set to the ADL version the ADL 1.4 archetype should be converted into. Options: ID_CODED, AT_CODED. Defaults to ID_CODED.
     */
    private ADL2VERSION adlConfiguration = ADL2VERSION.ID_CODED;
    public enum ADL2VERSION {
        ID_CODED,
        AT_CODED
    }

    // GETTERS
    public List<TerminologyUriTemplate> getTerminologyConversionTemplates() {
        return terminologyConversionTemplates;
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

    public boolean isApplyDiff() {
        return applyDiff;
    }

    public String getRmRelease() {
        return rmRelease;
    }

    public ADL2VERSION getAdlConfiguration() {
        return adlConfiguration;
    }

    public String getAdlVersion() {
        if (adlConfiguration == ADL2VERSION.AT_CODED) {
            return "2.4.0";
        } else {
            return adlVersion;
        }
    }

    // SETTERS
    public void setTerminologyConversionTemplates(List<TerminologyUriTemplate> terminologyConversionTemplates) {
        this.terminologyConversionTemplates = terminologyConversionTemplates;
    }

    public void setAllowDuplicateFieldNames(boolean allowDuplicateFieldNames) {
        this.allowDuplicateFieldNames = allowDuplicateFieldNames;
    }

    public void setApplyDiff(boolean applyDiff) {
        this.applyDiff = applyDiff;
    }

    public void setRmRelease(String rmRelease) {
        this.rmRelease = rmRelease;
    }

    public void setAdlConfiguration(ADL2VERSION adlConfiguration) {
        this.adlConfiguration = adlConfiguration;
    }
}
