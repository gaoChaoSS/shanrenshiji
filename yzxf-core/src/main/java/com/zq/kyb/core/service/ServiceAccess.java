package com.zq.kyb.core.service;

import com.zq.kyb.core.conn.http.RpcFilter;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.ctrl.ControllerProcess;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.ProtoMessage;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.OutputStreamContentProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 服务访问
 */
public class ServiceAccess {

    /**
     * 异步访问服务,如果是本模块直接反射访问java类, 如果是其他模块,通过websocket建立连接访问对应的api
     *
     * @param msg
     * @return
     * @throws Exception
     */
    public static CompletableFuture<Message> call(Message msg) throws Exception {
        String path = msg.getActionPath();
        final CompletableFuture<Message> future = new CompletableFuture<>();

        Map<String, Object> m = Message.actionPathToMap(path);
        String moduleName = (String) m.get("moduleName");
        if (moduleName.equals(Constants.moduleName)) {
            try {
                ControllerContext.getContext().setReq(msg);
                ControllerContext.getContext().setToken(msg.getTokenStr());
                new ControllerProcess().exeAction();
                future.complete(ControllerContext.getContext().getResp());
            } catch (Exception e) {
                sendErr(e, msg, future);
            } finally {
                ControllerContext.clearContext();
            }
        } else {
            // callRemoteByWebsocket(msg, future, moduleName);
            callRemoteByHttp(msg, future, moduleName);
        }
        return future;
    }

    static HttpClient httpClient = null;

    private static void callRemoteByHttp(Message msg, CompletableFuture<Message> future, String moduleName) {
        InputStream responseContent = null;
        OutputStream outputStream = null;
        try {
            if (StringUtils.isEmpty(msg.getTokenStr())) {
                msg.setTokenStr(ControllerContext.getContext().getToken());
            }

            // ClientAdapter.reqMsgMap.put(msg.get_id(), future);

//            if (msg.getActionPath().startsWith("1:POST@/file/FileItem/upload")) {//包括所有的上传
//                ByteBuffer data = msg.toByteArray();
//                // sess.getRemote().sendBytesByFuture(data).get(15, TimeUnit.SECONDS);
//            } else {
            String s = msg.toString();
            Logger.getLogger(ServiceAccess.class).info("--------->> req: " + s);
            // Instantiate HttpClient
            if (httpClient == null) {
                httpClient = new HttpClient();
                // Configure HttpClient, for example:
                httpClient.setFollowRedirects(false);
                // Start HttpClient
                httpClient.start();
            }
            OutputStreamContentProvider contentProvider = new OutputStreamContentProvider();
            String hostName = null;

            ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> li = RpcFilter.hostInfoMap.get(moduleName);
            if (li != null && li.size() > 0) {
                for (String s1 : li.keySet()) {
                    ConcurrentHashMap<String, Object> obj = li.get(s1);//TODO 通过一定规则来计算选择哪个服务器
                    hostName = s1;
                    break;
                }
            }
            if (hostName == null) {
                throw new UserOperateException(500, "模块 [" + moduleName + "] 不可用!");
            }

            Request request = httpClient.POST("http://" + hostName + "/rpc/api/action").content(contentProvider);
            InputStreamResponseListener listener = new InputStreamResponseListener();
            request.send(listener);
            ProtoMessage.message resp = Message.genProtoMessage(msg);
            outputStream = contentProvider.getOutputStream();
            resp.writeDelimitedTo(outputStream);

            // ContentResponse response =
            Response response = listener.get(15, TimeUnit.SECONDS);
            // Look at the response
            if (response.getStatus() == 200) {
                // Use try-with-resources to close input stream.
                responseContent = listener.getInputStream();
                // Your logic here
                // byte[] content =
                ProtoMessage.message req = ProtoMessage.message.parseDelimitedFrom(responseContent);
                Message re = Message.newReqMessage(req);
                Logger.getLogger(ServiceAccess.class).info("<<--------- resp:" + re);
                future.complete(re);
            } else {
                throw new UserOperateException(400, "请求异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErr(e, msg, future);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (responseContent != null) {
                try {
                    responseContent.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sendErr(Exception e, Message msg, CompletableFuture<Message> future) {
        e.printStackTrace();
        msg.setCode(500);
        msg.getContent().clear();
        msg.getContent().put("errMsg", e.getMessage());
        future.complete(msg);
    }

    public static Message callService(Message msg) throws Exception {
        CompletableFuture<Message> f = call(msg);
        Message m = f.get(30, TimeUnit.SECONDS);
        if (m.getCode() != 200) {
            Logger.getLogger("transcation_error").error(msg.toString() + "," + m.toString());
            throw new UserOperateException(m.getCode(), (String) m.getContent().get("errMsg"));
        }
        return m;
    }

    public static JSONObject getRemoveEntity(String modelName, String entityName, String entityId) throws Exception {
        if (Constants.moduleName.equals(modelName)) {
            throw new RuntimeException("不能在本模块中远程调用本模块!");
        }
        Message m = Message.newReqMessage("1:GET@/" + modelName + "/" + entityName + "/show");
        m.getContent().put("_id", entityId);
        String storeId = ControllerContext.getPString("storeId");
        if (StringUtils.isNotEmpty(storeId)) {
            m.getContent().put("storeId", storeId);
        }
        JSONObject content = ServiceAccess.callService(m).getContent();
        return content.size() == 0 ? null : content;
    }
}
