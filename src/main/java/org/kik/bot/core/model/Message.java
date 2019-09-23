package org.kik.bot.core.model;

public abstract class Message {
    private final String message;
    private final boolean isMultiMedia;

    protected Message(String message, boolean isMultiMedia) {
        this.message = message;
        this.isMultiMedia = isMultiMedia;
    }

    public String getMessage() {
        return message;
    }

    public boolean isMultiMedia() {
        return isMultiMedia;
    }
}
