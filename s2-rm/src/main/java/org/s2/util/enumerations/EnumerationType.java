package org.s2.util.enumerations;

import java.util.ArrayList;
import java.util.Arrays;

/// Definition of an enumeration type. In the BMM system, an 'enumeration' type is understood as an underlying basic type
/// and a set of named constants of that type. It is designed so that the default type is Integer, and the default
/// constants are numbered 0, 1, …
/// Optional model elements can be specified to override the values and / or the type.
public abstract class EnumerationType<T> {

  /// The list of names of the enumeration. If no values are supplied, the integer values 0, 1, 2, … are assumed.
  private final String[] itemNames;

  /// Optional list of specific values. Must be 1:1 with `items_names` list.
  private final T[] itemValues;

  public EnumerationType(String[] itemNames, T[] itemValues) {
    this.itemNames = itemNames;
    this.itemValues = itemValues;
  }

  public String getItemName(int itemValue) {
    return itemNames[itemValue];
  }

  public T getItemValue(int index) {
    return itemValues[index];
  }

  public boolean isValidItemName(String itemName) {
    return Arrays.asList(itemNames).contains(itemName);
  }
}
