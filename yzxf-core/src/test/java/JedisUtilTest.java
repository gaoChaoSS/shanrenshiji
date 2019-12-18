import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.init.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hujoey on 17/2/23.
 */
public class JedisUtilTest {
    public static void main(String[] args) {
        //测试数据库脏数据问题
        JedisUtilTest.checkDBSync();
    }

    private static void checkDBSync() {
        Constants.mainDB = "kyb_db";
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    String lockKey = "_sync_table_1";
                    try {
                        //CREATE TABLE `area` (id int(11) not null default 0,num int(11) default 0, primary key (id));
                        //insert sync_table (id,num) values(1,0);
                        //update sync_table set num=0;

                        JedisUtil.whileGetLock(lockKey, 30);
                        List<String> r = new ArrayList<>();
                        r.add("num");
                        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql("select num from kyb_db.sync_table where id=1", r, null);
                        if (li != null && li.size() > 0) {
                            Integer num = (Integer) li.get(0).get("num");
                            num++;
                            List<Object> p = new ArrayList<>();
                            p.add(num);
                            MysqlDaoImpl.getInstance().exeSql("update kyb_db.sync_table set num=? where id=1", p, "sync_table");
                        }
                        MysqlDaoImpl.commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        try {
                            MysqlDaoImpl.rollback();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        MysqlDaoImpl.clearContext();
                        JedisUtil.del(lockKey);//释放锁
                    }
                }
            }.start();
        }
    }
}
