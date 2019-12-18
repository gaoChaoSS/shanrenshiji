package com.zq.kyb.util.json;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;
import net.sf.json.processors.JsonValueProcessor;

import java.sql.Timestamp;

public class DateJsonProcessor implements JsonValueProcessor, JsonBeanProcessor {
    @Override
    public Object processObjectValue(String key, Object target, JsonConfig arg2) {
        return process(target);
    }

    private Object process(Object target) {
        if (target instanceof Timestamp) {
            Timestamp p = (Timestamp) target;
            return p.getTime();
        }
        if (target instanceof JSONNull) {
            return null;
        } else {
            return "";
        }
    }

    @Override
    public Object processArrayValue(Object target, JsonConfig arg1) {
        return process(target);
    }

    @Override
    public JSONObject processBean(Object arg0, JsonConfig arg1) {
        if (arg0 instanceof JSONNull) {
            return null;
        }
        return null;
    }


    public static void setJsonConfig(JsonConfig config) {
        config.setJsonPropertyFilter(new EntityPropertyFilter());
        config.registerJsonValueProcessor(Timestamp.class, new DateJsonProcessor());
        config.registerJsonBeanProcessor(Timestamp.class, new DateJsonProcessor());
        config.registerJsonBeanProcessor(JSONNull.class, new DateJsonProcessor());
        config.registerJsonValueProcessor(JSONNull.class, new DateJsonProcessor());
    }

}
