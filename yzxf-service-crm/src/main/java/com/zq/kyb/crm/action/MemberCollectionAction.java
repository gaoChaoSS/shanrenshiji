package com.zq.kyb.crm.action;

import com.mysql.jdbc.MySQLConnection;
import com.zq.kyb.core.annotation.Member;
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
import java.util.*;


/**
 * Created by luoyunze on 16/12/9.
 */
public class MemberCollectionAction extends BaseActionImpl {
    /**
     * 获取收藏的商品
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMyCollection")
    public void getMyCollection() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String memberId = ControllerContext.getPString("id");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("totalCount");
        String whereStr = " where t1.entityType='product' and t1.memberId=?";
        String hql = "select  count(t1._id) as totalCount " +
                " from MemberCollection t1" +
                " left join ProductInfo t2 on t1.entityId = t2._id" +
                " left join Member t3 on t1.memberId = t3._id" +
                whereStr + " order by t1.createTime desc ";
        List<Map<String, Object>> collection = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, params);
        Long totalNum = collection.size() == 0 ? 0 : (Long) collection.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);

        returnFields.clear();
        returnFields.add("entityId");
        returnFields.add("goodsId");
        returnFields.add("name");
        returnFields.add("memberId");
        returnFields.add("sellerId");
        returnFields.add("productIcon");
        returnFields.add("oldPrice");
        returnFields.add("salePrice");

        String sql = "select " +
                "t1.entityId" +
                ",t2._id as goodsId" +
                ",t2.name" +
                ",t1.memberId" +
                ",t2.sellerId" +
                ",t2.icon as productIcon" +
                ",t2.oldPrice" +
                ",t2.salePrice" +
                " from MemberCollection t1" +
                " left join ProductInfo t2 on t1.entityId = t2._id" +
                " left join Member t3 on t1.memberId = t3._id" +
                whereStr + " order by t1.createTime desc limit " + indexNum + "," + pageSize;

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("collectionList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 获取收藏的商家
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMyCollectionStore")
    public void getMyCollectionStore() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String memberId = ControllerContext.getPString("id");
        Long pageSize = ControllerContext.getPLong("pageSize");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long indexNum = ControllerContext.getPLong("indexNum");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> params = new ArrayList<>();
        String whereStr = " where t1.entityType='seller' and t1.memberId=?";
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("totalCount");
        String hql = "select count(t1._id) as totalCount" +
                " from MemberCollection t1" +
                " left join Member t2 on t1.memberId = t2._id" +
                " left join Seller t3 on t1.entityId=t3._id"
                + whereStr + " order by t1.createTime desc";
        List<Map<String, Object>> sellerList = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, params);
        Long totalNum = sellerList.size() == 0 ? 0 : (Long) sellerList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);

        returnFields.clear();
        returnFields.add("sellerName");
        returnFields.add("sellerId");
        returnFields.add("integralRate");
        returnFields.add("address");
        returnFields.add("icon");
        returnFields.add("doorImg");
        returnFields.add("operateType");
        returnFields.add("sellerIcon");
        returnFields.add("intro");

        String sql = "select" +
                " t3.name as sellerName" +
                ",t3._id as sellerId" +
                ",t3.integralRate" +
                ",t3.address" +
                ",t3.icon" +
                ",t3.doorImg" +
                ",t3.operateType" +
                ",t3.icon as sellerIcon" +
                ",t3.intro" +
                " from MemberCollection t1" +
                " left join Member t2 on t1.memberId = t2._id" +
                " left join Seller t3 on t1.entityId=t3._id"
                + whereStr + " order by t1.createTime desc limit " + indexNum + "," + pageSize;

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("sellerList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 添加或删除收藏商家
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/addOrDelStoreCollection")
    public void addOrDelStoreCollection() throws Exception {

        String entityId = ControllerContext.getContext().getPString("sellerId");
        String memberId = ControllerContext.getContext().getCurrentUserId();
        if (StringUtils.isEmpty(memberId)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请登录后再收藏");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("entityId", entityId);
        map.put("memberId", memberId);
        Map<String, Object> collection = MysqlDaoImpl.getInstance().findOne2Map(entityName, map, new String[]{"_id"}, Dao.FieldStrategy.Include);
        if (collection != null) {
            MysqlDaoImpl.getInstance().remove("MemberCollection", (String) collection.get("_id"));
        } else {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("entityType", "seller");
            paramMap.put("memberId", memberId);
            paramMap.put("entityId", entityId);
            paramMap.put("createTime", System.currentTimeMillis());
            paramMap.put("_id", UUID.randomUUID().toString());

            MysqlDaoImpl.getInstance().saveOrUpdate("MemberCollection", paramMap);
        }
    }

    /**
     * 商品页面查询有没有收藏该商品
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getGoodsIsCollection")
    public void getGoodsIsCollection() throws Exception {
        String goodsId = ControllerContext.getContext().getPString("goodsId");
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Map<String, Object> map = new HashMap<>();
        map.put("entityId", goodsId);
        map.put("entityType", "product");
        map.put("memberId", memberId);
        Map<String, Object> isCollection = MysqlDaoImpl.getInstance().findOne2Map("MemberCollection", map, new String[]{"_id"}, Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), isCollection);
    }

    /**
     * 添加或删除收藏商品
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/addOrDelGoodsCollection")
    public void addOrDelGoodsCollection() throws Exception {
        String goodsId = ControllerContext.getContext().getPString("goodsId");
        String memberId = ControllerContext.getContext().getCurrentUserId();
        if (StringUtils.isEmpty(memberId)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请登录后再收藏");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("entityId", goodsId);
        map.put("entityType", "product");
        map.put("memberId", memberId);
        Map<String, Object> collection = MysqlDaoImpl.getInstance().findOne2Map("MemberCollection", map, new String[]{"_id"}, Dao.FieldStrategy.Include);
        if (collection != null && collection.size()!=0) {
            MysqlDaoImpl.getInstance().remove("MemberCollection", (String) collection.get("_id"));
        } else {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("entityType", "product");
            paramMap.put("memberId", memberId);
            paramMap.put("entityId", goodsId);
            paramMap.put("_id", UUID.randomUUID().toString());
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberCollection", paramMap);
        }
    }


}
