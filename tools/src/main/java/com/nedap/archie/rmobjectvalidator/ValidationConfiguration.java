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
    private final Set<String> disabledInvariants;
    private final boolean failOnUnknownTerminologyId;

    private ValidationConfiguration(Builder builder) {
        this.validateInvariants = builder.validateInvariants == null || builder.validateInvariants;
        this.enabledInvariants = builder.enabledInvariants;
        this.disabledInvariants = builder.disabledInvariants;
        this.failOnUnknownTerminologyId = builder.failOnUnknownTerminologyId;
    }

    /**
     * Get whether to validate invariants or not. If true, all invariants are validated, unless
     * {@link #getEnabledInvariants()} or {@link #getDisabledInvariants()} is set, which limit the invariants that
     * are validated.
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
     * Get the invariants that are disabled, in the form RM_TYPE_NAME.Invariant_name, for example DV_TEXT.Language_valid.
     * If set, all other invariants are validated.
     *
     * @return the disabled invariants, or null if not set
     */
    public Set<String> getDisabledInvariants() {
        return disabledInvariants;
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
        private Boolean validateInvariants;
        private Set<String> enabledInvariants;
        private Set<String> disabledInvariants;
        private boolean failOnUnknownTerminologyId;

        /**
         * Set whether to validate invariants or not. If true, all invariants are validated, if false, none are.
         * <p>
         * Use only one of this, {@link #enabledInvariants(Set)} and {@link #disabledInvariants(Set)}. Setting more than
         * one makes {@link #build()} throw an {@link IllegalStateException}.
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
         * Use only one of this, {@link #validateInvariants(boolean)} and {@link #disabledInvariants(Set)}. Setting more
         * than one makes {@link #build()} throw an {@link IllegalStateException}.
         * <p>
         * Every name must match an invariant that is not ignored, otherwise the {@link RMObjectValidator}
         * constructor throws an {@link IllegalArgumentException}.
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
         * Validate all invariants except the given ones, for example {@code RMInvariants.DV_TEXT.LANGUAGE_VALID}.
         * Disabling an invariant on a type also disables it on its subtypes.
         * Use only one of this, {@link #validateInvariants(boolean)} and {@link #enabledInvariants(Set)}. Setting more
         * than one makes {@link #build()} throw an {@link IllegalStateException}.
         * <p>
         * Every name must match an existing invariant, otherwise the {@link RMObjectValidator} constructor throws an
         * {@link IllegalArgumentException}. Disabling an ignored invariant only logs a warning, as it is never
         * validated anyway.
         * <p>
         * Default value: null
         *
         * @param disabledInvariants the invariants not to validate
         */
        public Builder disabledInvariants(Set<String> disabledInvariants) {
            this.disabledInvariants = disabledInvariants == null ? null : Set.copyOf(disabledInvariants);
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
         *
         * @throws IllegalStateException if more than one of {@link #validateInvariants(boolean)},
         *                               {@link #enabledInvariants(Set)} and {@link #disabledInvariants(Set)} is set
         */
        public ValidationConfiguration build() {
            int invariantOptionsSet = (validateInvariants != null ? 1 : 0)
                    + (enabledInvariants != null ? 1 : 0)
                    + (disabledInvariants != null ? 1 : 0);
            if (invariantOptionsSet > 1) {
                throw new IllegalStateException("Set only one of validateInvariants, enabledInvariants and disabledInvariants");
            }
            return new ValidationConfiguration(this);
        }
    }
}
