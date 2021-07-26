package com.nedap.archie.opt14;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.primitives.CBoolean;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.aom.primitives.CString;

import static com.nedap.archie.opt14.BaseTypesConverter.convertInterval;

import com.nedap.archie.opt14.schema.*;

class PrimitiveConverter {

    public static CObject convertPrimitive(CPRIMITIVEOBJECT cobject14) {
        CPRIMITIVE primitive14 = cobject14.getItem();
        if(primitive14 instanceof CINTEGER) {
            CINTEGER cinteger14 = (CINTEGER) primitive14;
            CInteger cInteger = new CInteger();
            if(cinteger14.getList() != null) {
                for(Integer integer:cinteger14.getList()) {
                    cInteger.addConstraint(new com.nedap.archie.base.Interval<Long>(integer.longValue(), integer.longValue()));
                }
            }
            if(cinteger14.getRange() != null) {
                cInteger.addConstraint(convertInterval(cinteger14.getRange()));
            }
            if(cinteger14.getAssumedValue() != null) {
                cInteger.setAssumedValue(cinteger14.getAssumedValue().longValue());
            }
            return cInteger;
        } else if (primitive14 instanceof CREAL) {
            CREAL cinteger14 = (CREAL) primitive14;
            CReal cInteger = new CReal();
            if(cinteger14.getList() != null) {
                for(Float integer:cinteger14.getList()) {
                    cInteger.addConstraint(new com.nedap.archie.base.Interval<Double>(integer.doubleValue(), integer.doubleValue()));
                }
            }
            if(cinteger14.getRange() != null) {
                cInteger.addConstraint(convertInterval(cinteger14.getRange()));
            }
            if(cinteger14.getAssumedValue() != null) {
                cInteger.setAssumedValue(cinteger14.getAssumedValue().doubleValue());
            }
            return cInteger;
        } else if (primitive14 instanceof CBOOLEAN) {
            CBOOLEAN cboolean14 = (CBOOLEAN) primitive14;
            CBoolean cBoolean = new CBoolean();
            if(cboolean14.isFalseValid()) {
                cBoolean.addConstraint(false);
            }
            if(cboolean14.isTrueValid()) {
                cBoolean.addConstraint(true);
            }
            if(cboolean14.isAssumedValue() != null) {
                cBoolean.setAssumedValue(cboolean14.isAssumedValue());
            }
        } else if (primitive14 instanceof CSTRING) {
            CSTRING cstring14 = (CSTRING) primitive14;
            CString cString = new CString();
            if(cstring14.getList() != null) {
                cString.getConstraint().addAll(cstring14.getList());
            }
            if(cstring14.getPattern() != null) {
                cString.getConstraint().add("/" + cstring14.getPattern() + "/");
            }
            if(cstring14.isListOpen() != null) {
                //TODO. Just return null?
            }
            cString.setAssumedValue(cstring14.getAssumedValue());
            return cString;
        } else if (primitive14 instanceof CDATE) {
            return null;
        } else if (primitive14 instanceof CDATETIME) {
            return null;
        } else if (primitive14 instanceof CTIME) {
            return null;
        } else if (primitive14 instanceof CDURATION) {
            return null;
        }
        throw new IllegalArgumentException("Uknoown primitive type found");




    }

}
