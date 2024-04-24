package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.antlr.AdlParser.Boolean_list_valueContext;
import com.nedap.archie.adlparser.antlr.AdlParser.Boolean_valueContext;
import com.nedap.archie.adlparser.antlr.AdlParser.String_list_valueContext;
import com.nedap.archie.adlparser.antlr.AdlParser.String_valueContext;
import com.nedap.archie.adlparser.antlr.ContainedRegexLexer;
import com.nedap.archie.adlparser.antlr.ContainedRegexParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CBoolean;
import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.aom.primitives.CDateTime;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.CTime;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.odin.OdinValueParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

/**
 * TODO: Due to how the grammar is built, it's a lot of work to create a treewalker. Much copy/paste code.
 * Instead, we could adapt the grammar to work with:
 *
 * c_primitive_object: (value | list_value | interval_value | interval_list_value) assumed_value?
 *
 * This has the drawback that we need to check for type correctness in the treewalker. But that is quite simple, mostly
 * that all objects have the same type and for a few that you cannot use an interval.
 *
 * Created by pieter.bos on 15/10/15.
 */
public class PrimitivesConstraintParser extends BaseTreeWalker {

    private final NumberConstraintParser numberConstraintParser;
    private final TemporalConstraintParser temporalConstraintParser;
    private MetaModels metaModels;

    public PrimitivesConstraintParser(ANTLRParserErrors errors, MetaModels metaModels) {
        super(errors);
        numberConstraintParser = new NumberConstraintParser(errors);
        temporalConstraintParser = new TemporalConstraintParser(errors);
        this.metaModels = metaModels;
    }

    public CPrimitiveObject<?, ?> parsePrimitiveObject(AdlParser.C_primitive_objectContext objectContext) {
        /*c_integer
                | c_real
                | c_date
                | c_time
                | c_date_time
                | c_duration
                | c_string
                | c_terminology_code
                | c_boolean*/
        CPrimitiveObject<?, ?> result = null;
        if (objectContext.c_integer() != null) {
            result = numberConstraintParser.parseCInteger(objectContext.c_integer());
        } else if (objectContext.c_real() != null) {
            result = numberConstraintParser.parseCReal(objectContext.c_real());
        } else if (objectContext.c_date() != null) {
            result = parseCDate(objectContext.c_date());
        } else if (objectContext.c_time() != null) {
            result = parseCTime(objectContext.c_time());
        } else if (objectContext.c_date_time() != null) {
            result = parseCDateTime(objectContext.c_date_time());
        } else if (objectContext.c_duration() != null) {
            result = parseCDuration(objectContext.c_duration());
        } else if (objectContext.c_string() != null) {
            result = parseCString(objectContext.c_string());
        } else if (objectContext.c_terminology_code() != null) {
            result = parseCTerminologyCode(objectContext.c_terminology_code());
        } else if (objectContext.c_boolean() != null) {
            result = parseCBoolean(objectContext.c_boolean());
        }

        if (result != null) {
            // set the RmTypeName in the object
            setCPrimitiveTypeRmTypeName(result);
        }

        return result;
    }

    public CBoolean parseCBoolean(AdlParser.C_booleanContext booleanContext) {
        CBoolean result = new CBoolean();

        if (booleanContext.assumed_boolean_value() != null) {
            result.setAssumedValue(parseBoolean(booleanContext.assumed_boolean_value().boolean_value()));
        }
        Boolean_list_valueContext booleanListValue = booleanContext.boolean_list_value();
        if (booleanListValue != null) {
            parseBooleanValues(result, booleanListValue.boolean_value());
        }
        Boolean_valueContext booleanValueContext = booleanContext.boolean_value();
        if (booleanValueContext != null) {
            result.addConstraint(parseBoolean(booleanValueContext));
        }
        return result;
    }

    private boolean parseBoolean(Boolean_valueContext context) {
        return context.SYM_FALSE() == null;
    }

    private void parseBooleanValues(CBoolean result, List<Boolean_valueContext> booleanValues) {
        for (Boolean_valueContext booleanValue : booleanValues) {
            result.addConstraint(parseBoolean(booleanValue));
        }
    }

    public CTerminologyCode parseCTerminologyCode(AdlParser.C_terminology_codeContext terminologyCodeContext) {
        CTerminologyCode result = new CTerminologyCode();

        boolean containsAssumedValue = !terminologyCodeContext.getTokens(AdlLexer.SYM_SEMICOLON).isEmpty();

        if (containsAssumedValue) {
            String terminologyId = terminologyCodeContext.AC_CODE().getText();
            TerminologyCode assumedValue = new TerminologyCode();
            assumedValue.setTerminologyId(terminologyId);
            String assumedValueString = terminologyCodeContext.AT_CODE().getText();
            assumedValue.setCodeString(assumedValueString);
            result.setAssumedValue(assumedValue);
            result.addConstraint(assumedValue.getTerminologyIdString());
        } else {
            if (terminologyCodeContext.AC_CODE() != null) {
                result.addConstraint(terminologyCodeContext.AC_CODE().getText());
            } else {
                result.addConstraint(terminologyCodeContext.AT_CODE().getText());
            }
        }

        if (terminologyCodeContext.identifier() != null) {
            String constraintStatusText = terminologyCodeContext.identifier().getText();
            switch (constraintStatusText.toLowerCase()) {
                case "required":
                    result.setConstraintStatus(ConstraintStatus.REQUIRED);
                    break;
                case "extensible":
                    result.setConstraintStatus(ConstraintStatus.EXTENSIBLE);
                    break;
                case "preferred":
                    result.setConstraintStatus(ConstraintStatus.PREFERRED);
                    break;
                case "example":
                    result.setConstraintStatus(ConstraintStatus.EXAMPLE);
                    break;
                default:
                    getErrors().addError("constraint status must be one of required, extensible, preferred or example, but was " + constraintStatusText,
                            "Constraint status incorrect",
                            terminologyCodeContext.getStart().getLine(),
                            terminologyCodeContext.getStart().getCharPositionInLine(),
                            constraintStatusText.length(),
                            constraintStatusText
                    );
            }
        }

        return result;
    }

    public CString parseCString(AdlParser.C_stringContext stringContext) {

        CString result = new CString();
        if (stringContext.assumed_string_value() != null) {
            result.setAssumedValue(OdinValueParser.parseOdinStringValue(stringContext.assumed_string_value().string_value()));
        }

        String_valueContext stringValueContext = stringContext.string_value();
        String_list_valueContext stringListValueContext = stringContext.string_list_value();

        if (stringListValueContext != null) {
            for (String_valueContext string : stringListValueContext.string_value()) {
                result.addConstraint(OdinValueParser.parseOdinStringValue(string));
            }
        }
        if (stringValueContext != null) {
            result.addConstraint(OdinValueParser.parseOdinStringValue(stringValueContext));
        }

        return result;
    }

    public CDuration parseCDuration(AdlParser.C_durationContext context) {
        return temporalConstraintParser.parseCDuration(context);
    }

    public CDateTime parseCDateTime(AdlParser.C_date_timeContext context) {
        return temporalConstraintParser.parseCDateTime(context);
    }

    public CTime parseCTime(AdlParser.C_timeContext context) {
        return temporalConstraintParser.parseCTime(context);
    }

    public CDate parseCDate(AdlParser.C_dateContext context) {
        return temporalConstraintParser.parseCDate(context);
    }

    public CPrimitiveObject<?, ?> parseRegex(TerminalNode terminalNode) {
        ContainedRegexLexer lexer = new ContainedRegexLexer(CharStreams.fromString(terminalNode.getText()));
        ContainedRegexParser parser = new ContainedRegexParser(new CommonTokenStream(lexer));
        ContainedRegexParser.RegexContext regex = parser.regex();
        CString result = new CString();
        result.addConstraint(regex.REGEX().getText());
        if (regex.STRING() != null) {
            String assumedValue = regex.STRING().getText();
            result.setAssumedValue(assumedValue.substring(1, assumedValue.length() - 1));
        }
        return result;
    }

    // set the RmTypeName in the object, if the ModelNamingStrategy has been supplied within the MetaModel,
    // if indeed that has been supplied to the AOMParser
    private void setCPrimitiveTypeRmTypeName(CPrimitiveObject<?,?> cPrimObj) {
        if(metaModels !=null) {
            MetaModel metaModel = metaModels.getSelectedModel();
            if (metaModel != null) {
                cPrimObj.setRmTypeName(metaModel.getSelectedModel().getNamingStrategy().getTypeNameForCPrimitiveType(CTerminologyCode.class));
            }
        }
    }
}
