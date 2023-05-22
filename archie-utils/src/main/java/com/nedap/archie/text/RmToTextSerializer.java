package com.nedap.archie.text;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.text.serializers.*;
import com.nedap.archie.text.serializers.action.ActivitySerializer;
import com.nedap.archie.text.serializers.action.InstructionDetailsSerializer;
import com.nedap.archie.text.serializers.action.IsmTransitionSerializer;
import com.nedap.archie.text.serializers.audit.FeederAuditDetailsSerializer;
import com.nedap.archie.text.serializers.audit.FeederAuditSerializer;
import com.nedap.archie.text.serializers.datatypes.*;
import com.nedap.archie.text.serializers.demographic.PartyIdentifiedSerializer;
import com.nedap.archie.text.serializers.demographic.PartyRelatedSerializer;
import com.nedap.archie.text.serializers.demographic.PartySelfSerializer;
import com.nedap.archie.text.serializers.entries.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts instances of the openEHR RM to human readable text
 */
public class RmToTextSerializer {

    Map<Class, RmSerializer> serializers;
    ReflectionRmSerializer fallbackSerializer;

    StringBuilder stringBuilder;

    public RmToTextSerializer() {
        serializers = new HashMap<>();
        addSerializer(new ClusterSerializer());
        addSerializer(new CompositionSerializer());
        addSerializer(new ElementSerializer());
        addSerializer(new ItemListSerializer());
        addSerializer(new ItemTreeSerializer());
        addSerializer(new SectionSerializer());

        //composition extra classes
        addSerializer(new EventContextSerializer());
        addSerializer(new ParticipationSerializer());

        //entries
        addSerializer(new EvaluationSerializer());
        addSerializer(new ObservationSerializer());
        addSerializer(new InstructionSerializer());
        addSerializer(new ActionSerializer());
        addSerializer(new GenericEntrySerializer());
        addSerializer(new AdminEntrySerializer());

        //classes used in Action and Instruction
        addSerializer(new ActivitySerializer());
        addSerializer(new InstructionDetailsSerializer());
        addSerializer(new IsmTransitionSerializer());

        //references to demographics
        addSerializer(new PartyIdentifiedSerializer());
        addSerializer(new PartyRelatedSerializer());
        addSerializer(new PartySelfSerializer());

        //data values
        addSerializer(new DvBooleanSerializer());
        addSerializer(new DvCodedTextSerializer());
        addSerializer(new DvCountSerializer());
        addSerializer(new DvDateSerializer());
        addSerializer(new DvDateTimeSerializer());
        addSerializer(new DvDurationSerializer());
        addSerializer(new DvOrdinalSerializer());
        addSerializer(new DvProportionSerializer());
        addSerializer(new DvQuantitySerializer());
        addSerializer(new DvTextSerializer());
        addSerializer(new DvTimeSerializer());
        addSerializer(new DvIdentifierSerializer());

        //Feeder audits
        addSerializer(new FeederAuditSerializer());
        addSerializer(new FeederAuditDetailsSerializer());

        fallbackSerializer = new ReflectionRmSerializer();
        stringBuilder = new StringBuilder();
    }

    private void addSerializer(RmSerializer serializer) {
        serializers.put(serializer.getSerializedClass(), serializer);
    }

    public void append(RMObject object){
        if(object == null) {
            return;
        }
        RmSerializer serializer = serializers.get(object.getClass());

        if(serializer != null) {
            serializer.serialize(object, this);
        } else {
            fallbackSerializer.serialize(object, this);
        }
    }

    public void appendIfNotNull(String name, RMObject data) {
        if(data != null) {
            append(name);
            append(": ");
            append(data);
            append("\n");
        }
    }

    public void appendIfNotNull(String name, String data) {
        if(data != null) {
            append(name);
            append(": ");
            append(data);
            append("\n");
        }
    }

    public String toString() {
        return stringBuilder.toString();
    }

    public void append(String toWrite) {
        stringBuilder.append(toWrite);
    }


}
