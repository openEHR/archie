package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.xml.types.XmlResourceDescription;
import com.nedap.archie.xml.types.XmlResourceDescriptionItem;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.nedap.archie.xml.adapters.StringDictionaryUtil.*;

/**
 * Created by pieter.bos on 01/08/16.
 */
public class ResourceDescriptionAdapter extends XmlAdapter<XmlResourceDescription, ResourceDescription> {
    @Override
    public ResourceDescription unmarshal(XmlResourceDescription v) throws Exception {
        if(v == null) {
            return null;
        }
        ResourceDescription result = new ResourceDescription();
        result.setConversionDetails(convertStringDictionaryListToStringMap(v.getConversionDetails()));
        result.setCopyright(v.getCopyright());
        result.setCustodianNamespace(v.getCustodianNamespace());
        result.setCustodianOrganisation(v.getCustodianOrganisation());
        result.setDetails(convertDetails(v.getDetails()));
        result.setIpAcknowledgements(convertStringDictionaryListToStringMap(v.getIpAcknowledgements()));
        result.setLicence(v.getLicence());
        result.setLifecycleState(v.getLifecycleState());
        result.setOriginalAuthor(convertStringDictionaryListToStringMap(v.getOriginalAuthor()));
        result.setOriginalNamespace(v.getOriginalNamespace());
        result.setOriginalPublisher(v.getOriginalPublisher());
        result.setOtherContributors(v.getOtherContributors());
        result.setReferences(convertStringDictionaryListToStringMap(v.getReferences()));
        result.setResourcePackageUri(v.getResourcePackageUri());
        return result;
    }

    @Override
    public XmlResourceDescription marshal(ResourceDescription v) throws Exception {
        if(v == null) {
            return null;
        }
        XmlResourceDescription result = new XmlResourceDescription();
        result.setConversionDetails(convertStringMapIntoStringDictionaryList(v.getConversionDetails()));
        result.setCopyright(v.getCopyright());
        result.setCustodianNamespace(v.getCustodianNamespace());
        result.setCustodianOrganisation(v.getCustodianOrganisation());
        result.setDetails(convertDetails(v.getDetails()));
        result.setIpAcknowledgements(convertStringMapIntoStringDictionaryList(v.getIpAcknowledgements()));
        result.setLicence(v.getLicence());
        result.setLifecycleState(v.getLifecycleState());
        result.setOriginalAuthor(convertStringMapIntoStringDictionaryList(v.getOriginalAuthor()));
        result.setOriginalNamespace(v.getOriginalNamespace());
        result.setOriginalPublisher(v.getOriginalPublisher());
        result.setOtherContributors(v.getOtherContributors());
        result.setReferences(convertStringMapIntoStringDictionaryList(v.getReferences()));
        result.setResourcePackageUri(v.getResourcePackageUri());
        return result;
    }

    private List<XmlResourceDescriptionItem> convertDetails(Map<String, ResourceDescriptionItem> details) {
        List<XmlResourceDescriptionItem> result = new ArrayList<>();
        for(ResourceDescriptionItem item:details.values()) {
            XmlResourceDescriptionItem xmlItem = new XmlResourceDescriptionItem();
            xmlItem.setKeywords(item.getKeywords());
            xmlItem.setLanguage(item.getLanguage());
            xmlItem.setMisuse(item.getMisuse());
            xmlItem.setOriginalResourceUri(convertUriMapIntoStringDictionaryList(item.getOriginalResourceUri()));
            xmlItem.setOtherDetails(convertStringMapIntoStringDictionaryList(item.getOtherDetails()));
            xmlItem.setPurpose(item.getPurpose());
            xmlItem.setUse(item.getUse());
            result.add(xmlItem);
        }
        return result;
    }

    private Map<String, ResourceDescriptionItem> convertDetails(List<XmlResourceDescriptionItem> xmlDetails) {
        Map<String, ResourceDescriptionItem> result = new LinkedHashMap<>();
        for(XmlResourceDescriptionItem xmlItem:xmlDetails) {
            ResourceDescriptionItem item = new ResourceDescriptionItem();
            item.setKeywords(xmlItem.getKeywords());
            item.setLanguage(xmlItem.getLanguage());
            item.setMisuse(xmlItem.getMisuse());
            try {
                item.setOriginalResourceUri(convertStringDictionaryListToUriMap(xmlItem.getOriginalResourceUri()));
            } catch (URISyntaxException e) {
            }
            item.setOtherDetails(convertStringDictionaryListToStringMap(xmlItem.getOtherDetails()));
            item.setPurpose(xmlItem.getPurpose());
            item.setUse(xmlItem.getUse());
            result.put(item.getLanguage().getCodeString(), item);
        }
        return result;
    }
}
