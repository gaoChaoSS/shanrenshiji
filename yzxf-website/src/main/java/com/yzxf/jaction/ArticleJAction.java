package com.yzxf.jaction;

import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hujoey on 17/2/17.
 */
public class ArticleJAction {

    public void list(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String allPid = req.getParameter("allPid");
        String dirId = req.getParameter("dirId");


        Message msg;

        if (StringUtils.isNotEmpty(allPid)) {
            msg = Message.newReqMessage("1:@/crm/ArticleList/query");
            msg.getContent().put("_pid", allPid);
            Message dirList = ServiceAccess.callService(msg);
            req.setAttribute("dirPage", dirList.getContent());

            JSONArray items = dirList.getContent().getJSONArray("items");

            dirId = StringUtils.isEmpty(dirId) && items != null && items.size() > 0 ? items.getJSONObject(0).getString("_id") : dirId;
            req.setAttribute("selectDirId", dirId);

            String pageNo = req.getParameter("pageNo");
            int p = 1;
            try {
                p = Integer.valueOf(pageNo);
            } catch (Exception e) {
            }

            p = p < 1 ? 1 : p;
            msg = Message.newReqMessage("1:@/crm/Article/query");
            msg.getContent().put("_pId", dirId);
            msg.getContent().put("pageNo", p);
            msg.getContent().put("pageSize", 10);
            Message articleList = ServiceAccess.callService(msg);

            req.setAttribute("page", articleList.getContent());
        }
    }

    public void show(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String _id = req.getParameter("_id");
        JSONObject article = ServiceAccess.getRemoveEntity("crm", "Article", _id);
        req.setAttribute("article", article);

        String dirId = StringUtils.mapValueIsEmpty(article, "pId") ? null : (String) article.get("pId");
        if (dirId != null) {
            req.setAttribute("articleDir", ServiceAccess.getRemoveEntity("crm", "ArticleList", dirId));
        }
    }
}
