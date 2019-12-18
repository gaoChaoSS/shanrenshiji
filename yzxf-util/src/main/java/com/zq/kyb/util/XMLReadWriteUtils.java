/**
 * 关于xml文件操作的一些基本的方法 ，使用的xml解析工具是dom4j http://www.dom4j.org
 * 使用单例的设计模式
 */
package com.zq.kyb.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class XMLReadWriteUtils {
    private static Log log = LogFactory.getLog(XMLReadWriteUtils.class);
    private static XMLReadWriteUtils xmlReadWriteUtils = null;
    private static final Object obj = new Object();
    private static OutputFormat format;

    private XMLReadWriteUtils() {
    }

    /**
     * 获取读取XML的实例
     *
     * @return XMLReader
     */
    public static XMLReadWriteUtils getInstance() {
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        if (xmlReadWriteUtils == null) {
            synchronized (obj) {
                if (xmlReadWriteUtils == null) {
                    xmlReadWriteUtils = new XMLReadWriteUtils();
                    // logger.debug("初始创建xmlReadWrite对象 ：" + xmlReadWriteUtils);
                }
            }
        }
        if (format == null) {
            format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");
            format.setIndent("    ");
        }
        return xmlReadWriteUtils;
    }

    /**
     * 读取XML文件，生成Document
     *
     * @param xmlFilePath xml文件路径
     * @param dtdHostName dtd文件主机名
     * @param dtdFileName dtd文件名
     * @return document
     * @throws Exception 抛出异常
     */
    public Document read(String xmlFilePath, String dtdHostName, String dtdFileName) throws Exception {
        ClassPathEntityResolver resolver = new ClassPathEntityResolver();
        resolver.setHostname(dtdHostName);
        SAXReader reader = new SAXReader();
        reader.setEntityResolver(resolver);

        reader.setIncludeExternalDTDDeclarations(true);
        reader.setIncludeInternalDTDDeclarations(false);
        // Document doc = reader.read(new File(getRealPath(xmlPath)));
        // logger.debug("成功载入xml文件");
        return reader.read(new FileInputStream(xmlFilePath));
    }

    public Document read(InputStream inputStream, Map<String, String> map) throws Exception {
        SAXReader reader = new SAXReader();
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        return reader.read(inputStream);
    }

    public Document read(InputStream inputStream) throws Exception {
        return read(inputStream, null, null);
    }

    /**
     * 根据文件流载入，生成Document
     *
     * @param inputStream 输入流
     * @param dtdHostName dtd文件主机名
     * @param dtdFileName dtd文件名
     * @return dom4j的document
     * @throws Exception 抛出异常
     */
    public Document read(InputStream inputStream, String dtdHostName, String dtdFileName) throws Exception {
        ClassPathEntityResolver resolver = new ClassPathEntityResolver();
        resolver.setHostname(dtdHostName);
        resolver.resolveEntity(null, dtdFileName);
        SAXReader reader = new SAXReader();
        reader.setEntityResolver(resolver);

        reader.setIncludeExternalDTDDeclarations(true);
        reader.setIncludeInternalDTDDeclarations(false);
        // Document doc = reader.read(new File(getRealPath(xmlPath)));
        // logger.debug("成功载入xml文件");
        Document read = reader.read(inputStream);
        return read;
    }

    public Document read(String xmlPath) throws Exception {
        return read(xmlPath, null);
    }

    public Document readForStr(String xmlstr) throws Exception {
        return readForStr(xmlstr, null);
    }

    /**
     * 读取XML文件
     *
     * @param xmlPath          xml文件路径
     * @param default_encoding
     * @return Document dom4j的document
     * @throws Exception 抛出异常
     */
    public Document read(String xmlPath, String default_encoding) throws Exception {
        SAXReader reader = new SAXReader();
        default_encoding = StringUtils.isNotEmpty(default_encoding) ? default_encoding : "utf-8";
        reader.setEncoding(default_encoding);
        return reader.read(new FileInputStream(xmlPath));
    }

    /**
     * 将字符串内容的xml转化为dom4j的document
     *
     * @param xmlstr           xml字符串
     * @param default_encoding
     * @return dom4j的document
     * @throws DocumentException 抛出异常
     */
    public Document readForStr(String xmlstr, String default_encoding) throws DocumentException {
        return readForStr(xmlstr, default_encoding, null);
    }

    public Document readForStr(String xmlstr, String default_encoding, Map map) throws DocumentException {
        SAXReader reader = new SAXReader();
        default_encoding = StringUtils.isNotEmpty(default_encoding) ? default_encoding : "utf-8";
        if (map != null)
            reader.getDocumentFactory().setXPathNamespaceURIs(map);
        reader.setEncoding(default_encoding);
        StringReader in = new StringReader(filter(xmlstr));
        return reader.read(in);
    }

    public static String filter(String xmlStr) {
        StringBuilder sb = new StringBuilder();
        char[] chs = xmlStr.toCharArray();
        // System.out.println("filter before=" +chs.length);
        for (char ch : chs) {
            if ((ch >= 0x00 && ch <= 0x08) || (ch >= 0x0b && ch <= 0x0c) || (ch >= 0x0e && ch <= 0x1f)) {
            } else {
                sb.append(ch);
            }
        }
        // System.out.println("filter after=" +sb.length());
        return sb.toString();
    }

    /**
     * 将dom4j的document转化为字符串内容的xml
     *
     * @param doc xml字符串
     * @return dom4j的document
     * @throws Exception 抛出异常
     */
    public String writeToStr(Document doc) throws Exception {

        StringWriter writeStr = new StringWriter();
        XMLWriter writer = new XMLWriter(writeStr, format);
        writer.write(doc);
        doc.clone();
        String s = writeStr.toString();
        writeStr.close();
        writer.close();
        return s;
    }

    public String writeToStrNoFormat(Document doc) throws Exception {

        StringWriter writeStr = new StringWriter();
        XMLWriter writer = new XMLWriter(writeStr);
        writer.write(doc);
        doc.clone();
        String s = writeStr.toString();
        writeStr.close();
        writer.close();
        return s;
    }

    /**
     * 写xml到文件
     *
     * @param document 需要写的内容
     * @param xmlPath  需要写的xml的路径
     * @throws Exception 抛出异常
     */
    public void write(Document document, String xmlPath) throws Exception {
        // 美化格式
        FileExecuteUtils.createDirForPath(xmlPath);
        FileOutputStream out = new FileOutputStream(xmlPath);

        write(document, out);
        out.close();
    }

    public void write(Document doc, OutputStream out) throws IOException {
        Charset ch = Charset.forName("utf-8");
        OutputStreamWriter fileWriter = new OutputStreamWriter(out, ch);
        XMLWriter writerx = new XMLWriter(fileWriter, format);
        writerx.write(doc);
        writerx.close();
    }

    /**
     * 文件的相对路径
     *
     * @param path String
     * @return String
     */
    public String getRealPath(String path) {
        // logger.debug("path = " + path);
        URL url = null;
        try {
            url = this.getClass().getClassLoader().getResource(path); // com.ntsky
            // .
            // properties
            // .
            log.debug("成功获得XML的物理路径 : " + url.getFile());
        } catch (Exception e) {
            // logger.debug("url error :" + e.getMessage());
        }
        assert url != null;
        return url.getFile();
    }

    public static String setValueFromNode(Node node, String name) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        if (node != null && name != null) {
            try {
                Node ch = node.selectSingleNode(name);
                if (ch == null) {
                    ch = node.selectSingleNode("@" + name);
                }
                if (ch != null) {
                    return ch.getText();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Document changeMap(TreeMap<String, String> treeMap) {
        Document doc = new DefaultDocument();
        Element root = new DefaultElement("d");
        doc.setRootElement(root);
        for (String key : treeMap.keySet()) {
            Element el = new DefaultElement("e");
            DefaultCDATA conTblOprCdata = new DefaultCDATA(treeMap.get(key));// CDATA格式化
            Attribute al = new DefaultAttribute("v", key);
            el.add(al);
            el.add(conTblOprCdata);
            root.add(el);
        }
        return doc;
    }

    /**
     * 删除标签属性
     *
     * @param parent
     * @return
     */
    public static Node delAttr(Element parent) {
        String name = parent.getName();
        Element n = new DefaultElement(name);
        n.setText(parent.getText());
        List li = parent.selectNodes("./*");
        if (li != null && li.size() > 0) {
            for (Object object : li) {
                n.add(delAttr((Element) object));
            }
        }
        return n;
    }

    public static void main(String[] args) {
        TreeMap<String, String> t = new TreeMap<String, String>();
        t.put("name", "<joey>");
        t.put("df", "<sdf>");
        System.out.println(changeMap(t).asXML());
    }
}
