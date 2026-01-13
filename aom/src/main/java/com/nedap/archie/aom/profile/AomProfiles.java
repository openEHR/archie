package com.nedap.archie.aom.profile;

import com.nedap.archie.serializer.odin.OdinObjectParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AomProfiles {

    private List<AomProfile> profiles;

    public AomProfiles() {
        profiles = new ArrayList<>();
    }

    public void add(AomProfile profile) {
        profiles.add(profile);
    }

    /**
     * Parse the ODIN Aom profile and add it
     * @param stream
     */
    public void add(InputStream stream) throws IOException {
        profiles.add(OdinObjectParser.convert(stream, AomProfile.class));
    }

    /**
     * Parse the ODIN Aom profile and add it
     * @param profile
     */
    public void add(String profile) {
        profiles.add(OdinObjectParser.convert(profile, AomProfile.class));

    }

    public List<AomProfile> getProfiles() {
        return profiles;
    }
}
