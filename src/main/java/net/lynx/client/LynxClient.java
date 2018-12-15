package net.lynx.client;

public class LynxClient {
    public static void main(String[] args) {
        try {
            KikClient kikClient = new KikClient();
            kikClient.login_to_kik_server("z", "z");
            kikClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
