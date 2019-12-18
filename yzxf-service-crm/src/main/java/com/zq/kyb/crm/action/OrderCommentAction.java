package com.zq.kyb.crm.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by luoyunze on 16/12/9.
 */
public class OrderCommentAction extends BaseActionImpl {
    @GET
    @Member
    @Path("/getMyComment")
    public void getMyComment() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        String memberId = ControllerContext.getContext().getPString("memberId");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t2._id) as totalCount" +
                " from Member t1" +
                " left join OrderComment t2 on t1._id = t2.memberId" +
                " left join Seller t3 on t2.sellerId = t3._id where t2.memberId=?";
        List<Map<String, Object>> commentList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) commentList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("sellerId");
        returnFields.add("memberName");
        returnFields.add("memberIcon");
        returnFields.add("createTime");
        returnFields.add("serviceStar");
        returnFields.add("score");
        returnFields.add("commentContent");
        returnFields.add("sellerName");
        returnFields.add("sellerIcon");


        String sql = "select" +
                " t2.sellerId" +
                ",t1.mobile as memberName" +
                ",t1.icon as memberIcon" +
                ",t2.createTime" +
                ",t2.serviceStar" +
                ",t2.score" +
                ",t2.name as commentContent" +
                ",t3.name as sellerName" +
                ",t3.icon as sellerIcon" +
                " from Member t1" +
                " left join OrderComment t2 on t1._id = t2.memberId" +
                " left join Seller t3 on t2.sellerId = t3._id"+
                " where t2.memberId=?"+
                " order by t2.createTime desc"+
                " limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        resultMap.put("commentList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }
}
