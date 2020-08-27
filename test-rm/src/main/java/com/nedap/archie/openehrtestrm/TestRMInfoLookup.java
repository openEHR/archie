package com.nedap.archie.openehrtestrm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.*;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rminfo.ArchieModelNamingStrategy;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMPackageId;
import com.nedap.archie.rminfo.ReflectionModelInfoLookup;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * Created by pieter.bos on 02/02/16.
 */
public class TestRMInfoLookup extends ReflectionModelInfoLookup {

    private static TestRMInfoLookup instance;

    private TestRMInfoLookup() {
        super(new ArchieModelNamingStrategy(), TestRMBase.class);
        addSubtypesOf(DataValue.class); //extra class from the base package. No RMObject because it is also used in the AOM
        addSubtypesOf(CodePhrase.class);
    }

    public static TestRMInfoLookup getInstance() {
        if(instance == null) {
            instance = new TestRMInfoLookup();
        }
        return instance;
    }

    @Override
    public Class getClassToBeCreated(String rmTypename) {
        return getClass(rmTypename);
    }

    @Override
    public Object convertToConstraintObject(Object object, CPrimitiveObject cPrimitiveObject) {
        return object;
    }



    public Object convertConstrainedPrimitiveToRMObject(Object object) {
        return object;
    }


    @Override
    public void processCreatedObject(Object createdObject, CObject constraint) {

    }

    @Override
    public String getArchetypeNodeIdFromRMObject(Object rmObject) {
        return null;
    }

    @Override
    public String getArchetypeIdFromArchetypedRmObject(Object rmObject) {
        return null;
    }

    @Override
    public String getNameFromRMObject(Object rmObject) {
        if(rmObject == null) {
            return null;
        }
        return null;
    }

    @Override
    public Object clone(Object rmObject) {
        //if(rmObject instanceof TestRMBase) {
         //   return ((TestRMBase) rmObject).clone();
       // }
        throw new IllegalArgumentException("The TestRMInfoLookup can not yet clone");
    }

    /**
     * Notification that a value at a given path has been updated in the given archetype. Perform tasks here to make sure
     * every other paths are updated as well.
     * @param rmObject
     * @param archetype
     * @param pathOfParent
     * @param parent
     */
    @Override
    public Map<String, Object> pathHasBeenUpdated(Object rmObject, Archetype archetype, String pathOfParent, Object parent) {
        return new HashMap<>();
    }

    @Override
    public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject cObject) {
        RMAttributeInfo attributeInfo = this.getAttributeInfo(rmTypeName, rmAttributeName);
        if(attributeInfo == null) {
            return true;//cannot validate
        }
        if(cObject instanceof CInteger) {
            return attributeInfo.getTypeInCollection().equals(Long.class);
        } else if(cObject instanceof CReal) {
            return attributeInfo.getTypeInCollection().equals(Double.class);
        } else if(cObject instanceof CString) {
            return attributeInfo.getTypeInCollection().equals(String.class);
        } else if(cObject instanceof CDate) {
            return attributeInfo.getTypeInCollection().equals(String.class) ||
                    attributeInfo.getTypeInCollection().isAssignableFrom(Temporal.class);
        } else if(cObject instanceof CDateTime) {
            return attributeInfo.getTypeInCollection().equals(String.class) ||
                    attributeInfo.getTypeInCollection().isAssignableFrom(Temporal.class);
        } else if(cObject instanceof CDuration) {
            return attributeInfo.getTypeInCollection().equals(String.class) ||
                    attributeInfo.getTypeInCollection().isAssignableFrom(TemporalAccessor.class);
        } else if(cObject instanceof CTime) {
            return attributeInfo.getTypeInCollection().equals(String.class) ||
                    attributeInfo.getTypeInCollection().isAssignableFrom(TemporalAccessor.class);
        } else if(cObject instanceof CTerminologyCode) {
            return attributeInfo.getTypeInCollection().equals(CodePhrase.class) ||
                    attributeInfo.getTypeInCollection().equals(DvCodedText.class);
        } else if(cObject instanceof CBoolean) {
            return attributeInfo.getTypeInCollection().equals(Boolean.class);
        }
        return false;

    }

    @Override
    public Collection<RMPackageId> getId() {
        List<RMPackageId> result = new ArrayList<>();
        result.add(new RMPackageId("openEHR", "TEST_PKG"));
        return result;
    }

}

