import com.zq.kyb.core.conn.websocket.adapter.ClientAdapter;
import com.zq.kyb.util.ByteUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * Created by hujoey on 16/8/2.
 */
public class TestSendFile {
    public static void main(String[] args) throws Exception {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true);//信任所有证书
        WebSocketClient client = new WebSocketClient(sslContextFactory);
        client.start();
        URI uri = URI.create("wss://localhost:9009/events");
        client.connect(new WebSocketAdapter() {

            public Session session;

            @Override
            public void onWebSocketClose(int statusCode, String reason) {
                session.close();
                session = null;
                System.out.println("connect close!");

            }

            @Override
            public void onWebSocketConnect(Session sess) {
                session = sess;
                System.out.println("connect success!");
                WriteCallback callback = new WriteCallback() {
                    @Override
                    public void writeFailed(Throwable x) {
                        System.out.println("发送失败!");
                    }

                    @Override
                    public void writeSuccess() {
                        System.out.println("发送成功!");
                    }
                };
                RemoteEndpoint remote = session.getRemote();


                Path filePath = Paths.get("/Users/hujoey/Downloads/brand.zip");
                try (
                        FileChannel fileChannel_from = (FileChannel.open(filePath, EnumSet.of(StandardOpenOption.READ)));
                ) {
                    long startTime = System.nanoTime();
                    // Allocate a direct ByteBuffer
                    ByteBuffer bytebuffer = ByteBuffer.allocateDirect(1024 * 128);//128k

                    // Read data from file into ByteBuffer
                    int bytesCount;
                    while ((bytesCount = fileChannel_from.read(bytebuffer)) > 0) {
                        //flip the buffer which set the limit to current position, and position to 0
                        bytebuffer.flip();
                        //write data from ByteBuffer to file
                        //fileChannel_to.write(bytebuffer);
                        remote.sendPartialBytes(bytebuffer, false);
                        //for the next read
                        bytebuffer.clear();
                    }
                    remote.sendPartialBytes(ByteBuffer.allocate(0), true);

                    long elapsedTime = System.nanoTime() - startTime;
                    System.out.println("Elapsed Time is " + (elapsedTime / 1000000000.0) + " seconds");
                } catch (IOException ex) {
                    System.err.println(ex);
                }


                //remote.sendBytes(buf, callback);
            }

            @Override
            public void onWebSocketBinary(byte[] payload, int offset, int len) {
                System.out.println("recv byte[]: " + ByteUtil.bytesToHexString(payload));
            }

            @Override
            public void onWebSocketText(String message) {
                System.out.println("recv text: " + message);
            }
        }, uri);
    }
}
