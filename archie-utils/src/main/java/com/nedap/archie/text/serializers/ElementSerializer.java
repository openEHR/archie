package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ElementSerializer implements RmSerializer<Element> {

    @Override
    public void serialize(Element data, RmToTextSerializer serializer) {
        serializer.append(data.getName());
        serializer.append(": ");

        if(data.getValue() != null) {
            serializer.append(data.getValue());
        } else {
            if(data.getNullFlavour() != null) {
                serializer.append(" leeg. Reden: ");
                serializer.append(data.getNullFlavour());
                if(data.getNullReason() != null) {
                    serializer.append(" ");
                    serializer.append(data.getNullReason());
                }
            }
        }

    }

    @Override
    public Class getSerializedClass() {
        return Element.class;
    }
}
