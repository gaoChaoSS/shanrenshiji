package com.zq.kyb.payment.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
    /**
     * 连接超时时间
     */
    public static final int CONNECTION_TIMEOUT_MS = 10000;

    /**
     * 读取数据超时时间
     */
    public static final int SO_TIMEOUT_MS = 10000;

    public static final String CONTENT_TYPE_JSON_CHARSET = "application/json;charset=utf-8";

    public static final String CONTENT_TYPE_XML_CHARSET = "application/xml;charset=utf-8";

    /**
     * httpclient读取内容时使用的字符集
     */
    public static final String CONTENT_CHARSET = "UTF-8";

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static final Charset GBK = Charset.forName("GBK");

    static {
//        try {
//            File f = new File(System.getProperty("user.home") + "/" + WeChatConfig.partnerId + ".p12");
//            if (f.exists()) {
//                KeyStore keyStore = KeyStore.getInstance("PKCS12");
//                InputStream instream = new FileInputStream(f);
//                try {
//                    keyStore.load(instream, WeChatConfig.partnerId.toCharArray());
//                } finally {
//                    instream.close();
//                }
//                // Trust own CA and all self-signed certs
//                SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, WeChatConfig.partnerId.toCharArray()).build();
//                // Allow TLSv1 protocol only
//                sslSocketFactory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//                System.out.println("wechat p12 证书加载成功");
//            } else {
//                System.err.println("wechat p12 证书未找到");
//            }
//        } catch (Exception e) {
//            throw new UserOperateException(400, "wechat p12 证书加载失败");
//        }
    }

    public static String simpleGetInvoke(String url) throws Exception {
        return simpleGetInvoke(url, null);
    }

    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String simpleGetInvoke(String url, Map<String, String> params) throws Exception {
        return simpleGetInvoke(url, params, CONTENT_CHARSET);
    }

    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String simpleGetInvoke(String url, Map<String, String> params, String charset) throws Exception {

        HttpClient client = buildHttpClient(false);

        HttpGet get = buildHttpGet(url, params);

        Logger.getLogger(HttpClientUtils.class).info("[simpleGetInvoke]: url=" + url);
        Logger.getLogger(HttpClientUtils.class).info("[params]: url=" + params);
        Logger.getLogger(HttpClientUtils.class).info("[charset]: url=" + charset);

        org.apache.http.HttpResponse response = client.execute(get);

        assertStatus(response);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String returnStr = EntityUtils.toString(entity, charset);
            return returnStr;
        }
        return null;
    }

    public static String simplePostInvoke(String url) throws Exception {
        return simplePostInvoke(url, new HashMap<String, String>());
    }

    /**
     * 简单post调用
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */

    public static String simplePostInvoke(String url, Map<String, String> params) throws Exception {
        return simplePostInvoke(url, params, CONTENT_CHARSET);
    }

    public static String simplePostInvoke(String url, String content) throws Exception {
        return simplePostInvoke(url, content, CONTENT_CHARSET);
    }

    public static String simplePostInvoke(String url, String content, String charset) throws Exception {
        HttpClient client = buildHttpClient(false);

        HttpPost postMethod = buildHttpPost(url, content, charset);

        org.apache.http.HttpResponse response = client.execute(postMethod);

        assertStatus(response);

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String returnStr = EntityUtils.toString(entity, charset);
            return returnStr;
        }

        return null;
    }

    /**
     * 简单post调用
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String simplePostInvoke(String url, Map<String, String> params, String charset) throws Exception {

        HttpClient client = buildHttpClient(false);

        HttpPost postMethod = buildHttpPost(url, params, charset);

        org.apache.http.HttpResponse response = client.execute(postMethod);

        assertStatus(response);

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String returnStr = EntityUtils.toString(entity, charset);
            return returnStr;
        }

        return null;
    }

    /**
     * 创建HttpClient
     *
     * @param isMultiThread
     * @return
     * @throws Exception
     */
    public static HttpClient buildHttpClient(boolean isMultiThread) throws Exception {
        HttpClientBuilder builder = HttpClientBuilder.create();
        if (sslSocketFactory != null) {
            builder.setSSLSocketFactory(sslSocketFactory);
        }
        if (isMultiThread) {
            builder.setConnectionManager(new PoolingHttpClientConnectionManager());
        }
        return builder.build();
    }

    public static HttpPost buildHttpPost(String url, String content, String charset) throws UnsupportedEncodingException, URISyntaxException {
        Assert.notNull(url, "构建HttpPost时,url不能为null");
        Assert.notNull(content, "构建HttpPost时,content不能为null");
        HttpPost post = new HttpPost(url);
        setCommonHttpMethod(post);
        StringEntity he = new StringEntity(content, charset);
        post.setEntity(he);
        // 在RequestContent.process中会自动写入消息体的长度，自己不用写入，写入反而检测报错
        // setContentLength(post, he);
        return post;
    }

    private static SSLConnectionSocketFactory sslSocketFactory = null;

    /**
     * 构建httpPost对象
     *
     * @param url
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, Map<String, String> params, String charset) throws UnsupportedEncodingException, URISyntaxException {
        Assert.notNull(url, "构建HttpPost时,url不能为null");
        HttpPost post = new HttpPost(url);
        setCommonHttpMethod(post);
        HttpEntity he = null;
        if (params != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
            he = new UrlEncodedFormEntity(formparams, charset);
            post.setEntity(he);
        }
        // 在RequestContent.process中会自动写入消息体的长度，自己不用写入，写入反而检测报错
        // setContentLength(post, he);
        return post;
    }

    /**
     * 构建httpGet对象
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpGet(String url, Map<String, String> params) throws URISyntaxException {
        Assert.notNull(url, "构建HttpGet时,url不能为null");
        HttpGet get = new HttpGet(buildGetUrl(url, params));
        return get;
    }

    /**
     * build getUrl str
     *
     * @param url
     * @param params
     * @return
     */
    private static String buildGetUrl(String url, Map<String, String> params) {
        StringBuffer uriStr = new StringBuffer(url);
        if (params != null) {
            List<NameValuePair> ps = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                ps.add(new BasicNameValuePair(key, params.get(key)));
            }
            uriStr.append("?");
            uriStr.append(URLEncodedUtils.format(ps, UTF_8));
        }
        return uriStr.toString();
    }

    /**
     * 设置HttpMethod通用配置
     *
     * @param httpMethod
     */
    public static void setCommonHttpMethod(HttpRequestBase httpMethod) {
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, CONTENT_CHARSET);// setting
        // contextCoding
        // httpMethod.setHeader(HTTP.CHARSET_PARAM, CONTENT_CHARSET);
        // httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON_CHARSET);
        // httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_XML_CHARSET);
    }

    /**
     * 设置成消息体的长度 setting MessageBody length
     *
     * @param httpMethod
     * @param he
     */
    public static void setContentLength(HttpRequestBase httpMethod, HttpEntity he) {
        if (he == null) {
            return;
        }
        httpMethod.setHeader(HTTP.CONTENT_LEN, String.valueOf(he.getContentLength()));
    }

    /**
     * 构建公用RequestConfig
     *
     * @return
     */
    public static RequestConfig buildRequestConfig() {
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT_MS).setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
        return requestConfig;
    }

    /**
     * 强验证必须是200状态否则报异常
     *
     * @param res
     * @throws HttpException
     */
    static void assertStatus(org.apache.http.HttpResponse res) throws IOException {
        Assert.notNull(res, "http响应对象为null");
        Assert.notNull(res.getStatusLine(), "http响应对象的状态为null");
        switch (res.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                // case HttpStatus.SC_CREATED:
                // case HttpStatus.SC_ACCEPTED:
                // case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
                // case HttpStatus.SC_NO_CONTENT:
                // case HttpStatus.SC_RESET_CONTENT:
                // case HttpStatus.SC_PARTIAL_CONTENT:
                // case HttpStatus.SC_MULTI_STATUS:
                break;
            default:
                throw new IOException("服务器响应状态异常,失败.");
        }
    }

    private HttpClientUtils() {

    }

    public static void main(String[] args) throws Exception {
//        System.out.println(simpleGetInvoke("http://www.baidu.com", new HashMap<String, String>()));
//        System.out.println(simpleGetInvoke("http://www.baidu.com", new HashMap<String, String>()));
//        System.out.println(simpleGetInvoke("http://www.baidu.com", new HashMap<String, String>()));
//        System.out.println(simpleGetInvoke("http://www.baidu.com", new HashMap<String, String>()));
    }
}
