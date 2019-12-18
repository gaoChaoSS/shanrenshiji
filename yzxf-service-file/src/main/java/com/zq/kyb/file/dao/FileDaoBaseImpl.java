package com.zq.kyb.file.dao;


import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.file.img.ImageIM4JavaUtils;
import com.zq.kyb.util.FileExecuteUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class FileDaoBaseImpl implements FileDao {
    public static String basefilePath = "C:\\file_strage";

    static {
        if (!new File(basefilePath).exists()) {
            basefilePath = "D:\\file_strage";
        }
        if (!new File(basefilePath).exists()) {
            basefilePath = "/data/kyb/file_strage";
        }
        if (!new File(basefilePath).exists()) {
            throw new RuntimeException("--basefilePath not exist!,path=" + basefilePath + ",windows 可以是:C:\\file_strage或D:\\file_strage");
        }
    }

    public static final Map<String, Boolean> i = new HashMap<String, Boolean>();

    static {
        i.put("50_50", true);// 头像
        i.put("300_300", true);// 头像
        i.put("100_100", true);// 头像
        i.put("500_500", true);// 头像
        i.put("90_70", true);// 头像
        i.put("650_0", true);// 头像
    }

    @Override
    public InputStream getFile(String fileId, String wh) throws Exception {
        if (wh == null) {
            return new FileInputStream(getFilePath(fileId));
        } else {
            i.put("300_0", true);
            String newId = fileId + "_" + wh + ".jpg";
            if (!i.containsKey(wh)) {
                throw new UserOperateException(400, "不支持该格式：" + wh);
            }
            File dir = new File(basefilePath + "/resize_img");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir.getCanonicalPath() + "/" + newId);
            if (!file.exists()) {// 执行切图
                Integer w = Integer.valueOf(wh.split("_")[0]);
                Integer h = Integer.valueOf(wh.split("_")[1]);
                ImageIM4JavaUtils.absoluteScaleFile(getFilePath(fileId), w, h, file.getCanonicalPath());
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            return fileInputStream;
        }
    }

    @Override
    public byte[] getFile(String fileId) throws Exception {
        return new byte[0];
    }

    private String getFilePath(String fileId) throws Exception {
        Map<String, Object> fileobj = MysqlDaoImpl.getInstance().findById2Map("FileItem", fileId, new String[]{"createTime", "sellerId"}, Dao.FieldStrategy.Include);
        if (fileobj == null) {
            throw new UserOperateException(400, "文件记录不存在");
        }
        return new File(getFileDirPath(fileobj) + '/' + fileId).getCanonicalPath();
    }

    @Override
    public void saveFile(String fileId, InputStream in) throws Exception {

        Map<String, Object> fileobj = MysqlDaoImpl.getInstance().findById2Map("FileItem", fileId, new String[]{"createTime", "sellerId"}, Dao.FieldStrategy.Include);
        if (fileobj == null) {
            throw new UserOperateException(400, "文件记录不存在");
        }
        String fileDirPath = getFileDirPath(fileobj);
        File dir = new File(fileDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String f = fileDirPath + '/' + fileId;
        FileExecuteUtils.getInstance().writeFile(in, f, false);
        Logger.getLogger(this.getClass()).info("-save file to: " + dir.getCanonicalPath() + "/" + fileId);
    }

    @Override
    public void saveFile(String fileId, byte[] in) throws Exception {

    }

    @Override
    public void delFile(String fileId) throws Exception {
        Map<String, Object> fileobj = MysqlDaoImpl.getInstance().findById2Map("FileItem", fileId, new String[]{"createTime", "sellerId"}, Dao.FieldStrategy.Include);
        if (fileobj == null) {
            throw new UserOperateException(400, "文件记录不存在");
        }
        String filePath = getFileDirPath(fileobj);
        if (filePath != null) {
            File f = new File(filePath + "/" + fileId);
            f.deleteOnExit();
        }
    }

    private String getFileDirPath(Map<String, Object> fileobj) {
        Long time = (Long) fileobj.get("createTime");
        if (time != null && time > 0) {
            String dataStr = new SimpleDateFormat("yy_MM_dd").format(new Date(time));
            return basefilePath + "/" + fileobj.get("sellerId") + "/" + dataStr;
        }
        return null;
    }
}
