package com.zq.kyb.order;


import org.apache.poi.util.IOUtils;

import java.io.*;

public class Temp {


    public static void main(String[] args) throws  Exception {
        String fileName = "/Users/zq2014/Downloads/apiclient_cert.p12";
        byte [] b = IOUtils.toByteArray(new FileInputStream(fileName));
        String s=  com.zq.kyb.util.Base64.encodeBytes(b);
        System.out.println(s);
    }
}
