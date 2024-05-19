package org.openehr.referencemodels;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Compares the AOM with the official BMM.
 *
 * For now the test is ignored and it can be run manually. it is possible to fix that, but then we need a certified ok AOM BMM,
 * and need to adapt this test to accept known model differences.
 */
public class AOMComparedWithBmmTest {

    @Test
    @Ignore
    public void testAOM() throws Exception{
        Map<String, String> typeMap =  new HashMap<>();
        typeMap.put("Any", "OPEN_EHRBASE");
        typeMap.put("Integer64", "Long");
        typeMap.put("Integer", "Long");
        typeMap.put("REAL", "Double");
        typeMap.put("HASH", "Map");
        typeMap.put("Character", "char");
        typeMap.put("Octet", "Byte"); //TODO: fix this properly
        typeMap.put("PROPORTION_KIND", "Long");//TODO: replace with enum!
        typeMap.put("TIME", "TEMPORAL_ACCESSOR");
        typeMap.put("DATE_TIME", "TEMPORAL_ACCESSOR");
        typeMap.put("DURATION", "TEMPORAL_AMOUNT");
        typeMap.put("DATE", "TEMPORAL");

        Set<String> extraParams = Sets.newHashSet("parent", "path");

        Map<String, String> typeNamesOverride = new HashMap<>();
        typeNamesOverride.put("DV_URI.value", "URI");
        typeNamesOverride.put("DV_EHR_URI.value", "URI");
        typeNamesOverride.put("DV_DATE.value", "temporal");
        typeNamesOverride.put("DV_TIME.value", "temporal_accessor");
        typeNamesOverride.put("DV_DATE_TIME.value", "temporal_accessor");
        typeNamesOverride.put("DV_DURATION.value", "temporal_amount");
        typeNamesOverride.put("Interval.lower", "object");
        typeNamesOverride.put("Interval.upper", "object");

        BmmRepository bmmRepository = new BmmRepository();
        String[] resources = {"/aom/openehr_am_230.bmm", "/aom/openehr_base_110.bmm", "/aom/openehr_expression_104.bmm"};
        for(String resource:resources) {
            try(InputStream stream = getClass().getResourceAsStream(resource)) {
                PBmmSchema pBmmSchema = BmmOdinParser.convert(stream);
                bmmRepository.addPersistentSchema(pBmmSchema);
            } catch(Exception e) {
                throw new RuntimeException("error parsing " + resource, e);
            }
        }
        new BmmSchemaConverter(bmmRepository).validateAndConvertRepository();
        for(BmmValidationResult validationResult:bmmRepository.getInvalidModels()) {
            System.out.println(validationResult.getLogger());
        }
        assertEquals(0, bmmRepository.getInvalidModels().size());

        BmmModel model = bmmRepository.getModel("openehr_am_2.3.0").getModel();

        List<ModelDifference> compared = new BmmComparison(extraParams, typeMap, typeNamesOverride).compare(model, ArchieAOMInfoLookup.getInstance());


        Set<ModelDifference> knownDifferences = new HashSet<>();
//System.out.println(Joiner.on("\n").join(compared));
        List<ModelDifference> foundErrors = new ArrayList<>();

        for(ModelDifference difference:compared) {
            BmmClass classDefinition = model.getClassDefinition(difference.getClassName());
            if(classDefinition == null || classDefinition.getPackagePath() == null || !classDefinition.getPackagePath().contains("org.openehr.base.expression")) {
                if (!knownDifferences.contains(difference)) {
                    foundErrors.add(difference);
                }
            }
        }

        List<ModelDifference> noLongerFoundErrors = new ArrayList<>();

        for(ModelDifference difference:knownDifferences) {
            if(!compared.contains(difference)) {
                noLongerFoundErrors.add(difference);
            }
        }
        assertTrue("unexpected model differences: "+ Joiner.on("\n").join(foundErrors), foundErrors.isEmpty());

        assertTrue("difference was in known difference, but is actually not a problem anymore: "+ Joiner.on("\n").join(noLongerFoundErrors), noLongerFoundErrors.isEmpty());
        assertEquals(knownDifferences.size(), compared.size());
    }
}
