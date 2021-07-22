package com.nedap.archie.adl14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceDescription;

import java.util.LinkedHashMap;
import java.util.Map;

public class ADL14DescriptionConverter {

    public void convert(Archetype archetype) {
        ResourceDescription description = archetype.getDescription();
        if(description == null) {
            return;
        }
        description.setLicence(description.getOtherDetails().remove("licence"));
        description.setOriginalNamespace(description.getOtherDetails().remove("original_namespace"));
        description.setOriginalPublisher(description.getOtherDetails().remove("original_publisher"));
        description.setCustodianNamespace(description.getOtherDetails().remove("custodian_namespace"));
        description.setCustodianOrganisation(description.getOtherDetails().remove("custodian_organisation"));
        archetype.setBuildUid(description.getOtherDetails().remove("build_uid"));
        String references = description.getOtherDetails().remove("references");
        if(references != null) {
            Map<String, String> newReferences = new LinkedHashMap<>();
            String[] lines = references.split("\r?\n");
            int i = 1;
            for(String line:lines) {
                String trimmed = line.trim();
                if(!trimmed.isEmpty()) {
                    newReferences.put(Integer.toString(i), line);
                    i++;
                }
            }
            description.setReferences(newReferences);
        }
        String ipAcknowledgements = description.getOtherDetails().remove("ip_acknowledgements");
        if(ipAcknowledgements != null) {
            Map<String, String> acknowledgements = new LinkedHashMap<>();
            String[] lines = ipAcknowledgements.split("\r?\n");
            int i = 1;
            for(String line:lines) {
                String trimmed = line.trim();
                if(!trimmed.isEmpty()) {
                    acknowledgements.put(Integer.toString(i), line);
                    i++;
                }
            }
            description.setIpAcknowledgements(acknowledgements);
        }

        String revision = description.getOtherDetails().remove("revision");
        if(revision != null) {
            archetype.getArchetypeId().setReleaseVersion(revision);
        }

        archetype.setGenerated(true);
    }
}
