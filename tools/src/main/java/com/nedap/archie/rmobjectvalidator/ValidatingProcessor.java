package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pieter.bos on 22/02/16.
 */
public class ValidatingProcessor {

    private List<ValidationMessage> messages = new ArrayList<>();

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    protected void clearMessages() {
        messages.clear();
    }

    protected void addMessage(ValidationMessage message) {
        messages.add(message);
    }

    protected void addMessage(CObject cobject, String actualPath, String message) {
        messages.add(new ValidationMessage(cobject, actualPath, message));
    }

    protected void addMessage(CObject cobject, String actualPath, String message, ValidationMessageType type) {
        messages.add(new ValidationMessage(cobject, actualPath, message, type));
    }

    protected void addAllMessages(Collection<ValidationMessage> messages) {
        this.messages.addAll(messages);
    }

    protected void addAllMessagesFrom(ValidatingProcessor other) {
        addAllMessages(other.getMessages());

    }

}
