<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:20%">归属</div>
    <div style="width:20%">用户</div>
    <div style="width:10%">用户类型</div>
    <div style="width:15%">提现金额</div>
    <div style="width:15%">手续费</div>
    <div style="width:20%">申请时间</div>
</div>
<div ng-repeat="order in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==order._id?'selected':''"
    ng-click="dataPage.$$selectedItem=order;">
    <div style="width:20%" ng-bind="order.belongArea"></div>
    <div style="width:20%" ng-bind="order.userName"></div>
    <div style="width:10%" ng-bind="getUserType(order.userType)"></div>
    <div style="width:15%" ng-bind="order.withdrawMoney"></div>
    <div style="width:15%" ng-bind="order.fee"></div>
    <div style="width:20%" ng-bind="showYFullTime(order.createTime)"></div>
</div>