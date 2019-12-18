package com.zq.kyb.file.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.file.dao.FileCloudDaoBaseImpl;
import com.zq.kyb.file.dao.FileDao;
import com.zq.kyb.file.dao.FileDaoBaseImpl;
import com.zq.kyb.file.img.ImageIM4JavaUtils;
import com.zq.kyb.util.EhcacheUtil;
import com.zq.kyb.util.FileExecuteUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileItemAction extends BaseActionImpl {

    public static final Map<String, Boolean> imgResizeMap = new HashMap<String, Boolean>();
    final static Logger log =  Logger.getLogger(FileItemAction.class);
    //图片上传地址
    public static final String UPLOADPIC_URL ="https://lzftest.gygscb.com/IMP-CPos/b2c/pay/uploadPic.do";

    static {
        //总共为如下5种尺寸
        imgResizeMap.put("50_50", true);// 头像
        imgResizeMap.put("300_300", true);// 头像
        imgResizeMap.put("400_260", true);// 头像
        imgResizeMap.put("50_0", true);// 头像
        imgResizeMap.put("300_0", true);// 头像
        imgResizeMap.put("650_0", true);// 头像
    }

    /**
     * 仅适合小图片
     *
     * @throws Exception
     */
    FileDao fileDao = null;
    public static String FileEntityLink = "FileEntityLink";

    public FileItemAction() {
        fileDao = new FileCloudDaoBaseImpl();
    }

    @POST
    @Seller
    @Member
    @Path("/deleteEntityFiles")
    public void deleteEntityFiles() throws Exception {
        String _id = ControllerContext.getPString("_id");
        List<Object> params = new ArrayList<>();
        params.add(_id);
        String sql = "delete from " + FileEntityLink + " where _id=?";
        MysqlDaoImpl.getInstance().exeSql(sql, params, FileEntityLink);
    }

    @POST
    @Seller
    @Member
    @Path("/uploadBase64")
    public void uploadBase64() throws Exception {
        upload();
    }

    public static enum StoringStatus {
        //create刚创建还未存储完成,fail存储失败,success存储成功,del已被删除
        create, fail, success, del
    }

    @POST
    @Seller
    @Member
    @Path("/upload")
    public void upload() throws Exception {
        byte[] re = ControllerContext.getContext().getReq().getContentByteArray();
        String imageName = UUID.randomUUID().toString()+".jpg";
        String fileName = "\\data\\kyb\\file_strage\\image\\"+imageName;
        FileOutputStream fout = new FileOutputStream(fileName);
        //将字节写入文件
        fout.write(re);
        fout.close();
        //检查对应的字段是否完整
        String fileId = ControllerContext.getPString("fileId");
        String entityField = ControllerContext.getPString("entityField");
        String category = null;
        if ("businessLicense".equals(entityField)) {
            category = "03";
        } else if ("idCardImgFront".equals(entityField)) {
            category = "00";
        } else if ("idCardImgBack".equals(entityField)) {
            category = "01";
        } else if ("idCardImgHand".equals(entityField)) {
            category = "02";
        } else if ("doorImg".equals(entityField)) {
            category = "04";
        } else if ("sitephoto".equals(entityField)) {
            category = "05";
        } else {
            category = "06";
        }
        if (StringUtils.isEmpty(fileId)) {
            throw new UserOperateException(400, "fileId is null");
        }
        String name = ControllerContext.getPString("name");
        if (StringUtils.isEmpty(name)) {
            throw new UserOperateException(400, "name is null");
        }
        String projectName = ControllerContext.getPString("projectName");
        if (StringUtils.isEmpty(projectName)) {
            throw new UserOperateException(400, "projectName is null");
        }
        String entity = ControllerContext.getPString("entityName");
        if (StringUtils.isEmpty(entity)) {
            throw new UserOperateException(400, "entityName is null");
        }
        String entityId = ControllerContext.getPString("entityId");
        if (StringUtils.isEmpty(entityId)) {
            throw new UserOperateException(400, "entityId is null");
        }
        if (StringUtils.isEmpty(entityField)) {
            throw new UserOperateException(400, "entityField is null");
        }

        Map<String, Object> old = MysqlDaoImpl.getInstance().findById2Map(entityName, fileId, null, null);
        if (old != null) {
            throw new UserOperateException(400, "fileId 已经存在!");
        }
        File file = new File(fileName);
        Map<String,String> data = new HashMap<>();
        Message msg = Message.newReqMessage("1:POST@/payment/Gpay/uploadPic");
        msg.getContent().put("category",category);
        JSONObject req = ServiceAccess.callService(msg).getContent();
        data.putAll(req);
        Map<String,String> image = httpUploadFile(data,UPLOADPIC_URL,"UTF-8",file);

        Map<String, Object> v = new HashMap<>();
        v.put("_id", fileId);
        v.put("name", name);
        v.put("extendName", image.get("filename"));
        v.put("type", FileExecuteUtils.getExtension(name));
        v.put("createTime", System.currentTimeMillis());
        v.put("creator", ControllerContext.getContext().getCurrentUserId());
        v.put("storingStatus", StoringStatus.create.toString());
        v.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("FileItem", v);

        try {
            fileDao.saveFile(fileId, re);
        } catch (Exception e) {
            e.printStackTrace();
            v.clear();
            v.put("_id", fileId);
            v.put("storingStatus", StoringStatus.fail.toString());
            MysqlDaoImpl.getInstance().saveOrUpdate("FileItem", v);
            throw new RuntimeException(e);
        }
        v.clear();
        v.put("_id", fileId);
        v.put("storingStatus", StoringStatus.success.toString());
        MysqlDaoImpl.getInstance().saveOrUpdate("FileItem", v);


        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
        }
        Map<String, Object> link = new HashMap<>();
        link.put("_id", UUID.randomUUID().toString());
        link.put("fileId", fileId);
        link.put("projectName", projectName);
        link.put("entityName", entity);
        link.put("entityId", entityId);
        link.put("entityField", entityField);
        link.put("createTime", System.currentTimeMillis());
        link.put("creator", ControllerContext.getContext().getCurrentUserId());
        MysqlDaoImpl.getInstance().saveOrUpdate("FileEntityLink", link);

        //System.out.println("===get OK:" + re.length);
        ControllerContext.getContext().getReq().setContentByteArray(null);

    }


    public static void main(String[] args){
        String s ="phsh315_B18121200000278_03_20190315_19031516525376210087.jpeg";
        System.out.println(s.length());
    }


    @GET
    @Seller
    @Member
    @Path("/queryEntityFiles")
    public void queryEntityFiles() throws Exception {

        String sql = "select t2._id as _id,t1._id as icon,t1.name as name,t1.size as size,t1._id as fileId from FileItem t1";
        sql += " left join FileEntityLink t2 on t2.fileId=t1._id";
        sql += " where t2.entityName=?";
        sql += " and t2.entityId=?";
        sql += " and t2.entityField=?";
        sql += " order by t1.createTime asc";
        List<String> returnField = new ArrayList<String>();
        returnField.add("_id");
        returnField.add("icon");
        returnField.add("name");
        returnField.add("size");
        returnField.add("fileId");
        List<Object> paramValues = new ArrayList<Object>();
        paramValues.add(ControllerContext.getPString("_entityName"));
        paramValues.add(ControllerContext.getPString("_entityId"));
        paramValues.add(ControllerContext.getPString("_entityField"));
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, paramValues);
        JSONObject rObj = new JSONObject();
        rObj.put("items", li);
        rObj.put("count", li.size());
        rObj.put("pageSize", 500);
        toResult(200, rObj);
    }

    /**
     * 2进制的方式上传图片
     *
     * @throws Exception
     */
    @POST
    @Path("/uploadByte_byteIn")
    public void saveMyIcon() throws Exception {
        // 通过当前登录的userId覆盖_id参数
        String fileId = UUID.randomUUID().toString();

        FileDaoBaseImpl fileDao = new FileDaoBaseImpl();
        InputStream in = ControllerContext.getContext().getReqIn();
        Map<String, Object> v = new HashMap<>();
        v.put("_id", fileId);
        v.put("name", "icon.jpg");
        v.put("createTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("FileItem", v);

        fileDao.saveFile(fileId, in);
        JSONObject rObj = new JSONObject();
        rObj.put("fileId", fileId);
        toResult(200, rObj);
    }


    /**
     * base64的方式保存图片
     *
     * @throws Exception
     */
    @POST
    @Path("/uploadBase64_byteIn")
    public void uploadBase64_byteIn() throws Exception {

        String requestBodyStr = FileExecuteUtils.getInstance().readInputStream(ControllerContext.getContext().getReqIn(), "utf-8").toString();
        String body = null, idName = null;
        if (requestBodyStr.indexOf(",") > 1) {
            String[] split = requestBodyStr.split(",");
            idName = split[0];
            body = split[2];
        }

        String[] split = idName.split("/");
        String fileId = split[0];
        String name = URLDecoder.decode(split[1], "utf-8");
        JSONObject con = ControllerContext.getContext().getReq().getContent();
        con.put("_id", fileId);
        con.put("name", name);
        super.save();

        byte[] b = Base64.getDecoder().decode(body);
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        fileDao.saveFile(fileId, in);

        toResult(200, con);
    }

    /**
     * 传统的form表单保存图片
     *
     * @throws Exception
     */
    @POST
    @Path("/upload_byteIn")
    public void upload_byteIn() throws Exception {
        //super.save();
        //1.向中央服务器获取
    }

    /**
     * 获取图片
     *
     * @throws Exception
     */
    @GET
    @Member
    @Seller
    @Path("/showImg")
    public void showImg() throws Exception {
        JSONObject re = ControllerContext.getContext().getReq().getContent();
        String fileId = (String) re.get("_id");
        if (StringUtils.isEmpty(fileId)) {
            throw new UserOperateException(400, "必须设置fileId");
        }
        String wh = (String) re.get("wh");
        byte[] value;
        if (wh != null) {
            if (!imgResizeMap.containsKey(wh)) {
                throw new UserOperateException(400, "不存在的图片尺寸");
            }
            String key = fileId + "_" + wh;
            String cacheName = "fileResize";
            Object fileResize = EhcacheUtil.getInstance().get(cacheName, key);
            if (fileResize != null) {
                value = (byte[]) fileResize;
            } else {
                Integer w = Integer.valueOf(wh.split("_")[0]);
                Integer h = Integer.valueOf(wh.split("_")[1]);
                byte[] oldByte = fileDao.getFile(fileId);
                File src = File.createTempFile("img_resize", key);
                Files.write(Paths.get(src.toURI()), oldByte);
                File target = File.createTempFile("img_resize", key + "_target");
                ImageIM4JavaUtils.absoluteScaleFile(src.getCanonicalPath(), w, h, target.getCanonicalPath());
                value = Files.readAllBytes(Paths.get(target.toURI()));
                EhcacheUtil.getInstance().put(cacheName, key, value);
            }
        } else {
            value = fileDao.getFile(fileId);
        }

        ControllerContext.getContext().getResp().setContentByteArray(value);

        toResult(200, "{}");
    }

    @PUT
    @Path("/uploadFileBase64")
    public void uploadFileBase64() throws Exception {
        JSONObject value = ControllerContext.getContext().getReq().getContent();
        if (!value.containsKey("_id")) {
            new UserOperateException(200, "必须包含_id");
        }
        Map<String, Object> file = new HashMap<String, Object>();
        String fileId = UUID.randomUUID().toString();
        file.put("_id", value.getString("_id"));
        file.put("name", value.get("name"));
        file.put("extendType", value.get("extendType"));
        file.put("type", value.get("name"));
        file.put("creator", value);
        file.put("createTime", System.currentTimeMillis());

        String contentStr = value.getString("contentStr");
        byte[] b = Base64.getDecoder().decode(contentStr);
        int size = b.length;
        file.put("size", Long.valueOf(size));
        dao.saveOrUpdate(entityName, file);

        ByteArrayInputStream in = new ByteArrayInputStream(b);
        fileDao.saveFile(fileId, in);
    }

    @Override
    @POST
    @Path("/del")
    public void del() throws Exception {
        String id = ControllerContext.getPString("_id");
        checkIsLink(id);
        super.del();
    }

    @POST
    @Path("/delFileLink")
    public void delFileLink() throws Exception {
        String fileId = ControllerContext.getPString("fileId");
        Map<String, Object> p = new HashMap<>();
        p.put("fileId", fileId);
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().findAll2Map("FileEntityLink", p, null, null, Dao.FieldStrategy.Include);
        if (li != null) {
            for (Map<String, Object> map : li) {
                String _id = (String) map.get("_id");
                MysqlDaoImpl.getInstance().remove("FileEntityLink", _id);
            }
        }

        long count = MysqlDaoImpl.getInstance().findCount("FileEntityLink", p);
        if (count == 0) {//将文件对象置为删除标志
            Map<String, Object> v = new HashMap<>();
            v.put("_id", fileId);
            v.put("storingStatus", StoringStatus.del.toString());
            MysqlDaoImpl.getInstance().saveOrUpdate(entityName, v);
        }

    }


    private void checkIsLink(String id) throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("fileId", id);
        Map<String, Object> l = MysqlDaoImpl.getInstance().findOne2Map("FileEntityLink", p, null, null);
        if (l != null) {
            String json = JSONObject.fromObject(l).toString();
            Logger.getLogger(this.getClass()).info(json);
            throw new UserOperateException(400, "该文件[" + id + "]还关联了: " + json);
        }
    }

    @Override
    @POST
    @Path("/deleteMore")
    public void deleteMore() throws Exception {

        JSONObject values = ControllerContext.getContext().getReq().getContent();
        JSONArray ids = values.getJSONArray("ids");
        for (Object id : ids) {
            checkIsLink((String) id);
        }
        super.deleteMore();
    }

   /* @Test
    public void deleteFile() {
        String fileName = "D:\\workspace\\yzxf_test\\yzxf-core\\image\\8a607bfd-4dce-42eb-bdd2-5a00bbbc2ba6.jpg";
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
        }
    }*/


    /**
     * 上传图片
     * @param reqData  请求参数
     * @param reqUrl   请求地址
     * @param encoding  编码
     * @param fileparam file对象
     * @return
     * @throws Exception
     */
    public static  Map<String,String> httpUploadFile(Map<String, String> reqData, String reqUrl, String encoding, File fileparam) throws Exception{
        Map<String, String> rspData = new HashMap<String,String>();
        log.info("请求地址:" + reqUrl);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(reqUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        httpPost.setHeader("user-Agent",
                "Mozilla/4.0(compatible; MSIE 5.0; Windows NT; DigExt)");
        String type = fileparam.getName().substring(fileparam.getName().lastIndexOf(".") + 1);

        ContentType contentType = null;
        if (("jpg".equalsIgnoreCase(type)) || ("jpeg".equalsIgnoreCase(type)))
            contentType = ContentType.create("image/jpeg");
        else if ("png".equalsIgnoreCase(type))
            contentType = ContentType.create("image/png");
        else if ("bmp".equalsIgnoreCase(type))
            contentType = ContentType.create("image/bmp");
        else
            contentType = ContentType.MULTIPART_FORM_DATA;

        //设置上传的图片内容
        builder.addBinaryBody("fileContent", fileparam, contentType, fileparam.getName());
        if (reqData != null)
            //设置其它form表单参数
            for (Iterator localIterator = reqData.entrySet().iterator(); localIterator.hasNext(); ) { Map.Entry entry = (Map.Entry)localIterator.next();
                builder.addTextBody((String)entry.getKey(), (String)entry.getValue());
            }

        HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String body = null;
        if (entity != null){
            body = EntityUtils.toString(entity, encoding);
            Map<String,String> tmpRspData  = SDKUtil.convertResultStringToMap(body);
            rspData.putAll(tmpRspData);
        }
        EntityUtils.consume(entity);
        response.close();

        return rspData;

    }

}