package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.rmoverlay.RMOverlay;
import com.nedap.archie.aom.rmoverlay.RMAttributeVisibility;
import com.nedap.archie.xml.types.XmlRMAttributeVisibility;
import com.nedap.archie.xml.types.XmlRMOverlay;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;

public class RMOverlayXmlAdapter extends XmlAdapter<XmlRMOverlay, RMOverlay> {
    @Override
    public RMOverlay unmarshal(XmlRMOverlay xmlRMOverlay) {
        if(xmlRMOverlay == null) {
            return null;
        }
        RMOverlay result = new RMOverlay();
        if(xmlRMOverlay.getRmVisibility() == null) {
            return result;
        }
        for (XmlRMAttributeVisibility xmlVisibility : xmlRMOverlay.getRmVisibility()) {
            result.getRmVisibility().put(xmlVisibility.getPath(), new RMAttributeVisibility(xmlVisibility.getVisibility(), xmlVisibility.getAlias()));
        }

        return result;
    }

    @Override
    public XmlRMOverlay marshal(RMOverlay rmOverlay) {
        if(rmOverlay == null) {
            return null;
        }
        XmlRMOverlay result = new XmlRMOverlay();
        if(rmOverlay.getRmVisibility() == null) {
            return result;
        }
        List<XmlRMAttributeVisibility> resultList = new ArrayList<>();
        for (String path:rmOverlay.getRmVisibility().keySet()) {
            RMAttributeVisibility rmAttributeVisibility = rmOverlay.getRmVisibility().get(path);
            resultList.add(new XmlRMAttributeVisibility(path, rmAttributeVisibility.getVisibility(), rmAttributeVisibility.getAlias()));
        }
        result.setRmVisibility(resultList);

        return result;
    }
}
