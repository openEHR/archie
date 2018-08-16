package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pieter.bos on 22/02/16.
 */
public class RMObjectValidatingProcessor {

    private List<RMObjectValidationMessage> messages = new ArrayList<>();

    public List<RMObjectValidationMessage> getMessages() {
        return messages;
    }

    protected void clearMessages() {
        messages.clear();
    }

    protected void addMessage(RMObjectValidationMessage message) {
        messages.add(message);
    }

    protected void addMessage(CObject cobject, String actualPath, String message) {
        messages.add(new RMObjectValidationMessage(cobject, actualPath, message));
    }

    protected void addMessage(CObject cobject, String actualPath, String message, RMObjectValidationMessageType type) {
        messages.add(new RMObjectValidationMessage(cobject, actualPath, message, type));
    }

    protected void addAllMessages(Collection<RMObjectValidationMessage> messages) {
        this.messages.addAll(messages);
    }

    protected void addAllMessagesFrom(RMObjectValidatingProcessor other) {
        addAllMessages(other.getMessages());

    }

}
