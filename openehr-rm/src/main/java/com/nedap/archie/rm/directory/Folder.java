package com.nedap.archie.rm.directory;

import com.nedap.archie.rm.archetyped.*;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 21/06/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FOLDER", propOrder = {
        "folders",
        "items",
        "details"
})
public class Folder extends Locatable {
    @Nullable
    private List<ObjectRef> items = new ArrayList<>();
    @Nullable
    private List<Folder> folders = new ArrayList<>();

    @Nullable
    @XmlElement(name="details")
    private ItemStructure details;

    public Folder() {
    }

    public Folder(String archetypeNodeId, DvText name, ItemStructure details, @Nullable List<ObjectRef> items, @Nullable List<Folder> folders) {
        super(archetypeNodeId, name);
        this.items = items;
        this.folders = folders;
        this.details = details;
    }

    public Folder(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, ItemStructure details, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<ObjectRef> items, @Nullable List<Folder> folders) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.items = items;
        this.folders = folders;
        this.details = details;
    }

    @Nullable
    public List<ObjectRef> getItems() {
        return items;
    }

    public void setItems(@Nullable List<ObjectRef> items) {
        this.items = items;
    }

    public void addItem(ObjectRef item) {
        this.items.add(item);
    }

    @Nullable
    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(@Nullable List<Folder> folders) {
        this.folders = folders;
        setThisAsParent(folders, "folders");
    }

    public void addFolder(Folder folder) {
        this.folders.add(folder);
        this.setThisAsParent(folder, "folders");
    }

    @Nullable
    public ItemStructure getDetails() {
        return details;
    }

    public void setDetails(@Nullable ItemStructure details) {
        this.details = details;
        setThisAsParent(details, "details");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Folder folder = (Folder) o;

        return
            (items != null ? items.equals(folder.items) : folder.items == null) &&
                (folders != null ? folders.equals(folder.folders) : folder.folders == null) &&
                    (details != null ? details.equals(folder.details) : folder.details == null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (folders != null ? folders.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }
}
