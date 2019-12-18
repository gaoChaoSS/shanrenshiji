package com.zq.kyb.account.action;


import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.zq.kyb.core.annotation.Lock;
import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.lang.BooleanUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AgentAction extends BaseActionImpl {

    /**
     * 查询代理商
     *
     * @throws Exception
     */
    @Override
    @Member
    public void query() throws Exception {
        super.query();
    }

    /**
     * 获取会员正在申请或者申请成功的代理商的信息
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getAgentApplyInfo")
    public void getAgentApplyInfo() throws Exception {
        List<Object> params = new ArrayList<>();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("_id");
        returnFields.add("creator");
        returnFields.add("name");
        returnFields.add("contactPerson");
        returnFields.add("realCard");
        returnFields.add("phone");
        returnFields.add("address");
        returnFields.add("areaValue");
        returnFields.add("area");
        returnFields.add("businessLicense");
        returnFields.add("idCardImgFront");
        returnFields.add("idCardImgBack");
        returnFields.add("idCardImgHand");
        String sql = "select" +
                " _id" +
                ",creator" +
                ",name" +
                ",contactPerson" +
                ",realCard" +
                ",phone" +
                ",address" +
                ",areaValue" +
                ",area" +
                ",businessLicense" +
                ",idCardImgFront" +
                ",idCardImgBack" +
                ",idCardImgHand" +
                " from Agent" +
                " where creator=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (re == null || re.size() == 0 || StringUtils.isEmpty((String) re.get(0).get("creator"))) {
            String _id = "A-" + ZQUidUtils.generateAgentNo();
            Map<String, Object> s = new HashMap<>();
            s.put("_id", _id);
            s.put("canUse", false);
            s.put("creator", memberId);
            s.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("Agent", s);
            Map<String, Object> reMap = MysqlDaoImpl.getInstance().findById2Map("Agent", _id, null, null);
            toResult(Response.Status.OK.getStatusCode(), reMap);
            return;
        }

        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }


    /**
     * 获取当前登录代理商信息,若有则返回信息;无需登录判断
     *
     * @throws Exception
     */
    public Map<String, Object> getAgentByIsCurrent() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (!StringUtils.mapValueIsEmpty(other, "agentId")) {
            String agentId = (String) other.get("agentId");
            Map<String, Object> agent = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, null, null);
            return agent;
        } else {
            return null;
        }
    }

    /**
     * 获取当前登录代理商信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getAgentByCurrent")
    public void getAgentByCurrent() throws Exception {
        Map<String, Object> re = getCurrentAgent();
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    public Map<String, Object> getCurrentAgent() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录");
        }
        String agentId = (String) other.get("agentId");
        Map<String, Object> agent = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, null, null);

        if(!StringUtils.mapValueIsEmpty(agent,"level")){
            //同时查询所有父亲的信息
            String areaValue = (String) agent.get("areaValue");
            areaValue = areaValue.replaceAll("_", " ").trim();
            String[] ids = areaValue.split(" ");
            int len = ids.length;
            List li = new ArrayList<>();
            if (len > 1) {
                for (int i = 0; i < len - 1; i++) {
                    li.add(MysqlDaoImpl.getInstance().findById2Map("Agent", ids[i], null, null));
                    agent.put("$$parent", li);
                }
            }
        }

        return agent;
    }

    /**
     * 获取当前登陆的是否是财务管理员
     *
     * @throws Exception
     */
    @GET
    @Path("/getAgentByCurrentByCw")
    public void getAgentByCurrentByCw() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录");
        }
        String agentId = (String) other.get("agentId");
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + agentId);
        if(!"1".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }
    }

    /**
     * 平台管理:根据ID获取代理商信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getAgentById")
    public void getAgentById() throws Exception {
        String agentId = ControllerContext.getPString("agentId");
        toResult(Response.Status.OK.getStatusCode(), getAgentById(agentId));
    }

    public Map<String, Object> getAgentById(String agentId) throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        if (StringUtils.isEmpty(agentId)) {
            throw new UserOperateException(400, "找不到该用户");
        }

        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, null, null);
        return re;
    }

    /**
     * 平台管理:获取管理员
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getAgentAdmin")
    public void getAgentAdmin() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("level","1");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Agent", "A-000001", null, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }


    /**
     * 平台管理:根据 拼接的ID(areaValue字段) 获取 代理商地址
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getAgentAreaValueById")
    public void getAgentAreaValueById() throws Exception {
        String areaValue = ControllerContext.getPString("areaValue");
        toResult(Response.Status.OK.getStatusCode(), getAgentAreaValueById(areaValue));
    }

    public Map<String, Object> getAgentAreaValueById(String areaValue) throws Exception {
        String agentNameAll = "";
        if (StringUtils.isNotEmpty(areaValue)) {
            String[] grade = areaValue.substring(1, areaValue.length() - 1).split("_");

            boolean isFactor = "F".equals(grade[grade.length - 1].substring(0, 1));
            for (int index = 0; index < grade.length; index++) {
                if (index == grade.length - 1 && isFactor) {
                    Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Factor", grade[index], new String[]{"name"}, Dao.FieldStrategy.Include);
                    if (re != null && re.size() > 0 && re.get("name") != null) {
                        agentNameAll += "-" + re.get("name");
                    }
                    break;
                }
                Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Agent", grade[index], new String[]{"name"}, Dao.FieldStrategy.Include);

                if (re == null || re.size() == 0) {
                    break;
                }
                agentNameAll += "-" + re.get("name");
            }
            agentNameAll = agentNameAll.substring(1, agentNameAll.length());
        } else {
            agentNameAll += "暂无";
        }

        Map<String, Object> re = new HashMap<>();
        re.put("agentNameAll", agentNameAll);
        return re;
    }

    /**
     * 平台管理:根据代理商id获取areaValue
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getAgentAreaByValue")
    public void getAgentAreaByValue() throws Exception {
        String agentId = ControllerContext.getPString("agentId");
        if (StringUtils.isEmpty(agentId)) {
            throw new UserOperateException(400, "找不到该用户");
        }

        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, new String[]{"areaValue"}, Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 平台管理:代理商列表
     */
    @POST
    @Seller
    @Path("/getAgentList")
    public void getAgentList() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        Map<String, Object> resultMap = new HashMap<>();
        List<Object> params = new ArrayList();
        Long startTime = ControllerContext.getPLong("startDate");
        Long endTime = ControllerContext.getPLong("endDate");
        String search = ControllerContext.getPString("search");
        int selectCheck = ControllerContext.getPInteger("selectCheck");
        Long indexNum = 0l;
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");

        if (pageNo != 1) {
            indexNum = (pageNo - 1) * pageSize;
        }
        String whereStr = " where t1.applyTime is not null";

        if (startTime != 0) {
            whereStr += " and t1.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t1.createTime<=?";
            params.add(endTime);
        }
        if (StringUtils.isNotEmpty(search)) {
            whereStr += " and t1.name like ?";
            params.add("%" + search + "%");
        }
        if (selectCheck == 1) {//已通过
            whereStr += " and t1.canUse=true";
        } else if (selectCheck == 2) {//未通过
            whereStr += " and t1.canUse=false or t1.canUse is null";
        }
//        else if(selectCheck==3){//待审核
//            whereStr += " and t1.isApply=false and t1.canUse=false";
//        }
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String hql = "select count(t1._id) as totalCount" +
                " from Agent t1";
        hql += whereStr + " order by t1.createTime desc";
        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> r = new ArrayList<>();
        String itemSql = "select" +
                " t1._id" +
                ",t1.createTime" +
                ",t1.name" +
                ",t1.canUse" +
                ",t1.phone" +
                ",t1.contactPerson" +
                ",t1.applyTime" +
                ",t1.area" +
                " from Agent t1";
        itemSql += whereStr + " order by t1.createTime desc limit " + indexNum + "," + pageSize;

        r.add("_id");
        r.add("createTime");
        r.add("name");
        r.add("canUse");
        r.add("phone");
        r.add("contactPerson");
        r.add("applyTime");
        r.add("area");
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(itemSql, r, params);
        resultMap.put("orderList", li);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 平台管理:批量审核 代理商
     */
    @POST
    @Seller
    @Path("/updateAgentCanUse")
    public void updateAgentCanUse() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        String idStr = ControllerContext.getPString("idStr");

        if (idStr.length() <= 1) {
            throw new UserOperateException(400, "请选择需要修改的代理商");
        }
        String canUseStr = ControllerContext.getPString("canUseStr");
        if (StringUtils.isEmpty(canUseStr)) {
            throw new UserOperateException(400, "请选择审核通过或不通过");
        }
        String[] idArr = idStr.split(",");
        String[] canUseArr = canUseStr.split(",");
        Map<String, Object> s = new HashMap<>();
        for (int i = 0; i < idArr.length; i++) {
            s.clear();
            s.put("_id", idArr[i]);
            s.put("canUse", Boolean.valueOf(canUseArr[i]));
            s.put("modifyTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("Agent", s);
        }
    }

    /**
     * 平台管理:批量删除 代理商
     */
    @POST
    @Seller
    @Path("/deleteAgent")
    public void deleteAgent() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        String idStr = ControllerContext.getPString("idStr");
        if (idStr.length() <= 1) {
            throw new UserOperateException(400, "请选择需要删除的代理商");
        }

        String[] idArr = idStr.split(",");
        Map<String, Object> s = new HashMap<>();
        List<Object> params = new ArrayList<>();

        for (int i = 0; i < idArr.length; i++) {
            s.clear();
            params.clear();

            params.add(idArr[i]);
            MysqlDaoImpl.getInstance().exeSql("delete from Agent where _id=?", params, "Agent", false);
        }
    }

    /**
     * 平台管理:新增 代理商
     */
    @POST
    @Seller
    @Path("/addAgent")
    public void addAgent() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        JSONObject values = ControllerContext.getContext().getReq().getContent();

        if (values.get("_id") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "提交失败,请重试!");
        }
        if (values.get("address") == null || values.get("area") == null || values.get("areaValue") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写完整地址!");
        }
        if (String.valueOf(values.get("area")).length() > 200) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "所在区域不能超过200位");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("areaValue", values.get("areaValue"));
        Map<String, Object> agentAreaValue = MysqlDaoImpl.getInstance().findOne2Map("Agent", params, null, null);
        if (agentAreaValue != null && agentAreaValue.size() > 0 && (Boolean) agentAreaValue.get("canUse")) {
            throw new UserOperateException(400, "该地区已经存在代理商,请选择其他地区注册代理商!");
        }

        if (values.get("name") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "代理商名字不能为空");
        }
        if (String.valueOf(values.get("name")).length() > 64) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "代理商名字不能超过64位");
        }
        if (String.valueOf(values.get("address")).length() > 64) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "街道地址不能超过64位");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) values.get("realCard"))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证格式错误!");
        }
        if (values.get("phone") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号不能为空!");
        }
        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) values.get("phone"))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,10}", (String) values.get("contactPerson"))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写2~10位的中文姓名");
        }
        if (values.get("businessLicense") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传营业执照!");
        }
        if (values.get("idCardImgFront") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传身份证正面照片!");
        }
        if (values.get("idCardImgBack") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传身份证背面照片!");
        }
        if (values.get("idCardImgHand") == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传手持身份证照片!");
        }

        Map<String, Object> s = new HashMap<>();
        s.putAll(values);
        s.put("_id", values.get("_id"));
        s.put("canUse", true);
        s.put("applyTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("Agent", s);
    }


    /**
     * 代理商获取旗下代理商
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/agentGetAgentList")
    public void agentGetAgentList() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "该用户已被禁用");
        }
        String agentId = (String) other.get("agentId");
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String l = cache.getCache("agent_level_cache_" + agentId);
        Long indexNum = 0l;
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        String whereStr = " where 1=1";
        if (pageNo != 1) {
            indexNum = (pageNo - 1) * pageSize;
        }
        Map<String, Object> resultMap = new HashMap<>();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("createTime");
        r.add("name");
        r.add("level");
        r.add("address");
        r.add("surplusCardNum");
        r.add("phone");
        if ("1".equals(l)) {
            String sql = "select" +
                    " _id" +
                    ",createTime" +
                    ",name" +
                    ",level" +
                    ",address" +
                    ",surplusCardNum" +
                    ",phone" +
                    " from agent" +
                    " where canUse=true and level<>1 order by createTime desc limit " + indexNum + "," + pageSize;
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, null);
            r.clear();
            r.add("totalCount");
            String hql = "select" +
                    " count(_id) as totalCount" +
                    " from agent" +
                    " where canUse=true and level<>1";
            List<Map<String, Object>> memberList = MysqlDaoImpl.getInstance().queryBySql(hql, r, null);
            Long totalNum = (Long) memberList.get(0).get("totalCount");
            Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalNum", totalNum);
            resultMap.put("totalPage", totalPage);
            resultMap.put("agentList", re);
            toResult(Response.Status.OK.getStatusCode(), resultMap);
        } else if ("4".equals(l)) {
            String sql = "select" +
                    " t1._id" +
                    ",t1.createTime" +
                    ",t1.name" +
                    ",t1.level" +
                    ",t1.address" +
                    ",t1.surplusCardNum" +
                    ",t1.mobile as phone" +
                    " from Factor t1" +
                    " left join Agent t2 on t1.pid=t2._id" +
                    " where t1.canUse=true and t2._id=? order by t1.createTime desc limit " + indexNum + "," + pageSize;
            p.add(agentId);
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            r.clear();
            r.add("totalCount");
            String hql = "select" +
                    " count(t1._id) as totalCount" +
                    " from Factor t1" +
                    " left join Agent t2 on t1.pid=t2._id" +
                    " where t1.canUse=true and t2._id=?";
            List<Map<String, Object>> memberList = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
            Long totalNum = (Long) memberList.get(0).get("totalCount");
            Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalNum", totalNum);
            resultMap.put("totalPage", totalPage);
            resultMap.put("agentList", re);
            toResult(Response.Status.OK.getStatusCode(), resultMap);

        } else {
            //根据当前登陆代理商的ID查询到地区值areaValue.
            String areaValue = getCurrentAgent().get("areaValue").toString();
            //根据代理商的areaValue查询出旗下所有代理商
            String sql = "select" +
                    " _id" +
                    ",createTime" +
                    ",name" +
                    ",level" +
                    ",address" +
                    ",surplusCardNum" +
                    ",phone" +
                    " from agent" +
                    " where canUse=true and areaValue like ? and _id<>? order by createTime desc limit " + indexNum + "," + pageSize;
            p.add(areaValue.replace("_", "\\_") + "%");
            p.add(agentId);
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            r.clear();
            r.add("totalCount");
            String hql = "select" +
                    " count(_id) as totalCount" +
                    " from agent" +
                    " where canUse=true and areaValue like ? and _id<>?";
            List<Map<String, Object>> memberList = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
            Long totalNum = (Long) memberList.get(0).get("totalCount");
            Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
            resultMap.put("pageNo", pageNo);
            resultMap.put("totalNum", totalNum);
            resultMap.put("totalPage", totalPage);
            resultMap.put("agentList", re);
            toResult(Response.Status.OK.getStatusCode(), resultMap);
        }
    }

    /**
     * 通过条件查询代理商旗下代理商列表
     */
    @GET
    @Seller
    @Path("/agentCriteriaAgentList")
    public void agentCriteriaAgentList() throws Exception {

        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String agentName = ControllerContext.getPString("_agentName");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        String agentQueryId = ControllerContext.getPString("_agentQueryId");
        String agentLevel = ControllerContext.getPString("_agentLevel");


        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String where = " where 1=1 and t1.level<>1";
        String from = ",t1.phone from Agent t1";
        String select = "";

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            int grade = areaValue.substring(1, areaValue.length() - 1).split("_").length;
            if (grade != 5 && grade != 6) {
                where += " and t1.areaValue like ? and t1.areaValue not like ?";
                select += ",t1.phone";
                p.add(areaValue + "%");
                p.add(areaValue);
                r.add("phone");
            } else {
                from = ",t1.mobile from Factor t1";
                where = " where t1.areaValue like ?";
                p.add(areaValue + "%");
                select += ",t1.mobile";
                r.add("mobile");
            }
        }
        if (StringUtils.isNotEmpty(agentQueryId)) {
            where += " and t1._id=?";
            p.add(agentQueryId);
        }
        if (StringUtils.isNotEmpty(agentName)) {
            where += " and t1.name like ?";
            p.add(agentName + "%");
        }
        if ("One".equals(agentLevel)) {
            where += " and t1.level=2";
        }
        if ("Two".equals(agentLevel)) {
            where += " and t1.level=3";
        }
        if ("Three".equals(agentLevel)) {
            where += " and t1.level=4";
        }
        if (startTime != 0) {
            where += " and t1.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            p.add(endTime);
        }

        ///////
        r.add("totalCount");
        String hql = "select" +
                " count(t1._id) as totalCount" +
                from +
                " left join CardField t2 on t1.areaValue=t2.belongAreaValue" +
                where + " group by t1._id";
        List<Map<String, Object>> agentCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = 0L;
        if (agentCount.size() != 0) {
            totalNum = Long.valueOf(agentCount.size());
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();
        r.add("_id");
        r.add("createTime");
        r.add("name");
        r.add("level");
        r.add("address");
        r.add("areaValue");
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            int grade = areaValue.substring(1, areaValue.length() - 1).split("_").length;
            if (grade != 5 && grade != 6) {
                select += ",t1.phone";
                r.add("phone");
            } else {
                select += ",t1.mobile";
                r.add("mobile");
            }
        }
        r.add("cardNum");

        String sql = "select" +
                " t1._id" +
                ",t1.createTime" +
                ",t1.name" +
                ",t1.level" +
                ",t1.address" +
                ",t1.areaValue" +
                select +
                ",sum(t2.cardNum) as cardNum" +
                from +
                " left join CardField t2 on t1.areaValue=t2.belongAreaValue" +
                where + " group by t1._id order by t1.createTime limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 代理商获取旗下代理商详细信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/agentGetAgentInfo")
    public void agentGetAgentInfo() throws Exception {

        Map<String, Object> agent = getCurrentAgent();

        String agentId = ControllerContext.getPString("agentId");
        String userType = agentId.substring(0, 1);
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> factorInfo = new HashMap<>();
        Map<String, Object> agentInfo = new HashMap<>();
        if ("F".equals(userType)) {
            factorInfo = MysqlDaoImpl.getInstance().findById2Map("Factor", agentId, null, null);
            if (!StringUtils.isEmpty((String) factorInfo.get("pid"))) {
                Map<String, Object> superiorAgent = MysqlDaoImpl.getInstance().findById2Map("Agent", factorInfo.get("pid").toString(), new String[]{"name"}, Dao.FieldStrategy.Include);
                resultMap.put("superiorAgent", superiorAgent);
            }
            resultMap.put("agentInfo", factorInfo);
        } else {
            agentInfo = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, null, null);
            if (!StringUtils.isEmpty((String) agentInfo.get("pid"))) {
                Map<String, Object> superiorAgent = MysqlDaoImpl.getInstance().findById2Map("Agent", agentInfo.get("pid").toString(), new String[]{"name"}, Dao.FieldStrategy.Include);
                resultMap.put("superiorAgent", superiorAgent);
            }
            resultMap.put("agentInfo", agentInfo);
        }
        String sql = "select" +
                " startCardNo" +
                ",endCardNo" +
                ",cardNum" +
                " from CardField" +
                " where belongAreaValue = ? group by _id order by createTime";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        r.add("startCardNo");
        r.add("endCardNo");
        r.add("cardNum");
        if ("F".equals(userType)) {
            p.add(factorInfo.get("areaValue"));
        } else {
            p.add(agentInfo.get("areaValue"));
        }
        List<Map<String, Object>> cardField = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        Map<String, Object> adminAgent = new HashMap<>();
        adminAgent.put("level", agent.get("level"));
        adminAgent.put("name", agent.get("name"));
        adminAgent.put("id", agent.get("_id"));
        adminAgent.put("surplusCardNum", agent.get("surplusCardNum"));
        resultMap.put("adminAgent", adminAgent);
        resultMap.put("cardField", cardField);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    //代理商给下级分配卡时给自己产生的卡段
    public void assignToMe(String startCardNo, String endCardNo, String areaValue, Long cardNum , String grant , String receive,String type) throws Exception {
        Message message = Message.newReqMessage("1:POST@/crm/Member/allocationCardFiled");
        message.getContent().put("startCardNo", startCardNo);
        message.getContent().put("endCardNo", endCardNo);
        message.getContent().put("belongAreaValue", areaValue);
        message.getContent().put("cardNum", cardNum);
        message.getContent().put("grant", grant);
        message.getContent().put("receive", receive);
        message.getContent().put("type", type);
        ServiceAccess.callService(message).getContent();
    }

    //代理商给下级分配卡时给下级产生的卡段
//    public void assignToDown(String startCardNo, String endCardNo, String areaValue, Long cardNum, String grant , String receive) throws Exception {
//        Message message = Message.newReqMessage("1:POST@/crm/Member/allocationCardFiled");
//        message.getContent().put("startCardNo", startCardNo);
//        message.getContent().put("endCardNo", endCardNo);
//        message.getContent().put("belongAreaValue", areaValue);
//        message.getContent().put("cardNum", cardNum);
//        message.getContent().put("grant", grant);
//        message.getContent().put("receive", receive);
//        ServiceAccess.callService(message).getContent();
//    }

    /**
     * 获取代理商或发卡点剩余卡量
     *
     * @throws Exception
     */
    @GET
    @Path("/getAgentCardNum")
    public void getAgentCardNum() throws Exception {
        String userType = ControllerContext.getPString("userType");
        String userId = ControllerContext.getPString("userId");
        Map<String, Object> user = new HashMap<>();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userType)) {
            throw new UserOperateException(400, "获取剩余卡量失败");
        }
//        if ("001".equals(userId)) {
//            return;
//        }
        if ("Factor".equals(userType)) {
            user = new FactorAction().getFactorById(userId);
        } else if ("Agent".equals(userType)) {
            user = getAgentById(userId);
        }
        String sql = "select" +
                " startCardNo" +
                ",endCardNo" +
                ",cardNum" +
                " from CardField" +
                " where belongAreaValue = ? group by _id order by createTime";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        r.add("startCardNo");
        r.add("endCardNo");
        r.add("cardNum");
        p.add(user.get("areaValue"));
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }


    /**
     * 分配会员卡
     *
     * @throws Exception
     */
    @POST
    @Path("/assignCard")
    public void assignCard() throws Exception {
        Map<String, Object> adminAgent = getCurrentAgent();
        String adminValue = adminAgent.get("areaValue").toString();
        String startCardNo = ControllerContext.getPString("startCardNo");
        String endCardNo = ControllerContext.getPString("endCardNo");
        String grant = adminAgent.get("_id").toString();//发卡方
        String receive = ControllerContext.getPString("receive");//接收方
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        if(StringUtils.isEmpty(receive)){
            throw new UserOperateException(500, "请选择接收方");
        }
        //获取接收方数据
        Map<String,Object> lowerAgent = MysqlDaoImpl.getInstance().findById2Map(
                "F".equals(receive.substring(0,1))?"Factor":"Agent",receive,null,null);
        if(Integer.parseInt(adminAgent.get("level").toString())!=Integer.parseInt(lowerAgent.get("level").toString())-1){
            throw new UserOperateException(500, "您不能越级选择接收方");
        }
        if (!Pattern.matches("^(" + adminAgent.get("areaValue").toString() + "\\S*)$", lowerAgent.get("areaValue").toString())) {
            throw new UserOperateException(500, "您只能选择您归属下的代理方(接收方)");
        }
        String areaValue = lowerAgent.get("areaValue").toString();

        //先查询该卡段是否已经有了
        String sql1 = "select" +
                " _id" +
                " from CardField" +
                " where ((cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?)" +
                " or (cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?)) and belongAreaValue=?";
        r.add("_id");
        p.add(Long.parseLong(startCardNo));
        p.add(Long.parseLong(startCardNo));
        p.add(Long.parseLong(endCardNo));
        p.add(Long.parseLong(endCardNo));
        p.add(areaValue);
        List<Map<String, Object>> cardFieldList = MysqlDaoImpl.getInstance().queryBySql(sql1, r, p);
        if (cardFieldList.size() != 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该号段已使用");
        }
        //从自己所拥有的号段是否有当前号段
        String sql = "select" +
                " _id" +
                ",startCardNo as queryStartCardNo" +
                ",endCardNo as queryEndCardNo" +
                ",`grant`" +
                ",receive" +
                " from CardField" +
                " where ((cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?)" +
                " or (cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?))" +
                " and belongAreaValue=?";
        r.clear();
        p.clear();
        r.add("_id");
        r.add("queryStartCardNo");
        r.add("queryEndCardNo");
        r.add("grant");
        r.add("receive");
        p.add(Long.valueOf(startCardNo));
        p.add(Long.valueOf(startCardNo));
        p.add(Long.valueOf(endCardNo));
        p.add(Long.valueOf(endCardNo));
        p.add(adminValue);
        List<Map<String, Object>> cardFieldListToMe = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (cardFieldListToMe.size() == 0) {
            throw new UserOperateException(500, "该号段未划分使用!");
        }
        String id = cardFieldListToMe.get(0).get("_id").toString();
        Long queryStartCardNo = Long.valueOf(cardFieldListToMe.get(0).get("queryStartCardNo").toString());
        String queryStartCardNoStr = cardFieldListToMe.get(0).get("queryStartCardNo").toString();
        Long queryEndCardNo = Long.valueOf(cardFieldListToMe.get(0).get("queryEndCardNo").toString());
        String queryEndCardNoStr = cardFieldListToMe.get(0).get("queryEndCardNo").toString();
        String oldGrant = cardFieldListToMe.get(0).get("grant").toString();
        String oldReceive = cardFieldListToMe.get(0).get("receive").toString();
        //删除原有数据
        p.clear();
        p.add(id);
        MysqlDaoImpl.getInstance().exeSql("delete from CardField where _id=?", p, "CardField", false);
        if (queryStartCardNo < Long.valueOf(startCardNo) && queryEndCardNo > Long.valueOf(endCardNo)) {
            //如果号码在号段中间
            //将取原本的数据分割成2部分
            //第一部分
            assignToMe(queryStartCardNoStr
                    , String.valueOf(Long.valueOf(startCardNo) - 1)
                    , adminValue
                    , Long.valueOf(startCardNo) - queryStartCardNo
                    , oldGrant
                    , oldReceive
                    , null);
            //第二部分
            assignToMe(String.valueOf(Long.valueOf(endCardNo) + 1)
                    , queryEndCardNoStr
                    , adminValue
                    , queryEndCardNo - Long.valueOf(endCardNo)
                    , oldGrant
                    , oldReceive
                    , null);
            //分配给下级
            assignToMe(startCardNo
                    , endCardNo
                    , areaValue
                    , Long.valueOf(endCardNo) - Long.valueOf(startCardNo) + 1
                    , grant
                    , receive
                    , "1");
        } else if (queryStartCardNo == Long.valueOf(startCardNo).longValue() && queryEndCardNo > Long.valueOf(endCardNo)) {
            //号码在左边
            //本身剩余
            assignToMe(String.valueOf(Long.valueOf(endCardNo) + 1)
                    , queryEndCardNoStr
                    , adminValue
                    , queryEndCardNo - Long.valueOf(endCardNo)
                    , oldGrant
                    , oldReceive
                    , null);
            //分配给下级
            assignToMe(startCardNo
                    , endCardNo
                    , areaValue
                    , Long.valueOf(endCardNo) - Long.valueOf(startCardNo) + 1
                    , grant
                    , receive
                    , "1");

        } else if (queryEndCardNo == Long.valueOf(endCardNo).longValue() && queryStartCardNo < Long.valueOf(startCardNo)) {
            //号码在右边
            //本身剩余
            assignToMe(queryStartCardNoStr
                    , String.valueOf(Long.valueOf(startCardNo) - 1)
                    , adminValue
                    , Long.valueOf(startCardNo) - queryStartCardNo + 1
                    , oldGrant
                    , oldReceive
                    , null);
            //分配给下级
            assignToMe(startCardNo
                    , queryEndCardNoStr
                    , areaValue
                    , queryEndCardNo - Long.valueOf(startCardNo) + 1
                    , grant
                    , receive
                    , "1");
        } else {
            //刚好整个卡段
            //分配给下级
            assignToMe(queryStartCardNoStr
                    , queryEndCardNoStr
                    , areaValue
                    , queryEndCardNo - queryStartCardNo + 1
                    , grant
                    , receive
                    , "1");
        }
    }

    /**
     * 回收会员卡
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/cardRecycle")
    public void cardRecycle() throws Exception {
        Map<String, Object> adminAgent = getCurrentAgent();
        String adminLevel = adminAgent.get("level").toString();
        String adminValue = adminAgent.get("areaValue").toString();
        String agentLevel = ControllerContext.getPString("agentLevel");
        String areaValue = ControllerContext.getPString("areaValue");
        String startCardNo = ControllerContext.getPString("startCardNo");
        String endCardNo = ControllerContext.getPString("endCardNo");
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        if (Integer.valueOf(agentLevel) - Integer.valueOf(adminLevel) != 1) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!请不要越级回收!");
        }
        //先查询会员卡段表中下级代理商是否已经有了该号段的卡
        String sql1 = "select" +
                " _id" +
                ",startCardNo as queryStartCardNo" +
                ",endCardNo as queryEndCardNo" +
                ",`grant`" +
                ",receive" +
                " from CardField" +
                " where ((cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?)" +
                " or (cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?))" +
                " and belongAreaValue = ?";
        r.add("_id");
        r.add("queryStartCardNo");
        r.add("queryEndCardNo");
        r.add("grant");
        r.add("receive");
        p.add(Long.parseLong(startCardNo));
        p.add(Long.parseLong(startCardNo));
        p.add(Long.parseLong(endCardNo));
        p.add(Long.parseLong(endCardNo));
        p.add(areaValue);
        List<Map<String, Object>> cardFieldList = MysqlDaoImpl.getInstance().queryBySql(sql1, r, p);
        if (cardFieldList.size() == 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该代理商(或发卡点)没有这个号段的卡号");
        }
        String id = cardFieldList.get(0).get("_id").toString();
        Long queryStartCardNo = Long.valueOf(cardFieldList.get(0).get("queryStartCardNo").toString());
        String queryStartCardNoStr = cardFieldList.get(0).get("queryStartCardNo").toString();
        Long queryEndCardNo = Long.valueOf(cardFieldList.get(0).get("queryEndCardNo").toString());
        String queryEndCardNoStr = cardFieldList.get(0).get("queryEndCardNo").toString();
        String grant = cardFieldList.get(0).get("grant").toString();
        String receive = cardFieldList.get(0).get("receive").toString();
        //删除原有数据
        p.clear();
        p.add(id);
        MysqlDaoImpl.getInstance().exeSql("delete from CardField where _id=?", p, "CardField", false);

        String pid;
        if(!"1".equals(adminAgent.get("level").toString())){
            pid=adminAgent.get("pid").toString();
        }else{
            pid=adminAgent.get("_id").toString();
        }

        if (queryStartCardNo < Long.valueOf(startCardNo) && queryEndCardNo > Long.valueOf(endCardNo)) {
            //如果号码在号段中间
            //将取原本的数据分割成2部分
            //第一部分
            assignToMe(queryStartCardNoStr
                    , String.valueOf(Long.valueOf(startCardNo) - 1)
                    , areaValue
                    , Long.valueOf(startCardNo) - queryStartCardNo
                    , grant
                    , receive
                    , null);
            //第二部分
            assignToMe(String.valueOf(Long.valueOf(endCardNo) + 1)
                    , queryEndCardNoStr
                    , areaValue
                    , queryEndCardNo - Long.valueOf(endCardNo)
                    , grant
                    , receive
                    , null);
            //回收给上级
            assignToMe(startCardNo
                    , endCardNo
                    , adminValue
                    , Long.valueOf(endCardNo) - Long.valueOf(startCardNo) + 1
                    , pid
                    , adminAgent.get("_id").toString()
                    , "2");
        } else if (queryStartCardNo == Long.valueOf(startCardNo).longValue() && queryEndCardNo > Long.valueOf(endCardNo)) {
            //号码在左边
            //本身剩余
            assignToMe(String.valueOf(Long.valueOf(endCardNo) + 1)
                    , queryEndCardNoStr
                    , areaValue
                    , queryEndCardNo - Long.valueOf(endCardNo)
                    , grant
                    , receive
                    , null);
            //回收给上级
            assignToMe(startCardNo
                    , endCardNo
                    , adminValue
                    , Long.valueOf(endCardNo) - Long.valueOf(startCardNo) + 1
                    , pid
                    , adminAgent.get("_id").toString()
                    , "2");

        } else if (queryEndCardNo == Long.valueOf(endCardNo).longValue() && queryStartCardNo < Long.valueOf(startCardNo)) {
            //号码在右边
            //本身剩余
            assignToMe(queryStartCardNoStr
                    , String.valueOf(Long.valueOf(startCardNo) - 1)
                    , areaValue
                    , Long.valueOf(startCardNo) - queryStartCardNo
                    , grant
                    , receive
                    , null);
            //回收给上级
            assignToMe(startCardNo
                    , queryEndCardNoStr
                    , adminValue
                    , queryEndCardNo - Long.valueOf(startCardNo) + 1
                    , pid
                    , adminAgent.get("_id").toString()
                    , "2");
        } else {
            //刚好整个卡段
            //回收给上级
            assignToMe(queryStartCardNoStr
                    , queryEndCardNoStr
                    , adminValue
                    , queryEndCardNo - queryStartCardNo + 1
                    , pid
                    , adminAgent.get("_id").toString()
                    , "2");
        }
    }
//    /**
//     * 平台设置特殊起始号段
//     *
//     * @throws Exception
//     */
//    @POST
//    @Seller
//    @Path("/adminSetSpecialField")
//    public void adminSetSpecialField() throws Exception {
//        int startCardNo = ControllerContext.getPInteger("startCardNo");
//        int endCardNo = ControllerContext.getPInteger("endCardNo");
//        List<String> r = new ArrayList<>();
//        String sql = "select" +
//                " _id" +
//                " from CardField" +
//                " where belongAreaValue='admin'";
//        r.add("_id");
//        List<Map<String,Object>> cardFieldList = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
//        if(cardFieldList.size()!=0){
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!您已设置特殊起始号段!");
//        }
//        //生成号段
//        assignToMe(startCardNo, endCardNo, "admin", endCardNo-startCardNo+1);
//
//    }

    /**
     * 平台设置起始号段
     *
     * @throws Exception
     */
    @POST
    @Path("/adminSetStartCardField")
    public void adminSetStartCardField() throws Exception {
        Map<String, Object> adminAgent = getCurrentAgent();
        String adminAreaValue = adminAgent.get("areaValue").toString();
        String startCardNo = ControllerContext.getPString("startCardNo");
        String endCardNo = ControllerContext.getPString("endCardNo");
//        Long startCardNoLong = Long.parseLong(startCardNo);
//        Long endCardNoLong = Long.parseLong(endCardNo);
        if (Long.valueOf(startCardNo) > Long.valueOf(endCardNo)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!起始号段不能大于终止号段!");
        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        //先查询会员卡段表中省级代理商是否已经有了该号段的卡
        String sql = "select" +
                " _id" +
                " from CardField" +
                " where ((cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?)" +
                " or (cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?))";
        r.add("_id");
        p.add(Long.valueOf(startCardNo).longValue());
        p.add(Long.valueOf(startCardNo).longValue());
        p.add(Long.valueOf(endCardNo).longValue());
        p.add(Long.valueOf(endCardNo).longValue());
//        p.add(adminAreaValue);
//        p.add(adminAreaValue);
//        p.add(adminAreaValue);
        List<Map<String, Object>> cardFieldList = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (cardFieldList.size() != 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该号段已经分配!");
        }
        //生成号段
        assignToMe(startCardNo
                , endCardNo
                , adminAreaValue
                , Long.valueOf(endCardNo) - Long.valueOf(startCardNo) + 1
                , adminAgent.get("_id").toString()
                , adminAgent.get("_id").toString()
                , "1");
    }

    /**
     * 修改平台当前登陆用户的密码
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/modifyLoginPwd")
    public void modifyLoginPwd() throws Exception {
        String oldPwd = ControllerContext.getPString("oldPwd");
        String newPwd = ControllerContext.getPString("pwdOne");
        String newSPwd = ControllerContext.getPString("pwdTwo");
        String userId = ControllerContext.getContext().getCurrentUserId();
        String agentId = getCurrentAgent().get("_id").toString();
        Map<String, Object> map = MysqlDaoImpl.getInstance().findById2Map("User", userId, new String[]{"password"}, Dao.FieldStrategy.Include);
        if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(newSPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
        }
        if (!Pattern.matches("^[0-9a-zA-Z]{6,16}$", newPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请输入6~16位字母或者数字作为密码!");
        }
        if (!Pattern.matches("^[0-9a-zA-Z]{6,16}$", newSPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请输入6~16位字母或者数字作为密码!");
        }
        if (!newPwd.equals(newSPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次输入密码不一致!");
        }
        if (oldPwd.equals(newPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "新密码与原密码不能相同!");
        }
        if (!map.get("password").equals(MessageDigestUtils.digest(oldPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "原密码输入错误!");
        }
        String sql = "update User set password=? where _id=? and agentId=?";
        List<Object> p = new ArrayList<>();
        p.add(MessageDigestUtils.digest(newPwd));
        p.add(userId);
        p.add(agentId);
        MysqlDaoImpl.getInstance().exeSql(sql, p, "User");


    }

    /**
     * 获取当前登陆管理员拥有的号段
     *
     * @throws Exception
     */
    @POST
    @Path("/agentGetAdminCardField")
    public void agentGetAdminCardField() throws Exception {
        String _id = getCurrentAgent().get("_id").toString();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String sql = "select" +
                " startCardNo" +
                ",endCardNo" +
                ",cardNum" +
                " from CardField" +
                " where receive=? order by createTime";
        p.add(_id);
        r.add("startCardNo");
        r.add("endCardNo");
        r.add("cardNum");
        List<Map<String, Object>> adminCardFiled = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        String sql1 = "select" +
                " startCardNo" +
                ",endCardNo" +
                ",cardNum" +
                " from CardField" +
                " where belongAreaValue='admin'";
        List<Map<String, Object>> adminSpecial = MysqlDaoImpl.getInstance().queryBySql(sql1, r, null);
        Map<String, Object> reMap = new HashMap<>();
        reMap.put("adminCardFiled", adminCardFiled);
        reMap.put("adminSpecial", adminSpecial);
        reMap.put("adminLevel", getCurrentAgent().get("level"));
        toResult(Response.Status.OK.getStatusCode(), reMap);
    }

    /**
     * 1.获取当前登陆代理商旗下归属代理商的号段
     * 2.当entityName不为空时,查询发卡/回收卡记录
     * @throws Exception
     */
    @GET
    @Path("/getCardByBelong")
    public void getCardByBelong() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String agentName = ControllerContext.getPString("_agentName");
        String agentLevel = ControllerContext.getPString("_agentLevel");
        String entityName = ControllerContext.getPString("_entityName");
        String cardNo = ControllerContext.getPString("_cardNo");
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        Map<String,Object> curAgent = getCurrentAgent();
        String areaValueCur = curAgent.get("areaValue").toString();

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }

        if(StringUtils.isEmpty(areaValueCur)){
            throw new UserOperateException(500,"获取代理商数据失败");
        }
        //如果客户端传归属value,则判断是否属于当前归属之下
        if(StringUtils.isNotEmpty(areaValue)){
            areaValue = areaValue.replaceAll("___like_", "");

            String[] areaValueList = areaValue.substring(2,areaValue.length()-2).split("\\\\_");
            areaValue="";
            for(int i=0,len=areaValueList.length;i<len;i++){
                areaValue+="_"+areaValueList[i];
            }
            areaValue+="_";

            if (!Pattern.matches("^(" + areaValueCur + "\\S*)$", areaValue)) {
                throw new UserOperateException(500, "您不能越级查看其它省市的代理商");
            }
            areaValueCur=areaValue;
        }

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where t1.belongAreaValue like ?";
        p.add(areaValueCur+"%");

        if(StringUtils.isEmpty(entityName)){
            entityName="CardField";
        }else{
            entityName="GrantCardLog";
            where+=" and t1.`type`=?";
            String type = ControllerContext.getPString("_type");
            if(StringUtils.isNotEmpty(type)){
                p.add(type);
            }else{
                p.add("1");//默认查询发卡记录
            }
        }

        if(StringUtils.isNotEmpty(agentLevel)
                && Integer.parseInt(curAgent.get("level").toString())<=Integer.parseInt(agentLevel)){
            where+=" and t3.level=?";
            p.add(Integer.parseInt(agentLevel));
        }

        if(StringUtils.isNotEmpty(agentName)){
            where+=" and t3.name like ?";
            p.add("%"+agentName+"%");
        }

        if (startTime != 0) {
            where += " and t1.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(cardNo)) {
            where += " and (cast(t1.startCardNo as SIGNED INTEGER)<=? and cast(t1.endCardNo as SIGNED INTEGER)>=?)";
            p.add(cardNo);
            p.add(cardNo);
        }

        r.add("totalCount");
        String sql = "select" +
                " count(t1._id) as totalCount" +
                " from "+entityName+" t1" +
                " left join Agent t2 on t1.grant=t1._id" +
                " left join Agent t3 on t1.receive=t3._id" +
                " left join Factor t4 on t1.receive=t4._id"+
                where;
        List<Map<String, Object>> count = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        Long totalNum = 0L;
        if (count.size() != 0) {
            totalNum = Long.parseLong(String.valueOf(count.get(0).get("totalCount")));
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();
        sql = "select" +
                " t1._id" +
                ",t1.startCardNo" +
                ",t1.endCardNo" +
                ",t1.cardNum" +
                ",t1.grant" +
                ",t1.receive" +
                ",t1.createTime" +
                ",t2.name as grantName" +
                ",t3.name as receiveNameAgent" +
                ",t4.name as receiveNameFactor" +
                " from "+entityName+" t1" +
                " left join Agent t2 on t1.grant=t2._id" +
                " left join Agent t3 on t1.receive=t3._id" +
                " left join Factor t4 on t1.receive=t4._id" +
                where +
                " order by t1.createTime desc" +
                " limit "+page.getStartIndex()+","+pageSize;
        r.add("_id");
        r.add("startCardNo");
        r.add("endCardNo");
        r.add("cardNum");
        r.add("grant");
        r.add("receive");
        r.add("createTime");
        r.add("grantName");
        r.add("receiveNameAgent");
        r.add("receiveNameFactor");
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 根据代理商区域 value获取代理商下级
     *
     * @throws Exception
     */
    @GET
    @Path("/getAgentByValue")
    public void getAgentByValue() throws Exception {
        String areaValue = ControllerContext.getPString("areaValue");
        String isFactorCanUse = ControllerContext.getPString("isFactorCanUse");
        String areaValueCur = getCurrentAgent().get("areaValue").toString();

        // 获取当前登录账号的区域value,和查询的value进行比较,是否超出该代理商的查询范围
        if (!Pattern.matches("^(" + areaValueCur + "\\S*)$", areaValue)) {
            throw new UserOperateException(400, "您无此查询权限!");
        }

        List<String> returnFields = new ArrayList<>();

        returnFields.add("_id");
        returnFields.add("name");
        returnFields.add("areaValue");

        int grade = areaValue.substring(1, areaValue.length() - 1).split("_").length;
        if (grade != 4) {
            areaValue = areaValue.replace("_", "\\_");

            String sql = "select" +
                    " _id" +
                    ",name" +
                    ",areaValue" +
                    " from Agent" +
                    " where areaValue REGEXP '^(" + areaValue + ")[^\\_]+\\_$'";
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
            toResult(Response.Status.OK.getStatusCode(), re);
        } else {
            String where = "";
            if (StringUtils.isNotEmpty(isFactorCanUse) && Boolean.valueOf(isFactorCanUse)) {
                where = " and canUse = true";
            }
            areaValue = areaValue.replace("_", "\\_");
            String sql = "select" +
                    " _id" +
                    ",name" +
                    ",areaValue" +
                    " from Factor" +
                    " where areaValue REGEXP '^(" + areaValue + ")[^\\_]+\\_$'" + where;
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, null);
            toResult(Response.Status.OK.getStatusCode(), re);
        }


    }

    /**
     * 根据代理商区域 value获取代理商名字(无需登录)
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getAgentNameByValue")
    public void getAgentNameByValue() throws Exception {
        String areaValue = ControllerContext.getPString("areaValue");
        toResult(Response.Status.OK.getStatusCode(), getAgentNameByValue(areaValue));
    }

    public Map<String, Object> getAgentNameByValue(String areaValue) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("areaValue", areaValue);
        return MysqlDaoImpl.getInstance().findOne2Map("Agent", params, new String[]{"name"}, Dao.FieldStrategy.Include);
    }

    /**
     * 平台管理:会员卡统计
     */
    @GET
    @Path("/agentQueryMemberCardNum")
    public void agentQueryMemberCardNum() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue").replaceAll("___like_", "");
        String name = ControllerContext.getPString("_name");
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        List<String> r = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        String entityName="Agent";
//        int level = areaValue.substring(2,areaValue.length()-2).split("\\_").length;
//        if(level>=4){
//            entityName="Factor";
//        }

        String where = " where (t1.areaValue like ? or t3.areaValue like ?)";
        String leftJoin = " from "+entityName+" t1" +
                " right join GrantCardLog t2 on t1._id=t2.receive" +
                " left join Factor t3 on t2.receive=t3._id";
        p.add(areaValue+"%");
        p.add(areaValue+"%");

//        if(entityName.equals("Agent")){
//            where += " and (t1.adminType is null or t1.adminType=0)";
//        }
        if(StringUtils.isNotEmpty(name)){
            where += " and (t1.name like ? or t3.name like ?)";
            p.add("%"+name+"%");
            p.add("%"+name+"%");
        }

        r.add("totalCount");
        String sql = "select count(distinct t1._id) as totalCount"+leftJoin+where;
        List<Map<String, Object>> count = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        Long totalNum = 0L;
        if (count.size() != 0) {
            totalNum = Long.parseLong(String.valueOf(count.get(0).get("totalCount")));
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        // 回收卡
        sql = "select" +
                " t1.name as receiveAgentName" +
                ",t1.belongArea as belongAreaAgent" +
                ",t1.areaValue" +
                ",t3.name as receiveFactorName" +
                ",t3.belongArea as belongAreaFactor" +
                ",t3.areaValue as belongAreaValueFactor" +
//                ",sum(case when t2.type='1' then t2.cardNum else 0 end) as grantNum" +
                ",sum(case when t2.type='2' then t2.cardNum else 0 end) as backNum"+
                leftJoin +
                where +
                " group by t2.receive" +
                " limit "+page.getStartIndex()+","+pageSize;
        r.clear();
        r.add("receiveAgentName");
        r.add("belongAreaAgent");
        r.add("areaValue");
        r.add("receiveFactorName");
        r.add("belongAreaFactor");
        r.add("belongAreaValueFactor");
//        r.add("grantNum");
        r.add("backNum");

        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);

        // 分配卡
        leftJoin = " from "+entityName+" t1" +
                " right join CardField t2 on t1._id=t2.receive" +
                " left join Factor t3 on t2.receive=t3._id";

        sql = "select" +
                " t1.areaValue" +
                ",t3.name" +
                ",sum(t2.cardNum) as grantNum" +
                ",t3.areaValue as belongAreaValueFactor" +
                leftJoin +
                where + " and t2.belongAreaValue is not null"+
                " group by t2.receive" +
                " limit "+page.getStartIndex()+","+pageSize;
        r.clear();
        r.add("areaValue");
        r.add("name");
        r.add("grantNum");
        r.add("belongAreaValueFactor");

        List<Map<String,Object>> fen = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);

        for(int k=0,klen = re.size();k<klen;k++){
            for(int j=0,jlen = fen.size();j<jlen;j++){
                if(StringUtils.mapValueIsEmpty(re.get(k),"areaValue")){
                    if(re.get(k).get("belongAreaValueFactor").equals(fen.get(j).get("belongAreaValueFactor"))){
                        re.get(k).put("grantNum",fen.get(j).get("grantNum"));
                        break;
                    }
                }else{
                    if(re.get(k).get("areaValue").equals(fen.get(j).get("areaValue"))){
                        re.get(k).put("grantNum",fen.get(j).get("grantNum"));
                        break;
                    }
                }
            }
        }

        //添加每个代理商/发卡点的激活卡数量
        if (re != null && re.size() > 0) {
            sql = "select count(t1._id) as activeNum" +
                    " from CardField t1";
            r.clear();
            r.add("activeNum");
            for (int i = 0; i < re.size(); i++) {
                String sql2= "";
                p.clear();
                sql2=sql+" left join Factor t2 on t2._id=t1.grant" +
                        " where t2.areaValue like ? and (t1.belongAreaValue is null or t1.belongAreaValue='')";
                if(StringUtils.mapValueIsEmpty(re.get(i),"areaValue")) {
                    p.add(re.get(i).get("belongAreaValueFactor") + "%");
                }else{
                    if(re.get(i).get("areaValue").equals("_A-000001_")){
                        sql2=sql+" left join Agent t2 on t2._id=t1.grant" +
                                " where t1.grant = 'A-000001' and (t1.belongAreaValue is null or t1.belongAreaValue='')";
                    }else{
                        p.add(re.get(i).get("areaValue") + "%");
                    }
                }

                List<Map<String, Object>> cList = MysqlDaoImpl.getInstance().queryBySql(sql2, r, p);
                if (cList != null && cList.size() > 0){
                    re.get(i).put("activeNum", cList.get(0).get("activeNum"));
                }
            }
        }

        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 查询会员卡
     */
    @GET
    @Path("/agentQueryMemberCardInfo")
    public void agentQueryMemberCardInfo() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String memberCardId = ControllerContext.getPString("_memberCardId");
        String cardStatus = ControllerContext.getPString("_cardStatus");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }


        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        String areaValueCur = getCurrentAgent().get("areaValue").toString();

        String where = " where 1=1 ";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();


        if (StringUtils.isNotEmpty(memberCardId)) {
            where += " and t1.cardNo like ?";
            p.add(memberCardId+"%");
        }
        if (startTime != 0) {
            where += " and t4.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t4.createTime<?";
            p.add(endTime);
        }
//        if (StringUtils.isNotEmpty(cardStatus)) {
//            if ("Y".equals(cardStatus)) {
//                where += " and t1.isActive=true";
//            } else if ("N".equals(cardStatus)) {
//                where += " and (t1.isActive<>true or ISNULL(t1.isActive))";
//            }
//        }

//        String name = "";
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
//            int grade = areaValue.substring(1, areaValue.length() - 1).split("_").length;
//            if (grade != 4) {
//                name += ",3.name";
//                where += " and t2.areaValue like ?";
//                p.add(areaValue + "%");
//            } else {
//                name += ",t2.name";
//                where += " and t2.areaValue like ?";
//                p.add(areaValue + "%");
//            }
            where += " and t1.belongAreaValue like ?";
            p.add(areaValue + "%");
        }
        String hql = "select" +
                " count(t1._id) as totalCount" +
                " from Member t1" +
                " inner join Factor t2 on t2.areaValue=t1.belongAreaValue" +
                " inner join Agent t3 on  t3._id = t2.pid" +
                " inner join MemberCard t4 on t1._id = t4.memberId" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);
        r.clear();
        r.add("memberCardId");
        r.add("isActive");
        r.add("createTime");
        r.add("realName");
        r.add("mobile");
        r.add("realArea");
        r.add("belongArea");
        String sql = "select" +
                " t4.memberCardId" +
                ",t4.isActive" +
                ",t4.createTime" +
                ",t1.realName" +
                ",t1.mobile" +
                ",t1.realArea" +
                ",t1.belongArea" +
                " from Member t1" +
                " inner join Factor t2 on t2.areaValue=t1.belongAreaValue" +
                " inner join Agent t3 on  t3._id = t2.pid" +
                " inner join MemberCard t4 on t1._id = t4.memberId" +
                where + " order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 检查当前登陆会员是否被禁用
     */
    @GET
    @Path("/getAgentIsTrue")
    public void getAgentIsTrue() throws Exception {
        String agentId = ControllerContext.getPString("agentId");
        Map<String, Object> agent = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, new String[]{"canUse"}, Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), agent);
    }

    /**
     * 查询会员激活数量
     */
    @GET
    @Path("/getAgentActiveNum")
    public void getAgentActiveNum() throws Exception {
        String areaValue = ControllerContext.getPString("_areaValue");
        String agentId = ControllerContext.getPString("_agentId");
        String createTime = ControllerContext.getPString("_createTime");
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }

        String where = " where 1=1 ";
        String groupby = "";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            int grade = areaValue.substring(1, areaValue.length() - 1).split("_").length;
            if (grade < 5) {
                where += " and t1.areaValue REGEXP '^(" + areaValue + ")[^\\_]+\\_$'";
                groupby = " group by t1._id";
                if (startTime != 0) {
                    where += " and t1.createTime>?";
                    p.add(startTime);
                }
                if (endTime != 0) {
                    where += " and t1.createTime<?";
                    p.add(endTime);
                }
            } else {

                where += " and t2.areaValue REGEXP '^(" + areaValue + ")[^\\_]+\\_$'";
                groupby = " group by t2._id";
                if (startTime != 0) {
                    where += " and t2.createTime>?";
                    p.add(startTime);
                }
                if (endTime != 0) {
                    where += " and t2.createTime<?";
                    p.add(endTime);
                }
            }

        } else {
            areaValue = "_" + getCurrentAgent().get("_id").toString() + "_";
            where += " and t1.areaValue REGEXP '^(" + areaValue + ")[^\\_]+\\_$'";
            groupby = " group by t1._id";
        }
        if (StringUtils.isNotEmpty(agentId) && !"undefined".equals(agentId)) {
            where += " and t1._id = ?";
            p.add(agentId);
        }

        String sql = "select" +
                " " +
                " from Agent t1" +
                " left join Factor t2 on";

    }

}

