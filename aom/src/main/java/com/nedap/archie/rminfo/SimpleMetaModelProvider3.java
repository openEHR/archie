package com.nedap.archie.rminfo;

import java.util.Objects;

/** Reuses model lookup with a separate Jackson 3 mapper provider. */
public class SimpleMetaModelProvider3 implements MetaModelProvider3 {
    private final MetaModelProvider metadataProvider;
    private final RMObjectMapperProvider3 mapperProvider;

    /** Use this adapter when all selected models use the same mapper provider. */
    public SimpleMetaModelProvider3(MetaModelProvider metadataProvider, RMObjectMapperProvider3 mapperProvider) {
        this.metadataProvider = Objects.requireNonNull(metadataProvider, "metadataProvider");
        this.mapperProvider = mapperProvider;
    }

    @Override
    public MetaModel3 getMetaModel(String rmPublisher, String rmPackage, String rmRelease) {
        MetaModel metadata = metadataProvider.getMetaModel(rmPublisher, rmPackage, rmRelease);
        return new MetaModel3(metadata.getModelInfoLookup(), metadata.getBmmModel(),
                mapperProvider);
    }
}
