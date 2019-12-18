<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/contactUs.css?_v=${_v}" rel="stylesheet">
    <%@include file="./inc/top.jsp" %>
    <script src="/js/lib/qrcode.js?_v=${_v}"></script>
</head>
<%@include file="./inc/header.jsp" %>
<body>

<div class="mod-center">
    <div class="mod-table" style="overflow: hidden;">
        <div><span class="iconfont icon-mokuaishixin"></span>安卓版</div>
        <div class="qrcode">
            <div>
                <div class="qrcodeImg0"></div>
                <a href="https://www.phsh315.com/phsh_files/android_member_1.43.apk" class="qrcodeText0">会员版</a>
            </div>
            <div>
                <div class="qrcodeImg1"></div>
                <a href="https://www.phsh315.com/phsh_files/android_seller_1.44.apk" class="qrcodeText1">商家版</a>
            </div>
        </div>
    </div>
    <div class="mod-table" style="overflow: hidden;">
        <div><span class="iconfont icon-mokuaishixin"></span>苹果版</div>
        <div class="qrcode">
            <%--<div>--%>
                <%--<div class="qrcodeImg"></div>--%>
                <%--<div>会员版</div>--%>
            <%--</div>--%>
            <div>
                <div class="qrcodeImg2"></div>
                <a href="https://itunes.apple.com/cn/app/id1315919462?mt=8" class="qrcodeText2">商家版</a>
            </div>
        </div>
    </div>
</div>

<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="./js/appDownload.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>