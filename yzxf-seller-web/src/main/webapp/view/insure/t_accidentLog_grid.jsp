<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:15%">归属地</div>
    <div style="width:15%">会员卡号</div>
    <div style="width:10%">会员姓名</div>
    <div style="width:15%">保险公司</div>
    <div style="width:15%">保险单号</div>
    <div style="width:10%">投保金额</div>
    <div style="width:5% ">投保年份</div>
    <div style="width:15%">创建时间</div>
</div>
<div class="trBk" ng-repeat="order in dataPage.items"
    ng-class="dataPage.$$selectedItem._id==order._id?'selected':''"
    ng-click="dataPage.$$selectedItem=order;">
    <div style="width:15%" ng-bind="order.area"></div>
    <div style="width:15%" ng-bind="order.cardNo"></div>
    <div style="width:10%" ng-bind="order.realName"></div>
    <div style="width:15%" ng-bind="order.company"></div>
    <div style="width:15%" ng-bind="order.insureNO"></div>
    <div style="width:10%;color: red;" ng-bind="getMoney(order.money)"></div>
    <div style="width:5% " ng-bind="order.year"></div>
    <div style="width:15%" ng-bind="showYFullTime(order.createTime)"></div>
</div>