package net.lynx.client.utils;

import net.lynx.client.Constants;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
