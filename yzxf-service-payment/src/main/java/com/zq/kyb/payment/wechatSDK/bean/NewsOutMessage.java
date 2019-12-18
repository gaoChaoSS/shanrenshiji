/* 
 * jeasyPro
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/
 * 2013-8-11 下午3:32:55
 */
package com.zq.kyb.payment.wechatSDK.bean;


import com.zq.kyb.payment.wechatSDK.inf.MsgTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * 输出图文消息
 *
 * @author ____′↘
 */
public class NewsOutMessage extends OutMessage {

    private String MsgType = MsgTypes.NEWS.getType();
    private Integer ArticleCount;

    private List<Articles> Articles;

    public String getMsgType() {
        return MsgType;
    }

    public int getArticleCount() {
        return ArticleCount;
    }

    public List<Articles> getArticles() {
        return Articles;
    }

    public void setArticles(List<Articles> articles) {
        if (articles != null) {
            if (articles.size() > 10)
                articles = new ArrayList<Articles>(articles.subList(0, 10));

            ArticleCount = articles.size();
        }
        Articles = articles;
    }
}
