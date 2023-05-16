package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Event;
import com.nedap.archie.rm.datastructures.History;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ObservationSerializer implements RmSerializer<Observation> {
    @Override
    public void serialize(Observation data, RmToTextSerializer serializer) {
        serializer.append("### ");

        serializer.writeToText(data.getName());
        serializer.append("\n");
        writeHistory(data.getData(), serializer);
        serializer.append("\n");
        writeHistory(data.getState(), serializer);
        serializer.writeToText(data.getProtocol());
    }

    private void writeHistory(History<ItemStructure> data, RmToTextSerializer serializer) {
        if(data != null) {
            if(data.getEvents() != null) {
                for(Event<ItemStructure> event: data.getEvents()) {
                    //TODO: event name: probably not interesting, although sometimes it could be!
                    serializer.append("Moment van observatie:");
                    serializer.writeToText(event.getTime());
                    serializer.append("\n");
                    serializer.writeToText(event.getData());
                    serializer.writeToText(event.getState());
                }
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return Observation.class;
    }
}
