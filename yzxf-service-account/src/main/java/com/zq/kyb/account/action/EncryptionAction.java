package com.zq.kyb.account.action;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import net.sf.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class EncryptionAction extends BaseActionImpl{
    //KeyGenerator 提供对称密钥生成器的功能，支持各种算法
//    private KeyGenerator keygen;
//    //SecretKey 负责保存对称密钥
//    private SecretKey deskey;
//    //Cipher负责完成加密或解密工作
//    private Cipher c;
//    //该字节数组负责保存加密的结果
//    private byte[] cipherByte;

    /**
     * 对字符串加密
     */
    public Map<String,Object> encrytor(String str) throws Exception{
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        //实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        //生成密钥
        SecretKey deskey = keygen.generateKey();
        //生成Cipher对象,指定其支持的DES算法
        Cipher c = Cipher.getInstance("AES");

        c.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] src = str.getBytes();
        // 加密，结果保存进cipherByte
        byte[] cipherByte = c.doFinal(src);

        Map<String,Object> re = new HashMap<>();
        re.put("val",HexBin.encode(cipherByte));
        re.put("key",HexBin.encode(deskey.getEncoded()));
        return re;
    }

    /**
     * 对字符串解密
     */
    public String decryptor(String val,String key) throws Exception {
        //生成密钥
        byte[] keybyte= HexBin.decode(key);
        SecretKey deskey = new SecretKeySpec(keybyte,"AES");
        Cipher c = Cipher.getInstance("AES");
        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
        c.init(Cipher.DECRYPT_MODE, deskey);
        byte[] cipherByte = c.doFinal(HexBin.decode(val));
        return new String(cipherByte);
    }

    public Map<String,Object> decryptorMap(String val,String key) throws Exception {
        String reStr = decryptor(val,key);
        reStr = reStr.substring(1,reStr.length()-1);
        String[] arr = reStr.split(",");
        Map<String,Object> re = new HashMap<>();
        String[] keyValue;
        for(String str : arr){
            keyValue = str.split("=");
            re.put(keyValue[0].replace(" ",""),keyValue[1]);
        }
        return re;
    }

    @POST
    @Path("/decryptorMap")
    public void decryptorMap() throws Exception{
        String key = ControllerContext.getPString("key");
        String val = ControllerContext.getPString("val");
        toResult(200, decryptorMap(val,key));
    }

    public static void main(String[] args) throws Exception {
        EncryptionAction e = new EncryptionAction();
        Map<String,Object> p = new HashMap<>();
        p.put("sellerId","S-000007");
        p.put("password","000000");
//        System.out.println(e.encrytor(p.toString()));
//        System.out.println(e.decryptor("1E9F4E9F60A037E391E6EEF1CBB073B979257B5FB634BE168E2B99678A04AE157CCAE5183DDE9F009AC82F10C18CA5D3","3D9E4073414E0CA314672FF6917F2F88"));
    }
}
