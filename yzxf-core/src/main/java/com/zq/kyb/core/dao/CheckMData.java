package com.zq.kyb.core.dao;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.FileExecuteUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

/**
 */
public class CheckMData {


    //delete from MData where pid is null or pid='';
    //update MData set modelName=name,entityName=null where level=0;
    //update MData t1,MData t2 set t1.modelName=t2.name where t1.pid=t2._id and t1.level=1;
    //update MData t1,MData t2 set t1.modelName=t2.modelName where t1.pid=t2._id and t1.level=2;

    //update MData set entityName=name where level=1;
    //update MData t1,MData t2 set t1.entityName=t2.name where t1.pid=t2._id and t1.level=2;


    public static String fields = "_id,name,title,`desc`,pid,level,modelName,entityName,sellerOwner,storeOwner" +
            ",isSyncTable,createTime,type,maxLength,minLength,inputType,inputTypeList,readOnly,isNotNull,setByServer,tableType,_linkTable";

    /**
     * 查询模块下的表定义
     *
     * @param moduleName
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getMDataByModule(String moduleName) throws Exception {
        return getMDataByModule(moduleName, null);
    }

    public static List<Map<String, Object>> getMDataByModule(String moduleName, String ownerField) throws Exception {
        String ownerFieldSql = StringUtils.isEmpty(ownerField) ? "" : (" and " + ownerField + "=1");
        String sql = "select " + fields +
                " from MData" +
                " where level=1 and pid=" +
                "(select _id from MData where name=? and level=0)" + ownerFieldSql + " order by sortNo desc";
        List<Object> p = new ArrayList<>();
        p.add(moduleName);
        List<String> re = new ArrayList<>();
        for (String f : fields.split(",")) {
            re.add(f.replaceAll("`", ""));
        }

        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, re, p);
        putFieldList(list);
        return list;
    }

    private static void putFieldList(List<Map<String, Object>> list) throws Exception {
        if (!Dao.entityMap.containsKey("MData")) {
            CheckMData.putMDataAllToCacheBySql();
        }

        for (Map<String, Object> map : list) {
            //查询表的所有字段,然后封装
            Map<String, Object> params = new HashMap<>();
            params.put("pid", map.get("_id"));
            Map<String, Object> orderBy = new HashMap<>();
            orderBy.put("sortNo", -1);
            List<Map<String, Object>> mdataFields = MysqlDaoImpl.getInstance().findAll2Map("MData", params, orderBy, null, null);
            map.put("fieldList", mdataFields);
        }
    }

    /**
     * 查询拥有SellerOwner 的表的模块
     *
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getModuleListBySellerOwner(String ownerField) throws Exception {
        String sql = "select t1._id as _id,t1.name as name,count(t1._id) as entityCount" +
                " from MData t1" +
                " left join MData t2 on t1._id=t2.pid" +
                " where t1.level=0 and t2." + ownerField + "=1" +
                " group by t1._id";

        List<String> re = new ArrayList<>();
        re.add("_id");
        re.add("name");
        re.add("entityCount");

        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, re, null);
        putFieldList(list);
        return list;
    }

    /**
     * 获取单个实体定义
     *
     * @param entityName
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getMDataByEntity(String entityName) throws Exception {
        String sql = "select " + fields + " from MData where level=1 and name=?";
        List<String> re = new ArrayList<>();
        for (String f : fields.split(",")) {
            re.add(f.replaceAll("`", ""));
        }
        List<Object> p = new ArrayList<>();
        p.add(entityName);
        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, re, p);
        putFieldList(list);
        return list;
    }


    /**
     * 添加字段的MData定义到内存
     *
     * @param mdataField
     */

    public static synchronized void putMDataFieldToCache(Map mdataField) {
        // Logger.getLogger(CheckMData.class).info("---:" + mdataField.get("entityName"));
        String tableName = (String) mdataField.get("entityName");
        String fieldName = (String) mdataField.get("name");
//        if (tableName.equals("OrderPay")) {
//            Logger.getLogger(CheckMData.class).info(fieldName);
//        }
        Dao.fieldMap.put(tableName + "_" + fieldName, mdataField);
        Logger.getLogger(CheckMData.class).info(JSONObject.fromObject(mdataField));
        if (!Dao.entityFieldsMap.containsKey(tableName)) {
            Dao.entityFieldsMap.put(tableName, new ArrayList<>());
        }
        List<Object> list = Dao.entityFieldsMap.get(tableName);
        int i = 0;
        boolean exist = false;
        for (Object object : list) {
            Map<String, Object> m = (Map<String, Object>) object;
            if (m.get("name").equals(mdataField.get("name"))) {
                list.set(i, mdataField);
                exist = true;
                break;
            }
            i++;
        }
        if (!exist) {
            list.add(mdataField);
        }
    }

    /**
     * 从数据库中获取MData表的定义,将其放入到内存中, 仅需要中央服务器启动时调用.
     *
     * @throws Exception
     */
    public static synchronized void putMDataAllToCacheBySql() throws Exception {
        Dao.entityMap.remove("MData");
        //Dao.fieldMap.clear();
        Dao.entityFieldsMap.remove("MData");

        //cache tables;
        List<String> fli = getBaseMDataFiled();
        //查询当前模块的所有表
        String sql1 = "select * from " + Dao.getFullTableName(Constants.mainDB, "MData") + " where (pid=(select _id from MData where name=? and level=0) or name='MData') and level=1";
        List<Object> p = new ArrayList<>();
        p.add(Constants.moduleName);
        List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql1, fli, p);
        // .findAll2Map("MData", null, null, fields, FieldStrategy.Include);
        for (Map<String, Object> map : list) {
            String name = (String) map.get("name");
            Dao.entityMap.put(name, map);// 将表的定义添加到内存
        }

        //cache fields;
        sql1 = "select * from " + Dao.getFullTableName(Constants.mainDB, "MData") + " where (modelName=? or entityName='MData') and level=2 order by sortNo desc";
        list = MysqlDaoImpl.getInstance().queryBySql(sql1, fli, p);
        for (Map<String, Object> map : list) {
            putMDataFieldToCache(map);// 将字段的定义添加到内存
        }
    }

    /**
     * 除中央服务器以外的服务器调用,他们通过中央服务器获取到的MData数据,然后通过调用该方法加入到cache,两种情况下使用:<br/>
     * 1) 启动服务器时同步<br/>
     * 2) 中央服务器主动通知元数据发生变化<br/>
     *
     * @param list
     */
    public static void putMDataAllToCacheByList(List<?> list, Boolean isClear) throws IOException {
        if (isClear) {
            Dao.entityMap.clear();
            Dao.fieldMap.clear();
            Dao.entityFieldsMap.clear();

            //将LinkTable等基础表等字段加载到内存里
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("baseMData.json");
            StringBuffer jsonStr = FileExecuteUtils.getInstance().readInputStream(resourceAsStream, "utf-8");
            JSONArray li = JSONArray.fromObject(jsonStr.toString());
            for (Object o : li) {
                putMDataTableToCache((JSONObject) o);
            }
        }

        for (Object o : list) {
            Map<String, Object> map = (Map<String, Object>) o;
            putMDataTableToCache(map);
        }
    }

    public static void putMDataTableToCache(Map<String, Object> map) {
        String tableName = (String) map.get("name");
        List fieldList = (List) map.get("fieldList");
        if (fieldList != null && fieldList.size() > 0) {
            for (Object oo : fieldList) {
                Map m = (Map) oo;
                m.put("entityName", tableName);
                putMDataFieldToCache(m);
            }
        }
        //map.remove("fieldList");
        Map<String, Object> entity = new HashMap<>();
        entity.putAll(map);
        entity.remove("fieldList");
        Dao.entityMap.put(tableName, map);// 将表的定义添加到内存
    }


    /**
     * 从表MData中level=2 and entityName='MData'获取元数据的原始字段名
     *
     * @return
     * @throws SQLException
     */
    private static List<String> getBaseMDataFiled() throws SQLException {
        String sql = "select name from " + Dao.getFullTableName(Constants.mainDB, "MData") + " where level=2 and entityName='MData' order by sortNo desc";
        List<String> f = new ArrayList<>();
        f.add("name");
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(sql, f, null);
        List<String> fli = new ArrayList<>();
        for (Map<String, Object> map : li) {
            fli.add((String) map.get("name"));
        }
        String[] fields = new String[fli.size()];
        fli.toArray(fields);
        return fli;
    }

    static String regex = "([0-9A-Za-z]+)$";

    private static void checkTableName(String name) {
        if (com.zq.kyb.util.StringUtils.isEmpty(name)) {
            return;
        }
        if (!Character.isUpperCase(name.charAt(0))) {
            throw new UserOperateException(400, "name 必须为首字母大写");
        }
        if (!name.matches(regex)) {
            throw new UserOperateException(400, "name 只能包含字母和数字");
        }
    }

    /**
     * 检查多个表的结构是否按照元数据的定义
     *
     * @throws Exception
     */
    public static void checkMysqlTableList(List<?> tableDefList) throws Exception {
        if (tableDefList != null) {
            for (Object o : tableDefList) {
                checkMysqlTable((Map<String, Object>) o);
            }
        }
    }

    /**
     * seller注册时创建表
     *
     * @param tableDefList
     * @param sellerId
     * @throws Exception
     */
    public static void checkMysqlTableListSeller(List<?> tableDefList, String sellerId) throws Exception {
        if (tableDefList != null) {
            String dbName = Dao.getSellerDBName(sellerId);
            createDBOnly(dbName);
            for (Object o : tableDefList) {
                Map<String, Object> tableDef = (Map<String, Object>) o;
                Boolean isStoreOwner = !StringUtils.mapValueIsEmpty(tableDef, "storeOwner") && (Boolean) tableDef.get("storeOwner");
                Boolean isSellerOwner = !StringUtils.mapValueIsEmpty(tableDef, "sellerOwner") && (Boolean) tableDef.get("sellerOwner");
                String tableName = (String) tableDef.get("name");
                Boolean isRename = !StringUtils.mapValueIsEmpty(tableDef, "$$reName") && (Boolean) tableDef.get("$$reName");
                if (isStoreOwner) {
                    //如果是storeOwner,则:isSellerOwner是否为true都不创表,只创建Store表,否则要冲突
                } else {
                    createOrRenameTable(dbName, tableDef, isSellerOwner, tableName, isRename);
                }
            }
        }
    }

    /**
     * store注册时创建表
     */

    public static void checkMysqlTableListStore(List<?> tableDefList, String storeId) throws Exception {
        if (tableDefList != null) {
            String dbName = Dao.getStoreDBName(storeId);
            createDBOnly(dbName);
            for (Object o : tableDefList) {
                Map<String, Object> tableDef = (Map<String, Object>) o;
                Boolean isStoreOwner = !StringUtils.mapValueIsEmpty(tableDef, "storeOwner") && (Boolean) tableDef.get("storeOwner");
                String tableName = (String) tableDef.get("name");
                Boolean isRename = !StringUtils.mapValueIsEmpty(tableDef, "$$reName") && (Boolean) tableDef.get("$$reName");
                createOrRenameTable(dbName, tableDef, isStoreOwner, tableName, isRename);
            }
        }
    }

    private static void createOrRenameTable(String dbName, Map<String, Object> tableDef, Boolean isStoreOwner, String tableName, Boolean isRename) throws Exception {
        if (isStoreOwner) {
            if (isRename) {
                String oldName = (String) tableDef.get("$$oldName");
                reNameTable(dbName, oldName, tableName);
            } else {
                createTableOnly(dbName, tableName);//建表
                checkMysqlTableFieldList(dbName, tableName, (List) tableDef.get("fieldList"));//建字段
            }
        }
    }

    static boolean isCheckDb = false;

    /**
     * 检查某一个表的结构是否按照元数据的定义
     *
     * @param tableDef
     * @throws Exception
     */
    private static void checkMysqlTable(Map<String, Object> tableDef) throws Exception {
        Boolean isStoreOwner = !StringUtils.mapValueIsEmpty(tableDef, "storeOwner") && (Boolean) tableDef.get("storeOwner");
        Boolean isSellerOwner = !StringUtils.mapValueIsEmpty(tableDef, "sellerOwner") && (Boolean) tableDef.get("sellerOwner");
        String tableName = (String) tableDef.get("name");
        if (tableName == null) {
            throw new RuntimeException("tableName[" + tableName + "] 不能为空");
        }
        checkTableName(tableName);

        Boolean isRename = !StringUtils.mapValueIsEmpty(tableDef, "$$reName") && (Boolean) tableDef.get("$$reName");

        if (isStoreOwner) {
//            JSONObject re = ServiceAccess.callService(Message.newReqMessage("1:GET@/common/StoreBase/queryAll")).getContent();
//            JSONArray items = re.getJSONArray("items");
//            if (isRename) {
//                String oldName = (String) tableDef.get("$$oldName");
//                for (Object item : items) {
//                    reNameTable(Dao.getStoreDBName(((JSONObject) item).getString("_id")), oldName, tableName);
//                }
//            } else {
//                for (Object item : items) {
//                    String dbName = Dao.getStoreDBName(((JSONObject) item).getString("_id"));
//                    if (!isCheckDb) {
//                        createDBOnly(dbName);
//                        isCheckDb = true;
//                    }
//                    createTableOnly(dbName, tableName);//建表
//                    checkMysqlTableFieldList(dbName, tableName, (List) tableDef.get("fieldList"));//建字段
//                }
//            }
        } else {
            if (isSellerOwner) {
//                JSONObject re = ServiceAccess.callService(Message.newReqMessage("1:GET@/common/SellerBase/queryAll")).getContent();
//                JSONArray items = re.getJSONArray("items");
//                if (isRename) {
//                    String oldName = (String) tableDef.get("$$oldName");
//                    for (Object item : items) {
//                        reNameTable(Dao.getSellerDBName(((JSONObject) item).getString("_id")), oldName, tableName);
//                    }
//                } else {
//                    for (Object item : items) {
//                        String dbName = Dao.getSellerDBName(((JSONObject) item).getString("_id"));
//
//                        if (!isCheckDb) {
//                            createDBOnly(dbName);
//                            isCheckDb = true;
//                        }
//                        createTableOnly(dbName, tableName);//建表
//                        checkMysqlTableFieldList(dbName, tableName, (List) tableDef.get("fieldList"));//建字段
//                    }
//                }
            } else {
                if (isRename) {
                    String oldName = (String) tableDef.get("$$oldName");
                    reNameTable(Constants.mainDB, oldName, tableName);
                } else {

                    if (!isCheckDb) {
                        createDBOnly(Constants.mainDB);
                        isCheckDb = true;
                    }
                    createTableOnly(Constants.mainDB, tableName);//建表
                    checkMysqlTableFieldList(Constants.mainDB, tableName, (List) tableDef.get("fieldList"));//建字段
                }
            }
        }
    }

    public static String DataVersion = "DataVersion";
    public static String DataTableVersion = "DataTableVersion";
    public static String DataVersionContent = "DataVersionContent";
    public static String LinkTable = "LinkTable";

    public static void createDBOnly(String dbName) throws SQLException, IOException {

        //检查数据库:
        MysqlDaoImpl.getInstance().exeSql("CREATE DATABASE IF NOT EXISTS " + dbName + " DEFAULT CHARSET utf8mb4", null, "CREATE_DB");

        //关联数据表
        String linkTableSql = "CREATE TABLE If NOT EXISTS  `" + dbName + "`.`" + LinkTable + "` (`_id` varchar(64) NOT NULL,";
        linkTableSql += "`createTime` bigint DEFAULT NULL,";
        linkTableSql += "`creator` varchar(64) DEFAULT NULL,";
        linkTableSql += "`entityName` varchar(64) DEFAULT NULL,";
        linkTableSql += "`entityField` varchar(64) DEFAULT NULL,";
        linkTableSql += "`entityId` varchar(64) DEFAULT NULL,";
        linkTableSql += "`linkModule` varchar(64) DEFAULT NULL,";
        linkTableSql += "`linkEntity` varchar(64) DEFAULT NULL,";
        linkTableSql += "`linkEntityId` varchar(64) DEFAULT NULL,";
        linkTableSql += "PRIMARY KEY (`_id`)";
        linkTableSql += ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        MysqlDaoImpl.getInstance().exeSql(linkTableSql, null, LinkTable);

        //版本控制相关表
        String dataVersionSql = "CREATE TABLE If NOT EXISTS  `" + dbName + "`.`" + DataVersion + "` (`_id` varchar(64) NOT NULL,";
        dataVersionSql += "`createTime` bigint DEFAULT NULL,";
        dataVersionSql += "`creator` varchar(64) DEFAULT NULL,";
        dataVersionSql += "`entityName` varchar(64) DEFAULT NULL,";
        dataVersionSql += "`entityId` varchar(64) DEFAULT NULL,";
        dataVersionSql += "`isDel` bit(1) DEFAULT NULL,";
        dataVersionSql += "`version` bigint DEFAULT 0,";
        dataVersionSql += "`versionRemote` bigint DEFAULT 0,";//远程版本号
        dataVersionSql += "PRIMARY KEY (`_id`)";
        dataVersionSql += ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        MysqlDaoImpl.getInstance().exeSql(dataVersionSql, null, DataVersion);

        dataVersionSql = "CREATE TABLE If NOT EXISTS  `" + dbName + "`.`" + DataTableVersion + "` (`_id` varchar(64) NOT NULL,";
        dataVersionSql += "`updateTime` bigint DEFAULT NULL,";
        dataVersionSql += "`updater` varchar(64) DEFAULT NULL,";
        dataVersionSql += "`version` bigint DEFAULT 0,";
        dataVersionSql += "`versionRemote` bigint DEFAULT 0,";//远程版本号
        dataVersionSql += "PRIMARY KEY (`_id`)";
        dataVersionSql += ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";//使用适合存储的引擎
        MysqlDaoImpl.getInstance().exeSql(dataVersionSql, null, DataTableVersion);


        dataVersionSql = "CREATE TABLE If NOT EXISTS  `" + dbName + "`.`" + DataVersionContent + "` (`_id` varchar(64) NOT NULL,";
        dataVersionSql += "`entityJson` varchar(4096) DEFAULT NULL,";
        dataVersionSql += "PRIMARY KEY (`_id`)";
        dataVersionSql += ") ENGINE=MyISAM DEFAULT CHARSET=utf8mb4";//使用适合存储的引擎
        MysqlDaoImpl.getInstance().exeSql(dataVersionSql, null, DataVersionContent);

    }

    public static void createTableOnly(String dbName, String tableName) throws Exception {


        String fullTableName = Dao.getFullTableName(dbName, tableName);
        String sql = "CREATE TABLE If NOT EXISTS " + fullTableName + " (`_id` varchar(64) NOT NULL,";
        sql += "`createTime` bigint DEFAULT NULL,";
        //sql += "`updateTime` bigint DEFAULT NULL,";
        //sql += "`creator` varchar(64) DEFAULT NULL,";
        //sql += "`status` int(11) DEFAULT NULL,";
        sql += "PRIMARY KEY (`_id`)";
        sql += ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        MysqlDaoImpl.getInstance().exeSql(sql, null, tableName, false);
    }

    public static void reNameTable(String dbName, String oldName, String name) throws SQLException {
        String fullTableName = Dao.getFullTableName(dbName, oldName);
        String sql = "rename table " + fullTableName + " to `" + name + "`";
        //MysqlDaoImpl.getInstance().exeSqlLockTable(sql, null, fullTableName);
        MysqlDaoImpl.getInstance().exeSql(sql, null, fullTableName);
    }

    /**
     * 检查一个表的所有字段是否都按照MData的定义被创建
     *
     * @param dbName
     * @param tableName
     * @param fieldList
     * @throws Exception
     */
    private static void checkMysqlTableFieldList(String dbName, String tableName, List fieldList) throws Exception {
        // 获取mysql现在存在的所有字段
        List<Map<String, Object>> mysqlFields = MysqlDaoImpl.getInstance().getColumnList(dbName, tableName);
        Map<String, Map<String, Object>> mysqlFieldMap = new HashMap<>();
        for (Map<String, Object> map : mysqlFields) {
            mysqlFieldMap.put((String) map.get("name"), map);
        }

        if (fieldList != null) {
            //List<Map<String, Object>> li = MysqlDaoImpl.getInstance().getColumnList(dbName, tableName);
//            Map<String, Map<String, Object>> dbFieldMap = new HashMap<>();
//            for (Map<String, Object> map : mysqlFields) {
//                dbFieldMap.put((String) map.get("name"), map);
//            }

            for (Object o : fieldList) {
                Map<String, Object> mdataField = (Map<String, Object>) o;
                if (StringUtils.mapValueIsEmpty(mdataField, "name")) {
                    throw new UserOperateException(400, "字段名不能为空");
                }
                String fieldName = (String) mdataField.get("name");
                if ("_id".equals(fieldName) || "_linkTable".equals(fieldName)) {
                    continue;
                }

                if (!Character.isLowerCase(fieldName.charAt(0))) {
                    throw new UserOperateException(400, "字段 [" + fieldName + "] 名第一个字母必须小写");
                }
                if (!fieldName.matches(regex)) {
                    throw new UserOperateException(400, "name: [" + fieldName + "] 只能包含字母和数字");
                }
                //Map<String, Object> mysqlField = mysqlFieldMap.get(fieldName);
                createMysqlTableField(dbName, tableName, mdataField, mysqlFieldMap.get(fieldName));
            }
        }
    }

    /**
     * 检查具体的某一个字段
     * 根据mdata的定义和现在已经存在的字段的定义来检查字段是否匹配，不匹配则添加或修改
     *
     * @param tableName
     * @param mdataField
     * @throws Exception
     */
    public static void createMysqlTableField(String dbName, String tableName, Map<String, Object> mdataField, Map<String, Object> dbField) throws Exception {

        // linkEntity,linkEntityMore需要单独处理
        String fieldName = (String) mdataField.get("name");
        String inputType = StringUtils.mapValueIsEmpty(mdataField, "inputType") ? "input" : (String) mdataField.get("inputType");

        String type = StringUtils.mapValueIsEmpty(mdataField, "type") ? "string" : (String) mdataField.get("type");
        Integer maxLength = StringUtils.mapValueIsEmpty(mdataField, "maxLength") ? 64 : Integer.valueOf(String.valueOf(mdataField.get("maxLength")));

        // ***只需定义5种数据类型：varchar,int,long,double,datatime
        String dbType = "varchar";
        if ("int".equals(type) || "linkEntityMore".equals(inputType) || "fileMore".equals(inputType)) {//关联多个对象需要创建字段,用于保存管理对象的数量
            dbType = "int";
            maxLength = 10;
        } else if ("long".equals(type)) {
            dbType = "bigint";
            maxLength = 19;
        } else if ("boolean".equals(type)) {
            dbType = "bit";
            maxLength = 1;
        } else if ("double".equals(type)) {
            dbType = "double";
            maxLength = 0;
        } else if ("string".equals(type)) {
            maxLength = maxLength == 0 ? 64 : maxLength;
        }

        String fullDbType = null;
        if (maxLength > 0) {
            if (maxLength >= 21845) {
                dbType = "text";
                fullDbType = dbType;
            } else {
                fullDbType = dbType + "(" + maxLength + ")";
            }
            if (fieldName.equals("isSellerAdmin")) {
                Logger.getLogger(CheckMData.class).info("isSellerAdmin");
            }
        } else {
            fullDbType = dbType;
        }
        if (dbField == null) {
            dbField = MysqlDaoImpl.getInstance().getColumn(dbName, tableName, fieldName);
        }
        String faction = "ADD";

        if (dbField != null && dbField.size() > 0) {
            if (fieldName.equals(dbField.get("name")) && dbType.equals(dbField.get("type")) && maxLength.equals(dbField.get("size"))) {
                Logger.getLogger(CheckMData.class).info("-- field: " + fieldName + " not change");
                return;
            } else {
                //Logger.getLogger(CheckMData.class).info("-- db map:" + dbField.get("name") + "," + dbField.get("type") + "," + dbField.get("size"));
                //Logger.getLogger(CheckMData.class).info("-- db map:" + fieldName + "," + dbType + "," + maxLength);
            }
            faction = "MODIFY";
        }

        if (dbField != null && dbField.size() > 0) {
            faction = "MODIFY";
        }
        String fullTableName = Dao.getFullTableName(dbName, tableName);
        String sql = "ALTER TABLE " + fullTableName + " " + faction + " COLUMN `" + fieldName + "` " + fullDbType;
        //MysqlDaoImpl.getInstance().exeSqlLockTable(sql, null, fullTableName);
        MysqlDaoImpl.getInstance().exeSql(sql, null, fullTableName);

        putMDataFieldToCache(mdataField);// 同时更新到内存
    }
}


