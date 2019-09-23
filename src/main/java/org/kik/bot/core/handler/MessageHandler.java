package org.kik.bot.core.handler;

import net.lynx.client.objects.Node;
import org.apache.log4j.Logger;
import org.kik.bot.core.behaviour.BotBehaviour;
import org.kik.bot.core.model.GroupChatMember;
import org.kik.bot.core.model.GroupChatMessage;
import org.kik.bot.core.model.PersonChat;
import org.kik.bot.core.model.PersonalChatMessage;
import org.kik.bot.core.util.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains most of the logic for unwrapping the xml response of a message from kik
 */
public abstract class MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class);

    private final List<BotBehaviour> botBehaviours;

    MessageHandler() {
        botBehaviours = new ArrayList<>();
    }

    public void addBotBehaviour(BotBehaviour botBehaviour) {
        botBehaviours.add(botBehaviour);
    }

    void handle(Node node) {
        if(MessageType.isGroupChat(node)) {
            handleGroupChatMessage(node);
        } else if(MessageType.isPersonChat(node)) {
            handlePersonalChatMessage(node);
        }
    }

    private void handlePersonalChatMessage(Node node) {
        String jid = node.getAttribute("from");
        PersonChat personChat = getPersonChat(jid);
        String message;
        if((message = getSimpleMessage(node)) != null) {
            handlePersonalChatMessage(new PersonalChatMessage(personChat, message, false));
        } else if((message = getMultiMediaMessage(node)) != null){
            handlePersonalChatMessage(new PersonalChatMessage(personChat, message, true));
        } else {
            LOGGER.warn("[System] Can't extract personal chat message: " + node);
        }
    }
    protected abstract PersonChat getPersonChat(String jid);
    private void handlePersonalChatMessage(PersonalChatMessage personalChatMessage) {
        for(BotBehaviour botBehaviour: botBehaviours) {
            botBehaviour.onPersonalChatMessage(personalChatMessage);
        }

    }

    private void handleGroupChatMessage(Node node) {
        String memberJid = node.getAttribute("from");
        Node groupDetailNode = node.getFirstChildByName("g");
        String groupJid = groupDetailNode.getAttribute("jid");

        GroupChatMember member = getGroupChatMember(memberJid, groupJid);

        String message;
        if((message = getSimpleMessage(node)) != null) {
            handleGroupChatMessage(new GroupChatMessage(member, message, false));
        } else if(( message = getMultiMediaMessage(node)) != null){
            handleGroupChatMessage(new GroupChatMessage(member, message, true));
        } else {
            LOGGER.warn("[System] Can't extract group chat message: " + node);
        }
    }
    protected abstract GroupChatMember getGroupChatMember(String memberJid, String groupJid);
    private void handleGroupChatMessage(GroupChatMessage groupChatMessage) {
        for(BotBehaviour botBehaviour: botBehaviours) {
            botBehaviour.onGroupChatMessage(groupChatMessage);
        }
    }

    private String getSimpleMessage(Node node) {
        Node body = node.getFirstChildByName("body");
        if(body != null) {
            return body.getText();
        }
        return null;
    }

    private String getMultiMediaMessage(Node node) {
        Node content = node.getFirstChildByName("content");
        if(content != null){
            return getMessageFromMultiMediaMessage(content);
        }

        LOGGER.warn("[System] Couldn't extract body/content from: " + node.toString());
        return "*** unexpected message content ***";
    }


    private String getMessageFromMultiMediaMessage(Node content) {
        Node uris = content.getFirstChildByName("uris");
        Node strings = content.getFirstChildByName("strings");
        LOGGER.debug("[System] for contentId" + content.getAttribute("id"));
        if(uris.getFirstChildByName("uri") != null) {
            for(Node uri: uris.getChildrensByName("uri")) {
                LOGGER.debug("[System] found " + uri);
                if(validUrl(uri.getText())) {
                    return uri.getText();
                }
            }
        }
        if(strings != null) {
            for(Node uri: strings.getChildrensByName("file-url")) {
                if(validUrl(uri.getText())) {
                    return uri.getText();
                }
            }
        }
        LOGGER.warn("[System] Couldn't extract body/content from: " + content.toString());
        return "*** unexpected multi media content ***";
    }

    private boolean validUrl(String text) {
        if(text == null) {
            return false;
        }
        return text.startsWith("http");
    }
}
