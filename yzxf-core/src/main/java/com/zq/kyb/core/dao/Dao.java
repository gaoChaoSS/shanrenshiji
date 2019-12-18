package com.zq.kyb.core.dao;

import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Dao {

    enum FieldStrategy {
        Include, Exclude
    }

    // 建立2个Map，用于存放源数据的表和字段
    Map<String, Object> fieldMap = new ConcurrentHashMap<>();// 存放单个字段定义,用：tableName+"_"+fieldName作为Key
    Map<String, Object> entityMap = new ConcurrentHashMap<>();// 存放表定义
    Map<String, List<Object>> entityFieldsMap = new ConcurrentHashMap<>();// 存放一个表下的字段集合


    /**
     * 根据对应的逻辑找到对应的数据库+表名
     *
     * @param tableName
     * @return
     */
    static String getDataBaseName(String tableName) {

        Map<String, Object> tableDef = (Map<String, Object>) Dao.entityMap.get(tableName);
        if (tableDef != null && !StringUtils.mapValueIsEmpty(tableDef, "storeOwner") && (Boolean) tableDef.get("storeOwner")) {
            String storeId = ControllerContext.getPString("storeId");
            if (StringUtils.isEmpty(storeId)) {
                throw new UserOperateException(400, "storeId is null");
            }
            return getStoreDBName(storeId);
        }
        if (tableDef != null && !StringUtils.mapValueIsEmpty(tableDef, "sellerOwner") && (Boolean) tableDef.get("sellerOwner")) {
            String sellerId = ControllerContext.getContext().getCurrentSellerId();
            if (StringUtils.isEmpty(sellerId)) {
                throw new UserOperateException(400, "sellerId is null");
            }
            return getSellerDBName(sellerId);
        }
        return Constants.mainDB;
    }

    static boolean isSync(String tableName) {
        Map<String, Object> entity = (Map<String, Object>) Dao.entityMap.get(tableName);
        Boolean isSync = entity != null && !StringUtils.mapValueIsEmpty(entity, "isSyncTable") && (Boolean) entity.get("isSyncTable");
        return isSync;
    }

    static String getSellerDBName(String sellerId) {
        return "Seller_" + sellerId;
    }

    static String getStoreDBName(String storeId) {
        return "Store_" + storeId;
    }

    static String getFullTableName(String tableName) {
        return getFullTableName(getDataBaseName(tableName), tableName);
    }

    static String getFullTableName(String db, String tableName) {
        return "`" + db + "`.`" + tableName + "`";
    }

    void destory();

    /**
     * 新增对象
     *
     * @param collectionName
     *            集合名称
     * @param obj
     *            要添加的对象 可以是Map 或 Map<String,Object>
     * @return 受影响的行数
     */
//    int insert(String collectionName, Map<String, Object> obj) throws Exception;

    // int remove(String
    // collectionName,Map<String,Object><String,Object> obj);
    // int remove(String collectionName,String jsonString);

    /**
     * 删除对象
     *
     * @param collectionName 集合名称
     * @param id             数据主键值
     * @return 受影响的行数
     */
    int remove(String collectionName, String id) throws Exception;

    /**
     * 删除集合下所有数据
     *
     * @param collectionName
     *            集合名称
     */
    // void removeAll(String collectionName);

    // int remove(String collectionName, Map<String, Object> obj);

    /**
     * 修改对象
     *
     * @param collectionName 集合名称
     * @param newObj         要修改的对象 可以是Map 或 Map<String,Object>
     * @return 受影响的行数
     */
    int update(String collectionName, Map<String, Object> newObj) throws Exception;

    /**
     * 更新更多
     *
     * @param collectionName
     * @param newObj
     * @param params
     * @return
     */
    int updateAll(String collectionName, Map<String, Object> newObj, Map<String, Object> params) throws Exception;

    /**
     * 查询一个对象
     *
     * @param collectionName 集合名称
     * @param obj            查询条件 可以是Map 或 Map<String,Object> {"a":1,"b":"test"} 表示 where a = 1 and b = "test"
     * @return 查询到得对象 Map
     */
    Map<String, Object> findOne2Map(String collectionName, Map<String, Object> obj, String[] fields, FieldStrategy fs) throws Exception;

    /**
     * 根据ID查询一个对象
     *
     * @param collectionName 集合名称
     * @param id             数据主键值
     * @return 查询到得对象 Map
     * @throws Exception
     */
    Map<String, Object> findById2Map(String collectionName, String id, String[] fields, FieldStrategy fs) throws Exception;

    /**
     * 查询集合下所有记录
     *
     * @param collectionName 集合名称
     * @param orderBy        排序规则 Map {age :1,name:-1} order by age asc , name desc
     * @return 查询到得对象 Map
     * @throws Exception
     */
    List<Map<String, Object>> findAll2Map(String collectionName, Map<String, Object> p, Map<String, Object> orderBy, String[] fields, FieldStrategy fs) throws Exception;

    /**
     * 查询集合总记录数
     *
     * @param collectionName 集合名称
     * @return 集合总记录数
     * @throws Exception
     */
    long findCount(String collectionName, Map<String, Object> params) throws Exception;

    /**
     * 移除集合
     *
     * @param collectionName
     */
    void removeAll(String collectionName) throws Exception;

    /**
     * 分组查询
     * <p>
     * BasicDBObject key = new BasicDBObject("baseId", true); BasicDBObject cond = new BasicDBObject(); BasicDBObject initial = new BasicDBObject("buyCount", 0); String reduce =
     * "function(obj,pre){pre.buyCount += obj.buyCount}"; List returnList = MongoDaoImpl.getInstance().group(collectionName, key, cond, initial)
     *
     * @param collectionName
     * @param key
     * @param cond
     * @param initial
     * @param reduce
     * @return
     * @throws Exception
     */
    List<Object> group(String collectionName, Map<String, Object> key, Map<String, Object> cond, Map<String, Object> initial, String reduce, String finial) throws Exception;

    /**
     * 分页查询，需结合 findCount 使用,先查询出总记录数，根据分页算法，得出 skip
     *
     * @param collectionName 集合名称
     * @param skip           跳过多少条记录
     * @param pageSize       取多少条记录
     * @return 查询到得对象 Map
     */
    List<Map<String, Object>> findPage2Map(String collectionName, long skip, int pageSize, Map<String, Object> params, Map<String, Object> orderBy, String[] fields, FieldStrategy fs) throws Exception;

    Double sum(String collectionName, Map<String, Object> params, String sumField) throws Exception;

    Long saveOrUpdate(String entityName, Map<String, Object> values) throws Exception;

}