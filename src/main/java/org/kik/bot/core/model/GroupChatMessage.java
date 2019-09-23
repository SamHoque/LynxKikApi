package org.kik.bot.core.model;

public class GroupChatMessage extends Message {
    private final String memberJid;
    private final String groupJid;
    private final String memberName;
    private final String groupName;
    private final boolean fromAdmin;

    public GroupChatMessage(String memberJid, String groupJid, String memberName, String groupName, String message, boolean fromAdmin, boolean isMultiMedia) {
        super(message, isMultiMedia);
        this.memberJid = memberJid;
        this.groupJid = groupJid;
        this.memberName = memberName;
        this.groupName = groupName;
        this.fromAdmin = fromAdmin;
    }

    public GroupChatMessage(GroupChatMember groupChatMember, String message, boolean isMultiMedia) {
        super(message, isMultiMedia);
        memberJid = groupChatMember.getJid();
        groupJid = groupChatMember.getGroupJid();
        memberName = groupChatMember.getDisplayName();
        groupName = groupChatMember.getGroupName();
        fromAdmin = groupChatMember.isAdmin();
    }

    public String getMemberJid() {
        return memberJid;
    }

    public String getGroupJid() {
        return groupJid;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isFromAdmin() {
        return fromAdmin;
    }
}
