package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class ElementSerializer implements RmSerializer<Element> {

    @Override
    public void serialize(Element data, RmToMarkdownSerializer serializer) {
        serializer.append(data.getName());
        serializer.append(": ");

        if(data.getValue() != null) {
            serializer.append(data.getValue());
        } else {
            if(data.getNullFlavour() != null) {
                serializer.append(I18n.t("Element is empty. Reason: "));
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
