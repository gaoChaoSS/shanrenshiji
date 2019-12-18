package file;

import com.zq.kyb.util.ByteUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileChannel提供了map方法来把文件影射为内存映像文件：
 * MappedByteBuffer map(int mode,long position,long size);
 * 可以把文件的从position开始的size大小的区域映射为内存映像文件，mode指出了 可访问该内存映像文件的方式：READ_ONLY,READ_WRITE,PRIVATE.
 * a. READ_ONLY,（只读）： 试图修改得到的缓冲区将导致抛出 ReadOnlyBufferException.(MapMode.READ_ONLY)
 * b. READ_WRITE（读/写）： 对得到的缓冲区的更改最终将传播到文件；该更改对映射到同一文件的其他程序不一定是可见的。 (MapMode.READ_WRITE)
 * c. PRIVATE（专用）： 对得到的缓冲区的更改不会传播到文件，并且该更改对映射到同一文件的其他程序也不是可见的；
 * 相反，会创建缓冲区已修改部分的专用副本。 (MapMode.PRIVATE)
 * <p>
 * 三个方法：
 * <p>
 * a. force();缓冲区是READ_WRITE模式下，此方法对缓冲区内容的修改强行写入文件
 * b. load()将缓冲区的内容载入内存，并返回该缓冲区的引用
 * c. isLoaded()如果缓冲区的内容在物理内存中，则返回真，否则返回假
 * <p>
 * 三个特性：
 * 调用信道的map()方法后，即可将文件的某一部分或全部映射到内存中，映射内存缓冲区是个直接缓冲区，继承自ByteBuffer,但相对于ByteBuffer,它有更多的优点：
 * a. 读取快
 * b. 写入快
 * c. 随时随地写入
 */
public class FileWRTest {
    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<String>();
        list.add("第一行");
        list.add("第二行");
        list.add("第三行");
        Path path = Paths.get("/Users/hujoey/Downloads/f.txt");
        //Files.write(path, list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);


        //BufferedWriter writer = new BufferedWriter(new FileWriter(dest, true));

        //启动1个线程来不停的读取文件
        new Thread() {
            int i = 0;

            @Override
            public void run() {
                RandomAccessFile raf1 = null;
                try {
                    raf1 = new RandomAccessFile("/Users/hujoey/Downloads/f.txt", "r");
                    //FileChannel f = FileChannel.open(path, StandardOpenOption.WRITE);
                    FileChannel f = raf1.getChannel();
                    MappedByteBuffer b = f.map(FileChannel.MapMode.READ_ONLY, 5, 10);
                    while (i++ < 10) {
                        byte[] bf = new byte[4];
                        Logger.getLogger(this.getClass()).info("get:" + i + ":" + ByteUtil.bytesToHexString(bf));
                        b.flip();
                        // Thread.sleep(2000);
                    }
                    b.force();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();


        //启动1个线程来不停的追加写文件
        new Thread() {
            int i = 0;

            @Override
            public void run() {
                try {
                    RandomAccessFile raf1 = new RandomAccessFile("/Users/hujoey/Downloads/f.txt", "rw");
                    //FileChannel f = FileChannel.open(path, StandardOpenOption.WRITE);
                    FileChannel f = raf1.getChannel();
                    MappedByteBuffer b = f.map(FileChannel.MapMode.READ_WRITE, raf1.length(), 1024);
                    while (i++ < 10) {
                        byte[] bytes = "111".getBytes("utf-8");
                        b.put(bytes);
                        //Thread.sleep(2000);
                        b.force();
                        b.flip();
                        Logger.getLogger(this.getClass()).info("put:" + i);
                    }
                    b.force();
                    //f.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }


}
