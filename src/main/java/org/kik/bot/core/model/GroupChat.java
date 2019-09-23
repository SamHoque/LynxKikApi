package org.kik.bot.core.model;

import net.lynx.client.objects.Node;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Optional;

public class GroupChat extends Chat {
    private static final Logger LOG = Logger.getLogger(GroupChat.class);

    private String code;
    private String name;
    private HashMap<String, GroupChatMember> members;

    public GroupChat(Node group) {
        super(group);
        Optional.ofNullable(group.getFirstChildByName("code"))
                .ifPresent(node -> code = node.getText());
        Optional.ofNullable(group.getFirstChildByName("n"))
                .ifPresent(node -> name = node.getText());
        initMembers(group.getChildrensByName("m"));
    }

    private void initMembers(Node[] groupMembers) {
        members = new HashMap<>();
        for(Node groupMember: groupMembers) {
            GroupChatMember member = new GroupChatMember(groupMember, getJid(), getName());
            String memberJid = member.getJid();
            members.put(memberJid, member);
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, GroupChatMember> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, GroupChatMember> members) {
        this.members = members;
    }

    public GroupChatMember findMemberByJid(String jid) {
        return members.get(jid);
    }

    public void updateMemberDetails(String memberJid, Node item) {
        GroupChatMember member = members.get(memberJid);
        if(member == null) {
            LOG.warn("Member[" + memberJid + "] is not in ChatGroup[" + getJid() + "].");
            return;
        }
        String displayName = item.getFirstChildByName("display-name").getText();
        LOG.debug("[System] Member[" + memberJid + "] of ChatGroup[" + getJid() + "] set displayName to: " + displayName);
        member.setDisplayName(displayName);
        members.put(memberJid, member);
    }

    @Override
    public String getDefaultDisplayName() {
        return getName();
    }

    @Override
    public String toString() {
        return "ChatGroup{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}

