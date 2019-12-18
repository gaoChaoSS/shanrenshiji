/*
 *
 */
package com.zq.kyb.payment.utils;

import com.zq.kyb.core.init.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于对文件的一些基本操作,读写等
 */
public class FileExecuteUtils {
    public static final String FILENAME_KEY = "FILENAME_STR";
    public static final String DIRPATH_KEY = "DIRPATH_STR";

    private static FileExecuteUtils fileExecuteUtils;
    private static Log log = LogFactory.getLog(FileExecuteUtils.class);
    private List<File> al = new ArrayList<File>();

    public static FileExecuteUtils getInstance() {
        if (fileExecuteUtils == null) {
            fileExecuteUtils = new FileExecuteUtils();
        }
        return fileExecuteUtils;
    }

    public static void main(String[] args) throws IOException {
        // FileExecuteUtils.ss();
        String path = "D:\\1.jpg";
        // Logger.getLogger(this.getClass()).info(new
        // File("D:\\ss\\product.pconline.com.cn\\mobile\\nokia\\p167"
        // ).mkdirs());
        System.out.print(FileExecuteUtils.getInstance().readFile("d:/", "PhoneImgTest.java", null));
    }

    public static void createDirForPath(String pathName) throws IOException {
        File file = new File(pathName);
        String dirPath = file.getCanonicalPath().substring(0, file.getCanonicalPath().length() - file.getName().length());
        File dir = new File(dirPath);
        if (!dir.exists() || dir.isFile()) {
            dir.mkdirs();
        }
    }

    // =========================写文件 ===========================================

    /**
     * 保存文件
     *
     * @param content  文件内容
     * @param path     输出文件的目录路径
     * @param filename 输出文件的文件名
     * @param rename   是否重命名
     * @return 存放文件的路径
     * @throws java.io.IOException 抛出异常
     */
    public String writeFile(InputStream content, String path, String filename, boolean rename) throws IOException {
        return writeFile(content, path + File.separator + filename, rename);
    }

    /**
     * @param content
     * @param filepath
     * @param isRename
     * @return
     * @throws IOException
     */
    public String writeFile(InputStream content, String filepath, boolean isRename) throws IOException {
        File file = null;
        FileOutputStream buf = null;
        FileChannel cf = null;
        try {
            filepath = getNoExistsFilePathStr(filepath, isRename);
            file = new File(filepath);
            buf = new FileOutputStream(file);
            cf = buf.getChannel();
            byte[] src = new byte[1024 * 8];
            int i = 0;
            while ((i = content.read(src)) != -1) {
                cf.write(ByteBuffer.wrap(src, 0, i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cf != null)
                    cf.close();
                if (buf != null)
                    buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filepath;
    }

    /**
     * 写文本文件
     *
     * @param content  文本文件内容
     * @param filePath 输出文件的目录路径
     * @param fileName 输出文件的文件名
     * @param rename   是否重命名
     * @throws java.io.IOException 抛出异常
     */
    public void writeFile(String content, String filePath, String fileName, boolean isRename, String default_encoding) throws IOException {
        writeFile(content, filePath + "/" + fileName, isRename, default_encoding);
    }

    /**
     * 新io写文件
     *
     * @param content
     * @param filepath
     * @param isRename
     * @param default_encoding
     * @return
     */
    public File writeFile(String content, String filepath, boolean isRename, String default_encoding) {
        File file = null;
        FileOutputStream buf = null;
        FileChannel cf = null;
        try {
            filepath = getNoExistsFilePathStr(filepath, isRename);
            file = new File(filepath);
            buf = new FileOutputStream(file);
            byte[] src = content.getBytes(default_encoding == null ? Constants.DEFAULT_ENCODING : default_encoding);
            cf = buf.getChannel();
            cf.write(ByteBuffer.wrap(src));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cf != null)
                    cf.close();
                if (buf != null)
                    buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public String writeFile(byte[] content, String filepath, boolean isRename) {
        File file = null;
        FileOutputStream buf = null;
        FileChannel cf = null;
        try {
            filepath = getNoExistsFilePathStr(filepath, isRename);
            file = new File(filepath);
            buf = new FileOutputStream(file);
            cf = buf.getChannel();
            cf.write(ByteBuffer.wrap(content));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cf != null)
                    cf.close();
                if (buf != null)
                    buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filepath;
    }

    // =========================读文件 ===========================================

    public StringBuffer readFile(String filepath, String filename, String default_encoding) throws IOException {
        return readFile(filepath + "/" + filename, default_encoding);
    }

    public StringBuffer readFile(String filepath) throws IOException {
        return readFile(filepath, Constants.DEFAULT_ENCODING);
    }

    public StringBuffer readInputStream(InputStream filename, String default_encoding) throws IOException {
        StringBuffer str = new StringBuffer();

        String charsetName = default_encoding == null ? Constants.DEFAULT_ENCODING : default_encoding;
        Charset ch = Charset.forName(charsetName);
        InputStreamReader in = new InputStreamReader(filename, ch);
        BufferedReader br = new BufferedReader(in);
        char[] b = new char[1024 * 16];
        int i = 0;
        while ((i = br.read(b)) != -1) {
            str.append(b, 0, i);
        }
        br.close();
        return str;
    }

    public StringBuffer readFile(String filepath, String default_encoding) throws IOException {
        filepath = getNoExistsFilePathStr(filepath, false);
        File file = new File(filepath);
        file.createNewFile();
        return readInputStream(new FileInputStream(file), default_encoding);

    }

    /**
     * 将文件输出给流
     *
     * @param path
     * @param outputStream
     * @throws IOException
     */
    public void readFileToOutStream(String filepath, OutputStream outputStream) throws IOException {

        File file = null;
        FileInputStream buf = null;
        FileChannel cf = null;
        BufferedOutputStream bf = new BufferedOutputStream(outputStream);
        try {
            filepath = getNoExistsFilePathStr(filepath, false);
            file = new File(filepath);
            file.createNewFile();
            buf = new FileInputStream(file);
            cf = buf.getChannel();
            byte[] array = new byte[1024 * 8];
            int i = 0;
            while ((i = cf.read(ByteBuffer.wrap(array))) != -1) {
                bf.write(array, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cf != null)
                    cf.close();
                if (buf != null)
                    buf.close();
                if (buf != null)
                    buf.close();
                if (bf != null)
                    bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 适合小文件
     *
     * @param string
     * @return
     * @throws FileNotFoundException
     */
    public byte[] readFileToBytes(String filepath) throws FileNotFoundException {
        File file = new File(filepath);
        FileInputStream buf = new FileInputStream(file);

        return readFileToBytes(buf);
    }

    public byte[] readFileToBytes(FileInputStream buf) {
        ByteArrayOutputStream bf = new ByteArrayOutputStream();
        FileChannel cf = null;
        try {
            cf = buf.getChannel();
            byte[] array = new byte[1024 * 8];
            int i = 0;
            while ((i = cf.read(ByteBuffer.wrap(array))) != -1) {
                bf.write(array, 0, i);
            }
            return bf.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cf != null)
                    cf.close();
                if (buf != null)
                    buf.close();
                if (buf != null)
                    buf.close();
                if (bf != null)
                    bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String fileExist(String filepath, String filename) {

        File file = new File(filepath);
        String path;
        if (!file.exists()) {
            // Logger.getLogger(this.getClass()).debug("filepath:" + filepath);
            file.mkdirs();
        }
        if (filepath.substring(filepath.length()).equals("/")) {
            path = filepath + filename;
        } else {
            path = filepath + "/" + filename;
        }
        return path;
    }

    public static void fileCopy(String file1, String file2, boolean reName) throws IOException {
        File file_in = new File(file1);
        String filenamePath = getNoExistsFilePathStr(getFileFillPath(file2), reName);
        File file_out = new File(filenamePath);

        BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(file_in));
        BufferedOutputStream out1 = new BufferedOutputStream(new FileOutputStream(file_out));
        byte[] bytes = new byte[1024];
        int c;
        while ((c = in1.read(bytes)) != -1) {
            out1.write(bytes, 0, c);
        }
        in1.close();
        out1.close();
    }

    public static void mkdirs(File filein) {
        if (filein.exists()) {
            if (filein.isFile()) {
                filein.mkdirs();
            }
        } else {
            filein.mkdirs();
        }
    }

    /**
     * 根据一个文件返回一个规范完整的包含"/"的路径。
     *
     * @param file 输入文件
     * @return 路径
     * @throws java.io.IOException 抛出异常
     */
    public static String getFileFillPath(File file) throws IOException {
        String path = file.getCanonicalPath();
        path = getFileFillPath(path);
        return path;
    }

    public static String getFileFillPath(String path) {
        if (path.indexOf("\\") != -1) {
            path = path.replaceAll("\\\\", "/");
        }
        return path;
    }

    public void mkdirPath(String path) throws IOException {
        Hashtable<String, String> hashtable;
        String dirpath;
        path = getFileFillPath(new File(path));
        hashtable = getPathStr(path, "/");
        assert hashtable != null;
        dirpath = hashtable.get(DIRPATH_KEY);
        File file = new File(dirpath);
        // 创建目录
        mkdirs(file);
    }

    public Object[] getDirAllFile(File file) {
        al = new ArrayList<File>();
        getDirFile(file);
        return al.toArray();

    }

    private void getDirFile(File file) {
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isFile()) {
                al.add(file1);
            } else {
                getDirFile(file1);
            }
        }
    }

    /**
     * 判断文件路径是否存在，不存在创建目录，然后判读文件是否存在，若存在就自动改名 注意：自动改名只针对文件操作，不包含文件夹
     *
     * @param path     文件的全路径
     * @param isRename 是否重命名,true:重命名，false:不重命名，覆盖原文件
     * @return String 重命名的全路径
     * @throws java.io.IOException 抛出异常
     */
    public static String getNoExistsFilePathStr(String path, boolean isRename) throws IOException {
        Hashtable<String, String> hashtable;
        String dirpath;
        path = getFileFillPath(new File(path));
        hashtable = getPathStr(path, "/");
        assert hashtable != null;

        dirpath = hashtable.get(DIRPATH_KEY);
        File file = new File(dirpath);
        // 创建目录
        mkdirs(file);

        String filesname = hashtable.get(FILENAME_KEY);
        // 文件是否存在
        String newPath = path;
        String newName = filesname;
        String outextension = getNameWithoutExtension(filesname);
        String extension = getExtension(filesname);
        file = new File(newPath);
        int fileid = 1;
        if (isRename) {
            while (file.exists() && file.isFile()) {
                if (newName.indexOf(".") != -1) {
                    newName = outextension + "(" + fileid + ")." + extension;
                } else {
                    newName = filesname + "(" + fileid + ")";
                }
                newPath = dirpath + "/" + newName;
                file = new File(newPath);
                fileid = fileid + 1;
            }
        }
        log.debug(newPath);
        return newPath;
    }

    /**
     * 根据某个文件路径取的文件目录的路径
     *
     * @param path 路径
     * @return String
     */
    public static String getDirpathForPath(String path) {
        Hashtable<String, String> hashtable = null;
        String dir_path;
        if (path.indexOf("/") != -1) {
            hashtable = getPathStr(path, "/");
        } else if (path.indexOf("\\") != -1) {
            hashtable = getPathStr(path, "\\");
        }
        if (hashtable != null) {
            dir_path = hashtable.get(DIRPATH_KEY);
        } else {
            dir_path = path;
        }
        return dir_path;
    }

    /**
     * 根据有文件路径取的文件名
     *
     * @param path 文件路径
     * @return 文件名
     */
    public static String getFilenameForPath(String path) {
        Hashtable<String, String> hashtable = null;
        String files_name;
        if (path.indexOf("/") != -1) {
            hashtable = getPathStr(path, "/");
        } else if (path.indexOf("\\") != -1) {
            hashtable = getPathStr(path, "\\");
        }
        if (hashtable != null) {
            files_name = hashtable.get(FILENAME_KEY);
        } else {
            files_name = path;
        }
        return files_name;
    }

    /**
     * 拆分字符串，根据分隔符
     *
     * @param path 路径
     * @param sp   分隔符
     * @return 一个hashtable包含拆分后的两个字符串
     */
    public static Hashtable<String, String> getPathStr(String path, String sp) {
        String filename = "";
        String dirpath = "";
        if (path != null) {
            String spStr = sp;
            if ("\\".equals(sp)) {
                spStr = "\\\\";
            }
            if (path.indexOf(sp) != -1) {
                String[] s = path.split(spStr);
                // for (String s1 : s) {
                // Logger.getLogger(FileExecuteUtils.class).debug("s:" + s1);
                // }
                if (s.length > 0) {
                    filename = s[s.length - 1];
                    dirpath = path.substring(0, path.length() - filename.length() - 1);
                }
            } else {
                filename = path;
            }
            // Logger.getLogger(FileExecuteUtils.class).debug("filename:" + filename);
            // Logger.getLogger(FileExecuteUtils.class).debug("dirpath:" + dirpath);
        }
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put(FILENAME_KEY, filename);
        hashtable.put(DIRPATH_KEY, dirpath);
        return hashtable;
    }

    /**
     * 创建子目录
     *
     * @param file    目标文件
     * @param dirname 子目录名
     * @return 建好的目录的file对象
     * @throws java.io.IOException 异常处理
     */
    public static File createChildDir(File file, String dirname) throws IOException {
        File file1 = new File(file.getCanonicalPath() + File.separator + dirname);
        file1.mkdirs();
        return file1;
    }

    /**
     * delete a file form the path of a file
     *
     * @param path the path
     * @return 是否删除成功 ,
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static int deleteDir(File file) {
        int level = 0;
        if (file.isDirectory()) {
            File[] subFile = file.listFiles();
            for (File aSubFile : subFile) {
                level = level + deleteDir(aSubFile);
            }
        }
        if (file.delete()) {
            level++;
        }
        return level;
    }

    public static List<File> searchFile(String fileName, String path) {
        Pattern p = Pattern.compile(fileName.toLowerCase());
        List<File> list = new ArrayList<File>();

        File file = new File(path);
        File[] subFiles = file.listFiles();
        for (File subFile : subFiles) {
            if (subFile.isDirectory()) {
                list.addAll(searchFile(fileName, subFile.getAbsolutePath()));
            } else {

                Matcher m = p.matcher(subFile.getName().toLowerCase());
                if (m.find()) {
                    list.add(subFile);
                }

            }
        }
        return list;
    }

    /**
     * 根据文件名得到文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名, 如果没有扩展名为:"";
     */
    public static String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index == -1)
            return "";
        return filename.substring(index + 1);
    }

    /**
     * 根据文件名得到文件除去扩展名的部分
     *
     * @param fileName 文件名
     * @return 文件除去扩展名的部分.
     */
    public static String getNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.equals(""))
            return "";
        int index = fileName.lastIndexOf(".");
        if (index == -1)
            return fileName;
        return fileName.substring(0, index);
    }

    // 递归删除目录下的文件
    public void deleteDirs(String path) throws IOException {
        File deltempfile = new File(path);
        while (deltempfile.exists()) {
            if (deltempfile.isFile()) {
                deltempfile.delete();
            } else {
                File[] cher = deltempfile.listFiles();
                if (cher.length > 0) {
                    for (File aCher : cher) {
                        if (aCher.isFile()) {
                            aCher.delete();
                        } else {
                            Logger.getLogger(this.getClass()).info(aCher.getCanonicalPath());
                            deleteDirs(aCher.getCanonicalPath());
                        }
                    }
                } else {
                    deltempfile.delete();
                }
            }

        }
    }

    /**
     * 取得用户一个临时文件
     * <p/>
     * 临时文件的特点是,业务逻辑不关注文件的名称和路径,只关注内容.
     *
     * @param fileName 文件名
     * @return 临时文件
     * @throws java.io.IOException io异常处理
     */
    public File getTempFile(String fileName) throws IOException {
        String filepath = getNoExistsFilePathStr(getFileFillPath(Constants.TEMP_DIR) + "/" + fileName, true);
        return new File(filepath);
    }

}
