<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="phonecat">
<head>
    <%
        pageContext.setAttribute("serverVersion", "0.23");
    %>
    <script>
        window.angular_temp_version = ${serverVersion};
    </script>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1"/>
    <link rel="stylesheet" href="../css/i_common.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/icomoon/style.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/iconfont/iconfont.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/i_layout.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_date.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_file.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/i_form.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/i_popWin.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/i_grid.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/i_fileBrowser.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/i_htmlEditor.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="../css/view/index.css?_v=${serverVersion}"/>
</head>
<body ng-controller="allBodyCtrl" ng-mousedown="clickAllBody();">
<!-- 过渡层 -->
<div id="allLoadingCon" class="loadingAllCon"
     style="background-color: rgb(255, 255, 255); position: fixed; z-index: 10000; top: 0; left: 0; right: 0; bottom: 0; text-align: center; padding-top: 100px;">
    <!-- img src="/member_page/img/logo.png" -->
    <div class="title" style="color: #888; text-shadow: 0 0 2px #888;" ng-bind="site.title">加载中...</div>
</div>

<div id="loginCon" class="loadingAllCon" ng-show="showLogin"
     style="background-color: #eee; position: absolute; z-index: 9999; top: 0; left: 0; right: 0; bottom: 0; text-align: center; padding-top: 100px;">
    <!-- img src="/member_page/img/logo.png" -->
    <form class="mainForm" ng-submit="doLogin();"
          style="width: 400px; height: 100px; margin: auto; padding: 50px 10px;">
        <div style="padding-bottom: 20px;font-size:18px;">{{site.name}}</div>
        <div class="row" ng-if="loginFormType=='reg'">
            <div class="field notHigh">姓 名:</div>
            <div class="input">
                <input ng-model="login.name" required="required"/>
            </div>
        </div>
        <div class="row">
            <div class="field notHigh">用户名:</div>
            <div class="input">
                <input ng-model="login.loginName" required="required"/>
            </div>
        </div>
        <div class="row">
            <div class="field notHigh">密 码:</div>
            <div class="input">
                <input type="password" ng-model="login.password" required="required"/>
            </div>
        </div>
        <div>
            <button type="submit" class="button highT">{{loginFormType=='reg'?'注册新用户':'登录'}}</button>
            <a href="javascript:void(0)"
               ng-click="changeLoginType('login')" ng-show="loginFormType=='reg'" style="color: blue;">返回登录</a>
        </div>
    </form>
</div>
<div class="layout" id="layout_all_main" ng-show="!$scope.showLogin">
    <div style="top:0;left:0;right:0;height:40px;overflow: hidden;border-bottom: 1px solid #ccc;">
        <div class="fl" class="index_top" style="font-size: 16px;padding:8px;">
            {{site.name}}
        </div>
        <div class="fr" style="padding: 6px; text-align: right;">
            <%--<span class="notHigh">商户: </span>--%>
            <%--<select ng-model="sellerId" ng-change="selectSeller(sellerId)"--%>
                    <%--ng-options="item._id as item.name for item in selerList"></select>--%>
            <%--<span class="notHigh">店铺: </span>--%>
            <%--<select ng-model="storeId" ng-change="selectStore(storeId)"--%>
                    <%--ng-options="item._id as item.name for item in storeList"></select>--%>
            <%--&lt;%&ndash;<span style="font-weight: bold;">{{my.$$name}}</span> <span class="notHigh">({{my.$$roles}})</span>&ndash;%&gt;--%>
            <button class="button high" ng-click="doLogout()">注销</button>
        </div>

    </div>
    <div class="left" style="width:220px;bottom:0;top:40px;left:0;" id="leftMenu">
        <div class="item" ng-repeat="item in menus" ng-show="!item.hide">
            <div class="title icon-play3" ng-click="clickLeftMenu(item);"
                 ng-class="menus.__selectMenuId==item._id&&menus.__openMenu?'open':''">{{item.name}}
            </div>
            <div class="items" ng-show="menus.__selectMenuId==item._id&&menus.__openMenu"
                 ng-repeat="citem in item.items ">
                <a href="javascript:void(0)"
                   ng-click="doMenuAction(citem,citem.url==null?(item._id+'/'+citem._id):citem.url)"
                   ng-class="citem.__selected?'selected':''">{{citem.name}}</a>
            </div>
        </div>
    </div>
    <div class="center" style="top:40px;left:220px;">
        <div ng-view style="width: 100%; height: 100%;">
        </div>
    </div>
</div>

<!-- pop window list -->
<%--<%@include file="./temp/i_popwinList.jsp"%>--%>
<div class="mark" ng-repeat="item in popWindows"
<%--ng-click="item.close();" --%>
     ng-style="{zIndex:$index+10}"></div>
<div pop-win index="{{$index}}"
     id="{{item.name}}"
     ng-style="{zIndex:$index+10,width:item.winWidth,height:winHeight}"
     hide-top="{{item.hideTop}}"
     hide-down="{{item.hideDown}}"
     is-pop="true"
     win-width="{{item.width}}"
     win-height="{{item.height}}"
     win-title="{{item.title}}"
     win-temp="{{item.temp}}"
     ng-repeat="item in popWindows"></div>

<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/angular${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/getBrowserInfo.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/angular-route${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/script${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/extend-1.0.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/config.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/init_websocket.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="./admin_app.js"></script>

<script type="text/javascript" src="./js/directive/layout.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/base.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/date.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/file.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/form.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/popWin.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/tree.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/fileBrowser.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/htmlEditor.js?_v=${serverVersion}"></script>
</body>
</html>