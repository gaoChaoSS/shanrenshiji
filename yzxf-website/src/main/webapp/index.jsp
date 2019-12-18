<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/index.css?_v=${_v}" rel="stylesheet">
    <%@include file="./inc/top.jsp" %>
</head>
<%--<%--%>
    <%--if (session.isNew()) {--%>
        <%--session.setAttribute("videoPlay", true);--%>
    <%--} else {--%>
        <%--session.setAttribute("videoPlay", false);--%>
    <%--}--%>
<%--%>--%>
<body style="overflow-y: hidden;">
<div id="mod0">
    <%--<div id="videoSession" style="display: none;">${videoPlay}</div>--%>
    <%--<div id="videoDiv">--%>
        <%--<video id="video" src="./video/huawei.mp4" controls="controls" autoplay="autoplay"></video>--%>
        <%--<img class="closeIcon" src="img/closeIcon.png">--%>
    <%--</div>--%>
    <%@include file="./inc/header.jsp" %>
    <div class="bannerImg" id="bannerImg"></div>
</div>

<div id="mod1" class="mod-center minWidth">
    <div>
        <div class="conTitle pagesDiv">项目简介</div>
        <img class="conTitle2" src="./img/title_icon1.png">
        <div class="mod-text1">
            <p style="color:#138bbe">核心理念：共享创新，合作共赢</p>
            <p>根本宗旨：建立一个完全同市场对接，充满内生动力的新型养老保障制度，
                将老百姓传统被动缴纳转换为积极主动积攒，开辟养老金来源的新渠道。</p>
            <div>"四川国联普惠科技有限公司"是一个开放、智能的网络和移动互联应用平台，利用互联网技术整合各类生产资源、
                产品服务商及消费终端用户，集商务流通、保险金融、信息交流、广告宣传、消费增值于一身，
                充分满足多方需求，建立商务流通利益共同体，实现用户日常消费虚拟价值累积转换，从而塑
                造一种创新消费生态链体系，并最终实现和谐循环的共享共赢商业新模式。</div>
            <div>通过联合中国银联、保险公司、诚信商家、第三方支付等共同搭建消费养老O2O增值服务平台，
                用户在平台线上或线下合作商家、联盟商家、直营店消费的同时额外获得让利回馈，并进入用
                户在保险公司的专属账户，日计息月复利不断保值增值，作为个人养老金补充保障。</div>
        </div>
        <%--<div class="mod-guild1">--%>
            <%--<img src="./img/index_intro.png">--%>
            <%--<div>--%>
                <%--<span style="margin-left:12px">养老保障</span>--%>
                <%--<span style="margin-left:197px">互联网金融</span>--%>
                <%--<span style="margin-left:201px">三方整合</span>--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>
</div>

<div id="mod2" class="bgWidth minWidth" style="background: #eee;">
    <div>
        <div class="conTitle pagesDiv">平台运行</div>
        <img class="conTitle2" src="./img/title_icon1.png">
        <img class="autoImg" src="./img/about_2.png">
    </div>
</div>

<div id="mod3" class="minWidth">
    <div>
        <div class="conTitle pagesDiv">项目特点</div>
        <img class="conTitle2" src="./img/title_icon1.png">
        <div class="mod-guild2 flex3">
            <div>
                <img src="./img/trait_1.png">
                <div>
                    <div>惠民超市</div>
                    <div>supermarket</div>
                    <div>遍布社区、乡镇街道和新农村的惠民超市，体验方便购物的同时
                        又能获得不菲的养老金充！真正地做到了一边消费一边养老！</div>
                </div>
            </div>
            <%--<div>--%>
                <%--<img src="./img/trait_2.png">--%>
                <%--<div>--%>
                    <%--<div>移动金融</div>--%>
                    <%--<div>mobile finance</div>--%>
                    <%--<div>随时随地享受移动快捷的支付和信用保值增值,让消费理财更轻松！--%>
                        <%--用户可以使用支付宝，微信以及现金支付，让支付更加便捷！</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <div>
                <img src="./img/trait_3.png">
                <div>
                    <div>电子商务</div>
                    <div>internet commerce</div>
                    <div>未来将是互联网大数据的应用时代，四川国联普惠科技有限公司也会用行动来跟着新时代
                        电商的脚步。将这一新型技术得以展现！</div>
                </div>
            </div>
            <div>
                <img src="./img/trait_4.png">
                <div>
                    <div>慈善事业</div>
                    <div>charitable</div>
                    <div>消费和慈善事业紧密相连，这很好地体现出来了社会服务再分配到每
                        个消费者，这也是四川国联普惠科技有限公司重要的一部分！</div>
                </div>
            </div>
            <%--<div>--%>
                <%--<img src="./img/trait_5.png">--%>
                <%--<div>--%>
                    <%--<div>商业保险</div>--%>
                    <%--<div>commercial insurance</div>--%>
                    <%--<div>四川国联普惠科技有限公司联合各大保险公司，给消费者、代理商带来非常贴合自--%>
                        <%--身的保险产品，让大家用得放心、舒心！</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <div>
                <img src="./img/trait_6.png">
                <div>
                    <div>养老保障</div>
                    <div>elderly support</div>
                    <div>四川国联普惠科技有限公司实现的是让所有消费者都可以拿到一笔可观的养老保障，
                        一边消费的同时也给自己提供了可靠的养老保障！</div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="mod4" class="minWidth" style="background: url('./img/stern_bk.png') 50% 0 no-repeat;height:502px;overflow:hidden;background-size: auto 100%;">
    <div>
        <div class="conTitle pagesDiv" style="color:#fff">项目亮点</div>
        <img class="conTitle2" src="./img/title_icon1.png">
        <div class="mod-guild3 flex3">
            <div>
                <img src="./img/stern_1.png">
                <div>商业模式独特</div>
                <div>利于资源整合</div>
            </div>
            <div class="line1"></div>
            <div>
                <img src="./img/stern_2.png">
                <div>新兴惠民产业</div>
                <div>市场前景广阔</div>
            </div>
            <div class="line1"></div>
            <div>
                <img src="./img/stern_3.png">
                <div>传承大爱精神</div>
                <div>铸就爱心事业</div>
            </div>
            <div class="line1"></div>
            <div>
                <img src="./img/stern_4.png">
                <div>符合国家政策</div>
                <div>契合民生需求</div>
            </div>
        </div>
    </div>
</div>

<div id="mod5" class="minWidth" style="background:#fff;overflow:hidden">
    <div>
        <div class="conTitle pagesDiv">加入我们</div>
        <img class="conTitle2" src="./img/title_icon1.png">
        <div class="mod-guild4 flex1">
            <div>
                <img src="./img/joinUs_1.png" >
                <div class="guild5-title">商家加盟</div>
                <div class="guild5-text" >通过四川国联普惠科技有限公司App附近商店功能,主动向附近消费者推荐店铺,寻找目标客户。</div>
                <div>
                    <a href="businessJoin.jsp" class="btn-red">立刻前往</a>
                </div>
            </div>
            <div>
                <img src="./img/joinUs_2.png" >
                <div class="guild5-title">消费者加入</div>
                <div class="guild5-text" >在四川国联普惠科技有限公司联盟商户内购物,能享受不同程度让利,并获赠养老金。</div>
                <div>
                    <a href="register.jsp" class="btn-red">立刻前往</a>
                </div>
            </div>
            <div>
                <img src="./img/joinUs_3.png" >
                <div class="guild5-title">合作伙伴</div>
                <div class="guild5-text" >申请"居民养老金补充卡"发放权限,开发消费者,可获得收益。</div>
                <div>
                    <a href="angetJoin.jsp" class="btn-red">立刻前往</a>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="mod6" style="overflow:hidden">
    <div class="minWidth" style="background:#F2F2F2;overflow:hidden">
        <div>
            <div class="conTitle pagesDiv">合作机构</div>
            <img class="conTitle2" src="./img/title_icon1.png">
            <div class="mod-guild5 flex1">
                <img src="./img/together_1.png" >
                <img src="./img/together_2.png" >
            </div>
        </div>
    </div>

    <div class="index footerCon">
        <%@include file="./inc/footer.jsp" %>
    </div>
</div>

<script src="./js/index.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>