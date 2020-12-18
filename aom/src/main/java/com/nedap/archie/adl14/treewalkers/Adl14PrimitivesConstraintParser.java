package com.nedap.archie.adl14.treewalkers;

import com.nedap.archie.adlparser.antlr.Adl14Lexer;
import com.nedap.archie.adlparser.antlr.Adl14Parser;
import com.nedap.archie.adlparser.antlr.Adl14Parser.Boolean_list_valueContext;
import com.nedap.archie.adlparser.antlr.Adl14Parser.Boolean_valueContext;
import com.nedap.archie.adlparser.antlr.Adl14Parser.String_list_valueContext;
import com.nedap.archie.adlparser.antlr.Adl14Parser.String_valueContext;
import com.nedap.archie.adlparser.antlr.ContainedRegexLexer;
import com.nedap.archie.adlparser.antlr.ContainedRegexParser;
import com.nedap.archie.adlparser.treewalkers.BaseTreeWalker;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CBoolean;
import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.aom.primitives.CDateTime;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.CTime;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.serializer.odin.OdinValueParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
public class Adl14PrimitivesConstraintParser extends BaseTreeWalker {

    private final Adl14NumberConstraintParser numberConstraintParser;
    private final Adl14TemporalConstraintParser temporalConstraintParser;

    public Adl14PrimitivesConstraintParser(ANTLRParserErrors errors) {
        super(errors);
        numberConstraintParser = new Adl14NumberConstraintParser(errors);
        temporalConstraintParser = new Adl14TemporalConstraintParser(errors);
    }

    public CPrimitiveObject parsePrimitiveObject(Adl14Parser.C_primitive_objectContext objectContext) {
        /*c_integer
                | c_real
                | c_date
                | c_time
                | c_date_time
                | c_duration
                | c_string
                | c_terminology_code
                | c_boolean*/
        if(objectContext.c_integer() != null) {
            return numberConstraintParser.parseCInteger(objectContext.c_integer());
        } else if (objectContext.c_real() != null) {
            return numberConstraintParser.parseCReal(objectContext.c_real());
        } else if (objectContext.c_date() != null) {
            return parseCDate(objectContext.c_date());
        } else if (objectContext.c_time() != null) {
            return parseCTime(objectContext.c_time());
        } else if (objectContext.c_date_time() != null) {
            return parseCDateTime(objectContext.c_date_time());
        } else if (objectContext.c_duration() != null) {
            return parseCDuration(objectContext.c_duration());
        } else if (objectContext.c_string() != null) {
            return parseCString(objectContext.c_string());
        } else if (objectContext.c_terminology_code() != null) {
            return parseCTerminologyCode(objectContext.c_terminology_code());
        } else if (objectContext.c_boolean() != null) {
            return parseCBoolean(objectContext.c_boolean());
        }
        throw new IllegalArgumentException("unknown primitive: " + objectContext.getText());
    }

    public CBoolean parseCBoolean(Adl14Parser.C_booleanContext booleanContext) {
        CBoolean result = new CBoolean();
        if(booleanContext.assumed_boolean_value() != null) {
            result.setAssumedValue(parseBoolean(booleanContext.assumed_boolean_value().boolean_value()));
        }
        Boolean_list_valueContext booleanListValue = booleanContext.boolean_list_value();
        if(booleanListValue != null) {
            parseBooleanValues(result, booleanListValue.boolean_value());
        }
        Boolean_valueContext booleanValueContext = booleanContext.boolean_value();
        if(booleanValueContext != null) {
            result.addConstraint(parseBoolean(booleanValueContext));
        }
        return result;
    }

    private boolean parseBoolean(Boolean_valueContext context) {
        return context.SYM_FALSE() == null;
    }

    private void parseBooleanValues(CBoolean result, List<Boolean_valueContext> booleanValues) {
        for(Boolean_valueContext booleanValue:booleanValues) {
            result.addConstraint(parseBoolean(booleanValue));
        }
    }

    public CTerminologyCode parseCTerminologyCode(Adl14Parser.C_terminology_codeContext terminologyCodeContext) {
        CTerminologyCode result = new CTerminologyCode();

        boolean containsAssumedValue = !terminologyCodeContext.getTokens(Adl14Lexer.SYM_SEMICOLON).isEmpty();

        Adl14Parser.QualifiedTermCodeContext qualifiedTermCodeContext = terminologyCodeContext.qualifiedTermCode();
        if(qualifiedTermCodeContext != null) {

            if (qualifiedTermCodeContext.TERM_CODE_REF() != null) {
                //need to do parsing here because the lexer matched the entire term code ref
                TerminologyCode terminologyCode = TerminologyCode.createFromString(qualifiedTermCodeContext.TERM_CODE_REF().getText());
                if (terminologyCode.getTerminologyId() != null && terminologyCode.getTerminologyId().equalsIgnoreCase("local")) {
                    result.addConstraint(terminologyCode.getCodeString());
                } else {
                    //non-local term constraints. Just add the text here, it will be converted later
                    result.addConstraint(qualifiedTermCodeContext.TERM_CODE_REF().getText());
                }
            } else {
                String terminologyId = qualifiedTermCodeContext.identifier(0).getText();
                if (terminologyId.equalsIgnoreCase("local")) {
                    //we need to create a value set. For now just add the constraint, the value set will come after
                    //the parser
                    for (int i = 1; i < qualifiedTermCodeContext.identifier().size(); i++) {
                        result.addConstraint(qualifiedTermCodeContext.identifier(i).getText());
                    }
                } else {
                    //non-local term constraints. Add the text here, will be converted later
                    //result.addConstraint(qualifiedTermCodeContex);
                    for (int i = 0; i < qualifiedTermCodeContext.identifier().size(); i++) {
                        result.addConstraint(qualifiedTermCodeContext.identifier(i).getText());
                    }
                }

            }
            if(qualifiedTermCodeContext.assumed_value() != null) {
                result.setAssumedValue(TerminologyCode.createFromString(qualifiedTermCodeContext.assumed_value().getText()));
            }
        } else {
            //this is an AC-code.
            if(terminologyCodeContext.localTermCode().AC_CODE() != null) {
                result.addConstraint(terminologyCodeContext.localTermCode().AC_CODE().getText());
            } else {
                throw new RuntimeException("unknown terminology code format - this looks adl2 inside the adl 1.4 format?");
            }
            if(containsAssumedValue) {
                result.setAssumedValue(TerminologyCode.createFromString(terminologyCodeContext.localTermCode().AT_CODE().getText()));
            }

        }



        return result;
    }

    public CString parseCString(Adl14Parser.C_stringContext stringContext) {

        CString result = new CString();
        if(stringContext.assumed_string_value() != null) {
            result.setAssumedValue(Odin14ValueParser.parseOdinStringValue(stringContext.assumed_string_value().string_value()));
        }

        String_valueContext stringValueContext = stringContext.string_value();
        String_list_valueContext stringListValueContext = stringContext.string_list_value();

        if(stringListValueContext != null) {
            for(String_valueContext string:stringListValueContext.string_value()) {
                result.addConstraint(Odin14ValueParser.parseOdinStringValue(string));
            }
        }
        if(stringValueContext != null) {
            result.addConstraint(Odin14ValueParser.parseOdinStringValue(stringValueContext));
        }
//        if(stringContext.regex_constraint() != null) {
//            result.addConstraint(stringContext.regex_constraint().getText());
//        }
        return result;
    }

    public CDuration parseCDuration(Adl14Parser.C_durationContext context) {
        return temporalConstraintParser.parseCDuration(context);
    }

    public  CDateTime parseCDateTime(Adl14Parser.C_date_timeContext context) {
        return temporalConstraintParser.parseCDateTime(context);
    }

    public CTime parseCTime(Adl14Parser.C_timeContext context) {
        return temporalConstraintParser.parseCTime(context);
    }

    public CDate parseCDate(Adl14Parser.C_dateContext context) {
        return temporalConstraintParser.parseCDate(context);
    }

    public CPrimitiveObject parseRegex(TerminalNode terminalNode) {
        ContainedRegexLexer lexer = new ContainedRegexLexer(CharStreams.fromString(terminalNode.getText()));
        ContainedRegexParser parser = new ContainedRegexParser(new CommonTokenStream(lexer));
        ContainedRegexParser.RegexContext regex = parser.regex();
        CString result = new CString();
        result.addConstraint(regex.REGEX().getText());
        if(regex.STRING() != null) {
            String assumedValue = regex.STRING().getText();
            result.setAssumedValue(assumedValue.substring(1, assumedValue.length()-1));
        }
        return result;
    }
}
