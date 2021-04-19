package org.openehr.bmm.persistence.validation;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BmmDefinitions extends BasicDefinitions {

    /**
     * 	current notional version of the BMM_SCHEMA class model used in this software; used for
     * 	comparison with the BMM version recorded in schema files. If no bmm_version attribute is
     * 	found, the `Assumed_bmm_version' is used.
     */
    public static final String BMM_INTERNAL_VERSION = "2.1";


    // delimiter between parts of `schema_id`
    public static final Character SCHEMA_ID_DELIMITER = '-';
    public static final String SCHEMA_NAME_DELIMITER = "::";
    public static final Character PACKAGE_NAME_DELIMITER = '.';
    public static final Character GENERIC_LEFT_DELIMITER = '<';
    public static final Character GENERIC_RIGHT_DELIMITER = '>';
    public static final Character GENERIC_SEPARATOR = ',';

    public static final Character TUPLE_LEFT_DELIMITER = '[';
    public static final Character TUPLE_RIGHT_DELIMITER = ']';
    public static final Character TUPLE_SEPARATOR = ',';

    public static final Character CONSTRAINT_LEFT_DELIMITER = '«';
    public static final Character CONSTRAINT_RIGHT_DELIMITER = '»';

    public static final Character PATH_DELIMITER = '/';
    /**
     * Delimiter between class_name and qualifiying closure name, e.g. EHR-ENTRY
     */
    public static final Character GENERIC_CONSTRAINT_DELIMITER = ':';
    public static final String UNKNOWN_PACKAGE_NAME = "(uninitialized)";
    public static final String UNKNOWN_SCHEMA_ID = "(uninitialized)";
    public static final String UNKNOWN_SCHEMA_NAME = "(uninitialized)";
    public static final String UNKNOWN_TYPE_NAME = "UNKNOWN";

    /**
     * built-in container types used to represent class-class 1:N relations
     */
    public static final List<String> BMM_CONTAINER_TYPES = Collections.unmodifiableList(new ArrayList<String>() {{
        add("List");
        add("Set");
        add("Array");
    }});

    public static final String BMM_SCHEMA_FILE_EXTENSION = ".bmm";

    public static final String BMM_SCHEMA_FILE_MATCH_REGEX = ".*\\" + BMM_SCHEMA_FILE_EXTENSION + "$";

    /**
     * ODIN attribute name of logical attribute 'bmm_version' in schema file;
     * MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_BMM_VERSION = "bmm_version";
    /**
     * ODIN attribute name of logical attribute 'model_publisher' in schema file;
     * MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_RM_PUBLISHER = "rm_publisher";
    /**
     * 	ODIN attribute name of logical attribute 'schema_name' in schema file;
     * 	MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_SCHEMA_NAME = "schema_name";
    /**
     * ODIN attribute name of logical attribute 'model_release' in schema file;
     * MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_RM_RELEASE = "rm_release";
    /**
     * ODIN attribute name of logical attribute 'schema_revision' in schema file;
     * MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_SCHEMA_REVISION = "schema_revision";
    /**
     * ODIN attribute name of logical attribute 'schema_lifecycle_state' in schema file;
     * MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_SCHEMA_LIFECYCLE_STATE = "schema_lifecycle_state";
    /**
     * ODIN attribute name of logical attribute 'schema_description' in schema file;
     * MUST correspond to attribute of same name in P_BMM_SCHEMA class
     */
    public static final String METADATA_SCHEMA_DESCRIPTION = "schema_description";
    /**
     * path of schema file
     */
    public static final String METADATA_SCHEMA_PATH = "schema_path";

    /**
     * version of BMM to assume for a schema that doesn't have the bmm_version attribute
     */
    public static final String ASSUMED_BMM_VERSION = "1.0";

    public static final Pattern WELL_FORMED_TYPE_NAME_REGEX = Pattern.compile("[a-z]\\w*( *< *[a-z]\\w*( *< *[a-z]\\w*( *, *[a-z]\\w+)* *>)?( *, *[a-z]\\w*( *< *[a-z]\\w*( *, *[a-z]\\w+)* *>)?)* *>)?", Pattern.CASE_INSENSITIVE);

    public static final Pattern WELL_FORMED_CLASS_NAME_REGEX = Pattern.compile("[a-z]\\w+", Pattern.CASE_INSENSITIVE);

    /**
     * True if `a_meta_data' is valid for creation of a SCHEMA_DESCRIPTOR
     * @param aMetadata
     * @return
     */
    public static boolean isValidMetadata(Map<String,String> aMetadata) {
        return aMetadata != null && !aMetadata.isEmpty() && aMetadata.containsKey(METADATA_SCHEMA_NAME) && aMetadata.containsKey(METADATA_RM_RELEASE) && aMetadata.containsKey(METADATA_SCHEMA_PATH);
    }

    /**
     * True if the type name has a valid form, either a single name or a well-formed generic
     * @param aTypeName
     * @return
     */
    public static boolean isWellFormedTypeName(String aTypeName) {
        return WELL_FORMED_TYPE_NAME_REGEX.matcher(aTypeName).matches();
    }

    /**
     * True if the class name has a valid form
     *
     * @param aClassName
     * @return
     */
    public static boolean isWellFormedClassName(String aClassName) {
        return WELL_FORMED_CLASS_NAME_REGEX.matcher(aClassName).matches();
    }

    public static boolean isFormalGenericParameterName(String typeName) {
        return typeName.length() == 1;
    }

    /**
     * True if the type name is valid and includes a generic parameters part
     *
     * @param aGenericTypeName
     * @return
     */
    public static boolean isWellFormedGenericTypeName(String aGenericTypeName) {
        return isWellFormedTypeName(aGenericTypeName) && aGenericTypeName.indexOf(GENERIC_LEFT_DELIMITER) > 0;
    }

    /**
     * True if the type name includes a generic parameters part
     *
     * @return
     */
    public static boolean isGenericTypeName(String aGenericTypeName) {
        return aGenericTypeName.indexOf(GENERIC_LEFT_DELIMITER) > 0;
    }

    /**
     * is the software version of the BMM (defined by the constant `Bmm_version', above)
     * compatible with that found in the schema?
     * Returns True if the two versions have the same major version number
     *
     * @param aSchemaBmmVersion
     * @return
     */
    public static boolean isBmmVersionCompatible(String aSchemaBmmVersion) {
        return aSchemaBmmVersion.substring (0, aSchemaBmmVersion.indexOf ('.')).equals (BMM_INTERNAL_VERSION.substring (0, BMM_INTERNAL_VERSION.indexOf ('.')));
    }

    /**
     * Derived name of schema in 3 part form model_publisher '_' a_schema_name '_' model_release.
     * Any or all arguments can be Void or empty; for each missing element,
     * result contains "unknown", e.g. "unknown_test_1.0"
     * Result is lower case
     *
     * @param aModelPublisher
     * @param aSchemaName
     * @param aModelRelease
     * @return
     */
    public static String createSchemaId(String aModelPublisher, String aSchemaName, String aModelRelease) {
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isEmpty(aModelPublisher)) {
            builder.append("unknown");
        } else {
            builder.append(aModelPublisher);
        }
        builder.append("_");
        if(StringUtils.isEmpty(aSchemaName)) {
            builder.append("unknown");
        } else {
            builder.append(aSchemaName);
        }
        builder.append("_");
        if(StringUtils.isEmpty(aModelRelease)) {
            builder.append("unknown");
        } else {
            builder.append(aModelRelease);
        }
        return builder.toString().toLowerCase();
    }

    /**
     * package name might be of form xxx.yyy.zzz ; we only want 'zzz'
     * @param aPackageName
     * @return
     */
    public static String packageBaseName(String aPackageName) {
        if(aPackageName.indexOf(PACKAGE_NAME_DELIMITER) >= 0) {
            return aPackageName.substring(aPackageName.lastIndexOf(PACKAGE_NAME_DELIMITER) + 1);
        } else {
            return aPackageName;
        }
    }

    /**
     * mixed-case standard model-package name string, e.g. "openEHR-EHR" for UI display
     * uses `package_base_name' to obtain terminal form of package name
     *
     * @param aRmPublisher
     * @param aRmClosureName
     * @return
     */
    public static String publisherQualifiedRmClosureName(String aRmPublisher, String aRmClosureName) {
        return aRmPublisher + SCHEMA_ID_DELIMITER + packageBaseName(aRmClosureName).toUpperCase();
    }

    /**
     * lower-case form of `publisher_qualified_rm_closure_name'
     *
     * @param aRmPublisher
     * @param aRmClosureName
     * @return
     */
    public static String publisherQualifiedRmClosureKey(String aRmPublisher, String aRmClosureName) {
        return publisherQualifiedRmClosureName(aRmPublisher, aRmClosureName).toLowerCase();
    }

    /**
     * generate a standard model-class name string, e.g. "ehr-observation" for use in finding RM schemas
     *
     * @param aRmClosureName
     * @param aClassName
     * @return
     */
    public static String rmClosureQualifiedClassName(String aRmClosureName, String aClassName) {
        return aRmClosureName + SCHEMA_ID_DELIMITER + aClassName;
    }

    /**
     * convert a type name to a flat set of strings. E.g., for type Map&lt;String,MyClass&gt;
     * returns {Map,String,MyClass}
     *
     * @param aTypeName
     * @return
     */
    public static List<String> typeNameAsFlatList(String aTypeName) {
        List<String> retVal = new ArrayList<>();
        String cleanedType = aTypeName.replaceAll("\\s","");
        boolean isGenType = false;
        int lpos = -1, rpos = -1;
        if(cleanedType.indexOf(GENERIC_LEFT_DELIMITER) > 0) {
            rpos = cleanedType.indexOf(GENERIC_LEFT_DELIMITER);
            isGenType = true;
        }
        if(rpos < 0) {
            retVal.add(aTypeName);
        } else {
            retVal.add(aTypeName.substring(0, rpos));
        }

        if(isGenType) {
            cleanedType = cleanedType.substring(rpos + 1);
            cleanedType = cleanedType.replaceAll(""+ GENERIC_RIGHT_DELIMITER, "");
            String[] genericTypes = cleanedType.split("" + GENERIC_SEPARATOR);
            retVal.addAll(Arrays.asList(genericTypes));
        }
        return retVal;
    }

    /**
     * convert a type name which might have a generic part to a simple class name; Result will be upper case
     *
     * @param aTypeName
     * @return
     */
    public static String typeNameToClassKey(String aTypeName) {
        if(aTypeName.indexOf(GENERIC_LEFT_DELIMITER) >= 0) {
            return aTypeName.substring(0, aTypeName.indexOf(GENERIC_LEFT_DELIMITER)).toUpperCase();
        } else {
            return aTypeName.toUpperCase();
        }
    }

    /**
     * for a generic type name, extract the parameter type name(s) (which could themselves be generic)
     * and put them into a list.
     * Example: for "Hash &lt;List &lt;String&gt;, Integer&gt;", return a list with contents:
     * "List &lt;String&gt;", "Integer"
     * @param aTypeName
     * @return
     */
    public static List<String> genericParameterTypes(String aTypeName) {
        if(!isWellFormedGenericTypeName(aTypeName)) {
            throw new IllegalArgumentException("Invalid generic type " + aTypeName);
        }
        List<String> retVal = new ArrayList<>();
        //remove the root class and one level of generic delimiters
        String genericClause = aTypeName.substring(aTypeName.indexOf(GENERIC_LEFT_DELIMITER) + 1, aTypeName.length() - 1);
        genericClause = genericClause.replaceAll("\\s", "");
        char[] chars = genericClause.toCharArray();
        int genericsLevel = 0, startPosition = 0;
        for(int index = 0; index < chars.length; index++) {
            if(chars[index] == GENERIC_LEFT_DELIMITER) {
                genericsLevel += 1;
            } else if(chars[index] == GENERIC_RIGHT_DELIMITER) {
                genericsLevel -= 1;
            }
            //if we hit a comma and we are at the outermost generic level, save the string before the comma as a
            //generic parameter type
            if(genericsLevel == 0) {
                if(chars[index] == GENERIC_SEPARATOR) {
                    retVal.add(genericClause.substring(startPosition, index));
                    startPosition += 1;
                } else if(index == chars.length -1) {
                    retVal.add(genericClause.substring(startPosition));
                }
            }
        }
        return retVal;
    }

}
