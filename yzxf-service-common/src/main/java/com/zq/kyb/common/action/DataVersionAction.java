package com.zq.kyb.common.action;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.mysql.OtherMysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 数据版本同步到一个公网服务器,其他开发者可以进行同步,使用频率低,所以不用考虑数据库连接池
 */
public class DataVersionAction extends BaseActionImpl {

    /**
     * 更新数据
     * 在服务器端需要一个统一编号生成机制,任何一个保存本地备份的数据都需要先将数据同步到服务器,然后获取一个版本号,同时更新本地的版本号
     *
     * @throws Exception
     */
    @POST
    @Path("/updateData")
    public void updateData() throws Exception {
        String module = ControllerContext.getPString("module");
        String entity = ControllerContext.getPString("entity");
        String entityId = ControllerContext.getPString("entityId");
        Long version = ControllerContext.getPLong("version");

        //先同步元数据,然后做建表的动作,然后再做其他数据的同步
        updateData("common", "MData", null, version);
    }

    private void updateData(String module, String entity, String entityId, Long version) throws Exception {
        version = version == null ? 0 : version;
        String whereStr = "version=?";
        List<Object> p = new ArrayList<>();
        p.add(version);
        if (StringUtils.isNotEmpty(module)) {
            whereStr += " module=?";
            p.add(module);
        }
        if (StringUtils.isNotEmpty(entity)) {
            whereStr += " entity=?";
            p.add(entity);
        }
        if (StringUtils.isNotEmpty(entityId)) {
            whereStr += " entityId=?";
            p.add(entityId);
        }

        SyncMysqlDaoImpl syncMysqlDao = new SyncMysqlDaoImpl();
        String sql = "select _id" +
                ",module" +
                ",entity" +
                ",entityId" +
                ",entityJson" +
                ",max(version) from DataVersion " + whereStr +
                " group by entityId";
        List<String> re = new ArrayList<>();
        List<Map<String, Object>> li = syncMysqlDao.querySql(null, sql, re, p);
        for (Map<String, Object> map : li) {
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, map);

            String json = (String) map.get("module");
            String table = (String) map.get("entity");
            String eId = (String) map.get("entityId");
            JSONObject jsonObj = JSONObject.fromObject(json);
            MysqlDaoImpl.getInstance().saveOrUpdate(table, jsonObj);
            if ("MData".equals(table)) {//如果是发生了改变,需要做表的同步,以及发送消息给其他模块
                // new MDataAction().updateTable(jsonObj);
            }
        }
    }


    /**
     * 上传版本数据
     *
     * @throws Exception
     */
    @POST
    @Path("/commitData")
    public void commitData() throws Exception {
        //首先锁住这个表
        String module = ControllerContext.getPString("module");
        String entity = ControllerContext.getPString("entity");
        String entityId = ControllerContext.getPString("entityId");
        String entityJson = ControllerContext.getPString("entityJson");
        Long oldVersion = ControllerContext.getPLong("version");

        List<Object> p = new ArrayList<>();
        p.add(module);
        p.add(entity);
        p.add(entityId);
        SyncMysqlDaoImpl syncMysqlDao = new SyncMysqlDaoImpl();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet re = null;
        Long v = 0L;
        try {
            String sql = "select max(`version`) from DataVersion";
            conn = syncMysqlDao.getConn(null);
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);
            int i = 0;
            for (Object object : p) {
                Logger.getLogger(this.getClass()).info("param" + i + ": " + object);
                stmt.setObject(++i, object);
            }

            long t = System.currentTimeMillis();
            re = stmt.executeQuery();
            Logger.getLogger("sql_exe").info("-EXE SQL: " + (System.currentTimeMillis() - t) + ", " + sql);

            while (re.next()) {
                v = re.getLong("version");
            }

            if (v > 0 && (oldVersion != v)) {
                throw new UserOperateException(400, "服务器版本已更新,请先下载最新版本进行合并! " + module + "-" + entity + ",entityId:" + entityId);
            }

            v++;
            List<Object> d = new ArrayList<>();
            d.add(UUID.randomUUID().toString());
            d.add(System.currentTimeMillis());
            d.add(ControllerContext.getContext().getCurrentUserId());
            d.add(module);
            d.add(entity);
            d.add(entityId);
            d.add(v);
            d.add(entityJson);
            String updateSql = "insert DataVersion (_id,createTime,creator,`module`,entity,entityId,`version`,entityJson) values (?,?,?,?,?,?,?)";
            stmt.executeUpdate(updateSql);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
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

    public static class SyncMysqlDaoImpl extends OtherMysqlDaoImpl {
        private static ComboPooledDataSource ds;

        /**
         * 代码设置远程数据库服务器:
         */
        String host = null;
        String db = null;
        String user = null;
        String pass = null;

        public Connection getConn(String data) throws Exception {

            JSONObject map = SettingAction.getSettingMapByType("all");

            if (StringUtils.mapValueIsEmpty(map, "devSyncHost")) {
                throw new UserOperateException(400, "请设置系统参数: devSyncHost");
            }
            if (StringUtils.mapValueIsEmpty(map, "devSyncDBName")) {
                throw new UserOperateException(400, "请设置系统参数: devSyncDBName");
            }
            if (StringUtils.mapValueIsEmpty(map, "devSyncDBUser")) {
                throw new UserOperateException(400, "请设置系统参数: devSyncDBUser");
            }
            if (StringUtils.mapValueIsEmpty(map, "devSyncDBPass")) {
                throw new UserOperateException(400, "请设置系统参数: devSyncDBPass");
            }

            String hostL = (String) map.get("devSyncHost");
            String dbL = (String) map.get("devSyncDBName");
            String userL = (String) map.get("devSyncDBUser");
            String passL = (String) map.get("devSyncDBPass");

            //发生改变后,重新穿件数据库连接池
            if (host != null && db != null && user != null && pass != null) {
                if (!host.equals(hostL) || !db.equals(dbL) || !user.equals(userL) || !pass.equals(passL)) {

                    if (ds != null) {
                        ds.close();
                        ds = null;
                    }
                }
            }

            host = hostL;
            db = dbL;
            user = userL;
            pass = passL;

            if (ds == null) {
                ds = new ComboPooledDataSource();
                ds.setDriverClass("com.mysql.jdbc.Driver");
                ds.setJdbcUrl("jdbc:mysql://" + host + ":3306/" + db + "?useUnicode=true&amp;characterEncoding=utf8");
                ds.setUser(user);
                ds.setPassword(pass);
            }

            return ds.getConnection();
        }
    }
}

