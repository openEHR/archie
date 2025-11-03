package com.nedap.archie.xml.adapters;

import com.nedap.archie.base.terminology.TerminologyCode;
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
 * 1. Future/simple text form: {@code <lifecycle_state>published</lifecycle_state>}
 * 2. Current form with code_string: {@code <lifecycle_state><code_string>published</code_string></lifecycle_state>}
 *
 * The adapter always returns the lifecycle state value as a plain String, regardless of the input format.
 */
public class LifecycleStateXmlAdapter extends XmlAdapter<LifecycleStateXmlAdapter.MixedHolder, TerminologyCode> {

    /**
     * Unmarshalls a lifecycle_state element to a String.
     *
     * @param holder the mixed content holder containing the element's content
     * @return the lifecycle state value as a trimmed String, or null if no value is found
     */
    @Override
    public TerminologyCode unmarshal(MixedHolder holder) {
        if (holder == null || holder.content == null || holder.content.isEmpty()) {
            return null;
        }

        TerminologyCode resultTerminologyCode = new TerminologyCode();

        for (Object o : holder.content) {
            if (o instanceof Element) {
                Element element = (Element) o;
                String elementName = element.getLocalName() != null ? element.getLocalName() : element.getNodeName();

                if ("code_string".equals(elementName)) {
                    String text = element.getTextContent();
                    if (text != null) {
                        resultTerminologyCode.setCodeString(text.trim());
                        return resultTerminologyCode;
                    }
                }
            }
        }

        for (Object o : holder.content) {
            if (o instanceof String) {
                String text = ((String) o).trim();
                if (!text.isEmpty()) {
                    resultTerminologyCode.setCodeString(text.trim());
                    return resultTerminologyCode;
                }
            }
        }

        return null;
    }

    /**
     * Not implemented for this adapter as marshaling is not required.
     */
    @Override
    public MixedHolder marshal(TerminologyCode v) {
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
        @XmlMixed
        @XmlAnyElement(lax = true)
        public List<Object> content;
    }
}