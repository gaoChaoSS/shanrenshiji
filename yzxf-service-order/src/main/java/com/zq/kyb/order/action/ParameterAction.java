package com.zq.kyb.order.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.BigDecimalUtil;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ParameterAction extends BaseActionImpl {

    public static void checkAdmin() throws Exception{
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + other.get("agentId"));
        if(!"3".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }
    }

    /**
     * 查询配置
     * @throws Exception
     */
    @GET
    @Path("/getParameter")
    public void getParameter() throws Exception{
        checkAdmin();
        String isType = ControllerContext.getPString("isType");

        if(StringUtils.isNotEmpty(isType)){
            toResult(200,getType());
        }else{
            List<Map<String,Object>> re = getParameter(ControllerContext.getContext().getReq().getContent());
            if(re.size()>1){
                toResult(200,re);
            }else if(re.size()!=0){
                toResult(200,re.get(0));
            }
        }
    }

    public List<Map<String,Object>> getParameter(Map<String,Object> filter) throws Exception{
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> orderBy = new HashMap<>();

        orderBy.put("no",1);
        if(!StringUtils.mapValueIsEmpty(filter,"type")){
            params.put("type",filter.get("type"));
        }
        if(!StringUtils.mapValueIsEmpty(filter,"_id")){
            params.put("_id",filter.get("_id"));
        }
        return MysqlDaoImpl.getInstance().findAll2Map(entityName,params,orderBy,null,null);
    }


    public List<Map<String,Object>> getType() throws Exception{
        List<String> returnFields = new ArrayList<>();
        returnFields.add("typeTitle");
        returnFields.add("type");
        returnFields.add("unit");
        String sql = "select typeTitle,type,unit from Parameter group by type order by createTime";

        return MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,null);
    }

    /**
     * 获取单个配置的值
     * @param type
     * @return
     * @throws Exception
     */
    public static double getValueOne(String type) throws Exception{
        Map<String,Object> params = new HashMap<>();
        params.put("type",type);
        Map<String,Object> re = MysqlDaoImpl.getInstance().findOne2Map("Parameter",params,new String[]{"val"}, Dao.FieldStrategy.Include);
        if(re==null || re.size()==0){
            throw new UserOperateException(500,"未配置"+type+"参数");
        }
        return NumberUtils.toDouble(re.get("val").toString());
    }
    /**
     * 获取多个配置的值
     * @param type
     * @return
     * @throws Exception
     */
    public static List<Map<String,Object>> getValueMore(String type) throws Exception{
        Map<String,Object> params = new HashMap<>();
        params.put("type",type);
        Map<String,Object> orderBy = new HashMap<>();
        orderBy.put("no",1);
        return MysqlDaoImpl.getInstance().findAll2Map("Parameter",params,orderBy,new String[]{"key","val","no"},Dao.FieldStrategy.Include);
    }

//    @GET
//    @Path("/getParameterType")
//    public void getParameterType() throws Exception{
//        checkAdmin();
//        toResult(200,getType());
//    }
    /**
     * 保存 type为单个 的配置
     * @throws Exception
     */
    @POST
    @Path("/saveParameter")
    public void saveParameter() throws Exception{
        checkAdmin();

        Map<String,Object> req = ControllerContext.getContext().getReq().getContent();

        if(StringUtils.mapValueIsEmpty(req,"_id")){
            throw new UserOperateException(500,"设置参数不存在");
        }

        Map<String,Object> old = MysqlDaoImpl.getInstance().findById2Map("Parameter",req.get("_id").toString(),null,null);

        if(old==null || old.size()==0){
            throw new UserOperateException(500,"设置参数不存在");
        }

        req.put("oldVal",old.get("val").toString());
        saveParameter(req);
    }

    public void saveParameter(Map<String,Object> item) throws Exception{
        Map<String,Object> saveItem = new HashMap<>();

        if(StringUtils.mapValueIsEmpty(item,"type")
                || StringUtils.mapValueIsEmpty(item,"_id")
                || StringUtils.mapValueIsEmpty(item,"val")){
            throw new UserOperateException(500,"设置参数失败");
        }

        saveItem.put("_id",item.get("_id"));
        saveItem.put("val",item.get("val"));

        MysqlDaoImpl.getInstance().saveOrUpdate(entityName,saveItem);

        saveParameterLog(item);
    }

    /**
     * 保存 type为多个 的配置
     * @throws Exception
     */
    @POST
    @Path("/saveParameterMore")
    public void saveParameterMore() throws Exception{
        checkAdmin();

        List<Map<String,Object>> req = ControllerContext.getContext().getReq().getContent().getJSONArray("items");

        //若是百分比单位的多个配置，则累计所有百分比是否等于100
        if(req.size()!=0 && req.get(0).get("unit").equals("%")){
            double num = 0;
            int oldIndex=0;
            Map<String,Object> filter= new HashMap<>();
            filter.put("type",req.get(0).get("type").toString());
            List<Map<String,Object>> list = getParameter(filter);

            //可能只修改了部分数据，若找到修改的数据，则累计修改的数据，没有则累计旧数据
            for(Map<String,Object> old : list){
                for(int i=oldIndex;i<req.size();i++){
                    if(old.get("key").equals(req.get(i).get("key"))){
                        if(StringUtils.mapValueIsEmpty(req.get(i),"val")){
                            throw new UserOperateException(500,"值不能为空或零");
                        }
                        if(!Pattern.matches("^\\d+(\\.\\d{1,3})?$",req.get(i).get("val").toString())){
                            throw new UserOperateException(500,"小数位数不能超过三位");
                        }
                        old.put("oldVal",old.get("val"));
                        old.put("val",req.get(i).get("val"));
                        oldIndex=i;
                    }
                }
                num = BigDecimalUtil.add(Double.parseDouble(old.get("val").toString()),num);
            }
            num = BigDecimalUtil.fixDoubleNumProfit(num);
            if(num!=100){
                throw new UserOperateException(500,"分配比例不等于100%");
            }
        }

        for(Map<String,Object> item : req){
            saveParameter(item);
        }
    }

    /**
     * 保存修改日志
     * @throws Exception
     */
    public void saveParameterLog(Map<String,Object> item) throws Exception{
        item.put("_id", ZQUidUtils.genUUID());
        item.put("createTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("ParameterLog",item);
    }

    /**
     * 查询修改日志
     * @throws Exception
     */
    @GET
    @Path("/getParameterLog")
    public void getParameterLog() throws Exception{
        long pageNo = 1;
        long indexNum = 0;
        int pageSize = 20;

        Map<String,Object> req = ControllerContext.getContext().getReq().getContent();

        Map<String,Object> params = new HashMap<>();
        Map<String,Object> orderBy = new HashMap<>();
        orderBy.put("createTime",0);

        if(!StringUtils.mapValueIsEmpty(req,"pageNo")){
            pageNo = Long.parseLong(req.get("pageNo").toString());
        }
        indexNum = (pageNo-1)*pageSize;

        if(!StringUtils.mapValueIsEmpty(req,"pageSize")){
            pageSize = Integer.parseInt(req.get("pageSize").toString());
        }
        if(!StringUtils.mapValueIsEmpty(req,"type")){
            params.put("type",req.get("type"));
        }
        if(!StringUtils.mapValueIsEmpty(req,"key")){
            params.put("key",req.get("key"));
        }
        if(!StringUtils.mapValueIsEmpty(req,"_createTime")){
            params.put("createTime",req.get("_createTime"));
        }
        if(!StringUtils.mapValueIsEmpty(req,"orderBy")){
            orderBy.put("createTime",Integer.parseInt(req.get("orderBy").toString()));
        }

        toResult(200,MysqlDaoImpl.getInstance().findPage2Map("ParameterLog",indexNum,pageSize,params,orderBy,null,null));
    }
}
