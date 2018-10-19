package net.lynx.client;

import net.lynx.client.exception.KikErrorException;
import net.lynx.client.objects.KikUUIDGen;
import net.lynx.client.objects.Node;
import net.lynx.client.objects.User;
import net.lynx.client.objects.XMPPHolder;
import net.lynx.client.utils.CryptoUtils;
import net.lynx.client.utils.KikTimestampUtils;
import net.lynx.client.utils.MapUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static net.lynx.client.Constants.kikHost;
import static net.lynx.client.Constants.kikPort;

public class KikClient {
    private OnDataReceivedListener onDataReceived;
    private SSLSocketFactory sslsocketfactory;
    private SSLSocket sslsocket;
    private BufferedWriter writer;
    private InputStream is;
    private BufferedInputStream bufferedInputStream;

    public KikClient() throws IOException {
        setOnDataReceived(KikDataHandler::handleData);
        setupNewConnection();
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
        } catch (IOException e) {
            Log("Failed connecting to kik server...");
            e.printStackTrace();
        } catch (KikErrorException e) {
            e.printStackTrace();
        }
    }

    public void login_to_kik_server(String username, String password) throws IOException {
        write_to_kik_server(XMPPHolder.login_xmpp(username, password));
        String ackRequest = read_from_kik_server_once();
        String loginResponse = read_from_kik_server_once();
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(loginResponse));
            Node iq = new Node(null, parser);
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
            establishSession(user, password);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void establishSession(User user, String password) throws IOException {
        resetConnection();
        String jid = user.getJid();
        String jidWithDeviceID = jid + "/CAN" + Constants.device_id;
        String sid = KikUUIDGen.getKikUUID();
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
        write_to_kik_server(connectionPaylaod);
        String k = read_from_kik_server_once();
        System.out.println(k);
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
        query.addTextNode("device-id", Constants.device_id);
        query.addTextNode("username", username);
        query.addTextNode("first", "Made");
        query.addTextNode("last", "By Rab");
        query.addTextNode("birthday", "1984-7-25");
        query.addTextNode("version", Constants.KIK_VERSION);
        query.addTextNode("install-date", String.valueOf(System.currentTimeMillis() - 69000));
        query.addTextNode("device-type", "android");
        query.addTextNode("brand", "HTC");
        query.addTextNode("logins-since-install", "0");
        query.addTextNode("lang", "en_US");
        query.addTextNode("android-sdk", "17");
        query.addTextNode("registrations-since-install", "0");
        query.addTextNode("prefix", "CAN");
        query.addTextNode("android-id", Constants.android_id);
        query.addTextNode("model", "EndeavorU");
        iq.addChild(query);
        write_to_kik_server(iq.toString());
    }

    public void start() {
        new Thread(() -> {
            byte[] buffer = new byte[32768];
            try {
                for (int b; ((b = bufferedInputStream.read(buffer)) > 0); ) {
                    onDataReceived.onDataReceived(new String(buffer, 0, b, StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    private void setupNewConnection() throws IOException {
        sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        sslsocket = (SSLSocket) sslsocketfactory.createSocket(kikHost, kikPort);
        writer = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
        is = sslsocket.getInputStream();
        bufferedInputStream = new BufferedInputStream(is);
        sslsocket.startHandshake();
    }

    private void resetConnection() throws IOException {
        write_to_kik_server("</k>");
        writer.close();
        setupNewConnection();
    }

    private void write_to_kik_server(Node data) throws IOException {
        write_to_kik_server(data.toString());
    }

    private void write_to_kik_server(String data) throws IOException {
        writer.write(data);
        writer.flush();
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
}

