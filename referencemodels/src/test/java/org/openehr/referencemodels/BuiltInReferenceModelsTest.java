package org.openehr.referencemodels;

import org.junit.jupiter.api.Test;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuiltInReferenceModelsTest {

    @Test
    public void bmmRepository() throws Exception {
        BmmRepository bmmRepository = BuiltinReferenceModels.getBmmRepository();

        for(BmmValidationResult validation:bmmRepository.getInvalidModels()) {
            System.out.println("validation " + validation.getSchemaId() + " contains errors:");
            System.out.println(validation.getLogger().toString());

        }
        assertEquals(35, bmmRepository.getPersistentSchemas().size());
        assertEquals(35, bmmRepository.getModels().size());
        assertEquals(32, bmmRepository.getValidModels().size());
        assertEquals(3, bmmRepository.getInvalidModels().size());
    }
}
