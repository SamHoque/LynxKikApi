package net.lynx.client;

public class LynxClient {
    public static void main(String[] args) {
        try {
            KikClient kikClient = new KikClient();
            kikClient.login_to_kik_server("TestUsername97", "TestPass");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
