package com.zq.kyb.account.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.regex.Pattern;

public class SellerAction extends BaseActionImpl {

    /**
     * 查询商户详细信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/querySeller")
    public void querySeller() throws Exception {
        Map<String, Object> seller = getCurrentSeller();
        seller.remove("cashPassword");
        Map<String,Object> params = new HashMap<>();
        params.put("sellerId",ControllerContext.getContext().getCurrentSellerId());
        Map<String,Object> user = MysqlDaoImpl.getInstance().findOne2Map("User",params,new String[]{"password"},Dao.FieldStrategy.Exclude);
        seller.put("user",user);
        if(!StringUtils.mapValueIsEmpty(user,"memberId")){
            JSONObject member = ServiceAccess.getRemoveEntity("crm","Member",user.get("memberId").toString());
            member.remove("password");
            seller.put("member",member);
        }
        toResult(200, seller);
    }

    public Map<String,Object> getCurrentSeller() throws Exception {
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Seller", ControllerContext.getContext().getCurrentSellerId(), new String[]{"password"}, Dao.FieldStrategy.Exclude);
        if (re == null) {
            throw new UserOperateException(404, "获取商户信息失败,请重新登录");
        }
        return re;
    }

    @GET
    @Path("/getSellerById")
    public void getSellerById() throws Exception {
        String sellerId = ControllerContext.getPString("_id");
        if(StringUtils.isEmpty(sellerId)){
            return;
        }
        toResult(200, MysqlDaoImpl.getInstance().findById2Map("Seller",sellerId,
                new String[]{"name","operateType","integralRate","phone","area","address","icon","canUse"},
                Dao.FieldStrategy.Include));
    }


    /**
     * 查询商户列表
     *
     * @throws Exception
     */
    @GET
    @Member
    @Seller
    @Path("/queryAllSellerList")
    public void queryAllSellerList() throws Exception {
        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        returnField.add("_id");
        returnField.add("name");
        returnField.add("address");
        returnField.add("area");
        returnField.add("integralRate");
        returnField.add("desc");
        returnField.add("phone");
        returnField.add("icon");
        returnField.add("isRecommend");
        String sql = "select" +
                " t1._id" +
                ",t1.name" +
                ",t1.address" +
                ",t1.area" +
                ",t1.integralRate" +
                ",t1.`desc`" +
                ",t1.phone" +
                ",t1.icon" +
                ",t1.isRecommend" +
                " from Seller t1";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 查询商户列表
     *
     * @throws Exception
     */
    @GET
    @Member
    @Seller
    @Path("/querySellerList")
    public void querySellerList() throws Exception {
        List<Object> params = new ArrayList<>();
        List<String> returnField = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        String selectType = ControllerContext.getContext().getPString("selectType");
        int selectedIndex = ControllerContext.getContext().getPInteger("selectedIndex");
        String areaValue = ControllerContext.getContext().getPString("areaValue");
        String searchSOP = ControllerContext.getContext().getPString("searchSOP");

        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        Long indexNum = ControllerContext.getPLong("indexNum");

        String lat = ControllerContext.getPString("lat");
        String lng = ControllerContext.getPString("lng");

        if (StringUtils.isEmpty(ControllerContext.getPString("indexNum")) && pageNo != null) {
            indexNum = (pageNo - 1);
        }
        if (!StringUtils.isEmpty(ControllerContext.getPString("indexNum")) && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }


        String field = "";
        String notCountField="";//不需要纳入页数统计的字段
        String leftJoinStr = "";
        String whereStr = " where 1=1 and t1.canUse=true";
        String groupStr = "";
        String orderStr = "";

        //经纬度
        if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)){
            params.add(lat);
            params.add(lat);
            params.add(lng);
            returnField.add("distance");
            notCountField += ",6371 * 2 * ASIN(SQRT(POWER(SIN((? - abs(latitude)) * pi()/180 / 2),2) + COS(? * pi()/180 ) * COS(abs(latitude) * pi()/180) * POWER(SIN((? - longitude) * pi()/180 / 2), 2) )) AS distance";
            if(selectedIndex==-1){
                orderStr += ",distance is null,distance asc";
            }
            params.add(lat);
            params.add(lat);
            params.add(lng);
            whereStr+=" and 6371 * 2 * ASIN(SQRT(POWER(SIN((? - abs(latitude)) * pi()/180 / 2),2) + COS(? * pi()/180 ) * COS(abs(latitude) * pi()/180) * POWER(SIN((? - longitude) * pi()/180 / 2), 2) )) < 30";
        }
        if (StringUtils.isNotEmpty(selectType)) {
            whereStr += " and t1.operateValue like ?";
            params.add(selectType.replace("_", "\\_") + "%");
        }
        if(StringUtils.isNotEmpty(searchSOP)){
            whereStr += " and t1.name like ?";
            params.add("%"+searchSOP+"%");
        }
        if (selectedIndex == 0) {//积分率最高
            orderStr = ",t1.integralRate desc";
        } else if (selectedIndex == 1) {//距离最近

        } else if (selectedIndex == 2) {//推荐商家
            whereStr += " and t1.isRecommend=?";
            params.add(true);
        } else if (selectedIndex == 3) {//销量
            field += ",sum(case when t2.saleCount is null then 0 else t2.saleCount end) as countOrder";
            leftJoinStr += " left join ProductInfo t2 on t1._id=t2.sellerId";
            groupStr += " group by t1._id";
            orderStr += ",countOrder desc";
        } else if (selectedIndex == 4) {//评分
            field += ",avg(t3.serviceStar) as countStar";
            leftJoinStr += " left join OrderInfo t2 on t1._id=t2.sellerId left join OrderComment t3 on t2._id=t3.orderId";
            groupStr += " group by t1._id";
            orderStr += ",countStar desc";
        }
        //地址
        if (StringUtils.isNotEmpty(areaValue)) {
            if (StringUtils.isEmpty(areaValue)) {
                areaValue += "%";
            } else {
                areaValue = areaValue.replace("_", "\\_") + "%";
            }
            whereStr += " and areaValue like ?";
            params.add(areaValue);
        }

        if(StringUtils.isNotEmpty(orderStr)){
            orderStr=" order by "+orderStr.substring(1,orderStr.length());
        }
        String hql = "select count(t1._id) as total,t1._id" + field +notCountField+ " from Seller t1 " + leftJoinStr + whereStr + groupStr + orderStr;
//        if (selectedIndex == 3 || selectedIndex == 4) {//销量
//            hql = "select count(t1._id) as total from Seller t1 " + whereStr;
//        }
        returnField.add("total");
        List<Map<String, Object>> countList = MysqlDaoImpl.getInstance().queryBySql(hql, returnField, params);
        long totalNum = 0;
        if (countList.size() > 0) {
            if(Pattern.matches("^(-1)|[02]$",String.valueOf(selectedIndex))){
                totalNum = (Long) countList.get(0).get("total");
            }else{
                totalNum = Long.parseLong(String.valueOf(countList.size()));
            }
        }
        long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        resultMap.put("pageNo", pageNo);
        returnField.clear();
        if (selectedIndex == 3) {//销量
            returnField.add("countOrder");
        }
        if (selectedIndex == 4) {//销量
            returnField.add("countStar");
        }

        if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)){
            returnField.add("distance");
        }

        returnField.add("_id");
        returnField.add("name");
        returnField.add("address");
        returnField.add("area");
        returnField.add("integralRate");
        returnField.add("desc");
        returnField.add("phone");
        returnField.add("operateType");
        returnField.add("icon");
        returnField.add("doorImg");
        returnField.add("isRecommend");
        returnField.add("intro");
        returnField.add("latitude");
        returnField.add("longitude");
        String sql = "select" +
                " t1._id" +
                ",t1.name" +
                ",t1.address" +
                ",t1.area" +
                ",t1.integralRate" +
                ",t1.`desc`" +
                ",t1.phone" +
                ",t1.operateType" +
                ",t1.icon" +
                ",t1.doorImg" +
                ",t1.intro" +
                ",t1.latitude" +
                ",t1.longitude" +
                ",t1.isRecommend" +
                field +
                notCountField+
                " from Seller t1" +
                leftJoinStr +
                whereStr +
                groupStr +
                orderStr +
                " LIMIT " + indexNum + "," + pageSize;


        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, params);
        resultMap.put("sellerList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }


    /**
     * 修改商户头像
     */
    @PUT
    @Seller
    @Path("/saveSellerIcon")
    public void saveSellerIcon() throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("_id", ControllerContext.getContext().getCurrentSellerId());
        p.put("icon", ControllerContext.getPString("icon"));
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, p);
    }

    /**
     * 添加或修改店铺活动
     */
    @POST
    @Seller
    @Path("/addStoreEvent")
    public void addStoreEvent() throws Exception {
        String name = ControllerContext.getPString("name");
        String content = ControllerContext.getPString("content");
        Long startTime = ControllerContext.getPLong("startTime");
        Long endTime = ControllerContext.getPLong("endTime");
        String explain = ControllerContext.getPString("explain");
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        String eventId = ControllerContext.getContext().getPString("_id");
        if(StringUtils.isEmpty(name)){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "活动名字不能为空!");
        }
        if(StringUtils.isEmpty(content)){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "优惠规则不能为空!");
        }
        if(StringUtils.isEmpty(explain)){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "特殊说明不能为空!");
        }
        if(name.length()>32){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "活动名字超长!");
        }
        if(content.length()>64){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "优惠规则超长!");
        }
        if(explain.length()>64){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "特殊说明超长!");
        }

        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("content", content);
        m.put("startTime", startTime);
        m.put("endTime", endTime);
        m.put("createTime", System.currentTimeMillis());
        m.put("explain", explain);
        m.put("sellerId", sellerId);
        m.put("isGoing", true);
        if (StringUtils.isEmpty(eventId)) {
            m.put("_id", UUID.randomUUID().toString());
        } else {
            m.put("_id", eventId);
        }
        MysqlDaoImpl.getInstance().saveOrUpdate("StoreEvent", m);
    }

    /**
     * 根据活动ID获取店铺活动
     */
    @GET
    @Seller
    @Path("/getStoreEventById")
    public void getStoreEventById() throws Exception {
        String eventId = ControllerContext.getContext().getPString("eventId");
        Map<String, Object> event = MysqlDaoImpl.getInstance().findById2Map("StoreEvent", eventId, null, null);
        toResult(Response.Status.OK.getStatusCode(), event);
    }


    /**
     * 获取店铺活动
     */
    @GET
    @Seller
    @Path("/getStoreEvent")
    public void getStoreEvent() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Boolean isGoing = ControllerContext.getContext().getPBoolean("isGoing");
        String eventId = ControllerContext.getContext().getPString("eventId");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        long curDate = new Date().getTime();
        String haveSellerId = ControllerContext.getContext().getPString("sellerId");
        if (StringUtils.isNotEmpty(haveSellerId)) {
            sellerId = haveSellerId;
        }

        List<Object> params = new ArrayList<>();
        params.add(sellerId);

        String whereStr = "";
        //是否查正在进行中
        if (isGoing != null && isGoing) {
            whereStr += " and endTime>=? and isGoing=true";
            params.add(curDate);
        }
        if (StringUtils.isNotEmpty(eventId)) {
            whereStr += " and _id=?";
            params.add(eventId);
        }
        List<String> p = new ArrayList<String>();
        p.add("totalCount");
        String hql = "select count(_id) as totalCount" +
                " from StoreEvent" +
                " where sellerId=?" +
                whereStr +
                " order by isGoing desc,endTime desc ";
        List<Map<String, Object>> activityList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) activityList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnField = new ArrayList<>();
        returnField.add("name");
        returnField.add("content");
        returnField.add("startTime");
        returnField.add("endTime");
        returnField.add("explain");
        returnField.add("isGoing");
        returnField.add("_id");

        String sql = "select" +
                " name" +
                ",content" +
                ",startTime" +
                ",endTime" +
                ",`explain`" +
                ",isGoing" +
                ",_id" +
                " from StoreEvent" +
                " where sellerId=?" +
                whereStr +
                " order by isGoing desc,endTime desc " +
                "limit " + indexNum + "," + pageSize;

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, params);
        resultMap.put("eventList", re);
        if(re!=null&&re.size()>0){
            for(int i = 0 ;i<re.size();i++){
                re.get(i).put("isOverdue",Long.valueOf(re.get(i).get("endTime").toString())<new Date().getTime());
                re.get(i).put("isNoStart",Long.valueOf(re.get(i).get("startTime").toString())>new Date().getTime());
            }
        }
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 关闭活动
     */
    @GET
    @Seller
    @Path("/closeEvent")
    public void closeEvent() throws Exception {
        String eventId = ControllerContext.getContext().getPString("_id");
        Boolean isGoing = ControllerContext.getContext().getPBoolean("isGoing");
        if (!isGoing) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该活动已关闭");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("_id", eventId);
        params.put("isGoing", false);
//        params.put("endTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("StoreEvent", params);
    }
    /**
     * 删除活动
     */
    @GET
    @Seller
    @Path("/deleteEvent")
    public void deleteEvent() throws Exception {
        String eventId = ControllerContext.getContext().getPString("_id");
//        Boolean isGoing = ControllerContext.getContext().getPBoolean("isGoing");
//        if (isGoing) {
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该活动尚未关闭");
//        }
        List<Object> p = new ArrayList<>();
        p.add(eventId);
        String sql = "delete from StoreEvent where _id=?";
        MysqlDaoImpl.getInstance().exeSql(sql,p,"StoreEvent");
    }

    /**
     * 查询商户是否开通功能
     *
     * @throws Exception
     */

    @GET
    @Seller
    @Path("/querySellerFun")
    public void querySellerFun() throws Exception {
        String funType = ControllerContext.getContext().getPString("funType");
        Map<String, Object> re = dao.findById2Map("Seller", ControllerContext.getContext().getCurrentSellerId(), new String[]{funType}, Dao.FieldStrategy.Include);
        toResult(200, re);
    }

    /**
     * 获取经商类别
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Member
    @Path("/getOperate")
    public void getOperate() throws Exception {
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String parentCode = ControllerContext.getContext().getPString("parentCode");
        String whereStr = "";
        if (StringUtils.isNotEmpty(parentCode)) {
            whereStr = " and parentCode=?";
            params.add(parentCode);
        }

        returnFields.add("parentCode");
        returnFields.add("code");
        returnFields.add("name");
        String sql = "select" +
                " parentCode" +
                ",code" +
                ",name" +
                " from industry" +
                " where 1=1" +
                whereStr+
                " order by code asc";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }


    /**
     * 店铺发卡记录
     *
     * @throws Exception
     **/
    @POST
    @Seller
    @Path("/getSendCardLog")
    public void getSendCardLog() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList<>();
        String whereStr = "";

        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        String memberCard = ControllerContext.getContext().getPString("memberCard");
        String memberPhone = ControllerContext.getContext().getPString("memberPhone");
        String memberName = ControllerContext.getContext().getPString("memberName");
        String memberIdCard = ControllerContext.getContext().getPString("memberIdCard");
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");

        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "获取发卡点数据失败,请重新登录");
        }
        String factorId=(String)other.get("factorId");
        params.add(factorId);


        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        if (StringUtils.isNotEmpty(memberCard)) {
            whereStr += " and t2.memberCardId like ?";
            params.add("%" + memberCard + "%");
        }
        if (StringUtils.isNotEmpty(memberIdCard)) {
            whereStr += " and t1.idCard like ?";
            params.add("%" + memberIdCard + "%");
        }
        if (StringUtils.isNotEmpty(memberName)) {
            whereStr += " and (t1.name like ? or t1.realName like ?)";
            params.add("%" + memberName + "%");
            params.add("%" + memberName + "%");
        }
        if (StringUtils.isNotEmpty(memberPhone)) {
            whereStr += " and t1.mobile like ?";
            params.add("%" + memberPhone + "%");
        }
        if (startTime != 0) {
            whereStr += " and t2.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t2.createTime<=?";
            params.add(endTime);
        }
        String from = " from Member t1" +
                " left join MemberCard t2 on t2.memberId=t1._id" +
                " where t2.factorId=? and t2.isActive=true";
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t1._id) as totalCount" +
                from + whereStr;
        List<Map<String, Object>> sendCardList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) sendCardList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("realName");
        returnFields.add("memberCardId");
        returnFields.add("createTime");
        String sql = "select" +
                " t1.realName" +
                ",t2.memberCardId as memberCardId" +
                ",t2.createTime as createTime" +
                from + whereStr + " order by t2.createTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("sendCardList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }
    /**
     * 发卡点尚未使用的卡号段
     *
     * @throws Exception
     **/
    @POST
    @Seller
    @Path("/getNotSendCardLog")
    public void getNotSendCardLog() throws Exception {
        String checkCard = ControllerContext.getPString("checkCard");
        String factorId="";

        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (!StringUtils.mapValueIsEmpty(other, "factorId")) {
            factorId = (String)other.get("factorId");
        }else if(!StringUtils.mapValueIsEmpty(other, "agentId")){
            factorId = ControllerContext.getPString("factorId");
        }else{
            throw new UserOperateException(400, "获取服务站数据失败");
        }

        Map<String,Object> factor = MysqlDaoImpl.getInstance().findById2Map("Factor",factorId,new String[]{"areaValue"},Dao.FieldStrategy.Include);
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList<>();
        String whereStr = "";
        params.add(factor.get("areaValue"));
        params.add(factor.get("_id"));
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(_id) as totalCount" +
                " from CardField" +
                " where belongAreaValue=? and `grant`<>?";
        List<Map<String, Object>> sendCardList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) sendCardList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("startCardNo");
        returnFields.add("endCardNo");
        returnFields.add("createTime");
        returnFields.add("cardNum");
        String sql = "select" +
                " startCardNo" +
                ",endCardNo" +
                ",createTime" +
                ",cardNum" +
                " from CardField" +
                " where belongAreaValue=? and `grant`<>? order by createTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        // 若没有会员卡且checkCard=1，则新生成一张卡
        if((re==null || re.size()==0) && StringUtils.isNotEmpty(checkCard) && "1".equals(checkCard)){
            Message msg = Message.newReqMessage("1:POST@/crm/Member/createCardAuto");
            msg.getContent().put("factorId",factorId);
            msg.getContent().put("belongAreaValue",factor.get("areaValue"));
            JSONObject card = ServiceAccess.callService(msg).getContent();
            Map<String,Object> cardMap = new HashMap<>();
            cardMap.put("startCardNo",card.getString("memberCardId"));
            re.add(cardMap);
        }
        resultMap.put("sendCardList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }



    /**
     * 获取商户银行账户
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getBankInfoBySeller")
    public void getBankInfoBySeller() throws Exception {
        String sellerId = ControllerContext.getContext().getCurrentSellerId();

        List<Object> p = new ArrayList<>();
        p.add(sellerId);

        List<String> r = new ArrayList<>();
        r.add("bankId");
        r.add("bankUser");
        r.add("bankUserPhone");
        r.add("bankUserCardId");
        r.add("bankName");

        String sql = "select" +
                " bankId" +
                ",bankUser" +
                ",bankUserPhone" +
                ",bankUserCardId" +
                ",bankName" +
                " from Seller" +
                " where _id=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }

    /**
     * 设置支付密码
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/setPayPassword")
    public void setPayPassword() throws Exception {
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        String userId = ControllerContext.getContext().getCurrentUserId();
        String firstPwd = ControllerContext.getContext().getPString("firstPwd");
        String secondPwd = ControllerContext.getContext().getPString("secondPwd");
        if (StringUtils.isEmpty(firstPwd) || StringUtils.isEmpty(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
        }
        if (!Pattern.matches("^[0-9]{6}$", firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码只能为6位数字!");
        }
        if (!firstPwd.equals(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次输入密码不一致!");
        }
        Map<String, Object> n = new HashMap<>();
        n.put("sellerId", sellerId);
        n.put("_id", userId);
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findOne2Map("User", n, new String[]{"password"}, Dao.FieldStrategy.Include);
        if (MessageDigestUtils.digest(loginPwd.get("password").toString()).equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登录密码一样!");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("_id", sellerId);
        m.put("cashPassword", MessageDigestUtils.digest(firstPwd));
        MysqlDaoImpl.getInstance().saveOrUpdate("Seller", m);
    }

    /**
     * 商户修改支付密码
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/modifySellerPayPwd")
    public void modifySellerPayPwd() throws Exception {
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        String userId = ControllerContext.getContext().getPString("userId");
        String firstPwd = ControllerContext.getContext().getPString("firstPwd");
        String secondPwd = ControllerContext.getContext().getPString("secondPwd");
        String oldPwd = ControllerContext.getContext().getPString("oldPwd");
        if (StringUtils.isEmpty(firstPwd) || StringUtils.isEmpty(secondPwd) || StringUtils.isEmpty(oldPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
        }
        if (!Pattern.matches("^[0-9]{6}$", firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码只能为6位数字!");
        }
        if (!firstPwd.equals(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次输入密码不一致!");
        }
        if (oldPwd.equals(firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "新密码与原密码不能相同!");
        }
        Map<String, Object> n = new HashMap<>();
        n.put("sellerId", sellerId);
        n.put("_id", userId);
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findOne2Map("User", n, new String[]{"password"}, Dao.FieldStrategy.Include);
        if (loginPwd.get("password").equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登录密码一样!");
        }

        List<String> returnFields = new ArrayList<String>();
        returnFields.add("cashPassword");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map(entityName, sellerId, null, null);
        if (MessageDigestUtils.digest(oldPwd).equals(re.get("cashPassword"))) {
            Map<String, Object> m = new HashMap<>();
            m.put("_id", sellerId);
            m.put("cashPassword", MessageDigestUtils.digest(secondPwd));
            MysqlDaoImpl.getInstance().saveOrUpdate("Seller", m);
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "原密码输入错误!");
        }
    }

    /**
     * 商户是否设置支付密码
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/isSellerSetPayPwd")
    public void isSellerSetPayPwd() throws Exception {

        List<String> returnFields = new ArrayList<String>();
        returnFields.add("cashPassword");
        List<Object> params = new ArrayList<>();

        params.add(ControllerContext.getContext().getCurrentSellerId());
        String sql = "select " +
                " cashPassword" +
                " from Seller" +
                " where _id=?";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        Map<String, Object> flag = new HashMap<>();
        flag.put("flag", false);
        if (StringUtils.isEmpty((String) re.get(0).get("cashPassword"))) {
            flag.put("flag", true);
        }
        toResult(Response.Status.OK.getStatusCode(), flag);
    }

    /**
     * 查询商户余额
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/querySellerCashCount")
    public void querySellerCashCount() throws Exception {
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(sellerId);
        r.add("cashCount");

        String sql = "select" +
                " cashCount" +
                " from SellerMoneyAccount" +
                " where sellerId=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (re.size() != 0) {
            toResult(Response.Status.OK.getStatusCode(), re.get(0));
        }
    }

    /**
     * 查询商户交易记录
     */
    @GET
    @Seller
    @Path("/queryTransaction")
    public void queryTransaction() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        String tradeType = ControllerContext.getPString("tradeType");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }

        List<String> s = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(sellerId);

        String where = " where t1.sellerId = ? and t1.tradeType !=6 and t2.orderStatus=100";
        if (startTime != 0) {
            where += " and t2.endTime>=?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t2.endTime<=?";
            p.add(endTime);
        }

        if("undefined".equals(tradeType)){
            tradeType="";
        }
        if(StringUtils.isNotEmpty(tradeType)){
            where += " and t2.orderType=?";
            p.add(tradeType);
        }else{
            where += " and t2.orderType in (0,1,2,11)";
        }

        String from = " from SellerMoneyLog t1" +
                " left join OrderInfo t2 on t1.orderId=t2.orderNo" +
                " left join Member t3 on t1.tradeId=t3._id" +
                " left join Seller t4 on t1.sellerId=t4._id" +
                " left join WithdrawLog t5 on t2.orderNo = t5.orderNo";
        s.add("orderId");
        s.add("_id");
        String sql = "select DISTINCT t1.orderId,t1._id" +
                from + where ;
        List<Map<String, Object>> notDistinct = MysqlDaoImpl.getInstance().queryBySql(sql, s, p);
        if(notDistinct!=null && notDistinct.size()!=0){
            String dempField = "";
            for(Map<String,Object> item:notDistinct){
                dempField += ",'"+item.get("_id").toString()+"'";
            }
            where += " and t1._id in ("+dempField.substring(1,dempField.length())+")";
        }

        s.clear();
        s.add("totalCount");
        s.add("totalPrice");
        s.add("totalBrokerage");
        s.add("totalIncomeOne");

        String hql = "select " +
                " count(DISTINCT t1.orderId) as totalCount" +
                ",sum(t2.totalPrice) as totalPrice" +
                ",sum(t1.brokerageCount) as totalBrokerage" +
                ",sum(t1.incomeOne) as totalIncomeOne" +
                from +
                where+" order by t2.endTime desc";
        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(hql, s, p);
        Long totalNum = (Long) list.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalPrice", list.get(0).get("totalPrice"));
        resultMap.put("totalBrokerage", list.get(0).get("totalBrokerage"));
        resultMap.put("totalIncomeOne", list.get(0).get("totalIncomeOne"));
        r.add("orderId");
        r.add("tradeType");
        r.add("orderCash");
        r.add("createTime");
        r.add("endTime");
        r.add("payMoney");
        r.add("payType");
        r.add("orderType");
        r.add("memberIcon");
        r.add("realName");
        r.add("mobile");
        r.add("brokerageCount");
        r.add("sellerIcon");
        r.add("tradeType");
        r.add("fee");
        sql = "select" +
                " DISTINCT t1.orderId" +
                ",t1.tradeType" +
                ",t1.orderCash" +
                ",t2.createTime" +
                ",t2.endTime" +
                ",t2.payMoney" +
                ",t2.payType" +
                ",t2.orderType" +
                ",t3.icon memberIcon" +
                ",t3.realName" +
                ",t3.mobile" +
                ",t1.brokerageCount" +
                ",t4.icon as sellerIcon" +
                ",t1.tradeType" +
                ",t5.fee" +
                " from SellerMoneyLog t1" +
                " left join OrderInfo t2 on t1.orderId=t2.orderNo" +
                " left join Member t3 on t1.tradeId=t3._id" +
                " left join Seller t4 on t1.sellerId=t4._id" +
                " left join WithdrawLog t5 on t2.orderNo = t5.orderNo" +
                where+" order by t2.endTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        resultMap.put("items", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }


    /**
     * 分类查询
     *
     * @throws Exception
     */
    @GET
    @Path("/queryType")
    public void queryType() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("name");
        r.add("value");
        r.add("pvalue");
        r.add("img");
        String sql = "select" +
                " name" +
                ",value" +
                ",pvalue" +
                ",img" +
                " from OperateType" +
                " where isNav=true order by memberSerialNum";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 商城分类查询
     *
     * @throws Exception
     */
    @GET
    @Path("/queryTypeForMall")
    public void queryTypeForMall() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("name");
        r.add("value");
        r.add("pid");
        r.add("_id");
        r.add("pvalue");
        r.add("mallImg");
        r.add("cMallImg");
        r.add("bgColor");
        r.add("level");
        String sql = "select" +
                " name" +
                ",pid" +
                ",_id" +
                ",value" +
                ",pvalue" +
                ",mallImg" +
                ",cMallImg" +
                ",bgColor" +
                ",level" +
                " from OperateType" +
                " where mallIsNav=true order by mallSerialNum";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 商城分类商品查询
     *
     * @throws Exception
     */
        @GET
        @Path("/queryCommodityList")
        public void queryCommodityList() throws Exception {
            String value = ControllerContext.getPString("selectType");
            String areaValue = ControllerContext.getPString("selectType");
            List<Object> p = new ArrayList<>();
            List<String> r = new ArrayList<>();
            p.add(value + "%");
            r.add("_id");
            r.add("name");
            r.add("icon");
            r.add("tag");
            String sql = "select" +
                    " _id" +
                    ",name" +
                    ",icon" +
                    ",tag" +
                    " from ProductInfo" +
                    " where operateValue like ? order by createTime limit 0,9";
            List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    /**
     * 平台管理:根据ID获取商家信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getSellerInfoById")
    public void getSellerInfoById() throws Exception {
        toResult(Response.Status.OK.getStatusCode(), getSellerInfoById(ControllerContext.getPString("sellerId")));
    }

    public Map<String,Object> getSellerInfoById(String sellerId) throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        if (StringUtils.isEmpty(sellerId)) {
            throw new UserOperateException(400, "找不到该用户");
        }

        Map<String,Object> re = MysqlDaoImpl.getInstance().findById2Map("Seller", sellerId, new String[]{"cashPassword"}, Dao.FieldStrategy.Exclude);
        return re;
    }

    /**
     * 平台管理:商户审核管理
     */
    @GET
    @Seller
    @Path("/queryAllSeller")
    public void queryAllSeller() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }
        String agentId=String.valueOf(other.get("agentId"));

        //查询代理商的地址value
        List<Object> params = new ArrayList();

        Map<String,Object> reAgent=MysqlDaoImpl.getInstance().findById2Map("Agent",agentId,new String[]{"areaValue"}, Dao.FieldStrategy.Include);
        String agentValue=String.valueOf(reAgent.get("areaValue"));
        params.clear();

        String sellerName = ControllerContext.getPString("name");
        String status = ControllerContext.getPString("status");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        Long indexNum = 0l;
        if (pageNo!=1) {
            indexNum = (pageNo-1) * pageSize;
        }

        String whereStr = " where applyTime is not null and areaValue like ?";
        params.add(agentValue.replace("_", "\\_") + "%");
        if (StringUtils.isNotEmpty(sellerName)) {
            whereStr += " and name like ?";
            params.add("%" + sellerName + "%");
        }
        if (StringUtils.isNotEmpty(status)) {
            if ("1".equals(status)) {
                whereStr += " and canUse=true";
            } else if ("2".equals(status)) {
                whereStr += " and canUse=false";
            }
        }

        List<String> returnFields = new ArrayList<>();
        returnFields.add("totalCount");
        String hql = "select count(t1._id) as totalCount" +
                " from Seller t1";
        hql += whereStr + " order by t1.applyTime desc";
        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalCount", totalNum);
        resultMap.put("totalPage", totalPage);

        returnFields.clear();
        returnFields.add("_id");
        returnFields.add("name");
        returnFields.add("phone");
        returnFields.add("contactPerson");
        returnFields.add("canUse");
        returnFields.add("area");
        returnFields.add("applyTime");

        String sql = "select" +
                " _id" +
                ",name" +
                ",phone" +
                ",contactPerson" +
                ",canUse" +
                ",area" +
                ",applyTime" +
                " from Seller " +
                whereStr +
                " order by applyTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> sellerList = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("sellerList", sellerList);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 平台管理:商户管理
     */
    @GET
    @Seller
    @Path("/queryCanUseSeller")
    public void queryCanUseSeller() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String sellerName = ControllerContext.getContext().getPString("name");
        String sellerNo = ControllerContext.getContext().getPString("sellerNo");
        String sellerId = ControllerContext.getContext().getPString("sellerId");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long indexNum = 0l;
        if (pageNo != 1) {
            indexNum = pageSize * (pageNo - 1);
        }
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String agentId = msg.getContent().get("_id").toString();
        String areaValue = msg.getContent().get("areaValue").toString();
        String l = cache.getCache("agent_level_cache_" + agentId);
        List<String> p = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        Map<String, Object> pm = new HashMap<>();
        if (!"1".equals(l)) {
            pm.put("canUse", true);
            pm.put("___like_areaValue", areaValue);
            if (StringUtils.isNotEmpty(sellerName)) {
                pm.put("___like_name", sellerName);
            }
            if (StringUtils.isNotEmpty(sellerId)) {
                pm.put("___like__id", sellerId);
            }
            if (StringUtils.isNotEmpty(sellerNo)) {
                pm.put("___like_sellerNo", sellerNo);
            }
            Long totalCount = MysqlDaoImpl.getInstance().findCount("Seller", pm);
            Long totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalCount", totalCount);
            resultMap.put("totalPage", totalPage);
            p.add("_id");
            p.add("name");
            p.add("canUse");
            p.add("sellerNo");
            p.add("area");
            p.add("areaPValue");
            p.add("createTime");
            String whereStr = " where 1=1 and canUse=true and areaValue like ?";
            params.add(areaValue.replace("_", "\\_")+ "%");
            if (StringUtils.isNotEmpty(sellerName)) {
                whereStr += " and name like '%" + sellerName + "%'";
            }
            if (StringUtils.isNotEmpty(sellerId)) {
                whereStr += " and _id like '%" + sellerId + "%'";
            }
            if (StringUtils.isNotEmpty(sellerNo)) {
                whereStr += " and sellerNo like '%" + sellerNo + "%'";
            }
            String sql = "select _id,name,canUse,sellerNo,area,areaPValue,createTime from Seller " + whereStr + " order by createTime desc limit " + indexNum + "," + pageSize;
            List<Map<String, Object>> sellerList = MysqlDaoImpl.getInstance().queryBySql(sql, p, params);
            resultMap.put("sellerList", sellerList);
            toResult(Response.Status.OK.getStatusCode(), resultMap);
        } else {
            pm.put("canUse", true);
            if (StringUtils.isNotEmpty(sellerName)) {
                pm.put("___like_name", sellerName);
            }
            if (StringUtils.isNotEmpty(sellerId)) {
                pm.put("___like__id", sellerId);
            }
            if (StringUtils.isNotEmpty(sellerNo)) {
                pm.put("___like_sellerNo", sellerNo);
            }
            Long totalCount = MysqlDaoImpl.getInstance().findCount("Seller", pm);
            Long totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalCount", totalCount);
            resultMap.put("totalPage", totalPage);
            p.add("_id");
            p.add("address");
            p.add("name");
            p.add("canUse");
            p.add("sellerNo");
            p.add("area");
            p.add("areaPValue");
            p.add("createTime");
            String whereStr = " where 1=1 and canUse=true";
            if (StringUtils.isNotEmpty(sellerName)) {
                whereStr += " and name like '%" + sellerName + "%'";
            }
            if (StringUtils.isNotEmpty(sellerId)) {
                whereStr += " and _id like '%" + sellerId + "%'";
            }
            if (StringUtils.isNotEmpty(sellerNo)) {
                whereStr += " and sellerNo like '%" + sellerNo + "%'";
            }
            String sql = "select _id,address,name,canUse,sellerNo,area,areaPValue,createTime from Seller " + whereStr + " order by createTime desc limit " + indexNum + "," + pageSize;
            List<Map<String, Object>> sellerList = MysqlDaoImpl.getInstance().queryBySql(sql, p, params);
            resultMap.put("sellerList", sellerList);
            toResult(Response.Status.OK.getStatusCode(), resultMap);
        }
    }

    /**
     * 平台管理:商品管理
     */
    @GET
    @Seller
    @Path("/queryAllProduct")
    public void queryAllProduct() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String sellerName = ControllerContext.getContext().getPString("sellerName");
        String productName = ControllerContext.getContext().getPString("productName");
        String status = ControllerContext.getContext().getPString("status");
        String count = ControllerContext.getContext().getPString("productStock");
        String productStatus = ControllerContext.getContext().getPString("productAudit");
        String isAct = ControllerContext.getContext().getPString("isAct");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long indexNum = 0l;
        if (pageNo != 1) {
            indexNum = pageSize * (pageNo - 1);
        }
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String l = cache.getCache("agent_level_cache_" + msg.getContent().get("_id"));
        if (!"1".equals(l)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "登陆用户不是平台管理员!");
        } else {
            List<String> p = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            String whereStr = " where 1=1";
            if (StringUtils.isNotEmpty(sellerName)) {
                whereStr += " and t2.name like '%" + sellerName + "%'";
            }
            if (StringUtils.isNotEmpty(productName)) {
                whereStr += " and t1.name like '%" + productName + "%'";
            }
            if (StringUtils.isNotEmpty(count)) {
                whereStr += " and t1.stockCount=" + Long.valueOf(count) + "";
            }
            if (StringUtils.isNotEmpty(productStatus)) {
                if ("0".equals(productStatus)) {

                } else if ("1".equals(productStatus)) {

                } else if ("2".equals(productStatus)) {
                    whereStr += " and t1.isDeploy=false";
                } else if ("3".equals(productStatus)) {
                    whereStr += " and t1.isDeploy=true";
                } else if ("4".equals(productStatus)) {
                    whereStr += " and t1.isDeploy=false";
                }
            }
            if (StringUtils.isNotEmpty(isAct)) {
                if ("0".equals(isAct)) {
                    whereStr += " and t1.isActivity=true";
                } else if ("1".equals(isAct)) {
                    whereStr += " and (t1.isActivity=false or t1.isActivity is null)";
                }
            }
            p.add("totalCount");
            String s = "select count(t1._id) as totalCount from ProductInfo t1 left join Seller t2 on t1.sellerId=t2._id " + whereStr + " order by t1.createTime desc ";
            List<Map<String, Object>> productCount = MysqlDaoImpl.getInstance().queryBySql(s, p, params);
            Long totalCount = (Long) productCount.get(0).get("totalCount");
            Long totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalCount", totalCount);
            resultMap.put("totalPage", totalPage);
            p = new ArrayList<>();
            p.add("t1._id");
            p.add("productName");
            p.add("isDeploy");
            p.add("stockCount");
            p.add("isActivity");
            p.add("createTime");
            p.add("sellerId");
            p.add("sellerName");
            String sql = "select t1._id,t1.name as productName,t1.isDeploy as isDeploy,t1.stockCount as stockCount,t1.isActivity as isActivity,t1.createTime as createTime,t2._id as sellerId,t2.name as sellerName from ProductInfo t1 left join Seller t2 on t1.sellerId=t2._id " + whereStr + " order by t1.createTime desc limit " + indexNum + "," + pageSize;
            List<Map<String, Object>> productList = MysqlDaoImpl.getInstance().queryBySql(sql, p, params);
            resultMap.put("productList", productList);
            toResult(Response.Status.OK.getStatusCode(), resultMap);
        }
    }

    /**
     * 删除商户
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/deleteSeller")
    public void deleteSeller() throws Exception {
        String id = ControllerContext.getPString("id");
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String l = cache.getCache("agent_level_cache_" + msg.getContent().get("_id"));
        if (!"1".equals(l)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "登陆用户不是平台管理员!");
        } else {
            MysqlDaoImpl.getInstance().remove("Seller", id);
            toResult(Response.Status.OK.getStatusCode(), 200);
        }
    }
    /**
     * 代理商查看商户信息
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/agentQuerySellerInfo")
    public void agentQuerySellerInfo() throws Exception {
        String sellerId = ControllerContext.getPString("sellerId");
        Map<String,Object> map = new HashMap<>();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(sellerId);
        r.add("_id");
        r.add("icon");
        r.add("sellerName");
        r.add("operateType");
        r.add("openTime");
        r.add("closeTime");
        r.add("phone");
        r.add("agentName");
        r.add("area");
        r.add("address");
        r.add("integralRate");
        r.add("bankId");
        r.add("bankName");
        r.add("bankUser");
        r.add("bankUserPhone");
        String sql = "select" +
                " t1._id" +
                ",t1.icon" +
                ",t1.name as sellerName" +
                ",t1.operateType" +
                ",t1.openTime" +
                ",t1.closeTime" +
                ",t1.phone" +
                ",t2.name as agentName" +
                ",t1.area" +
                ",t1.address" +
                ",t1.integralRate" +
                ",t1.bankId" +
                ",t1.bankName" +
                ",t1.bankUser" +
                ",t1.bankUserPhone" +
                " from Seller t1" +
                " left join Agent t2 on t1.areaValue=t2.areaValue" +
                " where t1._id=?";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        map.put("sellerInfo",re);
        toResult(Response.Status.OK.getStatusCode(), map);
    }
    /**
     * 修改商家积分率
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/updateSellerIntegral")
    public void updateSellerIntegral() throws Exception {
        String sellerId = ControllerContext.getPString("sellerId");
        String sellerIntegral = ControllerContext.getPString("sellerIntegral");
        Map<String,Object> map = new HashMap<>();
        map.put("_id",sellerId);
        map.put("integralRate",sellerIntegral);
        MysqlDaoImpl.getInstance().saveOrUpdate("Seller",map);
        Map<String,Object> sellerTR = MysqlDaoImpl.getInstance().findById2Map("Seller",sellerId,new String[]{"integralRate"},Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), sellerTR);
    }


    /**
     * 生成经营范围数据
     *
     * @throws Exception
     */
    @GET
    @Path("/createOperate")
    public void createOperate() throws Exception {
        List<List<Map<String,Object>>> all = new ArrayList<>();
        for(int i=0;i<3;i++){
            String sql = "select " +
                    " pid" +
                    ",pvalue" +
                    ",value" +
                    ",_id" +
                    ",level" +
                    ",name" +
                    " from OperateType where level=?";

            List<Object> params = new ArrayList<>();
            params.add(i+1);

            List<String> returnFields = new ArrayList<>();
            returnFields.add("pid");
            returnFields.add("pvalue");
            returnFields.add("value");
            returnFields.add("_id");
            returnFields.add("level");
            returnFields.add("name");
            List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
            all.add(re);
        }

        for(int i=0;i<3;i++){
            for(int j=0,jlen=all.get(i).size();j<jlen;j++){
                //查找当前类别的下一级,并给value,pvalue赋值
                String sql = "select " +
                        " pid" +
                        ",pvalue" +
                        ",value" +
                        ",_id" +
                        ",level" +
                        ",name" +
                        " from OperateType where pid=?";
                List<Object> params = new ArrayList<>();
                params.add(all.get(i).get(j).get("_id"));

                List<String> returnFields = new ArrayList<>();
                returnFields.add("pid");
                returnFields.add("pvalue");
                returnFields.add("value");
                returnFields.add("_id");
                returnFields.add("level");
                returnFields.add("name");
                List<Map<String,Object>> reChild = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);

                for(int k=0,klen=reChild.size();k<klen;k++){
                    if(StringUtils.mapValueIsEmpty(all.get(i).get(j),"pvalue")){
                        reChild.get(k).put("pvalue","_"+all.get(i).get(j).get("value"));
                    }else{
                        String pvalueTemp=all.get(i).get(j).get("pvalue").toString();
                        if(all.get(i).get(j).get("pvalue")!=null && !"_".equals(all.get(i).get(j).get("pvalue").toString().substring(0,1))){
                            pvalueTemp="_"+all.get(i).get(j).get("pvalue").toString();
                        }
                        reChild.get(k).put("pvalue",pvalueTemp+"_"+all.get(i).get(j).get("value"));
                    }
                    reChild.get(k).put("value",k+1);
                    MysqlDaoImpl.getInstance().saveOrUpdate("OperateType",reChild.get(k));
                }
            }
        }
    }
    /**
     * 查询手机号是否当前登陆用户绑定的店铺的手机号
     *
     * @throws Exception
     */
    @GET
    @Path("/getMobileIsSeller")
    public void getMobileIsSeller() throws Exception {
        Map<String,Object> seller = getCurrentSeller();
        String forgotPhone = ControllerContext.getPString("phone");
        if(!forgotPhone.equals(seller.get("phone"))){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该手机号不是当前登陆用户绑定的手机号!");
        }
        toResult(Response.Status.OK.getStatusCode(), seller);
    }
    /**
     * 店铺支付密码找回
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/payForgotPassword")
    public void payForgotPassword() throws Exception {
        String sellerId = getCurrentSeller().get("_id").toString();
        String userId = ControllerContext.getContext().getCurrentUserId();
        String loginName = ControllerContext.getPString("loginName");
        String newPassword = ControllerContext.getPString("newPassword");
        String verification = ControllerContext.getPString("verification");
        if (StringUtils.isEmpty(newPassword) || !Pattern.matches("^[0-9]{6}$",newPassword)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码格式错误!");
        }
        if (StringUtils.isEmpty(loginName) || !Pattern.matches("^1[34578]{1}\\d{9}$", loginName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
        }
        if (StringUtils.isEmpty(verification)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "验证码不能为空!");
        }
        Map<String,Object> map = MysqlDaoImpl.getInstance().findById2Map("User",userId,new String[]{"password"},Dao.FieldStrategy.Include);

        if(MessageDigestUtils.digest(newPassword).equals(map.get("password"))){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登陆密码相同!");
        }
        Message msg = Message.newReqMessage("1:PUT@/common/Sms/checkSmsCode");
        msg.getContent().put("type", "change_password");
        msg.getContent().put("smsCode", verification);
        msg.getContent().put("phone", loginName);
        ServiceAccess.callService(msg);

        Map<String,Object> v = new HashMap<>();
        v.put("_id",sellerId);
        v.put("cashPassword",MessageDigestUtils.digest(newPassword));
        MysqlDaoImpl.getInstance().saveOrUpdate("Seller",v);

    }

    /**
     * 获取商品列表
     *
     * @throws Exception
     */
    @GET
    @Path("/getCommodityForType")
    public void getCommodityForType() throws Exception {
        String isTejia = ControllerContext.getPString("isTejia");
        String isGongyi = ControllerContext.getPString("isGongyi");
        String isRemen = ControllerContext.getPString("isRemen");
        String operate = ControllerContext.getPString("operate");
        String areaValue = ControllerContext.getPString("areaValue");
        String searchSOP = ControllerContext.getPString("searchSOP");
        int selectedIndex = ControllerContext.getPInteger("selectedIndex");
        String orderByType = ControllerContext.getPString("orderByType");


        Long indexNum = 0l;
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        if (pageNo != 1) {
            indexNum = (pageNo - 1) * pageSize;
        }
        String where = " where 1=1";
        String orderby = " order by t1.createTime";
        if(StringUtils.isNotEmpty(isTejia)){
            where += " and t1.te=true";
        }
        if(StringUtils.isNotEmpty(isGongyi)){
            where += " and t1.gongyi=true";
        }
        if(StringUtils.isNotEmpty(isRemen)){
            where += " and (t1.hot=true or t1.isIndexCommodity=true)";
        }
        if(StringUtils.isNotEmpty(operate)){
            where += " and t1.operateValue like ?";
            p.add(operate+"%");
        }
        if(StringUtils.isNotEmpty(areaValue)){
            where += " and t2.areaValue like ?";
            p.add(areaValue+"%");
        }
        if(StringUtils.isNotEmpty(searchSOP)){
            where += " and t1.name like ?";
            p.add("%"+searchSOP+"%");
        }
        String field = "";
        String groupStr = "";
        String leftJoinStr = "";

        if (selectedIndex == 0) {//积分率最高
            orderby = " order by t2.integralRate";
        } else if (selectedIndex == 1) {//距离最近

        } else if (selectedIndex == 2) {//推荐商家
            where += " and t2.isRecommend=?";
            p.add(true);
        } else if (selectedIndex == 3) {//销量
            orderby = " order by t1.saleCount";
        } else if (selectedIndex == 4) {//评分
            field += ",avg(t4.serviceStar) as countStar";
            leftJoinStr += " left join OrderInfo t3 on t2._id=t3.sellerId left join OrderComment t4 on t3._id=t4.orderId";
            groupStr += " group by t1._id";
            orderby = " order by countStar";
        }
        if(StringUtils.isNotEmpty(orderByType)){
            orderby+=" asc";
        }else{
            orderby+=" desc";
        }
        String hql = "select count(t1._id) as totalCount" +
                field+
                " from ProductInfo t1" +
                " left join Seller t2 on t1.sellerId=t2._id"+
                leftJoinStr +
                where + groupStr + orderby;
        r.add("totalCount");
        List<Map<String,Object>> commodityNum = MysqlDaoImpl.getInstance().queryBySql(hql,r,p);
        Long totalNum = 0l;
        if(selectedIndex == 4){
            totalNum =  Long.valueOf(commodityNum.size());
        }else{
            if(commodityNum.size()!=0){
                totalNum = (long) commodityNum.get(0).get("totalCount");
            }
        }

        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        String sql = "select" +
                " t1._id" +
                ",t1.icon" +
                ",t1.salePrice" +
                ",t1.oldPrice" +
                ",t1.name" +
                ",t1.saleCount" +
                ",t2.integralRate" +
                field +
                " from ProductInfo t1" +
                " left join Seller t2 on t1.sellerId=t2._id" +
                leftJoinStr +
                 where + groupStr + orderby +" limit "+indexNum +","+pageSize;
        r.clear();
        r.add("_id");
        r.add("icon");
        r.add("salePrice");
        r.add("oldPrice");
        r.add("name");
        r.add("saleCount");
        r.add("integralRate");
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        resultMap.put("items", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }
    /**
     *  检查当前登陆商户是否被禁用
     */
    @GET
    @Path("/getSellerIsTrue")
    public void getSellerIsTrue() throws Exception {
        String sellerId = ControllerContext.getPString("sellerId");
        Map<String,Object> seller = MysqlDaoImpl.getInstance().findById2Map("Seller",sellerId,new String[]{"canUse"},Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), seller);
    }
    /**
     *  商城获取选中的分类名字
     */
    @GET
    @Path("/getNavGoodsType")
    public void getNavGoodsType() throws Exception {
        String operateType = ControllerContext.getPString("operateType");
        String[] typeNum = operateType.substring(1,operateType.length()-1).split("_");
        List<String> typeName = new ArrayList<>();
        Map<String,Object> v = new HashMap<>();
        Map<String,Object> r = new HashMap<>();
        String id="";
        for(int i =0,len=typeNum.length; i<len ; i++){
            v.put("level",i+1);
            v.put("value",typeNum[i]);
            if(!StringUtils.isEmpty(id)){
                v.put("pid",id);
            }
            r = MysqlDaoImpl.getInstance().findOne2Map("OperateType",v,new String[] {"name","_id"},Dao.FieldStrategy.Include);
            typeName.add(r.get("name").toString());
            id=r.get("_id").toString();
            r.clear();
        }
        toResult(Response.Status.OK.getStatusCode(), typeName);
    }
    /**
     * 商品类型管理:获取商品列表
     */
    @GET
    @Path("/getCommodityTypeList")
    public void getCommodityTypeList() throws  Exception {

        String createTime = ControllerContext.getPString("_createTime");
        String commodityName = ControllerContext.getPString("_commodityName");
        String sellerName = ControllerContext.getPString("_sellerName");
        String commodityType = ControllerContext.getPString("_commodityType");
        String operateType = ControllerContext.getPString("_operateType");
        String areaValue = ControllerContext.getPString("_areaValue");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }


        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0 || !"1".equals(agentInfo.get("level"))) {
            throw new UserOperateException(400, "你无此操作权限");
        }

        String where = " where 1=1 ";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        if (StringUtils.isNotEmpty(commodityName)) {
            where += " and t1.name like ?";
            p.add("%"+commodityName+"%");
        }
        if (StringUtils.isNotEmpty(sellerName)) {
            where += " and t2.name like ?";
            p.add("%"+sellerName+"%");
        }
        if (StringUtils.isNotEmpty(operateType)) {
            where += " and t1.operateType like ?";
            p.add("%"+operateType+"%");
        }
        if (startTime != 0) {
            where += " and t1.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            p.add(endTime);
        }
        if(StringUtils.isNotEmpty(areaValue)){
            areaValue = areaValue.replaceAll("___like_", "");
            where += " and t2.belongAreaValue like ?";
            p.add(areaValue+"%");
        }
        if (StringUtils.isNotEmpty(commodityType)) {
            if ("gongyi".equals(commodityType)) {
                where += " and t1.gongyi = true";
            } else if ("tejia".equals(commodityType)) {
                where += " and t1.te = true";
            } else if ("remen".equals(commodityType)) {
                where += " and t1.hot = true";
            } else {
                where += " and t1.isIndexCommodity = true";
            }
        }

        String hql = "select" +
                " count(t1._id) as totalCount" +
                " from ProductInfo t1" +
                " left join Seller t2 on t2._id=t1.sellerId" +
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
        r.add("sellerName");
        r.add("commodityName");
        r.add("operateType");
        r.add("te");
        r.add("hot");
        r.add("gongyi");
        r.add("isIndexCommodity");
        r.add("createTime");
        r.add("saleCount");
        String sql = "select" +
                " t1._id" +
                ",t2.name as sellerName" +
                ",t1.name as commodityName" +
                ",t1.operateType" +
                ",t1.te" +
                ",t1.hot" +
                ",t1.gongyi" +
                ",t1.isIndexCommodity" +
                ",t1.createTime" +
                ",t1.saleCount" +
                " from ProductInfo t1" +
                " left join Seller t2 on t2._id=t1.sellerId" +
                where + " order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 商户推荐管理:获取商户列表
     */
    @GET
    @Path("getSellerRecommendList")
    public void getSellerRecommendList() throws  Exception {
        //获取当前登录的代理商

        String id = ControllerContext.getPString("_id");
        String sellerName = ControllerContext.getPString("_sellerName");
        String recommendType = ControllerContext.getPString("_recommendType");
        String operateType = ControllerContext.getPString("_operateType");
        String areaValue = ControllerContext.getPString("_areaValue");


        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        String where = " where 1=1 ";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        if (StringUtils.isNotEmpty(sellerName)) {
            where += " and name like ?";
            p.add("%"+sellerName+"%");
        }
        if (StringUtils.isNotEmpty(operateType)) {
            where += " and operateType like ?";
            p.add("%"+operateType+"%");
        }
        if (StringUtils.isNotEmpty(id)) {
            where += " and _id like ?";
            p.add("%"+id+"%");
        }
        if(StringUtils.isNotEmpty(areaValue)){
            areaValue = areaValue.replaceAll("___like_", "");
            where += " and belongAreaValue like ?";
            p.add(areaValue+"%");
        }
        if (StringUtils.isNotEmpty(recommendType)) {
            if ("foodCourt".equals(recommendType)) {
                where += " and foodCourt = true";
            } else if ("isOfflineBalance".equals(recommendType)) {
                where += " and isOfflineBalance = true";
            } else {
                where += " and isRecommend = true";
            }
        }
        String hql = "select" +
                " count(_id) as totalCount" +
                " from Seller" +
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
        r.add("name");
        r.add("operateType");
        r.add("isRecommend");
        r.add("foodCourt");
        r.add("isOfflineBalance");
        String sql = "select" +
                " _id" +
                ",name" +
                ",operateType" +
                ",isRecommend" +
                ",foodCourt" +
                ",isOfflineBalance" +
                " from Seller" +
                where + " order by createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);

    }
    /**
     * 商户推荐管理:修改商户推荐
     */
    @GET
    @Path("changeSellerRecommend")
    public void changeSellerRecommend() throws  Exception {
        String sellerId = ControllerContext.getPString("sellerId");
        String recommendType = ControllerContext.getPString("recommendTypeType");
        String status = ControllerContext.getPString("status");

        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "您无此操作权限");
        }
        //只有管理员有权限修改余额支付权限
        if ("isOfflineBalance".equals(recommendType)){
            if (!"1".equals(agentInfo.get("level").toString())) {
                throw new UserOperateException(400, "您无此操作权限");
            }
        }
//        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
//        String level = cache.getCache("agent_level_cache_" + other.getString("agentId"));
//        if (!"1".equals(level)) {
//            throw new UserOperateException(400, "无权限");
//        }

        Map<String,Object> seller = MysqlDaoImpl.getInstance().findById2Map("Seller",sellerId,new String[]{"belongAreaValue"},Dao.FieldStrategy.Include);
        if(seller == null || seller.size() ==0){
            throw new UserOperateException(400, "获取商家数据失败!");
        }

        if (!Pattern.matches("^(" + agentInfo.get("areaValue").toString() + "\\S*)$", seller.get("belongAreaValue").toString())) {
            throw new UserOperateException(400, "只能选择您旗下的商家!");
        }

        Map<String,Object> v = new HashMap<>();
        List<String> r = new ArrayList<>();
        r.add("_id");
        v.put("_id",sellerId);
        if(StringUtils.isEmpty(status)||"false".equals(status)||"null".equals(status)){
            if("isRecommend".equals(recommendType)){
                String sql = "select _id from Seller where isRecommend=true";
                List<Map<String,Object>> pCount = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
//                if(pCount.size()>=4){
//                    throw new UserOperateException(400, "首页推荐商家最多只能设置4个!");
//                }
            }else if("foodCourt".equals(recommendType)){
                String sql = "select _id from Seller where foodCourt=true";
                List<Map<String,Object>> pCount = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
//                if(pCount.size()>=3){
//                    throw new UserOperateException(400, "美食广场只能设置3个!");
//                }
            }
            v.put(recommendType,true);
        }else{
            v.put(recommendType,false);
        }

        MysqlDaoImpl.getInstance().saveOrUpdate("Seller",v);
    }
    /**
     * 商户推荐管理:获取商户列表
     */
    @GET
    @Path("/getFoodCourList")
    public void getFoodCourList() throws  Exception {
        String lat = ControllerContext.getPString("lat");
        String lng = ControllerContext.getPString("lng");

        String field= "";
        String orderStr = "";
        String limit = "";
        List<String> r = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if(StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)){
            params.add(lat);
            params.add(lat);
            params.add(lng);
            r.add("distance");
            field = ",6371 * 2 * ASIN(SQRT(POWER(SIN((? - abs(latitude)) * pi()/180 / 2),2) + COS(? * pi()/180 ) * COS(abs(latitude) * pi()/180) * POWER(SIN((? - longitude) * pi()/180 / 2), 2) )) AS distance";
            orderStr = " order by distance is null,distance asc";
            limit = " limit 0,3";
        }

        r.add("_id");
        r.add("name");
        r.add("icon");
        r.add("doorImg");
        String sql = "select" +
                " _id" +
                ",name" +
                ",icon" +
                ",doorImg" +
                field+
                " from Seller" +
                " where foodCourt=true" +
                " and operateValue like '\\_1\\_%'" +
                ""+orderStr+limit;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,params);

        //若没有坐标，则随机分配3个商家
        if(StringUtils.isEmpty(lat) && StringUtils.isEmpty(lng)){
            int max = re.size();
            if(max>3){
                int nums[] = new int[3];
                List<Map<String,Object>> temp = new ArrayList<>();
                for(int i=0;i<3;i++){
                    int tempNum = (int)(Math.random()*max);
                    boolean isReset = false;
                    for(int j=0;j<=i;j++){
                        if(nums[j]==tempNum){
                            i--;
                            isReset=true;
                            break;
                        }
                    }
                    if(!isReset){
                        nums[i]=tempNum;
                        temp.add(re.get(tempNum));
                    }
                }
                re = temp;
            }
        }
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员版跳转商家版
     * @throws Exception
     */
    @Member
    @GET
    @Path("/memberToSeller")
    public void memberToSeller() throws Exception{
        String memberId = ControllerContext.getContext().getCurrentUserId();
        System.out.println("memberId=="+memberId);
        Map<String,Object> params = new HashMap<>();
        params.put("memberId",memberId);
        Map<String,Object> user = MysqlDaoImpl.getInstance().findOne2Map("User",params,new String[]{"sellerId","password"},Dao.FieldStrategy.Include);
        if(user==null || user.size()==0 || StringUtils.mapValueIsEmpty(user,"sellerId") || StringUtils.mapValueIsEmpty(user,"password")){
            throw new UserOperateException(500,"您暂未绑定商家");
        }
        Map<String,Object> re = new EncryptionAction().encrytor(user.toString());
        re.put("deviceId",ZQUidUtils.genUUID());
        re.put("userType","seller");
        toResult(200,re);
    }

    /**
     * 商家版跳转会员版
     * @throws Exception
     */
    @Seller
    @GET
    @Path("/sellerToMember")
    public void sellerToMember() throws Exception{
        String sellerId = ControllerContext.getContext().getCurrentSellerId();

        Map<String,Object> params = new HashMap<>();
        params.put("sellerId",sellerId);
        Map<String,Object> user = MysqlDaoImpl.getInstance().findOne2Map("User",params,new String[]{"memberId"},Dao.FieldStrategy.Include);
        if(user==null || user.size()==0 || StringUtils.mapValueIsEmpty(user,"memberId")){
            throw new UserOperateException(500,"您暂未绑定会员");
        }

        String memberId = user.get("memberId").toString();
        JSONObject member = ServiceAccess.getRemoveEntity("crm","Member",memberId);
        if(member==null || member.size()==0){
            throw new UserOperateException(500,"您尚未绑定会员");
        }
        user.put("password",member.get("password"));

        Map<String,Object> re = new EncryptionAction().encrytor(user.toString());
        re.put("deviceId",ZQUidUtils.genUUID());
        re.put("userType","member");
        toResult(200,re);
    }

    public static void main(String[] str) throws  Exception{
        String x = "12345678912345";
        String y = "22345678912345";
        Long i = Long.valueOf(x);
        Long j = Long.valueOf(y);
        Long z = i+j;
        System.out.print(z);

        System.out.println(MessageDigestUtils.digest("123123"));
    }

    @GET
    @Path("/sellerSaveOrUpdate")
    public void sellerSaveOrUpdate() throws Exception{
        Map<String,Map<String,Object>> map = ControllerContext.getContext().getReq().getContent();
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName,map.get("map"));
    }

    @POST
    @Path("/findSellerbyName")
    public void findSellerbyName() throws Exception{
        String bankName = ControllerContext.getPString("name");
        List<String> returnField = new ArrayList<>();
        returnField.add("_id");
        List<Object> params = new ArrayList<>();
        params.add(bankName);
        String sql="SELECT _id FROM seller WHERE name=?";
        List<Map<String, Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        Map<String,Object> map = new HashMap<>();
        if(!seller.isEmpty() && seller.size()!=0){
            map.putAll(seller.get(0));
        }
        toResult(200,map);
    }
}
