package com.zq.kyb.util;

/**
 * 作用:关于实体操作，和实体命名规范的一些操作
 * <p/>
 * Timestamp: 2007-4-26 Time: 11:11:33
 */
public class EntityUtils {
	/**
	 * 将名称改为名称习惯的命名，第一哥字母大写
	 * 
	 * @param input
	 *            输入字符串
	 * @return 字符串
	 */
	public static String getEntityName(String input) {
		return StringUtils.toFirstUp(input);
	}

	/**
	 * 将名称改为变量习惯的命名，第一哥字母小写
	 * 
	 * @param input
	 *            输入字符串
	 * @return 字符串
	 */
	public static String getEntityVar(String input) {
		return input != null && input.length() > 0 ? input.substring(0, 1).toLowerCase() + input.substring(1) : input;
	}

	public static void main(String[] args) {
		String s = "sss.asdf.fff";
		String p = "sss.";
		String n = null;
		if (p != null && s.startsWith(p)) {
			s = s.substring(p.length());
		}
		System.out.println(s);
		if (n != null && s.endsWith(n)) {
			s = s.substring(0, s.length() - n.length());
		}
		System.out.println(s);
	}
}
