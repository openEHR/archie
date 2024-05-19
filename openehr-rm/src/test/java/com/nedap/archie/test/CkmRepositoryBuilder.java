package com.nedap.archie.test;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.*;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by pieter.bos on 06/04/16.
 */
public class CkmRepositoryBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CkmRepositoryBuilder.class);

    public static FullArchetypeRepository parseCKM() {
        return parseCKM(".*\\.adls");
    }

    public static FullArchetypeRepository parseCKM(String filter) {
        InMemoryFullArchetypeRepository result = new InMemoryFullArchetypeRepository();
        Reflections reflections = new Reflections("ckm-mirror", Scanners.Resources);

        List<String> adlFiles = new ArrayList<>(reflections.getResources(Pattern.compile(filter)));

        for(String file:adlFiles) {
            Archetype archetype;
            ANTLRParserErrors errors;
            try (InputStream stream = CkmRepositoryBuilder.class.getResourceAsStream("/" + file)) {
                ADLParser parser = new ADLParser();
                parser.setLogEnabled(false);
                archetype = parser.parse(stream);
                errors = parser.getErrors();
                if (errors.hasNoErrors()) {
                    result.addArchetype(archetype);
                } else {
                    logger.warn("error parsing archetype: {}", errors);
                }
            } catch (Exception e) {
                logger.warn("exception parsing archetype {}", file, e);
            }
        }
        return result;
    }


}
