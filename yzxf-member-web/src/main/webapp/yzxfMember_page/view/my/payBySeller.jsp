<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_payBySeller_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        付款
        <span class="titleManage notHigh" ng-show="isPay" ng-click="delOrder()">取消订单</span>
    </div>
    <div class="circleProgress_wrapper" ng-show="!isPay">
        <div class="circleProgress_text">等待商家发起订单</div>
        <div class="wrapper right">
            <div class="circleProgress rightcircle"></div>
        </div>
        <div class="wrapper left">
            <div class="circleProgress leftcircle"></div>
        </div>
    </div>

    <div class="sectionMain" ng-show="isPay">
        <div class="mainRow">
            <div class="rowTitle">订单信息:</div>
            <div class="rowInput textRight" ng-bind="orderInfo.sellerName+'-线下付款'"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">会员账号:</div>
            <div class="rowInput textRight" ng-bind="orderInfo.mobile"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">付款方式:</div>
            <div class="rowInput textRight">余额</div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">需付款:</div>
            <div class="rowInput textRight colorRed2" ng-bind="orderInfo.totalPrice+'元'"></div>
        </div>
    </div>
    <button class="submitBtn" ng-show="isPay" ng-click="getPwdWin()">确认付款</button>

    <div class="hideMenu" ng-show="menuCheck">
        <div class="enterPsw">
            <div class="textCenter lineHeight50" ng-bind="'付款给 '+orderInfo.sellerName"></div>
            <div class="textCenter textSize30 lineHeight40" ng-bind="'¥ '+orderInfo.totalPrice"></div>
            <div class="enterInput">
                <input maxlength="1" type="tel" ng-model="pwd1"/>
                <input maxlength="1" type="tel" ng-model="pwd2"/>
                <input maxlength="1" type="tel" ng-model="pwd3"/>
                <input maxlength="1" type="tel" ng-model="pwd4"/>
                <input maxlength="1" type="tel" ng-model="pwd5"/>
                <input maxlength="1" type="tel" ng-model="pwd6"/>
            </div>
            <div class="enterInputDes">请输入6位支付密码</div>
            <div class="selectDiv">
                <div style="border-left: 0;border-bottom: 0" ng-click="menuCheck=false">取消</div>
                <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="submitOrder()">确认</div>
            </div>
        </div>
    </div>
</div>