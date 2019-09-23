package org.kik.bot.core.handler;

import org.kik.bot.core.model.GroupChatMember;
import org.kik.bot.core.model.PersonChat;

/**
 * Basic implementation of associating a message to a Chat interaction, just returns model with jids as names, also cant distinguish Group Chat admins
 */
public class BasicMessageHandler extends MessageHandler {

    public BasicMessageHandler() {
        super();
    }

    @Override
    protected PersonChat getPersonChat(String jid) {
        return new PersonChat(jid);
    }

    @Override
    protected GroupChatMember getGroupChatMember(String memberJid, String groupJid) {
        return new GroupChatMember(memberJid, memberJid, groupJid, groupJid);
    }
}
