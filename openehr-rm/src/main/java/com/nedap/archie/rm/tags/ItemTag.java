package com.nedap.archie.rm.tags;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ITEM_TAG")
public class ItemTag extends RMObject {

    private UIDBasedId target;
    @Nullable
    @XmlElement(name="target_path")
    private String targetPath;
    private String key;
    @Nullable
    private String value;
    @XmlElement(name="owner_id")
    private ObjectRef ownerId;

    public ItemTag(UIDBasedId target, @Nullable String targetPath, String key, @Nullable String value, ObjectRef ownerId) {
        this.target = target;
        this.targetPath = targetPath;
        this.key = key;
        this.value = value;
        this.ownerId = ownerId;
    }

    public UIDBasedId getTarget() {
        return target;
    }

    public void setTarget(UIDBasedId target) {
        this.target = target;
    }

    @Nullable
    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(@Nullable String targetPath) {
        this.targetPath = targetPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    public ObjectRef getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectRef ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemTag itemTag = (ItemTag) o;
        return Objects.equals(target, itemTag.target) &&
                Objects.equals(targetPath, itemTag.targetPath) &&
                Objects.equals(key, itemTag.key) &&
                Objects.equals(value, itemTag.value) &&
                Objects.equals(ownerId, itemTag.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, targetPath, key, value, ownerId);
    }
}
