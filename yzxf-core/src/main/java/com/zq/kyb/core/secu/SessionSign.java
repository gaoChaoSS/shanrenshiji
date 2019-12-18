package com.zq.kyb.core.secu;


import com.zq.kyb.util.Base64;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SessionSign {
    private static SessionSign sessionSign;
    public final static int KEY_SIZE = 1024;
    public static String SESSION_KEY = "./sessionKey";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private MessageDigest md;

    private SessionSign() throws NoSuchAlgorithmException, InvalidKeySpecException {
        md = MessageDigest.getInstance("MD5");
    }

    public static SessionSign getInstant() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (sessionSign == null) {
            sessionSign = new SessionSign();
            try {
                File prvfile = new File(SESSION_KEY + "/privateKey");
                File pubfile = new File(SESSION_KEY + "/publicKey");
                Logger.getLogger(SessionSign.class).info("--privateKey path:" + prvfile.getCanonicalPath());
                Logger.getLogger(SessionSign.class).info("--publicKey path:" + pubfile.getCanonicalPath());
                //不能生成,必须先手动生成!
//                if (!prvfile.exists() || !pubfile.exists()) {
//                    // 生成密钥对
//                    sessionSign.genKeyPair();
//                    // 保存私钥文件，这个文件需要保密，
//                    sessionSign.savePrivateKey(prvfile);
//                    // 保存公钥文件，这个文件分发给 Application.
//                    sessionSign.savePublicKey(pubfile);
//                } else {
                if (prvfile.exists()) {
                    sessionSign.loadPrivateKey(prvfile);
                }
                if (pubfile.exists()) {
                    sessionSign.loadPublicKey(pubfile);
                }
                // }

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sessionSign;
    }


    public void loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded;
        InputStream publicKeyIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("publicKey");
        if (publicKeyIn == null)
            throw new RuntimeException("公钥不存在！");
        encoded = loadKey(publicKeyIn);
        KeyFactory keyFactory1 = KeyFactory.getInstance("RSA-SHA1");
        X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(encoded);
        publicKey = keyFactory1.generatePublic(keySpec1);
    }

    public void loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream privateKeyIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("privateKey");
        if (privateKeyIn == null)
            throw new RuntimeException("私钥不存在！");
        byte[] encoded = loadKey(privateKeyIn);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA-SHA1");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        privateKey = keyFactory.generatePrivate(keySpec);
    }


    /**
     * 生成私钥和公钥
     *
     * @throws NoSuchAlgorithmException
     */
    public void genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(KEY_SIZE);
        KeyPair kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void savePublicKey(File filename) throws IOException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(filename);
        try {
            fos.write(x509EncodedKeySpec.getEncoded());
        } finally {
            fos.close();
        }

    }

    public void savePrivateKey(File filename) throws IOException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        Logger.getLogger(SessionSign.class).info("session key path:" + filename.getCanonicalPath());
        FileOutputStream fos = new FileOutputStream(filename);
        try {
            fos.write(pkcs8EncodedKeySpec.getEncoded());
        } finally {
            fos.close();
        }

    }

    private byte[] loadKey(File file) throws IOException {
        DataInputStream is = new DataInputStream(new FileInputStream(file));
        try {
            byte[] result = new byte[(int) file.length()];
            is.readFully(result);
            return result;
        } finally {
            is.close();
        }
    }

    public void loadPublicKey(File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = loadKey(file);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        publicKey = keyFactory.generatePublic(keySpec);
    }

    public void loadPrivateKey(File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = loadKey(file);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        privateKey = keyFactory.generatePrivate(keySpec);
    }

    private byte[] md5(String data) throws UnsupportedEncodingException {
        byte[] input = data.getBytes("UTF-8");
        return md.digest(input);
    }

    public String sign(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] sign = cipher.doFinal(md5(data));
        String str = Base64.encodeBytes(sign);
        str = URLEncoder.encode(str, "utf-8");
        return str;

    }

    public synchronized boolean check(String data, String sign) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        if (publicKey == null) {
            throw new RuntimeException("publicKey is not exist [" + new File(SESSION_KEY).getCanonicalPath() + "]!");
        }
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] signbin = Base64.decode(URLDecoder.decode(sign, "utf-8"));
        byte[] decryptMd5 = cipher.doFinal(signbin);
        return Arrays.equals(md5(data), decryptMd5);
    }

    private static byte[] loadKey(InputStream resourceAsStream) {
        ByteArrayOutputStream is = new ByteArrayOutputStream();
        byte[] read = new byte[1024];
        try {
            int k = -1;
            while ((k = resourceAsStream.read(read)) != -1) {
                is.write(read, 0, k);
            }
            return is.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                resourceAsStream.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 密钥对只需要生成一次，除非发生私钥泄密等事件，一般不用再生成。
        SessionSign signer = new SessionSign();
        // 生成密钥对
        signer.genKeyPair();
        // 保存私钥文件，这个文件需要保密，可以只保存在Shawei ID上，
        File dir = new File("./sessionKey");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        signer.savePrivateKey(new File("./sessionKey/privateKey"));
        // 保存公钥文件，这个文件分发给 Application.
        signer.savePublicKey(new File("./sessionKey/publicKey"));
    }

    public static void main1(String[] args) throws Exception {
        /*
         * 使用说明
         */

        /***********************************************************************
         * 公共变量
         ************************************************************************/

        // 每次都要用到signer，定义为公共变量
        SessionSign signer = null;

        // 保存用户基本信息的Cookie
        // String id_4_sw_user =
        // "2940230_%7C_theseus.liu%40gmail.com_%7C_%E8%96%9B%E5%AE%9A%E8%B0%94%E7%9A%84%E5%85%AC%E7%8C%AB_%7C_http%3A%2F%2Fsto.shawei.com%2Fuid%2Fid%2Fnbaid%2Fportrait%2F30A%2F4A0%2Ftheseus.liuPOTIIRgmail.com_48X48.jpg";
        String id_4_sw_user = "84915865-3947-4a02-8396-b227e29c8c1a,9bcb28aa-21f7-48a9-88b7-ee85db883e78,51c7c59f-f134-6186-5edb-2e22d49d1b1c,-1,Assistant";
        Logger.getLogger(SessionSign.class).info(URLDecoder.decode(id_4_sw_user, "utf-8"));

        // 保存对用户信息的签名，用来判断用户信息是否来自ShaweiID。
        String id_4_sw_user_sign = null;

        /***********************************************************************
         * 生成密钥
         ************************************************************************/

        // 密钥对只需要生成一次，除非发生私钥泄密等事件，一般不用再生成。
        signer = new SessionSign();
        // 生成密钥对
        signer.genKeyPair();
        // 保存私钥文件，这个文件需要保密，可以只保存在Shawei ID上，
        signer.savePrivateKey(new File("./privateKey"));
        // 保存公钥文件，这个文件分发给 Application.
        signer.savePublicKey(new File("./publicKey"));

        /***********************************************************************
         * Shawei ID 上要做的事情
         ************************************************************************/

        // Shawei ID启动后初始化一个 Signer 的实例，然后加载私钥文件.
        // 用户登录成功后生成 id_4_sw_user Cookie，然后调用 sign 方法对Cookie进行签名，签名的结果保存在
        // id_4_sw_user_sign Cookie里。
        // signer = new SessionSign();

        // 加载私钥
        // signer.loadPrivateKey(new File("./privateKey"));

        // 签名
        id_4_sw_user_sign = SessionSign.getInstant().sign(id_4_sw_user);

        Logger.getLogger(SessionSign.class).info("sign: " + id_4_sw_user_sign);

        id_4_sw_user_sign = SessionSign.getInstant().sign(id_4_sw_user);

        Logger.getLogger(SessionSign.class).info("sign: " + id_4_sw_user_sign);

        id_4_sw_user_sign = SessionSign.getInstant().sign(id_4_sw_user);

        Logger.getLogger(SessionSign.class).info("sign: " + id_4_sw_user_sign);

        /***********************************************************************
         * Application上要做的事情
         ************************************************************************/

        // Application 启动后初始化一个 Signer 实例, 并加载公钥。
        // 每次收到两个Cookie后调用 check 方法来判断 Cookie 是否来自 Shawei ID，返回 true
        // 表示 Cookie 是来自 Shawei ID. 如果返回 false 或者发生异常说明不是来自 Shawei ID.

        // 加载公钥
        // signer.loadPublicKey(new File("./publicKey"));

        // 检查签名

        boolean result = SessionSign.getInstant().check(id_4_sw_user, id_4_sw_user_sign);

        Logger.getLogger(SessionSign.class).info("result: " + result);

    }
}
