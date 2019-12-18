package com.zq.kyb.account.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luoyunze on 16/12/2.
 */
public class SellerTypeAction extends BaseActionImpl {
    @Override
    public void show() throws Exception {
        super.show();
    }

    /**
     * 商户类型
     *
     * @throws Exception
     */
    @Override
    @Member
    public void query() throws Exception {
        super.query();
    }

    @Override
    public void save() throws Exception {
        super.save();
    }

    @Override
    public void del() throws Exception {
        super.del();
    }

    /**
     * 获取经营类别
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getOperateType")
    public void getOperateType() throws Exception {
        List<String> returnField = new ArrayList<>();
        returnField.add("_id");
        returnField.add("pid");
        returnField.add("name");
        returnField.add("iconClass");
        returnField.add("bgColor");

        String sql = "select" +
                " _id" +
                ",pid" +
                ",name" +
                ",iconClass" +
                ",bgColor" +
                " from OperateType" +
                " where pid=-1" +
                " and isNav=true order by number asc" +
                " limit 0,8";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
}
