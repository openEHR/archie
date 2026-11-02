package com.nedap.archie.definitions;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017 Cognitive Medical Systems
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status of a versioned artefact, as one of a number of possible values: uncontrolled, prerelease, release, build.
 *
 * Created by cnanjo on 5/18/16.
 */
public enum VersionStatus {
    /**
     * Value representing a version which is ‘unstable’, i.e. contains an unknown size of change with
     * respect to its base version. Rendered with the build number as a string in the form “N.M.P-alpha.B”
     * e.g. “2.0.1-alpha.154”.
     *
     */
    ALPHA("alpha"),
    /**
     * Value representing a version which is ‘beta’, i.e. contains an unknown but reducing size of change
     * with respect to its base version. Rendered with the build number as a string in the form “N.M.P-beta.B”
     * e.g. “2.0.1-beta.154”.
     */
    BETA("beta"),
    /**
     * Value representing a version which is ‘release candidate’, i.e. contains only patch-level changes on
     * the base version. Rendered as a string as “N.M.P-rc.B” e.g. “2.0.1-rc.27”.
     */
    RELEASE_CANDIDATE("rc"),
    /**
     * Value representing a version which is ‘released’, i.e. is the definitive base version. Rendered with the
     * build number as a string in the form “N.M.P” e.g. “2.0.1”.
     */
    RELEASED(""),
    /**
     * Value representing a version which is a build of the current base release. Rendered with the build number
     * as a string in the form “N.M.P+B” e.g. “2.0.1+33”.
     */
    BUILD("+");

    private final String value;

    VersionStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VersionStatus getEnum(String value) {
        if (value.startsWith("-")) {
            value = value.substring(1);
        }
        for (VersionStatus versionStatus : values()) {
            if (versionStatus.getValue().equals(value)) {
                return versionStatus;
            }
        }
        throw new IllegalArgumentException("Unknown String value: " + value);
    }
}