package com.nedap.archie.rules.evaluation;

import com.google.common.collect.ArrayListMultimap;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMQueryContext;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rmobjectvalidator.APathQueryCache;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.RuleElement;
import com.nedap.archie.rules.RuleStatement;
import com.nedap.archie.rules.evaluation.evaluators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by pieter.bos on 31/03/16.
 */
public class RuleEvaluation<T> {

    private static Logger logger = LoggerFactory.getLogger(RuleEvaluation.class);;

    private Archetype archetype;
    private List<Evaluator<?>> evaluators = new ArrayList<>();
    private HashMap<Class<?>, Evaluator<?>> classToEvaluator = new HashMap<>();
    private FunctionEvaluator functionEvaluator;

    //evaluation state
    private T root;
    private VariableMap variables;
    EvaluationResult evaluationResult;
    private List<AssertionResult> assertionResults;

    private ArrayListMultimap<RuleElement, ValueList> ruleElementValues = ArrayListMultimap.create();
    private FixableAssertionsChecker fixableAssertionsChecker;

    private ModelInfoLookup modelInfoLookup;

    private RMObjectCreator creator;

    private final JAXBContext jaxbContext;
    private RMQueryContext rmQueryContext;
    private APathQueryCache queryCache = new APathQueryCache();

    private final AssertionsFixer assertionsFixer;

    public RuleEvaluation(ModelInfoLookup modelInfoLookup, Archetype archetype) {
        this(modelInfoLookup, null, archetype);
    }

    /**
     * Deprecated. Use the constructor without the jaxbContext for new implementations. Here to ease transition
     * to the new method.
     * @param modelInfoLookup the model info lookup to make this rule evaluator for
     * @param jaxbContext the jaxb context, use for queries. If null, will use RMPahtQuery instead
     * @param archetype the archetype to evaluate rules for
     */
    @Deprecated
    public RuleEvaluation(ModelInfoLookup modelInfoLookup, JAXBContext jaxbContext, Archetype archetype) {
        this.jaxbContext = jaxbContext;
        this.modelInfoLookup = modelInfoLookup;
        this.creator = new RMObjectCreator(modelInfoLookup);
        assertionsFixer = new AssertionsFixer(this, creator);
        this.archetype = archetype;
        this.functionEvaluator = new FunctionEvaluator();
        add(new VariableDeclarationEvaluator());
        add(new ConstantEvaluator());
        add(new AssertionEvaluator());
        add(new BinaryOperatorEvaluator(modelInfoLookup, archetype));
        add(new UnaryOperatorEvaluator());
        add(new VariableReferenceEvaluator());
        add(new ModelReferenceEvaluator());
        add(new ForAllEvaluator());
        add(functionEvaluator);
    }

    private void add(Evaluator<?> evaluator) {
        evaluators.add(evaluator);
        for(Class<?> clazz: evaluator.getSupportedClasses()) {
            classToEvaluator.put(clazz, evaluator);
        }
    }

    public EvaluationResult evaluate(T root, List<RuleStatement> rules) {

        this.root = (T) modelInfoLookup.clone(root);

        refreshQueryContext();

        ruleElementValues = ArrayListMultimap.create();
        variables = new VariableMap();
        assertionResults = new ArrayList<>();
        evaluationResult = new EvaluationResult();

        fixableAssertionsChecker = new FixableAssertionsChecker(ruleElementValues);

        for(RuleStatement rule:rules) {
            evaluate(rule);
        }
        return evaluationResult;

    }

    public ValueList evaluate(RuleElement rule) {
        Evaluator evaluator = classToEvaluator.get(rule.getClass());
        if(evaluator != null) {
            ValueList valueList = evaluator.evaluate(this, rule);
            ruleElementValueSet(rule, valueList);
            logger.debug("evaluated rule: {}", valueList);
            return valueList;
        }
        throw new UnsupportedOperationException("no evaluator present for rule type " + rule.getClass().getSimpleName());
    }


    public T getRMRoot() {
        return root;
    }

    public void registerFunction(FunctionImplementation function) {
        functionEvaluator.registerFunction(function);
    }

    public VariableMap getVariableMap() {
        return variables;
    }

    private void ruleElementValueSet(RuleElement expression, ValueList values) {
        ruleElementValues.put(expression, values);
    }

    /**
     * Callback: an assertion has been evaluated with the given result
     */
    public void assertionEvaluated(String tag, Expression expression, ValueList valueList) {
        AssertionResult assertionResult = new AssertionResult();
        assertionResult.setTag(tag);
        assertionResult.setAssertion(expression);

        boolean result = valueList.getSingleBooleanResult();
        assertionResult.setResult(result);
        assertionResult.setRawResult(valueList);
        evaluationResult.addAssertionResult(assertionResult);

        fixableAssertionsChecker.checkAssertionForFixablePatterns(assertionResult, expression, 0);

        //Fix any assertions that should be fixed before processing the next rule
        //this means we can calculate a score, then use that score in the next rule
        //otherwise this would mean several passes through the evaluator
        Map<String, Object> valuesToUpdate = assertionsFixer.fixAssertions(archetype, assertionResult);
        for (String path : valuesToUpdate.keySet()) {
            Object value = valuesToUpdate.get(path);
            assertionResult.setSetPathValue(path, new ValueList(value));
        }

        //before re-evaluation, reset any overridden existence from evaluation?
    }


    public EvaluationResult getEvaluationResult() {
        return evaluationResult;
    }

    public ModelInfoLookup getModelInfoLookup() {
        return modelInfoLookup;
    }


    public List<RMObjectWithPath> findListWithPaths(String path) {
        if(rmQueryContext == null) {
            return queryCache.getApathQuery(path).findList(getModelInfoLookup(), getRMRoot());
        } else {
            try {
                return rmQueryContext.findListWithPaths(path);
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void refreshQueryContext() {
        if(jaxbContext != null) {
            //updating a single node does not seem to work with the default JAXB-implementation, so just reload the entire query
            //context
            rmQueryContext = new RMQueryContext(modelInfoLookup, root, jaxbContext);
        }
    }

    public List<Object> findList(String path) {
        if(rmQueryContext != null) {
            try {
                return rmQueryContext.findList(path);
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        } else {
            List<RMObjectWithPath> parentsWithPath = findListWithPaths(path);
            return parentsWithPath.stream().map(p -> p.getObject()).collect(Collectors.toList());
        }
    }

}
