<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:20%">创建日期</div>
    <div style="width:10%">支付方式</div>
    <div style="width:15%">订单编号</div>
    <div style="width:10%">订单金额</div>
    <div style="width:10%">支付金额</div>
    <div style="width:15%">支付人</div>
    <div style="width:10%">支付人类型</div>
    <div style="width:10% ">结果</div>
</div>
<div ng-repeat="pay in dataPage.items"  class="trBk"
    ng-class="dataPage.$$selectedItem.payId==pay.payId?'selected':''"
    ng-click="dataPage.$$selectedItem=pay;setPayId(pay)">
    <div style="width:20%" ng-bind="showYFullTime(pay.createTime)"></div>
    <div style="width:10%" ng-bind="getPayType(pay.payType)"></div>
    <div style="width:15%" ng-bind="pay.orderNo"></div>
    <div style="width:10%" ng-bind="getMoney(pay.totalFee)"></div>
    <div style="width:10%" ng-bind="pay.payStatus=='SUCCESS'?getMoney(pay.totalFee):0"></div>
    <div style="width:15%" ng-bind="getPayName(pay.orderType,pay.memberName,pay.sellerName,pay.factorName)"></div>
    <div style="width:10%" ng-bind="getPayNameType(pay.orderType)"></div>
    <div style="width:10%" ng-bind="payResults(pay.payStatus,pay.returnStatus)" ng-class="payResultsClass(pay.payStatus,pay.returnStatus)"></div>
</div>