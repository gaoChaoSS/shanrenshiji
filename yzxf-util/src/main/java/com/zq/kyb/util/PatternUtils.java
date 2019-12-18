package com.zq.kyb.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PowerEE. User: Joey Date: 2008-5-23 Time: 0:02:28
 */
public class PatternUtils {
    /**
     * 得到网页的<meta标签的数据> <META http-equiv=Content-Type content="text/html; charset=UTF-8">
     */
    public static final String REGEX_CHARSET = "<meta [^>]*charset=[^\\>]*\\>";
    // public static final String REGEX_EMAIL =
    // "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
    // ;
    public static final String REGEX_EMAIL = "[\\w][\\w\\.\\-_]+@([\\w\\-]+\\.)+[\\w]+";

    /**
     * 得到链接<a 中的 href属性的数据
     */
    // public static final String REGEX_HREF_URL =
    // "(src|href)=[\\s\"\']*([^\\>\\s\"])*[\"\'[^\\>\\s]]";
    public static final String REGEX_HREF_URL = "href=[\\s\"\']*([^\\>\\s\"])*[\"\'[^\\>\\s]]";
    public static final String ALL_ELEMENT = "<([^\\>]+?)>";

    /**
     * 得到所有链接
     */
    public static final String REGEX_NODE_A = "(<a )(.+?)(</a>)";
    public static final String REGEX_CHINESE = "[\\u4e00-\\u9fa5]+";

    /**
     * 去掉所有标签数据
     * 
     * @param x
     * @return
     */
    public static String deleteElemet(String x) {
        return x.replaceAll(ALL_ELEMENT, "");
    }

    public static List<String> forFilePattern(String patternStr, String filePath) throws IOException {
        StringBuffer r = FileExecuteUtils.getInstance().readFile(filePath, "gb2312");
        Matcher m = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(r.toString());
        ArrayList<String> li = new ArrayList<String>();
        while (m.find()) {
            li.add(m.group());
        }
        return li;
    }

    /**
     * 去调没有用的信息，包括css和javascript
     * 
     * @throws IOException
     */

    public static String deleteScript(String str) throws IOException {
        Pattern pt = Pattern.compile("<(script|style)([^>]*)>([^<]*)</(script|style)>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = pt.matcher(str);
        if (m.find())
            str = m.replaceAll("");
        return str;
    }

    public static Boolean exePattern(String patternStr, String str) throws IOException {
        Pattern pt = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.UNICODE_CASE);
        Matcher m = pt.matcher(str);
        return m.matches();
    }

    public static String getString(String patternStr, String str) {
        Pattern pt = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = pt.matcher(str);
        if (m.find())
            return m.group();
        return null;
    }

    public static List<String> getStrings(String patternStr, String str) {
        Pattern pt = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = pt.matcher(str);
        List<String> list = new ArrayList<String>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    public static List<String> forStrPattern(String patternStr, String s) {
        Matcher m = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(s);
        ArrayList<String> li = new ArrayList<String>();
        while (m.find()) {
            li.add(m.group());
        }
        return li;
    }

    public static String replaceAll(String str, String patternStr, String repTo) {
        // Pattern.CASE_INSENSITIVE:大小写不敏感
        // Pattern.DOTALL:"."可匹配任意字符,包括行的结束符,默认是不包括行的结束符
        // Pattern.UNICODE_CASE:unicode可以参与匹配

        Matcher m = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(str);
        return m.replaceAll(repTo);
    }

    public static final String REGEX_GETPHOME = "((\\(\\d{3}\\))|(\\d{3}\\-))?((0\\d{2,3}-)?\\d{7,12})";
    public static final String REGEX_Mobile = "((\\(\\d{3}\\))|(\\d{3}\\-))?((13|15|18)\\d{9})";

    /**
     * 提取电话号码的表达式
     * 
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {
        String number = "(086)186 0283-5979";
        Matcher m = Pattern.compile("[^\\d]+", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(number);
        number = m.replaceAll("");

        Logger.getLogger(PatternUtils.class.getName()).info(number);
    }

    /**
     * 检查是否为Double
     *
     * @param count
     * @return
     */
    public static boolean checkDouble(String count) throws IOException {
        String p = "[-+]?[\\d|\\.]+";
        return exePattern(p, count);
    }
}
