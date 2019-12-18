<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="hideMenu" ng-show="showAgreePage" style="background:#fff;z-index:999;overflow:auto;">
    <div class="title titleRedBottom">
        <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBack()"></span>
        商家服务协议
    </div>
    <div class="article overflowPC" style="height:630px;text-align: justify;"
         ng-include="'/yzxfMember_page/view/other/agreeText_sellerApply.jsp'">
    </div>

    <div class="mod-bottom1" style="bottom:0">
        <div class="bottom1-btn" style="width:50%" ng-click="setScopeFlag('showAgreePage')">同意</div>
        <div class="bottom1-btn" style="width:50%;background: #aaa;" ng-click="goBack()">不同意</div>
    </div>
</div>