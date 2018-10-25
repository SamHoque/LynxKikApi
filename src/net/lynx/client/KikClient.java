package net.lynx.client;

import net.lynx.client.exception.KikErrorException;
import net.lynx.client.objects.*;
import net.lynx.client.utils.CryptoUtils;
import net.lynx.client.utils.MapUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.UUID;

import static net.lynx.client.Constants.kikHost;
import static net.lynx.client.Constants.kikPort;

public class KikClient {
    private OnDataReceivedListener onDataReceived;
    private SSLSocket sslsocket;
    private BufferedWriter bufferedWriter;
    private BufferedInputStream bufferedInputStream;

    public KikClient() throws IOException {
        setOnDataReceived(KikDataHandler::handleData);
        setupNewConnection();
        connect_to_kik_server();
    }

    private void connect_to_kik_server() {
        Logger.PLUS.log("Trying to connect to kik servers");
        String initial_connection_payload = "<k anon=\"\">";
        try {
            write_to_kik_server(initial_connection_payload);
            String response = read_from_kik_server_once();
            Logger.PLUS.log("payload: " + response);
            if (!response.equals("<k ok=\"1\">")) {
                throw new KikErrorException("Could not connect to kik server: " + response);
            }
            Logger.NULL.log("Connection to kik servers successful");
        } catch (IOException | KikErrorException e) {
            Logger.NEGATIVE.log("Connection to kik servers failed");
            Logger.NEGATIVE.log("Error: %s", e.toString());
        }
    }

    public void login_to_kik_server(String username, String password) throws IOException, XmlPullParserException {
        Logger.PLUS.log("Trying to login with username '%s'", username);
        write_to_kik_server(XMPPHolder.login_xmpp(username, password));
        String ackRequest = read_from_kik_server_once();
        Logger.NULL.log("Received ack response from kik with id '%s'", getNode(ackRequest).getAttribute("id"));
        String loginResponse = read_from_kik_server_once();
        Node iq = getNode(loginResponse);
        String attribute = iq.getAttribute("type");
        if (attribute.equals("error")) {
            Node error = iq.getFirstChildByName("error");
            if (error.containsChild("not-registered")) {
                Logger.NEGATIVE.log("Could not login to kik because user is not registered");
            } else if (error.containsChild("password-mismatch")) {
                Logger.NEGATIVE.log("Could not login to kik because password is wrong");
            } else if (error.containsChild("device-change-timeout")) {
                Logger.NEGATIVE.log("Could not login to kik because device has changed");
            } else if (error.containsChild("captcha-url")) {
                Logger.NEGATIVE.log("Could not login to kik because of captcha");
            } else {
                Logger.NEGATIVE.log("Could not login to kik because of an unknown error");
            }
            return;
        }
        Node query = iq.getFirstChildByName("query");
        Node node = query.getFirstChildByName("node");
        Node email = query.getFirstChildByName("email");
        Node usernameNode = query.getFirstChildByName("username");
        Node first = query.getFirstChildByName("first");
        Node last = query.getFirstChildByName("last");
        Node xdata = query.getFirstChildByName("xdata");
        Node[] records = query.getChildrensByName("record");
        User user = new User();
        user.setNode(node);
        user.setEmail(email);
        user.setUsername(usernameNode);
        user.setFirst(first);
        user.setLast(last);
        Logger.PLUS.log("Login to kik successful, Trying to establish a session now");
        establishSession(user, password);
    }

    public void establishSession(User user, String password) throws IOException {
        resetConnection();
        String jid = user.getJid();
        String jidWithDeviceID = jid + "/CAN" + Constants.device_id;
        //String sid = KikUUIDGen.getKikUUID();

        //Hard coded SID
        String sid = "e7f76e44-42a6-4008-82ce-3320d5138842";
        String timestamp = "1496333389122";
        String signature = MapUtils.generateRsaSign(sid, timestamp, Constants.KIK_VERSION, jid);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("p", CryptoUtils.hashPassword(user.getUsername(), password));
        map.put("cv", CryptoUtils.genHmac(timestamp + ":" + user.getJid()));
        map.put("n", "1");
        map.put("v", Constants.KIK_VERSION);
        map.put("conn", "WIFI");
        map.put("to", "talk.kik.com");
        map.put("lang", "en_US");
        map.put("from", jidWithDeviceID);
        map.put("sid", sid);
        map.put("signed", signature);
        map.put("ts", timestamp);
        String connectionPaylaod = MapUtils.makeConnectionPayload(map);
        System.out.println(connectionPaylaod);

        // write_to_kik_server(connectionPaylaod);

        /**
         * Hardcoded packet output from python lib
         * works fine on python lib every time!
         */

        write_to_kik_server("<k n=\"1\" ts=\"1496333389122\" conn=\"WIFI\" signed=\"PmH7FymKsQdUUHttmKDOwdr77a7oaiNQ0LbnHJM9WMO0f4scXsVfC3V-zlMvp5bP4PsV0lg8lQCg408sf-kM3g\" to=\"talk.kik.com\" from=\"testusername97_4p4@talk.kik.com/CAN62030843678b7376a707ca3d11e87837\" sid=\"e7f76e44-42a6-4008-82ce-3320d5138842\" v=\"14.5.0.13136\" cv=\"f885e7de4937a9c530227d9a88515de1f37cce78\" lang=\"en_US\" p=\"b5d76ee10188c50b6dac0740108acb77\">");
        String k = read_from_kik_server_once();
        System.out.println(k);
    }

    public void start() {
        new Thread(() -> {
            byte[] buffer = new byte[32768];
            try {
                String data;
                for (int b; ((b = bufferedInputStream.read(buffer)) > 0);) {
                    data = new String(buffer, 0, b, StandardCharsets.UTF_8);
                    onDataReceived.onDataReceived(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void sendMessage(String body, String jid, boolean isGroup) throws IOException {
        Node message = new Node("message")
                .addAttribute("type", isGroup ? "groupchat" : "chat")
                .addAttribute("to", jid)
                .addAttribute("id", UUID.randomUUID().toString())
                .addAttribute("cts", String.valueOf(System.currentTimeMillis()))
                .addTextNode("body", body)
                .addTextNode("preview", body);

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

    private void setupNewConnection() throws IOException {
        sslsocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(kikHost, kikPort);
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
        bufferedInputStream = new BufferedInputStream(sslsocket.getInputStream());
        sslsocket.startHandshake();
        sslsocket.setKeepAlive(true);
    }

    private void resetConnection() throws IOException {
        write_to_kik_server("</k>");
        bufferedWriter.close();
        sslsocket.close();
        bufferedInputStream.close();
        setupNewConnection();
    }

    private void write_to_kik_server(Node data) throws IOException {
        write_to_kik_server(data.toString());
    }

    private void write_to_kik_server(String data) throws IOException {
        bufferedWriter.write(data);
        bufferedWriter.flush();
    }

    private String read_from_kik_server_once() throws IOException {
        byte[] buffer = new byte[32768];
        return new String(buffer, 0, bufferedInputStream.read(buffer), StandardCharsets.UTF_8);
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

    public Node getNode(String string) throws XmlPullParserException, IOException {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new StringReader(string));
        return new Node(null, parser);
    }
}

