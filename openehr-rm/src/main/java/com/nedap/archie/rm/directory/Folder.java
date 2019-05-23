package com.nedap.archie.rm.directory;

import com.nedap.archie.rm.archetyped.*;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 21/06/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FOLDER", propOrder = {
        "folders",
        "items"
})
public class Folder extends Locatable {
    @Nullable
    private List<ObjectRef> items = new ArrayList<>();
    @Nullable
    private List<Folder> folders = new ArrayList<>();

    public Folder() {
    }

    public Folder(String archetypeNodeId, DvText name, @Nullable List<ObjectRef> items, @Nullable List<Folder> folders) {
        super(archetypeNodeId, name);
        this.items = items;
        this.folders = folders;
    }

    public Folder(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<ObjectRef> items, @Nullable List<Folder> folders) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.items = items;
        this.folders = folders;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Folder folder = (Folder) o;

        if (items != null ? !items.equals(folder.items) : folder.items != null) return false;
        return folders != null ? folders.equals(folder.folders) : folder.folders == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (folders != null ? folders.hashCode() : 0);
        return result;
    }
}
