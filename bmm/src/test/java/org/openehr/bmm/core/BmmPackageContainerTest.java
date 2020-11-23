package org.openehr.bmm.core;

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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BmmPackageContainerTest {

    private BmmPackage level_0;
    private BmmPackage level_0_1;
    private BmmPackage level_0_2;
    private BmmPackage level_0_1_1;
    private BmmPackage level_0_1_2;
    private BmmPackage level_0_2_1;
    private BmmPackage level_0_2_2;
    private BmmPackage level_0_1_1_1;
    private BmmModel rootPackage;

    @Before
    public void setup() {
        rootPackage = new BmmModel();

        level_0 = new BmmPackage("level_0");
        level_0_1 = new BmmPackage("level_0_1");
        level_0_2 = new BmmPackage("level_0_2");
        level_0_1_1 = new BmmPackage("level_0_1_1");
        level_0_1_2 = new BmmPackage("level_0_1_2");
        level_0_2_1 = new BmmPackage("level_0_2_1");
        level_0_2_2 = new BmmPackage("level_0_2_2");
        level_0_1_1_1 = new BmmPackage("level_0_1_1_1");
        level_0.addPackage(level_0_1);
        level_0.addPackage(level_0_2);
        level_0_1.addPackage(level_0_1_1);
        level_0_1.addPackage(level_0_1_2);
        level_0_2.addPackage(level_0_2_1);
        level_0_2.addPackage(level_0_2_2);
        level_0_1_1.addPackage(level_0_1_1_1);
        rootPackage.addPackage(level_0);

    }

    @Test
    public void packageAtPath() throws Exception {
        assertEquals(level_0_2_2, rootPackage.packageAtPath("level_0.level_0_2.level_0_2_2"));
        assertEquals(level_0_1_1, rootPackage.packageAtPath("level_0.level_0_1.level_0_1_1"));
        assertEquals(level_0_2, rootPackage.packageAtPath("level_0.level_0_2"));
        assertNull(rootPackage.packageAtPath("level_0.level_0_1.level_0_1_3"));
        assertEquals(level_0_1_1_1, rootPackage.packageAtPath("level_0.level_0_1.level_0_1_1.level_0_1_1_1"));
        assertEquals(level_0, rootPackage.packageAtPath("level_0"));
        assertNull(rootPackage.packageAtPath("level_4"));
        assertNull(rootPackage.packageAtPath("level_0.level_0_1.level_0_1_1.level_0_1_1_1.level_0_1_1_1_1"));
    }

    @Test
    public void doRecursivePackages() throws Exception {

    }

    @Test
    public void hasPackagePath() throws Exception {
        assertTrue(rootPackage.hasPackagePath("level_0.level_0_2.level_0_2_2"));
        assertTrue(rootPackage.hasPackagePath("level_0.level_0_1.level_0_1_1"));
        assertTrue(rootPackage.hasPackagePath("level_0.level_0_2"));
        assertFalse(rootPackage.hasPackagePath("level_0.level_0_1.level_0_1_3"));
        assertTrue(rootPackage.hasPackagePath("level_0.level_0_1.level_0_1_1.level_0_1_1_1"));
        assertTrue(rootPackage.hasPackagePath("level_0"));
        assertFalse(rootPackage.hasPackagePath("level_4"));
        assertFalse(rootPackage.hasPackagePath("level_0.level_0_1.level_0_1_1.level_0_1_1_1.level_0_1_1_1_1"));
    }


}