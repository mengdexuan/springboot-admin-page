package com.boot.base;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.boot.base.mail.send.dto.Email;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 1.非对称密钥签名验签
 * 2.对称密钥加密解密
 * @author mengdexuan on 2023-06-04 09:59.
 */
@Slf4j
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
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
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
     *
     * @param privateKey 私钥
     * @param data       数据
     * @return
     */
    public static byte[] sign(String privateKey, byte[] data) {

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
     *
     * @param publicKey 公钥
     * @param data      数据
     * @param signBytes 签名
     * @return
     */
    public static boolean verify(String publicKey, byte[] data, byte[] signBytes) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKeyFromBase64(publicKey));
            signature.update(data);
            boolean isValidSignature = signature.verify(signBytes);
            return isValidSignature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //    这个密钥可以替换为任何其他的 16 字节长度的字节数组
    private static String secretKeyStr = "1234567890123456";


    /**
     * 使用密钥，对明文进行加密
     *
     * @param plaintext
     * @return
     */
    public static String encode(String plaintext) {
        String encoded = "";
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("加密后的数据: " + encoded + " , 长度：" + encoded.length());

        return encoded;
    }


    /**
     * 使用密钥，对密文进行解密
     *
     * @param encodeStr
     * @return
     */

    public static String decode(String encodeStr) {
        String decryptedText = "";
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("解密后的数据: " + decryptedText);

        return decryptedText;

    }


    /**
     * 使用密钥，对明文进行加密
     * @param secretKeyStr
     * @param plaintext
     * @return
     */
    public static String encode(String secretKeyStr,String plaintext) {
        String encoded = "";
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("加密后的数据: " + encoded + " , 长度：" + encoded.length());

        return encoded;
    }


    /**
     * 使用密钥，对密文进行解密
     *
     * @param secretKeyStr
     * @param encodeStr
     * @return
     */
    public static String decode(String secretKeyStr,String encodeStr) {
        String decryptedText = "";
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("解密后的数据: " + decryptedText);

        return decryptedText;

    }



    public static void main2(String[] args) {
        KeyPair keyPair = keyPair();

        String privateKey = privateKey2Base64(keyPair.getPrivate());
        String publicKey = publicKey2Base64(keyPair.getPublic());

        PrivateKey key1 = privateKeyFromBase64(privateKey);
        PublicKey key2 = publicKeyFromBase64(publicKey);

        System.out.println();
    }


    public static void main(String[] args) {

        String url = "http://test.zjsjwyt2axfs.ticket.iciyun.net/gateway/xfs/test";

        Map<String,Object> param = new HashMap<>();
        param.put("orderId","1234567");
        param.put("userId","4634");
        param.put("orderAmount","12345");
        param.put("orderTime","2023-07-11 15:11:59");
        param.put("shopOrgNum","91110000773396449E");

        String result = HttpUtil.post(url,JSONUtil.toJsonStr(param));

        System.out.println(result);
    }


    /**
     * 接口调用示例
     * @param url 接口请求地址
     * @param appKey 客户端 key
     * @param appSecret 客户端 secret
     * @param headers 请求头参数，固定为 2 个参数，如下：
     *                {
     *                  "time":1687943282163, #当前请求时间毫秒值
     *                  "route":"/addUser"  #接口路径
     *                }
     * @param param 接口参数, 例如：
     *              {
     *                  "id":1,
     *                  "name":"小明"
     *              }
     *
     */
    public static String invokeTemplate(String url,String appKey,String appSecret,Map<String, String> headers,Map<String,Object> param) {

        //原始参数
        String paramData = JSONUtil.toJsonStr(param);

        //使用 appKey 加密后的参数
        String paramDataEncode = KeyPairSign.encode(appKey,paramData);

        //将加密后的参数转为 byte 形式
        byte[] data = KeyPairSign.str2byteArr(paramDataEncode);

        //使用 appSecret 进行签名
        byte[] signData = KeyPairSign.sign(appSecret, data);

        Map<String,Object> requestMap = new HashMap<>();

        //将参数及签名转化为 str 进行传输
        requestMap.put("data",KeyPairSign.byteArr2Str(data));
        requestMap.put("signData",KeyPairSign.byteArr2Str(signData));

        //请请求参数转化为 json ，构造 post 请求体
        String body = JSONUtil.toJsonStr(requestMap);
        HttpRequest post = HttpUtil.createPost(url);
        post.headerMap(headers,true);
        post.body(body);

        //请求并获取结果
        String result = post.execute().body();

        return result;
    }




}
