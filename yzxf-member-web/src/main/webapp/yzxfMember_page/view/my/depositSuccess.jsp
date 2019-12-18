<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_depositSuccess_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/my')"></span>
        支付结果
    </div>
    <div class="sectionMain" style="padding:50px 0;position: absolute;height: calc(100% - 56px);width: 100%;">
        <div class="iconfont iconImg8" ng-class="statusIcon"></div>
        <div class="btn4" ng-bind="statusText"></div>
    </div>
    <div class="btn3 textSize16" ng-show="isSuccess==1">
        <span ng-bind="countTimeNum +'秒后'"  ng-show="countTimeNum!=null && countTimeNum!='' && countTimeNum!=0"></span>
        <span ng-click="goMyWallet()" class="dodgerBlue">返回 我的主页</span>
    </div>
</div>