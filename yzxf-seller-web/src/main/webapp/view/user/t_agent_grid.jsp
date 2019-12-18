<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:15%">归属</div>
    <div style="width:15%">名称</div>
    <div style="width:10%">状态</div>
    <div style="width:10%">联系人</div>
    <div style="width:10%">联系方式</div>
    <div style="width:25%">地址</div>
    <div style="width:15%">创建时间</div>
</div>
<div ng-repeat="agent in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==agent._id?'selected':''"
    ng-click="dataPage.$$selectedItem=agent;">
    <div style="width:15%" ng-bind="agent.belongArea"></div>
    <div style="width:15%" ng-bind="agent.name"></div>
    <div style="width:10%" ng-bind="agent.canUse?'有效':'禁用'" ng-class="agent.canUse?'colorBlue1':'colorRed2'"></div>
    <div style="width:10%" ng-bind="agent.contactPerson"></div>
    <div style="width:10%" ng-bind="agent.phone"></div>
    <div style="width:25%" ng-bind="isNullText(agent.area)+isNullText(agent.address)"></div>
    <div style="width:15%" ng-bind="showYFullTime(agent.createTime)"></div>
</div>
