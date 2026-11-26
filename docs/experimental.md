# Experimental features

[Back to the README](../README.md)

The following features can be used, but their API is likely to change in future versions.

## Rule evaluation

Archie has a basic implementation of the evaluation of rules in archetypes, in the `RuleEvaluation` class. The result is a list of assertions, each noting:

- whether it evaluated to true or false
- whether it can be fixed automatically, or the user needs to take a step
- what must be done to fix the rule automatically, by setting paths to values and paths that must exist or not exist
- which fields were used to evaluate the result, so errors can be shown

You can link this to a user interface that sets paths to values, hides or shows fields and displays validation messages. This makes it easy to implement things like score calculations and conditional fields. For example, score calculations:

```
/path/to[id12]/score_result = /path/to[id12]/field_one + /path/to[id12]/field_two
```

Conditionally hiding fields:

```
/path/to[id12]/score_result > 3 implies exists /path/to/hidden/field
```

Or validations that the user must fix:

```
total_score_must_be_high: /path/to[id12]/score_result + /some_other_score > 24
```

Supported are comparison and arithmetic operators, path retrieval, `exists` and `not exists`, `implies`, variable declaration and use, and `for_all`. Integer, string and boolean literals are supported. String literals can only be used with the `=` and `!=` operators. This means you can set fields to string values, including the code strings of DV_CODED_TEXT and DV_ORDINAL fields.

Rules are very similar to XPath expressions. The specification does not define how to handle multiplicities in paths, so where these occur outside of a `for_all` rule, the W3C XPath 1 specification is used.

## Full XPath support on the reference model

The `RMQueryContext` provides full XPath 1 support on reference model objects. APath shorthand notations like `/items[id2]` are converted to the corresponding XPath. It uses the JAXB `Binder` to get a DOM to run XPath on. You can choose your own JAXB implementation with the usual mechanisms, although only the default implementation has been tested.
