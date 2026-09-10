package com.nedap.archie.rminfo;

import tools.jackson.databind.ObjectMapper;
import org.openehr.bmm.core.BmmModel;

/** Model metadata and cached Jackson 3 mappers for ADL parsing. */
public class MetaModel3 {
    private final ModelInfoLookup modelInfoLookup;
    private final BmmModel bmmModel;
    private final RMObjectMapperProvider3 objectMapperProvider;
    private volatile ObjectMapper odinInputObjectMapper;
    private volatile ObjectMapper jsonObjectMapper;

    public MetaModel3(ModelInfoLookup modelInfoLookup, BmmModel bmmModel,
                      RMObjectMapperProvider3 objectMapperProvider) {
        if (modelInfoLookup == null && bmmModel == null) {
            throw new IllegalArgumentException("Either a ModelInfoLookup or a BMM model must be provided");
        }
        this.modelInfoLookup = modelInfoLookup;
        this.bmmModel = bmmModel;
        this.objectMapperProvider = objectMapperProvider;
    }

    public ModelInfoLookup getModelInfoLookup() { return modelInfoLookup; }
    public BmmModel getBmmModel() { return bmmModel; }

    /** Reads JSON converted from ODIN. */
    public ObjectMapper getOdinInputObjectMapper() {
        ObjectMapper mapper = odinInputObjectMapper;
        if (mapper == null && objectMapperProvider != null) {
            synchronized (this) {
                mapper = odinInputObjectMapper;
                if (mapper == null) {
                    mapper = objectMapperProvider.getInputOdinObjectMapper();
                    odinInputObjectMapper = mapper;
                }
            }
        }
        return mapper;
    }

    public ObjectMapper getJsonObjectMapper() {
        ObjectMapper mapper = jsonObjectMapper;
        if (mapper == null && objectMapperProvider != null) {
            synchronized (this) {
                mapper = jsonObjectMapper;
                if (mapper == null) {
                    mapper = objectMapperProvider.getJsonObjectMapper();
                    jsonObjectMapper = mapper;
                }
            }
        }
        return mapper;
    }

}
