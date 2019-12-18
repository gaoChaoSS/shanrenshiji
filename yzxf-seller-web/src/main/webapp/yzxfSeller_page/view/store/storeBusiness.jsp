<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeBusiness_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/sellerInfo')"></span>
        营业时间
        <span class="titleManage black textSize15" ng-click="updateStoreTime()">保存</span>
    </div>

    <div class="mainRowTitle notHigh">调整您的营业时间</div>

    <div class="sectionMain3">
        <div class="mainRowTopBottom20">
            <span class="iconfont icon-icon19 textSize25" ng-click="timeBtn(-1,true)"></span>
            <span class="iconfont icon-icon19 mainRowLeftB25 textSize25" ng-click="timeBtn(-1,false)"></span>
        </div>
        <div class="mainRowTopBottom20 bgGrayccc">
            <span class="squareBtn2" ng-bind="openTime+':00'"></span>
            ——
            <span class="squareBtn2" ng-bind="closeTime+':00'"></span>
        </div>
        <div class="mainRowTopBottom20">
            <span class="iconfont icon-iconfontarrows textSize25" ng-click="timeBtn(1,true)"></span>
            <span class="iconfont icon-iconfontarrows mainRowLeftB25 textSize25" ng-click="timeBtn(1,false)"></span>
        </div>
    </div>

    <div class="sectionMain2">
        <div ng-repeat="week in weekList" ng-bind="'星期'+week.name" class="row2Btn"
             ng-class="week.check?'bgBlue2 grayeee':''" ng-click="week.check=!week.check"></div>
    </div>
</div>

