package net.lynx.client.objects;

import net.lynx.client.Constants;
import net.lynx.client.utils.CryptoUtils;

import java.util.UUID;

public class XMPPHolder {
    public static Node login_xmpp(String username, String password){
        Node iq = new Node("iq");
        iq.addAttribute("type", "set");
        iq.addAttribute("id", UUID.randomUUID().toString());
        Node query = new Node("query");
        query.setNamespace("jabber:iq:register");
        query.addTextNode("username", username);
        query.addTextNode("passkey-u", CryptoUtils.hashPassword(username, password));
        query.addTextNode("device-id", Constants.device_id);
        query.addTextNode("install-date", String.valueOf(System.currentTimeMillis() - 69000));
        query.addTextNode("device-type", "android");
        query.addTextNode("brand", "generic");
        query.addTextNode("logins-since-install", "1");
        query.addTextNode("version", "14.8.0.14887");
        query.addTextNode("lang", "en_US");
        query.addTextNode("android-sdk", "19");
        query.addTextNode("registrations-since-install", "0");
        query.addTextNode("prefix", "CAN");
        query.addTextNode("android-id", "849d4ffb0c020de7");
        query.addTextNode("model", "Samsung Galaxy S5 - 4.4.4 - API 19 - 1080x1920");
        iq.addChild(query);
        return iq;
    }
}
