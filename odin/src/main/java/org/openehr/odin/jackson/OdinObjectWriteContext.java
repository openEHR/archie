package org.openehr.odin.jackson;

public class OdinObjectWriteContext {

    private final OdinObjectWriteContext parent;
    private String typeId;
    private boolean arrayHasObjects = false;

    public OdinObjectWriteContext(OdinObjectWriteContext parent, String typeId) {
        this.parent = parent;
        this.typeId = typeId;
    }

    public OdinObjectWriteContext createChild(String typeId) {
        return new OdinObjectWriteContext(this, typeId);
    }

    public static OdinObjectWriteContext createRootContext() {
        return new OdinObjectWriteContext(null, null);
    }

    public OdinObjectWriteContext getParent() {
        return parent;
    }

    public String getTypeId() {
        return typeId;
    }

    public boolean hasTypeId() {
        return typeId != null;
    }

    public boolean arrayHasObjects() {
        return arrayHasObjects;
    }

    public void markHasChildObjects(boolean arrayHasObjects) {
        this.arrayHasObjects = arrayHasObjects;
    }
}
