package net.lynx.client;

import net.lynx.client.exception.KikEmptyResponseException;

import java.io.IOException;

public class LynxClient {
    public static void main(String[] args) {
        try {
            new KikClient()
            .login_to_kik_server("TestUsername97", "TestPass");
        } catch (IOException | KikEmptyResponseException e) {
            e.printStackTrace();
        }
    }
}
