<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_paySetting_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/wallet')"></span>
        支付设置
    </div>
    <div class="sectionMain">
        <div class="mainRow" ng-click="goPage('/my/payModifyPassword')">
            <div class="rowTitle notHigh">支付密码修改</div>
            <span class="icon-right-1-copy iconfont mainRowRight"></span>
        </div>
        <div class="mainRow" ng-click="goPage('/my/payForgotPassword')">
            <div class="rowTitle notHigh">支付密码找回</div>
            <span class="icon-right-1-copy iconfont mainRowRight"></span>
        </div>
    </div>
</div>