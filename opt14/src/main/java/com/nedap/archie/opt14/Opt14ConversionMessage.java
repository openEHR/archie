package com.nedap.archie.opt14;

import org.openehr.utils.message.MessageCode;

public enum Opt14ConversionMessage implements MessageCode {
    PATH_CONVERSION_ERROR("Could not convert annotation and rm_overlay paths. They will possibly not be correct: {0}");


    private final String messageTemplate;

    Opt14ConversionMessage(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
