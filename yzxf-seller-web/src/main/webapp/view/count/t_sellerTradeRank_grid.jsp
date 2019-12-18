<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:30%">归属</div>
    <div style="width:20%">商家编号</div>
    <div style="width:20%">名称</div>
    <div style="width:15%">交易笔数</div>
    <div style="width:15%">交易金额</div>
</div>
<div ng-repeat="sellerT in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem.userId==sellerT.userId?'selected':''"
    ng-click="dataPage.$$selectedItem=sellerT">
    <div style="width:30%" ng-bind="sellerT.belongArea==null?'普惠生活-平台':sellerT.belongArea"></div>
    <div style="width:20%" ng-bind="sellerT.userNo"></div>
    <div style="width:20%" ng-bind="sellerT.userName"></div>
    <div style="width:15%" ng-bind="sellerT.tradeNum"></div>
    <div style="width:15%;color:red" ng-bind="getMoney(sellerT.payMoneySum)"></div>
</div>