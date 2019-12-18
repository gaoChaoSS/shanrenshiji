package com.zq.kyb.payment.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/* *
 *类名：HttpProtocolHandler
 *功能：HttpClient方式访问
 *详细：获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class HttpProtocolHandler {

    private static String DEFAULT_CHARSET = "UTF-8";

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int defaultConnectionTimeout = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int defaultSoTimeout = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int defaultIdleConnTimeout = 60000;

    private int defaultMaxConnPerHost = 30;

    private int defaultMaxTotalConn = 80;

    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒 */
    private static final long defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager connectionManager;

    private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();

    /**
     * 工厂方法
     * 
     * @return
     */
    public static HttpProtocolHandler getInstance() {
        return httpProtocolHandler;
    }

    /**
     * 私有的构造方法
     */
    private HttpProtocolHandler() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new HttpConnectionManager();
        // connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        // connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);
        //
        // IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        // ict.addConnectionManager(connectionManager);
        // ict.setConnectionTimeout(defaultIdleConnTimeout);
        //
        // ict.start();

    }

    /**
     * 执行Http请求
     * 
     * @param request
     *            请求数据
     * @param strParaFileName
     *            文件类型的参数名
     * @param strFilePath
     *            文件路径
     * @return
     * @throws HttpException
     *             , IOException
     */
    public HttpResponse execute(HttpRequest request, String strParaFileName, String strFilePath) throws HttpException, IOException {

        HttpClient httpclient = HttpConnectionManager.getHttpClient();

        // 设置连接超时
        int connectionTimeout = defaultConnectionTimeout;
        if (request.getConnectionTimeout() > 0) {
            connectionTimeout = request.getConnectionTimeout();
        }
        httpclient.getParams().setIntParameter("connectionTimeout", connectionTimeout);

        // 设置回应超时
        int soTimeout = defaultSoTimeout;
        if (request.getTimeout() > 0) {
            soTimeout = request.getTimeout();
        }
        httpclient.getParams().setIntParameter("soTimeout", soTimeout);

        // 设置等待ConnectionManager释放connection的时间
        httpclient.getParams().setLongParameter("ConnectionManagerTimeout", defaultHttpConnectionManagerTimeout);

        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;
        HttpRequestBase method = null;

        // get模式且不带上传文件
        if (request.getMethod().equals(HttpRequest.METHOD_GET)) {
            method = new HttpGet(request.getUrl() + request.getQueryString());
            method.getParams().setParameter("credentialCharset", charset);
            // parseNotifyConfig会保证使用GET方法时，request一定使用QueryString
        } else if (strParaFileName.equals("") && strFilePath.equals("")) {
            // post模式且不带上传文件
            method = new HttpPost(request.getUrl());
            NameValuePair[] nv = request.getParameters();
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(nv.length);
            if (nv != null)
                for (NameValuePair n : nv) {
                    formParams.add(n);
                }
            HttpEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
            ((HttpPost) method).setEntity(entity);
        } else {
            // post模式且带上传文件
            method = new HttpPost(request.getUrl());
            MultipartEntity entity = new MultipartEntity();
            for (int i = 0; i < request.getParameters().length; i++) {
                entity.addPart(request.getParameters()[i].getName(), new StringBody(request.getParameters()[i].getValue(), Charset.forName("UTF-8")));
            }
            // 增加文件参数，strParaFileName是参数名，使用本地文件
            entity.addPart("strParaFileName",new FileBody(new File(strFilePath)));
            // 设置请求体
            ((HttpPost) method).setEntity(entity);
        }

        // 设置Http Header中的User-Agent属性
        method.addHeader("User-Agent", "Mozilla/4.0");
        HttpResponse response = new HttpResponse();

        HttpEntity responseEntity = null;
        
        try {
            org.apache.http.HttpResponse httpResponse = httpclient.execute(method);
            responseEntity = httpResponse.getEntity();
            if (request.getResultType().equals(HttpResultType.STRING)) {
                response.setStringResult(EntityUtils.toString(responseEntity, "UTF-8"));
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                response.setByteResult(EntityUtils.toByteArray(responseEntity));
            }
            response.setResponseHeaders(method.getAllHeaders());
        } catch (UnknownHostException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        } finally {
//            method.releaseConnection();
            responseEntity.consumeContent();
        }
        return response;
    }
    /**
     * 将NameValuePairs数组转变为字符串
     * 
     * @param nameValues
     * @return
     */
    protected String toString(NameValuePair[] nameValues) {
        if (nameValues == null || nameValues.length == 0) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nameValues.length; i++) {
            NameValuePair nameValue = nameValues[i];

            if (i == 0) {
                buffer.append(nameValue.getName() + "=" + nameValue.getValue());
            } else {
                buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
            }
        }

        return buffer.toString();
    }
}
