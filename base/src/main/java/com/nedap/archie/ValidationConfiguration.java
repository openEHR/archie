package com.nedap.archie;

/**
 * This controls static configuration, applied to all Validation runs, that cannot be applied on the validator itself
 * due to technical reasons
 *
 * @deprecated Use {@code com.nedap.archie.rmobjectvalidator.ValidationConfiguration} instead.
 */
@Deprecated
public class ValidationConfiguration {

    @Deprecated
    private static boolean failOnUnknownTerminologyId = false;

    /**
     * Set whether to fail validation or not if in CTerminologyCode.isValidValue(), an unknown terminology is encountered
     * Since Archie does not validate external terminologies at all, this currently means all terminologies with id "local",
     * including "openehr" until that is implemented.
     * Sets this globally for the entire JVM!
     * @param failOnUnknownTerminologyId whether to fail or not
     * @deprecated Use {@code com.nedap.archie.rmobjectvalidator.ValidationConfiguration.Builder.failOnUnknownTerminologyId} instead.
     */
    @Deprecated
    public static void setFailOnUnknownTerminologyId(boolean failOnUnknownTerminologyId) {
        ValidationConfiguration.failOnUnknownTerminologyId = failOnUnknownTerminologyId;
    }


    /**
    * Get whether to fail validation or not if in CTerminologyCode.isValidValue(), an uknown terminology is encountered
    * Since Archie does not validate external terminologies at all, this currently means all terminologies with id "local",
    * including "openehr" until that is implemented.
    * This configuration is applied globally to the entire JVM!
    * @return whether to fail or not
    * @deprecated Use {@code com.nedap.archie.rmobjectvalidator.ValidationConfiguration.isFailOnUnknownTerminologyId} instead.
    **/
    @Deprecated
    public static boolean isFailOnUnknownTerminologyId() {
        return failOnUnknownTerminologyId;
    }
}
