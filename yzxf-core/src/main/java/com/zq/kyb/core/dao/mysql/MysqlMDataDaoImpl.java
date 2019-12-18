package com.zq.kyb.core.dao.mysql;

import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.util.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujoey on 16/12/19.
 */
public abstract class MysqlMDataDaoImpl implements Dao {

    abstract Connection getConn();


    /**
     * @throws SQLException
     * @Description: 获取数据库基本信息
     */
    public void getDataBaseInfo() throws SQLException {
        ResultSet rs = null;
        Logger.getLogger(getClass()).info("\n---获取数据库基本信息:");
        DatabaseMetaData dbmd = getConn().getMetaData();
        Logger.getLogger(getClass()).info("-数据库已知的用户: " + dbmd.getUserName());
        Logger.getLogger(getClass()).info("-数据库的系统函数的逗号分隔列表: " + dbmd.getSystemFunctions());
        Logger.getLogger(getClass()).info("-数据库的时间和日期函数的逗号分隔列表: " + dbmd.getTimeDateFunctions());
        Logger.getLogger(getClass()).info("-数据库的字符串函数的逗号分隔列表: " + dbmd.getStringFunctions());
        Logger.getLogger(getClass()).info("-数据库供应商用于 'schema' 的首选术语: " + dbmd.getSchemaTerm());
        Logger.getLogger(getClass()).info("-数据库URL: " + dbmd.getURL());
        Logger.getLogger(getClass()).info("-是否允许只读:" + dbmd.isReadOnly());
        Logger.getLogger(getClass()).info("-数据库的产品名称:" + dbmd.getDatabaseProductName());
        Logger.getLogger(getClass()).info("-数据库的版本:" + dbmd.getDatabaseProductVersion());
        Logger.getLogger(getClass()).info("-驱动程序的名称:" + dbmd.getDriverName());
        Logger.getLogger(getClass()).info("-驱动程序的版本:" + dbmd.getDriverVersion());

        Logger.getLogger(getClass()).info("-数据库中使用的表类型:");
        rs = dbmd.getTableTypes();
        while (rs.next()) {
            Logger.getLogger(getClass()).info("  " + rs.getString("TABLE_TYPE"));
        }
    }

    /**
     * @throws SQLException
     * @throws
     * @Description:获得数据库中所有Schemas(对应于oracle中的Tablespace)
     */
    public void getSchemasInfo() throws SQLException {

        ResultSet rs = null;
        Logger.getLogger(getClass()).info("\n---获得数据库中所有Schemas(对应于oracle中的Tablespace):");
        DatabaseMetaData dbmd = getConn().getMetaData();
        rs = dbmd.getSchemas();
        while (rs.next()) {
            String tableSchem = rs.getString("TABLE_SCHEM");
            Logger.getLogger(getClass()).info(tableSchem);
        }

    }

    /**
     * @throws SQLException
     * @throws
     * @Description: 获取数据库中所有的表信息
     * @author: chenzw
     * @CreateTime: 2014-1-27 下午5:08:28
     */
    public List<String> getTableList() throws SQLException {

        ResultSet rs = null;
        Logger.getLogger(getClass()).info("\n---获取数据库中所有的表信息");
        List<String> li = new ArrayList<>();
        /**
         * 设置连接属性,使得可获取到表的REMARK(备注)
         */
        DatabaseMetaData dbmd = getConn().getMetaData();
        String[] types = {"TABLE"};// 可以为：{ "TABLE","VIEW" }
        rs = dbmd.getTables(null, null, "%", types);// %表示所有
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME"); // 表名
            // String tableType = rs.getString("TABLE_TYPE"); // 表类型
            // String remarks = rs.getString("REMARKS"); // 表备注
            Logger.getLogger(MysqlDaoImpl.class).info("-表：" + tableName);
            // Logger.getLogger(getClass()).info("-表类型：" + tableType);
            // Logger.getLogger(getClass()).info("-表备注：" + remarks);
            // getTablesInfo(tableName);
            // getColumnsInfo(tableName);

            li.add(tableName);
        }
        return li;
    }

    public void getTablesInfo(String db, String tableName) throws SQLException {

        ResultSet rs = null;
        /**
         * 设置连接属性,使得可获取到表的REMARK(备注)
         */
        DatabaseMetaData dbmd = getConn().getMetaData();
        /**
         * 获取给定类别中使用的表的描述。 方法原型:ResultSet getTables(String catalog,String schemaPattern,String tableNamePattern,String[] types); catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。 schema -
         * 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),或多字符通配符("%"); tableNamePattern - 表名称;可包含单字符通配符("_"),或多字符通配符("%"); types - 表类型数组; "TABLE"、"VIEW"、"SYSTEM TABLE"
         * 、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM";null表示包含所有的表类型;可包含单字符通配符("_"),或多字符通配符("%");
         */
        rs = dbmd.getTables(db, null, tableName, new String[]{"TABLE", "VIEW"});

        while (rs.next()) {
            String tableCat = rs.getString("TABLE_CAT"); // 表类别(可为null)
            String tableSchemaName = rs.getString("TABLE_SCHEM");// 表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
            String t = rs.getString("TABLE_NAME"); // 表名
            String tableType = rs.getString("TABLE_TYPE"); // 表类型,典型的类型是
            // "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS"
            // 和 "SYNONYM"。
            String remarks = rs.getString("REMARKS"); // 表备注
            Logger.getLogger(getClass()).info(tableCat + " - " + tableSchemaName + " - " + t + " - " + tableType + " - " + remarks);
        }
    }

    /**
     * 获取表主键信息
     *
     * @param tableName
     * @throws SQLException
     */
    public void getPrimaryKeysInfo(String tableName) throws SQLException {

        ResultSet rs = null;
        DatabaseMetaData dbmd = getConn().getMetaData();
        /**
         * 获取对给定表的主键列的描述 方法原型:ResultSet getPrimaryKeys(String catalog,String schema,String table); catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。 schema -
         * 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),或多字符通配符("%"); table - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
         */
        rs = dbmd.getPrimaryKeys(null, null, tableName);

        while (rs.next()) {
            String tableCat = rs.getString("TABLE_CAT"); // 表类别(可为null)
            String tableSchemaName = rs.getString("TABLE_SCHEM");// 表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
            String t = rs.getString("TABLE_NAME"); // 表名
            String columnName = rs.getString("COLUMN_NAME");// 列名
            short keySeq = rs.getShort("KEY_SEQ");// 序列号(主键内值1表示第一列的主键，值2代表主键内的第二列)
            String pkName = rs.getString("PK_NAME"); // 主键名称

            Logger.getLogger(getClass()).info(tableCat + " - " + tableSchemaName + " - " + t + " - " + columnName + " - " + keySeq + " - " + pkName);
        }
    }

    /**
     * 获取表索引
     *
     * @throws SQLException
     */
    public void getIndexInfo() throws SQLException {

        ResultSet rs = null;
        DatabaseMetaData dbmd = getConn().getMetaData();
        /**
         * 获取给定表的索引和统计信息的描述 方法原型:ResultSet getIndexInfo(String catalog,String schema,String table,boolean unique,boolean approximate) catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。 schema -
         * 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),或多字符通配符("%"); table - 表名称;可包含单字符通配符("_"),或多字符通配符("%"); unique - 该参数为 true时,仅返回唯一值的索引; 该参数为 false时,返回所有索引;
         * approximate - 该参数为true时,允许结果是接近的数据值或这些数据值以外的值;该参数为 false时,要求结果是精确结果;
         */
        rs = dbmd.getIndexInfo(null, null, "CUST_INTER_TF_SERVICE_REQ", false, true);
        while (rs.next()) {
            String tableCat = rs.getString("TABLE_CAT"); // 表类别(可为null)
            String tableSchemaName = rs.getString("TABLE_SCHEM");// 表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
            String tableName = rs.getString("TABLE_NAME"); // 表名
            boolean nonUnique = rs.getBoolean("NON_UNIQUE");// 索引值是否可以不唯一,TYPE为
            // tableIndexStatistic时索引值为
            // false;
            String indexQualifier = rs.getString("INDEX_QUALIFIER");// 索引类别（可能为空）,TYPE为
            // tableIndexStatistic
            // 时索引类别为
            // null;
            String indexName = rs.getString("INDEX_NAME");// 索引的名称 ;TYPE为
            // tableIndexStatistic
            // 时索引名称为 null;
            /**
             * 索引类型： tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息 tableIndexClustered - 此为集群索引 tableIndexHashed - 此为散列索引 tableIndexOther - 此为某种其他样式的索引
             */
            short type = rs.getShort("TYPE");// 索引类型;
            short ordinalPosition = rs.getShort("ORDINAL_POSITION");// 在索引列顺序号;TYPE为
            // tableIndexStatistic
            // 时该序列号为零;
            String columnName = rs.getString("COLUMN_NAME");// 列名;TYPE为
            // tableIndexStatistic时列名称为
            // null;
            String ascOrDesc = rs.getString("ASC_OR_DESC");// 列排序顺序:升序还是降序[A:升序;
            // B:降序];如果排序序列不受支持,可能为
            // null;TYPE为
            // tableIndexStatistic时排序序列为
            // null;
            int cardinality = rs.getInt("CARDINALITY"); // 基数;TYPE为
            // tableIndexStatistic
            // 时,它是表中的行数;否则,它是索引中唯一值的数量。
            int pages = rs.getInt("PAGES"); // TYPE为
            // tableIndexStatisic时,它是用于表的页数,否则它是用于当前索引的页数。
            String filterCondition = rs.getString("FILTER_CONDITION"); // 过滤器条件,如果有的话(可能为
            // null)。

            Logger.getLogger(getClass()).info(tableCat + " - " + tableSchemaName + " - " + tableName + " - " + nonUnique + " - " + indexQualifier + " - " + indexName + " - " + type + " - " + ordinalPosition + " - "
                    + columnName + " - " + ascOrDesc + " - " + cardinality + " - " + pages + " - " + filterCondition);
        }
    }

    /**
     * 获取一个表的某一列
     *
     * @param tableName
     * @param fieldName
     * @return
     * @throws SQLException
     */
    public Map<String, Object> getColumn(String dbName, String tableName, String fieldName) throws SQLException {
        List<Map<String, Object>> li = getColumnList(dbName, tableName, fieldName);
        return li != null && li.size() > 0 ? li.get(0) : null;
    }

    /**
     * 获取一个表的所有列
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> getColumnList(String dbName, String tableName) throws SQLException {
        return getColumnList(dbName, tableName, null);
    }

    /**
     * 获取表中列值信息
     *
     * @param tableName
     * @param fieldName
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> getColumnList(String dbName, String tableName, String fieldName) throws SQLException {

        ResultSet rs;

        DatabaseMetaData dbmd = getConn().getMetaData();
        /**
         * 获取可在指定类别中使用的表列的描述。
         *
         * 方法原型:ResultSet getColumns(String catalog,String schemaPattern,String tableNamePattern,String columnNamePattern)<br/>
         * catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。
         *
         * schema - 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),或多字符通配符("%"); <br/>
         * tableNamePattern - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
         *
         * columnNamePattern - 列名称; ""表示获取列名为""的列(当然获取不到);null表示获取所有的列;可包含单字符通配符("_"),或多字符通配符("%");
         */
        rs = dbmd.getColumns(dbName, null, tableName, fieldName);
        List<Map<String, Object>> mapList = new ArrayList<>();
        Logger.getLogger(MysqlDaoImpl.class).info("--" + tableName + "的字段列表:");

        while (rs.next()) {
            // String tableCat = rs.getString("TABLE_CAT"); // 表类别（可能为空）
            // String tableSchemaName = rs.getString("TABLE_SCHEM"); //
            // 表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
            // String tableName_ = rs.getString("TABLE_NAME"); // 表名
            String columnName = rs.getString("COLUMN_NAME"); // 列名
            int dataType = rs.getInt("DATA_TYPE"); // 对应的java.sql.Types的SQL类型(列类型ID)
            String dataTypeName = rs.getString("TYPE_NAME"); // java.sql.Types类型名称(列类型名称)
            int columnSize = rs.getInt("COLUMN_SIZE"); // 列大小
            int decimalDigits = rs.getInt("DECIMAL_DIGITS"); // 小数位数
            int numPrecRadix = rs.getInt("NUM_PREC_RADIX"); // 基数（通常是10或2） --未知
            /**
             * 0 (columnNoNulls) - 该列不允许为空 1 (columnNullable) - 该列允许为空 2 (columnNullableUnknown) - 不确定该列是否为空
             */
            int nullAble = rs.getInt("NULLABLE"); // 是否允许为null
            String remarks = rs.getString("REMARKS"); // 列描述
            String columnDef = rs.getString("COLUMN_DEF"); // 默认值
            int charOctetLength = rs.getInt("CHAR_OCTET_LENGTH"); // 对于 char
            // 类型，该长度是列中的最大字节数
            int ordinalPosition = rs.getInt("ORDINAL_POSITION"); // 表中列的索引（从1开始）
            /**
             * ISO规则用来确定某一列的是否可为空(等同于NULLABLE的值:[ 0:'YES'; 1:'NO'; 2:''; ]) YES -- 该列可以有空值; NO -- 该列不能为空; 空字符串--- 不知道该列是否可为空
             */
            String isNullAble = rs.getString("IS_NULLABLE");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", columnName);
            map.put("type", dataTypeName.toLowerCase());
            map.put("size", columnSize);
            map.put("isNotNull", nullAble);
            map.put("columnDef", columnDef);
            mapList.add(map);

            /**
             * 指示此列是否是自动递增 YES -- 该列是自动递增的 NO -- 该列不是自动递增 空字串--- 不能确定该列是否自动递增
             */
            // String isAutoincrement = rs.getString("IS_AUTOINCREMENT");
            // //该参数测试报错
            Logger.getLogger(MysqlDaoImpl.class).debug(
                    "列名：" + columnName + "" +
                            "，类型：" + dataType + "" +
                            "，类型名称：" + dataTypeName + "" +
                            "，大小：" + columnSize + "" +
                            "，" + decimalDigits + "" +
                            "，" + numPrecRadix + "" +
                            "，是否为空：" + nullAble + "" +
                            "，备注：" + remarks + "" +
                            "，默认值" + columnDef + "" +
                            "，最大字节数：" + charOctetLength + "" +
                            "，索引：" + ordinalPosition + "" +
                            "，是否为空：" + isNullAble);

        }
        return mapList;
    }


}
