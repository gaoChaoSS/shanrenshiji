<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:5% " class="tdSelect1" ng-click="selectAll()" ng-bind="selectThName"></div>
    <div style="width:20%">用户ID</div>
    <div style="width:20%">用户</div>
    <div style="width:15%">用户类型</div>
    <div style="width:15%">提交人</div>
    <div style="width:25%">创建时间</div>
</div>
<div ng-repeat="user in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==user._id?'selected':''"
    ng-click="dataPage.$$selectedItem=user">
    <div style="width:5% " ><input type="checkbox" ng-model="delList[$index].$$selectedItem"></div>
    <div style="width:20%" ng-bind="user.ownerId"></div>
    <div style="width:20%" ng-bind="user.owner"></div>
    <div style="width:15%" ng-bind="getOwnerType(user.ownerType)"></div>
    <div style="width:15%" ng-bind="user.create"></div>
    <div style="width:25%" ng-bind="showYFullTime(user.createTime)"></div>
</div>