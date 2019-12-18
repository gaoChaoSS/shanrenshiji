package com.zq.kyb.file.dao;


/**
 * 文件存储方案
 */

import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileCloudDaoBaseImpl implements FileDao {
    public static String storingPath = null;
    private static RandomAccessFile rwLock;
    private static FileChannel fcLock;
    private static FileLock flLock;

    public static int position = 0;//当前写文件的位置,必须静态变量,一个实例只能有一个变量
    public static int blockNo = 0;

    private static int maxSize = 256 * 1024 * 1024;//256M一个块
    private static int fileMaxSize = 1024 * 1024 * 4;

    public static ConcurrentMap<String, Integer[]> indexMap = new ConcurrentHashMap<>();

    static {
        initIndexMap();
    }

    public static void initIndexMap() {
        Properties prop = new Properties();
        InputStream in = Object.class.getResourceAsStream("/fileStoring.properties");
        RandomAccessFile rw = null;
        FileChannel fc = null;
        try {
            prop.load(in);
            storingPath = prop.getProperty("storingPath").trim();

            getFileLock();

            indexMap.clear();
            position = 0;

            //加载 index
            String indexFilePath = storingPath + "/index";
            File f = new File(indexFilePath);
            if (!f.exists()) {
                new File(f.getParent()).mkdirs();
                f.createNewFile();
                return;
            }
            rw = new RandomAccessFile(indexFilePath, "r");
            fc = rw.getChannel();
            byte[] array = new byte[48];
            ByteBuffer wrap = ByteBuffer.wrap(array);
            while (fc.read(wrap) != -1) {
                byte[] b = new byte[36];
                wrap.flip();
                wrap.get(b);
                String fileId = new String(b, Constants.DEFAULT_ENCODING);
                //注意下面顺序
                int blockNoValue = wrap.getInt();
                int positionValue = wrap.getInt();
                int lenValue = wrap.getInt();

                indexMap.put(fileId, new Integer[]{blockNoValue, positionValue, lenValue});
                position = positionValue + lenValue;
                blockNo = blockNoValue;
                wrap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                releaseFileLock();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fc != null) {
                    fc.close();
                }
                if (rw != null) {
                    rw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void releaseFileLock() throws IOException {
        if (flLock != null) {
            flLock.release();
        }
        if (fcLock != null) {
            fcLock.close();
        }
        if (rwLock != null) {
            rwLock.close();
        }
    }

    public static void getFileLock() throws IOException {
        rwLock = new RandomAccessFile(storingPath + "/lock", "rw");
        fcLock = rwLock.getChannel();
        flLock = fcLock.tryLock();
        int times = 15;
        while (flLock == null && times-- > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flLock = fcLock.tryLock();
        }
        if (flLock == null) {
            throw new UserOperateException(500, "获取文件锁出错");
        }
    }


    @Override
    public InputStream getFile(String fileId, String cwh) throws Exception {
        return null;
    }

    @Override
    public byte[] getFile(String fileId) throws Exception {
        //依次从cache,节点1,节点2,节点3读取,前面任何一个读取到,后面就不管了.
        Integer[] value = indexMap.get(fileId);
        String name = storingPath + "/block_" + value[0];
        java.io.RandomAccessFile raf = null;
        java.nio.channels.FileChannel channel = null;
        try {//创建一个随机读写文件对象
            raf = new java.io.RandomAccessFile(name, "r");
            //long totalLen = raf.length();
            //System.out.println("文件总长字节是: " + totalLen);
            //打开一个文件通道
            channel = raf.getChannel();
            //映射文件中的某一部分数据以读写模式到内存中
            java.nio.MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, value[1], value[2]);
            byte[] b = new byte[value[2]];
            buffer.get(b);
            return b;
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (raf != null) {
                raf.close();
            }
        }
    }

    @Override
    public void saveFile(String fileId, InputStream in) throws Exception {

    }

    @Override
    public void saveFile(String fileId, byte[] in) throws Exception {
        if (in.length > fileMaxSize) {//存储的文件不能大于4M
            throw new UserOperateException(400, "存储的文件不能大于4M");
        }
        //1.获取需要存储的节点列表

        //2.向每个节点发送存储数据

        //3.所以节点返回成功,写入主节点索引,存储完毕
        try {
            getFileLock();
            Path path = Paths.get(storingPath + "/block_" + blockNo);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, in, StandardOpenOption.APPEND);

            Integer[] value = {blockNo, position, in.length};
            indexMap.put(fileId, value);

            Path indexPath = Paths.get(storingPath + "/index");
            if (!Files.exists(indexPath)) {
                Files.createFile(indexPath);
            }
            ByteBuffer indexB = ByteBuffer.allocate(fileId.length() + 12);
            indexB.put(fileId.getBytes(Constants.DEFAULT_ENCODING));
            indexB.putInt(value[0]);
            indexB.putInt(value[1]);
            indexB.putInt(value[2]);
            indexB.flip();
            Files.write(indexPath, indexB.array(), StandardOpenOption.APPEND);

            position += in.length;
            if (position > maxSize) {
                blockNo++;
                position = 0;
            }
        } finally {
            releaseFileLock();
        }
    }


    @Override
    public void delFile(String fileId) throws Exception {

    }


}
