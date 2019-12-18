package com.zq.kyb.order.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.order.util.XmlUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.axis.encoding.XMLType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.rpc.ParameterMode;
import java.text.SimpleDateFormat;
import java.util.*;

public class InsureAction extends BaseActionImpl {
//    public static String postUrl="http://123.177.21.38/pre/PREservices/XYZWebService?wsdl"; //测试接口
    public static String postUrl="http://eservice.aeonlife.com.cn/PREservices/XYZWebService?wsdl"; //正式接口

    public String getTransDate() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public String getTransTime() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 查询交易请求
     * @return
     * @throws Exception
     */
    public String queryInsureXML(Map<String,Object> item) throws Exception{
        String xml = "" +
                "<?xml version=\"1.0\" encoding=\"GBK\"?><TransData>" +
                "<BaseInfo>" +
                "<TransType>"+item.get("transType")+"</TransType>" +
//                "--必录 值固定\n" +
                "<TransCode>"+item.get("transCode")+"</TransCode>" +
//                "--必录 交易代码 值固定\n" +
                "<SubTransCode>"+item.get("subTransCode")+"</SubTransCode>" +
//                "--必录 子交易代码固定为1\n" +
                "<TransDate>"+item.get("transDate")+"</TransDate>" +
//                "--必录 报文发送日期\n" +
                "<TransTime>"+item.get("transTime")+"</TransTime>" +
//                "--必录 报文发送时间\n" +
                "<TransSeq>"+item.get("transSeq")+"</TransSeq>" +
//                "--必录 交易流水号(由代理方自定义规则生成)\n" +
                "<Operator>"+item.get("operator")+"</Operator>" +
//                "--必录 （合作方交易编码，由百年提供）\n" +
                "</BaseInfo>" +
//                "<!-- 接收报文 -->\n" +
                "<InputData>" +
//                "<!-- 合同基本信息 ，除已标识的必录信息外，其他信息是否必录根据合作产品的要求来确定-->" +
                "<QueryType>"+item.get("queryType")+"</QueryType>" +
//                "--必录  查询类别，G-团单信息查询，C-分单信息查询" +
                "<ContPlanCode>"+item.get("contPlanCode")+"</ContPlanCode>" +
//                "--必录  套餐产品编码(与百年合作的产品代码)" +
                "<ContNo>"+item.get("contNo")+"</ContNo>" +
//                "--非必录  保单号，查询团单信息时可为空" +
                "</InputData>" +
                "</TransData>";
        return xml;
    }

    /**
     * 通过身份证获取生日
     * @return
     * @throws Exception
     */
    public String getBirthday(String no) throws Exception{
        if(StringUtils.isEmpty(no)){
            throw new UserOperateException(500,"获取身份证号码失败");
        }
        no = no.substring(6,14);
        return no.substring(0,4)+"-"+no.substring(4,6)+"-"+no.substring(6,8);
    }

    public Map<String,Object> applyData() throws Exception{
        Map<String,Object> item = new HashMap<>();
        item.put("transDate",getTransDate());
        item.put("transTime",getTransTime());
        item.put("transSeq",ZQUidUtils.genUUID());
        item.put("contSerialNumber",ZQUidUtils.genUUID());
        item.put("appntName","国联普惠");
        item.put("appntSex","1");
        item.put("appntBirthday","1980-1-1");
        item.put("appntIDNo","91510100MA6DH3WW60");
        item.put("appntMobile","");
        item.put("appntAddress","");
        item.put("insuredName","罗云泽");
        item.put("insuredSex","1");
        item.put("insuredIDNo","510781199501247115");
        item.put("insuredBirthday",getBirthday("510781199501247115"));
        item.put("insuredMobile","13540461624");
        item.put("insuredAddress","");
        return item;
    }

    public String applyXML(Map<String,Object> item) throws Exception{
        String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
                "<TransData>" +
                "<BaseInfo>" +
                "<TransType>CALLCENTER</TransType>" +
//                "--必录 值固定" +
                "<TransCode>00030010</TransCode>" +
//                "--必录 交易代码 值固定" +
                "<SubTransCode>1</SubTransCode>" +
//                "--必录 子交易代码固定为1" +
                "<TransDate>"+item.get("transDate")+"</TransDate>" +
//                "--必录 报文发送日期" +
                "<TransTime>"+item.get("transTime")+"</TransTime>" +
//                "--必录 报文发送时间" +
                "<TransSeq>"+item.get("transSeq")+"</TransSeq>" +
//                "--必录 交易流水号(由代理方自定义规则生成)" +
                "<Operator>glph</Operator>" +
//                "--必录 （合作方交易编码，由百年提供）" +
                "</BaseInfo>" +
//                "<!-- 接收报文 -->" +
                "<InputData>" +
//                "<!-- 合同基本信息 ，除已标识的必录信息外，其他信息是否必录根据合作产品的要求来确定-->" +
                "<ContSerialNumber>"+item.get("contSerialNumber")+"</ContSerialNumber>" +
//                "--必录  订单号" +
                "<ContNo></ContNo>" +
//                "--非必录  保单号,合作方可自主生成" +
                "<ContPlanCode>1802</ContPlanCode>" +
//                "--必录  套餐产品编码(与百年合作的产品代码)" +
                "<UWFlag></UWFlag>" +
//                "--选录 核保标志  Y-核保交易  N-承保交易 默认为N （是否核保由百年确定，默认为N）" +
                "<AgentCode></AgentCode>" +
//                "--选录  直销-团险业务员代码/中介-中介机构代码/综拓-个险业务员代码 默认为空,是否需要填写由合作的产品决定(百年与合作方确认)" +
                "<Mult>1</Mult>" +
//                "--必录  产品购买份数，传1即可" +
                "<Amnt>0</Amnt>" +
//                "--必录  总保额 （每份的保额*份数），传0即可" +
                "<Prem>"+item.get("prem")+"</Prem>" +
//                "--必录  总保费" +
                "<InsuYear></InsuYear>" +
//                "--非必录  保险期间" +
                "<InsuYearFlag></InsuYearFlag>" +
//                "--非必录  保险期间标志 Y-年，M-月，D-日" +
                "<CValidate></CValidate>" +
//                "--非必录  保单生效日期,传空时取次日生效" +
                "<Origin></Origin>" +
//                "--非必录 出发地" +
                "<Destionation></Destionation>" +
//                "--非必录 目的地" +
                "<SetOutDate></SetOutDate>" +
//                "--非必录 出发日期" +
                "<SetOutTime></SetOutTime>" +
//                "--非必录 出发时间" +
                "<Vehicle></Vehicle>" +
//                "--非必录 交通工具类型:1-飞机;2-火车;3-轮船;4-汽车 5-其它" +
                "<ScheduledNo></ScheduledNo>" +
//                "--非必录 班次号" +
                "<SeatNo></SeatNo>" +
//                "--非必录 座位号" +
                "<TourNo></TourNo>" +
//                "--非必录 订单号（如通过第三方收费实时进入百年账户，则需要填写交费订单号，如合作方代收，则填写承保订单号）" +
                "" +
//                "<!-- 投保人信息 -->" +
                "<AppntName>"+item.get("appntName")+"</AppntName>" +
//                "--必录  投保人姓名" +
                "<AppntForeignName></AppntForeignName>" +
//                "--非必录  投保人英文姓名" +
                "<AppntSex>"+item.get("appntSex")+"</AppntSex>" +
//                "--必录  投保人性别:1-男，2-女" +
                "<AppntBirthday>"+item.get("appntBirthday")+"</AppntBirthday>" +
//                "--必录  投保人生日" +
                "<AppntIDType>5</AppntIDType>" +
//                "--必录  投保人证件类型:1-身份证，2-护照，3-回乡证，4-港澳台胞证" +
                "<AppntIDNo>"+item.get("appntIDNo")+"</AppntIDNo>" +
//                "--必录  投保人证件号" +
                "<AppntPhone></AppntPhone>" +
//                "--非必录  投保人电话" +
                "<AppntMobile>"+item.get("appntMobile")+"</AppntMobile>" +
//                "--非必录  投保人手机" +
                "<AppntEmail></AppntEmail>" +
//                "--非必录  投保人电子邮件" +
                "<AppntAddress>"+item.get("appntAddress")+"</AppntAddress>" +
//                "--非必录  投保人地址" +
                "<AppntPostCode></AppntPostCode>" +
//                "--非必录  投保人邮编" +
//                "<!-- 被保人信息 -->" +
                "<RelationToAppnt>5</RelationToAppnt>" +
//                "--必录  投保人是被保人的:1-本人,2-配偶,3-父母,4-子女,5-其他" +
                "<InsuredName>"+item.get("insuredName")+"</InsuredName>" +
//                "--必录  被保人姓名" +
                "<InsuredForeignName></InsuredForeignName>" +
//                "--非必录  被保人英文姓名" +
                "<InsuredSex>"+item.get("insuredSex")+"</InsuredSex>" +
//                "--必录  被保人性别:1-男，2-女" +
                "<InsuredIDType>1</InsuredIDType>" +
//                "--必录  被保人证件类型:1-身份证，2-护照，3-回乡证，4-港澳台胞证" +
                "<InsuredIDNo>"+item.get("insuredIDNo")+"</InsuredIDNo>" +
//                "--必录  被保人证件号" +
                "<InsuredBirthday>"+item.get("insuredBirthday")+"</InsuredBirthday>" +
//                "--必录  被保人生日" +
                "<InsuredPhone></InsuredPhone>" +
//                "--非必录  被保人电话" +
                "<InsuredMobile>"+item.get("insuredMobile")+"</InsuredMobile>" +
//                "--必录  被保人手机" +
                "<InsuredEmail></InsuredEmail>" +
//                "--非必录  被保人电子邮件" +
                "<InsuredAddress>"+item.get("insuredAddress")+"</InsuredAddress>" +
//                "--非必录  被保人地址" +
                "<InsuredPostCode></InsuredPostCode>" +
//                "--非必录  被保人邮编" +
                "<InsuredOccupationCode></InsuredOccupationCode>" +
//                "--非必录  被保人职业代码" +
                "<InsuredOccupationType></InsuredOccupationType>" +
//                "--非必录  被保人职业类型" +
                "<InsuredSocialInsuNo></InsuredSocialInsuNo>" +
//                "--非必录  被保人社会保障号" +
                "<InsuredSocialInsuFlag>Y</InsuredSocialInsuFlag>" +
//                "--非必录  被保人社保标志(是否社保）      " +
//                "<!-- 以下为其它特殊信息 -->" +
//                "<!-- 受益人1信息 -->" +
                "<BnfInfo></BnfInfo>" +
//                "--非必录  受益人信息，格式：受益人名字;受益人性别;受益人证件号;受益人与被保人关系;受益人受益比例（分号隔开）" +
//                "<!-- 受益人2信息 -->" +
                "<BnfInfo2></BnfInfo2>" +
//                "--选录 受益人2信息，格式：受益人2名字;受益人2性别;受益人2证件号;受益人2与被保人关系;受益人2受益比例（分号隔开）" +
//                "<!-- 紧急联系人1信息 -->" +
                "<ContactorInfo></ContactorInfo>" +
//                "--选录 紧急联系人信息，格式：紧急联系人姓名;紧急联系人证件号;紧急联系人生日;紧急联系人电话;紧急联系人手机;紧急联系人与被保人关系（分号隔开）" +
//                "<!-- 紧急联系人2信息 -->" +
                "<ContactorInfo2></ContactorInfo2>" +
//                "--选录 紧急联系人2信息，格式：紧急联系人2姓名;紧急联系人2证件号;紧急联系人2生日;紧急联系人2电话;紧急联系人2手机;紧急联系人2与被保人关系（分号隔开）" +
//                "<!-- 业务相关公司（机构）信息 -->" +
                "<RelaBussCom></RelaBussCom>" +
//                "--选录 业务相关公司（机构）信息,格式：公司（机构）名称;公司（机构）地址;公司（机构）联系方式（分号隔开） 用于记录 旅行社信息;航空公司信息;就职公司信息" +
//                "<!-- 业务相关公司（机构）2信息 -->" +
                "<RelaBussCom2></RelaBussCom2>" +
//                "--选录 业务相关公司（机构）2信息,格式：公司（机构）名称;公司（机构）地址;公司（机构）联系方式（分号隔开）用于记录 旅行社信息;航空公司信息;就职公司信息" +
//                " <!-- 学生保障类 -->" +
                " <SchoolInfo></SchoolInfo>" +
//                "--选录 学校班级信息，格式：学校名称;班级;阶段;地区" +
//                "<!-- 车主意外保障类 -->" +
                "<LicenseInfo></LicenseInfo>" +
//                "--选录 驾照信息，格式：准驾车型;驾照号;换证时间;首次申领驾照时间，申领地点" +
//                "<!-- 家政人员保障类 -->" +
                "<WorkInfo></WorkInfo>" +
//                "--选录 工作信息，格式：行业;工作时段;工作时间（分号隔开）" +
//                "<!-- 极特殊业务:残疾信息 -->" +
                "<DisabilityInfo></DisabilityInfo>" +
//                "--选录 残疾信息，格式：残疾程度;是否先天残疾;致残事故时间;是否已经治愈（分号隔开）" +
//                "<!-- 小额信贷 -->" +
                "<LoanInfo></LoanInfo>" +
//                "--选录 贷款信息，格式：贷款金额;贷款合同号;贷款日期（分号隔开）" +
//                "<!-- 航意险返程信息 -->" +
                "<ReturnTicketInfo></ReturnTicketInfo>" +
//                "--选录 航意险返程信息,格式：出发地;目的地;出发日期;出发时间;交通工具类型;班次号;座位号。该字段为购买往返机票时，填写返程信息用。仅购买单程机票的情况，该字段置为空即可。" +
                "</InputData>" +
                "</TransData>";
        return xml;
    }

    public Map<String,Object> confirmData() throws Exception{
        Map<String,Object> item = new HashMap<>();
        item.put("transDate",getTransDate());
        item.put("transTime",getTransTime());
        item.put("transSeq","d027d12e330641bbbd9995069f71a526");
        item.put("contSerialNumber","e045918c79854aab908b0f3c7e313dde");
        return item;
    }

    public String confirmXML(Map<String,Object> item) throws Exception{
        String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" +
            "<TransData>" +
            "<BaseInfo>" +
            "<TransType>CALLCENTER</TransType>" +
            "<TransCode>00031003</TransCode>" +
            //"--交易代码 必录 固定" +
            "<SubTransCode>1</SubTransCode>" +
            //"--子交易代码承保确认固定 必录" +
            "<TransDate>"+item.get("transDate")+"</TransDate>" +
            //"--报文发送日期 必录" +
            "<TransTime>"+item.get("transTime")+"</TransTime>" +
            //"--报文发送时间 必录" +
            "<TransSeq>"+item.get("transSeq")+"</TransSeq>" +
            //"--交易流水号 必录" +
            "<Operator>glph</Operator>" +
            //"--合作方名称 必录" +
            "</BaseInfo>" +
            //"  <!-- 接收报文 -->" +
            "<InputData>" +
            "<ContSerialNumber>"+item.get("contSerialNumber")+"</ContSerialNumber>" +
            //--必录  订单号" +
            "<TradeNo></TradeNo>" +
            //"--非必录  资金流水号，供财务对账使用" +
            "<Plattype></Plattype>" +
            //"--非必录  交易平台 1：支付宝；TL:B2C通联；YL：B2C银联" +
            "</InputData>" +
            "</TransData>";
        return xml;
    }

    /**
     * 向百年人寿提交报文
     * @param xml
     * @throws Exception
     */
    public String postData(String xml) throws Exception{
        org.apache.axis.client.Service service = new org.apache.axis.client.Service();
        org.apache.axis.client.Call call = (org.apache.axis.client.Call) service.createCall();
        call.setTargetEndpointAddress(new java.net.URL(postUrl));
        javax.xml.namespace.QName operator = new javax.xml.namespace.QName("http://webservice.sinosoft.com/",
                "dealXYZServlet");
        call.setOperationName(operator);
        call.setTimeout(600000);
        call.addParameter("xml", XMLType.SOAP_STRING, ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);
        return (String) call.invoke(new Object[]{xml});
    }

    /**
     * 查询投保信息
     * @throws Exception
     */
    @GET
    @Path("/queryInsureLog")
    public void queryInsureLog() throws Exception{
        String memberId = ControllerContext.getPString("memberId");
        String mobile = ControllerContext.getPString("mobile");
        String logId = ControllerContext.getPString("logId");
        String idCard = ControllerContext.getPString("idCard");
        List<String> returnFields = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String where = " where 1=1";
        String fields = "",limit="",sql,
               from = " from InsureLog t1" +
                    " left join OrderInfo t2 on t1.transSeq = t2.orderNo" +
                    " left join Member t3 on t1.memberId = t3._id";;
        Page page = new Page();

        if(StringUtils.isNotEmpty(memberId)){
            where += " and t1.memberId = ?";
            params.add(memberId);
        }
        if(StringUtils.isNotEmpty(mobile)){
            where += " and t3.mobile = ?";
            params.add(mobile);
        }
        if(StringUtils.isNotEmpty(idCard)){
            where += " and t3.idCard = ?";
            params.add(idCard);
        }
        if(StringUtils.isNotEmpty(logId)){
            where += " and t1._id = ?";
            params.add(logId);
        }else{
            long pageNo = ControllerContext.getPString("pageNo") == null ? 1 : ControllerContext.getPLong("pageNo");
            int pageSize = ControllerContext.getPString("pageSize") == null ? 20 : ControllerContext.getPInteger("pageSize");
            sql = "select count(t1._id) as totalCount";
            returnFields.add("totalCount");
            List<Map<String, Object>> count = MysqlDaoImpl.getInstance().queryBySql(sql+from+where, returnFields, params);
            long totalNum = 0L;
            if (count.size() != 0) {
                totalNum = Long.parseLong(count.get(0).get("totalCount").toString());
            }
            page = new Page(pageNo, pageSize, totalNum);
            limit = " limit " + page.getStartIndex() + "," + pageSize;
        }
        returnFields.clear();
        returnFields.add("_id");
        returnFields.add("createTime");
        returnFields.add("contNo");
        returnFields.add("status");
        returnFields.add("returnFlag");
        returnFields.add("prem");
        returnFields.add("transSeq");
        returnFields.add("orderType");
        returnFields.add("realName");
        returnFields.add("cardNo");

        if(StringUtils.isNotEmpty(logId)){
            fields += ",t1.contSerialNumber" +
                    ",t1.applyReturnXML"+
                    ",t1.applyXML"+
                    ",t1.confirmReturnXML"+
                    ",t1.confirmXML"+
                    ",t1.queryReturnXML"+
                    ",t1.desc";
            returnFields.add("contSerialNumber");
            returnFields.add("applyReturnXML");
            returnFields.add("applyXML");
            returnFields.add("confirmReturnXML");
            returnFields.add("confirmXML");
            returnFields.add("queryReturnXML");
            returnFields.add("desc");
        }
        sql = "select" +
                " t1._id" +
                ",t1.createTime" +
                ",t1.contNo" +
                ",t1.status" +
                ",t1.returnFlag" +
                ",t1.prem"+
                ",t1.transSeq"+
                ",t2.orderType" +
                ",t3.realName"+
                ",t3.cardNo"+
                fields+
                from + where +
                " order by t1.createTime desc" +
                limit;

        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(StringUtils.isNotEmpty(logId)){
            Map<String,Object> item = new HashMap<>();
            if(re!=null && re.size()!=0){
                item = re.get(0);
                item.put("applyReturnItem",XmlUtils.xmlToMapObject(item.get("applyReturnXML")));
                item.put("applyItem",XmlUtils.xmlToMapObject(item.get("applyXML")));
                item.put("confirmReturnItem",XmlUtils.xmlToMapObject(item.get("confirmReturnXML")));
                item.put("confirmItem",XmlUtils.xmlToMapObject(item.get("confirmXML")));
                item.put("queryReturnItem",XmlUtils.xmlToMapObject(item.get("queryReturnXML")));
            }
            toResult(200,item);
        }else{
            page.setItems(re);
            toResult(200,page);
        }
    }

    /**
     * 获取投保信息
     * @param pensionId
     * @throws Exception
     */
    public Map<String,Object> getInsureLog(String pensionId) throws Exception{
        if(StringUtils.isEmpty(pensionId)){
            return null;
        }
        Map<String,Object> params = new HashMap<>();
        params.put("pensionId",pensionId);
        return MysqlDaoImpl.getInstance().findOne2Map("InsureLog",params
                ,new String[]{"_id","pensionId","status","returnFlag","transSeq","contSerialNumber","contNo","prem"}, Dao.FieldStrategy.Include);
    }

    /**
     * 会员消费投保:缴费申请
     * @throws Exception
     * transSeq 就是 orderNo
     */
    public Map<String,Object> tradeMemberInsure(String memberId,String orderNo,String pensionId,double prem) throws Exception{
        String key = "insure_"+orderNo;
        JedisUtil.whileGetLock(key,60);
        Map<String,Object> log = new HashMap<>();
        try{
            if(prem==0){
                throw new UserOperateException(500,"总保额错误");
            }
            Map<String,Object> params = new HashMap<>();
            params.put("transSeq",orderNo);
            Map<String,Object> old = MysqlDaoImpl.getInstance().findOne2Map("InsureLog",params,null,null);
            if(old!=null && old.size()!=0){
                throw new UserOperateException(500,"请勿重复投保");
            }
            log = getInsureLog(pensionId);
            if(log!=null && log.size()!=0 && !StringUtils.mapValueIsEmpty(log,"status") && !log.get("status").equals("fail")){
                throw new UserOperateException(500,"请勿重复投保");
            }else if(log==null || log.size()==0){
                log = new HashMap<>();
                log.put("_id",ZQUidUtils.genUUID());
                log.put("status","apply");
                log.put("returnFlag","start");
                log.put("pensionId",pensionId);
                log.put("prem",prem);
                log.put("memberId",memberId);
                log.put("transSeq",orderNo);
                log.put("createTime",System.currentTimeMillis());
                MysqlDaoImpl.getInstance().saveOrUpdate("InsureLog",log);
            }
            if(log.get("status").equals("apply") && log.get("returnFlag").equals("start")){
                // 缴费申请
                log = checkPostData(log,applyInsure(log,memberId,prem));
            }
            if(log.get("status").equals("apply") && log.get("returnFlag").equals("success")){
                // 缴费确认
                log.put("status","confirm");
                log.put("returnFlag","start");
                log = checkPostData(log,confirmInsure(log));
            }
            if(log.get("status").equals("confirm") && log.get("returnFlag").equals("success")){
                // 交易查询
                log.put("status","end");
                log.put("returnFlag","start");
                log = checkPostData(log,queryInsure(log));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JedisUtil.del(key);
        }
        return log;
    }

    /**
     * 获取状态对应的报文
     * @param item
     * @param status
     * @return
     * @throws Exception
     */
    public String getXMLByStatus(Map<String,Object> item,String status) throws Exception{
        if(status.equals("apply")){
            return applyXML(item);
        }else if(status.equals("confirm")){
            return confirmXML(item);
        }else if(status.equals("end")){
            return queryInsureXML(item);
        }else{
            throw new UserOperateException(500,"错误的状态类型");
        }
    }

    /**
     * 检查返回数据并保存
     * @param log
     * @param item
     * @return
     * @throws Exception
     */
    public Map<String,Object> checkPostData(Map<String,Object> log,Map<String,Object> item) throws Exception{
        String status = log.get("status").toString();
        String xml = getXMLByStatus(item,status);

        Map<String,Object> newLog = new HashMap<>();
        newLog.put("_id",log.get("_id"));
        newLog.put("status",status);
        newLog.put("transSeq",log.get("transSeq"));
        newLog.put("contSerialNumber",log.get("contSerialNumber"));
        newLog.put(status+"XML",xml);

        // 提交报文数据
        String returnXML = postData(xml);
        newLog.put((status.equals("end")?"query":status)+"ReturnXML",returnXML);
        Map<String,Object> applyReturn = XmlUtils.toMap(returnXML);
        if(applyReturn==null || applyReturn.size()==0){
            newLog.put("returnFlag","fail");
        }else{
            Map<String,Object> transData = (Map<String,Object>)applyReturn.get("TransData");
            if(transData==null || transData.size()==0){
                newLog.put("returnFlag","fail");
            }else{
                Map<String,Object> outputData = (Map<String,Object>) transData.get("OutputData");
                if(outputData!=null && outputData.get("ReturnFlag").equals("0")){
                    newLog.put("returnFlag","success");
                    newLog.put("desc",outputData.get("ReturnDesc"));
                    if(!StringUtils.mapValueIsEmpty(outputData,"ContNo")){
                        newLog.put("contNo",outputData.get("ContNo"));
                    }
                } else {
                    newLog.put("returnFlag","fail");
                    if(!StringUtils.mapValueIsEmpty(outputData,"ReturnDesc")){
                        newLog.put("desc",outputData.get("ReturnDesc"));
                    }
                }
            }
        }
        MysqlDaoImpl.getInstance().saveOrUpdate("InsureLog",newLog);
        return newLog;
    }

    /**
     * 缴费申请
     * @param memberId
     * @throws Exception
     */
    public Map<String,Object> applyInsure (Map<String,Object> log ,String memberId ,double prem) throws Exception{
        JSONObject member = ServiceAccess.getRemoveEntity("crm","Member",memberId);
        if(member==null){
            throw new UserOperateException(500,"获取会员信息失败");
        }
        if(StringUtils.mapValueIsEmpty(member,"isRealName") || !Boolean.parseBoolean(member.get("isRealName").toString())
                || StringUtils.mapValueIsEmpty(member,"realName") || StringUtils.mapValueIsEmpty(member,"sex")
                || StringUtils.mapValueIsEmpty(member,"idCard") || StringUtils.mapValueIsEmpty(member,"realArea")
                || StringUtils.mapValueIsEmpty(member,"realAddress")){
            throw new UserOperateException(500,"请先完善实名认证信息");
        }

        if(StringUtils.mapValueIsEmpty(member,"isBindCard") || !Boolean.parseBoolean(member.get("isBindCard").toString())){
            throw new UserOperateException(500,"请先激活会员");
        }
        Map<String,Object> item = new HashMap<>();
        String transSeq = log.get("transSeq").toString();
        String contSerialNumber = ZQUidUtils.genUUID();
        log.put("transSeq",transSeq);
        log.put("contSerialNumber",contSerialNumber);
        MysqlDaoImpl.getInstance().saveOrUpdate("InsureLog",log);
        item.put("transDate",getTransDate());
        item.put("transTime",getTransTime());
        item.put("transSeq",transSeq);
        item.put("contSerialNumber",contSerialNumber);
        item.put("prem",prem);
        //  投保人： 国联普惠
        item.put("appntName","国联普惠");
        item.put("appntSex","1");
        item.put("appntBirthday","1980-1-1");
        item.put("appntIDNo","91510100MA6DH3WW60");
        item.put("appntMobile","");
        item.put("appntAddress","");
        //  被投保人：会员
        item.put("insuredName",member.get("realName").toString());
        item.put("insuredSex",member.get("sex"));
        item.put("insuredIDNo",member.get("idCard").toString());
        item.put("insuredBirthday",getBirthday(member.get("idCard").toString()));
        item.put("insuredMobile",member.get("mobile").toString());
        item.put("insuredAddress",member.get("realArea").toString() + member.get("realAddress").toString());
        return item;
    }

    /**
     * 缴费确认
     * @param log
     * @throws Exception
     */
    public Map<String,Object> confirmInsure (Map<String,Object> log) throws Exception{
        Map<String,Object> item = new HashMap<>();
        item.put("transDate",getTransDate());
        item.put("transTime",getTransTime());
        item.put("transSeq",log.get("transSeq"));
        item.put("contSerialNumber",log.get("contSerialNumber"));
        return item;
    }

    /**
     * 缴费交易查询
     * @param log
     * @throws Exception
     */
    public Map<String,Object> queryInsure(Map<String,Object> log) throws Exception{
        Map<String,Object> item = new HashMap<>();
        item.put("transType","CALLCENTER");
        item.put("transCode","00030013");
        item.put("subTransCode","1");
        item.put("transDate",getTransDate());
        item.put("transTime",getTransTime());
        item.put("transSeq",log.get("transSeq"));
        item.put("operator","glph");
        item.put("queryType","G");
        item.put("contPlanCode","1802");
        item.put("contNo",log.get("contNo"));
        return item;
    }
}
