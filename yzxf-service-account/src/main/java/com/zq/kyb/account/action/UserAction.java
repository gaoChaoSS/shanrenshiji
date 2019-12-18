package com.zq.kyb.account.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.secu.SessionServiceImpl;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.PatternUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class UserAction extends BaseActionImpl {
    public static final String TABLE_NAME_SHARE = "ShareCoupon";
    public static final String TABLE_NAME_RECEIVE = "ReceiveCoupon";
    public static final String TABLE_NAME_COUPON = "Coupon";
    public static final String TABLE_NAME_USER = "User";
    //public static final String TABLE_NAME_SELLER_USER = "L_Seller_User";

    @POST
    @Path("/del")
    @Seller(isAdmin = true)
    @Override
    public void del() throws Exception {
        // 检查是否是admin，不能删除admin
        super.del();
    }


    private static String randomPassword() {
        String newPassword = Math.abs(new Random().nextInt(10000000)) + "";
        if (newPassword.length() < 6) {
            String bu = new Random().nextInt(9) * 99999999 + "";
            newPassword += bu.substring(0, 6 - newPassword.length());
        } else {
            newPassword = newPassword.substring(0, 6);
        }
        return newPassword;
    }


    @POST
    @Seller(isAdmin = true)
    @Path("/deleteMore")
    @Override
    public void deleteMore() throws Exception {
        super.deleteMore();
    }


    /**
     * 会员 验证码登陆
     */
    @POST
    @Path("/codeLogin")
    public void codeLogin() throws Exception {
        String phone = ControllerContext.getPString("phone");
        String smsCode = ControllerContext.getPString("smsCode");
        String type = ControllerContext.getPString("type");
        String deviceId = ControllerContext.getPString("deviceId");
        String password = ControllerContext.getPString("password");
        String openId = ControllerContext.getPString("openId");

        if (StringUtils.isEmpty(phone)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号码为空");
        }

        Message msg = Message.newReqMessage("1:GET@/crm/Member/getMemberInfoByMobile");
        msg.getContent().put("mobile", phone);
        JSONObject member = ServiceAccess.callService(msg).getContent();

        if (member == null || member.size()==0) {
            throw new UserOperateException(500,"该手机号码未注册");
        }

        if(StringUtils.mapValueIsEmpty(member,"canUse") || !Boolean.parseBoolean(member.get("canUse").toString())){
            throw new UserOperateException(500,"会员已被禁用");
        }

        if(StringUtils.isNotEmpty(password)){
            // 密码登录
            if(!MessageDigestUtils.digest(password).equals(member.get("password").toString())){
                throw new UserOperateException(500,"密码错误");
            }
        }else{
            // 验证码登录
            if (StringUtils.isEmpty(smsCode)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "验证码为空");
            }

            msg = Message.newReqMessage("1:PUT@/common/Sms/checkSmsCode");
            msg.getContent().put("type", "reg");
            msg.getContent().put("smsCode", smsCode);
            msg.getContent().put("phone", phone);
            ServiceAccess.callService(msg);
        }
        if(StringUtils.isNotEmpty(openId)){
            member.put("openId",openId);
        }

        JSONObject rObj = loginAfter(-1, deviceId, member);
        toResult(200, rObj);
    }

    /**
     * 会员 自动登录
     * @param expireTime
     * @param deviceId
     * @param user
     * @return
     * @throws Exception
     */
    public static JSONObject loginAfter(long expireTime, String deviceId, Map<String, Object> user) throws Exception {
        if(!StringUtils.mapValueIsEmpty(user,"openId")){
            //如果是第3方登录则进行绑定
            String openId = user.get("openId").toString();
            Map<String,Object> params = new HashMap<>();
            params.put("memberId",user.get("_id"));
            Map<String, Object> auth = MysqlDaoImpl.getInstance().findOne2Map("Oauth", params, null, null);
            if(auth==null || auth.size()==0){
                auth=new HashMap<>();
                auth.put("_id", UUID.randomUUID().toString());
                auth.put("memberId", user.get("_id"));
                auth.put("createTime", System.currentTimeMillis());
            }
            auth.put("bind", true);
            auth.put("openId", openId);
            auth.put("wechatOpenId", openId);
            auth.put("memberId", user.get("_id"));
            auth.put("nickName", user.get("realName"));
            auth.put("updateTime", System.currentTimeMillis());
            auth.put("canUse", true);
            auth.put("type", OauthType.WECHAT.toString());
            MysqlDaoImpl.getInstance().saveOrUpdate("Oauth", auth);
        }
        Map<String, Object> session = new HashMap<>();
        String userId = (String) user.get("_id");
        // session
        session.put("creator", userId);
        session.put("loginName", user.get("mobile"));
        session.put("type", "member");
        session.put("deviceId", deviceId);
        session.put("expireTime", expireTime == -1 ? expireTime : (System.currentTimeMillis() + expireTime));

        SessionServiceImpl ss = new SessionServiceImpl();
        session = ss.startSession(session);
        String sessionFullStr = (String) session.get("sessionFullStr");
//        ControllerContext.getContext().setToken(sessionFullStr);

        JSONObject rObj = new JSONObject();
        rObj.put("token", sessionFullStr);
        rObj.put("_id", userId);
        rObj.put("mobile", user.get("mobile"));
        rObj.put("name", user.get("name"));
        rObj.put("icon", user.get("icon"));
        ControllerContext.getContext().setToken(sessionFullStr);

        return rObj;
    }

    @POST
    @Seller
    @Path("/login")
    public void login() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();

        String userType = req.getString("userType");
        String sellerId="",factorId="",agentId="";
        String loginName,password;
        String[] split;
        List<Object> agentAreaValue = new ArrayList<>();
        Long expireTime = -1L;
        if (req.containsKey("expireTime")) {
            expireTime = req.getLong("expireTime");
            expireTime = expireTime == null ? -1 : expireTime;
        }

        if(req.containsKey("key")){
            if(!req.containsKey("val")){
                throw new UserOperateException(500,"登录失败");
            }
            Map<String,Object> data= new EncryptionAction().decryptorMap(req.getString("val"),req.getString("key"));
            loginName = data.get("sellerId").toString();
            password = data.get("password").toString();
        }else{
            if (!req.containsKey("loginName")) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "loginName_is_null");
            }
            if (!req.containsKey("password")) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "password_is_null");
            }
            password = MessageDigestUtils.digest(req.getString("password"));
            loginName = req.getString("loginName");
        }

        String loginNameStr = loginName;
        if (!loginNameStr.contains("@")) {//如果不输入全名称,则自动获取User的loginName
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户名密码错误");
            //拼接ID,例如传过来的值为A000001,则拼接为A-000001
            String tempId;
            if(!loginNameStr.contains("-") && !Pattern.matches("^\\d+",loginNameStr)){
                tempId = loginNameStr.substring(0,1) +"-"+ loginNameStr.substring(1,loginNameStr.length());
            }else{
                tempId = loginNameStr;//其他类型的管理员
            }
            String sql = "select loginName from User where sellerId = ? or factorId = ? or agentId = ?";
            List<String> returnFields = new ArrayList<>();
            returnFields.add("loginName");
            List<Object> params = new ArrayList<>();
            params.add(tempId);
            params.add(tempId);
            params.add(tempId);
            List<Map<String,Object>> user = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
            if(user==null || user.size()==0){
                throw new UserOperateException(500,"用户名或密码错误");
            }
            loginNameStr=user.get(0).get("loginName").toString()+"@"+tempId;
        }
        split = loginNameStr.split("@");
        loginName = split[0];

        Map<String, Object> seller = null;
        Map<String, Object> factor = null;
        Map<String, Object> agent = null;
        Map<String, Object> session = new HashMap<>();
        JSONObject rObj = new JSONObject();
        String agentName = null;
        String adminType = "";

        if ("seller".equals(userType)) {
            sellerId = split[1];
            seller = MysqlDaoImpl.getInstance().findById2Map("Seller", sellerId, null, null);
            if (seller == null || StringUtils.mapValueIsEmpty(seller, "canUse") || !(Boolean) seller.get("canUse")) {
                throw new UserOperateException(400, "商户不存在或被禁用");
            }
            session.put("sellerId", seller.get("_id"));

        } else if ("factor".equals(userType)) {
            factorId = split[1];
            factor = MysqlDaoImpl.getInstance().findById2Map("Factor", factorId, null, null);
            if (factor == null || StringUtils.mapValueIsEmpty(factor, "canUse") || !(Boolean) factor.get("canUse")) {
                throw new UserOperateException(400, "发卡点不存在或被禁用");
            }
            session.put("factorId", factor.get("_id"));
        } else if ("agent".equals(userType)) {
            agentId = split[1];
            agent = MysqlDaoImpl.getInstance().findById2Map("Agent", agentId, null, null);
            if (agent == null || StringUtils.mapValueIsEmpty(agent, "canUse") || !(Boolean) agent.get("canUse")) {
                throw new UserOperateException(400, "代理商不存在或被禁用");
            }
            CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
            if(!StringUtils.mapValueIsEmpty(agent,"level")){
                rObj.put("agentLevel",agent.get("level"));
                cache.putCache("agent_level_cache_" + agent.get("_id"), agent.get("level").toString(), 60 * 60 * 24 * 3);
            }
            if(!StringUtils.mapValueIsEmpty(agent,"adminType")){
                adminType=agent.get("adminType").toString();
            }
            cache.putCache("agent_type_cache_" + agent.get("_id"), adminType, 60 * 60 * 24 * 3);
            session.put("agentId", agent.get("_id"));
            agentName = agent.get("name").toString();

//            //查询代理商所有上级:用于平台管理的 '归属' 条件筛选,自动选择上级,且无法选择其他上级,只能选择他的下级
//            Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentAreaValueById");
//            msg.getContent().put("agentId", agent.get("_id"));
//            msg = ServiceAccess.callService(msg);
//            String areaValue = msg.getContent().get("areaValue").toString();
//            if (StringUtils.isNotEmpty(areaValue) && !"-1".equals(areaValue)) {
//                String[] grade = areaValue.substring(1, areaValue.length() - 1).split("_");
//                areaValue = "_";
//
//                List<String> returnFields = new ArrayList<>();
//                List<Object> params = new ArrayList<>();
//                String sql = "";
//                for (int index = 0; index < grade.length; index++) {
//                    areaValue += grade[index] + "_";
//                    returnFields.add("name");
//                    sql = "select name from Agent where areaValue=?";
//                    params.add(areaValue);
//                    List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
//                    if (re == null || re.size() == 0) {
//                        break;
//                    }
//                    agentAreaValue.add(re.get(0).get("name"));
//
//                    returnFields.clear();
//                    params.clear();
//                }
//                agentAreaValue.add(areaValue);
//            } else if ("-1".equals(areaValue)) {
//                agentAreaValue.add(areaValue);
//            }
        }

        // queryUserParams.put("password", password);
        // queryUserParams.put("status", true);

        String sql = "select _id,loginName,name,password,userNo,isSellerAdmin,isFactorAdmin,factorId,sellerId,agentId,canUse from User where loginName=? and password=?";
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("loginName");
        r.add("name");
        r.add("password");
        r.add("userNo");
        r.add("isSellerAdmin");
        r.add("isFactorAdmin");
        r.add("factorId");
        r.add("sellerId");
        r.add("agentId");
        r.add("canUse");
        ArrayList<Object> p = new ArrayList<>();
        p.add(loginName);
        p.add(password);
        if ("seller".equals(userType)) {
            sql += " and sellerId=?";
            p.add(sellerId);
        } else if ("factor".equals(userType)) {
            sql += " and factorId=?";
            p.add(factorId);
        } else if ("agent".equals(userType)) {
            sql += " and agentId=?";
            p.add(agentId);
        }
        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (list == null || list.size() == 0) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户名或密码错误");
        }

        //session.put("agentId", list.get(0).get("agentId"));
        Map<String, Object> user = list.size() > 0 ? list.get(0) : null;
        if (user == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户名或密码错误");
        } else if (user != null && user.get("canUse") != null && !(Boolean) user.get("canUse")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户已经被禁用");
        }
        //店铺管理员权限
        if ("seller".equals(userType)) {
            List<Object> pUser = new ArrayList<>();
            pUser.add(loginName);
            pUser.add(sellerId);
            List<String> rUser = new ArrayList<>();
            rUser.add("_id");
            String canUserSql = "select" +
                    " _id" +
                    " from User" +
                    " where loginName=? and sellerId=?";
            List<Map<String, Object>> CanUser = MysqlDaoImpl.getInstance().queryBySql(canUserSql, rUser, pUser);
            if (CanUser.size() == 0) {
                throw new UserOperateException(400, "用户没有权限进入该商户");
            }
        } else if ("factor".equals(userType)) {
            List<Object> pUser = new ArrayList<>();
            pUser.add(loginName);
            pUser.add(factorId);
            List<String> rUser = new ArrayList<>();
            rUser.add("_id");
            String canUserSql = "select" +
                    " _id" +
                    " from User" +
                    " where loginName=? and factorId=?";
            List<Map<String, Object>> CanUser = MysqlDaoImpl.getInstance().queryBySql(canUserSql, rUser, pUser);
            if (CanUser.size() == 0) {
                throw new UserOperateException(400, "用户没有权限进入该发卡点");
            }
        }


        String userId = (String) user.get("_id");
        // session
        session.put("creator", userId);
        session.put("loginName", user.get("loginName"));
        // session.put("nickName", user.get("nickName"));
        session.put("type", "user");
        //session.put("deviceId", deviceId);
        session.put("expireTime", expireTime == -1 ? expireTime : (System.currentTimeMillis() + expireTime));
        //将sellerId装入session
//        Map<String, Object> sellp = new HashMap<>();
//        sellp.put("userList", userId);
//        Map<String, Object> sellerMap = MysqlDaoImpl.getInstance().findById2Map("Seller", (String) user.get("sellerId"), null, null);
//        if (sellerMap != null) {
//            session.put("sellerId", sellerMap.get("_id"));
//        }
        Boolean isSellerAdmin = StringUtils.mapValueIsEmpty(user, "isSellerAdmin") ? false : (Boolean) user.get("isSellerAdmin");
        Boolean isFactorAdmin = StringUtils.mapValueIsEmpty(user, "isFactorAdmin") ? false : (Boolean) user.get("isFactorAdmin");
        Boolean isAgent = !StringUtils.mapValueIsEmpty(user, "agentId");
        session.put("isSellerAdmin", isSellerAdmin);
        sellerId = (String) user.get("sellerId");
        factorId = (String) user.get("factorId");
        JSONObject otherData = new JSONObject();
        otherData.put("sellerId", sellerId);
        otherData.put("factorId", factorId);
        otherData.put("agentId", isAgent ? user.get("agentId") : "");

        session.put("otherDataJson", otherData.toString());
        session.put("isFactorAdmin", isFactorAdmin);

        SessionServiceImpl ss = new SessionServiceImpl();
        session = ss.startSession(session);
        String sessionFullStr = (String) session.get("sessionFullStr");
        // ControllerContext.getContext().setToken(sessionFullStr);

        // 将用户的权限资源Map存到cache
        // MemcachedService.getInstance().cacheEntity(loginName, entityId, value, duration);

        rObj.put("token", sessionFullStr);
        rObj.put("agentAreaValue", agentAreaValue);
        rObj.putAll(user);
        //rObj.remove("password");
        rObj.put("sellerId", ControllerContext.getContext().getCurrentSellerId());
        //返回所在的店铺
        ControllerContext.getContext().setToken(sessionFullStr);
        String myId = ControllerContext.getContext().getCurrentUserId();

        // rObj.put("storeInfoList", StoreInfoAction.getStoreInfoList(myId));
        //返回拥有的角色
        //rObj.put("roleList", getRoleList(myId));
        rObj.put("isSellerAdmin", isSellerAdmin);
        rObj.put("isFactorAdmin", isFactorAdmin);
        rObj.put("agentName", agentName);
        rObj.put("adminType", adminType);
        rObj.put("sellerId", sellerId);
        toResult(200, rObj);
        // WebSocketChatService.putUserConn(ControllerContext.getContext().getCurrentUserId());
    }


    @PUT
    @Seller
    @Path("/auth")
    public void auth() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();
        String token = (String) req.get("token");
        if (StringUtils.isEmpty(token)) {
            throw new UserOperateException(400, "token is null");
        }
        // ControllerContext.getContext().setToken(token);
        SessionServiceImpl.checkToken(token);
        ControllerContext.getContext().setToken(token);
        String currentUserId = ControllerContext.getContext().getCurrentUserId();
        Map<String, Object> user = MysqlDaoImpl.getInstance().findById2Map("User", currentUserId, null, null);
        if (user == null) {
            throw new UserOperateException(400, "验证失败");
        }
        JSONObject rObj = new JSONObject();
        rObj.put("name", user.get("name"));
        rObj.put("token", token);
        toResult(200, rObj);
    }


    @Override
    @PUT
    @Seller
    @Path("/save")
    public void save() throws Exception {
        JSONObject reqContent = ControllerContext.getContext().getReq().getContent();
        String _id = (String) reqContent.get("_id");
        //String sellerId = (String) reqContent.get("sellerId");
        String loginName = (String) reqContent.get("loginName");
        //String password = (String) reqContent.get("password");
        String userNo = (String) reqContent.get("userNo");

        reqContent.remove("password");

        boolean isEdit = false;
        boolean isSellerAdmin = false;
        if (StringUtils.isEmpty(_id)) {
            reqContent.put("_id", UUID.randomUUID().toString());
        } else {
            Map<String, Object> user = MysqlDaoImpl.getInstance().findById2Map("User", _id, null, null);
            isEdit = user != null;
            if (isEdit) {
                reqContent.remove("loginName");
                reqContent.remove("password");
                reqContent.remove("userNo");

                //如果改变了角色,删除用户的权限缓存
                //if (reqContent.containsKey("roleList") && reqContent.get("roleList") != null) {
                //MemcachedService.getInstance().removeEntity("User_Resource", _id);

                // }
                CacheServiceFactory.getInc().removeCache(CacheServiceFactory.cache_prefix_userResources + _id);
            }
//            isSellerAdmin = user != null && user.get("isSellerAdmin") != null && (Boolean) user.get("isSellerAdmin");
//            if (isSellerAdmin) {
//                reqContent.remove("roleList");
//                reqContent.remove("storeInfoList");
//                reqContent.remove("canUse");
//            }
        }


        //添加的情况 判断工号和登录名是否重复
        if (!isEdit) {
//            if (StringUtils.isEmpty(userNo)) {
//                throw new UserOperateException(400, "工号不能为空");
//            }
            //if (StringUtils.isEmpty(sellerId)) {
            //    throw new UserOperateException(400, "商户编号不能为空");
            //}
            if (StringUtils.isEmpty(loginName)) {
                throw new UserOperateException(400, "登录名不能为空");
            }
            //if (!ControllerContext.getContext().isAdminUser() && StringUtils.isEmpty(password)) {
            //    throw new UserOperateException(400, "密码不能为空");
            //}

            String pp = "([\\d]|[a-zA-Z]){3,16}";
            if (!PatternUtils.exePattern(pp, loginName)) {
                throw new UserOperateException(400, "登录名只能包含数字与字母,且长度为2-16位");
            }

            Map<String, Object> p = new HashMap<>();
            p.put("loginName", loginName);
            //p.put("sellerId", sellerId);
            Map<String, Object> user = MysqlDaoImpl.getInstance().findOne2Map("User", p, null, null);
            if (user != null) {
                throw new UserOperateException(400, "登录名[" + loginName + "] 已经被注册");
            }

            p = new HashMap<>();
            p.put("userNo", userNo);
            user = MysqlDaoImpl.getInstance().findOne2Map("User", p, null, null);
            if (user != null) {
                throw new UserOperateException(400, "工号[" + userNo + "] 已经被使用");
            }

            //if (StringUtils.isNotEmpty(password)) {
            //     reqContent.put("password", MessageDigestUtils.digest(password));
            // }
        }
        super.save();

        //将对应在的store,添加到notification的ChatGroup
//        List<String> ids = new ArrayList<>();
//        if (isSellerAdmin) {
//            List<Map<String, Object>> allList = StoreInfoAction.getAllStoreList();
//            for (Map<String, Object> map : allList) {
//                ids.add((String) map.get("_id"));
//            }
//        } else {
//            if (!StringUtils.mapValueIsEmpty(reqContent, "storeInfoList")) {
//                JSONArray storeInfoList = reqContent.getJSONArray("storeInfoList");
//                for (Object o : storeInfoList) {
//                    Map m = (Map) o;
//                    ids.add((String) m.get("_id"));
//                }
//            }
//        }
//        String storeIds = StringUtils.join(ids, ",");
//
//        Message msg = Message.newReqMessage("1:POST@/notification/ChatGroup/updateUserGroups");
//        msg.getContent().put("userId", _id);
//        msg.getContent().put("groupIds", storeIds);
//        msg.getContent().put("groupType", "store");
//        ServiceAccess.callService(msg);
    }


    public static void main(String[] args) throws IOException {
        String p = "([\\d]|[a-zA-Z]){3,16}";
//        Logger.getLogger(this.getClass()).info(PatternUtils.exePattern(p, "s234e"));
//        Logger.getLogger(this.getClass()).info(PatternUtils.exePattern(p, "s33"));
//        Logger.getLogger(this.getClass()).info(PatternUtils.exePattern(p, "aaa"));
//        Logger.getLogger(this.getClass()).info(PatternUtils.exePattern(p, "s"));
//        Logger.getLogger(this.getClass()).info(PatternUtils.exePattern(p, "s3"));
//        Logger.getLogger(this.getClass()).info(PatternUtils.exePattern(p, "ddd"));
    }


    @GET
    @Seller
    @Path("/getUserCanUseFactor")
    public void getUserCanUseFactor() throws Exception {
        String userId = ControllerContext.getPString("userId");
        List<Object> p = new ArrayList<>();
        p.add(userId);
        List<String> f = new ArrayList<>();
        f.add("name");
        String where = " where _id=? and isFactorAdmin=true";
        String sql = "select" +
                " name" +
                " from User";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, where, null, null, null, f, p);
        Logger.getLogger(this.getClass()).info(re);
        if (re.size() != 0) {
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }
    @POST
    @Seller
    @Path("/modifyPassWord")
    public void modifyPassWord() throws Exception {
        String userId = ControllerContext.getContext().getPString("userId");
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
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("password");
//        String sql = "select password from Member where _id=?";
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("User", userId, null, null);
        List<Object> params = new ArrayList<>();
        params.add(MessageDigestUtils.digest(secondPwd));
        params.add(userId);
        if (MessageDigestUtils.digest(oldPwd).equals(re.get("password"))) {
            String updateSql = "update User set password = ? where _id=?";
            MysqlDaoImpl.getInstance().exeSql(updateSql, params, "Member");
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "原密码输入错误!");
        }
    }

    @GET
    @Seller
    @Path("/getUserCanUseStore")
    public void getUserCanUseStore() throws Exception {
        String userId = ControllerContext.getContext().getPString("userId");
        List<Object> p = new ArrayList<>();
        p.add(userId);

        List<String> f = new ArrayList<>();
        f.add("name");
        String where = " where _id=? and isSellerAdmin=true";
        String sql = "select" +
                " name" +
                " from User";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, where, null, null, null, f, p);
        Logger.getLogger(this.getClass()).info(re);
        if (re.size() != 0) {
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /**
     * 平台管理:重置密码
     *
     * @throws Exception
     */
    @GET
    @Path("/resetUserPwd")
    public void resetUserPwd() throws Exception {
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if(!"1".equals(agent.get("level")) && !"4".equals(agent.get("level"))){
            throw new UserOperateException(400, "您无此权限!");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("loginName",ControllerContext.getPString("loginName"));

        Map<String, Object> user = MysqlDaoImpl.getInstance().findOne2Map(entityName, params, null, null);
        if(user==null || user.size()==0){
            throw new UserOperateException(400, "获取用户信息失败!");
        }
        params.clear();
        String pwd = randomPassword();
        params.put("_id", user.get("_id"));
        params.put("password", MessageDigestUtils.digest(pwd));
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, params);
        params.clear();
        params.put("password", pwd);
        toResult(200, params);
    }

    /**
     * 获取会员版Banner
     */
    @GET
    @Path("/getMemberBanner")
    public void getMemberBanner() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("entityType");
        r.add("entityId");
        r.add("serialNum");
        String sql ="select _id,icon,entityType,entityId,serialNum from CommodityTypeManage where type=1 order by serialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 获取商城Banner
     */
    @GET
    @Path("/getMallBanner")
    public void getMallBanner() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("entityType");
        r.add("entityId");
        r.add("serialNum");
        String sql ="select _id,icon,entityType,entityId,serialNum from CommodityTypeManage where type=2 order by serialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 获取官网Banner
     */
    @GET
    @Path("/getWebSiteBanner")
    public void getWebSiteBanner() throws Exception {
        String type = ControllerContext.getPString("type");
        String serialNum = ControllerContext.getPString("serialNum");

        String where = " where type=?";
        List<Object> p = new ArrayList<>();
        p.add(type);
        if(StringUtils.isNotEmpty(serialNum)){
            p.add(serialNum);
            where +=" and serialNum=?";
        }
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("entityId");
        r.add("serialNum");
        String sql ="select _id,icon,entityId,serialNum from CommodityTypeManage" +
                where+
                " order by serialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 获取会员版首页ICON
     */
    @GET
    @Path("/getIndexIcon")
    public void getIndexIcon() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("pvalue");
        r.add("value");
        r.add("img");
        r.add("name");
        r.add("memberSerialNum");
        String sql = "select" +
                " _id" +
                ",pvalue" +
                ",value" +
                ",img" +
                ",name" +
                ",memberSerialNum" +
                " from OperateType" +
                " where isNav=true order by memberSerialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 获取商城首页ICON
     */
    @GET
    @Path("/getMallIndexIcon")
    public void getMallIndexIcon() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("pvalue");
        r.add("value");
        r.add("mallImg");
        r.add("cMallImg");
        r.add("bgColor");
        r.add("name");
        r.add("mallSerialNum");
        String sql = "select" +
                " _id" +
                ",pvalue" +
                ",value" +
                ",mallImg" +
                ",cMallImg" +
                ",bgColor" +
                ",name" +
                ",mallSerialNum" +
                " from OperateType" +
                " where mallIsNav=true order by mallSerialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 获取商城首页广告
     */
    @GET
    @Path("/getMallAdvertising")
    public void getMallAdvertising() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("entityType");
        r.add("entityId");
        r.add("serialNum");
        String sql ="select _id,icon,entityType,entityId,serialNum from CommodityTypeManage where type=3 order by serialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 获取商城首页长方形广告
     */
    @GET
    @Path("/getMallRectangle")
    public void getMallRectangle() throws Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("icon");
        r.add("entityType");
        r.add("entityId");
        r.add("serialNum");
        String sql ="select _id,icon,entityType,entityId,serialNum from CommodityTypeManage where type=4 order by serialNum";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    /**
     * 更换广告.
     */
    @POST
    @Path("/updateAdvertising")
    public void updateAdvertising() throws Exception {
        String id = ControllerContext.getPString("_id");
        int serialNum = ControllerContext.getPInteger("serialNum");
        String icon = ControllerContext.getPString("icon");
        String associatedType = ControllerContext.getPString("entityType");
        String associatedId = ControllerContext.getPString("entityId");
        String type = ControllerContext.getPString("type");
        String tableName = associatedType;

        if(StringUtils.isEmpty(associatedType)){
            tableName = "Seller";
        }
        if(Pattern.matches("^(Seller)|(ProductInfo)$",tableName)){
            Map<String,Object> re = MysqlDaoImpl.getInstance().findById2Map(tableName,associatedId,null,null);
            if(StringUtils.mapValueIsEmpty(re,"_id")){
                throw new UserOperateException(400, "请输入正确的关联ID!");
            }
        }

        Map<String,Object> v = new HashMap<>();
        if(StringUtils.isEmpty(id)){
            id=UUID.randomUUID().toString();
        }
        v.put("_id",id);
        v.put("serialNum",serialNum);
        v.put("type",type);
        v.put("icon",icon);
        v.put("entityType",tableName);
        v.put("entityId",associatedId);
        MysqlDaoImpl.getInstance().saveOrUpdate("CommodityTypeManage",v);
    }
    /**
     * 更换广告.
     */
    @POST
    @Path("/updateWebSite")
    public void updateWebSite() throws Exception {
        String id = ControllerContext.getPString("_id");
        int serialNum = ControllerContext.getPInteger("serialNum");
        String icon = ControllerContext.getPString("icon");
        String associatedId = ControllerContext.getPString("entityId");
        String type = ControllerContext.getPString("type");
        Map<String,Object> v = new HashMap<>();
        if(StringUtils.isEmpty(id)){
            id=UUID.randomUUID().toString();
        }
        v.put("_id",id);
        v.put("serialNum",serialNum);
        v.put("type",type);
        v.put("icon",icon);
        v.put("entityId",associatedId);
        MysqlDaoImpl.getInstance().saveOrUpdate("CommodityTypeManage",v);
    }
    /**
     * 更换图标.
     */
    @POST
    @Path("/updateIcon")
    public void updateIcon() throws Exception {
        String oldId = ControllerContext.getPString("oldId");
        String newId = ControllerContext.getPString("newId");
        String icon = ControllerContext.getPString("icon");
        String field = ControllerContext.getPString("field");
        String section = ControllerContext.getPString("section");
        String bgColor = ControllerContext.getPString("bgColor");
        int serialNum = ControllerContext.getPInteger("serialNum");
        Map<String,Object> v = new HashMap<>();
        //判断是否更换了类型或者只是更换当前类型的图片
        //相等则只是更换图片
        if(oldId.equals(newId)){
            v.put("_id",oldId);
            v.put(field,icon);
            if(StringUtils.isNotEmpty(bgColor)){
                v.put("bgColor",bgColor);
            }
            MysqlDaoImpl.getInstance().saveOrUpdate("OperateType",v);
        }else{
            //检查更换的是否是已经存在的
            Map<String,Object> newItem = MysqlDaoImpl.getInstance().findById2Map("OperateType",newId,new String[]{"isNav"},Dao.FieldStrategy.Include);
            if(newItem!=null && newItem.size()!=0 && !StringUtils.mapValueIsEmpty(newItem,"isNav")
                    && Boolean.parseBoolean(newItem.get("isNav").toString())){
                throw new UserOperateException(500,"替换的分类已存在");
            }

            //删除
            v.put("_id",oldId);
            v.put(section,false);
            MysqlDaoImpl.getInstance().saveOrUpdate("OperateType",v);
            v.clear();
            //增加
            v.put("_id",newId);
            v.put(field,icon);
            if("isNav".equals(section)){
                v.put("memberSerialNum",serialNum);
            }else{
                v.put("mallSerialNum",serialNum);
            }
            if(StringUtils.isNotEmpty(bgColor)){
                v.put("bgColor",bgColor);
            }
            v.put(section,true);
            MysqlDaoImpl.getInstance().saveOrUpdate("OperateType",v);
        }

    }

    /**
     * 获取代付支行城市.
     */
    @GET
    @Path("/getBankCity")
    public void getBankCity() throws Exception {
        String type = ControllerContext.getPString("type");

        String file = "";
        String where = " where 1=1";
        String group = "";
        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if(StringUtils.isEmpty(type)){
            file = "distinct province,provinceValue";
            returnField.add("province");
            returnField.add("provinceValue");
            group = " group by province";
        }else{
            String provinceValue = ControllerContext.getPString("provinceValue");
            if(StringUtils.isNotEmpty(provinceValue)){
                where += " and provinceValue=?";
                params.add(provinceValue);
            }
            file = "city,cityValue";
            returnField.add("city");
            returnField.add("cityValue");
        }

        String sql = "select "+file+" from BankCity"+where+group;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        toResult(200,re);
    }

    /**
     * 分销系统获取用户信息.
     */
    @GET
    @Path("/getUserInfo")
    public void getUserInfo() throws Exception {
        String userId = ControllerContext.getPString("userId");
        String mobile = ControllerContext.getPString("mobile");
        String userType = ControllerContext.getPString("userType");
        String[] userTypeList = new String[]{"Seller","Factor","Member"};
        int count = 0;

        if(StringUtils.isEmpty(userId) && StringUtils.isEmpty(mobile)){
            throw new UserOperateException(500,(StringUtils.isEmpty(userId)?"userId":"mobile")+"不能为空");
        }
        if(StringUtils.isEmpty(userType)){
            throw new UserOperateException(500,"用户类型不能为空");
        }
        userType = userType.toLowerCase();
        userType = userType.substring(0, 1).toUpperCase() + userType.substring(1);
        for(String str : userTypeList){
            if(!str.equals(userType)){
                count++;
            }
        }
        if(count>=3){
            throw new UserOperateException(500,"用户类型错误");
        }

        List<Map<String,Object>> re = new ArrayList<>();
        if(userType.equals("Member")){
            Message msg = Message.newReqMessage("1:GET@/crm/Member/getMyInfoById2");
            if(StringUtils.isNotEmpty(userId)){
                msg.getContent().put("_id",userId);
            }
            if(StringUtils.isNotEmpty(mobile)){
                msg.getContent().put("mobile",mobile);
            }
            re.add(ServiceAccess.callService(msg).getContent());
        }else{
            Map<String,Object> params = new HashMap<>();
            if(StringUtils.isNotEmpty(userId)){
                params.put("_id",userId);
            }
            if(StringUtils.isNotEmpty(mobile)){
                params.put(userType.equals("Seller")?"phone":"mobile",mobile);
            }
            String[] fields = new String[]{userType.equals("Seller")?"phone":"mobile","name","area","address","realCard"};
            re = MysqlDaoImpl.getInstance().findAll2Map(userType,params,null,fields,Dao.FieldStrategy.Include);
            if(re!=null && re.size()!=0 && userType.equals("Seller")){
                for(Map<String,Object> item : re){
                    mobile = item.get("phone").toString();
                    item.remove("phone");
                    item.put("mobile",mobile);
                }
            }
        }
        toResult(200,re);
    }

    public static void checkAdmin() throws Exception{
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
    }

    /**
     * 获取会员、商家、服务站、代理商关联情况
     */
    @GET
    @Path("/getRelateUser")
    public void getRelateUser() throws Exception {
        checkAdmin();
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize =ControllerContext.getPInteger("pageSize");
        String mobile = ControllerContext.getPString("mobile");
        String name = ControllerContext.getPString("name");
        String userId = ControllerContext.getPString("userId");
        String createTime = ControllerContext.getPString("createTime");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String where = " where 1=1 and t5.adminType is null";

        if(StringUtils.isEmpty(ControllerContext.getPString("pageNo"))){
            pageNo = 1;
        }
        if(StringUtils.isEmpty(ControllerContext.getPString("pageSize"))){
            pageSize = 10;
        }

        if (startTime != 0) {
            where += " and t1.createTime>?";
            params.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            params.add(endTime);
        }

        if(StringUtils.isNotEmpty(userId)){
            where += " and t1._id=?";
            params.add(userId);
        }

        if(StringUtils.isNotEmpty(mobile)){
            where += " and (t2.mobile like ? or t3.phone like ? or t4.mobile like ? or t5.phone like ?)";
            mobile = "%"+mobile+"%";
            params.add(mobile);
            params.add(mobile);
            params.add(mobile);
            params.add(mobile);
        }

        if(StringUtils.isNotEmpty(name)){
            where += " and (t2.realName like ? or t3.name like ? or t4.name like ? or t5.name like ?)";
            name = "%"+name+"%";
            params.add(name);
            params.add(name);
            params.add(name);
            params.add(name);
        }

        String from = " from User t1" +
                " left join Member t2 on t1.memberId = t2._id" +
                " left join Seller t3 on t1.sellerId = t3._id" +
                " left join Factor t4 on t1.factorId = t4._id" +
                " left join Agent t5 on t1.agentId = t5._id";
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
        returnFields.add("userId");
        returnFields.add("loginName");
        returnFields.add("memberId");
        returnFields.add("sellerId");
        returnFields.add("factorId");
        returnFields.add("agentId");
        returnFields.add("createTime");
        returnFields.add("memberMobile");
        returnFields.add("memberName");
        returnFields.add("sellerName");
        returnFields.add("factorName");
        returnFields.add("agentName");

        sql = "select" +
                " t1._id as userId" +
                ",t1.loginName" +
                ",t1.memberId" +
                ",t1.sellerId" +
                ",t1.factorId" +
                ",t1.agentId" +
                ",t1.createTime" +

                ",t2.mobile as memberMobile" +
                ",t2.realName as memberName" +
                ",t3.name as sellerName" +
                ",t4.name as factorName" +
                ",t5.name as agentName" +
                from + where+
                " order by t1.createTime desc" +
                " limit " + page.getStartIndex() + "," + pageSize;

        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        page.setItems(re);
        toResult(200,page);
    }

    /**
     * 获取会员、商家、服务站、代理商基本信息
     */
    @GET
    @Path("/getUserBaseInfo")
    public void getUserBaseInfo() throws Exception {
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize =ControllerContext.getPInteger("pageSize");
        String userType = ControllerContext.getPString("userType");
        String search = ControllerContext.getPString("search");

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String where = " where 1=1",fields="";

        if(StringUtils.isEmpty(userType) && !Pattern.matches("^(Seller)|(Factor)|(Agent)|(Member)$",userType)){
            throw new UserOperateException(500,"错误的用户类型");
        }

        if(StringUtils.isEmpty(ControllerContext.getPString("pageNo"))){
            pageNo = 1;
        }
        if(StringUtils.isEmpty(ControllerContext.getPString("pageSize"))){
            pageSize = 10;
        }

        if(StringUtils.isNotEmpty(search)){
            if(Pattern.matches("^(Seller)|(Agent)$",userType)){
                where += " and phone like ? or name like ?";
            }else{
                where += " and mobile like ? or name like ?";
            }
            params.add("%"+search+"%");
            params.add("%"+search+"%");
        }

        String from = " from "+userType;
        String sql = "select count(_id) as totalCount" +
                from+where;
        returnFields.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        returnFields.clear();
        returnFields.add("_id");

        if(Pattern.matches("^(Seller)|(Agent)$",userType)){
            fields+=",phone as mobile";
        }else{
            fields+=",mobile";
        }
        if(Pattern.matches("^(Member)$",userType)){
            fields+=",realName as name";
        }else{
            fields+=",name";
        }

        returnFields.add("mobile");
        returnFields.add("name");

        sql = "select" +
                " _id" +
                fields+
                from + where+
                " limit " + page.getStartIndex() + "," + pageSize;

        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        page.setItems(re);
        toResult(200,page);
    }

    /**
     * 绑定关联 会员、商家、服务站、代理商
     */
    @POST
    @Path("/bindUser")
    public void bindUser() throws Exception {
        checkAdmin();
        String userId = ControllerContext.getPString("userId");
        String bindId = ControllerContext.getPString("bindId");
        String bindType = ControllerContext.getPString("bindType");

        if(StringUtils.isEmpty(bindType) && !Pattern.matches("^(Seller)|(Factor)|(Agent)|(Member)$",bindType)){
            throw new UserOperateException(500,"错误的用户类型");
        }
        String bindTypeStr = bindType.toLowerCase()+"Id";

        Map<String,Object> params = new HashMap<>();

        // 先检查userId或bindId是否已经做了关联
        if(!StringUtils.isEmpty(userId)){
            params.put("_id",userId);
//            params.put(bindType.toLowerCase()+"Id",bindId);
        }
        Map<String,Object> user = MysqlDaoImpl.getInstance().findOne2Map("User",params,null,null);
        if(user==null || user.size()==0){
            throw new UserOperateException(500,"关联失败，用户不存在");
        }

        List<String> list = new ArrayList<>();
        list.add("sellerId");
        list.add("factorId");
        list.add("agentId");
        list.add("memberId");

        int count = 0;
        for(String val : list){
            if(!bindTypeStr.equals(val) && !StringUtils.mapValueIsEmpty(user,val)){
                count++;
            }
        }
        if(count==0){
            throw new UserOperateException(500,"关联失败，此账户仅关联了一个角色，无需再更换当前角色");
        }

        // 若准备绑定的账号类型A已经在user中存在，则先将原先的账号类型B新开一个user关联
        // 当A从原来的user中删除后，user没有关联账号，则删除user
        if(!StringUtils.mapValueIsEmpty(user,bindTypeStr) && user.get(bindTypeStr).equals(bindId)){
            throw new UserOperateException(500,"关联失败，无法与相同角色关联");
        } else if(!StringUtils.mapValueIsEmpty(user,bindTypeStr)){
            // 将原来的账号新开一个User保存
            Map<String,Object> newUser = new HashMap<>();
            newUser.put("_id",ZQUidUtils.genUUID());
            newUser.put("loginName",ZQUidUtils.generateUserNo());
            newUser.put("password", user.get("password"));
            newUser.put("canUse", user.get("canUse"));
            newUser.put(bindTypeStr, user.get(bindTypeStr));
            newUser.put("createTime", System.currentTimeMillis());
            newUser = checkBindTypeMap(newUser,bindType,true);
            MysqlDaoImpl.getInstance().saveOrUpdate("User",newUser);
        }

        //若准备绑定的角色bindId已经存在于其他角色，则移除
        params.clear();
        params.put(bindTypeStr,bindId);
        Map<String,Object> oldBindUser = MysqlDaoImpl.getInstance().findOne2Map("User",params,null,null);
        if(oldBindUser!=null && oldBindUser.size()!=0){
            oldBindUser.put(bindTypeStr,"");

            //若移除后没有任何关联角色，则删除此账号
            count = 0;
            for(String val : list){
                if(!StringUtils.mapValueIsEmpty(oldBindUser,val)){
                    count++;
                }
            }
            if(count==0){
                MysqlDaoImpl.getInstance().remove("User",oldBindUser.get("_id").toString());
            }else{
                oldBindUser = checkBindTypeMap(oldBindUser,bindType,false);
                MysqlDaoImpl.getInstance().saveOrUpdate("User",oldBindUser);
            }
        }

        user = checkBindTypeMap(user,bindType,true);
        user.put(bindTypeStr,bindId);
        MysqlDaoImpl.getInstance().saveOrUpdate("User",user);

        // 若修改的是会员ID或商家ID，且user已经绑定了两个，则更新会员和商家的归属关系;
        // 若更换的是商家，则更改当前user会员的团队关系；若更换的是会员，则改变移入到当前user会员的团队关系
        if(Pattern.matches("^(Member)|(Seller)$",bindType)
                && !StringUtils.mapValueIsEmpty(user,"memberId")
                && !StringUtils.mapValueIsEmpty(user,"sellerId")){
            Message msg = Message.newReqMessage("1:POST@/order/Team/updateUserMemberTeam");

            msg.getContent().put("memberId",user.get("memberId"));
            msg.getContent().put("sellerId",user.get("sellerId"));
            ServiceAccess.callService(msg).getContent();
        }

    }

    /**
     * 解除绑定 会员、商家、服务站、代理商
     */
    @POST
    @Path("/relieveUser")
    public void relieveUser() throws Exception {
        checkAdmin();
        String userId = ControllerContext.getPString("userId");
        String bindId = ControllerContext.getPString("bindId");
        String bindType = ControllerContext.getPString("bindType");

        if(StringUtils.isEmpty(bindType) && !Pattern.matches("^(Seller)|(Factor)|(Agent)|(Member)$",bindType)){
            throw new UserOperateException(500,"错误的用户类型");
        }
        String bindTypeStr = bindType.toLowerCase()+"Id";

        Map<String,Object> params = new HashMap<>();
        // 先检查userId或bindId是否已经做了关联
        if(!StringUtils.isEmpty(userId)){
            params.put("_id",userId);
            params.put(bindType.toLowerCase()+"Id",bindId);
        }
        Map<String,Object> user = MysqlDaoImpl.getInstance().findOne2Map("User",params,null,null);
        if(user==null || user.size()==0){
            throw new UserOperateException(500,"解绑失败，角色账号不存在");
        }

        List<String> list = new ArrayList<>();
        list.add("sellerId");
        list.add("factorId");
        list.add("agentId");
        list.add("memberId");

        int count = 0;
        for(String val : list){
            if(!bindTypeStr.equals(val) && !StringUtils.mapValueIsEmpty(user,val)){
                count++;
            }
        }
        if(count==0){
            throw new UserOperateException(500,"解绑失败，此账户仅关联了一个角色，无需解绑");
        }

        // 将原来的账号新开一个User保存
        Map<String,Object> newUser = new HashMap<>();
        newUser.put("_id",ZQUidUtils.genUUID());
        newUser.put("loginName",ZQUidUtils.generateUserNo());
        newUser.put("password", user.get("password"));
        newUser.put("canUse", user.get("canUse"));
        newUser.put(bindTypeStr, user.get(bindTypeStr));
        newUser.put("createTime", System.currentTimeMillis());
        newUser = checkBindTypeMap(newUser,bindType,true);

        MysqlDaoImpl.getInstance().saveOrUpdate("User",newUser);

        // 移除原来账号的角色
        user.put(bindTypeStr,"");
        user = checkBindTypeMap(user,bindType,false);
        MysqlDaoImpl.getInstance().saveOrUpdate("User",user);
    }

    public Map<String,Object> checkBindTypeMap(Map<String,Object> user,String bindType,boolean flag) throws Exception{
        if(bindType.equals("Factor")){
            user.put("isFactorAdmin",flag);
        }else if(bindType.equals("Seller")){
            user.put("isSellerAdmin",flag);
        }
        return user;
    }

    /**
     * 重置账号角色密码
     */
    @POST
    @Path("/resetPwd")
    public void resetPwd() throws Exception {
        checkAdmin();
        String userId = ControllerContext.getPString("userId");
//        String bindId = ControllerContext.getPString("bindId");
        String bindType = ControllerContext.getPString("bindType");

        if(StringUtils.isEmpty(bindType) && !Pattern.matches("^(Seller)|(Factor)|(Agent)|(Member)$",bindType)){
            throw new UserOperateException(500,"错误的用户类型");
        }

        Map<String,Object> user = MysqlDaoImpl.getInstance().findById2Map("User",userId,null,null);
        if(user==null || user.size()==0){
            throw new UserOperateException(500,"未找到账号");
        }
        if(StringUtils.mapValueIsEmpty(user,bindType.toLowerCase()+"Id")){
            throw new UserOperateException(500,"未找到角色账号");
        }

        Map<String,Object> re = new HashMap<>();
        String password = "000000";
        if("Member".equals(bindType)){
            Message msg = Message.newReqMessage("1:POST@/crm/Member/resetPwd");
            msg.getContent().put("memberId",ControllerContext.getPString("bindId"));
            JSONObject member = ServiceAccess.callService(msg).getContent();
            password = member.get("password").toString();
        }else{
            re.put("_id",userId);
            re.put("password",MessageDigestUtils.digest(password));
            MysqlDaoImpl.getInstance().saveOrUpdate("User",re);
        }
        re.put("password",password);
        toResult(200,re);
    }
}

