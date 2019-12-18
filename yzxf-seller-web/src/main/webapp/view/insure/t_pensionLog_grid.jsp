<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:5% " class="tdSelect1" ng-click="selectAll()" ng-bind="selectThName"></div>
    <div style="width:20%">归属地</div>
    <%--<div style="width:15%">保险单号</div>--%>
    <div style="width:15%">会员卡号</div>
    <div style="width:15%">会员姓名</div>
    <%--<td style="width:10%">保险公司</td>--%>
    <div style="width:15%">未投保金额</div>
    <div style="width:15%">投保金额</div>
    <div style="width:15%">投保状态</div>
    <%--<td style="width:7%">投保方式</td>--%>
    <%--<div style="width:10%">时间</div>--%>
</div>
<div class="trBk" ng-repeat="order in dataPage.items"
    ng-class="dataPage.$$selectedItem._id==order._id?'selected':''"
    ng-click="dataPage.$$selectedItem=order;">
    <div style="width:5%">
        <input type="checkbox" ng-model="order.$$selectedSubmit"
               ng-show="!order.insureStatus" style="zoom: 150%;">
    </div>
    <div style="width:20%" ng-bind="order.belongArea"></div>
    <%--<div style="width:15%" ng-bind="order.insureNO"></div>--%>
    <div style="width:15%" ng-bind="order.cardNo"></div>
    <div style="width:15%" ng-bind="order.realName"></div>
    <%--<td style="width:10%" ng-bind="order.company">保险公司</td>--%>
    <div style="width:15%" ng-bind="getMoney(order.insureCount)"></div>
    <div style="width:15%" ng-bind="getMoney(order.money)"></div>
    <div style="width:15%" ng-bind="getInsureStatus(order.insureStatus)" ng-class="order.insureStatus==1?'colorRed1':'colorGray888'">投保状态</div>
    <%--<td style="width:7%" ng-bind="insureType(order.insureType)">投保方式</td>--%>
    <%--<div style="width:10%" ng-bind="showYFullTime(order.createTime)"></div>--%>
</div>