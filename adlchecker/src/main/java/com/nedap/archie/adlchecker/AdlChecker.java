package com.nedap.archie.adlchecker;

import com.google.common.io.CharStreams;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.antlr.errors.ANTLRParserMessage;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationMessage;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AdlChecker {

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("AdlChecker")
                .defaultHelp(true)
                .description("Checks the syntax of ADL files");

        parser.addArgument("path").nargs("*")
                .help("file or directory that contains archetypes to validate");

        parser.addArgument("-f", "--outputFlat")
                .action(Arguments.storeTrue())
                .help("if the --outputFlat flag is present, also output the flat ADL");

        parser.addArgument("-l", "--lint")
                .action(Arguments.storeTrue())
                .help("if the --lint flag is present, also output the linted ADL, which formats and adds missing id codes");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        if(ns.getList("path").isEmpty()) {
            parser.printUsage();
            parser.printHelp();
        } else {
            validateArchetypes(ns.getList("path"), ns.getBoolean("outputFlat"), ns.getBoolean("lint"));
        }
    }

    private static void validateArchetypes(List<String> directories, boolean printFlatAdl, boolean lint) {

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        for (String directory : directories) {
            System.out.println("step 1: parsing archetypes");
            System.out.println();
            try {
                Files.walk(Paths.get(directory)).forEach((path) -> parseArchetype(path, repository));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        repository.compile(BuiltinReferenceModels.getMetaModels());

        System.out.println("step 2: validations");

        for(ValidationResult result:repository.getAllValidationResults()) {
            printValidationResult(result);
            if(printFlatAdl && result.passes()) {
                System.out.println(ADLArchetypeSerializer.serialize(result.getFlattened()));
            }

        }

        if(lint) {
            System.out.println("step 1: running archetypes through linter");
            System.out.println();
            for (String directory : directories) {
                try {
                    Files.walk(Paths.get(directory)).forEach((path) -> lint(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void lint(Path path) {
        File file = path.toFile();
        if(file.isDirectory()) {
            return;
        }
        try (FileInputStream stream = new FileInputStream(file)) {
            String fileContent = CharStreams.toString(new InputStreamReader(stream));
            System.out.println("linting " + file.getAbsolutePath());
            System.out.println();
            TerminologyContentGenerator generator = new TerminologyContentGenerator(BuiltinReferenceModels.getMetaModels());
            Archetype resultingArchetype = generator.addTerms(fileContent);
            System.out.println(ADLArchetypeSerializer.serialize(resultingArchetype));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void printValidationResult(ValidationResult result) {
        printHeader(result.getArchetypeId(), result.passes() ? "PASSED" : "FAILED");
        for(ValidationMessage error:result.getErrors()) {
            System.out.println(error.toString());
        }
        for(ValidationResult overlayResult:result.getOverlayValidations()) {
            printValidationResult(overlayResult);
        }
    }

    private static void printHeader(String archetypeId, String status) {
        System.out.println();
        System.out.print("============= ");
        System.out.print(archetypeId);
        System.out.print("      ");
        System.out.print(status);
        System.out.print(" =============");
        System.out.println();
        System.out.println();
    }

    private static void parseArchetype(Path path, InMemoryFullArchetypeRepository repository) {
        File file = path.toFile();
        if(file.isDirectory()) {
            return;
        }
        ADLParser adlParser = new ADLParser();
        adlParser.setLogEnabled(false);
        try (FileInputStream stream = new FileInputStream(file)) {
            try {
                Archetype parsed = adlParser.parse(stream);
                if(adlParser.getErrors().hasNoErrors()) {
                    repository.addArchetype(parsed);
                }
                if(!adlParser.getErrors().hasNoMessages()){
                    printParseErrors(path, adlParser);
                }
            } catch (Exception e) {
                printParseErrors(path, adlParser);
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("error opening file");
            e.printStackTrace();
        }
    }

    private static void printParseErrors(Path path, ADLParser adlParser) {
        if(adlParser.getErrors() == null) {
            printHeader(path.getFileName().toString(), "PARSING FAILED");
            return;
        }
        else if(adlParser.getErrors().hasNoErrors()) {
            printHeader(path.getFileName().toString(), "PARSING GENERATED WARNINGS");
        } else {
            printHeader(path.getFileName().toString(), "PARSING FAILED");
        }
        System.out.println("errors found for " + path.getFileName());

        if(adlParser.getErrors() != null) {
            for (ANTLRParserMessage message : adlParser.getErrors().getWarnings()) {
                System.err.println("warning: " + message.getMessage());
            }
            for (ANTLRParserMessage message : adlParser.getErrors().getErrors()) {
                System.err.println("error: " + message.getMessage());
            }

        }
    }

}