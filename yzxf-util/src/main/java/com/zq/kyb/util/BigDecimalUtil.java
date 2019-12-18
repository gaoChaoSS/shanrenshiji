package com.zq.kyb.util;

import java.math.BigDecimal;

/**
 * Created by hujoey on 16/4/17.
 */
public class BigDecimalUtil {
    public static Double fixDoubleNum2(double num) {
        return new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//四舍五入保留两位小数
    }

    public static Double fixDoubleNum(double num, int scale) {
        num = new BigDecimal(num).setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();//解决科学计数法引起的问题
        return new BigDecimal(num).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();//向负无穷方向舍入
    }

    public static Double fixDoubleNumProfit(double num) {
        Double d1 = new BigDecimal(num).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        String d1Str = d1.toString();
        int i = d1Str.indexOf(".");
        String newStr = d1Str;
        if (i > -1) {
            if (d1Str.length() > i + 3) {
                newStr = d1Str.substring(0, i + 3);
            }
        }
        return Double.valueOf(newStr);
//        return new BigDecimal(num).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();//四舍五入保留三位小数,再舍去最后一位
//      return fixDoubleNum2Down(new BigDecimal(num).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());//四舍五入保留三位小数,再舍去最后一位
    }

    public static Double fixDoubleNum2Down(double num) {
        Double d1 = new BigDecimal(num).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        String d1Str = d1.toString();
        int i = d1Str.indexOf(".");
        String newStr = d1Str;
        if (i > -1) {
            if (d1Str.length() > i + 3) {
                newStr = d1Str.substring(0, i + 3);
            }
        }
        return Double.valueOf(newStr);
    }


    public static Double add(double old, double add) {
        return new BigDecimal(old).add(new BigDecimal(add)).doubleValue();//四舍五入保留两位小数
    }

    public static Double multiply(double old, double add) {//乘法
        return new BigDecimal(old).multiply(new BigDecimal(add)).doubleValue();//四舍五入保留两位小数
    }

    public static Double divide(double old, double add) {//除法
        return new BigDecimal(old).divide(new BigDecimal(add)).doubleValue();//四舍五入保留两位小数
    }

    public static void main(String[] args) {
        System.out.println(fixDoubleNumProfit(123456));
        System.out.println(fixDoubleNumProfit(5.1));
        System.out.println(fixDoubleNumProfit(0.12));
        System.out.println(fixDoubleNumProfit(0.120));

        System.out.println("------------");

        System.out.println(fixDoubleNumProfit(0.125656));
        System.out.println(fixDoubleNumProfit(0.129999));
        System.out.println(fixDoubleNumProfit(0.12444));
        System.out.println(fixDoubleNumProfit(0.1200001));
        System.out.println(fixDoubleNumProfit(add(4.41, 0.61)));
    }
}
