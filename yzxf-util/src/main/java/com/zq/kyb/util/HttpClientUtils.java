package com.zq.kyb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import net.sf.json.JSONObject;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpClientUtils {

    @SuppressWarnings("deprecation")
    private DefaultHttpClient httpclient = null;

    public HttpClientUtils() {
        setHttpclient((DefaultHttpClient) createHttpClient());
    }

    public JSONObject getJSON(String url, String a) {
        return null;
    }

    public JSONObject getJSON(String url) {
        Logger.getLogger(HttpClientUtils.class.getName()).info("---reqest url:" + url);
        DefaultHttpClient client = getHttpclient();
        CloseableHttpResponse response = null;
        try {
            if (url.toLowerCase().startsWith("https")) {
                setHttps(client);
            }

            // 创建一个本地上下文信息
            HttpContext localContext = new BasicHttpContext();
            // 在本地上下问中绑定一个本地存储
            //localContext.setAttribute(ClientContext.COOKIE_STORE, httpclient.getCookieStore());

            HttpGet httpget = new HttpGet(url);
            setRequestHeader(httpget);
            response = client.execute(httpget, localContext);
            String str = EntityUtils.toString(response.getEntity(), "utf-8");
            //Logger.getLogger(HttpClientUtils.class.getName()).info("re content: " + str);
            JSONObject re = null;
            if (str.startsWith("{")) {
                re = JSONObject.fromObject(str);
            }
            Logger.getLogger(HttpClientUtils.class.getName()).info("re statusCode:" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                return re;
            } else {
                if (str.startsWith("{")) {
                    return JSONObject.fromObject(str);
                }
                throw new RuntimeException("request error " + response.getStatusLine().toString() + "\nresponse:" + re);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    public static byte[] simpleGetInvokeByte(String url) throws Exception {
        URL u = new URL(url);
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            setBaseReqHeader(connection);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                Logger.getLogger(HttpClientUtils.class.getName()).info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            InputStream inputStream = connection.getInputStream();
            return FileExecuteUtils.getInstance().readInputStreamByByte(inputStream);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void setBaseReqHeader(URLConnection connection) {
        // 设置通用的请求属性
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    }

    private void setHttps(DefaultHttpClient client) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("SSL");
        // Implementation of a trust manager for X509 certificates
        X509TrustManager tm = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {

            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
        //这里需要忽略掉HostName的比较，否则访问一些网站时，会报异常
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = client.getConnectionManager();
        // register https protocol in httpclient's scheme registry
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }

    public JSONObject postFrom(String url, Map data) {
        CloseableHttpResponse response = null;
        HttpPost login = new HttpPost(url);
        UrlEncodedFormEntity postEntity;
        try {
            postEntity = new UrlEncodedFormEntity(getParam(data), "UTF-8");
            postEntity.setContentType("application/x-www-form-urlencoded");
            login.setEntity(postEntity);
            // 创建一个本地上下文信息
            HttpContext localContext = new BasicHttpContext();
            // 在本地上下问中绑定一个本地存储
            localContext.setAttribute(ClientContext.COOKIE_STORE, httpclient.getCookieStore());
            DefaultHttpClient httpclient = getHttpclient();
            if (url.toLowerCase().startsWith("https")) {
                setHttps(httpclient);
            }
            response = httpclient.execute(login, localContext);
            if (response.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(response.getEntity());
                if (str.startsWith("{")) {
                    return JSONObject.fromObject(str);
                }
                return null;
            } else {
                throw new RuntimeException("request error " + response.getStatusLine().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    public JSONObject postData(String url, Map<String, Object> deviceJson, List<BasicClientCookie> cookies) throws Exception {
        Logger.getLogger(HttpClientUtils.class.getName()).info("---reqest url:" + url);

        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        DefaultHttpClient client = getHttpclient();
        if (url.toLowerCase().startsWith("https")) {
            setHttps(client);
        }
        try {
            if (deviceJson != null) {
                StringEntity deviceEntity = new StringEntity(deviceJson.toString(), "utf-8");// 解决中文乱码问题
                deviceEntity.setContentEncoding("UTF-8");
                deviceEntity.setContentType("application/json");
                httpPost.setEntity(deviceEntity);
            }
            if (cookies != null && cookies.size() > 0) {
                BasicCookieStore cookisStore = new BasicCookieStore();
                for (BasicClientCookie cookie : cookies) {
                    cookisStore.addCookie(cookie);
                }
                client.setCookieStore(cookisStore);
            }

            response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(response.getEntity());
                if (str.startsWith("{")) {
                    return JSONObject.fromObject(str);
                }
                return new JSONObject();
            } else {
                throw new RuntimeException("request error " + response.getStatusLine().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public JSONObject putData(String url, Map<String, Object> deviceJson, List<BasicClientCookie> cookies) {
        Logger.getLogger(HttpClientUtils.class.getName()).info("---reqest url:" + url);

        CloseableHttpResponse response = null;
        HttpPut httpPut = new HttpPut(url);
        try {
            StringEntity deviceEntity = new StringEntity(deviceJson.toString(), "utf-8");// 解决中文乱码问题
            deviceEntity.setContentEncoding("UTF-8");
            deviceEntity.setContentType("application/json");
            httpPut.setEntity(deviceEntity);
            if (cookies != null && cookies.size() > 0) {
                BasicCookieStore cookisStore = new BasicCookieStore();
                for (BasicClientCookie cookie : cookies) {
                    cookisStore.addCookie(cookie);
                }
                getHttpclient().setCookieStore(cookisStore);
            }

            response = getHttpclient().execute(httpPut);
            if (response.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(response.getEntity());
                if (str.startsWith("{")) {
                    return JSONObject.fromObject(str);
                }
                return new JSONObject();
            } else {
                throw new RuntimeException("request error " + response.getStatusLine().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    public InputStream getResponseContent(String url) {
        Logger.getLogger(HttpClientUtils.class.getName()).info("---reqest url:" + url);
        CloseableHttpResponse response = null;
        InputStream is = null;
        try {
            HttpGet httpget = new HttpGet(url);
            setRequestHeader(httpget);
            response = getHttpclient().execute(httpget);
            if (response.getStatusLine().getStatusCode() == 200) {
                is = response.getEntity().getContent();
                return is;
            } else {
                throw new RuntimeException("request error " + response.getStatusLine().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Document getHTML(String url, String baseUri) {
        Logger.getLogger(HttpClientUtils.class.getName()).info("---reqest url:" + url);

        CloseableHttpResponse response = null;
        Document doc = null;
        InputStream is = null;
        try {
            HttpGet httpget = new HttpGet(url);
            setRequestHeader(httpget);
            response = getHttpclient().execute(httpget);
            if (response.getStatusLine().getStatusCode() == 200) {
                is = response.getEntity().getContent();
                doc = Jsoup.parse(is, "UTF-8", baseUri);
                return doc;
            } else {
                throw new RuntimeException("request error " + response.getStatusLine().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    public void closeClient() {
        getHttpclient().close();
    }

    private static void setRequestHeader(HttpRequestBase httpget) {
        httpget.setHeader("Accept", "*/*");
        // httpget.setHeader("Accept-Encoding", "gzip, deflate");
        httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        httpget.setHeader("Cache-Control", "no-cache");
        httpget.setHeader("Connection", "keep-alive");
        httpget.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
    }

    private static HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 433));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        return new DefaultHttpClient(conMgr, params);
    }

    public static List<NameValuePair> getParam(Map parameterMap) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        Iterator it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry parmEntry = (Entry) it.next();
            param.add(new BasicNameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue()));
        }
        return param;
    }

    public DefaultHttpClient getHttpclient() {
        return httpclient;
    }

    public void setHttpclient(DefaultHttpClient httpclient) {
        this.httpclient = httpclient;
    }
}
