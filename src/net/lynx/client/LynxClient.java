package net.lynx.client;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class LynxClient {
    public static void main(String[] args) {
        try {
            KikClient kikClient = new KikClient();
            kikClient.login_to_kik_server("TestUsername97", "TestPass");
            kikClient.setOnDataReceived(System.out::println);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
