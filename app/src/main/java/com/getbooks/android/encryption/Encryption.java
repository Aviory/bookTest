package com.getbooks.android.encryption;

import com.getbooks.android.Const;
import com.getbooks.android.util.SystemUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by marina on 07.08.17.
 */

public class Encryption {

    private String key;

    private static String buildStingKey() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(new char[]{'n', '8', 'q', 'z', 'l', 'a', '1', '4', 'r', 'k', '.', 'd', '%', 'p', '9', '3', '7', '@', 'd', 'g', '1', '$', '{', '4', 's', 'j', 't', '6', 'v', '7', '}', 'o', 'p', 'y', '*', '%', '3', 'u', 'a', 'x', '^', 'c', '1', '2', '@', 'v', 'r', '3', '4', 'q', 'w', 'i', '4', 'z', '9', '4', '@', 'd', '1'});
        return stringBuilder.toString();
    }

    private String digestString(String str) {
        try {
            return Encryption.getStringFromBytesUtf(MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8")));
        } catch (Exception e) {
            return "";
        }
    }

    public String getKey() {
        return key;
    }

    private static String getStringFromBytesUtf(byte[] bArr) {
        BigInteger bigInteger = new BigInteger(1, bArr);
        return String.format("%0" + (bArr.length << 1) + "x", bigInteger);
    }

    public void createKey(String str, String str2) {
        key = digestString(digestString(str) + str2 + Encryption.buildStingKey());
    }

    public static CipherOutputStream encryptStream(String encryptFilePath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(encryptFilePath);

        Encryption encryption = new Encryption();
        encryption.createKey(Const.USER_SESSION_ID, SystemUtil.getSetting());

        // Length is 16 byte
        // Careful when taking user input!!! https://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec(encryption.key.getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        return cos;
    }
}
