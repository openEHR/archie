package org.openehr.docgen;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.bmm.rmaccess.ReferenceModelAccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Copyright 2017 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by cnanjo on 3/7/17.
 */

@Ignore
public class DocumentGeneratorTest {

    private ReferenceModelAccess access;

    @Before
    public void setup() {
        try {
            List<String> schemaDirectories = new ArrayList<>();
            String path = DocumentGeneratorTest.class.getResource("/cimi/CIMI-RM-3.0.5.bmm").getFile();
            path = path.substring(0, path.lastIndexOf('/'));
            schemaDirectories.add(path);
            access = new ReferenceModelAccess();
            access.setSchemaDirectories(schemaDirectories);
            //access.initializeAll();
        } catch(Exception e) {
            e.printStackTrace();
            fail("Error initializing test");
        }
    }
    @Test
    public void generateDocument() throws Exception {
        assertNotNull(access.getValidModels().get("cimi_rm_clinical_0.0.3".toUpperCase()));
        DocumentGenerator generator = new DocumentGenerator();
        File file = new File(DocumentGeneratorTest.class.getResource("/templates/").getFile());
        assertTrue(file.isDirectory());
        generator.configure(file);
        generator.setOutputDirectory("/Users/cnanjo/work/cimi_doc");
        generator.generateDocument(access.getValidModels().get("cimi_rm_clinical_0.0.3".toUpperCase()));
    }

}