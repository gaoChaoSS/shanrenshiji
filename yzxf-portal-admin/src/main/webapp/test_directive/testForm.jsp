<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="phonecat">
<head>
    <%
        pageContext.setAttribute("serverVersion", "0.15");
    %>
    <script>
        window.angular_temp_version = ${serverVersion};
    </script>
    <link rel="stylesheet" href="/css/i_common.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/icomoon/style.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/iconfont/iconfont.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_layout.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_popWin.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_date.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_file.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_form.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="/css/i_grid.css?_v=${serverVersion}"/>
</head>
<body style="background-color: #eee;padding:30px;" ng-controller="allBodyCtrl" ng-mousedown="clickAllBody();">
<div myform
     model="common"
     entity="TestForm"
     row-value="addfdf111"
     hide-top="false"
     hide-down="false"
     is-edit-str="true"
     exclude-keys="_id"
     style="width:500px;height: 600px;"></div>


<div class="mark" ng-repeat="item in popWindows"
<%--ng-click="item.close();" --%>
     ng-style="{zIndex:$index+10}"></div>
<div pop-win index="{{$index}}" id="{{item.name}}" ng-style="{zIndex:$index+10,width:item.winWidth,height:winHeight}"
     hide-top="{{item.hideTop}}"
     hide-down="{{item.hideDown}}"
     is-pop="true"
     win-width="{{item.width}}"
     win-height="{{item.height}}"
     win-title="{{item.title}}"
     win-temp="{{item.temp}}"
     ng-repeat="item in popWindows"></div>

<script type="text/javascript" src="/js/lib/jquery-2.1.3.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/lib/angular.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/extend-1.0.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/binaryajax.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/canvasResize.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/exif.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="./testForm_app.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="/js/directive/base.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/layout.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/popWin.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/date.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/file.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/form.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/grid.js?_v=${serverVersion}"></script>

</body>
</html>