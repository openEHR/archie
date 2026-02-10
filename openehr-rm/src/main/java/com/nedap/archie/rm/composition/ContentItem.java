package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.archetyped.*;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;


/**
 * Created by pieter.bos on 03/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CONTENT_ITEM")
public abstract class ContentItem extends Locatable {

    public ContentItem() {
    }

    public ContentItem(String archetypeNodeId, DvText name) {
        super(archetypeNodeId, name);
    }

    public ContentItem(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
    }
}
