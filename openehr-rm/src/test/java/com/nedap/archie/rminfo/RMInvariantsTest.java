package com.nedap.archie.rminfo;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RMInvariantsTest {

    @Test
    public void rmInvariantsIsUpToDate() throws IOException {
        String committed = Files.readString(RMInvariantsGenerator.getFilePath(), StandardCharsets.UTF_8);
        assertEquals(RMInvariantsGenerator.generate(), committed,
                "RMInvariants is out of date, run RMInvariantsGenerator.main to regenerate it");
    }
}
