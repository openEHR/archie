/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017  Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
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
package org.openehr.utils.message;


public class MessageDescriptor {

    private MessageCode code;
    private MessageSeverity severity;
    private String message;
    private String location;

    public MessageCode getCode() {
        return code;
    }

    public void setCode(MessageCode code) {
        this.code = code;
    }

    public MessageSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(MessageSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public MessageDescriptor() {
    }

    public MessageDescriptor(MessageCode aCode, MessageSeverity aSeverity, String aMessage, String aLoc) {
        code = aCode;
        severity = aSeverity;
        message = aMessage;
        location = aLoc;
    }

    public static MessageDescriptor createError(MessageCode aCode, String aMessage, String aLoc) {
        return create(aCode, MessageSeverity.ERROR, aMessage, aLoc);
    }

    public static MessageDescriptor createWarning(MessageCode aCode, String aMessage, String aLoc) {
        return create(aCode, MessageSeverity.WARNING, aMessage, aLoc);
    }

    public static MessageDescriptor createInfo(MessageCode aCode, String aMessage, String aLoc) {
        return create(aCode, MessageSeverity.INFO, aMessage, aLoc);
    }

    public static MessageDescriptor createDebug(MessageCode aCode, String aMessage, String aLoc) {
        return create(aCode, MessageSeverity.DEBUG, aMessage, aLoc);
    }

    public static MessageDescriptor create(MessageCode aCode, MessageSeverity aSeverity, String aMessage, String aLoc) {
        return new MessageDescriptor(aCode, aSeverity, aMessage, aLoc);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(severity.name())
                .append(" ");
        if (location != null && !location.trim().isEmpty()) {
            builder.append(location).append(": ");
        }
        builder.append("(")
                .append(code)
                .append(") ")
                .append(message);
        return builder.toString();
    }
}
