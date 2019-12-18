import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by hujoey on 16/12/19.
 */
public class DBTest {
    public static void main(String[] args) throws SQLException {
        MysqlDaoImpl.getInstance().getTablesInfo("cms_db", "Article");
        //List<Map<String, Object>> r = MysqlDaoImpl.getInstance().queryBySql("select * from cms_db.article;", null, null, null);
        //System.out.println(r.size());
    }
}
