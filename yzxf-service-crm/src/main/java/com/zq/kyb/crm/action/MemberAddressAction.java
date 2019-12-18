package com.zq.kyb.crm.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.regex.Pattern;

public class MemberAddressAction extends BaseActionImpl {
    @Override
    @Member
    @Seller
    public void show() throws Exception {
        super.show();
    }

    @Override
    @Member
    public void del() throws Exception {
        super.del();
    }

    @Override
    @Seller
    @Member
    public void save() throws Exception {

        JSONObject values = ControllerContext.getContext().getReq().getContent();
        String _id = values.getString("_id");
        values.put("_id", _id);
        values.remove("createTime");
        values.remove("creator");
        values.remove("owner");

        // 对关联文件进行额外的处理

        if (dao.findById2Map(entityName, _id, null, Dao.FieldStrategy.Include) == null) {
            values.put("createTime", System.currentTimeMillis());
            values.put("creator", ControllerContext.getContext().getCurrentUserId());
            if ("user".equals(ControllerContext.getContext().getCurrentUserType())) {//商户修改必须设定memberId
                String memberId = ControllerContext.getPString("memberId");
                if (StringUtils.isEmpty(memberId)) {
                    throw new UserOperateException(400, "必须设置memberId");
                }
            }
        } else {//修改后就不能修改memberId了
            values.remove("memberId");
        }
        dao.saveOrUpdate(entityName, values);

        // doAddMapLog(collectionName,values,"编辑"+collectionName.toString(),"");
        Map<String, Object> m = dao.findById2Map(entityName, _id, null, null);
        toResult(Response.Status.OK.getStatusCode(), JSONObject.fromObject(m));
    }

    @Override
    @Member
    public void query() throws Exception {
        super.query();
    }

    /**
     * 获取会员收货地址
     * @throws Exception
     */

    @GET
    @Member
    @Path("/getMemberAddress")
    public void getMemberAddress() throws Exception {

        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getCurrentUserId());
        String addressId = ControllerContext.getContext().getPString("_id");
        String whereStr = "";
        if(!StringUtils.isEmpty(addressId)){
            whereStr = " and _id=?";
            params.add(addressId);
        }


        List<String> returnFields = new ArrayList<String>();
        returnFields.add("_id");
        returnFields.add("defaultAddress");
        returnFields.add("zoneName");
        returnFields.add("phone");
        returnFields.add("createTime");
        returnFields.add("name");
        returnFields.add("gender");
        returnFields.add("postcode");
        returnFields.add("zoneId");
        returnFields.add("area");
        returnFields.add("address");
        returnFields.add("updateTime");
        returnFields.add("areaValue");

        String sql = "select " +
                " _id" +
                " ,defaultAddress" +
                " ,zoneName" +
                " ,phone" +
                " ,createTime" +
                " ,name" +
                " ,gender" +
                " ,postcode" +
                " ,zoneId" +
                " ,area" +
                " ,address" +
                " ,updateTime" +
                " ,areaValue" +

                " from MemberAddress" +

                " where memberId=?" +
                whereStr +
                " order by defaultAddress desc";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员修改,添加收货地址
     * @throws Exception
     */
    @POST
    @Member
    @Path("/addAddress")
    public void addAddress() throws Exception {
            String memberId = ControllerContext.getContext().getCurrentUserId();
            String _id = ControllerContext.getPString("_id");
            String name = ControllerContext.getPString("consignee");
            String gender = ControllerContext.getPString("gender");
            String phone = ControllerContext.getPString("phone");
            String address = ControllerContext.getPString("address");
            String area = ControllerContext.getPString("area");
            String areaValue = ControllerContext.getPString("areaValue");
            String postcode = ControllerContext.getPString("postalcode");
            if (StringUtils.isEmpty(name)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "收货人姓名不能为空!");
            }
            if (!Pattern.matches("[\u0391-\uFFE5]{2,10}",name)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写2~10位的中文姓名");
            }
            if (StringUtils.isEmpty(phone)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号不能为空!");
            }
            if(!Pattern.matches("^[1][3456789][0-9]{9}$",phone)){
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
            }
            if (StringUtils.isEmpty(address) || StringUtils.isEmpty(area)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写完整地址!");
            }
            if (address.length()>200) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "街道地址不能超过200位");
            }
            if (area.length()>200) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "区域地址不能超过200位");
            }
            if (StringUtils.isEmpty(postcode)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "邮政编码不能为空!");
            }
            if(!Pattern.matches("[1-9][0-9]{5}",postcode)){
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "邮政编码格式错误!");
            }

            Map<String, Object> m = new HashMap<>();
            m.put("memberId",memberId);
            Long mCount = MysqlDaoImpl.getInstance().findCount("MemberAddress",m);
            if(mCount>=10){
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "每个会员最多只能添加10条地址!");
            }
            m.clear();
            if(StringUtils.isEmpty(_id)){
                m.put("_id", UUID.randomUUID().toString());
            }else{
                m.put("_id", _id);
            }
            m.put("memberId", memberId);
            m.put("name", name);
            m.put("gender", gender);
            m.put("phone", phone);
            m.put("address", address);
            m.put("area", area);
            m.put("areaValue", areaValue);
            m.put("postcode", postcode);
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberAddress", m);
        }

    /**
     *会员删除收货地址
     * @throws Exception
     */
    @POST
    @Member
    @Path("/delAddress")
    public void delAddress() throws Exception {
        String _id = ControllerContext.getPString("_id");
//        MysqlDaoImpl.getInstance().remove("MemberAddress",_id);
        List<Object> params = new ArrayList<>();
        params.add(_id);
        MysqlDaoImpl.getInstance().exeSql("delete from MemberAddress where _id=?", params, "MemberAddress", false);
    }
    /**
     * 设置会员默认地址
     */
    @GET
    @Member
    @Path("setDefaultAddress")
    public void setDefaultAddress() throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getCurrentUserId());
        params.add(ControllerContext.getContext().getPString("addressId"));
        String sql = "update MemberAddress set defaultAddress=true where memberId=? and _id=?";
        MysqlDaoImpl.getInstance().exeSql(sql,params,"MemberAddress");
        sql = "update MemberAddress set defaultAddress=false where memberId=? and _id<>?";
        MysqlDaoImpl.getInstance().exeSql(sql,params,"MemberAddress");
    }

    /**
     * 获取会员默认收货地址
     */
    @GET
    @Member
    @Path("getDefaultAddress")
    public void getDefaultAddress() throws Exception {
        Map<String,Object> params = new HashMap<>();
        params.put("memberId",ControllerContext.getContext().getCurrentUserId());
        params.put("defaultAddress",true);
        Map<String,Object> re = MysqlDaoImpl.getInstance().findOne2Map("MemberAddress",params,null,null);

        //若没有默认收货地址,则获取其他地址
        if(re==null || re.size()==0){
            params.remove("defaultAddress");
            re = MysqlDaoImpl.getInstance().findOne2Map("MemberAddress",params,null,null);
        }

        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 根据ID获取会员地址
     * @throws Exception
     */
    @GET
    @Member
    @Path("getAddressById")
    public void getAddressById() throws Exception {
        Map<String,Object> params = new HashMap<>();
        params.put("memberId",ControllerContext.getContext().getCurrentUserId());
        params.put("_id",ControllerContext.getPString("_id"));
        Map<String,Object> re = MysqlDaoImpl.getInstance().findOne2Map("MemberAddress",params,null,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
}
