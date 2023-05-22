package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Event;
import com.nedap.archie.rm.datastructures.History;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import com.nedap.archie.text.serializers.LocatableSerializer;
import org.openehr.utils.message.I18n;

public class ObservationSerializer implements RmSerializer<Observation> {
    @Override
    public void serialize(Observation data, RmToTextSerializer serializer) {
        serializer.append("### ");

        new LocatableSerializer().serialize(data, serializer);
        writeHistory(data.getData(), serializer);

        serializer.append("\n");
        writeHistory(data.getState(), serializer);
        serializer.append(data.getProtocol());
    }

    private void writeHistory(History<ItemStructure> data, RmToTextSerializer serializer) {
        if(data != null) {
            if(data.getEvents() != null) {
                for(Event<ItemStructure> event: data.getEvents()) {
                    //TODO: event name: probably not interesting, although sometimes it could be!
                    serializer.append(I18n.t("Time of observation"));
                    serializer.append(event.getTime());
                    serializer.append("\n");
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
