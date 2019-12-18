package com.zq.kyb.file.dao;

import java.io.InputStream;

public interface FileDao {


    public InputStream getFile(String fileId, String cwh) throws Exception;

    public byte[] getFile(String fileId) throws Exception;

    public void saveFile(String fileId, InputStream in) throws Exception;

    void saveFile(String fileId, byte[] in) throws Exception;

    public void delFile(String fileId) throws Exception;
}
