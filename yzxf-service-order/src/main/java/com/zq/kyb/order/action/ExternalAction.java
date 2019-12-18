package com.zq.kyb.order.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExternalAction extends BaseActionImpl {

    /**
     * 检查来源
     * @throws Exception
     */
    public void checkFrom() throws Exception{
        String relateFrom = ControllerContext.getPString("relateFrom");//店铺来源
        String relateStoreId = ControllerContext.getPString("relateStoreId");//店铺ID
//        String relateType = ControllerContext.getPString("relateType");//店铺ID

        if(StringUtils.isEmpty(relateFrom) || StringUtils.isEmpty(relateStoreId)){
            throw new UserOperateException(500,"来源不能为空");
        }

//        String userType = relateFrom.substring(0,1).toUpperCase()+relateFrom.substring(1,relateFrom.length());

        Message msg = Message.newReqMessage("1:GET@/account/RelateStore/getRelateStore");
        msg.getContent().put("relateStoreId",relateStoreId);
        msg.getContent().put("relateFrom",relateFrom);
//        msg.getContent().put("userType",relateType);
        JSONObject relateStore = ServiceAccess.callService(msg).getContent();
        if(relateStore==null || relateStore.size()==0){
            throw new UserOperateException(500,"获取数据失败");
        }
    }

//    /**
//     * 查询养老金记录
//     * @throws Exception
//     */
//    @GET
//    @Path("/getPension")
//    public void getPension() throws Exception{
//        checkFrom();

//        Map<String, Object> resultMap = new HashMap<>();
//        String memberId = ControllerContext.getContext().getCurrentUserId();
//        long startTime = ControllerContext.getPLong("startTime");
//        long endTime = ControllerContext.getPLong("endTime");
//        long pageSize = ControllerContext.getPLong("pageSize");
//        long pageNo = ControllerContext.getPLong("pageNo");
//
//        List<Object> params = new ArrayList<>();
//        params.add(memberId);
//        List<String> p = new ArrayList<>();
//        p.add("totalCount");
//        String where = " where t1._id = ? and t2.orderType in (0,1,7,8,13) and t2.orderStatus=100";
//        if (startTime != 0) {
//            where += " and t2.endTime>=?";
//            params.add(startTime);
//        }
//        if (endTime != 0) {
//            where += " and t2.endTime<=?";
//            params.add(endTime);
//        }
//        String hql = "select count(t2._id) as totalCount" +
//                " from Member t1" +
//                " left join OrderInfo t2 on t1._id = t2.memberId" +
//                " left join Seller t3 on t2.sellerId = t3._id" +
//                " left join MemberPensionLog t4 on t2.orderNo=t4.orderId"
//                + where;
//
//        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
//        Long totalNum = (Long) orderList.get(0).get("totalCount");
//        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
//        resultMap.put("pageNo", pageNo);
//        resultMap.put("totalNum", totalNum);
//        resultMap.put("totalPage", totalPage);
//        List<String> returnFields = new ArrayList<>();
//        returnFields.clear();
//        returnFields.add("createTime");
//        returnFields.add("endTime");
//        returnFields.add("payMoney");
//        returnFields.add("pensionMoney");
//        returnFields.add("score");
//        returnFields.add("orderType");
//        returnFields.add("name");
//        returnFields.add("sellerId");
//        returnFields.add("sellerIcon");
//        returnFields.add("integralRate");
//        returnFields.add("insureCount");
//        returnFields.add("insureCountUse");
//        returnFields.add("isInsure");
//
//        String sql = "select" +
//                " t2.createTime" +
//                ",t2.endTime" +
//                ",t2.payMoney" +
//                ",t2.pensionMoney" +
//                ",t2.score" +
//                ",t2.orderType" +
//                ",t3.name" +
//                ",t3._id as sellerId" +
//                ",t3.icon as sellerIcon" +
//                ",t3.integralRate" +
//                ",t4.insureCount" +
//                ",t4.insureCountUse" +
//                ",t4.isInsure" +
//                " from Member t1" +
//                " left join OrderInfo t2 on t1._id = t2.memberId" +
//                " left join Seller t3 on t2.sellerId = t3._id" +
//                " left join MemberPensionLog t4 on t2.orderNo=t4.orderId"
//                + where + " order by t2.createTime desc limit " + indexNum + "," + pageSize;
//
//        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
//        resultMap.put("orderList", re);
//        toResult(Response.Status.OK.getStatusCode(), resultMap);
//    }

    /**
     * 查询养老金账户
     * @throws Exception
     */
    @GET
    @Path("/getPensionAccount")
    public void getPensionAccount() throws Exception {
        checkFrom();

        String mobile = ControllerContext.getPString("mobile");
        String memberId = ControllerContext.getPString("memberId");

        String where = " where 1=1";
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();

        if(!StringUtils.isEmpty(mobile)){
            where +=" and t1.mobile = ?";
            params.add(mobile);
        }else if(!StringUtils.isEmpty(memberId)){
            where +=" and t1._id = ?";
            params.add(memberId);
        }else{
            throw new UserOperateException(500,"手机号码或ID不能为空");
        }

        returnFields.add("memberId");
        returnFields.add("mobile");
        returnFields.add("realName");
        returnFields.add("cardNo");
        returnFields.add("pensionCount");
        returnFields.add("insureCountUse");
        returnFields.add("insureCount");

        String sql = "select" +
                " t1._id as memberId" +
                ",t1.mobile" +
                ",t1.realName" +
                ",t1.cardNo" +
                ",t2.pensionCount" +
                ",t2.insureCountUse" +
                ",t2.insureCount" +
                " from Member t1 " +
                " left join MemberPensionAccount t2 on t1._id = t2.memberId" +
                where;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(re==null || re.size()==0){
            toResult(200,new HashMap<>());
        }else{
            toResult(200,re.get(0));
        }
    }

    /**
     * 查询养老金账户
     * @throws Exception
     */
    @GET
    @Path("/getInsureLog")
    public void getInsureLog() throws Exception {
        checkFrom();

        new InsureAction().queryInsureLog();
    }
}
