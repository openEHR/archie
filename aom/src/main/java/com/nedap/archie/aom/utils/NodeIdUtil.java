package com.nedap.archie.aom.utils;

import com.google.common.base.Joiner;
import com.nedap.archie.definitions.AdlCodeDefinitions;

import java.util.ArrayList;
import java.util.List;

public class NodeIdUtil {


    private String prefix;
    private List<Integer> codes = new ArrayList<>();;

    public NodeIdUtil(String nodeId) {
        if(AOMUtils.isValidCode(nodeId) || AOMUtils.isValidADL14Code(nodeId)) {
            String[] split = nodeId.substring(2).split("\\" + AdlCodeDefinitions.SPECIALIZATION_SEPARATOR);
            prefix = nodeId.substring(0, 2);
            for (int i = 0; i < split.length; i++) {
                codes.add(Integer.parseInt(split[i]));
            }
        }
    }

    public boolean isRedefined() {
        if(!isValid()) {
            return false;
        }
        if(codes.size() > 1) {
            for(int i = 0; i < codes.size()-1;i++) {
                if(codes.get(i) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValid() {
        return prefix != null;
    }


    public boolean isIdCode() {
        return AdlCodeDefinitions.ID_CODE_LEADER.equals(prefix);
    }

    public boolean isValueCode() {
        return AdlCodeDefinitions.VALUE_CODE_LEADER.equals(prefix);
    }

    public boolean isValueSetCode() {
        return AdlCodeDefinitions.VALUE_SET_CODE_LEADER.equals(prefix);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Integer> getCodes() {
        return codes;
    }

    public String toString() {
        return prefix + Joiner.on('.').join(codes);

    }


}
