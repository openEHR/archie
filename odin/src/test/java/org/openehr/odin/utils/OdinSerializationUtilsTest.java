package org.openehr.odin.utils;

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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OdinSerializationUtilsTest {

    @Test
    public void buildOdinStringObject() throws Exception {
        assertEquals("<\"content\">", OdinSerializationUtils.buildOdinStringObject("content"));
        assertEquals("bmm_version = <\"2.1\">\n", OdinSerializationUtils.buildOdinStringObjectPropertyInitialization("bmm_version", "2.1"));
    }

    @Test
    public void buildOdinStringListObjectCase1() throws Exception {
        List<String> stringList = new ArrayList<>();
        assertEquals("<...>", OdinSerializationUtils.buildOdinStringList(stringList));
    }

    @Test
    public void buildOdinStringListObjectCase2() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add("item1");
        assertEquals("<\"item1\",...>", OdinSerializationUtils.buildOdinStringList(stringList));
    }

    @Test
    public void buildOdinStringListObjectCase3() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add("item1");
        stringList.add("item2");
        assertEquals("<\"item1\", \"item2\">", OdinSerializationUtils.buildOdinStringList(stringList));
    }

    @Test
    public void buildOdinStringListObjectCase4() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add("item1");
        stringList.add("item2");
        stringList.add("item3");
        assertEquals("<\"item1\", \"item2\", \"item3\">", OdinSerializationUtils.buildOdinStringList(stringList));
    }

    @Test
    public void buildOdinStringListObjectCase5() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add("item1");
        stringList.add("...");
        assertEquals("<\"item1\",...>", OdinSerializationUtils.buildOdinStringList(stringList));
    }

}