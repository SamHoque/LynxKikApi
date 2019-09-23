package org.kik.bot.core.handler;

import net.lynx.client.objects.Node;
import org.apache.log4j.Logger;
import org.kik.bot.core.validator.XmlValidator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import static org.kik.bot.core.constants.IqType.IQ;
import static org.kik.bot.core.util.MessageType.MESSAGE;

public class DataHandler {
    private static final Logger LOG = Logger.getLogger(DataHandler.class);
    private final RosterHandler rosterHandler;
    private final MessageHandler messageHandler;
    private final StringToNodeParser stringToNodeParser;
    private final XmlValidator xmlValidator;

    public DataHandler(RosterHandler rosterHandler, MessageHandler messageHandler) {
        this.rosterHandler = rosterHandler;
        this.messageHandler = messageHandler;
        stringToNodeParser = new StringToNodeParser();
        xmlValidator = new XmlValidator();
    }

    public void handleData(String data) {
        String completeData = xmlValidator.getAsCompleteValidXml(data);
        if(!xmlValidator.isStringValidXml(completeData)) {
            LOG.warn("[System] Received invalid xml response from kik server. \n" + completeData);
            return;
        }

        try {
            Node node = stringToNodeParser.stringToNode(completeData);
            String nodeName = node.getName();
            if (MESSAGE.equalsIgnoreCase(nodeName)) {
                getMessageHandler().handle(node);
            } else if(IQ.equalsIgnoreCase(nodeName)) {
                getRosterHandler().handle(node);
            } else if("ack".equalsIgnoreCase(nodeName)) {
                LOG.trace("[System] Ignoring <ack> response");
            } else {
                LOG.warn("[System] Unexpected response: " + completeData);
            }
        } catch (XmlPullParserException | IOException e) {
            LOG.error("[System] Parsing/IO exception from handling data response: " + completeData, e);
        } catch (Exception e) {
            LOG.error("[System] Unexpected Error from handling data response: " + completeData, e);
        }
    }

    private RosterHandler getRosterHandler() {
        return rosterHandler;
    }

    private MessageHandler getMessageHandler() {
        return messageHandler;
    };

    private static class StringToNodeParser {
        private Node stringToNode(String data) throws XmlPullParserException, IOException {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(data));
            return new Node(null, parser);
        }
    }
}
