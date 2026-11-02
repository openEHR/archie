package org.openehr.bmm.persistence.validation;

import java.util.Date;
import java.util.regex.Pattern;

public class BasicDefinitions {
    public static final String ANY_TYPE = "Any";
    public static final String TERMINOLOGY_SEPARATOR = "::";
    public static final String REGEX_ANY_PATTERN = ".*";
    public static final Character CR = '\r';
    public static final Character LF = '\n';
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_ENCODING = "utf-8";

    //UTF-8 files don't normally have a BOM (byte order marker) at the start as can be
    //required by UTF-16 files, but if the file has been converted from UTF-16 or UTF-32
    //then the BOM in a UTF-8 file will be 0xEF 0xBB 0xBF (dec equivalent: 239, 187, 191)
    public static final Character UTF8_BOM_CHAR_1 = 239;
    public static final Character UTF8_BOM_CHAR_2 = 187;
    public static final Character UTF8_BOM_CHAR_3 = 191;

    public static final Integer UTF8_BOM_LENGTH = 3;
    public static final Character UTF8_COPYRIGHT_CHAR = 169;
    public static final Date timeEpoch = new Date(0);

    //Regex for 1, 2, or 3-part version string string of form N.M.P
    public static final String STANDARD_VERSION_REGEX = "[0-9]+(\\.[0-9]+){0,2}";
    public static final Pattern STANDARD_VERSION_REGEX_PATTERN = Pattern.compile(STANDARD_VERSION_REGEX);

    public static String bareTypeName(String typeString) {
        //remove any leading ! from broken TYPE.name
        if(typeString.charAt(0) == '!') {
            return typeString.substring(1);
        } else {
            return typeString;
        }
    }

    public static String formatReal(Double real) {
        return real.toString();
    }

    /**
     * Is `lver'' logically earlier than `rver' in a standard scheme of dot-separated version numbers?
     *
     * @param lver
     * @param rver
     * @return
     */
    public static boolean versionLessThan(String lver, String rver) {
        if(!isValidStandardVersion(lver) || !isValidStandardVersion(rver)) {
            throw new IllegalArgumentException("Invalid version argument " + lver + "," + rver);
        }
        String[] lverString = lver.split("\\.");
        String[] rverString = rver.split("\\.");

        boolean retVal = false;

        for(int index = 0; index <= 3; index++) {
            int lvalue = Integer.parseInt(lverString[index]);
            int rvalue = Integer.parseInt(rverString[index]);
            if(lvalue < rvalue) {
                retVal = true;
                break;
            } else if(lvalue != rvalue){
                break;
            }
        }
        return retVal;
    }

    //True if `a_ver' fits the pattern of a 1, 2 or 3 part numeric version string
	//with '.' separators
    public static boolean isValidStandardVersion(String version) {
        return STANDARD_VERSION_REGEX_PATTERN.matcher(version).matches();
    }

}
