package com.nedap.archie.aom;

import com.esotericsoftware.kryo.Kryo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.RMPropertyIgnore;
import com.nedap.archie.util.KryoUtil;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Common root class for all archetype objects - so we do not have to use java.lang.Object!
 * Created by pieter.bos on 15/10/15.
 */
//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class ArchetypeModelObject extends OpenEHRBase implements Serializable, Cloneable {

    private Integer startLine;
    private Integer startCharInLine;
    private Integer tokenLength;

    public ArchetypeModelObject clone() {
        Kryo kryo = null;
        try {
            kryo = KryoUtil.getPool().borrow();
            return kryo.copy(this);
        } finally {
            KryoUtil.getPool().release(kryo);
        }
    }

    @JsonIgnore
    @RMPropertyIgnore
    @XmlTransient
    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    @JsonIgnore
    @RMPropertyIgnore
    @XmlTransient
    public Integer getStartCharInLine() {
        return startCharInLine;
    }

    public void setStartCharInLine(Integer startCharInLine) {
        this.startCharInLine = startCharInLine;
    }

    @JsonIgnore
    @RMPropertyIgnore
    @XmlTransient
    public Integer getTokenLength() {
        return tokenLength;
    }

    public void setTokenLength(Integer tokenLength) {
        this.tokenLength = tokenLength;
    }
}
