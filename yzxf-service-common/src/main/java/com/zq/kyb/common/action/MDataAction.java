package com.zq.kyb.common.action;


import com.zq.kyb.core.conn.websocket.adapter.ServiceAdapter;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CheckMData;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.PatternUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.*;

public class MDataAction extends BaseActionImpl {

    // ------ 解决开发过程中元数据同步的个问题 ------
    // 在项目的根目录下创建或查找文件MData.json
    public static String export_filepath = "./MData.json";


    /**
     * 通过模块名查询其下面的所有表和字段元数据
     *
     * @throws Exception
     */
    @GET
    @Path("/getMDataByModule")
    public void getMDataByModule() throws Exception {
        String moduleName = ControllerContext.getPString("moduleName");
        //查询所有的表
        List<Map<String, Object>> list = CheckMData.getMDataByModule(moduleName);
        toResult(200, list);
    }


    @GET
    @Path("/queryMDataObj")
    public void queryMDataObj() throws Exception {
        Map<String, Object> obj = new HashMap<>();
        obj.put("name", "MData");
        obj.put("level", 1.0);
        Map<String, Object> m = MysqlDaoImpl.getInstance().findOne2Map("MData", obj, null, null);
        toResult(200, JSONObject.fromObject(m));
    }

    @Override
    @PUT
    @Path("/save")
    public void save() throws Exception {
        saveMData();
    }

    @PUT
    @Path("/saveMData")
    public void saveMData() throws Exception {
        JSONObject values = ControllerContext.getContext().getReq().getContent();
        saveMData(values);
    }

    @Override
    public void query() throws Exception {
        //ControllerContext.getContext().getReq().getContent().put("_name", "___!MData");
        super.query();
    }

    @Override
    public void del() throws Exception {
        Map<String, Object> values = dao.findById2Map(entityName, ControllerContext.getPString("_id"), null, null);
        String _id = (String) values.get("_id");
        Integer level = (Integer) values.get("level");
        if (level < 2) {
            Map<String, Object> p = new HashMap<>();
            p.put("pid", _id);
            Map<String, Object> li = dao.findOne2Map(entityName, p, null, Dao.FieldStrategy.Include);
            String xxx = level == 0 ? "模块" : "";
            xxx = level == 1 ? "表" : xxx;

            String yyy = level == 0 ? "表和字段" : "";
            yyy = level == 1 ? "字段" : yyy;

            if (li != null && li.size() > 0) {
                throw new UserOperateException(400, "请先删除该" + xxx + " 下面的 " + yyy);
            }
        }
        //remove cache
        delMDataCache(values, level);
        super.del();
        //发送广播消息给其他服务器,告诉字段发生改变了
        sendChangeEvent(values);
    }

    private void delMDataCache(Map<String, Object> values, Integer level) {
        if (level == 0) {

        } else if (level == 1) {//表被删除,清除对应的cache
            String tName = (String) values.get("name");
            delTableCache(tName);
        } else if (level == 2) {//字段被删除,清除对应的cache
            String tName = (String) values.get("entityName");
            String fName = (String) values.get("name");
            // Dao.entityFieldsMap.remove(fName);
            List<Object> objects = Dao.entityFieldsMap.get(tName);
            if (objects != null) {
                List<Object> newList = new ArrayList<>();
                for (Object fo : objects) {
                    Map<String, Object> field = (Map<String, Object>) fo;
                    if (!fName.equals(field.get("name"))) {
                        newList.add(field);
                    }
                }
                Dao.entityFieldsMap.put(tName, newList);
            }
            Dao.fieldMap.remove(tName + "_" + fName);
        }
    }

    private void delTableCache(String tName) {
        Dao.entityMap.remove(tName);
        List<Object> objects = Dao.entityFieldsMap.get(tName);
        if (objects != null) {
            for (Object fo : objects) {
                Map<String, Object> field = (Map<String, Object>) fo;
                Dao.fieldMap.remove(tName + "_" + field.get("name"));
            }
        }
        Dao.entityFieldsMap.remove(tName);
    }


    @Override
    public void deleteMore() throws Exception {
        throw new UserOperateException(400, "请使用单条删除数据API");
    }

    public synchronized void saveMData(JSONObject values) throws Exception {
        String id = (String) values.get("_id");
        String name = (String) values.get("name");

        if (StringUtils.isEmpty(id)) {
            throw new UserOperateException(400, "_id 不能为空");
        }

        // **名称必须严格校验
        if (values.containsKey("name") && !"_id".equals(values.getString("name")) && !PatternUtils.exePattern("[A-Z,a-z,0-9]+", values.getString("name"))) {
            throw new UserOperateException(400, "name 必须是数字或字母");
        }
        if (!values.containsKey("_id")) {
            throw new UserOperateException(400, "必须设置_id参数");
        }
        Map<String, Object> my = MysqlDaoImpl.getInstance().findById2Map("MData", id, null, null);

        String oldName = my != null ? (String) my.get("name") : null;

        int level = 0;
        String pid;
        if (values.containsKey("pid")) {
            // 父亲
            pid = values.getString("pid");
        } else if (my != null) {
            pid = (String) my.get("pid");
        } else {
            throw new UserOperateException(400, "必须指定pid");
        }


        Map<String, Object> fu = MysqlDaoImpl.getInstance().findById2Map("MData", pid, null, null);
        if (fu != null) {
            Double plavel = Double.valueOf(fu.get("level").toString());
            plavel = plavel == null ? 0 : plavel;
            if (plavel == 0) {
                values.put("modelName", fu.get("name"));
                values.put("entityName", name);
            } else if (plavel == 1) {
                values.put("modelName", fu.get("modelName"));
                values.put("entityName", fu.get("name"));
                // 爷爷
                // pid = (String) fu.get("pid");
                // Map<String, Object> yeye = MysqlDaoImpl.getInstance().findById2Map("MData", pid, null, null);
                // values.put("modelName", yeye.get("name"));
            } else if (plavel == 2) {
                if ("creator".equals(name)) {
                    values.put("setByServer", true);
                }
                if (StringUtils.mapValueIsEmpty(values, "sortNo")) {
                    int sortNo = 0;
                    if ("createTime".equals(name)) {
                        sortNo = 100;
                    } else if ("creator".equals(name)) {
                        sortNo = 99;
                    } else if ("_id".equals(name)) {
                        sortNo = 98;
                    } else if ("name".equals(name)) {
                        sortNo = 97;
                    } else if ("title".equals(name)) {
                        sortNo = 96;
                    } else if ("desc".equals(name)) {
                        sortNo = -1;
                    }
                    values.put("sortNo", sortNo);
                }
            }
            level = plavel.intValue() + 1;
            values.put("level", level);// 层级
        } else {
            values.put("level", 0);
            values.put("modelName", name);//如果是添加模块,这补齐modelName字段
            values.put("entityName", null);
        }


        if (!StringUtils.mapValueIsEmpty(values, "name")) {
            if (level == 1 && !Character.isUpperCase(name.charAt(0))) {
                throw new UserOperateException(400, "表名必须首字母大写");
            }
            if (((level == 0 || (level == 2 && !"_id".equals(name))) && !Character.isLowerCase(name.charAt(0)))) {
                throw new UserOperateException(400, "模块名或字段名必须首字母小写");
            }
            //如果是模块或实体,名字不能重复
            if (level <= 1) {
                Map<String, Object> p = new HashMap<>();
                p.put("name", name);
                p.put("level", level);
                Map<String, Object> old = MysqlDaoImpl.getInstance().findOne2Map("MData", p, null, null);
                if (old != null && !id.equals(old.get("_id"))) {
                    throw new UserOperateException(400, "模块或实体,不能有重复的名字");
                }
            }
        }

        super.save();
        String modelName = values.getString("modelName");
        boolean isRenameTable = false;
        Boolean isCommonModule = "common".equals(modelName);

        if (level == 0) {
            List<Map<String, Object>> list = CheckMData.getMDataByModule(name);
            //如果模块名被修改,需要更新下属的表和字段的modelName字段:
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(oldName) && !name.equals(oldName)) {
                //String sql = "update "+entityName+" set modelName=? where level=1 and pid=?";
                //List<Object> params = new ArrayList<>();
                // params.add(name);
                // params.add(id);
                //MysqlDaoImpl.getInstance().exeSql(sql, params, tableName);

                Map<String, Object> p = new HashMap<>();
                p.put("level", 1);
                p.put("pid", id);
                List<Map<String, Object>> li = MysqlDaoImpl.getInstance().findAll2Map(entityName, p, null, null, null);
                for (Map<String, Object> map : li) {

                    map.put("modelName", name);
                    MysqlDaoImpl.getInstance().saveOrUpdate(entityName, map);
                    //修改实体下的所有字段

                    Map<String, Object> fieldP = new HashMap<>();
                    fieldP.put("level", 2);
                    fieldP.put("pid", map.get("_id"));
                    List<Map<String, Object>> fieldLi = MysqlDaoImpl.getInstance().findAll2Map(entityName, fieldP, null, null, null);
                    for (Map<String, Object> stringObjectMap : fieldLi) {
                        stringObjectMap.put("modelName", name);
                        stringObjectMap.put("entityName", map.get("name"));
                        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, map);
                    }
                }
                CheckMData.putMDataAllToCacheByList(list, false);
            }
            if (!isCommonModule) {
                ServiceAdapter.sendMDataChanged(name, list, null, null);
            }
        } else if (level == 1) {
            if (StringUtils.isEmpty(oldName)) {//新加表
                Map<String, Object> _idField = new HashMap<>();
                _idField.put("_id", UUID.randomUUID().toString());
                _idField.put("name", "_id");
                _idField.put("title", "主键");
                _idField.put("type", "string");
                _idField.put("level", 2);
                _idField.put("pid", id);
                _idField.put("readOnly", true);
                _idField.put("modelName", modelName);
                _idField.put("entityName", name);
                MysqlDaoImpl.getInstance().saveOrUpdate("MData", _idField);

                _idField = new HashMap<>();
                _idField.put("_id", UUID.randomUUID().toString());
                _idField.put("name", "createTime");
                _idField.put("title", "创建时间");
                _idField.put("type", "long");
                _idField.put("level", 2);
                _idField.put("pid", id);
                _idField.put("modelName", modelName);
                _idField.put("entityName", name);
                _idField.put("inputType", "dateTime");
                MysqlDaoImpl.getInstance().saveOrUpdate("MData", _idField);

                if (StringUtils.mapValueIsEmpty(values, "tableType") && "tree".equals(values.get("tableType"))) {
                    _idField = new HashMap<>();
                    _idField.put("_id", UUID.randomUUID().toString());
                    _idField.put("name", "pid");
                    _idField.put("title", "父id");
                    _idField.put("type", "string");
                    _idField.put("level", 2);
                    _idField.put("pid", id);
                    _idField.put("readOnly", true);
                    _idField.put("modelName", modelName);
                    _idField.put("entityName", name);
                    _idField.put("inputType", "dateTime");
                    MysqlDaoImpl.getInstance().saveOrUpdate("MData", _idField);
                }
            }

            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(oldName) && !name.equals(oldName)) {//如果表名被修改
                isRenameTable = true;

                Map<String, Object> p = new HashMap<>();
                p.put("level", 2);
                p.put("pid", id);
                List<Map<String, Object>> li = MysqlDaoImpl.getInstance().findAll2Map(entityName, p, null, null, null);
                //修改实体下的所有字段
                for (Map<String, Object> map : li) {
                    map.put("entityName", name);
                    MysqlDaoImpl.getInstance().saveOrUpdate(entityName, map);
                }
            }

            //            if (!StringUtils.mapValueIsEmpty(values, "sellerOwner")) {//如果是seller拥有的数据表
            //                Boolean s = (Boolean) values.get("sellerOwner");
            //                Map<String, Object> p = new HashMap<>();
            //                p.put("entityName", name);
            //                p.put("modelName", modelName);
            //                p.put("level", 2);
            //                p.put("pid", id);
            //                p.put("name", "sellerId");
            //                Map<String, Object> exist = MysqlDaoImpl.getInstance().findOne2Map("MData", p, null, Dao.FieldStrategy.Include);
            //                if (s && exist == null) {//检查这个表:"",是否有"sellerId"字段,没有自动添加
            //                    p.put("_id", UUID.randomUUID().toString());
            //                    p.put("title", "商户id");
            //                    p.put("type", "string");
            //                    MysqlDaoImpl.getInstance().saveOrUpdate("MData", p);
            //                }
            //            }

            //            if (!StringUtils.mapValueIsEmpty(values, "memberOwner")) {//如果是member拥有的数据表
            //                Boolean s = (Boolean) values.get("memberOwner");
            //                Map<String, Object> p = new HashMap<>();
            //                p.put("entityName", name);
            //                p.put("modelName", modelName);
            //                p.put("level", 2);
            //                p.put("pid", id);
            //                p.put("name", "memberId");
            //                Map<String, Object> exist = MysqlDaoImpl.getInstance().findOne2Map("MData", p, null, Dao.FieldStrategy.Include);
            //
            //                if (s && exist == null) {//检查这个表:"",是否有"sellerId"字段,没有自动添加
            //                    p.put("_id", UUID.randomUUID().toString());
            //                    p.put("title", "客户id");
            //                    p.put("type", "string");
            //                    MysqlDaoImpl.getInstance().saveOrUpdate("MData", p);
            //                }
            //            }

            List<Map<String, Object>> mDataByEntity = CheckMData.getMDataByEntity(name);
            if (mDataByEntity != null && mDataByEntity.size() > 0) {//更改表名
                mDataByEntity.get(0).put("$$reName", isRenameTable);
                mDataByEntity.get(0).put("$$oldName", oldName);
            }
            if (isCommonModule) {
                if (isRenameTable) {
                    CheckMData.reNameTable(Constants.mainDB, oldName, name);
                    CheckMData.checkMysqlTableList(mDataByEntity);
                } else {
                    CheckMData.createTableOnly(Constants.mainDB, name);
                }
            } else {
                ServiceAdapter.sendMDataChanged(modelName, mDataByEntity, null, null);
            }
            CheckMData.putMDataAllToCacheByList(mDataByEntity, false);

        } else if (level == 2) {
            //同步数据库结构及cache
            String tName = values.getString("entityName");
            if (isCommonModule) {
                CheckMData.createTableOnly(Constants.mainDB, tName);//如果这个字段没有被建表,就建表
                CheckMData.createMysqlTableField(Constants.mainDB, tName, values,null);
            } else {
                ServiceAdapter.sendMDataChanged(modelName, CheckMData.getMDataByEntity(tName), null, null);
            }
            CheckMData.putMDataFieldToCache(values);
        }
    }


    /**
     * 发送元数据发生改变的事件给对应的模块
     * 在修改元数据时调用
     *
     * @param values
     */
    private static void sendChangeEvent(Map values) throws Exception {
        String modelName = (String) values.get("modelName");
        Integer level = (Integer) values.get("level");
        if (StringUtils.isEmpty(modelName)) {
            throw new UserOperateException(400, "modelName is null!");
        }
        if (!"common".equals(modelName)) {
            List tableList = null;
            if (level == 0) {
                tableList = CheckMData.getMDataByModule(modelName);
            } else if (level == 1) {
                String tName = (String) values.get("name");
                tableList = CheckMData.getMDataByEntity(tName);
            } else if (level == 2) {
                String tName = (String) values.get("entityName");
                tableList = CheckMData.getMDataByEntity(tName);
            }
            if (tableList != null) {
                ServiceAdapter.sendMDataChanged(modelName, tableList, null, null);
            }
        }
    }

    /**
     * 发送元数据发生改变的事件给对应的模块
     * 在注册商户或店铺时调用
     *
     * @param type
     * @param ownerId
     */
    public static void sendChangeEventByReg(String type, String ownerId) throws Exception {
        String ownerField = "";
        if (type.equals("sellerId")) {
            ownerField = "sellerOwner";
        } else if (type.equals("storeId")) {
            ownerField = "storeOwner";
        }
        List<Map<String, Object>> modelList = CheckMData.getModuleListBySellerOwner(ownerField);
        for (Map<String, Object> map : modelList) {
            String modelName = (String) map.get("name");
            if (!"common".equals(modelName)) {
                List<Map<String, Object>> tableList = CheckMData.getMDataByModule(modelName, ownerField);
                if (tableList != null && tableList.size() > 0) {
                    ServiceAdapter.sendMDataChanged(modelName, tableList, type, ownerId);
                }
            }
        }
    }

    private static int countTemp=0;
    /**
     * 获取所有表
     *
     */
    @GET
    @Path("/getAllOrder")
    public void getAllOrder() throws Exception {
        List<String> returnField = new ArrayList<>();
        returnField.add("entityName");
        returnField.add("title");
        returnField.add("desc");
        String sql = "select entityName,`title`,`desc` from Mdata where level=1 group by entityName";
        List<Map<String,Object>> order = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,null);

        List<Object> param = new ArrayList<>();
        returnField.clear();
        returnField.add("desc");
        returnField.add("name");
        returnField.add("title");
        returnField.add("type");
        sql="select `desc`,`name`,`title`,`type` from Mdata where entityName=? and level=2 order by name asc";
        for(int i=0,len=order.size();i<len;i++){
            if(StringUtils.mapValueIsEmpty(order.get(i),"entityName")){
                continue;
            }
            param.clear();
            param.add(order.get(i).get("entityName").toString());
            List<Map<String,Object>> orderItem = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,param);
            order.get(i).put("orderItem",orderItem);
        }

        toResult(200,order);
    }

    /**
     * 获取所有API
     *
     */
    @GET
    @Path("/getAllAPI")
    public void getAllAPI() throws Exception {
        //先查模块
        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        returnField.add("title");
        returnField.add("name");
        String sql = "select title,name from resource where level=0 and title is not null " +
                " and name in ('account','bugtracking','common','crm','file','order','payment','wechat') order by name asc";
        List<Map<String,Object>> model = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);

        for(int i = 0,len=model.size();i<len;i++){
            if(StringUtils.mapValueIsEmpty(model.get(i),"title")){
                continue;
            }
            model.get(i).put("actionItem",getAction(model.get(i),returnField,params));
        }
        toResult(200,model);
    }

    /**
     * 获取API action列表
     * @return
     * @throws Exception
     */
    private List<Map<String,Object>> getAction(Map<String,Object> model,List<String> returnField,List<Object> params) throws Exception {
        returnField.add("pid");
        String sql = "select title,name,pid from resource where level=1 and title is not null" +
                "  and pid='"+model.get("name")+"' order by name asc";
        List<Map<String,Object>> action = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        for(int i = 0,len=action.size();i<len;i++){
            if(StringUtils.mapValueIsEmpty(action.get(i),"title")){
                continue;
            }
            action.get(i).put("apiItem",getAPI(action.get(i)));
        }
        return action;
    }

    /**
     * 获取API api列表
     * @return
     * @throws Exception
     */
    private List<Map<String,Object>> getAPI(Map<String,Object> action) throws Exception {
        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        returnField.add("title");
        returnField.add("name");
        returnField.add("apiType");
        returnField.add("desc");
        returnField.add("pid");
        String sql = "select title,name,apiType,`desc`,pid from resource" +
                " where level=2 and title is not null"+
                " and canUse<>false and pid='"+action.get("name")+"' order by name asc";
        List<Map<String,Object>> api = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        for(int i=0,len=api.size();i<len;i++){
            api.get(i).put("no",countTemp++);
        }

        return api;
    }


    public static void main(String[] args) {
//        checkName("Adfd");
//        checkName("Sdf01");
//        checkName("A");
//        checkName("中");
//        checkName("1");
    }


}
