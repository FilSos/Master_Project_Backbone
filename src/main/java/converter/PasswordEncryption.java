package converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncryption {

    //losowo wygenerowane hasło
    private static final String sysPassword = "a02tBEdVSR";
    // The salt (probably) can be stored along with the encrypted data
    private static final byte[] salt = ("8d667324ee9e54d8").getBytes();
    // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
    private final static int iterationCount = 40000;
    // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
    private final static int keyLength = 128;
    private static Logger logger = LogManager.getLogger(PasswordEncryption.class);

    public static String encryptPassword(String password) throws Exception {
        SecretKeySpec key = createSecretKey(sysPassword.toCharArray(), salt, iterationCount, keyLength);
        String encryptedPassword = encrypt(password, key);
        logger.info("Encrypted password: " + encryptedPassword);
        return encryptedPassword;

    }

    public static String decryptPassword(String encryptedPassword) throws Exception {
        SecretKeySpec key = createSecretKey(sysPassword.toCharArray(), salt, iterationCount, keyLength);
        String decryptedPassword = decrypt(encryptedPassword, key);
        logger.info("Decrypted password: " + decryptedPassword);
        return decryptedPassword;
    }

    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }

}
