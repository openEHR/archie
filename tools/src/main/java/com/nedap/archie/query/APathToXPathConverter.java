package com.nedap.archie.query;

import com.google.common.collect.ImmutableSet;
import com.nedap.archie.adlparser.antlr.XPathLexer;
import com.nedap.archie.adlparser.antlr.XPathParser;
import com.nedap.archie.adlparser.antlr.XPathParser.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts an APath to an XPath-query
 * <p>
 * Created by pieter.bos on 11/05/16.
 */
public class APathToXPathConverter {

    //Pattern is thread-safe and immutable, so fine to store here for performance reasons
    private static Pattern idCodePattern = Pattern.compile("id(\\d|\\.\\d)+");
    private static Pattern numberPattern = Pattern.compile("\\d+");
    //warning: do NOT modify this set, only create it!
    private static Set<String> literalsThatShouldHaveSpacing = new ImmutableSet.Builder<String>().add("and", "or", ",", "-", "+", "*", "|", "<", ">", "<=", ">=").build();


    public static String convertQueryToXPath(String query, String firstNodeName) {
        String convertedQuery = convertWithAntlr(query);
        if (convertedQuery.equals("/")) {
            return "/" + firstNodeName;
        } else if (query.startsWith("//")) {
            return convertedQuery;
        } else if (query.startsWith("/")) {
            return "/" + firstNodeName + convertedQuery;
        } else {
            return convertedQuery;
        }

    }


    public static String convertWithAntlr(String query) {
        XPathLexer lexer = new XPathLexer(CharStreams.fromString(query));
        XPathParser parser = new XPathParser(new CommonTokenStream(lexer));
        MainContext mainCtx = parser.main();
        StringBuilder output = new StringBuilder();
        writeTree(output, mainCtx, false);
        return output.toString();

    }

    /**
     * Converts and ANTLR ParseTree of the XPath grammar to APath String output
     * Adds the output to the given StringBuilder.
     * <p>
     * Simple approach that writes every element, adding some whitespace where needed, and converting just a few elements
     *
     * @param output      the output to write to
     * @param tree        the parse tree
     * @param inPredicate whether we are inside or outside a predicate
     */
    private static void writeTree(StringBuilder output, ParseTree tree, boolean inPredicate) {

        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            if (child instanceof TerminalNode) {
                boolean shouldAppendSpace = literalsThatShouldHaveSpacing.contains(child.getText().toLowerCase());
                if (shouldAppendSpace) {
                    output.append(" ");
                }
                output.append(child.getText());
                if (shouldAppendSpace) {
                    output.append(" ");
                }
            } else if (child instanceof AndExprContext) {
                for (int j = 0; j < child.getChildCount(); j++) {
                    ParseTree andChild = child.getChild(j);
                    if (andChild instanceof TerminalNode) {
                        output.append(" and "); //this rewrites the "," syntax that is equivalent to 'and'
                    } else {
                        writeTree(output, andChild, andChild.getChildCount() < 3);
                    }
                }
            } else if (child instanceof PredicateContext) {
                writeTree(output, child, true);
            } else if (inPredicate && child instanceof RelativeLocationPathContext) {
                Matcher idCodeMatcher = idCodePattern.matcher(child.getText());

                if (idCodeMatcher.matches()) {
                    output.append("@archetype_node_id = '");
                    output.append(child.getText());
                    output.append("'");
                } else {
                    writeTree(output, child, true);
                }
            } else if (inPredicate && child instanceof FilterExprContext) {
                FilterExprContext filterExprContext = (FilterExprContext) child;
                //not sure if we should support [id5, 1]. This is not standard xpath!
                Matcher numberMatcher = numberPattern.matcher(child.getText());
                if (numberMatcher.matches()) {
                    output.append("position() = ");
                    output.append(child.getText());
                } else if (filterExprContext.primaryExpr().Literal() != null) {
                    output.append("name/value = ");
                    output.append(child.getText());
                } else {
                    writeTree(output, child, true);
                }
            } else {
                writeTree(output, child, inPredicate);
            } //TODO: rewrite id nodes and some other nodes when we find them in this very simple bit of code :)
        }
    }
}
