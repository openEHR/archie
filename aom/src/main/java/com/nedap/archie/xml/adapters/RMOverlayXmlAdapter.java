package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.rmoverlay.RmOverlay;
import com.nedap.archie.aom.rmoverlay.RmAttributeVisibility;
import com.nedap.archie.xml.types.XmlRmAttributeVisibility;
import com.nedap.archie.xml.types.XmlRmOverlay;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;

public class RMOverlayXmlAdapter extends XmlAdapter<XmlRmOverlay, RmOverlay> {
    @Override
    public RmOverlay unmarshal(XmlRmOverlay xmlRMOverlay) {
        if(xmlRMOverlay == null) {
            return null;
        }
        RmOverlay result = new RmOverlay();
        if(xmlRMOverlay.getRmVisibility() == null) {
            return result;
        }
        for (XmlRmAttributeVisibility xmlVisibility : xmlRMOverlay.getRmVisibility()) {
            result.getRmVisibility().put(xmlVisibility.getPath(), new RmAttributeVisibility(xmlVisibility.getVisibility(), xmlVisibility.getAlias()));
        }

        return result;
    }

    @Override
    public XmlRmOverlay marshal(RmOverlay rmOverlay) {
        if(rmOverlay == null) {
            return null;
        }
        XmlRmOverlay result = new XmlRmOverlay();
        if(rmOverlay.getRmVisibility() == null) {
            return result;
        }
        List<XmlRmAttributeVisibility> resultList = new ArrayList<>();
        for (String path:rmOverlay.getRmVisibility().keySet()) {
            RmAttributeVisibility rmAttributeVisibility = rmOverlay.getRmVisibility().get(path);
            resultList.add(new XmlRmAttributeVisibility(path, rmAttributeVisibility.getVisibility(), rmAttributeVisibility.getAlias()));
        }
        result.setRmVisibility(resultList);

        return result;
    }
}
