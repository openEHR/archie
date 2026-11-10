package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 22/02/16.
 */
public class RMObjectValidatingProcessor {

    protected final ValidationMessages messages = new ValidationMessages();

    public List<RMObjectValidationMessage> getMessages() {
        return messages.getMessages();
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

    protected void addAllMessages(List<RMObjectValidationMessage> messages) {
        this.messages.addAll(messages);
    }

    protected void addAllMessagesFrom(RMObjectValidatingProcessor other) {
        this.messages.addAll(other.messages.messages);

    }

    public static final class ValidationMessages {

        private List<RMObjectValidationMessage> messages = null;

        public void add(RMObjectValidationMessage message) {
            if (message == null) {
                return;
            }
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            this.messages.add(message);
        }

        public void addAll(List<RMObjectValidationMessage> newMessages) {
            if (newMessages == null || newMessages.isEmpty()) {
                return;
            }
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            this.messages.addAll(newMessages);

        }

        public List<RMObjectValidationMessage> getMessages() {
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            return this.messages;
        }

        public void clear() {
            if (this.messages != null) {
                this.messages.clear();
            }
        }

        public boolean isEmpty() {
            return this.messages == null || this.messages.isEmpty();
        }
    }

}
