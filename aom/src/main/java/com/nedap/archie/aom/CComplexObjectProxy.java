package com.nedap.archie.aom;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 19/10/15.
 */

@XmlType(name="C_COMPLEX_OBJECT_PROXY")
public class CComplexObjectProxy extends CObject {

    @XmlElement(name="target_path")
    private String targetPath;

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
