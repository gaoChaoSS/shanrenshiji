package com.zq.kyb.payment.utils;

import com.zq.kyb.util.XMLReadWriteUtils;
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
}
