package com.nedap.archie.opt14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CDefinedObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class DefaultValueConverter {
    //TODO: default values
//
//    public void convertDefaultValues(OPERATIONALTEMPLATE opt14, OperationalTemplate opt) {
//        convertDefaultValues(opt14.getDefinition(), opt);
//    }
//
//    /**
//     * convert the OPT 1.4 default values and store in the archetype structure. Stop processing on Archetype roots
//     * , those things will contain new default values at that point.
//     * @param root14
//     * @param archetype
//     */
//    public void convertDefaultValues(CARCHETYPEROOT root14, Archetype archetype) {
//        List<DEFAULTVALUE> defaultValues = root14.getDefaultValues();
//        //the non-primitive default values can directly be converted
//        List<DEFAULTVALUE> nonPrimitiveDefaults = new ArrayList<>();
//        //the primitive ones must be grouped by the first parent non-primitive object to be converted
//        //to a non-primitive object
//        Map<String, List<DEFAULTVALUE>> groupedPrimitiveDefaults = new LinkedHashMap<>();
//        groupDefaultValues(defaultValues, nonPrimitiveDefaults, groupedPrimitiveDefaults);
//
//        addNonPrimitiveDefaultValues(nonPrimitiveDefaults, archetype);
//        addPrimitiveDefaultValues(groupedPrimitiveDefaults, archetype);
//    }
//
//    private void addPrimitiveDefaultValues(Map<String, List<DEFAULTVALUE>> groupedPrimitiveDefaults, Archetype archetype) {
//        RMObjectCreator creator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());
//        for(String path:groupedPrimitiveDefaults.keySet()) {
//            ArchetypeModelObject archetypeModelObject = archetype.itemAtPath(path);
//            if(archetypeModelObject instanceof CAttribute) {
//
//            } else if (archetypeModelObject instanceof CDefinedObject) {
//                CDefinedObject cObject = (CDefinedObject) archetypeModelObject;
//                Object object = creator.create(cObject);
//                for(DEFAULTVALUE value:groupedPrimitiveDefaults.get(path)) {
//                    APathQuery parsedPath = new APathQuery(value.getPath());
//                    PathSegment lastPathSegment = parsedPath.getPathSegments().remove(parsedPath.getPathSegments().size() - 1);
//                    creator.set(object, lastPathSegment.getNodeName(), Lists.newArrayList(convertPrimitiveDefaultsValue(value)));
//                }
//                cObject.setDefaultValue(object);
//            } else {
//                //todo warn
//            }
//
//        }
//    }
//
//    private Object convertPrimitiveDefaultsValue(DEFAULTVALUE value) {
//        if(value instanceof DEFAULTBOOLEAN) {
//            DEFAULTBOOLEAN d = (DEFAULTBOOLEAN) value;
//            return d.isValue();
//        } else if (value instanceof DEFAULTCODEPHRASE) {
//            DEFAULTCODEPHRASE d = (DEFAULTCODEPHRASE) value;
//            //TODO: potentially tricky - coded text may be needed here instead, look at rm model?
//            BaseTypesConverter.convertToCodePhrase(d.getValue());
//        } else if (value instanceof DEFAULTSTRING) {
//            DEFAULTSTRING d = (DEFAULTSTRING) value;
//            return d.getValue();
//        } else if (value instanceof DEFAULTINTEGER) {
//            DEFAULTINTEGER d = (DEFAULTINTEGER) value;
//            return (long) d.getValue();
//        } else if (value instanceof DEFAULTREAL) {
//            DEFAULTREAL d = (DEFAULTREAL) value;
//            return (double) d.getValue();
//        } else if (value instanceof DEFAULTTIME) {
//            DEFAULTTIME d = (DEFAULTTIME) value;
//            return DateTimeParsers.parseTimeValue(d.getValue());
//        } else if (value instanceof DEFAULTDATE) {
//            DEFAULTDATE d = (DEFAULTDATE) value;
//            return DateTimeParsers.parseDateValue(d.getValue());
//        } else if (value instanceof DEFAULTDATETIME) {
//            DEFAULTDATETIME d = (DEFAULTDATETIME) value;
//            return DateTimeParsers.parseDateTimeValue(d.getValue());
//        } else if (value instanceof DEFAULTDURATION) {
//            DEFAULTDURATION d = (DEFAULTDURATION) value;
//            return DateTimeParsers.parseDurationValue(d.getValue());
//        }
//        return null;
//    }
//
//    private void addNonPrimitiveDefaultValues(List<DEFAULTVALUE> nonPrimitiveDefaults, Archetype archetype) {
//        for(DEFAULTVALUE defaultvalue:nonPrimitiveDefaults) {
//            ArchetypeModelObject archetypeModelObject = archetype.itemAtPath(defaultvalue.getPath());
//            if(archetypeModelObject == null) {
//                //TODO: warn
//            } else if (archetypeModelObject instanceof CAttribute) {
//                //ADL 2 requires this under a C_OBJECT< so convert it
//                CAttribute attribute = (CAttribute) archetypeModelObject;
//                CObject parent = attribute.getParent();
//                if(parent instanceof CDefinedObject) {
//                    CDefinedObject cObject = (CDefinedObject) parent;
//                    cObject.setDefaultValue(convertToNonPrimitiveRMObject(attribute, cObject, defaultvalue));
//                } else {
//                    //TODO: warn
//                }
//            } else if (archetypeModelObject instanceof CDefinedObject) {
//                CDefinedObject cObject = (CDefinedObject) archetypeModelObject;
//
//                cObject.setDefaultValue(convertToNonPrimitiveRMObject(defaultvalue));
//
//            } else {
//                //right....
//                //TODO: WARN
//            }
//        }
//    }
//
//    private Object convertToNonPrimitiveRMObject(CAttribute attribute, CDefinedObject parentCObject, DEFAULTVALUE defaultvalue) {
//        //get the possible type of the attribute from the default value, get the type of the parent of this and convert
//        RMObjectCreator creator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());
//        Object defaultValue = creator.create(parentCObject);
//        creator.set(defaultValue, attribute.getRmAttributeName(), Lists.newArrayList(convertToNonPrimitiveRMObject(defaultvalue)));
//        return defaultValue;
//    }
//
//    private Object convertToNonPrimitiveRMObject(DEFAULTVALUE defaultvalue) {
//        if(defaultvalue instanceof DEFAULTDVSTATE) {
//            return null; //this is for later
//        } else if (defaultvalue instanceof DEFAULTDVORDINAL) {
//            DEFAULTDVORDINAL defaultdvordinal = (DEFAULTDVORDINAL) defaultvalue;
//            DvOrdinal ordinal = convertDvOrdinal(defaultdvordinal.getValue());
//
//            return ordinal;
//        } else if (defaultvalue instanceof DEFAULTDVQUANTITY) {
//            DVQUANTITY defaultdvquantity = ((DEFAULTDVQUANTITY) defaultvalue).getValue();
//            DvQuantity quantity = convertDvQuantity(defaultdvquantity);
//
//            return quantity;
//        }
//        return null;
//    }
//
//    private DvQuantity convertDvQuantity(DVQUANTITY defaultdvquantity) {
//        DvQuantity quantity = new DvQuantity();
//        quantity.setMagnitude(defaultdvquantity.getMagnitude());
//        quantity.setUnits(defaultdvquantity.getUnits());
//        quantity.setPrecision(defaultdvquantity.getPrecision() == null ? null : defaultdvquantity.getPrecision().longValue());
//        quantity.setAccuracy(defaultdvquantity.getAccuracy() == null ? null : defaultdvquantity.getAccuracy().doubleValue());
//        quantity.setAccuracyIsPercent(defaultdvquantity.isAccuracyIsPercent());
//        quantity.setNormalStatus(BaseTypesConverter.convertToCodePhrase(defaultdvquantity.getNormalStatus()));
//        quantity.setNormalRange(convertQuantityNormalRange(defaultdvquantity.getNormalRange()));
//        quantity.setOtherReferenceRanges(convertQuantityReferenceRanges(defaultdvquantity.getOtherReferenceRanges()));
//        return quantity;
//    }
//
//    private DvOrdinal convertDvOrdinal(DVORDINAL ordinal14) {
//        DvOrdinal ordinal = new DvOrdinal();
//        ordinal.setValue((long) ordinal14.getValue());
//        ordinal.setSymbol(BaseTypesConverter.convert(ordinal14.getSymbol()));
//        ordinal.setNormalStatus(BaseTypesConverter.convertToCodePhrase(ordinal14.getNormalStatus()));
//        ordinal.setNormalRange(convertOrdinalNormalRange(ordinal14.getNormalRange()));
//        ordinal.setOtherReferenceRanges(convertOrdinalReferenceRanges(ordinal14.getOtherReferenceRanges()));
//        return ordinal;
//    }
//
//    private List<ReferenceRange<DvOrdinal>> convertOrdinalReferenceRanges(List<REFERENCERANGE> otherReferenceRanges) {
//        return null;//todo implement me
//    }
//
//    private List<ReferenceRange<DvQuantity>> convertQuantityReferenceRanges(List<REFERENCERANGE> otherReferenceRanges) {
//        return null;//todo implement me
//    }
//
//    private DvInterval<DvQuantity> convertQuantityNormalRange(DVINTERVAL range) {
//        if(range == null) {
//            return null;
//        }
//        if(!( (range.getLower() == null || range.getLower() instanceof DVQUANTITY)
//                && (range.getUpper() == null || range.getUpper() instanceof DVQUANTITY)))  {
//
//        }
//        DvInterval<DvQuantity> result = new DvInterval<>(
//                range.getLower() == null ? null : convertDvQuantity((DVQUANTITY) range.getLower()) ,
//                range.getUpper() == null ? null : convertDvQuantity((DVQUANTITY) range.getUpper()));
//        result.setLowerIncluded(range.isLowerIncluded() == null ? true : range.isLowerIncluded());
//        result.setUpperIncluded(range.isUpperIncluded() == null ? true : range.isUpperIncluded());
//        result.setLowerUnbounded(range.isLowerUnbounded());
//        result.setUpperUnbounded(range.isUpperUnbounded());
//        return result;
//    }
//
//
//
//    public DvInterval<DvOrdinal> convertOrdinalNormalRange(DVINTERVAL range) {
//        if(range == null) {
//            return null;
//        }
//        if(!( (range.getLower() == null || range.getLower() instanceof DVORDINAL)
//                && (range.getUpper() == null || range.getUpper() instanceof DVORDINAL)))  {
//
//        }
//        DvInterval<DvOrdinal> result = new DvInterval<>(
//                range.getLower() == null ? null : convertDvOrdinal((DVORDINAL) range.getLower()) ,
//                range.getUpper() == null ? null : convertDvOrdinal((DVORDINAL) range.getUpper()));
//        result.setLowerIncluded(range.isLowerIncluded() == null ? true : range.isLowerIncluded());
//        result.setUpperIncluded(range.isUpperIncluded() == null ? true : range.isUpperIncluded());
//        result.setLowerUnbounded(range.isLowerUnbounded());
//        result.setUpperUnbounded(range.isUpperUnbounded());
//        return result;
//    }
//
//
//    private void groupDefaultValues(List<DEFAULTVALUE> defaultValues14, List<DEFAULTVALUE> nonPrimitiveDefaults, Map<String, List<DEFAULTVALUE>> groupedPrimitiveDefaults) {
//        for(DEFAULTVALUE defaultValue:defaultValues14) {
//            if(defaultValue instanceof DEFAULTDVORDINAL || defaultValue instanceof DEFAULTDVQUANTITY || defaultValue instanceof  DEFAULTDVSTATE) {
//                //non-primitive type
//                nonPrimitiveDefaults.add(defaultValue);
//            } else {
//                //primitive type
//                APathQuery parsedPath = new APathQuery(defaultValue.getPath());
//                PathSegment lastPathSegment = parsedPath.getPathSegments().remove(parsedPath.getPathSegments().size() - 1);
//                List<DEFAULTVALUE> groupedDefaultValues = groupedPrimitiveDefaults.get(parsedPath.toString());
//                if(defaultValues14 == null) {
//                    groupedDefaultValues = new ArrayList<>();
//                    groupedPrimitiveDefaults.put(parsedPath.toString(), groupedDefaultValues);
//                }
//                groupedDefaultValues.add(defaultValue);
//            }
//        }
//    }
}
