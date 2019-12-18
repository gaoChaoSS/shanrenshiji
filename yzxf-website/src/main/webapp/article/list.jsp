<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="/css/news.css?_v=${_v}" rel="stylesheet">
    <%@include file="../inc/top.jsp" %>
</head>
<body>
<%@include file="../inc/header.jsp" %>
<div class="banner">

</div>

<div class="mod-scroll" id="flexSlider">
    <%--<c:forEach items="${dirPage.items}" var="item">--%>
    <%--<div id="${item._id}" class="${item._id==selectDirId?'newsNavSelect':''}"--%>
    <%--onclick="window.location.href='/action/article/list.html?allPid=1F06AB19-B2CA-4364-88ED-AD43DB008ACA&dirId=${item._id}'">${item.name}</div>--%>
    <%--</c:forEach>--%>
    <div class="rollPicture">
        <%--<img src="/img/rollPicture4.png">--%>
        <%--<img src="/img/rollPicture2.jpg">--%>
        <%--<img src="/img/rollPicture3.jpg">--%>
    </div>
    <div class="scroll-title"></div>
    <div class="rollNumber">
        <%--<div class="currentRollNum">1</div>--%>
        <%--<div>2</div>--%>
        <%--<div>3</div>--%>
    </div>
</div>

<div class="conDiv">
    <div class="con" style="min-height: 700px;text-align: left;">
        <div class="newsCon">
            <div class="line"></div>
            <div id="articles">
                <c:if test="${page.totalNum==0}">
                    <div style="text-align: center;padding:10px 0;"><img src="/img/blankCon.png"/></div>
                </c:if>
            </div>
            <c:if test="${page.totalPage>1}">
                <div class="pagesDiv">
                    <div class="pages" style="margin-bottom: 90px;margin-top: 40px;"></div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<div class="index footerCon">
    <%@include file="../inc/footer.jsp" %>
</div>
<script src="/js/news.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>