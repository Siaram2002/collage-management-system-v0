package com.cms.qr;




import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptDecryptUtil {

    // AES key size (128, 192, or 256 bits)
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Secret key (must be 16 bytes for AES-128)
    private static final String SECRET_KEY = "MySuperSecretKey";

    /**
     * Encrypts a plain text string
     */
    public static String encrypt(String input) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts an encrypted string
     */
    public static String decrypt(String encryptedInput) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedInput);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }


}

