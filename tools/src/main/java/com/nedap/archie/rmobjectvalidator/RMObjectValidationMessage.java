package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeConstraint;

import java.util.Objects;

/**
 * Created by pieter.bos on 02/09/15.
 */
public class RMObjectValidationMessage {

    private String archetypePath;
    private String path;
    @Deprecated
    private String humanReadableArchetypePath;
    private String message;
    private String archetypeId;

    private RMObjectValidationMessageType type;


    public RMObjectValidationMessage(ArchetypeConstraint constraint, String actualPath, String message) {
        this(constraint, actualPath, message, RMObjectValidationMessageType.DEFAULT);
    }

    public RMObjectValidationMessage(ArchetypeConstraint constraint, String actualPath, String message, RMObjectValidationMessageType type) {
        this(actualPath,
                getArchetypeId(constraint),
                constraint == null ? null : constraint.getPath(),
                constraint == null ? null : constraint.getLogicalPath(),
                message,
                type);
    }


    // Constructors with attribute assignment
    public RMObjectValidationMessage(String path, String archetypeId, String archetypePath, String message, RMObjectValidationMessageType type) {
        this(path, archetypeId, archetypePath, null, message, type);
    }

    /**
     * @deprecated humanPath will be removed. Use {@link #RMObjectValidationMessage(String, String, String, String,
     * RMObjectValidationMessageType)} instead.
     */
    @Deprecated
    public RMObjectValidationMessage(String path, String archetypeId, String archetypePath, String humanPath, String message, RMObjectValidationMessageType type) {
        this.path = path;
        this.archetypeId = archetypeId;
        this.archetypePath = archetypePath;
        this.humanReadableArchetypePath = humanPath;
        this.message = message;
        this.type = type;
    }

    /**
     * @deprecated The RMObjectValidationException class will be removed.
     */
    @Deprecated
    public RMObjectValidationMessage(RMObjectValidationException e) {
        this.path = e.getPath();
        this.humanReadableArchetypePath = e.getHumanPath();
        this.message = e.getMessage();
    }

    /**
     * Get the archetype id of the archetype used for validation, if available. Usually this will be the id of the OPT provided to the
     * RMObjectValidator. However, if archetype slots where used, it will return the id of the archetype in the slot instead.
     * It will return null in case no archetype was available or the validation error was not due to an archetype constraint
     * for example, validations from the RM instead of the archetype.
     * @return the archetype id of the used archetype for validation, or null if none was available
     */
    public String getArchetypeId() {
        return archetypeId;
    }

    /**
     * Get the path in the archetype that this validation refers to - used to retrieve the constraint that was used to generate this error
     * @return the path of the constraint that was used to generate this error in the archetype
     */
    public String getArchetypePath() {
        return archetypePath;
    }

    /**
     * returns the path in the RM Object that resulted in this validation message.
     * @return The path in the RM Object that resulted in this validation message.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the type of the validation message.
     * @return the type of the validation message
     */
    public RMObjectValidationMessageType getType() {
        return type;
    }

    public void setType(RMObjectValidationMessageType type) {
        this.type = type;
    }

    /**
     * Get the human readable path in the archetype that this validation refers to - used to retrieve the constraint that was used to generate this error
     * @return the human readablepath of the constraint that was used to generate this error in the archetype
     * @deprecated This functionality will be removed.
     */
    @Deprecated
    public String getHumanReadableArchetypePath() {
        return humanReadableArchetypePath;
    }

    /**
     * Gets the validation message, which is a human readable string indicating the cause of this validation message.
     * @return the validation message
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return RMObjectValidationMessageIds.rm_VALIDATION_MESSAGE_TO_STRING.getMessage(humanReadableArchetypePath == null ? path : humanReadableArchetypePath, path, message);
    }

    private static String getArchetypeId(ArchetypeConstraint constraint) {
        if(constraint == null) {
            return null;
        }
        Archetype archetype = constraint.getArchetype();
        if(archetype != null) {
            return archetype.getArchetypeId().getFullId();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RMObjectValidationMessage that = (RMObjectValidationMessage) o;
        return Objects.equals(archetypePath, that.archetypePath) &&
                Objects.equals(path, that.path) &&
                Objects.equals(humanReadableArchetypePath, that.humanReadableArchetypePath) &&
                Objects.equals(message, that.message) &&
                Objects.equals(archetypeId, that.archetypeId) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(archetypePath, path, humanReadableArchetypePath, message, archetypeId, type);
    }
}
