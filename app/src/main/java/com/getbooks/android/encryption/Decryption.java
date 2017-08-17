package com.getbooks.android.encryption;

import com.getbooks.android.Const;
import com.getbooks.android.util.SystemUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by marina on 10.08.17.
 */

public class Decryption {

    public static CipherInputStream decryptStream(String decryptFilePath, String bookName) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileInputStream fos = new FileInputStream(decryptFilePath +"/"+ bookName + ".epub" );

        Encryption encryption = new Encryption();
        encryption.createKey(Const.USER_SESSION_ID, SystemUtil.getSetting());

        // Length is 16 byte
        // Careful when taking user input!!! https://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec(encryption.getKey().getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        // Wrap the output stream
        CipherInputStream cos = new CipherInputStream(fos, cipher);
        return cos;
    }
}
