<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:15%">归属</div>
    <div style="width:10%">订单编号</div>
    <div style="width:10%">交易类型</div>
    <div style="width:10%">支付方式</div>
    <div style="width:10%">付款人</div>
    <div style="width:10%">收款人</div>
    <div style="width:10%">交易金额{{totalPricePage}}</div>
    <div style="width:10%">状态</div>
    <div style="width:15%">时间</div>
</div>
<div ng-repeat="order in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==order._id?'selected':''"
    ng-click="dataPage.$$selectedItem=order;setSelectData($index)">
    <div style="width:15%" ng-bind="getBelongArea(order.belongMember,order.belongSeller,order.belongFactor,order.cardNo,order.orderType)"></div>
    <div style="width:10%" ng-bind="order.orderNo"></div>
    <div style="width:10%" ng-bind="getTradeType(order.orderType)"></div>
    <div style="width:10%" ng-bind="getPayType(order.payType)"></div>
    <div style="width:10%" ng-bind="getNameByPay($index,order.nameMember,order.nameSeller,order.nameFactor,order.orderType)"></div>
    <div style="width:10%" ng-bind="getNameByAcq($index,order.nameMember,order.nameMemberAcq,order.nameSeller,order.nameFactor,order.orderType)"></div>
    <div style="width:10%;color: red;" ng-bind="getMoney(order.payMoney)"></div>
    <div style="width:10%" ng-bind="getOrderStatus(order.orderStatus)"></div>
    <div style="width:15%" ng-bind="showYFullTime(order.orderCreateTime)"></div>
</div>