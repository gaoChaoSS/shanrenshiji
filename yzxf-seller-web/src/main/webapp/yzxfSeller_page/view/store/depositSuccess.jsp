<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_depositSuccess_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goMyWallet()"></span>
        支付结果
    </div>
    <div class="sectionMain" style="padding:50px 0;position: absolute;height: calc(100% - 56px);width: 100%;">
        <div class="iconfont iconImgText1" ng-class="statusIcon"></div>
        <div class="btnText2" ng-bind="statusText"></div>
        <div class="sectionMain" ng-show="orderInfo.orderNo!=null && orderInfo.orderNo!=''">
            <div class="mainRow lineHeight50">
                <div class="rowTitle rowTitleBtn black">订单编号</div>
                <div class="rowInput mainRowLeft135" ng-bind="orderInfo.orderNo"></div>
            </div>
            <div class="mainRow lineHeight50" ng-show="orderInfo.totalPrice!=null && orderInfo.totalPrice!=''">
                <div class="rowTitle rowTitleBtn black">订单总价</div>
                <div class="rowInput mainRowLeft135" ng-bind="orderInfo.totalPrice"></div>
            </div>
            <div class="mainRow lineHeight50" ng-show="orderInfo.memberName!=null && orderInfo.memberName!=''">
                <div class="rowTitle rowTitleBtn black">会员姓名</div>
                <div class="rowInput mainRowLeft135" ng-bind="orderInfo.memberName"></div>
            </div>
            <div class="mainRow lineHeight50" ng-show="orderInfo.createTime!=null && orderInfo.createTime!=''">
                <div class="rowTitle rowTitleBtn black">创建时间</div>
                <div class="rowInput mainRowLeft135" ng-bind="showYFullTime(orderInfo.createTime)"></div>
            </div>
        </div>

        <div class="btnText1 textSize16" ng-show="isSuccess==1">
            <span ng-bind="countTimeNum+'秒后'" ng-show="countTimeNum!=null && countTimeNum!='' && countTimeNum!=0"></span>
            <span ng-click="goMyWallet()" class="dodgerBlue">返回首页</span>
        </div>
    </div>


</div>