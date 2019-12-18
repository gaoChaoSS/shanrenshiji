package com.zq.kyb.payment.utils.xml;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;
import net.sf.json.processors.JsonValueProcessor;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 用户JAXB和Json对日期处理的转换
 *
 * @author Administrator
 */
public class TimestampAdapter extends XmlAdapter<String, Timestamp> implements JsonValueProcessor, JsonBeanProcessor {
    public static final java.lang.String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public Timestamp unmarshal(String v) throws Exception {
        return new Timestamp(new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_SSS).parse(v).getTime());
    }

    @Override
    public String marshal(Timestamp v) throws Exception {
        return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_SSS).format(v);
    }

    @Override
    public JSONObject processBean(Object target, JsonConfig arg1) {

        return null;
    }

    @Override
    public Object processArrayValue(Object target, JsonConfig arg1) {
        if (target instanceof java.sql.Timestamp) {
            Timestamp p = (Timestamp) target;
            try {
                return marshal(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return null;
    }

    @Override
    public Object processObjectValue(String arg0, Object target, JsonConfig arg2) {
        if (target instanceof java.sql.Timestamp) {
            Timestamp p = (Timestamp) target;
            try {
                return marshal(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return null;
    }
}