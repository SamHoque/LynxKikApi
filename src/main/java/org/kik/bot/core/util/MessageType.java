package org.kik.bot.core.util;

import net.lynx.client.objects.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageType {
    private static final String CHAT = "chat";
    private static final String GROUP_CHAT = "groupchat";
    private static final List<String> IGNORE_PERSONAL_CHAT_JIDS = new ArrayList<>(Collections.singletonList("webpushbot_xyz@talk.kik.com"));
    public static final String MESSAGE = "message";

    public static boolean isGroupChat(Node node) {
        String type = node.getAttribute("type");
        return GROUP_CHAT.equalsIgnoreCase(type) && node.getFirstChildByName("is-typing") == null;
    }

    public static boolean isPersonChat(Node node) {
        String type = node.getAttribute("type");
        String jid = node.getAttribute("from");
        return CHAT.equalsIgnoreCase(type) && !IGNORE_PERSONAL_CHAT_JIDS.contains(jid);
    }
}
