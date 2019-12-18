package com.zq.kyb.core.model;

import com.google.protobuf.ByteString;
import com.zq.kyb.core.ctrl.BaseAction;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 关于消息的设计
 *
 * @author hujoey
 */

public class Message implements java.io.Serializable {
    private static final long serialVersionUID = -8485381335760882410L;


    public enum MsgType {
        TYPE_NOTIFI,
        TYPE_REQ;
    }

    public enum MsgFormat {
        JSON,
        BYTE;
    }

    private String _id;// 每条消息的主键
    private String type = MsgType.TYPE_REQ.toString();//消息类型,notifi为广播消息,req为请求响应消息
    private String creator;// 消息原始创建者，用于跟踪最终消息应该由谁收到响应，由服务器生成,客户端由user@deviceId构成，服务器直接由serverId构成
    private Long createTime;// 由服务器生成
    private String actionPath;//后台逻辑执行的路径, 格式为1.0:get@/common/ZQService/query
    private String tokenStr;//发送者的身份验证
    private int code = -1;// -1为请求消息，>=0为响应消息(收到响应消息，若发现)，如果：20x为成功，大于40x为失败

    private MsgFormat reqContentType = MsgFormat.JSON;
    private MsgFormat respContentType = MsgFormat.JSON;

    private JSONObject content = new JSONObject();// 默认为一个JSON对象
    private byte[] contentByteArray = null;// 消息的内容是一个二进制数组
    private InputStream conIn = null;//一个输入的流
    private OutputStream conOut = null;//一个输出的流


    public byte[] getContentByteArray() {
        return contentByteArray;
    }

    public void setContentByteArray(byte[] contentByteArray) {
        this.contentByteArray = contentByteArray;
    }

    /**
     * 将传送二进制的消息,转换为二进制传输,以后考虑其他的:如protobuf,thrift
     * 协议为:前面4个字节(存储消息的长度)+消息的字节+byteArray的字节.
     *
     * @return
     */
    public ByteBuffer toByteArray() throws UnsupportedEncodingException {
        byte[] msgByte = this.toString().getBytes(Constants.DEFAULT_ENCODING);
        int size = 4;//表示消息数据的长度
        size += msgByte.length;
        size += this.contentByteArray.length;
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.putInt(msgByte.length);
        bb.put(msgByte);
        bb.put(this.getContentByteArray());
        Logger.getLogger(this.getClass()).info("1position:" + bb.position() + ", limit:" + bb.limit());
        bb.flip();
        Logger.getLogger(this.getClass()).info("2position:" + bb.position() + ", limit:" + bb.limit());

        return bb;
    }

    public static Message byteArrayTo(byte[] payload) throws UnsupportedEncodingException {
        //Logger.getLogger(this.getClass()).info("payload:" + payload.length);
        ByteBuffer b = ByteBuffer.wrap(payload);
        //Logger.getLogger(this.getClass()).info("position:" + b.position() + ", limit:" + b.limit());
        //b.flip();
        int msgLength = b.getInt();
        byte[] msgByte = new byte[msgLength];
        b.get(msgByte);
        byte[] contentBytes = new byte[payload.length - 4 - msgLength];
        b.get(contentBytes);
        Message msg = Message.jsonTo(JSONObject.fromObject(new String(msgByte, Constants.DEFAULT_ENCODING)));
        msg.setContentByteArray(contentBytes);
        return msg;
    }


    // private byte[] contentByte;// 二进制内容

    public Message() {

    }

    public static Message copy(Message req) {
        Message m = new Message();
        m.set_id(req.get_id());
        m.setType(req.getType());
        m.setCreator(req.getCreator());
        m.setCreateTime(req.getCreateTime());
        m.setActionPath(req.getActionPath());
        m.setTokenStr(req.getTokenStr());
        m.setCode(req.getCode());
        m.setContent(req.getContent());
        //m.setContentByteArray(req.getContentByteArray());
        return m;
    }

    public static Message jsonTo(JSONObject json) {
        Message m = new Message();
        m.set_id(json.getString("_id"));
        if (json.containsKey("creator")) {
            m.setCreator(json.getString("creator"));
        }
        if (json.containsKey("type")) {
            m.setType(json.getString("type"));
        }
        if (json.containsKey("createTime")) {
            m.setCreateTime(json.getLong("createTime"));
        }
        m.setActionPath(json.getString("actionPath"));
        if (json.containsKey("tokenStr")) {
            m.setTokenStr(json.getString("tokenStr"));
        }
        if (json.containsKey("code")) {
            m.setCode(json.getInt("code"));
        }
        if (json.containsKey("content")) {
            m.setContent(json.getJSONObject("content"));
        }

        return m;
    }

    public static Map<String, Object> actionPathToMap(String pathStr) {
        String[] splits = pathStr.split(":");
        Integer version = Integer.valueOf(splits[0]);
        String[] split = splits[1].split("@");
        String actionType = split[0].toUpperCase();
        String actionPath = split[1];
        String[] actionsStr = actionPath.split("/");
        int i = 0;
        String moduleName = null;
        String actionName = null;
        String methodName = null;
        for (String string : actionsStr) {
            if (StringUtils.isNotEmpty(string)) {
                i++;
                if (i == 1) {
                    moduleName = string;
                } else if (i == 2) {
                    actionName = string;
                } else if (i == 3) {
                    methodName = string;
                }
            }
        }
        Map<String, Object> path = new HashMap<>();
        path.put("version", version);
        path.put("actionType", actionType);
        path.put("moduleName", moduleName);
        path.put("actionName", actionName);
        path.put("methodName", methodName);
        return path;
    }

    public static Message newReqMessage(ProtoMessage.message msg) {
        Message m = new Message();
        m.set_id(msg.getId());
        m.setType(msg.getType().name());
        m.setCreator(msg.getCreator());
        m.setCreateTime(msg.getCreateTime());
        m.setActionPath(msg.getActionPath());
        m.setTokenStr(msg.getTokenStr());
        m.setCode(msg.getCode());

        m.setReqContentType(MsgFormat.valueOf(msg.getReqContentType().name()));
        m.setRespContentType(MsgFormat.valueOf(msg.getRespContentType().name()));

        m.setContentByteArray(msg.getContentByteArray().toByteArray());
        String content = msg.getContent();
        if (StringUtils.isNotEmpty(content)) {
            m.setContent(JSONObject.fromObject(content));
        }
        return m;
    }

    public static ProtoMessage.message genProtoMessage(Message msg) {
        ProtoMessage.message.Builder m = ProtoMessage.message.newBuilder();
        m.setId(msg.get_id());
        m.setType(ProtoMessage.message.MsgType.valueOf(msg.getType()));
        if (msg.getCreator() != null) {
            m.setCreator(msg.getCreator());
        }
        if (msg.getCreateTime() != null) {
            m.setCreateTime(msg.getCreateTime());
        }
        m.setActionPath(msg.getActionPath());
        if (msg.getTokenStr() != null) {
            m.setTokenStr(msg.getTokenStr());
        }
        m.setCode(msg.getCode());

        m.setReqContentType(ProtoMessage.message.MsgFormat.valueOf(msg.getReqContentType().name()));
        m.setRespContentType(ProtoMessage.message.MsgFormat.valueOf(msg.getRespContentType().name()));

        byte[] contentByteArray = msg.getContentByteArray();
        if (contentByteArray == null) {
            contentByteArray = new byte[]{};
        }
        m.setContentByteArray(ByteString.copyFrom(contentByteArray));
        JSONObject c = msg.getContent();
        if (c != null) {
            m.setContent(c.toString());
        }

        return m.build();
    }

    /**
     * 新建一条请求消息
     *
     * @param path
     * @return
     */
    public static Message newReqMessage(String path) {
        Message m = new Message();
        m.setActionPath(path);
        m.set_id(UUID.randomUUID().toString());
        return m;
    }

    public Message(String id, String actionPath) {
        this._id = id;
        this.actionPath = actionPath;
    }

    public Message(String id, String actionPath, JSONObject content) {
        this._id = id;
        this.actionPath = actionPath;
        this.content = content;
    }

    public JSONObject getContent() {
        content = content == null ? new JSONObject() : content;
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String toString() {
        JSONObject map = JSONObject.fromObject(this);
        //map.remove("tokenStr");
        return map.toString();
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTokenStr() {
        return tokenStr;
    }

    public void setTokenStr(String tokenStr) {
        this.tokenStr = tokenStr;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public InputStream getConIn() {
        return conIn;
    }

    public void setConIn(InputStream conIn) {
        this.conIn = conIn;
    }

    public OutputStream getConOut() {
        return conOut;
    }

    public void setConOut(OutputStream conOut) {
        this.conOut = conOut;
    }


    public MsgFormat getReqContentType() {
        return reqContentType;
    }

    public void setReqContentType(MsgFormat reqContentType) {
        this.reqContentType = reqContentType;
    }

    public MsgFormat getRespContentType() {
        return respContentType;
    }

    public void setRespContentType(MsgFormat respContentType) {
        this.respContentType = respContentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
