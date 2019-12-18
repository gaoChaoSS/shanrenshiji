<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/newsDetails.css?_v=${_v}" rel="stylesheet">
    <%@include file="./inc/top.jsp" %>
</head>
<%@include file="./inc/header.jsp" %>
<body>

<div class="conDiv">
    <div class="con where">
        <a href="index.jsp">新闻动态</a> &gt; <a href="news.jsp">公司新闻</a> &gt; <span style="color: #3E96C4">详细内容</span>
    </div>
</div>

<div class="conDiv">
    <div class="con">
        <div class="detailCon">

        </div>
    </div>
</div>


<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="/js/newsDetails.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>
