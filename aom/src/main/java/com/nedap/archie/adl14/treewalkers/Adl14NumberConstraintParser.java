package com.nedap.archie.adl14.treewalkers;

import com.nedap.archie.adlparser.antlr.Adl14Parser.*;
import com.nedap.archie.adlparser.treewalkers.BaseTreeWalker;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.base.Interval;

/**
 * Created by pieter.bos on 18/10/15.
 */
public class Adl14NumberConstraintParser extends BaseTreeWalker {

    public Adl14NumberConstraintParser(ANTLRParserErrors errors) {
        super(errors);
    }

    public CInteger parseCInteger(C_integerContext integerContext) {
        CInteger result = new CInteger();

        if(integerContext.assumed_integer_value() != null) {
            result.setAssumedValue(Long.parseLong(integerContext.assumed_integer_value().integer_value().getText()));
        }

        Integer_valueContext integerValueContext = integerContext.integer_value();
        if(integerValueContext != null) {
            parseIntegerConstraint(result, integerValueContext);
        }

        Integer_list_valueContext integerListValueContext = integerContext.integer_list_value();
        if(integerListValueContext != null) {
            for(Integer_valueContext integerValueContext1:integerListValueContext.integer_value()) {
                parseIntegerConstraint(result, integerValueContext1);
            }
        }
        Integer_interval_valueContext intervalContext = integerContext.integer_interval_value();

        if(intervalContext != null) {
            result.addConstraint(parseIntegerInterval(intervalContext));
        }
        if(integerContext.integer_interval_list_value() != null) {
            for(Integer_interval_valueContext intervalListContext:integerContext.integer_interval_list_value().integer_interval_value()) {
                result.addConstraint(parseIntegerInterval(intervalListContext));
            }
        }

        if(result.getConstraint().size() == 1) {
            Interval<Long> interval = result.getConstraint().get(0);
        }
        return result;
    }

    private void parseIntegerConstraint(CInteger cInteger, Integer_valueContext integerValueContext) {
        long integer = Long.parseLong(integerValueContext.getText());
        Interval<Long> interval = new Interval<>();
        interval.setLower(integer);
        interval.setUpper(integer);
        cInteger.addConstraint(interval);
    }

    private Interval<Long> parseIntegerInterval(Integer_interval_valueContext context) {
        Interval<Long> interval = null;
        if(context.relop() != null) {
            interval = parseRelOpIntegerInterval(context);
        } else {
            interval = new Interval<>();
            if(context.integer_value().size() == 1) {
                interval.setLower(Long.parseLong(context.integer_value(0).getText()));
                interval.setUpper(interval.getLower());
            } else {
                interval.setLower(Long.parseLong(context.integer_value(0).getText()));
                interval.setUpper(Long.parseLong(context.integer_value(1).getText()));
            }
            if(context.SYM_GT() != null) {//'|>a..b|'
                interval.setLowerIncluded(false);
            }
            if(context.SYM_LT() != null) {//'|a..<b|
                interval.setUpperIncluded(false);
            }
        }
        return interval;
    }

    private Interval<Long> parseRelOpIntegerInterval(Integer_interval_valueContext intervalContext) {
        Interval<Long> interval = new Interval<>();
        long integer = Long.parseLong(intervalContext.integer_value().get(0).getText());
        switch(intervalContext.relop().getText()) {
            case "<":
                interval.setUpperIncluded(false);
            case "<=":
                interval.setLowerUnbounded(true);
                interval.setUpper(integer);
                break;
            case ">":
                interval.setLowerIncluded(false);
            case ">=":
                interval.setUpperUnbounded(true);
                interval.setLower(integer);
                break;
        }
        return interval;
    }

    public CReal parseCReal(C_realContext realContext) {
        // ( real_value | real_list_value | real_interval_value | real_interval_list_value ) ( ';' real_value )? ;
        CReal result = new CReal();

        if(realContext.assumed_real_value() != null) {
            result.setAssumedValue(Double.parseDouble(realContext.assumed_real_value().real_value().getText()));
        }

        Real_valueContext realValueContext = realContext.real_value();
        if(realValueContext != null) {
            parseRealConstraint(result, realValueContext);
        }

        Real_list_valueContext realListValueContext = realContext.real_list_value();
        if(realListValueContext != null) {
            for(Real_valueContext realValueContext1:realListValueContext.real_value()) {
                parseRealConstraint(result, realValueContext1);
            }
        }
        Real_interval_valueContext intervalContext = realContext.real_interval_value();

        if(intervalContext != null) {
            result.addConstraint(parseRealInterval(intervalContext));
        }
        if(realContext.real_interval_list_value() != null) {
            for(Real_interval_valueContext intervalListContext:realContext.real_interval_list_value().real_interval_value()) {
                result.addConstraint(parseRealInterval(intervalListContext));
            }
        }

        if(result.getConstraint().size() == 1) {
            Interval<Double> interval = result.getConstraint().get(0);
        }
        return result;
    }

    private void parseRealConstraint(CReal cReal, Real_valueContext realValueContext) {
        double real = Double.parseDouble(realValueContext.getText());
        Interval<Double> interval = new Interval<>();
        interval.setLower(real);
        interval.setUpper(real);
        cReal.addConstraint(interval);
    }

    private Interval<Double> parseRealInterval(Real_interval_valueContext context) {
        Interval<Double> interval = null;
        if(context.relop() != null) {
            interval = parseRelOpRealInterval(context);
        } else {
            interval = new Interval<>();
            interval.setLower(Double.parseDouble(context.real_value(0).getText()));
            if(context.real_value().size() > 1) {
                interval.setUpper(Double.parseDouble(context.real_value(1).getText()));
            } else {
                interval.setUpper(interval.getLower());
            }
        }
        if(context.SYM_GT() != null) {//'|>a..b|'
            interval.setLowerIncluded(false);
        }
        if(context.SYM_LT() != null) {//'|a..<b|
            interval.setUpperIncluded(false);
        }
        return interval;
    }

    private Interval<Double> parseRelOpRealInterval(Real_interval_valueContext intervalContext) {
        Interval<Double> interval = new Interval<>();
        double real = Double.parseDouble(intervalContext.real_value().get(0).getText());
        switch(intervalContext.relop().getText()) {
            case "<":
                interval.setUpperIncluded(false);
            case "<=":
                interval.setLowerUnbounded(true);
                interval.setUpper(real);
                break;
            case ">":
                interval.setLowerIncluded(false);
            case ">=":
                interval.setUpperUnbounded(true);
                interval.setLower(real);
                break;
        }
        return interval;
    }
}
