<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="account_login_Ctrl" class="form_section d_content title_section">
    <form class="loginBk" ng-submit="submitForm()">
        <div>
            <img src="/yzxfSeller_page/img/loginLogo.png" class="loginLogo">
            <div class="loginRow loginRowFirst">
                <span class="icon-yonghu iconfont loginIcon"></span>
                <input class="loginInput" type="text" placeholder="请输入您的账号" ng-model="sellerName" ng-change="submitBtn()"/>
            </div>
            <div class="loginRow">
                <span class="icon-mima iconfont loginIcon"></span>
                <input class="loginInput" type="password" placeholder="请输入您的密码" ng-model="password" ng-change="submitBtn()"/>
            </div>
            <button type="submit" class="loginRow loginSubmitBtn">登 录</button>
        </div>
        <div>&copy;Copyright 2017 普惠生活版权所属</div>
    </form>
</div>