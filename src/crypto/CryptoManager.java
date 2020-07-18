package a.b.c.d.crypto;

import a.b.c.d.util.ByteUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

public class CryptoManager {

    private byte[] key = new byte[16];
    private int rsaProtocol = 12;
    private int aesProtocol = 2;

    public CryptoManager() {
        new SecureRandom().nextBytes(key);
    }

    public RSAPublicKey getRSAKey() {
        try {
            String pem = "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEApElgRBx+g7sniYFW7LE8ivrwXShKTRFV8lXNItMXbN5QSC8vJ/cTSOTS619Xv5Zx7xXJIk4EKxtWesEGbgZpEUP2xQ+IeH9oz0JxayEMvvD1nVNAWgpWE4pociEoArsK7qY3YwXb1CiDHo9hojLv7djbo3cwXvlyMh4TUrX2RjCZPlVJxk/LVjzcl9ohJLkl3eoSrf0AE4kQ9mk3+raEhq5Dv+IDxKYX+fIytUWKmrQJusjtre9oVUX5sBOYZ0dzez/XapusEhUWImmB6mciVXfRXQ8IK4IH6vfNyxMSOTfLEhRYN2SMLzplAYFiMV536tLS3VmG5GJRdkpDubqPeQIBAw==";
            byte[] rsaKey = Base64.decodeBase64(pem);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(rsaKey));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptedRSA(byte[] msg) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getRSAKey());
            return cipher.doFinal(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getRSAEncryptedKey() {
        return encryptedRSA(this.key);
    }

    public byte[] encryptedAES(byte[] value) {
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            SecretKeySpec keySpec = new SecretKeySpec(this.key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(value);
            byte[] result = new byte[encrypted.length + 20];
            System.arraycopy(ByteUtil.intToByteArrayLE(encrypted.length + 16), 0, result, 0, 4);
            System.arraycopy(iv, 0, result, 4, 16);
            System.arraycopy(encrypted, 0, result, 20, encrypted.length);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptAES(byte[] value) {
        try {
            byte[] lenBytes = new byte[4];
            System.arraycopy(value, 0, lenBytes, 0, 4);
            int len = ByteUtil.byteArrayToIntLE(lenBytes);

            byte[] ivBytes = new byte[16];
            System.arraycopy(value, 4, ivBytes, 0, 16);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            byte[] body = new byte[value.length - 20];
            System.arraycopy(value, 20, body, 0, value.length - 20);

            SecretKeySpec keySpec = new SecretKeySpec(this.key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(body);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateHandshake() {
        byte[] keyBytes = this.getRSAEncryptedKey();
        byte[] result = new byte[12 + keyBytes.length];
        System.arraycopy(ByteUtil.intToByteArrayLE(keyBytes.length), 0, result, 0, 4);
        System.arraycopy(ByteUtil.intToByteArrayLE(this.rsaProtocol), 0, result, 4, 4);
        System.arraycopy(ByteUtil.intToByteArrayLE(this.aesProtocol), 0, result, 8, 4);
        System.arraycopy(keyBytes, 0, result, 12, keyBytes.length);

        return result;
    }

}