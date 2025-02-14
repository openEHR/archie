package com.nedap.archie.rmobjectvalidator;

/**
 * Configuration for {@link RMObjectValidator} and related classes.
 * <p>
 * This object is immutable. Use {@link Builder} to create a new instance.
 */
public class ValidationConfiguration {
    private final boolean validateInvariants;
    private final boolean failOnUnknownTerminologyId;

    private ValidationConfiguration(Builder builder) {
        this.validateInvariants = builder.validateInvariants;
        this.failOnUnknownTerminologyId = builder.failOnUnknownTerminologyId;
    }

    /**
     * Get whether to validate invariants or not.
     *
     * @return whether to validate invariants or not
     */
    public boolean isValidateInvariants() {
        return validateInvariants;
    }

    /**
     * Get whether to fail validation or not if an uknown terminology is encountered.
     *
     * @return whether to fail or not
     **/
    public boolean isFailOnUnknownTerminologyId() {
        return failOnUnknownTerminologyId;
    }

    /**
     * Builder for {@link ValidationConfiguration}.
     */
    public static class Builder {
        private boolean validateInvariants = true;
        private boolean failOnUnknownTerminologyId;

        /**
         * Set whether to validate invariants or not.
         * <p>
         * Default value: true
         *
         * @param validateInvariants whether to validate invariants or not
         */
        public Builder validateInvariants(boolean validateInvariants) {
            this.validateInvariants = validateInvariants;
            return this;
        }

        /**
         * Set whether to fail validation or not if an unknown terminology is encountered.
         * <p>
         * Default value: false
         *
         * @param failOnUnknownTerminologyId whether to fail or not
         */
        public Builder failOnUnknownTerminologyId(boolean failOnUnknownTerminologyId) {
            this.failOnUnknownTerminologyId = failOnUnknownTerminologyId;
            return this;
        }

        /**
         * Build a new {@link ValidationConfiguration} instance.
         */
        public ValidationConfiguration build() {
            return new ValidationConfiguration(this);
        }
    }
}
