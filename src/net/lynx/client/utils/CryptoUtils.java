package net.lynx.client.utils;

import net.lynx.client.Constants;
import net.lynx.client.objects.User;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class CryptoUtils {

    public static String hashPassword(String username, String password) {
        try {
            String salt = username.toLowerCase() + "niCRwL7isZHny24qgLvy";
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(getSha1(password.getBytes()).toCharArray(),
                    salt.getBytes(StandardCharsets.UTF_8), 8192, 128);
            return getHexStringFromBytes(secretKeyFactory.generateSecret(pbeKeySpec).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(String.format("Failed to hash password error is %s", e.toString()));
        }
    }

    public static String genHmac(String str) {
        try {
            Key secretKeySpec = new SecretKeySpec(Constants.kikHash.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            Mac instance = Mac.getInstance("HmacSHA1");
            instance.init(secretKeySpec);
            return getHexStringFromBytes(instance.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "deadbeef101";
        }
    }

    public static ArrayList<Map.Entry<String, String>> ToKikHashMap(LinkedHashMap<String, String> source) {
        LinkedHashMap<String, String> dictionary = new LinkedHashMap<>(source);
        ArrayList<Map.Entry<String, String>> result = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>(source.keySet());
        Collections.sort(keys);
        for (int i = 0; i < source.size(); i++) {
            int hashCode = HashKikMap(dictionary, -1964139357, 7);
            hashCode = hashCode % dictionary.size();
            if (hashCode < 0) {
                hashCode += dictionary.size();
            }
            String selectedKey = keys.get(hashCode);
            keys.remove(hashCode);
            result.add(new AbstractMap.SimpleEntry<>(selectedKey, dictionary.get(selectedKey)));
            dictionary.remove(selectedKey);
        }
        return result;
    }

    public static int HashKikMap(LinkedHashMap<String, String> source, int hashCodeBase, int hashCodeOffset) {
        ArrayList<String> keys = new ArrayList<>(source.keySet());
        Collections.sort(keys);
        StringBuilder dictionaryForward = new StringBuilder();
        for (String key : keys) {
            dictionaryForward.append(key).append(source.get(key));
        }
        StringBuilder dictionaryBackward = new StringBuilder();
        Collections.reverse(keys);
        for (String key : keys) {
            dictionaryBackward.append(key).append(source.get(key));
        }
        byte[] bytesForward = dictionaryForward.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytesBackward = dictionaryBackward.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int hash0 = CompressKikMapHash(getAlgorithm(0, bytesForward));
        int hash1 = CompressKikMapHash(getAlgorithm(1, bytesForward));
        int hash5 = CompressKikMapHash(getAlgorithm(2, bytesBackward));
        return (((hashCodeBase ^ (hash0 << hashCodeOffset)) ^ (hash5 << (hashCodeOffset * 2))) ^ (hash1 << hashCodeOffset)) ^ hash0;
    }

    private static byte[] getAlgorithm(int i, byte[] b) {
        MessageDigest[] algos = new MessageDigest[3];
        try {
            algos[0] = MessageDigest.getInstance("SHA-256");
            algos[1] = MessageDigest.getInstance("SHA-1");
            algos[2] = MessageDigest.getInstance("MD5");
            algos[i].update(b);
        } catch (Exception e){

        }
        return algos[i].digest();
    }

    private static int CompressKikMapHash(byte[] digest) {
        long j = 0;
        for (int i = 0; i < digest.length; i += 4) {
            long b4 = ByteToSignedInt(digest[i + 3]) << 24;
            long b3 = ByteToSignedInt(digest[i + 2]) << 16;
            long b2 = ByteToSignedInt(digest[i + 1]) << 8;
            long b1 = ByteToSignedInt(digest[i]);
            long modifier = b4 | b3 | b2 | b1;
            j ^= modifier;
        }
        return (int) j;
    }

    private static int ByteToSignedInt(byte value) {
        return value;
    }

    private static String getSha1(byte[] bytes) throws NoSuchAlgorithmException {
        return getHexStringFromBytes(MessageDigest.getInstance("SHA-1").digest(bytes));
    }

    private static String getHexStringFromBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append(Constants.HEX_CHARS[b >> 4 & 0xF]).append(Constants.HEX_CHARS[b & 0xF]);
        }
        return sb.toString();
    }
}
