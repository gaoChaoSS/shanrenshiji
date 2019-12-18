package com.zq.kyb.payment.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.payment.config.GpayConfig;
import com.zq.kyb.payment.service.gpay.impl.GpayBaseServiceImpl;
import com.zq.kyb.payment.service.gpay.util.AcpService;
import com.zq.kyb.payment.service.gpay.util.SDKUtil;
import com.zq.kyb.payment.service.gpay.util.Signature;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zq2014 on 18/11/28.
 */
public class GpayAction extends BaseActionImpl {
    final static Logger log =  Logger.getLogger(GpayAction.class);
    private static final String BUSI_API_KEY = GpayConfig.BUSI_API_KEY;
    private static final String PUBLIC_KEY = GpayConfig.PUBLIC_KEY;
    @POST
    @Path("/submitAccount")
    public  void submitAccount() throws Exception{
        JSONObject text = ControllerContext.getContext().getReq().getContent();
        System.out.println("text=="+JSONObject.fromObject(text));
        if (text == null || StringUtils.mapValueIsEmpty(text,"_id") || StringUtils.mapValueIsEmpty(text,"pendingId") ) {
            throw new UserOperateException(400, "获取数据失败!");
        }
        if (text.get("name") == null || StringUtils.isEmpty(text.get("name").toString()) || text.get("name").toString().length() > 100) {
            throw new UserOperateException(400, "商家名称在100字以内!");
        }
        if (Pattern.matches("^(\\S*((([轻亲]+)([送松菘颂淞讼凇忪]+)\\S*)|(([惠慧蕙会]+)([网罔王旺]+)\\S*)))\\S*$", text.get("name").toString())){
            throw new UserOperateException(400, "商家名称不能含有'普惠生活'相关的敏感字!");
        }
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
        if (text.get("operateType") == null || StringUtils.isEmpty(text.get("operateType").toString())) {
            throw new UserOperateException(400, "请选择经营范围!");
        }
        if (text.get("areaValue") == null || StringUtils.isEmpty(text.get("areaValue").toString()) || text.get("areaValue").toString().length() > 200) {
            throw new UserOperateException(400, "请选择完整的所在区域位置!");
        }
        if (text.get("address") == null || StringUtils.isEmpty(text.get("address").toString()) || text.get("address").toString().length() > 200) {
            throw new UserOperateException(400, "请填写完整的所在街道详细位置,且不能超过200位字符!");
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
        if (text.get("legalPerson") == null || StringUtils.isEmpty(text.get("legalPerson").toString())) {
            throw new UserOperateException(400, "请填写法人名称!");
        }
        if (!Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$", (String) text.get("realCard"))) {
            throw new UserOperateException(400, "请输入正确的法人身份证号码!");
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
        }
        if((text.get("sitephoto") == null || StringUtils.isEmpty(text.get("sitephoto").toString()))){
            throw new UserOperateException(400, "请上传营业场所照!");
        }
        if((text.get("mark") == null || StringUtils.isEmpty(text.get("mark").toString()))){
            throw new UserOperateException(400, "请选择线上线下标志!");
        }
        if((text.get("paytype") == null || StringUtils.isEmpty(text.get("paytype").toString()))){
            throw new UserOperateException(400, "请选择支付方式!");
        }
        if((text.get("accountType") == null || StringUtils.isEmpty(text.get("accountType").toString()))){
            throw new UserOperateException(400, "请选择账户类型!");
        }
        if(!StringUtils.isEmpty(text.get("contactType").toString())){
            if("2".equals(text.get("contactType").toString())){
                if((text.get("authphoto") == null || StringUtils.isEmpty(text.get("authphoto").toString()))){
                    throw new UserOperateException(400, "请上传代理人授权书照片!");
                }
                if((text.get("agentphotof") == null || StringUtils.isEmpty(text.get("agentphotof").toString()))){
                    throw new UserOperateException(400, "请上传代理人证件正面照!");
                }
                if((text.get("agentphotob") == null || StringUtils.isEmpty(text.get("agentphotob").toString()))){
                    throw new UserOperateException(400, "请上传代理人证件背面照!");
                }
            }
        }else {
            throw new UserOperateException(400, "请选择开户人类型类型!");
        }

        if((text.get("lpcertval") == null || StringUtils.isEmpty(text.get("lpcertval").toString()))){
            throw new UserOperateException(400, "请填写法人证件有效期!");
        }
        if(!StringUtils.isEmpty(text.get("merchantsType").toString())) {
            if (!"0".equals(text.get("merchantsType").toString())) {
                if (text.get("businessLicense") == null || StringUtils.isEmpty(text.get("businessLicense").toString())) {
                    throw new UserOperateException(400, "请上传营业执照!");
                }
                if (text.get("companyCertificateType") == null || StringUtils.isEmpty(text.get("companyCertificateType").toString())) {
                    throw new UserOperateException(400, "选择一种商户证件类型!");
                }
                if (text.get("companyIdNumber") == null || StringUtils.isEmpty(text.get("companyIdNumber").toString())) {
                    throw new UserOperateException(400, "请填写商户证件号!");
                }
                if (text.get("registeredAddress") == null || StringUtils.isEmpty(text.get("registeredAddress").toString())) {
                    throw new UserOperateException(400, "请填写证件注册地址!");
                }

            }
        }else {
            throw new UserOperateException(400, "请选择商户类型!");
        }

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

        //更改状态
        String pendingId = "";
        if (text.get("pendingId") == null || StringUtils.isEmpty(text.get("pendingId").toString())) {
            pendingId = UUID.randomUUID().toString();
        } else {
            pendingId = text.get("pendingId").toString();
            text.remove("pendingId");//text只保存seller里存在的字段
        }
        Map<String, Object> s = new HashMap<>();
        s.put("_id", pendingId);
        System.out.println("pendingId=="+pendingId);
        s.put("owner", text.get("name").toString());
        s.put("ownerId", text.get("_id").toString());
        s.put("ownerType", "Seller");//Seller,Factor,Agent
        s.put("createTime",System.currentTimeMillis());
        Message m = Message.newReqMessage("1:GET@/account/UserPending/userPendingSaveOrUpdate");
        m.getContent().put("s", s);
        ServiceAccess.callService(m);
        String id = "\'" + pendingId + "\'";
        List<Object> parms = new ArrayList<>();
        parms.add(text);
        parms.add(text.get("name"));
        parms.add(4);//0:草稿;1:待审;2:通过;3:不通过;4:贵商待审;5:开户失败;6:绑定失败
        String hql = "UPDATE userpending SET text=?,owner=?,status=? WHERE _id="+id;
        MysqlDaoImpl.getInstance().exeSql(hql,parms,"userpending");

        Map<String, String> data = new HashMap<>();
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod",GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", GpayConfig.KHTYPE);
        data.put("subcode",GpayConfig.SUBCODE_ADDBUSI);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        data.put("serialno",pendingId);
        data.put("regname",text.get("phone").toString());
        data.put("commname",text.get("name").toString());
        data.put("busiabbr",text.get("merchantsAbbreviation").toString());
        data.put("trade",text.get("trade").toString());
        data.put("province_code",text.get("province_code").toString()); //地区编码（省）
        data.put("city_code",text.get("city_code").toString());  //地区编码（市）
        data.put("district_code",text.get("district_code").toString());  //地区编码（区）
        data.put("address",text.get("address").toString());
        data.put("onlineflag",text.get("mark").toString());
        data.put("busitype",text.get("merchantsType").toString());
        if(!"0".equals(text.get("merchantsType").toString())){
            data.put("certtype",text.get("companyCertificateType").toString());
            data.put("certno",text.get("companyIdNumber").toString());
            data.put("regaddress",text.get("registeredAddress").toString());
            data.put("optscope",text.get("operateType").toString());
            String businessLicense = findFilename(text.get("businessLicense").toString());
            data.put("certphoto",businessLicense);//营业执照
        }
        //data.put("market",text.get("marketName").toString()); //市场名称
        //data.put("url",text.get()) //线上商城地址
        data.put("paytype",text.get("paytype").toString());//支付方式(必填)
        String doorphoto = findFilename(text.get("doorImg").toString());
        data.put("doorphoto",doorphoto);//门头照
        String sitephoto = findFilename(text.get("sitephoto").toString());
        data.put("sitephoto",sitephoto); //经营场所照(必填)
        //data.put("specialphoto",); 特殊经营许可证
        data.put("lpname",text.get("legalPerson").toString());
        data.put("lpcerttype","1"); //法人证件类型,1-身份证，固定值
        data.put("lpcertno",text.get("realCard").toString());
        String lpcertval = text.get("lpcertval").toString().replace("-","");
        data.put("lpcertval",lpcertval);
        String certphotof = findFilename(text.get("idCardImgFront").toString());
        String certphotob = findFilename(text.get("idCardImgBack").toString());
        String certphotoh = findFilename(text.get("idCardImgHand").toString());
        data.put("certphotof",certphotof);//法人身份证正面照
        data.put("certphotob",certphotob);//法人身份证背面照
        data.put("certphotoh",certphotoh);//法人手持身份证照
        data.put("contact_type",text.get("contactType").toString());
        if("2".equals(text.getString("contactType"))){
            String authphoto = findFilename(text.get("authphoto").toString());
            String agentphotof = findFilename(text.get("agentphotof").toString());
            String agentphotob = findFilename(text.get("agentphotob").toString());
            data.put("authphoto",authphoto); //代理人授权书照片
            data.put("agentphotof",agentphotof);  //代理人证件照正面
            data.put("agentphotob",agentphotob);  //代理人证件照背面
        }
        data.put("acctype",text.get("accountType").toString());
        data.put("basename",text.get("bankUser").toString());
        Message message = Message.newReqMessage("1:POST@/payment/Gpay/findBankbyName");
        message.getContent().put("bankName",text.get("bankName"));
        JSONObject bank = ServiceAccess.callService(message).getContent();
        data.put("basebankno",bank.get("code").toString()); //开户行行号(必填)
        data.put("basebusiacc",text.get("bankId").toString());

        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + GpayConfig.BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, GpayConfig.PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type","RSA");
        String url = GpayConfig.ADDBUSI_URL;
        System.out.println("data=="+JSONObject.fromObject(data));
        Map<String, String> rsp = AcpService.post(data, url, GpayConfig.ENCODE);
        System.out.println("rsp=="+JSONObject.fromObject(rsp));
        Map<String,Object> rspData = new HashMap<>();
        String subbusino = rsp.get("subbusino");
        rspData.put("subbusino",rsp.get("subbusino"));
        rspData.put("businessLicense",text.get("businessLicense"));
        rspData.put("status",4);//0:草稿;1:待审;2:通过;3:不通过;4:开户待审核;5:开户成功;6:开户失败;7:绑定成功;8:绑定失败;9:商家解绑
        rspData.put("applyTime",s.get("createTime"));
        rspData.put("area",text.get("area"));
        rspData.put("areaValue",text.get("areaValue"));
        rspData.put("bankId",text.get("bankId"));
        rspData.put("bankImg",text.get("bankImg"));
        rspData.put("bankName",text.get("bankName"));
        rspData.put("bankUser",text.get("bankUser"));
        rspData.put("canUse",false);
        rspData.put("closeTime","21");
        rspData.put("doorImg",text.get("doorImg").toString());
        rspData.put("contactPerson",text.get("lpname"));
        rspData.put("createTime",s.get("createTime"));
        rspData.put("idCardImgBack",text.get("idCardImgBack"));
        rspData.put("idCardImgFront",text.get("idCardImgFront"));
        rspData.put("idCardImgHand",text.get("idCardImgHand"));
        Double integralRate = Double.valueOf(text.get("integralRate").toString());
        rspData.put("integralRate",integralRate);
        rspData.put("address",text.get("address"));
        rspData.put("intro",text.get("intro"));
        rspData.put("legalPerson",text.get("lpname"));
        rspData.put("name",text.get("name"));
        rspData.put("openTime",text.get("openTime"));
        rspData.put("openWeek",text.get("openWeek"));
        rspData.put("bankAddress",text.get("bankAddress"));
        List<String> returnOpearte = new ArrayList<>();
        returnOpearte.add("name");
        List<Object> param = new ArrayList<>();
        param.add(text.get("trade"));
        String str="SELECT name FROM industry WHERE code=?";
        List<Map<String, Object>> operate = MysqlDaoImpl.getInstance().queryBySql(str,returnOpearte,param);
        rspData.put("operateType",operate.get(0).get("name"));
        rspData.put("phone",text.get("phone"));
        rspData.put("realCard",text.get("realCard"));
        rspData.put("serverPhone",text.get("serverPhone"));
        rspData.put("email",text.get("email"));
        String _id = text.get("_id").toString();
        rspData.put("_id",_id);
        Message msg = Message.newReqMessage("1:GET@/account/Seller/sellerSaveOrUpdate");
        msg.getContent().put("map", rspData);
        ServiceAccess.callService(msg);
        List<Object> list = new ArrayList<>();
        list.add(subbusino);
        list.add(pendingId);
        list.add(_id);
        String hsql = "UPDATE seller SET status=4,subbusino=?,pendingId=? WHERE _id=?";
        MysqlDaoImpl.getInstance().exeSql(hsql,list,"seller");
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

    @POST
    @Path("/bindECP")
    public void bindECP() throws Exception{
        JSONObject re = ControllerContext.getContext().getReq().getContent();
        String rate = ControllerContext.getPString("rate");
        String filename = ControllerContext.getPString("filename");//签约文件名
        String assignno = ControllerContext.getPString("assignno"); //分润方式
        String subbusino = ControllerContext.getPString("subbusino");
        String merchantsAbbreviation = ControllerContext.getPString("merchantsAbbreviation");
        Map<String,Object> bind = new HashMap<>();
        if(StringUtils.mapValueIsEmpty(ControllerContext.getContext().getReq().getContent(),"subbusino")){
            List<String> returnOpearte = new ArrayList<>();
            returnOpearte.add("subbusino");
            returnOpearte.add("pendingId");
            returnOpearte.add("_id");
            List<Object> param = new ArrayList<>();
            param.add(merchantsAbbreviation);
            String str="SELECT subbusino,pendingId,_id FROM seller WHERE name=?";
            List<Map<String, Object>> seller = MysqlDaoImpl.getInstance().queryBySql(str,returnOpearte,param);
            subbusino = seller.get(0).get("subbusino").toString();
        }
        bind.put("rate",rate);
        bind.put("filename",filename);
        bind.put("assignno",assignno);
        bind.put("subbusino",subbusino);
        Map<String,Object> map = new GpayBaseServiceImpl().bindECP(bind);
        if(null == map.get("isSuccess") || !Boolean.valueOf(String.valueOf(map.get("isSuccess")))){
            throw new UserOperateException(400,"绑定失败");
        }
        toResult(200,map);
    }

    @POST
    @Path("/queryBusiStatus")
    public void queryBusiStatus() throws Exception{
        String subbusino = ControllerContext.getPString("subbusino");
        Map<String,String> data = new HashMap<>();
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod",GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", GpayConfig.KHTYPE);
        data.put("subcode",GpayConfig.SUBCODE_QUERYBUSISTATUS);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
        data.put("subbusino",subbusino);
        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + GpayConfig.BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, GpayConfig.PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type","RSA");
        String url = GpayConfig.QUERYBUSISTATUS_URL;
        System.out.println("开户查询=="+JSONObject.fromObject(data));
        Map<String, String> rsp = AcpService.post(data, url, GpayConfig.ENCODE);
        System.out.println("开户返回=="+JSONObject.fromObject(rsp));
        if (!rsp.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rsp, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE);
            if (flag) {
                rsp.put("verifyflag", "验证签名成功");
                if ("00000".equals(rsp.get("respcode"))) {
                    rsp.put("isSuccess", "true");
                }
            }
        } else {
            rsp.put("isSuccess", "false");
            log.error("未获取到返回报文或返回http状态码非200");
        }
        toResult(200,rsp);
    }


    @POST
    @Path("/unbindECP")
    public void unbindECP() throws Exception{
        String sellerId = ControllerContext.getPString("sellerId");
        if(StringUtils.isEmpty(sellerId)){
            throw new UserOperateException(400,"子商户号不能为空");
        }
        Map map = new GpayBaseServiceImpl().unbindECP(sellerId);
        if(null == map.get("isSuccess") || !Boolean.valueOf(String.valueOf(map.get("isSuccess")))){
            throw new UserOperateException(400,"解绑失败");
        }else{
            String id="\'"+sellerId+"\'";
            String field = "UPDATE seller SET canUse=FALSE,status = 9 WHERE _id ="+id;
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(field);
            MysqlDaoImpl.commit();
        }
    }

    @POST
    @Path("/downloadBill")
    public void downloadBill() throws Exception{
//        String sellerId = ControllerContext.getPString("sellerId");
        String checkDate = ControllerContext.getPString("date");
//        if(StringUtils.isEmpty(sellerId)){
//            throw new UserOperateException(400,"子商户号不能为空");
//        }else
        if(StringUtils.isEmpty(checkDate)){
            throw new UserOperateException(400,"对账日期不能为空");
        }
        Map map = new GpayBaseServiceImpl().downloadBill(checkDate);
        if(null == map.get("isSuccess") || !Boolean.valueOf(String.valueOf(map.get("isSuccess")))){
            throw new UserOperateException(400,"生成对账文件失败");
        }
    }

    @POST
    @Path("/uploadPic")
    public void uploadPic() throws  Exception{
        String category = ControllerContext.getPString("category");
        Map<String,String> data = new HashMap<>();
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod",GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", "GP00036");
        data.put("subcode",GpayConfig.SUBCODE_UPLOADPIC);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
        data.put("website","phsh315");
        data.put("category",category);
        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + GpayConfig.BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, GpayConfig.PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type","RSA");
        toResult(200,data);

    }

    public String  findFilename(String fileId) throws Exception{
        List<String> returnField = new ArrayList<>();
        returnField.add("extendName");
        List<Object> params = new ArrayList<>();
        params.add(fileId);
        String sql="SELECT extendName FROM fileitem WHERE _id=?";
        List<Map<String, Object>> files = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        String filename = files.get(0).get("extendName").toString();
        return filename;
    }

    //买家确认收货
    @POST
    @Path("/cfrecv")
    public void cfrecv() throws  Exception{
        String transno = ControllerContext.getPString("transno");
        Map<String,String> data = new HashMap<>();
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod",GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", GpayConfig.KHTYPE);
        data.put("subcode",GpayConfig.SUBCODE_CFRECV);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
        data.put("operflag","2");
        data.put("transno",transno);
        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + GpayConfig.BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, GpayConfig.PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type","RSA");
        String url = GpayConfig.CFRECV_URL;
        Map<String, String> rsp = AcpService.post(data, url, GpayConfig.ENCODE);
    }

    @POST
    @Path("/findBankbyName")
    public void findBankbyName() throws Exception{
        String bankName = ControllerContext.getPString("bankName");
        List<String> returnField = new ArrayList<>();
        returnField.add("code");
        List<Object> params = new ArrayList<>();
        params.add(bankName);
        String sql="SELECT code FROM bank WHERE name=?";
        List<Map<String, Object>> bank = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        Map<String,Object> map = new HashMap<>();
        if(!bank.isEmpty() && bank.size()!=0){
            map.putAll(bank.get(0));
        }
        toResult(200,map);
    }

    //线上扫码支付
    @POST
    @Path("/applyQrcode")
    public void applyQrcode() throws Exception{
        JSONObject map = ControllerContext.getContext().getReq().getContent();
        Map<String,String> data = new HashMap<>();
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod",GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", "GP00011");
        data.put("subcode",GpayConfig.SUBCODE_APPLYQRCODE);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
        /*data.put("subbusino",);子商户号
        data.put("qrcodeType","00");
        data.put("orderno",);商户订单号
        data.put("amt_trans",);订单金额
        data.put("currency","156");
        data.put("ordertitle","线下支付");
        data.put("orderdesc",);商品描述
        data.put("return_url",); 同步
        data.put("notify_url",); 异步
        data.put("channel_type",);渠道类型
        data.put("confflag","9");*/
        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + GpayConfig.BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, GpayConfig.PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type","RSA");
        String url = GpayConfig.APPLYQRCODE_URL;
        Map<String, String> rsp = AcpService.post(data, url, GpayConfig.ENCODE);
    }
}
