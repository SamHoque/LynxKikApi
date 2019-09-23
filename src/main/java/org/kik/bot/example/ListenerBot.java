package org.kik.bot.example;

import org.apache.log4j.Logger;
import org.kik.bot.core.KikClient;
import org.kik.bot.core.behaviour.BotBehaviour;
import org.kik.bot.core.handler.DataHandler;
import org.kik.bot.core.handler.DetailedRosterMessageHandler;
import org.kik.bot.core.handler.MessageHandler;
import org.kik.bot.core.handler.RosterHandler;
import org.kik.bot.core.model.GroupChatMessage;
import org.kik.bot.core.model.PersonalChatMessage;
import org.kik.bot.core.translator.JidTranslator;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ListenerBot {
    private static final Logger LOGGER = Logger.getLogger(ListenerBot.class);

    private final String username;
    private final String password;

    public ListenerBot(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void start() throws IOException, XmlPullParserException {
        KikClient kikClient = new KikClient();
        kikClient.login_to_kik_server(username, password);
        //set handlers
        RosterHandler rosterHandler = new RosterHandler(kikClient);
        JidTranslator jidTranslator = new JidTranslator(rosterHandler);
        MessageHandler messageHandler = new DetailedRosterMessageHandler(jidTranslator);

        messageHandler.addBotBehaviour(new ListenerBotBehaviour());
        DataHandler dataHandler = new DataHandler(rosterHandler, messageHandler);
        kikClient.setOnDataReceived(dataHandler::handleData);

        kikClient.start();
    }

    private static class ListenerBotBehaviour implements BotBehaviour {
        @Override
        public void onPersonalChatMessage(PersonalChatMessage message) {
            String displayMessage = String.format("[PM] '%s' said '%s'.", message.getDisplayName(), message.getMessage());
            LOGGER.info(displayMessage);
        }

        @Override
        public void onGroupChatMessage(GroupChatMessage message) {
            String displayMessage = String.format("[GM] from '%s' by '%s' said '%s'.", message.getGroupName(), message.getMemberName(), message.getMessage());
            LOGGER.info(displayMessage);
        }
    }
}
