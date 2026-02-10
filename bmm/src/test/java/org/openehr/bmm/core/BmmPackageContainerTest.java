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

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BmmPackageContainerTest {
    @Test
    public void packageAtPath() throws Exception {

    }

    @Test
    public void doRecursivePackages() throws Exception {

    }

    @Test
    public void hasPackagePath() throws Exception {
        BmmPackageContainer container = createPackageContainer();
        assertTrue(container.hasPackagePath("level_0.level_0_2.level_0_2_2"));
        assertTrue(container.hasPackagePath("level_0.level_0_1.level_0_1_1"));
        assertTrue(container.hasPackagePath("level_0.level_0_2"));
        assertFalse(container.hasPackagePath("level_0.level_0_1.level_0_1_3"));
        assertTrue(container.hasPackagePath("level_0.level_0_1.level_0_1_1.level_0_1_1_1"));
        assertTrue(container.hasPackagePath("level_0"));
        assertFalse(container.hasPackagePath("level_4"));
        assertFalse(container.hasPackagePath("level_0.level_0_1.level_0_1_1.level_0_1_1_1.level_0_1_1_1_1"));
    }

    @Test
    public void packageExistsInHierarchy() throws Exception {
        BmmPackageContainer container = createPackageContainer();
        assertTrue(container.hasPackageNameInHierarchy(new BmmPackage("level_0_1_2")));
        assertTrue(container.hasPackageNameInHierarchy(new BmmPackage("level_0_1_1_1")));
        assertFalse(container.hasPackageNameInHierarchy(new BmmPackage("level_3")));
    }

    public BmmPackage createPackageContainer() {
        BmmPackage root = new BmmPackage("level_0");
        BmmPackage level_0_1 = new BmmPackage("level_0_1");
        BmmPackage level_0_2 = new BmmPackage("level_0_2");
        BmmPackage level_0_1_1 = new BmmPackage("level_0_1_1");
        BmmPackage level_0_1_2 = new BmmPackage("level_0_1_2");
        BmmPackage level_0_2_1 = new BmmPackage("level_0_2_1");
        BmmPackage level_0_2_2 = new BmmPackage("level_0_2_2");
        BmmPackage level_0_1_1_1 = new BmmPackage("level_0_1_1_1");
        root.addPackage(level_0_1);
        root.addPackage(level_0_2);
        level_0_1.addPackage(level_0_1_1);
        level_0_1.addPackage(level_0_1_2);
        level_0_2.addPackage(level_0_2_1);
        level_0_2.addPackage(level_0_2_2);
        level_0_1_1.addPackage(level_0_1_1_1);
        return root;
    }
}