package org.openehr.odin.loader;

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

import com.nedap.archie.adlparser.antlr.odinLexer;
import com.nedap.archie.adlparser.antlr.odinParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.openehr.odin.antlr.OdinVisitorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class OdinLoaderImpl {

    private static Logger log = LoggerFactory.getLogger(OdinLoaderImpl.class);

    public OdinVisitorImpl<?> loadOdinFile(String bmmFilePath) {
        try {
         return loadOdinFile(CharStreams.fromFileName(bmmFilePath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("Error loading reference model", ioe);
            throw new RuntimeException("Error loading reference model", ioe);
        }
    }

    public OdinVisitorImpl<?> loadOdinFromString(String odinContent) {
        return loadOdinFile(CharStreams.fromString(odinContent));
    }

    public OdinVisitorImpl<?> loadOdinFile(InputStream inputStream) {
        try {
            return loadOdinFile(CharStreams.fromStream(inputStream));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("Error loading odin file", ioe);
            throw new RuntimeException("Error loading odin file", ioe);
        }
    }

    public OdinVisitorImpl<?> loadOdinFile(CharStream input) {

        OdinVisitorImpl<?> visitor = new OdinVisitorImpl<>();
        odinLexer lexer = new odinLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        odinParser parser = new odinParser(tokens);
        ANTLRParserErrors errors = new ANTLRParserErrors();
        ArchieErrorListener listener = new ArchieErrorListener(errors);
        parser.addErrorListener(listener);
        ParseTree tree = parser.odin_text();
        visitor.visit(tree);
        if (errors.hasErrors()) {
            throw new RuntimeException("errors parsing ODIN file: " + errors);
        }
        return visitor;

    }


}
