package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.antlr.AdlParser.*;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.base.Interval;

/**
 * Created by pieter.bos on 18/10/15.
 */
public class NumberConstraintParser extends BaseTreeWalker {

    public NumberConstraintParser(ANTLRParserErrors errors) {
        super(errors);
    }

    public CInteger parseCInteger(AdlParser.C_integerContext integerContext) {
        CInteger result = new CInteger();

        if(integerContext.assumed_integer_value() != null) {
            result.setAssumedValue(Long.parseLong(integerContext.assumed_integer_value().integer_value().getText()));
        }

        Integer_valueContext integerValueContext = integerContext.integer_value();
        if(integerValueContext != null) {
            parseIntegerConstraint(result, integerValueContext);
        }

        AdlParser.Integer_list_valueContext integerListValueContext = integerContext.integer_list_value();
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
            for(AdlParser.Integer_interval_valueContext intervalListContext:integerContext.integer_interval_list_value().integer_interval_value()) {
                result.addConstraint(parseIntegerInterval(intervalListContext));
            }
        }
        //TODO: set enumeratedTypeConstraint if only integers?

        return result;
    }

    private void parseIntegerConstraint(CInteger cInteger, Integer_valueContext integerValueContext) {
        long integer = Long.parseLong(integerValueContext.getText());
        Interval<Long> interval = new Interval<>();
        interval.setLower(integer);
        interval.setUpper(integer);
        cInteger.addConstraint(interval);
    }

    private Interval<Long> parseIntegerInterval(AdlParser.Integer_interval_valueContext context) {
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

    private Interval<Long> parseRelOpIntegerInterval(AdlParser.Integer_interval_valueContext intervalContext) {
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
        interval.fixUnboundedIncluded();
        return interval;
    }

    public CReal parseCReal(AdlParser.C_realContext realContext) {
        // ( real_value | real_list_value | real_interval_value | real_interval_list_value ) ( ';' real_value )? ;
        CReal result = new CReal();

        if(realContext.assumed_real_value() != null) {
            result.setAssumedValue(Double.parseDouble(realContext.assumed_real_value().real_value().getText()));
        }

        Real_valueContext realValueContext = realContext.real_value();
        if(realValueContext != null) {
            parseRealConstraint(result, realValueContext);
        }

        AdlParser.Real_list_valueContext realListValueContext = realContext.real_list_value();
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
            for(AdlParser.Real_interval_valueContext intervalListContext:realContext.real_interval_list_value().real_interval_value()) {
                result.addConstraint(parseRealInterval(intervalListContext));
            }
        }
        //TODO: set enumeratedTypeConstraint if only reals?
        return result;
    }

    private void parseRealConstraint(CReal cReal, Real_valueContext realValueContext) {
        double real = Double.parseDouble(realValueContext.getText());
        Interval<Double> interval = new Interval<>();
        interval.setLower(real);
        interval.setUpper(real);
        cReal.addConstraint(interval);
    }

    private Interval<Double> parseRealInterval(AdlParser.Real_interval_valueContext context) {
        Interval<Double> interval = null;
        if(context.relop() != null) {
            interval = parseRelOpRealInterval(context);
        } else {
            interval = new Interval<>();
            interval.setLower(Double.parseDouble(context.real_value(0).getText()));
            interval.setUpper(Double.parseDouble(context.real_value(1).getText()));
        }
        if(context.SYM_GT() != null) {//'|>a..b|'
            interval.setLowerIncluded(false);
        }
        if(context.SYM_LT() != null) {//'|a..<b|
            interval.setUpperIncluded(false);
        }
        return interval;
    }

    private Interval<Double> parseRelOpRealInterval(AdlParser.Real_interval_valueContext intervalContext) {
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
        interval.fixUnboundedIncluded();
        return interval;
    }
}
