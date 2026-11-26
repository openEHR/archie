package com.nedap.archie.adl14.log;

import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.aom.terminology.ValueSet;

import java.util.LinkedHashMap;
import java.util.Map;

public class ADL2ConversionLog {

    private String archetypeId;
    private Map<String, CreatedCode> createdCodes = new LinkedHashMap<>();
    private Map<String, ValueSet> createdValueSets = new LinkedHashMap<>();
    private ADL14ConversionConfiguration.NodeIdCodeSystem nodeIdCodeSystem;

    public ADL2ConversionLog() {

    }

    public ADL2ConversionLog(Map<String, ConvertedCodeResult> convertedCodes,
                             Map<String, CreatedCode> createdCodes,
                             Map<String, ValueSet> createdValueSets) {
        this.createdCodes = createdCodes;
        this.createdValueSets = createdValueSets;
    }

    public Map<String, CreatedCode> getCreatedCodes() {
        return createdCodes;
    }

    public void setCreatedCodes(Map<String, CreatedCode> createdCodes) {
        this.createdCodes = createdCodes;
    }

    public Map<String, ValueSet> getCreatedValueSets() {
        return createdValueSets;
    }

    public void setCreatedValueSets(Map<String, ValueSet> createdValueSets) {
        this.createdValueSets = createdValueSets;
    }

    /**
     * The code system of the conversion. Null in logs from before this was stored, which are id-coded.
     */
    public ADL14ConversionConfiguration.NodeIdCodeSystem getNodeIdCodeSystem() {
        return nodeIdCodeSystem;
    }

    public void setNodeIdCodeSystem(ADL14ConversionConfiguration.NodeIdCodeSystem nodeIdCodeSystem) {
        this.nodeIdCodeSystem = nodeIdCodeSystem;
    }
}
