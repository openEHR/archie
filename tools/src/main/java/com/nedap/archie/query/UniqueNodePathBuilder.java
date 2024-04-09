package com.nedap.archie.query;

import com.nedap.archie.apath.PathSegment;
import org.w3c.dom.Node;

import java.util.Optional;

/**
 * Constructs unique paths to archetype DOM node objects.
 *
 * Created by pieter.bos on 11/05/16.
 */
public class UniqueNodePathBuilder {

    public static String constructPath(Node node) {
        return constructPathInner(node).toString();
    }

    private static StringBuilder constructPathInner(Node node) {
        Node parent = node.getParentNode();
        if (parent == null || parent.getParentNode() == null) {
            return new StringBuilder("");
        } else {
            String archetypeNodeId = Optional.ofNullable(node.getAttributes().getNamedItem("archetype_node_id"))
                .map(Node::getNodeValue)
                .orElse(null);
            String pathSegment = new PathSegment(node.getNodeName(), archetypeNodeId, findNodeIndex(node, parent)).toString();
            return constructPathInner(parent).append(pathSegment);
        }
    }

    private static Integer findNodeIndex(Node node, Node parent) {
        Integer nodeIndex = null;
        int numberOfFoundNodes = 0;

        for(int i = 0; i < parent.getChildNodes().getLength(); i++) {
            //nodes with same name in XML means in the same collection in Java
            if(parent.getChildNodes().item(i).getNodeName().equals(node.getNodeName())) {
                numberOfFoundNodes++;
                if (parent.getChildNodes().item(i) == node) {
                    nodeIndex = numberOfFoundNodes;
                }
            }
        }

        if (numberOfFoundNodes <= 1) {
            return null;
        } else {
            return nodeIndex;
        }
    }
}
