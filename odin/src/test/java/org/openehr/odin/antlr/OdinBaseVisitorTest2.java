package org.openehr.odin.antlr;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openehr.odin.CompositeOdinObject;
import org.openehr.odin.loader.OdinLoaderImpl;

import static org.junit.Assert.assertEquals;

public class OdinBaseVisitorTest2 {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

//    @Test
//    public void loadReferenceModel1() throws Exception {
//        OdinLoaderImpl loader = new OdinLoaderImpl();
//        OdinVisitorImpl visitor = loader.loadOdinFile(OdinBaseVisitorTest2.class.getResourceAsStream("/odin/CIMI-RM-3.0.5_tweaked.bmm"));
//        assertEquals("Stack should consist of a single item", 1, visitor.getStack().size());
//        CompositeOdinObject root = visitor.getAstRootNode();
//    }

    @Test
    public void loadReferenceModel2() throws Exception {
        OdinLoaderImpl loader = new OdinLoaderImpl();
        OdinVisitorImpl visitor = loader.loadOdinFile(OdinBaseVisitorTest2.class.getResourceAsStream("/odin/CIMI_RM_CLINICAL.v.0.0.1.bmm"));
        assertEquals("Stack should consist of a single item", 1, visitor.getStack().size());
        CompositeOdinObject root = visitor.getAstRootNode();
    }
//
//    @Test
//    public void loadReferenceModel3() throws Exception {
//        OdinLoaderImpl loader = new OdinLoaderImpl();
//        OdinVisitorImpl visitor = loader.loadOdinFile(OdinBaseVisitorTest2.class.getResourceAsStream("/odin/CIMI_RM_CORE.v.0.0.1.bmm"));
//        assertEquals("Stack should consist of a single item", 1, visitor.getStack().size());
//        CompositeOdinObject root = visitor.getAstRootNode();
//    }
//
//    @Test
//    public void loadReferenceModel4() throws Exception {
//        OdinLoaderImpl loader = new OdinLoaderImpl();
//        OdinVisitorImpl visitor = loader.loadOdinFile(OdinBaseVisitorTest2.class.getResourceAsStream("/odin/CIMI_RM_FOUNDATION.v.0.0.1.bmm"));
//        assertEquals("Stack should consist of a single item", 1, visitor.getStack().size());
//        CompositeOdinObject root = visitor.getAstRootNode();
//    }
}