package org.s2.util.enumerations;

/// Definition of an enumeration type. In the BMM system, an 'enumeration' type is understood as an underlying basic type
/// and a set of named constants of that type. It is designed so that the default type is Integer, and the default
/// constants are numbered 0, 1, …
/// Optional model elements can be specified to override the values and / or the type.
public class Enumeration<T> {
  static String P_BMM_ENUMERATION_UNDERLYING_TYPE_INTEGER = "Integer";
  static String P_BMM_ENUMERATION_UNDERLYING_TYPE_STRING = "RmString";

  /// The list of names of the enumeration. If no values are supplied, the integer values 0, 1, 2, … are assumed.
  private final String[] itemNames;

  /// Optional list of specific values. Must be 1:1 with `items_names` list.
  private final T[] itemValues;

  /// Name of type any concrete BMM_ENUMERATION_* sub-type is based on, i.e. the name of type bound to 'T' in a
  /// declared use of this type.
  private final String underlyingTypeName;

  public Enumeration(String underlyingTypeName, String[] itemNames, T[] itemValues) {
    this.underlyingTypeName = underlyingTypeName;
    this.itemNames = itemNames;
    this.itemValues = itemValues;
  }

  public String getItemName(int itemValue) {
    return itemNames[itemValue];
  }
}
