package com.zq.kyb.payment.config;

/**
 * 贵商银行支付配置信息
 * Created by zq2014 on 18/11/12.
 */
public class GpayConfig {
    //以下地址均为测试地址(非生产环境地址,上生产环境时需要切换地址)
    //消费类交易地址  http://http://lzftest.gygscb.com/IMP-CPos/b2c/pay/consume.do
    public static final String FRONT_TRANS_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/consume.do";
    //退款(撤销)类交易地址
    public static final String BACK_TRANS_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/refund.do";
    //查询单笔消费地址
    public static final String SINGLE_QUERY_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/consumeQuery.do";
    //查询单笔退款地址
    public static final String REFUND_QUERY_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/refundQuery.do";
    //对账文件地址
    public static final String FILE_TRANS_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/downloadBill.do";
    //商户绑定地址
    public static final String BIND_TRANS_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/bindECP.do";
    //商户解绑地址
    public static final String UNBIND_TRANS_URL = "https://lzftest.gygscb.com/IMP-CPos/b2c/pay/unbindECP.do";
    //商户开户地址
    public static final String ADDBUSI_URL="https://lzftest.gygscb.com/IMP-CPos/b2c/pay/addBusi.do";
    //商户开户查询地址
    public static final String QUERYBUSISTATUS_URL="https://lzftest.gygscb.com/IMP-CPos/b2c/pay/queryBusiStatus.do";
    //图片上传地址
    public static final String UPLOADPIC_URL ="https://lzftest.gygscb.com/IMP-CPos/b2c/pay/uploadPic.do";
    //确认收货地址
    public static final String CFRECV_URL ="https://lzftest.gygscb.com/IMP-CPos/b2c/pay/cfrecv.do";
    //扫码支付子代码
    public static final String APPLYQRCODE_URL ="https://lzftest.gygscb.com/IMP-CPos/b2c/pay/applyQrcode.do";

    //版本号
    public static final String VERSION = "1.0.0";
    //编码方式
    public static final String ENCODE = "UTF-8";
    //证书ID
    public static final String CERTID = "GLPH081202000001";
    //交易代码
    public static final String TRTYPE = "GP00011";
    //开户代码
    public static final String KHTYPE = "GP00062";
    //开户子代码
    public static final String SUBCODE_ADDBUSI = "guokai.gpay.addbusi";
    //支付子代码
    public static final String SUBCODE_CONSUME = "guokai.gpay.consume";
    //支付查询子代码
    public static final String SUBCODE_CONSUME_QUERY = "guokai.gpay.consumequery";
    //撤销/退款交易子代码
    public static final String SUBCODE_REFUND = "guokai.gpay.refund";
    //撤销/退款查询子代码
    public static final String SUBCODE_REFUND_QUERY = "guokai.gpay.refundquery";
    //平台商户绑定子代码
    public static final String SUBCODE_BINDECP = "guokai.gpay.bindecp";
    //平台商户解绑子代码
    public static final String SUBCODE_UNBINDECP = "guokai.gpay.unbindecp";
    //对账子代码
    public static final String SUBCODE_BILL = "guokai.gpay.downloadbill";
    //商户查询子代码
    public static final String SUBCODE_QUERYBUSISTATUS = "guokai.gpay.querybusistatus";
    //图片上传子代码
    public static final String SUBCODE_UPLOADPIC = "guokai.gpay.uploadpic";
    //确认收货子代码
    public static final String SUBCODE_CFRECV ="guokai.gpay.cfrecv";
    //扫码支付子代码
    public static final String SUBCODE_APPLYQRCODE = "guokai.gpay.applyQrcode";
    //货币代码
    public static final String CURRENCY = "156";
    //签名方式
    public static final String SIGN_TYPE = "RSA";
    //是否验证https证书，测试环境请设置false，生产环境建议优先尝试true，不行再false。非true的值默认都当false处理。
    public static final Boolean IF_VALIDATE_REMOTE_CERT=false;
    //签名方法
    public static final String SIGN_METHOD ="01";

    //公钥
    public static final String PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlu0DhvPUYA1GFZr5lAhDaSMgZ/MaTrDuLJvcbUZgfNVUuR6PKdQPZwTRS+NoH7jef4eoUYOfdMI5m8Ewq14KtHOoNoGLoSlcso8WO4SgSXEceh6GmJrF4z3o45ncMH1yzcu7Prm/J/A9V+yYbxC5LW5HUybLUt5lfwjIIiPOJpxWI+PzBIRNX2VHi3fUZp3dKbFNdl/zkWe/RHBT2VIdc6XNAb3J0Nyy42cSAajzhoZGV5dTy3UN/gT0wkzMLUjfrmdf/El0LXf1hSUbpqZZzX1GqzSd3onzmlwqCzkkONrK9JIZM1QqJG8/q5CejPhDdLMvGPsNFZG3FKPWC+qRDQIDAQAB";
    //私钥
    public static final String PRIVATE_KEY="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDA7HglcvDgABHnUWxjkzme9AuyKBc2N30XLthZfYsX8dfl816mBpF3O9RgFfezC8Rlbkl6mwtfYOuqMVL5p9C8vcwPJOsQhjM3Am2xKiqA5O6JYqt4H1z9hkiXmazegzqo1F964skuLF0vlrWZXmo496VMHOTDncEx24+/q85g199ofJCH1X60c4LbUKvyjhR6I9rNF1dHLBW9nzyJj/uc2VQeYZiVR4yTBRjS9EaenVG+jLx/TXyRmxKeMRCxgL5sGzVWef4Y/tfrMgvkInQKe1xfwpI/CqoKrEWPMb4VQt33y12wpbaQgf9q1hpgQWOTvd0HvnsQGzrJJ5KZ/4otAgMBAAECggEAWZMx8cldd9PwfCO9HLq17UzIxW6B4IWBCiuQ/nQhCfwbT0RhdNrl3aOk5vwsJzDWfnXnngqxDBb3NO1z5kD51TiWr41nqyN0Uh1JixHV7ETfUGDE1qBRT9FykRkkP6hUqyD4OBlmaY7lsXvbU5uX3F13nVBpEz6C/kBAjTEbQLEMPRUAqx2ApdFz8kGsoO/9vTCfERxhPAXhvKUEZH2j0N3goyaYhCWHLM+Y8G3dz4EHwAoG60H5xDN2xuBBDUZ/nJI0vMPPlBGIQFY5jcS8YClWpV16gxxRkKwu9pS633bDA05JVaAdFV+D4fx0A5/pSOoJN7IbYoPbWU5X0JTHwQKBgQDiTbe1+oGS/eps3uJQSUsyIfrqIOXmKgVh2M1apZA0D6TqywvBL82Y2Qqcd63Hcj4jVXJiLf0tvMqAcv4KmjketD+o8o8vGooLkqTRGl5XQlRD84LktuNo6Jwer5BUbG1wYLqzMi4fa0l3b13krfLK4fWhKEnElYP5ZCAa6e71nQKBgQDaPWlqJQWMB2rgbmFcY1GNAEhnFpbD9ByIi6TSaAhs7jwYwPSBZo2I7bzAybKvsCnIG4lljhcdsHfwnnL4yRWgUJ91wEd7zG5zvbKPJUfRMRDJIe94FlwzSQjLI8/w/WVnlScfllbs0c8dIfTS0Ur2eddcASW441+A4up3gNiJ0QKBgFAIHiUsT3C1fZc9B5pPIVm8bKkqM0O/rqGY857QGHxg3/jtD94lUrdwYnFNXdbADzudt1MDYpsvPgpJIJCNVBAIvM654WtOHm3TUZhlk+GWIojZcHwENc5fHP85JXjF07o/ayd+YpNX9OZZK0J5Rfj8CksRHW15Vu/2uefqvUh9AoGAWQt9m2WHod7U6MWgzAVqHNHkuMsqsMFFcyEnGwJ/jZKWyrLQEIw7a4c04KRrV+vU4GT75ofXPrHl/jNoTcIeJM9AgNb3U7fRyT+5P1bCusP+SVKjVqgo7nP6NohiK734Rg9Ba034IaBplUVpkyr6Hx8Pk+aT8aA0M25ipGfZ8kECgYBoiT8JbQnJ7iNvPln8robXkh6T3tqKAEKpkkw571LHlyvR9TB3f9e9HFGCnGSSZ0y3PBbvg78dk3JKtqykIIl9zduY5mXVRq/TSdPQqCPS1adjYgBtcRbI6E/nAEwK9uT4KH1x8V4y/o1VNvxYmI9CAc2QeOPFCYtFk6zdkOOpjw==";
    //业务API钥
    public static final String BUSI_API_KEY="1234567890ABCDEFGHIJ1234567890AB";
}
