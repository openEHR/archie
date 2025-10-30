package com.nedap.archie.xml.adapters;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.List;

/**
 * JAXB adapter for deserializing lifecycle_state from XML.
 * <p>
 * Supports two XML representations:
 * 1. Current/simple text form: {@code <lifecycle_state>published</lifecycle_state>}
 * 2. Legacy form with code_string: {@code <lifecycle_state><code_string>published</code_string></lifecycle_state>}
 *
 * The adapter always returns the lifecycle state value as a plain String, regardless of the input format.
 */
public class LifecycleStateXmlAdapter extends XmlAdapter<LifecycleStateXmlAdapter.MixedHolder, String> {

    /**
     * Unmarshalls a lifecycle_state element to a String.
     * <p>
     * Attempts to extract the lifecycle state value from either:
     * - A nested {@code <code_string>} element
     * - Direct text content (legacy simple form)
     *
     * @param holder the mixed content holder containing the element's content
     * @return the lifecycle state value as a trimmed String, or null if no value is found
     */
    @Override
    public String unmarshal(MixedHolder holder) {
        if (holder == null || holder.content == null || holder.content.isEmpty()) {
            return null;
        }

        for (Object o : holder.content) {
            if (o instanceof Element) {
                Element element = (Element) o;
                String elementName = element.getLocalName() != null ? element.getLocalName() : element.getNodeName();

                if ("code_string".equals(elementName)) {
                    String text = element.getTextContent();
                    return text != null ? text.trim() : null;
                }
            }
        }

        for (Object o : holder.content) {
            if (o instanceof String) {
                String text = ((String) o).trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }

        return null;
    }

    /**
     * Marshals a String to a lifecycle_state element.
     * <p>
     * Not implemented for this adapter as marshaling is not required.
     *
     * @param v the lifecycle state value
     * @return null (marshaling not supported)
     */
    @Override
    public MixedHolder marshal(String v) {
        return null;
    }

    /**
     * Holder class for mixed XML content (text and elements).
     * <p>
     * Used by JAXB to capture both text nodes and child elements within the
     * lifecycle_state element. The content list may include whitespace strings
     * (from XML formatting) and Element objects.
     */
    @XmlRootElement(name = "lifecycle_state")
    public static class MixedHolder {
        /**
         * Mixed content: contains both text strings (including whitespace from formatting)
         * and Element nodes (such as {@code <code_string>}).
         */
        @XmlMixed
        @XmlAnyElement(lax = true)
        public List<Object> content;
    }
}