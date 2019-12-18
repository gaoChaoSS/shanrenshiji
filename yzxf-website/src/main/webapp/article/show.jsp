<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="/css/newsDetails.css?_v=${_v}" rel="stylesheet">
    <%@include file="/inc/top.jsp" %>
    <title>${article.title}</title>
</head>
<%@include file="/inc/header.jsp" %>

<body>

<div class="mod-center" style="margin-bottom: 100px;">
    <div class="detailCon">
    </div>
</div>


<div class="index footerCon">
    <%@include file="/inc/footer.jsp" %>
</div>
<script src="/js/newsDetails.js?_v=${_v}" type="text/javascript"></script>
<script>
    $(function(){
        var newTime = new Date(parseInt($(".createTime").html())).showDateTime();
        $(".createTime").html(newTime);

    });
</script>
</body>
</html>