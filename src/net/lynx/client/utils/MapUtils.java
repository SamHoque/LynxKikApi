package net.lynx.client.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MapUtils {
    public static String makeConnectionPayload(LinkedHashMap<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<k");
        ArrayList<Map.Entry<String, String>> sorted = CryptoUtils.ToKikHashMap(map);
        for (Map.Entry<String, String> attr : sorted) {
            stringBuilder.append(String.format(" %1$s=\"%2$s\"", attr.getKey(), attr.getValue()));
        }
        stringBuilder.append(">");
        return stringBuilder.toString();
    }

    public static String generateRsaSign(String uuid, String timestamp, String version, String jid) {
        try {
            RSAPrivateKey rSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.decode("MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEA0RZQQg2pXUo0btiJ\n70ZIzy3vlm91N6pPuQ4XjSS8Mcin8Le1fZtw2AtOcYWzzIDabanuEqgUujGHri9n\nHl9nKQIDAQABAkBP+ELWILeIcNtBEh0foTgz1ZPva83fbopzcwpa95PrTexQBYWV\noRrlPzQYGI/+pe309oOglZx0oevtGoOr7yehAiEA+HmFpNIa7QwWzRiItEuqKslZ\ndrhA+bhbmfPlUYpdoq0CIQDXa2lSWTLEkG64oLKQhBuJRccTDMVhswcrkT+4aQWh\n7QIhALq5iAc+pWFybkgeoczr96tDuOmQubNwKdZeBPzsAEXZAiEAjOt/IpenVl8F\nj1HQfiltugcji5q3JIpxDlceUAyj2qECIQDjfO4gySclIbBrbMu3/cWZWe4IicPo\n66fl1txieqtomg==")));
            Signature instance = Signature.getInstance("SHA256withRSA");
            byte[] bytes = (jid + ':' + version + ':' + timestamp + ':' + uuid).getBytes(StandardCharsets.UTF_8);
            instance.initSign(rSAPrivateKey);
            instance.update(bytes);
            return Base64.encodeBytes(instance.sign());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}