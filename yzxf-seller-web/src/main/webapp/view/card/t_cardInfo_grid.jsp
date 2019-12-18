<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:15%">卡归属</div>
    <div style="width:20%">卡号</div>
    <div style="width:5% ">状态</div>
    <div style="width:10%">真实姓名</div>
    <div style="width:15%">会员手机</div>
    <div style="width:20%">所在城市</div>
    <div style="width:15%">创建日期</div>
</div>
<div ng-repeat="card in dataPage.items" class="trBk">
    <div style="width:15%" ng-bind="card.belongArea==null?'普惠生活-平台':card.belongArea"></div>
    <div style="width:20%;color:deepskyblue;" ng-bind="card.memberCardId"></div>
    <div style="width:5% " ng-bind="card.isActive==true?'已激活':'未激活'"></div>
    <div style="width:10%" ng-bind="card.realName"></div>
    <div style="width:15%;color: red" ng-bind="card.mobile"></div>
    <div style="width:20%" ng-bind="card.realArea"></div>
    <div style="width:15%" ng-bind="showYFullTime(card.createTime)"></div>
</div>