package com.zq.kyb.account.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.regex.Pattern;

public class UserPendingAction extends BaseActionImpl {

    /**
     * 查询
     *
     * @throws Exception
     */
    @Override
    public void query() throws Exception {
        super.query();
    }

    @Override
    public void show() throws Exception {
        super.show();
    }

    /**
     * 申请:提交初审资料
     *
     * @throws Exception
     */
    @POST
    @Path("/userApplyFirst")
    public void userApplyFirst() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();

        if (text == null || StringUtils.mapValueIsEmpty(text,"_id") || StringUtils.mapValueIsEmpty(text,"pendingId") ) {
            throw new UserOperateException(400, "获取数据失败!");
        }

        Map<String,Object> pending = MysqlDaoImpl.getInstance().findById2Map(entityName,text.get("pendingId").toString(),null,null);
        if(pending==null || pending.size()==0){
            throw new UserOperateException(400, "获取数据失败!");
        }
        if(!Pattern.matches("^(0)|(0.3)$",pending.get("status").toString())){
            throw new UserOperateException(400, "此申请不是初审申请!");
        }

        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("contactPerson"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的联系人名字!");
        }
        if (!Pattern.matches("^[1][34578][0-9]{9}$",
                "Factor".equals(pending.get("ownerType").toString())?(String) text.get("mobile"):(String) text.get("phone"))) {
            throw new UserOperateException(400, "手机号格式错误!");
        }
        if (StringUtils.mapValueIsEmpty(text,"area")) {
            if("Seller".equals(pending.get("ownerType").toString())){
                if(StringUtils.mapValueIsEmpty(text,"areaValue") || text.get("areaValue").toString().length()>200){
                    throw new UserOperateException(400, "请选择完整的所在区域位置!");
                }
            }else{
                if(StringUtils.mapValueIsEmpty(text,"realAreaValue") || text.get("realAreaValue").toString().length()>200){
                    throw new UserOperateException(400, "请选择完整的所在区域位置!");
                }
            }
        }

        pending.put("text",text.toString());
        pending.put("status","0.1");
        pending.put("owner",text.get("contactPerson"));
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", pending);
    }

    /**
     * 审核:初审
     *
     * @throws Exception
     */
    @POST
    @Path("/verifyFirstUser")
    public void verifyFirstUser() throws Exception {
        String pendingId = ControllerContext.getPString("pendingId");
        String status = ControllerContext.getPString("status");
        String explain = ControllerContext.getPString("explain");

        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if (!"1".equals(agent.get("level").toString()) && !"4".equals(agent.get("level").toString())) {
            throw new UserOperateException(400, "您没有审批权限!");
        }
        if(StringUtils.isEmpty(pendingId)){
            throw new UserOperateException(400, "获取数据失败!");
        }
        Map<String,Object> pending = MysqlDaoImpl.getInstance().findById2Map(entityName,pendingId,null,null);
        if(pending==null || pending.size()==0){
            throw new UserOperateException(400, "获取数据失败!");
        }
        if("agent".equals(pending.get("ownerType").toString()) && !"1".equals(agent.get("level"))){
            throw new UserOperateException(400, "您没有审批权限!");
        }

        if(StringUtils.mapValueIsEmpty(pending,"status") || !"0.1".equals(pending.get("status").toString())){
            throw new UserOperateException(400, "此申请不是待初审申请!");
        }
        if(!"0.2".equals(status) && !"0.3".equals(status)){
            throw new UserOperateException(400, "错误的审核结果!");
        }else if("0.3".equals(status) && (StringUtils.isEmpty(explain) || explain.length()>200)){
            throw new UserOperateException(400, "请填写审批不通过的理由(200字以内)!");
        }else if("0.3".equals(status)){
            pending.put("explain",explain);
        }else{//存一个空值,清空之前的不通过理由
            pending.put("explain","");
        }

        pending.put("status",status);
        pending.put("verifierFirst",agent.get("name"));
        pending.put("verifierFirstId",agent.get("_id"));
        pending.put("verifierFirstTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", pending);
    }


    /**
     * 申请商户:保存商户草稿
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Member
    @Path("/saveApplySeller")
    public void saveApplySeller() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();

        //获取商家归属(发卡点);若是平台管理申请,则自动选择当前登录的发卡点 (暂无做会员端申请的判断)
        String belongAreaValue = (String) text.get("belongAreaValue");//发卡点value
        text.put("belongAreaValue", belongAreaValue);

        //获取当前登录用户
        //获取平台管理ID
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        String createId;
        String create = "";

        if (agent == null || agent.get("_id") == null) {
            createId = ControllerContext.getContext().getCurrentUserId();
//            if (StringUtils.isEmpty(createId)) {
//                createId = "other";//通过官网申请
//                create = "官网申请";
//            }
        } else {
            createId = (String) agent.get("_id");
            create = (String) agent.get("name");
        }


        String _id = "";
        String ownerId = "";
        boolean isFirst = false;
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            _id = UUID.randomUUID().toString();
            ownerId = "S-" + ZQUidUtils.generateSellerNo();
            text.put("_id", ownerId);
            isFirst = true;
        } else {
            _id = text.get("pendingId").toString();
            text.remove("pendingId");//text只保存seller里存在的字段
            ownerId = text.get("_id").toString();
        }

        Map<String, Object> s = new HashMap<>();
        s.put("_id", _id);
        s.put("status", 0);//0:草稿;0.1:初审;0.2;初审通过;0.3;初审不通过;1:待审;2:通过;3:不通过
        s.put("owner", text.get("name"));
        s.put("ownerId", ownerId);
        s.put("ownerType", "Seller");//Seller,Factor,Agent
        s.put("create", create);
        s.put("createId", createId);
        s.put("text", text.toString());
        s.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", s);
        if (isFirst) {
            Map<String, Object> re = new HashMap<>();
            re.put("pendingId", _id);
            re.put("ownerId", ownerId);
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /**
     * 申请商户:提交商户申请
     *
     * @throws Exception
     */
    @POST
    @Path("/submitSeller")
    public void submitSeller() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();
        //text.put("creatorType","member");//模拟测试用数据，之后需改正

        if ((text == null || text.get("_id") == null || StringUtils.isEmpty(text.get("_id").toString())) ||
                (text.get("creator")!=null  && StringUtils.isEmpty(text.get("creator").toString()) && !"other".equals(text.get("creatorType")))) {
            throw new UserOperateException(400, "网络异常,请刷新页面重试!");
        }

        //获取平台管理ID
        String createId="";
        String create = "";
        //获取当前申请是否重复
        int userApplyType = 0;//0:平台;1:会员/发卡点;2:官网/非会员;
        if(!"other".equals(text.get("creatorType"))){
            List<Object> params = new ArrayList<>();
            List<String> returnFields = new ArrayList<>();
            returnFields.add("createId");
            params.add(text.get("_id").toString());
            String sql = "select createId from UserPending where ownerId=? and status=1";
            List<Map<String, Object>> pend = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            if (pend != null && pend.size() != 0) {
                throw new UserOperateException(400, "已经存在当前用户的审核申请!");
            }

            returnFields.add("create");
            //获取创建者ID,用来判断是否是会员;因为会员进入申请页面的时候已经生成了创建者,不必再这里再次生成;会员提交的申请也不需要归属
            sql = "select `create`,createId from UserPending where ownerId=? and status in (0.2,0.3,3)";
            pend = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

            if (pend != null && pend.size() != 0 && Pattern.matches("^[MF]$", pend.get(0).get("createId").toString().substring(0, 1))) {
                if("M".equals(pend.get(0).get("createId").toString().substring(0, 1)) || "F".equals(pend.get(0).get("createId").toString().substring(0, 1))){
                    userApplyType=1;
                }else{
                    userApplyType=0;
                }
                if(userApplyType==1){
                    createId=pend.get(0).get("createId").toString();
                    if(pend.get(0).get("create")==null || StringUtils.isEmpty(pend.get(0).get("create").toString())){//若没有实名认证
                        create=createId;
                    }else{
                        create=pend.get(0).get("create").toString();
                    }
                }
            }
        }else{
            userApplyType = 2;
        }

        boolean isAdmin = false;

        Map<String, Object> s = new HashMap<>();
        Map<String, Object> agent = new HashMap<>();
        //获取商家归属(发卡点);若是会员申请,则不选择归属,提交审核后交给管理员选择;若是平台管理申请,则自动选择当前登录的发卡点
        if (userApplyType==0) {
            String belongAreaValue = (String) text.get("belongAreaValue");
            agent = new AgentAction().getCurrentAgent();
            String agentAreaValue = (String) agent.get("areaValue");//当前登录的县级代理商value
            if (!"4".equals(agent.get("level")) && !"1".equals(agent.get("level"))) {
                throw new UserOperateException(400, "您没有审核商户的权限!");
            }else{
                isAdmin=true;
            }
            if (StringUtils.isEmpty(belongAreaValue)) {
                throw new UserOperateException(400, "请选择发卡点!");
            }
            if (!Pattern.matches("^(" + agentAreaValue + "\\S*)$", belongAreaValue)) {
                throw new UserOperateException(400, "只能选择您旗下的发卡点!");
            }
            Map<String, Object> factorMap = new FactorAction().getFactorNameByValue(belongAreaValue);
            if (factorMap != null && factorMap.get("name") != null) {
                text.put("belongArea", factorMap.get("name"));//获取发卡点的名字
            }
            text.put("belongAreaValue", belongAreaValue);

            String[] belongValueArr = agentAreaValue.substring(1, agentAreaValue.length() - 1).split("_");
            text.put("pid", belongValueArr[belongValueArr.length - 1]);

            s.put("verifierFirst",agent.get("name"));
            s.put("verifierFirstId",agent.get("_id"));
            s.put("verifierFirstTime",System.currentTimeMillis());
        }else if(userApplyType==1){
            // 自动选择创建者(会员，发卡点)的归属：如果创建者归属于某个发卡点或服务站，则申请的商家归属与创建者一致，并提交给此归属所在的服务中心审核；
            // 若创建者归属于平台,则根据申请所填写的地址去匹配上级，若没有匹配，则提交给平台审核（归属为空则平台审核）
            if("M".equals(createId.substring(0, 1))){
                Message msg = Message.newReqMessage("1:GET@/crm/Member/show");
                msg.getContent().put("_id", createId);
                JSONObject member = ServiceAccess.callService(msg).getContent();

                if(member == null || member.size()==0){
                    throw new UserOperateException(500,"获取会员信息失败");
                }
                if(!StringUtils.mapValueIsEmpty(member,"belongArea") && !StringUtils.mapValueIsEmpty(member,"belongAreaValue")){
                    String belongAreaValue = member.get("belongAreaValue").toString();
                    //若上级归属只有一段，则表示创建者归属于平台;若不是，则直接归属在会员归属下
                    if(belongAreaValue.substring(1,belongAreaValue.length()-1).split("_").length==1){
                        if(!StringUtils.mapValueIsEmpty(text,"areaValue")){
                            Map<String,Object> params = new HashMap<>();
                            params.put("realAreaValue",text.get("areaValue"));
                            Map<String,Object> factor = MysqlDaoImpl.getInstance().findOne2Map(
                                    "Factor",params,new String[]{"belongArea","areaValue"},Dao.FieldStrategy.Include);
                            if(factor!=null && factor.size()!=0){
                                text.put("belongArea",factor.get("belongArea"));
                                text.put("belongAreaValue",factor.get("areaValue"));
                            }
                        }
                    }else{
                        text.put("belongArea",member.get("belongArea"));
                        text.put("belongAreaValue",member.get("belongAreaValue"));
                    }
                }
            }else if("F".equals(createId.substring(0, 1))){
                Map<String,Object> factor = MysqlDaoImpl.getInstance().findById2Map(
                        "Factor", createId, new String[]{"belongArea","areaValue"}, Dao.FieldStrategy.Include);
                if(factor==null || factor.size()==0){
                    throw new UserOperateException(500,"获取服务站信息失败");
                }
                if(!StringUtils.mapValueIsEmpty(factor,"belongArea") && !StringUtils.mapValueIsEmpty(factor,"areaValue")){
                    text.put("belongArea",factor.get("belongArea"));
                    text.put("belongAreaValue",factor.get("areaValue"));
                }
            }
        }else if(userApplyType==2){
            if(!StringUtils.mapValueIsEmpty(text,"areaValue")){
                Map<String,Object> params = new HashMap<>();
                params.put("realAreaValue",text.get("areaValue"));
                Map<String,Object> factor = MysqlDaoImpl.getInstance().findOne2Map(
                        "Factor",params,new String[]{"belongArea","areaValue"},Dao.FieldStrategy.Include);
                if(factor!=null && factor.size()!=0){
                    text.put("belongArea",factor.get("belongArea"));
                    text.put("belongAreaValue",factor.get("areaValue"));
                }
            }
        }

      /*  if (text.get("name") == null || StringUtils.isEmpty(text.get("name").toString()) || text.get("name").toString().length() > 100) {
            throw new UserOperateException(400, "商家名称在100字以内!");
        }
//        if (Pattern.matches("^(\\S*((([轻亲]+)([送松菘颂淞讼凇忪]+)\\S*)|(([惠慧蕙会]+)([网罔王旺]+)\\S*)))\\S*$", text.get("name").toString())){
//            throw new UserOperateException(400, "商家名称不能含有'普惠生活'相关的敏感字!");
//        }
        if (!Pattern.matches("^(([123456789]|[123456789]\\d)(\\.\\d+)?)$", text.get("integralRate").toString())) {
            throw new UserOperateException(400, "商家积分率应在1~99以内!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("contactPerson"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的联系人名字!");
        }
        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) text.get("phone"))) {
            throw new UserOperateException(400, "手机号格式错误!");
        }
        if (text.get("serverPhone") == null || StringUtils.isEmpty(text.get("serverPhone").toString()) || text.get("serverPhone").toString().length() > 64) {
            throw new UserOperateException(400, "请输入正确的客服号码!");
        }
        if (text.get("openWeek") == null || StringUtils.isEmpty(text.get("openWeek").toString())) {
            throw new UserOperateException(400, "请选择营业星期!");
        }
        if (text.get("openTime") == null || StringUtils.isEmpty(text.get("openTime").toString()) || text.get("closeTime") == null || StringUtils.isEmpty(text.get("closeTime").toString())) {
            throw new UserOperateException(400, "请选择营业时间!");
        }
        if (text.get("operateType") == null || StringUtils.isEmpty(text.get("operateType").toString()) || text.get("operateValue") == null || StringUtils.isEmpty(text.get("operateValue").toString())) {
            throw new UserOperateException(400, "请选择经营范围!");
        }
        if (text.get("area") == null || StringUtils.isEmpty(text.get("area").toString()) || text.get("areaValue") == null || StringUtils.isEmpty(text.get("areaValue").toString()) || text.get("areaValue").toString().length() > 200) {
            throw new UserOperateException(400, "请选择完整的所在区域位置!");
        }
        if (text.get("address") == null || StringUtils.isEmpty(text.get("address").toString()) || text.get("address").toString().length() > 200) {
            throw new UserOperateException(400, "请选择完整的所在区域位置,且不能超过200位字符!");
        }
        if (StringUtils.mapValueIsEmpty(text,"longitude") || StringUtils.mapValueIsEmpty(text,"latitude")){
            throw new UserOperateException(400, "获取经纬度失败!");
        }

        if(!StringUtils.mapValueIsEmpty(text,"longitude") && !Pattern.matches("^\\d{1,3}\\.\\d+$",text.get("longitude").toString())){
            throw new UserOperateException(400, "获取经度失败!");
        }
        if(!StringUtils.mapValueIsEmpty(text,"latitude") && !Pattern.matches("^\\d{1,2}\\.\\d+$",text.get("latitude").toString())){
            throw new UserOperateException(400, "获取纬度失败!");
        }

        if ((text.get("intro") != null || !StringUtils.isEmpty(text.get("intro").toString())) && text.get("intro").toString().length() > 300) {//可不填
            throw new UserOperateException(400, "商家简介不能超过300位字符!");
        }
        if (StringUtils.mapValueIsEmpty(text,"bankId")) {
            throw new UserOperateException(400, "请输入银行账号!");
        }
        if (text.get("bankName") == null || StringUtils.isEmpty(text.get("bankName").toString())) {
            throw new UserOperateException(400, "请输入开户行!");
        }
        if (StringUtils.mapValueIsEmpty(text,"bankAddress")) {
            throw new UserOperateException(400, "请输入开户行地址!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("bankUser"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的户名!");
        }
//        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) text.get("bankUserPhone"))) {
//            throw new UserOperateException(400, "请输入正确的持卡人电话!");
//        }
//        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("bankUserCardId"))) {
//            throw new UserOperateException(400, "请输入正确的持卡人身份证号码!");
//        }
        if (text.get("legalPerson") == null || StringUtils.isEmpty(text.get("legalPerson").toString())) {
            throw new UserOperateException(400, "请填写法人名称!");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("realCard"))) {
            throw new UserOperateException(400, "请输入正确的法人身份证号码!");
        }
        if (text.get("businessLicense") == null || StringUtils.isEmpty(text.get("businessLicense").toString())) {
            throw new UserOperateException(400, "请上传营业执照!");
        }
        if (text.get("idCardImgFront") == null || StringUtils.isEmpty(text.get("idCardImgFront").toString())) {
            throw new UserOperateException(400, "请上传身份证正面照片!");
        }
        if (text.get("idCardImgBack") == null || StringUtils.isEmpty(text.get("idCardImgBack").toString())) {
            throw new UserOperateException(400, "请上传身份证背面照片!");
        }
        if (!text.get("bankUser").toString().equals(text.get("legalPerson").toString())
                && (text.get("idCardImgHand") == null || StringUtils.isEmpty(text.get("idCardImgHand").toString()))) {
            throw new UserOperateException(400, "请上传手持身份证照片!");
        }*/

        checkImgMore("银行卡/开户许可证",text.get("bankImg").toString(),"uploadNum");
        checkImgMore("店铺门头照",text.get("doorImg").toString(),"uploadNum");

//        if(isAdmin){
//            checkImgMore("合同照片",text.get("contractImg").toString(),"uploadNum");
//        }else{
//            checkImgMore("合同照片",text.get("contractImg").toString(),"notUpload");
//        }

//        text.put("isCouponVerification", false);
//        text.put("isMoneyTransaction", false);
//        text.put("isOnlinePay", false);
//        text.put("isRecommend", false);
        text.put("applyTime", System.currentTimeMillis());
        //IOS可能是string类型
//        text.put("isOnlinePay", Boolean.valueOf(text.get("isOnlinePay").toString()));

        if (userApplyType==0) {
            if (agent != null && agent.get("_id") != null) {
                createId = (String) agent.get("_id");
                create = (String) agent.get("name");
            }
            s.put("create", create);
            s.put("createId", createId);
        }else if(userApplyType==1) {
            s.put("create", create);
            s.put("createId", createId);
        }

        //更改状态
        String pendingId = "";
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            pendingId = UUID.randomUUID().toString();
        } else {
            pendingId = text.get("pendingId").toString();
            text.remove("pendingId");//text只保存seller里存在的字段
        }
        s.put("_id", pendingId);
        s.put("status", 1);//0:草稿;1:待审;2:通过;3:不通过
        s.put("belongArea", text.get("belongArea"));
        s.put("belongAreaValue", text.get("belongAreaValue"));
        s.put("owner", text.get("name").toString());
        s.put("ownerId", text.get("_id").toString());
        s.put("ownerType", "Seller");//Seller,Factor,Agent
        s.put("text", text.toString());
        s.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", s);

        //若是平台/县级 审核,则直接通过审核
        if (agent != null && agent.get("level") != null &&
                ("1".equals(agent.get("level").toString()) || "4".equals(agent.get("level").toString()))) {
            Map<String,Object> isAgentApply=MysqlDaoImpl.getInstance().findById2Map("Agent",text.get("_id").toString(),new String[]{"_id"}, Dao.FieldStrategy.Include);
            if(isAgentApply==null || isAgentApply.size()==0 || isAgentApply.get("_id")==null){
                if(StringUtils.mapValueIsEmpty(text,"canUse")){
                    text.put("canUse", true);
                }
            }
            text.put("pendingId", pendingId);
            text.put("status", "2");
            verifyUser(text);
        }

    }

    /**
     * 申请代理商:保存代理商草稿
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/saveApplyAgent")
    public void saveApplyAgent() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();

        //获取平台管理ID
        String create = "";
        String createId;
        String _id = "";
        String ownerId = "";
        boolean isFirst = false;
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            _id = UUID.randomUUID().toString();
            isFirst = true;
        } else {
            _id = text.get("pendingId").toString();
            text.remove("pendingId");
        }

        if (text.get("_id") == null || StringUtils.isEmpty(text.get("_id").toString())) {
            ownerId = "A-" + ZQUidUtils.generateAgentNo();
            text.put("_id", ownerId);
        } else {
            ownerId = text.get("_id").toString();
        }

        //获取代理商归属;若是平台管理添加,则自动选择当前登录的代理商 (暂无做会员端申请的判断)
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if (agent != null) {
            if (!Pattern.matches("^[123]$", (String) agent.get("level"))) {
                throw new UserOperateException(400, "您没有添加代理商的权限!");
            }
            text.put("belongArea", agent.get("name"));//获取发卡点的名字
            text.put("areaValue", agent.get("areaValue") + (String) text.get("_id") + "_");
            create = (String) agent.get("name");
            createId = (String) agent.get("_id");
        } else {
            //获取会员ID
            createId = ControllerContext.getContext().getCurrentUserId();
            if (StringUtils.isEmpty(createId)) {
                createId = "other";//通过官网申请
            }
        }

        Map<String, Object> s = new HashMap<>();
        s.put("_id", _id);
        s.put("status", 0);//0:草稿;1:待审;2:通过;3:不通过
        s.put("owner", text.get("name"));
        s.put("ownerId", ownerId);
        s.put("ownerType", "Agent");//Seller,Factor,Agent
        s.put("create", create);
        s.put("createId", createId);
        s.put("text", text.toString());
        s.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", s);
        if (isFirst) {
            Map<String, Object> re = new HashMap<>();
            re.put("pendingId", _id);
            re.put("ownerId", ownerId);
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /**
     * 申请代理商:提交代理商申请
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/submitAgent")
    public void submitAgent() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();

        //获取平台管理ID
        String create = "";
        String createId = "";
        String agentName = "";

        if ((text == null || text.get("_id") == null || StringUtils.isEmpty(text.get("_id").toString())) ||
                (text.get("creator")!=null && StringUtils.isEmpty(text.get("creator").toString()) && !"other".equals(text.get("creator")))) {
            throw new UserOperateException(400, "网络异常,请刷新页面重试!");
        }

        //获取当前申请是否重复
        int isMemberApply = 0;//0:平台;1:会员;发卡点;2:官网
        if(!"other".equals(text.get("creatorType"))) {
            List<Object> params = new ArrayList<>();
            List<String> returnFields = new ArrayList<>();
            returnFields.add("createId");
            params.add(text.get("_id").toString());
            String sql = "select createId from UserPending where ownerId=? and status=1";
            List<Map<String, Object>> pend = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            if (pend != null && pend.size() != 0) {
                throw new UserOperateException(400, "已经存在当前用户的审核申请!");
            }
            //获取创建人/创建人ID
            returnFields.add("create");

            String where = " where ownerId=? and status in (0.2,0.3,3)";
            if(!StringUtils.mapValueIsEmpty(text,"pendingId")){
                where+=" and _id = ?";
                params.add(text.get("pendingId").toString());
            }

            //获取创建者ID,用来判断是否是会员;因为会员进入申请页面的时候已经生成了创建者,不必再这里再次生成;会员提交的申请也不需要归属
            sql = "select `create`,createId from UserPending"+where;
            pend = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            if (pend != null && pend.size() != 0) {
                isMemberApply = "M".equals(pend.get(0).get("createId").toString().substring(0, 1))?1:0;
                if(isMemberApply==1){
                    createId=pend.get(0).get("createId").toString();
                    if(pend.get(0).get("create")==null || StringUtils.isEmpty(pend.get(0).get("create").toString())){//若没有实名认证
                        create=createId;
                    }else{
                        create=pend.get(0).get("create").toString();
                    }
                }
            }
        }else{
            isMemberApply=2;
        }

        if (text.get("name") == null || StringUtils.isEmpty(text.get("name").toString()) || text.get("name").toString().length() > 100) {
            throw new UserOperateException(400, "代理商名称在100字以内!");
        }
//        if (Pattern.matches("^(\\S*((([轻亲]+)([送松菘颂淞讼凇忪]+)\\S*)|(([惠慧蕙会]+)([网罔王旺]+)\\S*)))\\S*$", text.get("name").toString())){
//            throw new UserOperateException(400, "代理商名称不能含有'普惠生活'相关的敏感字!");
//        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("contactPerson"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的联系人名字!");
        }
        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) text.get("phone"))) {
            throw new UserOperateException(400, "手机号格式错误!");
        }
        if (text.get("area") == null || StringUtils.isEmpty(text.get("area").toString())) {
            throw new UserOperateException(400, "请选择完整的所在区域位置!");
        }
        if (text.get("address") == null || StringUtils.isEmpty(text.get("address").toString()) || text.get("address").toString().length() > 200) {
            throw new UserOperateException(400, "请选择完整的所在区域位置,且不能超过200位字符!");
        }
//        if (StringUtils.mapValueIsEmpty(text,"bankTypeValue") || StringUtils.mapValueIsEmpty(text,"bankType")) {
//            throw new UserOperateException(400, "请选择银行总行!");
//        }
//        if (text.get("bankName") == null || StringUtils.isEmpty(text.get("bankName").toString())) {
//            throw new UserOperateException(400, "请输入开户总行!");
//        }
//        if (StringUtils.mapValueIsEmpty(text,"bankProvinceValue") || StringUtils.mapValueIsEmpty(text,"bankProvinceValue")
//                || StringUtils.mapValueIsEmpty(text,"bankCity") || StringUtils.mapValueIsEmpty(text,"bankCityValue")) {
//            throw new UserOperateException(400, "请选择开户支行城市!");
//        }
        if (StringUtils.mapValueIsEmpty(text,"bankId")) {
            throw new UserOperateException(400, "请输入银行账号!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("bankUser"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的户名!");
        }
//        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) text.get("bankUserPhone"))) {
//            throw new UserOperateException(400, "请输入正确的持卡人电话!");
//        }
//        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("bankUserCardId"))) {
//            throw new UserOperateException(400, "请输入正确的持卡人身份证号码!");
//        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("legalPerson"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的法人名字!");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("realCard"))) {
            throw new UserOperateException(400, "请输入正确的法人身份证号码!");
        }
        if (text.get("businessLicense") == null || StringUtils.isEmpty(text.get("businessLicense").toString())) {
            throw new UserOperateException(400, "请上传营业执照!");
        }
        if (text.get("idCardImgFront") == null || StringUtils.isEmpty(text.get("idCardImgFront").toString())) {
            throw new UserOperateException(400, "请上传身份证正面照片!");
        }
        if (text.get("idCardImgBack") == null || StringUtils.isEmpty(text.get("idCardImgBack").toString())) {
            throw new UserOperateException(400, "请上传身份证背面照片!");
        }
        if (text.get("idCardImgHand") == null || StringUtils.isEmpty(text.get("idCardImgHand").toString())) {
            throw new UserOperateException(400, "请上传手持身份证照片!");
        }

        text.put("applyTime", System.currentTimeMillis());
        String pendingId = "";
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            pendingId = UUID.randomUUID().toString();
        } else {
            pendingId = text.get("pendingId").toString();
            text.remove("pendingId");//text只保存seller里存在的字段
        }

        boolean isAdmin = false;
        //更改状态
        Map<String, Object> s = new HashMap<>();
        Map<String, Object> agent = new HashMap<>();
        if (isMemberApply==0) {
            //获取代理商归属;若是平台管理添加,则自动选择当前登录的代理商 (暂无做会员端申请的判断)
            agent = new AgentAction().getCurrentAgent();
            if (agent != null) {
                if(Pattern.matches("^[1234]$", String.valueOf(agent.get("level")))){
                    isAdmin = true;
                }
                if (text.get("belongArea") == null || StringUtils.isEmpty(text.get("belongArea").toString()) ||
                        text.get("areaValue") == null || StringUtils.isEmpty(text.get("areaValue").toString())) {
                    if (!Pattern.matches("^[123]$", (String) agent.get("level"))) {
                        throw new UserOperateException(400, "您没有审核代理商的权限!");
                    }
                    agentName = (String) agent.get("name");
                    text.put("belongArea", agentName);
                    text.put("areaValue", agent.get("areaValue").toString() + text.get("_id") + "_");
                    text.put("level", Integer.parseInt(agent.get("level").toString())+1);
                    text.put("pid", agent.get("_id"));
                }else{
                    String belongValue = agent.get("areaValue").toString();
                    if(text.get("areaValue")!=null && StringUtils.isNotEmpty(text.get("areaValue").toString())){
                        belongValue=text.get("areaValue").toString();
                    }
                    String[] belongValueArr = belongValue.substring(1, belongValue.length() - 1).split("_");
                    if(text.get("level")==null || StringUtils.isEmpty(text.get("level").toString())){
                        text.put("level", belongValueArr.length);
                        text.put("pid", belongValueArr[belongValueArr.length - 1]);
                    }
                }
                create = (String) agent.get("name");
                createId = (String) agent.get("_id");
            }
            s.put("create", create);
            s.put("createId", createId);

            s.put("verifierFirst",agent.get("name"));
            s.put("verifierFirstId",agent.get("_id"));
            s.put("verifierFirstTime",System.currentTimeMillis());
        }else if(isMemberApply==1) {
            s.put("create", create);
            s.put("createId", createId);
        }else if(isMemberApply==2) {
//            createId = "other";//通过官网申请
//            create = "other";//通过官网申请
//            text.put("_id", "S-" + ZQUidUtils.generateSellerNo());
//            s.put("create", create);
//            s.put("createId", createId);
        }

        checkImgMore("银行卡/开户许可证",text.get("bankImg").toString(),"uploadNum");

        if(isAdmin){
            checkImgMore("合同照片",text.get("contractImg").toString(),"uploadNum");
        }else{
            checkImgMore("合同照片",text.get("contractImg").toString(),"notUpload");
        }


        s.put("_id", pendingId);
        s.put("status", 1);//0:草稿;1:待审;2:通过;3:不通过
        s.put("belongArea", agentName);
        s.put("belongAreaValue", text.get("areaValue"));
        s.put("owner", text.get("name"));
        s.put("ownerId", text.get("_id"));
        s.put("ownerType", "Agent");//Seller,Factor,Agent

        s.put("text", text.toString());
        s.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", s);

        //若是平台管理员审核代理商,则直接通过审核
        if (agent != null && agent.get("level") != null && "1".equals(agent.get("level").toString())) {
            Map<String,Object> isAgentApply=MysqlDaoImpl.getInstance().findById2Map("Agent",text.get("_id").toString(),new String[]{"_id"}, Dao.FieldStrategy.Include);
            if(isAgentApply==null || isAgentApply.size()==0 || isAgentApply.get("_id")==null){
                text.put("canUse", true);
            }
            text.put("pendingId", pendingId);
            text.put("status", "2");

            verifyUser(text);
        }
    }

    /**
     * 申请发卡点:保存草稿
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Member
    @Path("/saveApplyFactor")
    public void saveApplyFactor() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();

        //获取平台管理ID
        String create = "";
        String createId;
        //获取代理商归属;若是平台管理添加,则自动选择当前登录的代理商 (暂无做会员端申请的判断)
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if (agent != null) {
            if (!Pattern.matches("^[4]$", (String) agent.get("level"))) {
                throw new UserOperateException(400, "您没有添加发卡点的权限!");
            }
            text.put("belongArea", agent.get("name"));//获取发卡点的名字
            text.put("areaValue", agent.get("areaValue") + (String) text.get("_id") + "_");
            createId = (String) agent.get("_id");
            create = (String) agent.get("name");
        } else {
            //获取会员ID
            createId = ControllerContext.getContext().getCurrentUserId();
            if (StringUtils.isEmpty(createId)) {
                createId = "other";//通过官网申请
            }
        }

        String _id = "";
        String ownerId = "";
        boolean isFirst = false;
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            _id = UUID.randomUUID().toString();
            isFirst = true;
        } else {
            _id = text.get("pendingId").toString();
            text.remove("pendingId");
        }

        if (text.get("_id") == null || StringUtils.isEmpty(text.get("_id").toString())) {
            ownerId = "F-" + ZQUidUtils.generateFactorNo();
            text.put("_id", ownerId);
        } else {
            ownerId = text.get("_id").toString();
        }

        Map<String, Object> s = new HashMap<>();
        s.put("_id", _id);
        s.put("status", 0);//0:草稿;1:待审;2:通过;3:不通过
        s.put("owner", text.get("name"));
        s.put("ownerId", ownerId);
        s.put("ownerType", "Factor");//Seller,Factor,Agent
        s.put("create", create);
        s.put("createId", createId);
        s.put("text", text.toString());
        s.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", s);
        if (isFirst) {
            Map<String, Object> re = new HashMap<>();
            re.put("pendingId", _id);
            re.put("ownerId", ownerId);
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /**
     * 申请发卡点:提交发卡点申请
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Member
    @Path("/submitFactor")
    public void submitFactor() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();

        //获取平台管理ID
        String create = "";
        String createId = "";
        String agentName = "";

        //获取当前申请是否重复
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        returnFields.add("createId");
        params.add(text.get("_id").toString());
        String sql = "select createId from UserPending where ownerId=? and status=1";
        List<Map<String, Object>> pend = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (pend != null && pend.size() != 0) {
            throw new UserOperateException(400, "已经存在当前用户的审核申请!");
        }
        returnFields.add("create");
        //获取创建者ID,用来判断是否是会员;因为会员进入申请页面的时候已经生成了创建者,不必再这里再次生成;会员提交的申请也不需要归属
        sql = "select `create`,createId from UserPending where ownerId=? and (status=0.2 or status=3)";
        pend = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        boolean isMSApply = false;//判断是否是会员或商家提交的申请
        if (pend != null && pend.size() != 0 && Pattern.matches("^[MS]$", pend.get(0).get("createId").toString().substring(0, 1))) {
            isMSApply = true;
            createId=pend.get(0).get("createId").toString();
            if(pend.get(0).get("create")==null || StringUtils.isEmpty(pend.get(0).get("create").toString())){//若没有实名认证
                create=createId;
            }else{
                create=pend.get(0).get("create").toString();
            }
        }

        if (text.get("name") == null || StringUtils.isEmpty(text.get("name").toString()) || text.get("name").toString().length() > 100) {
            throw new UserOperateException(400, "发卡点名称在100字以内!");
        }
//        if (Pattern.matches("^(\\S*((([轻亲]+)([送松菘颂淞讼凇忪]+)\\S*)|(([惠慧蕙会]+)([网罔王旺]+)\\S*)))\\S*$", text.get("name").toString())){
//            throw new UserOperateException(400, "发卡点名称不能含有'普惠生活'相关的敏感字!");
//        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("contactPerson"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的联系人名字!");
        }
        if (!Pattern.matches("^[1][345789][0-9]{9}$", (String) text.get("mobile"))) {
            throw new UserOperateException(400, "手机号格式错误!");
        }
        if (text.get("area") == null || StringUtils.isEmpty(text.get("area").toString())) {
            throw new UserOperateException(400, "请选择完整的所在区域位置!");
        }
        if (text.get("address") == null || StringUtils.isEmpty(text.get("address").toString()) || text.get("address").toString().length() > 200) {
            throw new UserOperateException(400, "请选择完整的所在区域位置,且不能超过200位字符!");
        }
        if (StringUtils.mapValueIsEmpty(text,"bankId")) {
            throw new UserOperateException(400, "请输入银行账号!");
        }
        if (text.get("bankName") == null || StringUtils.isEmpty(text.get("bankName").toString())) {
            throw new UserOperateException(400, "请输入开户行!");
        }
        if (!Pattern.matches("[\u0391-\uFFE5]{2,64}", (String) text.get("bankUser"))) {
            throw new UserOperateException(400, "请填写2~64位中文汉字之间的持卡人名字!");
        }
//        if (!Pattern.matches("^[1][34578][0-9]{9}$", (String) text.get("bankUserPhone"))) {
//            throw new UserOperateException(400, "请输入正确的持卡人电话!");
//        }
//        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("bankUserCardId"))) {
//            throw new UserOperateException(400, "请输入正确的持卡人身份证号码!");
//        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("realCard"))) {
            throw new UserOperateException(400, "请输入正确的发卡点身份证号码!");
        }
        if (text.get("idCardImgFront") == null || StringUtils.isEmpty(text.get("idCardImgFront").toString())) {
            throw new UserOperateException(400, "请上传身份证正面照片!");
        }
        if (text.get("idCardImgBack") == null || StringUtils.isEmpty(text.get("idCardImgBack").toString())) {
            throw new UserOperateException(400, "请上传身份证背面照片!");
        }
        if (text.get("idCardImgHand") == null || StringUtils.isEmpty(text.get("idCardImgHand").toString())) {
            throw new UserOperateException(400, "请上传手持身份证照片!");
        }

        text.put("applyTime", System.currentTimeMillis());
        String pendingId = "";
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            pendingId = UUID.randomUUID().toString();
        } else {
            pendingId = text.get("pendingId").toString();
            text.remove("pendingId");//text只保存seller里存在的字段
        }

        boolean isAdmin = false;
        //更改状态
        Map<String, Object> s = new HashMap<>();
        //获取代理商归属;若是平台管理添加,则自动选择当前登录的代理商 (暂无做会员端申请的判断)
        Map<String, Object> agent = new HashMap<>();
        if("other".equals(text.get("creatorType"))){
            // 非会员申请
            Map<String,Object> parentParams = new HashMap<>();
            parentParams.put("realAreaValue",text.get("realAreaValue"));
            parentParams.put("level",4);
            Map<String,Object> parent = MysqlDaoImpl.getInstance().findOne2Map(
                    "Agent",parentParams,new String[]{"belongArea","areaValue"},Dao.FieldStrategy.Include);
            if(parent!=null && parent.size()!=0){
                agentName=parent.get("belongArea").toString();
                String belongAreaValue = parent.get("areaValue").toString().split("F")[0];
                text.put("belongArea",agentName);
                text.put("areaValue",belongAreaValue+text.get("_id")+"_");
            }
        }else if (!isMSApply) {
            agent = new AgentAction().getCurrentAgent();
            if("1".equals(agent.get("level")) || "4".equals(agent.get("level"))){
                isAdmin = true;
            }
            if(text.get("areaValue")==null || StringUtils.isEmpty(text.get("areaValue").toString())){
                if (agent != null) {
                    if (!Pattern.matches("^[14]$", (String) agent.get("level"))) {
                        throw new UserOperateException(400, "您没有审核发卡点的权限!");
                    }
                    agentName = (String) agent.get("name");
                    text.put("belongArea", agentName);
                    text.put("areaValue", agent.get("areaValue") + (String) text.get("_id") + "_");
                    create = (String) agent.get("name");
                    createId = (String) agent.get("_id");
                }
//                else {
//                    createId = "other";//通过官网申请
//                }
            }else{
                create = (String) agent.get("name");
                createId = (String) agent.get("_id");
            }
            s.put("create", create);
            s.put("createId", createId);

            s.put("verifierFirst",agent.get("name"));
            s.put("verifierFirstId",agent.get("_id"));
            s.put("verifierFirstTime",System.currentTimeMillis());
        }else{
            // 自动选择创建者(会员，商家)的归属：如果创建者归属于某个发卡点，则申请的发卡点归属取创建者归属前面四段，拼接申请的发卡点ID，并提交给此归属所在的服务中心审核；
            // 若创建者归属于平台，则提交给平台审核（归属为空则平台审核）

            String belongAreaValue = "";

            if("M".equals(createId.substring(0, 1))){
                Message msg = Message.newReqMessage("1:GET@/crm/Member/show");
                msg.getContent().put("_id", createId);
                JSONObject member = ServiceAccess.callService(msg).getContent();

                if(member == null || member.size()==0){
                    throw new UserOperateException(500,"获取会员信息失败");
                }
                if(!StringUtils.mapValueIsEmpty(member,"belongAreaValue")){
                    belongAreaValue = member.get("belongAreaValue").toString();
                    //若上级归属只有一段，则表示创建者归属于平台;若不是，则直接归属在会员归属下
                    if(belongAreaValue.substring(1,belongAreaValue.length()-1).split("_").length==1){
                        if(!StringUtils.mapValueIsEmpty(text,"realAreaValue")){
                            Map<String,Object> parentParams = new HashMap<>();
                            parentParams.put("realAreaValue",text.get("realAreaValue"));
                            parentParams.put("level",4);
                            Map<String,Object> parent = MysqlDaoImpl.getInstance().findOne2Map(
                                    "Agent",parentParams,new String[]{"belongArea","areaValue"},Dao.FieldStrategy.Include);
                            if(parent!=null && parent.size()!=0){
                                belongAreaValue = parent.get("areaValue").toString();
                            }else{
                                belongAreaValue="";
                            }
                        }else{
                            belongAreaValue="";
                        }
                    }else{
                        belongAreaValue = member.get("belongAreaValue").toString();
                    }
                }
            }else if("S".equals(createId.substring(0, 1))){
                Map<String,Object> seller = MysqlDaoImpl.getInstance().findById2Map(
                        "Seller",createId,new String[]{"belongArea","belongAreaValue"},Dao.FieldStrategy.Include);
                if(seller==null || seller.size()==0){
                    throw new UserOperateException(500,"获取商家信息失败");
                }
                if(!StringUtils.mapValueIsEmpty(seller,"belongAreaValue")){
                    belongAreaValue = seller.get("belongAreaValue").toString();
                }
            }

            if(StringUtils.isNotEmpty(belongAreaValue)){
                String parentBelong = belongAreaValue.split("F")[0];

                //根据拼接的上级归属，查出上级名称
                Map<String,Object> parentParams = new HashMap<>();
                parentParams.put("areaValue",parentBelong);
                Map<String,Object> parent = MysqlDaoImpl.getInstance().findOne2Map(
                        "Agent",parentParams,new String[]{"_id","name"},Dao.FieldStrategy.Include);
                if(parent==null || parent.size()==0){
                    throw new UserOperateException(500,"自动匹配上级归属失败");
                }
                agentName=parent.get("name").toString();

                belongAreaValue=parentBelong+text.get("_id")+"_";
                text.put("areaValue",belongAreaValue);
                text.put("belongArea",agentName);
                text.put("level",5);
                text.put("pid",parent.get("_id"));
            }

            s.put("create", create);
            s.put("createId", createId);
        }

        checkImgMore("银行卡/开户许可证",text.get("bankImg").toString(),"uploadNum");

        if(isAdmin){
            checkImgMore("合同照片",text.get("contractImg").toString(),"uploadNum");
        }else{
            checkImgMore("合同照片",text.get("contractImg").toString(),"notUpload");
        }

        s.put("_id", pendingId);
        s.put("status", 1);//0:草稿;1:待审;2:通过;3:不通过
        s.put("belongArea", agentName);
        s.put("belongAreaValue", text.get("areaValue"));
        s.put("owner", text.get("name"));
        s.put("ownerId", text.get("_id"));
        s.put("ownerType", "Factor");//Seller,Factor,Agent
        s.put("text", text.toString());
        s.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", s);

        //若是平台审核,则直接通过审核
        if (agent != null && agent.get("level") != null &&
                ("1".equals(agent.get("level").toString()))) {
            Map<String,Object> isAgentApply=MysqlDaoImpl.getInstance().findById2Map("Agent",text.get("_id").toString(),new String[]{"_id"}, Dao.FieldStrategy.Include);
            if(isAgentApply==null || isAgentApply.size()==0 || isAgentApply.get("_id")==null){
                text.put("canUse", true);
            }
            text.put("pendingId", pendingId);
            text.put("status", "2");
            verifyUser(text);
        }
    }

    /**
     * 审批:是否有新的申请
     *
     * @throws Exception
     */
    @GET
    @Path("/getNewPending")
    public void getNewPending() throws Exception {
        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if (!"1".equals(agent.get("level"))) {
            return;
        }
        Map<String,Object> re = new HashMap<>();
        List<String> r = new ArrayList<>();
        String hql = "select" +
                " sum(case when ownerType='Seller' and status=1 then 1 else 0 end) as sellerCountSecond" +
                ",sum(case when ownerType='Factor' and status=1 then 1 else 0 end) as factorCountSecond" +
                ",sum(case when ownerType='Agent' and status=1 then 1 else 0 end) as agentCountSecond" +
                " from UserPending";
        r.add("sellerCountSecond");
        r.add("factorCountSecond");
        r.add("agentCountSecond");
        List<Map<String, Object>> pending = MysqlDaoImpl.getInstance().queryBySql(hql, r, null);
        re.put("userPend",pending.get(0));

//        r.clear();
//        r.add("withdrawCount");
//        hql = "select count(_id) as withdrawCount from WithdrawLog where status=0";
//        pending = MysqlDaoImpl.getInstance().queryBySql(hql, r, null);
//        re.put("withdrawPend",pending.get(0));

        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 审批历史
     *
     * @throws Exception
     */
    @GET
    @Path("/getPendRecord")
    public void getPendRecord() throws Exception {
        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        //获取参数
        String status = ControllerContext.getPString("_status");
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String owner = ControllerContext.getPString("_owner");
        String create = ControllerContext.getPString("_create");
        String verifier = ControllerContext.getPString("_verifier");
        String ownerType = ControllerContext.getPString("_ownerType");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }

        long pageNo = ControllerContext.getPLong("pageNo") == null ? 0l : ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1";

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            if(!"\\_A-000001\\_".equals(areaValue)){
                where += " and belongAreaValue like ?";
                p.add(areaValue + "%");
            }
        } else {
            where += " and belongAreaValue like ?";
            p.add(agent.get("areaValue") + "%");
        }
        if (startTime != 0) {
            where += " and verifierTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and verifierTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(owner)) {
            where += " and owner like ?";
            p.add("%" + owner + "%");
        }
        if (StringUtils.isNotEmpty(create)) {
            where += " and `create` like ?";
            p.add("%" + create + "%");
        }
        if (StringUtils.isNotEmpty(verifier)) {
            where += " and verifier like ?";
            p.add("%" + verifier + "%");
        }
        if (StringUtils.isNotEmpty(status) && Pattern.matches("^[23]|(0.2)|(0.3)$", status)) {
            where += " and status=?";
            p.add(status);
        } else {
            where += " and status in (2,3)";
//            where += " and status in (0.2,0.3,1,2,3)";
        }
        if (StringUtils.isNotEmpty(ownerType)) {
            where += " and ownerType=?";
            p.add(ownerType);
        } else if ("4".equals(agent.get("level"))) {
            where += " and (ownerType = 'Seller' or ownerType = 'Factor')";
        }
        String hql = "select count(_id) as totalCount" +
                " from UserPending" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();
        r.add("_id");
        r.add("belongArea");
        r.add("belongAreaValue");
        r.add("create");
        r.add("createId");
        r.add("createTime");
        r.add("explain");
        r.add("owner");
        r.add("ownerId");
        r.add("ownerType");
        r.add("status");
        r.add("verifier");
        r.add("verifierId");
        r.add("verifierTime");
        r.add("verifierFirst");
        r.add("verifierFirstId");
        r.add("verifierFirstTime");

        String sql = "select " +
                " _id" +
                ",belongArea" +
                ",belongAreaValue" +
                ",`create`" +
                ",createId" +
                ",createTime" +
                ",`explain`" +
                ",owner" +
                ",ownerId" +
                ",ownerType" +
                ",status" +
                ",verifier" +
                ",verifierId" +
                ",verifierTime" +
                ",verifierFirst" +
                ",verifierFirstId" +
                ",verifierFirstTime" +
                " from UserPending" +
                where +
                " order by createTime desc" +
                " limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);

        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 草稿箱
     *
     * @throws Exception
     */
    @GET
    @Path("/getPendDrafts")
    public void getPendDrafts() throws Exception {
        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        //获取参数
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String ownerType = ControllerContext.getPString("_ownerType");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1 and status=0 and `createId`=?";
        p.add(agent.get("_id"));
        if ("4".equals(agent.get("level"))) {
            where += " and (ownerType = 'Seller' or ownerType = 'Factor')";
        } else {
            where += " and ownerType = 'Agent'";
        }

        if (startTime != 0) {
            where += " and createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(ownerType)) {
            where += " and ownerType =?";
            p.add(ownerType);
        }
        String hql = "select count(_id) as totalCount" +
                " from UserPending" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();
        r.add("_id");
        r.add("belongArea");
        r.add("belongAreaValue");
        r.add("create");
        r.add("createId");
        r.add("createTime");
        r.add("explain");
        r.add("owner");
        r.add("ownerId");
        r.add("ownerType");
        r.add("status");

        String sql = "select " +
                " _id" +
                ",belongArea" +
                ",belongAreaValue" +
                ",`create`" +
                ",createId" +
                ",createTime" +
                ",`explain`" +
                ",owner" +
                ",ownerId" +
                ",ownerType" +
                ",status" +
                " from UserPending" +
                where +
                " order by createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);

        toResult(Response.Status.OK.getStatusCode(), page);
    }


    /**
     * 待初审/复审
     *
     * @throws Exception
     */
    @GET
    @Path("/getPendingList")
    public void getPendingList() throws Exception {
        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        //获取参数
        String areaValue = ControllerContext.getPString("_areaValue");
        String createTime = ControllerContext.getPString("_createTime");
        String owner = ControllerContext.getPString("_owner");
        String create = ControllerContext.getPString("_create");
        String verifier = ControllerContext.getPString("_verifier");
        String ownerType = ControllerContext.getPString("_ownerType");
        String status = ControllerContext.getPString("_status");

        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }

        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        String where = " where 1=1";

        if(StringUtils.isEmpty(status)){//默认查复审
            where+=" and status=1";
        }else{
            where+=" and status=?";
            p.add(status);
        }

        if (StringUtils.isNotEmpty(areaValue)) {
            areaValue = areaValue.replaceAll("___like_", "");
            //若选择的是查询所有,则删掉管理员的id,因为会员申请,没有归属点
            if(areaValue.substring(2,areaValue.length()-2).split("\\_").length!=1){
                where += " and belongAreaValue like ?";
                p.add(areaValue + "%");
            }
        } else {
            if(!"1".equals(agent.get("level"))){
                where += " and belongAreaValue like ?";
                p.add(agent.get("areaValue") + "%");
            }
        }
//        if("1".equals(agent.get("level"))){
//            where+=" and ownerType<>'Seller'";
////            where += " and (belongAreaValue is null or belongAreaValue ='')";
//        }

        if (startTime != 0) {
            where += " and createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and createTime<?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(owner)) {
            where += " and owner like ?";
            p.add("%" + owner + "%");
        }
        if (StringUtils.isNotEmpty(create)) {
            where += " and `create` like ?";
            p.add("%" + create + "%");
        }
        if (StringUtils.isNotEmpty(verifier)) {
            where += " and verifier like ?";
            p.add("%" + verifier + "%");
        }
        if (StringUtils.isNotEmpty(ownerType)) {
            where += " and ownerType=?";
            p.add(ownerType);
        } else if ("4".equals(agent.get("level"))) {
            where += " and (ownerType = 'Seller')";
        }
        String hql = "select count(_id) as totalCount" +
                " from UserPending" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        r.clear();
        r.add("_id");
        r.add("belongArea");
        r.add("belongAreaValue");
        r.add("create");
        r.add("createId");
        r.add("createTime");
        r.add("explain");
        r.add("owner");
        r.add("ownerId");
        r.add("ownerType");
        r.add("status");
        r.add("verifier");
        r.add("verifierId");
        r.add("verifierTime");

        String sql = "select " +
                " _id" +
                ",belongArea" +
                ",belongAreaValue" +
                ",`create`" +
                ",createId" +
                ",createTime" +
                ",`explain`" +
                ",owner" +
                ",ownerId" +
                ",ownerType" +
                ",status" +
                ",verifier" +
                ",verifierId" +
                ",verifierTime" +
                " from UserPending" +
                where +
                " order by createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);

        toResult(Response.Status.OK.getStatusCode(), page);
    }

    /**
     * 审批用户:审核/修改/添加用户
     *
     * @throws Exception
     */
    public void verifyUser(JSONObject text) throws Exception {
        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if ((!"1".equals(agent.get("level"))) && !"4".equals(agent.get("level"))) {
            throw new UserOperateException(400, "您没有审批权限!");
        }

        if (text.get("_id") == null || StringUtils.isEmpty(text.get("_id").toString())) {
            throw new UserOperateException(400, "提交失败,请刷新页面重试!");
        }

        if (text.get("pendingId") == null || StringUtils.isEmpty((String) text.get("pendingId"))) {
            throw new UserOperateException(400, "提交失败,请刷新页面重试!");
        }
        //从服务器 读取审核信息,避免客户端误操作修改数据,只修改状态/审核结果
        Map<String, Object> verifyInfo = MysqlDaoImpl.getInstance().findById2Map("UserPending", (String) text.get("pendingId"), null, null);
        if (text.get("status") == null || StringUtils.isEmpty(text.get("status").toString()) || !Pattern.matches("^[23]$", text.get("status").toString())) {
            throw new UserOperateException(400, "请选择是否审核通过!");
        } else if ("3".equals(text.get("status")) && (text.get("explain") == null || StringUtils.isEmpty((String) text.get("explain"))
                || ((String) text.get("explain")).length()>200)) {
            throw new UserOperateException(400, "请填写审核不通过的理由,且在200字以内!");
        }
        verifyInfo.put("status", text.get("status"));//0:草稿;1:待审;2:通过;3:不通过
        if (text.get("explain") != null || StringUtils.isNotEmpty((String) text.get("explain"))) {
            verifyInfo.put("explain", text.get("explain"));
        }else{
            verifyInfo.put("explain", "");//如果之前申请过一次没通过,留下了申请不通过的原因.再次申请通过得时候,把内容清空
        }
        //更新审核表,方便审核历史查看
//        JSONObject verifyText = JSONObject.fromObject(verifyInfo.get("text"));
//        if("Seller".equals(verifyInfo.get("ownerType"))) {//商家
//            verifyText.put("isCouponVerification", text.get("isCouponVerification"));
//            verifyText.put("isMoneyTransaction", text.get("isMoneyTransaction"));
//            verifyText.put("isOnlinePay", text.get("isOnlinePay"));
//            verifyText.put("isRecommend", text.get("isRecommend"));
//        }

        if ("2".equals(text.get("status")) && text.get("canUse") == null) {//添加用户
            throw new UserOperateException(400, "请为新注册账号选择可用状态!");
        }
        verifyInfo.put("verifier", agent.get("name"));
        verifyInfo.put("verifierId", agent.get("_id"));
        verifyInfo.put("verifierTime", System.currentTimeMillis());

        //如果没有归属地,说明是会员/商家申请的,管理员审核的时候添加的归属地只保存在了text的json字段里,需要再保存到belongArea字段里
        if (!"3".equals(text.get("status").toString()) && (verifyInfo.get("belongAreaValue") == null || StringUtils.isEmpty(verifyInfo.get("belongAreaValue").toString()))) {
            String userAreaValue = "";
            if ("Agent".equals(verifyInfo.get("ownerType")) && (text.get("areaValue") == null
                    || StringUtils.isEmpty(text.get("areaValue").toString()))) {
                text.put("areaValue", "_A-000001_");
            } else if ("Seller".equals(verifyInfo.get("ownerType")) && StringUtils.mapValueIsEmpty(text,"belongAreaValue")) {
                throw new UserOperateException(400, "请为用户选择归属!");
            } else if ("Factor".equals(verifyInfo.get("ownerType")) && StringUtils.mapValueIsEmpty(text,"areaValue")){
                throw new UserOperateException(400, "请为用户选择归属!");
            }
            if("Agent".equals(verifyInfo.get("ownerType")) || "Factor".equals(verifyInfo.get("ownerType"))){
                userAreaValue = text.get("areaValue").toString();
            } else {
                userAreaValue = text.get("belongAreaValue").toString();
            }
            String[] belongAgentId = userAreaValue.substring(1, userAreaValue.length() - 1).split("_");

            if ("Seller".equals(verifyInfo.get("ownerType")) && belongAgentId.length != 5) {
                throw new UserOperateException(400, "请选择归属发卡点!");
            } else if ("Factor".equals(verifyInfo.get("ownerType")) && belongAgentId.length != 4) {
                throw new UserOperateException(400, "请选择县级归属代理商!");
            } else if ("Agent".equals(verifyInfo.get("ownerType")) && belongAgentId.length > 3) {
                throw new UserOperateException(400, "请选择市级以上的归属代理商!");
            }
            String[] userAreaArr = new AgentAction().getAgentAreaValueById(userAreaValue).get("agentNameAll").toString().split("-");
            String userArea = userAreaArr[userAreaArr.length - 1];
            JSONObject verifyText = JSONObject.fromObject(verifyInfo.get("text"));
            if ("Agent".equals(verifyInfo.get("ownerType")) || "Factor".equals(verifyInfo.get("ownerType"))) {
                userAreaValue += verifyText.get("_id") + "_";
                verifyText.put("level", userAreaValue.substring(1,userAreaValue.length()-1).split("_").length);
                verifyText.put("pid", belongAgentId[belongAgentId.length - 1]);
            }

            verifyText.put("belongArea", userArea);
            verifyInfo.put("belongArea", userArea);
            verifyInfo.put("belongAreaValue", userAreaValue);
            if ("Seller".equals(verifyInfo.get("ownerType"))) {
                verifyText.put("belongAreaValue", userAreaValue);
            } else {
                verifyText.put("areaValue", userAreaValue);
            }
            verifyInfo.put("text", verifyText);
        }

        if(!StringUtils.mapValueIsEmpty(text,"canUse") && "2".equals(String.valueOf(text.get("status"))) && agent.get("_id").equals(text.get("_id"))){
            throw new UserOperateException(400, "您不能禁用自己!");
        }


        //生成 登录用户的账号信息
        if (!StringUtils.mapValueIsEmpty(text,"canUse") && "2".equals(String.valueOf(text.get("status")))) {
            //检查管理员是否上传了合同照片
            if(!"Seller".equals(verifyInfo.get("ownerType"))){
                checkImgMore("合同照片",text.get("contractImg").toString(),"uploadNum");
            }

            String ownerType = verifyInfo.get("ownerType").toString();
            //获取 用户信息
            JSONObject userInfoJson = JSONObject.fromObject(verifyInfo.get("text"));
            Map<String, Object> userInfo = new HashMap<>();
            Iterator it = userInfoJson.keys();
            // 遍历userInfo数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = userInfoJson.get(key);
                userInfo.put(key, value);
            }
            if(text.get("createTime")==null || StringUtils.isEmpty(text.get("createTime").toString())){
                userInfo.put("createTime", System.currentTimeMillis());
            }else{
                userInfo.put("modifyTime", System.currentTimeMillis());
            }

            userInfo.put("canUse", text.get("canUse"));
            userInfo.put("creator", verifyInfo.get("createId"));
            userInfo.put("bankImg", text.get("bankImg"));
            userInfo.put("contractImg", text.get("contractImg"));

            //生成User表
            Map<String, Object> user = new HashMap<>();
            String mobile;
//            Map<String, Object> userInfoByData = MysqlDaoImpl.getInstance().findById2Map(ownerType, (String) text.get("_id"), null, null);
//            if(userInfoByData!=null && userInfoByData.size()>0 && userInfoByData.get("canUse")!=null && (Boolean)userInfoByData.get("canUse")){//已经存在该用户,不能更改此用户的信息
//                throw new UserOperateException(400, "已经存在该用户!");
//            }

            //如果是不存在,需要生成user,汇总表
            Map<String, Object> params = new HashMap<>();
            params.put(ownerType + "Id", userInfo.get("_id"));
            Map<String, Object> reUser = MysqlDaoImpl.getInstance().findOne2Map("User", params, null, null);
            String loginName = "";
            Message msg;
            String[] tempId = text.get("_id").toString().split("-");
            if (reUser == null || reUser.size() == 0) {
                String ownerTypeCN = "";
                if ("Seller".equals(ownerType)) {//商家
                    ownerTypeCN = "商家";
                    userInfo.put("isCouponVerification", text.get("isCouponVerification"));
                    userInfo.put("isMoneyTransaction", text.get("isMoneyTransaction"));
                    userInfo.put("isOnlinePay", text.get("isOnlinePay"));
                    userInfo.put("isRecommend", text.get("isRecommend"));

                    user.put("sellerId", userInfo.get("_id"));
                    user.put("isSellerAdmin", true);
                    mobile = (String) userInfo.get("phone");
                    msg = Message.newReqMessage("1:GET@/order/OrderInfo/initSellerAccount");//生成商家的现金汇总表
                } else if ("Factor".equals(ownerType)) {//发卡点
                    ownerTypeCN = "发卡点";
                    String areaValue = userInfo.get("areaValue").toString();
                    String[] areaArr = areaValue.substring(1, areaValue.length()).split("_");
                    userInfo.put("pid", areaArr[areaArr.length - 2]);//获取areaValue倒数第二个作为Pid
                    userInfo.put("surplusCardNum", 0);
                    userInfo.put("level", 5);

                    user.put("factorId", userInfo.get("_id"));
                    user.put("isFactorAdmin", true);
                    user.put("isSendCardLog", true);
                    mobile = (String) userInfo.get("mobile");
                    msg = Message.newReqMessage("1:GET@/order/OrderInfo/initFactorAccount");//生成发卡点的现金汇总表
                } else if ("Agent".equals(ownerType)) {//代理商
                    ownerTypeCN = "代理商";
                    String areaValue = userInfo.get("areaValue").toString();
                    String[] areaArr = areaValue.substring(1, areaValue.length()).split("_");
                    userInfo.put("pid", areaArr[areaArr.length - 2]);//获取areaValue倒数第二个作为Pid
                    userInfo.put("surplusCardNum", 0);

                    user.put("agentId", userInfo.get("_id"));
                    mobile = (String) userInfo.get("phone");
                    msg = Message.newReqMessage("1:GET@/order/OrderInfo/initAgentAccount");//生成代理商的现金汇总表
                } else {
                    throw new UserOperateException(400, "提交失败,请刷新页面重试!");
                }

                String createType = verifyInfo.get("createId").toString().substring(0,1);
                if(createType.equals("M")){
                    user.put("memberId",verifyInfo.get("createId"));
                }

                msg.getContent().put("userId", userInfo.get("_id"));
                ServiceAccess.callService(msg);

                //发送短信
                msg = Message.newReqMessage("1:PUT@/common/Sms/sendAuditIsOk");
                String userId = "";
                //如果是商家版的申请,则需要关联账号
                if (Pattern.matches("^[MSF]$", createType)) {
                    Map<String, Object> userParams = new HashMap<>();
                    String createId=verifyInfo.get("createId").toString();
                    //如果申请的是发卡点,则查由该会员创建的商家所属的User表账号,关联商家和发卡点;商家同理;代理商另外生成新的User
                    if("M".equals(createType) && !"Agent".equals(verifyInfo.get("ownerType"))){
                        String createTypeByM="";
                        if("Seller".equals(verifyInfo.get("ownerType"))){
                            createTypeByM="Factor";
                        }else{
                            createTypeByM="Seller";
                        }
                        String sql = "select t2._id from "+ createTypeByM +" t1" +
                                " left join User t2 on t1._id = t2."+createTypeByM+"Id" +
                                " where t1.creator=?";
                        List<Object> paramsM = new ArrayList<>();
                        paramsM.add(verifyInfo.get("createId"));
                        List<String> returnFieldsM = new ArrayList<>();
                        returnFieldsM.add("_id");
                        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFieldsM,paramsM);
                        if(re!=null && re.size()!=0){
                            createId = re.get(0).get("_id").toString();
                            userParams.put("_id",createId);
                        }
                    }
                    if ("F".equals(createType)) {
                        userParams.put("factorId", createId);
                    } else if("S".equals(createType)) {
                        userParams.put("sellerId", createId);
                    }
                    Map<String, Object> userMap = new HashMap<>();
                    if(userParams.size()!=0){
                        userMap = MysqlDaoImpl.getInstance().findOne2Map("User", userParams, null, null);
                    }
                    if (userMap == null || userMap.size() == 0) {
                        userId = UUID.randomUUID().toString();
                        loginName = ZQUidUtils.generateUserNo();
                        user.put("loginName", loginName);//生成user表的loginName
                        user.put("password", MessageDigestUtils.digest(mobile.substring(mobile.length() - 6, mobile.length())));
                        msg.getContent().put("sendInfo", ",密码默认为手机号码后六位");
                    } else {
                        userId = userMap.get("_id").toString();
                        loginName = userMap.get("loginName").toString();
                        msg.getContent().put("sendInfo", ",密码与您的"+("Seller".equals(verifyInfo.get("ownerType"))?"发卡点":"商家")+"账号关联");//不需要发送密码是多少,商家与发卡点的密码是共享的
                    }
                } else {
                    userId = UUID.randomUUID().toString();
                    loginName = ZQUidUtils.generateUserNo();
                    user.put("password", MessageDigestUtils.digest(mobile.substring(mobile.length() - 6, mobile.length())));
                    user.put("loginName", loginName);//生成user表的loginName
                    msg.getContent().put("sendInfo", ",密码默认为手机号码后六位");
                }
                user.put("_id", userId);
                user.put("canUse", true);
                user.put("name", userInfo.get("name"));
                user.put("createTime", System.currentTimeMillis());
                user.put("creator", verifyInfo.get("createId"));
                MysqlDaoImpl.getInstance().saveOrUpdate("User", user);

                //可用才发短信
                if((boolean)text.get("canUse")){
                    msg.getContent().put("mobile", mobile);
                    msg.getContent().put("type", ownerTypeCN);
                    msg.getContent().put("loginName", tempId[0]+tempId[1]);
//                    msg.getContent().put("loginName", loginName + "@" + text.get("_id"));
                    ServiceAccess.callService(msg);
                }
            } else {
                loginName = tempId[0]+tempId[1];
            }

            //生成对应的表(商家/发卡点/代理商)
            MysqlDaoImpl.getInstance().saveOrUpdate(ownerType, userInfo);
            //更新下级归属的名称
            if("Agent".equals(ownerType) || "Factor".equals(ownerType)){
                Map<String,Object> userParams = new HashMap<>();
                userParams.put("_id",text.get("_id"));
                userParams.put("name",text.get("name"));
                userParams.put("userType",ownerType);
                userParams.put("level",text.get("level"));

                if("Factor".equals(ownerType)){
                    userParams.put("belongAreaValue",text.get("areaValue"));
                }
                synchroBelongArea(userParams);
            }

            //会员申请商家，更新该会员的团队关系
            if("M".equals(verifyInfo.get("createId").toString().substring(0,1)) && "Seller".equals(verifyInfo.get("ownerType"))){
                Message teamMsg = Message.newReqMessage("1:POST@/order/Team/checkActive");
                teamMsg.getContent().put("shareSeller",verifyInfo.get("ownerId"));
                teamMsg.getContent().put("regMember",verifyInfo.get("createId"));
                ServiceAccess.callService(teamMsg);
                //更新自己推荐会员的团队关系Path
                Message changeMsg = Message.newReqMessage("1:POST@/order/Team/changeUnderPath");
                changeMsg.getContent().put("regMember",verifyInfo.get("createId"));
                ServiceAccess.callService(changeMsg);
            } else if("M".equals(verifyInfo.get("createId").toString().substring(0,1)) && "Factor".equals(verifyInfo.get("ownerType"))){
                Message teamMsg = Message.newReqMessage("1:POST@/order/Team/checkActive");
                teamMsg.getContent().put("factorId",verifyInfo.get("ownerId"));
                teamMsg.getContent().put("regMember",verifyInfo.get("createId"));
                ServiceAccess.callService(teamMsg);
                //更新自己推荐会员的团队关系Path
                Message changeMsg = Message.newReqMessage("1:POST@/order/Team/changeUnderPath");
                changeMsg.getContent().put("regMember",verifyInfo.get("createId"));
                ServiceAccess.callService(changeMsg);
            }
            //更新审核表,方便审核历史查看
            text.put("loginName", tempId[0]+tempId[1]);//保存登录名:返回给客户端或平台管理
            verifyInfo.put("text", text);
        }
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", verifyInfo);
    }

    /**
     * 审批平台添加的用户:审核/修改/添加用户
     *
     * @throws Exception
     */
    @POST
    @Path("/verifyUser")
    public void verifyUser() throws Exception {
        JSONObject text = ControllerContext.getContext().getReq().getContent();
        verifyUser(text);
    }

    /**
     * 创建申请的用户的Id
     * @return
     * @throws Exception
     */
    public String createApplyUserId(String ownerType) throws Exception{
        if ("Seller".equals(ownerType)) {
            return "S-" + ZQUidUtils.generateSellerNo();
        } else if ("Agent".equals(ownerType)) {
            return "A-" + ZQUidUtils.generateAgentNo();
        } else if ("Factor".equals(ownerType)) {
            return "F-" + ZQUidUtils.generateFactorNo();
        } else {
            throw new UserOperateException(400, "网络异常,请刷新页面重试!");
        }
    }

    /**
     * 会员版:非会员访问，创建一个临时的草稿记录
     *
     * @throws Exception
     */
    @GET
    @Path("/getOtherIsApply")
    public void getOtherIsApply() throws Exception {
        String createId = ControllerContext.getPString("createId");
        String ownerType = ControllerContext.getPString("ownerType");
        String ownerId = createApplyUserId(ownerType);

        Map<String, Object> params = new HashMap<>();
        params.put("createId", createId);
        params.put("ownerType", ownerType);
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("UserPending", params, null, null);

        if (re == null || re.size() == 0 || re.get("_id") == null) {
            re = new HashMap<>();
            re.put("_id", UUID.randomUUID().toString());
            re.put("status", 0.2);
            re.put("ownerId", ownerId);
            re.put("ownerType", ownerType);//Seller,Factor,Agent
            re.put("create", "非会员");
            re.put("createId", createId);
            re.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", re);
        }

        toResult(200,re);
    }

    /**
     * 会员版:查询会员是否提交角色申请;若提交了,则返回用户提交信息;若没有,则创建一个草稿记录
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMemberIsApply")
    public void getMemberIsApply() throws Exception {
        String ownerType = ControllerContext.getPString("ownerType");
        Message msg = Message.newReqMessage("1:GET@/crm/Member/getMyInfo");
        msg = ServiceAccess.callService(msg);
        Map<String, Object> params = new HashMap<>();
        params.put("createId", msg.getContent().get("_id"));
        params.put("ownerType", ownerType);
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("UserPending", params, null, null);
        if (re == null || re.size() == 0 || re.get("_id") == null) {
            String ownerId = createApplyUserId(ownerType);

            re = new HashMap<>();
            re.put("_id", UUID.randomUUID().toString());
            re.put("status", 0.2);//0:草稿;1:待审;2:通过;3:不通过
            re.put("ownerId", ownerId);
            re.put("ownerType", ownerType);//Seller,Factor,Agent
            if(StringUtils.mapValueIsEmpty(msg.getContent(),"realName")){//如果没有实名认证,则取ID
                re.put("create", msg.getContent().get("_id"));
            }else{
                re.put("create", msg.getContent().get("realName"));
            }
            re.put("createId", msg.getContent().get("_id"));
            re.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", re);
 //       }else if("0".equals(re.get("status").toString())){//如果是草稿,则把之前提交的图片清空
//            delImgMore(ownerType,"contractImg",re.get("ownerId").toString());
//            delImgMore(ownerType,"bankImg",re.get("ownerId").toString());
        }else if(!"0.2".equals(re.get("status").toString()) && !"3".equals(re.get("status").toString())) {
            JSONObject text = JSONObject.fromObject(re.get("text"));
            re.remove("text");
            String mark = null;
            String merchantsType = null;
            String companyCertificateType = null;
            String accountType = null;
            String contactType = null;
            //线上线下标志 0-线下   1-线上 2-线上线下
            if ("0".equals(text.get("mark").toString())) {
                mark = "线下";
            } else if ("1".equals(text.get("mark").toString())) {
                mark = "线上";
            } else if ("2".equals(text.get("mark").toString())) {
                mark = "线上线下";
            }
            text.remove("mark");
            text.put("mark", mark);
            //商户类型,0-小微商户  3-普通商户
            if ("0".equals(text.getString("merchantsType"))) {
                merchantsType = "小微商户";
            } else if ("3".equals(text.getString("merchantsType"))) {
                merchantsType = "普通商户";
            }
            text.remove("merchantsType");
            text.put("merchantsType", merchantsType);
            //商户证件类型,00-三证合一 01-营业执照
            if ("00".equals(text.getString("companyCertificateType"))) {
                companyCertificateType = "三证合一";
            }
            if ("01".equals(text.getString("companyCertificateType"))) {
                companyCertificateType = "营业执照";
            }
            text.remove("companyCertificateType");
            text.put("companyCertificateType", companyCertificateType);
            //开户人类型,1-法人 2-代理人
            if ("1".equals(text.getString("contactType"))) {
                contactType = "法人";
            }
            if ("2".equals(text.getString("contactType"))) {
                contactType = "代理人";
            }
            text.remove("contactType");
            text.put("contactType", contactType);
            //账户类型,00-对公 01-对私
            if ("00".equals(text.getString("accountType"))) {
                accountType = "对公";
            } else if ("01".equals(text.getString("accountType"))) {
                accountType = "对私";
            }
            text.remove("accountType");
            text.put("accountType", accountType);
            if("999999999".equals(text.getString("lpcertval"))){
                String lpcertval ="长期";
                text.remove("lpcertval");
                text.put("lpcertval",lpcertval);
            }
            if(!text.getString("paytype").isEmpty()){
                String str[] = text.getString("paytype").split(",");
                List<String> list = Arrays.asList(str);
                String paytype = "";
                for (String s:list) {
                    if("00".equals(s) && paytype==""){
                        paytype +="银联";
                    }else if("00".equals(s) && paytype!=""){
                        paytype +=",银联";
                    }
                    if("01".equals(s) && paytype==""){
                        paytype +="支付宝";
                    }else if("01".equals(s) && paytype!=""){
                        paytype +=",支付宝";
                    }
                    if("02".equals(s) && paytype==""){
                        paytype +="微信";
                    }else if("02".equals(s) && paytype!=""){
                        paytype +=",微信";
                    }
                }
                text.remove("paytype");
                text.put("paytype",paytype);
            }
            re.put("text", text);
        }
        toResult(Response.Status.OK.getStatusCode(), re);

    }

    /**
     * 商家版:查询商家/发卡点是否提交角色申请;若提交了,则返回用户提交信息;若没有,则创建一个草稿记录
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getSellerIsApply")
    public void getSellerIsApply() throws Exception {
        String ownerType = ControllerContext.getPString("ownerType");
        if (StringUtils.isEmpty(ownerType)) {
            throw new UserOperateException(400, "网络异常,请刷新页面重试!");
        }
        Map<String, Object> user;
        String ownerId = "";
        if ("Seller".equals(ownerType)) {
            user = new FactorAction().getCurrentFactor();
            ownerId = "S-" + ZQUidUtils.generateSellerNo();
        } else if ("Factor".equals(ownerType)) {
            user = new SellerAction().getCurrentSeller();
            ownerId = "F-" + ZQUidUtils.generateFactorNo();
        } else {
            throw new UserOperateException(400, "网络异常,请刷新页面重试!");
        }

        //通过User表查找createId
        String createId="";
        //1.先查询当前登录用户的申请创建者ID
        Map<String, Object> params = new HashMap<>();
        params.put("ownerId", user.get("_id"));
        params.put("ownerType", "Seller".equals(ownerType)?"Factor":"Seller");
        params.put("status", "2");
        Map<String, Object> curRe = MysqlDaoImpl.getInstance().findOne2Map("UserPending", params, null, null);
        //如果不是会员/代理商创建的,则默认是商家/发卡点
        createId=user.get("_id").toString();
        if(curRe!=null && curRe.size()>0 && curRe.get("createId")!=null && StringUtils.isNotEmpty(curRe.get("createId").toString())){
            if(createId.substring(0,1).equals("M")){
                JSONObject member = ServiceAccess.getRemoveEntity("crm", "Member", curRe.get("createId").toString());
                if(member!=null && member.size()!=0){
                    createId=curRe.get("createId").toString();
                }
            }else if(createId.substring(0,1).equals("A")){
                //如果是当前登录用户是代理商创建的,则当前登录用户创建的 用户 的创建者 为当前用户
                Map<String,Object> agent = MysqlDaoImpl.getInstance().findById2Map("Agent",curRe.get("createId").toString(),null,null);
                if(agent!=null && agent.size()!=0){
                    createId=user.get("_id").toString();
                }
            }
        }

        //3.查询提交的申请是否已经存在
        params = new HashMap<>();
        params.put("createId", createId);
        params.put("ownerType", ownerType);
//        params.put("status", "1");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findOne2Map("UserPending", params, null, null);

        if (re == null || re.size() == 0 || re.get("_id") == null) {
            params.put("status", "0.2");//修改为直接创建复审状态申请0.2；0为初审
            re = MysqlDaoImpl.getInstance().findOne2Map("UserPending", params, null, null);
            params.put("status", "3");
            Map<String,Object> re2 = MysqlDaoImpl.getInstance().findOne2Map("UserPending", params, null, null);

            boolean isCreate1=(re == null || re.size() == 0 || re.get("_id") == null);
            boolean isCreate2=(re2 == null || re2.size() == 0 || re2.get("_id") == null);

            if (isCreate1 && isCreate2) {
                re = new HashMap<>();
                re.put("_id", UUID.randomUUID().toString());
                re.put("status", 0.2);//0:草稿;1:待审;2:通过;3:不通过
                re.put("ownerId", ownerId);
                re.put("ownerType", ownerType);//Seller,Factor,Agent
                re.put("create", user.get("name"));
                re.put("createId", createId);
                re.put("createTime", System.currentTimeMillis());
                MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", re);
                toResult(Response.Status.OK.getStatusCode(), re);
            }else{
                if(isCreate1){
                    toResult(Response.Status.OK.getStatusCode(), re2);
                }else{
                    toResult(Response.Status.OK.getStatusCode(), re);
                }
            }
        }else{
            toResult(Response.Status.OK.getStatusCode(), re);
        }
    }

    /**
     * 返回某个用户的状态
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getUserCanUse")
    public void getUserCanUse() throws Exception {
        String userType = ControllerContext.getPString("userType");
        String userId = ControllerContext.getPString("userId");

        if (StringUtils.isEmpty(userType) || StringUtils.isEmpty(userId)) {
            throw new UserOperateException(400, "获取用户数据失败");
        }

        String sql = "select canUse from " + userType + " where _id=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("canUse");
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        if (re == null || re.size() == 0 || re.get(0).get("canUse") == null) {
            Map<String, Object> reMap = new HashMap<>();
            reMap.put("canUse", false);
            toResult(Response.Status.OK.getStatusCode(), reMap);
        } else {
            toResult(Response.Status.OK.getStatusCode(), re.get(0));
        }
    }

    /**
     * 平台管理:获取指定用户的登陆名
     *
     * @throws Exception
     */
    @GET
    @Path("/getUserLoginName")
    public void getUserLoginName() throws Exception {
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        String userId=ControllerContext.getPString("userId");
        String userType=ControllerContext.getPString("userType");

        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(userType)){
            throw new UserOperateException(400, "获取用户登录账号失败");
        }

        Map<String,Object> params = new HashMap<>();
        params.put(userType+"Id",userId);
        Map<String,Object> user = MysqlDaoImpl.getInstance().findOne2Map("User",params,new String[]{"loginName"},Dao.FieldStrategy.Include);
        if(user==null || user.size()==0){
            throw new UserOperateException(400, "获取用户账号失败");
        }

        String[] userList = userId.split("-");
        Map<String,Object> re = new HashMap<>();
        re.put("loginName",userList[0]+userList[1]);
        re.put("userName",user.get("loginName").toString());
        toResult(200,re);


//        String sql = "select t2.loginName from "+ userType +" t1" +
//                " left join User t2 on t1._id = t2."+userType+"Id" +
//                " where t1._id=?";
//        List<Object> params = new ArrayList<>();
//        params.add(userId);
//        List<String> returnFields = new ArrayList<>();
//        returnFields.add("loginName");
//        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
//        if(re!=null && re.size()==1 && re.get(0)!=null){
//            re.get(0).put("loginName",re.get(0).get("loginName")+"@"+userId);
//            toResult(Response.Status.OK.getStatusCode(), re.get(0));
//        }
    }

    @GET
    @Path("/createUserLoginName")
    public void createUserLoginName() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("no",ZQUidUtils.generateUserNo());
        toResult(Response.Status.OK.getStatusCode(), map);
    }

    @GET
    @Path("/delUserPending")
    public void delUserPending() throws Exception {
        String[] pendList = ControllerContext.getPString("idList").split("_");
        Map<String,Object> pend;
        List<Object> params = new ArrayList<>();
        String paramsStr="";
        for(int i=0,len=pendList.length;i<len;i++){
            if(StringUtils.isEmpty(pendList[i])){
                throw new UserOperateException(400,"获取审核资料失败");
            }
            pend = MysqlDaoImpl.getInstance().findById2Map("UserPending",pendList[i],new String[]{"status","createId"},Dao.FieldStrategy.Include);
            if(pend==null || pend.size()==0){
                throw new UserOperateException(400,"获取审核资料失败");
            }
            if(!"0".equals(pend.get("status"))){
                throw new UserOperateException(400,"您只能删除草稿!");
            }
            if(pend.get("createId")==null || !"A".equals(pend.get("createId").toString().split("-")[0])
                    || !pend.get("createId").equals(new AgentAction().getCurrentAgent().get("_id"))){
                throw new UserOperateException(400,"您不能删除其他用户创建的草稿!");
            }
            params.add(pendList[i]);
            paramsStr+=",?";
        }
//        if(StringUtils.isEmpty(pendId)){
//            throw new UserOperateException(400,"获取审核资料失败");
//        }
//        Map<String,Object> pend = MysqlDaoImpl.getInstance().findById2Map("UserPending",pendId,null,null);
//        if(pend==null || pend.size()==0){
//            throw new UserOperateException(400,"获取审核资料失败");
//        }
//        if("0".equals(pend.get("status"))){
//            throw new UserOperateException(400,"您只能删除草稿!");
//        }
//        if(pend.get("createId")==null || "A".equals(pend.get("createId").toString().split("-")[0])
//                || pend.get("createId").equals(new AgentAction().getCurrentAgent().get("name"))){
//            throw new UserOperateException(400,"您不能删除其他用户创建的草稿!");
//        }

        if(paramsStr.length()>1){
            paramsStr=paramsStr.substring(1,paramsStr.length());
        }
        MysqlDaoImpl.getInstance().exeSql("delete from UserPending where _id in("+paramsStr+") and status=0", params, "UserPending", false);
    }

    public void checkImgMore(String entityName,String entityId,String checkType) throws Exception {
        if ("notUpload".equals(checkType)) {
            if (!StringUtils.isEmpty(entityId) && entityId.split("_").length > 0) {
                throw new UserOperateException(400, "你没有上传" + entityName + "的权限!");
            }
        } else if ("uploadNum".equals(checkType)) {
            if (StringUtils.isEmpty(entityId) || entityId.split("_").length == 0) {
                throw new UserOperateException(400, "请上传至少一张" + entityName + "!");
            }
            if (entityId.split("_").length> 10) {
                throw new UserOperateException(400, "上传" + entityName + "不能超过十张!");
            }
        }
    }
//    public void checkImgMore(String table,String entityField,String entityId,String checkType) throws Exception{
//        Message msg = Message.newReqMessage("1:GET@/file/FileItem/queryEntityFiles");
//        msg.getContent().put("_entityName", table);
//        msg.getContent().put("_entityField", entityField);
//        msg.getContent().put("_entityId", entityId);
//        JSONObject imgJson = ServiceAccess.callService(msg).getContent();
//
//        Map<String, Object> imgList = new HashMap<>();
//        Iterator it = imgJson.keys();
//        while (it.hasNext()) {
//            String key = String.valueOf(it.next());
//            Object value = imgJson.get(key);
//            imgList.put(key, value);
//        }
//
//        String entityName="";
//        if("contractImg".equals(entityField)){
//            entityName="合同照片";
//        }else if("bankImg".equals(entityField)){
//            entityName="银行卡/开户许可证";
//        }else{
//            throw new UserOperateException(400,"获取图片信息失败");
//        }
//
//        if("notUpload".equals(checkType)){
//            if(imgList.get("items")==null || ((JSONArray)imgList.get("items")).size()>0){
//                throw new UserOperateException(400, "你没有上传"+entityName+"的权限!");
//            }
//        }else if("uploadNum".equals(checkType)){
//            if(imgList.get("items")==null || ((JSONArray)imgList.get("items")).size()==0){
//                throw new UserOperateException(400, "请上传至少一张"+entityName+"!");
//            }
//            if(((JSONArray)imgList.get("items")).size()>10){
//                throw new UserOperateException(400, "上传"+entityName+"不能超过十张!");
//            }
//        }
//    }

//    public void delImgMore(String table,String entityField,String entityId) throws Exception{
//        Message msg = Message.newReqMessage("1:GET@/file/FileItem/queryEntityFiles");
//        msg.getContent().put("_entityName", table);
//        msg.getContent().put("_entityField", entityField);
//        msg.getContent().put("_entityId", entityId);
//        JSONObject imgJson = ServiceAccess.callService(msg).getContent();
//
//        Map<String, Object> imgList = new HashMap<>();
//        Iterator it = imgJson.keys();
//        while (it.hasNext()) {
//            String key = String.valueOf(it.next());
//            Object value = imgJson.get(key);
//            imgList.put(key, value);
//        }
//        for(int i=0,len=((JSONArray)imgList.get("items")).size();i<len;i++){
//            msg = Message.newReqMessage("1:POST@/file/FileItem/deleteEntityFiles");
//            msg.getContent().put("_id", ((JSONObject)((JSONArray)imgList.get("items")).get(i)).get("_id"));
//            ServiceAccess.callService(msg).getContent();
//        }
//    }


    /**
     * 审核不通过的记录转换为草稿
     * @throws Exception
     */
    @GET
    @Path("/modifyPendRecord")
    public void modifyPendRecord() throws Exception {
        Map<String, Object> agent = new AgentAction().getCurrentAgent();

        String pendId = ControllerContext.getPString("_id");

        Map<String,Object> pend = MysqlDaoImpl.getInstance().findById2Map("UserPending",pendId,null,null);
        if(pend==null || pend.size()==0 || pend.get("status")==null){
            throw new UserOperateException(400,"获取数据失败");
        }
        if("0".equals(pend.get("status"))){
            throw new UserOperateException(400,"该审批记录已经为草稿");
        }
        if(!"3".equals(pend.get("status"))){
            throw new UserOperateException(400,"您只能将审核不通过的记录恢复为草稿");
        }

        //获取创建者信息
        Map<String,Object> createAgent = new HashMap<>();
        String createAreaValue="";
        String createType=pend.get("createId").toString().substring(0,1);
        if("S".equals(createType)){
            createAgent = new SellerAction().getSellerInfoById(pend.get("createId").toString());
        }else if("M".equals(createType)){
            Message msg = Message.newReqMessage("1:GET@/crm/Member/getMyInfoById");
            msg.getContent().put("memberId",pend.get("createId"));
            JSONObject userJson = ServiceAccess.callService(msg).getContent();

            Iterator it = userJson.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = createAgent.get(key);
                createAgent.put(key, value);
            }
        }else{
            createAgent = new AgentAction().getAgentById(pend.get("createId").toString());
        }

        //判断是否修改的自己归属下的记录
        if("S".equals(createType) || "M".equals(createType)){
            if(createAgent.get("belongAreaValue")==null || StringUtils.isEmpty(createAgent.get("belongAreaValue").toString())){
                createAreaValue="";
            }else{
                createAreaValue=createAgent.get("belongAreaValue").toString();
            }
        }else{
            createAreaValue=createAgent.get("areaValue").toString();
        }
        if (!"1".equals(agent.get("level")) && !Pattern.matches("^(" + agent.get("areaValue") + "\\S*)$", createAreaValue)) {
            throw new UserOperateException(400, "您只能更改归属于自己的用户");
        }

        //判断是否存在同一个待审批用户的多个草稿
        Map<String,Object> params = new HashMap<>();
        params.put("ownerId",pend.get("ownerId"));
        params.put("status",0);
        Map<String,Object> pendTable = MysqlDaoImpl.getInstance().findOne2Map("UserPending",params,null,null);
        if(pendTable!=null && pendTable.size()>0){
            throw new UserOperateException(400, "已经存在该用户的草稿!");
        }

        pend.put("status",0);
        pend.put("explain","");
        MysqlDaoImpl.getInstance().saveOrUpdate("UserPending", pend);
    }

    /**
     * 修改名称同步其他用户归属上级名称
     * 1.修改代理商，更新下级所有代理商上级名称；若是县级，则还要更新发卡点上级
     * 2.修改发卡点，更新下级所有会员、商家上级名称
     * @throws Exception
     */
    public void synchroBelongArea(Map<String,Object> user) throws Exception {
        if(StringUtils.mapValueIsEmpty(user,"name")){
            throw new UserOperateException(500, "获取同步的上级用户名称失败");
        }
        // 修改代理商名称，它的下级(agent,factor(县级才需要更新factor))只需要pid判断;
        // 修改发卡点名称，它的下级(seller,member)只需要belongAreaValue判断;
        if(StringUtils.mapValueIsEmpty(user,"userType")){
            throw new UserOperateException(500, "获取同步的上级用户类型失败");
        }else if("Agent".equals(user.get("userType")) && StringUtils.mapValueIsEmpty(user,"_id")){
            throw new UserOperateException(500, "获取同步的上级用户id失败");
        }else if("Factor".equals(user.get("userType")) && StringUtils.mapValueIsEmpty(user,"belongAreaValue")){
            throw new UserOperateException(500, "获取同步的上级用户归属失败");
        }

        List<Object> params = new ArrayList<>();
        params.add(user.get("name"));
        String sql = "";
        if("Agent".equals(user.get("userType"))){
            String table;
            if("4".equals(user.get("level"))){
                table = "Factor";
            }else{
                table = "Agent";
            }
            sql = "update "+table+" set belongArea=? where pid=?";
            params.add(user.get("_id"));
        }else if("Factor".equals(user.get("userType"))){
            sql = "update Seller set belongArea=? where belongAreaValue=?";
            String sql2 = "update Member set belongArea=? where belongAreaValue=?";
            params.add(user.get("belongAreaValue"));
            MysqlDaoImpl.getInstance().exeSql(sql2,params,"Member",false);
        }else{
            throw new UserOperateException(500, "获取同步的上级用户类型失败");
        }
        MysqlDaoImpl.getInstance().exeSql(sql,params,user.get("userType").toString(),false);
    }

    /**
     * 修改是否可用
     * @throws Exception
     */
    @POST
    @Path("/modifyCanUse")
    public void modifyCanUse() throws Exception {
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if(!agent.get("level").toString().equals("1")){
            throw new UserOperateException(500,"您无此权限");
        }
        Boolean canUse = ControllerContext.getPBoolean("canUse");
        String userId = ControllerContext.getPString("userId");
        String userType = ControllerContext.getPString("userType");

        if(!Pattern.matches("^(Factor)|(Agent)|(Seller)|(Member)$",userType)){
            throw new UserOperateException(500,"错误的用户类型");
        }
        if(StringUtils.isEmpty(userId)){
            throw new UserOperateException(500,"错误的用户ID");
        }
        if("Member".equals(userType)){

        }else{
            Map<String,Object> user = new HashMap<>();
            user.put("_id",userId);
            user.put("canUse",canUse);
            MysqlDaoImpl.getInstance().saveOrUpdate(userType,user);
        }
    }

    @GET
    @Path("/userPendingSaveOrUpdate")
    public void userPendingSaveOrUpdate() throws Exception{
        Map<String,Map<String,Object>> map = ControllerContext.getContext().getReq().getContent();
        MysqlDaoImpl.getInstance().saveOrUpdate("userPending",map.get("s"));
    }


//    public static void main (String[] args) throws Exception{
//        Map<String,Object> params = new HashMap<>();
//        params.put("_id","A-000006");
//        params.put("name","成都代理商2");
//        params.put("userType","Agent");
//        new UserPendingAction().synchroBelongArea(params);
//
//        if(Pattern.matches("^(\\S*((([轻亲]+)([送松菘颂淞讼凇忪]+)\\S*)|(([惠慧蕙会]+)([网罔王旺]+)\\S*)))\\S*$", "白松慧网啊啊")){
//            System.out.println("===========包含===========");
//        }else{
//            System.out.println("===========不包含===========");
//        }
//    }
}
