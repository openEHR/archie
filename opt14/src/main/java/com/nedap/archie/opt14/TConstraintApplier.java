package com.nedap.archie.opt14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.opt14.schema.*;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;

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
                            DataValue target = convertDataValue(defaultValue);
                            System.out.println(diffPath);
                            System.out.println(attributeName);
                            System.out.println(defaultValue);
                            System.out.println(obj);
                            setDefaultValue(obj, attributeName, target);
                        }
                    }
                }

            }
        }

    }

    private void setDefaultValue(ArchetypeModelObject obj, String attributeName, DataValue target) {
        if(obj instanceof CComplexObject) {
            CComplexObject difftarget = (CComplexObject) obj;
            RMObjectCreator creator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());
            creator.setProcessRmSpecificConstraints(false);
            RMObject o = creator.create(difftarget);
            creator.set(o, attributeName, Lists.newArrayList(target));
            if(difftarget.getDefaultValue() == null) {
                difftarget.setDefaultValue(o);
            } else {
                //apply it to existing default value
                creator.set(difftarget.getDefaultValue(), attributeName, Lists.newArrayList(target));
            }

            CAttribute defaultAttribute = difftarget.getAttribute(attributeName);
            if(defaultAttribute == null) {
                System.out.println("Expected child attribute, but not found?");
                //todo: make one? or just add a default for the element instead of the low level one?
            } else {
            }
        } else {
            System.out.println("exception object, got attribute?");
        }
    }

    private DataValue convertDataValue(DATAVALUE defaultValue) {
        if(defaultValue instanceof DVCODEDTEXT) {
            return DataValuesConverter.convert((DVCODEDTEXT) defaultValue);
        } else if (defaultValue instanceof DVTEXT) {
            return DataValuesConverter.convert((DVTEXT) defaultValue);
        } else if (defaultValue instanceof DVURI) {
            return DataValuesConverter.convert((DVURI) defaultValue);
        } else if (defaultValue instanceof DVIDENTIFIER) {
            return DataValuesConverter.convert((DVIDENTIFIER) defaultValue);
        } else if (defaultValue instanceof DVQUANTITY) {
            return DataValuesConverter.convert((DVQUANTITY) defaultValue);
        } else if (defaultValue instanceof DVORDINAL) {
            return DataValuesConverter.convert((DVORDINAL) defaultValue);
        }  else if (defaultValue instanceof DVBOOLEAN) {
            return DataValuesConverter.convert((DVBOOLEAN) defaultValue);
        } else if (defaultValue instanceof DVCOUNT) {
            return DataValuesConverter.convert((DVCOUNT) defaultValue);
        }
        return null;
    }
}
