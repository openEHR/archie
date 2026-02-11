package org.openehr.bmm.utils;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017  Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReferenceModelVersionUtilsTest {
    @Test
    public void validateVersion() throws Exception {
        assertTrue(ReferenceModelVersionUtils.validateVersion("12"));
        assertTrue(ReferenceModelVersionUtils.validateVersion("12.3"));
        assertTrue(ReferenceModelVersionUtils.validateVersion("12.3.11"));
        assertFalse(ReferenceModelVersionUtils.validateVersion("12.3.11.1"));
        assertFalse(ReferenceModelVersionUtils.validateVersion("a1.r4.23"));
    }


}