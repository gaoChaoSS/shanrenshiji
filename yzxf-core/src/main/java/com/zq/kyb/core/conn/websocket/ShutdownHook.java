package com.zq.kyb.core.conn.websocket;


import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * 服务器关闭需要做的一些事
 */
public class ShutdownHook extends Thread {
    @Override
    public void run() {
        Logger.getLogger(this.getClass()).info("--stop service server:");
        Logger.getLogger(this.getClass()).info("moduleHost:" + Constants.moduleHost);
        Logger.getLogger(this.getClass()).info("moduleType:" + Constants.moduleType);
        Logger.getLogger(this.getClass()).info("moduleName:" + Constants.moduleName);
        Logger.getLogger(this.getClass()).info("modulePort:" + Constants.modulePort);
        Logger.getLogger(this.getClass()).info("adminHost:" + Constants.adminHost);
        Logger.getLogger(this.getClass()).info("adminPort:" + Constants.adminPort);

        //具体逻辑
        if (Constants.moduleType.equals("portal")) {
//            Logger.getLogger(this.getClass()).info("clear user online info!");
//            Message msg = Message.newReqMessage("1:PUT@/notification/UserLinkServer/clearServerUserLink");
//            msg.getContent().put("host", Constants.moduleHost);
//            msg.getContent().put("port", "" + Constants.modulePort);
//            try {
//                JSONObject con = ServiceAccess.callService(msg).getContent();
//                Logger.getLogger(this.getClass()).info("resp:" + con);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        } else if (Constants.moduleType.equals("service")) {

        }
    }
}
