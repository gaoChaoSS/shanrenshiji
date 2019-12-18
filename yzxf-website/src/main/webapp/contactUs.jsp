<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/contactUs.css?_v=${_v}" rel="stylesheet">
    <script type="text/javascript"
            src="https://api.map.baidu.com/api?v=2.0&ak=BtjVtPxvPD1vpYUNxwsURnLalj8Ghy0p"></script>
    <%@include file="./inc/top.jsp" %>
</head>
<%@include file="./inc/header.jsp" %>
<body>
<div class="banner">

</div>

<div class="mod-center">
    <div class="mod-table">
        <div><span class="iconfont icon-mokuaishixin"></span>公司业务职能电话</div>
        <table>
            <tr>
                <td>行政管理中心: <span>028-69191567-1</span></td>
                <td>财务管理中心: <span>028-69191567-2</span></td>
                <td>事业发展中心: <span>028-69191567-3</span></td>
            </tr>
            <tr>
                <td>产品运营中心: <span>028-69191567-4</span></td>
                <td>品牌推广中心: <span>028-69191567-5</span></td>
                <td>平台技术中心: <span>028-69191567-6</span></td>
            </tr>
        </table>
    </div>
    <div class="mod-table">
        <div><span class="iconfont icon-mokuaishixin"></span>客服热线咨询热线</div>
        <table>
            <tr>
                <td>行政前台: <span>028-69191567-1</span></td>
                <td>商家入驻: <span>028-69191567-2</span></td>
                <td>加盟合作: <span>028-69191567-3</span></td>
            </tr>
            <tr>
                <td>产品采购: <span>028-69191567-4</span></td>
                <td>保险咨询: <span>028-69191567-5</span></td>
                <td>投诉建议: <span>028-69191567-6</span></td>
            </tr>
        </table>
    </div>
    <div class="mod-address">
        <div>
            <div><span class="iconfont icon-dianhua"></span>联系电话: 028-69191567</div>
            <div><span class="iconfont icon-gongsi"></span>公司名称: 四川国联普惠科技有限公司</div>
            <div><span class="iconfont icon-dizhi"></span>总部地址: 成都市金牛区金牛坝路6号</div>
            <div><span class="iconfont icon-dizhi"></span>成都运营中心地址: 成都市金牛区金牛坝路6号</div>
            <div><span class="iconfont icon-globe"></span>公司网站: www.phsh315.com</div>
        </div>
        <div id="container"></div>
    </div>
</div>

<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="./js/contactUs.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>