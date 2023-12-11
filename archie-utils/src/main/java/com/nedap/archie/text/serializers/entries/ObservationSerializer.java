package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Event;
import com.nedap.archie.rm.datastructures.History;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import com.nedap.archie.text.serializers.LocatableUtil;
import org.openehr.utils.message.I18n;

public class ObservationSerializer implements RmSerializer<Observation> {
    @Override
    public void serialize(Observation data, RmToMarkdownSerializer serializer) {
        serializer.append("### ");

        LocatableUtil.serialize(data, serializer);
        writeHistory(data.getData(), serializer);

        serializer.appendNewLine();
        writeHistory(data.getState(), serializer);
        serializer.append(data.getProtocol());
    }

    private void writeHistory(History<ItemStructure> data, RmToMarkdownSerializer serializer) {
        if(data != null) {
            if(data.getEvents() != null) {
                for(Event<ItemStructure> event: data.getEvents()) {
                    //TODO: event name: probably not interesting, although sometimes it could be!
                    serializer.appendIfNotNull(I18n.t("Time of observation"), event.getTime());
                    serializer.append("\n\n");
                    serializer.append(event.getData());
                    serializer.append(event.getState());
                }
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return Observation.class;
    }
}
