package com.willer.common;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Hack on 2016/11/30.
 */
public class DESHelper {
    private static final Logger RUN_LOG = Logger.getLogger(DESHelper.class);
    private static final String KEY_ALGORITHM = "DES";
    private static final String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

    private static SecretKey generateSecurityKey(String keyStr) {
        try {
            byte input[] = Hex.decodeHex(Hex.encodeHex(keyStr.getBytes()));
            return SecretKeyFactory.getInstance(KEY_ALGORITHM).generateSecret(new DESKeySpec(input));
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static String encrypt(String data, String key) {
        try {
            if (!StringUtils.isEmpty(data) && !StringUtils.isEmpty(key)) {
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, generateSecurityKey(key));
                byte[] results = cipher.doFinal(data.getBytes());
                return Base64.encodeBase64String(results);
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static String decrypt(String data, String key) throws Exception {
        try {
            if (!StringUtils.isEmpty(data) && !StringUtils.isEmpty(key)) {
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, generateSecurityKey(key));
                return new String(cipher.doFinal(Base64.decodeBase64(data)));
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        String source = "amigoxie";
        System.out.println("原文: " + source);
        String key = "GOD#LOVES@DEVIL";
        String encryptData = encrypt(source, key);
        System.out.println("加密后: " + encryptData);
        String decryptData = decrypt(encryptData, key);
        System.out.println("解密后: " + decryptData);
    }
}
