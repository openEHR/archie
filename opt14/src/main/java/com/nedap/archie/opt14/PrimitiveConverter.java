package com.nedap.archie.opt14;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.primitives.CBoolean;
import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.aom.primitives.CDateTime;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.aom.primitives.CString;

import static com.nedap.archie.opt14.BaseTypesConverter.convertInterval;

import com.nedap.archie.aom.primitives.CTime;
import com.nedap.archie.base.Interval;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.opt14.schema.*;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

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
            return cBoolean;
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
        }
        else if (primitive14 instanceof CDATE) {
            CDATE cDate14 = (CDATE) primitive14;
            CDate cDate = new CDate();
            cDate.setPatternedConstraint(cDate.getPatternedConstraint());
            if(cDate14.getRange() != null) {
                cDate.addConstraint(convertInterval(cDate14.getRange()));
            }
            if(cDate14.getAssumedValue() != null) {
                cDate.setAssumedValue(DateTimeParsers.parseDateValue(cDate14.getAssumedValue()));
            }
            return cDate;
        } else if (primitive14 instanceof CDATETIME) {
            CDATETIME cDateTime14 = (CDATETIME) primitive14;
            CDateTime cDateTime = new CDateTime();
            cDateTime.setPatternedConstraint(cDateTime.getPatternedConstraint());
            if(cDateTime14.getRange() != null) {
                cDateTime.addConstraint(convertInterval(cDateTime14.getRange()));
            }
            if(cDateTime14.getAssumedValue() != null) {
                cDateTime.setAssumedValue(DateTimeParsers.parseDateValue(cDateTime14.getAssumedValue()));
            }
            return cDateTime;
        } else if (primitive14 instanceof CTIME) {
            CTIME cTime14 = (CTIME) primitive14;
            CTime cTime = new CTime();
            cTime.setPatternedConstraint(cTime.getPatternedConstraint());
            if(cTime14.getRange() != null) {
                cTime.addConstraint(convertInterval(cTime14.getRange()));
            }
            if(cTime14.getAssumedValue() != null) {
                cTime.setAssumedValue(DateTimeParsers.parseDateValue(cTime14.getAssumedValue()));
            }
            return cTime;
        } else if (primitive14 instanceof CDURATION) {
            CDURATION cDuration14 = (CDURATION) primitive14;
            CDuration cDuration = new CDuration();
            cDuration.setPatternedConstraint(cDuration14.getPattern());
            if(cDuration14.getRange() != null) {
                cDuration.addConstraint(convertInterval(cDuration14.getRange()));
            }
            if(cDuration14.getAssumedValue() != null) {
                cDuration.setAssumedValue(DateTimeParsers.parseDurationValue(cDuration14.getAssumedValue()));
            }
            return cDuration;
        }
        throw new IllegalArgumentException("Unknown primitive type found: " + primitive14.getClass());
    }

}
