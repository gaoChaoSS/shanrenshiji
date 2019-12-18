package com.zq.kyb.common.sms;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SendSMSCF {
    
  public static Map<String,String>   errorMsg = new HashMap<String, String>(); 
    
    static{
        errorMsg.put("0", "发送成功");
        errorMsg.put("-1", "提交接口错误");
        errorMsg.put("-3", "用户名或密码错误");
        errorMsg.put("-4", "短信内容和备案的模板不一样");
        errorMsg.put("-5", "签名不正确（格式为:XXXX【签名内容】）注意，短信内容最后一个字符必须是】");
        errorMsg.put("-7", "余额不足");
        errorMsg.put("-8", "通道错误");
        errorMsg.put("-9", "无效号码");
        errorMsg.put("-10", "签名内容不符合长度");
        errorMsg.put("-11", "用户有效期过期");
        errorMsg.put("-12", "黑名单");
        errorMsg.put("-13", "语音验证码的 Amount 参数必须是整形字符串");
        errorMsg.put("-14", "语音验证码的内容只能为数字");
        errorMsg.put("-15", "语音验证码的内容最长为 6 位");
        errorMsg.put("-16", "余额请求过于频繁，5 秒才能取余额一次");
        
        
    }
    
    /** 发送短信 **/
    public static Map<String, Object> send(Properties prop,String mob, String msg) {
        String str = "";
         Map<String,Object> re = new HashMap<String, Object>();
        try {
           
            // 创建HttpClient实例     
            HttpClient httpclient =new DefaultHttpClient();
           
             //构造一个post对象
             HttpPost httpPost = new HttpPost("http://h.1069106.com:1210/Services/MsgSend.asmx/SendMsg");
             //添加所需要的post内容
             List<NameValuePair> nvps = new ArrayList<NameValuePair>();
             nvps.add(new BasicNameValuePair("userCode", prop.getProperty("chufa_name") ) );
             nvps.add(new BasicNameValuePair("userPass", prop.getProperty("chufa_pwd") ) );
             nvps.add(new BasicNameValuePair("DesNo", mob));
             nvps.add(new BasicNameValuePair("Msg", msg));
             nvps.add(new BasicNameValuePair("Channel", "0"));
              

             httpPost.setEntity( new UrlEncodedFormEntity(nvps, "UTF-8") );
             HttpResponse response = httpclient.execute(httpPost);
             HttpEntity entity = response.getEntity();
             if (entity != null) {    
                 InputStream instreams = entity.getContent();    
                 str = convertStreamToString(instreams);  
                 Logger.getLogger(SendSMSCF.class).info(str);
             }
            
       
            Document doc = null;
            doc = DocumentHelper.parseText(str); // 将字符串转为XML

            if (doc == null ) { 
                re.put("code",false);
                return re;
             }
            Element rootElt = doc.getRootElement(); // 获取根节点
            if (rootElt == null ) {
                re.put("code",false);
                return re;
            }
            String re_msg = rootElt.getText();
            //Logger.getLogger(SendSMSCF.class).info("根节点：" + rootElt.getName()); // 拿到根节点的名称
            Logger.getLogger(SendSMSCF.class).info("根节点的值：" + rootElt.getText()); // 拿到根节点的名称
            System.out.println("根节点的值：" + rootElt.getText());

            if (rootElt.getText() == null || "".equals(rootElt.getText())){
                re.put("code",false);
                re.put("status", re_msg);
                re.put("msg", "发送失败");
                return re;
            }
            if (Long.parseLong(rootElt.getText()) > 0 ) {
                re.put("code",true);
                re.put("status", "0");
                re.put("msg", "发送成功！");
                return re;
            } else {
                re.put("code",false);
                re.put("msg", errorMsg.get(re_msg));
                return re;
            }
           
        } catch (DocumentException e) {
            e.printStackTrace();
            re.put("code",false);
            return re;
        } catch (Exception e) {
            e.printStackTrace();
            re.put("code",false);
            return re;
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
