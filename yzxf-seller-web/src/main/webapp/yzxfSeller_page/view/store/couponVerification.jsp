<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--卡券核销--%>
<div ng-controller="store_couponVerification_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        卡券核销
    </div>

    <div class="commentList">
        <div class="paddingTopBottom50">
            <div class="iconfont icon-duigou textSize85 limeGreen textCenter"></div>
            <div class="lineHeight40 textCenter">已使用</div>
            <div class="lineHeight40 textCenter gray666" ng-bind="value.name"></div>
            <div class="lineHeight40 textCenter gray666">
                <span class="mainRowRight20 gray666">有效期至</span><span ng-bind="showDate(value.endTime)"></span>
            </div>
        </div>
    </div>
</div>