package com.nedap.archie.rmutil;

import com.nedap.archie.adl14.ADL14NodeIDConverter;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMListener;
import com.nedap.archie.rminfo.RMTreeWalker;

import java.util.Objects;

/**
 * Converts OpenEHR RM data created with ADL 1.4 to RM data as legal with ADL 2, and the other way around.
 * Only works on the OpenEHR RM, no generic RM support.
 * Converts in place, so be sure to clone your objects first if that is undesired.
 */
public class RMADL2Converter {

    private final ModelInfoLookup lookup;

    public RMADL2Converter() {
        this.lookup = ArchieRMInfoLookup.getInstance();
    }

    public void convertToADL2(Object rmObject) {
        ToADL2ConvertingListener convertingListener = new ToADL2ConvertingListener();
        new RMTreeWalker(lookup).walk(rmObject, convertingListener);

    }

    public void convertToADL14(Object rmObject) {
        ToADL14ConvertingListener convertingListener = new ToADL14ConvertingListener();
        new RMTreeWalker(lookup).walk(rmObject, convertingListener);
    }

    private abstract class NodeIdConvertingListener implements RMListener {

        @Override
        public void enterObject(Object object) {
            String nodeId = lookup.getArchetypeNodeIdFromRMObject(object);
            if(nodeId != null) {
                if(shouldConvertNodeId(nodeId)) {
                    String newNodeId = convertNodeId(nodeId);
                    lookup.setArchetypeNodeId(object, newNodeId);
                }
            } else if (object instanceof CodePhrase) {
                CodePhrase codePhrase = (CodePhrase) object;
                if(codePhrase.getTerminologyId() != null) {
                    if(Objects.equals("local", codePhrase.getTerminologyId().getValue())) {
                        String newValueCode = convertValueCode(codePhrase.getCodeString());
                        codePhrase.setCodeString(newValueCode);
                    }
                }
            }
        }

        abstract boolean shouldConvertNodeId(String nodeId);

        abstract String convertNodeId(String nodeId);

        abstract String convertValueCode(String codeString);

        @Override
        public void exitObject(Object object) {

        }
    }

    private class ToADL2ConvertingListener extends NodeIdConvertingListener {

        @Override
        boolean shouldConvertNodeId(String nodeId) {
            return AOMUtils.isValueCode(nodeId);
        }

        @Override
        String convertNodeId(String nodeId) {
            return ADL14NodeIDConverter.convertCode(nodeId, "id");
        }

        @Override
        String convertValueCode(String codeString) {
            return ADL14NodeIDConverter.convertCode(codeString, "at");
        }
    }

    private class ToADL14ConvertingListener extends NodeIdConvertingListener {

        @Override
        boolean shouldConvertNodeId(String nodeId) {
            return AOMUtils.isIdCode(nodeId);
        }

        @Override
        String convertNodeId(String nodeId) {
            return convertCodeToADL14(nodeId, "at");
        }

        @Override
        String convertValueCode(String codeString) {
            return convertCodeToADL14(codeString, "at");
        }
    }

    private static String convertCodeToADL14(String oldCode, String newCodePrefix) {
        NodeIdUtil nodeIdUtil = new NodeIdUtil(oldCode);
        if (nodeIdUtil.getCodes().isEmpty()) {
            return oldCode;
        }
        nodeIdUtil.setPrefix(newCodePrefix); //will automatically strip the leading zeroes due to integer-parsing
        if(!oldCode.startsWith("at0.") && !oldCode.startsWith("ac0.") && !oldCode.startsWith("id0.")) {
            //a bit tricky, since the root of an archetype starts with at0000.0, but that's different from this I guess
            nodeIdUtil.getCodes().set(0, nodeIdUtil.getCodes().get(0) - 1); //increment with 1, old is 0-based
        }
        return nodeIdUtil.toStringWithLeftPaddedCodes(4);
    }
}
