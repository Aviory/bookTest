package com.getbooks.android.encryption;

import com.getbooks.android.Const;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by marina on 07.08.17.
 */

public class Encryption {

    private String mKey;

    private char[] keyCharArray = new char[]{'n', '8', 'q', 'z', 'l', 'a', '1', '4', 'r', 'k', '.',
            'd', '%', 'p', '9', '3', '7', '@', 'd', 'g', '1', '$', '{', '4', 's', 'j', 't', '6',
            'v', '7', '}', 'o', 'p', 'y', '*', '%', '3', 'u', 'a', 'x', '^', 'c', '1', '2', '@',
            'v', 'r', '3', '4', 'q', 'w', 'i', '4', 'z', '9', '4', '@', 'd', '1'};


    void createKey(String str) {
        mKey = digestString(digestString(str) + Arrays.toString(keyCharArray));
    }

    private String digestString(String str) {
        try {
            byte[] bytes = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
            BigInteger bigInteger = new BigInteger(1, bytes);
            return String.format("%0" + (bytes.length << 1) + "x", bigInteger);
        } catch (Exception e) {
            return "";
        }
    }

    String getKey() {
        return mKey;
    }

    public static CipherOutputStream encryptStream(String encryptFilePath) throws IOException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(encryptFilePath);

        Encryption encryption = new Encryption();
        encryption.createKey(Const.USER_SESSION_ID);

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec(encryption.mKey.getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        return cos;
    }
}
