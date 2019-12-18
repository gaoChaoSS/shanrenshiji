package com.zq.kyb.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 对字符进行单向加密,生成密文,有SHA-1和md5等
 */
public class MessageDigestUtils {

    public char[] cryptPassword(char pwd[], String algorithm) throws Exception {

        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        byte pwdb[] = new byte[pwd.length];
        for (int b = 0; b < pwd.length; b++) {
            pwdb[b] = (byte) pwd[b];
        }
        char crypt[] = hexDump(md.digest(pwdb));
        smudge(pwdb);
        return crypt;
    }

    private static char[] hexDump(byte src[]) {
        char buf[] = new char[src.length * 2];
        for (int b = 0; b < src.length; b++) {
            String byt = Integer.toHexString(src[b] & 0xFF);
            if (byt.length() < 2) {
                buf[(b * 2)] = '0';
                buf[b * 2 + 1] = byt.charAt(0);
            } else {
                buf[(b * 2)] = byt.charAt(0);
                buf[b * 2 + 1] = byt.charAt(1);
            }
        }
        return buf;
    }

    public static void smudge(byte pwd[]) {
        if (null != pwd) {
            for (int b = 0; b < pwd.length; b++) {
                pwd[b] = 0;
            }
        }
    }

    /**
     * @param str 明文
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String digest(String str) throws Exception {
        return digest(str, null);
    }

    public static String digest(String str, String digest) throws Exception {
        char[] s = new MessageDigestUtils().cryptPassword(str.toCharArray(), digest == null ? "SHA-1" : digest);
        return String.valueOf(s);
    }

    public static String getRandomStr(int strLength) throws Exception {
        Random random = new Random();
        Integer randNumber = random.nextInt();
        String str = MessageDigestUtils.digest(String.valueOf(randNumber));
        return str.substring(0, strLength > str.length() ? str.length() : strLength);
    }

    // SSHA
    public static boolean verifySHA(String ldappw, String inputpw) throws NoSuchAlgorithmException {
        byte[] userInputBytes = inputpw.getBytes();
        // MessageDigest 提供了消息摘要算法，如 MD5 或 SHA，的功能，这里LDAP使用的是SHA-1
        // 取出加密字符
        if (ldappw.startsWith("{SSHA}")) {
            ldappw = ldappw.substring(6);
        } else if (ldappw.startsWith("{SHA}")) {
            ldappw = ldappw.substring(5);
        }
        // 解码BASE64
        byte[] ldappwBytes = Base64.decode(ldappw);
        return verifySHA(ldappwBytes, userInputBytes);
    }

    public static boolean verifySHA(byte[] ldappwbyte, byte[] userInputBytes) throws NoSuchAlgorithmException {
        byte[] shacode;
        byte[] salt;
        // 前20位是SHA-1加密段，20位后是最初加密时的随机明文
        if (ldappwbyte.length <= 20) {
            shacode = ldappwbyte;
            salt = new byte[0];
        } else {
            shacode = new byte[20];
            salt = new byte[ldappwbyte.length - 20];
            System.arraycopy(ldappwbyte, 0, shacode, 0, 20);
            System.arraycopy(ldappwbyte, 20, salt, 0, salt.length);
        }
        MessageDigest md = MessageDigest.getInstance("SHA");
        // 把用户输入的密码添加到摘要计算信息
        md.update(userInputBytes);
        // 把随机明文添加到摘要计算信息
        md.update(salt);
        // 按SSHA把当前用户密码进行计算
        byte[] inputpwbyte = md.digest();
        // 返回校验结果
        return MessageDigest.isEqual(shacode, inputpwbyte);
    }


    public static byte[] digestFile(String pathname) throws IOException, NoSuchAlgorithmException {
        File file = new File(pathname);
        FileInputStream in = new FileInputStream(file);
        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        MessageDigest md5 = MessageDigest.getInstance("SHA-1");
        md5.update(byteBuffer);
        byte[] digest = md5.digest();
        BigInteger bi = new BigInteger(1, digest);
        String value = bi.toString(16);
        return digest;
    }

    public static void main(String[] arg) throws Exception {
        // 1、candy 2205836 a299b429e6b8c79bd7da521636373b28
        // 2、mayuejuan 750808 fd6876d04a40e39d6287b732c7ba784b
        // 3、lijian7377 730707 682a28ccb1c073e05d3e9cba2a468023
        // 4、may 28533648 f5b42fd14f063c313c3ee0f9b5b10509
        // 5、pang 28533790 1e386107d14cb45deaa415f43d451d3f
        // 6、oris 28533191 27e9f74a100d0e38a767f848f872e0cb
        // 7、simon 33399168 07cac3095cc5ab794161cdfa1b7ccab4
        // 96e79218965eb72c92a549dd5a330112

        // ab421754f5dff088c58beaf566824f7c
        String password = "joey";
        // char[] s = password.toCharArray();
        // s = digest(password, "md5");
        // System.out.println(digest(password, "md5"));

        System.out.println(MessageDigestUtils.digest(password));
        // System.out.println(getRandomStr(16));

        //对文件进行操作
//        long s = System.currentTimeMillis();
//        String pathname = "/Users/hujoey/Downloads/jdk-8u65-linux-x64.rpm";
//        byte[] digest = digestFile(pathname);
//        System.out.println(ByteUtil.bytesToHexString(digest));
//        System.out.println(s - System.currentTimeMillis());
    }


}