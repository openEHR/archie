package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmSchema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

/**
 * Performs an operation on BmmClasses, once per class.
 * The order is in the dependency order - that is, should any class need a dependency, that gets processed before it is
 * supplied to that class.
 * If this would cause StackOverflowErrors, just returns the class half processed to fix cross-dependencies
 *
 * Might be better to find a way to get rid of this altogether - complex and very tricky to see what it does!
 */
public class BmmClassProcessor {


    private final BmmModel model;
    private final BiFunction<PBmmClass, BmmClassProcessor, BmmClass> populateBmmClass;
    private final Map<String, PBmmClass> pBmmClassMap;
    private final Set<String> unprocessedClasses;

    private final Stack<String> processingStack;

    public BmmClassProcessor(BmmModel model, PBmmSchema pBmmSchema, BiFunction<PBmmClass, BmmClassProcessor, BmmClass> populateBmmClass) {
        this.model = model;
        unprocessedClasses = new LinkedHashSet<>();
        pBmmClassMap = new LinkedHashMap<>();
        processingStack = new Stack<>();
        pBmmSchema.getPrimitiveTypes().forEach( (name, clazz) -> {
            pBmmClassMap.put(name.toUpperCase(), clazz);
            unprocessedClasses.add(name.toUpperCase());
        });
        pBmmSchema.getClassDefinitions().forEach( (name, clazz) -> {
            pBmmClassMap.put(name.toUpperCase(), clazz);
            unprocessedClasses.add(name.toUpperCase());
        });
        this.populateBmmClass = populateBmmClass;
        //TODO: sort on descendant tree order, better heuristic than order in which it is defined
    }

    public void run() {
        for(String className:new ArrayList<>(unprocessedClasses)) {
            processBmmClassIfNeeded(className);
        }
    }

    public BmmClass getClassDefinition(String typeName) {
        processBmmClassIfNeeded(typeName);
        return model.getClassDefinition(typeName);
    }

    private void processBmmClassIfNeeded(String typeName) {
        if(unprocessedClasses.contains(typeName.toUpperCase()) && !processingStack.contains(typeName.toUpperCase())) {
            try {
                processingStack.push(typeName.toUpperCase());
                BmmClass apply = populateBmmClass.apply(pBmmClassMap.get(typeName.toUpperCase()), this);
                unprocessedClasses.remove(typeName.toUpperCase());
            } finally {
                processingStack.pop();
            }
        }
    }

    public BmmClass getAnyClassDefinition() {
        BmmClass anyClassDefinition = model.getAnyClassDefinition();
        processBmmClassIfNeeded(anyClassDefinition.getTypeName());
        return model.getAnyClassDefinition();
    }

    public BmmClass getUnprocessedClassDefinition(String name) {
        return model.getClassDefinition(name);
    }
}
