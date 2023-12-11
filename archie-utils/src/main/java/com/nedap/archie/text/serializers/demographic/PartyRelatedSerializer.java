package com.nedap.archie.text.serializers.demographic;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.generic.PartyRelated;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class PartyRelatedSerializer implements RmSerializer<PartyRelated> {
    @Override
    public void serialize(PartyRelated data, RmToMarkdownSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("Name"), data.getName());
        serializer.appendIfNotNull(I18n.t("Relationship"), data.getRelationship());

        if(data.getIdentifiers() != null) {
            for(DvIdentifier identifier: data.getIdentifiers()) {
                serializer.append(identifier);
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return PartyRelated.class;
    }
}
