package com.zq.kyb.file.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.file.dao.FileCloudDaoBaseImpl;
import com.zq.kyb.file.dao.FileDao;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用于确定一个文件的位置及是否删除状态
 * 字段包括:ipHouse(机房),ip(公网ip地址),blockId(文件块的编号),position(文件块中的偏移量),isDelete(是否被删除,用于整理文件块时),fileId(文件编号)
 * 写文件时: 一个fileId至少包括3个不同主机的块定义,
 */
public class FileConfigAction extends BaseActionImpl {

    FileCloudDaoBaseImpl fileDao = null;

    public FileConfigAction() {
        fileDao = new FileCloudDaoBaseImpl();
    }


    /**
     * 获取文件存储目录的信息
     */
    @GET
    @Path("/getFileDirInfo")
    public void getFileDirInfo() throws IOException {

        File file = new File(FileCloudDaoBaseImpl.storingPath);
        String[] li = file.list((dir, name) -> name.startsWith("block_"));


        JSONObject re = new JSONObject();
        //可能数据很大,只显示前面的100项
        Map<String, Object> n = new HashMap<>();
        Iterator<String> it = FileCloudDaoBaseImpl.indexMap.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String key = it.next();
            n.put(key, FileCloudDaoBaseImpl.indexMap.get(key));
            if (i++ > 100) {
                break;
            }
        }

        re.put("fileIdMap", n);
        re.put("fileIdMapSize", FileCloudDaoBaseImpl.indexMap.size());
        re.put("position", FileCloudDaoBaseImpl.position);
        re.put("blockNo", FileCloudDaoBaseImpl.blockNo);
        re.put("blockList", li);

        toResult(200, re);
    }

    /**
     * 获取文件存储目录的信息
     */
    @PUT
    @Path("/initIndexMap")
    public void initIndexMap() throws IOException {
        FileCloudDaoBaseImpl.initIndexMap();
        getFileDirInfo();
    }
}
