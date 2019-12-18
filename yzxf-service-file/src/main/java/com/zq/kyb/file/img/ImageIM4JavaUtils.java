package com.zq.kyb.file.img;

import com.zq.kyb.util.FileExecuteUtils;
import org.apache.log4j.Logger;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

public class ImageIM4JavaUtils {

    public static String ImageMagickPath = null;

    /**
     * 如果安装不在:/usr/local/ImageMagick,就建立一个连接到真实安装路径
     */
    public static final String LinuxImageMagickPath = "/usr/local/ImageMagick/bin";

    public static final String WindowsImageMagickPath = "d:/ImageMagick";

    static {
        File f = new File(LinuxImageMagickPath);
        if (f.exists()) {
            ImageMagickPath = LinuxImageMagickPath;
        }
        f = new File(WindowsImageMagickPath);
        if (f.exists()) {
            ImageMagickPath = WindowsImageMagickPath;
        }
        if (ImageMagickPath == null) {
            throw new RuntimeException("Not find ImageMagick Search Path~!");
        }
    }

    public static String absoluteScaleFile(String srcPath, int w, int h, String targetFile) throws Exception {
        try {
            IMOperation op = new IMOperation();
            op.addImage(srcPath);
            op.resize(w, h, '^').gravity("center").extent(w, h);

            // op.resize(width, height);
            // op.crop(w, h, left, top);
            op.addImage(targetFile);

            //将非jpg图片转换成jpg
            //IMOperation op1 = new IMOperation();
            //convert a.png -background white -flatten b.jpg
            //op1.addImage(targetFile);
            //op1.background("white");
            //op1.flatten();
            //op1.addImage(targetFile + ".jpg");

            ConvertCmd convert = new ConvertCmd(false);
            convert.setSearchPath(ImageMagickPath);
            convert.run(op);
            //convert.run(op1);

        } catch (Exception e) {
            Logger.getLogger(ImageIM4JavaUtils.class).info("--error:" + e.getMessage());
        }
        return targetFile;
    }

    public static byte[] absoluteScale(byte[] in, int w, int h) throws Exception {
        ByteArrayInputStream s = new ByteArrayInputStream(in);
        BufferedImage img = ImageIO.read(s);

        File tempfile = File.createTempFile("temp_zqcore_img_" + new java.util.Random().nextInt(10000), "");
        String path = FileExecuteUtils.getInstance().writeFile(in, tempfile.getPath(), true);
        String srcPath = FileExecuteUtils.getInstance().writeFile(in, tempfile.getPath(), true);
        int oldWidth = img.getWidth();
        int oldHeight = img.getHeight();

        int left = 0, top = 0, width = w, height = h;
        // if (oldWidth < w || oldHeight < h) {
        // return srcImg;
        // 新的需求：将其按原始比例放大到期望的宽或高

        // }
        if (w == 0) {
            width = (int) (1.0F * oldWidth * h / oldHeight);
        } else if (h == 0) {
            height = (int) (1.0F * oldHeight * w / oldWidth);
        } else {
            // 这种情况可能要裁图
            height = (int) (1.0f * w / oldWidth * oldHeight);
            if (height > h) {// 截高的情况
                top = (int) (1.0f * (height - h) / 2);
            } else if (height < h) {// 截宽的情况
                width = (int) (1.0f * h / oldHeight * oldWidth);
                height = h;
                left = (int) (1.0f * (width - w) / 2);
            }
        }
        // Image img = srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // BufferedImage ret = new BufferedImage(width, height, srcImg.getType() != 0 ? srcImg.getType() : 2);
        // ret.getGraphics().drawImage(img, 0, 0, null);

        IMOperation op = new IMOperation();

        op.addImage(srcPath);
        op.resize(w, h, '^').gravity("center").extent(w, h);
        // op.resize(width, height);
        // op.crop(w, h, left, top);

        tempfile = File.createTempFile("temp_zqcore_img_" + new java.util.Random().nextInt(100000), "");
        String outPath = FileExecuteUtils.getNoExistsFilePathStr(tempfile.getPath(), true);
        op.addImage(outPath);

        ConvertCmd convert = new ConvertCmd(false);
        convert.setSearchPath(ImageMagickPath);
        convert.run(op);

        byte[] readFileToBytes = FileExecuteUtils.getInstance().readFileToBytes(outPath);

        return readFileToBytes;
    }
}