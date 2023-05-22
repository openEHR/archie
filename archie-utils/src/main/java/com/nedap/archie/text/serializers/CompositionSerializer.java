package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import org.openehr.utils.message.I18n;

public class CompositionSerializer implements RmSerializer<Composition> {

    @Override
    public void serialize(Composition data, RmToTextSerializer serializer) {
        serializer.append("# ");
        new LocatableSerializer().serialize(data, serializer);

        for(ContentItem item: data.getContent()) {
            serializer.append(item);
            serializer.append("\n");
        }
        serializer.appendIfNotNull(I18n.t("Composer"), data.getComposer());
        serializer.appendIfNotNull(I18n.t("## Context"), data.getContext());
        //not written for now: language, territory

    }

    @Override
    public Class getSerializedClass() {
        return Composition.class;
    }
}
