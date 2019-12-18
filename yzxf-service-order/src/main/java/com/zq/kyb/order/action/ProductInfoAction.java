package com.zq.kyb.order.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.PatternUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.zq.kyb.core.model.Page;
import net.sf.json.JSONObject;

import javax.ws.rs.PUT;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by luoyunze on 16/12/22.
 */
public class ProductInfoAction extends BaseActionImpl {

    /**
     * 商品详情展示页面
     */
    @GET
    @Member
    @Path("/goodsInfoShow")
    public void goodsInfoShow() throws Exception {
//        List<Object> params = new ArrayList<>();
//        params.add(ControllerContext.getContext().getPString("_id"));
//        List<String> returnField = new ArrayList<>();
//        returnField.add("salesCount");
//        String sql = "select" +
//                " count(_id) as salesCount" +
//                " from OrderItem" +
//                " where productId=?";
//        List<Map<String, Object>> salesCount = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, params);
        Map<String, Object> info = MysqlDaoImpl.getInstance().findById2Map("ProductInfo", ControllerContext.getContext().getPString("_id"), null, null);
//        String sql1 = "select" +
//                " (t1.salePrice*t2.integralRate/100/2) as goodsPensionMoney" +
//                " from ProductInfo t1" +
//                " left join Seller t2 on t1.sellerId = t2._id" +
//                " where t1._id=?";
//        returnField.clear();
//        params.clear();
//        returnField.add("goodsPensionMoney");
//        params.add(ControllerContext.getContext().getPString("_id"));
//        List<Map<String,Object>> goodsPensionMoney = MysqlDaoImpl.getInstance().queryBySql(sql1,returnField,params);
//        Map<String, Object> re = new HashMap<>();
//        re.put("goodsInfo", info);
//        re.put("salesCount", salesCount.get(0).get("salesCount"));
//        re.put("goodsPensionMoney", goodsPensionMoney.get(0).get("goodsPensionMoney"));
        toResult(Response.Status.OK.getStatusCode(), info);
    }

    /**
     * 商品评价页面
     */
    @GET
    @Member
    @Path("/goodsCommentShow")
    public void goodsCommentShow() throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getPString("goodsId"));
        List<String> returnFields = new ArrayList<>();
        returnFields.add("name");
        returnFields.add("score");
        returnFields.add("createTime");
        returnFields.add("serviceStar");
        returnFields.add("mobile");
        returnFields.add("icon");
        String sql = "select" +
                " t1.name" +
                ",t1.score" +
                ",t1.createTime" +
                ",t1.serviceStar" +
                ",t2.mobile" +
                ",t2.icon" +
                " from OrderComment t1" +
                " left join Member t2 on t1.memberId = t2._id" +
                " where t1.goodsId = ?";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 首页商品展示
     */
    @GET
    @Member
    @Path("/queryProduct")
    public void queryProduct() throws Exception {
        String areaValue = ControllerContext.getContext().getPString("areaValue");
        List<String> returnField = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        String where = " where 1=1 and t1.te=true";
        if(StringUtils.isNotEmpty(areaValue)){
            where += " and t2.areaValue like ?";
            p.add(areaValue+"%");
        }

        returnField.add("_id");
        returnField.add("te");
        returnField.add("icon");
        returnField.add("tag");
        returnField.add("oldPrice");
        returnField.add("name");
        returnField.add("productNo");
        returnField.add("sellerName");
        returnField.add("salePrice");
        returnField.add("sellerId");
        String sql = "select" +
                " t1._id" +
                ",t1.te" +
                ",t1.icon" +
                ",t1.name" +
                ",t1.productNo" +
                ",t1.tag" +
                ",t1.oldPrice" +
                ",t1.salePrice" +
                ",t1.sellerId" +
                ", t2.`name` as sellerName" +
                " from ProductInfo as t1" +
                " left join Seller as t2 on t1.sellerId=t2._id" +
                where+" order by t1.createTime desc limit 0,8";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 保存商品
     */
    @POST
    @Seller
    @Path("/saveCommodity")
    public void saveCommodity() throws Exception {
        JSONObject values = ControllerContext.getContext().getReq().getContent();
        if(values.get("name").toString().length()>64){
            throw new UserOperateException(500, "商品名最多64位");
        }
//        if(values.get("tag").toString().length()>64){
//            throw new UserOperateException(500, "标签最多64位");
//        }
        if(!Pattern.matches("^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,2})?$",values.get("salePrice").toString()) || values.get("salePrice").toString().length()>64){
            throw new UserOperateException(500, "请输入正确的商品售价");
        }
        if(!Pattern.matches("^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,2})?$",values.get("oldPrice").toString()) || values.get("oldPrice").toString().length()>64){
            throw new UserOperateException(500, "请输入正确的商品售价");
        }
        if(!StringUtils.mapValueIsEmpty(values,"tag") && values.get("tag").toString().length()>20){
            throw new UserOperateException(500, "商品标签最多不超过20个字符长度");
        }
        if(values.get("spec")==null || StringUtils.isEmpty(values.get("spec").toString()) || ((JSONArray)values.get("spec")).size()==0){
            throw new UserOperateException(500, "请创建至少一条规格");
        }
        if(!StringUtils.mapValueIsEmpty(values,"desc") && values.get("desc").toString().length()>1024){
            throw new UserOperateException(500, "商品介绍最多不超过1024个字符长度");
        }

        //检查规格
        JSONArray specArray = ((JSONArray)values.get("spec"));
        JSONObject specItem;
        JSONArray item;
        JSONArray addMoney;
        for(int i=0,len=specArray.size();i<len;i++){
            specItem = (JSONObject) specArray.get(i);
            if(StringUtils.mapValueIsEmpty(specItem,"name")){
                throw new UserOperateException(500, "请为第"+(i+1)+"条规格命名");
            }
            item = (JSONArray) specItem.get("items");
            addMoney = (JSONArray) specItem.get("addMoney");
            if(item.size() != addMoney.size()){
                throw new UserOperateException(500, "规格的种类与加价无法一一对应");
            }
            for(int j=0,jlen = ((JSONArray) specItem.get("items")).size();j<jlen;j++){
                if(StringUtils.isEmpty(((JSONArray) specItem.get("items")).get(j).toString())){
                    throw new UserOperateException(500, "规格第"+(j+1)+"的种类未命名");
                }
            }
            for(int j=0,jlen = ((JSONArray) specItem.get("addMoney")).size();j<jlen;j++){
                if(StringUtils.isEmpty(((JSONArray) specItem.get("addMoney")).get(j).toString())){
                    ((JSONArray) specItem.get("addMoney")).set(j,0);
                }
            }
        }

        String _id = ControllerContext.getPString("_id");

        if (StringUtils.isEmpty(_id)) {
            _id = UUID.randomUUID().toString();
        }
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Map<String, Object> product = MysqlDaoImpl.getInstance().findById2Map("ProductInfo", _id, null, null);
        if(product==null || product.size()==0 || StringUtils.mapValueIsEmpty(product,"saleCount")){
            product = new HashMap<>();
            product.put("saleCount", (int)(Math.random()*200));
        }
        product.putAll(values);
        product.put("_id", _id);
        product.put("sellerId", sellerId);
        product.put("createTime", System.currentTimeMillis());

        MysqlDaoImpl.getInstance().saveOrUpdate("ProductInfo", product);
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("ProductInfo", _id, null, null);
        if (re == null) {
            throw new UserOperateException(500, "保存失败");
        }
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 商品展示
     */
    @GET
    @Seller
    @Member
    @Path("/queryCommoditys")
    public void queryCommoditys() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (com.zq.kyb.util.StringUtils.mapValueIsEmpty(other, "sellerId")) {
            throw new UserOperateException(400, "获取商家数据失败");
        }
        String sellerId = other.get("sellerId").toString();

        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        long count = MysqlDaoImpl.getInstance().findCount("ProductInfo", params);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("count", count);
        List<Object> p = new ArrayList<>();
        p.add(sellerId);
        List<String> returnField = new ArrayList<>();
        returnField.add("_id");
        returnField.add("te");
        returnField.add("icon");
        returnField.add("name");
        returnField.add("productNo");
        returnField.add("salePrice");
        returnField.add("saleCount");
        String sql = "select" + " _id,te,icon,name,productNo,salePrice,saleCount from ProductInfo where sellerId=? order by createTime desc" + " LIMIT " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, p);
        resultMap.put("productList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 某个商品展示
     */
    @GET
    @Member
    @Seller
    @Path("/queryCommodity")
    public void queryCommodity() throws Exception {
        String _id = ControllerContext.getContext().getPString("_id");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("ProductInfo", _id, null, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 删除商品
     */
    @POST
    @Seller
    @Path("/delCommodity")
    public void delCommodity() throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject values = ControllerContext.getContext().getReq().getContent();
        String _id = values.getString("_id");
        dao.remove("ProductInfo", _id);
        toResult(Response.Status.OK.getStatusCode(), true);
    }

//    /**
//     * 更新商品销量
//     */
//    @GET
//    @Member
//    @Path("/updateSaleCount")
//    public void updateSaleCount() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("te", true);
//        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().findAll2Map("ProductInfo", params, null, null, null);
//        for (Map<String, Object> map : list) {
//            String productId = (String) map.get("_id");
//            Map<String, Object> p = new HashMap<>();
//            p.put("productId", productId);
//            Long saleCount = MysqlDaoImpl.getInstance().findCount("OrderItem", p);
//            map.put("saleCount", new Long(saleCount).intValue());
//            MysqlDaoImpl.getInstance().saveOrUpdate("ProductInfo", map);
//        }
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("status", 400);
//        toResult(Response.Status.OK.getStatusCode(), resultMap);
//    }

    /**
     * 天天特价 / 公益专区
     */
    @GET
    @Path("/querySpecial")
    public void querySpecial() throws Exception {
        Integer indexNum = ControllerContext.getPInteger("indexNum");
        Integer pageSize = ControllerContext.getPInteger("pageSize");
        Integer pageNo = ControllerContext.getPInteger("pageNo");
        Boolean saleCountMore = ControllerContext.getPBoolean("saleCountMore");
        String operateValue = ControllerContext.getPString("operate");
        String type = ControllerContext.getPString("type");

        List<Object> params2 = new ArrayList<>();
        List<String> returnFields = new ArrayList<String>();

        String orderBy = "";
        String where = " where 1=1";

        if(StringUtils.isEmpty(type)){
            where+=" and t1.te=true";
        }else{
            where+=" and t1.gongyi=true";
        }

        if (saleCountMore != null && saleCountMore) {
            orderBy=" order by t1.saleCount asc,t1.createTime asc";
        } else {
            orderBy=" order by t1.saleCount desc,t1.createTime desc";
        }

        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }

        if (StringUtils.isNotEmpty(operateValue)) {
            where+=" and t1.operateValue like ?";
            params2.add(operateValue+"%");
        }

        returnFields.add("totalNum");
        String sql = "select count(t1._id) as totalNum from ProductInfo t1"+
                " left join Seller t2 on t1.sellerId=t2._id" + where;

        int totalNum = Integer.parseInt(MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params2).get(0).get("totalNum").toString());
        long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        Map<String, Object> page = new HashMap<>();
        page.put("pageNo", pageNo);
        page.put("pageSize", pageSize);
        page.put("totalNum", totalNum);
        page.put("totalPage", totalPage);

        returnFields.clear();
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
        returnFields.add("tag");
        returnFields.add("sellerId");
        returnFields.add("integralRate");

        sql = "select" +
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
                ",t1.tag" +
                ",t1.sellerId" +
                ",t2.integralRate" +
                " from ProductInfo t1"+
                " left join Seller t2 on t1.sellerId=t2._id" +
                where +
                orderBy +
                " limit "+indexNum+","+pageSize;

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params2);
        page.put("items", re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

//    /**
//     * 公益专区商品展示
//     */
//    @GET
//    @Member
//    @Path("/queryGongyi")
//    public void queryGongyi() throws Exception {
//        Integer indexNum = ControllerContext.getPInteger("indexNum");
//        Integer pageSize = ControllerContext.getPInteger("pageSize");
//        Integer pageNo = ControllerContext.getPInteger("pageNo");
//        Boolean saleCountMore = ControllerContext.getPBoolean("saleCountMore");
//        String operateValue = ControllerContext.getPString("operate");
//        Map<String, Object> params = new HashMap<>();
//        params.put("gongyi", true);
//        JSONObject orderby = new JSONObject();
//        if (saleCountMore != null && saleCountMore) {
//            orderby.put("saleCount", -1);
//            orderby.put("createTime", -1);
//        } else {
//            orderby.put("saleCount", 1);
//            orderby.put("createTime", 1);
//        }
//        if (indexNum != null && indexNum > 0) {
//            indexNum = indexNum * pageSize;
//        }
//        if (StringUtils.isNotEmpty(operateValue)) {
//            params.put("operateValue", operateValue);
//        }
//        int totalNum = (int) MysqlDaoImpl.getInstance().findCount("ProductInfo", params);
//        long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
//        Map<String, Object> page = new HashMap<>();
//        page.put("pageNo", pageNo);
//        page.put("pageSize", pageSize);
//        page.put("totalNum", totalNum);
//        page.put("totalPage", totalPage);
//        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().findPage2Map("ProductInfo", indexNum, pageSize, params, orderby, null, null);
//        page.put("specialList", re);
//        toResult(Response.Status.OK.getStatusCode(), page);
//    }

    /**
     * 首页:热门商品展示
     */
    @GET
    @Member
    @Path("/queryIndexProduct")
    public void queryIndexProduct() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("name");
        r.add("tag");
        r.add("sellerId");
        String sql = "select" +
                " _id" +
                ",icon" +
                ",name" +
                ",tag" +
                ",sellerId" +
                " from ProductInfo" +
                " where isIndexCommodity=true group by _id limit 0,4";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

//    /**
//     * 会员:热门商品 页面
//     */
//    @GET
//    @Path("/getProductByHot")
//    public void getProductByHot() throws Exception {
//        Long pageNo = ControllerContext.getPLong("pageNo");
//        Long pageSize = ControllerContext.getPLong("pageSize");
//        Long indexNum = 0l;
//        if (pageNo!=1) {
//            indexNum = (pageNo-1) * pageSize;
//        }
//        List<String> returnFields = new ArrayList<>();
//        returnFields.add("totalCount");
//        String hql = "select count(t1._id) as totalCount" +
//                " from ProductInfo" +
//                " where isIndexCommodity=true or hot=true ";
//        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, null);
//        Long totalNum = (Long) orderList.get(0).get("totalCount");
//        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
//
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("pageNo", pageNo);
//        resultMap.put("totalCount", totalNum);
//        resultMap.put("totalPage", totalPage);
//
//
//        List<String> r = new ArrayList<>();
//        r.add("_id");
//        r.add("icon");
//        r.add("salePrice");
//        r.add("oldPrice");
//        r.add("name");
//        r.add("saleCount");
//        r.add("integralRate");
//        r.add("tag");
//        r.add("sellerId");
//        String sql = "select" +
//                " _id" +
//                ",icon" +
//                ",salePrice" +
//                ",oldPrice" +
//                ",name" +
//                ",saleCount" +
//                ",integralRate" +
//                " from ProductInfo" +
//                " where isIndexCommodity=true or hot=true" +
//                " order by createTime desc" +
//                " limit " + indexNum + "," + pageSize;
//        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
//
//        resultMap.put("sellerList", re);
//        toResult(Response.Status.OK.getStatusCode(), resultMap);
//    }
}
