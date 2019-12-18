package com.zq.kyb.core.dao;

import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ZQUidUtils {

    //所有Redis Key的前缀都写在这里:
    private final static String KEY_OrderNo = "OrderNo";
    private final static String KEY_SellerNo = "SellerNo";
    private final static String KEY_AgentNo = "AgentNo";
    private final static String KEY_StoreNo = "StoreNo";
    private final static String KEY_MemberNo = "MemberNo";
    private final static String KEY_PayTransNo = "PayTransNo";
    private final static String KEY_PayReturnNo = "PayReturnNo";
    private final static String KEY_MemberCardNo = "MemberCardNo";
    private final static String KEY_FactorNo = "FactorNo";
    private final static String KEY_UserNo = "UserNo";

    public final static String KEY_PushNo = "PushNo";//充值订单编号


    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

    private final static NumberFormat memberFormat = new DecimalFormat("000000000");// 累计10亿客户
    private final static NumberFormat orderFormat = new DecimalFormat("0000000");// 每天产生最高1千万单
    private final static NumberFormat productStockInFormat = new DecimalFormat("000");//  累计入库次数最高1千万次
    private final static NumberFormat productStockOutFormat = new DecimalFormat("000");//  累计出库次数最高1千万次
    private final static NumberFormat MemberCardNoFormat = new DecimalFormat("00000000000000");//  
    private final static NumberFormat FactorNoFormat = new DecimalFormat("000000");//  
    private final static NumberFormat sellerNoFormat = new DecimalFormat("000000");//  
    private final static NumberFormat agentNoFormat = new DecimalFormat("000000");//  
    private final static NumberFormat storeNoFormat = new DecimalFormat("000");//  
    private final static NumberFormat payTnoFormat = new DecimalFormat("00000");//  
    private final static NumberFormat payReturnFormat = new DecimalFormat("00000");//  
    private final static NumberFormat userNoFormat = new DecimalFormat("000000");//  

    private final static NumberFormat pictureNumberFormat = new DecimalFormat();//  

    private static final FieldPosition HELPER_POSITION = new FieldPosition(0);

    static String entityName = "Increase";

    public static String genUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //因贵商对请求参数长度有要求所以只能截取满足条件的最大长度（30位）
        if(uuid.length()>30){
            return uuid.substring(0,29);
        }else{
            return uuid;
        }
    }

    public static String getSerialNo(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssSSS");
        String serialNo = sdf.format(new Date())+(int)((Math.random()*9+1)*10000);
        return serialNo;
    }

    /**
     * 产品编号生成规则，200开头
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generateProductNo() throws Exception {
        // 前三位 店内码
        // 020 ～ 029；040 ～ 049；200 ～ 299
        while (true) {
            String code = "200" + RandomStringUtils.randomNumeric(9);
            int length = code.length();
            int s1 = 0;
            int s2 = 0;
            int s3 = 0;
            for (int n = 0; n < length; n++) {
                if (n % 2 == 0) {
                    s1 = s1 + Integer.valueOf(code.substring(n, n + 1));
                } else {
                    s2 = s2 + Integer.valueOf(code.substring(n, n + 1));
                }
            }
            s3 = (s1 + s2 * 3) % 10;
            code = code + String.valueOf((s3 > 0) ? 10 - s3 : 0);
            // long count =
            // MongoDaoImpl.getInstance().findCount(CollectionName.ProductInfo,
            // new BasicDBObject("sn", code));
            // if (count > 0)
            // continue;
            return code;
        }
    }

    /**
     * 仓库条码生成规则,201开头
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generateStockNo() throws Exception {

        // 020 ～ 029；040 ～ 049；200 ～ 299
        while (true) {
            String code = "201" + RandomStringUtils.randomNumeric(9);
            int length = code.length();
            int s1 = 0;
            int s2 = 0;
            int s3 = 0;
            for (int n = 0; n < length; n++) {
                if (n % 2 == 0) {
                    s1 = s1 + Integer.valueOf(code.substring(n, n + 1));
                } else {
                    s2 = s2 + Integer.valueOf(code.substring(n, n + 1));
                }
            }
            s3 = (s1 + s2 * 3) % 10;
            code = code + String.valueOf((s3 > 0) ? 10 - s3 : 0);
            // long count =
            // MongoDaoImpl.getInstance().findCount(CollectionName.ProductStockPlace,
            // new BasicDBObject("_id", code));
            // if (count > 0)
            // continue;
            return code;
        }
    }

    /**
     * 入库单生成,202开头
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generateInStockNo() throws Exception {

        // 020 ～ 029；040 ～ 049；200 ～ 299
        while (true) {
            String code = "202" + RandomStringUtils.randomNumeric(9);
            int length = code.length();
            int s1 = 0;
            int s2 = 0;
            int s3 = 0;
            for (int n = 0; n < length; n++) {
                if (n % 2 == 0) {
                    s1 = s1 + Integer.valueOf(code.substring(n, n + 1));
                } else {
                    s2 = s2 + Integer.valueOf(code.substring(n, n + 1));
                }
            }
            s3 = (s1 + s2 * 3) % 10;
            code = code + String.valueOf((s3 > 0) ? 10 - s3 : 0);
            // long count =
            // MongoDaoImpl.getInstance().findCount(CollectionName.ProductStockIn,
            // new BasicDBObject("_id", code));
            // if (count > 0)
            // continue;
            return code;
        }
    }

    /**
     * 出库单生成,203开头
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generateOutStockNo() throws Exception {

        // 020 ～ 029；040 ～ 049；200 ～ 299
        while (true) {
            String code = "203" + RandomStringUtils.randomNumeric(9);
            int length = code.length();
            int s1 = 0;
            int s2 = 0;
            int s3 = 0;
            for (int n = 0; n < length; n++) {
                if (n % 2 == 0) {
                    s1 = s1 + Integer.valueOf(code.substring(n, n + 1));
                } else {
                    s2 = s2 + Integer.valueOf(code.substring(n, n + 1));
                }
            }
            s3 = (s1 + s2 * 3) % 10;
            code = code + String.valueOf((s3 > 0) ? 10 - s3 : 0);
            // long count =
            // MongoDaoImpl.getInstance().findCount(CollectionName.ProductStockOut,
            // new BasicDBObject("_id", code));
            // if (count > 0)
            // continue;
            return code;
        }
    }

    /**
     * 移库单生成,204开头
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generateMoveStockNo() throws Exception {

        // 020 ～ 029；040 ～ 049；200 ～ 299
        while (true) {
            String code = "204" + RandomStringUtils.randomNumeric(9);
            int length = code.length();
            int s1 = 0;
            int s2 = 0;
            int s3 = 0;
            for (int n = 0; n < length; n++) {
                if (n % 2 == 0) {
                    s1 = s1 + Integer.valueOf(code.substring(n, n + 1));
                } else {
                    s2 = s2 + Integer.valueOf(code.substring(n, n + 1));
                }
            }
            s3 = (s1 + s2 * 3) % 10;
            code = code + String.valueOf((s3 > 0) ? 10 - s3 : 0);
            // long count =
            // MongoDaoImpl.getInstance().findCount(CollectionName.ProductStockMove,
            // new BasicDBObject("_id", code));
            // if (count > 0)
            // continue;
            return code;
        }
    }


    /**
     * 客户编号生成规则
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generateMemberNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        memberFormat.format(JedisUtil.incr(KEY_MemberNo), sb, HELPER_POSITION);
        return sb.toString();
    }


    /**
     * 生成支付时候的交易号
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generatePayTno() throws Exception {
        StringBuffer sb = new StringBuffer();
        Calendar rightNow = Calendar.getInstance();
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
        String dateStr = sb.toString();
        payTnoFormat.format(JedisUtil.incr(KEY_PayTransNo + "_" + dateStr), sb, HELPER_POSITION);
        return sb.toString();
    }

    /**
     * 生成退款编号
     *
     * @return
     * @throws Exception
     */
    public static synchronized String generatePayReturnTno(String sellerId) throws Exception {
        if (StringUtils.isEmpty(sellerId)) {
            throw new UserOperateException(400, "sellerId is null");
        }
        StringBuffer sb = new StringBuffer();
        dateFormat.format(new Date().getTime(), sb, HELPER_POSITION);
        String dateStr = sb.toString();
        payReturnFormat.format(JedisUtil.incr(KEY_PayReturnNo + "_" + sellerId + "_" + dateStr), sb, HELPER_POSITION);
        return sb.toString();
    }


    public static synchronized String genPictureNumber() throws Exception {
        StringBuffer sb = new StringBuffer();
        pictureNumberFormat.format(JedisUtil.incr("PictureNumber"), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static synchronized String genProductStockOutNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        Calendar rightNow = Calendar.getInstance();
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
        productStockOutFormat.format(JedisUtil.incr("ProductStockOut") % 999, sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String B2C_ORDER = "1";// 终端客户订单
    public static String B2B_ORDER = "2";// B2B客户订单
    public static String CACH_ORDER = "3";// 终端客户充值到现金账户

    /**
     * 订单号生成规则,1位类别码+6位日期+7位序号=14位订单号
     *
     * @return
     * @throws Exception
     */

    public static synchronized String generateOrderNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        Calendar rightNow = Calendar.getInstance();
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
        String dateStr = sb.toString();
        orderFormat.format(JedisUtil.incr(KEY_OrderNo + "_" + dateStr), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String generatePushNo(String currentSellerId) {
        if (StringUtils.isEmpty(currentSellerId)) {
            throw new UserOperateException(400, "currentSellerId is null");
        }
        StringBuffer sb = new StringBuffer();
        Calendar rightNow = Calendar.getInstance();
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
        String dateStr = sb.toString();
        orderFormat.format(JedisUtil.incr(KEY_PushNo + "_" + currentSellerId + "_" + dateStr), sb, HELPER_POSITION);
        return CACH_ORDER + sb.toString();
    }


    public static String generateSellerNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        sellerNoFormat.format(JedisUtil.incr(KEY_SellerNo), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String generateFactorNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        FactorNoFormat.format(JedisUtil.incr(KEY_FactorNo), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String generateMemberCardNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        MemberCardNoFormat.format(JedisUtil.incr(KEY_MemberCardNo), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String generateStoreNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        storeNoFormat.format(JedisUtil.incr(KEY_StoreNo), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String generateAgentNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        agentNoFormat.format(JedisUtil.incr(KEY_AgentNo), sb, HELPER_POSITION);
        return sb.toString();
    }

    public static String generateUserNo() throws Exception {
        StringBuffer sb = new StringBuffer();
        userNoFormat.format(JedisUtil.incr(KEY_UserNo), sb, HELPER_POSITION);
        return sb.toString();
    }
}
