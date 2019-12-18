<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:15%">归属</div>
    <div style="width:15%">会员卡号</div>
    <div style="width:10%">名称</div>
    <div style="width:10%">交易笔数</div>
    <div style="width:10%">线上交易金额</div>
    <div style="width:10%">线下交易金额</div>
    <div style="width:10%">充值总金额</div>
    <div style="width:10%">目前余额</div>
    <div style="width:10%">交易总金额</div>
</div>
<div ng-repeat="member in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem.userId==member.userId?'selected':''"
    ng-click="dataPage.$$selectedItem=member">
    <div style="width:15%" ng-bind="member.belongArea==null?'普惠生活-平台':member.belongArea"></div>
    <div style="width:15%" ng-bind="member.userNo"></div>
    <div style="width:10%" ng-bind="member.userName"></div>
    <div style="width:10%" ng-bind="member.tradeNum"></div>
    <div style="width:10%" ng-bind="getMoney(member.cashOnlineCount)"></div>
    <div style="width:10%" ng-bind="getMoney(member.cashOfflineCount)"></div>
    <div style="width:10%" ng-bind="getMoney(member.rechargeCount)"></div>
    <div style="width:10%" ng-bind="getMoney(member.cashCount)"></div>
    <div style="width:10%" ng-bind="getMoney(member.totalConsume)"></div>
</div>