package net.lynx.client;

import net.lynx.client.exception.KikEmptyResponseException;
import net.lynx.client.exception.KikErrorException;
import net.lynx.client.objects.Node;
import net.lynx.client.utils.CryptoUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static net.lynx.client.Constants.kikHost;
import static net.lynx.client.Constants.kikPort;

public class KikClient {
    private OnDataReceivedListener onDataReceived;
    private SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(kikHost, kikPort);
    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
    private InputStream is = sslsocket.getInputStream();

    public KikClient() throws IOException {
        setOnDataReceived(KikDataHandler::handleData);
        sslsocket.startHandshake();
        connect_to_kik_server();
    }

    private static void Log(Object object) {
        System.out.println(object);
    }

    private void connect_to_kik_server() {
        String initial_connection_payload = "<k anon=\"\">";
        Log("Connecting to kik server...");
        try {
            write_to_kik_server(initial_connection_payload);
            String response = read_from_kik_server_once();
            if (!response.equals("<k ok=\"1\">")) {
                throw new KikErrorException("Could not connect to kik server: " + response);
            }
            Log("Connected to kik server");
            new Thread(() -> {
                byte[] buffer = new byte[32768];
                String data;
                try {
                    for (int b; ((b = is.read(buffer)) > 0); ) {
                        data = new String(buffer, 0, b, StandardCharsets.UTF_8);
                        onDataReceived.onDataReceived(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            Log("Failed connecting to kik server...");
            e.printStackTrace();
        } catch (KikErrorException | KikEmptyResponseException e) {
            e.printStackTrace();
        }
    }

    public void login_to_kik_server(String username, String password) throws IOException, KikEmptyResponseException {
        Node iq = new Node("iq");
        iq.addAttribute("type", "set");
        iq.addAttribute("id", UUID.randomUUID().toString());
        Node query = new Node("query");
        query.setNamespace("jabber:iq:register");
        query.addTextNode("username", username);
        query.addTextNode("passkey-u", CryptoUtils.hashPassword(username, password));
        query.addTextNode("device-id", "167da12427ee4dc4a36b40e8debafc26");
        query.addTextNode("install-date", String.valueOf(System.currentTimeMillis() - 69000));
        query.addTextNode("device-type", "android");
        query.addTextNode("brand", "generic");
        query.addTextNode("logins-since-install", "1");
        query.addTextNode("version", "14.8.0.14887");
        query.addTextNode("lang", "en_US");
        query.addTextNode("android-sdk", "28");
        query.addTextNode("registrations-since-install", "0");
        query.addTextNode("prefix", "CAN");
        query.addTextNode("android-id", "ef5012723e606cd9");
        query.addTextNode("model", "Samsung Galaxy S5 - 4.4.4 - API 19 - 1080x1920");
        iq.addChild(query);
        write_to_kik_server(iq.toString());
    }

    private void register_to_kik(String username, String password, String email) throws IOException {
        String usernamePassKey = CryptoUtils.hashPassword(username, password);
        String emailPassKey = CryptoUtils.hashPassword(email, password);
        Node iq = new Node("iq");
        iq.addAttribute("type", "set");
        iq.addAttribute("id", UUID.randomUUID().toString());
        Node query = new Node("query");
        query.setNamespace("jabber:iq:register");
        query.addTextNode("email", email);
        query.addTextNode("passkey-e", emailPassKey);
        query.addTextNode("passkey-u", usernamePassKey);
        query.addTextNode("device-id", "JDQ39");
        query.addTextNode("username", username);
        query.addTextNode("first", "Made");
        query.addTextNode("last", "By Rab");
        query.addTextNode("birthday", "1984-7-25");
        query.addTextNode("version", "14.8.0.14887");
        query.addTextNode("install-date", String.valueOf(System.currentTimeMillis() - 69000));
        query.addTextNode("device-type", "android");
        query.addTextNode("brand", "HTC");
        query.addTextNode("logins-since-install", "0");
        query.addTextNode("lang", "en_US");
        query.addTextNode("android-sdk", "17");
        query.addTextNode("registrations-since-install", "0");
        query.addTextNode("prefix", "CAN");
        query.addTextNode("android-id", "ef5606e327210cd9");
        query.addTextNode("model", "EndeavorU");
        iq.addChild(query);
        write_to_kik_server(iq.toString());
    }

    public void establishConnection() {
        //TODO: write a way to auth the connection to kik after logging in
    }

    public void sendMessage(String body, String jid, boolean isGroup) throws IOException {
        Node message = new Node("message")
                .addAttribute("type", isGroup ? "groupchat" : "chat")
                .addAttribute("to", jid)
                .addAttribute("id", UUID.randomUUID().toString())
                .addAttribute("cts", String.valueOf(System.currentTimeMillis()))
                .addTextNode("body", body).addTextNode("preview", body);

        Node kik = new Node("kik")
                .addAttribute("push", "true")
                .addAttribute("qos", "true")
                .addAttribute("timestamp", String.valueOf(System.currentTimeMillis()));

        Node request = new Node("request")
                .addAttribute("xmlns", "kik:message:receipt")
                .addAttribute("r", "true")
                .addAttribute("d", "true")
                .addEmptyNode("ri");

        message.addChild(kik);
        message.addChild(request);
        write_to_kik_server(message.toString());
    }

    private void write_to_kik_server(String date) throws IOException {
        writer.write(date);
        writer.flush();
    }

    private String read_from_kik_server_once() throws IOException, KikEmptyResponseException {
        byte[] buffer = new byte[32768];
        String data;
        data = new String(buffer, 0, is.read(buffer), StandardCharsets.UTF_8);
        if (data.isEmpty()) {
            throw new KikEmptyResponseException("Kik server returned empty response");
        }
        return data;
    }

    public OnDataReceivedListener getOnDataReceived() {
        return onDataReceived;
    }

    /**
    * This sets how the data is handled after receiving it
    * from kik servers only Override onDataReceived
    * if you know what you are doing!
     */
    public void setOnDataReceived(OnDataReceivedListener onDataReceived) {
        this.onDataReceived = onDataReceived;
    }
}

