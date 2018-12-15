package net.lynx.client;

import net.lynx.client.objects.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class KikDataHandler {
    public static void handleData(String data, KikClient kikClient) {
        Node node;
        System.out.println(data);
        try {
            node = kikClient.getNode(data);
            String nodeName = node.getName();
            if (nodeName.equals("message")) {
                String type = node.getAttribute("type");
                String jid = node.getAttribute("from");
                String id = node.getAttribute("id");
                Node friendattr = node.getFirstChildByName("friend-attribution");
                if (type.equals("chat")) {
                    Node body = node.getFirstChildByName("body");
                    if (body != null) {
                        String messageText = body.getText();
                        if (messageText != null) {
                            kikClient.sendMessage("pong", jid, false);
                        }
                    } else if (friendattr != null) {
                        kikClient.sendMessage("Thanks for adding me as a friend! ", jid, false);
                    }
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
}
