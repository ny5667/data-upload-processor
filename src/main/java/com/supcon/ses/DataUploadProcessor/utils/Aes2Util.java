package com.supcon.ses.DataUploadProcessor.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author: xulong2
 * @create: 2020-12-27 18:49
 * @description
 **/
public class Aes2Util {

    private static final Logger log = LoggerFactory.getLogger(Aes2Util.class);

    public static final String AESKEY = "rZxl3zy!rZxl3zy!";

    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String DEFAULT_CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS7Padding";

    private static Cipher cipher;

    public static final String SECRET = "QAZWSXEDCRFVTGBH";

    public static final String OFFSET = "1954682168745975";

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
        } else {
            Security.removeProvider("BC");
            Security.addProvider((Provider)new BouncyCastleProvider());
        }
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, new SecretKeySpec("rZxl3zy!rZxl3zy!".getBytes(), "AES"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String content, String key) {
        try {
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(1, new SecretKeySpec(key.getBytes(), "AES"));
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(result);
        } catch (Exception ex) {
            log.error("AES encrypt error ", ex);
            return null;
        }
    }

    public static String decrypt(String content, String key) {
        if (StringUtils.isEmpty(content))
            return null;
        try {
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "utf-8");
        } catch (Exception ex) {
            log.error("AES decrypt [{}]error ", content, ex);
            return null;
        }
    }

    private static SecretKeySpec getSecretKey(String key) {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom(key.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException ex) {
            log.error("AES getSecretKey error ", ex);
            return null;
        }
    }


    public static String encryptCBC(String content, String key, String ivParameter) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            byte[] byteContent = content.getBytes("utf-8");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(1, new SecretKeySpec(key.getBytes(), "AES"), ivParameterSpec);
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(result);
        } catch (Exception ex) {
            log.error("AES encrypt error ", ex);
            return null;
        }
    }

    public static String decryptCBC(String content, String key, String ivParameter) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(2, new SecretKeySpec(key.getBytes(), "AES"), ivParameterSpec);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "utf-8");
        } catch (Exception ex) {
            log.error("AES decrypt error ", ex);
            return null;
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String content = "hello";
        String key = "QAZWSXEDCRFVTGBH";
        String iv = "1954682168745975";
        log.info("\n{}", content);
        String cbcStr = encryptCBC(content, key, iv);
        log.info("\n{}", cbcStr);
        String decryptCBC = decryptCBC(cbcStr, key, iv);
        log.info("\n{}", decryptCBC);
    }
}
