package com.zq.kyb.crm.action;

import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 *
 */
public class ArticleAction extends BaseActionImpl {

//    @GET
//    @Path("/show")
//    @Seller
//    public void show() throws Exception {
//        String pageNo = ControllerContext.getContext().getPString("pageNo");
//        String pageSize = ControllerContext.getContext().getPString("pageSize");
//        String pId = ControllerContext.getContext().getPString("pId");
//        if(pId.isEmpty()){
//            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "这里没有文章!");
//        }
//        List<String> list = new ArrayList<>();
//        list.add("_id");
//        list.add("title");
//        list.add("desc");
//        list.add("source");
//        list.add("pictureId");
//        list.add("contents");
//        list.add("createTime");
//        Map<String,Object> params = new HashMap<>();
//        params.put("pId",ControllerContext.getContext().getPString("pId"));
//        String sql = "select * from Article where pId='"+pId+"' order by createTime desc limit "+pageNo+","+pageSize;
//        List<Map<String, Object>> all = MysqlDaoImpl.getInstance().queryBySql(sql,list,null);
//        Long count = MysqlDaoImpl.getInstance().findCount("Article",params);
//        Map<String,Object> re = new HashMap<>();
//        re.put("Article",all);
//        re.put("count",count);
//        toResult(Response.Status.OK.getStatusCode(), re);
//    }

    @GET
    @Path("/showNewsList")
    @Seller
    public void showNewsList() throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("name", "新闻列表");
        Map<String,Object> news = MysqlDaoImpl.getInstance().findOne2Map("ArticleList", p,null,null);
        if(news == null){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "缺少'新闻列表'的数据");
        }
        List<String> list = new ArrayList<>();
        list.add("_id");
        list.add("name");
        list.add("desc");
        String sql = "select * from ArticleList where pId='"+news.get("_id")+"' order by createTime desc";
        List<Map<String, Object>> all = MysqlDaoImpl.getInstance().queryBySql(sql,list,null);
        toResult(Response.Status.OK.getStatusCode(), all);
    }

    @GET
    @Path("/showBranchesList")
    @Seller
    public void showBranchesList() throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("name", "分支机构");
        Map<String,Object> branches = MysqlDaoImpl.getInstance().findOne2Map("ArticleList", p,null,null);
        if(branches == null){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "缺少'分支机构'的数据");
        }
        List<String> list = new ArrayList<>();
        list.add("_id");
        list.add("name");
        list.add("desc");
        String sql = "select * from ArticleList where pId='"+branches.get("_id")+"' order by createTime";
        List<Map<String, Object>> all = MysqlDaoImpl.getInstance().queryBySql(sql,list,null);
        toResult(Response.Status.OK.getStatusCode(), all);
    }

    @GET
    @Path("/showHelpList")
    @Seller
    public void showHelpList() throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("name", "帮助中心");
        Map<String,Object> help = MysqlDaoImpl.getInstance().findOne2Map("ArticleList", p,null,null);
        if(help == null){
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "缺少'帮助中心'的数据");
        }
        List<String> list = new ArrayList<>();
        list.add("_id");
        list.add("name");
        list.add("desc");
        String sql = "select * from ArticleList where pId='"+help.get("_id")+"' order by createTime";
        List<Map<String, Object>> all = MysqlDaoImpl.getInstance().queryBySql(sql,list,null);
        if(all.size() != 0){
            for(int i=0;i<all.size();i++){
                all.get(i).get("_id");
                String sqlList = "select * from ArticleList where pId='"+all.get(i).get("_id")+"' order by createTime";
                List<Map<String, Object>> allList = MysqlDaoImpl.getInstance().queryBySql(sqlList,list,null);
                all.get(i).put("list", allList);
            }
        }
        toResult(Response.Status.OK.getStatusCode(), all);
    }

    @GET
    @Path("/queryDetail")
    @Seller
    public void queryDetail() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("_id");
        list.add("title");
        list.add("desc");
        list.add("source");
        list.add("pictureId");
        list.add("contents");
        list.add("createTime");
        List<Object> params = new ArrayList<>();
        params.add(ControllerContext.getContext().getPString("_id"));
        String sql = "select * from Article where _id=?";
        List<Map<String, Object>> all = MysqlDaoImpl.getInstance().queryBySql(sql,list,params);
        toResult(Response.Status.OK.getStatusCode(), all);
    }

    @GET
    @Path("/queryFlexSlider")
    public void queryFlexSlider() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("_id");
        list.add("title");
        list.add("pictureId");
        String sql = "select t1._id,t1.title,t1.pictureId from Article t1" +
                " left join ArticleList t2 on t1.pid=t2._id" +
                " where t2.name='新闻轮播'";
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,list,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    /**
     * 获取官网文章列表
     */
    @GET
    @Path("/getWebSiteNewsList")
    public void getWebSiteNewsList() throws  Exception {

        String createTime = ControllerContext.getPString("_createTime");
        long startTime = 0, endTime = 0;
        if (StringUtils.isNotEmpty(createTime)) {
            String[] sp = createTime.replaceAll("___in_", "").split("-");
            startTime = Long.valueOf(sp[0]);
            endTime = Long.valueOf(sp[1]);
        }


        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        //获取当前登录的代理商
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + agentInfo.get("_id"));
        if(!"2".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }


        String where = " where 1=1 ";
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();

        if (startTime != 0) {
            where += " and t1.createTime>?";
            p.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<?";
            p.add(endTime);
        }

        String hql = "select" +
                " count(t1._id) as totalCount" +
                " from Article t1" +
                " left join articlelist t2 on t1.pid=t2._id" +
                where;
        r.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(hql, r, p);
        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);
        r.clear();
        r.add("_id");
        r.add("createTime");
        r.add("title");
        r.add("contents");
        r.add("pid");
        r.add("name");
        r.add("icon");
        r.add("source");
        r.add("desc");
        String sql = "select" +
                " t1._id" +
                ",t1.createTime" +
                ",t1.title" +
                ",t1.contents" +
                ",t1.pid" +
                ",t2.name" +
                ",t1.pictureId as icon" +
                ",t1.source" +
                ",t1.desc" +
                " from Article t1" +
                " left join articlelist t2 on t1.pid=t2._id" +
                where + " order by t1.createTime desc limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
        page.setItems(re);
        toResult(Response.Status.OK.getStatusCode(), page);
    }
    @POST
    @Path("saveNews")
    public void saveNews() throws  Exception {
        String newsId = ControllerContext.getPString("newsId");
        String title = ControllerContext.getPString("title");
        String contents = ControllerContext.getPString("contents");
        String icon = ControllerContext.getPString("icon");
        String pid = ControllerContext.getPString("pid");
        String source = ControllerContext.getPString("source");
        String desc = ControllerContext.getPString("desc");
        if(StringUtils.isEmpty(newsId)){
            newsId= UUID.randomUUID().toString();
        }
        if(StringUtils.isEmpty(pid) || "1".equals(pid)){
            throw new UserOperateException(400, "请选择分类!");
        }
        if(StringUtils.isEmpty(title)){
            throw new UserOperateException(400, "请输入标题!");
        }
        Map<String,Object> v = new HashMap<>();
        v.put("_id",newsId);
        v.put("title",title);
        v.put("contents",contents);
        v.put("pictureId",icon);
        v.put("pId",pid);
        v.put("source",source);
        v.put("desc",desc);
        v.put("createTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("Article",v);
    }
    @GET
    @Path("getArticleList")
    public void getArticleList() throws  Exception {
        List<String> r = new ArrayList<>();
        r.add("_id");
        r.add("name");
        String sql ="select" +
                " t1._id" +
                ",t1.name" +
                " from articlelist t1" +
                " where t1.name='市场动态' or t1.name='新闻轮播'";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,r,null);
        toResult(Response.Status.OK.getStatusCode(), re);
    }
    @GET
    @Path("deleteNews")
    public void deleteNews() throws  Exception {
        String newsId = ControllerContext.getPString("newsId");
        MysqlDaoImpl.getInstance().remove("Article",newsId);
        toResult(Response.Status.OK.getStatusCode(), null);
    }

    /**
     * 检查是否是管理员
     * @throws Exception
     */
    public void checkAdmin() throws Exception{
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "找不到用户");
        }
        CacheServiceJRedisImpl cache = new CacheServiceJRedisImpl();
        String adminType = cache.getCache("agent_type_cache_" + other.get("agentId"));
        if(!"2".equals(adminType)){
            throw new UserOperateException(400, "你无此操作权限");
        }
    }

    /**
     * 获取文章列表 及所有子列表（格式化）
     * @throws Exception
     */
    @GET
    @Path("/getMenu")
    public void getMenu() throws Exception {
        String name = ControllerContext.getPString("name");

        if(StringUtils.isEmpty(name)){
            throw new UserOperateException(500,"列表名称不能为空");
        }
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("name",name);
        Map<String,Object> re = MysqlDaoImpl.getInstance().findOne2Map("ArticleList",paramsMap,null,null);

        List<Map<String,Object>> child = getMenuChild(re.get("_id").toString());
        for(int j=0,jlen = child.size();j<jlen;j++){
            List<Map<String,Object>> child2 = getMenuChild(child.get(j).get("_id").toString());
            child.get(j).put("items",child2);
        }
        re.put("items",child);
        toResult(200,re);
    }

    /**
     * 获取文章下的子列表
     * @param pid
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getMenuChild(String pid) throws Exception{
        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        returnField.add("_id");
        returnField.add("name");
        returnField.add("pid");
        returnField.add("line");

        params.add(pid);
        String sql = "select _id,name,pid,line from ArticleList where pid=? order by line asc";
        return MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
    }

    /**
     * 添加文章列表
     * @throws Exception
     */
    @POST
    @Path("/addMenu")
    public void addMenu() throws  Exception {
        checkAdmin();

        String name = ControllerContext.getPString("name");
        String pid = ControllerContext.getPString("pid");
        int line=0;

        if(StringUtils.isEmpty("name")){
            throw new UserOperateException(500,"列表名称不能为空");
        }
        if(StringUtils.isEmpty("pid")){
            throw new UserOperateException(500,"请选择列表上级");
        }

        List<String> returnField = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        returnField.add("_id");
        returnField.add("name");
        params.add(pid);

        String sql = "select _id,name from ArticleList where pid=?";
        List<Map<String,Object>> menuList = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        // 则默认排在末尾
        line = menuList.size();

        for (Map<String,Object> item : menuList) {
            if(name.equals(item.get("name").toString())){
                throw new UserOperateException(500,"该目录下已存在相同名称的子目录");
            }
        }

        if(!"-1".equals(pid)){
            returnField.clear();
            params.clear();

            returnField.add("line");
            params.add(pid);
            sql = "select line from ArticleList where pid = ? order by line desc";
            List<Map<String,Object>> parent = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);

            if(parent == null || parent.size()==0 || StringUtils.mapValueIsEmpty(parent.get(0),"line")){
                line=0;
            }else{
                line = Integer.parseInt(parent.get(0).get("line").toString())+1;
            }

        }

        Map<String,Object> data = new HashMap<>();
        data.put("_id",UUID.randomUUID().toString());
        data.put("name",name);
        data.put("pid",pid);
        data.put("line",line);
        data.put("createTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("ArticleList",data);
        toResult(200,data);
    }

    /**
     * 删除文章列表
     * @throws Exception
     */
    @POST
    @Path("/delMenu")
    public void delMenu() throws  Exception {
        checkAdmin();

        String _id = ControllerContext.getPString("_id");
        if(StringUtils.isEmpty(_id)){
            throw new UserOperateException(500,"获取目录失败");
        }
        Map<String,Object> menu = MysqlDaoImpl.getInstance().findById2Map("ArticleList",_id,new String[]{"_id"},Dao.FieldStrategy.Include);
        if(menu == null || menu.size() == 0){
            throw new UserOperateException(500,"该目录不存在");
        }
        //判断该目录下是否至少有一个目录
        Map<String,Object> params = new HashMap<>();
        params.put("pid",_id);
        Map<String,Object> child = MysqlDaoImpl.getInstance().findOne2Map("ArticleList",params,new String[]{"_id"},Dao.FieldStrategy.Include);
        if(child !=null && child.size()!=0){
            throw new UserOperateException(500,"请先删除该目录下的子目录或文章");
        }

        MysqlDaoImpl.getInstance().remove("ArticleList",_id);
    }

    /**
     * 添加文章列表
     * @throws Exception
     */
    @POST
    @Path("/saveArticle")
    public void saveArticle() throws  Exception {
        checkAdmin();

        JSONObject art = ControllerContext.getContext().getReq().getContent();
        if(art!=null && art.size()==0){
            throw new UserOperateException(500,"获取数据失败");
        }
        if(StringUtils.mapValueIsEmpty(art,"pId")){
            throw new UserOperateException(500,"获取上级目录失败");
        }
        Map<String,Object> p = MysqlDaoImpl.getInstance().findById2Map("ArticleList",art.get("pId").toString(),new String[]{"_id","name"},Dao.FieldStrategy.Include);
        if(p==null || p.size()==0 || StringUtils.mapValueIsEmpty(p,"_id")){
            throw new UserOperateException(500,"获取上级目录为空");
        }
        if(StringUtils.mapValueIsEmpty(art,"title")){
            throw new UserOperateException(500,"请输入标题");
        }
        if(StringUtils.mapValueIsEmpty(art,"contents")){
            throw new UserOperateException(500,"文章内容为空");
        }
        if(art.get("contents").toString().length()>1024000){
            throw new UserOperateException(500,"文章内容过多");
        }

        Map<String,Object> data = new HashMap<>();
        String _id;
        if(StringUtils.mapValueIsEmpty(art,"_id")){
            _id=UUID.randomUUID().toString();
            data.put("pId",art.get("pId"));
            data.put("createTime",System.currentTimeMillis());
        }else{
            _id=art.get("_id").toString();
            data = MysqlDaoImpl.getInstance().findById2Map("Article",_id,new String[]{"_id","pId","title"},Dao.FieldStrategy.Include);
            if(data==null || data.size()==0){
                throw new UserOperateException(500,"保存失败");
            }
            if(!data.get("pId").toString().equals(data.get("pId").toString())){
                throw new UserOperateException(500,"获取上级目录错误");
            }
        }
        //更新目录名称
        if(!p.get("name").equals(art.get("title").toString())){
            Map<String,Object> params = new HashMap<>();
            params.put("title",art.get("title").toString());
            params.put("pId",art.get("title").toString());
            Map<String,Object> demo = MysqlDaoImpl.getInstance().findOne2Map("Article",params,new String[]{"_id","pId","title"},Dao.FieldStrategy.Include);
            if(demo!=null && demo.size()!=0){
                throw new UserOperateException(500,"该目录下已经存在相同名称的文章");
            }

            p.put("name",art.get("title").toString());
            MysqlDaoImpl.getInstance().saveOrUpdate("ArticleList",p);
        }

        data.put("_id",_id);
        data.put("title",art.get("title"));
        data.put("contents",art.get("contents"));
        data.put("canUse",StringUtils.isEmpty(ControllerContext.getPString("canUse"))?false:ControllerContext.getPBoolean("canUse"));
        data.put("updateTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("Article",data);

        data.remove("contents");
        toResult(200,data);
    }

    /**
     * 获取单个文章
     * @throws Exception
     */
    @GET
    @Path("/getArticle")
    public void getArticle() throws  Exception {
        String _id = ControllerContext.getPString("_id");
        String pId = ControllerContext.getPString("pId");

//        boolean isAdmin = false;
//
//        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
//        JSONObject other = JSONObject.fromObject(otherDataJson);
//        if (!StringUtils.mapValueIsEmpty(other, "agentId")) {
//            isAdmin=true;
//        }

        if(StringUtils.isEmpty(_id)){
            if(StringUtils.isEmpty(pId)){
                throw new UserOperateException(500,"获取文章失败");
            }
            Map<String,Object> params = new HashMap<>();
            params.put("pId",pId);
//            if(isAdmin){ //若是商城访问，则只显示可用的文章
//                params.put("canUse",true);
//            }
            toResult(200,MysqlDaoImpl.getInstance().findOne2Map("Article",params,null,null));
        }else{
            toResult(200,MysqlDaoImpl.getInstance().findById2Map("Article",_id,null,null));
        }
    }

    /**
     * 删除文章 或 目录
     * @throws Exception
     */
    @POST
    @Path("/delArticle")
    public void delArticle() throws  Exception {
        checkAdmin();

        String artId = ControllerContext.getPString("_id");
        String pId = ControllerContext.getPString("pId");

        if(StringUtils.isEmpty(artId) && StringUtils.isEmpty(pId)){
            throw new UserOperateException(500,"删除数据失败");
        }
        if(StringUtils.isNotEmpty(artId)){
            Map<String,Object> art = MysqlDaoImpl.getInstance().findById2Map("Article",artId,new String[]{"_id"},null);
            if(art==null || art.size()==0){
                throw new UserOperateException(500,"未获取到需要删除的数据");
            }

            MysqlDaoImpl.getInstance().remove("Article",artId);
        }
        if(StringUtils.isNotEmpty(pId)){
            Map<String,Object> p = MysqlDaoImpl.getInstance().findById2Map("ArticleList",pId,new String[]{"_id"},null);
            if(p==null || p.size()==0){
                throw new UserOperateException(500,"未获取到需要删除的数据");
            }

            MysqlDaoImpl.getInstance().remove("ArticleList",pId);
        }
    }
}
