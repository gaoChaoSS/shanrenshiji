package com.zq.kyb.core.dao.mysql;

import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSON;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujoey on 16/12/19.
 */
public abstract class MysqlBaseDaoImpl extends MysqlMDataDaoImpl {

    /**
     * 锁表的方式执行某个SQL
     *
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public int exeSqlLockTable(String sql, List<Object> params, String tableName) throws SQLException {
        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("tableName is null!");
        }
        //判断表定义是否为同步表,如果是就不能执行任何sql方式的修改(包括增加,删除和修改)
        if (Dao.isSync(tableName)) {
            throw new RuntimeException(tableName + " 是同步表,不能使用exeSql修改数据,必须使用:saveOrUpdate等方式修改数据!");
        }
        //判断是否是seller所属数据
        //复制参数列表
        ArrayList<Object> n = new ArrayList<Object>();
        if (params != null) {
            n.addAll(params);
        }

        //Logger.getLogger(MysqlDaoImpl.class).info("EXE SQL: " + (System.currentTimeMillis() - t) + "ms, " + sql);
        PreparedStatement stmt = null;
        long t = System.currentTimeMillis();
        try {
            stmt = getConn().prepareStatement(sql);
            if (n.size() > 0) {
                int i = 0;
                for (Object object : n) {
                    if (object instanceof net.sf.json.JSONNull) {
                        object = null;
                    }
                    if (object instanceof JSON) {
                        object = object.toString();
                    }
                    stmt.setObject(++i, object);
                    Logger.getLogger(MysqlDaoImpl.class).info("Param " + i + ": " + object);
                }
            }
            stmt.execute("LOCK TABLES " + tableName + " WRITE");
            int i = stmt.executeUpdate();
            return i;
        } finally {
            Logger.getLogger("sql_exe").info("-EXE SQL: " + (System.currentTimeMillis() - t) + "ms, " + sql);
            stmt.execute("UNLOCK TABLES");
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public int exeSql(String sql, List<Object> params, String tableName) throws SQLException {
        return exeSql(sql, params, tableName, true);
    }

    public int exeSql(String sql, List<Object> params, String tableName, Boolean isCheckSync) throws SQLException {
        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("tableName is null!");
        }
        //判断表定义是否为同步表,如果是就不能执行任何sql方式的修改(包括增加,删除和修改)
        if (isCheckSync != null && isCheckSync && Dao.isSync(tableName)) {
            throw new RuntimeException(tableName + " 是同步表,不能使用exeSql修改数据,必须使用:saveOrUpdate等方式修改数据!");
        }
        //判断是否是seller所属数据
        //复制参数列表
        ArrayList<Object> n = new ArrayList<Object>();
        if (params != null) {
            n.addAll(params);
        }

        //Logger.getLogger(MysqlDaoImpl.class).info("EXE SQL: " + (System.currentTimeMillis() - t) + "ms, " + sql);
        PreparedStatement stmt = null;
        long t = System.currentTimeMillis();
        try {
            stmt = getConn().prepareStatement(sql);
            if (n.size() > 0) {
                int i = 0;
                for (Object object : n) {
                    if (object instanceof net.sf.json.JSONNull) {
                        object = null;
                    }
                    if (object instanceof JSON) {
                        object = object.toString();
                    }
                    stmt.setObject(++i, object);
                    Logger.getLogger(MysqlDaoImpl.class).info("Param " + i + ": " + object);
                }
            }

            int i = stmt.executeUpdate();
            return i;
        } finally {
            Logger.getLogger("sql_exe").info("-EXE SQL: " + (System.currentTimeMillis() - t) + "ms, " + sql);

            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public List<Map<String, Object>> queryBySql(String baseSql, String where, String groupBy, String orderBy, String limit, List<String> returnField, List<Object> params) throws SQLException {
        //复制参数列表
        ArrayList<Object> n = new ArrayList<>();
        if (params != null) {
            n.addAll(params);
        }
        where = where == null ? "" : where;
        String sql = baseSql + " " + where;
        if (StringUtils.isNotEmpty(groupBy)) {
            sql += " " + groupBy;
        }
        if (StringUtils.isNotEmpty(orderBy)) {
            sql += " " + orderBy;
        }
        if (StringUtils.isNotEmpty(limit)) {
            sql += " " + limit;
        }
        return queryBySql(sql, returnField, n);
    }

    public List<Map<String, Object>> queryBySql(String sql, List<String> returnField, List<Object> params) throws SQLException {

        if (returnField == null) {
            returnField = new ArrayList<>();
            returnField.add("_id");
        }

        //复制参数列表
        ArrayList<Object> n = new ArrayList<>();
        if (params != null) {
            n.addAll(params);
        }

        //Logger.getLogger(MysqlDaoImpl.class).info("QUERY SQL: " + sql);
        PreparedStatement stmt = null;
        ResultSet re = null;
        long t = System.currentTimeMillis();
        try {
            stmt = getConn().prepareStatement(sql);
            int i = 0;
            for (Object object : n) {
                Logger.getLogger(this.getClass()).info("param" + i + ": " + object);
                stmt.setObject(++i, object);
            }

            re = stmt.executeQuery();

            List<Map<String, Object>> reList = new ArrayList<>();
            while (re.next()) {
                Map<String, Object> map = new HashMap<>();
                for (Object key : returnField) {
                    map.put((String) key, re.getObject((String) key));
                }
                reList.add(map);
            }
            return reList;
        } finally {
            Logger.getLogger("sql_exe").info("-QUERY SQL: " + (System.currentTimeMillis() - t) + "ms, " + sql);
            if (re != null) {
                re.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

}
