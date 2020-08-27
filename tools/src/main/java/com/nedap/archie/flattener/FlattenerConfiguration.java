package com.nedap.archie.flattener;

import com.esotericsoftware.kryo.Kryo;
import com.nedap.archie.util.KryoUtil;

public class FlattenerConfiguration {

    /**
     * If true, create an operational template
     * default false
     */
    private boolean createOperationalTemplate = false;
    /**
     * If true, replaces the archetype root with a C_COMPLEX_OBJECT with the archetype_ref set
     * If false, leaves the ArchetypeRoot in place, with the children as attributes, since archetype root itself is a C_COMPLEX_OBJECT
     * Default false
     */
    private boolean useComplexObjectForArchetypeSlotReplacement = false;
    /**
     * Remove all zero occurrences objects. Default false, except for OperationalTemplates, where it is default true.
     */
    private boolean removeZeroOccurrencesObjects = false;
    /**
     * Remove zero occurrences objects in parents. Useful for modeling tools, where you cannot undo a zero occurrences if it's defined in a parent, so don't need to know that it exists
     */
    private boolean removeZeroOccurrencesInParents = false;
    /**
     * Set to true to remove languages from the metadata and translations. Only for operational templates. Default false
     */
    private boolean removeLanguagesFromMetaData = false;
    /**
     * The languages to not remove. Only works when removeLanguagesFromMetadata=true && createOperationalTemplate == true
     */
    private String[] languagesToKeep = null;

    /**
     * Only for Operational templates: replace the use node constructs with their replacements. Default true.
     */
    private boolean replaceUseNode = true;

    /**
     * Only for Operational templates: replace the archetype roots with the corresponding structure. Defaults to true
     */
    private boolean fillArchetypeRoots = true;
    /**
     * Only for Operational templates: remove any closed archetype slots from the archetype.
     */
    private boolean closeArchetypeSlots = true;

    /**
     * Only for Operational templates: replace any empty occurrences with effectiveOccurrences.
     */
    private boolean fillEmptyOccurrences = true;

    private FlattenerConfiguration() {

    }

    public static FlattenerConfiguration forFlattened() {
        return new FlattenerConfiguration();
    }

    public static FlattenerConfiguration forOperationalTemplate() {
        FlattenerConfiguration result = new FlattenerConfiguration();
        result.setCreateOperationalTemplate(true);
        result.setRemoveZeroOccurrencesObjects(true);
        return result;
    }



    public boolean isCreateOperationalTemplate() {
        return createOperationalTemplate;
    }

    public void setCreateOperationalTemplate(boolean createOperationalTemplate) {
        this.createOperationalTemplate = createOperationalTemplate;
    }

    public boolean isRemoveLanguagesFromMetaData() {
        return removeLanguagesFromMetaData;
    }

    public void setRemoveLanguagesFromMetaData(boolean removeLanguagesFromMetaData) {
        this.removeLanguagesFromMetaData = removeLanguagesFromMetaData;
    }

    public boolean isUseComplexObjectForArchetypeSlotReplacement() {
        return useComplexObjectForArchetypeSlotReplacement;
    }

    public void setUseComplexObjectForArchetypeSlotReplacement(boolean useComplexObjectForArchetypeSlotReplacement) {
        this.useComplexObjectForArchetypeSlotReplacement = useComplexObjectForArchetypeSlotReplacement;
    }

    public boolean isRemoveZeroOccurrencesObjects() {
        return removeZeroOccurrencesObjects;
    }

    public void setRemoveZeroOccurrencesObjects(boolean removeZeroOccurrencesObjects) {
        this.removeZeroOccurrencesObjects = removeZeroOccurrencesObjects;
    }

    public String[] getLanguagesToKeep() {
        return languagesToKeep;
    }

    public void setLanguagesToKeep(String[] languagesToKeep) {
        this.languagesToKeep = languagesToKeep;
    }

    public FlattenerConfiguration clone() {
        Kryo kryo = null;
        try {
            kryo = KryoUtil.getPool().borrow();
            return kryo.copy(this);
        } finally {
            KryoUtil.getPool().release(kryo);
        }
    }

    public boolean isReplaceUseNode() {
        return replaceUseNode;
    }

    public void setReplaceUseNode(boolean replaceUseNode) {
        this.replaceUseNode = replaceUseNode;
    }

    public boolean isFillArchetypeRoots() {
        return fillArchetypeRoots;
    }

    public void setFillArchetypeRoots(boolean fillArchetypeRoots) {
        this.fillArchetypeRoots = fillArchetypeRoots;
    }

    public boolean isCloseArchetypeSlots() {
        return closeArchetypeSlots;
    }

    public void setCloseArchetypeSlots(boolean closeArchetypeSlots) {
        this.closeArchetypeSlots = closeArchetypeSlots;
    }

    public boolean isRemoveZeroOccurrencesInParents() {
        return removeZeroOccurrencesInParents;
    }

    public void setRemoveZeroOccurrencesInParents(boolean removeZeroOccurrencesInParents) {
        this.removeZeroOccurrencesInParents = removeZeroOccurrencesInParents;
    }

    public boolean isFillEmptyOccurrences() {
        return fillEmptyOccurrences;
    }

    public void setFillEmptyOccurrences(boolean fillEmptyOccurrences) {
        this.fillEmptyOccurrences = fillEmptyOccurrences;
    }
}
