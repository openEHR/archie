package org.openehr.rm.directory;

import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;
import org.openehr.rm.archetyped.*;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<ObjectRef<? extends ObjectId>> items = new ArrayList<>();
    @Nullable
    private List<Folder> folders = new ArrayList<>();

    @Nullable
    @XmlElement(name="details")
    private ItemStructure details;

    public Folder() {
    }

    public Folder(String archetypeNodeId, DvText name, ItemStructure details, @Nullable List<ObjectRef<? extends ObjectId>> items, @Nullable List<Folder> folders) {
        super(archetypeNodeId, name);
        this.items = items;
        this.folders = folders;
        this.details = details;
    }

    public Folder(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, ItemStructure details, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<ObjectRef<? extends ObjectId>> items, @Nullable List<Folder> folders) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.items = items;
        this.folders = folders;
        this.details = details;
    }

    @Nullable
    public List<ObjectRef<? extends ObjectId>> getItems() {
        return items;
    }

    public void setItems(@Nullable List<ObjectRef<? extends ObjectId>> items) {
        this.items = items;
    }

    public void addItem(ObjectRef<? extends ObjectId> item) {
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
        return Objects.equals(items, folder.items) &&
                Objects.equals(folders, folder.folders) &&
                Objects.equals(details, folder.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items, folders, details);
    }

    @Invariant(value="Folders_valid", ignored = true)
    public boolean foldersValid() {
        return InvariantUtil.nullOrNotEmpty(folders);
    }
}
