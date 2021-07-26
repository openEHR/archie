package com.nedap.archie.opt14;

import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.opt14.schema.DATAVALUE;
import com.nedap.archie.opt14.schema.DVCODEDTEXT;
import com.nedap.archie.opt14.schema.DVTEXT;
import com.nedap.archie.opt14.schema.OPERATIONALTEMPLATE;
import com.nedap.archie.opt14.schema.TATTRIBUTE;
import com.nedap.archie.opt14.schema.TCOMPLEXOBJECT;
import com.nedap.archie.opt14.schema.TCONSTRAINT;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;

import java.util.List;

class TConstraintApplier {


    public void apply(OPERATIONALTEMPLATE opt14, OperationalTemplate opt2) {
        TCONSTRAINT constraints = opt14.getConstraints();
        if(constraints != null && constraints.getAttributes() != null) {
            for(TATTRIBUTE attribute: constraints.getAttributes()) {
                String diffPath = attribute.getDifferentialPath();
                if(diffPath.startsWith("[")) {
                    //sometimes these paths start with a root node constraint. Sice there's only one
                    //and we don't support that, strip that here.
                    diffPath = diffPath.substring(diffPath.indexOf(']')+1);
                }
                List<ArchetypeModelObject> archetypeModelObjects = opt2.itemsAtPath(diffPath);
                if(archetypeModelObjects.isEmpty()) {
                    System.out.println("no archetype objects found for " + diffPath);
                } else if (archetypeModelObjects.size() > 1) {
                    System.out.println("many archetype objects found for " + diffPath);
                } else {
                    ArchetypeModelObject obj = archetypeModelObjects.get(0);

                    String attributeName = attribute.getRmAttributeName();
                    for(TCOMPLEXOBJECT tcomplexobject:attribute.getChildren()) {
                        //TODO:
                        //occurrences
                        //rm type name
                        //node id?
                        //child-attributes?

                        DATAVALUE defaultValue = tcomplexobject.getDefaultValue();
                        if(defaultValue != null) {

                            if(defaultValue instanceof DVCODEDTEXT) {
                                DvCodedText converted = BaseTypesConverter.convert((DVCODEDTEXT) defaultValue);
                                System.out.println(converted);
                            } else if (defaultValue instanceof DVTEXT) {
                                DvText converted = BaseTypesConverter.convert((DVTEXT) defaultValue);
                                System.out.println(converted);
                            }
                            System.out.println(diffPath);
                            System.out.println(attributeName);
                            System.out.println(defaultValue);
                            System.out.println(obj);
                        }
                    }
                }

            }
        }

    }
}
