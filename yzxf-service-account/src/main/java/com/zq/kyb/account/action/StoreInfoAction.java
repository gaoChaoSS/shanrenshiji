package com.zq.kyb.account.action;

import com.zq.kyb.core.annotation.Lock;
import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import net.sf.json.JSONObject;
import com.zq.kyb.util.StringUtils;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.*;

public class StoreInfoAction extends BaseActionImpl {

    @Override
    //@Seller
    @Member
    public void query() throws Exception {
        super.query();
    }

    /**
     * @throws Exception
     */
    @PUT
    @Path("/save")
    @Override
    public void save() throws Exception {
        throw new UserOperateException(500,"禁用");
    }

    /**
     * 首页展示商家信息(附近商家).
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/queryStore")
    public void queryStore() throws Exception {
        List<String> result = new ArrayList<>();
        result.add("_id");
        result.add("bankId");
        result.add("closeTime");
        result.add("openTime");
        result.add("operateType");
        result.add("name");
        result.add("address");
        result.add("phone");
        result.add("sellerNo");
        result.add("icon");
        String sql = "select _id,bankId,closeTime,openTime,operateType,name,address,phone,sellerNo,icon from Seller where canUse=true order by createTime desc limit 0,4";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, result, null);
        toResult(Response.Status.OK.getStatusCode(), re);

    }

    /**
     * 首页展示商家信息(推荐商家).
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/queryStoreList")
    public void queryStoreList() throws Exception {
        String areaValue = ControllerContext.getPString("areaValue");
        String lat = ControllerContext.getPString("lat");
        String lng = ControllerContext.getPString("lng");

        List<String> result = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        String field="";
        String orderStr="";
        String where = " where 1=1 and canUse=true and isRecommend=true";
        if(StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)){
            p.add(lat);
            p.add(lat);
            p.add(lng);
            result.add("distance");
            field = ",6371 * 2 * ASIN(SQRT(POWER(SIN((? - abs(latitude)) * pi()/180 / 2),2) + COS(? * pi()/180 ) * COS(abs(latitude) * pi()/180) * POWER(SIN((? - longitude) * pi()/180 / 2), 2) )) AS distance";
            orderStr = " order by distance is null,distance asc";
        }
        if(StringUtils.isNotEmpty(areaValue)){
            where += " and areaValue like ?";
            p.add(areaValue+"%");
        }
        result.add("_id");
        result.add("bankId");
        result.add("closeTime");
        result.add("openTime");
        result.add("operateType");
        result.add("name");
        result.add("desc");
        result.add("address");
        result.add("phone");
        result.add("sellerNo");
        result.add("icon");
        result.add("doorImg");
        String sql = "select" +
                " _id,bankId" +
                ",closeTime" +
                ",openTime" +
                ",operateType" +
                ",name" +
                ",`desc`" +
                ",address" +
                ",phone" +
                ",sellerNo" +
                ",icon" +
                ",doorImg" +
                field+
                " from Seller" +
                where+
                orderStr+
                " limit 0,4";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, result, p);
        toResult(Response.Status.OK.getStatusCode(), re);

    }


    /**
     * 首页搜索
     **/
    @GET
    @Member
    @Path("/queryKeyword")
    public void queryKeyword() throws Exception {
        String keyword = ControllerContext.getPString("keyword");
        Integer indexNum = ControllerContext.getPInteger("indexNum");
        Integer pageNo = ControllerContext.getPInteger("pageNo");
        Integer pageSize = ControllerContext.getPInteger("pageSize");
        String type = ControllerContext.getPString("type");
        String hql = "";
        String sql = "";
        Map<String, Object> resultMap = new HashMap<>();
        List<String> result = new ArrayList<>();
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        result.add("_id");
        if ("all".equals(type) || "seller".equals(type)) {
            result.add("_id");
            result.add("bankId");
            result.add("closeTime");
            result.add("openTime");
            result.add("operateType");
            result.add("sellerNo");
            result.add("address");
            result.add("phone");
            result.add("doorImg");
        }
        result.add("name");
        result.add("icon");

        if (StringUtils.isNotEmpty(type)) {
            if ("all".equals(type)) {
                hql += "select _id ,bankId,closeTime,openTime,operateType,name,address,phone,sellerNo,icon,doorImg from Seller where canUse=true and  (name like'%" + keyword + "%' or type like '%" + keyword + "%') \n" +
                        "UNION \n" +
                        "select _id,'','','','',name,'','','',icon,'' from ProductInfo where name like '%" + keyword + "%'";
            } else if ("seller".equals(type)) {
                hql += "select _id,bankId,closeTime,openTime,operateType,name,address,phone,sellerNo,icon,doorImg from Seller where canUse=true and  (name like'%" + keyword + "%' or type like '%" + keyword + "%')";
            } else if ("product".equals(type)) {
                hql += "select _id,name,icon from ProductInfo where name like '%" + keyword + "%'";
            }
        }
        List<Map<String, Object>> totalList = MysqlDaoImpl.getInstance().queryBySql(hql, result, null);
        List<String> sellerList = new ArrayList<>();
        for (Map<String, Object> map : totalList) {
            String sellerNo = (String) map.get("_id");
            if ("S".equals(sellerNo.substring(0, 1))) {
                sellerList.add(sellerNo);
            }
        }
        int sellerNum = sellerList.size();
        int totalNum = totalList.size();
        int productNum = totalNum - sellerNum;
        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("totalPage", totalPage);
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("sellerNum", sellerNum);
        resultMap.put("productNum", productNum);
        if (StringUtils.isNotEmpty(type)) {
            if ("all".equals(type)) {
                sql += "select _id ,bankId,closeTime,openTime,operateType,name,address,phone,sellerNo,icon,doorImg from Seller where canUse=true and  (name like'%" + keyword + "%' or type like '%" + keyword + "%') \n" +
                        "UNION \n" +
                        "select _id,'','','','',name,'','','',icon,'' from ProductInfo where name like '%" + keyword + "%' limit " + indexNum + "," + pageSize;
            } else if ("seller".equals(type)) {
                sql += "select _id ,bankId,closeTime,openTime,operateType,name,address,phone,sellerNo,icon,doorImg from Seller where canUse=true and  (name like'%" + keyword + "%' or type like '%" + keyword + "%') limit " + indexNum + "," + pageSize;
            } else if ("product".equals(type)) {
                sql += "select _id,name,icon from ProductInfo where name like '%" + keyword + "%' limit " + indexNum + "," + pageSize;
            }
        }
        List<Map<String, Object>> resultList = MysqlDaoImpl.getInstance().queryBySql(sql, result, null);
        resultMap.put("resultList", resultList);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 查询当前登录用户的角色信息,如果是管理员就拥有所有店铺权限
     */
    static List<String> res = new ArrayList<>();

    static {
        res.add("_id");
        res.add("name");
        res.add("phone");
        res.add("address");
        res.add("isRun");
        res.add("runTime");
        res.add("sendTime");
        res.add("notSend");
        res.add("storeNo");
    }

    /**
     * 根据商户ID查询商户基本信息
     */
    @GET
    @Member
    @Path("/getSellerIntroById")
    public void getSellerIntroById() throws Exception {
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("_id", ControllerContext.getPString("sellerId"));
        Map<String, Object> re = dao.findOne2Map("Seller", p,
                new String[]{"bankId", "bankName", "bankUser", "cashPassword", "isCouponVerification", "isMoneyTransaction", "isSendCardLog"},
                Dao.FieldStrategy.Exclude);
        if (re == null) {
            throw new UserOperateException(400, "商家不存在");
        }
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 根据商户ID查询商户信息
     * 并获取会员是否收藏该商户
     */
    @GET
    @Member
    @Path("/getStoreInfoById")
    public void getStoreInfoById() throws Exception {
        List<String> returnFields = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        Map<String, Object> re = new HashMap<>();

        String sellerId = ControllerContext.getContext().getPString("sellerId");
        params.add(sellerId);
        String memberId = ControllerContext.getContext().getCurrentUserId();

        //会员是否收藏
        if (StringUtils.isNotEmpty(memberId)) {
            returnFields.add("_id");
            params.add(memberId);
            String sql = "select " +
                    " _id" +
                    " from MemberCollection" +
                    " where entityType='seller'" +
                    " and entityId=?" +
                    " and memberId=?";
            List<Map<String, Object>> collection = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            if (collection.size() > 0) {
                re.put("isCollection", true);
            } else {
                re.put("isCollection", false);
            }
        } else {
            re.put("isCollection", false);
        }

        returnFields.clear();
        params.clear();

        //查询该商店是否有卡券
        String sql = "select _id from Coupon where sellerId=? and endTime>?";
        params.add(sellerId);
        params.add(System.currentTimeMillis());
        returnFields.add("_id");
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (couponList.size() > 0) {
            re.put("isCoupon", true);
        } else {
            re.put("isCoupon", false);
        }

        //查询商户是否支持在线支付
        returnFields.clear();
        params.clear();
        sql = "select" +
                " _id" +
                ",name" +
                ",isOnlinePay" +
                ",integralRate" +
                ",icon" +
                ",doorImg" +
                ",intro" +
                ",area" +
                ",address" +
                " from Seller" +
                " where _id=?";
        params.add(sellerId);
        returnFields.add("_id");
        returnFields.add("name");
        returnFields.add("isOnlinePay");
        returnFields.add("integralRate");
        returnFields.add("icon");
        returnFields.add("doorImg");
        returnFields.add("intro");
        returnFields.add("area");
        returnFields.add("address");
        List<Map<String, Object>> sellerInfo = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if(sellerInfo!=null&&sellerInfo.size()!=0){
            re.put("sellerInfo", sellerInfo.get(0));
        }
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 根据商户ID查询商品
     */
    @GET
    @Member
    @Path("/getGoodsInfoById")
    public void getGoodsInfoById() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList<>();
        String sellerId = ControllerContext.getContext().getPString("sellerId");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        if (!StringUtils.isNotEmpty(sellerId)) {
            sellerId = ControllerContext.getContext().getCurrentSellerId();
        }
        params.add(sellerId);
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t1._id) as totalCount" +
                " from ProductInfo t1" +
                " left join OperateType t2 on t1.typeId=t2._id where t1.sellerId=?";
        List<Map<String, Object>> goodsList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) goodsList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("_id");
        returnFields.add("name");
        returnFields.add("icon");
        returnFields.add("originalPrice");
        returnFields.add("salePrice");
        returnFields.add("saleCount");
        returnFields.add("isSelf");
        returnFields.add("Description");
        returnFields.add("pensionMoney");
        returnFields.add("operateType");
        returnFields.add("operateValue");
        returnFields.add("oldPrice");
        returnFields.add("sellerId");
        returnFields.add("integralRate");

        String sql = "select" +
                " t1._id " +
                ",t1.name" +
                ",t1.icon" +
                ",t1.originalPrice" +
                ",t1.salePrice" +
                ",t1.saleCount" +
                ",t1.isSelf" +
                ",t1.`desc` as Description" +
                ",t1.pensionMoney" +
                ",t1.operateType" +
                ",t1.operateValue" +
                ",t1.oldPrice" +
                ",t1.sellerId" +
                ",t2.integralRate" +
                " from ProductInfo t1"+
                " left join Seller t2 on t1.sellerId=t2._id";
        String where = " where t1.sellerId=?";
        String groupBy = " ";
        String orderBy = " ";
        String limit = " limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, where, groupBy, orderBy, limit, returnFields, params);
        resultMap.put("goodsList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 查询店铺评论
     */
    @GET
    @Member
    @Path("/getStoreCommentList")
    public void getStoreCommentList() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getPString("sellerId"));
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<String> p = new ArrayList<String>();
        p.add("totalCount");
        String hql = "select count(t2._id) as totalCount" +
                " from Member t1" +
                " left join OrderComment t2 on t1._id = t2.memberId" +
                " where t2.sellerId=?";
        List<Map<String, Object>> commentList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) commentList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("mobile");
        returnFields.add("memberIcon");
        returnFields.add("storeComment");
        returnFields.add("score");
        returnFields.add("serviceStar");
        returnFields.add("createTime");
        returnFields.add("orderStatus");

        String sql = "select " +
                " t1.mobile" +
                ",t1.icon as memberIcon" +
                ",t2.name as storeComment" +
                ",t2.score" +
                ",t2.serviceStar" +
                ",t2.createTime" +
                ",t3.orderStatus" +
                " from Member t1" +
                " left join OrderComment t2 on t1._id = t2.memberId" +
                " left join OrderInfo t3 on t2.orderId=t3._id" +
                " where t2.sellerId=? " +
                " order by t2.createTime desc" +
                " limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("commentList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);

    }


    /**
     * 服务站获取商家
     */
    @GET
    @Path("/getBelongSeller")
    public void getBelongSeller() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "获取服务站信息失败");
        }
        String factorId = other.get("factorId").toString();

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        if(StringUtils.isEmpty(ControllerContext.getPString("pageNo"))){
            pageNo = 1;
        }
        if(StringUtils.isEmpty(ControllerContext.getPString("pageSize"))){
            pageSize = 20;
        }

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String from = " from Seller t1" +
                " left join Factor t2 on t1.belongAreaValue = t2.areaValue";
        String where = " where t2._id = ?";
        params.add(factorId);

        String sql = "select count(t1._id) as totalCount" +
                from+where;
        returnFields.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        returnFields.clear();
        returnFields.add("name");
        returnFields.add("icon");
//        returnFields.add("doorImg");
        returnFields.add("canUse");
        returnFields.add("createTime");
        returnFields.add("phone");
        returnFields.add("operateType");
        returnFields.add("integralRate");
        returnFields.add("contactPerson");
        returnFields.add("area");
        returnFields.add("address");

        sql = "select" +
                " t1.name" +
                ",ifNull(t1.doorImg,t1.icon) as icon" +
//                ",t1.icon" +
//                ",t1.doorImg" +
                ",t1.canUse" +
                ",t1.createTime" +
                ",t1.phone" +
                ",t1.operateType" +
                ",t1.integralRate" +
                ",t1.contactPerson" +
                ",t1.area" +
                ",t1.address" +
                from+where+
                " order by t1.createTime desc" +
                " limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        page.setItems(re);
        toResult(200,page);
    }
}


