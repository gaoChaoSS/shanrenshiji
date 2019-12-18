import com.zq.kyb.core.dao.CheckMData;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hujoey on 16/7/29.
 */
public class InsertMDataTest {
    public static void main(String[] args) throws Exception {
        //CheckMData.putAllDataToMap();
        Map<String, Object> m = new HashMap<>();
        m.put("_id", UUID.randomUUID().toString());
        m.put("pid", "be95b764-b063-4e23-99a1-b5ba789665e3");
        m.put("name", "_id");
        m.put("level", 2);
        m.put("modelName", "common");
        m.put("entityName", "ServerInfo");
        MysqlDaoImpl.getInstance().saveOrUpdate("MData", m);

        m = new HashMap<>();
        m.put("_id", UUID.randomUUID().toString());
        m.put("pid", "be95b764-b063-4e23-99a1-b5ba789665e3");
        m.put("name", "name");
        m.put("level", 2);
        m.put("modelName", "common");
        m.put("entityName", "ServerInfo");
        MysqlDaoImpl.getInstance().saveOrUpdate("MData", m);

        m = new HashMap<>();
        m.put("_id", UUID.randomUUID().toString());
        m.put("pid", "be95b764-b063-4e23-99a1-b5ba789665e3");
        m.put("name", "host");
        m.put("level", 2);
        m.put("modelName", "common");
        m.put("entityName", "ServerInfo");
        MysqlDaoImpl.getInstance().saveOrUpdate("MData", m);

        m = new HashMap<>();
        m.put("_id", UUID.randomUUID().toString());
        m.put("pid", "be95b764-b063-4e23-99a1-b5ba789665e3");
        m.put("name", "type");
        m.put("level", 2);
        m.put("modelName", "common");
        m.put("entityName", "ServerInfo");
        MysqlDaoImpl.getInstance().saveOrUpdate("MData", m);
        MysqlDaoImpl.commit();

        m = new HashMap<>();
        m.put("_id", UUID.randomUUID().toString());
        m.put("pid", "be95b764-b063-4e23-99a1-b5ba789665e3");
        m.put("name", "port");
        m.put("level", 2);
        m.put("modelName", "common");
        m.put("entityName", "ServerInfo");
        MysqlDaoImpl.getInstance().saveOrUpdate("MData", m);
        MysqlDaoImpl.commit();
    }
}
