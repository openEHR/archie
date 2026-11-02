package com.nedap.archie.adl14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.definitions.AdlCodeDefinitions;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Node id generator that generates codes in the id/at/ac9xxx range.
 * <p>
 * This is useful for synthesized codes, so as not to create any codes that would overlap with regular edits.
 */
public class OutsideRangeIdCodeGenerator implements IdCodeGenerator {

    private final Archetype archetype;
    private final Set<String> allUsedCodes;

    private Set<String> generatedCodes = new LinkedHashSet<>();

    private int counter = 9000;

    public OutsideRangeIdCodeGenerator(Archetype archetype) {
        this.archetype = archetype;
        this.allUsedCodes = archetype.getAllUsedCodes();
        this.counter = Math.max(counter, AOMUtils.getMaximumIdCode(archetype.specializationDepth(), allUsedCodes));

    }

    @Override
    public String generateNextIdCode() {
        return generateNextCode(AdlCodeDefinitions.ID_CODE_LEADER);
    }

    @Override
    public String generateNextValueCode() {
        return generateNextCode(AdlCodeDefinitions.VALUE_CODE_LEADER);
    }

    @Override
    public String generateNextValueSetCode() {
        return generateNextCode(AdlCodeDefinitions.VALUE_SET_CODE_LEADER);
    }

    @Override
    public String generateNextSpecializedIdCode(String nodeId) {
        //no specific logic needed here
        return archetype.generateNextSpecializedIdCode(nodeId);
    }

    private String generateNextCode(String prefix) {
        int specializationDepth = archetype.specializationDepth();

        String result = prefix + generateSpecializationDepthCodePrefix(specializationDepth) + counter;
        while(allUsedCodes.contains(result)) {
            counter = counter + 1;
            result = prefix + generateSpecializationDepthCodePrefix(specializationDepth) + counter;
        }
        counter = counter + 1;
        generatedCodes.add(result);
        return result;

    }

    private String generateSpecializationDepthCodePrefix (int specializationDepth) {
        return ("0" + AdlCodeDefinitions.SPECIALIZATION_SEPARATOR).repeat(Math.max(0, specializationDepth));
    }
}
