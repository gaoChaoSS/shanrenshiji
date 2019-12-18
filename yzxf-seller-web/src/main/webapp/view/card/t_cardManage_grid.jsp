<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
    <%--<div class="btn1 bgOrange" style="width: 190px" ng-click="getCur()">查看当前登录用户拥有卡段</div>--%>
<div>
    <div style="width:20%">发卡方</div>
    <div style="width:20%">接收方</div>
    <div style="width:20%">起始号段</div>
    <div style="width:20%">结束号段</div>
    <div style="width:5%">卡号数量</div>
    <div style="width:15%">创建时间</div>
</div>
<div ng-repeat="agent in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==agent._id?'selected':''"
    ng-click="dataPage.$$selectedItem=agent;">
    <div style="width:20%" ng-bind="agent.grantName"></div>
    <div style="width:20%" ng-show="agent.receiveNameFactor==null || agent.receiveNameFactor==''" ng-bind="agent.receiveNameAgent"></div>
    <div style="width:20%" ng-show="agent.receiveNameFactor!=null && agent.receiveNameFactor!=''" ng-bind="agent.receiveNameFactor"></div>
    <div style="width:20%" ng-bind="agent.startCardNo"></div>
    <div style="width:20%" ng-bind="agent.endCardNo"></div>
    <div style="width:5%" ng-bind="agent.cardNum"></div>
    <div style="width:15%" ng-bind="showYFullTime(agent.createTime)"></div>
</div>