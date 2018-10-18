package net.lynx.client;

import net.lynx.client.exception.KikEmptyResponseException;

import java.io.IOException;

public class LynxClient {
    public static void main(String[] args) {
        try {
            KikClient kikClient = new KikClient();
            kikClient.login_to_kik_server("TestUsername97", "TestPass");
            kikClient.setOnDataReceived(data -> {
                System.out.println(data);
            });
        } catch (IOException | KikEmptyResponseException e) {
            e.printStackTrace();
        }
    }
}
