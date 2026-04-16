package com.nedap.archie.diff;

import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.CodeRedefinitionStatus;

import java.util.*;

/**
 * Calculates the Longest common subsequence along two sets of node ids. Useful for sibling order diffing
 *
 * Or perhaps diffing in general with a few modifications?
 */
public class NodeIdLCS {


    private List<String> lcs;
    //mapping from node id in child to parent. Used when id4 is replaced by id4.1 without cloning
    private Map<String, String> nodeIdReplacements = new HashMap<>();

    public NodeIdLCS(List<String> parent, List<String> child, int childSpecializationDepth) {
        calculateReplacements(parent, child, childSpecializationDepth);
        lcs = longestCommonSubsequence(parent, child);
    }

    public List<String> longestCommonSubsequence(final List<String> parent, final List<String> child) {

        // Quick return
        if (parent == null || child == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }
        List<String> longestCommonSubString = new ArrayList<>(Math.max(parent.size(), child.size()));

        int[][] lcsLengthArray = longestCommonSubstringLengthArray(parent, child);
        int i = parent.size() - 1;
        int j = child.size() - 1;
        int k = lcsLengthArray[parent.size()][child.size()] - 1;
        while (k >= 0) {
            if (equals(parent.get(i), child.get(j))) {
                longestCommonSubString.add(parent.get(i));
                i = i - 1;
                j = j - 1;
                k = k - 1;
            } else if (lcsLengthArray[i + 1][j] < lcsLengthArray[i][j + 1]) {
                i = i - 1;
            } else {
                j = j - 1;
            }
        }
        Collections.reverse(longestCommonSubString);
        return longestCommonSubString;
    }

    private void calculateReplacements(List<String> parent, List<String> child, int childSpecializationDepth) {
        for(String nodeId:child) {
            if(AOMUtils.getSpecialisationStatusFromCode(nodeId, childSpecializationDepth) == CodeRedefinitionStatus.REDEFINED) {
                String parentNodeId = AOMUtils.getCodeInNearestParent(nodeId);
                if(!child.contains(parentNodeId)) {
                    //replacement!
                    nodeIdReplacements.put(nodeId, parentNodeId);
                }

            }
        }
    }

    public int[][] longestCommonSubstringLengthArray(final List<String> parent, final List<String> child) {
        int[][] lcsLengthArray = new int[parent.size() + 1][child.size() + 1];
        for (int i=0; i < parent.size(); i++) {
            for (int j=0; j < child.size(); j++) {
                if (i == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (j == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (equals(parent.get(i), child.get(j))) {
                    lcsLengthArray[i + 1][j + 1] = lcsLengthArray[i][j] + 1;
                } else {
                    lcsLengthArray[i + 1][j + 1] = Math.max(lcsLengthArray[i + 1][j], lcsLengthArray[i][j + 1]);
                }
            }
        }
        return lcsLengthArray;
    }

    public boolean equals(String parent, String child) {
        String replacedChild = nodeIdReplacements.get(child);
        if(replacedChild == null) {
            replacedChild = child;
        }
        //left is always parent, right always child. Might be useful here  for ordering tricks?
        return parent.equals(replacedChild);
    }

    public boolean contains(String childNodeId) {
        String replacedChild = nodeIdReplacements.get(childNodeId);
        if(replacedChild == null) {
            replacedChild = childNodeId;
        }
        return lcs.contains(replacedChild);
    }

    public List<String> getLCS() {
        return lcs;
    }

    public boolean isLast(String nodeId) {
        return equals(lcs.get(lcs.size() - 1), nodeId);
    }
}
