package com.zq.kyb.core.ctrl;

import com.zq.kyb.core.annotation.Lock;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.dao.CheckMData;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.Dao.FieldStrategy;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.HttpClientUtils;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * 作用: Aaction 的基础类，大部分功能通过该类能够实现
 * <p/>
 * Timestamp: 2007-4-13 Time: 15:04:12
 */
public class BaseActionImpl implements BaseAction {
    public static int SUCCESS = 200;
    public static int BAD_REQUEST = 400;


    public Object resultObj;
    protected String entityName;
    protected String modelName;
    protected Dao dao;
    private Object entityVersion;


    public static String keywordValueRep(String keywordValue) {
        keywordValue = keywordValue.replaceAll("\\(", "\\\\(");
        keywordValue = keywordValue.replaceAll("\\)", "\\\\)");
        keywordValue = keywordValue.replaceAll("\\.", "\\\\.");
        keywordValue = keywordValue.replaceAll("\\[", "\\\\[");
        keywordValue = keywordValue.replaceAll("\\]", "\\\\]");
        return keywordValue.trim();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    static String remoteToken = null;

    static String domain = "a.kyb001.com";
    static String baseUrl = "https://" + domain + "/s_admin/api";


    /**
     * 查询本地历史记录数据修改列表
     *
     * @throws SQLException
     * @throws IOException
     */
    @GET
    @Path("/getEntityVersion")
    public void getEntityVersion() throws SQLException, IOException {
        toResult(200, findEntityVersion());
    }

    /**
     * 查询本地历史某条数据的某个版本
     *
     * @throws SQLException
     * @throws IOException
     */
    @GET
    @Path("/getEntityByIdVersion")
    public void getEntityByIdVersion() throws SQLException, IOException {

        String _id = ControllerContext.getPString("_id");
        Long version = ControllerContext.getPLong("version");
        String db = Dao.getDataBaseName(entityName);

        List<String> re = new ArrayList<>();
        re.add("entityJson");
        List<Object> p = new ArrayList<>();
        p.add(entityName + "_" + _id + "_" + version);
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql("select entityJson from " + Dao.getFullTableName(db, CheckMData.DataVersionContent) + " where _id=?", re, p);
        if (li == null || li.size() == 0) {
            throw new UserOperateException(400, "版本数据不存在!");
        }
        toResult(200, li.get(0));
    }

    /**
     * 查询远程历史某条数据的某个版本
     *
     * @throws SQLException
     * @throws IOException
     */
    @GET
    @Path("/getEntityByIdVersionRemote")
    public void getEntityByIdVersionRemote() throws Exception {
        HttpClientUtils meituanClient = new HttpClientUtils();
        if (remoteToken == null) {
            setRemoteToken(meituanClient);
        }
        BasicCookieStore store = new BasicCookieStore();
        BasicClientCookie c = new BasicClientCookie("___ADMIN_TOKEN", URLEncoder.encode(remoteToken, "utf-8"));
        c.setPath("/");
        c.setDomain(domain);
        store.addCookie(c);
        meituanClient.getHttpclient().setCookieStore(store);

        String _id = ControllerContext.getPString("_id");
        Long version = ControllerContext.getPLong("version");
        if (StringUtils.isEmpty(_id)) {
            throw new UserOperateException(400, "_id is null");
        }
        if (version == 0) {
            throw new UserOperateException(400, "version 必须大于0");
        }

        String getUrl = baseUrl + "/common/" + entityName + "/getEntityByIdVersion?1=1";
        getUrl += "&_id=" + _id;
        getUrl += "&version=" + version;
        JSONObject re = meituanClient.getJSON(getUrl);
        toResult(re.getInt("code"), re.getJSONObject("content"));
    }

    /**
     * 查询本地历史记录数据修改列表
     *
     * @throws SQLException
     * @throws IOException
     */
    @GET
    @Path("/getVersionList")
    public void getVersionList() throws SQLException, IOException {

        Long startVersion = ControllerContext.getPLong("startVersion");
        Long endVersion = ControllerContext.getPLong("endVersion");
        Long pageNo = ControllerContext.getPLong("pageNo");

        toResult(200, getDataVersionList(startVersion, endVersion, pageNo));
    }

    /**
     * 查询远程历史记录
     *
     * @throws SQLException
     * @throws IOException
     */
    @GET
    @Path("/getVersionListRemote")
    public void getVersionListRemote() throws Exception {

        Long startVersion = ControllerContext.getPLong("startVersion");
        Long endVersion = ControllerContext.getPLong("endVersion");
        Long pageNo = ControllerContext.getPLong("pageNo");

        toResult(200, getRemoteData(startVersion, endVersion, pageNo));
    }

    private JSONObject getRemoteData(Long startV, Long endV, Long pageNo) throws Exception {
        HttpClientUtils meituanClient = new HttpClientUtils();
        BasicCookieStore store = new BasicCookieStore();
        if (remoteToken == null) {
            setRemoteToken(meituanClient);
        }
        BasicClientCookie c = new BasicClientCookie("___ADMIN_TOKEN", URLEncoder.encode(remoteToken, "utf-8"));
        c.setPath("/");
        c.setDomain(domain);
        store.addCookie(c);
        meituanClient.getHttpclient().setCookieStore(store);
        String getUrl = baseUrl + "/common/" + entityName + "/getVersionList?1=1";
        if (startV > 0) {
            getUrl += "&startVersion=" + startV;
        }
        if (endV > 0) {
            getUrl += "&endVersion=" + endV;
        }
        if (pageNo > 0) {
            getUrl += "&pageNo=" + pageNo;
        }
        JSONObject re = meituanClient.getJSON(getUrl);
        return re.getJSONObject("content");
    }

    private void setRemoteToken(HttpClientUtils meituanClient) throws Exception {
        JSONObject j = new JSONObject();
        j.put("deviceId", "9A3EDFBB-7BA9-43F2-B4B8-0ABF52436F99");
        j.put("loginName", "sys_admin");
        j.put("password", "joey");

        JSONObject re = meituanClient.postData(baseUrl + "/account/AdminUser/login", j, null);
        remoteToken = re.getJSONObject("content").getString("token");
        Logger.getLogger(BaseActionImpl.class.getName()).info("___ADMIN_TOKEN1: " + remoteToken);
        //remoteToken = URLDecoder.decode(remoteToken, "UTF-8");
        //System.out.println("___ADMIN_TOKEN2: " + remoteToken);
    }

    public static void main(String[] args) throws Exception {
        // getRemoteData();
    }

    public Map<String, Object> findEntityVersion() throws SQLException {
        String db = Dao.getDataBaseName(entityName);
        List<String> re = new ArrayList<>();
        re.add("_id");
        re.add("updateTime");
        re.add("updater");
        re.add("version");
        re.add("versionRemote");
        String sql = "select _id,updateTime,updater,`version`,versionRemote from " + Dao.getFullTableName(db, CheckMData.DataTableVersion) + " where _id=?";
        List<Object> p = new ArrayList<>();
        p.add(entityName);
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(sql, re, p);
        if (li != null && li.size() > 0) {
            return li.get(0);
        }
        Map<String, Object> r = new HashMap<>();
        r.put("version", 0);
        r.put("versionRemote", 0);
        return r;
    }


    private JSONObject getDataVersionList(Long startV, Long endV, Long pageNo) throws SQLException, IOException {


        String db = Dao.getDataBaseName(entityName);
        JSONObject j = new JSONObject();
        boolean endVIsNull = endV == null || endV == 0;
        //分页显示 每个字段的跟新记录
        String sql = "select count(_id) as `count`" + (endVIsNull ? ",max(version) as endVersion" : "") + " from " + Dao.getFullTableName(db, CheckMData.DataVersion) + " where version>? and entityName=?";
        List<String> re = new ArrayList<>();
        re.add("count");
        if (endVIsNull) {
            re.add("endVersion");
        }
        List<Object> p = new ArrayList<>();
        p.add(startV);
        p.add(entityName);
        List<Map<String, Object>> countList = MysqlDaoImpl.getInstance().queryBySql(sql, re, p);
        Long count = 0L;
        if (countList != null && countList.size() > 0) {
            count = (Long) countList.get(0).get("count");
            if (endVIsNull) {
                endV = (Long) countList.get(0).get("endVersion");
            }
        }

        if (startV >= endV) {
            throw new UserOperateException(400, "版本区间错误:" + startV + "-" + endV);
        }

        pageNo = pageNo == 0 ? 1 : pageNo;
        Page page = new Page(pageNo, 500, count);

        if (count > 0) {
            re.clear();
            re.add("_id");
            re.add("createTime");
            re.add("creator");
            re.add("version");
            re.add("versionRemote");
            re.add("entityName");
            re.add("entityId");
            re.add("versionRemote");
            sql = "select _id,createTime,creator,`version`,`versionRemote`,entityName,entityId from " + Dao.getFullTableName(db, CheckMData.DataVersion) + " where entityName=? and version>? and version<=? limit " + page.getStartIndex() + "," + page.getPageSize();
            p.clear();
            p.add(entityName);
            p.add(startV);
            p.add(endV);
            List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql, re, p);
            page.setItems(list);
        }
        j.put("start", startV);
        j.put("end", endV);
        j.put("dataVersion", page);
        return j;
    }

    @GET
    @Path("/getMData")
    public void getMData() throws Exception {
        //        Map<String, Object> params = new HashMap<String, Object>();
        //        params.put("modelName", getModelName());
        //        params.put("entityName", getEntityName());
        //        Map<String, Object> orderBy = new HashMap<String, Object>();
        //        orderBy.put("orderNo", -1);
        // 只有这个是例外
        //List<Map<String, Object>> all = MysqlDaoImpl.getInstance().findAll2Map("MData", params, orderBy, null, null);
        JSONObject re = new JSONObject();
        Map<String, Object> value = (Map<String, Object>) Dao.entityMap.get(entityName);
        if (!StringUtils.mapValueIsEmpty(value, "isSyncTable")) {
            re.put("isSyncTable", value.get("isSyncTable"));
            re.put("entityVersion", findEntityVersion());
        }
        re.put("items", Dao.entityFieldsMap.get(entityName));
        toResult(Status.OK.getStatusCode(), re);
    }

    @PUT
    @Path("/save")
    //@Lock(key = "_id")
    public void save() throws Exception {
        JSONObject values = ControllerContext.getContext().getReq().getContent();
        String _id = values.getString("_id");
        values.put("_id", _id);
        values.remove("createTime");
        values.remove("creator");
        values.remove("owner");

        // 对关联文件进行额外的处理

        Map<String, Object> oldMap = dao.findById2Map(entityName, _id, null, FieldStrategy.Include);
        if (oldMap == null) {
            values.put("createTime", System.currentTimeMillis());
            values.put("creator", ControllerContext.getContext().getCurrentUserId());
        } else {
            if (oldMap.get("createTime") == null) {
                values.put("createTime", System.currentTimeMillis());
            } else {
                values.put("createTime", oldMap.get("createTime"));
            }
            values.put("creator", oldMap.get("creator"));
            values.put("updateTime", System.currentTimeMillis());
            values.put("updater", ControllerContext.getContext().getCurrentUserId());
        }
        System.out.print("values======" + values + "entityName=====" + entityName);
        Long version = dao.saveOrUpdate(entityName, values);

        // doAddMapLog(collectionName,values,"编辑"+collectionName.toString(),"");
        Map<String, Object> m = null;
        if (version > 0) {
            List<Object> p = new ArrayList<>();
            p.add(entityName + "_" + _id + "_" + version);
            List<String> r = new ArrayList<>();
            r.add("entityJson");
            List<Map<String, Object>> list = MysqlDaoImpl.getInstance().queryBySql("select entityJson from " + Dao.getFullTableName(Dao.getDataBaseName(entityName), CheckMData.DataVersionContent) + " where _id=?", r, p);
            if (list != null && list.size() > 0) {
                m = JSONObject.fromObject(list.get(0).get("entityJson"));
                m.put("__version", version);
            }
        } else {
            m = dao.findById2Map(entityName, _id, null, null);
        }
        //m.put("__version", version);
        toResult(Response.Status.OK.getStatusCode(), JSONObject.fromObject(m));
    }

    @GET
    @Path("/show")
    public void show() throws Exception {

        String _id = ControllerContext.getPString("_id");
        String excludeKeys = ControllerContext.getPString("excludeKeys");
        String[] excludeKeysArray = null;
        if (StringUtils.isNotEmpty(excludeKeys)) {
            excludeKeysArray = excludeKeys.split(",");
        }
        Map<String, Object> m = dao.findById2Map(entityName, _id, excludeKeysArray, null);
        toResult(Response.Status.OK.getStatusCode(), JSONObject.fromObject(m));
    }

    @GET
    @Path("/byName")
    public void byName() throws Exception {
        String name = ControllerContext.getPString("name");
        String excludeKeys = ControllerContext.getPString("excludeKeys");

        JSONObject obj = new JSONObject();
        obj.put("name", name);
        String[] excludeKeysArray = null;
        if (StringUtils.isNotEmpty(excludeKeys)) {
            excludeKeysArray = excludeKeys.split(",");
        }
        Map<String, Object> m = dao.findOne2Map(entityName, obj, excludeKeysArray, null);
        toResult(Response.Status.OK.getStatusCode(), JSONObject.fromObject(m));
    }

    @POST
    @Path("/del")
    public void del() throws Exception {
        String _id = ControllerContext.getPString("_id");
        dao.remove(entityName, _id);
        toResult(Response.Status.OK.getStatusCode(), null);
    }

    @POST
    @Path("/deleteMore")
    public void deleteMore() throws Exception {
        JSONObject values = ControllerContext.getContext().getReq().getContent();
        JSONArray ids = values.getJSONArray("ids");
        for (Object id : ids) {
            dao.remove(entityName, (String) id);
        }
        toResult(Response.Status.OK.getStatusCode(), null);
    }

    @GET
    @Path("/query")
    public void query() throws Exception {
        JSONObject re = queryData();
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    @GET
    @Path("/queryAll")
    public void queryAll() throws Exception {
        Map<String, Object> orderBy = new HashMap<String, Object>();
        orderBy.put("createTime", -1);
        List<Map<String, Object>> li = dao.findPage2Map(entityName, (int) 0, 500, null, orderBy, null, null);
        JSONObject re = new JSONObject();
        re.put("items", li);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    public static String getOrderByStr() {
        String orderByStr = ControllerContext.getPString("orderBy");
        orderByStr = StringUtils.isEmpty(orderByStr) ? "t1.createTime" : orderByStr;
        String orderAsc = ControllerContext.getPString("orderByAsc");
        orderAsc = org.apache.commons.lang.StringUtils.isEmpty(orderAsc) ? "-1" : orderAsc;
        orderAsc = "-1".equals(orderAsc) ? "desc" : "asc";
        return " order by " + orderByStr + " " + orderAsc;
    }

    protected JSONObject queryData() throws Exception {
        String excludeKeys = ControllerContext.getPString("exclude_keys");
        String includeKeys = ControllerContext.getPString("include_keys");

        int pageSize = ControllerContext.getPInteger("pageSize");
        int pageNo = ControllerContext.getPInteger("pageNo");

        pageNo = pageNo <= 1 ? 1 : pageNo;
        pageSize = pageSize == 0 ? 20 : pageSize;
//        pageSize = pageSize > 5000 ? 5000 : pageSize;// 需要一次查出来

        String keywords = ControllerContext.getPString("keywords");
        String keywordValue = ControllerContext.getPString("keywordValue");

        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(keywordValue)) {
            keywords = StringUtils.isEmpty(keywords) ? "name" : keywords;
            params.put("$keywords", keywords + ":" + keywordValue);
        }


        for (Object key : ControllerContext.getContext().getReq().getContent().keySet()) {
            String name = (String) key;
            if (name.equals("____quickFilterMap")) {//grid控件的快速过滤组件
                String str = ControllerContext.getPString(name);
                JSONObject j = JSONObject.fromObject(str);
                for (Object o : j.keySet()) {
                    String k = (String) o;
                    if (!StringUtils.mapValueIsEmpty(j, k)) {
                        if (Dao.fieldMap.containsKey(entityName + "_" + k)) {
                            Map<String, Object> kField = (Map<String, Object>) Dao.fieldMap.get(entityName + "_" + k);
                            String type = StringUtils.mapValueIsEmpty(kField, "type") ? "string" : (String) kField.get("type");
                            String input = StringUtils.mapValueIsEmpty(kField, "inputType") ? "input" : (String) kField.get("type");
                            if (type.equals("string")
                                    && (input.equals("input") || input.equals("password") || input.equals("textarea"))) {
                                params.put(k, "___like_" + j.get(k));
                            }
                        }
                    }
                }
            } else if (name.startsWith("_")) {
                params.put(name.substring(1), ControllerContext.getPString(name));
            }
        }

        String orderBy = ControllerContext.getPString("orderBy");
        String orderByAsc = ControllerContext.getPString("orderByAsc");

        orderByAsc = orderByAsc == null ? "-1" : orderByAsc;
        if (StringUtils.isEmpty(orderBy)) {
            if (Dao.fieldMap.containsKey(entityName + "_sortNo")) {
                orderBy = "sortNo";
            } else {
                orderBy = "createTime";
            }
        }

        Map<String, Object> od = new HashMap<>();
        od.put(orderBy, Integer.valueOf(orderByAsc));

        Long lastTime = ControllerContext.getPLong("lastTime");
        if (lastTime > 0) {
            JSONObject v = new JSONObject();
            v.put("$lt", lastTime);
            params.put("createTime", v);
        }

        Map<String, Object> newParams = new HashMap<>();
        newParams.putAll(params);

        long count = dao.findCount(entityName, newParams);
        long searchCount = count >500 ? 500:count;
        Page page = new Page();
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotalNum(searchCount);
        String[] keys = null;
        FieldStrategy s = null;
        if (StringUtils.isNotEmpty(excludeKeys)) {
            List<String> x = new ArrayList<String>();
            for (String k : excludeKeys.split(",")) {
                if ("_id".equals(k)) {
                    continue;
                }
                x.add(k);
            }
            keys = x.toArray(new String[x.size()]);
            s = FieldStrategy.Exclude;
        }
        // 注意是include覆盖exclude
        if (StringUtils.isNotEmpty(includeKeys)) {
            keys = includeKeys.split(",");
            s = FieldStrategy.Include;
        }

        newParams = new HashMap<>();
        newParams.putAll(params);

        List<Map<String, Object>> li = dao.findPage2Map(entityName, (int) page.getStartIndex(), pageSize, newParams, od, keys, s);
        page.setItems(li);
        page.setTotalNum(count);

        return JSONObject.fromObject(page);
    }

    /**
     * 查找一个节点的所有父
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path("/queryTreeParents")
    public void queryTreeParents() throws Exception {
        List<String> li = _queryTreeParents(new ArrayList<String>(), ControllerContext.getPString("_id"));
        Collections.reverse(li);
        JSONObject rObj = new JSONObject();
        rObj.put("items", li);
        toResult(200, rObj);
    }

    public List<String> _queryTreeParents(List<String> list, String id) throws Exception {
        // String id = ControllerContext.getPString("_id");
        Map<String, Object> v = dao.findById2Map(entityName, id, new String[]{"pid"}, FieldStrategy.Include);
        if (v != null && v.containsKey("pid") && v.get("pid") != null && !"-1".equals(v.get("pid"))) {
            list.add((String) v.get("pid"));
            _queryTreeParents(list, (String) v.get("pid"));
        }
        return list;
    }

    /**
     * 直接装载整棵树，用于小数据量
     *
     * @throws Exception
     */
    @GET
    @Path("/queryTree")
    public void queryTree() throws Exception {
        String pid = ControllerContext.getPString("pid");
        // String pid = ControllerContext.getPString("pid");
        String excludeKeys = ControllerContext.getPString("exclude_keys");
        String includeKeys = ControllerContext.getPString("include_keys");

        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isEmpty(pid)) {
            pid = "-1";
        }
        params.put("pid", pid);
        FieldStrategy f = null;
        String[] filed = null;
        if (excludeKeys != null) {
            filed = excludeKeys.split(",");
            f = FieldStrategy.Exclude;
        }
        if (includeKeys != null) {
            filed = includeKeys.split(",");
            f = FieldStrategy.Include;
        }

        Map<String, Object> order = null;

        List<Map<String, Object>> list = dao.findAll2Map(entityName, params, order, filed, f);
        for (Map<String, Object> map : list) {
            createNode(map, entityName, order, filed, f);
        }
        JSONObject re = new JSONObject();
        re.put("items", list);
        toResult(Status.OK.getStatusCode(), re);
    }

    @GET
    @Path("/queryTreeByPid")
    public void queryTreeByPid() throws Exception {
        String excludeKeys = ControllerContext.getPString("exclude_keys");
        Integer indexNum = ControllerContext.getPInteger("indexNum");
        Integer pageSize = ControllerContext.getPInteger("pageSize");
        String pid = ControllerContext.getPString("pid");
        pageSize = pageSize == 0 ? 200 : pageSize;
        pageSize = pageSize > 500 ? 500 : pageSize;

        Map<String, Object> params = new HashMap<String, Object>();
        String keywords = ControllerContext.getPString("keywords");
        String keywordValue = ControllerContext.getPString("keywordValue");

        if (StringUtils.isNotEmpty(keywordValue)) {
            keywords = StringUtils.isEmpty(keywords) ? "name" : keywords;
            params.put("$keywords", keywords + ":" + keywordValue);
        }

        JSONObject orderBy = new JSONObject();
        orderBy.put("createTime", -1);

        Long lastTime = ControllerContext.getPLong("lastTime");
        if (lastTime > 0) {
            JSONObject v = new JSONObject();
            v.put("$lt", lastTime);
            params.put("createTime", v);
        }
        if (StringUtils.isNotEmpty(pid)) {
            params.put("pid", pid);
        }

        long count = dao.findCount(entityName, params);
        String[] excludeKeysArray = null;
        if (StringUtils.isNotEmpty(excludeKeys)) {
            excludeKeysArray = excludeKeys.split(",");
        }
        if (indexNum != null && indexNum > 0) {
            indexNum = indexNum * pageSize;
        }
        List<Map<String, Object>> li = dao.findPage2Map(entityName, indexNum, pageSize, params, orderBy, excludeKeysArray, null);
        JSONObject re = new JSONObject();
        re.put("items", li);
        re.put("count", count);
        toResult(Response.Status.OK.getStatusCode(), re);
    }

    protected void createNode(Map<String, Object> node, String collectionName, Map<String, Object> order, String[] filed, FieldStrategy f) throws Exception {
        node.put("id", node.get("_id"));
        node.put("text", node.get("name"));

        Map<String, Object> p = new HashMap<String, Object>();
        p.put("pid", node.get("_id"));
        List<Map<String, Object>> list = dao.findAll2Map(collectionName, p, order, filed, f);
        if (list.size() > 0) {
            for (Map<String, Object> mm : list) {
                createNode(mm, collectionName, order, filed, f);
            }
            node.put("classes", "important");
            node.put("expanded", true);
            node.put("children", list);
        }
    }

    @POST
    @Seller(isAdmin = true)
    @Path("/resetPassword")
    public void resetPassword() throws Exception {
        String _id = ControllerContext.getPString("_id");
        String name = ControllerContext.getPString("name");
        Map<String, Object> map = (Map<String, Object>) Dao.fieldMap.get(entityName + "_" + name);
        if (StringUtils.mapValueIsEmpty(map, "inputType") || !"password".equals(map.get("inputType"))) {
            throw new UserOperateException(400, "字段:" + name + "不是一个密码字段!");
        }

        Map<String, Object> obj = MysqlDaoImpl.getInstance().findById2Map(entityName, _id, new String[]{name}, null);
        if (obj == null) {
            throw new UserOperateException(400, "数据不存在: _id=" + _id);
        }
        String newPassword = randomPassword();
        Map<String, Object> n = new HashMap<>();
        n.put(name, MessageDigestUtils.digest(newPassword));
        n.put("_id", _id);
        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, n);
        n.put("password", newPassword);
        toResult(200, n);
    }

    /**
     * 删除一个字段的关联某条数据
     */


    @POST
    @Path("/saveLinkTableData")
    public void delLinkTableDataByd() throws Exception {
        String _id = ControllerContext.getPString("_id");
        Map<String, Object> old = MysqlDaoImpl.getInstance().findById2Map(CheckMData.LinkTable, _id, null, null);
        MysqlDaoImpl.getInstance().remove(CheckMData.LinkTable, _id);

        String entityField = (String) old.get("entityField");
        String entityId = (String) old.get("entityId");

        Map<String, Object> params = getLinkTableParams(entityName, entityField, entityId);
        long count = MysqlDaoImpl.getInstance().findCount(CheckMData.LinkTable, params);
        updateLinkTableCount(entityField, entityId, count);
        JSONObject re = new JSONObject();
        re.put("count", count);
        toResult(200, re);
    }

    /**
     * 保存一个字段的关联表数据列表
     *
     * @throws Exception
     */
    @POST
    @Path("/saveLinkTableData")
    public void saveLinkTableData() throws Exception {
        String entityField = ControllerContext.getPString("entityField");//关联的本表字段
        Boolean isAdd = ControllerContext.getPBoolean("isAdd");//是否累加的方式,否则就是新的列表替换旧的列表
        String entityId = ControllerContext.getPString("entityId");
        String linkEntityIds = ControllerContext.getPString("linkEntityIds");

        Map<String, Object> params = getLinkTableParams(entityName, entityField, entityId);

        String[] ids = linkEntityIds.split(",");
        List<Map<String, Object>> oldList = MysqlDaoImpl.getInstance().findAll2Map(CheckMData.LinkTable, params, null, new String[]{"linkEntityId"}, FieldStrategy.Include);
        Map<String, String> oldMap = new HashMap<>();
        for (Map<String, Object> old : oldList) {
            oldMap.put((String) old.get("linkEntityId"), (String) old.get("_id"));
        }

        for (String id : ids) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.putAll(params);
            newItem.put("linkEntityId", id);
            newItem.put("_id", UUID.randomUUID().toString());
            newItem.put("createTime", System.currentTimeMillis());
            newItem.put("creator", ControllerContext.getContext().getCurrentUserId());
            if (oldMap.containsKey(id)) {
                oldMap.remove(id);
                continue;
            }
            MysqlDaoImpl.getInstance().saveOrUpdate(CheckMData.LinkTable, newItem);
        }

        if (!isAdd) {
            for (String v : oldMap.values()) {
                MysqlDaoImpl.getInstance().remove(CheckMData.LinkTable, v);
            }
        }

        long count = MysqlDaoImpl.getInstance().findCount(CheckMData.LinkTable, params);
        updateLinkTableCount(entityField, entityId, count);
        JSONObject re = new JSONObject();
        re.put("count", count);
        toResult(200, re);
    }

    private void updateLinkTableCount(String entityField, String entityId, long count) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("_id", entityId);
        m.put(entityField, count);
        dao.saveOrUpdate(entityName, m);
    }


    /**
     * 查询一个字段的关联表数据列表
     *
     * @throws Exception
     */
    @GET
    @Path("/findLinkTableData")
    public void findLinkTableData() throws Exception {
        String entityField = ControllerContext.getPString("entityField");
        String entityId = ControllerContext.getPString("entityId");
        Long pageNo = ControllerContext.getPLong("pageNo");

        Page p = findLinkTableDataPage(entityName, entityField, entityId, pageNo, true);
        toResult(200, p);
    }

    public static Map<String, Object> getLinkTableParams(String entityName, String entityField, String entityId) {
        Map<String, Object> map = (Map<String, Object>) Dao.fieldMap.get(entityName + "_" + entityField);
        if (StringUtils.mapValueIsEmpty(map, "_linkTable")) {
            throw new UserOperateException(400, "_linkTable未设定!");
        }
        String _linkTable = (String) map.get("_linkTable");
        String module, entity;
        if (_linkTable.startsWith("/")) {
            module = _linkTable.split("/")[1];
            entity = _linkTable.split("/")[2];
        } else {
            module = Constants.moduleName;
            entity = _linkTable;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("entityName", entityName);
        params.put("entityField", entityField);
        params.put("entityId", entityId);
        params.put("linkModule", module);
        params.put("linkEntity", entity);
        return params;
    }

    public static List<Map<String, Object>> findAllLinkTableData(String entityName, String entityField, String entityId, boolean isLoadData) throws Exception {
        Map<String, Object> params = getLinkTableParams(entityName, entityField, entityId);
        String module = (String) params.get("linkModule");
        String entity = (String) params.get("linkEntity");

        Map<String, Object> orderBy = new HashMap<>();
        orderBy.put("createTime", -1);
        List<Map<String, Object>> items = MysqlDaoImpl.getInstance().findAll2Map(CheckMData.LinkTable, params, orderBy, new String[]{"_id", "linkEntityId"}, FieldStrategy.Include);
        if (isLoadData) {
            for (Map<String, Object> item : items) {
                loadData(module, entity, item);
            }
        }
        return items;
    }

    private static void loadData(String module, String entity, Map<String, Object> item) throws Exception {
        String linkTableId = (String) item.get("_id");
        String linkEntityId = (String) item.get("linkEntityId");
        Map<String, Object> m;
        if (module.equals(Constants.moduleName)) {
            m = MysqlDaoImpl.getInstance().findById2Map(entity, linkEntityId, null, null);
        } else {
            m = ServiceAccess.callService(Message.newReqMessage("1:GET@/" + module + "/" + entity + "/show")).getContent();
        }
        if (m == null) {
            Logger.getLogger(BaseActionImpl.class.getName() + ":findLinkTableData").warning("_id:" + linkEntityId + " find map is null");
        } else {
            item.putAll(m);
            item.put("$$linkTableId", linkTableId);
        }
    }

    public static Page findLinkTableDataPage(String entityName, String entityField, String entityId, Long pageNo, boolean isLoadData) throws Exception {
        //Integer pageSize = ControllerContext.getPInteger("pageSize");
        int pageSize = 50;

        Map<String, Object> params = getLinkTableParams(entityName, entityField, entityId);
        long count = MysqlDaoImpl.getInstance().findCount(CheckMData.LinkTable, params);

        String module = (String) params.get("linkModule");
        String entity = (String) params.get("linkEntity");

        Page p = new Page(pageNo, pageSize, count);
        Map<String, Object> orderBy = new HashMap<>();
        orderBy.put("createTime", -1);
        List<Map<String, Object>> items = MysqlDaoImpl.getInstance().findPage2Map(CheckMData.LinkTable, p.getStartIndex()
                , pageSize, params, orderBy, new String[]{"_id", "linkEntityId"}, FieldStrategy.Include);
        if (isLoadData) {
            for (Map<String, Object> item : items) {
                loadData(module, entity, item);
            }
        }

        p.setItems(items);
        return p;
    }

    private static String randomPassword() {
        String newPassword = Math.abs(new Random().nextInt(10000000)) + "";
        if (newPassword.length() < 6) {
            String bu = new Random().nextInt(9) * 99999999 + "";
            newPassword += bu.substring(0, 6 - newPassword.length());
        } else {
            newPassword = newPassword.substring(0, 6);
        }
        return newPassword;
    }

    public void toResult(int statusCode, Object rObj) throws IOException {
        rObj = rObj == null ? new JSONObject() : rObj;
        if (rObj instanceof JSONObject) {
            ControllerContext.setResult(statusCode, (JSONObject) rObj);
        } else if (rObj instanceof Map || rObj instanceof Page) {
            rObj = JSONObject.fromObject(rObj);
            ControllerContext.setResult(statusCode, (JSONObject) rObj);
        } else if (rObj instanceof List) {
            JSONObject rJson = new JSONObject();
            rJson.put("items", rObj);
            ControllerContext.setResult(statusCode, rJson);
        }
    }


    public String getEntityName() {
        return entityName;
    }

    @Override
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public void setDao(Dao dao) {
        this.dao = dao;
    }


}
