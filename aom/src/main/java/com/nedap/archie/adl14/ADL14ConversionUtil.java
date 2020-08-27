package com.nedap.archie.adl14;

import com.nedap.archie.adl14.terms.TerminologyUriTemplate;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;

public class ADL14ConversionUtil {

    private final ADL14ConversionConfiguration conversionConfiguration;

    public ADL14ConversionUtil(ADL14ConversionConfiguration conversionConfiguration) {
        this.conversionConfiguration = conversionConfiguration;
    }

    public URI convertToUri(TerminologyCode value) throws URISyntaxException {
        if(value.getUri() != null) {
            return value.getUri();
        }

        TerminologyUriTemplate template = conversionConfiguration.getTerminologyUriTemplate(value.getTerminologyId(), value.getTerminologyVersion());
        if(template != null) {
            final String templateString = template.getTemplate();
            String result = templateString.replace(TerminologyUriTemplate.TERMINOLOGY_ID_TEMPLATE_STRING, value.getTerminologyId());
            result = result.replace(TerminologyUriTemplate.CODE_STRING_TEMPLATE_STRING, value.getCodeString());
            if(value.getTerminologyVersion() != null) {
                result = result.replace(TerminologyUriTemplate.TERMINOLOGY_VERSION_TEMPLATE_STRING, value.getTerminologyVersion());
            }
            return new URI(result);
        }

        if(value.getTerminologyVersion() != null) {
            return new URI(MessageFormat.format("http://{0}.org/ver/{1}/id/{2}", value.getTerminologyId(), value.getTerminologyVersion(), value.getCodeString()));
        }
        return new URI(MessageFormat.format("http://{0}.org/id/{1}", value.getTerminologyId(), value.getCodeString()));
    }

    public static String findExistingTermBinding(String terminologyId, Archetype archetype, Archetype flatParentArchetype, URI uri, Map<String, URI> termBindingsMap) {
        if(flatParentArchetype != null && flatParentArchetype.getTerminology().getTermBindings() != null && flatParentArchetype.getTerminology().getTermBindings().get(terminologyId) != null) {
            String termBinding = findExistingTermBinding(uri, flatParentArchetype.getTerminology().getTermBindings().get(terminologyId));
            if(termBinding != null) {
                return termBinding;
            }
        }
        return findExistingTermBinding(uri, termBindingsMap);
    }

    private static String findExistingTermBinding(URI uri, Map<String, URI> termBindingsMap) {
        for(Map.Entry<String, URI> existingBinding:termBindingsMap.entrySet()) {
            if(existingBinding.getValue().equals(uri) && !existingBinding.getKey().startsWith("/")) {
                return existingBinding.getKey();
            }
        }
        return null;
    }
}
