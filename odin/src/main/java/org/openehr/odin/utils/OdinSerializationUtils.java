package org.openehr.odin.utils;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017 Cognitive Medical Systems
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import org.openehr.odin.IntegerIntervalObject;

import java.util.ArrayList;
import java.util.List;

public class OdinSerializationUtils {

    public static String buildOdinStringObject(String content) {
        return "<\"" + content + "\">";
    }

    public static String buildOdinBooleanObject(Boolean bool) {
        if(bool) {
            return "<True>";
        } else  {
            return "<False>";
        }
    }

    /**
     * Creates a new ODIN object declaration:
     * <pre>
     *     <code>
     *         objectName = &lt;
     *     </code>
     * </pre>
     * @param objectName
     * @return
     */
    public static String buildOdinObjectDeclaration(String objectName) {
        return objectName + " = <\n";
    }

    /**
     * <pre>
     *     <code>
     *         propertyName = &lt;"content"&gt;
     *
     *     </code>
     * </pre>
     * @param propertyName
     * @param content
     * @return
     */
    public static String buildOdinStringObjectPropertyInitialization(String propertyName, String content) {
        return propertyName + " = " + buildOdinStringObject(content) + "\n";
    }

    /**
     * <pre>
     *     <code>
     *         propertyName = &lt;True&gt;
     *
     *     </code>
     * </pre>
     * @param propertyName
     * @param bool
     * @return
     */
    public static String buildOdinBooleanObjectPropertyInitialization(String propertyName, Boolean bool) {
        return propertyName + " = " + buildOdinBooleanObject(bool) + "\n";
    }

    /**
     * <pre>
     *     <code>
     *         propertyName = &lt;[0..*]&gt;
     *     </code>
     * </pre>
     * @param propertyName
     * @param cardinality
     * @return
     */
    public static String buildOdinCardinalityInitialization(String propertyName, IntegerIntervalObject cardinality) {
        return propertyName + " = <" + cardinality.toString() + ">\n";
    }

    public static String buildOdinCommentBlockSeparator() {
        return "-- ----------------------------------\n";
    }

    public static String buildOdinComment(String commentText) {
        return "-- " + commentText + "\n";
    }

    public static String buildOdinCommentBlock(String... commentBlock) {
        return buildOdinCommentBlock(0, commentBlock);
    }

    public static String buildOdinCommentBlock(int indentLevel, String... commentBlock) {
        StringBuilder builder = new StringBuilder();
        String indents = indentByTabCount(indentLevel);
        builder.append(indents).append(buildOdinCommentBlockSeparator());
        for(String comment : commentBlock) {
            builder.append(indents).append(buildOdinComment(comment));
        }
        builder.append(indents).append(buildOdinCommentBlockSeparator());
        return builder.toString();
    }

    /**
     * TODO Fix '...' when multiple items exist or simply merge with buildOdinList.
     *
     * @param items
     * @return
     */
    public static String buildOdinStringList(List<String> items) {
        List<String> elements = new ArrayList<>(items);
        if(!items.isEmpty() && items.get(items.size() - 1).equals("...")) { //remove training '...' when present
            elements.remove(items.size() - 1);
        }
        StringBuilder builder = new StringBuilder("<");
        for(int i = 0; i < elements.size(); i++) {
            String item = elements.get(i);
            builder.append("\"").append(item).append("\"");
            if(i < elements.size() - 1) {
                builder.append(", ");
            }
        }
        handleListEnding(elements, builder);
        return builder.toString();
    }

    public static String buildOdinStringListPropertyInitialization(String propertyName, List<String> content) {
        return propertyName + " = " + buildOdinStringList(content) + "\n";
    }

//    public static String buildOdinList(List<String> items) {
//        StringBuilder builder = new StringBuilder("<");
//        for(int i = 0; i < items.size(); i++) {
//            if(i < items.size() - 1) {
//                builder.append("\"").append(items.get(i)).append("\", ");
//            } else {
//                builder.append("\"").append(items.get(i)).append("\"");
//            }
//        }
//        builder.append(">");
//        return builder.toString();
//    }

    /**
     * TODO Fix '...' when multiple items exist or simply merge with buildOdinList.
     *
     * @param items
     * @return
     */
    public static String buildOdinIntegerList(List<Integer> items) {
        List<Integer> elements = new ArrayList<>(items);
        if(items.size() > 0 && items.get(items.size() - 1).equals("...")) { //remove training '...' when present
            elements.remove(items.size() - 1);
        }
        StringBuilder builder = new StringBuilder("<");
        for(int i = 0; i < elements.size(); i++) {
            Integer item = elements.get(i);
            builder.append(item);
            if(i < elements.size() - 1) {
                builder.append(", ");
            }
        }
        handleListEnding(elements, builder);
        return builder.toString();
    }

    public static String buildOdinIntegerListPropertyInitialization(String propertyName, List<Integer> content) {
        return propertyName + " = " + buildOdinIntegerList(content) + "\n";
    }

    protected static void handleListEnding(List<?> elements, StringBuilder builder) {
        int itemCount = elements.size();
        if(itemCount == 0) {
            builder.append("...>");
        } else if(itemCount == 1) {
            builder.append(",...>");
        } else {
            builder.append(">");
        }
    }

    /**
     * Method generates a string with the specified number of
     * tabs.
     *
     * @param tabCount
     * @return
     */
    public static String indentByTabCount(int tabCount) {
        return "\t".repeat(Math.max(0, tabCount));
    }

    /**
     * Method builds a single line declaration for a keyed object:
     * <br>
     * <pre>
     *     <code>
     *         ["key"] = &lt;
     *
     *     </code>
     * </pre>
     * @param key
     * @return
     */
    public static String buildKeyedObjectOpeningDeclaration(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\"").append(key).append("\"] = <").append("\n");
        return builder.toString();
    }

    /**
     * Method builds a single line declaration for a keyed object:
     * <br>
     * <pre>
     *     <code>
     *         ["key"] = (cast) &lt;
     *
     *     </code>
     * </pre>
     * @param key
     * @return
     */
    public static String buildKeyedObjectOpeningDeclarationWithCast(String key, String cast) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\"").append(key).append("\"] = ").append("(").append(cast).append(") <").append("\n");
        return builder.toString();
    }

    /**
     * Method builds a single line declaration for a keyed object:
     * <br>
     * <pre>
     *     <code>
     *         ["key"] = &lt;"value"&gt;
     *
     *     </code>
     * </pre>
     * @param key
     * @return
     */
    public static String buildKeyedStringObjectdeclaration(String key, String value) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\"").append(key).append("\"] = ").append("<\"").append(value).append("\">").append("\n");
        return builder.toString();
    }

    /**
     * Method builds a single cast line declaration:
     * <br>
     * <pre>
     *     <code>
     *         (cast) &lt;
     *
     *     </code>
     * </pre>
     * @param cast
     * @return
     */
    public static String buildOpeningDeclarationWithCast(String cast) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(cast).append(") <").append("\n");
        return builder.toString();
    }

    /**
     * <pre>
     *     <code>
     *         objectName = &lt;
     *
     *     </code>
     * </pre>
     * @param objectName
     * @return
     */
    public static String buildObjectOpeningDeclaration(String objectName) {
        StringBuilder builder = new StringBuilder();
        builder.append(objectName).append(" = <").append("\n");
        return builder.toString();
    }

    /**
     * <pre>
     *     <code>
     *         objectName = (cast) &lt;
     *
     *     </code>
     * </pre>
     * @param objectName
     * @param cast
     * @return
     */
    public static String buildObjectOpeningDeclarationWithCast(String objectName, String cast) {
        StringBuilder builder = new StringBuilder();
        builder.append(objectName).append(" = (").append(cast).append(") <").append("\n");
        return builder.toString();
    }

    /**
     * <br>
     * <pre>
     *     <code>
     *         &gt;
     *
     *     </code>
     * </pre>
     * @return
     */
    public static String closeOdinObject() {
        StringBuilder builder = new StringBuilder();
        builder.append(">\n");
        return builder.toString();
    }
}
