package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.aom.ArchetypeModelObject;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class FilePositionUtil {

    public static void setFilePosition(TerminalNode node, ArchetypeModelObject aomObject) {
        aomObject.setStartLine(node.getSymbol().getLine());
        aomObject.setStartCharInLine(node.getSymbol().getCharPositionInLine());
        aomObject.setTokenLength(node.getSymbol().getText().length());

    }

    public static void setFilePosition(ParserRuleContext context, ArchetypeModelObject aomObject) {
        aomObject.setStartLine(context.getStart().getLine());
        aomObject.setStartCharInLine(context.getStart().getCharPositionInLine());
        aomObject.setTokenLength(context.getText().length());

    }
}
