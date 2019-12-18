<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:15%">创建日期</div>
    <div style="width:20%">代理商</div>
    <div style="width:15%">等级</div>
    <div style="width:25%">街道地址</div>
    <div style="width:10%">剩余卡量</div>
    <div style="width:15%">联系方式</div>
</div>
<div ng-repeat="agent in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==agent._id?'selected':''"
    ng-click="dataPage.$$selectedItem=agent;">
    <div style="width:15%" ng-bind="showYFullTime(agent.createTime)"></div>
    <div style="width:20%" ng-bind="agent.name"></div>
    <div style="width:15%;color:deepskyblue;" ng-bind="agentLevelNum(agent.level)"></div>
    <div style="width:25%" ng-bind="agent.area+agent.address"></div>
    <div style="width:10%;color: red" ng-bind="agent.cardNum"></div>
    <div style="width:15%" ng-bind="agent.level=='5'?agent.mobile:agent.phone"></div>
</div>