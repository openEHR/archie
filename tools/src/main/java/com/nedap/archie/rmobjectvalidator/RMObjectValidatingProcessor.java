package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 22/02/16.
 */
public class RMObjectValidatingProcessor {

    @FunctionalInterface
    public interface RMObjectValidationMessageSink {
        void addMessage(RMObjectValidationMessage message);

        default void addAll(List<RMObjectValidationMessage> messages) {
            if (messages != null) {
                messages.forEach(this::addMessage);
            }
        }
    }

    protected final ValidationMessages messages = new ValidationMessages();

    public List<RMObjectValidationMessage> getMessages() {
        return messages.getMessages();
    }

    protected void clearMessages() {
        messages.clear();
    }

    protected void addMessage(ValidationMessages result, RMObjectValidationMessage message) {
        result.addMessage(message);
    }

    protected void addMessage(RMObjectValidationMessage message) {
        addMessage(messages, message);
    }

    protected void addMessage(CObject cobject, String actualPath, String message) {
        addMessage(messages, new RMObjectValidationMessage(cobject, actualPath, message));
    }

    protected void addMessage(CObject cobject, String actualPath, String message, RMObjectValidationMessageType type) {
        addMessage(messages, new RMObjectValidationMessage(cobject, actualPath, message, type));
    }

    protected void addAllMessages(List<RMObjectValidationMessage> messages) {
        messages.forEach(this::addMessage);
    }

    protected void addAllMessagesFrom(RMObjectValidatingProcessor other) {
        if (other.messages.messages != null) {
            other.messages.messages.forEach(this::addMessage);
        }
    }

    protected static final class ValidationMessages {

        private List<RMObjectValidationMessage> messages = null;

        public void addMessage(RMObjectValidationMessage message) {
            if (message == null) {
                return;
            }
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            this.messages.add(message);
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
