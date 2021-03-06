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
    Message message = Message.newReqMessage("1:GET@/order/OrderInfo/getMemberAddCount");
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
    message.getContent().put("pageNo",1);
    message.getContent().put("pageSize",9999);
//    System.out.print("--------------"+message.getContent()+"--------------");
    JSONObject trade = ServiceAccess.callService(message).getContent();
    //当前年月日
    Date d = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
    String dateNowStr = sdf.format(d);
    //文件名
    String fileName = dateNowStr+"-会员统计.xls";
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
    cell.setCellValue("服务中心名称");
    cell.setCellStyle(style);
    cell = row.createCell((short) 2);
    cell.setCellValue("会员总数");
    cell.setCellStyle(style);

    // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
    List<Object> list = (List<Object>) trade.get("items");


    for (int i = 0; i < list.size(); i++) {
        Map<String,Object> item = (Map<String,Object>)list.get(i);

        row = sheet.createRow(i+1);
//             第四步，创建单元格，并设置值
        row.createCell((short) 0).setCellValue(getNullText(item.get("agentNameAll")));
        row.createCell((short) 1).setCellValue(getFactorName(getNullText(item.get("name"))));
        row.createCell((short) 2).setCellValue(getNullText(item.get("count")));
    }

    OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
    response.setContentType("application/vnd.ms-excel;charset=utf-8");
    response.addHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName,"utf-8"));
    response.setHeader("Content-disposition","inline; filename="+URLEncoder.encode(fileName,"utf-8"));
    wb.write(toClient);
    toClient.flush();
    toClient.close();

}
    public String getFactorName(String name){
        if(StringUtils.isEmpty(name)){
            return "普惠生活-平台";
        }
        return name;
    }

    public String getNullText(Object text){
        if(text!=null){
            return text.toString();
        }
        return "";
    }

%>

<%
    createTradeExcelTable(request,response);
%>