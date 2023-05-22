package com.nedap.archie.text;

import com.nedap.archie.rm.RMObject;

public interface RmSerializer<T extends RMObject> {

    void serialize(T data, RmToTextSerializer serializer);

    Class getSerializedClass();
}
