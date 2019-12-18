package com.zq.kyb.core.dao.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CheckMData;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MysqlDaoImpl extends MysqlBaseDaoImpl {

    public static final ThreadLocal<MysqlDaoImpl> context = new ThreadLocal<>();
    public static ComboPooledDataSource ds = null;

    static {
        try {
            ds = new ComboPooledDataSource("mysql_" + Constants.dbConfig);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    private Connection conn;

    public MysqlDaoImpl() {
        try {
            if (conn == null) {
                conn = ds.getConnection();
                // 手动管理事务
                conn.setAutoCommit(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destory() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static MysqlDaoImpl getInstance() {
        MysqlDaoImpl s = context.get();
        if (s == null) {
            s = new MysqlDaoImpl();
            context.set(s);
        }
        return s;
    }

    public static void commit() throws SQLException {
        MysqlDaoImpl s = context.get();
        if (s != null) {
            if (s.conn != null) {
                s.conn.commit();
            }
        }
    }

    public static void rollback() throws SQLException {
        MysqlDaoImpl s = context.get();
        if (s != null) {
            if (s.conn != null) {
                s.conn.rollback();
            }
        }
    }


    public static void clearContext() {
        MysqlDaoImpl s = context.get();
        if (s != null) {
            s.destory();
        }
        context.set(null);
        context.remove();

    }

    public Connection getConn() {
        return conn;
    }


    public boolean tableIsExist(String dbName, String tableName) throws SQLException {
        String sql = "SHOW TABLES in `" + dbName + "` like '" + tableName + "'";
        PreparedStatement stmt = null;
        ResultSet re = null;
        try {
            stmt = conn.prepareStatement(sql);
            re = stmt.executeQuery(sql);
            if (re.next()) {
                return true;
            }
        } finally {
            if (re != null) {
                re.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        return false;
    }

    // ********** dao的逻辑开始 ***********//
    public Map<String, Object> findById2MapLock(String tableName, String _id, String[] fields, FieldStrategy fs) throws SQLException {
        fs = fs == null ? FieldStrategy.Exclude : fs;// 默认是排除

        ArrayList<String> fieldNames = new ArrayList<>();

        List<String> f = getQueryFields(tableName, fields, fs);
        String baseFieldStr = "t1.`" + StringUtils.join(f, "`,t1.`") + "`";
        String sql = "select " + baseFieldStr + " from " + Dao.getFullTableName(tableName) + " t1 where t1._id=? for update";
        List<Object> p = new ArrayList<>();
        p.add(_id);
        List<Map<String, Object>> li = queryBySql(sql, f, p);
        return li == null || li.size() == 0 ? null : li.get(0);
    }


    private List<String> getQueryFields(String tableName, String[] fields, FieldStrategy fs) {
        List<String> fieldList = new ArrayList<>();
        if (fs.equals(FieldStrategy.Exclude)) {
            HashMap<String, Boolean> eMap = new HashMap<>();
            if (fields != null && fields.length > 0) {
                for (String string : fields) {
                    eMap.put(string, true);
                }
            }
            List<Object> li = Dao.entityFieldsMap.get(tableName);
            List<String> includeFields = new ArrayList<>();
            if (li != null) {
                for (Object object : li) {
                    JSONObject j = JSONObject.fromObject(object);
                    String name = j.getString("name");
                    if (!eMap.containsKey(name) && !name.equals("_id")) {
                        includeFields.add(name);
                    }
                }
            }
            fieldList = includeFields;

        } else if (fs.equals(FieldStrategy.Include)) {
            String[] v = fields == null ? new String[0] : fields;
            for (String vitem : v) {
                fieldList.add(vitem);
            }
        }
        return fieldList;
    }

    private List<Map<String, Object>> queryData(String tableName, long skip, int pageSize, Map<String, Object> params, Map<String, Object> orderBy, String[] fields, FieldStrategy fs)
            throws SQLException {
        skip = skip < 0 ? 0 : skip;
        pageSize = pageSize <= 0 ? 1000 : pageSize;

        fs = fs == null ? FieldStrategy.Exclude : fs;// 默认是排除

        ArrayList<String> fieldNames = new ArrayList<>();
        //ArrayList<Object> values = new ArrayList<Object>();
        if (params != null) {
            for (String key : params.keySet()) {
                fieldNames.add(key);
                // values.add(params.get(key));
            }
        }


        ArrayList<String> sqlField = new ArrayList<>();
        sqlField.add("_id");
        List<String> f = getQueryFields(tableName, fields, fs);
        for (Object object : f) {
            String fieldName = (String) object;
            String key = tableName + "_" + fieldName;
            Map v = (Map) Dao.fieldMap.get(key);
            if (v == null) {
                throw new RuntimeException("字段定义不存在，[" + key + "], 请检查！");
            }

//            if (!StringUtils.mapValueIsEmpty(v, "inputType")) {
//                String inputType = (String) v.get("inputType");
//            }
            sqlField.add(fieldName);
        }

        String orderByStr = " order by t1.createTime desc";
        if (orderBy != null && orderBy.size() > 0) {
            int i = 0;
            orderByStr = " order by ";
            for (String key : orderBy.keySet()) {
                orderByStr += (i++ > 0 ? "," : "") + "t1." + key + " " + (((Integer) orderBy.get(key)) == 1 ? "asc" : "desc");
            }
        }

        Map<String, Object> whereStrMap = genWhereStr(tableName, fieldNames, params);
        String sql = "select " + ("t1.`" + StringUtils.join(sqlField.toArray(), "`,t1.`") + "`") + " from " + Dao.getFullTableName(tableName) + " t1";
        List<Map<String, Object>> li = queryBySql(sql, " where " + whereStrMap.get("whereStr"), null, orderByStr, " limit " + skip + "," + pageSize, sqlField, (List<Object>) whereStrMap.get("paramValues"));
        return li;
    }

    private static Map<String, Object> genWhereStr(String tableName, ArrayList<String> fieldNames, Map<String, Object> params) {
        // 生产where条件
        // TODO只是简单的and方式

        Map<String, Object> re = new HashMap<>();
        String whereStr = "1=1";
        List<Object> paramValues = new ArrayList<>();

        for (String key : fieldNames) {
            Map mongoFiled = (Map) Dao.fieldMap.get(tableName + "_" + key);
            mongoFiled = mongoFiled == null ? new HashMap<String, Object>() : mongoFiled;
//            Object o = mongoFiled.get("inputType") == null ? "string" : mongoFiled.get("inputType");
//            String inputType = "null".equals(o.toString()) ? "string" : o.toString();
            String type = StringUtils.mapValueIsEmpty(mongoFiled, "type") ? "string" : (String) mongoFiled.get("type");

            Object value = params.get(key);
//            if (inputType.indexOf("linkEntity") > -1) {
//                String linkTable = (String) mongoFiled.get("_linkTable");
//                String t = (String) mongoFiled.get("name");
//                t = t.substring(0, t.length() - "List".length());
//                t = t.substring(0, 1).toUpperCase() + t.substring(1);
//                whereStr += " and " + "t" + linkTableMap.get(linkTable) + "." + t + "Id=?";
//                paramValues.add(value);
//            } else {

            if (value != null && value instanceof String) {
                if ("___null".equals(value)) {//判断空值
                    whereStr += " and t1." + key + " is null ";
                } else if ("___notnull".equals(value)) {//判断非空
                    whereStr += " and t1." + key + " is not null ";
                } else if (((String) value).startsWith("___in_")) {//判断是否在某个区间,上界下界都包含
                    String v = ((String) value).substring("___in_".length());
                    String[] split = v.split("-");
                    String min = split.length > 0 ? split[0] : null;
                    String max = split.length > 1 ? split[1] : null;
                    if (StringUtils.isNotEmpty(min)) {
                        whereStr += " and t1." + key + ">=?";
                        paramValues.add(min);
                    }
                    if (StringUtils.isNotEmpty(max)) {
                        whereStr += " and t1." + key + "<=?";
                        paramValues.add(max);
                    }
                } else if ("$keywords".equals(key)) {//关键字查询
                    String[] splits = ((String) value).split(":");
                    String[] fields = splits[0].split(",");
                    String keyword = splits[1];
                    String keywordWhere = "";
                    for (String k : fields) {
                        keywordWhere += ("".equals(keywordWhere) ? "" : " or ") + "t1." + k + " like ?";
                        paramValues.add("%" + keyword + "%");
                    }
                    whereStr += " and (" + keywordWhere + ")";
                } else if (((String) value).startsWith("___like_")) {//like查询
                    String v = (String) value;
                    v = v.substring("___like_".length());
                    String keywordWhere = "t1." + key + " like ?";
                    paramValues.add("" + v + "%");
                    whereStr += " and (" + keywordWhere + ")";
                } else if (value.toString().startsWith("$$")) {//单个大于小于等于等操作
                    String[] split = value.toString().split("\\$\\$");
                    String b = split[1];
                    String k = split[2];
                    whereStr += " and t1." + key + b + "?";
                    paramValues.add(k);

                } else if (value.toString().startsWith("___!")) {//不等于
                    String v = ((String) value).substring("___!".length());
                    whereStr += " and t1." + key + "!=?";
                    paramValues.add(v);

                } else {

                    if ("boolean".equals(type)) {
                        Boolean b = Boolean.valueOf((String) value);
                        if (b) {
                            whereStr += " and t1." + key + "=?";// 基础数据的查询条件
                        } else {
                            whereStr += " and (t1." + key + "=? or t1." + key + " is null)";// 基础数据的查询条件
                        }
                        paramValues.add(b);
                    } else {
                        whereStr += " and t1." + key + "=?";
                        paramValues.add(value);
                    }
                }
            } else {
                whereStr += " and t1." + key + "=?";// 基础数据的查询条件
                paramValues.add(params.get(key));
            }
        }

//        }

        re.put("whereStr", whereStr);
        re.put("paramValues", paramValues);
        return re;
    }

//    public static String getLinkTableName(String selectListUrl) {
//        String[] linkTables = selectListUrl.split("/");
//        String linkTable = "";
//        int i = 0;
//        for (String string : linkTables) {
//            if (StringUtils.isNotEmpty(string)) {
//                if (i++ == 1) {
//                    linkTable = string;
//                    break;
//                }
//            }
//        }
//        return linkTable;
//    }


    @Override
    public Map<String, Object> findOne2Map(String tableName, Map<String, Object> params, String[] fields, FieldStrategy fs) throws Exception {
        List<Map<String, Object>> reList = queryData(tableName, -1, -1, params, null, fields, fs);
        return reList.size() > 0 ? reList.get(0) : null;

    }

    @Override
    public Map<String, Object> findById2Map(String tableName, String id, String[] fields, FieldStrategy fs) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("_id", id);
        List<Map<String, Object>> reList = queryData(tableName, -1, -1, params, null, fields, fs);
        return reList.size() > 0 ? reList.get(0) : null;
    }


    @Override
    public List<Map<String, Object>> findAll2Map(String tableName, Map<String, Object> p, Map<String, Object> orderBy, String[] fields, FieldStrategy fs) throws Exception {
        return queryData(tableName, -1, -1, p, orderBy, fields, fs);
    }

    @Override
    public long findCount(String tableName, Map<String, Object> params) throws Exception {

        ArrayList<String> fieldNames = new ArrayList<>();
        if (params != null) {
            for (String key : params.keySet()) {
                fieldNames.add(key);
            }
        }

        Map<String, Integer> link = new HashMap<>();
        String joinStr = "";
        int index = 1;
        for (String key : fieldNames) {
            if ("$keywords".equals(key) || key.startsWith("___like_")) {
                continue;
            }
            Map mongoFiled = (Map) Dao.fieldMap.get(tableName + "_" + key);
            if (mongoFiled == null) {
                throw new RuntimeException(tableName + "_" + key + " is not exist!");
            }
//            Object obj = mongoFiled.get("inputType");
//            obj = obj == null || "null".equals(obj.toString()) ? "string" : obj;
//            String inputType = obj.toString();
//            inputType = inputType == null ? "string" : inputType;
//            if (inputType.indexOf("linkEntity") > -1) {
//                String linkTable = (String) mongoFiled.get("_linkTable");
//                index++;
//                joinStr += " left join " + linkTable + " t" + index + " on t" + index + "." + tableName + "Id=t1._id";
//                link.put(linkTable, index);
//            }
        }

        Map<String, Object> whereStrMap = genWhereStr(tableName, fieldNames, params);

        String sql = "select count(_id) as `count` from " + Dao.getFullTableName(tableName) + " t1" + joinStr + " where " + whereStrMap.get("whereStr");
        Logger.getLogger(MysqlDaoImpl.class).info("QUERY SQL: " + sql);

        List<String> re = new ArrayList<>();
        re.add("count");
        List<Map<String, Object>> li = queryBySql(sql, re, (List) whereStrMap.get("paramValues"));
        Long count = 0L;
        if (li != null && li.size() > 0) {
            count = (Long) li.get(0).get("count");
        }
        return count;
    }

    @Override
    public Double sum(String tableName, Map<String, Object> params, String sumField) throws Exception {
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        for (String key : params.keySet()) {
            fieldNames.add(key);
            values.add(params.get(key));
        }

        // TODO只是简单的and方式
        String whereStr = "1=1";
        for (String key : fieldNames) {
            whereStr += " and `" + key + "`=?";
        }

        String sql = "select sum(" + sumField + ") as `sum` from " + Dao.getFullTableName(tableName) + " where " + whereStr;
        Logger.getLogger(MysqlDaoImpl.class).info("query sql: " + sql);


        List<String> re = new ArrayList<String>();
        re.add("count");
        List<Map<String, Object>> li = queryBySql(sql, re, values);
        Double count = 0D;
        if (li != null && li.size() > 0) {
            count = (Double) li.get(0).get("sum");
        }
        return count;
    }

    @Override
    public List<Map<String, Object>> findPage2Map(String tableName, long skip, int pageSize, Map<String, Object> params, Map<String, Object> orderBy, String[] fields, FieldStrategy fs) throws Exception {
        return queryData(tableName, skip, pageSize, params, orderBy, fields, fs);
    }

    @Override
    public List<Object> group(String collectionName, Map<String, Object> key, Map<String, Object> cond, Map<String, Object> initial, String reduce, String finial) throws Exception {
        // TODO Auto-generated method stub
        throw new RuntimeException("not impl");
    }

    /**
     * 写操作
     **/

//    @Override
//    public int insert(String tableName, Map<String, Object> obj) throws Exception {
//        throw new RuntimeException("not impl");
//    }
    @Override
    public int remove(String tableName, String id) throws Exception {
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        int i = exeSql("delete from " + Dao.getFullTableName(tableName) + " where _id=?", params, tableName, false);
        Map<String, Object> v = new HashMap<>();
        v.put("_id", id);
        if (Dao.isSync(tableName)) {
            saveVersion(Dao.getDataBaseName(tableName), tableName, v, true);
        }
        return i;
    }

    @Override
    public int update(String tableName, Map<String, Object> newObj) {
        // TODO Auto-generated method stub
        throw new RuntimeException("not impl");
    }

    @Override
    public int updateAll(String tableName, Map<String, Object> newObj, Map<String, Object> params) {
        // TODO Auto-generated method stub
        throw new RuntimeException("not impl");
    }

    @Override
    public void removeAll(String tableName) {
        // TODO Auto-generated method stub
        throw new RuntimeException("not impl");
    }

    @Override
    public Long saveOrUpdate(String tableName, Map<String, Object> values) throws Exception {
        if (!values.containsKey("_id")) {
            throw new UserOperateException(400, "_id is not exist");
        }

        // String id = values.getString("_id");
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<Object> v = new ArrayList<>();
        ArrayList<Object> markValue = new ArrayList<>();


        for (Object field : values.keySet()) {
            if ("_id".equals(field)) {// id不需要加入判断
                continue;
            }
            Map mfieldObj = (Map) fieldMap.get(tableName + "_" + field);
            if (mfieldObj == null) {
                Logger.getLogger(this.getClass()).warn("field[" + tableName + "_" + field + "] is not def");
                continue;
            }
            if (!StringUtils.mapValueIsEmpty(mfieldObj, "inputType")) {
                String inputType = (String) mfieldObj.get("inputType");

                String name = (String) mfieldObj.get("name");
//                if (inputType.equals("password") && !StringUtils.mapValueIsEmpty(values, name)) {
//                    MessageDigestUtils.digest((String) values.get(name));
//                }
            }


//            if (((String) object).contains("linkEntity")) {
//                linkField.add((String) field);
//            } else if (((String) object).contains("file")) {
//                fileField.add((String) field);
//            } else {

            fieldNames.add((String) field);
            Object value = values.get(field);
            Object typeObj = mfieldObj.get("type");
            typeObj = typeObj == null || "null".equals(typeObj.toString()) ? "string" : typeObj;
            String type = typeObj.toString();
            if (value == null || "".equals(value)) {
                if ("int".equals(type) || "long".equals(type) || "double".equals(type)) {
                    value = 0;
                } else if ("boolean".equals(type)) {
                    value = false;
                }
            }
            if ("boolean".equals(type)) {
                if ("0".equals(value) || "1".equals(value)) {
                    value = !"0".equals(value);
                }
            }
            v.add(value);
            markValue.add("?");
//            }
        }
        String _id = (String) values.get("_id");
        Long version = 0L;
        if (fieldNames.size() > 0) {
            fieldNames.add("_id");
            v.add(_id);
            markValue.add("?");

            //如果是同步数据表,必须要补充完整的数据内容,便于记录数据版本
//            if (Dao.isSync(tableName)) {
//                List<Object> f = Dao.entityFieldsMap.get(tableName);
//                Map<String, Object> oldMap = findById2Map(tableName, _id, null, null);
//                for (Object obj : f) {
//                    Map<String, Object> map = (Map<String, Object>) obj;
//                    String name = (String) map.get("name");
//                    //判断是否为必填字段
//                    if (values.containsKey(name)) {
//                        fieldNames.add(name);
//                        v.add(oldMap != null ? oldMap.get(name) : null);
//                        markValue.add("?");
//                    } else {
//                        //必填的情况下,但有没有设置这个值的情况
//                        if (!StringUtils.mapValueIsEmpty(map, "isNotNull") && (boolean) map.get("isNotNull")) {
//                            String key = StringUtils.mapValueIsEmpty(map, "title") ? name : (String) map.get("title");
//                            throw new RuntimeException("[" + key + "]不能为空, 请检查！");
//                        }
//                    }
//                }
//            }
            version = updateTable(Dao.getDataBaseName(tableName), tableName, fieldNames, v, markValue);
        }

        // 关联字段的存储
//        if (linkField.size() > 0) {
//            linkTableSet(tableName, values, linkField, _id);
//        }
        // 关联文件字段处理
//        if (fileField.size() > 0) {
//            linkFileSet(tableName, values, fileField, _id);
//        }

        return version;
    }

    /**
     * 管理文件操作，TODO
     *
     * @param tableName
     * @param values
     * @param fileField
     * @param _id
     * @throws Exception
     */
    private void linkFileSet(String tableName, Map<String, Object> values, ArrayList<String> fileField, String _id) throws Exception {
        for (String f : fileField) {
            String _linkTable = "FileEntityLink";

            ArrayList<String> linkf = new ArrayList<>();
            linkf.add("_id");
            linkf.add("entityName");
            linkf.add("entityField");
            linkf.add("entityId");
            linkf.add("fileId");
            linkf.add("createTime");

            Object obj = values.get(f);
            if (obj == null || obj instanceof net.sf.json.JSONNull) {
                continue;
            }

            List valueIds = (List) obj;

            Map<String, String> oldData = new HashMap<>();
            List<Object> params = new ArrayList<>();
            params.add(tableName);
            params.add(f);
            params.add(_id);

            String dbName = Dao.getDataBaseName(tableName);
            String fullLinkTableName = Dao.getFullTableName(dbName, _linkTable);
            String sql = "select " + StringUtils.join(linkf.toArray(), ",") + " from " + fullLinkTableName + " where entityName=? and entityField=? and entityId=?";
            List<Map<String, Object>> li = queryBySql(sql, linkf, params);
            for (Map<String, Object> map : li) {
                oldData.put((String) map.get("fileId"), (String) map.get("_id"));
            }

            if (valueIds != null && valueIds.size() > 0) {
                ArrayList<Object> linkmark = new ArrayList<>();
                linkmark.add("?");
                linkmark.add("?");
                linkmark.add("?");
                linkmark.add("?");
                linkmark.add("?");
                linkmark.add("?");
                for (Object o : valueIds) {
                    Map m = (Map) o;
                    String fileId = (String) m.get("_id");
                    if (oldData.containsKey(fileId)) {
                        oldData.remove(fileId);
                        continue;
                    }
                    ArrayList<Object> linkv = new ArrayList<>();
                    linkv.add(UUID.randomUUID().toString());
                    linkv.add(tableName);
                    linkv.add(f);
                    linkv.add(_id);
                    linkv.add(fileId);
                    linkv.add(System.currentTimeMillis());
                    updateTable(dbName, _linkTable, linkf, linkv, linkmark);
                }
            }
            if (oldData.keySet().size() > 0) {
                for (String key : oldData.keySet()) {
                    ArrayList<Object> rm = new ArrayList<>();
                    rm.add(oldData.get(key));
                    exeSql("delete from " + fullLinkTableName + " where `_id`=?", rm, tableName);
                }
            }
        }
    }

    /**
     * 对表的关联字段的关联数据进行操作
     *
     * @param tableName
     * @param values
     * @param linkField
     * @param _id
     * @throws SQLException
     */
    private void linkTableSet(String tableName, Map<String, Object> values, ArrayList<String> linkField, String _id) throws SQLException {
        for (String f : linkField) {
            Map mfieldObj = (Map) fieldMap.get(tableName + "_" + f);
            String _linkTable = (String) mfieldObj.get("_linkTable");
            String[] vvv = _linkTable.substring(2).split("_");
            String linkMainTable = vvv[0].equals(tableName) ? vvv[1] : vvv[0];
            // updateTable(tableName, fieldNames, v, markValue);

            String myIdName = tableName + "Id";
            String linkTableId = linkMainTable + "Id";
            ArrayList<String> linkf = new ArrayList<>();
            linkf.add(myIdName);
            linkf.add(linkTableId);
            ArrayList<Object> linkmark = new ArrayList<>();
            linkmark.add("?");
            linkmark.add("?");
            Object o1 = values.get(f);
            if (o1 == null || o1 instanceof net.sf.json.JSONNull) {
                continue;
            }
            List valueIds = (List) o1;

            Map<String, Boolean> oldData = new HashMap<>();
            List<Object> params = new ArrayList<>();
            params.add(_id);
            String dbName = Dao.getDataBaseName(tableName);
            String fullLinkTableName = Dao.getFullTableName(dbName, _linkTable);
            List<Map<String, Object>> li = queryBySql("select `" + StringUtils.join(linkf.toArray(), "`,`") + "` from " + fullLinkTableName + " where `" + myIdName + "`=?", linkf, params);
            for (Map<String, Object> map : li) {
                oldData.put((String) map.get(linkTableId), true);
            }

            if (valueIds != null && valueIds.size() > 0) {
                String linkId = null;
                for (Object o : valueIds) {
                    if (o instanceof String) {
                        linkId = (String) o;
                    } else {
                        Map m = (Map) o;
                        linkId = (String) m.get("_id");
                        if (oldData.containsKey(linkId)) {
                            oldData.remove(linkId);
                            continue;
                        }
                    }
                    ArrayList<Object> linkv = new ArrayList<>();
                    linkv.add(_id);
                    linkv.add(linkId);
                    updateTable(dbName, _linkTable, linkf, linkv, linkmark);
                }
            }
            if (oldData.keySet().size() > 0) {
                for (String key : oldData.keySet()) {
                    ArrayList<Object> rm = new ArrayList<>();
                    rm.add(_id);
                    rm.add(key);
                    exeSql("delete from " + fullLinkTableName + " where `" + myIdName + "`=? and `" + linkTableId + "`=?", rm, tableName);
                }
            }
        }
    }

    private Long updateTable(String dbName, String tableName, ArrayList<String> fieldNames, ArrayList<Object> value, ArrayList<Object> markValue) throws SQLException {
        String fullTableName = Dao.getFullTableName(dbName, tableName);
        // 空值预处理
        updateNull(fullTableName, fieldNames, value);
        //Map<String, Object> entity = (Map<String, Object>) Dao.entityMap.get(tableName);
        //Boolean isSync = entity != null && !StringUtils.mapValueIsEmpty(entity, "isSyncTable") && (Boolean) entity.get("isSyncTable");
        //if (StringUtils.mapValueIsEmpty(entity, "_id")) {
        //    throw new UserOperateException(400, "id is null");
        //}

        Map<String, Object> valueMap = new HashMap<>();
        int i = 0;
        for (String s : fieldNames) {
            valueMap.put(s, value.get(i++));
        }


        String sql = "INSERT " + fullTableName + " (`" + StringUtils.join(fieldNames.toArray(), "`,`") + "`) values (" + StringUtils.join(markValue.toArray(), ",") + ")";
        sql += " ON DUPLICATE KEY UPDATE `" + StringUtils.join(fieldNames.toArray(), "`=?,`") + "`=?";
        ArrayList<Object> params = new ArrayList<>();
        params.addAll(value);
        params.addAll(value);
        exeSql(sql, params, tableName, false);

        //记录版本日志,同时不影响主业务的继续
        if (Dao.isSync(tableName)) {//同步数据
            return saveVersion(dbName, tableName, valueMap, false);
        }
        return 0L;
    }

    private long saveVersion(String dbName, String tableName, Map<String, Object> valueMap, boolean isDel) throws SQLException {
        String _id = (String) valueMap.get("_id");
        String creator = ControllerContext.getContext().getCurrentUserId();
        Long createTime = System.currentTimeMillis();


        //每条数据的版本记录
        String fullDataVersion = Dao.getFullTableName(dbName, CheckMData.DataVersion);
        String fullDataTableVersion = Dao.getFullTableName(dbName, CheckMData.DataTableVersion);

        long version = JedisUtil.incr(dbName + "_" + tableName);
        String fieldVersionId = tableName + "_" + _id;

        //锁住两张表
        List<String> r = new ArrayList<>();
        r.add("_id");
        List<Object> p = new ArrayList<>();
        p.add(fieldVersionId);
        List<Object> pt = new ArrayList<>();
        pt.add(tableName);
        queryBySql("select _id from " + fullDataVersion + " where _id=? for update", r, p);
        queryBySql("select _id from " + fullDataTableVersion + " where _id=? for update", r, pt);

        Map<String, Object> v = new LinkedHashMap<>();

        v.put("_id", fieldVersionId);
        v.put("createTime", createTime);
        v.put("creator", creator);
        v.put("entityName", tableName);
        v.put("entityId", _id);
        v.put("version", version);
        v.put("isDel", isDel);
        List<String> markValue = new ArrayList<>();
        int len = v.size();
        for (int i = 0; i < len; i++) {
            markValue.add("?");
        }

        String sql = "INSERT " + fullDataVersion + " (`" + StringUtils.join(v.keySet().toArray(), "`,`") + "`) values (" + StringUtils.join(markValue.toArray(), ",") + ")";
        sql += " ON DUPLICATE KEY UPDATE `" + StringUtils.join(v.keySet().toArray(), "`=?,`") + "`=?";
        ArrayList<Object> params = new ArrayList<>();
        params.addAll(v.values());
        params.addAll(v.values());
        exeSql(sql, params, tableName, false);

        //每个表的版本记录
        v.clear();
        v.put("_id", tableName);
        v.put("updateTime", createTime);
        v.put("updater", creator);
        v.put("version", version);
        markValue.clear();
        len = v.size();
        for (int i = 0; i < len; i++) {
            markValue.add("?");
        }
        sql = "INSERT " + fullDataTableVersion + " (`" + StringUtils.join(v.keySet().toArray(), "`,`") + "`) values (" + StringUtils.join(markValue.toArray(), ",") + ")";
        sql += " ON DUPLICATE KEY UPDATE `" + StringUtils.join(v.keySet().toArray(), "`=?,`") + "`=?";
        params = new ArrayList<>();
        params.addAll(v.values());
        params.addAll(v.values());
        exeSql(sql, params, tableName, false);

        if (!isDel) {
            //保存改条数据的最后版本记录,TODO 后续可改为存储在 mongodb等数据库
            v.clear();
            v.put("_id", fieldVersionId + "_" + version);
            v.put("entityJson", JSONObject.fromObject(valueMap).toString());
            markValue.clear();
            len = v.size();
            for (int i = 0; i < len; i++) {
                markValue.add("?");
            }
            sql = "INSERT " + Dao.getFullTableName(dbName, CheckMData.DataVersionContent) + " (`" + StringUtils.join(v.keySet().toArray(), "`,`") + "`) values (" + StringUtils.join(markValue.toArray(), ",") + ")";
            sql += " ON DUPLICATE KEY UPDATE `" + StringUtils.join(v.keySet().toArray(), "`=?,`") + "`=?";
            params = new ArrayList<>();
            params.addAll(v.values());
            params.addAll(v.values());
            exeSql(sql, params, tableName, false);
        }
        return version;
    }

    private void updateNull(String tableName, ArrayList<String> fieldNames, ArrayList<Object> value) {
        int i = 0;
        for (String fieldName : fieldNames) {
            String key = tableName + "_" + fieldName;
            Map fieldDef = (Map) Dao.fieldMap.get(key);
            if (fieldDef == null) {
                Logger.getLogger(getClass()).info("===null key:" + key);
            }
            if (fieldDef != null) {
                Object typeObj = fieldDef.get("type");
                typeObj = typeObj == null || "null".equals(typeObj.toString()) ? "string" : typeObj;
                String type = typeObj.toString();
                Object v = value.get(i);
                if ("string".equals(type)) {
                    v = v == null || "null".equals(v.toString()) ? "" : v;
                } else if ("int".equals(type) || "long".equals(type) || "double".equals(type) || "boolean".equals(type)) {
                    v = v == null || "null".equals(v.toString()) ? 0 : v;
                }
                value.set(i, v);
            }
            i++;
        }
    }

    /**
     * 多线程下测试数据库及连接池
     *
     * @param args
     * @throws
     * @Description: TODO
     */
    public static void main(String[] args) throws SQLException {


//        new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        MysqlDaoImpl.getInstance().getDataBaseInfo(); // 获取数据库信息
//                        MysqlDaoImpl.getInstance().getSchemasInfo(); // 获取数据库所有Schema
//                        MysqlDaoImpl.getInstance().getTableList(); // 获取某用户下所有的表
//                        // MysqlDaoImpl.getInstance().getPrimaryKeysInfo(); //
//                        // 获取表主键信息
//                        // MysqlDaoImpl.getInstance().getIndexInfo(); // 获取表索引信息
//                        // MysqlDaoImpl.getInstance().getColumnsInfo(); //
//                        // 获取表中列值信息
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        MysqlDaoImpl.clearContext();
//                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
//
//        // 监控数据库的连接数
//        new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Logger.getLogger(getClass()).info("--未关闭的链接数：" + ds.getNumConnections());
//                    } catch (SQLException e1) {
//                        e1.printStackTrace();
//                    }
//                    try {
//                        Thread.sleep(2 * 1000);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();

    }


}
