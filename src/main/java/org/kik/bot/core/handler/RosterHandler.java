package org.kik.bot.core.handler;

import net.lynx.client.objects.Node;
import org.apache.log4j.Logger;
import org.kik.bot.core.KikClient;
import org.kik.bot.core.model.Chat;
import org.kik.bot.core.model.GroupChat;
import org.kik.bot.core.model.GroupChatMember;
import org.kik.bot.core.model.PersonChat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.kik.bot.core.constants.IqType.*;

public class RosterHandler {
    private static final Logger LOG = Logger.getLogger(RosterHandler.class);

    private final KikClient kikClient;

    private Map<String, Chat> chatInteractionMap;
    private Map<String, String> groupChatMemberGroupChatMap;

    public RosterHandler(KikClient kikClient) {
        LOG.debug("[System] Initializing Empty Roster.");
        this.kikClient = kikClient;
        chatInteractionMap = new HashMap<>();
        groupChatMemberGroupChatMap = new HashMap<>();
    }

    public Map<String, Chat> getChatInteractionMap() {
        return chatInteractionMap;
    }

    public Map<String, String> getGroupChatMemberGroupChatMap() {
        return groupChatMemberGroupChatMap;
    }

    public void handle(Node node) {
        Node query = node.getFirstChildByName(QUERY);
        String queryType = query.getAttribute(XMLNS);
        if (XMLNS_ROSTER_REQUEST.equalsIgnoreCase(queryType)) {
            updateRoster(query);
        } else if(XMLNS_MEMBER_DETAIL_REQUEST.equalsIgnoreCase(queryType)) {
            updateChatGroupMember(query);
        } else {
            LOG.warn("[System] Unexpected <iq> query: " + node);
        }
    }

    private void updateRoster(Node rosterListWrapper) {
        for(Node rosterList: rosterListWrapper.getChildren()) {
            String nodeName = rosterList.getName();
            if("item".equalsIgnoreCase(nodeName)) {
                PersonChat item = new PersonChat(rosterList);
                chatInteractionMap.put(item.getJid(), item);
                LOG.debug("[System] Registered/Updated PersonChat[" + item.getJid() + "]");
            } else if("g".equalsIgnoreCase(nodeName)) {
                GroupChat group = new GroupChat(rosterList);
                String groupJid = group.getJid();
                Set<String> groupMemberJids = group.getMembers().keySet();
                for(String memberJid: groupMemberJids) {
                    addToMemberGroupMap(memberJid, groupJid);
                    requestMemberDetails(memberJid);
                }
                chatInteractionMap.put(groupJid, group);
                LOG.debug("[System] Registered/Updated GroupChat[" + groupJid + "]");
                //todo: implement update groupChat: would probably need to update the membership of the groupChat that's in the chat interaction map rather that overwriting it every time.
                // would need to add/remove chatGroupMembers based on the what's in the node and what's in the current chatGroup in the chatInteractionMap
            } else if("remove-group".equalsIgnoreCase(nodeName)) {
                //todo: implement remove chat interaction: remove entry from chatInteractionMap based on jid
            } else {
                LOG.warn("[System] Unknown Roster request data: " + rosterListWrapper.toString());
            }
        }
    }

    public void requestMemberDetails(String memberJid) {
        kikClient.sendMemberDetailRequest(memberJid);
    }

    public void addToMemberGroupMap(String memberJid, String groupJid) {
        if(groupChatMemberGroupChatMap == null) {
            groupChatMemberGroupChatMap = new HashMap<>();
        }
        groupChatMemberGroupChatMap.put(memberJid, groupJid);
    }

    private void updateChatGroupMember(Node query) {
        Node success = query.getFirstChildByName("success");
        if(success != null) {
            Node item = success.getFirstChildByName("item");
            String memberJid = item.getAttribute("jid");
            String groupJid = groupChatMemberGroupChatMap.get(memberJid);
            if(groupJid == null || "".equalsIgnoreCase(groupJid)) {
                LOG.warn("[System] Couldn't find groupJid for ChatGroupMember[" + memberJid + "].");
                return;
            }
            GroupChat group = (GroupChat) chatInteractionMap.get(groupJid);
            group.updateMemberDetails(memberJid, item);
            chatInteractionMap.put(groupJid, group);
            LOG.debug("[System] Updated Member[" + memberJid + "] for ChatGroup[" + groupJid + "].");
        }
    }

    public void requestFullChatRosterDetails() {
        kikClient.sendFullChatRosterRequest();
    }
}
