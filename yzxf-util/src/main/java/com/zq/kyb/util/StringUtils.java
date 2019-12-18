package com.zq.kyb.util;

import net.sf.json.JSONNull;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对String的一些扩展操作
 * <p>
 * User: joey hu Timestamp: Nov 11, 2005 Time: 4:23:21 PM
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {
    /**
     * 将整数前面补0,让其变为固定长度的字符串
     *
     * @param input  输入整数
     * @param numLen 固定的长度
     * @return 生成固定长度的字符串
     */
    public static String greStrInt(long input, int numLen) {

        String s = String.valueOf(input);
        int len = s.length();
        if (len > numLen) {
            s = s.substring(0, numLen);
        } else {
            for (int i = 0; i < numLen - len; i++) {
                s = "0" + s;
            }
        }
        return s;
    }

    /**
     * 对象是否包含在数组中
     *
     * @param objects 对象数组
     * @param o       目标对象
     * @return 是否包含;
     */
    public static boolean isContain(Object[] objects, Object o) {
        if (o != null && objects != null)
            for (int i = 0; i < objects.length; i++) {
                Object str = objects[i];
                if (o.equals(str)) {
                    return true;
                }
            }
        return false;
    }

    /**
     * @param target
     * @param srouce
     * @param reg
     * @return
     */
    public static boolean inc(String target, String srouce, String reg) {
        reg = reg == null ? "," : reg;
        return (reg + srouce + reg).indexOf(reg + target + reg) != -1;
    }

    /**
     * 将名称改为名称习惯的命名，第一哥字母大写
     *
     * @param input 输入字符串
     * @return 字符串
     */
    public static String getEntityName(String input) {
        return input != null && input.length() > 0 ? input.substring(0, 1).toUpperCase() + input.substring(1) : input;
    }

    /**
     * 将名称改为变量习惯的命名，第一哥字母小写
     *
     * @param input 输入字符串
     * @return 字符串
     */
    public static String getEntityInstance(String input) {
        return input != null && input.length() > 0 ? input.substring(0, 1).toLowerCase() + input.substring(1) : input;
    }

    /**
     * 将/com/joey/sss转换为包的路径表示方式com.joey.sss
     *
     * @param pathName
     * @return
     */
    public static String getPackageName(String pathName) {
        return pathName.replaceAll("\\/", "\\.");
    }

    /**
     * 将com.joey.sss转换为包的实际路径表示方式 /com/joey/sss
     *
     * @param packageName 包名
     * @return 字符串
     */
    public static String getPathName(String packageName) {
        return packageName.replaceAll("\\.", "\\/");
    }

    public static String merge(String str, Hashtable params) {
        if (str != null) {
            Set set = params.keySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();
                Object value = params.get(o);
                if (o instanceof String && value instanceof String) {
                    String reStrs = "${" + o + "}";
                    String reStr = "[$][{]" + o + "[}]+";
                    int i = str.indexOf(reStrs);
                    if (i != -1) {
                        str = str.replaceAll(reStr, (String) value);
                    }
                }
            }
        }
        return str;
    }

    /**
     * 根据 传入一个类似 k=sss,a=a1的字符串将值取出来。 注意暂时没有解决逗号的冲突。
     *
     * @param str  输入字符串
     * @param name name
     * @return value
     */
    public static String getFieldValue(String str, String name) {
        String[] ps = str.split(",");
        for (int i = 0; i < ps.length; i++) {
            String p = ps[i];
            int i1 = p.indexOf("=");
            if (i1 != -1) {
                if (p.substring(0, i1).trim().equals(name.trim())) {
                    // s=s.replaceAll("\\'","\\,");
                    return p.substring(i1 + 1);
                }
            }
        }
        return null;
    }

    /**
     * 类似javascript的join功能,将一个字符串数组的内容用一个连接字符连接起来
     *
     * @param strings 字符串数组
     * @param j       连接字符
     * @return 连接后的字符串
     */
    public static String join(Object[] strings, String j) {
        String str = "";
        if (strings != null && j != null && strings.length > 0 && !j.equals("")) {
            for (int i = 0; i < strings.length; i++) {
                Object s = strings[i];
                if (i != 0)
                    str += j + s;
                else
                    str += s;
            }
        }
        return str;
    }

    /**
     * 根据url得到参数map
     *
     * @param url 如:index.html?aaa=bbb&ccc=2222
     * @return map
     */
    public static Map<String, String> getParamsForUrl(String url) {
        int i = url.indexOf("?");
        Map<String, String> params = new HashMap<String, String>();
        if (i != -1 && i < url.length()) {
            String[] strings = url.substring(i + 1).split("&");
            for (String string : strings) {
                String[] s = string.split("=");
                if (s != null && s.length == 2) {
                    params.put(s[0], s[1]);
                }
            }

        }
        return params;
    }

    /**
     * 转全角的函数(SBC case) 全角空格为12288，半角空格为32 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     *
     * @param input 任意字符串
     * @return 全角字符串
     */
    public static String toSBC(String input) {
        // 半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 转半角的函数(DBC case) 全角空格为12288，半角空格为32 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     *
     * @param input 全角字符串
     * @return 半角字符串
     */

    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 取得字符串中的数字
     *
     * @param str
     * @return
     */
    public static int getIntInString(String str) {
        StringBuffer strs = new StringBuffer();
        char[] ch = toDBC(str).toCharArray();
        for (char c : ch) {
            if (c >= 48 && c <= 57)
                strs.append(c);
        }
        String s = strs.toString();
        if (s.length() > 0) {
            return Integer.parseInt(s);
        }
        return -1;
    }

    /**
     * 字符串的排列组合
     *
     * @param str
     */
    public static void permuter(String str) {
        int length = str.length();
        boolean[] visited = new boolean[length];
        StringBuffer out = new StringBuffer();
        char[] in = str.toCharArray();
        doPermuter(in, out, visited, length, 0);
    }

    static void doPermuter(char[] in, StringBuffer out, boolean[] visited, int length, int level) {
        // 如果超过了最后一个位置
        if (level == length) {
            Logger.getLogger(StringUtils.class.getName()).info(out.toString());
            // 打印这个字符
            return;
        }

        for (int i = 0; i < length; ++i) {

            if (visited[i])
                continue;
            // 对于输入的每一个字符 ,如果标记为已用过,跳过它,转向下一个字符
            out.append(in[i]);
            // 否则将这个字符 放在当前位置
            visited[i] = true;
            // 将这个字符标记为已用过
            doPermuter(in, out, visited, length, level + 1);
            // 从当前位置+1开始全排列剩下的符
            visited[i] = false;
            // 将这个字符标记为未用过
            out.setLength(out.length() - 1);
        }
    }

    /**
     * 字符串全组合
     *
     * @param str
     */

    public static void combine(String str) {
        int length = str.length();
        char[] instr = str.toCharArray();
        StringBuilder outstr = new StringBuilder();
        doCombine(instr, outstr, length, 0, 0);
    }

    static void doCombine(char[] instr, StringBuilder outstr, int length, int level, int start) {
        for (int i = start; i < length; i++) {
            // 对于输入字符串中从输入起始位置到结束 位置的每一个字符
            outstr.append(instr[i]);
            // 选择该字符放到输出字符串的当前位置
            Logger.getLogger(StringUtils.class.getName()).info(outstr.toString());
            if (i < length - 1) {
                // 如果当前字符不是输入字符串中的最后 一个字符
                doCombine(instr, outstr, length, level + 1, i + 1);
                // 在一下位置生成剩下的组合,从刚才选择的字符之后的一个字符开始迭代
                outstr.setLength(outstr.length() - 2);
            }
        }
    }

    public static String subStrInfo(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length - 3) + "...";
        }
        return str;
    }

    public static String getRandomString(int size) {// 随机字符串
        char[] c = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'};
        Random random = new Random(); // 初始化随机数产生器
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(c[Math.abs(random.nextInt()) % c.length]);
        }
        return sb.toString();
    }

    public static String toFirstUp(String input) {
        return input != null && input.length() > 0 ? input.substring(0, 1).toUpperCase() + input.substring(1) : input;
    }

    public static String toFirstLower(String input) {
        return input != null && input.length() > 0 ? input.substring(0, 1).toLowerCase() + input.substring(1) : input;
    }

    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    /**
     * Returns the name portion of a XMPP address. For example, for the address "matt@jivesoftware.com/Smack", "matt" would be returned. If no username is present in the address, the empty string will
     * be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the name portion of the XMPP address.
     */
    public static String parseName(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int atIndex = XMPPAddress.lastIndexOf("@");
        if (atIndex <= 0) {
            return "";
        } else {
            return XMPPAddress.substring(0, atIndex);
        }
    }

    /**
     * Returns the server portion of a XMPP address. For example, for the address "matt@jivesoftware.com/Smack", "jivesoftware.com" would be returned. If no server is present in the address, the empty
     * string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the server portion of the XMPP address.
     */
    public static String parseServer(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int atIndex = XMPPAddress.lastIndexOf("@");
        // If the String ends with '@', return the empty string.
        if (atIndex + 1 > XMPPAddress.length()) {
            return "";
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex > 0 && slashIndex > atIndex) {
            return XMPPAddress.substring(atIndex + 1, slashIndex);
        } else {
            return XMPPAddress.substring(atIndex + 1);
        }
    }

    /**
     * Returns the resource portion of a XMPP address. For example, for the address "matt@jivesoftware.com/Smack", "Smack" would be returned. If no resource is present in the address, the empty string
     * will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the resource portion of the XMPP address.
     */
    public static String parseResource(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex + 1 > XMPPAddress.length() || slashIndex < 0) {
            return "";
        } else {
            return XMPPAddress.substring(slashIndex + 1);
        }
    }

    /**
     * Returns the XMPP address with any resource information removed. For example, for the address "matt@jivesoftware.com/Smack", "matt@jivesoftware.com" would be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the bare XMPP address without resource information.
     */
    public static String parseBareAddress(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex < 0) {
            return XMPPAddress;
        } else if (slashIndex == 0) {
            return "";
        } else {
            return XMPPAddress.substring(0, slashIndex);
        }
    }

    /**
     * Escapes the node portion of a JID according to "JID Escaping" (JEP-0106). Escaping replaces characters prohibited by node-prep with escape sequences, as follows:
     * <p>
     * <p>
     * <table border="1">
     * <tr>
     * <td><b>Unescaped Character</b></td>
     * <td><b>Encoded Sequence</b></td>
     * </tr>
     * <tr>
     * <td>&lt;space&gt;</td>
     * <td>\20</td>
     * </tr>
     * <tr>
     * <td>"</td>
     * <td>\22</td>
     * </tr>
     * <tr>
     * <td>&</td>
     * <td>\26</td>
     * </tr>
     * <tr>
     * <td>'</td>
     * <td>\27</td>
     * </tr>
     * <tr>
     * <td>/</td>
     * <td>\2f</td>
     * </tr>
     * <tr>
     * <td>:</td>
     * <td>\3a</td>
     * </tr>
     * <tr>
     * <td>&lt;</td>
     * <td>\3c</td>
     * </tr>
     * <tr>
     * <td>&gt;</td>
     * <td>\3e</td>
     * </tr>
     * <tr>
     * <td>@</td>
     * <td>\40</td>
     * </tr>
     * <tr>
     * <td>\</td>
     * <td>\5c</td>
     * </tr>
     * </table>
     * <p>
     * <p>
     * This process is useful when the node comes from an external source that doesn't conform to nodeprep. For example, a username in LDAP may be "Joe Smith". Because the &lt;space&gt; character
     * isn't a valid part of a node, the username should be escaped to "Joe\20Smith" before being made into a JID (e.g. "joe\20smith@example.com" after case-folding, etc. has been applied).
     * <p>
     * <p>
     * All node escaping and un-escaping must be performed manually at the appropriate time; the JID class will not escape or un-escape automatically.
     *
     * @param node the node.
     * @return the escaped version of the node.
     */
    public static String escapeNode(String node) {
        if (node == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(node.length() + 8);
        for (int i = 0, n = node.length(); i < n; i++) {
            char c = node.charAt(i);
            switch (c) {
                case '"':
                    buf.append("\\22");
                    break;
                case '&':
                    buf.append("\\26");
                    break;
                case '\'':
                    buf.append("\\27");
                    break;
                case '/':
                    buf.append("\\2f");
                    break;
                case ':':
                    buf.append("\\3a");
                    break;
                case '<':
                    buf.append("\\3c");
                    break;
                case '>':
                    buf.append("\\3e");
                    break;
                case '@':
                    buf.append("\\40");
                    break;
                case '\\':
                    buf.append("\\5c");
                    break;
                default: {
                    if (Character.isWhitespace(c)) {
                        buf.append("\\20");
                    } else {
                        buf.append(c);
                    }
                }
            }
        }
        return buf.toString();
    }

    /**
     * Un-escapes the node portion of a JID according to "JID Escaping" (JEP-0106).
     * <p>
     * Escaping replaces characters prohibited by node-prep with escape sequences, as follows:
     * <p>
     * <p>
     * <table border="1">
     * <tr>
     * <td><b>Unescaped Character</b></td>
     * <td><b>Encoded Sequence</b></td>
     * </tr>
     * <tr>
     * <td>&lt;space&gt;</td>
     * <td>\20</td>
     * </tr>
     * <tr>
     * <td>"</td>
     * <td>\22</td>
     * </tr>
     * <tr>
     * <td>&</td>
     * <td>\26</td>
     * </tr>
     * <tr>
     * <td>'</td>
     * <td>\27</td>
     * </tr>
     * <tr>
     * <td>/</td>
     * <td>\2f</td>
     * </tr>
     * <tr>
     * <td>:</td>
     * <td>\3a</td>
     * </tr>
     * <tr>
     * <td>&lt;</td>
     * <td>\3c</td>
     * </tr>
     * <tr>
     * <td>&gt;</td>
     * <td>\3e</td>
     * </tr>
     * <tr>
     * <td>@</td>
     * <td>\40</td>
     * </tr>
     * <tr>
     * <td>\</td>
     * <td>\5c</td>
     * </tr>
     * </table>
     * <p>
     * <p>
     * This process is useful when the node comes from an external source that doesn't conform to nodeprep. For example, a username in LDAP may be "Joe Smith". Because the &lt;space&gt; character
     * isn't a valid part of a node, the username should be escaped to "Joe\20Smith" before being made into a JID (e.g. "joe\20smith@example.com" after case-folding, etc. has been applied).
     * <p>
     * <p>
     * All node escaping and un-escaping must be performed manually at the appropriate time; the JID class will not escape or un-escape automatically.
     *
     * @param node the escaped version of the node.
     * @return the un-escaped version of the node.
     */
    public static String unescapeNode(String node) {
        if (node == null) {
            return null;
        }
        char[] nodeChars = node.toCharArray();
        StringBuilder buf = new StringBuilder(nodeChars.length);
        for (int i = 0, n = nodeChars.length; i < n; i++) {
            compare:
            {
                char c = node.charAt(i);
                if (c == '\\' && i + 2 < n) {
                    char c2 = nodeChars[i + 1];
                    char c3 = nodeChars[i + 2];
                    if (c2 == '2') {
                        switch (c3) {
                            case '0':
                                buf.append(' ');
                                i += 2;
                                break compare;
                            case '2':
                                buf.append('"');
                                i += 2;
                                break compare;
                            case '6':
                                buf.append('&');
                                i += 2;
                                break compare;
                            case '7':
                                buf.append('\'');
                                i += 2;
                                break compare;
                            case 'f':
                                buf.append('/');
                                i += 2;
                                break compare;
                        }
                    } else if (c2 == '3') {
                        switch (c3) {
                            case 'a':
                                buf.append(':');
                                i += 2;
                                break compare;
                            case 'c':
                                buf.append('<');
                                i += 2;
                                break compare;
                            case 'e':
                                buf.append('>');
                                i += 2;
                                break compare;
                        }
                    } else if (c2 == '4') {
                        if (c3 == '0') {
                            buf.append("@");
                            i += 2;
                            break compare;
                        }
                    } else if (c2 == '5') {
                        if (c3 == 'c') {
                            buf.append("\\");
                            i += 2;
                            break compare;
                        }
                    }
                }
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Escapes all necessary characters in the String so that it can be used in an XML doc.
     *
     * @param string the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static String escapeForXML(String string) {
        if (string == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = string.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int) (len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            } else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            } else if (ch == '>') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            } else if (ch == '&') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                // Do nothing if the string is of the form &#235; (unicode
                // value)
                if (!(len > i + 5 && input[i + 1] == '#' && Character.isDigit(input[i + 2]) && Character.isDigit(input[i + 3]) && Character.isDigit(input[i + 4]) && input[i + 5] == ';')) {
                    last = i + 1;
                    out.append(AMP_ENCODE);
                }
            } else if (ch == '"') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(QUOTE_ENCODE);
            }
        }
        if (last == 0) {
            return string;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    public static String descapeForXML(String str) {
        if (str == null)
            return null;
        if (str.indexOf(";") == -1 || str.indexOf("&") == -1)
            return str;
        return str.replaceAll("&amp;", "&")//
                .replaceAll("&lt;", "<")//
                .replaceAll("&gt;", ">")//
                .replaceAll("&quot;", "\"");
    }

    /**
     * Used by the hash method.
     */
    private static MessageDigest digest = null;

    /**
     * Hashes a String using the SHA-1 algorithm and returns the result as a String of hexadecimal numbers. This method is synchronized to avoid excessive MessageDigest object creation. If calling
     * this method becomes a bottleneck in your code, you may wish to maintain a pool of MessageDigest objects instead of using this method.
     * <p>
     * A hash is a one-way function -- that is, given an input, an output is easily computed. However, given the output, the input is almost impossible to compute. This is useful for passwords since
     * we can store the hash and a hacker will then have a very hard time determining the original password.
     *
     * @param data the String to compute the hash of.
     * @return a hashed version of the passed-in String
     */
    public synchronized static String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("Failed to load the SHA-1 MessageDigest. " + "Jive will be unable to function normally.");
            }
        }
        // Now, compute hash.
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
        }
        return encodeHex(digest.digest());
    }

    /**
     * Encodes an array of bytes as String representation of hexadecimal.
     *
     * @param bytes an array of bytes to convert to a hex string.
     * @return generated hex string.
     */
    public static String encodeHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            if ((aByte & 0xff) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toString(aByte & 0xff, 16));
        }

        return hex.toString();
    }

    /**
     * Pseudo-random number generator object for use with randomString(). The Random class is not considered to be cryptographically secure, so only use these random Strings for low to medium security
     * applications.
     */
    private static Random randGen = new Random();

    /**
     * Array of numbers and letters of mixed case. Numbers appear in the list twice so that there is a more equal chance that a number will be picked. We can use the array to get a random number or
     * letter by picking a random array index.
     */
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    /**
     * Returns a random String of numbers and letters (lower and upper case) of the specified length. The method uses the Random class that is built-in to Java which is suitable for low to medium
     * grade security uses. This means that the output is only pseudo random, i.e., each number is mathematically generated so is not truly random.
     * <p>
     * <p>
     * The specified length must be at least one. If not, the method will return null.
     *
     * @param length the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */
    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    public static String fromUnicode(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String toUnicode(String chinese) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chinese.length(); i++) {
            sb.append("\\u").append(Integer.toHexString(chinese.charAt(i)));
        }
        return sb.toString();
    }

    public static String camel4underline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    public static String underline4camel(String param) {
        String[] ps = param.split("_");
        StringBuffer builder = new StringBuffer();
        for (int i = 0; i < ps.length; i++) {
            String p = toFirstUp(ps[i]);
            builder.append(p);
        }
        return builder.toString();
    }

    public static String getRandomNumber(int size) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            result.append(randGen.nextInt(10));
        }
        return result.toString();
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                // do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if (source == null) {
            return null;
        }
        if (!containsEmoji(source)) {
            return source;// 如果不包含，直接返回
        }
        // 到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codePoint);
            } else {
            }
        }
        if (buf == null) {
            return source;// 如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    public static void main(String[] args) {

        Logger.getLogger(StringUtils.class.getName()).info(underline4camel("abc_dd_fff_lll"));
    }

    public static boolean mapValueIsEmpty(Map values, String key) {
        return values == null || !values.containsKey(key) || values.get(key) == null || values.get(key) instanceof net.sf.json.JSONNull || "".equals(values.get(key).toString());
    }
}
