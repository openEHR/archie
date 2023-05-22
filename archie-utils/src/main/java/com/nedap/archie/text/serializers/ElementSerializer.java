package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ElementSerializer implements RmSerializer<Element> {

    @Override
    public void serialize(Element data, RmToTextSerializer serializer) {
        serializer.writeToText(data.getName());
        serializer.append(": ");

        if(data.getValue() != null) {
            serializer.writeToText(data.getValue());
        } else {
            if(data.getNullFlavour() != null) {
                serializer.append(" leeg. Reden: ");
                serializer.writeToText(data.getNullFlavour());
                if(data.getNullReason() != null) {
                    serializer.append(" ");
                    serializer.writeToText(data.getNullReason());
                }
            }
        }

    }

    @Override
    public Class getSerializedClass() {
        return Element.class;
    }
}
