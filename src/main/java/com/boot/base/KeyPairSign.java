package com.boot.base;

import cn.hutool.json.JSONUtil;
import com.boot.base.mail.send.dto.Email;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @author mengdexuan on 2023-06-04 09:59.
 */
public class KeyPairSign {


    public static KeyPair keyPair() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        keyPair.getPrivate();

        return keyPair;
    }


    public static String privateKey2Base64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static String publicKey2Base64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static PublicKey publicKeyFromBase64(String publicKeyBase64) {

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = null;
        PublicKey publicKey = null;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return publicKey;
    }

    public static PrivateKey privateKeyFromBase64(String privateKeyBase64) {

        byte[] bytes = Base64.getDecoder().decode(privateKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = null;
        PrivateKey privateKey = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return privateKey;
    }


    public static byte[] str2byteArr(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static String byteArr2Str(byte[] arr) {
        return Base64.getEncoder().encodeToString(arr);
    }


    /**
     * 使用私钥对数据签名，返回签名结果
     * @param privateKey 私钥
     * @param data 数据
     * @return
     */
    public static byte[] sign(String privateKey,byte[] data) {

        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKeyFromBase64(privateKey));
            signature.update(data);
            byte[] signatureBytes = signature.sign();
            return signatureBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

       return null;
    }


    /**
     * 使用公钥对签名进行验证
     * @param publicKey 公钥
     * @param data  数据
     * @param signBytes  签名
     * @return
     */
    public static boolean verify(String publicKey,byte[] data,byte[] signBytes) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKeyFromBase64(publicKey));
            signature.update(data);
            boolean isValidSignature = signature.verify(signBytes);
            return isValidSignature;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


//    这个密钥可以替换为任何其他的 16 字节长度的字节数组
    private static String secretKeyStr = "1234567890123456";


    /**
     * 使用密钥，对明文进行加密
     * @param plaintext
     * @return
     */
    public static String encode(String plaintext) {
        String encoded = "";
        try{
            // Generate a secret key
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyStr.getBytes(StandardCharsets.UTF_8), "AES");

//        我们使用 ECB 模式和 PKCS5Padding 填充方式来加密和解密数据
            // Create a cipher object and initialize it with the secret key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt the plaintext
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Convert the ciphertext to a base64-encoded string for transmission or storage
            encoded = Base64.getEncoder().encodeToString(ciphertext);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("加密后的数据: " + encoded+" , 长度："+encoded.length());

        return encoded;
    }


    /**
     * 使用密钥，对密文进行解密
     * @param encodeStr
     * @return
     */

    public static String decode(String encodeStr) {
        String decryptedText = "";
        try{
            // Generate a secret key
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyStr.getBytes(StandardCharsets.UTF_8), "AES");

            // Decode the ciphertext from the base64-encoded string
            byte[] decoded = Base64.getDecoder().decode(encodeStr);

            // Create a new cipher object and initialize it with the secret key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decrypt the ciphertext
            byte[] decrypted = cipher.doFinal(decoded);

            // Convert the decrypted plaintext to a string
            decryptedText = new String(decrypted, StandardCharsets.UTF_8);

        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("解密后的数据: " + decryptedText);

        return decryptedText;

    }


    public static void main(String[] args) {

        List<Email> emailList = new ArrayList<>();
        Email email = new Email();
        for (int i=0;i<10;i++){
            Email item = new Email();
            item.setFrom("from"+i);
            item.setContent("content"+i);
            item.setPersonal("personal"+i);
            Map<String,Object> map = new HashMap<>();
            map.put("a"+i,i);
            item.setKvMap(map);

            if (i==0){
                email = item;
            }

            emailList.add(item);
        }

        String str1 = encode(JSONUtil.toJsonStr(email));
        String str2 = encode(JSONUtil.toJsonStr(emailList));

        System.out.println(str1);
        System.out.println(str2);

        String str3 = decode(str1);
        String str4 = decode(str2);

        Email temp = JSONUtil.toBean(str3,Email.class);
        List<Email> tempList = JSONUtil.toList(str4,Email.class);

        System.out.println();
    }





}
