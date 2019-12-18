package com.zq.kyb.common.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import net.sf.json.JSONObject;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.Map;

public class DeviceAction extends BaseActionImpl {
    @Override
    @PUT
    @Seller
    @Member
    @Path("/save")
    public void save() throws Exception {
        super.save();
    }
}
