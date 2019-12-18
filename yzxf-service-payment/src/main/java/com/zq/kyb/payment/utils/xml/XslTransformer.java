package com.zq.kyb.payment.utils.xml;

import com.zq.kyb.core.init.Constants;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XslTransformer {
    static TransformerFactory factory = TransformerFactory.newInstance();

    /**
     * 转换成流
     *
     * @param xslFileName
     * @param node
     * @param encoding
     * @return
     * @throws TransformerException
     * @throws IOException
     */
    public static StreamResult transformWriter(String xslFileName, Node node, String encoding) throws TransformerException, IOException {
        encoding = encoding == null ? Constants.DEFAULT_ENCODING : encoding;
        FileReader fileReader = new FileReader(xslFileName);
        Transformer transformer = factory.newTransformer(new StreamSource(fileReader));
        StringReader sr = new StringReader(node.asXML());
        StreamSource source = new StreamSource(sr);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        transformer.setOutputProperty("encoding", encoding);
        transformer.transform(source, result);
        fileReader.close();
        return result;
    }

    /**
     * 转换成流
     *
     * @param xslFileName
     * @param node
     * @return
     * @throws TransformerException
     */
    public static StreamResult transformString(String xslFileName, String xmlstr) throws TransformerException {

        Transformer transformer = factory.newTransformer(new StreamSource(xslFileName));
        StringReader w = new StringReader(xmlstr);
        StringWriter sw = new StringWriter();
        StreamSource source = new StreamSource(w);
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        return result;
    }

    /**
     * 转换成xml文档
     *
     * @param xslFileName
     * @param node
     * @return
     * @throws TransformerException
     */
    public static Document transformNode(String xslFileName, Node node) throws TransformerException {
        Transformer transformer = factory.newTransformer(new StreamSource(xslFileName));
        DocumentSource source = new DocumentSource(node);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);
        return result.getDocument();
    }

    /**
     * 转换成文件
     *
     * @param xslFileName
     * @param node
     * @return
     * @throws TransformerException
     */
    public static void transformFile(String xslFileName, String xmlFileName, String outFileName) throws TransformerException {
        Transformer transformer = factory.newTransformer(new StreamSource(xslFileName));
        StreamSource source = new StreamSource(xmlFileName);
        StreamResult result = new StreamResult(outFileName);
        transformer.transform(source, result);
    }

}
