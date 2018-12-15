package net.lynx.client.objects;

import net.lynx.client.Constants;
import net.lynx.client.utils.CryptoUtils;

import java.util.UUID;

import static net.lynx.client.Constants.KIK_VERSION;
import static net.lynx.client.Constants.android_id;

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
        query.addTextNode("install-referrer", "utm_source=google-play&amp;utm_medium=organic");
        query.addTextNode("operator", "310260");
        query.addTextNode("install-date", "1494078709023");
        query.addTextNode("device-type", "android");
        query.addTextNode("brand", "generic");
        query.addTextNode("logins-since-install", "1");
        query.addTextNode("version", KIK_VERSION);
        query.addTextNode("lang", "en_US");
        query.addTextNode("android-sdk", "19");
        query.addTextNode("registrations-since-install", "0");
        query.addTextNode("prefix", "CAN");
        query.addTextNode("android-id", android_id);
        query.addTextNode("model", "Samsung Galaxy S5 - 4.4.4 - API 19 - 1080x1920");
        iq.addChild(query);
        return iq;
    }
}
