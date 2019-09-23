package org.kik.bot.core.handler;

import org.kik.bot.core.model.GroupChatMember;
import org.kik.bot.core.model.PersonChat;
import org.kik.bot.core.translator.JidTranslator;

/**
 * MessageHandler with Translate jids to PersonChat or GroupChatMember from looking it up from the RosterHandler,
 * the jidTranslator also procs a roster refresh if a member is not in the group.
 */
public class DetailedRosterMessageHandler extends MessageHandler {
    private final JidTranslator jidTranslator;

    public DetailedRosterMessageHandler(JidTranslator jidTranslator) {
        super();
        this.jidTranslator = jidTranslator;
    }

    @Override
    protected PersonChat getPersonChat(String jid) {
        return jidTranslator.getPersonChat(jid);
    }


    @Override
    protected GroupChatMember getGroupChatMember(String memberJid, String groupJid) {
        return jidTranslator.getGroupMember(memberJid, groupJid);
    }
}
