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
    <link rel="stylesheet" href="/css/i_file.css?_v=${serverVersion}"/>
</head>
<body style="background-color: #eee;padding:30px;" ng-controller="allBodyCtrl" ng-mousedown="clickAllBody();">
<div myfile model="account"
     entity="OperateType"
     entity-field="img"
     entity-id="DE24A467-15F0-46FB-8DF0-321330771150"
     input-type="file"
     select-data-type=""
     is-edit="true"
     show-type="form"
     file-id="value"></div>


<script type="text/javascript" src="/js/lib/jquery-2.1.3.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/lib/angular.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/extend-1.0.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/binaryajax.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/canvasResize.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/exif.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="./testForm_app.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="/js/directive/base.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/layout.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="/js/directive/file.js?_v=${serverVersion}"></script>

</body>
</html>