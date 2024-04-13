package com.nedap.archie.rminfo;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.*;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.base.RMObject;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datastructures.*;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.*;
import com.nedap.archie.rm.demographic.*;
import com.nedap.archie.rm.support.identification.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pieter.bos on 02/02/16.
 */
public class OpenEhrRmInfoLookup extends ReflectionModelInfoLookup {

    public static final String RM_VERSION = "1.1.0";

    private static OpenEhrRmInfoLookup instance;

    private OpenEhrRmInfoLookup() {
        super(new ArchieModelNamingStrategy(), RMObject.class);
    }

    /**
     * This list of classes generated in this method corresponds to any class that may be mentioned
     * in an archetype of this RM. Its extension for openEHR RM covers the following classes:
     * - classes in the supplier closures (classes reachable from via RM supplier relationships)
     *   of all classes in the top-level model packages, which for openEHR is EHR and demographic.
     * - except assumed primitives that are mapped to internal types.
     * = plus the classes base.RMObject which is the 'Any' class
     * - plus the class base.Interval
     * - plus aom.AuthoredResource // UNCLEAR WHY
     * - plus aom.TranslationDetails // UNCLEAR WHY
     *
     * The result should correspond to the set of classes in the corresponding BMM, minus those
     * classified as primitive. The BMM class set can be generated per the code in
     * RM102ConversionTest.testFindAllNonPrimitiveClasses
     * @param baseClass
     */
    @Override
    protected void addTypes(Class<?> baseClass) {
        addClass(Interval.class); //extra class from the base package. No RMObject because it is also used in the AOM
        addClass(RMObject.class); // Any class for type system
        addClass(AuthoredResource.class);
        addClass(TranslationDetails.class);

        // TODO: for whatever reason, passing the argument "com.nedap.archie.rm" does not work directly
        Set<Class<?>> classes = findAllClassesInPackage("com.nedap.archie")
                .stream()
                .filter(cl -> cl.getName().startsWith("com.nedap.archie.rm."))
                .collect(Collectors.toSet());

        classes.forEach(cl -> addClass(cl));
    }

    @Override
    protected boolean isNullable(Class<?> clazz, Method getMethod, Field field) {
        //The Party class has a non-null field that is nullable in its ancestor Actor. Cannot model that in Java
        //with Nullable annotations, or have to add really complicated stuff. This works too.
        if(field != null) {
            if (Party.class.isAssignableFrom(clazz) && field.getName().equalsIgnoreCase("uid")) {
                return false;
            }
        } else if (getMethod != null) {
            if (Party.class.isAssignableFrom(clazz) && getMethod.getName().equalsIgnoreCase("getUid")) {
                return false;
            }
        }
        return super.isNullable(clazz, getMethod, field);
    }

    public static OpenEhrRmInfoLookup getInstance() {
        if(instance == null) {
            instance = new OpenEhrRmInfoLookup();
        }
        return instance;
    }

    @Override
    public Class<?> getClassToBeCreated(String rmTypename) {
        if(rmTypename.equals("EVENT")) {
            //this is an abstract class and cannot be created. Create point event instead
            return PointEvent.class;
        }
        return getClass(rmTypename);
    }

    @Override
    public Object convertToConstraintObject(Object object, CPrimitiveObject<?, ?> cPrimitiveObject) {
        if(cPrimitiveObject instanceof CTerminologyCode) {
            if(object instanceof DvCodedText && ((DvCodedText) object).getDefiningCode() != null) {
                return convertCodePhrase(((DvCodedText) object).getDefiningCode());
            } else if (object instanceof CodePhrase) {
                return convertCodePhrase((CodePhrase) object);
            } else {
                return new TerminologyCode();
            }
        }
        return object;
    }

    private TerminologyCode convertCodePhrase(CodePhrase codePhrase) {
        TerminologyCode result = new TerminologyCode();
        result.setCodeString(codePhrase.getCodeString());
        result.setTerminologyId(codePhrase.getTerminologyId() == null ? null : codePhrase.getTerminologyId().getValue());
        return result;
    }

    public Object convertConstrainedPrimitiveToRMObject(Object object) {
        if(object instanceof TerminologyCode) {
            return convertTerminologyCode((TerminologyCode) object);
        }
        return object;
    }

    private CodePhrase convertTerminologyCode(TerminologyCode terminologyCode) {
        CodePhrase result = new CodePhrase();
        result.setCodeString(terminologyCode.getCodeString());
        result.setTerminologyId(terminologyCode == null ? null : new TerminologyId(terminologyCode.getTerminologyId()));
        return result;
    }

    @Override
    public void processCreatedObject(Object createdObject, CObject constraint) {
        if (createdObject instanceof Locatable) { //and most often, it will be
            Locatable locatable = (Locatable) createdObject;
            locatable.setArchetypeNodeId(constraint.getNodeId());
            locatable.setNameAsString(constraint.getMeaning());
            if(constraint instanceof CArchetypeRoot) {
                CArchetypeRoot root = (CArchetypeRoot) constraint;
                if(root.getArchetypeRef() != null) {
                    Archetyped details = new Archetyped();
                    details.setArchetypeId(new ArchetypeID(root.getArchetypeRef()));
                    details.setRmVersion(RM_VERSION);
                    locatable.setArchetypeDetails(details);
                }
            }
        }
    }

    @Override
    public String getArchetypeNodeIdFromRMObject(Object rmObject) {
        if(rmObject == null) {
            return null;
        }
        if(rmObject instanceof Locatable) {
            Locatable locatable = (Locatable) rmObject;
            return locatable.getArchetypeNodeId();
        }
        return null;
    }

    @Override
    public String getArchetypeIdFromArchetypedRmObject(Object rmObject) {
        if(rmObject instanceof Locatable) {
            Locatable locatable = (Locatable) rmObject;
            if(locatable.getArchetypeDetails() != null) {
                return locatable.getArchetypeDetails().getArchetypeId().getFullId();
            }
        }
        return null;
    }

    @Override
    public String getNameFromRMObject(Object rmObject) {
        if(rmObject == null) {
            return null;
        }
        if(rmObject instanceof Locatable) {
            Locatable locatable = (Locatable) rmObject;
            return locatable.getNameAsString();
        }
        return null;
    }

    @Override
    public Object clone(Object rmObject) {
        if(rmObject instanceof RMObject) {
            return ((RMObject) rmObject).clone();
        }
        throw new IllegalArgumentException("The ArchieRMInfoLookup can only clone openehr reference model objects");
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
        return OpenEhrRmUpdatedValueHandler.pathHasBeenUpdated(rmObject, archetype, pathOfParent, parent);
    }

    @Override
    public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject<?, ?> cObject) {
        RMAttributeInfo attributeInfo = this.getAttributeInfo(rmTypeName, rmAttributeName);
        if(attributeInfo == null) {
            return true;//cannot validate
        }
        Class<?> typeInCollection = attributeInfo.getTypeInCollection();
        if(cObject instanceof CInteger) {
            return typeInCollection.equals(Long.class) || typeInCollection.getName().equals("long");
        } else if(cObject instanceof CReal) {
            return typeInCollection.equals(Double.class) || typeInCollection.getName().equals("double");
        } else if(cObject instanceof CString) {
            return typeInCollection.equals(String.class);
        } else if(cObject instanceof CDate) {
            return typeInCollection.equals(String.class) ||
                    typeInCollection.isAssignableFrom(Temporal.class);
        } else if(cObject instanceof CDateTime) {
            return typeInCollection.equals(String.class) ||
                    typeInCollection.isAssignableFrom(Temporal.class);
        } else if(cObject instanceof CDuration) {
            return typeInCollection.equals(String.class) ||
                    typeInCollection.isAssignableFrom(TemporalAccessor.class) ||
                    typeInCollection.isAssignableFrom(TemporalAmount.class);
        } else if(cObject instanceof CTime) {
            return typeInCollection.equals(String.class) ||
                    typeInCollection.isAssignableFrom(TemporalAccessor.class);
        } else if(cObject instanceof CTerminologyCode) {
            return typeInCollection.equals(CodePhrase.class) ||
                    typeInCollection.equals(DvCodedText.class);
        } else if(cObject instanceof CBoolean) {
            return typeInCollection.equals(Boolean.class) || typeInCollection.getName().equals("boolean");
        }
        return false;

    }

    @Override
    public Collection<RMPackageId> getId() {
        List<RMPackageId> result = new ArrayList<>();
        result.add(new RMPackageId("openEHR", "EHR"));
        result.add(new RMPackageId("openEHR", "DEMOGRAPHIC"));
        return result;
    }

}

