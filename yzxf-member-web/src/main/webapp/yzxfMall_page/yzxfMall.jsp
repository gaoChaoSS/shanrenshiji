<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="phonecat">
<head>
    <%
        pageContext.setAttribute("serverVersion", "0.0211");
        String name = request.getServerName();
        //if ("www.youlai01.com".equals(name)) {
        pageContext.setAttribute("js_min", "");
        //}
    %>
    <script>
        window.angular_temp_version = ${serverVersion};
    </script>
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


    <%--<link rel="stylesheet" type="text/css" media="screen and (min-width:1000px)"--%>
    <%--href="/css/front/front_pc.css?_v=${serverVersion}"/>--%>
    <%--<link rel="stylesheet" type="text/css" media="(min-width: 100px) and (max-width: 1000px)"--%>
    <%--href="/css/front/front_mobile.css?_v=${serverVersion}"/>--%>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/index.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/my.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/help.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/template.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/general.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/account.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/mallCss/seller.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/aliFontTwo/iconfont.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/iconfont2/iconfont.css?_v=${serverVersion}"/>

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

<%--******* topbar 开始 *********--%>
<%--topBar的背景--%>
<div class="topBarBg" ng-show="!isWechat&&showTop"></div>
<%--******* topbar 结束 *********--%>
<div id="allmap" ng-show="false"></div>


<%--登录页面--%>
<div class=" loginCon" style="transition: 1s all" ng-if="notLogin" ng-include="loginTemp"></div>


<%--微信下回到主页按钮--%>
<%--<div id="home" class="rightFixed icon-home2" ng-click="goPage('/home/index')"></div>--%>

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

<script type="text/javascript" src="/yzxfMall_page/yzxfMall_app.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/yzxfMall_page/js/home/grid_type.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="../yzxfMall_page/js/home/cart.js?_v=${serverVersion}"></script>

<script type="text/javascript">
    function initialize() {
        window.getMap();
    }
    function loadScript() {
        if(!window.isEmpty(getCookie("_member_map"))){
            var map = JSON.parse(getCookie("_member_map"));
            setTimeout(function(){
//                window.getAreaCur({_id:-1,first:1});
                for(var i=map.length-1;i>=0;i--){
                    if(window.getAreaCur){
                        window.getAreaCur(map[i]);
                    }
                }
            },2000);
        }else{
            var script = document.createElement("script");
            script.src = "https://api.map.baidu.com/api?v=2.0&ak=R4lIN0cqMUqRiolY7zETtvB0KROmYWVR&callback=initialize"; //此 为 v2.0版本的引用方式
// http://api.map.baidu.com/api?v=2.0&ak=yourAppKey&callback=initialize"; // 此为 v2.0版本及以前版本的引用方式
            document.body.appendChild(script);
        }
    }
    // ￼JavaScript API 支持异步加载,您可以在引用脚本的时候添加 callback 参数,当脚本加载完成后 callback 函数会被立刻调用。请参考下面的使用示例:
    window.onload = loadScript;
</script>
<%--各种提示声音--%>
<audio id="notifi" style="display: none;">
</audio>

</body>
</html>