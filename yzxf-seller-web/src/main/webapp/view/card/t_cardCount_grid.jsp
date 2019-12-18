<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:35%">归属</div>
    <div style="width:30%">名称</div>
    <div style="width:15%">分配卡数量</div>
    <div style="width:15%">回收卡数量</div>
    <div style="width:15%">激活卡数量</div>
</div>
<div ng-repeat="card in dataPage.items">
    <div style="width:35%" ng-show="card.receiveFactorName==null || card.receiveFactorName==''" ng-bind="card.belongAreaAgent"></div>
    <div style="width:35%" ng-show="card.receiveFactorName!=null && card.receiveFactorName!=''" ng-bind="card.belongAreaFactor"></div>
    <div style="width:30%" ng-show="card.receiveFactorName==null || card.receiveFactorName==''" ng-bind="card.receiveAgentName"></div>
    <div style="width:30%" ng-show="card.receiveFactorName!=null && card.receiveFactorName!=''" ng-bind="card.receiveFactorName"></div>
    <div style="width:15%" ng-bind="card.grantNum"></div>
    <div style="width:15%" ng-bind="card.backNum"></div>
    <div style="width:15%" ng-bind="card.activeNum"></div>
</div>