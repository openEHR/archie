package com.nedap.archie.serializer.adl;

import com.nedap.archie.aom.*;
import com.nedap.archie.rminfo.RMObjectMapperProvider;

import java.util.function.Function;

/**
 * @author markopi
 */
abstract public class ADLArchetypeSerializer<T extends Archetype> {
    protected final T archetype;
    final RMObjectMapperProvider rmObjectMapperProvider;
    Function<String, Archetype> flatArchetypeProvider;
    protected final ADLStringBuilder builder = new ADLStringBuilder();

    private final ADLDefinitionSerializer definitionSerializer;
    private final ADLRulesSerializer rulesSerializer;


    protected ADLArchetypeSerializer(T archetype, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider rmObjectMapperProvider) {
        this.archetype = archetype;
        this.flatArchetypeProvider = flatArchetypeProvider;
        this.rmObjectMapperProvider = rmObjectMapperProvider;

        this.definitionSerializer = new ADLDefinitionSerializer(builder, flatArchetypeProvider, rmObjectMapperProvider);
        this.rulesSerializer = new ADLRulesSerializer(builder, definitionSerializer);
    }

    /**
     * Serialize the archetype to ADL, using the flattened parent archetypes to get names for in the comments
     * of specialized archetypes
     * @param archetype the archetype to serialize
     * @param flatArchetypeProvider the function to retrieve flat parent archetypes
     * @param rmObjectMapperProvider the RM Object Mapper provider used to serialize default values. If not provided,
     *                               the standard ODIN serializer will be used, which will likekely not be correct.
     * @return the ADL output
     */
    public static String serialize(Archetype archetype, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider rmObjectMapperProvider) {
        if (archetype instanceof Template) {
            return new ADLTemplateSerializer((Template) archetype, flatArchetypeProvider, rmObjectMapperProvider).serialize();
        } else if (archetype instanceof OperationalTemplate) {
            return new ADLOperationalTemplateSerializer((OperationalTemplate) archetype, flatArchetypeProvider, rmObjectMapperProvider).serialize();
        } else if (archetype instanceof TemplateOverlay) {
            return new ADLTemplateOverlaySerializer((TemplateOverlay) archetype, flatArchetypeProvider, rmObjectMapperProvider).serialize();
        } else if (archetype instanceof AuthoredArchetype) {
            return new ADLAuthoredArchetypeSerializer<>((AuthoredArchetype) archetype, flatArchetypeProvider, rmObjectMapperProvider).serialize();
        }
        throw new AssertionError("Could not serialize archetype of class " +
                (archetype == null ? null : archetype.getClass().getName()));
    }


    /**
     * Serialize the archetype to ADL
     * @param archetype the archetype to serialize
     * @return the ADL output
     */
    public static String serialize(Archetype archetype) {
        return serialize(archetype, null, null);
    }

    protected String serialize() {
        appendHead();
        appendSpecialize();
        appendLanguage();
        appendDescription();
        appendDefinition();
        appendRules();
        appendTerminology();
        appendAnnotations();

        return builder.toString();
    }

    protected void appendAnnotations() {
        if (archetype.getAnnotations()==null) return;
        builder.newline().append("annotations").newIndentedLine()
                .odin(archetype.getAnnotations())
                .unindent();
    }

    protected void appendRules() {
        if(archetype.getRules() == null) return;
        if(archetype.getRules().getRules().isEmpty()) return;

        builder.newline().append("rules").newIndentedLine();
        rulesSerializer.appendRules(archetype.getRules());
        builder.newUnindentedLine();
    }

    protected void appendTerminology() {
        if (archetype.getTerminology() == null) return;
        builder.newline().append("terminology").newIndentedLine()
                .odin(archetype.getTerminology())
                .unindent();

    }

    protected void appendDefinition() {
        if (archetype.getDefinition() == null) return;

        builder.newline().append("definition");
        definitionSerializer.appendCObject(archetype.getDefinition());
        builder.tryNewLine();
    }

    protected abstract void appendLanguage();

    protected abstract void appendDescription();

    private void appendSpecialize() {
        if (archetype.getParentArchetypeId() == null) return;

        builder.newline().append("specialize").newIndentedLine()
                .append(archetype.getParentArchetypeId())
                .newUnindentedLine();
    }

    abstract protected String headTag();

    private void appendHead() {
        builder.append(headTag());
        appendHeaderAttributes();

        builder.newIndentedLine()
                .append(archetype.getArchetypeId().getFullId())
                .newUnindentedLine();
    }

    abstract protected void appendHeaderAttributes();

    public ADLStringBuilder getBuilder() {
        return builder;
    }

    public T getArchetype() {
        return archetype;
    }


}
