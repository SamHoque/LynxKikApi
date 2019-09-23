package org.kik.bot.core.translator;

import org.apache.log4j.Logger;
import org.kik.bot.core.handler.RosterHandler;
import org.kik.bot.core.model.Chat;
import org.kik.bot.core.model.GroupChat;
import org.kik.bot.core.model.GroupChatMember;
import org.kik.bot.core.model.PersonChat;

import java.util.Map;

public class JidTranslator {
    private static final Logger LOG = Logger.getLogger(JidTranslator.class);

    private final RosterHandler rosterHandler;

    public JidTranslator(RosterHandler rosterHandler) {
        this.rosterHandler = rosterHandler;
    }

    private GroupChat getChatGroup(String jid) {
        if(jid == null || "".equals(jid)) {
            LOG.warn("Couldn't find Member[" + jid + "] because of missing ChatGroup in roster, will queue for a full Roster Request");
            rosterHandler.requestFullChatRosterDetails();
            return null;
        }

        Chat chat = getChatList().get(jid);
        if(chat instanceof GroupChat) {
            return (GroupChat) chat;
        }

        LOG.warn("[System] expected jid: " + jid + " to return a ChatGroup but found a " + chat.getClass());
        return null;
    }

    private Map<String, Chat> getChatList() {
        return rosterHandler.getChatInteractionMap();
    }

    private Map<String, String> getMemberGroupMap() {
        return rosterHandler.getGroupChatMemberGroupChatMap();
    }

    public PersonChat getPersonChat(String jid) {
        Chat chat = getChatList().get(jid);
        if(chat instanceof PersonChat) {
            return (PersonChat) chat;
        }

        LOG.warn("Couldn't find name for ChatPerson[" + jid + "].");
        return new PersonChat(jid);
    }

    public GroupChatMember getGroupMember(String jid, String groupJid) {
        GroupChat groupChat = getChatGroup(groupJid);
        if(groupChat != null) {
            if(jid.equalsIgnoreCase(groupJid)) {
                return new GroupChatMember(jid, groupChat.getName() + "'(Status)'");
            }

            GroupChatMember member = groupChat.findMemberByJid(jid);
            if (member == null) {
                LOG.warn("Couldn't translate member jid: " + jid + " to name because member jid not in ChatGroup[" + groupJid + "].");
                rosterHandler.addToMemberGroupMap(jid, groupJid);
                rosterHandler.requestMemberDetails(jid);
                return new GroupChatMember(jid, "non-member");
            } else {
                return member;
            }
        }

        LOG.warn("Couldn't translate member jid: " + jid + " to name because ChatGroup[" + groupJid + "] doesn't exit.");
        return new GroupChatMember(jid, jid);
    }
}
