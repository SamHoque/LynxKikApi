package org.kik.bot.core.model;

import net.lynx.client.objects.Node;

import java.util.Optional;

public class GroupChatMember {
    private String jid;
    private String displayName;
    private String groupJid;
    private String groupName;
    private boolean isAdmin;

    public GroupChatMember(Node member, String groupJid, String groupName) {
        this.jid = member.getText();
        this.groupJid = groupJid;
        this.groupName = groupName;
        Optional.ofNullable(member.getAttribute("a"))
                .ifPresent(value -> isAdmin = "1".equalsIgnoreCase(value));
    }


    public GroupChatMember(String jid, String displayName) {
        this.jid = jid;
        this.displayName = displayName;
    }

    public GroupChatMember(String jid, String groupJid, String displayName, String groupName) {
        this.jid = jid;
        this.groupJid = groupJid;
        this.displayName = displayName;
        this.groupName = groupName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getGroupJid() {
        return groupJid;
    }

    public void setGroupJid(String groupJid) {
        this.groupJid = groupJid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Member{" +
                "jid='" + jid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", groupJid='" + groupJid + '\'' +
                ", groupName='" + groupName + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
