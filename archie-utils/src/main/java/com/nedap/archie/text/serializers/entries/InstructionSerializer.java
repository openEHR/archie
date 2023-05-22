package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Activity;
import com.nedap.archie.rm.composition.Instruction;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import com.nedap.archie.text.serializers.LocatableSerializer;
import org.openehr.utils.message.I18n;

public class InstructionSerializer implements RmSerializer<Instruction> {
    @Override
    public void serialize(Instruction data, RmToTextSerializer serializer) {
        serializer.append("### ");
        new LocatableSerializer().serialize(data, serializer);
        if(data.getActivities() != null) {
            for(Activity activity:data.getActivities()) {
                serializer.append(activity);
                serializer.append("\n");
            }
        }
        serializer.appendIfNotNull(I18n.t("Protocol"), data.getProtocol());
        serializer.appendIfNotNull(I18n.t("Expiry time"), data.getExpiryTime());


        //TODO: wf definition - but we do not know the format here!
    }

    @Override
    public Class getSerializedClass() {
        return Instruction.class;
    }
}
