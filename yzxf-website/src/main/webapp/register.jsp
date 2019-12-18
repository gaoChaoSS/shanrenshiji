<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/join&register.css?_v=${_v}" rel="stylesheet">
    <link href="css/index.css?_v=${_v}" rel="stylesheet">
    <%@include file="./inc/top.jsp" %>
</head>
<%@include file="./inc/header.jsp" %>
<body>

<div class="conDiv">
    <div class="con where">
        <a class="colorRed1" href="index.jsp">首页</a> &gt; <span>消费者加入</span>
        <div style="text-align: center;padding:50px;">
            <img style="" src="img/member.png">
        </div>
    </div>
</div>

<div id="edit" class="conDiv" style="min-height: 450px">
    <div class="con joinCon" style="border-radius: 8px;margin-bottom: 30px;padding:25px 0;">
        <%--<p class="title">消费者注册</p>--%>
        <div>
            <span>手机号:</span>
            <div>
                <input id="phone" type="number">
                <button class="getCode" id="getPhoneCheck">点击获取验证码</button>
                <div class="prompt"><img src="img/error.png">填写不正确</div>
            </div>
        </div>
        <div>
            <span>验证码:</span>
            <div>
                <input id="code" type="text">
                <div class="prompt"><img src="img/error.png">填写不正确</div>
            </div>
        </div>
        <div>
            <span>密码:</span>
            <div>
                <input id="password" type="password">
                <div class="prompt"><img src="img/error.png">填写不正确,请填写6-20位的密码</div>
            </div>
        </div>
        <div>
            <span>确认密码:</span>
            <div>
                <input id="repeatPsw" type="password">
                <div class="prompt"><img src="img/error.png">填写与密码不一致</div>
            </div>
        </div>

        <button onclick="submit()" style="margin-top: 35px;">注册</button>
    </div>
</div>

<div id="success" class="conDiv" style="min-height: 410px;display: none">
    <div class="con joinCon">
        <img src="img/success.png">
        <p class="title" style="margin-top: 40px;">注册成功!</p>
        <%--<p class="message" style="width: 17%;margin: 0 auto;line-height: 33px;">--%>
        <%--<span id="time">5</span>秒后自动跳转到首页…--%>
        <%--</p>--%>
        <div class="downLoad">
            <img class="downQR" src="img/erWeiMa.png"><br><br>
            <img class="downWay" src="img/erWeiMa.jpg">
        </div>
        <div class="downLoad">
            <img class="downQR" src="img/erWeiMa.png"><br><br>
            <img class="downWay" src="img/erWeiMa.jpg">
        </div>
        <div class="downLoad">
            <img class="downQR" src="img/erWeiMa.png"><br><br>
            <img class="downWay" src="img/erWeiMa.jpg">
        </div>
    </div>
</div>

<div id="error" class="conDiv" style="min-height: 410px;display: none">
    <div class="con joinCon">
        <img src="img/error.png">
        <p class="title" style="margin-top: 40px">注册失败!</p>
        <button class="errorButton" onclick="reSubmit()">重新注册</button>
    </div>
</div>

<div style="background:#F2F2F2;overflow:hidden;margin-top:100px;">
    <div class="conTitle pagesDiv" style="margin-top:50px;">合作机构</div>
    <div class="conTitle2 pagesDiv">INTRODUCE</div>
    <div class="mod-guild5 flex1">
        <img src="./img/together_1.png" >
        <img src="./img/together_2.png" >
    </div>
</div>
<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="./js/register.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>