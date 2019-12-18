<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:15%">平台利润</div>
    <div style="width:15%">省级利润{{filter._modifyType}}</div>
    <div style="width:15%">市级利润</div>
    <div style="width:15%">县级利润</div>
    <div style="width:20%">服务站利润</div>
    <div style="width:20%">修改时间</div>
</div>
<div ng-repeat="agent in dataPage.items" class="divBk">
    <div style="width:15%;color: #ba7c25" ng-bind="agent.oneAgent+' %'"></div>
    <div style="width:15%;color: #ba7c25" ng-bind="agent.twoAgent+' %'"></div>
    <div style="width:15%;color: #ba7c25" ng-bind="agent.threeAgent+' %'"></div>
    <div style="width:15%;color: #ba7c25" ng-bind="agent.fourAgent+' %'"></div>
    <div style="width:20%;color: #ba7c25" ng-bind="agent.fiveAgent+' %'"></div>
    <div style="width:20%" ng-bind="showYFullTime(agent.createTime)"></div>
</div>
