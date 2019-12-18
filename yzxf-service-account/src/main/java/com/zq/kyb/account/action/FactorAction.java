package com.zq.kyb.account.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by haozigg on 17/1/5.
 */
public class FactorAction extends BaseActionImpl {
    /**
     * 查询发卡点的信息
     *
     * @throws Exception
     */
    @GET
    @Path("/getFactorInfo")
    public void getFactorInfo() throws Exception {
        toResult(Response.Status.OK.getStatusCode(), getCurrentFactor());
    }

    /**
     *  获取当前登录的发卡点
     * @throws Exception
     */
    public Map<String, Object> getCurrentFactor() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "获取发卡点数据失败,请重新登录");
        }
        String factorId=(String)other.get("factorId");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map("Factor", factorId, null, null);
        return re;
    }
    /**
     * 获取发卡点卡段
     */
    @PUT
    @Seller
    @Path("/getFactorField")
    public void getFactorField() throws Exception {
        Map<String,Object> factor = getCurrentFactor();
        List<String> r = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        String sql = "select" +
                " startCardNo" +
                ",endCardNo" +
                ",_id" +
                " from CardField" +
                " where belongAreaValue=? order by startCardNo";
        r.add("startCardNo");
        r.add("endCardNo");
        r.add("_id");
        p.add(factor.get("areaValue"));
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 修改发卡点头像
     */
    @PUT
    @Seller
    @Path("/saveFactorIcon")
    public void saveFactorIcon() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");
        Map<String, Object> p = new HashMap<>();
        p.put("_id", factorId);
        p.put("icon", ControllerContext.getPString("icon"));
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, p);
    }

    /**
     * 查询申请得发卡点的信息:进页面就生成发卡点ID
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/getFactorApplyInfo")
    public void getFactorApplyInfo() throws Exception {
        String memberId = ControllerContext.getContext().getPString("memberId");
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("_id");
        returnFields.add("creator");
        returnFields.add("name");
        returnFields.add("contactPerson");
        returnFields.add("mobile");
        returnFields.add("address");
        returnFields.add("areaValue");
        returnFields.add("area");
        returnFields.add("realCard");
        returnFields.add("idCardImgFront");
        returnFields.add("idCardImgBack");
        returnFields.add("idCardImgHand");
        String sql = "select" +
                " _id" +
                ",creator" +
                ",name" +
                ",contactPerson" +
                ",mobile" +
                ",address" +
                ",areaValue" +
                ",area" +
                ",realCard" +
                ",idCardImgFront" +
                ",idCardImgBack" +
                ",idCardImgHand" +
                " from Factor" +
                " where creator=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        if (re == null || re.size() == 0 || StringUtils.isEmpty((String) re.get(0).get("creator"))) {
            String _id = "F-" + ZQUidUtils.generateFactorNo();
            Map<String, Object> s = new HashMap<>();
            s.put("_id", _id);
            s.put("canUse", false);
            s.put("creator", memberId);
            s.put("createTime", System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate("Factor", s);
            Map<String, Object> reMap = MysqlDaoImpl.getInstance().findById2Map("Factor", _id, null, null);
            toResult(Response.Status.OK.getStatusCode(), reMap);
            return;
        }

        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }

    /**
     * 提交发卡点申请
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/memberApplyFactor")
    public void memberApplyFactor() throws Exception {
        String memberId = ControllerContext.getContext().getPString("memberId");
        String factorName = ControllerContext.getContext().getPString("factorName");
        String contactPerson = ControllerContext.getContext().getPString("contactPerson");
        String phoneNumber = ControllerContext.getContext().getPString("phoneNumber");
        String address = ControllerContext.getContext().getPString("address");
        String area = ControllerContext.getPString("area");
        String areaValue = ControllerContext.getPString("areaValue");
        String realCard = ControllerContext.getPString("realCard");

        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("isApplyFactor");
        String sql = "select" +
                " isApplyFactor" +
                " from Member" +
                " where _id=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        if (StringUtils.mapValueIsEmpty(re.get(0), "isApplyFactor") || !(Boolean) re.get(0).get("isApplyFactor")) {

            params.clear();
            returnFields.clear();
            params.add(memberId);

            returnFields.add("_id");
            returnFields.add("idCardImgFront");
            returnFields.add("idCardImgBack");
            returnFields.add("idCardImgHand");
            sql = "select" +
                    " _id" +
                    ",idCardImgFront" +
                    ",idCardImgBack" +
                    ",idCardImgHand" +
                    " from Factor" +
                    " where creator=?";
            re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
            String factorId = (String) re.get(0).get("_id");
            if (re.get(0).get("idCardImgFront") == null) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传身份证正面照片!");
            }
            if (re.get(0).get("idCardImgBack") == null) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传身份证背面照片!");
            }
            if (re.get(0).get("idCardImgHand") == null) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请上传手持身份证照片!");
            }
            if (StringUtils.isEmpty(phoneNumber)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号不能为空!");
            }
            if (!Pattern.matches("^[1][34578][0-9]{9}$", phoneNumber)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
            }
            if (StringUtils.isEmpty(realCard)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证不能为空");
            }
            if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", realCard)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证格式错误");
            }
            if (StringUtils.isEmpty(address) || StringUtils.isEmpty(area)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写完整地址!");
            }
            if (address.length() > 64) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "街道地址不能超过64位");
            }
            if (factorName.length() > 64) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "发卡点名字不能超过64位");
            }
            if (!Pattern.matches("[\u0391-\uFFE5]{2,10}", contactPerson)) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "请填写2~10位的中文姓名");
            }
            if (area.length() > 200) {
                throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "所在区域不能超过200位");
            }

            //保存发卡点归属
            String[] areaValueArr=areaValue.substring(1,areaValue.length()-1).split("_");
            String agentArea="";
            String agentAreaValue="_";
            String agentPid="";
            if(areaValueArr.length==1 && "-1".equals(areaValueArr[0])){
                agentArea="平台";
                agentPid="-1";
            }else{
                for(int i=0;i<areaValueArr.length;i++){
                    agentAreaValue+=areaValueArr[i]+"_";
                    Message message = Message.newReqMessage("1:GET@/account/Agent/getAgentNameByValue");
                    message.getContent().put("areaValue", agentAreaValue);
                    JSONObject agentJson = ServiceAccess.callService(message).getContent();
                    if(agentJson==null || agentJson.get("name")==null){
                        break;
                    }
                    agentArea+="-"+agentJson.get("name");
                }
                if(agentArea.length()>1){
                    agentArea=agentArea.substring(1,agentArea.length());
                }

                String agentAreaValue2="";
                agentAreaValue2="_";
                for(int i=0;i<3;i++){
                    agentAreaValue2+=areaValueArr[i]+"_";
                }
                Map<String,Object> paramsAgent = new HashMap<>();
                paramsAgent.put("areaValue",agentAreaValue2);
                Map<String,Object> reAgent = MysqlDaoImpl.getInstance().findOne2Map("Agent",paramsAgent, new String[]{"_id"},Dao.FieldStrategy.Include);
                if(reAgent != null && reAgent.size()>0){
                    agentPid=(String)reAgent.get("_id");
                }else{
                    throw new UserOperateException(400, "该地区暂无代理商,请更改区域");
                }
            }

            params.clear();
            sql = "update Member set isApplyFactor=true where _id=?";
            params.add(memberId);
            MysqlDaoImpl.getInstance().exeSql(sql, params, "Member");
            Map<String, Object> f = new HashMap<>();
            f.put("_id", factorId);
            f.put("name", factorName);
            f.put("creator", memberId);
            f.put("contactPerson", contactPerson);
            f.put("mobile", phoneNumber);
            f.put("address", address);
            f.put("area", area);
            f.put("areaValue", areaValue);
            f.put("canUse", false);
            f.put("applyTime", System.currentTimeMillis());
            f.put("realCard", realCard);
            f.put("belongArea", agentArea);
            f.put("pid", agentPid);
            MysqlDaoImpl.getInstance().saveOrUpdate("Factor", f);
            toResult(Response.Status.OK.getStatusCode(), re);

            //获取发卡点现金汇总表
            Map<String, Object> factorMap = new HashMap<>();
            factorMap.put("factorId", factorId);
            Map<String, Object> sellerMoneyAccount = MysqlDaoImpl.getInstance().findOne2Map("FactorMoneyAccount", factorMap, null, null);

            //生成发卡点现金汇总表
            if (sellerMoneyAccount == null || sellerMoneyAccount.size() == 0) {
                Map<String, Object> account = new HashMap<>();
                account.put("_id", UUID.randomUUID().toString());
                account.put("createTime", System.currentTimeMillis());
                account.put("factorId", factorId);
                account.put("cashCount", 0.0);
                account.put("cashCountUse", 0.0);
                account.put("income", 0.0);
                MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyAccount", account);
            }
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "您的申请正在审核中,请不要重复申请");
        }
    }

    /**
     * 申请发卡点:保存发卡点
     *
     * @throws Exception
     */
    @GET
    @Member
    @Path("/saveFactorSeller")
    public void saveFactorSeller() throws Exception {
        JSONObject values = ControllerContext.getContext().getReq().getContent();

        String _id = ControllerContext.getPString("_id");

        if (StringUtils.isEmpty(_id)) {
            _id = "F-" + ZQUidUtils.generateFactorNo();
        }

        Map<String, Object> s = new HashMap<>();
        s.putAll(values);
        s.put("_id", _id);
        s.put("canUse", false);
        s.put("modifyTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("Factor", s);
    }

    /**
     * 发卡点交易记录查询
     *
     * @throws Exception
     */
    @GET
    @Member
    @Seller
    @Path("/queryFactorOrderList")
    public void queryFactorOrderList() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");

        Long startTime = ControllerContext.getContext().getPLong("startTime");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        String search = ControllerContext.getPString("search");

        List<Object> p = new ArrayList<>();

        List<String> r = new ArrayList<>();

        String whereStr = " where 1=1";
        whereStr += " and t2.factorId=?";
        p.add(factorId);
        if (startTime != 0) {
            whereStr += " and t3.createTime>=?";
            p.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t3.createTime<=?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(search)) {
            if ("1[34578]{1}[0-9]{9}".matches(search)) {
                whereStr += " and t4.cardNo=?";
            } else {
                whereStr += " and t4.mobile=?";
            }
            p.add(search);
        }

        r.add("factorName");
        r.add("orderIntegralRate");
        r.add("createTime");
        r.add("totalPrice");
        r.add("name");
        r.add("cardNo");
        r.add("factorMoney");
        String sql = "select" +
                " t1.name as factorName" +
                ",t3.score as orderIntegralRate" +
                ",t3.createTime" +
                ",t3.totalPrice" +
                ",t5.name" +
                ",t2.memberCardId as cardNo" +
                ",t3.factorMoney" +
                " from Factor t1 " +
                " left join MemberCard t2 on t1._id=t2.factorId" +
                " left join OrderInfo t3 on t2.memberId=t3.memberId" +
                " left join Member t4 on t2.memberId = t4._id" +
                " left join Seller t5 on t3.sellerId=t5._id";

        sql += whereStr + " order by t3.createTime desc limit 0,10";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 发卡点交易记录列表查询
     *
     * @throws Exception
     */
    @GET
    @Member
    @Seller
    @Path("/queryFactorMoreOrderList")
    public void queryFactorMoreOrderList() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");

        Map<String, Object> resultMap = new HashMap<>();
        Long startTime = ControllerContext.getContext().getPLong("startTime");
        String memberCard = ControllerContext.getContext().getPString("memberCard");
        String memberPhone = ControllerContext.getContext().getPString("memberPhone");
        String memberName = ControllerContext.getContext().getPString("memberName");
        String memberIdCard = ControllerContext.getContext().getPString("memberIdCard");
        Long endTime = ControllerContext.getContext().getPLong("endTime");
        Long indexNum = ControllerContext.getContext().getPLong("indexNum");
        Long pageNo = ControllerContext.getContext().getPLong("pageNo");
        Long pageSize = ControllerContext.getContext().getPLong("pageSize");
        String search = ControllerContext.getPString("search");

        List<Object> p = new ArrayList<>();

        List<String> r = new ArrayList<>();
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        r.add("totalCount");
        String whereStr = " where t3.orderStatus=100 and t3.orderType in (0,1,11)";
        whereStr += " and t2.factorId=?";
        p.add(factorId);
        if (startTime != 0) {
            whereStr += " and t3.endTime>=?";
            p.add(startTime);
        }
        if (endTime != 0) {
            whereStr += " and t3.endTime<=?";
            p.add(endTime);
        }
        if (StringUtils.isNotEmpty(memberCard)) {
            whereStr += " and t2.memberCardId like ?";
            p.add("%" + memberCard + "%");
        }
        if (StringUtils.isNotEmpty(memberPhone)) {
            whereStr += " and t4.mobile like ?";
            p.add("%" + memberPhone + "%");
        }
        if (StringUtils.isNotEmpty(memberName)) {
            whereStr += " and (t4.name like ? or t4.realName like ?)";
            p.add("%" + memberName + "%");
            p.add("%" + memberName + "%");
        }
        if (StringUtils.isNotEmpty(memberIdCard)) {
            whereStr += " and t4.idCard like ?";
            p.add("%" + memberIdCard + "%");
        }
        if (StringUtils.isNotEmpty(search)) {
            if ("1[34578]{1}[0-9]{9}".matches(search)) {
                whereStr += " and t4.cardNo=?";
            } else {
                whereStr += " and t4.mobile=?";
            }
            p.add(search);
        }
        String hql = "select count(t3._id) as totalCount" +
                " from Factor t1 " +
                " left join MemberCard t2 on t1._id=t2.factorId" +
                " left join OrderInfo t3 on t2.memberId=t3.memberId" +
                " left join Member t4 on t2.memberId = t4._id" +
                " left join Seller t5 on t3.sellerId=t5._id";
        hql += whereStr + " order by t3.endTime desc ";
        List<Map<String, Object>> orderList = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = (Long) orderList.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        r = new ArrayList<>();
        r.add("factorName");
        r.add("orderIntegralRate");
        r.add("endTime");
        r.add("totalPrice");
        r.add("payType");
        r.add("orderType");
        r.add("memberName");
        r.add("name");
        r.add("cardNo");
        r.add("orderCash");
        String sql = "select" +
                " t1.name as factorName" +
                ",t3.score as orderIntegralRate" +
                ",t3.endTime" +
                ",t3.totalPrice" +
                ",t3.payType" +
                ",t3.orderType" +
                ",t4.realName as memberName" +
                ",t5.name" +
                ",t2.memberCardId as cardNo" +
                ",t6.orderCash" +
                " from Factor t1 " +
                " left join MemberCard t2 on t1._id=t2.factorId" +
                " left join OrderInfo t3 on t2.memberId=t3.memberId" +
                " left join Member t4 on t2.memberId = t4._id" +
                " left join Seller t5 on t3.sellerId=t5._id" +
                " left join FactorMoneyLog t6 on t3.orderNo = t6.orderId";

        sql += whereStr + " order by t3.endTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        resultMap.put("orderList", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }

    /**
     * 发卡点或商户绑定银行卡
     *
     * @throws Exception
     */
    @GET
    @Member
    @Seller
    @Path("/bankBindApply")
    public void bankBindApply() throws Exception {
        String bankId = ControllerContext.getContext().getPString("bankId");
        String bankUser = ControllerContext.getContext().getPString("bankUser");
        String bankUserPhone = ControllerContext.getContext().getPString("bankUserPhone");
//        String bankName = ControllerContext.getContext().getPString("bankName");
        String bankUserCardId = ControllerContext.getContext().getPString("bankUserCardId");
        String userId = ControllerContext.getContext().getPString("userId");
        String userType = ControllerContext.getContext().getPString("userType");

        if (StringUtils.isEmpty(userType) || !Pattern.matches("^(Factor)|(Seller)$", userType)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "网络出错,请返回重新绑定");
        } else if ("Seller".equals(userType)) {
            userId = ControllerContext.getContext().getCurrentSellerId();
        }
        if (StringUtils.isEmpty(bankId)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "银行账号不能为空!");
        }
        if (StringUtils.isEmpty(bankUser)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "银行持卡人姓名不能为空!");
        }
//        if (StringUtils.isEmpty(bankName)) {
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "银行卡类型不能为空!");
//        }
        if (StringUtils.isEmpty(bankUserPhone)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号不能为空!");
        }
        if (!Pattern.matches("^[1][34578][0-9]{9}$", bankUserPhone)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
        }
        if (StringUtils.isEmpty(bankUserCardId)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证不能为空");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", bankUserCardId)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "身份证格式错误");
        }


        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String sql = "select" +
                " bankId" +
                ",_id" +
                " from " + userType +
                " where _id=?";
        p.add(userId);
        r.add("bankId");
        r.add("_id");
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        Map<String, Object> map = new HashMap<>();
        if (re != null && re.size() != 0 && StringUtils.isEmpty((String) re.get(0).get("bankId"))) {
            map.put("_id", userId);
            map.put("bankId", bankId);
            map.put("bankUser", bankUser);
            map.put("bankUserPhone", bankUserPhone);
//            map.put("bankName",bankName);
            map.put("bankUserCardId", bankUserCardId);
            MysqlDaoImpl.getInstance().saveOrUpdate(userType, map);
            map.clear();
            map.put("isOK", true);
            toResult(Response.Status.OK.getStatusCode(), map);
        } else {
            map.put("isOK", false);
            toResult(Response.Status.OK.getStatusCode(), map);
        }
    }

    /**
     * 获取发卡点银行账户
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getBankInfo")
    public void getBankInfo() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");

        List<Object> p = new ArrayList<>();
        p.add(factorId);

        List<String> r = new ArrayList<>();
        r.add("bankId");
        r.add("bankUser");
        r.add("bankUserPhone");
        r.add("bankUserCardId");
        r.add("bankName");

        String sql = "select" +
                " bankId" +
                ",bankUser" +
                ",bankUserPhone" +
                ",bankUserCardId" +
                ",bankName"+
                " from Factor" +
                " where _id=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        toResult(Response.Status.OK.getStatusCode(), re.get(0));
    }

    /**
     * 设置支付密码
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/setPayPassword")
    public void setPayPassword() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");

        String firstPwd = ControllerContext.getContext().getPString("firstPwd");
        String secondPwd = ControllerContext.getContext().getPString("secondPwd");
        if (StringUtils.isEmpty(firstPwd) || StringUtils.isEmpty(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码不能为空!");
        }
        if (!Pattern.matches("^[0-9]{6}$", firstPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "密码只能为6位数字!");
        }
        if (!firstPwd.equals(secondPwd)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "两次输入密码不一致!");
        }
        Map<String, Object> n = new HashMap<>();
        n.put("factorId", factorId);
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findOne2Map("User", n, new String[]{"password"}, Dao.FieldStrategy.Include);
        if (loginPwd.get("password").equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登录密码一样!");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("_id", factorId);
        m.put("cashPassword", MessageDigestUtils.digest(firstPwd));
        MysqlDaoImpl.getInstance().saveOrUpdate("Factor", m);
    }

    /**
     * 发卡点是否设置支付密码
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/isFactorSetPayPwd")
    public void isFactorSetPayPwd() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");

        List<String> returnFields = new ArrayList<String>();
        returnFields.add("cashPassword");
        List<Object> params = new ArrayList<>();

        params.add(factorId);
        String sql = "select " +
                " cashPassword" +
                " from Factor" +
                " where _id=?";

        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);
        Map<String, Object> flag = new HashMap<>();
        flag.put("flag", false);
        if (StringUtils.isEmpty((String) re.get(0).get("cashPassword"))) {
            flag.put("flag", true);
            toResult(Response.Status.OK.getStatusCode(), flag);
        }
        toResult(Response.Status.OK.getStatusCode(), flag);
    }

    /**
     * 发卡点修改支付密码
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/modifyFactorPayPwd")
    public void modifyFactorPayPwd() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId=(String)other.get("factorId");

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
        Map<String, Object> n = new HashMap<>();
        n.put("factorId", factorId);
        Map<String, Object> loginPwd = MysqlDaoImpl.getInstance().findOne2Map("User", n, new String[]{"password"}, Dao.FieldStrategy.Include);
        if (loginPwd.get("password").equals(MessageDigestUtils.digest(firstPwd))) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登录密码一样!");
        }
        List<String> returnFields = new ArrayList<String>();
        returnFields.add("cashPassword");
        Map<String, Object> re = MysqlDaoImpl.getInstance().findById2Map(entityName, factorId, null, null);
        if (MessageDigestUtils.digest(oldPwd).equals(re.get("cashPassword"))) {
            Map<String, Object> m = new HashMap<>();
            m.put("_id", factorId);
            m.put("cashPassword", MessageDigestUtils.digest(secondPwd));
            MysqlDaoImpl.getInstance().saveOrUpdate("Factor", m);
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "原密码输入错误!");
        }
    }

    /**
     * 查询发卡点余额
     */
    @GET
    @Seller
    @Path("/queryFactorCashCount")
    public void queryFactorCashCount() throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId = (String)other.get("factorId");
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(factorId);
        r.add("cashCount");

        String sql = "select" +
                " cashCount" +
                " from FactorMoneyAccount" +
                " where factorId=?";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        if (re.size() != 0) {
            toResult(Response.Status.OK.getStatusCode(), re.get(0));
        }
    }

    /**
     * 查询发卡点交易记录
     */
    @GET
    @Seller
    @Path("/queryTransaction")
    public void queryTransaction() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "factorId")) {
            throw new UserOperateException(400, "该用户不拥有发卡点权限");
        }
        String factorId = (String)other.get("factorId");
        Long indexNum = ControllerContext.getPLong("indexNum");
        Long pageNo = ControllerContext.getPLong("pageNo");
        Long pageSize = ControllerContext.getPLong("pageSize");
        String tradeType = ControllerContext.getPString("tradeType");
        Long startTime = ControllerContext.getPLong("startTime");
        Long endTime = ControllerContext.getPLong("endTime");

        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<String> s = new ArrayList<>();
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        p.add(factorId);

        String where = " where t2.factorId = ? and t1.orderStatus=100 and t2.type<>6";

        if (startTime != 0) {
            where += " and t1.endTime>=?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.endTime<=?";
            p.add(endTime);
        }
        if("undefined".equals(tradeType)){
            tradeType="";
        }
        if(StringUtils.isNotEmpty(tradeType)){
            if(Pattern.matches("^[78]|(13)$",tradeType)){
                where += " and t1.orderType in (7,8,13)";
            }else{
                where += " and t1.orderType=?";
                p.add(tradeType);
            }
        }else{
            where += " and t1.orderType in (0,1,5,6,7,8,11,13)";
        }

        String from = " from OrderInfo t1" +
                " left join FactorMoneyLog t2 on t2.orderId=t1.orderNo" +
                " left join Member t3 on t2.tradeId=t3._id" +
                " left join Factor t4 on t2.factorId=t4._id" +
                " left join WithdrawLog t5 on t1.orderNo = t5.orderNo";

//        s.add("orderId");
//        s.add("_id");
//        String sql = "select DISTINCT t1.orderId,t1._id" +
//                from +
//                where + " group by t1.orderId";
//        List<Map<String, Object>> notDistinct = MysqlDaoImpl.getInstance().queryBySql(sql, s, p);
//        if(notDistinct!=null && notDistinct.size()!=0){
//            String dempField = "";
//            for(Map<String,Object> item:notDistinct){
//                dempField += ",'"+item.get("_id").toString()+"'";
//            }
//            where += " and t1._id in ("+dempField.substring(1,dempField.length())+")";
//        }

        s.clear();
        s.add("totalCount");
        s.add("totalPrice");
        String hql = "select count(t1._id) as totalCount" +
                ",sum(case when t2.type=4 then t2.orderCash else 0 end) as totalPrice" +
                from+
                where+" order by t1.endTime desc";
        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(hql, s, p);
        Long totalNum = (Long) list.get(0).get("totalCount");
        Long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        resultMap.put("pageNo", pageNo);
        resultMap.put("totalNum", totalNum);
        resultMap.put("totalPage", totalPage);
        resultMap.put("totalPrice", list.get(0).get("totalPrice"));
        r.add("orderId");
        r.add("type");
        r.add("orderCash");
        r.add("createTime");
        r.add("endTime");
        r.add("payMoney");
        r.add("orderType");
        r.add("payType");
        r.add("memberIcon");
        r.add("realName");
        r.add("mobile");
        r.add("factorIcon");
        r.add("fee");
        String sql = "select" +
                " t2.orderId" +
                ",t2.type" +
                ",t2.orderCash" +
                ",t1.createTime" +
                ",t1.endTime" +
                ",t1.payMoney" +
                ",t1.orderType" +
                ",t1.payType" +
                ",t3.icon as memberIcon" +
                ",t3.realName" +
                ",t3.mobile" +
                ",t4.icon as factorIcon" +
                ",t5.fee" +
                from +
                where+" order by t1.endTime desc limit " + indexNum + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        resultMap.put("items", re);
        toResult(Response.Status.OK.getStatusCode(), resultMap);
    }


    /**
     * 根据发卡点区域 value获取发卡点名字(无需登录)
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getFactorNameByValue")
    public void getFactorNameByValue() throws Exception {
        String areaValue= ControllerContext.getPString("areaValue");
        toResult(Response.Status.OK.getStatusCode(), getFactorNameByValue(areaValue));
    }

    public Map<String,Object> getFactorNameByValue(String areaValue) throws Exception {
        Map<String,Object> params = new HashMap<>();
        params.put("areaValue",areaValue);
        return MysqlDaoImpl.getInstance().findOne2Map("Factor",params, new String[]{"name"},Dao.FieldStrategy.Include);
    }

    /**
     * 平台管理:根据ID获取发卡点信息
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Path("/getFactorById")
    public void getFactorById() throws Exception {
        String factorId=ControllerContext.getPString("factorId");
        toResult(Response.Status.OK.getStatusCode(), getFactorById(factorId));
    }

    public Map<String,Object> getFactorById(String factorId) throws Exception {
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "请登录后操作");
        }

        if (StringUtils.isEmpty(factorId)) {
            throw new UserOperateException(400, "找不到该用户");
        }

        Map<String,Object> re = MysqlDaoImpl.getInstance().findById2Map("Factor", factorId, null, null);
        return re;
    }

    /**
     * 获取发卡点信息
     * @throws Exception
     */
    @GET
    @Path("/getFactorBaseById")
    public void getFactorBaseById() throws Exception {
        String factorId=ControllerContext.getPString("_id");
        if(StringUtils.isEmpty(factorId)){
            return;
        }
        toResult(200,MysqlDaoImpl.getInstance().findById2Map("Factor",
                factorId,new String[]{"_id","name","mobile","canUse","areaValue"},Dao.FieldStrategy.Include));
    }

    /**
     * 查询手机号是否当前登陆用户绑定的发卡点的手机号
     *
     * @throws Exception
     */
    @GET
    @Path("/getMobileIsFactor")
    public void getMobileIsFactor() throws Exception {
        Map<String,Object> factor = getCurrentFactor();
        String forgotPhone = ControllerContext.getPString("phone");
        if(!forgotPhone.equals(factor.get("mobile"))){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "该手机号不是当前登陆用户绑定的手机号!");
        }
        toResult(Response.Status.OK.getStatusCode(), factor);
    }
    /**
     * 支付密码找回
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/payForgotPassword")
    public void payForgotPassword() throws Exception {
        String factorId = getCurrentFactor().get("_id").toString();
        String userId = ControllerContext.getContext().getCurrentUserId();
        String loginName = ControllerContext.getPString("loginName");
        String newPassword = ControllerContext.getPString("newPassword");
        String verification = ControllerContext.getPString("verification");
        if (StringUtils.isEmpty(newPassword) || !Pattern.matches("^[0-9]{6}$",newPassword)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码格式错误!");
        }
        if (StringUtils.isEmpty(loginName) || !Pattern.matches("^1[34578]{1}\\d{9}$", loginName)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "手机号格式错误!");
        }
        if (StringUtils.isEmpty(verification)) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "验证码不能为空!");
        }
        Map<String,Object> map = MysqlDaoImpl.getInstance().findById2Map("User",userId,new String[]{"password"},Dao.FieldStrategy.Include);
        if(MessageDigestUtils.digest(newPassword).equals(map.get("password"))){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "支付密码不能与登陆密码相同!");
        }
        Message msg = Message.newReqMessage("1:PUT@/common/Sms/checkSmsCode");
        msg.getContent().put("type", "change_password");
        msg.getContent().put("smsCode", verification);
        msg.getContent().put("phone", loginName);
        ServiceAccess.callService(msg);

        Map<String,Object> v = new HashMap<>();
        v.put("_id",factorId);
        v.put("cashPassword",MessageDigestUtils.digest(newPassword));
        MysqlDaoImpl.getInstance().saveOrUpdate("Factor",v);

    }
    /**
     *  检查当前登陆发卡点是否被禁用
     */
    @GET
    @Path("/getFactorIsTrue")
    public void getFactorIsTrue() throws Exception {
        String factorId = ControllerContext.getPString("factorId");
        Map<String,Object> factor = MysqlDaoImpl.getInstance().findById2Map("Factor",factorId,new String[]{"canUse"},Dao.FieldStrategy.Include);
        toResult(Response.Status.OK.getStatusCode(), factor);
    }

    /**
     * 查询分支机构
     * @throws Exception
     */
    @GET
    @Path("/getBranches")
    public void getBranches() throws Exception {
        String _id = ControllerContext.getPString("_id");
        String pid = ControllerContext.getPString("_pid");
        String search = ControllerContext.getPString("_search");
        String isFormat = ControllerContext.getPString("isFormat");
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");

        String where = " where 1=1";

        List<Object> params = new ArrayList<>();
        if(StringUtils.isNotEmpty(_id)){
            where+=" and t1._id=?";
            params.add(_id);
            pageNo=1;
            pageSize=1;
        }
        if(StringUtils.isNotEmpty(pid)){
            if("notCity".equals(pid)){
                where+=" and t1.pid <> '-1'";
            }else{
                where+=" and t1.pid=?";
                params.add(pid);
            }
        }
        if(StringUtils.isNotEmpty(search)){
            where+=" and (t1.mobile like ? or t1.name like ? or t1.address like ?)";
            params.add("%"+search+"%");
            params.add("%"+search+"%");
            params.add("%"+search+"%");
        }

        List<String> returnFields = new ArrayList<>();
        String hql = "select count(t1._id) as totalCount" +
                " from Branches t1" +
                where;
        returnFields.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, returnFields, params);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        returnFields.clear();
        returnFields.add("_id");
        returnFields.add("pid");
        returnFields.add("name");
        returnFields.add("belongCity");
        returnFields.add("mobile");
        returnFields.add("address");
        returnFields.add("createTime");
        String sql="select" +
                " t1._id" +
                ",t1.pid" +
                ",t1.name" +
                ",(select name from Branches t2 where t1.pid=t2._id) as belongCity" +
                ",t1.mobile" +
                ",t1.address" +
                ",t1.createTime"+
                " from Branches t1"+
                where+
                " order by t1.pid asc" +
                " limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);

        //将得到的数据按城市进行分组
        if(StringUtils.isNotEmpty(isFormat) && ControllerContext.getPBoolean("isFormat")){
            //得到所有城市的集合
            List<Map<String,Object>> cityList = new ArrayList<>();
            Iterator<Map<String,Object>> cityIterator = re.iterator();
            while(cityIterator.hasNext()){
                //因为数据是按照pid排序,所以如果找到第一个pid!=-1的情况,直接结束
                Map<String,Object> temp = cityIterator.next();
                if(!"-1".equals(temp.get("pid").toString())){
                    break;
                }
                Map<String,Object> name = new HashMap<>();
                name.put("cityName",temp.get("name"));
                name.put("cityId",temp.get("_id"));
                cityList.add(name);
                cityIterator.remove();
            }
            //将每个城市拥有的机构添加进去
            int cityStartIndex=0;
            for(int i=0,len=cityList.size();i<len;i++){
                List<Map<String,Object>> branches = new ArrayList<>();
                for(int jlen=re.size();cityStartIndex<jlen;cityStartIndex++){
                    if(!cityList.get(i).get("cityId").toString().equals(re.get(cityStartIndex).get("pid").toString())){
                        break;
                    }
                    branches.add(re.get(cityStartIndex));
                }
                cityList.get(i).put("branches",branches);
            }
            re=cityList;
        }

        page.setItems(re);
        toResult(200,page);
    }


    /**
     * 添加/修改 分支机构/城市
     * @throws Exception
     */
    @POST
    @Path("/modifyBranches")
    public void modifyBranches() throws Exception {
        String _id=ControllerContext.getPString("_id");
        String pid=ControllerContext.getPString("pid");
        String name=ControllerContext.getPString("name");
        String mobile=ControllerContext.getPString("mobile");
        String address=ControllerContext.getPString("address");

        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if(agent==null || agent.size()==0){
            throw new UserOperateException(500,"您无此操作权限");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + agent.get("_id"));
        if(!"2".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }

        if("-1".equals(pid)){
            //查询是否有相同名称的城市
            Map<String,Object> params = new HashMap<>();
            params.put("name",name);
            Map<String,Object> oldBranch = MysqlDaoImpl.getInstance().findOne2Map("Branches",params,null,null);
            if(oldBranch!=null && oldBranch.size()!=0){
                throw new UserOperateException(500,"已存在相同名称的城市/分支机构");
            }
        }

        Map<String,Object> branches = new HashMap<>();
        if(StringUtils.isEmpty(_id)){
            _id=UUID.randomUUID().toString();
        }
        branches.put("_id",_id);
        branches.put("pid",pid);
        branches.put("name",name);
        branches.put("createTime",System.currentTimeMillis());
        if(!"-1".equals(pid)){
            if(address.length()==0 || address.length()>300){
                throw new UserOperateException(500,"地址长度在300位字符长度以内");
            }
            if(name.length()==0 || name.length()>300){
                throw new UserOperateException(500,"名称长度在100位字符长度以内");
            }
            if(mobile.length()==0 || mobile.length()>20){
                throw new UserOperateException(500,"联系方式长度在20位字符长度以内");
            }
            branches.put("mobile",mobile);
            branches.put("address",address);
        }
        MysqlDaoImpl.getInstance().saveOrUpdate("Branches",branches);
    }

    /**
     * 删除 分支机构/城市
     * @throws Exception
     */
    @GET
    @Path("/delBranches")
    public void delBranches() throws Exception {
        String _id=ControllerContext.getPString("_id");
        //获取当前登录的代理商
        //获取当前登录的代理商
        Map<String, Object> agent = new AgentAction().getCurrentAgent();
        if(agent==null || agent.size()==0){
            throw new UserOperateException(500,"您无此操作权限");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + agent.get("_id"));
        if(!"2".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }

        if(StringUtils.isEmpty(_id)){
            throw new UserOperateException(500,"请选择需要删除的城市/分支机构");
        }

        Map<String,Object> oldBranch = MysqlDaoImpl.getInstance().findById2Map("Branches",_id,null,null);
        if(oldBranch==null || oldBranch.size()==0){
            throw new UserOperateException(500,"未找到城市/分支机构");
        }
        if("-1".equals(oldBranch.get("pid").toString())){
            Map<String,Object> params = new HashMap<>();
            params.put("pid",oldBranch.get("_id").toString());
            Map<String,Object> oldChild = MysqlDaoImpl.getInstance().findOne2Map("Branches",params,null,null);
            if(oldChild!=null && oldChild.size()>0){
                throw new UserOperateException(500,"请先删除该城市下的所有分支机构");
            }
        }
        MysqlDaoImpl.getInstance().remove("Branches",_id);
    }
}