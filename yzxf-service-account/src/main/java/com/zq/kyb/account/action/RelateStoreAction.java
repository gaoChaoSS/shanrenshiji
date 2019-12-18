package com.zq.kyb.account.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelateStoreAction extends BaseActionImpl {
    public static String RELATE_FROM_KYB="kyb";

    public static String DEFAULT_RELATE_FROM=RELATE_FROM_KYB;

    /**
     * 获取关联商家配置
     * @throws Exception
     */
    @GET
    @Path("/getRelateStoreConf")
    public void getRelateStoreConf() throws Exception{
        String relateFrom = ControllerContext.getPString("relateFrom");
        String relateStoreId = ControllerContext.getPString("relateStoreId");
        String localSellerId = ControllerContext.getPString("localSellerId");
        String where = " where t1.relateFrom=?";
        List<String> returnFields = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        params.add(relateFrom);

        if(StringUtils.isEmpty(relateStoreId) && StringUtils.isEmpty(localSellerId)){
            throw new UserOperateException(500,"关联商家ID或本地商家ID不能为空");
        }else if (StringUtils.isEmpty(relateStoreId)){
            where+= " and t1.relateStoreId = ?";
            params.add(relateStoreId);
        }else{
            where+= " and t1.localSellerId = ?";
            params.add(relateStoreId);
        }
        if(StringUtils.isEmpty(relateFrom)){
            throw new UserOperateException(500,"关联商家类型不能为空");
        }

        String sql = "select" +
                " value" +
                ",key" +
                " from RelateStore t1" +
                " left join SellerConf t2 on t1.localSellerId = t2.sellerId" +
                where;
        toResult(200,MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params));
    }

    /**
     * 获取用户配置（商家、服务站）
     * @throws Exception
     */
    @GET
    @Path("/getUserConf")
    public void getUserConf() throws Exception {
        String userId = ControllerContext.getPString("userId");
        Map<String,Object> p = new HashMap<>();
        p.put("sellerId",userId);
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().findAll2Map("SellerConf",p,null,null,null);

        Map<String,Object> conf = new HashMap<>();
        for(Map<String,Object> item : re){
            conf.put(item.get("key").toString(),item.get("value"));
        }
        toResult(200,conf);
    }

    /**
     * 获取关联商家数据
     * @throws Exception
     */
    @GET
    @Path("/getRelateStore")
    public void getRelateStore() throws Exception{
        String relateFrom = ControllerContext.getPString("relateFrom");
        String relateStoreId = ControllerContext.getPString("relateStoreId");
        String localSellerId = ControllerContext.getPString("localSellerId");

        if(StringUtils.isEmpty(relateFrom)){
            relateFrom=DEFAULT_RELATE_FROM;
        }
//        if(StringUtils.isEmpty(relateStoreId) && StringUtils.isEmpty(localSellerId)){
//            throw new UserOperateException(500,"获取快易帮关联商家数据失败");
//        }

        toResult(200,getRelateStore(relateStoreId,localSellerId,relateFrom));
    }
    /**
     * 获取关联商家数据
     * @return
     * @throws Exception
     */
    public Map<String,Object> getRelateStore(String relateStoreId,String localSellerId,String relateFrom) throws Exception{
        String userType = ControllerContext.getPString("userType");
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();

        String fields = "";
        String where = " where t1.relateFrom = ?";
        params.add(relateFrom);

        if(StringUtils.isEmpty(userType)){
            userType = "Seller";
            fields += ",t2.integralRate" +
                    ",t2.belongAreaValue";
            returnFields.add("belongAreaValue");
            returnFields.add("integralRate");
        }

        if(!StringUtils.isEmpty(relateStoreId)){
            where += " and t1.relateStoreId = ?";
            params.add(relateStoreId);
        }
        if(!StringUtils.isEmpty(localSellerId)){
            where += " and t1.localSellerId = ?";
            params.add(localSellerId);
        }

        returnFields.add("localSellerId");
        returnFields.add("relateStoreId");
        returnFields.add("userType");
        returnFields.add("name");
        returnFields.add("canUse");

        String sql = "select " +
                " t1.localSellerId" +
                ",t1.relateStoreId" +
                ",t1.userType" +
                ",t2.name" +
                ",t2.canUse" +
                fields +
                " from relateStore t1" +
                " left join "+userType+" t2 on t1.localSellerId = t2._id" +
                where;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(re!=null && re.size()!=0){
            return re.get(0);
        }
        return null;
    }

    /**
     * 建立关联关系
     * 快易帮关联商家数据
     * @return
     * @throws Exception
     */
    @POST
    @Path("/relateStore")
    public void relateStore() throws Exception{
        String localSellerId = ControllerContext.getPString("localSellerId");//普惠生活商家ID
        String password = ControllerContext.getPString("localSellerPwd");//普惠生活商家密码
        String relateStoreId = ControllerContext.getPString("relateStoreId");//关联第三方的店铺ID
        String relateFrom = ControllerContext.getPString("relateFrom");//来源

        Map<String,Object> returnData = new HashMap<>();

        try{
            if(StringUtils.isEmpty(localSellerId)){
                returnData.put("returnStatus","FAIL");
                returnData.put("returnDesc","获取普惠生活商家ID失败");
                throw new UserOperateException(500,"获取普惠生活商家ID失败");
            }
            if(StringUtils.isEmpty(relateStoreId)){
                returnData.put("returnStatus","FAIL");
                returnData.put("returnDesc","获取关联店铺ID失败");
                throw new UserOperateException(500,"获取关联店铺ID失败");
            }
            if(StringUtils.isEmpty(password)){
                returnData.put("returnStatus","FAIL");
                returnData.put("returnDesc","获取关联店铺密码失败");
                throw new UserOperateException(500,"获取关联店铺密码失败");
            }

            Map<String,Object> p = new HashMap<>();
            p.put("sellerId",localSellerId);
            p.put("password",MessageDigestUtils.digest(password));
            Map<String,Object> seller = MysqlDaoImpl.getInstance().findOne2Map("User",p,new String[]{"sellerId"},Dao.FieldStrategy.Include);
            if(seller==null || seller.size()==0){
                returnData.put("returnStatus","FAIL");
                returnData.put("returnDesc","用户名或密码错误");
                throw new UserOperateException(500,"用户名或密码错误");
            }

            List<Object> params = new ArrayList<>();
            params.add(relateFrom);
            params.add(localSellerId);
            params.add(relateStoreId);

            List<String> returnFields = new ArrayList<>();
            returnFields.add("localSellerId");
            returnFields.add("relateStoreId");

            String sql = "select " +
                    " localSellerId" +
                    ",relateStoreId" +
                    " from relateStore" +
                    " where relateFrom=?" +
                    " and (localSellerId = ? or relateStoreId = ?)";
            List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);

            if(re!=null && re.size()!=0){
                if(relateStoreId.equals(re.get(0).get("relateStoreId"))){
                    returnData.put("returnStatus","FAIL");
                    returnData.put("returnDesc","店铺"+relateStoreId+"已被关联");
                    throw new UserOperateException(500,"店铺"+relateStoreId+"已被关联");
                }else{
                    returnData.put("returnStatus","FAIL");
                    returnData.put("returnDesc","店铺"+localSellerId+"已被关联");
                    throw new UserOperateException(500,"店铺"+localSellerId+"已被关联");
                }
            }

            Map<String,Object> relateStore = new HashMap<>();
            relateStore.put("_id", ZQUidUtils.genUUID());
            relateStore.put("localSellerId",localSellerId);
            relateStore.put("relateStoreId",relateStoreId);
            relateStore.put("relateFrom",relateFrom);
            relateStore.put("createTime",System.currentTimeMillis());
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName,relateStore);

            returnData.put("returnStatus","SUCCESS");
            returnData.put("returnDesc","关联成功");

            System.out.println("\n\n====================returnData:"+JSONObject.fromObject(returnData));
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            toResult(200,returnData);
        }


    }
}
