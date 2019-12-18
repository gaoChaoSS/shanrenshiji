package com.zq.kyb.core.dao.mysql;

import net.sf.json.JSON;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于链接第3方的Mysql数据库
 */
public abstract class OtherMysqlDaoImpl {


    public List<Map<String, Object>> querySql(String dataName, String sql, List<String> returnField, List<Object> params) throws Exception {


        Logger.getLogger(MysqlDaoImpl.class).info("QUERY SQL: " + sql);
        PreparedStatement stmt = null;
        ResultSet re = null;
        Connection conn = null;
        try {
            conn = getConn(dataName);
            stmt = conn.prepareStatement(sql);
            int i = 0;
            if (params != null) {
                for (Object object : params) {
                    Logger.getLogger(this.getClass()).info("param" + i + ": " + object);
                    stmt.setObject(++i, object);
                }
            }

            long t = System.currentTimeMillis();
            re = stmt.executeQuery();
            Logger.getLogger("sql_exe").info("-EXE SQL: " + (System.currentTimeMillis() - t) + ", " + sql);

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
            if (re != null) {
                re.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    protected abstract Connection getConn(String dataName) throws Exception;

    public int exeSql(String dataName, String sql, List<Object> params) throws Exception {


        //判断是否是seller所属数据
        //复制参数列表
        ArrayList<Object> n = new ArrayList<Object>();
        if (params != null) {
            n.addAll(params);
        }

        //Logger.getLogger(MysqlDaoImpl.class).info("EXE SQL: " + (System.currentTimeMillis() - t) + "ms, " + sql);
        Connection conn = null;
        PreparedStatement stmt = null;
        long t = System.currentTimeMillis();
        try {
            conn = getConn(dataName);
            stmt = conn.prepareStatement(sql);
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
                stmt = null;
            }
            if (stmt != null) {
                conn.close();
                conn = null;
            }
        }
    }
}
