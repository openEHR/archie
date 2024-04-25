package org.s2.serialisation.odin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.s2.rm.base.patterns.data_structures.Node;

import java.util.List;

public interface OdinParsingNodeMixin {

    @JsonDeserialize(converter = NodeMapToListConverter.class)
    void setItems(List<Node> child);

}
