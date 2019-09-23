package org.kik.bot.core.model;

public class PersonalChatMessage extends Message{
    private final String jid;
    private final String displayName;

    public PersonalChatMessage(String jid, String displayName, String message, boolean isMultiMedia) {
        super(message, isMultiMedia);
        this.jid = jid;
        this.displayName = displayName;
    }

    public PersonalChatMessage(PersonChat personChat, String message, boolean isMultiMedia) {
        super(message, isMultiMedia);
        jid = personChat.getJid();
        displayName = personChat.getDisplayName();
    }

    public String getJid() {
        return jid;
    }

    public String getDisplayName() {
        return displayName;
    }
}
