package com.nedap.archie.text.serializers.demographic;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class PartyIdentifiedSerializer implements RmSerializer<PartyIdentified> {
    @Override
    public void serialize(PartyIdentified data, RmToMarkdownSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("Name"), data.getName());
        if(data.getIdentifiers() != null) {
            for (DvIdentifier identifier : data.getIdentifiers()) {
                serializer.append(identifier);
                serializer.appendNewLine();
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return PartyIdentified.class;
    }
}
