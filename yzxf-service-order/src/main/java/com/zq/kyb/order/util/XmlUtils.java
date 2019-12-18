package com.zq.kyb.order.util;

import com.zq.kyb.util.StringUtils;
import com.zq.kyb.util.XMLReadWriteUtils;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaoke on 2016/8/22.
 */
public class XmlUtils {
    public static void main(String[] args) throws Exception {
        xmlToMap("");
    }

    public static Map<String, Object> xmlToMap(String result) throws Exception {
        Document doc = XMLReadWriteUtils.getInstance().readForStr(result);
        Element rootElement = doc.getRootElement();
        List list = rootElement.selectNodes("*");
        Map<String, Object> reqParams = new HashMap<>();
        for (Object c : list) {
            Node cnode = (Node) c;
            cnode.getName();
            reqParams.put(cnode.getName(), cnode.getText());
        }
        return reqParams;
    }

    public static Map<String, Object> xmlToMapObject(Object result) throws Exception {
        if(result==null){
            return null;
        }else{
            return toMap(result.toString());
        }
    }

    public static Map<String, Object> toMap(String result) throws Exception {
        if(StringUtils.isEmpty(result)){
            return null;
        }
        Document doc = XMLReadWriteUtils.getInstance().readForStr(result);
        Element rootElement = doc.getRootElement();
        Map<String, Object> rootParams = new HashMap<>();
        rootParams.put(rootElement.getName(), getMap(rootElement));
        System.out.println(JSONObject.fromObject(rootParams).toString());
        return rootParams;
    }


    public static Object getMap(Element element) {
        List list = element.selectNodes("*");
        if (list.size() > 0) {
            Map<String, Object> reqParams = new HashMap<>();
            for (Object c : list) {
                Element el = (Element) c;
                reqParams.put(el.getName(), getMap(el));
            }
            return reqParams;
        } else {
            return element.getText();
        }
    }
}
