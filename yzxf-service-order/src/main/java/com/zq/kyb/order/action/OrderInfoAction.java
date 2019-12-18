package com.zq.kyb.order.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.order.service.AgentMoneyAccountService;
import com.zq.kyb.order.service.FactorMoneyAccountService;
import com.zq.kyb.order.util.MD5Util;
import com.zq.kyb.order.util.XmlUtils;
import com.zq.kyb.util.BigDecimalUtil;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class OrderInfoAction extends BaseActionImpl {
    public static final int CANCEL_STATUS_NO = 0;//未取消
    public static final int CANCEL_STATUS_YES = -1;//已取消

    public static int PAY_TYPE_ONLINE = 0;//在线支付
    public static int PAY_TYPE_OFFLINE = 1;//线下付款
    public static int PAY_TYPE_BOTH = 2;//线上线下混合支付

    public static int SEND_TYPE_HOME = 0;//送货上门
    public static int SEND_TYPE_STORE = 1;//店上自提
    public static int SEND_TYPE_KUAIDI = 2;//快递发货


    //{_id: '0', name: '未配送'},
    //{_id: '1', name: '配送中'},
    //{_id: '2', name: '配送完成'}];

    public static int SEND_STATUS_NO = 0;//未配送
    public static int SEND_STATUS_SENDING = 1;//配送中
    public static int SEND_STATUS_DONE = 2;//配送完成

    public static int PAY_STATUS_NO = 0;//未支付
    public static int PAY_STATUS_PART = 1;//部分支付
    public static int PAY_STATUS_DONE = 2;//支付完成


//    {_id: '0', name: '草稿'},
//    {_id: '1', name: '已预订'},
//    {_id: '2', name: '商家已确认'},
//    {_id: '3', name: '商家打包中'},
//    {_id: '4', name: '商家已发货'},
//    {_id: '5', name: '买家已收货'},
//    {_id: '6', name: '买家申请退换货'},
//    {_id: '7', name: '退换货中'},
//    {_id: '8', name: '退换货完成'},
//    {_id: '9', name: '已退款'},
//    {_id: '100', name: '完结'}

    public static int ORDER_TYPE_CAOGAO = 0;//草稿
    public static int ORDER_TYPE_BOOKING = 1;//已预订
    public static int ORDER_TYPE_CONFIRM = 2;//商家已确认
    public static int ORDER_TYPE_BALE = 3;//商家打包制作中
    public static int ORDER_TYPE_SENT = 4;//商家已发货
    public static int ORDER_TYPE_REVICED = 5;//买家已收货
    public static int ORDER_TYPE_REQ_RETURN = 6;//买家申请退换货
    public static int ORDER_TYPE_RETURNING = 7;//退换货中
    public static int ORDER_TYPE_RETURNED = 8;//退换货完成
    public static int ORDER_TYPE_RETURN_MONEY = 9;//已退款
//    public static int ORDER_TYPE_RETURN_FAIL = 10;//退款失败(临时添加的)
    public static int ORDER_TYPE_END = 100;//完结

    public static Map<Integer, String> OrderTypeMap = new HashMap<>();

    static {
        OrderTypeMap.put(ORDER_TYPE_CAOGAO, "草稿");
        OrderTypeMap.put(ORDER_TYPE_BOOKING, "已预订");
        OrderTypeMap.put(ORDER_TYPE_CONFIRM, "商家已确认");
        OrderTypeMap.put(ORDER_TYPE_BALE, "打包制作中");
        OrderTypeMap.put(ORDER_TYPE_SENT, "商家已发货");
        OrderTypeMap.put(ORDER_TYPE_REVICED, "买家已收货");
        OrderTypeMap.put(ORDER_TYPE_REQ_RETURN, "买家申请退换货");
        OrderTypeMap.put(ORDER_TYPE_RETURNING, "退换货中");
        OrderTypeMap.put(ORDER_TYPE_RETURNED, "退换货完成");
        OrderTypeMap.put(ORDER_TYPE_RETURN_MONEY, "已退款");
        OrderTypeMap.put(ORDER_TYPE_END, "完结");

    }


    // 1：积分，2：优惠券，3：现金账户，4：支付宝，5：pos刷卡, 6：现金收款，7：银行转账，8：其他, 9:产品卡, 10:微信, 11:活动折扣 12:美团在线
    public static int PAY_TYPE_SCORE = 1;//积分
    public static int PAY_TYPE_COUPON = 2;//优惠券
    public static int PAY_TYPE_CASH = 3;//现金账户
    public static int PAY_TYPE_ALIPAY = 4;//支付宝
    //public static final int PAY_TYPE_ALIPAY_MOBILE = 4000;//仅用于支付回调区别为mobile
    public static int PAY_TYPE_POS = 5;//pos刷卡
    public static int PAY_TYPE_GET_CASH = 6;//现金收款
    public static int PAY_TYPE_BANK = 7;//银行转账
    public static int PAY_TYPE_OTHER = 8;//其他
    public static int PAY_TYPE_PRODUCT_CARD = 9;//产品卡
    public static int PAY_TYPE_WECHAT = 10;//微信
    public static int PAY_TYPE_PARTY_DISCOUNT = 11;//活动折扣
    public static int PAY_TYPE_MEITUAN_ONLINE = 12;//美团在线
    public static int PAY_TYPE_SAOBEI_WECHAT = 13;//扫呗支付（微信）
    public static int PAY_TYPE_SAOBEI_ALIPAY = 14;//扫呗支付（支付宝）
    public static int PAY_TYPE_SAOBEI_QPAY = 15;//扫呗支付（QQ钱包）
    public static int PAY_TYPE_PENSION = 16;//养老金
    public static int PAY_TYPE_TEAM = 17;//团队收益
    public static int PAY_TYPE_GPAY = 18;//贵商银行
    public static Map<Integer, String> PayTypeMap = new HashMap<>();

    static {
        PayTypeMap.put(PAY_TYPE_SCORE, "积分");
        PayTypeMap.put(PAY_TYPE_COUPON, "优惠券");
        PayTypeMap.put(PAY_TYPE_CASH, "现金账户");
        PayTypeMap.put(PAY_TYPE_ALIPAY, "支付宝");
        PayTypeMap.put(PAY_TYPE_POS, "pos刷卡");
        PayTypeMap.put(PAY_TYPE_GET_CASH, "现金收款");
        PayTypeMap.put(PAY_TYPE_BANK, "银行转账");
        PayTypeMap.put(PAY_TYPE_OTHER, "其他");
        PayTypeMap.put(PAY_TYPE_PRODUCT_CARD, "产品卡");
        PayTypeMap.put(PAY_TYPE_WECHAT, "微信支付");
        PayTypeMap.put(PAY_TYPE_PARTY_DISCOUNT, "活动折扣");
        PayTypeMap.put(PAY_TYPE_MEITUAN_ONLINE, "美团在线");
        PayTypeMap.put(PAY_TYPE_SAOBEI_WECHAT, "扫呗支付（微信）");
        PayTypeMap.put(PAY_TYPE_SAOBEI_ALIPAY, "扫呗支付（支付宝）");
        PayTypeMap.put(PAY_TYPE_SAOBEI_QPAY, "扫呗支付（QQ钱包）");
        PayTypeMap.put(PAY_TYPE_GPAY, "贵商银行");
    }


    public static String ORDER_BOOKING_TYPE_MEMBER = "member";//客户自己预订
    public static String ORDER_BOOKING_TYPE_USER = "user";//商家用户帮客户预订


    public static String ORDER_FROM_POS = "pos";//实体店收银机
    public static String ORDER_FROM_MEITUAN = "meituan";//美团
    public static String ORDER_FROM_WECHAT = "wechat";//微信
    public static String ORDER_FROM_IOS = "ios_app";//微信
    public static String ORDER_FROM_ANDROID = "android_app";//安卓
    public static String ORDER_FROM_PC_WEB = "pc_web";//pc浏览器
    public static String ORDER_FROM_MOBILE_WEB = "mobile_web";//移动浏览器
    public static String ORDER_FROM_PHONE = "phone";//电话订单
    public static String ORDER_FROM_OTHER = "other";//其他

    public static String PAY_ADMIN_ID = "001";//平台账户ID

    //订单类型
    public static String TYPE_SCAN= "0"; //会员扫码
    public static String TYPE_CASH= "1"; //现金交易
    public static String TYPE_OTHER= "2"; //互联网收款(非会员收款)
    public static String TYPE_SELLER_RECHARGE= "3"; //商家充值
    public static String TYPE_FACTOR_RECHARGE= "4"; //发卡点充值
    public static String TYPE_MEMBER_RECHARGE= "5"; //会员充值
    public static String TYPE_FRIEND_RECHARGE= "6"; //会员代充值(替朋友充值)
    public static String TYPE_FACTOR_ACTIVE= "7"; //服务站激活会员卡
    public static String TYPE_MEMBER_ACTIVE= "8"; //会员端激活会员卡
    public static String TYPE_SELLER_WITHDRAW= "9"; //商家提现
    public static String TYPE_FACTOR_WITHDRAW= "10"; //发卡点提现
    public static String TYPE_MEMBER_ONLINE= "11"; //会员在线商城购买
    public static String TYPE_KYB_CASH= "12"; //快易帮会员收银


    /**
     * 有非常复杂的权限和设 ，都有备注
     *
     * @throws Exception
     */
    @Override
    @PUT
    @Seller
    @Path("/save")
    public void save() throws Exception {
        super.save();
//        throw new UserOperateException(400, "don't impl");
    }

    public void deleteOrder(String id) throws Exception {
        Map<String, Object> m = MysqlDaoImpl.getInstance().findById2Map("OrderInfo", id, null, null);
        if (m == null) {
            throw new UserOperateException(400, "待删除订单不存在！");
        }
        if (ORDER_TYPE_CAOGAO != (Integer) m.get("orderStatus") && ORDER_TYPE_BOOKING != (Integer) m.get("orderStatus")) {
            throw new UserOperateException(400, "仅可草稿状态可以删除！");
        }
        if ("member".equals(ControllerContext.getContext().getCurrentUserType())) {
            if (!m.get("memberId").equals(ControllerContext.getContext().getCurrentUserId())) {
                throw new UserOperateException(403, "你没有操作该订单权限!");
            }
        }

        List<Object> p = new ArrayList<>();
        p.add(id);
        MysqlDaoImpl.getInstance().exeSql("delete from OrderInfo where _id=?", p, "OrderInfo", false);
        MysqlDaoImpl.getInstance().exeSql("delete from OrderItem where orderId=?", p, "OrderItem");
        MysqlDaoImpl.getInstance().exeSql("delete from OrderPay where orderId=?", p, "OrderPay");
    }

    @POST
    @Seller
    @Member
    @Path("/del")
    @Override
    public void del() throws Exception {
        deleteOrder(ControllerContext.getPString("_id"));
    }

    /**
     * 查询我的订单:会员或者商家
     */
    @GET
    @Path("/queryMyOrder")
    public void queryMyOrder() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList<>();
        String orderStatus = ControllerContext.getPString("orderStatus");
        String orderId = ControllerContext.getPString("orderId");
        String userType = ControllerContext.getPString("userType");
        String isCount = ControllerContext.getPString("isCount");//是否统计各个状态数量
        String isNotShowOrder = ControllerContext.getPString("isNotShowOrder");//是否查询订单列表(只查询状态)
        Long pageSize = ControllerContext.getPLong("pageSize");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long indexNum = ControllerContext.getPLong("indexNum");
        if ((StringUtils.isEmpty(isNotShowOrder) || !ControllerContext.getPBoolean("isNotShowOrder")) && indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        String whereStr = " where t1.sellerId is not null and (t1.isHide = false or t1.isHide is null)";

        if (StringUtils.isNotEmpty(orderStatus) && !Pattern.matches("^(-1)|(-2)$",orderStatus)) {
            whereStr += " and t1.orderStatus=?";
            params.add(orderStatus);
        }else if("-2".equals(orderStatus)){//所有退款订单
            whereStr += " and t1.orderStatus>=6 and t1.isApplyReturn=true";
        }

        if (!StringUtils.isEmpty(orderId)) {
            whereStr += " and t1._id = ?";
            params.add(orderId);
        }
        if ("seller".equals(userType)) {
            whereStr += " and ((t1.orderType =11 and t1.pid<>-1) or t1.orderType in (0,1))" +
                    " and t1.sellerId=?" +
                    " and t1.orderStatus not in (0,1)";
            params.add(ControllerContext.getContext().getCurrentSellerId());
        } else if ("Agent".equals(userType)) {//平台查看详情
            //获取当前登录的代理商
            Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
            JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
            if (agentInfo == null || agentInfo.size() == 0 || agentInfo.get("level") == null) {
                throw new UserOperateException(500, "您无此操作权限");
            }

            Map<String, Object> orderInfo = MysqlDaoImpl.getInstance().findById2Map("OrderInfo", orderId, null, null);

            msg = Message.newReqMessage("1:GET@/crm/Member/show");
            msg.getContent().put("_id", orderInfo.get("memberId"));
            JSONObject memberInfo = ServiceAccess.callService(msg).getContent();

            if (memberInfo == null || memberInfo.size() == 0) {
                throw new UserOperateException(500, "获取会员信息失败");
            }

            if (!"1".equals(agentInfo.get("level").toString()) &&
                    !Pattern.matches("^(" + agentInfo.get("areaValue").toString() + "\\S*)$", memberInfo.get("belongAreaValue").toString())) {
                throw new UserOperateException(500, "您只能查看您归属下的会员信息");
            }

            whereStr += " and t1.orderType=11" +
                    " and t1.memberId=?";
            params.add(memberInfo.get("_id"));
        } else {
            whereStr += " and ((t1.orderType =11 and t1.pid<>-1) or t1.orderType in (0,1))" +
                    " and t1.memberId=?";
            params.add(ControllerContext.getContext().getCurrentUserId());
        }

        //orderId代表单个查询,不查询分页
        if (StringUtils.isEmpty(orderId) && (StringUtils.isEmpty(isNotShowOrder) || !ControllerContext.getPBoolean("isNotShowOrder"))) {
            List<String> p = new ArrayList<>();
            p.add("totalCount");
            String hql = "select count(t1._id) as totalCount" +
                    " from OrderInfo t1" +
                    " left join Seller t2 on t1.sellerId=t2._id" +
                    " left join Member t3 on t1.memberId=t3._id" +
                    whereStr;

            List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
            Long totalNum = (Long) orderList.get(0).get("totalCount");
            Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalNum", totalNum);
            resultMap.put("totalPage", totalPage);
        }

        if(StringUtils.isEmpty(isNotShowOrder) || !ControllerContext.getPBoolean("isNotShowOrder")){
            List<String> r = new ArrayList<>();
            String fieldStr = "";
            if(StringUtils.isNotEmpty(orderId)){
                r.add("sendContact");
                r.add("sendContactPhone");
                r.add("sendAddress");
                r.add("sendPostcode");
                r.add("memberRemark");
                r.add("express");
                r.add("expressNo");
                r.add("couponId");
                r.add("couponPrice");
                r.add("couponName");

                r.add("returnImg");
                r.add("returnDesc");
                r.add("returnAddress");
                r.add("returnContact");
                r.add("returnPhone");
                r.add("returnRefuse");
                r.add("returnExpress");
                r.add("returnExpressNo");

                fieldStr = ",t1.sendContact" +
                        ",t1.sendContactPhone" +
                        ",t1.sendAddress" +
                        ",t1.sendPostcode" +
                        ",t1.memberRemark" +
                        ",t1.express" +
                        ",t1.expressNo" +
                        ",t1.couponId" +
                        ",t1.couponPrice" +
                        ",t5.name as couponName" +

                        ",t1.returnImg" +
                        ",t1.returnDesc" +
                        ",t1.returnAddress" +
                        ",t1.returnContact" +
                        ",t1.returnPhone" +
                        ",t1.returnRefuse" +
                        ",t1.returnExpress" +
                        ",t1.returnExpressNo";
            }

            r.add("_id");
            r.add("pid");
            r.add("orderNo");
            r.add("isComment");
            r.add("orderStatus");
            r.add("totalPrice");
            r.add("returnPrice");
            r.add("freight");
            r.add("payMoney");
            r.add("pensionMoney");
            r.add("createTime");
            r.add("bookingTime");
            r.add("sendTime");
            r.add("accountTime");
            r.add("endTime");
            r.add("returnApplyTime");
            r.add("returnTime");
            r.add("orderType");
            r.add("payType");
            r.add("sellerName");
            r.add("sellerId");
            r.add("sellerIcon");
            r.add("sellerDoorImg");
            r.add("memberIcon");
            r.add("memberId");
            r.add("memberName");
            r.add("memberCard");
            r.add("isReturn");
            r.add("isApplyReturn");


            String sql = "select " +
                    "t1._id" +
                    ",t1.pid" +
                    ",t1.orderNo" +
                    ",t1.isComment" +
                    ",t1.orderStatus" +
                    ",t1.totalPrice" +
                    ",t1.returnPrice" +
                    ",t1.freight" +
                    ",t1.payMoney" +
                    ",t1.pensionMoney" +
                    ",t1.createTime" +
                    ",t1.bookingTime" +
                    ",t1.sendTime" +
                    ",t1.accountTime" +
                    ",t1.returnApplyTime" +
                    ",t1.returnTime" +
                    ",t1.endTime" +
                    ",t1.orderType" +
                    ",t1.payType" +
                    ",t2.name as sellerName" +
                    ",t2._id as sellerId" +
                    ",t2.icon as sellerIcon" +
                    ",t2.doorImg as sellerDoorImg" +
                    ",t3.icon as memberIcon" +
                    ",t3._id as memberId" +
                    ",t3.realName as memberName" +
                    ",t3.cardNo as memberCard" +
                    ",t1.isReturn" +
                    ",t1.isApplyReturn" +
                    fieldStr+
                    " from OrderInfo t1" +
                    " left join Seller t2 on t1.sellerId=t2._id" +
                    " left join Member t3 on t1.memberId=t3._id" +
                    " left join MemberCouponLink t4 on t1.couponId=t4.serial" +
                    " left join Coupon t5 on t4.couponId=t5._id";

            String groupBy = " group by t1._id";
            String orderBy = " order by t1.createTime desc";
            String limit = " ";
            if (StringUtils.isEmpty(orderId)) {
                limit = " limit " + indexNum + "," + pageSize;
            } else {
                limit = "";
            }
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, whereStr, groupBy, orderBy, limit, r, params);
            List<String> ids = new ArrayList<>();
            Map<String, Map<String, Object>> orderMap = new HashMap<>();
            for (Map<String, Object> map : re) {
                String id = (String) map.get("_id");
                ids.add(id);
                orderMap.put(id, map);
            }
            if (ids.size() > 0) {
                String itemSql = "select _id,name,price,sellPirce,count,orderId,icon,productId,selectSpec " +
                        "from OrderItem where orderId in ('" + StringUtils.join(ids, "','") + "')";
                r.clear();
                r.add("_id");
                r.add("name");
                r.add("price");
                r.add("sellPirce");
                r.add("count");
                r.add("orderId");
                r.add("icon");
                r.add("productId");
                r.add("selectSpec");
                params.clear();
                List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(itemSql, r, params);
                for (Map<String, Object> productItem : li) {
                    String oId = (String) productItem.get("orderId");
                    Map<String, Object> orderInfo = orderMap.get(oId);
                    List<Map<String, Object>> productItems = (List<Map<String, Object>>) orderInfo.get("productItems");
                    if (productItems == null) {
                        productItems = new ArrayList<>();
                        orderInfo.put("productItems", productItems);
                    }
                    productItems.add(productItem);
                }
            }
            resultMap.put("orderList", re);
        }

        //获取统计数量
        if(StringUtils.isNotEmpty(isCount) && ControllerContext.getPBoolean("isCount") && (pageNo==1 || pageNo==0)){
            String leftJoin="";
            List<String> returnField = new ArrayList<>();
            params.clear();
            if ("seller".equals(userType)) {
                leftJoin = " left join Seller t2 on t1.sellerId=t2._id" +
                        " where t1.sellerId=? and t1.orderType = 11 and t1.pid<>-1";
                params.add(ControllerContext.getContext().getCurrentSellerId());
            }else{
                leftJoin = " left join Member t2 on t1.memberId=t2._id" +
                        " where t1.memberId=?" +
                        " and ((t1.orderType =11 and t1.pid<>-1) or t1.orderType in (0,1))";
                params.add(ControllerContext.getContext().getCurrentUserId());
            }

            returnField.add("orderStatus");
            returnField.add("count");
            String sql="select" +
                    " t1.orderStatus" +
                    ",count(t1._id) as count" +
                    " from OrderInfo t1" +
                    leftJoin+
                    " group by t1.orderStatus having t1.orderStatus not in (9,100)" +
                    " order by t1.orderStatus asc";
            List<Map<String,Object>> count = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
            resultMap.put("countList", count);
        }


        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 查询商户的订单 : 线上交易
     */
    @POST
    @Seller
    @Path("/querySellerOrder")
    public void querySellerOrder() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList();
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        String search = ControllerContext.getPString("search");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        String whereStr = " where orderType=11 and orderStatus not in (0,1)";

        whereStr += " and t2.sellerId=?";
        params.add(ControllerContext.getContext().getCurrentSellerId());

        if (startTime != 0) {
            whereStr += " and t2.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t2.createTime<=?";
            params.add(endTime);
        }
        if (StringUtils.isNotEmpty(search)) {
            whereStr += " and (t3.cardNo like ? or t3.mobile like ?)";
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t2._id) as totalCount" +
                " from OrderItem t1" +
                " left join OrderInfo t2 on t1.orderId=t2._id" +
                " left join Member t3 on t2.memberId=t3._id";
        hql += whereStr + " order by t2.createTime desc";
        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> r = new ArrayList<>();
        String itemSql = "select" +
                " t1.name as goodsName" +
                ",t1.count" +
                ",t1.icon as goodsIcon" +
                ",t2._id as orderId" +
                ",t2.totalPrice" +
                ",t2.score" +
                ",t2.orderStatus" +
                ",t2.bookingTime" +
                ",t2.sendTime" +
                ",t2.accountTime" +
                ",t2.returnApplyTime" +
                ",t2.returnTime" +
                ",t2.endTime" +
                ",t2.express" +
                ",t2.expressNo" +
                ",t3.realName" +
                ",t3.cardNo" +
                " from OrderItem t1" +
                " left join OrderInfo t2 on t1.orderId=t2._id" +
                " left join Member t3 on t2.memberId=t3._id";
        itemSql += whereStr + " order by t2.createTime desc limit " + indexNum + "," + pageSize;

        r.add("goodsName");
        r.add("count");
        r.add("goodsIcon");
        r.add("orderId");
        r.add("totalPrice");
        r.add("score");
        r.add("orderStatus");
        r.add("bookingTime");
        r.add("sendTime");
        r.add("accountTime");
        r.add("returnApplyTime");
        r.add("returnTime");
        r.add("endTime");
        r.add("express");
        r.add("expressNo");
        r.add("realName");
        r.add("cardNo");
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(itemSql, r, params);
        resultMap.put("orderList", li);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 查询商户的订单 : 线下交易
     */
    @POST
    @Seller
    @Path("/querySellerOrderByOffline")
    public void querySellerOrderByOffline() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList();
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        String search = ControllerContext.getPString("search");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        String whereStr = " where t1.orderType in (0,1,2) and t1.orderStatus=100";

        whereStr += " and t1.sellerId=?";
        params.add(ControllerContext.getContext().getCurrentSellerId());

        if (startTime != 0) {
            whereStr += " and t1.endTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t1.endTime<=?";
            params.add(endTime);
        }
        if (StringUtils.isNotEmpty(search)) {
            whereStr += " and (t2.cardNo like ? or t2.mobile like ?)";
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t1._id) as totalCount" +
                " from OrderInfo t1" +
                " left join Member t2 on t1.memberId=t2._id";
        hql += whereStr + " order by t1.endTime desc";
        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> r = new ArrayList<>();
        String itemSql = "select" +
                " distinct t1._id" +
                ",t1.createTime as orderCreateTime" +
                ",t1.endTime" +
                ",t1.score" +
                ",t1.pensionMoney" +
                ",t1.payMoney" +
                ",t1.payType" +
                ",t1.orderType" +
                ",t2.realName" +
                ",t2.cardNo" +
                ",t2.mobile" +
                ",t3.brokerageCount" +
                ",t3.orderId as orderNo" +
                " from OrderInfo t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join SellerMoneyLog t3 on t1.orderNo=t3.orderId and t3.tradeType != 6";
        itemSql += whereStr + " order by t1.endTime desc limit " + indexNum + "," + pageSize;

        r.add("orderCreateTime");
        r.add("endTime");
        r.add("score");
        r.add("pensionMoney");
        r.add("payMoney");
        r.add("payType");
        r.add("orderType");
        r.add("realName");
        r.add("cardNo");
        r.add("mobile");
        r.add("brokerageCount");
        r.add("orderNo");
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(itemSql, r, params);
        resultMap.put("orderList", li);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 添加评论
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/addOrderComment")
    public void addOrderComment() throws Exception {
        JSONObject content = ControllerContext.getContext().getReq().getContent();
        String orderId = ControllerContext.getContext().getPString("orderId");
        Integer serviceStar = ControllerContext.getContext().getPInteger("serviceStar");
        String name = ControllerContext.getContext().getPString("name");

        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(400, "获取订单数据失败");
        }

        List<Object> p = new ArrayList<>();
        p.add(orderId);
        List<String> r = new ArrayList<>();
        r.add("isComment");

        String sql = "select" +
                " isComment" +
                " from OrderInfo" +
                " where _id=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (!(StringUtils.mapValueIsEmpty(re.get(0), "isComment") || !(Boolean) re.get(0).get("isComment"))) {
            throw new UserOperateException(400, "您已经评论过!");
        }
        if (serviceStar > 5 || serviceStar <=0) {
            throw new UserOperateException(400, "评星错误!");
        }
        if (StringUtils.isEmpty(name) || String.valueOf(name).length() > 200) {
            throw new UserOperateException(400, "评论字数在200字以内!");
        }
        content.put("memberId", ControllerContext.getContext().getCurrentUserId());
        content.put("createTime", System.currentTimeMillis());
        content.put("_id", UUID.randomUUID().toString());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderComment", content);
        Map<String, Object> map = new HashMap<>();
        map.put("_id", ControllerContext.getPString("orderId"));
        map.put("isComment", true);
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", map);
    }

    /**
     * 发送短信给会员
     *
     * @param sellerId
     * @param memberId
     * @param totalPrice
     * @param pensionMoney
     * @throws Exception
     */
    public static void sendPayInfo(String sellerId, String memberId, String totalPrice, String product, String pensionMoney) throws Exception {
        //获取商家名字
        Message msg = Message.newReqMessage("1:GET@/account/Seller/show");
        msg.getContent().put("_id", sellerId);
        JSONObject sellerJson = ServiceAccess.callService(msg).getContent();
        String sendSeller = "编号:" + sellerId;
        if (sellerJson != null && sellerJson.size() != 0 && !StringUtils.mapValueIsEmpty(sellerJson, "name")) {
            sendSeller = sellerJson.get("name").toString();
        }
        //获取会员手机号码
        msg = Message.newReqMessage("1:GET@/crm/Member/show");
        msg.getContent().put("_id", memberId);
        JSONObject memberJson = ServiceAccess.callService(msg).getContent();
        if (sellerJson != null && sellerJson.size() != 0 && !StringUtils.mapValueIsEmpty(sellerJson, "mobile")) {
            return;
        }
        //发送短信
        msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendPay");
        msg.getContent().put("userPhone", memberJson.get("mobile"));
        msg.getContent().put("seller", sendSeller);
        msg.getContent().put("totalMoney", totalPrice);
        msg.getContent().put("product", product);
        msg.getContent().put("pensionMoney", pensionMoney);
        ServiceAccess.callService(msg);
    }
    /**
     * 发送短信给商家（结算）
     *
     * @param sellerId
     * @param memberId
     * @param totalPrice
     * @throws Exception
     */
    public static void sendPaySeller(String sellerId, String memberId, String totalPrice) throws Exception {
        //获取商家名字
        Message msg = Message.newReqMessage("1:GET@/account/Seller/show");
        msg.getContent().put("_id", sellerId);
        JSONObject sellerJson = ServiceAccess.callService(msg).getContent();
        //获取会员手机号码
        msg = Message.newReqMessage("1:GET@/crm/Member/show");
        msg.getContent().put("_id", memberId);
        JSONObject memberJson = ServiceAccess.callService(msg).getContent();
        if (sellerJson != null && sellerJson.size() != 0 && !StringUtils.mapValueIsEmpty(sellerJson, "mobile")) {
            return;
        }
        //发送短信
        msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendPaySeller");
        msg.getContent().put("userPhone", sellerJson.get("phone"));
        msg.getContent().put("memberName", memberJson.get("mobile").toString().replaceAll("^.{4,8}$","*"));
        msg.getContent().put("sellerName", sellerJson.get("name"));
        msg.getContent().put("totalMoney", totalPrice);
        ServiceAccess.callService(msg);
    }
    /**
     * 发送短信给商家(购买/下单)
     *
     * @param sellerId
     * @throws Exception
     */
    public static void sendPayBookingSeller(String sellerId) throws Exception {
        //获取商家名字
        Message msg = Message.newReqMessage("1:GET@/account/Seller/show");
        msg.getContent().put("_id", sellerId);
        JSONObject sellerJson = ServiceAccess.callService(msg).getContent();
        //发送短信
        msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendPayBookingSeller");
        msg.getContent().put("userPhone", sellerJson.get("phone"));
        msg.getContent().put("sellerName", sellerJson.get("name"));
        ServiceAccess.callService(msg);
    }

    public static void sendActivate(String mobile) throws Exception {
        Message msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendActivate");
        msg.getContent().put("mobile", mobile);
        ServiceAccess.callService(msg);
    }

    /**
     * 无货退款,发送给会员
     */
    public static void sendPayNotStockMember(String sellerId, String memberId,String product, String totalPrice) throws Exception {
        //获取商家名字
        Message msg = Message.newReqMessage("1:GET@/account/Seller/show");
        msg.getContent().put("_id", sellerId);
        JSONObject sellerJson = ServiceAccess.callService(msg).getContent();
        //获取会员手机号码
        msg = Message.newReqMessage("1:GET@/crm/Member/show");
        msg.getContent().put("_id", memberId);
        JSONObject memberJson = ServiceAccess.callService(msg).getContent();
        if (sellerJson != null && sellerJson.size() != 0 && !StringUtils.mapValueIsEmpty(sellerJson, "mobile")) {
            return;
        }
        //发送短信
        msg = Message.newReqMessage("1:PUT@/common/Sms/sendNotStockMember");
        msg.getContent().put("userPhone", memberJson.get("mobile").toString());
        msg.getContent().put("sellerName", sellerJson.get("name"));
        msg.getContent().put("product", product);
        msg.getContent().put("totalMoney", totalPrice);
        ServiceAccess.callService(msg);
    }

    /**
     * 检查商家是否支持余额支付
     * @param sellerId
     * @param payType
     * @return
     * @throws Exception
     */
    public JSONObject checkSellerPayType(String sellerId,String payType) throws Exception{
        if(StringUtils.isEmpty(sellerId)){
            throw new UserOperateException(500,"获取商家数据失败");
        }
        if(StringUtils.isEmpty(payType)){
            throw new UserOperateException(500,"获取支付方式失败");
        }
        JSONObject sellerMap = ServiceAccess.getRemoveEntity("account", "Seller", sellerId);
        if (sellerMap == null) {
            throw new UserOperateException(400, "获取商家数据失败");
        }
        //若是余额支付,则检查商家是否有权限
        if("3".equals(payType) && (StringUtils.mapValueIsEmpty(sellerMap,"isOfflineBalance")
                || !Boolean.parseBoolean(sellerMap.get("isOfflineBalance").toString()))){
            throw new UserOperateException(500,"该商家暂不支持积分支付");
        }
        return sellerMap;
    }

    /**
     * 检查商家是否支持余额支付
     * 支持检查多个商家
     */
    public void checkMoreSellerPayType(List<Map<String,Object>> seller) throws  Exception {
        String sql = "select isOfflineBalance,name from Seller where _id in (";
        StringBuffer where = new StringBuffer();
        List<Object> params = new ArrayList<>();
        List<String> returnField = new ArrayList<>();
        returnField.add("isOfflineBalance");
        returnField.add("name");

        for(int i=0,len=seller.size();i<len;i++){
            where.append(",?");
            params.add(seller.get(i).get("sellerId"));
        }
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql+where.substring(1,where.length())+")",returnField,params);

        for(int i=0,len=re.size();i<len;i++){
            if(StringUtils.mapValueIsEmpty(re.get(i),"isOfflineBalance")
                    || !Boolean.parseBoolean(re.get(i).get("isOfflineBalance").toString())){
                throw new UserOperateException(500,re.get(i).get("name").toString()+"不支持积分支付");
            }
        }
    }

    /**
     * 检查用户是否可用
     *
     */
    public void checkUser(String userType,String userId) throws  Exception {
        String modelName = "";
        if("Seller".equals(userType) || "Factor".equals(userType)){
            modelName = "account";
        }else if("Member".equals(userType)){
            modelName = "crm";
        }
        JSONObject user = ServiceAccess.getRemoveEntity(modelName,userType,userId);
        if(user == null || user.size()==0){
            throw new UserOperateException(500,"未找到用户");
        }
        if(StringUtils.mapValueIsEmpty(user,"canUse") || !Boolean.parseBoolean(user.get("canUse").toString())){
            String name = "";
            if(!StringUtils.mapValueIsEmpty(user,"name")){
                name = user.get("name").toString();
            }else if(!StringUtils.mapValueIsEmpty(user,"realName")){
                name = user.get("realName").toString();
            }else if("Seller".equals(userType)){
                name = "商家";
            }else if("Member".equals(userType)){
                name = "会员";
            }
            throw new UserOperateException(500,name+" 已被禁用");
        }else if("Member".equals(userType) &&
                (StringUtils.mapValueIsEmpty(user,"isBindCard") || !Boolean.parseBoolean(user.get("isBindCard").toString()))
                && (StringUtils.mapValueIsEmpty(user,"isFree") || !Boolean.parseBoolean(user.get("isFree").toString()))){
            throw new UserOperateException(500,"会员未激活");
        }
    }

    /**
     * 添加线下扫码订单:会员扫商家收银牌
     *
     * @throws Exception
     */
    @POST
    @Path("/createOrderOfflineByMember")
    public void createOrderOfflineByMember() throws Exception {
        JSONObject seller = ControllerContext.getContext().getReq().getContent();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        double totalPrice = seller.getDouble("totalPrice");
        String sellerId = seller.getString("sellerId");
        String payType = seller.getString("payType");
        String pwd = ControllerContext.getPString("pwd");

        if(!Pattern.matches("^[0-9]+(.[0-9]{1,2})?$",ControllerContext.getPString("totalPrice"))){
            throw new UserOperateException(500,"输入金额错误");
        }
        if(StringUtils.isEmpty(sellerId)){
            throw new UserOperateException(500,"获取商家数据失败");
        }
        JSONObject sellerMap = checkSellerPayType(sellerId,payType);

        checkUser("Seller",sellerId);
        // 若没有登录则获取手机号码去注册
        if(StringUtils.isEmpty(memberId)){
            System.out.println("sellerId=="+sellerId);
            Map<String,Object> member = autoCreateMember(sellerId,"Seller");
            memberId = member.get("_id").toString();
        }else{
            checkUser("Member",memberId);
        }

        if("3".equals(payType)){
            //检查会员支付密码
            Message msgPwd = Message.newReqMessage("1:GET@/crm/Member/checkMemberPayPwd");
            msgPwd.getContent().put("memberId", memberId);
            msgPwd.getContent().put("payPwd", pwd);
            ServiceAccess.callService(msgPwd);
        }

        String orderNo = ZQUidUtils.generateOrderNo();
        Map<String, Object> v = new HashMap<>();
        v.put("_id", ZQUidUtils.genUUID());
        v.put("memberId", memberId);
        v.put("orderNo", orderNo);
        v.put("totalPrice", totalPrice);
        v.put("payMoney", 0.0);
        v.put("sellerId", sellerId);
        v.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);//已预订/未付款
        v.put("createTime", System.currentTimeMillis());
        v.put("bookingTime", System.currentTimeMillis());
        v.put("o2o", true);
        v.put("score", sellerMap.get("integralRate").toString());
        v.put("orderType", 0);
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, v);

        if("3".equals(payType)){
            Map<String,Object> payInfo = new HashMap<>();
            payInfo.put("payType",3);
            payInfo.put("totalFee",totalPrice);
            updateOrderOfflineByScan(v,payInfo);
        }
        toResult(Response.Status.OK.getStatusCode(), v);
    }


    /**
     * 添加线下扫码订单:生成已预订
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/createOrderOfflineByScan")
    public void createOrderOfflineByScan() throws Exception {
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Double totalPrice = ControllerContext.getPDouble("totalPrice");
        String memberId = ControllerContext.getPString("memberId");
        String payType = ControllerContext.getPString("payType");

        String key = "create_order_" + memberId;
        try {
            JedisUtil.whileGetLock(key, 60);

            JSONObject sellerMap = checkSellerPayType(sellerId,payType);
            if (sellerMap == null) {
                throw new UserOperateException(400, "获取商家数据失败");
            }

            //检查最近30分钟是否对该会员发起过重复的订单
    //        Map<String,Object> oldOrder = checkOrderOfflineByMember(memberId);
    //        if(oldOrder!=null && oldOrder.size()!=0){
    //            oldOrder.put("isRepeat",true);
    //            toResult(200, oldOrder);
    //            return;
    //        }

            if(StringUtils.isEmpty(payType)){
                throw new UserOperateException(500,"请选择支付方式");
            }
            //若是余额支付,则检查商家是否有权限
            if("3".equals(payType) && (StringUtils.mapValueIsEmpty(sellerMap,"isOfflineBalance")
                    || !Boolean.parseBoolean(sellerMap.get("isOfflineBalance").toString()))){
                throw new UserOperateException(500,"您无权发起线下积分支付订单");
            }

            checkUser("Seller",sellerId);

            BigDecimal bd = new BigDecimal(totalPrice);
            double totalPriceBd = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            int orderType;
            //-1表示非会员支付
            if (StringUtils.isEmpty(memberId) || "null".equals(memberId)) {
                memberId = "-1";
                orderType = 2;
                if(true){
                    throw new UserOperateException(500,"此功能维护升级中");
                }
            } else {
                orderType = 0;
                String sql = "select" +
                        " _id" +
                        ",canUse" +
                        " from Member" +
                        " where _id=? and (isBindCard=true or isFree=true)";
                List<String> returnFields = new ArrayList<>();
                returnFields.add("_id");
                returnFields.add("canUse");
                List<Object> params = new ArrayList<>();
                params.add(memberId);
                List<Map<String, Object>> rMap = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
                if (rMap == null || rMap.size() == 0 || rMap.get(0).get("_id") == null || rMap.get(0).get("canUse") == null
                        || !(boolean) rMap.get(0).get("canUse")) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "会员尚未激活!");
                }
            }

            Integer integ = (Integer) sellerMap.get("integralRate");

            String orderNo = ZQUidUtils.generateOrderNo();
            Map<String, Object> v = new HashMap<>();
            v.put("_id", ZQUidUtils.genUUID());
            v.put("memberId", memberId);
            v.put("orderNo", orderNo);
            v.put("totalPrice", totalPriceBd);
            v.put("payMoney", 0.0);
            v.put("sellerId", sellerId);
            v.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);//已预订/未付款
            v.put("createTime", System.currentTimeMillis());
            v.put("bookingTime", System.currentTimeMillis());
            v.put("o2o", true);
            v.put("score", integ);
            v.put("orderType", orderType);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, v);

            Map<String, Object> order = new HashMap<>();
            order.put("orderNo", orderNo);
            order.put("_id", (String) v.get("_id"));
            toResult(Response.Status.OK.getStatusCode(), order);

    //        Map<String,Object> payInfo = new HashMap<>();
    //        payInfo.put("totalFee",totalPriceBd);
    //        payInfo.put("payType",payType);
    //        addOtherOrder(v,payInfo);
        } finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 线下扫码订单:会员获取商家发起的订单
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getOrderOfflineByMember")
    public void getOrderOfflineByMember() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Map<String,Object> re = checkOrderOfflineByMember(memberId);
        if(re!=null && re.size()!=0){
            toResult(200,re);
        }
    }

    /**
     * 查询最近30分钟商家发起的线下扫码订单
     * @return
     * @throws Exception
     */
    public Map<String,Object> checkOrderOfflineByMember(String memberId) throws Exception {
        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        String curSellerId = ControllerContext.getContext().getCurrentSellerId();

        long time = System.currentTimeMillis()-1000*60*30;
        params.add(time);
        params.add(memberId);

        String where=" where t1.createTime>=?" +
                " and t1.memberId=?" +
                " and t1.orderType=0" +
                " and t1.orderStatus=1";

        if(StringUtils.isNotEmpty(curSellerId)){
            params.add(ControllerContext.getContext().getCurrentSellerId());
            where+=" and t1.sellerId=?";
        }

        returnField.add("orderId");
        returnField.add("orderNo");
        returnField.add("totalPrice");
        returnField.add("sellerId");
        returnField.add("sellerName");
        returnField.add("mobile");

        String sql = "select " +
                " t1._id as orderId" +
                ",orderNo" +
                ",t1.totalPrice" +
                ",t1.sellerId" +
                ",t2.name as sellerName" +
                ",CONCAT(SUBSTR(t3.mobile,1,3),'****',SUBSTR(t3.mobile,8,4))as mobile" +
                " from OrderInfo t1" +
                " left join Seller t2 on t1.sellerId=t2._id" +
                " left join Member t3 on t1.memberId=t3._id" +
                where+
                " order by t1.createTime desc" +
                " limit 0,1";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        if(re!=null && re.size()!=0){
            return re.get(0);
        }else{
            return null;
        }
    }

//    /**
//     * 线下扫码订单:会员余额支付商家发起的订单
//     *
//     * @throws Exception
//     */
//    @POST
//    @Member
//    @Path("/payOrderOfflineByMember")
//    public void payOrderOfflineByMember() throws Exception {
//        String orderId = ControllerContext.getPString("orderId");
//        String pwd = ControllerContext.getPString("pwd");
//
//        if(StringUtils.isEmpty(orderId)){
//            throw new UserOperateException(500,"获取订单数据失败");
//        }
//        if(StringUtils.isEmpty(pwd)){
//            throw new UserOperateException(500,"请输入支付密码");
//        }
//
//        Map<String,Object> order = MysqlDaoImpl.getInstance().findById2Map(entityName,orderId,null,null);
//        if(order==null || order.size()==0){
//            throw new UserOperateException(500,"获取订单数据失败");
//        }
//
//        //检查会员支付密码
//        Message msgPwd = Message.newReqMessage("1:GET@/crm/Member/checkMemberPayPwd");
//        msgPwd.getContent().put("memberId", order.get("memberId"));
//        msgPwd.getContent().put("payPwd", pwd);
//        ServiceAccess.callService(msgPwd);
//
//        Map<String,Object> payInfo = new HashMap<>();
//        payInfo.put("payType",3);
//        payInfo.put("totalFee",order.get("totalPrice"));
//
//        updateOrderOfflineByScan(order,payInfo);
//    }

    /**
     * 线下扫码订单:会员扫码支付
     * @param orderInfo
     * @param payInfo
     * @throws Exception
     */
    public static void updateOrderOfflineByScan(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        String sellerId = (String) orderInfo.get("sellerId");
        Double totalPrice = Double.valueOf(orderInfo.get("totalPrice").toString());
        BigDecimal bd = new BigDecimal(totalPrice);
        double totalPriceBd = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double integralRate = BigDecimalUtil.divide(Double.parseDouble(orderInfo.get("score").toString()), 100);//积分率
        double brokerageCount = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.multiply(totalPrice, integralRate)); //商家支付佣金
        double pensionMoney = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.divide(brokerageCount, 2));//返回给会员的养老金
        double deduct = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(brokerageCount, -pensionMoney));//代理商/服务站提成金额

        String memberId = (String) orderInfo.get("memberId");
        String orderNo = (String) orderInfo.get("orderNo");
        int payType = Integer.valueOf(payInfo.get("payType").toString());

        double payMoney = Double.valueOf(payInfo.get("totalFee").toString());
        orderInfo.put("payMoney", payMoney);
        orderInfo.put("pensionMoney", pensionMoney);
        orderInfo.put("payType", payType);
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);

        //生成商家现金明细记录/汇总记录
        createSellerLog(sellerId, memberId, orderNo, brokerageCount, totalPrice, "5", 3,payType);
        //生成会员现金明细记录/汇总记录
        createMemberMoneyLog(memberId, memberId, orderNo, totalPriceBd, "4", payType);
        //生成养老金记录/汇总记录
        createPensionLog(memberId, orderNo, pensionMoney, "4");
        //生成代理商提成
        createAgentMoney(deduct, orderNo, memberId, 3);

        //发送短信
        sendPayInfo(sellerId, memberId, String.valueOf(totalPrice), "线下商品", ",已收获养老金" + String.valueOf(pensionMoney) + "元");
        sendPaySeller(sellerId, memberId, String.valueOf(totalPrice));
    }

    private static int changePayType(int payType) throws Exception {
        if (payType == PAY_TYPE_SAOBEI_WECHAT) {
            return PAY_TYPE_WECHAT;
        }else if (payType == PAY_TYPE_SAOBEI_ALIPAY) {
            return PAY_TYPE_ALIPAY;
        }
        return payType;
    }

    /**
     * 检查支付:第三方通知并处理
     *
     * @throws Exception
     */
    @POST
    @Path("/checkOrderStatusByPay")
    public void checkOrderStatusByPay() throws Exception {
        JSONObject re = ControllerContext.getContext().getReq().getContent();
        checkOrderStatus(re);
    }

    /**
     * 检查订单
     *
     * @return
     * @throws Exception
     */
    public static void checkOrderStatus(JSONObject re) throws Exception {
        Map<String, Object> payInfo = null;
        String isSuccess = null;
        String orderId = null;

        if (re != null) {
            payInfo = (Map<String, Object>) re.get("pay");
            if(StringUtils.mapValueIsEmpty(re,"payStatus")){
                isSuccess = payInfo.get("payStatus").toString();
            }else{
                isSuccess = re.getString("payStatus");
            }
            if(StringUtils.mapValueIsEmpty(re,"orderId")){
                orderId = payInfo.get("orderId").toString();
            }else{
                orderId = re.getString("orderId");
            }
        }
        if ("SUCCESS".equals(isSuccess)) {
            //获取订单信息
            Map<String, Object> orderInfo = MysqlDaoImpl.getInstance().findById2Map("OrderInfo", orderId, null, null);
            if (orderInfo == null || orderInfo.size() == 0) {
                throw new UserOperateException(400, "获取订单失败");
            }
            if ((Integer) orderInfo.get("orderStatus") != OrderInfoAction.ORDER_TYPE_BOOKING) {
                return;
            }
            int orderType = Integer.parseInt(orderInfo.get("orderType").toString());

            if(!StringUtils.mapValueIsEmpty(payInfo,"payType")){
                payInfo.put("payType",changePayType(Integer.parseInt(payInfo.get("payType").toString())));
            }else{
                throw new UserOperateException(500,"获取实际支付方式失败");
            }

            updateOrderStatus(orderType, orderInfo, payInfo);
        }
    }

    /**
     * 更新订单
     *
     * @param orderInfo
     * @param payInfo
     * @throws Exception
     */
    public static void updateOrderStatus(int orderType, Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        if (orderType == 0) {//会员扫码
            updateOrderOfflineByScan(orderInfo, payInfo);
        } else if (orderType == 1) {//现金交易
            updateOrderOffline(orderInfo, payInfo);
        } else if (orderType == 2) {//非会员扫码
            addOtherOrder(orderInfo, payInfo);
        } else if (orderType == 3) {//商家充值
            updateRecharge(orderInfo, payInfo, "seller");
        } else if (orderType == 4) {//服务站充值
            updateRecharge(orderInfo, payInfo, "factor");
        } else if (orderType == 5) {//会员充值
            memberRecharge(orderInfo, payInfo);
        } else if (orderType == 6) {//会员替朋友充值
            memberRechargeFriend(orderInfo, payInfo);
        } else if (orderType == 7) {//服务站激活会员卡
            updateMemberCardByFactor(orderInfo, payInfo);
        } else if (orderType == 8) {//会员自主激活会员卡
            updateMemberActive(orderInfo, payInfo);
        } else if (orderType == 11) {//会员在线支付
            updateOnlineOrderByMoreSeller(orderInfo, payInfo);
        }
    }


    /**
     * 检查支付
     *
     * @throws Exception
     */
    @POST
    @Path("/checkOrderStatus")
    public void checkOrderStatus() throws Exception {
        Map paramsMap = ControllerContext.getContext().getReq().getContent();
        String orderNo = ControllerContext.getPString("orderNo");
        String orderId = ControllerContext.getPString("orderId");
        String payId = ControllerContext.getPString("payId");
        String payType = ControllerContext.getPString("payType");


        Map<String, Object> re = new HashMap<>();

        String key = "order_";
        if(StringUtils.isNotEmpty(orderNo)){
            key +=orderNo;
        }else if(StringUtils.isNotEmpty(orderId)){
            key +=orderId;
        }else{
            key +=payId;
        }
        try{
            JedisUtil.whileGetLock(key,30);

            if(StringUtils.isNotEmpty(payType) && "3".equals(payType)){//检查余额支付
                Map<String,Object> params = new HashMap<>();
                if(StringUtils.isEmpty(orderNo)){
                    params.put("orderId",orderId);
                }else{
                    params.put("orderNo",orderNo);
                }
                Map<String, Object> order = MysqlDaoImpl.getInstance().findOne2Map(entityName,params,null,null);

                if(order!=null && order.size()!=0){
                    Message msg = Message.newReqMessage("1:GET@/crm/Member/show");
                    msg.getContent().put("_id", order.get("memberId"));
                    JSONObject member = ServiceAccess.callService(msg).getContent();
                    re.put("memberName",member.get("realName").toString());
                    re.put("totalPrice",order.get("totalPrice").toString());
                    re.put("orderNo",order.get("orderNo").toString());
                    re.put("createTime",order.get("createTime"));
                }

                if(order!=null && order.size()!=0 && "100".equals(order.get("orderStatus").toString())){

                    re.put("payStatus","SUCCESS");
                }
            }else{//检查第三方支付
                String sql = "select" +
                        " t2._id as payId" +
                        ",t1._id as orderId" +
                        ",t2.trId" +
                        ",t2.totalFee" +
 //                       ",t2.sellerName" +
                        " from OrderInfo t1" +
                        " left join Pay t2 on t2.orderId = t1._id" +
                        " where (t1.orderType<>11 or (t1.orderType=11 and t1.pid=-1))";
                List<Object> params = new ArrayList<>();
                if(!StringUtils.isEmpty(orderNo)){
                    sql+=" and t1.orderNo =?";
                    params.add(orderNo);
                }else if(!StringUtils.isEmpty(orderId)){
                    sql+=" and t1._id=?";
                    params.add(orderId);
                }else if(!StringUtils.isEmpty(payId)){
                    sql+=" and t2._id=?";
                    params.add(payId);
                }

                List<String> returnFiled = new ArrayList<>();
                returnFiled.add("payId");
                returnFiled.add("orderId");
                returnFiled.add("totalFee");
//                returnFiled.add("sellerName");
                List<Map<String, Object>> pay = MysqlDaoImpl.getInstance().queryBySql(sql, returnFiled, params);

                if(pay==null || pay.size()==0 || pay.get(0)==null || pay.get(0).size()==0){
                    throw new UserOperateException(500,"获取支付订单失败");
                }

                Message msg = Message.newReqMessage("1:GET@/payment/Pay/queryPayResultList");
                msg.getContent().put("orderIdList", pay.get(0).get("orderId").toString());
                msg.getContent().put("payIdList", pay.get(0).get("payId").toString());
                JSONArray result = ServiceAccess.callService(msg).getContent().getJSONArray("items");
                for (Object o : result) {
                    JSONObject r = (JSONObject) o;
                    checkOrderStatus(r);
                    re.put("payStatus", r.getString("payStatus"));
                    re.put("orderId", r.getString("orderId"));
                }
//                if(!pay.get(0).get("sellerName").toString().isEmpty()) {
//                    re.put("sellerName", pay.get(0).get("sellerName"));
//                }
                re.put("totalPrice",pay.get(0).get("totalFee"));
            }
            toResult(200, re);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 平台管理:补单
     * 第三方支付成功,若没有生成相应数据,则手动生成
     *
     * @throws Exception
     */
    @POST
    @Path("/updateOrderStatusByThird")
    public void updateOrderStatusByThird() throws Exception {
        String payId = ControllerContext.getPString("payId");
        String trId = ControllerContext.getPString("trId");

        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agent = ServiceAccess.callService(msg).getContent();
        if (!"1".equals(agent.get("level")) || !"A-000001".equals(agent.get("_id"))) {
            throw new UserOperateException(500, "您无此权限");
        }

        if (StringUtils.isEmpty(payId)) {
            throw new UserOperateException(500, "无法获取支付记录");
        }

        msg = Message.newReqMessage("1:GET@/payment/Pay/getPayById");
        msg.getContent().put("payId", payId);
        JSONObject payJson = ServiceAccess.callService(msg).getContent();

        if (payJson == null || payJson.size() == 0 || StringUtils.mapValueIsEmpty(payJson, "orderId")) {
            throw new UserOperateException(500, "无法获取支付记录");
        }
        Map<String, Object> orderInfo = MysqlDaoImpl.getInstance().findById2Map(entityName, payJson.get("orderId").toString(), null, null);
        if (orderInfo == null || orderInfo.size() == 0) {
            throw new UserOperateException(500, "无法获取订单数据");
        }
        if (!"1".equals(orderInfo.get("orderStatus").toString())) {
            throw new UserOperateException(500, "仅支持补全未支付订单");
        }

//        if(StringUtils.isNotEmpty(trId)){
//            msg = Message.newReqMessage("1:GET@/payment/Pay/checkSaobeiPay");
//            msg.getContent().put("trId", trId);
//            msg.getContent().put("payType", payJson.get("payType"));
//            msg.getContent().put("orderId", payJson.get("orderId"));
//        }else{
//            //查询Pay是否为start,若是start,则更新
//            msg = Message.newReqMessage("1:GET@/payment/Pay/queryPayResult");
//            msg.getContent().put("payId", payId);
//        }
//        JSONObject rePay = ServiceAccess.callService(msg).getContent();
//
//        if (!"SUCCESS".equals(rePay.get("payStatus").toString())) {
//            throw new UserOperateException(500, "尚未从第三方获取到支付结果,请检查是否支付");
//        }

        //查询Pay是否为start,若是start,则更新
        msg = Message.newReqMessage("1:GET@/payment/Pay/queryPayResult");
        msg.getContent().put("payId", payId);
        JSONObject rePay = ServiceAccess.callService(msg).getContent();

        if (!"SUCCESS".equals(rePay.get("payStatus").toString())) {
            String payType = "4".equals(payJson.get("payType").toString()) ? "支付宝" : "微信";

            throw new UserOperateException(500, "尚未从" + payType + "获取到支付结果,请检查是否支付");
        }

        updateOrderStatus(Integer.valueOf(orderInfo.get("orderType").toString()), orderInfo, (Map<String, Object>) rePay.get("pay"));
    }

//    /**
//     * 非会员扫码交易
//     *
//     * @throws Exception
//     */
//    @POST
//    @Seller
//    @Path("/addOtherOrder")
//    public void addOtherOrder() throws Exception {
//        String orderNo = ControllerContext.getPString("orderNo");
//        Map<String, Object> re = new HashMap<>();
//        re.put("payStatus", checkOrderStatus(orderNo));
//        toResult(200, re);
//    }

    /**
     * 更新非会员扫码交易
     * @param orderInfo
     * @param payInfo
     * @throws Exception
     */
    public static void addOtherOrder(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        if (payInfo.get("totalFee") == null) {
            return;
        }
        if (!"-1".equals(orderInfo.get("memberId"))) {//不是-1说明该订单有会员ID或者订单有误
            throw new UserOperateException(400, "订单获取错误!");
        }

        String sellerId = (String) orderInfo.get("sellerId");
        String orderId = (String) orderInfo.get("orderNo");
        Double totalPrice = Double.valueOf(orderInfo.get("totalPrice").toString());

        orderInfo.put("payMoney", payInfo.get("totalFee"));
        orderInfo.put("payType", payInfo.get("payType"));
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);

        // 扣除千分之五 手续费
        totalPrice = BigDecimalUtil.multiply(totalPrice,0.995);

        //生成商家现金明细记录/汇总记录
        createSellerLog(sellerId, "-1", orderId, 0, totalPrice, "5", PAY_TYPE_CASH ,Integer.parseInt(payInfo.get("payType").toString()));
    }

    public static void updateOrderOffline(Map<String, Object> order, Map<String, Object> payInfo) throws Exception {
        String orderId = order.get("_id").toString();
        String orderNo = order.get("orderNo").toString();
        String sellerId = order.get("sellerId").toString();
        String memberId = order.get("memberId").toString();
        double totalPrice;
        //调取Order的总金额,因为支付方是商家,且支付的是佣金
        totalPrice = Double.parseDouble(order.get("totalPrice").toString());

        double integralRate = BigDecimalUtil.divide(Double.parseDouble(order.get("score").toString()), 100);//积分率
        double brokerageCount = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.multiply(totalPrice, integralRate)); //商家支付佣金
        double pensionMoney = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.divide(brokerageCount, 2));//返回给会员的养老金
        double deduct = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(brokerageCount, -pensionMoney));//代理商/服务站提成金额

        //这里从订单里取出保存的seller的支付方式(临时保存),生成商家明细后,会把order的payType改为现金交易类型
        //生成商家现金明细记录/汇总记录
        createSellerLog(sellerId, memberId, orderNo, brokerageCount, totalPrice, "4", Integer.parseInt(order.get("payType").toString()),PAY_TYPE_GET_CASH);//6

        Map<String, Object> v = new HashMap<>();
        v.put("_id", orderId);
        v.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        v.put("payType", OrderInfoAction.PAY_TYPE_GET_CASH);
        v.put("payMoney", totalPrice);
        v.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", v);

        //生成会员现金明细记录/汇总记录
        createMemberMoneyLog(memberId, memberId, orderNo, totalPrice, "4", PAY_TYPE_GET_CASH);
        //生成养老金记录/汇总记录
        createPensionLog(memberId, orderNo, pensionMoney, "4");
        //生成代理商提成
        createAgentMoney(deduct, orderNo, memberId, 3);

        //发送短信
        sendPayInfo(sellerId, memberId, String.valueOf(totalPrice), "线下商品", ",已收获养老金" + String.valueOf(pensionMoney) + "元");
//        sendPaySeller(sellerId, memberId, String.valueOf(totalPrice));
    }

    /**
     * 自动创建会员  sellerId   Seller
     * @throws Exception
     */
    public Map<String,Object> autoCreateMember(String shareId,String shareType) throws Exception {
        String mobile = ControllerContext.getPString("mobile");
        System.out.println("mobile=="+mobile);
        if(StringUtils.isEmpty(mobile)){
            throw new UserOperateException(500,"获取会员手机号码失败");
        }
        Message msg = Message.newReqMessage("1:GET@/crm/Member/getMobileReg");
        msg.getContent().put("mobile",mobile);
        JSONObject member = ServiceAccess.callService(msg).getContent();
        System.out.println("member=="+member);

        // 若没有会员，则自动注册为免费会员，并归属于该商家
        if(member==null || member.size()==0){
            msg = Message.newReqMessage("1:POST@/crm/Member/createMemberFree");
            msg.getContent().put("mobile",mobile);
            msg.getContent().put("shareId",shareId);
            msg.getContent().put("shareType",shareType);
            member = ServiceAccess.callService(msg).getContent();
        }

        return member;
    }


    /**
     * 生成会员现金交易 预订订单
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/createOrderOffline")
    public void createOrderOffline() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        double totalPrice = ControllerContext.getPDouble("money");
        String sellerPayType = ControllerContext.getPString("sellerPayType");
//        if (StringUtils.isEmpty(memberId)) {
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "没有获取到会员!");
//        }
        String key = "create_order_" + memberId;
        try {
            JedisUtil.whileGetLock(key, 60);
            if (totalPrice <= 0) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付金额错误!");
            }
            if (StringUtils.isEmpty(sellerPayType)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请选择支付方式!");
            }
            List<String> returnFields = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            String sellerId = ControllerContext.getContext().getCurrentSellerId();
            checkUser("Seller",sellerId);

            if(!StringUtils.isEmpty(memberId)){
                checkUser("Member",memberId);
            }else{
                Map<String,Object> member = autoCreateMember(sellerId,"Seller");
                memberId = member.get("_id").toString();
            }

            if ("3".equals(sellerPayType)) {
                String sql = "select cashPassword from Seller where _id=?";
                returnFields.clear();
                params.clear();
                returnFields.add("cashPassword");
                params.add(sellerId);
                List<Map<String,Object>> rMap = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        //            String pwd = ControllerContext.getPString("pwd");
        //            if (rMap == null || rMap.size() == 0 || rMap.get(0).get("cashPassword") == null ||
        //                    StringUtils.isEmpty(rMap.get(0).get("cashPassword").toString())) {
        //                throw new UserOperateException(400, "请先设置支付密码!");
        //            }
        //            if (!MessageDigestUtils.digest(pwd).equals(rMap.get(0).get("cashPassword").toString())) {
        //                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码错误!");
        //            }
            }

            BigDecimal bd = new BigDecimal(totalPrice);
            double totalPriceBd = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            //获取积分率
            JSONObject sellerMap = ServiceAccess.getRemoveEntity("account", "Seller", sellerId);
            if (sellerMap == null) {
                throw new UserOperateException(400, "获取商家数据失败");
            }
            double integ = Double.parseDouble(sellerMap.get("integralRate").toString());

            Double integralRate = BigDecimalUtil.divide(integ, 100.00);//积分率
            double brokerageCount = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.multiply(totalPriceBd, integralRate)); //商家支付佣金
            double pensionMoney = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.divide(brokerageCount, 2));//返回给会员的养老金

            Map<String, Object> order = new HashMap<>();
            String orderId = ZQUidUtils.genUUID();
            String orderNo = ZQUidUtils.generateOrderNo();
            order.put("_id", orderId);
            order.put("memberId", memberId);
            order.put("orderNo", orderNo);
            order.put("pensionMoney", pensionMoney);
            order.put("totalPrice", totalPriceBd);
            order.put("payMoney", 0.0);
            order.put("sellerId", sellerId);
            order.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);
            order.put("createTime", System.currentTimeMillis());
            order.put("bookingTime", System.currentTimeMillis());
            order.put("o2o", true);
            order.put("score", integ);
            order.put("payType", sellerPayType);
            order.put("tradeType", OrderInfoAction.PAY_TYPE_OFFLINE);
            order.put("orderType", 1);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);

            if ("3".equals(sellerPayType)) {//如果是余额支付,直接完成交易
                Map<String, Object> payInfo = new HashMap<>();
                payInfo.put("totalFee", totalPrice);
                updateOrderOffline(order, payInfo);
            } else {
                Map<String, Object> re = new HashMap<>();
                re.put("orderNo", orderNo);
                re.put("orderId", orderId);
                toResult(Response.Status.OK.getStatusCode(), re);
            }
        } finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 创建 商家/服务站/会员/替朋友充值 充值订单
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/createRecharge")
    public void createRecharge() throws Exception {
//        if(!"1".equals(ControllerContext.getPString("canUse"))){
//            throw new UserOperateException(500,"该业务已关闭……");
//        }

        double totalPrice = ControllerContext.getPDouble("totalPrice");
        String userType = ControllerContext.getPString("userType");
        String memberId = "";
        String sellerId = "";
        String payType = "";
        int orderType;
        String keyUserId = "";

        if("seller".equals(userType)){
            keyUserId = ControllerContext.getContext().getCurrentSellerId();
        } else if ("factor".equals(userType)) {
            String otherDataJson = ControllerContext.getContext().getOtherDataJson();
            JSONObject other = JSONObject.fromObject(otherDataJson);
            keyUserId = (String) other.get("factorId");
        } else if ("member".equals(userType) || "memberFriend".equals(userType)) {
            keyUserId = ControllerContext.getContext().getCurrentUserId();
        }

        String key = "create_recharge_" + userType+"_"+keyUserId;
        try {
            JedisUtil.whileGetLock(key, 60);

            if (totalPrice <= 0) {
                throw new UserOperateException(400, "请输入正确的金额");
            }
            if (!Pattern.matches("^\\d+(?:\\.\\d{1,2})?$", ControllerContext.getPString("totalPrice"))) {
                throw new UserOperateException(400, "充值金额不能超过两位小数");
            }

            //所有充值在prepay()里已重新指定sellerId为平台账号
            if ("seller".equals(userType)) {
                orderType = 3;
                memberId = PAY_ADMIN_ID;//收钱方
                sellerId = ControllerContext.getContext().getCurrentSellerId();//付钱方
                checkUser("Seller",sellerId);
            } else if ("factor".equals(userType)) {
                String otherDataJson = ControllerContext.getContext().getOtherDataJson();
                JSONObject other = JSONObject.fromObject(otherDataJson);
                if (StringUtils.mapValueIsEmpty(other, "factorId")) {
                    throw new UserOperateException(400, "获取用户数据失败");
                }
                orderType = 4;
                memberId = PAY_ADMIN_ID;//收钱方
                sellerId = (String) other.get("factorId");//付钱方
                checkUser("Factor",sellerId);
            } else if ("member".equals(userType)) {
                orderType = 5;
                memberId = ControllerContext.getContext().getCurrentUserId();
                sellerId = memberId;

                checkUser("Member",memberId);

                if(!"SUCCESS".equals(checkUserAccount(totalPrice,"Member",memberId,true).get("status").toString())){
                    throw new UserOperateException(400, "您的积分已达到5000上限");
                }
    //            Message msg = Message.newReqMessage("1:GET@/crm/Member/getMemberIsRealName");
    //            msg.getContent().put("memberId", memberId);
    //            msg = ServiceAccess.callService(msg);
    //            List<Object> list = (List) msg.getContent().get("items");
    //            Map<String, Object> map = (Map) list.get(0);
    //            if (map.get("isRealName") == null || "null".equals(map.get("isRealName").toString()) || !(boolean) map.get("isRealName")) {
    //                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请先实名认证再进行充值!");
    //            }
            } else if ("memberFriend".equals(userType)) {
                orderType = 6;
                memberId = ControllerContext.getContext().getCurrentUserId();//支付钱的一方
                String friendMobile = ControllerContext.getPString("friendMobile");
                Message msgFriend = Message.newReqMessage("1:GET@/crm/Member/getMemberInfoByMobile");
                msgFriend.getContent().put("mobile", friendMobile);
                msgFriend = ServiceAccess.callService(msgFriend);
                if (msgFriend.getContent() == null) {
                    throw new UserOperateException(400, "无法获取朋友账号,请核对账号");
                }
                //如果填写的手机号码是当前用户的手机号码,则不支持
                if (ControllerContext.getContext().getCurrentUserId().equals(msgFriend.getContent().get("_id"))) {
                    throw new UserOperateException(400, "此页面不支持为自己充值");
                }
                sellerId = (String) msgFriend.getContent().get("_id");//接收钱的一方

                checkUser("Member",sellerId);
                checkUser("Member",memberId);

                payType = ControllerContext.getPString("payType");
                if(!Pattern.matches("^(4)|(10)$",payType)){
                    throw new UserOperateException(400, "错误的支付方式");
                }

                if(!"SUCCESS".equals(checkUserAccount(totalPrice,"Member",sellerId,true).get("status").toString())){
                    throw new UserOperateException(400, "代购用户积分已达到上限");
                }

    //            if (StringUtils.isNotEmpty(payType) && "3".equals(payType)) {
    //                String payPwd = ControllerContext.getPString("payPwd");
    //
    ////                Message msg = Message.newReqMessage("1:GET@/crm/Member/getMemberIsRealName");
    ////                msg.getContent().put("memberId", memberId);
    ////                msg = ServiceAccess.callService(msg);
    ////                List<Object> list = (List) msg.getContent().get("items");
    ////                Map<String, Object> map = (Map) list.get(0);
    ////                if (StringUtils.mapValueIsEmpty(map, "isRealName") || !(boolean) map.get("isRealName")) {
    ////                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请先实名认证再进行充值!");
    ////                }
    //                //检查会员支付密码
    //                Message msgPwd = Message.newReqMessage("1:GET@/crm/Member/checkMemberPayPwd");
    //                msgPwd.getContent().put("memberId", memberId);
    //                msgPwd.getContent().put("payPwd", payPwd);
    //                ServiceAccess.callService(msgPwd);
    //            }
            } else {
                throw new UserOperateException(400, "生成订单失败");
            }

            Map<String, Object> order = new HashMap<>();
            String orderId = ZQUidUtils.genUUID();
            order.put("_id", orderId);
            order.put("memberId", memberId);
            order.put("orderNo", ZQUidUtils.generateOrderNo());
            order.put("totalPrice", totalPrice);
            order.put("payMoney", 0.0);
            order.put("sellerId", sellerId);
            order.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);
            order.put("createTime", System.currentTimeMillis());
            order.put("bookingTime", System.currentTimeMillis());
            order.put("orderType", orderType);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);

            if ("memberFriend".equals(userType) && "3".equals(payType)) {
                Map<String, Object> payInfo = new HashMap<>();
                payInfo.put("totalFee", totalPrice);
                payInfo.put("payType", 3);
                toResult(Response.Status.OK.getStatusCode(), memberRechargeFriend(order, payInfo));
            } else {
                toResult(Response.Status.OK.getStatusCode(), order);
            }
        } finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 创建订单:会员自己激活会员
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/createMemberActive")
    public void createMemberActive() throws Exception {
        String payType = ControllerContext.getPString("payType");
        String cardNo = ControllerContext.getPString("cardNo");
        String memberId = ControllerContext.getContext().getCurrentUserId();
        double activeMoney = ParameterAction.getValueOne("activeMoney");

        String key = "active_" + memberId;
        try {
            JedisUtil.whileGetLock(key, 60);

            List<String> returnFields = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            if(StringUtils.isNotEmpty(cardNo)){
                returnFields.add("_id");
                params.add(cardNo);
                String sql = "select _id from Member where cardNo=?";
                List<Map<String, Object>> mobileRe = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
                if (mobileRe!=null && mobileRe.size()>0){
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该会员卡已被注册");
                }
            }

            //一个会员只能拥有一张会员卡
            returnFields.clear();
            returnFields.add("memberId");
            params.clear();
            params.add(memberId);
            String sql = "select" +
                    " t1.memberId" +
                    " from MemberCard t1" +
                    " left join Member t2 on t1.memberId=t2._id" +
                    " where t2._id=?";
            List<Map<String, Object>> re3 = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            if (re3.size() != 0) {
                throw new UserOperateException(400, "激活失败,该会员已经绑定会员卡");
            }

            String memberRemark = "";
            // 若输入了卡号,则查找会员团队关系是否关联了服务站，卡号是否归属于这个服务站
            Map<String,Object> p = new HashMap<>();
            p.put("memberId",memberId);
            Map<String,Object> team = MysqlDaoImpl.getInstance().findOne2Map("Team",p,null,null);
            if(team!=null && team.size()!=0 && !StringUtils.mapValueIsEmpty(team,"factorId")){
                String factorId = team.get("factorId").toString();
                String belongAreaValue = "";
                if("A-000001".equals(factorId)){
                    belongAreaValue = "_A-000001_";
                }else{
                    JSONObject factor = ServiceAccess.getRemoveEntity("account","Factor",factorId);
                    if(factor==null || factor.size()==0){
                        throw new UserOperateException(500,"服务站不存在");
                    }

                    belongAreaValue = factor.get("areaValue").toString();
                }

                if(StringUtils.isNotEmpty(cardNo)){
                    Message msg = Message.newReqMessage("1:GET@/crm/Member/checkMemberCardIsNotActive");
                    msg.getContent().put("activeCard",cardNo);
                    msg.getContent().put("belongAreaValue",belongAreaValue);
                    msg.getContent().put("memberId",memberId);
                    ServiceAccess.callService(msg).getContent();
                }else{
                    cardNo = "";
                }

                memberRemark = "cardNo="+cardNo+",belongAreaValue="+belongAreaValue;
            }else if(StringUtils.isNotEmpty(cardNo)){
                //判断是否存在这张会员卡
                returnFields.clear();
                returnFields.add("receive");
                returnFields.add("areaValue");

                params.clear();
                params.add(cardNo);
                params.add(cardNo);
                sql = "select t1.receive" +
                        ",t2.areaValue" +
                        " from CardField t1" +
                        " left join Factor t2 on t1.receive = t2._id"+
                        " where cast(t1.startCardNo as SIGNED INTEGER)<=? and cast(t1.endCardNo as SIGNED INTEGER)>=?";
                List<Map<String, Object>> factor = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

                if(factor!=null && factor.size()!=0 && factor.get(0)!=null){
                    String belongAreaValue = factor.get(0).get("areaValue").toString();
                    Message msg = Message.newReqMessage("1:GET@/crm/Member/checkMemberCardIsNotActive");
                    msg.getContent().put("activeCard",cardNo);
                    msg.getContent().put("belongAreaValue",belongAreaValue);
                    msg.getContent().put("memberId",memberId);
                    ServiceAccess.callService(msg).getContent();
                    memberRemark = "cardNo="+cardNo+",belongAreaValue="+belongAreaValue;
                }
            }

            if (StringUtils.isNotEmpty(payType) && "3".equals(payType)) {
                String payPwd = ControllerContext.getPString("payPwd");
                Message msgPwd = Message.newReqMessage("1:GET@/crm/Member/checkMemberPayPwd");
                msgPwd.getContent().put("memberId", memberId);
                msgPwd.getContent().put("payPwd", payPwd);
                ServiceAccess.callService(msgPwd);
            }
            Map<String, Object> order = new HashMap<>();
            String orderId = ZQUidUtils.genUUID();
            order.put("_id", orderId);
            order.put("memberId", memberId);
            order.put("orderNo", ZQUidUtils.generateOrderNo());
            order.put("totalPrice", activeMoney);
            order.put("payMoney", 0.0);
            order.put("sellerId", "A-000001");
            order.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);
            order.put("createTime", System.currentTimeMillis());
            order.put("bookingTime", System.currentTimeMillis());
            order.put("orderType", 8);
            order.put("memberRemark", memberRemark);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);

            if ("3".equals(payType)) {
                Map<String, Object> payInfo = new HashMap<>();
                payInfo.put("totalFee", activeMoney);
                payInfo.put("payType", 3);
                toResult(Response.Status.OK.getStatusCode(), updateMemberActive(order, payInfo));
            } else {
                toResult(Response.Status.OK.getStatusCode(), order);
            }
        } finally {
            JedisUtil.del(key);
        }

    }

    /**
     * 服务站:会员激活
     *
     * @throws Exception
     */
    @POST
    @Path("/createMemberCardByFactor")
    public void createMemberCardByFactor() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        String memberCardId = ControllerContext.getPString("memberCardId");
        int payType = ControllerContext.getPInteger("payType");

        double activeMoney = ParameterAction.getValueOne("activeMoney");

        String key = "active_" + memberId;
        try {
            JedisUtil.whileGetLock(key, 60);
            //服务站详细信息
            Message msg3 = Message.newReqMessage("1:GET@/account/Factor/getFactorInfo");
            Map<String, Object> factorInfo = ServiceAccess.callService(msg3).getContent();
            String factorId = (String) factorInfo.get("_id");
            String factorName=(String) factorInfo.get("name");

            Message msg = Message.newReqMessage("1:GET@/crm/Member/getBelongAreaById");
            msg.getContent().put("_id", memberId);
            JSONObject memberBelongArea = ServiceAccess.callService(msg).getContent();
            String belongArea = memberBelongArea.getString("belongArea");

            if(belongArea !=null && !belongArea.equals(factorName)){
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该会员不归属于本服务站!");
            }else {
                if (payType != 3 && payType != 10 && payType != 4) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请选择支付方式!");
                }

                if (payType == 3) {
                    String payPwd = ControllerContext.getPString("payPwd");
                    if (StringUtils.isEmpty(payPwd)) {
                        throw new UserOperateException(400, "请输入支付密码");
                    }
                    String payPwdDigest = MessageDigestUtils.digest(payPwd);

                    if (factorInfo == null || factorInfo.size() == 0 || !payPwdDigest.equals(factorInfo.get("cashPassword"))) {
                        throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码错误!");
                    }
                }

                Map<String,Object> paramsItem = new HashMap<>();
                paramsItem.put("factorId",factorId);
                paramsItem.put("memberCardId",memberCardId);
                paramsItem.put("memberId",memberId);
                paramsItem.put("areaValue",factorInfo.get("areaValue"));
                paramsItem.put("activeMoney",activeMoney);
                paramsItem.put("payType",payType);
                checkActive(paramsItem);

                JSONObject mobileRe = ServiceAccess.getRemoveEntity("crm","Member",memberId);
                if (mobileRe == null || mobileRe.size() == 0){
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该会员没有注册");
                }else if(!StringUtils.mapValueIsEmpty(mobileRe,"isBindCard") && Boolean.parseBoolean(mobileRe.get("isBindCard").toString())){
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该会员已激活");
                }


                Map<String, Object> order = new HashMap<>();
                String orderId = ZQUidUtils.genUUID();
                order.put("_id", orderId);
                order.put("memberId", memberId);
                order.put("orderNo", ZQUidUtils.generateOrderNo());
                order.put("totalPrice", activeMoney);
                order.put("payMoney", 0.0);
                order.put("sellerId", factorId);
                order.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);
                order.put("createTime", System.currentTimeMillis());
                order.put("bookingTime", System.currentTimeMillis());
                order.put("orderType", 7);
                order.put("memberRemark", memberCardId);//客户备注存放了会员卡号
                MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);

                if (payType == 3) {
                    Map<String, Object> payInfo = new HashMap<>();
                    payInfo.put("totalFee", activeMoney);
                    payInfo.put("payType", 3);
                    toResult(Response.Status.OK.getStatusCode(), updateMemberCardByFactor(order, payInfo));
                } else {
                    toResult(Response.Status.OK.getStatusCode(), order);
                }
            }
        } finally {
            JedisUtil.del(key);
        }
    }

    @POST
    @Path("/checkActive")
    public void checkActive() throws Exception {
        checkActive((Map<String,Object>)ControllerContext.getContext().getReq().getContent());
    }

    /**
     * 校验激活卡数据
     * @param item
     * @return
     * @throws Exception
     */
    public void checkActive(Map<String,Object> item) throws Exception {
        String factorId = item.get("factorId").toString();
        String areaValue = item.get("areaValue").toString();
        String memberId = item.get("memberId").toString();
        String memberCardId = item.get("memberCardId").toString();
        int payType = Integer.parseInt(item.get("payType").toString());
        double activeMoney = Double.parseDouble(item.get("activeMoney").toString());

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();

        //查询服务站余额是否足够
        Map<String,Object> p = new HashMap<>();
        p.put("factorId",factorId);
        Map<String, Object> account = MysqlDaoImpl.getInstance().findOne2Map("FactorMoneyAccount", p, new String[]{"cashCount"},Dao.FieldStrategy.Include);
        if (payType == 3 && (account == null || account.size() == 0 || Double.valueOf(account.get("cashCount").toString()) < activeMoney)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "服务站账户余额不足"+activeMoney+",请充值!");
        }

        //判断是否存在这张会员卡
        returnFields.clear();
        returnFields.add("_id");
        returnFields.add("startCardNo");
        returnFields.add("endCardNo");

        params.clear();
        params.add(memberCardId);
        params.add(memberCardId);
        params.add(memberCardId);
        params.add(memberCardId);
        params.add(areaValue);
        String sql = "select" +
                " _id" +
                ",startCardNo" +
                ",endCardNo" +
                " from CardField" +
                " where ((cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?)" +
                " or (cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?))" +
                " and belongAreaValue = ?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (re.size() == 0) {
            throw new UserOperateException(400, "激活失败,没有此卡号");
        }

        //一个会员只能拥有一张会员卡
        returnFields.clear();
        returnFields.add("memberId");
        params.clear();
        params.add(memberId);
        sql = "select memberId from MemberCard where memberId=?";
        List<Map<String, Object>> re3 = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (re3.size() != 0) {
            throw new UserOperateException(400, "激活失败,该会员已经绑定会员卡");
        }

        //判断会员卡是否已经使用
        returnFields.clear();
        returnFields.add("isActive");
        params.clear();
        params.add(memberCardId);
        String sql2 = "select isActive from MemberCard where memberCardId=?";
        List<Map<String, Object>> re2 = MysqlDaoImpl.getInstance().queryBySql(sql2, returnFields, params);
        if (re2.size() != 0) {
            throw new UserOperateException(400, "激活失败,该卡号已被激活");
        }
    }

    /**
     * 检查传入的商品规格是否与数据库一致,并计算商品价格
     *
     * @param product 商品
     * @param spec    商品规格
     * @return 计算规格后的商品单价
     * @throws Exception
     */
    public double getPriceBySpec(Map<String, Object> product, JSONArray spec) throws Exception {
        if (StringUtils.mapValueIsEmpty(product, "spec")) {
            throw new UserOperateException(500, "获取规格数据失败");
        }
        //判断选择的规格数量是否与商品的规格数据一致
        JSONArray specJsonP = JSONArray.fromObject(product.get("spec").toString());
        if (spec.size() != specJsonP.size()) {
            throw new UserOperateException(500, "获取规格数据失败");
        }

        JSONObject specItem;
        JSONArray itemsP;
        JSONArray addMoneyP;
        for (int i = 0, len = spec.size(); i < len; i++) {
            specItem = JSONObject.fromObject(spec.get(0));
            itemsP = JSONArray.fromObject(JSONObject.fromObject(specJsonP.get(i)).get("items"));
            addMoneyP = JSONArray.fromObject(JSONObject.fromObject(specJsonP.get(i)).get("addMoney"));

            if (itemsP.size() != addMoneyP.size() && len != itemsP.size()) {
                throw new UserOperateException(500, "获取规格数据失败");
            }
            //循环判断选择的规格是否与数据的商品规格一致
            for (int j = 0, lenJ = itemsP.size(); j < lenJ; j++) {
                if (specItem.get("items").toString().equals(itemsP.get(j))) {
                    double a = BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(specItem.get("addMoney").toString()));
                    double b = BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(addMoneyP.get(j).toString()));
                    if (a != b) {
                        throw new UserOperateException(500, "获取规格数据失败");
                    }
                }
            }
        }
        //获取商品基础价格
        double totalPrice = BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(product.get("salePrice").toString()));//订单总金额
        double tempAddMoney = 0;//临时数据:'加价'

        for (int i = 0, len = spec.size(); i < len; i++) {
            if(StringUtils.mapValueIsEmpty(JSONObject.fromObject(spec.get(i)),"addMoney")){
                throw new UserOperateException(500, "请选择规格类型");
            }
            tempAddMoney = Double.valueOf(JSONObject.fromObject(spec.get(i)).get("addMoney").toString());
            totalPrice = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(tempAddMoney, totalPrice));
        }

        return totalPrice;
    }

    /**
     * 删除未支付的订单,订单产品记录,pay记录
     *
     * @param orderId
     * @throws Exception
     */
    public void delNotPayOrder(String orderId,String childOrderId) throws Exception {

        MysqlDaoImpl.getInstance().remove("OrderInfo",orderId);
        MysqlDaoImpl.getInstance().remove("OrderInfo",childOrderId);
//        //获取订单主表下所有子表
//        sql = "select _id from OrderInfo where pid=?";
//        p.add(orderId);
//        r.add("_id");
//        List<Map<String,Object>> oldOrderChildList = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
//        if(oldOrderChildList==null || oldOrderChildList.size()==0){
//            return;
//        }
//        //删除订单总表的子表
//        for(int i=0,len=oldOrderChildList.size();i<len;i++){
//            MysqlDaoImpl.getInstance().remove("OrderInfo", oldOrderChildList.get(i).get("_id").toString());
//        }
        //获取订单主表
//        Map<String, Object> oldOrder = MysqlDaoImpl.getInstance().findById2Map(entityName, orderId, null, null);
//        if (oldOrder != null && oldOrder.size() != 0) {
//            MysqlDaoImpl.getInstance().remove("OrderInfo", orderId);
//        }

        //获取订单产品
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(childOrderId);
        r.add("_id");
        String sql="select _id from OrderItem where orderId=?";
        List<Map<String,Object>> oldOrderItemList = MysqlDaoImpl.getInstance().queryBySql(sql,r, p);
        for(int i=0,len=oldOrderItemList.size();i<len;i++){
            MysqlDaoImpl.getInstance().remove("OrderItem", oldOrderItemList.get(i).get("_id").toString());
        }
    }

    /**
     * 检查购买的商品是否支持余额支付
     * 获取购物车数据,检查每个商品是否属于公益专区或天天特价
     * @param cartList
     * @throws Exception
     */
    public void checkProductType(List<Map<String,Object>> cartList) throws Exception {
        List<String> returnFields = new ArrayList<>();
        returnFields.add("name");
        returnFields.add("te");
        returnFields.add("gongyi");
        List<Object> params = new ArrayList<>();
        String paramsStr = "";
        for(int i=0,len=cartList.size();i<len;i++){
            List<Map<String,Object>> itemList = (List<Map<String,Object>>)cartList.get(i).get("product");
            for(int j=0,jlen=itemList.size();j<jlen;j++){
                params.add(itemList.get(j).get("productId").toString());
                paramsStr+="?,";
            }
        }
        paramsStr=paramsStr.substring(0,paramsStr.length()-1);
        String sql = "select name,te,gongyi from ProductInfo where _id in ("+paramsStr+")";
        List<Map<String,Object>> productList = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(productList==null || productList.size()==0){
            throw new UserOperateException(500,"获取商品信息失败");
        }

        for(int i=0,len=productList.size();i<len;i++){
            if((StringUtils.mapValueIsEmpty(productList.get(i),"te") || !Boolean.valueOf(productList.get(i).get("te").toString())) &&
                    (StringUtils.mapValueIsEmpty(productList.get(i),"gongyi") || !Boolean.valueOf(productList.get(i).get("gongyi").toString()))){
                String text = productList.get(i).get("name").toString();
                if(text.length()>5){
                    text=text.substring(0,5)+"...";
                }
                text="("+text+")";
//                text="第"+(i+1)+"个商品("+text+")";
                throw new UserOperateException(500,"'"+text+"'不属于公益或特价的商品,您不能用积分购买");
            }
        }
    }

    /**
     * 生成在线会员购买订单
     * @throws Exception
     */
    @POST
    @Member
    @Path("/createOnlineOrder")
    public void createOnlineOrder() throws Exception {
            String payPwd = ControllerContext.getPString("payPwd");
            String payType = ControllerContext.getPString("payType");
            String memberRemark = ControllerContext.getPString("memberRemark");
            JSONArray linkIdArr = (JSONArray) ControllerContext.getContext().getReq().getContent().get("linkIdArr");
            String addressId = ControllerContext.getPString("addressId");
            String pid = ControllerContext.getPString("pid");
            String childOrderId = ControllerContext.getPString("childOrderId");
            String cartId = ControllerContext.getPString("cartId");
            String memberId = ControllerContext.getContext().getCurrentUserId();

            String key = "create_order_" + pid;
            try {
                JedisUtil.whileGetLock(key, 60);
                //        if(true){
                //            throw new UserOperateException(500,"暂未开放线上支付");
                //        }

                //若传childOrderId,说明是已经生成过的未支付的订单,先删除之前的pay支付记录,订单产品记录,订单主表
                if (StringUtils.isNotEmpty(childOrderId)) {
                    delNotPayOrder(pid, childOrderId);
                }
                pid = ZQUidUtils.genUUID();
                if (StringUtils.isEmpty(cartId)) {
                    throw new UserOperateException(500, "获取购物车数据失败");
                }
                if (!Pattern.matches("^(3)|(4)|(10)|(18)$", payType)) {
                    throw new UserOperateException(400, "错误的支付方式");
                }
                if (StringUtils.isNotEmpty(memberRemark) && memberRemark.length() > 50) {
                    throw new UserOperateException(400, "备注在50字以内");
                }

                //检查是否激活会员
                checkUser("Member", memberId);

                Message msg;

                //检查会员支付密码
                if ("3".equals(payType)) {
                    msg = Message.newReqMessage("1:GET@/crm/Member/checkMemberPayPwd");
                    msg.getContent().put("memberId", memberId);
                    msg.getContent().put("payPwd", payPwd);
                    ServiceAccess.callService(msg);
                }

                //获取收货人地址
                msg = Message.newReqMessage("1:GET@/crm/MemberAddress/getAddressById");
                msg.getContent().put("_id", addressId);
                JSONObject memberAddress = ServiceAccess.callService(msg).getContent();
                if (memberAddress == null || memberAddress.size() == 0) {
                    throw new UserOperateException(500, "请设置收货地址");
                }

                //获取购物车数据
                List<Map<String, Object>> selectedCart = new CartAction().getMyCart(cartId);
                if (selectedCart == null || selectedCart.size() == 0) {
                    throw new UserOperateException(500, "获取购物车数据失败");
                }
                //2017/10/9 更改：一次只能处理一个商家的订单
                //        if(selectedCart.size()>1){
                //            throw new UserOperateException(500, "暂不支持多个商家同时支付");
                //        }
                //检查是否属于余额支付类型的产品
                if ("3".equals(payType)) {
                    checkMoreSellerPayType(selectedCart);//检查商家是否支持
                    checkProductType(selectedCart);
                }

                //远程查询各个商家下的卡券 和 是否支持余额支付
                if (linkIdArr != null && linkIdArr.size() != 0) {
                    JSONObject coupon;
                    JSONObject linkItem;
                    for (int i = 0, len = selectedCart.size(); i < len; i++) {
                        linkItem = (JSONObject) linkIdArr.get(i);
                        //不能保证selectedCart的顺序与linkItem一致,所以循环检查一遍
                        int selectedCartIndex = -1;
                        for (int j = 0; j < len; j++) {
                            if (selectedCart.get(j).get("sellerId").toString().equals(linkItem.get("sellerId").toString())) {
                                if (selectedCartIndex != -1) {
                                    throw new UserOperateException(500, "同一商家下只能使用一张卡券");
                                }
                                selectedCartIndex = j;
                            }
                        }
                        if (selectedCartIndex == -1) {
                            throw new UserOperateException(500, "卡券信息与商家不匹配");
                        }

                        if (StringUtils.mapValueIsEmpty(linkItem, "linkId")) {
                            selectedCart.get(i).put("couponLinkId", "");
                            selectedCart.get(i).put("couponValue", 0);
                            continue;
                        }
                        Message msgPwd = Message.newReqMessage("1:GET@/crm/Coupon/getCouponByLinkId");
                        msgPwd.getContent().put("isGetAll", false);
                        msgPwd.getContent().put("linkId", linkItem.get("linkId"));
                        coupon = ServiceAccess.callService(msgPwd).getContent();
                        selectedCart.get(selectedCartIndex).put("couponCondition", coupon.get("condition"));
                        selectedCart.get(selectedCartIndex).put("couponValue", coupon.get("value"));
                        selectedCart.get(selectedCartIndex).put("couponLinkId", linkItem.get("linkId"));
                    }
                }

                //总订单金额
                double totalPrice = 0.0;
                //根据不同的商家,分别累计各个商家的商品的总价格
                for (int i = 0, len = selectedCart.size(); i < len; i++) {
                    double sellerPrice = 0.0;//商家下所有产品订单金额
                    List<Map<String, Object>> productList = (List<Map<String, Object>>) selectedCart.get(i).get("product");
                    for (int j = 0, jlen = productList.size(); j < jlen; j++) {
                        sellerPrice = BigDecimalUtil.add(sellerPrice, (double) productList.get(j).get("price"));
                    }
                    //抵扣该商家的卡券
                    if (!StringUtils.mapValueIsEmpty(selectedCart.get(i), "couponCondition")) {
                        if (sellerPrice < Double.valueOf(selectedCart.get(i).get("couponCondition").toString())) {
                            throw new UserOperateException(500, "订单金额未满足卡券条件");
                        }
                        if (StringUtils.mapValueIsEmpty(selectedCart.get(i), "couponValue")) {
                            throw new UserOperateException(500, "获取卡券数据失败");
                        }
                        sellerPrice = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(sellerPrice
                                , -Double.valueOf(selectedCart.get(i).get("couponValue").toString())));
                    }

                    double integralRate = BigDecimalUtil.divide(Double.valueOf(selectedCart.get(i).get("integralRate").toString()), 100);//积分率
                    double brokerageCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.multiply(sellerPrice, integralRate));//养老金
                    double pensionMoney = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.divide(brokerageCount, 2));//返回给会员的养老金
                    //累计订单总金额
                    totalPrice = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(totalPrice, sellerPrice));

                    String orderId = ZQUidUtils.genUUID();
                    orderId = orderId.length() > 30 ? orderId.substring(0, 30) : orderId;
                    String orderNo = ZQUidUtils.generateOrderNo();
                    //生成订单数据
                    Map<String, Object> order = new HashMap<>();
                    //如果只有一个商家的订单,则留言才生效
                    if (len == 1) {
                        order.put("memberRemark", memberRemark);//客户备注
                    }
                    order.put("_id", orderId);
                    pid = pid.length() > 30 ? pid.substring(0, 30) : pid;
                    order.put("pid", pid);//子订单全部归属一个总订单
                    order.put("creator", memberId);
                    order.put("memberId", memberId);
                    order.put("orderNo", orderNo);
                    order.put("totalPrice", sellerPrice);
                    order.put("payMoney", 0.0);
                    order.put("payType", payType);
                    order.put("pensionMoney", pensionMoney);
                    order.put("brokerageCount", brokerageCount);
                    order.put("sellerId", selectedCart.get(i).get("sellerId"));
                    order.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);
                    order.put("createTime", System.currentTimeMillis());
                    order.put("orderType", 11);
                    order.put("score", selectedCart.get(i).get("integralRate"));
                    order.put("sendArea", memberAddress.get("areaValue"));
                    order.put("sendAddress", memberAddress.get("area").toString() + memberAddress.get("address").toString());
                    order.put("sendContact", memberAddress.get("name"));
                    order.put("sendContactPhone", memberAddress.get("phone"));
                    order.put("sendPostcode", memberAddress.get("postcode"));
                    order.put("couponId", selectedCart.get(i).get("couponLinkId"));
                    order.put("couponPrice", selectedCart.get(i).get("couponValue"));
                    MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);


                    //生成订单产品数据
                    for (Map<String, Object> s : productList) {
                        Map<String, Object> orderItem = new HashMap<>();
                        orderItem.put("_id", UUID.randomUUID().toString());
                        orderItem.put("count", s.get("count"));
                        orderItem.put("createTime", System.currentTimeMillis());
                        orderItem.put("creator", memberId);
                        orderItem.put("icon", s.get("productIcon"));
                        orderItem.put("memberId", memberId);
                        orderItem.put("memberRemark", memberRemark);
                        orderItem.put("name", s.get("productName"));
                        orderItem.put("orderId", orderId);
                        orderItem.put("price", s.get("price"));
                        orderItem.put("productId", s.get("productId"));
                        orderItem.put("selectSpec", s.get("spec"));
                        orderItem.put("sellerId", selectedCart.get(i).get("sellerId"));
                        MysqlDaoImpl.getInstance().saveOrUpdate("OrderItem", orderItem);
                    }
                    //生成账户明细
                    createMemberMoneyLog(memberId, memberId, orderNo, sellerPrice, "3", ControllerContext.getPInteger("payType"));
                    //余额支付,直接跳转更新方法
                    if ("3".equals(payType)) {
                        Map<String, Object> payInfo = new HashMap<>();
                        payInfo.put("totalFee", sellerPrice);
                        payInfo.put("payType", 3);
                        updateOnlineOrder(order, payInfo);
                    }
                }
                //删除已支付的购物车信息
                new CartAction().deleteMyCart(cartId);


                Map<String, Object> allOrder = new HashMap<>();
                //若不是余额支付,则提交一个总表;若是余额支付,则返回一个结果给页面处理
                if (!"3".equals(payType)) {
                    allOrder.put("_id", pid);
                    allOrder.put("pid", -1);
                    allOrder.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);
                    allOrder.put("creator", memberId);
                    allOrder.put("memberId", memberId);
                    allOrder.put("sellerId", "moreSeller");
                    allOrder.put("orderNo", ZQUidUtils.generateOrderNo());
                    allOrder.put("totalPrice", totalPrice);
                    allOrder.put("orderType", 11);
                    allOrder.put("payType", payType);
                    allOrder.put("createTime", System.currentTimeMillis());
                    MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", allOrder);
                    toResult(Response.Status.OK.getStatusCode(), allOrder);
                } else {
                    allOrder.put("orderStatus", OrderInfoAction.ORDER_TYPE_CONFIRM);
                    toResult(Response.Status.OK.getStatusCode(), allOrder);
                }
            } finally {
                JedisUtil.del(key);
            }
    }

    /**
     * 在线订单:更新多个商家的订单,会员第三方支付
     *
     * @param orderInfo
     * @param payInfo
     * @return
     * @throws Exception
     */
    public static Map<String, Object> updateOnlineOrderByMoreSeller(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        if(Double.parseDouble(orderInfo.get("totalPrice").toString())!=Double.parseDouble(payInfo.get("totalFee").toString())){
            throw new UserOperateException(500, "支付金额与订单金额不符");
        }

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String sql = "select _id from OrderInfo where pid=?";
        p.add(orderInfo.get("_id").toString());
        r.add("_id");
        List<Map<String,Object>> childList = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);

        //循环查询此总订单下的子订单,并更新
        Map<String, Object> childOrder;
        Map<String,Object> childPay = new HashMap<>();
        for (int i = 0, len = childList.size(); i < len; i++) {
            if (StringUtils.mapValueIsEmpty(childList.get(i),"_id")) {
                throw new UserOperateException(500, "获取子订单失败");
            }
            childOrder = MysqlDaoImpl.getInstance().findById2Map("OrderInfo",childList.get(i).get("_id").toString(), null, null);
            if (childOrder == null || childOrder.size() == 0) {
                throw new UserOperateException(500, "获取子订单失败");
            }
            childPay.put("totalFee",childOrder.get("totalPrice"));
            updateOnlineOrder(childOrder,childPay);

            childPay.clear();
            childOrder.clear();
        }
        //返回总表的结果给页面做成功处理
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_CONFIRM);
        orderInfo.put("bookingTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo",orderInfo);
        return orderInfo;
    }


    /**
     * 根据orderId获取orderItem
     * 返回商品名字,销量
     * @param orderId
     * @return
     * @throws Exception
     */
    public static List<Map<String,Object>> getOrderItemByOrderId(String orderId) throws Exception{
        //获取订单产品
        String sql = "select t1.name,t1.count,t1.productId,t2.saleCount" +
                " from OrderItem t1 left join ProductInfo t2 on t1.productId=t2._id" +
                " where t1.orderId=?" +
                " order by t1.sellerId desc";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("name");
        returnFields.add("count");
        returnFields.add("productId");
        returnFields.add("saleCount");

        List<Map<String, Object>> orderItem = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if (orderItem == null || orderItem.size() == 0) {
            throw new UserOperateException(500, "获取订单产品数据失败");
        }
        return orderItem;
    }

    /**
     * 获取发送短信的多个商品名称
     * @param orderItem
     * @return
     * @throws Exception
     */
    public static String getProductName(List<Map<String,Object>> orderItem) throws Exception{
        String productName = "";//发送短信的商品内容
        //累计/保存 商品数量
        for(Map<String,Object> item : orderItem){
            if(StringUtils.mapValueIsEmpty(item,"productId") || StringUtils.mapValueIsEmpty(item,"saleCount")
                    || StringUtils.mapValueIsEmpty(item,"name")){
                throw new UserOperateException(500, "获取商品数据失败");
            }
            //如果没有重复的商品名字,才添加到短信内容中
            if(!productName.contains(item.get("name").toString())){
                productName+=item.get("name").toString()+"、";
            }
        }
        if(StringUtils.isEmpty(productName)){
            return "商品";
        }
        productName=productName.substring(0,productName.length()-1);
        if(productName.length()>10){
            productName="商品";
        }
        return productName;
    }

    /**
     * 在线订单:更新,会员支付
     *
     * @throws Exception
     */
    public static Map<String, Object> updateOnlineOrder(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        if (!"1".equals(orderInfo.get("orderStatus").toString())) {
            throw new UserOperateException(500, "此订单不是未支付的订单");
        }
        if (!"11".equals(orderInfo.get("orderType").toString())) {
            throw new UserOperateException(500, "此订单不是在线交易订单");
        }
//        if (!orderInfo.get("totalPrice").equals(payInfo.get("totalFee"))) {
        if (Double.valueOf(orderInfo.get("totalPrice").toString()).compareTo(Double.valueOf(payInfo.get("totalFee").toString())) != 0) {
            throw new UserOperateException(500, "支付金额与订单金额不符");
        }

        if (!StringUtils.mapValueIsEmpty(orderInfo, "couponId")) {
            //核销卡券
            Message msg = Message.newReqMessage("1:GET@/crm/Coupon/updateCouponSerial");
            msg.getContent().put("userType", "member");
            msg.getContent().put("sellerId", orderInfo.get("sellerId"));
            msg.getContent().put("couponNo", orderInfo.get("couponId"));
            ServiceAccess.callService(msg).getContent();
        }

        int payType = Integer.valueOf(orderInfo.get("payType").toString());
        double payMoney = Double.valueOf(payInfo.get("totalFee").toString());
        orderInfo.put("payMoney", payMoney);
        orderInfo.put("payType", payType);
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_CONFIRM); //2:商家已确认(会员已支付)
        orderInfo.put("bookingTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);

        //发送短信
        sendPayInfo(orderInfo.get("sellerId").toString(), orderInfo.get("memberId").toString(),
                orderInfo.get("totalPrice").toString(),
                getProductName(getOrderItemByOrderId(orderInfo.get("_id").toString()))
                , ",等待商家发货");
        sendPayBookingSeller(orderInfo.get("sellerId").toString());

        return orderInfo;
    }

    /**
     * 在线订单:商家更新订单:发货
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/updateOnlineOrderBySeller")
    public void updateOnlineOrderBySeller() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        String express = ControllerContext.getPString("express");
        String expressNo = ControllerContext.getPString("expressNo");
        double freight;

        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(500, "获取订单失败");
        }
        if (StringUtils.isEmpty(express)) {
            throw new UserOperateException(500, "请填写快递公司");
        }
        if (StringUtils.isEmpty(expressNo)) {
            throw new UserOperateException(500, "请填写快递单号");
        }
        if (StringUtils.isEmpty(ControllerContext.getPString("freight"))) {
            freight = 0;
        } else {
            freight = ControllerContext.getPDouble("freight");
        }
        if (freight < 0 || !Pattern.matches("^\\d+(?:\\.\\d{1,2})?$", ControllerContext.getPString("freight"))) {
            throw new UserOperateException(500, "请填写正确的运费");
        }

        Map<String, Object> order = MysqlDaoImpl.getInstance().findById2Map("OrderInfo", orderId, null, null);
        if (!"2".equals(order.get("orderStatus").toString())) {
            throw new UserOperateException(500, "获取订单数据失败");
        }
        if (!"11".equals(order.get("orderType").toString())) {
            throw new UserOperateException(500, "此订单不是在线交易订单");
        }

        order.put("_id", orderId);
        order.put("express", express);
        order.put("expressNo", expressNo);
        order.put("freight", freight);
        order.put("orderStatus", ORDER_TYPE_SENT); //4:商家已发货
        order.put("sendTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", order);

        //发送短信
        sendPayInfo(order.get("sellerId").toString(), order.get("memberId").toString(), order.get("totalPrice").toString(),
                getProductName(getOrderItemByOrderId(orderId))
                , ",商家已发货,快递:" + express + ",单号:" + expressNo);
    }

    /**
     * 在线订单:会员更新订单:手动点击确认收货
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/updateOnlineOrderByMember")
    public void updateOnlineOrderByMember() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        updateOnlineOrderByMember(orderId);
    }

    /**
     * 在线订单:会员更新订单:15天未确认,则自动确认收货
     *
     * @param orderId
     * @throws Exception
     */
    public static void updateOnlineOrderByMember(String orderId) throws Exception {
        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(500, "获取订单数据失败");
        }
        Map<String, Object> order = MysqlDaoImpl.getInstance().findById2Map("OrderInfo", orderId, null, null);

        if (order == null || order.size() == 0) {
            throw new UserOperateException(500, "获取订单数据失败");
        }
        if (!"11".equals(order.get("orderType").toString())) {
            throw new UserOperateException(500, "此订单不是线上交易订单");
        }
        if (!"4".equals(order.get("orderStatus").toString())) {
            throw new UserOperateException(500, "此订单未在配送中");
        }

        order.put("orderStatus", ORDER_TYPE_REVICED);//5:已收货
        order.put("accountTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", order);

        List<Map<String,Object>> orderItem = getOrderItemByOrderId(orderId);

        String sql ="SELECT trId FROM pay WHERE orderId=?";
        List<String> returnField = new ArrayList<>();
        returnField.add("trId");
        List<Object> params = new ArrayList<>();
        params.add(order.get("pid").toString());
        List<Map<String,Object>> pay = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        Message msg = Message.newReqMessage("1:POST@/payment/Gpay/cfrecv");
        msg.getContent().put("transno",pay.get(0).get("trId"));
        ServiceAccess.callService(msg).getContent();

        //发送短信
        sendPayInfo(order.get("sellerId").toString(), order.get("memberId").toString(), order.get("totalPrice").toString(),getProductName(orderItem),",确认收货7天后结算养老金");
    }


    @POST
    @Path("/endOnlineOrderConfirm")
    public void endOnlineOrderConfirm() throws Exception{
        endOnlineOrder(ControllerContext.getPString("orderId"));
    }

    /**
     * 在线订单:结束订单,结算分润,养老金
     *
     * @param orderId
     * @throws Exception
     */
    public static void endOnlineOrder(String orderId) throws Exception {
        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(500, "获取订单数据失败");
        }
        Map<String, Object> order = MysqlDaoImpl.getInstance().findById2Map("OrderInfo", orderId, null, null);
        if (order == null || order.size() == 0) {
            throw new UserOperateException(500, "获取订单数据失败");
        }
        if (!"11".equals(order.get("orderType").toString())) {
            throw new UserOperateException(500, "此订单不是线上交易订单");
        }
        if (!Pattern.matches("^[56]$",order.get("orderStatus").toString())) {
            throw new UserOperateException(500, "会员未确认收货");
        }

        order.put("orderStatus", ORDER_TYPE_END);//100:完结
        order.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", order);

        List<Map<String,Object>> orderItem = getOrderItemByOrderId(orderId);

        //累计/保存 商品数量
        if(StringUtils.mapValueIsEmpty(orderItem.get(0),"productId")){
            throw new UserOperateException(500, "获取商品数据失败");
        }
        if(StringUtils.mapValueIsEmpty(orderItem.get(0),"saleCount")){
            orderItem.get(0).put("saleCount",0);
        }
        int saleCount=Integer.parseInt(orderItem.get(0).get("saleCount").toString());
        int orderItemCount = Double.valueOf(orderItem.get(0).get("count").toString()).intValue();
//        int orderItemCount = Integer.parseInt(orderItem.get(0).get("count").toString().split("\\.")[0]);
        for(int i=0,len=orderItem.size();i<len;i++){
            if(StringUtils.mapValueIsEmpty(orderItem.get(i),"productId")){
                throw new UserOperateException(500, "获取商品数据失败");
            }
            if(StringUtils.mapValueIsEmpty(orderItem.get(i),"saleCount")){
                orderItem.get(i).put("saleCount",0);
            }
            orderItemCount = Double.valueOf(orderItem.get(i).get("count").toString()).intValue();
            //如果和前一个productId一致,则先累计,不做保存操作;如果不一致,则重新计算商品的数量,且保存前面相同ID的商品累计的数量;
            if(i==len-1 || !orderItem.get(i).get("productId").toString().equals(orderItem.get(i+1).get("productId").toString())){
                saleCount += orderItemCount;
                Map<String,Object> product = new HashMap<>();
                product.put("_id", orderItem.get(i).get("productId"));
                product.put("saleCount", saleCount);
                MysqlDaoImpl.getInstance().saveOrUpdate("ProductInfo", product);
                if(i+1==len){
                    break;
                }
//                if(StringUtils.mapValueIsEmpty(orderItem.get(i+1),"productId")){
//                    throw new UserOperateException(500, "获取商品数据失败");
//                }
                if(StringUtils.mapValueIsEmpty(orderItem.get(i+1),"saleCount")){
                    orderItem.get(i+1).put("saleCount",0);
                }
                //重新赋值
                saleCount = Integer.parseInt(String.valueOf(orderItem.get(i+1).get("saleCount")));
            }else{
                saleCount += orderItemCount;
            }
        }

        //声明生成明细表的数据
        String sellerId = order.get("sellerId").toString();
        String memberId = order.get("memberId").toString();
        String orderNo = order.get("orderNo").toString();
        double brokerageCount = Double.valueOf(order.get("brokerageCount").toString());
        double pensionMoney = Double.valueOf(order.get("pensionMoney").toString());
        double totalPrice = Double.valueOf(order.get("totalPrice").toString());
        double deduct = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(brokerageCount, -pensionMoney));

        //生成账户明细
        createMemberMoneyLog(memberId, memberId, orderNo, totalPrice, "6", ControllerContext.getPInteger("payType"));
        //生成商家现金明细记录/汇总记录
        createSellerLog(sellerId, memberId, orderNo, brokerageCount, totalPrice, "3", 3,Integer.parseInt(order.get("payType").toString()));
        //生成养老金记录/汇总记录
        createPensionLog(memberId, orderNo, pensionMoney, "4");
        //生成代理商提成
        createAgentMoney(deduct, orderNo, memberId, 3);

        //发送短信
        sendPayInfo(order.get("sellerId").toString(), order.get("memberId").toString(), order.get("totalPrice").toString()
                ,getProductName(orderItem)
                , ",已收获养老金" + String.valueOf(pensionMoney) + "元");
        sendPaySeller(sellerId, memberId, String.valueOf(totalPrice));
    }

    /**
     * 创建关联商家现金收银订单
     * 同时更新本地商家、会员数据
     * @throws Exception
     */
    @POST
    @Path("/createOrderByRelateStoreCash")
    public void createOrderByRelateStoreCash() throws Exception{
        String relateFrom = ControllerContext.getPString("relateFrom");//店铺来源
        String relateStoreId = ControllerContext.getPString("relateStoreId");//店铺ID
        String memberMobile = ControllerContext.getPString("memberMobile"); //会员手机号码
        String relateOrderId = ControllerContext.getPString("relateOrderId"); //订单ID
        double orderPrice = ControllerContext.getPDouble("orderPrice"); //订单金额

        String key = "relate_order_" + relateOrderId;
        try {
            JedisUtil.whileGetLock(key, 60);

            if(StringUtils.isEmpty(relateStoreId)){
                throw new UserOperateException(500,"获取快易帮店铺ID失败");
            }
            if(StringUtils.isEmpty(memberMobile)){
                throw new UserOperateException(500,"获取会员手机号码失败");
            }
            if(StringUtils.isEmpty(relateOrderId)){
                throw new UserOperateException(500,"获取订单失败");
            }
            if(StringUtils.isEmpty(ControllerContext.getPString("orderPrice")) || orderPrice<=0){
                throw new UserOperateException(500,"获取订单金额失败");
            }
            if(orderPrice>100000){
                throw new UserOperateException(500,"单笔交易金额不能超过10万元");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("relateOrderId",relateOrderId);
            Map<String,Object> relateOrder = MysqlDaoImpl.getInstance().findOne2Map("RelateStoreOrder",params,null,null);
            if(relateOrder!=null && relateOrder.size()!=0){
                throw new UserOperateException(500,"请勿重复提交订单");
            }

            Message msg = Message.newReqMessage("1:GET@/account/RelateStore/getRelateStore");
            msg.getContent().put("relateStoreId",relateStoreId);
            msg.getContent().put("relateFrom",relateFrom);
            JSONObject relateStore = ServiceAccess.callService(msg).getContent();
            if(relateStore==null || relateStore.size()==0 || StringUtils.mapValueIsEmpty(relateStore,"localSellerId")){
                throw new UserOperateException(500,"获取快易帮店铺信息失败");
            }
            if(StringUtils.mapValueIsEmpty(relateStore,"canUse") || !Boolean.parseBoolean(relateStore.get("canUse").toString())){
                throw new UserOperateException(500,"普惠生活商家已被禁用");
            }

            msg = Message.newReqMessage("1:GET@/crm/Member/getMemberInfoByMobile");
            msg.getContent().put("mobile", memberMobile);
            JSONObject member = ServiceAccess.callService(msg).getContent();
            if(member==null || member.size()==0){
                throw new UserOperateException(500,"获取会员数据失败");
            }
            if(StringUtils.mapValueIsEmpty(member,"canUse") || !Boolean.parseBoolean(member.get("canUse").toString())){
                throw new UserOperateException(500,"该会员被禁用");
            }
            if((StringUtils.mapValueIsEmpty(member,"isBindCard") && StringUtils.mapValueIsEmpty(member,"isFree"))
                    || (!Boolean.parseBoolean(member.get("isBindCard").toString()) && !Boolean.parseBoolean(member.get("isFree").toString()))){
                throw new UserOperateException(500,"该会员尚未激活");
            }

            String memberId = member.get("_id").toString();
            long time = System.currentTimeMillis();

            double integralRate = BigDecimalUtil.divide(Double.parseDouble(relateStore.get("integralRate").toString()), 100);//积分率
            double brokerageCount = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.multiply(orderPrice, integralRate)); //商家支付佣金
            double pensionMoney = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.divide(brokerageCount, 2));//返回给会员的养老金

            String sellerId = relateStore.get("localSellerId").toString();
            String orderNo = ZQUidUtils.generateOrderNo();
            String orderId = ZQUidUtils.genUUID();
            Map<String, Object> order = new HashMap<>();
            order.put("_id", ZQUidUtils.genUUID());
            order.put("memberId", memberId);
            order.put("orderNo", orderNo);
            order.put("totalPrice", orderPrice);
            order.put("payMoney", orderPrice);
            order.put("sellerId", sellerId);
            order.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
            order.put("createTime", time);
            order.put("bookingTime", time);
        //        order.put("endTime", time);
            order.put("o2o", true);
            order.put("score", relateStore.get("integralRate"));
            order.put("pensionMoney", pensionMoney);
            order.put("orderType", TYPE_KYB_CASH);
            order.put("payType", PAY_TYPE_CASH);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);

            Map<String, Object> payInfo = new HashMap<>();
            payInfo.put("totalFee", orderPrice);

            //创建 关联订单表
            Map<String,Object> relateStoreOrder = new HashMap<>();
            relateStoreOrder.put("_id",ZQUidUtils.genUUID());
            relateStoreOrder.put("orderId",orderId);
            relateStoreOrder.put("orderNo",orderNo);
            relateStoreOrder.put("orderPrice",orderPrice);
            relateStoreOrder.put("relateOrderId",relateOrderId);
            relateStoreOrder.put("relateFrom",relateFrom);
            relateStoreOrder.put("relateStoreId",relateStoreId);
            relateStoreOrder.put("memberMobile",memberMobile);
            relateStoreOrder.put("memberId",memberId);
            relateStoreOrder.put("sellerId",sellerId);
            relateStoreOrder.put("createTime", time);
            MysqlDaoImpl.getInstance().saveOrUpdate("RelateStoreOrder", relateStoreOrder);

            // 作为现金交易处理
            updateOrderOffline(order,payInfo);
        } finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 爱卡卡服务站生成订单养老金
     * @throws Exception
     */
    @POST
    @Path("/createOrderByAikaka")
    public void createOrderByAikaka() throws Exception{
        String relateFrom = ControllerContext.getPString("relateFrom");//店铺来源
        String localSellerId = ControllerContext.getPString("relateStoreId");//店铺ID
        String memberMobile = ControllerContext.getPString("memberMobile"); //会员手机号码
        double pensionPrice = ControllerContext.getPDouble("pensionPrice"); //养老金
        String relateOrderId = ControllerContext.getPString("relateOrderId"); //订单ID

        String key = "aikaka_order_" + relateOrderId;
        try {
            JedisUtil.whileGetLock(key, 60);

            if(StringUtils.isEmpty(localSellerId)){
                throw new UserOperateException(500,"获取关联服务站ID失败");
            }
            if(StringUtils.isEmpty(memberMobile)){
                throw new UserOperateException(500,"获取会员手机号码失败");
            }
            if(StringUtils.isEmpty(ControllerContext.getPString("pensionPrice")) || pensionPrice<=0){
                throw new UserOperateException(500,"获取养老金失败");
            }
            if(pensionPrice>100000){
                throw new UserOperateException(500,"养老金不能超过10万元");
            }
            if(!relateFrom.equals("aikaka")){
                throw new UserOperateException(500,"错误的关联服务站");
            }

            Map<String,Object> params = new HashMap<>();
            params.put("relateOrderId",relateOrderId);
            Map<String,Object> relateOrder = MysqlDaoImpl.getInstance().findOne2Map("RelateStoreOrder",params,null,null);
            if(relateOrder!=null && relateOrder.size()!=0){
                throw new UserOperateException(500,"请勿重复提交订单");
            }

            Message msg = Message.newReqMessage("1:GET@/account/RelateStore/getRelateStore");
            msg.getContent().put("localSellerId",localSellerId);
            msg.getContent().put("relateFrom",relateFrom);
            msg.getContent().put("userType","Factor");
            JSONObject relateStore = ServiceAccess.callService(msg).getContent();
            if(relateStore==null || relateStore.size()==0 || StringUtils.mapValueIsEmpty(relateStore,"localSellerId")){
                throw new UserOperateException(500,"获取关联服务站信息失败");
            }
            if(StringUtils.mapValueIsEmpty(relateStore,"canUse") || !Boolean.parseBoolean(relateStore.get("canUse").toString())){
                throw new UserOperateException(500,"普惠生活服务站已被禁用");
            }

            msg = Message.newReqMessage("1:GET@/crm/Member/getMemberInfoByMobile");
            msg.getContent().put("mobile", memberMobile);
            JSONObject member = ServiceAccess.callService(msg).getContent();
            if(member==null || member.size()==0){
                throw new UserOperateException(500,"获取会员数据失败");
            }
            if(StringUtils.mapValueIsEmpty(member,"canUse") || !Boolean.parseBoolean(member.get("canUse").toString())){
                throw new UserOperateException(500,"该会员被禁用");
            }
            if((StringUtils.mapValueIsEmpty(member,"isBindCard") && StringUtils.mapValueIsEmpty(member,"isFree"))
                    || (!Boolean.parseBoolean(member.get("isBindCard").toString()) && !Boolean.parseBoolean(member.get("isFree").toString()))){
                throw new UserOperateException(500,"该会员尚未激活");
            }

            String memberId = member.get("_id").toString();
            long time = System.currentTimeMillis();
            String factorId = relateStore.get("localSellerId").toString();
            String orderNo = ZQUidUtils.generateOrderNo();
            double total = BigDecimalUtil.multiply(pensionPrice,1.05);

            // 商家账户
            params.clear();
            params.put("factorId", factorId);
            Map<String, Object> sellerAccount = MysqlDaoImpl.getInstance().findOne2Map("FactorMoneyAccount", params, null, null);
            double cashCount = Double.parseDouble(sellerAccount.get("cashCount").toString());
            double cashCountUse = Double.parseDouble(sellerAccount.get("cashCountUse").toString());
            if(cashCount<total){
                throw new UserOperateException(500,"商家账户余额不足");
            }
            cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount,-total));
            cashCountUse = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCountUse,total));
            sellerAccount.put("cashCount",cashCount);
            sellerAccount.put("cashCountUse",cashCountUse);
            sellerAccount.put("updateTime",System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount",sellerAccount);

            // 会员养老金账户
            params.clear();
            params.put("memberId",memberId);
            Map<String, Object> pensionAccount = MysqlDaoImpl.getInstance().findOne2Map("MemberPensionAccount", params, null, null);
            if(pensionAccount==null || pensionAccount.size()==0){
                throw new UserOperateException(500,"获取会员账户失败");
            }
            double pensionCount = Double.parseDouble(pensionAccount.get("pensionCount").toString());//养老金总金额
            double insureCount = Double.parseDouble(pensionAccount.get("insureCount").toString());//未投保
            double insureCountUse = Double.valueOf(pensionAccount.get("insureCountUse").toString());//总共已投保
            insureCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(insureCount,pensionPrice));
            pensionCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(pensionCount,pensionPrice));

            //检查是否是免费会员
            boolean isFree=checkFreeMember(memberId);
            //调用投保上限
            double profitNum = ParameterAction.getValueOne("maxPensionMoney");
            //声明是否可投保
            boolean isInsure = insureCount >= profitNum;

            double insureCountUseLog = 0.0;//单次已投保
            //是否投保,计算投保金额;免费会员不计算投保
            if (isInsure && !isFree) {
                insureCountUseLog = insureCount;
                insureCountUse = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.add(insureCountUse, insureCountUseLog));
                insureCount = 0;
            }

            pensionAccount.put("insureCountUse",insureCountUse);
            pensionAccount.put("pensionCount",pensionCount);
            pensionAccount.put("insureCount",insureCount);
            pensionAccount.put("updateTime",System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionAccount",pensionAccount);

            //创建 关联订单表
            String relateId = ZQUidUtils.genUUID();
            Map<String,Object> relateStoreOrder = new HashMap<>();
            relateStoreOrder.put("_id",relateId);
            relateStoreOrder.put("orderId",ZQUidUtils.genUUID());
            relateStoreOrder.put("orderNo",orderNo);
            relateStoreOrder.put("pensionPrice",pensionPrice);
            relateStoreOrder.put("relateOrderId",relateOrderId);
            relateStoreOrder.put("relateFrom",relateFrom);
            relateStoreOrder.put("relateStoreId",localSellerId);
            relateStoreOrder.put("memberMobile",memberMobile);
            relateStoreOrder.put("memberId",memberId);
            relateStoreOrder.put("sellerId",factorId);
            relateStoreOrder.put("createTime", time);
            MysqlDaoImpl.getInstance().saveOrUpdate("RelateStoreOrder", relateStoreOrder);

            //投百年人寿
            if (isInsure && !isFree) {
//                Map<String,Object> param = new HashMap<>();
//                params.put("transSeq",orderNo);
//                Map<String,Object> queryResult= MysqlDaoImpl.getInstance().findOne2Map("InsureLog",param,null,null);
//                //根据订单号去查询投保记录,如果查询到有记录,则说明已经投保,则不投保,反之进行投保
//                if(null == queryResult || queryResult.size()== 0){
                    Map<String,Object> insureLog = new InsureAction().tradeMemberInsure(memberId,orderNo,relateId,insureCountUseLog);
                    relateStoreOrder.put("insureLog",insureLog);
//                }
            }

            relateStoreOrder.remove("_id");
            toResult(200,relateStoreOrder);
        } finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 关联商家订单退款
     * 撤销 会员账户明细、养老金，商家、服务站、代理商明细、入账
     * 先查询是否有明细，再扣除入账
     * @throws Exception
     */
    @POST
    @Path("/drawbackOrderByRelate")
    public void drawbackOrderByRelate() throws Exception{
        String relateOrderId = ControllerContext.getPString("relateOrderId");
        String orderId = ControllerContext.getPString("orderId");
        String relateFrom = ControllerContext.getPString("relateFrom");
        Map<String,Object> params = new HashMap<>();

        if(StringUtils.isEmpty(orderId) && StringUtils.isEmpty(relateOrderId)){
            throw new UserOperateException(500,"获取订单编号失败");
        }

//        if(true){
//            throw new UserOperateException(400, "此功能正在维护升级中");
//        }

        // 若是关联商家调用，则查出orderId；若是本地调用，则直接传orderId
        if(StringUtils.isEmpty(orderId) && !StringUtils.isEmpty(relateOrderId)){
            if(StringUtils.isEmpty(relateFrom)){
                throw new UserOperateException(500,"获取关联商家来源失败");
            }
            params.put("relateOrderId",relateOrderId);
            params.put("relateFrom",relateFrom);
            Map<String,Object> relateOrder = MysqlDaoImpl.getInstance().findById2Map("RelateStoreOrder",orderId,null,null);
            if(relateOrder == null || relateOrder.size()==0){
                throw new UserOperateException(500,"获取关联订单失败");
            }
            orderId = relateOrder.get("orderId").toString();
        }

        Map<String,Object> order = MysqlDaoImpl.getInstance().findById2Map("OrderInfo",orderId,null,null);
        if(order == null || order.size()==0 || StringUtils.mapValueIsEmpty(order,"memberId")
                || StringUtils.mapValueIsEmpty(order,"orderStatus")){
            throw new UserOperateException(500,"获取订单数据失败");
        }
        if(!Pattern.matches("^[12]|(12)$",order.get("orderType").toString())){
            throw new UserOperateException(500,"不支持该订单直接退款");
        }
        if(!"100".equals(order.get("orderStatus").toString())){
            throw new UserOperateException(500,"该订单未完结或已退款");
        }
        order.put("orderStatus",ORDER_TYPE_RETURN_MONEY);//9:已退款
        order.put("returnPrice",order.get("payMoney"));
        order.put("returnTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo",order);

//        String memberId = order.get("memberId").toString();
//        Message msg = Message.newReqMessage("1:GET@/crm/Member/show");
//        msg.getContent().put("_id",memberId);
//        JSONObject member = ServiceAccess.callService(msg).getContent();

        if(Pattern.matches("^[12]|(12)$",order.get("orderType").toString())){
            //检查是否有支付记录，如有，暂时不退款（后续添加此功能）
            Message msg = Message.newReqMessage("1:GET@/payment/Pay/getPayByOrderId");
            msg.getContent().put("orderId",orderId);
            msg.getContent().put("type","orderPay");
            JSONObject pay = ServiceAccess.callService(msg).getContent();
            if(pay!=null && pay.size()!=0 && !StringUtils.mapValueIsEmpty(pay,"payStatus")
                    && pay.get("payStatus").toString().equals("SUCCESS")){
                throw new UserOperateException(500,"暂不支持微信，支付宝退款");
            }
        }
        if(Pattern.matches("^[1]|(12)$",order.get("orderType").toString())){
            //会员退款
            drawbackMemberAccount(order);
            //养老金退款
            drawbackPensionAccount(order);
        }
        if(Pattern.matches("^[1]|(12)$",order.get("orderType").toString())){
            //服务站退款
            drawbackAgentAccount(order,"Factor",null);
            //代理商退款
            List<Object> p = new ArrayList<>();
            p.add(order.get("orderNo"));
            List<String> r = new ArrayList<>();
            r.add("agentId");
            String sql = "select distinct agentId from AgentMoneyLog where orderId = ?";
            List<Map<String,Object>> agentList = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
            if(agentList!=null && agentList.size()!=0){
                for(Map<String,Object> agent:agentList){
                    drawbackAgentAccount(order,"Agent",agent.get("agentId").toString());
                }
            }
        }
        if(Pattern.matches("^[12]|(12)$",order.get("orderType").toString())){
            //商家退款
            drawbackSellerAccount(order);
        }
    }

    /**
     * 线下现金交易退款：会员明细
     * @throws Exception
     */
    public void drawbackMemberAccount(Map<String,Object> order) throws Exception{
        List<Object> p = new ArrayList<>();
        p.add(order.get("orderNo"));

        List<String> r = new ArrayList<>();
        r.add("payType");
        r.add("createTime");
        r.add("memberId");
        r.add("orderId");
        r.add("_id");
        r.add("payId");
        r.add("cashCount");
        r.add("orderCash");
        r.add("tradeType");

        String sql = "select payType,createTime,memberId,orderId,_id,payId,cashCount,orderCash,tradeType" +
                " from MemberMoneyLog where orderId = ?";
        List<Map<String,Object>> memberLog = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        if(memberLog==null || memberLog.size()==0){
            throw new UserOperateException(500,"获取会员明细失败");
        }
        for(Map<String,Object> log : memberLog){
            if("100".equals(log.get("tradeType"))){
                throw new UserOperateException(500,"该订单已退款");
            }
        }
        Map<String,Object> log = memberLog.get(0);

        //会员明细中需要退款的金额
        double orderCash = Double.parseDouble(log.get("orderCash").toString());

        //查询会员账户
        Map<String,Object> params = new HashMap<>();
        params.put("memberId",log.get("memberId").toString());
        Map<String,Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberMoneyAccount",params,null,null);
        //线下交易金额
        double cashOfflineCount = Double.parseDouble(account.get("cashOfflineCount").toString());
        //交易总金额
        double totalConsume = Double.parseDouble(account.get("totalConsume").toString());

        //扣除增加的统计金额
        cashOfflineCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashOfflineCount,-orderCash));
        totalConsume = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(totalConsume,-orderCash));
        account.put("cashOfflineCount",cashOfflineCount);
        account.put("totalConsume",totalConsume);
        account.put("updateTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyAccount",account);

        //保存退款账户明细
        log.put("_id",ZQUidUtils.genUUID());
        log.put("createTime",System.currentTimeMillis());
        log.put("tradeType",100); //退款
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyLog",log);
    }

    /**
     * 线下交易退款：养老金
     * @param order
     * @throws Exception
     */
    public void drawbackPensionAccount(Map<String,Object> order) throws Exception{
        List<Object> p = new ArrayList<>();
        p.add(order.get("orderNo"));

        List<String> r = new ArrayList<>();
//        r.add("insureNO");
//        r.add("isInsure");
        r.add("insureCount");
        r.add("type");
        r.add("memberId");
        r.add("_id");
        r.add("insureStatus");
//        r.add("insureType");
        r.add("pensionTrade");
//        r.add("insureCountUse");
//        r.add("insureCompany");
        r.add("orderId");
        r.add("createTime");

        String sql = "select insureCount,type,memberId,_id,insureStatus,pensionTrade,orderId,createTime" +
                " from MemberPensionLog where orderId = ?";
        List<Map<String,Object>> allLog = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        if(allLog==null || allLog.size()==0){
            throw new UserOperateException(500,"获取养老金明细失败");
        }
        for(Map<String,Object> log : allLog){
            if("100".equals(log.get("tradeType"))){
                throw new UserOperateException(500,"该订单已退款");
            }
        }
        Map<String,Object> log = allLog.get(0);

        //养老金
        double pensionTrade =Double.parseDouble(log.get("pensionTrade").toString());

        Map<String,Object> params = new HashMap<>();
        params.put("memberId",order.get("memberId"));
        Map<String,Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberPensionAccount",params,null,null);
        if(account==null || account.size()==0){
            throw new UserOperateException(500,"获取养老金账户失败");
        }

        //养老金总金额
        double pensionCount = Double.parseDouble(account.get("pensionCount").toString());
        //未投保养老金
        double insureCount = Double.parseDouble(account.get("insureCount").toString());

        //扣除 未投保养老金
        insureCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(insureCount,-pensionTrade));
        pensionCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(pensionCount,-pensionTrade));

        account.put("updateTime",System.currentTimeMillis());
        account.put("insureCount",insureCount);
        account.put("pensionCount",pensionCount);
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionAccount",account);

        log.put("_id",ZQUidUtils.genUUID());
        log.put("insureCount",insureCount);
        log.put("type",100);
        log.put("insureStatus",2);
        log.put("insureCountUse",0);
        log.put("createTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionLog",log);
    }

    /**
     * 线下交易退款：商家
     * @param order
     * @throws Exception
     */
    public void drawbackSellerAccount(Map<String,Object> order) throws Exception{
        List<Object> p = new ArrayList<>();
        p.add(order.get("orderNo"));

        List<String> r = new ArrayList<>();
        r.add("brokerageCount");
        r.add("_id");
        r.add("tradeType");
        r.add("orderCash");
        r.add("incomeOne");
        r.add("createTime");
        r.add("sellerId");
        r.add("orderId");
        r.add("tradeId");

        String sql = "select brokerageCount,_id,tradeType,orderCash,incomeOne,createTime,sellerId,orderId,tradeId" +
                " from SellerMoneyLog where orderId = ?";
        List<Map<String,Object>> allLog = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        if(allLog==null || allLog.size()==0){
            throw new UserOperateException(500,"获取商家明细失败");
        }
        for(Map<String,Object> log : allLog){
            if("100".equals(log.get("tradeType"))){
                throw new UserOperateException(500,"该订单已退款");
            }
        }
        Map<String,Object> log = allLog.get(0);

        //佣金
        double brokerageCount = Double.parseDouble(log.get("brokerageCount").toString());
        //订单交易金额
        double orderCash = Double.parseDouble(log.get("orderCash").toString());

        //查询账户
        Map<String,Object> params = new HashMap<>();
        params.put("sellerId",log.get("sellerId").toString());
        Map<String,Object> account = MysqlDaoImpl.getInstance().findOne2Map("SellerMoneyAccount",params,null,null);
        // 余额
        double cashCount = Double.parseDouble(account.get("cashCount").toString());
        // 已使用余额
        double cashCountUse = Double.parseDouble(account.get("cashCountUse").toString());
        // 总佣金
        double brokerageCountTotal = Double.parseDouble(account.get("brokerageCountTotal").toString());
        // 订单交易金额
        double orderCashCount = Double.parseDouble(account.get("orderCashCount").toString());
        // 历史总入账
        double income = Double.parseDouble(account.get("income").toString());

        //增加 被扣除的余额
        cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount, brokerageCount));
        cashCountUse = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCountUse, -brokerageCount));
        income = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(income, brokerageCount));

        //扣除 增加的的统计金额
        orderCashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(orderCashCount,-orderCash));
        brokerageCountTotal = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(brokerageCountTotal,-brokerageCount));

        //若是互联网收款扫码，则扣除余额
        if(order.get("orderType").toString().equals("2")){
            cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount,-orderCash));
            income = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(income,-orderCash));
        }

        account.put("income",income);
        account.put("cashCount",cashCount);
        account.put("cashCountUse",cashCountUse);
        account.put("orderCashCount",orderCashCount);
        account.put("brokerageCountTotal",brokerageCountTotal);
        account.put("updateTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount",account);

        //保存退款账户明细
        log.put("_id",ZQUidUtils.genUUID());
        log.put("cashCountCur",cashCount);
        log.put("orderCash",orderCash);
        log.put("incomeOne",brokerageCount);
        log.put("createTime",System.currentTimeMillis());
        log.put("tradeType",100); //退款
        MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyLog",log);
    }

    /**
     * 线下交易退款：服务站，代理商
     * @param order
     * @throws Exception
     */
    public void drawbackAgentAccount(Map<String,Object> order,String userType,String userId) throws Exception{
        List<Object> p = new ArrayList<>();

        String userTypeLower = userType.toLowerCase();

        List<String> r = new ArrayList<>();
        r.add(userTypeLower+"Id");
        r.add("orderId");
        r.add("_id");
        r.add("createTime");
        r.add("type");
        r.add("cashProportion");
        r.add("tradeId");
        r.add("orderCash");

        String where = " where orderId = ?";
        p.add(order.get("orderNo").toString());

        if(!StringUtils.isEmpty(userId)){
            where += " and "+userTypeLower+"Id=?";
            p.add(userId);
        }

        String sql = "select " +userTypeLower+"Id,orderId,_id,createTime,type,cashProportion,tradeId,orderCash"+
                " from "+userType+"MoneyLog "+where;
        List<Map<String,Object>> allLog = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        if(allLog==null || allLog.size()==0){
            return; // 可能没有服务站、代理商分润的情况
//            throw new UserOperateException(500,"获取服务站/代理商明细失败");
        }
        for(Map<String,Object> log : allLog){
            if("100".equals(log.get("type"))){
                throw new UserOperateException(500,"该订单已退款");
            }
        }
        Map<String,Object> log = allLog.get(0);

        // 入账金额
        double orderCash = Double.parseDouble(log.get("orderCash").toString());

        //查询账户
        Map<String,Object> params = new HashMap<>();
        params.put(userTypeLower+"Id",log.get(userTypeLower+"Id").toString());
        Map<String,Object> account = MysqlDaoImpl.getInstance().findOne2Map(userType+"MoneyAccount",params,null,null);

        // 总收入
        double income = Double.parseDouble(account.get("income").toString());
        // 余额
        double cashCount = Double.parseDouble(account.get("cashCount").toString());

        // 扣除 增加的入账金额
        cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount,-orderCash));
        income = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(income,-orderCash));

        account.put("cashCount",cashCount);
        account.put("income",income);
        MysqlDaoImpl.getInstance().saveOrUpdate(userType+"MoneyAccount",account);

        //保存退款账户明细
        log.put("_id",ZQUidUtils.genUUID());
        log.put("createTime",System.currentTimeMillis());
        log.put("type",100); //退款
        MysqlDaoImpl.getInstance().saveOrUpdate(userType+"MoneyLog",log);
    }


    /**
     * 退货:检查申请退款订单数据是否正确
     * 会员已支付 或 会员申请退款 的情况才能退款
     * @throws Exception
     */
    public Map<String,Object> checkDrawbackField(String orderId,int orderStatus) throws Exception {
        if(StringUtils.isEmpty(orderId)){
            throw new UserOperateException(500, "获取订单数据失败");
        }
        Map<String,Object> order = MysqlDaoImpl.getInstance().findById2Map(entityName,orderId,null,null);
        if(order==null || order.size()==0 || StringUtils.mapValueIsEmpty(order,"orderStatus")){
            throw new UserOperateException(500, "获取订单数据失败");
        }
        if(!"11".equals(order.get("orderType").toString())){
            throw new UserOperateException(500, "仅线上商品订单支持退货");
        }
        int curOrderStatus=Integer.parseInt(order.get("orderStatus").toString());
        if(orderStatus!=curOrderStatus && curOrderStatus!=2){
            if(curOrderStatus<orderStatus){
                throw new UserOperateException(500, "订单数据不匹配");
            }
            if(curOrderStatus>orderStatus && curOrderStatus<=9){
                throw new UserOperateException(500, "此订单已申请退货,请勿重复申请");
            }
        }
        return order;
    }

    /**
     * 在线订单:退货:会员申请退货
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/applyDrawbackOnlineOrder")
    public void applyDrawbackOnlineOrder() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        String returnImg = ControllerContext.getPString("returnImg");
        String returnDesc = ControllerContext.getPString("returnDesc");

        Map<String,Object> order = checkDrawbackField(orderId,5);
        if(StringUtils.isEmpty(returnDesc) || returnDesc.length()>255){
            throw new UserOperateException(500, "请填写退货理由(255个字符长度以内)");
        }
        order.put("orderStatus",ORDER_TYPE_REQ_RETURN);//申请退货
        order.put("returnDesc",returnDesc);
        order.put("returnImg",returnImg);
        order.put("isApplyReturn",true);
        order.put("returnApplyTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName,order);
    }

    /**
     * 在线订单:退货:商家审核会员申请退货
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/isReturnDrawbackOnlineOrder")
    public void isReturnDrawbackOnlineOrder() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        String isApplyReturn = ControllerContext.getPString("isApplyReturn");

        Map<String,Object> order = checkDrawbackField(orderId,6);

        if(StringUtils.isEmpty(isApplyReturn)){
            throw new UserOperateException(500, "是否接受会员退货申请?");
        }
        if(ControllerContext.getPBoolean("isApplyReturn")){
            String returnContact = ControllerContext.getPString("returnContact");
            String returnPhone = ControllerContext.getPString("returnPhone");
            String returnAddress = ControllerContext.getPString("returnAddress");

            if (!Pattern.matches("[\u0391-\uFFE5]{2,10}",returnContact)) {
                throw new UserOperateException(400, "请填写2~10位中文汉字之间的联系人名字!");
            }
            if (!Pattern.matches("^[1][34578][0-9]{9}$", returnPhone)) {
                throw new UserOperateException(400, "手机号格式错误!");
            }
            if(StringUtils.isEmpty(returnAddress) || returnAddress.length()>300){
                throw new UserOperateException(500, "请填写退货地址(300个字符长度以内)");
            }
            order.put("isReturn",true);
            order.put("returnContact",returnContact);
            order.put("returnPhone",returnPhone);
            order.put("returnAddress",returnAddress);
            order.put("isApplyReturn",ControllerContext.getPBoolean("isApplyReturn"));
            order.put("orderStatus",ORDER_TYPE_RETURNING);//7:确认退货
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName,order);
        }else{
            String returnRefuse = ControllerContext.getPString("returnRefuse");
            if(StringUtils.isEmpty(returnRefuse) || returnRefuse.length()>255){
                throw new UserOperateException(500, "请填写拒绝退货理由(255个字符长度以内)");
            }
            order.put("isReturn",false);
            order.put("returnRefuse",returnRefuse);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName,order);

            endOnlineOrder(orderId);
        }
    }

    /**
     * 在线订单:退货:会员发货
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/sendDrawbackOnlineOrder")
    public void sendDrawbackOnlineOrder() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        String returnExpress = ControllerContext.getPString("returnExpress");
        String returnExpressNo = ControllerContext.getPString("returnExpressNo");

        Map<String,Object> order = checkDrawbackField(orderId,7);

        if(StringUtils.isEmpty(returnExpress) || returnExpress.length()>64){
            throw new UserOperateException(400, "请填写快递公司(64个字符串长度以内)");
        }
        if(StringUtils.isEmpty(returnExpressNo) || returnExpressNo.length()>64){
            throw new UserOperateException(400, "请填写快递单号(64个字符串长度以内)");
        }
        order.put("orderStatus",ORDER_TYPE_RETURNED);//8:已发货
        order.put("returnExpress",returnExpress);
        order.put("returnExpressNo",returnExpressNo);
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName,order);
    }

    /**
     * 在线订单:退货:商家确认收货,退款成功
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/endDrawbackOnlineOrder")
    public void endDrawbackOnlineOrder() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        Map<String,Object> order = checkDrawbackField(orderId,8);
        order.put("orderStatus",ORDER_TYPE_RETURN_MONEY);//9:已退款
        order.put("returnPrice",order.get("payMoney"));
        order.put("returnTime",System.currentTimeMillis());

        if("3".equals(order.get("payType").toString())){//余额支付
            createMemberMoneyLog(order.get("memberId").toString(), order.get("memberId").toString(),
                    order.get("orderNo").toString(), BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(order.get("payMoney").toString())),
                    "5", 3);
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName,order);
        }else{//第三方支付
            String sql = "select t1.payMoney,t3._id" +
                    " from OrderInfo t1" +
                    " left join OrderInfo t2 on t1.pid=t2._id" +
                    " left join Pay t3 on t2._id=t3.orderId" +
                    " where t1._id=?";

            List<Object> params = new ArrayList<>();
            params.add(orderId);
            List<String> returnFields = new ArrayList<>();
            returnFields.add("_id");
            returnFields.add("payMoney");
            List<Map<String,Object>> payInfo = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
            if(payInfo==null || payInfo.size()==0 || payInfo.get(0)==null || payInfo.get(0).size()==0){
                throw new UserOperateException(400, "未找到支付记录");
            }

          /*  //查询第三方是否已经退款
            Message msg = Message.newReqMessage("1:POST@/payment/Pay/queryRefund");
            msg.getContent().put("orderId",orderId);
            JSONObject payReturn = ServiceAccess.callService(msg).getContent();*/

            /*if(payReturn!=null && payReturn.size()!=0 && !StringUtils.mapValueIsEmpty(payReturn,"returnStatus")
                    && !"SUCCESS".equals(payReturn.get("returnStatus").toString())){
                Map<String,Object> p = new HashMap<>();
                p.put("orderId",payReturn.get("orderNo").toString());
                p.put("tradeType",5);
                // 检查payReturn，会员账户明细是否已经记录了退款
                Map<String,Object> log = MysqlDaoImpl.getInstance().findOne2Map("MemberMoneyLog",p,null,null);
                if(log == null || log.size()==0){
                    createMemberMoneyLog(order.get("memberId").toString(), order.get("memberId").toString(),
                            order.get("orderNo").toString(), BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(order.get("payMoney").toString())),
                            "5", Integer.parseInt(order.get("payType").toString()));
                }else{
                    throw new UserOperateException(500,"请勿重复退款");
                }
            }else{*/
                Message msg = Message.newReqMessage("1:GET@/payment/Pay/refund");
                msg.getContent().put("payId", payInfo.get(0).get("_id"));
                msg.getContent().put("returnAmount", payInfo.get(0).get("payMoney"));
                JSONObject con = ServiceAccess.callService(msg).getContent();
                if(con!=null && con.size()!=0 && !StringUtils.mapValueIsEmpty(con,"returnStatus") && "SUCCESS".equals(con.get("returnStatus"))){
                    MysqlDaoImpl.getInstance().saveOrUpdate(entityName,order);

                    createMemberMoneyLog(order.get("memberId").toString(), order.get("memberId").toString(),
                            order.get("orderNo").toString(), BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(order.get("payMoney").toString())),
                            "5", Integer.parseInt(order.get("payType").toString()));
                }else{
                    throw new UserOperateException(500,"退款失败");
                }
            /*}*/
        }

        String notStock = ControllerContext.getPString("notStock");
        if(StringUtils.isNotEmpty(notStock)){
            //发送短信
            sendPayNotStockMember(order.get("sellerId").toString(),
                    order.get("memberId").toString(),
                    getProductName(getOrderItemByOrderId(orderId)),
                    order.get("payMoney").toString()+"元"
            );
        }
    }

    /**
     * 更新 商家/服务站 充值订单
     *
     * @throws Exception
     */
    public static void updateRecharge(Map<String, Object> orderInfo, Map<String, Object> payInfo, String type) throws Exception {
        double payMoney = Double.parseDouble(payInfo.get("totalFee").toString());
        String userId = null;
        //明细
        Map<String, Object> map = new HashMap<>();
        map.put("_id", UUID.randomUUID().toString());

        userId = orderInfo.get("sellerId").toString();
        map.put(type+"Id", userId);

        map.put("orderCash", payMoney);
        map.put("createTime", System.currentTimeMillis());
        map.put("orderId", orderInfo.get("orderNo"));
        if ("factor".equals(type)) {
            map.put("type", 1);
            MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyLog", map);
        } else {
            map.put("tradeType", 2);
            MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyLog", map);
        }
        //账户汇总
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("cashCount");
        String from = " from";
        String where = " where";
        if ("factor".equals(type)) {
            from += " FactorMoneyAccount";
            where += " factorId=?";
        } else {
            from += " SellerMoneyAccount";
            where += " sellerId=?";
        }
        p.add(userId);

        String sql = "select" +
                " _id" +
                ",cashCount" +
                from +
                where;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (re == null || re.size() == 0) {
            map.clear();
            map.put("_id", UUID.randomUUID().toString());
            if ("factor".equals(type)) {
                map.put("factorId", userId);
            } else {
                map.put("sellerId", userId);
            }
            map.put("createTime", System.currentTimeMillis());
            map.put("cashCount", payMoney);
            map.put("cashCountUse", 0);
            map.put("income", 0);
            if ("factor".equals(type)) {
                MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", map);
            } else {
                MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", map);
            }

        } else {
            map.clear();
            map.put("_id", re.get(0).get("_id"));
            map.put("updateTime", System.currentTimeMillis());
            double cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(Double.valueOf(re.get(0).get("cashCount").toString()), payMoney));

            map.put("cashCount", cashCount);
            if ("factor".equals(type)) {
                MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", map);
            } else {
                MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", map);
            }
        }

        //更新Order表
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("payType", payInfo.get("payType"));
        orderInfo.put("payMoney", payMoney);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);
    }

    /**
     * 计算会员充值分润金额
     * @throws Exception
     */
    public static Map<String,Double> countRechargeRatio(double money) throws Exception{
        double rechargeRatio = BigDecimalUtil.divide(ParameterAction.getValueOne("rechargeRatio"),100.0);    //分润比例
        double rechargePensionRatio = BigDecimalUtil.divide(ParameterAction.getValueOne("rechargePensionRatio"),100.0); //赠送养老金比例
        //需要给代理商服务站分润的金额
        double ratioMoney = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.multiply(rechargeRatio,money));
        //赠送给会员的养老金
        double pension =  BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.multiply(rechargePensionRatio,money));
        Map<String,Double> returnFields = new HashMap<>();
        returnFields.put("ratioMoney",ratioMoney);
        returnFields.put("pension",pension);
        return returnFields;
    }

    /**
     * 更新 会员充值
     *
     * @throws Exception
     */
    public static void memberRecharge(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        String memberId = orderInfo.get("memberId").toString();
        Double money = Double.valueOf(payInfo.get("totalFee").toString());
        int payType = Integer.valueOf(payInfo.get("payType").toString());

        //生成会员现金明细记录/汇总记录
        createMemberMoneyLog(memberId, memberId, orderInfo.get("orderNo").toString(), money, "2", payType);

        Map<String,Double> ratio = countRechargeRatio(money);
        //分润
        createAgentMoney(ratio.get("ratioMoney"), orderInfo.get("orderNo").toString(), memberId, 6);
        //增加养老金
        double pensionMoney = BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(ratio.get("pension").toString()));
        createPensionLog(memberId, orderInfo.get("_id").toString(),pensionMoney, "5");

        //更新Order表
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("payType", payInfo.get("payType"));
        orderInfo.put("pensionMoney", pensionMoney);
        orderInfo.put("payMoney", money);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);
    }

    /**
     * 更新 会员替朋友充值
     *
     * @throws Exception
     */
    public static Map<String, Object> memberRechargeFriend(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        String memberId = orderInfo.get("memberId").toString();
        String friendId = orderInfo.get("sellerId").toString();
        Double money = Double.valueOf(payInfo.get("totalFee").toString());
        int payType = Integer.parseInt(payInfo.get("payType").toString());

        //生成会员现金明细记录/汇总记录
        createMemberMoneyLog(friendId, memberId, null, money, "1", payType);

        Map<String,Double> ratio = countRechargeRatio(money);
        //分润
        createAgentMoney(ratio.get("ratioMoney"), orderInfo.get("orderNo").toString(), memberId, 6);
        //增加养老金
        double pensionMoney = BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(ratio.get("pension").toString()));
        createPensionLog(friendId, orderInfo.get("_id").toString(),pensionMoney, "5");

        //更新Order表
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("payType", payInfo.get("payType"));
        orderInfo.put("pensionMoney", pensionMoney);
        orderInfo.put("payMoney", money);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);
        return orderInfo;
    }

    /**
     * 激活会员金额
     * @throws Exception
     */
    @GET
    @Path("/getActiveMoney2")
    public void getActiveMoney2() throws Exception{
        Map<String,Object> item = new HashMap<>();
        item.put("activeMoney",ParameterAction.getValueOne("activeMoney"));
        toResult(200,item);
    }


    /**
     * 更新 服务站激活会员卡 订单
     *
     * @throws Exception
     */
    public static Map<String, Object> updateMemberCardByFactor(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        String factorId = orderInfo.get("sellerId").toString();
        String memberId = orderInfo.get("memberId").toString();
        String memberCardId = orderInfo.get("memberRemark").toString();
        int payType = Integer.parseInt(payInfo.get("payType").toString());
        double payMoney = Double.valueOf(payInfo.get("totalFee").toString());

        double activeMoney = ParameterAction.getValueOne("activeMoney");
        if (payMoney < activeMoney) {
            throw new UserOperateException(400, "请支付"+activeMoney+"元激活费用!");
        }

        JSONObject factor = ServiceAccess.getRemoveEntity("account","Factor",factorId);
        if(factor==null || factor.size()==0){
            throw new UserOperateException(400, "获取服务站失败");
        }
        String belongAreaValue = factor.get("areaValue").toString();

        //服务站激活会员
        Message message = Message.newReqMessage("1:POST@/crm/Member/activeMember");
        JSONObject con = message.getContent();
        con.put("factorId", factorId);
        con.put("memberId", memberId);
        con.put("activeCard", memberCardId);
        con.put("belongAreaValue", belongAreaValue);
        con.put("isAutoCard", memberCardId.substring(0,1).equals("C"));
        ServiceAccess.callService(message);

        //生成服务站明细表
//        Map<String, Object> factorLog = new HashMap<>();
//        factorLog.put("_id", UUID.randomUUID().toString());
//        factorLog.put("factorId", factorId);
//        factorLog.put("orderCash", payMoney);
//        factorLog.put("orderId", orderInfo.get("orderNo"));
//        factorLog.put("factorId", factorId);
//        factorLog.put("type", 6);
//        factorLog.put("createTime", System.currentTimeMillis());
//        MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyLog", factorLog);
//
//        //更新服务站汇总
//        Map<String, Object> params = new HashMap<>();
//        params.put("factorId", factorId);
//        factorLog = MysqlDaoImpl.getInstance().findOne2Map("FactorMoneyAccount", params, null, null);
//        if (payType == 3) {
//            double cashCount = BigDecimalUtil.add(Double.valueOf(factorLog.get("cashCount").toString()), -payMoney);
//            factorLog.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(cashCount));
//            double cashCountUse = BigDecimalUtil.add(Double.valueOf(factorLog.get("cashCountUse").toString()), payMoney);
//            factorLog.put("cashCountUse", BigDecimalUtil.fixDoubleNumProfit(cashCountUse));
//        }
//
//        factorLog.put("updateTime", System.currentTimeMillis());
//        MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", factorLog);

        //生成服务站明细表
        Map<String,Object> updateParams = new HashMap<>();
        updateParams.put("factorId",factorId);
        updateParams.put("payMoney",payMoney);
        updateParams.put("orderNo",orderInfo.get("orderNo"));
        updateParams.put("payType",payType);
        updateFactorAccountByActive(updateParams);
        //分润
        createAgentMoney(payMoney, orderInfo.get("orderNo").toString(), memberId, 4);
        //养老金
        createPensionLog(memberId, orderInfo.get("orderNo").toString(), 0, "");

        //更新Order表
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("payType", payInfo.get("payType"));
        orderInfo.put("payMoney", payMoney);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);

        //发短信
        JSONObject member = ServiceAccess.getRemoveEntity("crm","Member",memberId);
        sendActivate(member.get("mobile").toString());
        return orderInfo;
    }

    @POST
    @Path("/updateFactorAccountByActive")
    public void updateFactorAccountByActive() throws Exception{
//        String factorId = ControllerContext.getPString("factorId");
//        double payMoney = ControllerContext.getPDouble("payMoney");
//        String orderNo = ControllerContext.getPString("orderNo");
//        int payType = ControllerContext.getPInteger("payType");

        updateFactorAccountByActive((Map<String,Object>)ControllerContext.getContext().getReq().getContent());
    }

    /**
     * 服务站激活会员支付费用
     * @param item
     * @throws Exception
     */
    public static void updateFactorAccountByActive(Map<String,Object> item) throws Exception{
        String factorId = item.get("factorId").toString();
        double payMoney = Double.parseDouble(item.get("payMoney").toString());
        String orderNo = item.get("orderNo").toString();
        int payType = Integer.parseInt(item.get("payType").toString());

        Map<String, Object> factorLog = new HashMap<>();
        factorLog.put("_id", UUID.randomUUID().toString());
        factorLog.put("factorId", factorId);
        factorLog.put("orderCash", payMoney);
        factorLog.put("orderId", orderNo);
        factorLog.put("factorId", factorId);
        factorLog.put("type", 6);
        factorLog.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyLog", factorLog);

        //更新服务站汇总
        Map<String, Object> params = new HashMap<>();
        params.put("factorId", factorId);
        factorLog = MysqlDaoImpl.getInstance().findOne2Map("FactorMoneyAccount", params, null, null);
        if (payType == 3) {
            double cashCount = BigDecimalUtil.add(Double.valueOf(factorLog.get("cashCount").toString()), -payMoney);
            factorLog.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(cashCount));
            double cashCountUse = BigDecimalUtil.add(Double.valueOf(factorLog.get("cashCountUse").toString()), payMoney);
            factorLog.put("cashCountUse", BigDecimalUtil.fixDoubleNumProfit(cashCountUse));
        }

        factorLog.put("updateTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", factorLog);
    }


    /**
     * 更新:会员自主激活
     *
     * @throws Exception
     */
    public static Map<String, Object> updateMemberActive(Map<String, Object> orderInfo, Map<String, Object> payInfo) throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();

        if(StringUtils.isEmpty(memberId) || !memberId.startsWith("M-")){
            memberId = orderInfo.get("memberId").toString();
        }

        double payMoney = Double.valueOf(payInfo.get("totalFee").toString());
        int payType = Integer.valueOf(payInfo.get("payType").toString());
        String orderNo = orderInfo.get("orderNo").toString();

        double activeMoney = ParameterAction.getValueOne("activeMoney");

        if(payMoney<activeMoney){
            throw new UserOperateException(400,"请支付"+activeMoney+"元费用!");
        }

        //生成会员现金明细记录/汇总记录
        createMemberMoneyLog(memberId, memberId, orderNo, payMoney, "4", payType);

        // 激活卡
        Message msg = Message.newReqMessage("1:GET@/crm/Member/activeMemberCard");
        msg.getContent().put("memberId", memberId);
        msg.getContent().put("orderNo", orderNo);
        msg.getContent().put("payMoney", payMoney);

        String memberRemark = orderInfo.get("memberRemark").toString();
        if(StringUtils.isNotEmpty(memberRemark) && (memberRemark.contains("cardNo") || memberRemark.contains("belongAreaValue"))){
            String[] keyArr = memberRemark.split(",");
            String[] cardNoArr = keyArr[0].split("=");
            msg.getContent().put("cardNo", cardNoArr.length<=1?"":cardNoArr[1]);
            msg.getContent().put("belongAreaValue", keyArr[1].split("=")[1]);
        }else{
            // 若没有卡则选择分享会员的归属
            Map<String,Object> teamUp = new TeamAction().getTeamUpOne(memberId);
            if(teamUp!=null && teamUp.size()!=0){
                msg.getContent().put("belongAreaValue", teamUp.get("belongAreaValue"));
            }
        }

        JSONObject card = ServiceAccess.callService(msg).getContent();

        // 养老金
        createPensionLog(memberId,orderNo,0,"");

        //更新Order表
        orderInfo.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        orderInfo.put("payType", payType);
        orderInfo.put("payMoney", payMoney);
        orderInfo.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", orderInfo);

        //更新团队关系
        Map<String,Object> filter = new HashMap<>();
        filter.put("regMember",memberId);
        filter.put("factorId",StringUtils.mapValueIsEmpty(card,"factorId")?"A-000001":card.get("factorId"));
        new TeamAction().checkActive(filter);

        //发短信
        JSONObject member = ServiceAccess.getRemoveEntity("crm","Member",memberId);
        sendActivate(member.get("mobile").toString());

        return orderInfo;
    }

    /**
     *  检查是否是免费会员
     */
    public static boolean checkFreeMember(String memberId) throws Exception {
        boolean isFree=false;
        Message msg = Message.newReqMessage("1:GET@/crm/Member/show");
        msg.getContent().put("_id", memberId);
        JSONObject member = ServiceAccess.callService(msg).getContent();
        if(member==null || member.size()==0){
            throw new UserOperateException(500,"获取会员信息失败");
        }
        if(!StringUtils.mapValueIsEmpty(member,"isFree") && Boolean.parseBoolean(member.get("isFree").toString())){
            isFree=true;
        }
        return isFree;
    }

    /**
     * 生成养老金明细记录 和 汇总记录
     *
     * @throws Exception pensionTrade 收入养老金
     */
    private static void createPensionLog(String memberId, String orderNo, double pensionTrade, String tradeType) throws Exception {
        //获取养老金汇总记录
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        Map<String, Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberPensionAccount", params, null, null);
        //若没有汇总记录,生成汇总记录
        if (account == null || account.size() == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("_id", UUID.randomUUID().toString());
            map.put("createTime", System.currentTimeMillis());
            map.put("pensionCount", 0.0);
            map.put("insureCountUse", 0.0);
            map.put("insureCount", 0.0);
            map.put("memberId", memberId);
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionAccount", map);
            account = map;
        }

        //检查是否是免费会员
        boolean isFree=checkFreeMember(memberId);
        //调用投保上限
        double profitNum = ParameterAction.getValueOne("maxPensionMoney");
        //声明投保金额
        boolean isInsure = (Double.valueOf(account.get("insureCount").toString()) + pensionTrade >= profitNum);
        double insureCount = Double.valueOf(account.get("insureCount").toString());//未投保
        double insureCountUse = Double.valueOf(account.get("insureCountUse").toString());//总共已投保
        double pensionCount = Double.valueOf(account.get("pensionCount").toString());//养老金总额
        pensionCount = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.add(pensionCount, pensionTrade));
        double insureCountUseLog = 0.0;//单次已投保

        // 若免费会员积累的养老金大于激活金额，且已实名认证和未激活，则自动激活
        Message msgMember = Message.newReqMessage("1:GET@/crm/Member/getMemberByMemberId");
        msgMember.getContent().put("memberId", memberId);
        ServiceAccess.callService(msgMember);
        Map<String,Object> member = msgMember.getContent();
        boolean isRealName = false;
        boolean notBindCard = false;
        if(null != member){
            if(!StringUtils.mapValueIsEmpty(member,"isRealName") && Boolean.parseBoolean(member.get("isRealName").toString())){
                isRealName = true;
            }
            if(StringUtils.mapValueIsEmpty(member,"isBindCard") || !Boolean.parseBoolean(member.get("isBindCard").toString())){
                notBindCard = true;
            }
        }
        //是否投保,计算投保金额;免费会员不计算投保（投保 单独开线程 处理）
//        if (isInsure && !isFree && isRealName && !notBindCard) {
        if (isInsure && !isFree) {
            insureCountUseLog = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.add(insureCount, pensionTrade));
            insureCountUse = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.add(insureCountUse, insureCountUseLog));
            insureCount = 0;
        } else {
            insureCount = BigDecimalUtil.add(insureCount, pensionTrade);
            insureCount = BigDecimalUtil.fixDoubleNumProfit(insureCount);
            isInsure = false;
        }

        double activeMoney = ParameterAction.getValueOne("activeMoney");
        if(isFree && isRealName && notBindCard && insureCount>=activeMoney){
            insureCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(insureCount,-activeMoney));
            pensionCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(pensionCount,-activeMoney));

            Map<String, Object> order = new HashMap<>();
            String orderNo2 = ZQUidUtils.generateOrderNo();
            order.put("_id", ZQUidUtils.genUUID());
            order.put("memberId", memberId);
            order.put("orderNo", orderNo2);
            order.put("totalPrice", activeMoney);
            order.put("payMoney", activeMoney);
            order.put("payType", OrderInfoAction.PAY_TYPE_PENSION);
            order.put("sellerId", "A-000001");
            order.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
            order.put("createTime", System.currentTimeMillis());
            order.put("bookingTime", System.currentTimeMillis());
            order.put("endTime", System.currentTimeMillis());
            order.put("orderType", 13);
            MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", order);

            Message msg = Message.newReqMessage("1:GET@/crm/Member/activeMemberCard");
            msg.getContent().put("memberId", memberId);
            msg.getContent().put("orderNo", orderNo2);
            msg.getContent().put("payMoney", activeMoney);
            msg.getContent().put("isAutoCard", true);
            ServiceAccess.callService(msg);
        }

        //生成养老金明细记录
        Map<String, Object> log = new HashMap<>();
        String logId = UUID.randomUUID().toString();
        log.put("isInsure", isInsure);
        log.put("_id", logId);
        log.put("pensionTrade", pensionTrade);
        log.put("orderId", orderNo);
        log.put("memberId", memberId);
        log.put("insureCount", insureCount);
        log.put("insureType", "1");
        log.put("insureStatus", "2");
        log.put("insureCountUse", insureCountUseLog);
        log.put("createTime", System.currentTimeMillis());
        log.put("type", tradeType);
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionLog", log);
        //更新养老金汇总记录
        Map<String, Object> newAccount = new HashMap<>();
        newAccount.put("_id", account.get("_id"));
        newAccount.put("pensionCount", pensionCount);
        newAccount.put("insureCount", insureCount);
        newAccount.put("insureCountUse", insureCountUse);
        newAccount.put("updateTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionAccount", newAccount);

        //投百年人寿
//        if (isInsure && !isFree && isRealName && !notBindCard) {
        if (isInsure && !isFree) {
            Map<String,Object> param = new HashMap<>();
            params.put("transSeq",orderNo);
            Map<String,Object> queryResult= MysqlDaoImpl.getInstance().findOne2Map("InsureLog",param,null,null);
            //根据订单号去查询投保记录,如果查询到有记录,则说明已经投保,则不投保,反之进行投保
            if(null == queryResult || queryResult.size()== 0){
                new InsureAction().tradeMemberInsure(memberId,orderNo,logId,insureCountUseLog);
            }
        }
    }

    /**
     * 生成会员现金明细记录 和 汇总记录
     *
     * @throws Exception payId:付款ID
     *                   type:  1转账
     *                   2充值
     *                   3线上消费：若是余额支付，只扣余额，不更改其他信息
     *                   4线下消费
     *                   5退款
     *                   6线上消费，已确认收货：更新其他交易数据
     *                   7分享会员交易提成
     */
    public static void createMemberMoneyLog(String memberId, String payId, String orderId, double orderCash, String tradeType, int payType) throws Exception {
        if (orderCash < 0) {
            throw new UserOperateException(400, "交易金额错误!");
        }

        if (!Pattern.matches("^[1234567]{1}$", tradeType)) {
            throw new UserOperateException(400, "操作失败!");
        }

        //获得会员汇总记录
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", "1".equals(tradeType) ? payId : memberId);
        Map<String, Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberMoneyAccount", params, null, null);
        //若没有汇总记录,生成汇总记录/或报错
        if (account == null || account.size() == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("_id", UUID.randomUUID().toString());
            map.put("createTime", System.currentTimeMillis());
            map.put("cashCount", 0.0);
            map.put("cashCountUse", 0.0);
            map.put("cashOfflineCount", 0.0);
            map.put("totalConsume", 0.0);
            map.put("rechargeCount", 0.0);
            map.put("cashOnlineCount", 0.0);
            map.put("memberId", memberId);
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyAccount", map);
            account = map;
            //throw new UserOperateException(400, "获取会员账户失败!");
        }

        //会员账户情况
        double cashCount = Double.valueOf(account.get("cashCount").toString());//账户余额
        double canWithdrawMoney = StringUtils.mapValueIsEmpty(account,"canWithdrawMoney")?0:Double.valueOf(account.get("canWithdrawMoney").toString());//可提现金额

        double cashCountUse = Double.valueOf(account.get("cashCountUse").toString());//已使用的余额
        double cashOfflineCount = Double.valueOf(account.get("cashOfflineCount").toString());//线下交易金额

        double totalConsume = Double.valueOf(account.get("totalConsume").toString());//交易总金额
        double rechargeCount = Double.valueOf(account.get("rechargeCount").toString());//充值总金额
        double cashOnlineCount = Double.valueOf(account.get("cashOnlineCount").toString());//线上交易金额
        double teamTotal = StringUtils.mapValueIsEmpty(account,"teamTotal")?0: NumberUtils.toDouble(account.get("teamTotal").toString());//团队收益总额

        //交易类型判断,并计算交易结果
        if ("1".equals(tradeType)) {//给朋友充值
            //在这里,memberId是朋友的ID,payId是当前登录用户的ID
            //获得朋友的汇总记录
            Map<String, Object> friendParams = new HashMap<>();
            friendParams.put("memberId", memberId);
            Map<String, Object> friendAccount = MysqlDaoImpl.getInstance().findOne2Map("MemberMoneyAccount", friendParams, null, null);

            if (friendAccount == null || friendAccount.size() == 0) {
                throw new UserOperateException(400, "您的朋友尚未开通账户业务!");
            }

            double friendCashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(Double.valueOf(friendAccount.get("cashCount").toString()), orderCash));
            //生成朋友的会员现金明细记录
            Map<String, Object> log = new HashMap<>();
            log.put("_id", UUID.randomUUID().toString());
            log.put("orderId", orderId);
            log.put("payId", payId);
            log.put("cashCount", friendCashCount);
            log.put("orderCash", orderCash);
            log.put("tradeType", tradeType);
            log.put("payType", payType);
            log.put("memberId", memberId);
            log.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyLog", log);

            //更新朋友的会员现金汇总记录
            Map<String, Object> newAccount = new HashMap<>();
            newAccount.put("_id", friendAccount.get("_id"));
            newAccount.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(Double.valueOf(friendAccount.get("cashCount").toString()), orderCash)));
            newAccount.put("rechargeCount", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(Double.valueOf(friendAccount.get("rechargeCount").toString()), orderCash)));
            newAccount.put("updateTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyAccount", newAccount);
        }
        if ("2".equals(tradeType)) {//给自己充值
            cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount, orderCash));
            rechargeCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(rechargeCount, orderCash));
        }
        if ("1".equals(tradeType) || "3".equals(tradeType) || "4".equals(tradeType)) {//转账/线上交易/线下交易
            if (payType == PAY_TYPE_CASH) {//余额支付,则扣除余额
                cashCount = BigDecimalUtil.add(cashCount, -orderCash);
                cashCount = BigDecimalUtil.fixDoubleNumProfit(cashCount);
                cashCountUse = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCountUse, orderCash));
                if (cashCount < 0) {
                    throw new UserOperateException(400, "会员账户积分不足!");
                }
            } else if (payType == PAY_TYPE_WECHAT
                        || payType == PAY_TYPE_ALIPAY
                        || payType == PAY_TYPE_GET_CASH
                        || payType == PAY_TYPE_GPAY) {//微信,支付宝,线下现金支付
            } else {
                throw new UserOperateException(400, "请选择支付方式!");
            }
            if ("4".equals(tradeType)) {//线下交易总额
                cashOfflineCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashOfflineCount, orderCash));
            }
//            if ("3".equals(tradeType)) {//线上交易总额
//                cashOnlineCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashOnlineCount, orderCash));
//            }
        }

        //退款
        if("5".equals(tradeType)){
            cashOnlineCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashOnlineCount, -orderCash));//线上交易额
            totalConsume = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(totalConsume, -orderCash));//交易总金额
            if(payType==3){
                cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount, orderCash));//余额
                cashCountUse = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCountUse, -orderCash));//已使用余额
            }
        }

        //确认收货
        if ("6".equals(tradeType)) {//线上交易总额
            cashOnlineCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashOnlineCount, orderCash));
        }

        //交易总额
        if("1".equals(tradeType) || "4".equals(tradeType) || "6".equals(tradeType)){
            totalConsume = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(totalConsume, orderCash));
        }

        if("7".equals(tradeType)){
            cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount, orderCash));
            canWithdrawMoney = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(canWithdrawMoney, orderCash));
            teamTotal = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(teamTotal, orderCash));
        }

        if (!"6".equals(tradeType)){
            //生成会员现金明细记录
            Map<String, Object> log = new HashMap<>();
            log.put("_id", UUID.randomUUID().toString());
            log.put("memberId", "1".equals(tradeType) ? payId : memberId);
            log.put("orderId", orderId);
            log.put("payId", payId);
            log.put("cashCount", cashCount);
            log.put("orderCash", orderCash);
            log.put("tradeType", tradeType);
            log.put("payType", payType);
            log.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyLog", log);
        }

        //更新会员现金汇总记录
        Map<String, Object> newAccount = new HashMap<>();
        newAccount.put("_id", account.get("_id"));
        newAccount.put("canWithdrawMoney", canWithdrawMoney);
        newAccount.put("cashCount", cashCount);
        newAccount.put("cashCountUse", cashCountUse);
        newAccount.put("cashOfflineCount", cashOfflineCount);
        newAccount.put("cashOnlineCount", cashOnlineCount);
        newAccount.put("totalConsume", totalConsume);
        newAccount.put("rechargeCount", rechargeCount);
        newAccount.put("teamTotal", teamTotal);
        newAccount.put("updateTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyAccount", newAccount);
    }

    /**
     * 初始化现金汇总
     */
    @GET
    @Path("/initSellerAccount")
    public void initSellerAccount() throws Exception {
        Map<String, Object> sellerAccount = new HashMap<>();
        sellerAccount.put("_id", UUID.randomUUID().toString());
        sellerAccount.put("createTime", System.currentTimeMillis());
        sellerAccount.put("sellerId", ControllerContext.getPString("userId"));
        sellerAccount.put("cashCount", 0.0);
        sellerAccount.put("cashCountUse", 0.0);
        sellerAccount.put("brokerageCountTotal", 0.0);
        sellerAccount.put("orderCashCount", 0.0);
        sellerAccount.put("income", 0.0);
        MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", sellerAccount);
    }

    /**
     * 初始化服务站汇总
     */
    @GET
    @Path("/initFactorAccount")
    public void initFactorAccount() throws Exception {
        Map<String, Object> account = new HashMap<>();
        account.put("_id", UUID.randomUUID().toString());
        account.put("createTime", System.currentTimeMillis());
        account.put("factorId", ControllerContext.getPString("userId"));
        account.put("cashCount", 0.0);
        account.put("cashCountUse", 0.0);
        account.put("income", 0.0);
        MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", account);
    }

    /**
     * 初始化代理商汇总
     */
    @GET
    @Path("/initAgentAccount")
    public void initAgentAccount() throws Exception {
        Map<String, Object> account = new HashMap<>();
        account.put("_id", UUID.randomUUID().toString());
        account.put("createTime", System.currentTimeMillis());
        account.put("agentId", ControllerContext.getPString("userId"));
        account.put("cashCount", 0.0);
        account.put("cashCountUse", 0.0);
        account.put("income", 0.0);
        MysqlDaoImpl.getInstance().saveOrUpdate("AgentMoneyAccount", account);
    }
    /**
     * 初始化代理商汇总
     */
    @POST
    @Path("/createSellerLog")
    public void createSellerLog() throws Exception {
        String sellerId = ControllerContext.getPString("sellerId");
        String memberId = ControllerContext.getPString("memberId");
        String orderNo = ControllerContext.getPString("orderNo");
        double brokerageCount = ControllerContext.getPDouble("brokerageCount");
        double orderCash = ControllerContext.getPDouble("orderCash");
        String tradeType = ControllerContext.getPString("tradeType");
        int sellerPayType = ControllerContext.getPInteger("sellerPayType");
        int memberPayType = ControllerContext.getPInteger("memberPayType");
        createSellerLog(sellerId, memberId, orderNo, brokerageCount, orderCash, tradeType, sellerPayType,memberPayType);
    }

    /**
     * 生成商家交易明细记录 和 汇总记录
     *
     * @throws Exception tradeType:  1转账//未使用
     *                   2充值//未使用
     *                   3线上消费
     *                   4现金交易
     *                   5会员/非会员 扫码支付
     *                   <p/>
     *                   sellerPayType:1.余额支付,2.微信,3.支付宝
     *                   6:团队收益
     */
    private static void createSellerLog(String sellerId, String memberId, String orderNo, double brokerageCount, double orderCash, String tradeType, int sellerPayType,int memberPayType) throws Exception {
        brokerageCount = BigDecimalUtil.fixDoubleNumProfit(brokerageCount);
        double incomeOne = 0.0;//单笔入账金额:交易金额-佣金

        //获取商家汇总记录
        Map<String, Object> sellerMap = new HashMap<>();
        sellerMap.put("sellerId", sellerId);
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("SellerMoneyAccount", sellerMap, null, null);
        //获取失败生成汇总记录/或报错
        if (re == null || re.size() == 0) {
            Map<String, Object> sellerAccount = new HashMap<>();
            sellerAccount.put("_id", UUID.randomUUID().toString());
            sellerAccount.put("createTime", System.currentTimeMillis());
            sellerAccount.put("sellerId", sellerId);
            sellerAccount.put("cashCount", 0.0);
            sellerAccount.put("cashCountUse", 0.0);
            sellerAccount.put("brokerageCountTotal", 0.0);
            sellerAccount.put("orderCashCount", 0.0);
            sellerAccount.put("income", 0.0);
            sellerAccount.put("teamTotal", 0.0);
            MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", sellerAccount);
            re = sellerAccount;
        }

        Double cashCount = Double.valueOf(re.get("cashCount").toString()); //余额
        Double cashCountUse = Double.valueOf(re.get("cashCountUse").toString()); //已使用余额
        Double brokerageCountTotal = Double.valueOf(re.get("brokerageCountTotal").toString()); //总佣金
        Double income = Double.valueOf(re.get("income").toString()); //历史总入账
        Double orderCashCount = BigDecimalUtil.add(Double.valueOf(re.get("orderCashCount").toString()), orderCash); //订单交易总额
        Double teamTotal = StringUtils.mapValueIsEmpty(re,"teamTotal")?0:BigDecimalUtil.add(Double.valueOf(re.get("teamTotal").toString()), orderCash); //团队分润总入账

        // 在线交易,会员扫码,和非会员扫码支付,若是余额支付的,商家增加余额,入账;现金交易 ,以及其他支付方式商家不增加余额,不增加入账
        // 团队收益也增加
//        if (Pattern.matches("^[35]$", tradeType) && Pattern.matches("^[3]$", String.valueOf(memberPayType))) {
        if (Pattern.matches("^[356]$", tradeType)) {
            cashCount = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(Double.valueOf(re.get("cashCount").toString()), orderCash));
            incomeOne = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(orderCash, -brokerageCount));
            income = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(income, incomeOne));//历史总入账
        }

        if(tradeType.equals("6")){
            teamTotal = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(teamTotal, orderCash));//团队分润总入账
        }

        //商家默认用余额支付佣金//-1:非会员支付
        if (!"-1".equals(memberId) && !tradeType.equals("6")) {
            if (sellerPayType == PAY_TYPE_CASH) {//余额支付
                cashCount = BigDecimalUtil.add(cashCount, -brokerageCount);
                cashCountUse = BigDecimalUtil.add(cashCountUse, brokerageCount);//已使用余额
            } else if (sellerPayType == PAY_TYPE_WECHAT) {//微信

            } else if (sellerPayType == PAY_TYPE_ALIPAY) {//支付宝

            } else {
                throw new UserOperateException(400, "请选择支付方式!");
            }
            //计算交易金额/余额支付
            brokerageCountTotal = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(brokerageCountTotal, brokerageCount));//总佣金

            if (cashCount < 0) {//余额为负继续扣款
//                throw new UserOperateException(400, "商家账户余额不足!");
            }
        }

        //更新商家汇总记录
        Map<String, Object> account = new HashMap<>();
        account.put("_id", re.get("_id"));
        account.put("updateTime", System.currentTimeMillis());
        account.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(cashCount));
        account.put("cashCountUse", BigDecimalUtil.fixDoubleNumProfit(cashCountUse));
        account.put("brokerageCountTotal", BigDecimalUtil.fixDoubleNumProfit(brokerageCountTotal));
        account.put("orderCashCount", orderCashCount);
        account.put("income", income);
        account.put("teamTotal", teamTotal);
        MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", account);

        //生成商家明细表
        Map<String, Object> log = new HashMap<>();
        log.put("_id", UUID.randomUUID().toString());
        log.put("orderCash", orderCash);
        log.put("sellerId", sellerId);
        log.put("orderId", orderNo);
        log.put("tradeId", memberId);
        log.put("brokerageCount", brokerageCount);
        log.put("tradeType", tradeType);
        log.put("createTime", System.currentTimeMillis());
        log.put("incomeOne", incomeOne);
        log.put("cashCountCur", cashCount);
        MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyLog", log);
    }


    /**
     * 生成代理商提成
     *
     * @throws Exception
     */
    @GET
    @Path("/createAgentMoney")
    public void createAgentMoney() throws Exception {
        double pensionMoney = ControllerContext.getPDouble("pensionMoney");
        String orderId = ControllerContext.getPString("orderId");
        String memberId = ControllerContext.getPString("memberId");
        String isActive = ControllerContext.getPString("isActive") == null ? "" : ControllerContext.getPString("isActive");
        int tradeType;
        if ("my".equals(ControllerContext.getPString("isActive"))) {
            tradeType = 5;
        } else if ("factor".equals(ControllerContext.getPString("isActive"))) {
            tradeType = 4;
        } else if (StringUtils.isEmpty(ControllerContext.getPString("isActive"))) {
            tradeType = 3;
        } else {
            tradeType = 4;
        }
        createAgentMoney(pensionMoney, orderId, memberId, tradeType);
    }


    /**
     * 生成代理商提成
     * tradeType   1-充值;2-提现;3-会员交易提成;4-激活提成;5-会员充值分润
     * @throws Exception
     */
    public static void createAgentMoney(double deduct, String orderId, String memberId, int tradeType) throws Exception {
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        List<Map<String, Object>> re1 = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> orderBy = new HashMap<>();
        orderBy.put("level", 1);
        if (tradeType == 4) {//会员激活提成 先检查会员是否是在服务站下注册（是否已经有归属），若是，则获取
            Message message = Message.newReqMessage("1:GET@/crm/Member/show");
            message.getContent().put("_id",memberId);
            JSONObject memberJson = ServiceAccess.callService(message).getContent();
            if(memberJson == null || memberJson.size()==0){
                throw new UserOperateException(500,"获取会员数据失败");
            }
            Map<String, Object> v = new HashMap<>();
            String belongAreaValue = ControllerContext.getPString("belongAreaValue");
            //保存会员归属地地址
            if(StringUtils.isEmpty(belongAreaValue)){
                if(StringUtils.mapValueIsEmpty(memberJson,"belongAreaValue")){
                    message = Message.newReqMessage("1:GET@/account/Factor/getFactorInfo");
                    JSONObject agentJson = ServiceAccess.callService(message).getContent();
                    v.put("belongAreaValue", agentJson.get("areaValue"));
                }else{
                    v.put("belongAreaValue", memberJson.get("belongAreaValue"));
                }
            }else{
                v.put("belongAreaValue", belongAreaValue);
            }

            re1.add(v);
            list = ParameterAction.getValueMore("cardProfit");
        } else if (tradeType == 6){//会员充值
            tradeType = 5;
            //查询该会员所在的服务站
            p.add(memberId);
            r.add("belongAreaValue");
            String sql = "select t1.belongAreaValue from Member t1 where t1._id=?";
            re1 = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
//            list = MysqlDaoImpl.getInstance().findAll2Map("RechargeProfitManage", null, orderBy, new String[]{"profitRatio"}, Dao.FieldStrategy.Include);
            list = ParameterAction.getValueMore("rechargeProfit");
        } else {
            p.add(memberId);
            r.add("belongAreaValue");
            //查询该会员所在的服务站
            String sql = "select t1.belongAreaValue from Member t1 where t1._id=?";
            re1 = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            if (tradeType == 5){
                list = ParameterAction.getValueMore("cardProfit");
                tradeType = 4;
            }else{
                list = ParameterAction.getValueMore("profitRatio");
//            list = MysqlDaoImpl.getInstance().findAll2Map("ProfitManage", null, orderBy, new String[]{"profitRatio"}, Dao.FieldStrategy.Include)
            }
        }

        //获取会员团队关系
        Map<String,Object> team = TeamAction.getTeamUp(memberId);

//        if (re1 == null || re1.size() == 0 || re1.get(0).get("belongAreaValue") == null
//                || StringUtils.isEmpty(re1.get(0).get("belongAreaValue").toString())) {
//            //throw new UserOperateException(400, "该会员没有匹配的服务站");
//            //没有匹配发卡的会员交易返佣, 全部给平台
//            String profitRatioStr = list.get(0).get("val").toString();
//            Double profitRatio = BigDecimalUtil.fixDoubleNum2Down(BigDecimalUtil.divide(Double.valueOf(profitRatioStr), 100));
//
//            new AgentMoneyAccountService().addMoneyToAgent(profitRatio, deduct, orderId, memberId, Constants.AGENT_PROFORM_ID, tradeType);
//        } else {

        String[] agents;
        if (re1 != null && re1.size() != 0 && !StringUtils.mapValueIsEmpty(re1.get(0),"belongAreaValue")) {
            String areaValue = (String) re1.get(0).get("belongAreaValue");
            areaValue = areaValue.substring(1);
            areaValue = areaValue.substring(0, areaValue.length() - 1);
            agents = areaValue.split("_");
        }else{
            agents = new String[]{"A-000001"};
        }
        // 将代理商、服务站、商家、会员添加进循环列表
        List<Map<String,Object>> userList = new ArrayList<>();
        int count=0;
        for(String str : agents){
            Map<String,Object> item = new HashMap<>();
            item.put(count==4?"factorId":"agentId",str);
            userList.add(item);
            count++;
        }
        if(team!=null && team.size()!=0){
            if(!StringUtils.mapValueIsEmpty(team,"sellerId") && !StringUtils.mapValueIsEmpty(team,"sellerCanUse")
                    && Boolean.parseBoolean(team.get("sellerCanUse").toString())){
                Map<String,Object> item = new HashMap<>();
                item.put("sellerId",team.get("sellerId"));
                item.put("canUse",team.get("sellerCanUse"));
                userList.add(item);
            }
            if(!StringUtils.mapValueIsEmpty(team,"member")){
                List<Map<String,Object>> member = (List<Map<String,Object>>)team.get("member");
                userList.addAll(member);
            }
        }

        //添加服务站的提成明细, 通过 member的belongAreaValue字段自动生成收益,同时记录舍去的分
        int i = 0;
        Double shen = deduct;
        Double pingtai_profitRatio = 0.0, pingtai_orderCash = 0.0;
        String pingtai_id = agents[0];
        Double useMoney = 0.0;//已分出去的提成金额

        // 循环分配代理商、服务站
        for (Map<String,Object> user : userList) {
            if(!StringUtils.mapValueIsEmpty(user,"factorId") && i<4){//4
                i=4;
            } else if(!StringUtils.mapValueIsEmpty(user,"sellerId") && i<5){//5
                i=5;
            } else if(!StringUtils.mapValueIsEmpty(user,"memberId") && i<6){//6,7,8
                i=6;
            }

            Double profitRatio = BigDecimalUtil.fixDoubleNum2Down(Double.valueOf(list.get(i).get("val").toString()));

            double orderCash = BigDecimalUtil.divide(BigDecimalUtil.multiply(deduct, profitRatio), 100);
            orderCash = BigDecimalUtil.fixDoubleNum2Down(orderCash);

            shen = BigDecimalUtil.add(shen, -orderCash);
            if (i == 0) {//平台
                pingtai_profitRatio = profitRatio;
                pingtai_orderCash = orderCash;
            } else if(!StringUtils.mapValueIsEmpty(user,"agentId")){
//                    new AgentMoneyAccountService().addMoneyToAgent(profitRatio, orderCash, orderId, memberId, agentId);
                //判断是否可用 不可用则将钱分给平台
                if ("true".equals(queryAgentCanUse("agent", user.get("agentId").toString()))) {
                    useMoney = BigDecimalUtil.add(useMoney, orderCash);
                    new AgentMoneyAccountService().addMoneyToAgent(profitRatio, orderCash, orderId, memberId, user.get("agentId").toString(), tradeType);
                } else {
                    shen = BigDecimalUtil.add(shen, orderCash);
                    pingtai_profitRatio = BigDecimalUtil.add(pingtai_profitRatio, profitRatio);
                }
            } else if(!StringUtils.mapValueIsEmpty(user,"factorId")){
//                    new FactorMoneyAccountService().addMoneyToFactor(profitRatio, orderCash, orderId, memberId, agentId);
                //判断是否可用 不可用则将钱分给平台
                if ("true".equals(queryAgentCanUse("factor", user.get("factorId").toString()))) {
                    useMoney = BigDecimalUtil.add(useMoney, orderCash);
                    new FactorMoneyAccountService().addMoneyToFactor(profitRatio, orderCash, orderId, memberId, user.get("factorId").toString(), tradeType);
                } else {
                    shen = BigDecimalUtil.add(shen, orderCash);
                    pingtai_profitRatio = BigDecimalUtil.add(pingtai_profitRatio, profitRatio);
                }
            } else if(!StringUtils.mapValueIsEmpty(user,"sellerId")){
                if (!StringUtils.mapValueIsEmpty(user,"canUse") && Boolean.parseBoolean(user.get("canUse").toString())) {
                    useMoney = BigDecimalUtil.add(useMoney, orderCash);
                    createSellerLog(user.get("sellerId").toString(),memberId,orderId,0,orderCash,"6",0,0);
                } else {
                    shen = BigDecimalUtil.add(shen, orderCash);
                    pingtai_profitRatio = BigDecimalUtil.add(pingtai_profitRatio, profitRatio);
                }
            }  else if(!StringUtils.mapValueIsEmpty(user,"memberId")){
                if (!StringUtils.mapValueIsEmpty(user,"canUse") && Boolean.parseBoolean(user.get("canUse").toString())
                        && !StringUtils.mapValueIsEmpty(user,"isBindCard") && Boolean.parseBoolean(user.get("isBindCard").toString())) {
                    useMoney = BigDecimalUtil.add(useMoney, orderCash);
                    createMemberMoneyLog(user.get("memberId").toString(),null,orderId,orderCash,"7",PAY_TYPE_TEAM);
                } else {
                    shen = BigDecimalUtil.add(shen, orderCash);
                    pingtai_profitRatio = BigDecimalUtil.add(pingtai_profitRatio, profitRatio);
                }
            }
            i++;
        }

        //将剩下的分数(如果有), 加给平台
        if (shen > 0) {
            if (deduct - useMoney > 0) {
                shen = BigDecimalUtil.add(deduct, -useMoney);
                pingtai_orderCash = BigDecimalUtil.fixDoubleNumProfit(shen);
            } else {
                pingtai_orderCash = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(pingtai_orderCash, shen));
            }

//                pingtai_orderCash = BigDecimalUtil.fixDoubleNum2Down(pingtai_orderCash);
        }
        new AgentMoneyAccountService().addMoneyToAgent(pingtai_profitRatio, pingtai_orderCash, orderId, memberId, pingtai_id, tradeType);
//        }
    }

    /**
     * 查询代理商服务站是否被可用
     *
     * @throws Exception
     */
    private static String queryAgentCanUse(String type, String id) throws Exception {
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        r.add("canUse");
        String from = " from";
        if ("agent".equals(type)) {
            from += " Agent";
        } else {
            from += " Factor";
        }
        String sql = "select" +
                " canUse" +
                from +
                " where _id=?";
        p.add(id);
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (re != null && re.size() > 0) {
            return re.get(0).get("canUse").toString();
        }
        return "false";
    }


//    /**
//     * 激活会员卡费用
//     *
//     * @throws Exception
//     */
//    public void withdrawal() throws Exception {
//        double money = ControllerContext.getPDouble("money");
//        String factorId = null;
//        String sellerId = null;
//        String type = ControllerContext.getPString("type");
//        String isActive = ControllerContext.getPString("isActive");
//        String tradeId = ControllerContext.getPString("tradeId");
//        String payType = ControllerContext.getPString("payType");
//        String notWithdraw = ControllerContext.getPString("notWithdraw");
//
//        if (StringUtils.isEmpty(notWithdraw) && "isActive".equals(notWithdraw)) {
//            throw new UserOperateException(400, "激活会员失败!");
//        }
//        //明细
//        Map<String, Object> map = new HashMap<>();
//        map.put("_id", UUID.randomUUID().toString());
//        map.put("orderCash", money);
//        map.put("createTime", System.currentTimeMillis());
//        if ("factor".equals(type)) {//服务站替会员支付100元激活会员卡
//            if (StringUtils.isNotEmpty(isActive)) {
//                if (StringUtils.isEmpty(payType)) {
//                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请选择支付方式!");
//                }
//                if (StringUtils.isEmpty(tradeId)) {
//                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "获取会员账号失败!");
//                }
//            }
//
//            String otherDataJson = ControllerContext.getContext().getOtherDataJson();
//            JSONObject other = JSONObject.fromObject(otherDataJson);
//            if (StringUtils.mapValueIsEmpty(other, "factorId")) {
//                throw new UserOperateException(400, "该用户不拥有服务站权限");
//            }
//            factorId = (String) other.get("factorId");
//            map.put("factorId", factorId);
//            map.put("tradeId", tradeId);
//
//            if ("Y".equals(isActive)) {
//                map.put("type", 4);
//            } else {
//                map.put("type", 2);
//            }
//            MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyLog", map);
//        } else {
//            sellerId = ControllerContext.getContext().getCurrentSellerId();
//            map.put("sellerId", sellerId);
//
//            map.put("tradeType", 2);
//            MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyLog", map);
//        }
//
//        //账户汇总
//        List<Object> p = new ArrayList<>();
//        List<String> r = new ArrayList<>();
//        r.add("_id");
//        r.add("cashCount");
//        r.add("cashCountUse");
//        String from = " from";
//        String where = " where";
//        if ("factor".equals(type)) {
//            p.add(factorId);
//            from += " FactorMoneyAccount";
//            where += " factorId=?";
//        } else {
//            p.add(sellerId);
//            from += " SellerMoneyAccount";
//            where += " sellerId=?";
//        }
//        String sql = "select" +
//                " _id" +
//                ",cashCount" +
//                ",cashCountUse" +
//                from + where;
//        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
//        if (re == null || re.size() == 0) {
//            map.clear();
//            map.put("_id", UUID.randomUUID().toString());
//            map.put("createTime", System.currentTimeMillis());
//            map.put("cashCount", 0);
//            map.put("cashCountUse", 0);
//            map.put("income", 0);
//            if ("factor".equals(type)) {
//                map.put("factorId", factorId);
//                MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", map);
//            } else {
//                map.put("sellerId", sellerId);
//                MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", map);
//            }
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "余额不足!");
//
//        } else {
//            map.clear();
//            if ("10".equals(payType)) {//微信支付
//
//            } else if ("4".equals(payType)) {//支付宝支付
//
//            } else {
//                if (Double.valueOf(re.get(0).get("cashCount").toString()) < money) {
//                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "余额不足!");
//                }
//                double cashCount = BigDecimalUtil.add(Double.valueOf(re.get(0).get("cashCount").toString()), -money);
//                map.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(cashCount));
//                double cashCountUse = BigDecimalUtil.add(Double.valueOf(re.get(0).get("cashCountUse").toString()), money);
//                map.put("cashCountUse", BigDecimalUtil.fixDoubleNumProfit(cashCountUse));
//            }
//
//            map.put("_id", re.get(0).get("_id"));
//            map.put("updateTime", System.currentTimeMillis());
//
//            if ("factor".equals(type)) {
//                MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", map);
//            } else {
//                MysqlDaoImpl.getInstance().saveOrUpdate("SellerMoneyAccount", map);
//            }
//        }
//    }


    /**
     * 新建会员,生成会员账户汇总表,养老金汇总表
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/createMemberAccount")
    public void createMemberAccount() throws Exception {
        String memberId = ControllerContext.getPString("memberId");

        //获得会员汇总记录
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        Map<String, Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberMoneyAccount", params, null, null);
        if (account == null || account.size() == 0) {
            Map<String, Object> newAccount = new HashMap<>();
            newAccount.put("_id", UUID.randomUUID().toString());
            newAccount.put("createTime", System.currentTimeMillis());
            newAccount.put("cashCount", 0.0);
            newAccount.put("cashCountUse", 0.0);
            newAccount.put("cashOfflineCount", 0.0);
            newAccount.put("totalConsume", 0.0);
            newAccount.put("rechargeCount", 0.0);
            newAccount.put("cashOnlineCount", 0.0);
            newAccount.put("memberId", memberId);
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberMoneyAccount", newAccount);
        }

        createPensionAccount(memberId);
    }

    public void createPensionAccount(String memberId) throws Exception {
        //获得会员养老金汇总记录
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        Map<String, Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberPensionAccount", params, null, null);
        if (account == null || account.size() == 0) {
            Map<String, Object> newAccount = new HashMap<>();
            newAccount.put("_id", UUID.randomUUID().toString());
            newAccount.put("createTime", System.currentTimeMillis());
            newAccount.put("pensionCount", 0.0);
            newAccount.put("insureCountUse", 0.0);
            newAccount.put("insureCount", 0.0);
            newAccount.put("memberId", memberId);
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberPensionAccount", newAccount);
        }
    }

    /**
     * 激活会员卡,生成会员养老金汇总表
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/createPensionAccount")
    public void createPensionAccount() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        createPensionAccount(memberId);
    }

//    /**
//     * 会员支付
//     *
//     * @throws Exception
//     */
//    @POST
//    @Member
//    @Path("/memberPay")
//    public void memberPay() throws Exception {
//        String memberId = ControllerContext.getContext().getCurrentUserId();
//        Double money = ControllerContext.getPDouble("money");
//        int payType = ControllerContext.getPInteger("payType");
//        String tradeType = ControllerContext.getPString("tradeType");
//        String orderNo = ControllerContext.getPString("orderNo");
//
//        //生成会员现金明细记录/汇总记录
//        createMemberMoneyLog(memberId, memberId, orderNo, money, tradeType, payType);
//    }


    /**
     * 获取养老金汇总记录
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getPensionCount")
    public void getPensionCount() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        Map<String, Object> account = MysqlDaoImpl.getInstance().findOne2Map("MemberPensionAccount", params, null, null);
        toResult(Response.Status.OK.getStatusCode(), account);
    }


    /**
     * 获取商家现金汇总
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getSellerAccount")
    public void getSellerAccount() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", ControllerContext.getContext().getCurrentSellerId());
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("SellerMoneyAccount", params, null, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 获取商家昨日收入
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getSellerAccountByYesterday")
    public void getSellerAccountByYesterday() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Long nowTime = cal.getTimeInMillis();

        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getCurrentSellerId());
        params.add(nowTime);
        params.add(nowTime + (60 * 60 * 1000 * 23 + 60 * 59 * 1000 + 59 * 1000));

        String where = " where sellerId=? and createTime>=? and createTime<? and (tradeType=3 or tradeType=4 or tradeType=5)";

        List<String> returnFields = new ArrayList<>();
        returnFields.add("account");
        String sql = "select" +
                " sum(orderCash) as account" +
                " from SellerMoneyLog" + where;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }

    /**
     * 获取商家最近七天收入情况
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getSellerAccountByWeek")
    public void getSellerAccountByWeek() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Long nowTime = cal.getTimeInMillis();

        List<Object> p = new ArrayList<>();
        p.add(ControllerContext.getContext().getCurrentSellerId());
        p.add(nowTime - (24 * 60 * 60 * 1000 * 7));
        p.add(nowTime);
        List<String> f = new ArrayList<>();
        f.add("days");
        f.add("week");
        f.add("daySales");

        String sql = "select" +
                " from_unixtime(floor(createTime/1000),'%Y%m%d') as days" +
                ",from_unixtime(floor(createTime/1000),'%w') as week" +
                ",truncate(sum(case when tradeType = 100 then -orderCash else orderCash end),2) as daySales" +
                " from SellerMoneyLog";
        String where = " where sellerId=? and createTime>=? and createTime<? and tradeType in (3,4,5,100)";
        String orderBy = " group by days";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, where, null, orderBy, null, f, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 获取服务站现金汇总
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getFactorAccount")
    public void getFactorAccount() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有服务站权限");
        }
        params.put("factorId", other.getString("factorId"));
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("FactorMoneyAccount", params, null, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 获取服务站今日收入
     * (备注:因为此方法初始为昨日收入,后因业务调整为今日)
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getFactorAccountByYesterday")
    public void getFactorAccountByYesterday() throws Exception {
//        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
//        JSONObject other = JSONObject.fromObject(otherDataJson);
//        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
//            throw new UserOperateException(400, "该用户不拥有服务站权限");
//        }
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//
//        Long nowTime = cal.getTimeInMillis();
//
//        List<Object> params = new ArrayList<>();
//        params.add(other.getString("factorId"));
//        params.add(nowTime);
//        params.add(nowTime + (60 * 60 * 1000 * 23 + 60 * 59 * 1000 + 59 * 1000));
//
//        String where = " where factorId=? and createTime>=? and createTime<? and (type in (3,5) or (type=4 and orderCash<>100))";
//
//        List<String> returnFields = new ArrayList<>();
//        returnFields.add("account");
//        String sql = "select" +
//                " sum(orderCash) as account" +
//                " from FactorMoneyLog" + where;
//        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
//        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }


    /**
     * 获取服务站最近七天收入情况
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getFactorAccountByWeek")
    public void getFactorAccountByWeek() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有服务站权限");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Long nowTime = cal.getTimeInMillis();

        List<Object> p = new ArrayList<>();
        p.add(other.getString("factorId"));
        p.add(nowTime - (24 * 60 * 60 * 1000 * 7));
        p.add(nowTime);
        List<String> f = new ArrayList<>();
        f.add("days");
        f.add("week");
        f.add("daySales");

        String sql = "select" +
                " from_unixtime(floor(createTime/1000),'%Y%m%d') as days" +
                ",from_unixtime(floor(createTime/1000),'%w') as week" +
                ",truncate(sum(orderCash),2) as daySales" +
                " from FactorMoneyLog";
        String where = " where factorId=? and createTime>=? and createTime<? and type in (3,4,5)";
        String orderBy = " group by days";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, where, null, orderBy, null, f, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }


    /**
     * 平台管理:清算报表
     */
    public Page getBillPage(Map<String, Object> v) throws Exception {
        String areaValue = v.get("areaValue").toString();
        String createTime = v.get("createTime").toString();
        String search = v.get("search").toString();
        String orderNo = v.get("orderNo").toString();
        String userId = v.get("userId").toString();
        String notOrderType = v.get("notOrderType").toString();
        String isGetOne = v.get("isGetOne").toString();
        String pid = v.get("pid").toString();
        String orderType = v.get("orderType").toString();
        String orderStatus = v.get("orderStatus").toString();
        String notOrderStatus = v.get("notOrderStatus").toString();
        String payType = v.get("payType").toString();
        String queryType = v.get("queryType").toString();

        long pageNo = Long.valueOf(v.get("pageNo").toString());
        int pageSize = Integer.valueOf(v.get("pageSize").toString());

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }

        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);
        int agentLevel = Integer.valueOf(msg.getContent().get("level").toString());

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1 ";
        String left = "";
        String sqlField="";

        if(StringUtils.isNotEmpty(pid)){
            where +=" and t1.pid=?";
            p.add(pid);
        }

        //单条查询
        if (StringUtils.isNotEmpty(isGetOne)){
            where += " and t1.orderNo = ? ";
            p.add(isGetOne);
        }

        //不显示某个状态的订单
        if(StringUtils.isNotEmpty(notOrderStatus)){
            String[] notList=notOrderStatus.split(",");
            String notStr = "";
            for(int i=0,len=notList.length;i<len;i++){
                if(!Pattern.matches("^[0123456789]|(10)|(11)|(12)$",notList[i])){
                    throw new UserOperateException(500,"错误的订单类型");
                }
                notStr+="?,";
                p.add(notList[i]);
            }
            where += " and orderStatus not in ("+notStr.substring(0,notStr.length()-1)+")";
        }

        if (StringUtils.isNotEmpty(orderStatus)){
            where += " and orderStatus = ?";
            p.add(orderStatus);
        }

        //用于结算报表的条件
        if (StringUtils.isNotEmpty(userId)) {
            String userType = userId.substring(0, 1);
            areaValue = areaValue.replaceAll("___like_", "");
            if ("A".equals(userType)) {
                where +=" and (t2.belongAreaValue like ? or t3.belongAreaValue like ? or t4.areaValue like ?";
                if(areaValue.indexOf("A-000001")!=-1){
                    where +=" or t2.belongAreaValue is null or t2.belongAreaValue =''";
                }
                where += " )" +
                        " and t1.orderType in (0,1,5,6,7,8,11,12,13) and ((t1.orderType=11 and t1.pid<>-1) or t1.orderType<>11)" +
                        " and t6.type in (3,4,5)" +
                        " and t6.agentId=?";
                left = " left join AgentMoneyLog t6 on t1.orderNo = t6.orderId";
                sqlField+=",t6.orderCash";
                r.add("orderCash");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(userId);
            } else if ("F".equals(userType)) {
                where += " and (t2.belongAreaValue like ? or t3.belongAreaValue like ? or t4.areaValue like ?)" +
                        " and t1.orderType in (0,1,5,6,7,8,11,12,13) and ((t1.orderType=11 and t1.pid<>-1) or t1.orderType<>11)" +
                        " and t6.type in (3,4,5)" +
                        " and t6.factorId=?";
                left = " left join FactorMoneyLog t6 on t1.orderNo = t6.orderId";
                sqlField+=",t6.orderCash";
                r.add("orderCash");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(userId);
            } else if ("S".equals(userType)) {
                where += " and t1.orderType in (0,1,2,11,12) and t1.sellerId=? and ((t1.orderType=11 and t1.pid<>-1) or t1.orderType<>11)" +
                        " and t6.tradeType in (3,4,5,6)" +
                        " and t6.sellerId=?";
                left = " left join SellerMoneyLog t6 on t1.orderNo = t6.orderId";
                sqlField+=",t6.incomeOne as orderCash";
                r.add("orderCash");
                p.add(userId);
                p.add(userId);
            }
        }else{
            where +=" and ((t1.orderType=11 and t1.pid<>-1) or (t1.orderType<>11))";
        }
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            if (areaValue.substring(2, areaValue.length() - 2).split("\\_").length != 1) {
                where += " and (t2.belongAreaValue like ? or t3.belongAreaValue like ? or t4.areaValue like ?)";
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
            }
            if (agentLevel > 1) {
                where += " and t2.belongAreaValue like '_A-000001_A%'";
            }
        }
        if (StringUtils.isNotEmpty(orderNo)) {
            where += " and t1.orderNo like ?";
            p.add("%" + orderNo + "%");
        }
        if(StringUtils.isNotEmpty(queryType) && queryType.equals("endTime")){
            where += " and t1.orderStatus=100";
        }else{
            queryType="createTime";
        }
        if (startTime != 0) {
            where += " and t1."+queryType+">?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1."+queryType+"<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(search)) {
            where += " and (t2.realName like ? or t3.name like ? or t4.name like ?" +
                    " or t2.mobile like ? or t3.phone like ? or t4.mobile like ?" +
                    " or t2.idCard like ? or t3.realCard like ? or t4.realCard like ?" +
                    " or t2.cardNo like ? or t2._id like ? or t3._id like ? or t4._id like ?)";
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
        }
        if (StringUtils.isNotEmpty(orderType)) {
            where += " and t1.orderType=?";
            p.add(orderType);
        }
        if (StringUtils.isNotEmpty(notOrderType)) {
            where += " and t1.orderType<>?";
            p.add(notOrderType);
        }
        if (StringUtils.isNotEmpty(payType)) {
            where += " and t1.payType = ?";
            p.add(payType);
        }

        String hql = "select count(t1._id) as totalCount" +
                " from OrderInfo t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join Seller t3 on t1.sellerId=t3._id" +
                " left join Factor t4 on t1.sellerId=t4._id" +
                left+
                where;
        List<String> returnFieldHql = new ArrayList<>();
        returnFieldHql.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, returnFieldHql, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.add("_id");
        r.add("pid");
        r.add("orderCreateTime");
        r.add("endTime");
        r.add("score");
        r.add("orderStatus");
        r.add("payType");
        r.add("payMoney");
        r.add("pensionMoney");
        r.add("orderType");
        r.add("orderNo");
        r.add("cardNo");
        r.add("belongMember");
        r.add("belongSeller");
        r.add("belongFactor");

        r.add("belongValueMember");
        r.add("belongValueFriend");
        r.add("belongValueSeller");
        r.add("belongValueFactor");

        r.add("nameMember");
        r.add("nameMemberAcq");
        r.add("nameSeller");
        r.add("nameFactor");

        r.add("memberId");
        r.add("sellerId");
        r.add("factorId");
        r.add("friendId");

        r.add("cashCount");
        String sql = "select" +
                " distinct t1._id" +
                ",t1.pid" +
                ",t1.createTime as orderCreateTime" +
                ",t1.endTime" +
                ",t1.score" +
                ",t1.orderStatus" +
                ",t1.payType" +
                ",t1.payMoney" +
                ",t1.pensionMoney" +
                ",t1.orderType" +
                ",t1.orderNo" +
                ",t2.cardNo" +
                ",t2.belongArea as belongMember" +
                ",t3.belongArea as belongSeller" +
                ",t4.belongArea as belongFactor" +

                ",t2.belongAreaValue as belongValueMember" +
                ",(select t5.belongAreaValue from Member t5 where t1.sellerId=t5._id) as belongValueFriend" +
                ",t3.belongAreaValue as belongValueSeller" +
                ",t4.areaValue as belongValueFactor" +

                ",t2.realName as nameMember" +//支付钱的会员
                ",(select t5.realName from Member t5 where t1.sellerId=t5._id) as nameMemberAcq" +//被充值的会员
                ",t3.name as nameSeller" +
                ",t4.name as nameFactor" +

                ",t2._id as memberId" +
                ",t3._id as sellerId" +
                ",t4._id as factorId" +
                ",(select t5._id from Member t5 where t1.sellerId=t5._id) as friendId" +
                ",t5.cashCount"+//会员账户余额
                sqlField+
                " from OrderInfo t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join Seller t3 on t1.sellerId=t3._id" +
                " left join Factor t4 on t1.sellerId=t4._id" +
                " left join MemberMoneyLog t5 on t1.orderNo = t5.orderId and t5.tradeType<>7" +
                left+
                where + " order by t1."+queryType+" desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        return page;
    }

    /**
     * 平台管理:清算报表
     */
    @GET
    @Seller
    @Path("/getBill")
    public void getBill() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue") == null ? "" : ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime") == null ? "" : ControllerContext.getPString("_createTime");
        String search = ControllerContext.getPString("_search") == null ? "" : ControllerContext.getPString("_search");
        String orderNo = ControllerContext.getPString("_orderNo") == null ? "" : ControllerContext.getPString("_orderNo");
        String orderType = ControllerContext.getPString("_orderType") == null ? "" : ControllerContext.getPString("_orderType");
        String notOrderType = ControllerContext.getPString("_notOrderType") == null ? "" : ControllerContext.getPString("_notOrderType");
        String userId = ControllerContext.getPString("_userId") == null ? "" : ControllerContext.getPString("_userId");
        String isGetOne = ControllerContext.getPString("_isGetOne") == null ? "" : ControllerContext.getPString("_isGetOne");
        String pid = ControllerContext.getPString("_pid") == null ? "" : ControllerContext.getPString("_pid");
        String orderStatus = ControllerContext.getPString("_orderStatus") == null ? "" : ControllerContext.getPString("_orderStatus");
        String notOrderStatus = ControllerContext.getPString("_notOrderStatus") == null ? "" : ControllerContext.getPString("_notOrderStatus");
        String payType = ControllerContext.getPString("_payType") == null ? "" : ControllerContext.getPString("_payType");
        String queryType = ControllerContext.getPString("_queryType") == null ? "" : ControllerContext.getPString("_queryType");
        long pageNo = ControllerContext.getPLong("pageNo") == null ? 0l : ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        Map<String, Object> v = new HashMap<>();
        v.put("areaValue", areaValue);
        v.put("createTime", createTime);
        v.put("search", search);
        v.put("orderType", orderType);
        v.put("notOrderType", notOrderType);
        v.put("userId", userId);
        v.put("isGetOne", isGetOne);
        v.put("pid", pid);
        v.put("orderStatus", orderStatus);
        v.put("notOrderStatus", notOrderStatus);
        v.put("payType", payType);
        v.put("queryType", queryType);
        v.put("pageNo", pageNo);
        v.put("pageSize", pageSize);
        v.put("orderNo", orderNo);
        toResult(Response.Status.OK.getStatusCode(), getBillPage(v));
    }

//    /**
//     * 平台管理:当前登录代理商旗下的会员 今日/昨日/本月/上月/ 的 消费排行
//     */
//    @POST
//    @Seller
//    @Path("/getMemberRank")
//    public void getMemberRank() throws Exception {
//        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
//        JSONObject other = JSONObject.fromObject(otherDataJson);
//        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
//            throw new UserOperateException(400, "找不到用户");
//        }
//        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
//        String level = cache.getCache("agent_level_cache_" + other.getString("agentId"));
//
//        Map<String, Object> resultMap = new HashMap<>();
//
//        Long indexNum = 0l;
//        Long pageNo = ControllerContext.getPLong("pageNo");
//        Long pageSize = ControllerContext.getPLong("pageSize");
//        if (pageNo != 1) {
//            indexNum = (pageNo - 1) * pageSize;
//        }
//
//        List<Object> params = new ArrayList<>();
//        List<String> returnFields = new ArrayList<>();
//
//        String leftJoin = " left join Member t2 on t1.memberId=t2._id";
//        String where = " where (t1.tradeType=3 or t1.tradeType=4)";//线下交易,线上交易
//        String groupBy = " group by t1.memberId";
//        String orderBy = " order by money desc";
//        String limit = " limit ?,?";
//
//        //如果是管理员,则查所有;若不是,则查代理商旗下的会员
////        if(!"1".equals(level)){
//        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
//        msg = ServiceAccess.callService(msg);
//        String areaValueFactor = msg.getContent().get("areaValue").toString();
//
//        leftJoin += " left join MemberCard t3 on t1.memberId=t3.memberId" +
//                " left join Factor t4 on t3.factorId=t4._id";
//        where += " and t4.areaValue like ?";
//        params.add(areaValueFactor.replace("_", "\\_") + "%");
////        }
//
//        List<String> reCount = new ArrayList<>();
//        reCount.add("totalCount");
//        String hql = "select count(distinct t2._id) as totalCount" +
//                " from MemberMoneyLog t1" +
//                leftJoin +
//                where;
//        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, reCount, params);
//        Long totalNum = (Long) orderList.get(0).get("totalCount");
//        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
//        resultMap.put("pageNo", pageNo);
//        resultMap.put("totalNum", totalNum);
//        resultMap.put("totalPage", totalPage);
//
//        params.add(indexNum);
//        params.add(pageSize);
//
//        returnFields.add("money");
//        returnFields.add("icon");
//        returnFields.add("money");
//        String sql = "select" +
//                " sum(t1.orderCash) as money" +
//                ",t2.icon" +
//                ",t2.name" +
//                " from MemberMoneyLog t1" +
//                leftJoin +
//                where +
//                groupBy +
//                orderBy +
//                limit;
//        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
//        resultMap.put("memberList", re);
//        toResult(Response.Status.OK.getStatusCode(), resultMap);
//    }

    /**
     * 代理商服务站月结算交易额
     */
    @GET
    @Seller
    @Path("/getIncomeTradeSum")
    public void getIncomeTradeSum() throws Exception {
        String areaValue = ControllerContext.getPString("areaValue");
        String time = ControllerContext.getPString("month");
        int year = Integer.valueOf(time.substring(0, 4));
        int month = Integer.valueOf(time.substring(4, 6));
        //设置时间
        Calendar startCal = new GregorianCalendar();
        Calendar endCal = new GregorianCalendar();
        Long startTime = 0l;
        Long endTime = 0l;
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, month - 1);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        endCal.set(Calendar.YEAR, year);
        endCal.set(Calendar.MONTH, month);
        endCal.set(Calendar.DAY_OF_MONTH, 1);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        endCal.add(Calendar.MILLISECOND, -1);
        startTime = startCal.getTimeInMillis();
        endTime = endCal.getTimeInMillis();

        List<String> r = new ArrayList<>();
        List<Object> p = new ArrayList<>();

        String where = " where t1.endTime>=? and t1.endTime<=?" +
                " and t1.orderType in (0,1,7,8,11,13)" +
                " and t1.orderStatus=100";
        p.add(startTime);
        p.add(endTime);

        if(areaValue.equals("_A-000001_")){
            where+=" and (t2.belongAreaValue like ? or t2.belongAreaValue is null or t2.belongAreaValue ='')";
        }else{
            where+=" and t2.belongAreaValue like ?";
        }
        p.add(areaValue + "%");

        String sql = "select" +
                " sum(t1.payMoney) as incomeTradeSum" +
                " from OrderInfo t1" +
                " left join Member t2 on t2._id=t1.memberId" +
                where;
        r.add("incomeTradeSum");

        List<Map<String, Object>> incomeTradeSum = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), incomeTradeSum);
    }


    /**
     * 平台管理:当前登录代理商 今日/昨日/本月/上月/ 的 交易额
     */
    @GET
    @Seller
    @Path("/getCountData")
    public void getCountData() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        String agentId = other.getString("agentId");

        int selectedDate = ControllerContext.getPInteger("selectedDate");
        if (selectedDate < 1 && selectedDate > 4) {
            throw new UserOperateException(400, "请选择日期");
        }

        //返回给客户端
        Map<String, Object> re = new HashMap<>();

        Calendar startCal = new GregorianCalendar();
        Calendar endCal = new GregorianCalendar();
        Long startTime = 0l;
        Long endTime = 0l;
        //昨日
        if (selectedDate == 2) {
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            endCal.setTimeInMillis(startCal.getTimeInMillis() - 1);
            startCal.setTimeInMillis(startCal.getTimeInMillis() - 3600 * 24 * 1000);
        } else if (selectedDate == 3) {
            //本月开始
            startCal.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONDAY), startCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
            //本月结束
            endCal.set(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONDAY), endCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (selectedDate == 4) {
            //上月开始
            startCal.add(Calendar.MONTH, -1);
            startCal.set(Calendar.DAY_OF_MONTH, 1);
            //上月结束
            int month = endCal.get(Calendar.MONTH);
            endCal.set(Calendar.MONTH, month - 1);
            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        //默认今日
        if (selectedDate != 2) {
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
        }
        startTime = startCal.getTimeInMillis();
        endTime = endCal.getTimeInMillis();

        //参数
        List<Object> params = new ArrayList<>();
        //返回值
        List<String> returnFields = new ArrayList<>();
        String sql = "";
        String leftJoin = "";
        String where = "";

        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String level = cache.getCache("agent_level_cache_" + agentId);

        //------------查询激活会员 和 注册会员------------
        if ("1".equals(level)) {//若是管理员:则还要查询 注册的会员
            //注册总数
            returnFields.add("account");
            where += " where t1.canUse=true";
            sql = "select" +
                    " count(t1._id) as account" +
                    " from Member t1" +
                    where;
            List<Map<String, Object>> reTotal = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
            re.put("totalMember", reTotal.get(0).get("account"));
            //某个时间段 注册总数
            sql += " and t1.createTime>=? and t1.createTime<?";
            params.add(startTime);
            params.add(endTime);
            List<Map<String, Object>> reAdd = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            re.put("addMember", reAdd.get(0).get("account"));

            returnFields.clear();
            params.clear();
            where = " where 1=1";
        } else {//若不是管理员:则查询当前登录代理商的下激活的会员
            Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
            msg = ServiceAccess.callService(msg);
            String areaValue = msg.getContent().get("areaValue").toString();
            where = " where t2.areaValue like ?";
            params.add(areaValue + "%");
            leftJoin += " left join Factor t2 on t2._id=t1.factorId";
        }

        //激活总数
        where += " and t1.isActive=true";
        returnFields.add("account");
        sql = "select" +
                " count(t1.memberId) as account" +
                " from MemberCard t1" +
                leftJoin +
                where;
        List<Map<String, Object>> reTotal = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        re.put("totalMemberActive", reTotal.get(0).get("account"));
        //某个时间段 激活总数
        sql += " and t1.createTime>=? and t1.createTime<?";
        params.add(startTime);
        params.add(endTime);
        List<Map<String, Object>> reAdd = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        re.put("addMemberActive", reAdd.get(0).get("account"));

        returnFields.clear();
        params.clear();
        where = "";

        //------------查询会员养老金------------
        //养老金总数
        returnFields.add("account");
        sql = "select" +
                " sum(t1.pensionTrade) as account" +
                " from MemberPensionLog t1";
        reTotal = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
        re.put("totalPension", reTotal.get(0).get("account"));
        //某个时间段 养老金
        sql += " where t1.createTime>=? and t1.createTime<?";
        params.add(startTime);
        params.add(endTime);
        reAdd = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        re.put("addPension", reAdd.get(0).get("account"));

        returnFields.clear();
        params.clear();
        where = "";

        //------------查询代理商收益------------
        //代理商收益总和
        returnFields.add("account");
        sql = "select" +
                " sum(income) as account" +
                " from AgentMoneyAccount";
        reTotal = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
        re.put("totalAgentIncome", reTotal.get(0).get("account"));
        //某个时间段 代理商收益
        returnFields.add("account");
        sql = "select" +
                " sum(orderCash) as account" +
                " from AgentMoneyLog" +

                " where type=3" +//3:提成金额
                " and createTime>=? and createTime<?" +
                " and agentId <> '-1'" +
                " and agentId <> '0'" +
                " and agentId is not null";//排除平台
        params.add(startTime);
        params.add(endTime);
        reAdd = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        re.put("addAgentIncome", reAdd.get(0).get("account"));

        returnFields.clear();
        params.clear();
        where = "";

        //------------查询代理商收益------------
        //服务站收益总和
        returnFields.add("account");
        sql = "select" +
                " sum(income) as account" +
                " from FactorMoneyAccount";
        reTotal = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
        re.put("totalFactorIncome", reTotal.get(0).get("account"));
        //某个时间段 服务站收益
        returnFields.add("account");
        sql = "select" +
                " sum(orderCash) as account" +
                " from FactorMoneyLog" +
                " where type=3" +//3:提成金额
                " and createTime>=? and createTime<?";
        params.add(startTime);
        params.add(endTime);
        reAdd = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        re.put("addFactorIncome", reAdd.get(0).get("account"));

        returnFields.clear();
        params.clear();
        where = "";

        //------------查询商家交易额------------
        //商家总交易额
        returnFields.add("account");
        sql = "select" +
                " sum(income) as account" +
                " from SellerMoneyAccount";
        reTotal = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
        re.put("totalSellerIncome", reTotal.get(0).get("account"));
        //某个时间段 商家收益
        returnFields.add("account");
        sql = "select" +
                " sum(orderCash) as account" +
                " from SellerMoneyLog" +
                " where (tradeType=3 or tradeType=4 or tradeType=5)" +//3:线上消费,4:现金交易,5:会员扫码
                " and createTime>=? and createTime<?";
        params.add(startTime);
        params.add(endTime);
        reAdd = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        re.put("addSellerIncome", reAdd.get(0).get("account"));

        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 平台管理:交易流水总额
     */
    @GET
    @Seller
    @Path("/getTradeSum")
    public void getTradeSum() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue") == null ? "" : ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime") == null ? "" : ControllerContext.getPString("_createTime");
        String search = ControllerContext.getPString("_search") == null ? "" : ControllerContext.getPString("_search");
        String orderNo = ControllerContext.getPString("_orderNo") == null ? "" : ControllerContext.getPString("_orderNo");
        String userId = ControllerContext.getPString("_userId") == null ? "" : ControllerContext.getPString("_userId");
        String notOrderType = ControllerContext.getPString("_notOrderType") == null ? "" : ControllerContext.getPString("_userId");
        String orderType = ControllerContext.getPString("_orderType") == null ? "" : ControllerContext.getPString("_orderType");
        String notOrderStatus = ControllerContext.getPString("_notOrderStatus") == null ? "" : ControllerContext.getPString("_notOrderStatus");
        String queryType = ControllerContext.getPString("_queryType") == null ? "" : ControllerContext.getPString("_queryType");
        String payType = ControllerContext.getPString("_payType") == null ? "" : ControllerContext.getPString("_payType");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }

        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);
        int agentLevel = Integer.valueOf(msg.getContent().get("level").toString());

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1 and orderStatus not in (0,1,9)";

        if (StringUtils.isNotEmpty(userId)) { //用于结算报表的条件
            String userType = userId.substring(0, 1);
            areaValue = areaValue.replaceAll("___like_", "");
            if ("A".equals(userType)) {
                where += " and (t2.belongAreaValue like ? or t3.belongAreaValue like ? or t4.areaValue like ?)" +
                        " and t1.orderType in (0,1,5,6,7,8,11,12,13) and t1.pid!=-1";
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
            } else if ("F".equals(userType)) {
                where += " and (t2.belongAreaValue like ? or t3.belongAreaValue like ? or t4.areaValue like ?)" +
                        " and t1.orderType in (0,1,5,6,7,11,12) and t1.pid!=-1";
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
            } else if ("S".equals(userType)) {
                where += " and t1.orderType in (0,1,2,11,12) and t1.sellerId=? and t1.pid!=-1";
                p.add(userId);
            }
        } else if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            if (areaValue.substring(2, areaValue.length() - 2).split("\\_").length != 1) {
                where += " and (t2.belongAreaValue like ? or t3.belongAreaValue like ? or t4.areaValue like ?)";
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
            }
            if (agentLevel > 1) {
                where += " and t2.belongAreaValue like '_A-000001_A%'";
            }
        }
        //不显示某个状态的订单
        if(StringUtils.isNotEmpty(notOrderStatus)){
            String[] notList=notOrderStatus.split(",");
            String notStr = "";
            for(int i=0,len=notList.length;i<len;i++){
                if(!Pattern.matches("^[0123456789]|(10)|(11)|(12)$",notList[i])){
                    throw new UserOperateException(500,"错误的订单类型");
                }
                notStr+="?,";
                p.add(notList[i]);
            }
            where += " and orderStatus not in ("+notStr.substring(0,notStr.length()-1)+")";
        }

        if (StringUtils.isNotEmpty(orderNo)) {
            where += " and t1.orderNo like ?";
            p.add("%" + orderNo + "%");
        }
        if(StringUtils.isNotEmpty(queryType) && queryType.equals("endTime")){
            where += " and t1.orderStatus=100";
        }else{
            queryType="createTime";
        }
        if (startTime != 0) {
            where += " and t1."+queryType+">?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1."+queryType+"<?";
            p.add(endTime);
        }

        if (StringUtils.isNotEmpty(search)) {
            where += " and (t2.realName like ? or t3.name like ? or t4.name like ?" +
                    " or t2.mobile like ? or t3.phone like ? or t4.mobile like ?" +
                    " or t2.idCard like ? or t3.realCard like ? or t4.realCard like ?" +
                    " or t2.cardNo like ? or t2._id like ? or t3._id like ? or t4._id like ?)";
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
        }
        if (StringUtils.isNotEmpty(orderType)) {
            where += " and t1.orderType=?";
            p.add(orderType);
        }
        if (StringUtils.isNotEmpty(notOrderType)) {
            where += " and t1.orderType<>?";
            p.add(notOrderType);
        }
        if (StringUtils.isNotEmpty(payType)) {
            where += " and t1.payType = ?";
            p.add(payType);
        }
        String hql = "select sum(t1.payMoney) as tradeMoneySum" +
                " from OrderInfo t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join Seller t3 on t1.sellerId=t3._id" +
                " left join Factor t4 on t1.sellerId=t4._id" +
                where;
        r.add("tradeMoneySum");
        List<Map<String, Object>> tradeMoneySum = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        toResult(Response.Status.OK.getStatusCode(), tradeMoneySum);
    }

    /**
     * 平台管理:当前登录代理商 月/周/日 的 交易额
     */
    @GET
    @Seller
    @Path("/getTurnover")
    public void getTurnover() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        String agentId = other.getString("agentId");

        int selectedDate = ControllerContext.getPInteger("selectedDate");
        if (selectedDate < 1 && selectedDate > 3) {
            throw new UserOperateException(400, "请选择月/周/日");
        }

        Calendar startCal = new GregorianCalendar();
        Calendar endCal = new GregorianCalendar();
        Long startTime = 0l;
        Long endTime = 0l;
        //周
        if (selectedDate == 1) {
            //本月开始
            startCal.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONDAY), startCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
            //本月结束
            endCal.set(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONDAY), endCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (selectedDate == 2) {
            //周一
            startCal.setFirstDayOfWeek(Calendar.MONDAY);
            startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            //周末
            endCal.setFirstDayOfWeek(Calendar.MONDAY);
            endCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);

        startTime = startCal.getTimeInMillis();
        endTime = endCal.getTimeInMillis();

        List<Object> params = new ArrayList<>();
        params.add(startTime);
        params.add(endTime);

        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String level = cache.getCache("agent_level_cache_" + agentId);

        String where = " where createTime>=? and createTime<=?";

        if (!"1".equals(level)) {//若不是管理员:则查询当前登录代理商的收入
            where += " and agentId=?";
            params.add(agentId);
        }
        List<String> returnFields = new ArrayList<>();
        returnFields.add("orderCash");
        String sql = "select" +
                " sum(orderCash) as orderCash" +
                " from AgentMoneyLog" + where;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }

    /**
     * 平台管理:总交易额
     */
    @GET
    @Seller
    @Path("/getTurnoverTotal")
    public void getTurnoverTotal() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        String agentId = other.getString("agentId");

        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String level = cache.getCache("agent_level_cache_" + agentId);

        if ("1".equals(level)) {//若是管理员:则查询所有代理商的总收入
            List<String> returnFields = new ArrayList<>();
            returnFields.add("income");
            String sql = "select" +
                    " sum(income) as income" +
                    " from AgentMoneyAccount";
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
            toResult(Response.Status.OK.getStatusCode(), re.get(0));
        } else {//若不是,则查询当前登陆的代理商
            Map<String, Object> params = new HashMap<>();
            params.put("agentId", agentId);
            Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("AgentMoneyAccount", params, null, null);
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /**
     * 平台管理:当前代理商的余额
     */
    @GET
    @Seller
    @Path("/getAgentMoney")
    public void getAgentMoney() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        String agentId = other.getString("agentId");

        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String level = cache.getCache("agent_level_cache_" + agentId);

//        if(!"1".equals(level)){
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", agentId);
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("AgentMoneyAccount", params, null, null);
        toResult(Response.Status.OK.getStatusCode(), re);
//        }else{//若是管理员:则返回-1,页面上则根据此值不显示圆饼图
//            Map<String, Object> re = new HashMap<>();
//            re.put("cashCount", -1);
//            toResult(Response.Status.OK.getStatusCode(), re);
//        }
    }

    /**
     * 平台管理:投保记录统计金额
     */
    @GET
    @Seller
    @Path("/getInsureMoneySum")
    public void getInsureMoneySum() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "无查看权限");
        }
//        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
//        String level = cache.getCache("agent_level_cache_" + other.getString("agentId"));
//        if (!"1".equals(level)) {
//            throw new UserOperateException(400, "无查看权限");
//        }
        List<Object> params = new ArrayList();
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String search = ControllerContext.getPString("_search");
        String notInsure = ControllerContext.getPString("_notInsure");
        String queryType = ControllerContext.getPString("_queryType");
        Long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        long startTime = 0, endTime = 0;
        String whereStr = " where 1=1";
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }
        if (startTime != 0) {
            whereStr += " and t1.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t1.createTime<=?";
            params.add(endTime);
        }

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            int grade = areaValue.substring(2, areaValue.length() - 2).split("\\_").length;
            if(grade!=1){
                whereStr += " and t2.belongAreaValue like ?";
                params.add(areaValue + "%");
            }
        }
        if (StringUtils.isNotEmpty(search)) {
            whereStr += " and (t2.cardNo like ? or t2.realName like ?)";
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }
        if (StringUtils.isNotEmpty(notInsure)
                && StringUtils.isEmpty(queryType)){
            whereStr += " and (t1.insureCountUse>=? or t1.insureCount>=?)";
            params.add(notInsure);
            params.add(notInsure);
        }
        if (StringUtils.isNotEmpty(queryType)
                && StringUtils.isNotEmpty(notInsure)) {
            //已投保
            if (queryType.equals("0")) {
                whereStr += " and t1.insureCountUse>=?";
            } else {//未投保
                whereStr += " and t1.insureCount>=?";
            }
            params.add(notInsure);
        }

        List<String> p = new ArrayList<>();
        p.add("totalNot");
        p.add("totalUse");
        String hql = "select" +
                " sum(t1.insureCount) as totalNot" +
                ",sum(t1.insureCountUse) as totalUse" +
                " from MemberPensionAccount t1" +
                " left join Member t2 on t1.memberId=t2._id"
                +whereStr;
        List<Map<String, Object>> insureMoney = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        toResult(Response.Status.OK.getStatusCode(), insureMoney);
    }


    /**
     * 平台管理:投保记录
     */
    @POST
    @Path("/getInsure")
    public void getInsure() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "无查看权限");
        }

        List<Object> params = new ArrayList();
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String search = ControllerContext.getPString("_search");
        String notInsure = ControllerContext.getPString("_notInsure");
        String queryType = ControllerContext.getPString("_queryType");
        Long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        long startTime = 0, endTime = 0;
        String whereStr = " where 1=1 ";
        String groupBy = " group by t1.memberId";
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }
        if (startTime != 0) {
            whereStr += " and t1.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t1.createTime<=?";
            params.add(endTime);
        }

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            int grade = areaValue.substring(2, areaValue.length() - 2).split("\\_").length;
            if(grade!=1){
                whereStr += " and t2.belongAreaValue like ?";
                params.add("%" + areaValue + "%");
            }
        }
        if (StringUtils.isNotEmpty(search)) {
            whereStr += " and (t2.cardNo like ? or t2.realName like ?)";
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        if (StringUtils.isNotEmpty(notInsure)
                && StringUtils.isEmpty(queryType)){
            whereStr += " and (t1.insureCountUse>=? or t1.insureCount>=?)";
            params.add(notInsure);
            params.add(notInsure);
        }
        if (StringUtils.isNotEmpty(queryType)
                && StringUtils.isNotEmpty(notInsure)) {
            //已投保
            if (queryType.equals("0")) {
                whereStr += " and t1.insureCountUse>=?";
            } else {//未投保
                whereStr += " and t1.insureCount>=?";
            }
            params.add(notInsure);
        }
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t1.memberId) as totalCount" +
                " from MemberPensionAccount t1" +
                " left join Member t2 on t1.memberId=t2._id";
        hql += whereStr +groupBy+ " order by t1.createTime desc";
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = Long.valueOf(cardCount.size());
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        List<String> r = new ArrayList<>();
        String itemSql = "select" +
                " distinct t1.memberId" +
                ",t1.createTime" +
                ",t1._id" +
                ",t1.insureCountUse as money" +
                ",t1.insureCount" +
                ",(case when t1.insureCount<=0 and t1.insureCountUse>0 then true else false end) as insureStatus" +
                ",t2.cardNo" +
                ",t2.idCard" +
                ",t2.realName" +
                ",t2.mobile" +
                ",t2.sex" +
                ",t2.belongArea" +
                ",t2.belongAreaValue as areaValue" +
                ",t4.name as agent4name" +
                " from MemberPensionAccount t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join Factor t3 on t2.belongAreaValue = t3.areaValue" +
                " left join Agent t4 on t3.pid = t4._id";
        itemSql += whereStr + groupBy +
                " order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;

        r.add("createTime");
        r.add("_id");
        r.add("money");
        r.add("insureCount");
        r.add("insureStatus");
        r.add("memberId");
        r.add("cardNo");
        r.add("idCard");
        r.add("realName");
        r.add("mobile");
        r.add("sex");
        r.add("belongArea");
        r.add("areaValue");
        r.add("agent4name");
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(itemSql, r, params);

        page.setItems(li);
        toResult(Response.Status.OK.getStatusCode(), page);
    }


    /**
     * 查询利润分配与养老金上限记录
     */
    @POST
    @Path("/getLogByModify")
    public void getLogByModify() throws Exception {
        String modifyType = ControllerContext.getPString("_modifyType");
        String createTime = ControllerContext.getPString("_createTime");
        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        String where = " where 1=1 ";
        String select = "";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        if (startTime != 0) {
            where += " and createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isEmpty(modifyType)) {
            where += " and modifyType=1";
        } else {
            where += " and modifyType=?";
            p.add(modifyType);
        }
        String hql = "select" +
                " count(_id) as totalCount" +
                " from ProfitsLog" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);
        r.clear();
        if (StringUtils.isEmpty(modifyType) || Pattern.matches("^[05]$",modifyType)) {
            select = " oneAgent" +
                    ",twoAgent" +
                    ",threeAgent" +
                    ",fourAgent" +
                    ",fiveAgent" +
                    ",createTime";
            r.add("oneAgent");
            r.add("twoAgent");
            r.add("threeAgent");
            r.add("fourAgent");
            r.add("fiveAgent");
            r.add("createTime");
        } else {
            select = " pensionLog" +
                    ",createTime" +
                    ",modifyType";
            r.add("pensionLog");
            r.add("createTime");
            r.add("modifyType");
        }
        String sql = "select" +
                select +
                " from ProfitsLog" +
                where + " order by createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }


    /**
     * 平台管理:获取会员余额 养老金 意外险
     */
    @GET
    @Seller
    @Path("/getMemberMoneyPension")
    public void getMemberMoneyPension() throws Exception {
        String memberId = ControllerContext.getPString("memberId");

        if (StringUtils.isEmpty(memberId)) {
            throw new UserOperateException(400, "获取会员资料失败!");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        Map<String, Object> money = MysqlDaoImpl.getInstance().findOne2Map("MemberMoneyAccount", params, null, null);

        Map<String, Object> money2 = MysqlDaoImpl.getInstance().findOne2Map("MemberPensionAccount", params, null, null);
        if (money2 != null && money2.size() > 0) {
            money.put("insureCount", money2.get("insureCount"));
            money.put("insureCountUse", money2.get("insureCountUse"));
        }

        Map<String, Object> money3 = MysqlDaoImpl.getInstance().findOne2Map("MemberAccidentLog", params, null, null);
        if (money3 != null && money3.size() > 0) {
            money.put("accidentCount", money3.get("money"));
        }

        toResult(Response.Status.OK.getStatusCode(), money);
    }

    /**
     * 获取新增会员数量
     */
    @GET
    @Seller
    @Path("/getMemberAddCount")
    public void getMemberAddCount() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String belongArea = ControllerContext.getPString("_belongArea");

        String num = ControllerContext.getPString("_num");
        String numUpDn = ControllerContext.getPString("_numUpDn");
        String numOrder = ControllerContext.getPString("_numOrder");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String name = "";
        String where = " where 1=1 ";
        String groupBy = "";
        String id = "";
        String havingStr = "";
        String orderBy = "";

        if (startTime != 0) {
            where += " and t1.activeTime>=?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.activeTime<=?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");

            name += " t2.name" +
                    ",t3.areaValue";
            id = "t2._id";
            where += " and t2.areaValue like ?";
            p.add(areaValue + "%");
            groupBy += " group by t2._id";
        } else {
            id = "t3._id";
            name += " t3.name" +
                    ",t3.areaValue";
            groupBy += " group by t3._id";
        }
        if (StringUtils.isNotEmpty(belongArea)) {
            areaValue = areaValue.replaceAll("___like_", "");
            int grade = areaValue.substring(1, areaValue.length() - 1).split("_").length;
            if (grade != 5) {
                where += " and t2.belongArea like ?";
            } else {
                where += " and t3.belongArea like ?";
            }
            p.add("%" + belongArea + "%");
        }

        String hql = " select" +
                " count(DISTINCT case when " + id + " is null then 1 else 1 end) as totalCount " +
                " from MemberCard t1" +
                " left join Factor t2 on t2._id=t1.factorId" +
                " left join Agent t3 on t2.pid=t3._id" +
                where + " and t1.isActive=true" + groupBy;
        r.clear();
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = Long.valueOf(cardCount.size());
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        if (StringUtils.isNotEmpty(num)) {
            havingStr += " having count(t1._id)";
            if (StringUtils.isEmpty(numUpDn)) {
                havingStr += ">=?";
            } else {
                havingStr += "<=?";
            }
            p.add(num);
            if (StringUtils.isEmpty(numOrder)) {
                orderBy += " order by count desc";
            } else {
                orderBy += " order by count asc";
            }
        }
        String sql = "select" +
                name +
                ",count(t1._id) as count" +
                ",t3._id as agentId" +
                " from MemberCard t1" +
                " left join Factor t2 on t2._id=t1.factorId" +
                " left join Agent t3 on t2.pid=t3._id" +
                where + " and t1.isActive=true " + groupBy + havingStr + orderBy + " limit " + page.getStartIndex() + "," + pageSize;
        r.clear();
        r.add("name");
        r.add("areaValue");
        r.add("count");
        r.add("agentId");
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        Map<String, Object> v = new HashMap<>();
        for (int i = 0; i < re.size(); i++) {
            v.clear();
            Message msg1 = Message.newReqMessage("1:GET@/account/Agent/getAgentAreaValueById");
            msg1.getContent().put("areaValue", re.get(i).get("areaValue").toString());
            JSONObject con1 = ServiceAccess.callService(msg1).getContent();
            re.get(i).put("agentNameAll", con1.get("agentNameAll").toString());
            Thread.sleep(50);
        }


        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);
        String level = msg.getContent().get("level").toString();
        //如果是平台管理员登陆,则查看归属于平台的会员
        if ("1".equals(level) && page.getPageNo() == 1 && "\\_A-000001\\_".equals(areaValue) &&
                !Pattern.matches("^[" + belongArea + "]$", "平台")) {
            p.clear();
            where = "";
            if (startTime != 0) {
                where += " and t1.activeTime>?";
                p.add(startTime);
            }
            if (endTime != 0) {
                where += " and t1.activeTime<?";
                p.add(endTime);
            }

            sql = "select count(t1._id) as count " +
                    " from MemberCard t1 " +
                    " left join Member t2 on t1.memberId=t2._id" +
                    " where t1.isActive=true and t2.belongAreaValue='_A-000001_'" + where;
            r.clear();
            r.add("count");
            List<Map<String, Object>> reAdmin = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            if (reAdmin != null && reAdmin.size() > 0) {
                reAdmin.get(0).put("agentNameAll", "普惠生活-平台");
                re.add(0, reAdmin.get(0));
                if (totalNum == 0L) {
                    page = new Page(pageNo, pageSize, 1);
                } else {
                    page = new Page(pageNo, pageSize, totalNum + 1);
                }
            }
        }

        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 查询商家/会员 交易排行
     */
    @GET
    @Seller
    @Path("/getSellerTradeRank")
    public void getSellerTradeRank() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String userType = ControllerContext.getPString("_userType");
        String belongArea = ControllerContext.getPString("_belongArea");

        String tradeNum = ControllerContext.getPString("_tradeNum");
        String tradeNumUpDn = ControllerContext.getPString("_tradeNumUpDn");
        String tradeNumOrder = ControllerContext.getPString("_tradeNumOrder");

        String payMoney = ControllerContext.getPString("_payMoney");
        String payMoneyUpDn = ControllerContext.getPString("_payMoneyUpDn");
        String payMoneyOrder = ControllerContext.getPString("_payMoneyOrder");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1 and t2.orderType in (0,1,2,11)";
        String orderBy = " order by 1=1";
        String havingStr = " having 1=1";
        String leftJoin = "";

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            where += " and t1.belongAreaValue like ?";
            p.add(areaValue + "%");
        }
        if (startTime != 0) {
            where += " and t2.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t2.createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(belongArea)) {
            where += " and t1.belongArea like ?";
            p.add("%" + belongArea + "%");
        }

        if (StringUtils.isNotEmpty(tradeNum)) {
            havingStr += " and tradeNum";
            if (StringUtils.isEmpty(tradeNumUpDn)) {
                havingStr += ">=?";
            } else {
                havingStr += "<=?";
            }
            p.add(tradeNum);
            if (StringUtils.isEmpty(tradeNumOrder)) {
                orderBy += " ,tradeNum desc";
            } else {
                orderBy += " ,tradeNum asc";
            }
        }
        if (StringUtils.isNotEmpty(payMoney)) {
            havingStr += " and sum(t2.payMoney)";
            if (StringUtils.isEmpty(payMoneyUpDn)) {
                havingStr += ">=?";
            } else {
                havingStr += "<=?";
            }
            p.add(payMoney);
            if("Member".equals(userType)){
                if (StringUtils.isEmpty(payMoneyOrder)) {
                    orderBy += " ,t5.totalConsume desc";
                } else {
                    orderBy += " ,t5.totalConsume asc";
                }
                if (StringUtils.isEmpty(tradeNum) && StringUtils.isEmpty(payMoney)) {
                    orderBy += " ,t5.totalConsume desc";
                }
            }
        }

        String hql = " select" +
                " count(distinct t1._id) as totalCount " +
                ",sum(t2.payMoney) as payMoneySum" +
                ",count(t2._id) as tradeNum" +
                " from " + userType + " t1" +
                " left join OrderInfo t2 on t2." + userType + "Id=t1._id" +
                " left join Factor t3 on t1.belongAreaValue=t3.areaValue" +
                " left join Agent t4 on t4._id=t3.pid" +
                where + " and (t2.payMoney>0 or t2.payMoney is not null) and t2.orderStatus=100 group by t1._id " + havingStr;
        r.clear();
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = Long.valueOf(cardCount.size());
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();

        String sqlParam = "";
        if ("Seller".equals(userType)) {
            sqlParam = ",t1._id as userNo ,t1.name as userName";
        } else if ("Member".equals(userType)) {
            leftJoin = " left join MemberMoneyAccount t5 on t1._id = t5.memberId";
            sqlParam = ",t1.cardNo as userNo" +
                    ",t1.realName as userName" +
                    ",t5.cashOnlineCount" +
                    ",t5.cashOfflineCount" +
                    ",t5.rechargeCount" +
                    ",t5.cashCount" +
                    ",t5.totalConsume" +
                    ",t5.cashCountUse";

            r.add("cashOnlineCount");
            r.add("cashOfflineCount");
            r.add("rechargeCount");
            r.add("cashCount");
            r.add("totalConsume");
            r.add("cashCountUse");
        }

        r.add("belongArea");
        r.add("areaValue");
        r.add("userId");

        if ("Seller".equals(userType)) {
            r.add("phone");
            sqlParam += ",t1.phone";
        } else {
            r.add("mobile");
            sqlParam += ",t1.mobile";
        }
        r.add("userNo");
        r.add("userName");
        r.add("payMoneySum");
        r.add("tradeNum");
        String sql = "select " +
                "t1.belongArea" +
                ",t1.belongAreaValue as areaValue" +
                ",t1._id as userId" +
                sqlParam +
                ",sum(t2.payMoney) as payMoneySum" +
                ",count(t2._id) as tradeNum" +
                " from " + userType + " t1" +
                " left join OrderInfo t2 on t2." + userType + "Id=t1._id" +
                " left join Factor t3 on t1.belongAreaValue=t3.areaValue" +
                " left join Agent t4 on t4._id=t3.pid" +
                leftJoin+
                where + " and (t2.payMoney>0 or t2.payMoney is not null) and t2.orderStatus=100 group by t1._id " + havingStr +
                orderBy + " limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);

        toResult(Response.Status.OK.getStatusCode(), page);


    }

    /**
     * 月收益结算统计
     *
     * @throws Exception
     */
    @GET
    @Path("/getUserEarningsSum")
    public void getUserEarningsSum() throws Exception {
        int userType = ControllerContext.getPInteger("_userType");
        String areaValue = ControllerContext.getPString("_areaValue");
        String dateStart = ControllerContext.getPString("_dateStart");
        String dateEnd = ControllerContext.getPString("_dateEnd");
        String isTransfer = ControllerContext.getPString("_isTransfer");
        String name = ControllerContext.getPString("_name");
        String cwCheck = ControllerContext.getPString("_cwCheck");
        String userId = ControllerContext.getPString("_userId");

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        int areaValueGrade = 0;
        String where = " where 1=1";
        String leftJoin = "";

        if(!StringUtils.isEmpty(userId)){
            where += " and t1.userId = ?";
            p.add(userId);
        }
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            areaValueGrade = areaValue.split("-").length;
            if (userType != 2) {
                where += " and t2.areaValue like ?";
            } else {
                where += " and t2.belongAreaValue like ?";
            }
            p.add(areaValue + "%");
        }
        if (StringUtils.isNotEmpty(dateStart)) {
            where += " and t1.month>=?";
            p.add(Integer.valueOf(dateStart));
        }
        if (StringUtils.isNotEmpty(dateEnd)) {
            where += " and t1.month<=?";
            p.add(Integer.valueOf(dateEnd));
        }
        if (userType == 0) {
            if (areaValueGrade >= 5) {
                where += " and t1.userType=1";
                leftJoin += " left join Factor t2 on t1.userId=t2._id";
            } else {
                where += " and t1.userType=0";
                leftJoin += " left join Agent t2 on t1.userId=t2._id";
            }
            if (StringUtils.isNotEmpty(cwCheck) && "1".equals(cwCheck)) {
                where += " and t2.level<>1";
            }
        } else if (userType == 1) {
            where += " and t1.userType=" + userType;
            leftJoin += " left join Factor t2 on t1.userId=t2._id";
        } else if (userType == 2) {
            where += " and t1.userType=" + userType;
            leftJoin += " left join Seller t2 on t1.userId=t2._id";
        }
        //不显示禁用的用户
        if (StringUtils.isNotEmpty(leftJoin)) {
            where += " and t2.canUse=true";
        }

        if (StringUtils.isNotEmpty(name)) {
            where += " and t2.name like ?";
            p.add(name + "%");
        }
        if (StringUtils.isNotEmpty(isTransfer)) {
            if (!Boolean.parseBoolean(isTransfer)) {
                where += " and (t1.isTransfer = false or t1.isTransfer is null)";
            } else {
                where += " and t1.isTransfer = true";
            }
        }

        String hql = " select" +
                " sum(t1.incomeAccount) as monthMoneySum " +
                " from AgentAccountMonth t1" +
                leftJoin +
                where;
        r.add("monthMoneySum");
        List<Map<String, Object>> monthMoneySum = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        toResult(Response.Status.OK.getStatusCode(), monthMoneySum);
    }

    /**
     * 月收益结算
     *
     * @throws Exception
     */
    @GET
    @Path("/getUserEarnings")
    public void getUserEarnings() throws Exception {
        int userType = ControllerContext.getPInteger("_userType");
        String areaValue = ControllerContext.getPString("_areaValue");
        String dateStart = ControllerContext.getPString("_dateStart");
        String dateEnd = ControllerContext.getPString("_dateEnd");
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        String isTransfer = ControllerContext.getPString("_isTransfer");
        String name = ControllerContext.getPString("_name");
        String cwCheck = ControllerContext.getPString("_cwCheck");
        String userId = ControllerContext.getPString("_userId");

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        int areaValueGrade = 0;
        String where = " where 1=1";
        String leftJoin = "";

        if(!StringUtils.isEmpty(userId)){
            where += " and t1.userId = ?";
            p.add(userId);
        }
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            areaValueGrade = areaValue.split("-").length;
            if (userType != 2) {
                where += " and t2.areaValue like ?";
            } else {
                where += " and t2.belongAreaValue like ?";
            }
            p.add(areaValue + "%");
        }
        if (StringUtils.isNotEmpty(dateStart)) {
            where += " and t1.month>=?";
            p.add(Integer.valueOf(dateStart));
        }
        if (StringUtils.isNotEmpty(dateEnd)) {
            where += " and t1.month<=?";
            p.add(Integer.valueOf(dateEnd));
        }
        String sqlType = "";
        if (userType == 0) {
            if (areaValueGrade >= 5) {
                where += " and t1.userType=1";
                leftJoin += " left join Factor t2 on t1.userId=t2._id";
                sqlType += ",t2.mobile";
            } else {
                where += " and t1.userType=0";
                leftJoin += " left join Agent t2 on t1.userId=t2._id";
                sqlType += ",t2.phone as mobile";
            }
            if (StringUtils.isNotEmpty(cwCheck) && "1".equals(cwCheck)) {
                where += " and t2.level<>1";
            }
        } else if (userType == 1) {
            where += " and t1.userType=" + userType;
            leftJoin += " left join Factor t2 on t1.userId=t2._id";
            sqlType += ",t2.mobile";
        } else if (userType == 2) {
            where += " and t1.userType=" + userType;
            leftJoin += " left join Seller t2 on t1.userId=t2._id";
            sqlType += ",t2.phone as mobile";
        }
        //不显示禁用的用户
        if (StringUtils.isNotEmpty(leftJoin)) {
            where += " and t2.canUse=true";
        }

        if (StringUtils.isNotEmpty(name)) {
            where += " and t2.name like ?";
            p.add(name + "%");
        }
        if (StringUtils.isNotEmpty(isTransfer)) {
            if (!Boolean.parseBoolean(isTransfer)) {
                where += " and (t1.isTransfer = false or t1.isTransfer is null)";
            } else {
                where += " and t1.isTransfer = true";
            }
        }

        String hql = " select" +
                " count(t1._id) as totalCount " +
                " from AgentAccountMonth t1" +
                leftJoin +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();

        if (userType != 2) {
            sqlType += ",t2.areaValue";
            r.add("areaValue");
        } else {
            sqlType += ",t2.belongAreaValue as areaValue";
            r.add("areaValue");
        }
        if (userType != 1) {

        } else {
            sqlType += ",t2.mobile";
            r.add("mobile");
        }
        r.add("mobile");
        r.add("belongArea");
        r.add("name");
        r.add("_id");
        r.add("userId");
        r.add("orderCash");
        r.add("incomeAccount");
        r.add("orderCount");
        r.add("isTransfer");
        r.add("bankId");
        r.add("bankName");
        r.add("bankUser");
        r.add("bankType");
        r.add("bankTypeValue");
        r.add("bankProvince");
        r.add("bankProvinceValue");
        r.add("bankCity");
        r.add("bankCityValue");
        r.add("month");
        r.add("transferTime");
        r.add("isSuccess");
        r.add("orderNo");
        String sql = "select" +
                " t2.belongArea" +
                sqlType +
                ",t2.name" +
                ",t1._id" +
                ",t1.userId" +
                ",t1.orderCash" +
                ",t1.incomeAccount" +
                ",t1.orderCount" +
                ",t1.isTransfer" +
                ",t1.bankId" +
                ",t1.bankName" +
                ",t1.bankUser" +
                ",t1.bankType"+
                ",t1.bankTypeValue"+
                ",t1.bankProvince"+
                ",t1.bankProvinceValue"+
                ",t1.bankCity"+
                ",t1.bankCityValue"+
                ",t1.month" +
                ",t1.transferTime" +
                ",t1.isSuccess" +
                ",t1.orderNo" +
                " from AgentAccountMonth t1" +
                leftJoin +
                where + " order by t1.month desc,t1.incomeAccount desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 手动调用生成月结算的方法
     */
    @GET
    @Path("/createAgentAccountMonthForWeb")
    public void createAgentAccountMonthForWeb() throws Exception {
        createAgentAccountMonth();
    }

    /**
     * 代理商>商户>服务站上月收入结算
     */
    public void createAgentAccountMonth() throws Exception {
        Calendar startCal = new GregorianCalendar();
        Calendar endCal = new GregorianCalendar();
        long startTime = 0l;
        long endTime = 0l;
        //上月开始
        startCal.add(Calendar.MONTH, -1);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        //上月结束
        int month = endCal.get(Calendar.MONTH);
        endCal.set(Calendar.MONTH, month - 1);
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);

        startTime = startCal.getTimeInMillis();
        endTime = endCal.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String startSdf = sdf.format(startTime);

        //查询上月是否已经有数据
        Map<String, Object> oldM = new HashMap<>();
        oldM.put("month", startSdf);
        Map<String, Object> oldL = MysqlDaoImpl.getInstance().findOne2Map("AgentAccountMonth", oldM, new String[]{"_id"}, Dao.FieldStrategy.Include);
        if (oldL != null && oldL.size() > 0) {
            throw new UserOperateException(400, "已存在上个月报表,请勿重复操作");
        }

        //查询每个代理商上月盈利和交易笔数
        String sqlA = "select" +
                " t1.agentId" +
                ",sum(case when type=100 then 0 else 1 end) as orderCount" +
                ",sum(case when type=100 then -orderCash else orderCash end) as tradeMoney" +
                ",t2.bankId" +
                ",t2.bankUser" +
                ",t2.bankName" +
                ",t2.bankType"+
                ",t2.bankTypeValue"+
                ",t2.bankProvince"+
                ",t2.bankProvinceValue"+
                ",t2.bankCity"+
                ",t2.bankCityValue"+
                ",t2.name" +
                " from AgentMoneyLog t1" +
                " left join Agent t2 on t1.agentId= t2._id" +
                " where t1.createTime>?" +
                " and t1.createTime<?" +
                " and t1.type in (3,4,5,100)" +
                " group by t1.agentId";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(startTime);
        p.add(endTime);
        r.add("agentId");
        r.add("orderCount");
        r.add("tradeMoney");
        r.add("bankId");
        r.add("bankUser");
        r.add("bankName");
        r.add("bankType");
        r.add("bankTypeValue");
        r.add("bankProvince");
        r.add("bankProvinceValue");
        r.add("bankCity");
        r.add("bankCityValue");
        r.add("name");
        List<Map<String, Object>> reA = MysqlDaoImpl.getInstance().queryBySql(sqlA, r, p);

        //将查询出来的数据放入月收入表中
        Map<String, Object> v = new HashMap<>();
        for (int i = 0; i < reA.size(); i++) {
            if (reA.get(i).get("agentId") == null || StringUtils.isEmpty(reA.get(i).get("agentId").toString())) {
                continue;
            }
            v.put("_id", UUID.randomUUID().toString());
            v.put("userId", reA.get(i).get("agentId"));
            v.put("userType", 0);
            v.put("bankId", reA.get(i).get("bankId"));
            v.put("bankUser", reA.get(i).get("bankUser"));
            v.put("bankName", reA.get(i).get("bankName"));
            v.put("bankType", reA.get(i).get("bankType"));
            v.put("bankTypeValue", reA.get(i).get("bankTypeValue"));
            v.put("bankProvince", reA.get(i).get("bankProvince"));
            v.put("bankProvinceValue", reA.get(i).get("bankProvinceValue"));
            v.put("bankCity", reA.get(i).get("bankCity"));
            v.put("bankCityValue", reA.get(i).get("bankCityValue"));
            v.put("userName", reA.get(i).get("name"));
            v.put("incomeAccount", BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(reA.get(i).get("tradeMoney").toString())));
            v.put("orderCount", reA.get(i).get("orderCount"));
            v.put("month", Integer.valueOf(startSdf));
            v.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("AgentAccountMonth", v);
            v.clear();
        }
        //查询服务站的盈利
        String sqlF = "select" +
                " factorId" +
                ",sum(case when type=100 then 0 else 1 end) as orderCount" +
                ",sum(case when type=100 then -orderCash else orderCash end) as tradeMoney" +
                " from FactorMoneyLog" +
                " where createTime>? and createTime<?" +
                " and type in (3,4,5,100) group by factorId";
        r.clear();
        r.add("factorId");
        r.add("orderCount");
        r.add("tradeMoney");
        List<Map<String, Object>> reF = MysqlDaoImpl.getInstance().queryBySql(sqlF, r, p);

        for (int i = 0; i < reF.size(); i++) {
            if (reF.get(i).get("factorId") == null || StringUtils.isEmpty(reF.get(i).get("factorId").toString())) {
                continue;
            }
            v.put("_id", UUID.randomUUID().toString());
            v.put("userId", reF.get(i).get("factorId"));
            v.put("userType", 1);
            v.put("incomeAccount", BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(reF.get(i).get("tradeMoney").toString())));
            v.put("orderCount", reF.get(i).get("orderCount"));
            v.put("month", Integer.valueOf(startSdf));
            v.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("AgentAccountMonth", v);
            v.clear();
        }
        //查询店铺上月收益
        String sqlS = "select" +
                " sellerId" +
                ",count(DISTINCT orderId) as orderCount" +
                ",sum(case when tradeType=100 then -orderCash else orderCash end) as tradeMoney" +
                ",sum(case when tradeType=100 then -incomeOne else incomeOne end) as incomeAccount" +
                " from SellerMoneyLog" +
                " where createTime>? and createTime<? and tradeType in (3,4,5,6,100) group by sellerId";
        r.clear();
        r.add("sellerId");
        r.add("orderCount");
        r.add("tradeMoney");
        r.add("incomeAccount");
        List<Map<String, Object>> reS = MysqlDaoImpl.getInstance().queryBySql(sqlS, r, p);

        for (int i = 0; i < reS.size(); i++) {
            if (reS.get(i).get("sellerId") == null || StringUtils.isEmpty(reS.get(i).get("sellerId").toString())) {
                continue;
            }
            v.put("_id", UUID.randomUUID().toString());
            v.put("userId", reS.get(i).get("sellerId"));
            v.put("userType", 2);
            v.put("orderCash", BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(reS.get(i).get("tradeMoney").toString())));
            if (reS.get(i).get("incomeAccount") != null && StringUtils.isNotEmpty(reS.get(i).get("incomeAccount").toString())) {
                v.put("incomeAccount", BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(reS.get(i).get("incomeAccount").toString())));
            }
            v.put("orderCount", reS.get(i).get("orderCount"));
            v.put("month", Integer.valueOf(startSdf));
            v.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("AgentAccountMonth", v);
            v.clear();
        }
    }

    /**
     * 平台管理:意外险
     */
    @GET
    @Path("/getAccidentLog")
    public void getAccidentLog() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String search = ControllerContext.getPString("_search");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1";

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            where += " and t2.belongAreaValue like ?";
            p.add(areaValue + "%");
        }
        if (startTime != 0) {
            where += " and t1.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(search)) {
            where += " and (t2.cardNo like ? or t2.realName like ?)";
            p.add("%" + search + "%");
            p.add("%" + search + "%");
        }
        String hql = "select count(t1._id) as totalCount" +
                " from MemberAccidentLog t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();
        r.add("_id");
        r.add("createTime");
        r.add("year");
        r.add("area");
        r.add("company");
        r.add("insureNO");
        r.add("money");
        r.add("memberId");
        r.add("cardNo");
        r.add("realName");
        r.add("areaValue");
        r.add("mobile");
        r.add("idCard");
        r.add("sex");
        r.add("agent4name");
        String sql = "select" +
                " t1._id" +
                ",t1.createTime" +
                ",t1.year" +
                ",t1.area" +
                ",t1.company" +
                ",t1.insureNO" +
                ",t1.money" +
                ",t1.memberId" +
                ",t2.cardNo" +
                ",t2.realName" +
                ",t2.belongAreaValue as areaValue" +
                ",t2.mobile" +
                ",t2.idCard" +
                ",t2.sex" +
                ",t4.name as agent4name" +
                " from MemberAccidentLog t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join Factor t3 on t2.belongAreaValue = t3.areaValue" +
                " left join Agent t4 on t3.pid = t4._id" +
                where + " order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);

        toResult(Response.Status.OK.getStatusCode(), page);
    }


    /**
     * 平台管理:创建意外险
     */
    @GET
    @Path("/createAccidentLog")
    public void createAccidentLog() throws Exception {
        JSONObject json = ControllerContext.getContext().getReq().getContent();
        Map<String, Object> log = new HashMap<>();
        Iterator it = json.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = json.get(key);
            log.put(key, value);
        }
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberAccidentLog", (Map<String, Object>) log.get("info"));
    }

    /**
     * 平台管理:补全保单号
     */
    @POST
    @Path("/updateAccidentLog")
    public void updateAccidentLog() throws Exception {
        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0 || !"1".equals(agentInfo.get("level"))) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        String insureNO = ControllerContext.getPString("insureNO");
        String company = ControllerContext.getPString("company");
        String _id = ControllerContext.getPString("_id");
        String table = ControllerContext.getPString("table");

        if (StringUtils.isEmpty(_id) || StringUtils.isEmpty(table)) {
            throw new UserOperateException(400, "获取保单失败");
        } else {
            Map<String, Object> temp = MysqlDaoImpl.getInstance().findById2Map(table, _id, null, null);
            if (temp == null || temp.size() == 0 || temp.get("_id") == null) {
                throw new UserOperateException(400, "获取保单失败");
            }
        }
        if (StringUtils.isEmpty(insureNO)) {
            throw new UserOperateException(400, "请填写保单号");
        }
        if (StringUtils.isEmpty(company)) {
            throw new UserOperateException(400, "请填写投保公司");
        }

        Map<String, Object> accidentLog = new HashMap<>();
        accidentLog.put("_id", _id);
        accidentLog.put("insureNO", insureNO);
        accidentLog.put("MemberPensionLog".equals(table) ? "insureCompany" : "company", company);
        MysqlDaoImpl.getInstance().saveOrUpdate(table, accidentLog);
    }

    /**
     * 平台管理:手动投保
     * （目前只是变动金额）
     */
    @POST
    @Path("/manualInsure")
    public void manualInsure() throws Exception {
        checkAdmin();
        String idStr = ControllerContext.getPString("_id");//MemberPensionAccount的ID
        if(StringUtils.isEmpty(idStr)){
            throw new UserOperateException(500,"获取数据失败");
        }

        List<Object> params = new ArrayList<>();
        String[] idList = idStr.split(";");
        StringBuilder inStr = new StringBuilder();
        for(String str:idList){
            inStr.append(",?");
            params.add(str);
        }
        idStr = inStr.substring(1);

        // 更新账户
        String sql = "update MemberPensionAccount" +
                " set insureCountUse = (insureCount+insureCountUse),insureCount=0,updateTime=" +System.currentTimeMillis()+
                " where _id in ("+idStr+")";
        MysqlDaoImpl.getInstance().exeSql(sql,params,"MemberPensionAccount");
    }

    /**
     * 获取充值列表
     */
    @GET
    @Member
    @Path("/getDepositRecord")
    public void getDepositRecord() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }

        if (StringUtils.isEmpty(memberId)) {
            throw new UserOperateException(400, "请登录!");
        }

        List<Object> params = new ArrayList<>();
        params.add(memberId);

        List<String> returnFields = new ArrayList<>();
        returnFields.add("totalCount");

        String sql = "select count(t1._id) as totalCount" +
                " from MemberMoneyLog t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " where payId=?" +
                " and (tradeType=2 or (tradeType=1 and payId!=memberId))";
        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);

        sql = "select" +
                " t1.payType" +
                ",t1.createTime" +
                ",t1.tradeType" +
                ",t1.memberId" +
                ",t1.orderId" +
                ",t1._id" +
                ",t1.payId" +
                ",t1.orderCash" +
                ",t2.mobile" +
                ",t2.realName" +
                ",t2.icon" +
                " from MemberMoneyLog t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " where payId=?" +
                " and (tradeType=2 or (tradeType=1 and payId!=memberId))" +
                " order by t1.createTime desc limit " + indexNum + "," + pageSize;
        returnFields.clear();
        returnFields.add("payType");
        returnFields.add("tradeType");
        returnFields.add("createTime");
        returnFields.add("memberId");
        returnFields.add("orderId");
        returnFields.add("_id");
        returnFields.add("payId");
        returnFields.add("orderCash");
        returnFields.add("mobile");
        returnFields.add("realName");
        returnFields.add("icon");

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("orderList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 获取会员卡激活历史
     *
     * @throws Exception
     */
    @GET
    @Path("/getCardMoneyLog")
    public void getCardMoneyLog() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue") == null ? "" : ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime") == null ? "" : ControllerContext.getPString("_createTime");
        String search = ControllerContext.getPString("_search") == null ? "" : ControllerContext.getPString("_search");
        long pageNo = ControllerContext.getPLong("pageNo") == null ? 0l : ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-09
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String where = " where 1=1 and t4.orderType in (7,8,13) and t4.orderStatus=100";

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            //若是管理员查看所有,则去掉管理员归属,因为会员自己激活的是没有服务站的
            if (areaValue.substring(2, areaValue.length() - 2).split("\\_").length != 1) {
                where += " and t2.areaValue like ?";
                p.add(areaValue + "%");
            }
        }
        if (StringUtils.isNotEmpty(search)) {
            where += " and (t3.cardNo like ? or t3.realName like ? or t3.mobile like ?)";
            p.add(search + "%");
            p.add("%" + search + "%");
            p.add("%" + search + "%");
        }
        if (startTime != 0) {
            where += " and t1.activeTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.activeTime<?";
            p.add(endTime);
        }
        r.add("totalCount");
        String hql = "select" +
                " count(t1._id) as totalCount" +
                " from MemberCard t1" +
                " left join Factor t2 on t1.factorId = t2._id" +
                " left join Member t3 on t1.memberId = t3._id" +
                " left join OrderInfo t4 on t1.memberId= t4.memberId" +
                where;
        List<Map<String, Object>> agentCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = 0L;
        if (agentCount.size() != 0) {
            totalNum = Long.valueOf(agentCount.get(0).get("totalCount").toString());
        }
        Page page = new Page(pageNo, pageSize, totalNum);
        r.clear();
        r.add("_id");
        r.add("memberCardId");
        r.add("memberName");
        r.add("factorName");
        r.add("activeTime");
        r.add("memberId");
        r.add("factorId");
        r.add("belongValueFactor");
        r.add("orderNo");
        r.add("orderType");
        String sql = "select" +
                " t1._id" +
                ",t1.memberCardId" +
                ",t3.realName as memberName" +
                ",t3.belongArea as factorName" +
                ",t1.activeTime" +
                ",t3._id as memberId" +
                ",t2._id as factorId" +
                ",t2.areaValue as belongValueFactor" +
                ",t4.orderNo" +
                ",t4.orderType" +
                " from MemberCard t1" +
                " left join Factor t2 on t1.factorId=t2._id" +
                " left join Member t3 on t1.memberId = t3._id" +
                " left join OrderInfo t4 on t1.memberId= t4.memberId" +
                where + " group by t1._id order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }


    /**
     * 商家/服务站/会员 创建提现申请
     *
     * @throws Exception
     */
    @POST
    @Path("/createWithdrawLog")
    public void createWithdrawLog() throws Exception {
        String userType = ControllerContext.getPString("userType");
        double withdrawMoney = ControllerContext.getPDouble("withdrawMoney");
        String payPwd = ControllerContext.getPString("payPwd");

        String userId = "";
        String accountTable = "";
        String showInfoUrl;
        String orderType;
        String payPwdEntity = "cashPassword";
        String withDrawType ="";

        if ("Seller".equals(userType)) {
            userId = ControllerContext.getContext().getCurrentSellerId();
            accountTable = "SellerMoneyAccount";
            showInfoUrl = "/account/" + userType + "/show";
            orderType = "9";
            withDrawType ="Seller";
        } else if ("Factor".equals(userType)) {
            String otherDataJson = ControllerContext.getContext().getOtherDataJson();
            JSONObject other = JSONObject.fromObject(otherDataJson);
            if (StringUtils.mapValueIsEmpty(other, "factorId")) {
                throw new UserOperateException(400, "获取服务站数据失败,请重新登录");
            }
            userId = (String) other.get("factorId");
            accountTable = "FactorMoneyAccount";
            showInfoUrl = "/account/" + userType + "/show";
            orderType = "10";
            withDrawType ="Factor";
        } else if ("Member".equals(userType)) {
            userId = ControllerContext.getContext().getCurrentUserId();
            accountTable = "MemberMoneyAccount";
            showInfoUrl = "/crm/Member/show";
            orderType = "14";
            payPwdEntity = "payPwd";
            withDrawType ="Member";
        } else {
            throw new UserOperateException(400, "获取用户信息失败");
        }

        //获取登录用户信息
        String cashPassword = MessageDigestUtils.digest(payPwd);
        Message msg = Message.newReqMessage("1:GET@"+showInfoUrl);
        msg.getContent().put("_id", userId);
        JSONObject userInfo = ServiceAccess.callService(msg).getContent();

        if (userInfo == null || userInfo.size() == 0 || !cashPassword.equals(userInfo.get(payPwdEntity))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码错误!");
        }
        //查询用户信息,获取银行卡信息
        if (StringUtils.mapValueIsEmpty(userInfo,"bankId")) {
            throw new UserOperateException(400, "获取用户银行卡账户失败");
        }
        if (ControllerContext.getPString("withdrawMoney").length() > 7) {
            throw new UserOperateException(400, "提现金额过大");
        }
        if (!Pattern.matches("^\\d+(?:\\.\\d{1,2})?$", ControllerContext.getPString("withdrawMoney"))) {
            throw new UserOperateException(400, "提现金额不能超过两位小数");
        }

        double minWithdrawalMoney = ParameterAction.getValueOne("minWithdrawalMoney" + withDrawType);
        double poundageRatio = ParameterAction.getValueOne("poundageRatio" + withDrawType);
        if (withdrawMoney < minWithdrawalMoney) {
            throw new UserOperateException(400, "提现金额不能低于" + minWithdrawalMoney + "元");
        }
        //手续费百分比
        double feeScale = BigDecimalUtil.divide(poundageRatio, 100);
        double fee = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.multiply(withdrawMoney, feeScale));//手续费
        double moneyFinal = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(withdrawMoney, fee)); //手续费+提现金额

        //查询用户余额
        Map<String, Object> params = new HashMap<>();
        params.put(userType + "Id", userId);
        Map<String, Object> userAccount = MysqlDaoImpl.getInstance().findOne2Map(accountTable, params, null, null);
        if (userAccount == null || userAccount.size() == 0 || userAccount.get("cashCount") == null ||
                Double.valueOf(userAccount.get("cashCount").toString()) < moneyFinal) {
            throw new UserOperateException(400, ("Member".equals(userType)?"积分":"余额")+"不足,无法提现(含手续费)");
        }

        double cashCount = Double.valueOf(userAccount.get("cashCount").toString());//余额
        userAccount.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(cashCount, -moneyFinal)));//更新余额

        // 会员不仅要扣除余额，还要从可提现金额里扣除，且不能超过可提现金额(可提现金额暂时只来源团队收益）
        if("Member".equals(userType)){
            if(StringUtils.mapValueIsEmpty(userAccount,"canWithdrawMoney")
                    || Double.valueOf(userAccount.get("canWithdrawMoney").toString())<moneyFinal){
                throw new UserOperateException(400, "可提现"+("Member".equals(userType)?"积分":"余额")+"不足(含手续费),无法提现");
            }

            double canWithdrawMoney = Double.valueOf(userAccount.get("canWithdrawMoney").toString());//可提现金额
            userAccount.put("canWithdrawMoney", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(canWithdrawMoney, -moneyFinal)));//更新可提现金额
        }

        MysqlDaoImpl.getInstance().saveOrUpdate(accountTable, userAccount);

        //生成 预订订单
        Map<String, Object> order = new HashMap<>();
        if("Member".equals(userType)){
            if(!StringUtils.mapValueIsEmpty(userInfo,"realName")){
                userInfo.put("name",userInfo.get("realName"));
            }else{
                userInfo.put("name",userInfo.get("mobile"));
            }
            order.put("memberId", userId);
        }else{
            order.put("sellerId", userId);
        }

        order.put("_id", ZQUidUtils.genUUID());
        order.put("orderNo", ZQUidUtils.generateOrderNo());
        order.put("totalPrice", withdrawMoney);
        order.put("payMoney", 0.0);

        order.put("orderStatus", OrderInfoAction.ORDER_TYPE_BOOKING);//已预订/未付款
        order.put("createTime", System.currentTimeMillis());
        order.put("bookingTime", System.currentTimeMillis());
        order.put("orderType", orderType);//9:商家提现,10:服务站提现,14:会员提现
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, order);

        //生成 申请记录
        Map<String, Object> withdrawLog = new HashMap<>();
        withdrawLog.put("_id", UUID.randomUUID().toString());
        withdrawLog.put("orderNo", order.get("orderNo"));
        withdrawLog.put("userId", userId);
        withdrawLog.put("userType", userType);
        withdrawLog.put("cashCount", userAccount.get("cashCount"));
        withdrawLog.put("fee", fee); //默认0.1%手续费
        withdrawLog.put("withdrawMoney", withdrawMoney);
        withdrawLog.put("status", 0);  //0.待提现;1.提现中;2.已提现;
        withdrawLog.put("createTime", System.currentTimeMillis());
        withdrawLog.put("bankId", userInfo.get("bankId"));
        withdrawLog.put("bankUser", userInfo.get("bankUser"));
        withdrawLog.put("bankName", userInfo.get("bankName"));
        withdrawLog.put("bankUserPhone", userInfo.get("bankUserPhone"));
        withdrawLog.put("bankUserCardId", userInfo.get("bankUserCardId"));
        withdrawLog.put("userName", userInfo.get("name"));
        withdrawLog.put("belongArea", userInfo.get("belongArea"));
        withdrawLog.put("belongAreaValue", "Factor".equals(userType) ? userInfo.get("areaValue") : userInfo.get("belongAreaValue"));
        MysqlDaoImpl.getInstance().saveOrUpdate("WithdrawLog", withdrawLog);
    }

    /**
     * 平台:获取提现申请
     *
     * @throws Exception
     */
    @GET
    @Path("/getWithdrawPend")
    public void getWithdrawPend() throws Exception {
        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + agentInfo.get("_id"));
        if(!"1".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }

        String userName = ControllerContext.getPString("_userName");
        String userType = ControllerContext.getPString("_userType");
        String createTime = ControllerContext.getPString("_createTime");
        String belongArea = ControllerContext.getPString("_belongArea");
        String belongAreaValue = ControllerContext.getPString("_areaValue");
        String status = ControllerContext.getPString("_status");

        String num = ControllerContext.getPString("_num");
        String numUpDn = ControllerContext.getPString("_numUpDn");
        String numOrder = ControllerContext.getPString("_numOrder");

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();

        String order = " order by createTime desc";
        String where = " where 1=1 and `status`=?";
        params.add(status);

        if (StringUtils.isNotEmpty(userName)) {
            where += " and userName=?";
            params.add(userName);
        }
        if (StringUtils.isNotEmpty(userType)) {
            where += " and userType=?";
            params.add(userType);
        }
        if (StringUtils.isNotEmpty(belongArea)) {
            where += " and belongArea like ?";
            params.add("%" + belongArea + "%");
        }
        if (StringUtils.isNotEmpty(belongAreaValue)) {
            belongAreaValue = belongAreaValue.replaceAll("___like_", "");
            where += " and belongAreaValue like ?";
            params.add(belongAreaValue + "%");
        }
        if (StringUtils.isNotEmpty(createTime)) {
            long startTime = 0, endTime = 0;
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
            if (startTime != 0) {
                where += " and createTime>=?";
                params.add(startTime);
            }
            if (endTime != 0) {
                where += " and createTime<=?";
                params.add(endTime);
            }
        }
        if (StringUtils.isNotEmpty(num)) {
            where += " and withdrawMoney";
            if (StringUtils.isEmpty(numUpDn)) {
                where += ">?";
            } else {
                where += "<?";
            }
            params.add(num);
            if (StringUtils.isEmpty(numOrder)) {
                order += " ,withdrawMoney desc";
            } else {
                where += " ,withdrawMoney asc";
            }
        }

        String sql = " select" +
                " count(_id) as totalCount" +
                " from WithdrawLog" +
                where;
        returnFields.add("totalCount");

        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        returnFields.clear();
        returnFields.add("_id");
        returnFields.add("bankId");
        returnFields.add("bankName");
        returnFields.add("bankUser");
        returnFields.add("bankUserCardId");
        returnFields.add("bankUserPhone");
        returnFields.add("belongArea");
        returnFields.add("belongAreaValue");
        returnFields.add("createTime");
        returnFields.add("fee");
        returnFields.add("status");
        returnFields.add("userId");
        returnFields.add("userName");
        returnFields.add("userType");
        returnFields.add("withdrawMoney");
        returnFields.add("voucher");

        sql = " select" +
                " _id" +
                ",bankId" +
                ",bankName" +
                ",bankUser" +
                ",bankUserCardId" +
                ",bankUserPhone" +
                ",belongArea" +
                ",belongAreaValue" +
                ",createTime" +
                ",fee" +
                ",status" +
                ",userId" +
                ",userName" +
                ",userType" +
                ",withdrawMoney" +
                ",voucher" +
                " from WithdrawLog" +
                where + order;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 平台:受理 提现申请
     *
     * @throws Exception
     */
    @GET
    @Path("/updateWithdraw")
    public void updateWithdraw() throws Exception {
        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0 || !"1".equals(agentInfo.get("level"))) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + agentInfo.get("_id"));
        if(!"1".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }
        String voucher = ControllerContext.getPString("voucher");
        String withdrawId = ControllerContext.getPString("withdrawId");
        String modifyStatus = ControllerContext.getPString("modifyStatus");
        if (StringUtils.isEmpty(withdrawId) || StringUtils.isEmpty(modifyStatus)) {
            throw new UserOperateException(400, "获取申请数据失败");
        }
        if(StringUtils.isEmpty(voucher) || voucher.length()>64){
            throw new UserOperateException(400, "请输入转账银行提供的凭证号码或唯一识别码（64位字符长度以内）");
        }
        if ("0".equals(modifyStatus)) {
            throw new UserOperateException(400, "请选择是否受理");
        }

        Map<String, Object> withdraw = MysqlDaoImpl.getInstance().findById2Map("WithdrawLog", withdrawId, null, null);
        if (withdraw == null || withdraw.size() == 0) {
            throw new UserOperateException(400, "获取申请数据失败");
        }
        if (!"0".equals(withdraw.get("status").toString())) {
            throw new UserOperateException(400, "该申请已被受理,请不要重复提交");
        }

        String userId = withdraw.get("userId").toString();
        String userType = withdraw.get("userType").toString();
        String accountTable = "";

        if ("Seller".equals(userType)) {
            accountTable = "SellerMoneyLog";
        } else if ("Factor".equals(userType)) {
            accountTable = "FactorMoneyLog";
        } else if ("Member".equals(userType)) {
            accountTable = "MemberMoneyLog";
        } else {
            throw new UserOperateException(400, "获取申请数据失败");
        }

        //生成 现金明细表
        Map<String, Object> accountLog = new HashMap<>();
        accountLog.put("_id", UUID.randomUUID().toString());
        accountLog.put("orderId", withdraw.get("orderNo"));
        accountLog.put(userType.toLowerCase() + "Id", userId);
        accountLog.put("orderCash", withdraw.get("withdrawMoney"));
        accountLog.put("createTime", System.currentTimeMillis());
        accountLog.put("Factor".equals(userType) ? "type" : "tradeType", "Factor".equals(userType) ? 2 : 1);
        MysqlDaoImpl.getInstance().saveOrUpdate(accountTable, accountLog);

        //更新 订单表
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", withdraw.get("orderNo"));
        Map<String, Object> order = MysqlDaoImpl.getInstance().findOne2Map("OrderInfo", params, null, null);
        if (order == null || order.size() == 0) {
            throw new UserOperateException(400, "获取申请数据失败");
        }
        order.put("orderStatus", OrderInfoAction.ORDER_TYPE_END);
        order.put("payType", OrderInfoAction.PAY_TYPE_BANK);//银行转账
        order.put("payMoney", withdraw.get("withdrawMoney"));
        order.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderInfo", order);

        //更新 提现表
        withdraw.put("status", 2);
        withdraw.put("voucher", voucher);
        withdraw.put("withdrawTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("WithdrawLog", withdraw);
    }

    /**
     * 获取线上商品订单:总订单下所有子订单的详情
     *
     * @throws Exception
     */
    @GET
    @Path("/getChildOrder")
    public void getChildOrder() throws Exception {
        String pid = ControllerContext.getPString("pid");
        if(StringUtils.isEmpty(pid)){
            throw new UserOperateException(500, "获取父订单数据失败");
        }
        List<Object> params = new ArrayList<>();

        String where = " where t1._id=?";
        params.add(pid);

        String sql = "select" +
                " distinct t2._id" +
                ",t2.pid" +
                ",t2.createTime as orderCreateTime" +
                ",t2.score" +
                ",t2.orderStatus" +
                ",t2.payMoney" +
                ",t2.pensionMoney" +
                ",t2.orderType" +
                ",t2.orderNo" +
                ",t3.cardNo" +
                ",t3.belongArea as belongMember" +
                ",t4.belongArea as belongSeller" +

                ",t3.belongAreaValue as belongValueMember" +
                ",t4.belongAreaValue as belongValueSeller" +

                ",t3.realName as nameMember" +
                ",t4.name as nameSeller" +

                ",t3._id as memberId" +
                ",t4._id as sellerId" +

                " from OrderInfo t1" +
                " left join OrderInfo t2 on t1._id=t2.pid" +
                " left join Member t3 on t2.memberId=t3._id" +
                " left join Seller t4 on t2.sellerId=t4._id" +
                where;
    }

    /**
     * 查询支付记录
     *
     * @throws Exception
     */
    @GET
    @Path("/queryPayLog")
    public void queryPayLog() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String payName = ControllerContext.getPString("_payName");
        String createTime = ControllerContext.getPString("_createTime");
        String payType = ControllerContext.getPString("_payType");
        String payStatus = ControllerContext.getPString("_payStatus");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }else{
            //如果查询时间为空，则默认查询当前时间以前的一个月的数据 --2019-03-08
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime().getTime();
            cal.add(cal.MONTH, -1);
            startTime =  cal.getTime().getTime();
        }

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        String where = " where 1=1 and (t2.pid=-1 or t2.pid is null)";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        if (StringUtils.isNotEmpty(payName)) {
            if ("非会员".equals(payName)) {
                where += " and t2.orderType = 2";
            } else {
                where += " and (t3.name like ? or t4.name like ? or t5.realName like ?)";
                p.add("%" + payName + "%");
                p.add("%" + payName + "%");
                p.add("%" + payName + "%");
            }
        }
        if (startTime != 0) {
            where += " and t1.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(payType)) {
            where += " and t2.payType=?";
            p.add(payType);
        }
        if (StringUtils.isNotEmpty(payStatus)) {
            if("payReturn".equals(payStatus)){
                where += " and t6.returnStatus='SUCCESS'";
            }else{
                where += " and t1.payStatus=? and (t6.returnStatus not in ('SUCCESS','FAIL','START') or t6.returnStatus is null)";
                p.add(payStatus);
            }
        }

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            if (!"\\_A-000001\\_".equals(areaValue)) {
                where += " and (t3.areaValue like ? or t4.belongAreaValue like ? or t5.belongAreaValue like ?)";
                p.add(areaValue + "%");
                p.add(areaValue + "%");
                p.add(areaValue + "%");
            }
        }

        String hql = "select" +
                " count(t1._id) as totalCount" +
                " from Pay t1" +
                " left join OrderInfo t2 on t1.orderId=t2._id" +
                " left join Factor t3 on t3._id = t2.sellerId" +
                " left join Seller t4 on t4._id = t2.sellerId" +
                " left join Member t5 on t5._id = t2.memberId" +
                " left join PayReturn t6 on t1.orderId = t6.orderId" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);
        r.clear();
        r.add("payId");
        r.add("totalFee");
        r.add("createTime");
        r.add("payType");
        r.add("payStatus");
        r.add("orderNo");
        r.add("factorName");
        r.add("factorId");
        r.add("sellerName");
        r.add("sellerId");
        r.add("memberName");
        r.add("memberId");
        r.add("totalPrice");
        r.add("orderType");
        r.add("orderId");
        r.add("pid");
        r.add("payMoney");
        r.add("orderStatus");
        r.add("returnStatus");
        String sql = "select" +
                " t1._id as payId" +
                ",t1.totalFee" +
                ",t1.createTime" +
                ",t1.payType" +
                ",t1.payStatus" +

                ",t2.orderNo" +
                ",t2.totalPrice" +
                ",t2.orderType" +
                ",t2._id as orderId" +
                ",t2.pid" +
                ",t2.payMoney" +
                ",t2.orderStatus" +

                ",t3.name as factorName" +
                ",t3._id as factorId" +
                ",t4.name as sellerName" +
                ",t4._id as sellerId" +
                ",t5.realName as memberName" +
                ",t5._id as memberId" +

                ",t6.returnStatus" +

                " from Pay t1" +
                " left join OrderInfo t2 on t1.orderId=t2._id" +
                " left join Factor t3 on t3._id = t2.sellerId" +
                " left join Seller t4 on t4._id = t2.sellerId" +
                " left join Member t5 on t5._id = t2.memberId" +
                " left join PayReturn t6 on t1.orderId = t6.orderId" +
                where + " order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);

    }

    /**
     * 月结算转账提交
     */
    @POST
    @Path("/agentIncomeSubmit")
    public void agentIncomeSubmit() throws Exception {
        //验证是否是财务管理员
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrentByCw");
        ServiceAccess.callService(msg).getContent();

        String[] agentIdList = ControllerContext.getPString("idList").split("_");

        List<Map<String, Object>> notSubmitList = new ArrayList<Map<String, Object>>();
        Map<String, Object> income = new HashMap<>();

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        returnFields.add("bankId");
        returnFields.add("bankUser");
        returnFields.add("bankName");
        returnFields.add("bankTypeValue");
        returnFields.add("bankCityValue");
        returnFields.add("isTransfer");
        returnFields.add("incomeAccount");
        returnFields.add("userName");
        returnFields.add("_id");

        String sql = "select" +
                " bankId" +
                ",bankUser" +
                ",bankName"+
                ",bankTypeValue"+
                ",bankCityValue"+
                ",isTransfer"+
                ",incomeAccount" +
                ",userName"+
                ",_id"+
                " from AgentAccountMonth"+
                " where _id in (";
        StringBuffer where = new StringBuffer();
        for(int i=0;i<agentIdList.length;i++){
            where.append(",?");
            params.add(agentIdList[i]);
        }
        sql = sql + where.substring(1) +")";
        List<Map<String, Object>> incomeList = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);

        if(incomeList==null || incomeList.size()!=agentIdList.length){
            throw new UserOperateException(500,"提交的转账请求未找到");
        }

        for (int i = 0, len = incomeList.size(); i < len; i++) {
            if (incomeList != null && incomeList.size() > 0 && (incomeList.get(i).get("isTransfer") == null
                    || !Boolean.valueOf(incomeList.get(i).get("isTransfer").toString()))) {
                Map<String, Object> user = new HashMap<>();
                user.put("name", incomeList.get(i).get("userName"));
                if (StringUtils.mapValueIsEmpty(incomeList.get(i),"bankId") || StringUtils.mapValueIsEmpty(incomeList.get(i),"bankUser")
                        || StringUtils.mapValueIsEmpty(incomeList.get(i),"bankName") || StringUtils.mapValueIsEmpty(incomeList.get(i),"bankTypeValue")
                        || StringUtils.mapValueIsEmpty(incomeList.get(i),"bankCityValue")) {
                    user.put("status", "失败：银行卡资料不完善");
                    notSubmitList.add(user);
                    continue;
                } else {
                    long orderNo = System.currentTimeMillis();
                    incomeList.get(i).put("incomeAccount",BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.multiply(Double.parseDouble(incomeList.get(i).get("incomeAccount").toString()),100)).intValue());
                    incomeList.get(i).put("orderNo",orderNo);
                    income.put("orderNo",orderNo);
                    Map<String,Object> status = payAgentMonth(incomeList.get(i));
                    if(status != null && !StringUtils.mapValueIsEmpty(status,"memo") && "成功".equals(status.get("memo"))){
                        income.put("isTransfer", true);
                        income.put("transferTime", System.currentTimeMillis());
                        user.put("status",status.get("memo").toString());
                    }else{
                        user.put("status","失败："+status.get("memo").toString());
                    }
                    notSubmitList.add(user);
                }
                income.put("status",user.get("status"));
                income.put("_id", agentIdList[i]);
                MysqlDaoImpl.getInstance().saveOrUpdate("AgentAccountMonth", income);
                income.clear();
            } else {
                throw new UserOperateException(400, "请选择尚未转账的数据,不要包含已转账的数据!");
            }
        }
        toResult(Response.Status.OK.getStatusCode(), notSubmitList);
    }

    public String getDate() throws Exception {
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        Date date=new Date();
        String date1=sf.format(date);
        return date1;
    }

    public String requestPost(String url,List<NameValuePair> params) throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

        CloseableHttpResponse response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity, "utf-8");
        httppost.releaseConnection();
        return jsonStr;
    }

    public Map<String,Object> payAgentMonth(Map<String,Object> user) throws Exception {
        try {
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"+
                    "<payforreq>"+
                    "<ver>1.00</ver>"+//版本号
                    "<merdt>"+ getDate()+"</merdt>"+//请求日期
                    "<orderno>"+user.get("orderNo")+"</orderno>"+//请求流水
                    "<bankno>"+user.get("bankTypeValue").toString()+"</bankno>"+//总行代码
                    "<cityno>"+user.get("bankCityValue").toString()+"</cityno>"+//城市代码
                    "<accntno>"+user.get("bankId").toString()+"</accntno>"+//账号
                    "<accntnm>"+user.get("bankUser").toString()+"</accntnm>"+//账户名称
                    "<amt>"+user.get("incomeAccount").toString()+"</amt>"+//金额
                    "</payforreq>";
            String macSource = "0006510F0495975|2b316100d83079616a8167ba57437a4f|"+"payforreq"+"|"+xml;
            String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
            String loginUrl = "https://fht.fuiou.com/fuMer/req.do";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("merid", "0006510F0495975"));
            params.add(new BasicNameValuePair("reqtype", "payforreq"));
            params.add(new BasicNameValuePair("xml", xml));
            params.add(new BasicNameValuePair("mac", mac));

            return XmlUtils.xmlToMap(requestPost(loginUrl,params));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFyPay(Map<String,Object> order) throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"+
                "<qrytransreq>"+
                "<ver>1.00</ver>"+
                "<busicd>AP01</busicd>"+    //AP01:代付  AC01：代收  TP01：退票
                "<orderno>"+order.get("orderNo")+"</orderno>"+      //查询多个流水，流水中间用英文,间隔，一次最多50个
                "<startdt>"+order.get("startdt")+"</startdt>"+
                "<enddt>"+order.get("enddt")+"</enddt>"+
//	    			"<transst>1</transst>"+
                "</qrytransreq>";
        String macSource = "0006510F0495975|2b316100d83079616a8167ba57437a4f|"+"qrytransreq"+"|"+xml;
        String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
        String loginUrl = "https://fht.fuiou.com/fuMer/req.do";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("merid", "0006510F0495975"));
        params.add(new BasicNameValuePair("reqtype", "qrytransreq"));
        params.add(new BasicNameValuePair("xml", xml));
        params.add(new BasicNameValuePair("mac", mac));
        return requestPost(loginUrl,params);
    }

    /**
     * 查询代付状态
     * 根据交易流水查询,如果本地已经保存且已完成或失败，则查本地；若本地没有保存且尚未完成，则查富友，并保存到本地
     * 若有多个订单，则用英文逗号分隔
     */
    @POST
    @Path("/getFyPay")
    public void getFyPay() throws Exception {
        String orderNo = ControllerContext.getPString("orderNo");
        String time = ControllerContext.getPString("time");//格式为yyyymm

        if(StringUtils.isEmpty(orderNo)){
            throw new UserOperateException(500,"交易流水不能为空");
        }
        if(StringUtils.isEmpty(time) || time.length()!=6){
            throw new UserOperateException(500,"交易时间不能为空");
        }

        String[] orderList = orderNo.split(",");
        int len = orderNo.split(",").length;
        if(len>50){
            throw new UserOperateException(500,"一次最多查询50笔交易流水");
        }

        //查询哪些交易已经完结
        List<String> returnField = new ArrayList<>();
        returnField.add("_id");
        returnField.add("userName");
        returnField.add("orderNo");
        returnField.add("isSuccess");
        returnField.add("incomeAccount");
        returnField.add("transferMoney");
        returnField.add("result");
        returnField.add("reason");
        List<Object> params = new ArrayList<>();
        String sql = "select _id,userName,orderNo,isSuccess,incomeAccount,transferMoney,result,reason from AgentAccountMonth" +
                " where orderNo in (";
        StringBuffer where = new StringBuffer();
        for(int i=0;i<len;i++){
            where.append(",?");
            params.add(orderList[i]);
        }
        sql = sql+where.substring(1,where.length())+")";
        List<Map<String,Object>> orderItem = MysqlDaoImpl.getInstance().queryBySql(
                sql+" and isSuccess=true",returnField,params);

        //全部完结直接返回，若有未完结的则查询代付
        if(orderItem == null || orderItem.size()<len){
            //获取全部记录
            List<Map<String,Object>> all = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
            orderItem = getFyPay(orderNo,time,all);
        }
        toResult(200,orderItem);
    }

    /**
     * 查询富友代付情况
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getFyPay(String orderNo,String time,List<Map<String,Object>> all) throws Exception {
        if(all == null) {
            String[] orderList = orderNo.split(",");
            int len = orderNo.split(",").length;

            List<String> returnField = new ArrayList<>();
            returnField.add("_id");
            returnField.add("userName");
            returnField.add("orderNo");
            returnField.add("isSuccess");
            returnField.add("incomeAccount");
            returnField.add("transferMoney");
            returnField.add("result");
            returnField.add("reason");
            List<Object> params = new ArrayList<>();
            String sql = "select _id,userName,orderNo,isSuccess,incomeAccount,transferMoney,result,reason from AgentAccountMonth" +
                    " where orderNo in (";
            StringBuffer where = new StringBuffer();
            for(int i=0;i<len;i++){
                where.append(",?");
                params.add(orderList[i]);
            }
            sql = sql+where.substring(1,where.length())+")";
            all = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        }

        Map<String,Object> order = new HashMap<>();
        order.put("orderNo",orderNo);
        order.put("startdt",getFormatDate(System.currentTimeMillis()-1000*60*60*24*14));
        order.put("enddt",getFormatDate(System.currentTimeMillis()));

        String xmlStr = new OrderInfoAction().getFyPay(order);
        Map<String,Object> initData = XmlUtils.xmlToMap(xmlStr);
        if(initData!=null && !StringUtils.mapValueIsEmpty(initData,"ret") && !"000000".equals(initData.get("ret"))){
            throw new UserOperateException(500,initData.get("memo").toString());
        }else{
            String[] xmlArr = xmlStr.substring(xmlStr.indexOf("</memo>")+7).replace("</qrytransrsp>","").split("</trans>");
            List<Map<String,Object>> xmlList = new ArrayList<>();
            Map<String,Object> xmlItem;
            for(int i=0,xmlLen=xmlArr.length;i<xmlLen;i++){
                xmlArr[i]="<qrytransrsp>"+xmlArr[i].replace("<trans>","")+"</qrytransrsp>";
                xmlItem = XmlUtils.xmlToMap(xmlArr[i]);
                xmlList.add(xmlItem);

                //将数据保存到本地
                for(int j=0,jlen=all.size();j<jlen;j++){
                    if(all.get(j).get("orderNo").equals(xmlItem.get("orderno"))){
                        all.get(j).put("result",xmlItem.get("result"));
                        all.get(j).put("reason",xmlItem.get("reason"));
                        if("1".equals(xmlItem.get("state"))){
                            all.get(j).put("transferMoney",BigDecimalUtil.fixDoubleNumProfit(
                                    BigDecimalUtil.divide(Double.parseDouble(xmlItem.get("amt").toString()),100)));
                            all.get(j).put("isSuccess",true);
                        }else{
                            all.get(j).put("isSuccess",false);
                        }
                        MysqlDaoImpl.getInstance().saveOrUpdate("AgentAccountMonth",all.get(j));
                    }
                }
            }
        }
        return all;
    }

    /**
     * 获取时间：yyyyMMdd
     * @throws Exception
     */
    public String getFormatDate(long time) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(new Date(time));
        return sdf.format(lastDate.getTime());
    }


    /**
     * 获取提现手续费比例
     */
    @GET
    @Path("/getWithdrawalProportion")
    public void getWithdrawalProportion() throws Exception {
        String type = ControllerContext.getPString("type");
        if(StringUtils.isEmpty(type)){
            type="Seller";
        }

        if(!Pattern.matches("^(Member)|(Seller)|(Factor)$",type)){
            throw new UserOperateException(500,"获取提现手续费失败");
        }

        Map<String,Object> re = new HashMap<>();
        re.put("poundageRatio",ParameterAction.getValueOne("poundageRatio"+type));
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 商品类型管理:修改商品展示类型
     */
    @GET
    @Path("/changeCommodityType")
    public void changeCommodityType() throws Exception {
        String commodityId = ControllerContext.getPString("commodityId");
        String commodityType = ControllerContext.getPString("commodityType");
        String status = ControllerContext.getPString("status");
        Map<String, Object> v = new HashMap<>();
        List<String> r = new ArrayList<>();
        r.add("_id");
        v.put("_id", commodityId);
        if (StringUtils.isEmpty(status) || "false".equals(status) || "null".equals(status)) {
            if ("tejia".equals(commodityType)) {
                v.put("te", true);
            } else if ("gongyi".equals(commodityType)) {
                v.put("gongyi", true);
            } else if ("remen".equals(commodityType)) {
                v.put("hot", true);
            } else {
                v.put("isIndexCommodity", true);
                String sql = "select _id from ProductInfo where isIndexCommodity=true";
                List<Map<String, Object>> pCount = MysqlDaoImpl.getInstance().queryBySql(sql, r, null);
                if (pCount.size() >= 4) {
                    throw new UserOperateException(400, "会员置顶热门商品最多只能设置4个!");
                }
            }
        } else {
            if ("tejia".equals(commodityType)) {
                v.put("te", false);
            } else if ("gongyi".equals(commodityType)) {
                v.put("gongyi", false);
            } else if ("remen".equals(commodityType)) {
                v.put("hot", false);
            } else {
                v.put("isIndexCommodity", false);
            }
        }

        MysqlDaoImpl.getInstance().saveOrUpdate("ProductInfo", v);
    }

    /**
     * 会员删除未支付的线上订单,订单产品表
     */
    @GET
    @Member
    @Path("/delOrderById")
    public void delOrderById() throws Exception {
        String pid = ControllerContext.getPString("pid");
        String childOrderId = ControllerContext.getPString("childOrderId");
        Map<String, Object> order = MysqlDaoImpl.getInstance().findById2Map(entityName, childOrderId, null, null);
        if (order == null || order.size() == 0) {
            throw new UserOperateException(500, "获取订单数据失败");
        }
        if (!"11".equals(order.get("orderType").toString()) || !"1".equals(order.get("orderStatus").toString())) {
            throw new UserOperateException(500, "你只能删除未支付的在线订单");
        }
        delNotPayOrder(pid,childOrderId);
    }

    /**
     * 获取用户余额
     * @throws Exception
     */
    @GET
    @Path("/getUserAccount")
    public void getUserAccount() throws Exception {
        String userType = ControllerContext.getPString("userType");
        String userId = ControllerContext.getPString("userId");

        if(StringUtils.isEmpty(userType) || "Seller".equals(userType)){
            userId = ControllerContext.getContext().getCurrentSellerId();
        }else if("Member".equals(userType)){
            if(StringUtils.isEmpty(userId)){//如果有userId,说明是替朋友充值,检查朋友的余额上限
                userId = ControllerContext.getContext().getCurrentUserId();
            }
        }else{
            throw new UserOperateException(500, "错误的用户类型");
        }

        Map<String,Object> params = new HashMap<>();
        params.put(userType+"Id",userId);
        Map<String,Object> account = MysqlDaoImpl.getInstance().findOne2Map(userType+"MoneyAccount",params,new String[]{"cashCount"}, Dao.FieldStrategy.Include);
        toResult(200,account);
    }

    /**
     * 检查用户余额
     *
     * 1.检查商家支付佣金,检查支付的余额是否大于当前余额,用于判断是否显示"去充值余额"按钮
     * 2.检查会员/商家余额,是否超过5000,超过,则不允许充值
     *
     */
    @GET
    @Path("/checkUserAccount")
    public void checkUserAccount() throws Exception {
        double money = ControllerContext.getPDouble("money");// 充值/支付金额
        String userType = ControllerContext.getPString("userType");
        String userId = ControllerContext.getPString("userId");//可不传,可传id,电话
        String checkUpper = ControllerContext.getPString("checkUpper");//是否检查充值上限

        if(StringUtils.isNotEmpty(userId) && Pattern.matches("^1[34578]{1}\\d{9}$",userId)){
            Message msgFriend = Message.newReqMessage("1:GET@/crm/Member/getMemberInfoByMobile");
            msgFriend.getContent().put("mobile", userId);
            msgFriend = ServiceAccess.callService(msgFriend);
            if (msgFriend.getContent() == null) {
                throw new UserOperateException(400, "无法获取朋友账号,请核对账号");
            }
            userId = msgFriend.getContent().get("_id").toString();
        }

        toResult(200,checkUserAccount(money,userType,userId,StringUtils.isEmpty(checkUpper)?false:ControllerContext.getPBoolean("checkUpper")));
    }

    public Map<String,Object> checkUserAccount(double money,String userType,String userId,boolean checkUpper) throws Exception {
        Map<String,Object> re = new HashMap<>();

        if(StringUtils.isEmpty(userType) || "Seller".equals(userType)){
            userId = ControllerContext.getContext().getCurrentSellerId();
        }else if("Member".equals(userType)){
            if(StringUtils.isEmpty(userId)){//如果有userId,说明是替朋友充值,检查朋友的余额上限
                userId = ControllerContext.getContext().getCurrentUserId();
            }
        }else{
            throw new UserOperateException(500, "错误的用户类型");
        }

        if(StringUtils.isEmpty(userId)){
            throw new UserOperateException(500, "获取用户数据失败");
        }

        // 当没有充值金额时,表明是输入金额前检查,判断当前余额是否大于5000元,是否进入充值页面;
        // 若有金额,则是输入金额后检查,判断充值的金额加上余额是否大于5000元
        if(checkUpper){
            if(StringUtils.isEmpty(String.valueOf(money))){
                money=0;
            }else if(!Pattern.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$",String.valueOf(money))){
                throw new UserOperateException(500, "无法识别的金额");
            }
        }else if(!Pattern.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$",String.valueOf(money))){
            throw new UserOperateException(500, "无法识别的金额");
        }

        Map<String,Object> params = new HashMap<>();
        params.put(userType+"Id",userId);
        Map<String,Object> account = MysqlDaoImpl.getInstance().findOne2Map(userType+"MoneyAccount",params,new String[]{"cashCount"}, Dao.FieldStrategy.Include);

        if(account==null || account.size()==0){
            throw new UserOperateException(500, "获取用户数据失败");
        }

        double cash = BigDecimalUtil.fixDoubleNumProfit(Double.parseDouble(account.get("cashCount").toString()));
        if(checkUpper){
            money+=cash;//充值的金额+余额
            cash=5000;//充值上限
        }

        if(cash < BigDecimalUtil.fixDoubleNumProfit(money)){
            re.put("status","FAIL");
        }else{
            re.put("status","SUCCESS");
        }
        return re;
    }

    /**
     * 平台管理:统计所有会员的交易数据
     * 代理商只可查看归属下的会员
     */
    @GET
    @Path("/getMemberOrderCount")
    public void getMemberOrderCount() throws Exception {
        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "你无此操作权限");
        }

        List<Object> params = new ArrayList<>();
        List<String> returnField = new ArrayList<>();
        returnField.add("线下交易总额");
        returnField.add("线上交易总额");
        returnField.add("充值总额");
        returnField.add("总积分");
        returnField.add("总已使用积分");
        returnField.add("交易总额");

        String sql = "select" +
                " sum(t1.cashOfflineCount) as 线下交易总额" +
                ",sum(t1.cashOnlineCount) as 线上交易总额" +
                ",sum(t1.rechargeCount) as 充值总额" +
                ",sum(t1.cashCount) as 总积分" +
                ",sum(t1.cashCountUse) as 总已使用积分" +
                ",sum(t1.totalConsume) as 交易总额" +
                " from MemberMoneyAccount t1";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);

        toResult(200,re.get(0));
    }


    /**
     * 获取当前登录的代理商
     * @throws Exception
     */
    public Map<String,Object> checkAdmin() throws Exception{
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        return agentInfo;
    }
}
