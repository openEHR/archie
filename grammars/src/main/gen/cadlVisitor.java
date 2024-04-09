// Generated from /home/thomas/Graphite/archie.git/grammars/src/main/antlr/cadl.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link cadlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface cadlVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_complex_object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_complex_object(cadlParser.C_complex_objectContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_objects}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_objects(cadlParser.C_objectsContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#sibling_order}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSibling_order(cadlParser.Sibling_orderContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_non_primitive_object_ordered}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_non_primitive_object_ordered(cadlParser.C_non_primitive_object_orderedContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_non_primitive_object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_non_primitive_object(cadlParser.C_non_primitive_objectContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_archetype_root}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_archetype_root(cadlParser.C_archetype_rootContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_complex_object_proxy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_complex_object_proxy(cadlParser.C_complex_object_proxyContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#archetype_slot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArchetype_slot(cadlParser.Archetype_slotContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_attribute_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_attribute_def(cadlParser.C_attribute_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_attribute(cadlParser.C_attributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_attribute_tuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_attribute_tuple(cadlParser.C_attribute_tupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#default_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefault_value(cadlParser.Default_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_object_tuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_object_tuple(cadlParser.C_object_tupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_object_tuple_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_object_tuple_items(cadlParser.C_object_tuple_itemsContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_object_tuple_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_object_tuple_item(cadlParser.C_object_tuple_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_includes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_includes(cadlParser.C_includesContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_excludes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_excludes(cadlParser.C_excludesContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_existence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_existence(cadlParser.C_existenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#existence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistence(cadlParser.ExistenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_cardinality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_cardinality(cadlParser.C_cardinalityContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#cardinality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCardinality(cadlParser.CardinalityContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#ordering_mod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrdering_mod(cadlParser.Ordering_modContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#unique_mod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnique_mod(cadlParser.Unique_modContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#multiplicity_mod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicity_mod(cadlParser.Multiplicity_modContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_occurrences}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_occurrences(cadlParser.C_occurrencesContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#multiplicity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicity(cadlParser.MultiplicityContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assertion_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertion_list(cadlParser.Assertion_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assertion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertion(cadlParser.AssertionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(cadlParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanAssertion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAssertion(cadlParser.BooleanAssertionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(cadlParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanForAllExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanForAllExpression(cadlParser.BooleanForAllExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanOrExpression(cadlParser.BooleanOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAndExpression(cadlParser.BooleanAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanXorExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanXorExpression(cadlParser.BooleanXorExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanNotExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanNotExpression(cadlParser.BooleanNotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanConstraintExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanConstraintExpression(cadlParser.BooleanConstraintExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanConstraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanConstraint(cadlParser.BooleanConstraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(cadlParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#relOpExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelOpExpression(cadlParser.RelOpExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#arithmeticExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpression(cadlParser.ArithmeticExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#expressionLeaf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionLeaf(cadlParser.ExpressionLeafContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(cadlParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#functionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionName(cadlParser.FunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#adlRulesPath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdlRulesPath(cadlParser.AdlRulesPathContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#variableReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableReference(cadlParser.VariableReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#plusMinusBinop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlusMinusBinop(cadlParser.PlusMinusBinopContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#multBinop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultBinop(cadlParser.MultBinopContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#powBinop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowBinop(cadlParser.PowBinopContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#equalityBinop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityBinop(cadlParser.EqualityBinopContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#relationalBinop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalBinop(cadlParser.RelationalBinopContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#booleanLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(cadlParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_primitive_object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_primitive_object(cadlParser.C_primitive_objectContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_integer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_integer(cadlParser.C_integerContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_integer_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_integer_value(cadlParser.Assumed_integer_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_real}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_real(cadlParser.C_realContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_real_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_real_value(cadlParser.Assumed_real_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_date_time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_date_time(cadlParser.C_date_timeContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_date_time_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_date_time_value(cadlParser.Assumed_date_time_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_date(cadlParser.C_dateContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_date_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_date_value(cadlParser.Assumed_date_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_time(cadlParser.C_timeContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_time_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_time_value(cadlParser.Assumed_time_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_duration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_duration(cadlParser.C_durationContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_duration_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_duration_value(cadlParser.Assumed_duration_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_string(cadlParser.C_stringContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_string_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_string_value(cadlParser.Assumed_string_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_terminology_code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_terminology_code(cadlParser.C_terminology_codeContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#c_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC_boolean(cadlParser.C_booleanContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#assumed_boolean_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumed_boolean_value(cadlParser.Assumed_boolean_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#adl_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdl_path(cadlParser.Adl_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#string_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_value(cadlParser.String_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#string_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_list_value(cadlParser.String_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#integer_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_value(cadlParser.Integer_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#integer_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_list_value(cadlParser.Integer_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#integer_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_interval_value(cadlParser.Integer_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#integer_interval_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_interval_list_value(cadlParser.Integer_interval_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#real_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_value(cadlParser.Real_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#real_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_list_value(cadlParser.Real_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#real_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_interval_value(cadlParser.Real_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#real_interval_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal_interval_list_value(cadlParser.Real_interval_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#boolean_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_value(cadlParser.Boolean_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#boolean_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_list_value(cadlParser.Boolean_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#character_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_value(cadlParser.Character_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#character_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_list_value(cadlParser.Character_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_value(cadlParser.Date_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_list_value(cadlParser.Date_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_interval_value(cadlParser.Date_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_interval_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_interval_list_value(cadlParser.Date_interval_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#time_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime_value(cadlParser.Time_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#time_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime_list_value(cadlParser.Time_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#time_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime_interval_value(cadlParser.Time_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#time_interval_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime_interval_list_value(cadlParser.Time_interval_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_time_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_time_value(cadlParser.Date_time_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_time_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_time_list_value(cadlParser.Date_time_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_time_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_time_interval_value(cadlParser.Date_time_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#date_time_interval_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate_time_interval_list_value(cadlParser.Date_time_interval_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#duration_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDuration_value(cadlParser.Duration_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#duration_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDuration_list_value(cadlParser.Duration_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#duration_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDuration_interval_value(cadlParser.Duration_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#duration_interval_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDuration_interval_list_value(cadlParser.Duration_interval_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#term_code_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm_code_value(cadlParser.Term_code_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#term_code_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm_code_list_value(cadlParser.Term_code_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#relop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelop(cadlParser.RelopContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#type_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_id(cadlParser.Type_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#attribute_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute_id(cadlParser.Attribute_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(cadlParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#archetype_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArchetype_ref(cadlParser.Archetype_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#odin_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOdin_text(cadlParser.Odin_textContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#attr_vals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr_vals(cadlParser.Attr_valsContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#attr_val}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr_val(cadlParser.Attr_valContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#odin_object_key}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOdin_object_key(cadlParser.Odin_object_keyContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#object_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_block(cadlParser.Object_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#object_value_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_value_block(cadlParser.Object_value_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#keyed_object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyed_object(cadlParser.Keyed_objectContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#included_other_language}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncluded_other_language(cadlParser.Included_other_languageContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#primitive_object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitive_object(cadlParser.Primitive_objectContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#primitive_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitive_value(cadlParser.Primitive_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#primitive_list_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitive_list_value(cadlParser.Primitive_list_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#primitive_interval_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitive_interval_value(cadlParser.Primitive_interval_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#object_reference_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_reference_block(cadlParser.Object_reference_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#odin_path_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOdin_path_list(cadlParser.Odin_path_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link cadlParser#odin_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOdin_path(cadlParser.Odin_pathContext ctx);
}