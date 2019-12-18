package com.zq.kyb.crm.action;

import com.mysql.jdbc.MySQLConnection;
import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by haozigg on 16/12/5.
 */
public class CouponAction extends BaseActionImpl {
    /**
     * 查询我的卡券
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/queryMyCoupon")
    public void queryMyCoupon() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Date date = new Date();
        long curTime = date.getTime();
        Long pageSize = ControllerContext.getPLong("pageSize");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long indexNum = 0l;
        if (pageNo >0) {
            indexNum = (pageNo - 1) * pageSize;
        }
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getCurrentUserId());
        params.add(curTime);

        List<String> result = new ArrayList<>();
        result.add("totalCount");
        String sql = "select count(t1._id) as totalCount from Coupon t1 left join MemberCouponLink t2 on t2.couponId=t1._id left join Seller t3 on t1.sellerId=t3._id where t2.memberId=? and t1.endTime>?";
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(sql, result, params);
        Long totalNum = (Long) couponList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalNum", totalNum);
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("condition");
        returnFields.add("couponName");
        returnFields.add("value");
        returnFields.add("sellerId");
        returnFields.add("startTime");
        returnFields.add("endTime");
        returnFields.add("serial");
        returnFields.add("canUse");
        returnFields.add("sellerIcon");
        returnFields.add("sellerName");


        String hql = "select " +
                "t1.condition" +
                ",t1.name as couponName" +
                ",t1.value" +
                ",t1.sellerId" +
                ",t1.startTime" +
                ",t1.endTime" +
                ",t2.serial" +
                ",t2.canUse" +
                ",t3.icon as sellerIcon" +
                ",t3.name as sellerName" +

                " from " + Dao.getFullTableName("Coupon") + " t1" +
                " left join MemberCouponLink t2 on t2.couponId=t1._id" +
                " left join Seller t3 on t1.sellerId=t3._id" +

                " where 1=1 " +
                " and t2.memberId=?" +
                " and t1.endTime>? order by t2.createTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, params);
        resultMap.put("couponList", re);
        if(re!=null&&re.size()>0){
            for(int i = 0 ;i<re.size();i++){
                re.get(i).put("isOverdue",Long.valueOf(re.get(i).get("endTime").toString())<new Date().getTime());
            }
        }
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 查询商家的卡券
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/queryStoreCoupon")
    public void queryStoreCoupon() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        String sellerId = ControllerContext.getContext().getPString("sellerId");
        Long indexNum = 0l;
        if (pageNo>0) {
            indexNum = (pageNo-1) * pageSize;
        }
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<String>();
        params.add(sellerId);
        Date date = new Date();
        long curTime = date.getTime();
        params.add(curTime);
        List<String> result = new ArrayList<>();
        result.add("totalCount");
        String sql = "select count(t1._id) as totalCount from Coupon t1 left join Seller t2 on t1.sellerId=t2._id where t1.sellerId=? and t1.endTime>?";
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(sql, result, params);
        Long totalNum = (Long) couponList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalNum", totalNum);
        returnFields.add("_id");
        returnFields.add("condition");
        returnFields.add("value");
        returnFields.add("sellerId");
        returnFields.add("startTime");
        returnFields.add("endTime");
        returnFields.add("couponName");
        returnFields.add("sellerName");
        returnFields.add("sellerIcon");
        String hql = "select" +
                " t1._id" +
                ",t1.condition" +
                ",t1.value" +
                ",t1.sellerId" +
                ",t1.startTime" +
                ",t1.endTime" +
                ",t1.name as couponName" +
                ",t2.name as sellerName" +
                ",t2.icon as sellerIcon" +

                " from Coupon t1" +
                " left join Seller t2 on t1.sellerId=t2._id" +

                " where 1=1 " +
                " and t1.sellerId=?" +
                " and t1.endTime>? order by endTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, params);
        resultMap.put("couponList", re);
        if(re!=null&&re.size()>0){
            for(int i = 0 ;i<re.size();i++){
                re.get(i).put("isOverdue",Long.valueOf(re.get(i).get("endTime").toString())<new Date().getTime());
            }
        }
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 查询会员在该商家已领取的卡券
     * 如果有订单金额,则返回满足条件的卡券
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/queryStoreCouponReceive")
    public void queryStoreCouponReceive() throws Exception {
        String isCanUse = ControllerContext.getPString("isCanUse");
        String isFormat = ControllerContext.getPString("isFormat");
        String sellerId = ControllerContext.getPString("sellerId");

        List<Object> params = new ArrayList<>();

        if(StringUtils.isEmpty(sellerId)){
            throw new UserOperateException(500,"获取商家信息失败");
        }
        //将商家ID拆分成数组,循环添加条件参数
        String[] sellerArr= sellerId.split(",");
        params.add(ControllerContext.getContext().getCurrentUserId());
        String where = " where t2.memberId = ? and t1.sellerId in (";
        int sellerCount=sellerArr.length;
        for(int i=0;i<sellerCount;i++){
            where+="?,";
            params.add(sellerArr[i]);
        }
        where=where.substring(0,where.length()-1);
        where+=")";

        String orderBy = "";
        String leftJoin="";
        String field="";
        List<String> returnFields = new ArrayList<String>();

        if(StringUtils.isNotEmpty(isCanUse)){
            returnFields.add("sellerIcon");
            field=",t3.icon as sellerIcon";
            leftJoin=" left join Seller t3 on t3._id=t1.sellerId";
            where +=" and t2.canUse=true" +
                    " and t1.startTime<?"+
                    " and t1.endTime>?";
            if(sellerCount>1){
                orderBy +=" order by t1.sellerId desc, t1.`value` desc";
            }else{
                orderBy +=" order by t1.`value` desc";
            }
            params.add(System.currentTimeMillis());
        }else{
            where +=" and t1.endTime>?";
        }
        params.add(System.currentTimeMillis());

        returnFields.add("_id");
        returnFields.add("condition");
        returnFields.add("name");
        returnFields.add("value");
        returnFields.add("sellerId");
        returnFields.add("startTime");
        returnFields.add("endTime");
        returnFields.add("linkId");

        String sql = "select" +
                " t1._id" +
                ",t1.condition" +
                ",t1.name" +
                ",t1.value" +
                ",t1.sellerId" +
                ",t1.startTime" +
                ",t1.endTime" +
                ",t2.serial as linkId" +
                field+

                " from Coupon t1" +
                " left join MemberCouponLink t2 on t2.couponId=t1._id" +
                leftJoin+
                where+
                orderBy;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        //如果传入的商家是多个商家,则返回的数据需要按照商家分组
        if (StringUtils.isNotEmpty(isFormat)) {
            if(re!=null && re.size()!=0){
                //分组的起始数
                int groupStartIndex=0;
                List<Map<String,Object>> sellerList = new ArrayList<>();
                for(int i=0,len=re.size();i<len;i++){
                    //比较当前id与后一个id是否一致,若不一致,则将之前一致的id所对应的卡券全部添加到对应的seller里,并重新赋值分组起始数
                    if(i==len-1 || !re.get(i).get("sellerId").toString().equals(re.get(i+1).get("sellerId").toString())){
                        Map<String,Object> sellerItem = new HashMap<>();
                        List<Map<String,Object>> couponList = new ArrayList<>();

                        sellerItem.put("sellerId",re.get(i).get("sellerId"));
                        for(int j=groupStartIndex;j<i+1;j++){
                            couponList.add(re.get(j));
                        }
                        sellerItem.put("couponList",couponList);
                        sellerList.add(sellerItem);
                        groupStartIndex=i+1;
                    }
                    re.get(i).remove("sellerId");
                }
                re=sellerList;
            }
        }

        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员领取卡券
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/addMemberCoupon")
    public void addMemberCoupon() throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getPString("couponId"));
        params.add(ControllerContext.getContext().getCurrentUserId());

        List<String> returnFields = new ArrayList<String>();
        returnFields.add("_id");

        String sql = "select" +
                " t1._id" +
                " from " + Dao.getFullTableName("MemberCouponLink") + " t1" +
                " where 1=1 " +
                " and t1.couponId=?" +
                " and t1.memberId=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (re.size() > 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "已经领取该卡券!");
        } else {
            //生成卡券序列号:年月日+时分秒+毫秒    //+四位随机数+随机大写字母
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String serialNumber = sdf.format(date);
//            String letter[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"
//                    , "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V"
//                    , "W", "X", "Y", "Z"};
//            for (int index = 1; index < 5; index++) {
//                serialNumber += new Random().nextInt(10);
//            }
//            serialNumber += letter[new Random().nextInt(26)];

            params.add(UUID.randomUUID().toString());
            params.add(serialNumber);
            params.add(date.getTime());
            params.add(true);
            String insertSql = "insert into MemberCouponLink (couponId,memberId,_id,serial,createTime,canUse) value (?,?,?,?,?,?)";
            MysqlDaoImpl.getInstance().exeSql(insertSql, params, "MemberCouponLink");
        }
    }

    @Override
    public void save() throws Exception {
        super.save();
    }

    /**
     * 查询店铺的卡券
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getStoreCoupon")
    public void getStoreCoupon() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Long indexNum = ControllerContext.getPLong("indexNum");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        List<String> result = new ArrayList<>();
        result.add("totalCount");
        String sql = "select count(t1._id) as totalCount from Coupon t1 left join Seller t2 on t1.sellerId=t2._id where t1.sellerId=?;";
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(sql, result, params);
        Long totalNum = (Long) couponList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalNum", totalNum);
        List<String> returnField = new ArrayList<>();
        returnField.add("_id");
        returnField.add("condition");
        returnField.add("name");
        returnField.add("value");
        returnField.add("createTime");
        returnField.add("startTime");
        returnField.add("endTime");
        returnField.add("icon");
        returnField.add("sellerName");
        String hql = "select" +
                " t1._id" +
                ",t1.`condition`" +
                ",t1.name" +
                ",t1.`value`" +
                ",t1.createTime" +
                ",t1.startTime" +
                ",t1.endTime" +
                ",t2.icon" +
                ",t2.name as sellerName" +
                " from Coupon t1" +
                " left join Seller t2 on t1.sellerId=t2._id" +
                " where t1.sellerId=?" +
                " order by t1.endTime desc,t1.createTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(hql, returnField, params);
        if(re!=null&&re.size()!=0){
            for(int i = 0 ;i<re.size();i++){
                re.get(i).put("isOverdue",Long.valueOf(re.get(i).get("endTime").toString())<new Date().getTime());
            }
        }
        resultMap.put("couponList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 添加店铺卡券
     */
    @POST
    @Seller
    @Path("/addStoreCoupon")
    public void addStoreCoupon() throws Exception {
        String name = ControllerContext.getPString("name");
        double condition = ControllerContext.getPDouble("condition");
        double value = ControllerContext.getPDouble("value");
        Long startTime = ControllerContext.getPLong("startTime");
        Long endTime = ControllerContext.getPLong("endTime");
        String sellerId = ControllerContext.getContext().getCurrentSellerId();

        if(condition<=value){
            throw new UserOperateException(500,"折扣金额不能大于或等于条件金额");
        }
        if (!Pattern.matches("^\\d{1,5}(?:\\.\\d{1,2})?$", ControllerContext.getPString("condition"))
                || !Pattern.matches("^\\d{1,5}(?:\\.\\d{1,2})?$", ControllerContext.getPString("value"))) {
            throw new UserOperateException(500, "金额最多5位,小数最多2位");
        }
        if(value<=0){
            throw new UserOperateException(500, "折扣金额不能小于或等于0");
        }
        if(StringUtils.isEmpty(name) || name.length()>20){
            throw new UserOperateException(500, "卡券名字最多20个字符长度");
        }

        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("condition", condition);
        m.put("value", value);
        m.put("startTime", startTime);
        m.put("endTime", endTime);
        m.put("createTime", System.currentTimeMillis());
        m.put("sellerId", sellerId);
        m.put("_id", UUID.randomUUID().toString());

        MysqlDaoImpl.getInstance().saveOrUpdate("Coupon", m);
    }

    /**
     * 根据关联ID获取卡券详情
     *
     * @throws Exception
     */
    @GET
    @Path("/getCouponByLinkId")
    public void getCouponByLinkId() throws Exception {
        String linkId = ControllerContext.getPString("linkId");
        Boolean isGetAll = ControllerContext.getPBoolean("isGetAll");
        if(StringUtils.isEmpty(linkId)){
            throw new UserOperateException(500,"获取卡券失败");
        }
        Map<String,Object> params = new HashMap<>();
        params.put("serial",linkId);
        Map<String,Object> link = MysqlDaoImpl.getInstance().findOne2Map("MemberCouponLink",params,null,null);
        if(link==null || link.size()==0 || StringUtils.mapValueIsEmpty(link,"couponId")){
            throw new UserOperateException(500,"获取卡券失败");
        }
        Map<String,Object> coupon = MysqlDaoImpl.getInstance().findById2Map("Coupon",link.get("couponId").toString(),null,null);
        if(coupon==null || coupon.size()==0){
            throw new UserOperateException(500,"获取卡券失败");
        }
        if(isGetAll){
            Map<String,Object> re = new HashMap<>();
            re.put("couponLink",link);
            re.put("coupon",coupon);
            toResult(Response.Status.OK.getStatusCode(),re);
        }else{
            toResult(Response.Status.OK.getStatusCode(),coupon);
        }
    }


    /**
     * 使用卡券
     */
    @POST
    @Seller
    @Path("/updateCouponSerial")
    public void updateCouponSerial() throws Exception {
        String sellerId;
        //会员在线购买,核销卡券
        if("member".equals(ControllerContext.getPString("userType"))){
            sellerId = ControllerContext.getPString("sellerId");
        }else{//商家核销卡券
            sellerId = ControllerContext.getContext().getCurrentSellerId();
        }

        String couponNo = ControllerContext.getPString("couponNo");
        if (StringUtils.isEmpty(couponNo)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "序列号不能为空!");
        }
        Map<String,Object> params = new HashMap<>();
        params.put("serial",couponNo);

        Map<String, Object> li = MysqlDaoImpl.getInstance().findOne2Map("MemberCouponLink",params,null,null);
        if (li == null || li.size() == 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "会员未拥有该卡券!");
        }

        params.clear();
        params.put("_id",li.get("couponId"));
        params.put("sellerId",sellerId);
        Map<String, Object> coupon = MysqlDaoImpl.getInstance().findOne2Map("Coupon",params,null,null);
        if(coupon==null || coupon.size()==0){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "商家未发布该卡券!");
        }

        if ((Boolean) li.get("canUse")) {
            Map<String, Object> m = new HashMap<>();
            m.put("useTime", new Date().getTime());
            m.put("_id", li.get("_id"));
            m.put("canUse", false);

            MysqlDaoImpl.getInstance().saveOrUpdate("MemberCouponLink", m);
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该卡券已经被使用!");
        }

        if(Long.valueOf(coupon.get("startTime").toString())>System.currentTimeMillis()){
            throw new UserOperateException(500, "该卡券尚未到使用有效期!");
        }

        Map<String, Object> re = new HashMap<>();
        re.put("couponId", li.get("couponId"));
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 查询卡券过期时间
     */
    @POST
    @Seller
    @Path("/getCouponUseTime")
    public void getCouponUseTime() throws Exception {
        String couponId = ControllerContext.getPString("couponId");
//        Map<String,Object> reMap = new HashMap<>();
        Map<String, Object> couponInfo = MysqlDaoImpl.getInstance().findById2Map("Coupon", couponId, null, null);
        toResult(Response.Status.OK.getStatusCode(), couponInfo);
    }

    /**
     * 查询商家已经核销的卡券
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/queryUseCouponList")
    public void queryUseCouponList() throws Exception {
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Long indexNum = ControllerContext.getPLong("indexNum");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        p.add(sellerId);
        r.add("totalCount");
        String hql = "select count(t1._id) as totalCount from MemberCouponLink t1 left join Coupon t2 on t1.couponId=t2._id left join Member t3 on t1.memberId=t3._id where t2.sellerId=? and t1.canUse=false;";
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = (Long) couponList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalNum", totalNum);
        List<String> returnField = new ArrayList<>();
        returnField.add("serial");
        returnField.add("realName");
        returnField.add("useTime");
        String sql = "select" +
                " t1.serial" +
                ",t3.realName" +
                ",t1.useTime" +
                " from MemberCouponLink t1" +
                " left join Coupon t2 on t1.couponId=t2._id" +
                " left join Member t3 on t1.memberId=t3._id" +
                " where t2.sellerId=? and t1.canUse=false" +
                " order by t1.useTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, p);
        resultMap.put("useCouponList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 删除卡券
     */
    @POST
    @Seller
    @Path("/delCoupon")
    public void delCoupon() throws Exception {
        String _id = ControllerContext.getPString("_id");

        if(StringUtils.isEmpty(_id)){
            throw new UserOperateException(500,"获取卡券失败");
        }
        Map<String,Object> coupon = MysqlDaoImpl.getInstance().findById2Map("Coupon",_id,null,null);
        if(coupon==null || coupon.size()==0){
            throw new UserOperateException(500,"获取卡券失败");
        }
        Map<String,Object> params = new HashMap<>();
        params.put("couponId",coupon.get("_id").toString());
        Map<String,Object> link = MysqlDaoImpl.getInstance().findOne2Map("MemberCouponLink",params,new String[]{"_id"},Dao.FieldStrategy.Include);
        if(link!=null && link.size()!=0){
            throw new UserOperateException(500,"该卡券已有会员领取，不可删除");
        }
        MysqlDaoImpl.getInstance().remove("Coupon",_id);
    }

}
