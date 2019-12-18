<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/branches.css?_v=${_v}" rel="stylesheet">
    <%@include file="./inc/top.jsp" %>
</head>
<%@include file="./inc/header.jsp" %>
<body>
<div class="banner">

</div>

<div>
    <div id="navTop">
        <div id="cityList" class="hotCity" style="overflow: hidden">
            <div style="float:left;line-height: 30px;font-size: 18px;">开通城市:</div>
        </div>
    </div>
    <%--用来做占位,防止标签跳转误差--%>
    <div class="hotCity" id="cloneCity">

    </div>
</div>


<div class="conDiv">
    <div id="branchCompany" class="con" style="padding-bottom: 300px;">
    </div>
</div>

<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="./js/branches.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>