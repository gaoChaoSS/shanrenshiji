<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.poi.hssf.usermodel.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.io.BufferedOutputStream" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="com.zq.kyb.core.model.Message" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="com.zq.kyb.core.service.ServiceAccess" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.zq.kyb.util.StringUtils" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.zq.kyb.util.BigDecimalUtil" %>
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<%!  public void createTradeExcelTable(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Message message = Message.newReqMessage("1:GET@/order/OrderInfo/getBill");
    Cookie[] cokies = request.getCookies();
    String token="";
    for (Cookie c:cokies){
        if("___AGENT_TOKEN".equals(c.getName())){
            token= URLDecoder.decode(c.getValue(),"utf-8");
        }
    }
    //System.out.println("token:::::"+token);
    message.setTokenStr(token);

    message.getContent().put("_areaValue",request.getParameter("_areaValue"));
    message.getContent().put("_createTime",request.getParameter("_createTime"));
    message.getContent().put("_search",request.getParameter("_search"));
    message.getContent().put("_tradeType",request.getParameter("_tradeType"));
    message.getContent().put("_orderNo",request.getParameter("_orderNo"));
    message.getContent().put("_userId",request.getParameter("_userId"));
    message.getContent().put("_notOrderType",request.getParameter("_notOrderType"));
    message.getContent().put("_notOrderStatus","0,1");
    message.getContent().put("pageNo",1);
    message.getContent().put("_orderType",request.getParameter("_orderType"));
    message.getContent().put("pageSize",9999);
//    System.out.print("--------------"+message.getContent()+"--------------");
    JSONObject trade = ServiceAccess.callService(message).getContent();
    //当前年月日
    Date d = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
    String dateNowStr = sdf.format(d);
    //文件名
    String fileName = dateNowStr+"查询交易流水.xls";
    //文件保存路径
    // 第一步，创建一个webbook，对应一个Excel文件
    HSSFWorkbook wb = new HSSFWorkbook();
    // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
    HSSFSheet sheet = wb.createSheet(fileName);
    // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
    HSSFRow row = sheet.createRow((int) 0);
    // 第四步，创建单元格，并设置值表头 设置表头居中
    HSSFCellStyle style = wb.createCellStyle();
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

    HSSFCell cell = row.createCell((short) 0);
    cell.setCellValue("归属");
    cell.setCellStyle(style);
    cell = row.createCell((short) 1);
    cell.setCellValue("订单编号");
    cell.setCellStyle(style);
    cell = row.createCell((short) 2);
    cell.setCellValue("交易类型");
    cell.setCellStyle(style);
    cell = row.createCell((short) 3);
    cell.setCellValue("付款人");
    cell.setCellStyle(style);
    cell = row.createCell((short) 4);
    cell.setCellValue("收款人");
    cell.setCellStyle(style);
    cell = row.createCell((short) 5);
    cell.setCellValue("交易金额");
    cell.setCellStyle(style);
    cell = row.createCell((short) 6);
    cell.setCellValue("交易时间");
    cell.setCellStyle(style);

    // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
    List<Object> list = (List<Object>) trade.get("items");


    for (int i = 0; i < list.size(); i++)
    {
    row = sheet.createRow(i+1);
//             第四步，创建单元格，并设置值
    row.createCell((short) 0).setCellValue(getBelongArea(
            ((Map<String,Object>)list.get(i)).get("belongMember").toString(),
            ((Map<String,Object>)list.get(i)).get("belongSeller").toString(),
            ((Map<String,Object>)list.get(i)).get("belongFactor").toString(),
            ((Map<String,Object>)list.get(i)).get("cardNo").toString(),
            ((Map<String,Object>)list.get(i)).get("orderType").toString()
            ));
    row.createCell((short) 1).setCellValue(getNullText(((Map<String,Object>)list.get(i)).get("orderNo").toString()));
    row.createCell((short) 2).setCellValue(getNullText(tradeType(((Map<String,Object>)list.get(i)).get("orderType").toString())));
    row.createCell((short) 3).setCellValue(getNullText(getNameByPay(
            ((Map<String,Object>)list.get(i)).get("nameMember").toString(),
            ((Map<String,Object>)list.get(i)).get("nameSeller").toString(),
            ((Map<String,Object>)list.get(i)).get("nameFactor").toString(),
            ((Map<String,Object>)list.get(i)).get("orderType").toString()
            )));
    row.createCell((short) 4).setCellValue(getNullText(getNameByAcq(
            ((Map<String,Object>)list.get(i)).get("nameMember").toString(),
            ((Map<String,Object>)list.get(i)).get("nameMemberAcq").toString(),
            ((Map<String,Object>)list.get(i)).get("nameSeller").toString(),
            ((Map<String,Object>)list.get(i)).get("nameFactor").toString(),
            ((Map<String,Object>)list.get(i)).get("orderType").toString()
            )));
    row.createCell((short) 5).setCellValue(BigDecimalUtil.fixDoubleNumProfit(Double.valueOf(((Map<String,Object>)list.get(i)).get("payMoney").toString())));
    row.createCell((short) 6).setCellValue(sdf.format(Long.valueOf(((Map<String,Object>)list.get(i)).get("orderCreateTime").toString())));
    }

    OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
    response.setContentType("application/vnd.ms-excel;charset=utf-8");
    response.addHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName,"utf-8"));
    response.setHeader("Content-disposition","inline; filename="+URLEncoder.encode(fileName,"utf-8"));
    wb.write(toClient);
    toClient.flush();
    toClient.close();

    }
    public String tradeType(String type){
        if ("0".equals(type)) {
            return "会员扫码";
        } else if ("1".equals(type)) {
            return "现金交易";
        } else if ("2".equals(type)) {
            return "非会员扫码";
        } else if ("3".equals(type)) {
            return "商家充值";
        } else if ("4".equals(type)) {
            return "服务站充值";
        } else if ("5".equals(type)) {
            return "会员充值";
        } else if ("6".equals(type)) {
            return "会员替朋友充值";
        } else if ("7".equals(type)) {
            return "服务站激活会员卡";
        } else if ("8".equals(type)) {
            return "会员端激活会员卡";
        } else if ("9".equals(type)) {
            return "商家提现";
        } else if ("10".equals(type)) {
            return "服务站提现";
        } else if ("11".equals(type)) {
            return "会员在线购买";
        }else{
            return "";
        }
    }

    public String getNullText(String text){
        if(StringUtils.isEmpty(text) || "null".equals(text)){
            return "";
        }else{
            return text;
        }
    }
//    归属
    public String getBelongArea(String m,String s,String f,String cardNo,String orderType){
        if(Pattern.matches("^[012568]|(11)$", orderType)){
            if(StringUtils.isEmpty(m) || "null".equals(m)){
                return "平台";
            }
            return m;
        }else if(Pattern.matches("^[39]$", orderType)){
            return s;
        }else if(Pattern.matches("^(4)|(7)|(10)$", orderType)){
            return f;
        }else{
            return "";
        }
    }
//    付款人
    public String getNameByPay(String m,String s,String f,String orderType){
        if(Pattern.matches("^[01568]|(11)$", orderType)){
            return m;
        }else if(Pattern.matches("^[3]$", orderType)){
            return s;
        }else if(Pattern.matches("^[47]$", orderType)){
            return f;
        }else if(Pattern.matches("^[2]$", orderType)){
            return "非会员";
        }else if(Pattern.matches("^(9)|(10)$", orderType)){
            return "平台";
        }else{
            return "";
        }
    }
//    收款人
    public String getNameByAcq(String m,String m2,String s,String f,String orderType){
        if(Pattern.matches("^[5]$", orderType)){
            return m;
        }else if(Pattern.matches("^[6]$", orderType)){
            return m2;
        }else if(Pattern.matches("^[01239]|(11)$", orderType)){
            return s;
        }else if(Pattern.matches("^(4)|(7)|(10)$", orderType)){
            return f;
        }else if(Pattern.matches("^[78]$", orderType)){
            return "平台";
        }else{
            return "";
        }
    }

//    public double getMoney(String money){
//        String moneyStr=money.toString().split(".")[1];
//        if(!StringUtils.isEmpty(moneyStr) && moneyStr.length()>2){
//            if(moneyStr.substring(2,3)=="9"){
//                money=money.toFixed(2);
//            }
//        }
//        return money.toString().replace(/([0-9]+.[0-9]{2})[0-9]*/, "$1");
//        return ((int)(Double.valueOf(money)*100))/100.0;
//    }

%>

<%
    createTradeExcelTable(request,response);
%>