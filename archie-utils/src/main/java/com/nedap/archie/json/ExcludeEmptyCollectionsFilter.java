package com.nedap.archie.json;

import java.util.Collection;
import java.util.Map;

public class ExcludeEmptyCollectionsFilter {
    @Override
    // Return true to exclude
    public boolean equals(Object o) {

        // Exclude Null
        if (o == null) {
            return true;
        }

        if (o instanceof Map) {
            return ((Map<?, ?>) o).isEmpty();
        }
        if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        }

        if (o instanceof Object[]) {
            return ((Object[]) o).length == 0;
        }

        // Include everything else
        return false;
    }
}
