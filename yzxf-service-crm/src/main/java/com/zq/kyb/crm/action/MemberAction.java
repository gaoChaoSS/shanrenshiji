package com.zq.kyb.crm.action;


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
import com.zq.kyb.core.secu.SessionServiceImpl;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.HttpClientUtils;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MemberAction extends BaseActionImpl {

    @PUT
    @Path("/saveMyInfo")
    @Member
    public void saveMyInfo() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Map<String, Object> p = new HashMap<>();
        p.put("_id", memberId);
        p.put("icon", ControllerContext.getPString("icon"));

        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, p);
    }

    // 多种方式登录
    @POST
    @Member
    @Path("/loginUserPass")
    public void loginUserPass() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();
        String userNameType = ControllerContext.getPString("userNameType");
        String deviceId = ControllerContext.getPString("deviceId");

        if (StringUtils.isEmpty(deviceId)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "设备Id为空!");
        }

        Map member=null;

        if(req.containsKey("key")){
            if(!req.containsKey("val")){
                throw new UserOperateException(500,"登录失败");
            }
            Message m = Message.newReqMessage("1:POST@/account/Encryption/decryptorMap");
            m.getContent().put("key",req.get("key"));
            m.getContent().put("val",req.get("val"));
            JSONObject data = ServiceAccess.callService(m).getContent();
//            loginName = data.get("sellerId").toString();
//            password = data.get("password").toString();

            Map<String,Object> params = new HashMap<>();
            params.put("_id",data.get("memberId"));
            params.put("password",data.get("password"));

            member = MysqlDaoImpl.getInstance().findOne2Map("Member",params,null,null);
        }else{
            if (StringUtils.isEmpty(userNameType)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "登录用户名类型为空!");
            }
            if (!req.containsKey("password")) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
            }
            Map account=null;
            if (userNameType.equals("mobile")) {
                Map<String, Object> m = new HashMap<>();
                m.put("mobile", ControllerContext.getPString("mobile"));
                account = MysqlDaoImpl.getInstance().findOne2Map("Member", m, null, null);
                if (account == null) {
                    throw new UserOperateException(400, "账号不存在请先注册!");
                }
            } else if (userNameType.equals("cardNo")) {
                Map<String, Object> m = new HashMap<>();
                m.put("cardNo", ControllerContext.getPString("cardNo"));
                account = MysqlDaoImpl.getInstance().findOne2Map("Member", m, null, null);
                if (account == null) {
                }
            } else if (userNameType.equals("idCard")) {
                Map<String, Object> m = new HashMap<>();
                m.put("idCard", ControllerContext.getPString("idCard"));
                account = MysqlDaoImpl.getInstance().findOne2Map("Member", m, null, null);
                if (account == null) {
                    throw new UserOperateException(400, "账号不存在请先注册!");
                }
            }

            if (userNameType.equals("mobile")) {
                if (!req.containsKey("mobile")) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号码不能为空");
                }
                Map<String, Object> p = new HashMap<>();
                p.put("mobile", ControllerContext.getPString("mobile"));
                p.put("password", MessageDigestUtils.digest(ControllerContext.getPString("password")));
                member = MysqlDaoImpl.getInstance().findOne2Map("Member", p, null, null);
            } else if (userNameType.equals("cardNo")) {
                if (!req.containsKey("cardNo")) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "卡号不能为空");
                }
                Map<String, Object> p = new HashMap<>();
                p.put("cardNo", ControllerContext.getPString("cardNo"));
                p.put("password", MessageDigestUtils.digest(ControllerContext.getPString("password")));
                member = MysqlDaoImpl.getInstance().findOne2Map("Member", p, null, null);

            } else if (userNameType.equals("idCard")) {
                if (!req.containsKey("idCard")) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证号不能为空");
                }
                Map<String, Object> p = new HashMap<>();
                p.put("idCard", ControllerContext.getPString("idCard"));
                p.put("password", MessageDigestUtils.digest(ControllerContext.getPString("password")));
                member = MysqlDaoImpl.getInstance().findOne2Map("Member", p, null, null);
            }
        }

        if (member == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户名或密码错误!");
        }
        if (member.get("canUse") == null || !(Boolean) member.get("canUse")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户已经被禁用");
        }
        member.remove("password");
        JSONObject rObj = loginAfter(deviceId, member);

        toResult(200, rObj);
    }

    //注册
    @POST
    @Member
    @Seller
    @Path("/register")
    public void register() throws Exception {
        String deviceId = ControllerContext.getPString("deviceId");
        String mobile = ControllerContext.getPString("mobile");
        String verification = ControllerContext.getPString("verification");
        String password = ControllerContext.getPString("password");
        String secondPwd = ControllerContext.getPString("secondPwd");
        String autoReg = ControllerContext.getPString("autoReg");

        String shareId = ControllerContext.getPString("shareId");
        String shareType = ControllerContext.getPString("shareType");
        String memberType = ControllerContext.getPString("memberType");

        if (StringUtils.isEmpty(verification)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "验证码不能为空!");
        }

        if(StringUtils.isEmpty(ControllerContext.getPString("notCode"))){
            Message msg = Message.newReqMessage("1:PUT@/common/Sms/checkSmsCode");
            msg.getContent().put("type", "reg");
            msg.getContent().put("smsCode", verification);
            msg.getContent().put("phone", mobile);
            ServiceAccess.callService(msg);
        }
        if(memberType.equals("1")){
            if (!"Yes".equals(autoReg)) {
                if (StringUtils.isEmpty(deviceId)) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "设备Id不能为空!");
                }
                if (StringUtils.isEmpty(mobile) || !Pattern.matches("^1[3456789]{1}\\d{9}$", mobile)) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
                }

                if (StringUtils.isEmpty(password)) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
                }
                if (!Pattern.matches("^[0-9a-zA-Z]{6,16}$", password)) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请输入6~16位字母或者数字作为密码!");
                }
                if (!password.equals(secondPwd)) {
                    throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次密码输入不一致");
                }
            }
        }

        JSONObject queryUserParams = new JSONObject();
        queryUserParams.put("mobile", mobile);
        Map<String, Object> member = MysqlDaoImpl.getInstance().findOne2Map("Member", queryUserParams, null, null);
        String memberId = "M-" + ZQUidUtils.generateMemberNo();
        if(memberType.equals("1")){
            if (member != null) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该手机号码已被注册");
            } else {
                if ("Yes".equals(autoReg)) {//未被注册且是自动注册
                    //获取 服务站 的归属信息;服务站注册的会员自动归属到此服务站下，即使会员回到会员版激活，默认归属到此服务站
                    String factorId = ControllerContext.getPString("shareId");
                    String url = "1:GET@/account/Factor/";
                    if (StringUtils.isEmpty(factorId)) {
                        url += "getFactorInfo";
                    } else {
                        url += "getFactorBaseById";
                    }
                    Message msg = Message.newReqMessage(url);
                    msg.getContent().put("_id", factorId);
                    JSONObject factor = ServiceAccess.callService(msg).getContent();
                    if (factor == null || factor.size() == 0) {
                        throw new UserOperateException(500, "获取服务站信息失败");
                    }

                    member = new HashMap<>();
                    member.put("mobile", mobile);
                    member.put("password", MessageDigestUtils.digest("000000"));
                    member.put("_id", memberId);
                    member.put("memberNo", JedisUtil.incr("MemberNoSeq"));

                    member.put("belongArea", factor.get("name"));
                    member.put("belongAreaValue", factor.get("areaValue"));

                    member.put("createTime", System.currentTimeMillis());
                    member.put("canUse", true);
                    member.put("isFree", true);
                    MysqlDaoImpl.getInstance().saveOrUpdate("Member", member);
                    List<Object> p = new ArrayList<>();
                    List<String> r = new ArrayList<>();
                    p.add(mobile);
                    r.add("_id");
                    String sql = "select _id from Member where mobile=?";
                    List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
                    re.get(0).put("memberType","1");
                    toResult(Response.Status.OK.getStatusCode(), re.get(0));
                }else {
                    //未注册且非自动注册 这里原本没有归属信息，需添加归属信息 AliCao
                    member = new HashMap<>();
                    member.put("mobile", mobile);
                    member.put("password", MessageDigestUtils.digest(password));
                    member.put("_id", memberId);
                    getBelong(member, shareType, shareId);
                    member.put("memberNo", JedisUtil.incr("MemberNoSeq"));
                    member.put("createTime", System.currentTimeMillis());
                    member.put("canUse", true);
                    member.put("isFree", true);
                    MysqlDaoImpl.getInstance().saveOrUpdate("Member", member);
                    member.put("memberType","1");
                    toResult(Response.Status.OK.getStatusCode(),member);
                }

            }
            //未注册且非自动注册 这里原本没有归属信息，需添加归属信息 AliCao
//        if (member == null) {
//            member = new HashMap<>();
//            member.put("mobile", mobile);
//            member.put("password", MessageDigestUtils.digest(password));
//            member.put("_id", memberId);
//            getBelong(member, shareType, shareId);
//            member.put("memberNo", JedisUtil.incr("MemberNoSeq"));
//            member.put("createTime", System.currentTimeMillis());
//            member.put("canUse", true);
//            member.put("isFree", true);
//            MysqlDaoImpl.getInstance().saveOrUpdate("Member", member);
//        }
            //生成会员现金汇总表
            Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/createMemberAccount");
            msg.getContent().put("memberId", memberId);
            ServiceAccess.callService(msg);

            //生成养老金汇总表
            msg = Message.newReqMessage("1:GET@/order/OrderInfo/createPensionAccount");
            msg.getContent().put("memberId", memberId);
            ServiceAccess.callService(msg);

            //生成团队关系
            createTeam(memberId,shareId,shareType);
        }else if(memberType.equals("2")) {
            member = new HashMap<>();
            getBelong(member, shareType, shareId);
            List<Object> p = new ArrayList<>();
            List<String> r = new ArrayList<>();
            p.add(mobile);
            r.add("_id");
            String sql = "select _id from Member where mobile=?";
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            member.put("_id",re.get(0).get("_id"));
            MysqlDaoImpl.getInstance().saveOrUpdate("Member", member);
            re.get(0).put("memberType","2");
            toResult(Response.Status.OK.getStatusCode(), re.get(0));
        }

    }

    private void getBelong(Map<String, Object> member, String shareType, String shareId) throws Exception {
        System.out.println("Map="+JSONObject.fromObject(member));
        System.out.println("shareType="+shareType);
        System.out.println("shareId="+shareId);
        String belongAreaValue = "";
        String belongArea = "";
        Map<String, Object> queryParm = new HashMap<>();
        queryParm.put("_id", shareId);
        String[] returnParm = new String[]{"belongAreaValue", "belongArea"};
        if(shareType != null){
            if (shareType.equals("Member")) {
                Map<String, Object> tempMember = MysqlDaoImpl.getInstance().findOne2Map("Member", queryParm, returnParm, Dao.FieldStrategy.Include);
                belongAreaValue = tempMember.get("belongAreaValue").toString();
                belongArea = tempMember.get("belongArea").toString();
            } else if (shareType.equals("Seller")) {
                List<Object> params = new ArrayList<>();
                params.add(shareId);
                List<String> returnFields = new ArrayList<>();
                returnFields.add("belongAreaValue");
                returnFields.add("belongArea");
                String sql = "select belongArea ,belongAreaValue from Seller where _id =?";
                List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
                belongAreaValue = list.get(0).get("belongAreaValue").toString();
                belongArea = list.get(0).get("belongArea").toString();
            } else if (shareType.equals("Factor")) {
                List<Object> params = new ArrayList<>();
                params.add(shareId);
                List<String> returnFields = new ArrayList<>();
                returnFields.add("areaValue");
                returnFields.add("name");
                String sql = "select name ,areaValue from Factor where _id =?";
                List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
                belongAreaValue = list.get(0).get("areaValue").toString();
                belongArea = list.get(0).get("name").toString();
            }
        } else {
            belongAreaValue = "_A-000001";
            belongArea = "普惠生活-平台";
        }
        member.put("belongAreaValue", belongAreaValue);
        member.put("belongArea", belongArea);
    }

    @POST
    @Member
    @Path("/login")
    public void login() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();
        if (!req.containsKey("loginName")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "loginName_is_null");
        }
        if (!req.containsKey("checkCode")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "checkCode_is_null");
        }

        String checkCode = req.getString("checkCode");
        // device
        String deviceId = req.getString("deviceId");
        // expireTime
        String loginName = req.getString("loginName");
        //EmpUtils.checkSmsCode(EmpUtils.FROM_MOBILE, checkCode, loginName);
        JSONObject queryUserParams = new JSONObject();
        queryUserParams.put("loginName", loginName);
        Map<String, Object> m = MysqlDaoImpl.getInstance().findOne2Map("Member", queryUserParams, null, null);
        if (m != null && m.get("canUse") != null && !(Boolean) m.get("canUse")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户已经被禁用");
        }
        if (m == null) {
            m = new HashMap<>();
            m.put("loginName", loginName);
            m.put("_id", UUID.randomUUID().toString());
            m.put("memberNo", ZQUidUtils.generateMemberNo());
            m.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("Member", m);
        }
        //如果是第3方登录则进行绑定
        String bindId = ControllerContext.getPString("bindId");
        if (StringUtils.isNotEmpty(bindId)) {
            Map<String, Object> auth = MysqlDaoImpl.getInstance().findById2Map("Oauth", bindId, null, null);
            if (auth != null) {
                auth.put("bind", true);
                auth.put("memberId", m.get("_id"));
                auth.put("updateTime", System.currentTimeMillis());
                auth.put("canUse", true);
                MysqlDaoImpl.getInstance().saveOrUpdate("Oauth", auth);
            }

            byte[] img = HttpClientUtils.simpleGetInvokeByte(String.valueOf(auth.get("logoUrl")));
            String fileId = UUID.randomUUID().toString();
            Map<String, Object> f = new HashMap<String, Object>();
            f.put("_id", fileId);
            f.put("name", "icon.jpg");
            f.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("FileItem", f);


            //TODO
            //new FileDaoBaseImpl().saveFile(fileId, new ByteArrayInputStream(img));
            List<Map<String, Object>> file = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("_id", fileId);
            file.add(map);
            m.put("icon", file);
            MysqlDaoImpl.getInstance().saveOrUpdate("Member", m);
        }

        JSONObject rObj = loginAfter(deviceId, m);
        toResult(200, rObj);
        // WebSocketChatService.putUserConn(ControllerContext.getContext().getCurrentUserId());
    }

    @PUT
    @Member
    @Path("/auth")
    public void auth() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();
        String token = req.getString("token");
        // ControllerContext.getContext().setToken(token);
        SessionServiceImpl.checkToken(token);
        ControllerContext.getContext().setToken(token);
        String currentUserId = ControllerContext.getContext().getCurrentUserId();
        Map<String, Object> member = MysqlDaoImpl.getInstance().findById2Map("Member", currentUserId, null, null);
        if (member == null) {
            throw new UserOperateException(400, "验证失败");
        }
        JSONObject rObj = new JSONObject();
        rObj.put("name", member.get("name"));
        rObj.put("token", token);
        toResult(200, rObj);
    }


    public static JSONObject loginAfter(String deviceId, Map<String, Object> m) throws Exception {
        Map<String, Object> session = new HashMap<String, Object>();
        // session
        session.put("creator", m.get("_id"));
        session.put("loginName", m.get("loginName"));
        // session.put("nickName", user.get("nickName"));
        session.put("type", "member");
        session.put("deviceId", deviceId);

        //Long expireTime = -1L;
        //if (req.containsKey("expireTime")) {
        //   expireTime = req.getLong("expireTime");
        //   expireTime = expireTime == null ? -1 : expireTime;
        //}
        // if (expireTime == -1) {
        Long expireTime = 3600000 * 24 * 365 * 3L;// 3年
        // }
        session.put("expireTime", (System.currentTimeMillis() + expireTime));
        session.put("sellerId", "member");

        SessionServiceImpl ss = new SessionServiceImpl();
        session = ss.startSession(session);
        String sessionFullStr = (String) session.get("sessionFullStr");
        ControllerContext.getContext().setToken(sessionFullStr);

        // 将用户的权限资源Map存到cache
        // MemcachedService.getInstance().cacheEntity(loginName, entityId, value, duration);

        JSONObject rObj = new JSONObject();
        rObj.put("token", sessionFullStr);
        rObj.putAll(m);
        rObj.remove("password");

        return rObj;
    }

    /**
     * 获取验证码之前检查该手机号是否已经注册
     *
     * @throws Exception
     */
    @GET
    @Path("/getMobileIsReg")
    public void getMobileIsReg() throws Exception {
        String mobile = ControllerContext.getPString("mobile");
        Map<String, Object> v = new HashMap<>();
        v.put("mobile", mobile);
        Map<String, Object> map = MysqlDaoImpl.getInstance().findOne2Map("Member", v, new String[]{"_id","belongAreaValue"}, Dao.FieldStrategy.Include);
        //判断用户是否已注册：1.未注册，2.已注册且归属于平台,3.已注册不归属于平台
        if(map == null){
            map = new HashMap<>();
            map.put("memberType","1");
        }else {
            if(map.get("belongAreaValue").toString().equals("_A-000001")){
                map.put("memberType","2");
            }else {
                map.put("memberType","3");
            }
        }
        System.out.println("map=="+JSONObject.fromObject(map));
        toResult(Response.Status.OK.getStatusCode(), map);
    }


    /**
     * 获取验证码之前检查该手机号是否已经注册
     *
     * @throws Exception
     */
    @GET
    @Path("/getMobileReg")
    public void getMobileReg() throws Exception {
        String mobile = ControllerContext.getPString("mobile");
        Map<String, Object> v = new HashMap<>();
        v.put("mobile", mobile);
        Map<String, Object> map = MysqlDaoImpl.getInstance().findOne2Map("Member", v, new String[]{"_id"}, Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), map);
    }

    /**
     * 获取当前登录会员的信息
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMyInfo")
    public void getMyInfo() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Map<String, Object> re = dao.findById2Map("Member", memberId, new String[]{"password"}, Dao.FieldStrategy.Exclude);
        //同时查询该用户是否有正在处理的订单和刚刚赠送的积分和优惠券
        if (re == null) {
            throw new UserOperateException(404, "客户不存在");
        }

        // 查询关联商户
        List<String> returnFields = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        returnFields.add("sellerId");
        returnFields.add("sellerName");
        String sql = "select" +
                " t1.sellerId" +
                ",t2.name as sellerName" +
                " from User t1 " +
                " left join Seller t2 on t1.sellerId = t2._id" +
                " where t1.memberId = ?";
        List<Map<String,Object>> store = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(store!=null && store.size()!=0){
            re.put("sellerInfo",store.get(0));
        }

        toResult(200, re);
    }

    /**
     * 平台:根据ID获取会员信息
     */
    @GET
    @Path("/getMyInfoById")
    public void getMyInfoById() throws Exception {
        String memberId= ControllerContext.getPString("memberId");
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        if (StringUtils.isEmpty(memberId)) {
            throw new UserOperateException(400, "找不到该用户");
        }

        Map<String,Object> re = MysqlDaoImpl.getInstance().findById2Map("Member", memberId, null, null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 获取用户信息
     */
    @GET
    @Path("/getMyInfoById2")
    public void getMyInfoById2() throws Exception {
        String _id= ControllerContext.getPString("_id");
        String mobile= ControllerContext.getPString("mobile");

        String[] fields = new String[]{"password","payPwd"};
        Map<String,Object> re;
        Map<String,Object> params = new HashMap<>();

        if(StringUtils.isNotEmpty(_id)){
            params.put("_id",_id);
        }else {
            params.put("mobile",mobile);
        }
        re = MysqlDaoImpl.getInstance().findOne2Map("Member",params,fields, Dao.FieldStrategy.Exclude);

        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员修改密码
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/modifyMyPwd")
    public void modifyMyPwd() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        String firstPwd = ControllerContext.getContext().getPString("firstPwd");
        String secondPwd = ControllerContext.getContext().getPString("secondPwd");
        String oldPwd = ControllerContext.getContext().getPString("oldPwd");
        if (StringUtils.isEmpty(firstPwd) || StringUtils.isEmpty(secondPwd) || StringUtils.isEmpty(oldPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
        }
        if (!Pattern.matches("^[0-9a-zA-Z]{6,16}$", firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请输入6~16位字母或者数字作为密码!");
        }
        if (!Pattern.matches("^[0-9a-zA-Z]{6,16}$", secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请输入6~16位字母或者数字作为密码!");
        }
        if (!firstPwd.equals(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次输入密码不一致!");
        }

        if (oldPwd.equals(firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "新密码与原密码不能相同!");
        }
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findById2Map("Member", memberId, new String[]{"payPwd"}, Dao.FieldStrategy.Include);

        if (loginPwd.get("payPwd") != null && loginPwd.get("payPwd").equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "登录密码不能与支付密码一样!");
        }
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("password");
//        String sql = "select password from Member where _id=?";
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map(entityName, ControllerContext.getContext().getCurrentUserId(), null, null);
        List<Object> params = new ArrayList<>();
        params.add(MessageDigestUtils.digest(secondPwd));
        System.out.print(MessageDigestUtils.digest(oldPwd));
        params.add(ControllerContext.getContext().getCurrentUserId());
        if (MessageDigestUtils.digest(oldPwd).equals(re.get("password"))) {
            String updateSql = "update Member set password = ? where _id=?";
            MysqlDaoImpl.getInstance().exeSql(updateSql, params, "Member");
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "原密码输入错误!");
        }
    }


    /**
     * 我的钱包
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getWallet")
    public void getWallet() throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getCurrentUserId());
        List<String> returnFields = new ArrayList<>();
        returnFields.add("cashCount");
        returnFields.add("canWithdrawMoney");
        String sql = "select" +
                " cashCount,canWithdrawMoney" +
                " from MemberMoneyAccount" +
                " where memberId=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 设置支付密码
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/setPayPassword")
    public void setPayPassword() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        String firstPwd = ControllerContext.getPString("firstPwd");
        String secondPwd = ControllerContext.getPString("secondPwd");
        if (StringUtils.isEmpty(firstPwd) || StringUtils.isEmpty(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
        }
        if (!Pattern.matches("^[0-9]{6}$", firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码只能为6位数字!");
        }
        if (!firstPwd.equals(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次输入密码不一致!");
        }
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findById2Map("Member", memberId, new String[]{"password","payPwd"}, Dao.FieldStrategy.Include);
        if (!StringUtils.mapValueIsEmpty(loginPwd,"payPwd")){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "您已经设置过初始支付密码!");
        }
        if (loginPwd.get("password").equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登录密码一样!");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("_id", memberId);
        m.put("payPwd", MessageDigestUtils.digest(firstPwd));
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", m);

    }

    /**
     * 会员是否设置支付密码
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/isMemberPayPwd")
    public void isMemberPayPwd() throws Exception {

        List<String> returnFields = new ArrayList<String>();
        returnFields.add("payPwd");
        List<Object> params = new ArrayList<>();

        params.add(ControllerContext.getContext().getCurrentUserId());
        String sql = "select " +
                " payPwd" +
                " from Member" +
                " where _id=?";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        Map<String, Object> flag = new HashMap<>();
        flag.put("flag", false);
        if (StringUtils.isEmpty((String) re.get(0).get("payPwd"))) {
            flag.put("flag", true);
            toResult(Response.Status.OK.getStatusCode(), flag);
        }
        toResult(Response.Status.OK.getStatusCode(), flag);
    }

    /**
     * 会员修改支付密码
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/modifyMyPayPwd")
    public void modifyMyPayPwd() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
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
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findById2Map("Member", memberId, new String[]{"password"}, Dao.FieldStrategy.Include);
        if (loginPwd.get("password").equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登录密码一样!");
        }

        List<String> returnFields = new ArrayList<String>();
        returnFields.add("payPwd");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map(entityName, memberId, null, null);
        if (MessageDigestUtils.digest(oldPwd).equals(re.get("payPwd"))) {
            Map<String, Object> m = new HashMap<>();
            m.put("_id", memberId);
            m.put("payPwd", MessageDigestUtils.digest(secondPwd));
            MysqlDaoImpl.getInstance().saveOrUpdate("Member", m);
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "原密码输入错误!");
        }
    }

    /**
     * 会员养老金页面
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMyPensionMoney")
    public void getMyPensionMoney() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        if(pageNo!=null && indexNum==null){
            indexNum=pageNo-1;
        }
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String where = " where t1._id = ? and t2.orderType in (11) and t2.orderStatus=100";
        if (startTime != 0) {
            where += " and t2.endTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            where += " and t2.endTime<=?";
            params.add(endTime);
        }
        String hql = "select count(distinct t4.orderId) as totalCount" +
                " from Member t1" +
                " left join OrderInfo t2 on t1._id = t2.memberId" +
                " left join Seller t3 on t2.sellerId = t3._id" +
                " left join MemberPensionLog t4 on t2.orderNo=t4.orderId"
                + where;

        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<>();
        returnFields.clear();
        returnFields.add("orderNo");
        returnFields.add("createTime");
        returnFields.add("endTime");
        returnFields.add("payMoney");
        returnFields.add("pensionMoney");
        returnFields.add("o2o");
        returnFields.add("score");
        returnFields.add("sellerId");
        returnFields.add("sellerDoorImg");
        returnFields.add("name");
        returnFields.add("sellerIcon");
        returnFields.add("integralRate");
        returnFields.add("insureCount");
        returnFields.add("insureCountUse");
        returnFields.add("isInsure");

        String sql = "select" +
                " distinct t4.orderId as orderNo" +
                ",t2.createTime" +
                ",t2.endTime" +
                ",t2.payMoney" +
                ",t2.pensionMoney" +
                ",t2.o2o" +
                ",t2.score" +
                ",t3._id as sellerId" +
                ",t3.name" +
                ",t3.icon as sellerIcon" +
                ",t3.doorImg as sellerDoorImg" +
                ",t3.integralRate" +
                ",t4.insureCount" +
                ",t4.insureCountUse" +
                ",t4.isInsure" +
                " from Member t1" +
                " left join OrderInfo t2 on t1._id = t2.memberId" +
                " left join Seller t3 on t2.sellerId = t3._id" +
                " left join MemberPensionLog t4 on t2.orderNo=t4.orderId"
                + where + " order by t2.endTime desc limit " + indexNum + "," + pageSize;

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("orderList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 会员养老金页面线下订单
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMyPensionMoneyByOffline")
    public void getMyPensionMoneyByOffline() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> p = new ArrayList<>();
        p.add("totalCount");
        String where = " where t1._id = ? and t2.orderType in (0,1,7,8,13) and t2.orderStatus=100";
        if (startTime != 0) {
            where += " and t2.endTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            where += " and t2.endTime<=?";
            params.add(endTime);
        }
        String hql = "select count(t2._id) as totalCount" +
                " from Member t1" +
                " left join OrderInfo t2 on t1._id = t2.memberId" +
                " left join Seller t3 on t2.sellerId = t3._id" +
                " left join MemberPensionLog t4 on t2.orderNo=t4.orderId"
                + where;

        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, p, params);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        List<String> returnFields = new ArrayList<>();
        returnFields.clear();
        returnFields.add("createTime");
        returnFields.add("endTime");
        returnFields.add("payMoney");
        returnFields.add("pensionMoney");
        returnFields.add("o2o");
        returnFields.add("score");
        returnFields.add("orderType");
        returnFields.add("name");
        returnFields.add("sellerId");
        returnFields.add("sellerIcon");
        returnFields.add("integralRate");
        returnFields.add("insureCount");
        returnFields.add("insureCountUse");
        returnFields.add("isInsure");

        String sql = "select" +
                " t2.createTime" +
                ",t2.endTime" +
                ",t2.payMoney" +
                ",t2.pensionMoney" +
                ",t2.o2o" +
                ",t2.score" +
                ",t2.orderType" +
                ",t3.name" +
                ",t3._id as sellerId" +
                ",t3.icon as sellerIcon" +
                ",t3.integralRate" +
                ",t4.insureCount" +
                ",t4.insureCountUse" +
                ",t4.isInsure" +
                " from Member t1" +
                " left join OrderInfo t2 on t1._id = t2.memberId" +
                " left join Seller t3 on t2.sellerId = t3._id" +
                " left join MemberPensionLog t4 on t2.orderNo=t4.orderId"
                + where + " order by t2.createTime desc limit " + indexNum + "," + pageSize;

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        resultMap.put("orderList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 获取位置
     *
     * @throws Exception
     */
    @POST
    @Member
    @Seller
    @Path("/getLocation")
    public void getLocation() throws Exception {
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String parentCode = ControllerContext.getContext().getPString("parentCode");
        String whereStr = "";
        if (StringUtils.isNotEmpty(parentCode)) {
            whereStr = " and parentCode=?";
            params.add(parentCode);
        }

        returnFields.add("code");
        returnFields.add("level");
        returnFields.add("name");
        returnFields.add("parentCode");
        returnFields.add("id");
        returnFields.add("value");
        returnFields.add("pValue");

        String sql = "select" +
                " code" +
                ",level" +
                ",name" +
                ",parentCode" +
                ",id" +
                ",value" +
                ",pValue" +
                " from " + Dao.getFullTableName("newarea") +
                " where 1=1" +
                whereStr + " order by convert(name using gbk) asc";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 通过城市名字获取位置,返回城市及所有上级的信息
     *
     * @throws Exception
     */
    @GET
    @Path("/getLocationByName")
    public void getLocationByName() throws Exception {
        String cityName = ControllerContext.getPString("cityName");
        String name = cityName.substring(0,2);
        String where = " where 1=1 and name like ?";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add("%"+name+"%");
        r.add("_id");
        r.add("pid");
        r.add("name");
        r.add("type");
        r.add("value");
        r.add("pvalue");
        r.add("level");
        String sql = "select _id ,pid ,name ,type ,value ,pvalue ,level from Area";
        List<Map<String,Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql+where,r,p);

        List<Map<String,Object>> re = new ArrayList<>();
        // 返回上级信息
        if(list!=null && list.size()>0 && !StringUtils.mapValueIsEmpty(list.get(0),"level")){
            re.add(list.get(0));
            if(Integer.valueOf(list.get(0).get("level").toString())>1){
                where=" where _id=?";
                for(int i=0,len=Integer.valueOf(list.get(0).get("level").toString());i<len;i++){
                    p.clear();
                    p.add(re.get(i).get("pid"));
                    list = MysqlDaoImpl.getInstance().queryBySql(sql+where,r,p);
                    if(list==null || list.size()==0){
                        break;
                    }
                    re.add(list.get(0));
                }
            }
        }

        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员激活获取会员信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Member
    @Path("/getMemberInfo")
    public void getMemberInfo() throws Exception {
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        params.add(ControllerContext.getPString("mobile"));
        returnFields.add("_id");
        returnFields.add("realName");
        returnFields.add("cardNo");
        returnFields.add("idCard");
        returnFields.add("isRealName");
        returnFields.add("icon");
        returnFields.add("mobile");
        String sql = "select" +
                " _id" +
                ",realName" +
                ",cardNo" +
                ",idCard" +
                ",isRealName" +
                ",icon" +
                ",mobile" +
                " from" + Dao.getFullTableName("Member") +
                " where mobile=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);

    }

    /**
     * 会员激活获取会员信息
     *
     * @throws Exception
     */
    @GET
    @Path("/createMemberAccidentLog")
    public void createMemberAccidentLog() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        String agentArea = ControllerContext.getPString("agentArea");
        String agentAreaValue = ControllerContext.getPString("agentAreaValue");
        createMemberAccidentLog(memberId,agentArea,agentAreaValue);
    }

    //会员激活支付意外保险
    public void createMemberAccidentLog(String memberId, String agentArea, String agentAreaValue) throws Exception {
        String isActive="";
        if (StringUtils.isEmpty(agentAreaValue)) {
            Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentAdmin");
            JSONObject agent = ServiceAccess.callService(msg).getContent();

            agentArea = agent.get("name").toString();
            agentAreaValue = agent.get("areaValue").toString();
            isActive="my";//会员端激活
        }else{
            isActive="factor";//发卡点激活
        }

        Calendar cal = Calendar.getInstance();
        Map<String, Object> v = new HashMap<>();
        v.put("_id", UUID.randomUUID().toString());
//        v.put("insureNO", "000013564646"); //等待保险接口数据
//        v.put("company", "太平洋保险公司");
        v.put("memberId", memberId);
        v.put("money", 0);
        v.put("year", cal.get(Calendar.YEAR));
        v.put("area", agentArea);
        v.put("areaValue", agentAreaValue);
        v.put("createTime", System.currentTimeMillis());
        Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/createAccidentLog");
        msg.getContent().put("info", JSONObject.fromObject(v));
        ServiceAccess.callService(msg);
    }

    /**
     * 保存并提交百年数据
     * @param memberId
     * @throws Exception
     */
    public void saveInsure(String memberId) throws Exception{
        Message msg = Message.newReqMessage("1:POST@/order/OrderInfo/saveInsureLog");

        if(StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"获取会员数据失败");
        }
        Map<String,Object> member = MysqlDaoImpl.getInstance().findById2Map("Member",memberId,null,null);
        if(member==null || member.size()==0){
            throw new UserOperateException(500,"获取会员数据失败");
        }

        if(StringUtils.mapValueIsEmpty(member,"realName")){
            throw new UserOperateException(500,"获取会员姓名失败");
        }
        if(StringUtils.mapValueIsEmpty(member,"sex")){
            throw new UserOperateException(500,"获取会员性别失败");
        }else{
            msg.getContent().put("sex",member.get("sex").toString());
        }
        if(StringUtils.mapValueIsEmpty(member,"idCard")){
            throw new UserOperateException(500,"获取会员身份证失败");
        }else{
            String birthday = member.get("idCard").toString().substring(6,13);
            msg.getContent().put("birthday",birthday.substring(0,4)+"-"+birthday.substring(4,6)+"-"+birthday.substring(6,8));
        }
        if(StringUtils.mapValueIsEmpty(member,"mobile")){
            throw new UserOperateException(500,"获取会员手机号码失败");
        }
        if(StringUtils.mapValueIsEmpty(member,"email")){
            throw new UserOperateException(500,"获取会员电子邮箱失败");
        }
        if(StringUtils.mapValueIsEmpty(member,"realArea") || StringUtils.mapValueIsEmpty(member,"realAddress")){
            throw new UserOperateException(500,"获取会员地址失败");
        }else{
            msg.getContent().put("address",member.get("realArea").toString()+member.get("realAddress").toString());
        }
        if(StringUtils.mapValueIsEmpty(member,"postCode")){
            throw new UserOperateException(500,"获取会员邮政编码失败");
        }

        msg.getContent().put("realName",member.get("realName"));
        msg.getContent().put("mobile",member.get("mobile"));
        msg.getContent().put("email",member.get("email"));
        msg.getContent().put("postCode",member.get("postCode"));
        ServiceAccess.callService(msg).getContent();
    }

    /**
     * 查询会员是否已经实名认证
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMemberIsRealName")
    public void getMemberIsRealName() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("isRealName");
        returnFields.add("icon");
        returnFields.add("memberCardId");
        String sql = "select" +
                " t1.isRealName" +
                ",t1.icon" +
                ",t2.memberCardId" +
                " from Member t1" +
                " left join MemberCard t2 on t1._id=t2.memberId" +
                " where t1._id =?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 查询会员支付密码是否正确
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/checkMemberPayPwd")
    public void checkMemberPayPwd() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        String payPwd = ControllerContext.getPString("payPwd");

        if(StringUtils.isEmpty(payPwd)){
            throw new UserOperateException(400,"获取密码失败!");
        }

        Map<String, Object> m = new HashMap<>();
        m.put("_id", memberId);
//        m.put("payPwd", MessageDigestUtils.digest(payPwd));
        Map<String, Object> member = MysqlDaoImpl.getInstance().findOne2Map("Member", m, new String[]{"payPwd"}, Dao.FieldStrategy.Include);
        if (StringUtils.mapValueIsEmpty(member,"payPwd")) {
            throw new UserOperateException(400, "未设置支付密码!");
        }
        if (!member.get("payPwd").toString().equals(MessageDigestUtils.digest(payPwd))) {
            throw new UserOperateException(400, "支付密码错误!");
        }
    }

    /**
     * 会员支付密码找回
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/payForgotPassword")
    public void payForgotPassword() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        String loginName = ControllerContext.getPString("loginName");
        String newPassword = ControllerContext.getPString("newPassword");
        String verification = ControllerContext.getPString("verification");

        if (StringUtils.isEmpty(newPassword) || !Pattern.matches("^[0-9]{6}$", newPassword)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码应为6位纯数字!");
        }
        if (StringUtils.isEmpty(loginName) || !Pattern.matches("^1[34578]{1}\\d{9}$", loginName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
        }
        if (StringUtils.isEmpty(verification)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "验证码不能为空!");
        }
        Map<String, Object> map = MysqlDaoImpl.getInstance().findById2Map("Member", memberId, new String[]{"mobile", "password"}, Dao.FieldStrategy.Include);
        if (!loginName.equals(map.get("mobile"))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该号码不是当前用户绑定的号码!");
        }
        if (MessageDigestUtils.digest(newPassword).equals(map.get("password"))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登陆密码相同!");
        }

        Message msg = Message.newReqMessage("1:PUT@/common/Sms/checkSmsCode");
        msg.getContent().put("type", "change_password");
        msg.getContent().put("smsCode", verification);
        msg.getContent().put("phone", loginName);
        ServiceAccess.callService(msg);

        Map<String, Object> v = new HashMap<>();
        v.put("_id", memberId);
        v.put("payPwd", MessageDigestUtils.digest(newPassword));
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", v);

    }

    /**
     * 会员登录密码找回
     *
     * @throws Exception
     */
    @POST
    @Path("/forgotPassword")
    public void forgotPassword() throws Exception {
        String loginName = ControllerContext.getPString("loginName");
        String newPassword = ControllerContext.getPString("newPassword");
        String verification = ControllerContext.getPString("verification");
        String confirmPassword = ControllerContext.getPString("confirmPassword");
        String forgetType = ControllerContext.getPString("forgetType");

        if (StringUtils.isEmpty(newPassword) && !Pattern.matches("/^.{6,16}$/", newPassword)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码格式错误!");
        }
        if (StringUtils.isEmpty(loginName) && !Pattern.matches("^1[34578]{1}\\d{9}$", loginName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
        }
        if (StringUtils.isEmpty(verification)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "验证码不能为空!");
        }
        if("mall".equals(forgetType)){
            if(!newPassword.equals(confirmPassword)){
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次密码输入不一致!");
            }
        }
        Map<String, Object> p = new HashMap<>();
        p.put("mobile", loginName);
        Map<String, Object> map = MysqlDaoImpl.getInstance().findOne2Map("Member", p, new String[]{"_id"}, Dao.FieldStrategy.Include);

        Message msg = Message.newReqMessage("1:PUT@/common/Sms/checkSmsCode");
        msg.getContent().put("type", "change_password");
        msg.getContent().put("smsCode", verification);
        msg.getContent().put("phone", loginName);
        ServiceAccess.callService(msg);

        Map<String, Object> v = new HashMap<>();
        v.put("_id", map.get("_id"));
        v.put("password", MessageDigestUtils.digest(newPassword));
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", v);

    }

    /**
     * 获取当前登陆会员个人基本信息
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMemberInfoByCur")
    public void getMemberInfoByCur() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("_id");
        returnFields.add("realName");
        returnFields.add("idCard");
        returnFields.add("icon");
        returnFields.add("address");
        returnFields.add("area");
        returnFields.add("infoAreaValue");
        returnFields.add("mobile");
        returnFields.add("isRealName");
        returnFields.add("email");
        returnFields.add("realArea");
        returnFields.add("realAddress");
        returnFields.add("sex");
        returnFields.add("memberCardId");
        String sql = "select" +
                " t1._id" +
                ",t1.realName" +
                ",t1.idCard" +
                ",t1.icon" +
                ",t1.address" +
                ",t1.area" +
                ",t1.infoAreaValue" +
                ",t1.mobile" +
                ",t1.isRealName" +
                ",t1.email" +
                ",t1.realArea" +
                ",t1.realAddress" +
                ",t1.sex" +
                ",t2.memberCardId" +
                " from Member t1" +
                " left join MemberCard t2 on t1._id=t2.memberId" +
                " where t1._id =?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员实名认证
     *
     * @throws Exception
     */
    @POST
    @Member
    @Seller
    @Path("/memberRealName")
    public void memberRealName() throws Exception {
        String memberId = ControllerContext.getContext().getPString("memberId");
        String realName = ControllerContext.getContext().getPString("realName");
//        String sex = ControllerContext.getContext().getPString("sex");
//        String email = ControllerContext.getContext().getPString("email");
        String idCard = ControllerContext.getContext().getPString("idCard");
        String realArea = ControllerContext.getContext().getPString("realArea");
        String realAreaValue = ControllerContext.getContext().getPString("realAreaValue");
        String realAddress = ControllerContext.getContext().getPString("realAddress");

        if (StringUtils.isEmpty(realName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "姓名不能为空!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,10}", realName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写2~10位的中文姓名");
        }
//        if (!Pattern.matches("^[01]$",sex)){
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请选择性别");
//        }
        if (StringUtils.isEmpty(realAddress) || StringUtils.isEmpty(realArea)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写完整地址!");
        }
        if (realAddress.length() > 200) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "街道地址不能超过200位");
        }
        if (realArea.length() > 200) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "所在区域不能超过200位");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", idCard)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证格式错误!");
        }
//        if (StringUtils.isNotEmpty(email)) {
//            if (!Pattern.matches("^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$", email)) {
//                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "邮箱格式错误!");
//            }
//        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(idCard);
        r.add("_id");
        String hql = "select _id from Member where idCard=?";
        List<Map<String, Object>> mIdCard = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        if (mIdCard.size() != 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该身份证已实名认证!");
        }

        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("isRealName");
        String sql = "select" +
                " isRealName" +
                " from Member" +
                " where _id=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (StringUtils.mapValueIsEmpty(re.get(0), "isRealName") || !(Boolean) re.get(0).get("isRealName")) {
            Map<String, Object> s = new HashMap<>();
            s.put("_id", memberId);
            s.put("realName", realName);
//            s.put("email", email);
            s.put("idCard", idCard);
            s.put("realArea", realArea);
            s.put("realAreaValue", realAreaValue);
            s.put("realAddress", realAddress);
            s.put("isRealName", true);
            s.put("sex", getSex(idCard));
            MysqlDaoImpl.getInstance().saveOrUpdate("Member", s);
            Map<String, Object> isOK = new HashMap<>();
            isOK.put("isOk", "isOK");
            isOK.put("memberId", memberId);
            toResult(Response.Status.OK.getStatusCode(), isOK);
        } else {
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /***
     * @description: 根据身份证号码识别出性别
     * @author: Ali.Cao
     * @param idCard
     * @return java.lang.String
     **/
    public String getSex(String idCard){
        int length = idCard.length();
        if(length == 18){
            int i = Integer.valueOf(idCard.substring(16,17)).intValue();
            return i % 2 == 1 ? "1" : "2";
        }else if(length == 15){
            int i = Integer.valueOf(idCard.substring(14,15)).intValue();
            return i % 2 == 1 ? "1" : "2";
        }
        return "";
    }

    /**
     * 根据会员卡获取会员基本信息
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/getMemberInfoByCard")
    public void getMemberInfoByCard() throws Exception {
        String card = ControllerContext.getPString("card");
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("realName");
        r.add("cardNo");
        r.add("isFree");
        r.add("canUse");

        String sql = "select" +
                " _id" +
                ",icon" +
                ",realName" +
                ",cardNo" +
                ",isFree" +
                ",canUse" +
                " from Member" +
                " where cardNo=? or idCard=? or mobile=?";

        List<Object> p = new ArrayList<>();
        p.add(card);
        p.add(card);
        p.add(card);
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (re.size() == 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该号码不存在");
        }
//        if (re.get(0).get("cardNo") == null) {
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该号码不是会员");
//        }
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 判断能否进入养老金页面
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/pensionCanUse")
    public void pensionCanUse() throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getCurrentUserId());
        List<String> r = new ArrayList<>();
        r.add("_id");
        String sql = "select" +
                " _id" +
                " from MemberCard" +
                " where memberId=? and isActive=true";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, params);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 会员端激活会员卡
     *
     * @throws Exception
     */
    @GET
    @Path("/activeMemberCard")
    public void activeMemberCard() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        String orderNo = ControllerContext.getPString("orderNo");
        String payMoney = ControllerContext.getPString("payMoney");
        String isAutoCard = ControllerContext.getPString("isAutoCard");
        //获取会员信息
        Map<String,Object> member = MysqlDaoImpl.getInstance().findById2Map(entityName,memberId,null,null);
        if(member==null || member.size()==0){
            throw new UserOperateException(500,"获取会员信息失败");
        }
        if(StringUtils.mapValueIsEmpty(member,"isRealName") || !Boolean.parseBoolean(member.get("isRealName").toString())){
            throw new UserOperateException(500, "会员未实名认证");
        }
        String factorId="";
        String belongArea = "";
        String belongAreaValue = ControllerContext.getPString("belongAreaValue");
        String cardNo=ControllerContext.getPString("cardNo");
        boolean hasCardNo = false;
        if(StringUtils.isNotEmpty(cardNo)){
            factorId = belongAreaValue.substring(1,belongAreaValue.length()-1).split("_")[4];
            checkMemberCardIsNotActive(cardNo,belongAreaValue,memberId);
            hasCardNo = true;
        }
        if(StringUtils.isNotEmpty(belongAreaValue)){
            hasCardNo = true;
        }
        //如果会员是在服务站注册，则有归属，自主激活默认归属于该服务站下，获取第一张卡；若服务站无卡，则生成一张新卡
        if(hasCardNo){
            if(!StringUtils.mapValueIsEmpty(member,"belongAreaValue") || StringUtils.isNotEmpty(belongAreaValue)){
                List<String> returnFields = new ArrayList<>();
                List<Object> params = new ArrayList<>();

                if(!StringUtils.mapValueIsEmpty(member,"belongAreaValue") && StringUtils.isEmpty(belongAreaValue)){
                    belongAreaValue = member.get("belongAreaValue").toString();
                    belongArea = member.get("belongArea").toString();
                }else{
                    String sql = "select _id,name from Factor where areaValue = ?";
                    returnFields.add("_id");
                    returnFields.add("name");
                    params.add(belongAreaValue);
                    List<Map<String,Object>> factor = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
                    if(factor==null || factor.size()!=1){
                        throw new UserOperateException(500,"获取服务站信息失败");
                    }
                    factorId = factor.get(0).get("_id").toString();
                    belongArea = factor.get(0).get("name").toString();
                }

                //判断是否存在这张会员卡
                returnFields.clear();
                returnFields.add("_id");
                returnFields.add("queryStartCardNo");
                returnFields.add("queryEndCardNo");
                returnFields.add("grant");
                returnFields.add("receive");

                params.clear();
                String where = " where belongAreaValue=?";
                params.add(belongAreaValue);
                if(StringUtils.isNotEmpty(cardNo)){
                    params.add(cardNo);
                    params.add(cardNo);
                    where += " and cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?";
                }

                String sql = "select" +
                        " _id" +
                        ",startCardNo as queryStartCardNo" +
                        ",endCardNo as queryEndCardNo" +
                        ",`grant`" +
                        ",receive" +
                        " from CardField" +
                        where +
                        " order by startCardNo asc";
                List<Map<String, Object>> cardField = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

                if(cardField==null || cardField.size()==0 || StringUtils.mapValueIsEmpty(cardField.get(0),"queryStartCardNo")
                        || cardField.get(0).get("queryStartCardNo").toString().startsWith("C-")
                        || (StringUtils.isNotEmpty(isAutoCard) && Boolean.parseBoolean(isAutoCard))){
                    cardNo = "C-" + ZQUidUtils.generateMemberCardNo();
                }else{
                    if(StringUtils.isEmpty(cardNo)){
                        cardNo = cardField.get(0).get("queryStartCardNo").toString();
                    }
                    long memberCardId = Long.parseLong(cardNo);

                    //卡号段操作
                    long queryStartCardNo = Long.parseLong(cardField.get(0).get("queryStartCardNo").toString());
                    String queryStartCardNoStr = cardField.get(0).get("queryStartCardNo").toString();
                    long queryEndCardNo = Long.parseLong(cardField.get(0).get("queryEndCardNo").toString());
                    String queryEndCardNoStr = cardField.get(0).get("queryEndCardNo").toString();
                    String oldGrant = cardField.get(0).get("grant").toString();
                    factorId = cardField.get(0).get("receive").toString();
                    String memberCardIdStr = cardNo;
                    //删除原有的卡段
                    List<Object> p = new ArrayList<>();
                    p.add(cardField.get(0).get("_id"));
                    MysqlDaoImpl.getInstance().exeSql("delete from CardField where _id=?", p, "CardField", false);

                    if (queryStartCardNo < memberCardId && queryEndCardNo > memberCardId) {
                        //如果号码在号段中间
                        //将取原本的数据分割成2部分
                        //第一部分
                        allocationCardFiledImpl(queryStartCardNoStr
                                , String.valueOf(memberCardId-1)
                                , memberCardId - queryStartCardNo
                                , belongAreaValue
                                , oldGrant
                                , factorId);
                        //第二部分
                        allocationCardFiledImpl(String.valueOf(memberCardId+1)
                                , queryEndCardNoStr
                                , queryEndCardNo - memberCardId
                                , belongAreaValue
                                , oldGrant
                                , factorId);
                        //生成一条已使用的号段
                        allocationCardFiledImpl(memberCardIdStr
                                ,memberCardIdStr
                                , 1l
                                , ""
                                , factorId
                                , memberId);

                    } else if (queryStartCardNoStr.equals(memberCardIdStr) && queryEndCardNo > memberCardId) {
                        //号码在左边
                        //本身剩余
                        allocationCardFiledImpl(String.valueOf(memberCardId+1)
                                , queryEndCardNoStr
                                , queryEndCardNo - queryStartCardNo
                                , belongAreaValue
                                , oldGrant
                                , factorId);
                        //生成一条已使用的号段
                        allocationCardFiledImpl(queryStartCardNoStr
                                ,queryStartCardNoStr
                                , 1l
                                , ""
                                , factorId
                                , memberId);

                    } else if (queryEndCardNoStr.equals(memberCardIdStr) && queryStartCardNo < memberCardId) {
                        //号码在右边
                        //本身剩余
                        allocationCardFiledImpl(queryStartCardNoStr
                                , String.valueOf(memberCardId-1)
                                , queryEndCardNo - queryStartCardNo
                                , belongAreaValue
                                , oldGrant
                                , factorId);
                        //生成一条已使用的号段
                        allocationCardFiledImpl(queryEndCardNoStr
                                ,queryEndCardNoStr
                                , 1l
                                , ""
                                , factorId
                                , memberId);
                    } else if (queryStartCardNoStr.equals(queryEndCardNoStr) && queryStartCardNoStr.equals(memberCardIdStr)) {
                        //生成一条已使用的号段
                        allocationCardFiledImpl(queryStartCardNoStr
                                ,queryEndCardNoStr
                                , 1l
                                , ""
                                , factorId
                                , memberId);
                    }
                }
            } else {//修改激活会员归属信息  update by AliCao 2019-01-21
                cardNo = "C-" + ZQUidUtils.generateMemberCardNo();
                belongArea = member.get("belongArea").toString();//"平台";
//                belongAreaValue = "_A-000001_";
                factorId = belongAreaValue.substring(1, belongAreaValue.length() - 1).split("_")[4];//"A-000001";
            }
        }

        if (StringUtils.isEmpty(factorId)) {//修改激活会员归属信息  update by AliCao 2019-01-21
            cardNo = "C-" + ZQUidUtils.generateMemberCardNo();
            belongArea = member.get("belongArea").toString();//"平台";
//            belongAreaValue = "_A-000001_";
            factorId = belongAreaValue.substring(1, belongAreaValue.length() - 1).split("_")[4];//"A-000001";
        }

        //保存会员
        Map<String, Object> card = new HashMap<>();
        card.put("_id", UUID.randomUUID().toString());
        card.put("memberCardId", cardNo);
        card.put("memberId", memberId);
        card.put("factorId", factorId);
        card.put("isActive", true);
        card.put("activeTime", System.currentTimeMillis());
        card.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberCard", card);

        Map<String, Object> v = new HashMap<>();
        v.put("_id", memberId);
        v.put("cardNo", cardNo);
        v.put("isBindCard", true);
        v.put("isFree", false);
        v.put("belongArea", belongArea);
        v.put("belongAreaValue", belongAreaValue);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", v);

        //生成养老金汇总表
        Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/createPensionAccount");
        msg.getContent().put("memberId", memberId);
        ServiceAccess.callService(msg);

        msg = Message.newReqMessage("1:GET@/order/OrderInfo/createAgentMoney");
        if(!hasCardNo){
            createMemberAccidentLog(memberId, null, null);//投意外保险
            msg.getContent().put("isActive", "my");
        }else{
            createMemberAccidentLog(memberId, belongArea,belongAreaValue);//投意外保险
            msg.getContent().put("isActive", "factor");
            msg.getContent().put("belongArea", belongArea);
            msg.getContent().put("belongAreaValue", belongAreaValue);
        }
        //生成代理商/发卡点明细
        msg.getContent().put("memberId", memberId);
        msg.getContent().put("pensionMoney", payMoney);
        msg.getContent().put("orderId", orderNo);
        ServiceAccess.callService(msg);

        //生成团队关系
        msg = Message.newReqMessage("1:POST@/order/Team/checkActive");
        msg.getContent().put("regMember", memberId);
        msg.getContent().put("factorId", factorId);
        ServiceAccess.callService(msg);

        // 发送激活短信
        msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendActivate");
        msg.getContent().put("mobile",member.get("mobile").toString());
        ServiceAccess.callService(msg);

        toResult(200,card);
    }

    /**
     * 修改会员个人信息
     * (传入的参数对应字段名,一次只修改一个字段)
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/updateMemberInfo")
    public void updateMemberInfo() throws Exception {
        Map<String, Object> m = new HashMap<>();
        String field = ControllerContext.getContext().getPString("field");
        String content = ControllerContext.getContext().getPString("content");

        if(Pattern.matches("(belongArea)|(belongAreaValue)|(canUse)|(cardNo)|(isBindCard)|(isFree)",field)){
            throw new UserOperateException(500,"您无权修改此数据");
        }

        if (StringUtils.isEmpty(field) || StringUtils.isEmpty(content)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "不能为空!");
        }
        if (content.length() > 254) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "内容过长,保存失败!");
        }
        m.put(field, content);
        m.put("_id", ControllerContext.getContext().getCurrentUserId());

        MysqlDaoImpl.getInstance().saveOrUpdate("Member", m);
    }

    /**
     * 修改会员个人信息:区域
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/updateMemberInfoArea")
    public void updateMemberInfoArea() throws Exception {
        Map<String, Object> m = new HashMap<>();
        String area = ControllerContext.getContext().getPString("area");
        String areaValue = ControllerContext.getContext().getPString("areaValue");

        if (StringUtils.isEmpty(area) || StringUtils.isEmpty(areaValue)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "不能为空!");
        }
        if (area.length() > 1024 || areaValue.length() > 1024) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "内容过长!");
        }
        m.put("_id", ControllerContext.getContext().getCurrentUserId());
        m.put("area", area);
        m.put("infoAreaValue", areaValue);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", m);
    }

    /**
     * @throws Exception 根据会员手机号码获取会员信息
     */
    @GET
    @Member
    @Path("/getMemberInfoByMobile")
    public void getMemberInfoByMobile() throws Exception {
        String mobile = ControllerContext.getPString("mobile");
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        Map<String, Object> memberInfo = MysqlDaoImpl.getInstance().findOne2Map("Member", params, null, null);
        if (memberInfo == null || memberInfo.size() == 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "找不到该会员!");
        }
        toResult(Response.Status.OK.getStatusCode(), memberInfo);
    }


    @Override
    public void query() throws Exception {
        super.query();
    }

    /**
     * 批量生成会员卡
     */
    @POST
    @Member
    @Path("/createMCard")
    public void createMCard() throws Exception {
        int areaCode = ControllerContext.getPInteger("areaCode");
        int createCardNum = ControllerContext.getPInteger("createCardNum");
        String factorId = ControllerContext.getPString("factorId");
        String KEY_CardNo = "CardNo";
        NumberFormat CardNoFormat = new DecimalFormat("0000000");//
        FieldPosition HELPER_POSITION = new FieldPosition(0);//  
        Map<String, Object> map = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < createCardNum; i++) {
            sb = CardNoFormat.format(JedisUtil.incr(KEY_CardNo + areaCode), sb, HELPER_POSITION);
            String cardNo = areaCode + sb.toString();
            map.put("_id", UUID.randomUUID().toString());
            map.put("factorId", factorId);
            map.put("memberCardId", cardNo);
            map.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberCard", map);
            map.clear();
            sb.delete(0, 7);
        }
    }

    /**
     * 分配卡段
     */
    @POST
    @Path("/allocationCardFiled")
    public void allocationCardFiled() throws Exception {
        String startCardNo = ControllerContext.getPString("startCardNo");
        String endCardNo = ControllerContext.getPString("endCardNo");
        Long cardNum = ControllerContext.getPLong("cardNum");
        String belongAreaValue = ControllerContext.getPString("belongAreaValue");
        String grant = ControllerContext.getPString("grant");
        String receive = ControllerContext.getPString("receive");
        String type = ControllerContext.getPString("type");

        allocationCardFiledImpl(startCardNo, endCardNo, cardNum, belongAreaValue,grant,receive);

        if(StringUtils.isNotEmpty(type)){//只在发卡和回收卡时保存记录,拆分当前拥有的卡不保存
            Map<String, Object> log = new HashMap<>();
            log.put("_id", UUID.randomUUID().toString());
            log.put("createTime", System.currentTimeMillis());
            log.put("startCardNo", startCardNo);
            log.put("endCardNo", endCardNo);
            log.put("cardNum", cardNum);
            log.put("belongAreaValue", belongAreaValue);
            log.put("grant", grant);
            log.put("receive", receive);
            log.put("type", type);
            MysqlDaoImpl.getInstance().saveOrUpdate("GrantCardLog", log);
        }
    }

    private void allocationCardFiledImpl(String startCardNo, String endCardNo, Long cardNum, String belongAreaValue, String grant , String receive) throws Exception {
        Map<String, Object> v = new HashMap<>();
        v.put("_id", UUID.randomUUID().toString());
        v.put("createTime", System.currentTimeMillis());
        v.put("startCardNo", startCardNo);
        v.put("endCardNo", endCardNo);
        v.put("cardNum", cardNum);
        v.put("belongAreaValue", belongAreaValue);
        v.put("grant", grant);
        v.put("receive", receive);

        MysqlDaoImpl.getInstance().saveOrUpdate("CardField", v);
    }

    /**
     * 服务站激活卡：自动生成的会员卡
     */
    @POST
    @Path("/createCardAuto")
    public void createCardAuto() throws Exception {
        String factorId = ControllerContext.getPString("factorId");
        String belongAreaValue = ControllerContext.getPString("belongAreaValue");

        if(StringUtils.isEmpty(factorId) || StringUtils.isEmpty(belongAreaValue)){
            throw new UserOperateException(500,"获取服务站数据失败");
        }

        //先查询是否有空余的新生成卡
        String sql = "select t1.startCardNo from CardField t1" +
                " left join MemberCard t2 on t1.startCardNo=t2.memberCardId" +
                " where t1.receive=?" +
                " and t1.belongAreaValue is null" +
                " and t2.isActive<>true";
        List<Object> params = new ArrayList<>();
        params.add(factorId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("startCardNo");
        List<Map<String, Object>> cardList = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        Map<String, Object> memberCard = new HashMap<>();
        if (cardList != null && cardList.size() != 0) {
            memberCard.put("memberCardId",cardList.get(0).get("startCardNo"));
        }else{
            //卡号
            String cardNo = "C-" + ZQUidUtils.generateMemberCardNo();
            memberCard.put("memberCardId",cardNo);

            //分配卡记录
            Map<String,Object> grantCardLog = new HashMap<>();
            grantCardLog.put("_id",UUID.randomUUID().toString());
            grantCardLog.put("type",1);
            grantCardLog.put("startCardNo",cardNo);
            grantCardLog.put("endCardNo",cardNo);
            grantCardLog.put("grant","A-000001");
            grantCardLog.put("receive",factorId);
            grantCardLog.put("belongAreaValue",belongAreaValue);
            grantCardLog.put("cardNum",1);
            grantCardLog.put("createTime",System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("GrantCardLog", grantCardLog);

            //会员卡段
            Map<String,Object> cardField = new HashMap<>();
            cardField.put("_id",UUID.randomUUID().toString());
            cardField.put("startCardNo",cardNo);
            cardField.put("endCardNo",cardNo);
            cardField.put("grant","A-000001");
            cardField.put("receive",factorId);
            cardField.put("belongAreaValue",belongAreaValue);
            cardField.put("cardNum",1);
            cardField.put("createTime",System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("CardField", cardField);
        }

        toResult(200,memberCard);
    }

    /**
     * 关联商家创建会员(第三方调用）
     */
    @POST
    @Path("/createMemberByRelate")
    public void createMemberByRelate() throws Exception {
        String mobile = ControllerContext.getPString("mobile"); // 会员手机号码
        String relateStoreId = ControllerContext.getPString("relateStoreId");
        String relateFrom = ControllerContext.getPString("relateFrom");
        String idCard = ControllerContext.getPString("idCard");
        String realName = ControllerContext.getPString("realName");
        String realArea = ControllerContext.getPString("realArea");
        String realAddress = ControllerContext.getPString("realAddress");
        String cardNo = ControllerContext.getPString("cardNo");

        if(StringUtils.isEmpty(mobile) || !Pattern.matches("^[1][3456789][0-9]{9}$", mobile)){
            throw new UserOperateException(500,"获取会员手机号码失败");
        }
        if(StringUtils.isEmpty(relateStoreId)){
            throw new UserOperateException(500,"获取关联商家ID失败");
        }
        if(StringUtils.isEmpty(relateFrom)){
            throw new UserOperateException(500,"获取关联来源失败");
        }
        if(!StringUtils.isEmpty(idCard) && !Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$",idCard)){
            throw new UserOperateException(500,"身份证格式错误");
        }
        if (!StringUtils.isEmpty(realName) && !Pattern.matches("[\u0391-\uFFE5]{2,50}", realName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写2~50位的中文姓名");
        }
        if (StringUtils.isEmpty(realArea)) {
            throw new UserOperateException(500, "请填写身份证行政区域");
        }
        if (StringUtils.isEmpty(realAddress)) {
            throw new UserOperateException(500, "请填写身份证街道地址");
        }

        // 检查商家
        Message msg = Message.newReqMessage("1:GET@/account/RelateStore/getRelateStore");
        msg.getContent().put("relateStoreId",relateStoreId);
        msg.getContent().put("relateFrom",relateFrom);
        JSONObject relateStore = ServiceAccess.callService(msg).getContent();

        if(relateStore==null || relateStore.size()==0){
            throw new UserOperateException(500,"获取关联商家失败");
        }

        // 检查会员
        Map<String,Object> params = new HashMap<>();
        params.put("mobile",mobile);
        Map<String,Object> member = MysqlDaoImpl.getInstance().findOne2Map("Member",params,new String[]{"_id","isBindCard"},Dao.FieldStrategy.Include);
        if(member!=null && member.size()!=0){
            if(!StringUtils.mapValueIsEmpty(member,"isBindCard") && !Boolean.parseBoolean(member.get("isBindCard").toString())){
                throw new UserOperateException(500,"该手机号码已经被激活");
            }
        }else{
            member = new HashMap<>();
            member.put("_id","M-" + ZQUidUtils.generateMemberNo());
            member.put("mobile",mobile);
            member.put("password",MessageDigestUtils.digest("000000"));
            member.put("from",relateFrom);
            member.put("createTime",System.currentTimeMillis());
            member.put("belongArea", msg.getContent().get("belongArea"));
            member.put("belongAreaValue", msg.getContent().get("belongAreaValue"));
            member.put("canUse",true);
            member.put("sex",getSex(idCard));
            member.put("idCard",idCard);
            member.put("realName",realName);
            member.put("realArea",realArea);
            member.put("realAddress",realAddress);
            member.put("isRealName",true);
            member.put("cardNo",cardNo);
            member.put("isFree", true);
            MysqlDaoImpl.getInstance().saveOrUpdate("Member",member);

            //生成会员现金汇总表
            msg = Message.newReqMessage("1:GET@/order/OrderInfo/createMemberAccount");
            msg.getContent().put("memberId", member.get("_id").toString());
            ServiceAccess.callService(msg);
        }

        // 获取配置
        msg = Message.newReqMessage("1:GET@/account/RelateStore/getUserConf");
        msg.getContent().put("userId", relateStore.get("localSellerId").toString());
        JSONObject conf = ServiceAccess.callService(msg).getContent();

        if(relateFrom.equals("aikaka")){
            String factorId = relateStore.get("localSellerId").toString();
            if(!Pattern.matches("^\\d{16}$",cardNo)){
                throw new UserOperateException(500,"会员卡号错误");
            }
            if(StringUtils.mapValueIsEmpty(conf,"activeFee")){
                throw new UserOperateException(500,"未配置激活费用");
            }

            double activeFee = Double.parseDouble(conf.get("activeFee").toString());
            if(StringUtils.mapValueIsEmpty(relateStore,"userType") || !relateStore.get("userType").equals("factor")){
                throw new UserOperateException(500,"仅支持服务站激活");
            }
            JSONObject factor = ServiceAccess.getRemoveEntity("account","Factor",factorId);

            String memberId = member.get("_id").toString();
            String belongAreaValue = factor.get("areaValue").toString();

            //检查数据
            msg = Message.newReqMessage("1:POST@/order/OrderInfo/checkActive");
            msg.getContent().put("factorId", factorId);
            msg.getContent().put("areaValue", belongAreaValue);
            msg.getContent().put("memberId", memberId);
            msg.getContent().put("memberCardId", cardNo);
            msg.getContent().put("payType", 3);
            msg.getContent().put("activeMoney",activeFee);
            ServiceAccess.callService(msg).getContent();

//            //生成卡号
//            Map<String,Object> updateParams = new HashMap<>();
//            updateParams.put("memberCardId",cardNo);
//            updateParams.put("factorId",factorId);
//            updateParams.put("memberId",member.get("_id"));
//            updateParams.put("payType",3);
//            updateMemberCardModify(updateParams);

            //生成卡号
            activeMember(null,memberId,cardNo,belongAreaValue,cardNo.substring(0,1).equals("C"));

            //服务站支付激活费用
            if(activeFee>0){
                msg = Message.newReqMessage("1:POST@/order/OrderInfo/updateFactorAccountByActive");
                msg.getContent().put("factorId", factorId);
                msg.getContent().put("payMoney",activeFee);
                msg.getContent().put("payType", 3);
                msg.getContent().put("orderNo", ZQUidUtils.generateOrderNo());
                ServiceAccess.callService(msg);
            }

            Map<String,Object> re = new HashMap<>();
            re.put("return_code","SUCCESS");
            re.put("memberId",member.get("_id"));
            re.put("cardNo",cardNo);
            toResult(200,re);
        }else if(relateFrom.equals("kyb")){ // 快易帮无需扣费，直接激活
            activeMember(null,member.get("_id").toString(),cardNo,relateStore.get("belongAreaValue").toString(),false);
        }else{
            throw new UserOperateException(500,"错误的关联类型");
        }
    }


    /**
     * 创建免费会员
     * @throws Exception
     */
    @POST
    @Path("/createMemberFree")
    public void createMemberFree() throws Exception {
        System.out.println("map=="+JSONObject.fromObject(ControllerContext.getContext()));
        String mobile = ControllerContext.getPString("mobile");
        String shareId = ControllerContext.getPString("shareId");
        String shareType = ControllerContext.getPString("shareType");

        Map<String,Object> params = new HashMap<>();
        params.put("mobile",mobile);
        Map<String,Object> member = MysqlDaoImpl.getInstance().findOne2Map("Member",params,new String[]{"_id"},Dao.FieldStrategy.Include);

        if(member != null && member.size()!=0){
            throw new UserOperateException(500,"该手机号码已经被注册");
        }
        String memberId = "M-" + ZQUidUtils.generateMemberNo();
        member = new HashMap<>();
        member.put("_id",memberId);
        member.put("mobile",mobile);
        System.out.println("11111111111111111111");
        getBelong(member, shareType, shareId);
        System.out.println("22222222222222222222");
        member.put("password",MessageDigestUtils.digest("000000"));
        member.put("createTime",System.currentTimeMillis());
        member.put("canUse",true);
        member.put("isFree", true);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member",member);

        //生成会员现金汇总表
        Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/createMemberAccount");
        msg.getContent().put("memberId", member.get("_id").toString());
        ServiceAccess.callService(msg);

        //生成团队关系
        createTeam(memberId,shareId,shareType);

        toResult(200,member);
    }

    /**
     * 生成团队关系
     * @throws Exception
     */
    public void createTeam(String memberId,String shareId,String shareType) throws Exception{
        if(StringUtils.isEmpty(shareId)){
            return;
        }
        Message msg = Message.newReqMessage("1:POST@/order/Team/checkActive");
        if(StringUtils.isEmpty(shareType)){
            shareType = "Member";
        }
        if(shareType.equals("Member")){
            msg.getContent().put("shareMember", shareId);
        }else if(shareType.equals("Seller")){
            msg.getContent().put("shareSeller", shareId);
        }else if(shareType.equals("Factor")){
            msg.getContent().put("factorId", shareId);
        }else{
            return;
        }
        msg.getContent().put("regMember", memberId);

        ServiceAccess.callService(msg);
    }

    /**
     * 获取会员归属发卡点的剩余卡段
     */
    @GET
    @Member
    @Path("/getMemberBelongField")
    public void getMemberBelongField() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        List<String> r = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        String sql = "select" +
                " t1.startCardNo" +
                ",t1.endCardNo" +
                ",t1._id" +
                " from CardField t1" +
                " left join Member t2 on t1.belongAreaValue=t2.belongAreaValue" +
                " where t2._id=? order by startCardNo";
        r.add("startCardNo");
        r.add("endCardNo");
        r.add("_id");
        p.add(memberId);
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 更换会员的会员卡
     */
    @GET
    @Member
    @Path("/exchangeCardTest")
    public void exchangeCardTest() throws Exception {
        String phoneNumber = ControllerContext.getPString("phoneNumber");
        String oldCard = ControllerContext.getPString("oldCard");
        String newCard = ControllerContext.getPString("newCard");

        Map<String,Object> v = new HashMap<>();
        v.put("cardNo",oldCard);
        v.put("mobile",phoneNumber);
        Map<String,Object> member = MysqlDaoImpl.getInstance().findOne2Map("Member",v,new String[]{"_id","belongAreaValue"},Dao.FieldStrategy.Include);
        if(member==null){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该卡号与手机号不匹配");
        }
        String memberId = member.get("_id").toString();
        v.clear();
        v.put("memberId", memberId);
        Map<String, Object> card = MysqlDaoImpl.getInstance().findOne2Map("MemberCard", v, new String[]{"_id"}, Dao.FieldStrategy.Include);
        //获取当前登陆的代理商的信息
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agent = ServiceAccess.callService(msg).getContent();
        if(!Pattern.matches("^[14]$",agent.get("level").toString())){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该功能只能由平台和县级代理商使用!");
        }
        if (Pattern.matches("^[4]$",agent.get("level").toString()) && "A-000001".equals(member.get("belongAreaValue"))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!您没有给该用户换卡的权限!");
        }
        String agentValue = member.get("belongAreaValue").toString();
        //从自己所拥有的号段是否有当前号段
        List<String> r = new ArrayList<>();
        List<Object> p = new ArrayList<>();
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
        r.add("_id");
        r.add("queryStartCardNo");
        r.add("queryEndCardNo");
        r.add("grant");
        r.add("receive");
        p.add(newCard);
        p.add(newCard);
        p.add(newCard);
        p.add(newCard);
        p.add(agentValue);
        List<Map<String, Object>> cardFieldListToMe = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        if(cardFieldListToMe==null || cardFieldListToMe.size()==0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "您更换的新卡号已被使用过，请另换卡号!");
        }
        String id = cardFieldListToMe.get(0).get("_id").toString();
        long queryStartCardNo = Long.valueOf(cardFieldListToMe.get(0).get("queryStartCardNo").toString());
        String queryStartCardNoStr = cardFieldListToMe.get(0).get("queryStartCardNo").toString();
        long queryEndCardNo = Long.valueOf(cardFieldListToMe.get(0).get("queryEndCardNo").toString());
        String queryEndCardNoStr = cardFieldListToMe.get(0).get("queryEndCardNo").toString();
        String grant = cardFieldListToMe.get(0).get("grant").toString();
        String receive = cardFieldListToMe.get(0).get("receive").toString();

        //修改会员的会员卡号
        v.clear();
        v.put("_id", memberId);
        v.put("cardNo", newCard);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", v);
        //修改会员卡表的数据
        v.clear();
        v.put("_id", card.get("_id").toString());
        v.put("memberCardId", newCard);
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberCard", v);
        //添加换卡记录
        v.clear();
        v.put("_id",UUID.randomUUID().toString());
        v.put("createTime",System.currentTimeMillis());
        v.put("oldCardNo",oldCard);
        v.put("newCardNo",newCard);
        v.put("belongAreaValue",agentValue);
        v.put("memberId",memberId);
        MysqlDaoImpl.getInstance().saveOrUpdate("ExchangeCardLog",v);
        //删除原有数据
        MysqlDaoImpl.getInstance().remove("CardField", id);

        //添加新数据
        if (queryStartCardNo < Long.valueOf(newCard) && queryEndCardNo > Long.valueOf(newCard)) {
            //如果号码在号段中间
            //将取原本的数据分割成2部分
            //第一部分
            allocationCardFiledImpl(queryStartCardNoStr
                    , String.valueOf(Long.valueOf(newCard)-1)
                    , Long.valueOf(newCard) - queryStartCardNo
                    , agentValue
                    , grant
                    , receive);
            //第二部分
            allocationCardFiledImpl(String.valueOf(Long.valueOf(newCard)+1)
                    , queryEndCardNoStr
                    , queryEndCardNo - Long.valueOf(newCard)
                    , agentValue
                    , grant
                    , receive);
            //生成一条已使用的号段
            allocationCardFiledImpl(newCard
                    ,newCard
                    , 1l
                    , ""
                    , grant
                    , memberId);

        } else if (queryStartCardNo == Long.parseLong(newCard) && queryEndCardNo > Long.valueOf(newCard)) {
            //号码在左边
            //本身剩余
            allocationCardFiledImpl(String.valueOf(queryStartCardNo+1)
                    , queryEndCardNoStr
                    , queryEndCardNo - queryStartCardNo
                    , agentValue
                    , grant
                    , receive);
            //生成一条已使用的号段
            allocationCardFiledImpl(queryStartCardNoStr
                    ,queryStartCardNoStr
                    , 1l
                    , ""
                    , grant
                    , memberId);

        } else if (queryEndCardNo == Long.parseLong(newCard) && queryStartCardNo < Long.valueOf(newCard)) {
            //号码在右边
            //本身剩余
            allocationCardFiledImpl(queryStartCardNoStr
                    , String.valueOf(queryEndCardNo-1)
                    , queryEndCardNo - queryStartCardNo
                    , agentValue
                    , grant
                    , receive);
            //生成一条已使用的号段
            allocationCardFiledImpl(queryEndCardNoStr
                    ,queryEndCardNoStr
                    , 1l
                    , ""
                    , grant
                    , memberId);
        } else if (queryStartCardNo == queryEndCardNo && queryStartCardNo == Long.parseLong(newCard)) {
            //生成一条已使用的号段
            allocationCardFiledImpl(newCard
                    ,newCard
                    , 1l
                    , ""
                    , grant
                    , memberId);
        }
    }

    /**
     * 获取需要更换会员卡的会员信息
     */
    @GET
    @Member
    @Path("/getMemberChange")
    public void getMemberChange() throws Exception {
        String mobile = ControllerContext.getPString("mobile");
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String sql = "select" +
                " cardNo" +
                ",realName" +
                ",_id" +
                " from Member" +
                " where mobile=?";
        p.add(mobile);
        r.add("cardNo");
        r.add("realName");
        r.add("_id");
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        toResult(Response.Status.OK.getStatusCode(), re);

    }
    /**
     * 获取会员的换卡信息
     */
    @GET
    @Member
    @Path("/getMemberCardTime")
    public void getMemberCardTime() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        Map<String,Object> v = new HashMap<>();
        v.put("memberId",memberId);
        Map<String,Object> active = MysqlDaoImpl.getInstance().findOne2Map("MemberCard",v,new String[]{"activeTime"},Dao.FieldStrategy.Include);
        Map<String,Object> exchange = MysqlDaoImpl.getInstance().findOne2Map("ExchangeCardLog",v,new String[]{"createTime"}, Dao.FieldStrategy.Include);
        v.clear();
        v.put("active",active);
        v.put("exchange",exchange);
        toResult(Response.Status.OK.getStatusCode(), v);
    }
    /**
     * 发卡点:会员换卡
     */
    @GET
    @Member
    @Path("/exchangeCardToFactor")
    public void exchangeCardToFactor() throws Exception {
        String newCard = ControllerContext.getPString("newCard");
        String oldCard = ControllerContext.getPString("oldCard");
        String phoneNumber = ControllerContext.getPString("phoneNumber");
        Map<String,Object> v = new HashMap<>();
        v.put("cardNo",oldCard);
        v.put("mobile",phoneNumber);
        Map<String,Object> member = MysqlDaoImpl.getInstance().findOne2Map("Member",v,new String[]{"_id","belongAreaValue"},Dao.FieldStrategy.Include);
        if(member==null){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该卡号与手机号不匹配");
        }
        String memberId = member.get("_id").toString();
        v.clear();
        v.put("memberId", memberId);
        Map<String, Object> card = MysqlDaoImpl.getInstance().findOne2Map("MemberCard", v, new String[]{"_id"}, Dao.FieldStrategy.Include);
        Message msg = Message.newReqMessage("1:GET@/account/Factor/getFactorInfo");
        JSONObject factor = ServiceAccess.callService(msg).getContent();
        String factorAreaValue = factor.get("areaValue").toString();
        if(member.get("belongAreaValue").equals(factorAreaValue)){
            v.clear();
            v.put("_id", memberId);
            v.put("cardNo", newCard);
            MysqlDaoImpl.getInstance().saveOrUpdate("Member", v);
            //修改会员卡表的数据
            v.clear();
            v.put("_id", card.get("_id").toString());
            v.put("memberCardId", newCard);
            MysqlDaoImpl.getInstance().saveOrUpdate("MemberCard", v);
            //添加换卡记录
            v.clear();
            v.put("_id",UUID.randomUUID().toString());
            v.put("createTime",System.currentTimeMillis());
            v.put("oldCardNo",oldCard);
            v.put("newCardNo",newCard);
            v.put("belongAreaValue",factorAreaValue);
            v.put("memberId",memberId);
            MysqlDaoImpl.getInstance().saveOrUpdate("ExchangeCardLog",v);
            //更新卡段表
            //从自己所拥有的号段是否有当前号段
            List<String> r = new ArrayList<>();
            List<Object> p = new ArrayList<>();
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
            r.add("_id");
            r.add("queryStartCardNo");
            r.add("queryEndCardNo");
            r.add("grant");
            r.add("receive");
            p.add(newCard);
            p.add(newCard);
            p.add(newCard);
            p.add(newCard);
            p.add(factorAreaValue);
            List<Map<String, Object>> cardFieldListToMe = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            if(cardFieldListToMe.size()!=0){
                //删除原有数据
                String id = cardFieldListToMe.get(0).get("_id").toString();
                Long queryStartCardNo = Long.valueOf(cardFieldListToMe.get(0).get("queryStartCardNo").toString());
                String queryStartCardNoStr = cardFieldListToMe.get(0).get("queryStartCardNo").toString();
                Long queryEndCardNo = Long.valueOf(cardFieldListToMe.get(0).get("queryEndCardNo").toString());
                String queryEndCardNoStr = cardFieldListToMe.get(0).get("queryEndCardNo").toString();
                String grant = cardFieldListToMe.get(0).get("grant").toString();
                String receive = cardFieldListToMe.get(0).get("receive").toString();
                //删除原有数据
                p.clear();
                p.add(id);
                MysqlDaoImpl.getInstance().exeSql("delete from CardField where _id=?", p, "CardField", false);
                if (queryStartCardNo < Long.valueOf(newCard) && queryEndCardNo > Long.valueOf(newCard)) {
                    //如果号码在号段中间
                    //将取原本的数据分割成2部分
                    //第一部分
                    allocationCardFiledImpl(queryStartCardNoStr
                            , String.valueOf(Long.valueOf(newCard)-1)
                            , Long.valueOf(newCard) - queryStartCardNo
                            , factorAreaValue
                            , grant
                            , receive);
                    //第二部分
                    allocationCardFiledImpl(String.valueOf(Long.valueOf(newCard)+1)
                            , queryEndCardNoStr
                            , queryEndCardNo - Long.valueOf(newCard)
                            , factorAreaValue
                            , grant
                            , receive);
                    //生成一条已使用的号段
                    allocationCardFiledImpl(newCard
                            ,newCard
                            , 1l
                            , ""
                            , grant
                            , memberId);

                } else if (queryStartCardNo == Long.valueOf(newCard).longValue() && queryEndCardNo > Long.valueOf(newCard)) {
                    //号码在左边
                    //本身剩余
                    allocationCardFiledImpl(String.valueOf(queryStartCardNo+1)
                            , queryEndCardNoStr
                            , queryEndCardNo - queryStartCardNo
                            , factorAreaValue
                            , grant
                            , receive);
                    //生成一条已使用的号段
                    allocationCardFiledImpl(queryStartCardNoStr
                            ,queryStartCardNoStr
                            , 1l
                            , ""
                            , grant
                            , memberId);

                } else if (queryEndCardNo == Long.valueOf(newCard).longValue() && queryStartCardNo < Long.valueOf(newCard)) {
                    //号码在右边
                    //本身剩余
                    allocationCardFiledImpl(queryStartCardNoStr
                            , String.valueOf(queryEndCardNo-1)
                            , queryEndCardNo - queryStartCardNo
                            , factorAreaValue
                            , grant
                            , receive);
                    //生成一条已使用的号段
                    allocationCardFiledImpl(queryEndCardNoStr
                            ,queryEndCardNoStr
                            , 1l
                            , ""
                            , grant
                            , memberId);
                } else if (queryStartCardNo == queryEndCardNo.longValue() && queryStartCardNo == Long.valueOf(newCard).longValue()) {
                    //生成一条已使用的号段
                    allocationCardFiledImpl(newCard
                            ,newCard
                            , 1l
                            , ""
                            , grant
                            , memberId);
                }

            }else {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "您更换的新卡号已被使用过，请另换卡号!");
            }
            //添加新数据

        }else{
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "抱歉!该会员不是当前服务站所属会员!");
        }
    }
    /**
     *  获取换卡记录
     */
    @GET
    @Path("/getExchangeCardList")
    public void getExchangeCardList() throws Exception {
        Message msg = Message.newReqMessage("1:GET@/account/Factor/getFactorInfo");
        JSONObject factor = ServiceAccess.callService(msg).getContent();
        String factorAreaValue = factor.get("areaValue").toString();

        Long indexNum = ControllerContext.getPLong("indexNum");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        p.add(factorAreaValue);
        r.add("totalCount");
        String hql = "select count(t1._id) as totalCount from ExchangeCardLog t1 left join Member t2 on t1.memberId=t2._id where t1.belongAreaValue=?";
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = (Long) couponList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalNum", totalNum);
        List<String> returnField = new ArrayList<>();
        returnField.add("newCardNo");
        returnField.add("oldCardNo");
        returnField.add("createTime");
        returnField.add("realName");
        String sql = "select" +
                " t1.newCardNo" +
                ",t1.oldCardNo" +
                ",t1.createTime" +
                ",t2.realName" +
                " from ExchangeCardLog t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " where t1.belongAreaValue=?" +
                " order by t1.createTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, p);
        resultMap.put("exchangeCardList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }


    /**
     *  平台获取换卡记录
     */
    @GET
    @Path("/getExchangeCardListForAgent")
    public void getExchangeCardListForAgent() throws Exception {
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agent = ServiceAccess.callService(msg).getContent();
        String AgentAreaValue = agent.get("areaValue").toString();
        String memberId = ControllerContext.getPString("memberId");

        Long indexNum = ControllerContext.getPLong("indexNum");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        p.add(AgentAreaValue+"%");
        String where="";
        if(StringUtils.isNotEmpty(memberId)){
            where+=" and t1.memberId=?";
            p.add(memberId);
        }
        r.add("totalCount");
        String hql = "select count(t1._id) as totalCount from ExchangeCardLog t1 left join Member t2 on t1.memberId=t2._id where t1.belongAreaValue like ?"+where;
        List<Map<String, Object>> couponList = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = (Long) couponList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalNum", totalNum);
        List<String> returnField = new ArrayList<>();
        returnField.add("_id");
        returnField.add("newCardNo");
        returnField.add("oldCardNo");
        returnField.add("createTime");
        returnField.add("realName");
        returnField.add("name");
        String sql = "select" +
                " t1._id" +
                ",t1.newCardNo" +
                ",t1.oldCardNo" +
                ",t1.createTime" +
                ",t2.realName" +
                ",t3.name" +
                " from ExchangeCardLog t1" +
                " left join Member t2 on t1.memberId=t2._id" +
                " left join Factor t3 on t1.belongAreaValue=t3.areaValue" +
                " where t1.belongAreaValue like ?" +where+
                " order by t1.createTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, p);
        resultMap.put("exchangeCardList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 平台管理:禁用/有效 会员
     */
    @GET
    @Path("/setMemberCanUse")
    public void setMemberCanUse() throws Exception {
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);

        if (!"1".equals(msg.getContent().get("level").toString())) {
            throw new UserOperateException(400, "您无此权限");
        }
        boolean canUse = ControllerContext.getPBoolean("canUse");
        String memberId = ControllerContext.getPString("memberId");

        if (StringUtils.isEmpty(memberId)) {
            throw new UserOperateException(400, "设置失败,请刷新页面重试!");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("_id", memberId);
        map.put("canUse", canUse);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", map);
    }
    /**
     *  检查当前登陆会员是否被禁用
     */
    @GET
    @Path("/getMemberIsTrue")
    public void getMemberIsTrue() throws Exception {
        String memberId = ControllerContext.getPString("memberId");
        Map<String,Object> member = MysqlDaoImpl.getInstance().findById2Map("Member",memberId,new String[]{"canUse","isBindCard"},Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), member);
    }

    /**
     *  平台：修改会员信息
     */
    @GET
    @Path("/modifyMember")
    public void modifyMember() throws Exception {
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        msg = ServiceAccess.callService(msg);

        JSONObject text = ControllerContext.getContext().getReq().getContent();

        if (!"1".equals(msg.getContent().get("level").toString())) {
            throw new UserOperateException(400, "您无此权限");
        }

        if (StringUtils.mapValueIsEmpty(text,"modifyType")){
            throw new UserOperateException(500,"操作失败，请重试");
        }

        if (StringUtils.mapValueIsEmpty(text,"mobile") || !Pattern.matches("^[1][3456789][0-9]{9}$",text.get("mobile").toString())){
            throw new UserOperateException(500,"手机号码格式错误");
        }

        Map<String,Object> member = new HashMap<>();
        if("modify".equals(text.get("modifyType").toString())){

            member = MysqlDaoImpl.getInstance().findById2Map("Member",text.get("_id").toString(),new String[]{"isRealName","mobile"},Dao.FieldStrategy.Include);
            if(member == null || member.size()==0){
                throw new UserOperateException(500,"获取会员数据失败");
            }
        }

        if ("add".equals(text.get("modifyType").toString()) || !member.get("mobile").equals(text.get("mobile"))){
            Map<String,Object> params = new HashMap<>();
            params.put("mobile",text.get("mobile"));
            member = MysqlDaoImpl.getInstance().findOne2Map("Member",params,new String[]{"_id","mobile"},Dao.FieldStrategy.Include);
            if(member!=null && member.size()!=0){
                throw new UserOperateException(500,text.get("mobile").toString()+"已被注册");
            }
        }

        if (!StringUtils.mapValueIsEmpty(text,"realName") && !Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("realName"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的会员名字!");
        }
        if (StringUtils.mapValueIsEmpty(text,"sex") && !Pattern.matches("^[12]$",text.get("sex").toString())){
            throw new UserOperateException(400, "请选择性别!");
        }
        if (!StringUtils.mapValueIsEmpty(text,"email") &&
                !Pattern.matches("^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$", (String) text.get("email"))) {
            throw new UserOperateException(400, "请填写邮箱地址!");
        }
        if (!StringUtils.mapValueIsEmpty(text,"realAddress") && StringUtils.mapValueIsEmpty(text,"realArea")) {
            throw new UserOperateException(400, "请选择完整的所在区域位置!");
        }
        if (!StringUtils.mapValueIsEmpty(text,"realAddress") && text.get("realAddress").toString().length() > 200) {
            throw new UserOperateException(400, "请选择完整的所在区域位置,且不能超过200位字符!");
        }
        if (!StringUtils.mapValueIsEmpty(text,"idCard") && !Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", text.get("idCard").toString())) {
            throw new UserOperateException(400, "请输入正确的身份证号码!");
        }

        // 只允许修改以下字段
        String entity = "_id_mobile_realName_sex_email_realArea_realAreaValue_realAddress_idCard_isRealName_canUse";

        boolean isRealName = true;
        Map<String, Object> userInfo = new HashMap<>();
        Iterator it = text.keys();
        // 遍历userInfo数据，添加到Map对象
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = text.get(key);

            if(entity.indexOf(key)==-1){
//                throw new UserOperateException(400, "您无权修改其他资料!");
            }else{
                userInfo.put(key, value);
            }
            if(StringUtils.isEmpty(value.toString())){
                isRealName = false;
            }
        }
        userInfo.put("isRealName",isRealName);
        if("add".equals(text.get("modifyType").toString())){
            String memberId = "M-" + ZQUidUtils.generateMemberNo();
            userInfo.put("canUse",true);
            userInfo.put("_id",memberId);
            userInfo.put("createTime",System.currentTimeMillis());
            userInfo.put("memberNo", JedisUtil.incr("MemberNoSeq"));
            userInfo.put("password", MessageDigestUtils.digest("000000"));
//            userInfo.put("belongAreaValue", msg.getContent().get("areaValue").toString());
//            userInfo.put("belongArea", msg.getContent().get("name").toString());
            //生成会员现金汇总表
            msg = Message.newReqMessage("1:GET@/order/OrderInfo/createMemberAccount");
            msg.getContent().put("memberId", memberId);
            ServiceAccess.callService(msg);
        }
        MysqlDaoImpl.getInstance().saveOrUpdate("Member",userInfo);
    }

    /**
     * 检查是否是管理员
     * @throws Exception
     */
    public String checkAdmin() throws Exception{
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + other.get("agentId"));
        if(!"0".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }
        return other.get("agentId").toString();
    }

    /**
     * @description: 根据会员Id查询会员信息
     * @author: Ali.Cao
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    @GET
    @Path("/getMemberByMemberId")
    public Map<String,Object> getMemberByMemberId() throws Exception{
        String memberId = ControllerContext.getPString("memberId");
        return MysqlDaoImpl.getInstance().findById2Map("Member",memberId,null,null);
    }

    public void activeMember(String agentId,String memberId,String activeCard,String belongAreaValue,Boolean isAutoCard) throws Exception {
        if(StringUtils.isEmpty(memberId)){
            throw new UserOperateException(400, "获取会员数据失败");
        }
        if(StringUtils.isEmpty(belongAreaValue)){
            throw new UserOperateException(400, "获取归属失败");
        }
        String[] belongList = belongAreaValue.substring(1,belongAreaValue.length()-1).split("_");
        if(belongList.length!=1 && belongList.length!=5){
            throw new UserOperateException(400, "只能选择平台或服务站作为归属");
        }

        Map<String,Object> member = MysqlDaoImpl.getInstance().findById2Map("Member",memberId,null,null);

        if(member==null || member.size()==0){
            throw new UserOperateException(500,"获取会员失败");
        }
        if(StringUtils.mapValueIsEmpty(member,"isRealName") || !Boolean.parseBoolean(member.get("isRealName").toString())){
            throw new UserOperateException(500,"请先实名认证");
        }
        if(!StringUtils.mapValueIsEmpty(member,"isBindCard") && Boolean.parseBoolean(member.get("isBindCard").toString())){
            throw new UserOperateException(500,"该会员已经被激活");
        }

        String belongArea = "";
        String factorId = "";

        //如果归属为平台，则会员卡不是必填
        if(belongList.length==1 && agentId.equals(belongList[0]) && StringUtils.isEmpty(activeCard)){
            belongArea = "平台";
            belongAreaValue = "_"+agentId+"_";
            isAutoCard=true;
            factorId=agentId;
        }else if(StringUtils.isEmpty(activeCard) && !isAutoCard){
            throw new UserOperateException(400, "获取激活卡数据失败");
        }
        if(StringUtils.isNotEmpty(activeCard) && activeCard.substring(0,1).equals("C")){
            isAutoCard = true;
        }
        // 如果不是自动生成卡，则从数据库卡段里划分一张卡
        if(!isAutoCard){
            long activeCardLong = Long.parseLong(activeCard);

            //分割 卡
            Map<String,Object> cardInfo = checkMemberCardIsNotActive(activeCard,belongAreaValue,memberId);
            cardInfo.put("memberId",memberId);
            cardInfo.put("activeCard",activeCardLong);
            splitCard(cardInfo);

            factorId= cardInfo.get("receive").toString();

            if(belongList.length==1 && agentId.equals(belongList[0])){
                factorId=agentId;
                belongArea="平台";
            }else{
                JSONObject factor = ServiceAccess.getRemoveEntity("account","Factor",factorId);
                if(factor==null || factor.size()==0){
                    throw new UserOperateException(500,"获取归属服务站失败");
                }
                belongArea = factor.get("name").toString();
            }
        }else{
            activeCard = "C-" + ZQUidUtils.generateMemberCardNo();
            if(StringUtils.isEmpty(belongArea) && StringUtils.isNotEmpty(belongAreaValue)){
                if(!belongAreaValue.contains("F")){
                    throw new UserOperateException(500,"获取归属服务站失败");
                }
                factorId = "F"+belongAreaValue.split("F")[1].split("_")[0];
                Message msg = Message.newReqMessage("1:GET@/account/Factor/show");
                msg.getContent().put("_id",factorId);
                JSONObject factor = ServiceAccess.callService(msg).getContent();
                if(factor==null || factor.size()==0){
                    throw new UserOperateException(500,"获取归属服务站失败");
                }
                belongArea = factor.get("name").toString();

            }
            //卡段表
            Map<String, Object> v = new HashMap<>();
            v.put("_id", UUID.randomUUID().toString());
            v.put("startCardNo", activeCard);
            v.put("endCardNo", activeCard);
            v.put("grant", factorId);
            v.put("receive", memberId);
            v.put("belongAreaValue",belongAreaValue);
            v.put("cardNum", 1);
            v.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("CardField", v);
        }

        //保存会员卡
        Map<String, Object> v = new HashMap<>();
        v.put("_id", UUID.randomUUID().toString());
        v.put("memberCardId", activeCard);
        v.put("memberId", memberId);
        v.put("factorId", factorId);
        v.put("isActive", true);
        v.put("activeTime", System.currentTimeMillis());
        v.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("MemberCard", v);
        //会员
        v.clear();
        v.put("_id", memberId);
        v.put("cardNo", activeCard);
        v.put("isBindCard", true);
        v.put("isFree", false);
        v.put("belongArea", belongArea);
        v.put("belongAreaValue", belongAreaValue);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member", v);

        //生成养老金汇总表
        Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/createPensionAccount");
        msg.getContent().put("memberId", memberId);
        ServiceAccess.callService(msg);

        createMemberAccidentLog(memberId,belongArea,belongAreaValue);//投意外保险

        //生成团队关系
        msg = Message.newReqMessage("1:POST@/order/Team/checkActive");
        msg.getContent().put("regMember", memberId);
        msg.getContent().put("factorId", factorId);
        ServiceAccess.callService(msg);

        //发送短信
        msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendActivate");
        msg.getContent().put("mobile", member.get("mobile"));
        ServiceAccess.callService(msg);

        Map<String,Object> re = new HashMap<>();
        re.put("return_code","SUCCESS");
        re.put("memberId",memberId);
        re.put("cardNo",activeCard);
        toResult(200,re);
    }

    /**
     * 检查会员激活卡是否未激活
     * @param activeCard
     * @param belongAreaValue
     * @return
     * @throws Exception
     */
    public Map<String,Object> checkMemberCardIsNotActive(String activeCard,String belongAreaValue,String memberId) throws Exception{
        if(StringUtils.isEmpty(belongAreaValue)){
            throw new UserOperateException(400, "获取归属失败");
        }
        long activeCardLong = Long.parseLong(activeCard);
        //判断是否存在这张会员卡
        List<String> returnFields = new ArrayList<>();
        returnFields.add("_id");
        returnFields.add("startCardNo");
        returnFields.add("endCardNo");
        returnFields.add("grant");
        returnFields.add("receive");
        returnFields.add("belongAreaValue");

        List<Object> params = new ArrayList<>();
        params.add(activeCardLong);
        params.add(activeCardLong);
        String sql = "select" +
                " _id" +
                ",startCardNo" +
                ",endCardNo" +
                ",`grant`" +
                ",receive" +
                ",belongAreaValue" +
                " from CardField"+
                " where cast(startCardNo as SIGNED INTEGER)<=? and cast(endCardNo as SIGNED INTEGER)>=?";
        List<Map<String, Object>> cardField = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        if(cardField==null || cardField.size()==0 || StringUtils.mapValueIsEmpty(cardField.get(0),"belongAreaValue")){
            Map<String,Object> p = new HashMap<>();
            p.put("memberCardId",activeCard);
            Map<String,Object> memberCard = MysqlDaoImpl.getInstance().findOne2Map("MemberCard",p,null,null);
            if(memberCard==null || memberCard.size()==0){
                throw new UserOperateException(500, "会员卡不存在");
            }else{
                throw new UserOperateException(500, "会员卡已被其他会员激活");
            }
        }

        Map<String,Object> item = cardField.get(0);

        if(!StringUtils.mapValueIsEmpty(item,"receive")
                && item.get("receive").toString().substring(0,1).equals("M")){
            throw new UserOperateException(500, "该会员卡已经被激活");
        }
        if(!item.get("belongAreaValue").toString().equals(belongAreaValue)){
            throw new UserOperateException(500, "会员卡不属于当前归属");
        }

        Message msg = Message.newReqMessage("1:GET@/order/Team/getMemberTeam");
        msg.getContent().put("memberId",memberId);
        JSONObject team = ServiceAccess.callService(msg).getContent();
        if(team!=null && team.size()!=0 && !StringUtils.mapValueIsEmpty(team,"sellerId")){
            String sellerId = team.get("sellerId").toString();

            JSONObject seller = ServiceAccess.getRemoveEntity("account","Seller",sellerId);
            if(seller!=null && seller.size()!=0 && !StringUtils.mapValueIsEmpty(seller,"belongAreaValue")){
                String sellerBelongAreaValue = seller.get("belongAreaValue").toString();
                if(!belongAreaValue.equals(sellerBelongAreaValue)){
                    throw new UserOperateException(500, "会员卡与团队关系[商家]归属不一致");
                }
            }
        }

        return item;
    }

    @GET
    @Path("/checkMemberCardIsNotActive")
    public void checkMemberCardIsNotActive() throws Exception{
        toResult(200,checkMemberCardIsNotActive(ControllerContext.getPString("activeCard"),ControllerContext.getPString("belongAreaValue"),ControllerContext.getPString("memberId")));
    }

    /**
     *  平台：激活会员卡
     */
    @POST
    @Path("/activeMember")
    public void activeMember() throws Exception {
        String agentId = "";
        String factorId = ControllerContext.getPString("factorId");
        if(StringUtils.isEmpty(factorId)){
            agentId = checkAdmin();
        }
        String memberId = ControllerContext.getPString("memberId");
        String activeCardStr = ControllerContext.getPString("activeCard");
        String belongAreaValue = ControllerContext.getPString("belongAreaValue");
        boolean isAutoCard = false;

        if(!StringUtils.isEmpty(ControllerContext.getPString("isAutoCard"))){
            isAutoCard = ControllerContext.getPBoolean("isAutoCard");
        }
        activeMember(agentId,memberId,activeCardStr,belongAreaValue,isAutoCard);
    }

    /**
     * 分割 会员卡卡
     */
    public void splitCard(Map<String,Object> cardField) throws Exception{
        String memberId = cardField.get("memberId").toString();
        String belongAreaValue = cardField.get("belongAreaValue").toString();
        long memberCardId = Long.parseLong(cardField.get("activeCard").toString());

        //卡号段操作
        long queryStartCardNo = Long.parseLong(cardField.get("startCardNo").toString());
        String queryStartCardNoStr = cardField.get("startCardNo").toString();
        long queryEndCardNo = Long.parseLong(cardField.get("endCardNo").toString());
        String queryEndCardNoStr = cardField.get("endCardNo").toString();
        String oldGrant = cardField.get("grant").toString();
        String factorId = cardField.get("receive").toString();
        String memberCardIdStr = cardField.get("activeCard").toString();
        //删除原有的卡段
        List<Object> p = new ArrayList<>();
        p.add(cardField.get("_id"));
        MysqlDaoImpl.getInstance().exeSql("delete from CardField where _id=?", p, "CardField", false);

        if (queryStartCardNo < memberCardId && queryEndCardNo > memberCardId) {
            //如果号码在号段中间
            //将取原本的数据分割成2部分
            //第一部分
            allocationCardFiledImpl(queryStartCardNoStr
                    , String.valueOf(memberCardId-1)
                    , memberCardId - queryStartCardNo
                    , belongAreaValue
                    , oldGrant
                    , factorId);
            //第二部分
            allocationCardFiledImpl(String.valueOf(memberCardId+1)
                    , queryEndCardNoStr
                    , queryEndCardNo - memberCardId
                    , belongAreaValue
                    , oldGrant
                    , factorId);
            //生成一条已使用的号段
            allocationCardFiledImpl(memberCardIdStr
                    ,memberCardIdStr
                    , 1l
                    , ""
                    , factorId
                    , memberId);
        } else if (queryStartCardNoStr.equals(memberCardIdStr) && queryEndCardNo > memberCardId) {
            //号码在左边
            //本身剩余
            allocationCardFiledImpl(String.valueOf(memberCardId+1)
                    , queryEndCardNoStr
                    , queryEndCardNo - queryStartCardNo
                    , belongAreaValue
                    , oldGrant
                    , factorId);
            //生成一条已使用的号段
            allocationCardFiledImpl(queryStartCardNoStr
                    ,queryStartCardNoStr
                    , 1l
                    , ""
                    , factorId
                    , memberId);
        } else if (queryEndCardNoStr.equals(memberCardIdStr) && queryStartCardNo < memberCardId) {
            //号码在右边
            //本身剩余
            allocationCardFiledImpl(queryStartCardNoStr
                    , String.valueOf(memberCardId-1)
                    , queryEndCardNo - queryStartCardNo
                    , belongAreaValue
                    , oldGrant
                    , factorId);
            //生成一条已使用的号段
            allocationCardFiledImpl(queryEndCardNoStr
                    ,queryEndCardNoStr
                    , 1l
                    , ""
                    , factorId
                    , memberId);
        } else if (queryStartCardNoStr.equals(queryEndCardNoStr) && queryStartCardNoStr.equals(memberCardIdStr)) {
            //生成一条已使用的号段
            allocationCardFiledImpl(queryStartCardNoStr
                    ,queryEndCardNoStr
                    , 1l
                    , ""
                    , factorId
                    , memberId);
        }else{
            throw new UserOperateException(500,"未知错误");
        }
    }

    @GET
    @Path("/getMemberListByFactor")
    public void getMemberListByFactor() throws Exception{
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "获取服务站信息失败");
        }
        String userId = other.get("factorId").toString();
        toResult(200,getMemberList(userId,"Factor"));
    }

    @GET
    @Seller
    @Path("/getMemberListBySeller")
    public void getMemberListBySeller() throws Exception{
        String userId = ControllerContext.getContext().getCurrentSellerId();
        if(StringUtils.isEmpty(userId)){
            throw new UserOperateException(400, "获取商家信息失败");
        }
        toResult(200,getMemberList(userId,"Seller"));
    }

    public Page getMemberList(String userId,String userType) throws Exception{
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
        String from = " from Member t1" +
                " left join "+userType+" t2 on t1.belongAreaValue = t2.areaValue";
        String where = " where t2._id = ?";
        params.add(userId);

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
        returnFields.add("mobile");
        returnFields.add("icon");
        returnFields.add("canUse");
        returnFields.add("createTime");
        returnFields.add("realName");
        returnFields.add("realArea");
        returnFields.add("realAddress");
        returnFields.add("from");
        returnFields.add("isRealName");
        returnFields.add("cardNo");
        returnFields.add("sex");

        sql = "select" +
                " t1.mobile" +
                ",t1.icon" +
                ",t1.canUse" +
                ",t1.createTime" +
                ",t1.realName" +
                ",t1.realArea" +
                ",t1.realAddress" +
                ",(case when t1.from='kyb' then '快易帮' when t1.from='aikaka' then '爱卡卡' else '' end) as `from`" +
                ",t1.isRealName" +
                ",t1.cardNo" +
                ",(case when t1.sex=1 then '男' when t1.sex=2 then '女' else '' end) as sex" +
                from+where+
                " order by t1.createTime desc" +
                " limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        page.setItems(re);
        return page;
    }

    /**
     * 重置密码
     * @throws Exception
     */
    @POST
    @Path("/resetPwd")
    public void resetPwd() throws Exception{
        String memberId = ControllerContext.getPString("memberId");
        if(StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"获取会员ID失败");
        }
        Map<String,Object> m = MysqlDaoImpl.getInstance().findById2Map("Member",memberId,new String[]{"_id"},Dao.FieldStrategy.Include);
        if(m == null || m.size()==0){
            throw new UserOperateException(500,"获取会员数据失败");
        }
        String password = "000000";
        m.put("password",MessageDigestUtils.digest(password));
        MysqlDaoImpl.getInstance().saveOrUpdate("Member",m);

        Map<String,Object> re = new HashMap<>();
        re.put("password",password);
        toResult(200,re);
    }


    /**
     * 获取银行卡信息
     * @throws Exception
     */
    @Member
    @GET
    @Path("/getBankInfo")
    public void getBankInfo() throws Exception{
        String memberId = ControllerContext.getContext().getCurrentUserId();
        if(StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"获取会员ID失败");
        }

        Map<String,Object> re = MysqlDaoImpl.getInstance().findById2Map("Member",memberId,new String[]{"bankId","bankName","bankAddress","bankUser","bankUserCardId","bankUserPhone"},Dao.FieldStrategy.Include);
        toResult(200,re);
    }


    /**
     * 保存银行卡信息
     * @throws Exception
     */
    @Member
    @GET
    @Path("/saveBankInfo")
    public void saveBankInfo() throws Exception{
        String memberId = ControllerContext.getContext().getCurrentUserId();
        if(StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"获取会员ID失败");
        }

        JSONObject req = ControllerContext.getContext().getReq().getContent();

        if (StringUtils.mapValueIsEmpty(req,"bankId")) {
            throw new UserOperateException(400, "请输入银行账号!");
        }
        if (req.get("bankName") == null || StringUtils.isEmpty(req.get("bankName").toString())) {
            throw new UserOperateException(400, "请输入开户行!");
        }
        if (StringUtils.mapValueIsEmpty(req,"bankAddress")) {
            throw new UserOperateException(400, "请输入开户行地址!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) req.get("bankUser"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的户名!");
        }
        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) req.get("bankUserPhone"))) {
            throw new UserOperateException(400, "请输入正确的持卡人电话!");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) req.get("bankUserCardId"))) {
            throw new UserOperateException(400, "请输入正确的持卡人身份证号码!");
        }

        String[] entityList = new String[]{"bankId","bankName","bankAddress","bankUser","bankUserCardId","bankUserPhone"};

        Map<String,Object> saveData = new HashMap<>();
        for(String str : entityList){
            saveData.put(str,req.get(str));
        }
        saveData.put("_id",memberId);
        MysqlDaoImpl.getInstance().saveOrUpdate("Member",saveData);
    }

    /**
     * 查询提现记录
     */
    @GET
    @Member
    @Path("/getWithdrawLog")
    public void getWithdrawLog() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        Long indexNum = ControllerContext.getPLong("indexNum");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        Long startTime = ControllerContext.getPLong("startTime");
        Long endTime = ControllerContext.getPLong("endTime");

        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<String> s = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(memberId);

        String where = " where t1.memberId = ? and t1.orderStatus=100 and t1.orderType=14";

        if (startTime != 0) {
            where += " and t1.endTime>=?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.endTime<=?";
            p.add(endTime);
        }

        String from = " from OrderInfo t1" +
                " left join WithdrawLog t2 on t1.orderNo = t2.orderNo";

        s.clear();
        s.add("totalCount");
        s.add("totalWithdraw");
        s.add("totalFee");
        String hql = "select count(t1._id) as totalCount" +
                ",sum(t2.withdrawMoney) as totalWithdraw" +
                ",sum(t2.fee) as totalFee" +
                from + where + " order by t1.endTime desc";
        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(hql, s, p);
        Long totalNum = (Long) list.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalWithdraw", list.get(0).get("totalWithdraw"));
        resultMap.put("totalFee", list.get(0).get("totalFee"));
        r.add("orderNo");
        r.add("createTime");
        r.add("endTime");
        r.add("withdrawMoney");
        r.add("fee");
        r.add("bankName");
        r.add("bankId");
        String sql = "select" +
                " t1.orderNo" +
                ",t1.createTime" +
                ",t1.endTime" +
                ",t2.withdrawMoney" +
                ",t2.fee" +
                ",t2.bankName" +
                ",t2.bankId" +
                from + where + " order by t1.endTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

        resultMap.put("items", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }


    /**
     * 更改会员归属
     */
    @POST
    @Path("/checkMemberBelongValue")
    public void checkMemberBelongValue() throws Exception {
        checkAdmin();
        String memberStr = ControllerContext.getPString("memberId");
        String factorId = ControllerContext.getPString("factorId");

        if(StringUtils.isEmpty(memberStr)){
            throw new UserOperateException(500,"获取会员失败");
        }
        if(StringUtils.isEmpty(factorId)){
            throw new UserOperateException(500,"获取服务站失败");
        }

        JSONObject factor = ServiceAccess.getRemoveEntity("account","Factor",factorId);
        if(factor == null || factor.size()==0){
            throw new UserOperateException(500,"获取服务站数据失败");
        }

        String[] memberArr = memberStr.split(",");

        for(String memberId : memberArr){
            Map<String,Object> member = MysqlDaoImpl.getInstance().findById2Map("Member",memberId,new String[]{"_id"},Dao.FieldStrategy.Include);
            if(member == null || member.size()==0){
                throw new UserOperateException(500,"获取会员["+memberId+"]数据失败");
            }

            member.put("belongArea",factor.get("name"));
            member.put("belongAreaValue",factor.get("areaValue"));
            MysqlDaoImpl.getInstance().saveOrUpdate("Member",member);

            Map<String,Object> params = new HashMap<>();
            params.put("receive",memberId);
            Map<String,Object> cardField = MysqlDaoImpl.getInstance().findOne2Map("CardField",params,new String[]{"_id"},Dao.FieldStrategy.Include);
            if(cardField!=null && cardField.size()>0){
                cardField.put("grant",factorId);
                MysqlDaoImpl.getInstance().saveOrUpdate("CardField",cardField);
            }

            params.clear();
            params.put("memberId",memberId);
            Map<String,Object> memberCard = MysqlDaoImpl.getInstance().findOne2Map("MemberCard",params,new String[]{"_id"},Dao.FieldStrategy.Include);
            if(memberCard!=null && memberCard.size()>0){
                memberCard.put("factorId",factorId);
                MysqlDaoImpl.getInstance().saveOrUpdate("MemberCard",memberCard);
            }

            Message msg = Message.newReqMessage("1:GET@/order/Team/query");
            msg.getContent().put("memberId",memberId);
            JSONArray teamList = ServiceAccess.callService(msg).getContent().getJSONArray("items");
            if(teamList!=null && teamList.size()>0){
                Map<String,Object> team = (Map<String,Object>)teamList.get(0);

                msg = Message.newReqMessage("1:GET@/order/Team/save");
                msg.getContent().put("_id", team.get("_id"));
                msg.getContent().put("factorId", factorId);
                ServiceAccess.callService(msg);
            }
        }
    }


    /**
     * 会员激活获取会员归属
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Member
    @Path("/getBelongAreaById")
    public void getBelongAreaById() throws Exception{
        String memberId= ControllerContext.getPString("_id");
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        params.add(memberId);
        returnFields.add("belongArea");
        String sql = "select" +
                " belongArea" +
                " from" + Dao.getFullTableName("Member") +
                " where _id=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }


    /**
     * 获取好果子传递的数据并创建用户
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMemberToHaoguozi")
    public void getMemberToHaoguozi() throws Exception{
        JSONObject member = ControllerContext.getContext().getReq().getContent();
        String factor = member.getString("factor");
    }
}


