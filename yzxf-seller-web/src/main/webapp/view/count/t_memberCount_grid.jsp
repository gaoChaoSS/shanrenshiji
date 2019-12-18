<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:50%">详细归属</div>
    <div style="width:30%">服务中心名称</div>
    <div style="width:20%">会员总数</div>
</div>
<div ng-repeat="newAdd in dataPage.items" ng-class="newAdd.agentId=='A-000001'?'bkColorYellow2':''">
    <div style="width:50%;font-weight: bold;text-align:left" ng-bind="newAdd.agentNameAll"></div>
    <div style="width:30%" ng-bind="getFactorName(newAdd.name)"></div>
    <div style="width:20%" class="num" ng-bind="newAdd.count"></div>
</div>