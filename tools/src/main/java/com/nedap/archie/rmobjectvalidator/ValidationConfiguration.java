package com.nedap.archie.rmobjectvalidator;

import java.util.Set;

/**
 * Configuration for {@link RMObjectValidator} and related classes.
 * <p>
 * This object is immutable. Use {@link Builder} to create a new instance.
 */
public class ValidationConfiguration {
    private final boolean validateInvariants;
    private final Set<String> enabledInvariants;
    private final boolean failOnUnknownTerminologyId;

    private ValidationConfiguration(Builder builder) {
        this.validateInvariants = builder.validateInvariants;
        this.enabledInvariants = builder.enabledInvariants;
        this.failOnUnknownTerminologyId = builder.failOnUnknownTerminologyId;
    }

    /**
     * Get whether to validate invariants or not. If true, all invariants are validated. Ignored if
     * {@link #getEnabledInvariants()} is set.
     *
     * @return whether to validate invariants or not
     */
    public boolean isValidateInvariants() {
        return validateInvariants;
    }

    /**
     * Get the invariants that are enabled, in the form RM_TYPE_NAME.Invariant_name, for example DV_TEXT.Language_valid.
     * If null, {@link #isValidateInvariants()} determines whether all or no invariants are validated.
     *
     * @return the enabled invariants, or null if not set
     */
    public Set<String> getEnabledInvariants() {
        return enabledInvariants;
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
        private Set<String> enabledInvariants;
        private boolean failOnUnknownTerminologyId;

        /**
         * Set whether to validate invariants or not. If true, all invariants are validated, if false, none are.
         * <p>
         * Use either this or {@link #enabledInvariants(Set)}, not both. If {@link #enabledInvariants(Set)} is set,
         * this setting is ignored and only the enabled invariants are validated.
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
         * Validate only the given invariants, for example {@code RMInvariants.DV_TEXT.LANGUAGE_VALID}.
         * Use either this or {@link #validateInvariants(boolean)}, not both. If set, this takes precedence.
         * <p>
         * Default value: null
         *
         * @param enabledInvariants the invariants to validate
         */
        public Builder enabledInvariants(Set<String> enabledInvariants) {
            this.enabledInvariants = enabledInvariants == null ? null : Set.copyOf(enabledInvariants);
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
