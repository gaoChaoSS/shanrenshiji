<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="phonecat">
<head>
    <%
        pageContext.setAttribute("serverVersion", "0.1205");
        String name = request.getServerName();
        //if ("www.youlai01.com".equals(name)) {
        pageContext.setAttribute("js_min", ".min");
        //}
    %>
    <script>
        window.angular_temp_version = ${serverVersion};
    </script>
    <script charset="utf-8" src="https://map.qq.com/api/js?v=2.exp&key=GVDBZ-62ZWJ-AFEFF-KOFMJ-FYRVF-NQF43"></script>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">

    <meta content="" name="Keywords">
    <meta name="description" content=""/>


    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>
    <link rel="icon" href="/favicon.ico" type="image/x-icon"/>
    <link rel="bookmark" href="/favicon.ico"/>

    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1"/>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>

    <link rel="stylesheet" href="/css/front/front.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/icomoon/style.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/iconfont/iconfont.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/iconfont2/iconfont.css?_v=${serverVersion}"/>


    <%--<link rel="stylesheet" type="text/css" media="screen and (min-width:1000px)"--%>
    <%--href="/css/front/front_pc.css?_v=${serverVersion}"/>--%>
    <%--<link rel="stylesheet" type="text/css" media="(min-width: 100px) and (max-width: 1000px)"--%>
    <%--href="/css/front/front_mobile.css?_v=${serverVersion}"/>--%>
    <link rel="stylesheet" type="text/css"
          href="/css/front/front_mobile_min.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" media="screen and (min-width:750px)"
          href="/css/front/front_pc_1.css?_v=${serverVersion}"/>


    <link rel="stylesheet" type="text/css" href="/css/front/index.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/front/form.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/front/title.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/front/store.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/front/order.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/aliFontTwo/iconfont.css?_v=${serverVersion}"/>

    <%-- 这处非常重要 --%>
    <base href="/${serverFront}/">
    <title></title>
</head>
<body ng-controller="allBodyCtrl" ng-style="">
<%--重要:用于javascript即时判断当前在什么模式--%>
<div id="isPc" class="pcCon" style="width: 1px; height: 1px; position: fixed; top: -100px;"></div>
<!-- 过渡层 -->
<div id="allLoadingCon" class="loadingAllCon"
     style="background-color: rgb(255, 255, 255); position: fixed; z-index: 10000; top: 0; left: 0; right: 0; bottom: 0; text-align: center; padding-top: 30px;">
    <div class="loadingBk"></div>
    <%--<div class="title" style="color: #888; text-shadow: 0 0 2px #888;padding:15px;">加载中...</div>--%>
</div>
<%--点击到顶端--%>
<div id="toTop" class="iconfont icon-fanhuidingbu1"></div>
<%--回到主页--%>
<div id="goHome" class="icon-home2" ng-show="!isIndex&&!isPc()&&!isLoginPage"
     ng-click="goPage('/home/index');"></div>
<%--回到主页--%>
<div id="closePop" class="iconfont icon-addcollapse" ng-if="isPop&&isPc()" onclick="window.history.back();"></div>


<%--******* topbar 开始 *********--%>
<%--topBar的背景--%>
<div class="topBarBg" ng-show="!isWechat&&showTop"></div>
<%--返回按钮, 仅用于移动端--%>
<div class="topBtn top_left textNowrap font1 iconfont icon-back mobileCon" ng-show="showTop&&!isWechat"
     ng-click="goBack();"></div>
<%--标题, 仅用于移动端--%>
<div class="top_title textNowrap font1 mobileCon" ng-show="showTop&&!isWechat" ng-bind="windowTitle"></div>
<%--弹出菜单,暂时不使用--%>
<%--<div class="topBtn top_right textNowrap font1 icon-menu" ng-show="!windowTitleHide&&mainMenuShow&&!isWechat"--%>

<%--靠右边的按钮组合--%>
<div class="top_right" ng-show="showTop">
    <%--<div class="fl topBtn icon-bubbles2 font2" style="cursor:pointer;"--%>
    <%--title="联系店家"></div>--%>
    <%--靠右边的第2️个按钮--%>
    <div class="fl topBtn textNowrap iconfont icon-search font2" ng-click="showSearchInputCon()"
         style="cursor:pointer;" title="搜索商品"></div>
    <div class="fl topBtn pcCon icon-arrow-right2 textNowrap" title="退出系统" style="color:orangered"
         ng-click="doLogout()"></div>
    <div class="clearDiv"></div>
</div>
<%--显示的店名--%>
<div class="top_left " ng-show="showTop||isPc()" ng-click="goPage('/store/index')">
    <div class="item font2" style="color:#fff;" ng-bind="storeInfo.seller.name"></div>
    <div class="item bu">
        <span class="font4" style="color:#aaa;" ng-bind="storeInfo.name"></span>
        <span class="font5" ng-class="storeInfo.$$statusClass" ng-bind="storeInfo.$$statusTitle"
              title="{{storeInfo.$$statusDesc}}"></span>
        <span class="notHigh iconfont icon-unfold"></span>
    </div>
    <div class="clearDiv"></div>
</div>
<%--******* topbar 结束 *********--%>
<div id="allmap" ng-show="false"></div>

<%--仅用于PC导航的bar--%>
<div class="daohang pcCon" style="">
    <div class="item fl textNowrap" ng-class="isShowPage($index)?'highL':'no'" ng-repeat="item in pageList"
         ng-if="!item.startsWith('/pop/')"
         ng-click="goPage(item)">
        <span class="notHigh font4" ng-bind="pageContextMap[item].title"></span>
        <i class="iconfont icon-shanchu1 notHigh" ng-if="pageList.length>1"
           ng-click="closePage($index);$event.stopPropagation();"></i>
    </div>
</div>

<!-- 微信下的返回按钮 -->
<div class="topBtn mobileCon textNowrap font1 iconfont icon-back"
     style="border-radius: 50%;opacity: 0.72;box-shadow: 0 0 4px #aaa;top:10px;left:8px;background-color: #777;height:40px;width:40px;padding-top:10px"
     ng-show="isShowBack()&&isWechat"
     ng-click="goBack();"></div>


<%--首页的底部tab--%>
<div id="bottomTabList" class="tab-bottom tab" ng-if="isIndex">
    <div class="item mobileCon" ng-repeat="item in bottomTabList" ng-style="{width:tabBottomItemWidth}"
         ng-class="hasSelectedBottom(item)?'selected':''"
         ng-click="goPage(item.url)" ng-hide="item.isWechat">
        <i ng-class="hasSelectedBottom(item)?item.hoverIcon:item.iconClass">
            <div class="notifi " ng-if="item.$$notifi>0" title="共:{{item.$$notifi}}个" ng-bind="item.$$notifiStr"></div>
        </i>
        <div ng-bind="item.name"></div>
    </div>

    <div class="item pcCon" ng-repeat="item in bottomTabList"
         ng-class="hasSelectedBottom(item)?'selected':''"
         ng-click="goPage(item.url)">
        <i class="font2" ng-class="item.iconClass">
            <div class="notifi " ng-if="item.$$notifi>0" title="共:{{item.$$notifi}}个" ng-bind="item.$$notifiStr"></div>
        </i>
        <div ng-bind="item.name"></div>
    </div>
</div>


<%--登录页面--%>
<div class=" loginCon" style="transition: 1s all" ng-if="notLogin" ng-include="loginTemp"></div>


<%--微信下回到主页按钮--%>
<%--<div id="home" class="rightFixed icon-home2" ng-click="goPage('/home/index')"></div>--%>

<%--页面加载的过渡--%>
<div id="pageLoadingCon" ng-class="isIndex?'':'notIndex'" class="notHigh">加载中...</div>
<%--半透明遮罩--%>
<div ng-if="isPop" class="mark"></div>
<%--页面层--%>
<div class="topPageCon" ng-class="pageConClass($index)" ng-show="!loadingPage&&isShowPage($index)"
     ng-repeat="item in pageList"
     ng-include="pagePathMap[item]"></div>


<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/lib/angular${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/getBrowserInfo.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/lib/script${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/extend-1.0.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/json2.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/init_websocket.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/singlePageApp.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.3.0.js"></script>
<%--<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=R4lIN0cqMUqRiolY7zETtvB0KROmYWVR&_v=${serverVersion}"></script>--%>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=R4lIN0cqMUqRiolY7zETtvB0KROmYWVR"></script>

<script type="text/javascript" src="/yzxfMember_page/yzxfMember_app.js?_v=${serverVersion}"></script>
<%--各种提示声音--%>
<audio id="notifi" style="display: none;">
    <%--<source type="audio/ogg" src="/yzxfMember_page/img/notifi.ogg"/>--%>
    <%--<source type="audio/mpeg" src="/yzxfMember_page/img/notifi.mp3"/>--%>
</audio>

</body>
</html>