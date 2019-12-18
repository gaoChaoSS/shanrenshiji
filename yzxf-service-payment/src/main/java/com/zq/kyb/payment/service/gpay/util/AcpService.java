package com.zq.kyb.payment.service.gpay.util;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zq2014 on 18/11/19.
 */
public class AcpService {
    private static final Logger logger = Logger.getLogger(AcpService.class);
    /**
     * 功能：前台交易构造HTTP POST自动提交表单<br>
     * @param reqUrl 表单提交地址<br>
     * @param hiddens 以MAP形式存储的表单键值<br>
     * @param encoding 上送请求报文域encoding字段的值<br>
     * @return 构造好的HTTP POST交易表单<br>
     */
    public static String createAutoFormHtml(String reqUrl, Map<String, String> hiddens, String encoding) {
        StringBuffer sf = new StringBuffer();
        sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
        sf.append("<form id = \"pay_form\" action=\"" + reqUrl
                + "\" method=\"post\">");
        if (null != hiddens && 0 != hiddens.size()) {
            Set<Map.Entry<String, String>> set = hiddens.entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> ey = it.next();
                String key = ey.getKey();
                String value = ey.getValue();
                if (StringUtils.areNotEmpty(key, value)){
                    sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
                            + key + "\" value=\"" + value + "\"/>");
                }
            }
        }
        sf.append("</form>");
        sf.append("</body>");
        sf.append("<script type=\"text/javascript\">");
        sf.append("document.all.pay_form.submit();");
        sf.append("</script>");
        sf.append("</html>");
        return sf.toString();
    }

    /**
     * 功能：后台交易提交请求报文并接收同步应答报文<br>
     * @param reqData 请求报文<br>
     * @param reqUrl  请求地址<br>
     * @param encoding<br>
     * @return 应答http 200返回true ,其他false<br>
     */
    public static Map<String,String> post(
            Map<String, String> reqData,String reqUrl,String encoding) {
        Map<String, String> rspData = new HashMap<String,String>();
        logger.info("请求地址:" + reqUrl);
        //发送后台请求数据
        //reqUrl="http://lzftest.gygscb.com/IMP-CPos/b2c/pay/consumeQuery.do";
        HttpClient hc = new HttpClient(reqUrl, 300000, 300000);//连接超时时间，读超时时间（可自行判断，修改）
        try {
            int status = hc.send(reqData, encoding);
            if (200 == status) {
                String resultString = hc.getResult();
                if (null != resultString && !"".equals(resultString)) {
                    // 将返回结果转换为map
                    Map<String,String> tmpRspData  = SDKUtil.convertResultStringToMap(resultString);
                    rspData.putAll(tmpRspData);
                }
            }else{
                logger.info("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rspData;
    }

    /**
     * 校验签名是否有效
     * @param param
     * @param publicKey
     * @param privateKey
     * @param encoding
     * @return
     */
    public static boolean validate256Sign(Map param, String publicKey, String privateKey, String encoding){

        boolean flag = false;
        try{
            String sign = String.valueOf(param.get("sign"));
            logger.info("sign=" + sign);
            String content = Signature.getSignCheckContentV1(param);
            String respcode = String.valueOf(param.get("respcode"));
            if (respcode.equals("00000")){
                //交易成功则需要校验安全验证码
                content = content + "&" + privateKey;
            }
            logger.info("待验签字符串="+content);
            if (!StringUtils.isEmpty(publicKey)){
                flag = Signature.rsa256CheckContent(content, sign, publicKey, encoding);
            } else {
                throw new RuntimeException("RSA public key invalid.");
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }
}
