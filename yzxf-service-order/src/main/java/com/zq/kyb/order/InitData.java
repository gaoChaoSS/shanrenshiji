package com.zq.kyb.order;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xiaoke on 2017/9/11.
 */
public class InitData {


    private static String pathPrefix = "/Users/zq2014/Downloads";

    private static StylesTable stylesTable;

    /**
     * 生成服务站/发卡点
     */
    public static void tempCreateFactor() throws Exception {
        clearTable("Factor");
        String filePath = pathPrefix + "/data/服务中心、创业合伙人资料2017.9.6(1).xlsx";
        String values = null;
        try {
            InputStream is = new FileInputStream(filePath);
            // 构造 XSSFWorkbook 对象，strPath 传入文件路径
            XSSFWorkbook xwb = new XSSFWorkbook(is);
            // 读取第一章表格内容
            XSSFSheet sheet = xwb.getSheetAt(0);
            // 定义 row、cell
            XSSFRow row;
            String cell;
            // 循环输出表格中的内容
            for (int i = sheet.getFirstRowNum() + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                String factorId = generateFactorNo();
                String pname = row.getCell(2).getStringCellValue().trim();
                String name = row.getCell(3).getStringCellValue().trim();
                String contactPerson = row.getCell(4).getStringCellValue().trim();
                String mobile = row.getCell(5).getStringCellValue().trim();
                String area = row.getCell(6).getStringCellValue().trim();
                String address = row.getCell(7).getStringCellValue().trim();
                String legalPerson = row.getCell(8).getStringCellValue().trim();
                String realCard = row.getCell(9).getStringCellValue().trim();
                String bankId = row.getCell(10).getStringCellValue().trim();
                String bankName = row.getCell(11).getStringCellValue().trim();
                String bankUser = row.getCell(12).getStringCellValue().trim();
                String pid = getParentId(pname, "4");
                String areaValue = getParentList(pid, factorId);
                String field = "insert into Factor (" +
                        "_id" +
                        ",canUse" +
                        ",createTime" +
                        ",mobile" +
                        ",level" +
                        ",pid" +
                        ",name" +
                        ",contactPerson" +
                        ",area" +
                        ",areaValue" +
                        ",address" +
                        ",legalPerson" +
                        ",realCard" +
                        ",bankId" +
                        ",bankName" +
                        ",bankUser" +
                        ",belongArea" +
                        ") values (" +
                        "'" + factorId + "'," +
                        "true," +
                        "'" + System.currentTimeMillis() + "'," +
                        "'" + mobile + "'," +
                        "'5'," +
                        "'" + pid + "'," +
                        "'" + name + "'," +
                        "'" + contactPerson + "'," +
                        "'" + area + "'," +
                        "'" + areaValue + "'," +
                        "'" + address + "'," +
                        "'" + legalPerson + "'," +
                        "'" + realCard + "'," +
                        "'" + bankId + "'," +
                        "'" + bankName + "'," +
                        "'" + bankUser + "'," +
                        "'" + pname + "'" +
                        ")";
//                System.out.println(field);
                MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(field);
            }
            MysqlDaoImpl.commit();
            MysqlDaoImpl.clearContext();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 创建代理商
     */
    public static void tempCreateAgent() throws Exception {
        clearTable("Agent");
        String filePath = pathPrefix + "/data/服务中心、创业合伙人资料2017.9.6(1).xlsx";
        String values = null;
        System.out.println("================ 开始创建 Agent ===============");
        try {
            InputStream is = new FileInputStream(filePath);
            XSSFWorkbook xwb = new XSSFWorkbook(is);
            XSSFSheet sheet = xwb.getSheetAt(1);
            XSSFRow row;
            for (int i = sheet.getFirstRowNum() + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                String type = row.getCell(3).getStringCellValue();
                String parent = row.getCell(1).getStringCellValue();
                String name = row.getCell(2).getStringCellValue();
                String legalPerson = row.getCell(4).getStringCellValue();
                String mobile = row.getCell(5).getStringCellValue();
                String area = row.getCell(6).getStringCellValue();
                String address = row.getCell(7).getStringCellValue();
                String contactPerson = row.getCell(8).getStringCellValue();
                String realCard = row.getCell(9).getStringCellValue();
                String businessLicense = row.getCell(10).getStringCellValue();
                String bankId = row.getCell(11).getStringCellValue();
                String bankName = row.getCell(12).getStringCellValue();
                String bankUser = row.getCell(13).getStringCellValue();
                createAgentToDb(type, parent, name, legalPerson, mobile, area, address, contactPerson, realCard,
                        businessLicense, bankId, bankName, bankUser);
            }
            MysqlDaoImpl.commit();
            MysqlDaoImpl.clearContext();
            is.close();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static void createAgentToDb(String type, String parent, String name,
                                        String legalPerson, String mobile, String area,
                                        String address, String contactPerson, String realCard,
                                        String businessLicense, String bankId, String bankName,
                                        String bankUser) throws Exception {
        String pid = "-1";
        //建立省级代理商
        String level = "1";
        if ("省公司".equals(type)) {
            level = "2";
            pid = getParentId(parent, "1");
        } else if ("子公司".equals(type)) {
            level = "3";
            pid = getParentId(parent, "2");
        } else if ("服务中心".equals(type)) {
            level = "4";
            pid = getParentId(parent, "3");
        }
        String id = generateAgentNo();
        String sql = "insert into Agent (" +
                "_id" +
                ",pid" +
                ",name" +
                ",level" +
                ",belongArea" +
                ",areaValue" +
                ",canUse" +
                ",createTime" +
                ",legalPerson" +
                ",phone" +
                ",area" +
                ",address" +
                ",contactPerson" +
                ",realCard" +
                ",businessLicense" +
                ",bankId" +
                ",bankName" +
                ",bankUser" +
                ") values (";
        String field = "'" + id + "'" +
                ",'" + pid + "'" +
                ",'" + name + "'" +
                ",'" + level + "'" +
                ",'" + parent + "'" +
                ",'" + getParentList(pid, id) + "'" +
                ",true" +
                ",'" + System.currentTimeMillis() + "'" +
                ",'" + legalPerson + "'" +
                ",'" + mobile + "'" +
                ",'" + area + "'" +
                ",'" + address + "'" +
                ",'" + contactPerson + "'" +
                ",'" + realCard + "'" +
                ",'" + businessLicense + "'" +
                ",'" + bankId + "'" +
                ",'" + bankName + "'" +
                ",'" + bankUser + "'" +
                ");";
//        System.out.println("agent表:" + sql + field);
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql + field);
    }

    static int agentNo = 1;

    private static String generateAgentNo() {
        String no = null;
        if (agentNo < 10) {
            no = "A-00000" + agentNo;
        } else if (agentNo < 100) {
            no = "A-0000" + agentNo;
        } else if (agentNo < 1000) {
            no = "A-000" + agentNo;
        }
        agentNo++;
        return no;
    }

    static int factorNo = 1;

    private static String generateFactorNo() {
        String no = null;
        if (factorNo < 10) {
            no = "F-00000" + factorNo;
        } else if (factorNo < 100) {
            no = "F-0000" + factorNo;
        } else if (factorNo < 1000) {
            no = "F-000" + factorNo;
        }
        factorNo++;
        return no;
    }


    static int sellerNo = 1;

    private static String generateSellerNo() {
        String no = null;
        if (sellerNo < 10) {
            no = "S-00000" + sellerNo;
        } else if (sellerNo < 100) {
            no = "S-0000" + sellerNo;
        } else if (sellerNo < 1000) {
            no = "S-000" + sellerNo;
        } else if (sellerNo < 10000) {
            no = "S-00" + sellerNo;
        }
        sellerNo++;
        return no;
    }

    private static String getParentList(String pid, String id) throws Exception {
        StringBuffer sb = new StringBuffer("_");
        if (pid.equals("-1")) {
            return "_A-000001_";
        }
        sb.insert(1, id + "_");
        if (!pid.equals("A-000001")) {
            sb.insert(1, pid + "_");
        }
        while (true) {
            String sql = "select pid from Agent where _id = '" + pid + "'";
            try {
                ResultSet rs =
                        MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(sql);
                if (rs.next()) {
                    if (pid.equals("A-000001")) {
                        sb.insert(1, "A-000001_");
                        break;
                    }
                    String ppid = rs.getString("pid");
                    if (StringUtils.isEmpty(ppid)) {
                        break;
                    } else {
                        if (ppid.equals("A-000001")) {
                            pid = ppid;
                            continue;
                        }
                        sb.insert(1, ppid + "_");
                        pid = ppid;
                        continue;
                    }
                }
            } catch (Exception ex) {
                throw ex;
            }
        }
        return sb.toString();
    }

    private static String[] getFactorId(String parent) throws Exception {
        if (StringUtils.isEmpty(parent)) return null;
        String sql = "select _id,pid from Factor where name = '" + parent.trim() + "'";
        try {
            ResultSet rs =
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(sql);
            if (rs.next()) {
                return new String[]{
                        rs.getString("_id"),
                        rs.getString("pid")
                };
            } else {
                throw new Exception("not found factor " + parent);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static String getParentId(String parent, String level) throws Exception {
        if (StringUtils.isEmpty(parent)) return "-1";
        String sql = "select _id from Agent where name = '" + parent.trim() + "' and level = '" + level + "'";
        try {
            ResultSet rs =
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(sql);
            if (rs.next()) {
                return rs.getString("_id");
            } else {
                throw new Exception("not found " + parent + "(" + level + ")");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static String generateUserNo() throws Exception {
        while (true) {
            String loginName = RandomStringUtils.random(8, "1234567890");
            ResultSet rs = MysqlDaoImpl.getInstance().getConn().createStatement()
                    .executeQuery("select loginName from  User where loginName = '" + loginName + "'");
            if (!rs.next()) {
                rs.close();
                return loginName;
            }
        }
    }

    /**
     * 创建用户表
     */
    public static void tempCreateUser() throws Exception {
        clearTable("User");
        createAgentUser();
        createFactorUser();
        createSellerUser();
    }

    private static void createSellerUser() throws Exception {
        System.out.println("================ 开始创建 Seller User ===============");
        //创建 seller
        String selectSeller = "select _id,name from Seller";
        ResultSet rs = MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectSeller);
        while (rs.next()) {
            String loginName = generateUserNo();
            String id = UUID.randomUUID().toString();
            String user = " insert into User (" +
                    "_id" +
                    ",canUse" +
                    ",sellerId" +
                    ",createTime" +
                    ",password" +
                    ",isSellerAdmin" +
                    ",loginName" +
                    ") values (" +
                    "'" + id + "'," +
                    "true," +
                    "'" + rs.getString(1) + "'," +
                    "'" + System.currentTimeMillis() + "'," +
                    "'" + MessageDigestUtils.digest("000000") + "'," +
                    "true," +
                    "'" + loginName + "')";
//            System.out.println(user);
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(user);
        }
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();

    }

    private static void createFactorUser() throws Exception {
        System.out.println("================ 开始创建 Factor User ===============");
        //创建 factor
        String selectFactor = "select _id,name from Factor";
        ResultSet rs = MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectFactor);
        while (rs.next()) {
            String loginName = generateUserNo();
            String id = UUID.randomUUID().toString();
            String user = " insert into User (" +
                    "_id" +
                    ",canUse" +
                    ",factorId" +
                    ",createTime" +
                    ",password" +
                    ",isFactorAdmin" +
                    ",loginName" +
                    ") values (" +
                    "'" + id + "'," +
                    "true," +
                    "'" + rs.getString(1) + "'," +
                    "'" + System.currentTimeMillis() + "'," +
                    "'" + MessageDigestUtils.digest("000000") + "'," +
                    "true," +
                    "'" + loginName + "')";
//            System.out.println(user);
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(user);
        }
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void createAgentUser() throws Exception {
        System.out.println("================ 开始创建 Agent User ===============");

        //创建 agent
        String selectAgent = "select _id,name from Agent";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectAgent);
        while (rs.next()) {
            String name = rs.getString(2);
            String loginName = null;
            if ("平台".equals(name)) {
                loginName = "admin";
            } else {
                loginName = generateUserNo();
            }
            String id = UUID.randomUUID().toString();
            String user = " insert into User (" +
                    "_id" +
                    ",canUse" +
                    ",agentId" +
                    ",createTime" +
                    ",password" +
                    ",loginName" +
                    ") values (" +
                    "'" + id + "'," +
                    "true," +
                    "'" + rs.getString(1) + "'," +
                    "'" + System.currentTimeMillis() + "'," +
                    "'" + MessageDigestUtils.digest("000000") + "'," +
                    "'" + loginName + "')";
//            System.out.println(user);
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(user);
        }
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void clearTable(String tableName) throws Exception {
        MysqlDaoImpl.getInstance().getConn()
                .createStatement().executeUpdate("delete from " + tableName);
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    public static void main(String args[]) throws Exception {
//        tempCreateAgent();
//        tempCreateFactor();
//        tempCreateSeller();
//        tempCreateUser();
        tempCreateMember();
        tempCreateCardFiled();
        tempAccount();
    }

    private static void tempAccount() throws Exception {
        createMemberAccount();
//        createFactorAccount();
//        createAgentAccount();
//        createSellerAccount();
    }

    private static void createSellerAccount() throws SQLException {
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from SellerMoneyAccount");
        String selectSql = "select _id from Seller";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectSql);
        while (rs.next()) {
            String sql = " insert into `sellermoneyaccount` " +
                    " ( `_id`, `createTime`, `sellerId`, `cashCount`, `cashCountUse`, `income`, `brokerageCount`, `brokerageCountTotal`, `orderCashCount`) " +
                    " values ( '" +
                    UUID.randomUUID().toString() + "', " +
                    "'" + System.currentTimeMillis() + "', " +
                    "'" + rs.getString(1) + "','0','0','0','0','0','0') ";
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql);
        }
        rs.close();
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void createAgentAccount() throws SQLException {
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from AgentMoneyAccount");
        String selectSql = "select _id from Agent";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectSql);
        while (rs.next()) {
            String sql = " insert into `AgentMoneyAccount` " +
                    " ( `_id`, `createTime`, `agentId`, `cashCountUse`, `cashCount`, `income`) " +
                    " values ( '" +
                    UUID.randomUUID().toString() + "', " +
                    "'" + System.currentTimeMillis() + "', " +
                    "'" + rs.getString(1) + "','0','0','0') ";
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql);
        }
        rs.close();
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void createFactorAccount() throws Exception {
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from FactorMoneyAccount");
        String selectSql = "select _id from Factor";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectSql);
        while (rs.next()) {
            String sql = " insert into `factormoneyaccount` " +
                    " ( `_id`, `createTime`, `cashCount`, `cashCountUse`, `factorId`, `income`) " +
                    " values ( '" +
                    UUID.randomUUID().toString() + "', " +
                    "'" + System.currentTimeMillis() + "', " +
                    "'0', '0', '" + rs.getString(1) + "', '0') ";
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql);
        }
        rs.close();
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void createMemberAccount() throws Exception {
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from MemberMoneyAccount");
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from memberpensionaccount");
        String selectSql = "select _id from Member";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectSql);
        while (rs.next()) {
            String sql = " insert into `membermoneyaccount` " +
                    " ( `_id`, `createTime`, `memberId`, `cashCountUse`, `cashCount`, " +
                    " `cashOfflineCount`, `cashOnlineCount`, `totalConsume`, `rechargeCount`) " +
                    " values ( '" +
                    UUID.randomUUID().toString() + "', " +
                    "'" + System.currentTimeMillis() + "', " +
                    "'" + rs.getString(1) + "', '0', '0', '0', '0', '0', '0') ";
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql);

            sql = " insert into `memberpensionaccount` " +
                    " (  `_id`, `createTime`, `pensionCountUse`, `pensionCount`, `insureCountUse`, `insureCount`, `memberId`) " +
                    " values ( '" +
                    UUID.randomUUID().toString() + "', " +
                    "'" + System.currentTimeMillis() + "', " +
                    "'0', '0', '0', '0', '" + rs.getString(1) + "') ";
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql);

        }
        rs.close();
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void tempCreateCardFiled() throws Exception {

        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from cardfield");
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate("delete from membercard");

        //创建 agent
        String selectMember = "select _id,cardNo,belongArea from Member where isBindCard = true";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(selectMember);

        while (rs.next()) {
            String factor = rs.getString(3);
            String memberId = rs.getString(1);
            String cardNo = rs.getString(2);
            String factorId = getFactorId(factor)[0];
            String id = UUID.randomUUID().toString();
            String sql = "insert into `cardfield` "
                    + " ( `_id`, `createTime`, `startCardNo`,  `cardNum`, `endCardNo`," +
                    " `grant`, `receive`) "
                    + " values" +
                    "('" + id + "'" +
                    ", " + System.currentTimeMillis() + " " +
                    ", '" + cardNo + "', " +
                    "1, " +
                    "'" + cardNo + "', " +
                    "'" + factorId + "', " +
                    "'" + memberId + "') ";
            MysqlDaoImpl.getInstance().getConn().
                    createStatement().executeUpdate(sql);
            sql = "insert into `membercard` "
                    + " ( `_id`, `createTime`, `memberCardId`, `memberId`, `factorId`, `isActive`, `activeTime`) "
                    + " values" +
                    "('" + id + "'" +
                    ", " + System.currentTimeMillis() + " " +
                    ", '" + cardNo + "', " +
                    "'" + memberId + "', " +
                    "'" + factorId + "', " +
                    "true," + System.currentTimeMillis() + " ) ";
            MysqlDaoImpl.getInstance().getConn().
                    createStatement().executeUpdate(sql);
        }
        rs.close();
        MysqlDaoImpl.commit();
        MysqlDaoImpl.clearContext();
    }

    private static void tempCreateSeller() throws Exception {
        clearTable("Seller");
        String filePath = pathPrefix + "/data/商家资料";

        File file = new File(filePath);
        for (File f : file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("~$")) return false;
                if (name.endsWith(".xlsx")) return true;
                return false;
            }
        })) {
            try {
                System.out.println(" =============== 开始创建Seller " + f.getAbsolutePath());
                InputStream is = new FileInputStream(f);
                XSSFWorkbook xwb = new XSSFWorkbook(is);
                XSSFSheet sheet = xwb.getSheetAt(1);
                XSSFRow row;
                if (sheet.getPhysicalNumberOfRows() < 5) {
                    throw new IllegalArgumentException(f.getAbsolutePath() + " 无数据");
                }
                for (int i = sheet.getFirstRowNum() + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    row = sheet.getRow(i);
//                    System.out.println(f.getAbsoluteFile() + "=============第" + i + "行" + "共 " + row.getLastCellNum() + " 列");
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        if (row.getCell(j) == null) {
                            row.createCell(j);
                            row.getCell(j).setCellValue("");
                        }
                        row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                    }
                    String parent = row.getCell(0).getStringCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    String integralRate = row.getCell(2).getStringCellValue();
                    String contactPerson = row.getCell(3).getStringCellValue();
                    String phone = row.getCell(4).getStringCellValue();
                    String area = row.getCell(5).getStringCellValue();
                    String address = row.getCell(6).getStringCellValue();
                    String legalPerson = row.getCell(7).getStringCellValue();
                    String realCard = row.getCell(8).getStringCellValue();
                    String bankId = row.getCell(9).getStringCellValue();
                    String bankName = row.getCell(10).getStringCellValue();
                    String bankUser = row.getCell(11).getStringCellValue();
                    String businessLicense = row.getCell(12).getStringCellValue();
                    String id = generateSellerNo();

                    String[] ids = getFactorId(parent);
                    String factorId = ids[0];
                    String serviceId = ids[1];

                    if (integralRate == null) {
                        integralRate = "0";
                    }
                    if (integralRate.contains("%")) {
                        integralRate = integralRate.replaceAll("%", "");
                    }
                    String sql = "insert into Seller (" +
                            "_id" +
                            ",name" +
                            ",belongArea" +
                            ",belongAreaValue" +
                            ",canUse" +
                            ",createTime" +
                            ",legalPerson" +
                            ",phone" +
                            ",area" +
                            ",address" +
                            ",contactPerson" +
                            ",realCard" +
                            ",businessLicense" +
                            ",bankId" +
                            ",bankName" +
                            ",bankUser" +
                            ",integralRate" +
                            ") values (";
                    String field = "'" + id + "'" +
                            ",'" + name + "'" +
                            ",'" + parent + "'" +
                            ",'" + getParentList(serviceId, factorId) + id + "_'" +
                            ",true" +
                            ",'" + System.currentTimeMillis() + "'" +
                            ",'" + legalPerson + "'" +
                            ",'" + phone + "'" +
                            ",'" + area + "'" +
                            ",'" + address + "'" +
                            ",'" + contactPerson + "'" +
                            ",'" + realCard + "'" +
                            ",'" + businessLicense + "'" +
                            ",'" + bankId + "'" +
                            ",'" + bankName + "'" +
                            ",'" + bankUser + "'" +
                            ",'" + integralRate + "'" +
                            ");";
//                    System.out.println(sql + field);
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(sql + field);
                }
                is.close();
                MysqlDaoImpl.commit();
            } catch (Exception ex) {
                throw ex;
            }
        }
        MysqlDaoImpl.clearContext();
    }


    /**
     * 生成会员
     */
    public static void tempCreateMember() throws Exception {
        clearTable("Member");
        String filePath = pathPrefix + "/会员资料-11-6.xlsx";
        processAllSheets(filePath);
//        filePath = pathPrefix + "/data/泸州会员资料.xlsx";
//        processAllSheets(filePath);
//        filePath = pathPrefix + "/data/凉山、德阳、绵阳、广元、达州会员资料(1).xlsx";
//        processAllSheets(filePath);
        MysqlDaoImpl.clearContext();
//        processOtherMember();
    }

    private static void processOtherMember() throws Exception {
        String filePath = pathPrefix + "/data/免费会员";
        File file = new File(filePath);
        for (File f : file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("~$")) return false;
                if (name.endsWith(".xlsx")) return true;
                return false;
            }
        })) {
            try {
                System.out.println("创建其它免费会员 ======= " + f.getAbsolutePath() + "");

                InputStream is = new FileInputStream(f);
                XSSFWorkbook xwb = new XSSFWorkbook(is);
                XSSFSheet sheet = xwb.getSheetAt(0);
                XSSFRow row;
                if (sheet.getPhysicalNumberOfRows() < 2) {
                    throw new IllegalArgumentException(f.getAbsolutePath() + " 无数据");
                }
                for (int i = sheet.getFirstRowNum() + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    row = sheet.getRow(i);
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        if (row.getCell(j) == null) {
                            row.createCell(j);
                            row.getCell(j).setCellValue("");
                        }
                        row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                    }
                    String factor = row.getCell(0).getStringCellValue().trim();
                    String mobile = row.getCell(1).getStringCellValue().trim();
                    String createDateStr = row.getCell(2).getStringCellValue().trim();
                    if (StringUtils.isEmpty(factor)) {
                        continue;
                    }
//                    System.out.println(f.getAbsoluteFile() + "=============第" + i + "行" + "共 " + row.getLastCellNum() + " 列");
                    Date d = parseDate(createDateStr);

                    if (factor.contains("创业合伙人") && !factor.startsWith("创业合伙人")) {
                        factor = factor.substring(factor.indexOf("创业合伙人"));
                    }
                    String sql = "select mobile from Member where mobile = '" + mobile + "'";
                    ResultSet rs =
                            MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(sql);
                    if (rs.next()) {
                        rs.close();
                        System.out.println("电话号码已存在,跳过 " + mobile);
                        continue;
                    }
                    rs.close();
                    sql = "select _id,name,areaValue from Factor where name = '" + factor + "'";
                    rs =
                            MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(sql);
                    String belongAreaValue = null;
                    if (!rs.next()) {
                        rs.close();
                        throw new RuntimeException("======== 未找到 factor " + factor);
                    } else {
                        belongAreaValue = rs.getString(3);
                    }
                    rs.close();
                    String id = generateMemberNo();

                    String field = "insert into Member (" +
                            "_id" +
                            ",belongArea" +
                            ",belongAreaValue" +
                            ",password" +
                            ",canUse" +
                            ",isBindCard" +
                            ",isRealName" +
                            ",mobile" +
                            ",createTime" +
                            ") values (" +
                            "'" + id + "'," +
                            "'" + factor + "'," +
                            "'" + belongAreaValue + "'," +
                            "'" + MessageDigestUtils.digest("000000") + "'," +
                            "true," +
                            "false," +
                            "false,";
                    field += "'" + mobile + "',";
                    field += "'" + d.getTime() + "',";
                    field = field.substring(0, field.length() - 1) + ");";
//                    System.out.println(field);
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(field);
                }
                is.close();
                MysqlDaoImpl.commit();
            } catch (Exception ex) {
                throw ex;
            }
        }
        MysqlDaoImpl.clearContext();
    }

    private static void createMemberToDB(String rowData) throws Exception {
        String[] datas = rowData.split(",");
        if (datas.length < 5) return;
        String realName = datas[2];
        String mobile = datas[4];
        String idCard = datas[3];
        String cardNo = datas[5];
        String factor = datas[1];
        factor = factor.replaceAll(" ","");
        String createDate = datas[6];
        if (cardNo.length() != 16) {
            cardNo = null;
        }
        if("注册时间".equals(createDate)){
            return;
        }
        String sql = "select mobile from Member where mobile = '" + mobile + "'";
        ResultSet rs =
                MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery(sql);
        if (rs.next()) {
            rs.close();
            System.out.println("电话号码已存在,跳过 " + mobile);
            return;
        }
        rs.close();
        Date d = parseDate(createDate);
        String belongAreaValue = null;
        int count =0;
        while(true){
            System.out.println("======== 开始搜索 factor " + factor);

             rs =
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeQuery("select _id,name,areaValue from Factor where name = '" +
                            factor + "'");
            if (!rs.next()) {
                count++;
                if(factor.contains("创业合伙人")) {
                    factor = factor.replaceAll("创业合伙人", "");
                }else if(factor.contains("服务中心")) {
                    factor = factor.replaceAll("服务中心","区组织发卡点");
                }else if(factor.contains("服务中心")) {
                    factor = "创业合伙人"+factor;
                }

                rs.close();
                if(count==1){
                    continue;
                }
                throw new RuntimeException("======== 未找到 factor " + factor);
            } else {
                belongAreaValue = rs.getString(3);
                rs.close();
                break;
            }
        }

        String id = generateMemberNo();
        boolean isBindCard = cardNo == null ? false : true;
        String field = "insert into Member (" +
                "_id" +
                ",belongArea" +
                ",belongAreaValue" +
                ",password" +
                ",canUse" +
                ",isBindCard" +
                ",isRealName" +
                ",realName" +
                ",mobile" +
                ",idCard" +
                ",cardNo" +
                ",createTime" +
                ") values (" +
                "'" + id + "'," +
                "'" + factor + "'," +
                "'" + belongAreaValue + "'," +
                "'" + MessageDigestUtils.digest("000000") + "'," +
                "true," +
                isBindCard + "," +
                isBindCard + ",";
        field += "'" + realName + "',";
        field += "'" + mobile + "',";
        field += "'" + idCard + "',";
        field += "'" + cardNo + "',";
        field += "'" + d.getTime() + "',";
        field = field.substring(0, field.length() - 1) + ");";
//        System.out.println(field);
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(field);
        MysqlDaoImpl.commit();
    }

    static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
    static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/");
    static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    static SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Date parseDate(String createDate) throws Exception {
        if (createDate.startsWith(" ")) {
            createDate = createDate.replaceAll(" ", "");
        }
        try {
            return sdf1.parse(createDate);
        } catch (Exception ex) {

        }
        try {
            return sdf2.parse(createDate);
        } catch (Exception ex) {

        }
        try {
            return sdf3.parse(createDate);
        } catch (Exception ex) {

        }
        try {
            return sdf4.parse(createDate);
        } catch (Exception ex) {

        }
        try {
            return sdf5.parse(createDate);
        } catch (Exception ex) {

        }
        try {
            return sdf6.parse(createDate);
        } catch (Exception ex) {

        }
        try {
            if (createDate.indexOf(".") != -1) {
                createDate = createDate.split("\\.")[0];
            }
            Integer day = Integer.valueOf(createDate);
            Date startDate = sdf1.parse("1900-01-01");
            return DateUtils.addDays(startDate, day - 2);
        } catch (Exception ex) {

        }
        throw new RuntimeException("====================无法解析日期" + createDate);
    }


    static int memberNo = 23246;

    private static String generateMemberNo() {
        String no = null;
        if (memberNo < 10) {
            no = "M-00000" + memberNo;
        } else if (memberNo < 100) {
            no = "M-0000" + memberNo;
        } else if (memberNo < 1000) {
            no = "M-000" + memberNo;
        } else if (memberNo < 10000) {
            no = "M-00" + memberNo;
        } else if (memberNo < 100000) {
            no = "M-0" + memberNo;
        }
        memberNo++;
        return no;
    }

    /**
     * 处理所有sheet
     *
     * @param filename
     * @throws Exception
     */
    public static void processAllSheets(String filename) throws Exception {
        System.out.println("==============开始处理文件" + filename);
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        Iterator<InputStream> sheets = r.getSheetsData();
        stylesTable = r.getStylesTable();

        while (sheets.hasNext()) {
            System.out.println("================ Processing new sheet:\n");
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
    }

    /**
     * 获取解析器
     *
     * @param sst
     * @return
     * @throws SAXException
     */
    public static XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "org.apache.xerces.parsers.SAXParser"
                );
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * 自定义解析处理器
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private static class SheetHandler extends DefaultHandler {

        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;

        private List<String> rowlist = new ArrayList<String>();
        private int curRow = 0;
        private int curCol = 0;

        //定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
        private String preRef = null, ref = null;
        //定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格
        private String maxRef = null;

        private CellDataType nextDataType = CellDataType.SSTINDEX;
        private final DataFormatter formatter = new DataFormatter();
        private short formatIndex;
        private String formatString;

        //用一个enum表示单元格可能的数据类型
        enum CellDataType {
            BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
        }

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        /**
         * 解析一个element的开始时触发事件
         */
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell
            if (name.equals("c")) {
                //前一个单元格的位置
                if (preRef == null) {
                    preRef = attributes.getValue("r");
                } else {
                    preRef = ref;
                }
                //当前单元格的位置
                ref = attributes.getValue("r");

                this.setNextDataType(attributes);

                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                if (cellType != null && cellType.equals("s")) {
                    nextIsString = true;
                } else {
                    nextIsString = false;
                }

            }
            // Clear contents cache
            lastContents = "";
        }

        /**
         * 根据element属性设置数据类型
         *
         * @param attributes
         */
        public void setNextDataType(Attributes attributes) {

            nextDataType = CellDataType.NUMBER;
            formatIndex = -1;
            formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType)) {
                nextDataType = CellDataType.BOOL;
            } else if ("e".equals(cellType)) {
                nextDataType = CellDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                nextDataType = CellDataType.INLINESTR;
            } else if ("s".equals(cellType)) {
                nextDataType = CellDataType.SSTINDEX;
            } else if ("str".equals(cellType)) {
                nextDataType = CellDataType.FORMULA;
            }
            if (cellStyleStr != null) {
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                formatIndex = style.getDataFormat();
                formatString = style.getDataFormatString();
                if ("m/d/yy" == formatString) {
                    nextDataType = CellDataType.DATE;
                    //full format is "yyyy-MM-dd hh:mm:ss.SSS";
                    formatString = "yyyy-MM-dd";
                }
                if (formatString == null) {
                    nextDataType = CellDataType.NULL;
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
            }
        }


        /**
         * 解析一个element元素结束时触发事件
         */
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if (nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if (name.equals("v")) {
                String value = this.getDataValue(lastContents.trim(), "");
                //补全单元格之间的空单元格
                if (!ref.equals(preRef)) {
                    if (preRef != null) {
                        int len = countNullCell(ref, preRef);
                        for (int i = 0; i < len; i++) {
                            rowlist.add(curCol, "");
                            curCol++;
                        }
                    }
                }
                rowlist.add(curCol, value);
                curCol++;
            } else {
                //如果标签名称为 row，这说明已到行尾，调用 optRows() 方法
                if (name.equals("row")) {
                    String value = "";
                    //默认第一行为表头，以该行单元格数目为最大数目
                    if (curRow == 0) {
                        maxRef = ref;
                    }
                    //补全一行尾部可能缺失的单元格
                    if (maxRef != null) {
                        if (preRef != null) {
                            int len = countNullCell(maxRef, ref);
                            for (int i = 0; i <= len; i++) {
                                rowlist.add(curCol, "");
                                curCol++;
                            }
                        }
                    }
                    //拼接一行的数据
                    for (int i = 0; i < rowlist.size(); i++) {
                        if (rowlist.get(i).contains(",")) {
                            value += "\"" + rowlist.get(i) + "\",";
                        } else {
                            value += rowlist.get(i) + ",";
                        }
                    }
                    //加换行符
//                    value += "\n";
//                    try {
//                        writer.write(value);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    curRow++;
                    //一行的末尾重置一些数据
                    rowlist.clear();
                    curCol = 0;
                    preRef = null;
                    ref = null;
                    if (!value.startsWith("序号") && !StringUtils.isEmpty(value)) {
                        try {
//                            System.out.println(value);
                            createMemberToDB(value);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        /**
         * 根据数据类型获取数据
         *
         * @param value
         * @param thisStr
         * @return
         */
        public String getDataValue(String value, String thisStr)

        {
            switch (nextDataType) {
                //这几个的顺序不能随便交换，交换了很可能会导致数据错误
                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    thisStr = '"' + value.toString() + '"';
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisStr = rtsi.toString();
                    rtsi = null;
                    break;
                case SSTINDEX:
                    String sstIndex = value.toString();
                    thisStr = value.toString();
                    break;
                case NUMBER:
                    if (formatString != null) {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                    } else {
                        thisStr = value;
                    }
                    thisStr = thisStr.replace("_", "").trim();
                    break;
                case DATE:
                    try {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                    } catch (NumberFormatException ex) {
                        thisStr = value.toString();
                    }
                    thisStr = thisStr.replace(" ", "");
                    break;
                default:
                    thisStr = "";
                    break;
            }
            return thisStr;
        }

        /**
         * 获取element的文本数据
         */
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            lastContents += new String(ch, start, length);
        }

        /**
         * 计算两个单元格之间的单元格数目(同一行)
         *
         * @param ref
         * @param preRef
         * @return
         */
        public int countNullCell(String ref, String preRef) {
            //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
            String xfd = ref.replaceAll("\\d+", "");
            String xfd_1 = preRef.replaceAll("\\d+", "");

            xfd = fillChar(xfd, 3, '@', true);
            xfd_1 = fillChar(xfd_1, 3, '@', true);

            char[] letter = xfd.toCharArray();
            char[] letter_1 = xfd_1.toCharArray();
            int res = (letter[0] - letter_1[0]) * 26 * 26 + (letter[1] - letter_1[1]) * 26 + (letter[2] - letter_1[2]);
            return res - 1;
        }

        /**
         * 字符串的填充
         *
         * @param str
         * @param len
         * @param let
         * @param isPre
         * @return
         */
        String fillChar(String str, int len, char let, boolean isPre) {
            int len_1 = str.length();
            if (len_1 < len) {
                if (isPre) {
                    for (int i = 0; i < (len - len_1); i++) {
                        str = let + str;
                    }
                } else {
                    for (int i = 0; i < (len - len_1); i++) {
                        str = str + let;
                    }
                }
            }
            return str;
        }
    }

}
