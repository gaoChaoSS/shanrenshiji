<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:25%">用户ID</div>
    <div style="width:20%" ng-bind="entityTitle=='待复审'?'用户':'联系人'"></div>
    <div style="width:15%">用户类型</div>
    <div style="width:15%">申请人</div>
    <div style="width:25%">创建时间</div>
</div>
<div ng-repeat="user in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==user._id?'selected':''"
    ng-click="dataPage.$$selectedItem=user;">
    <div style="width:25%" ng-bind="user.ownerId"></div>
    <div style="width:20%" ng-bind="user.owner"></div>
    <div style="width:15%" ng-bind="getOwnerType(user.ownerType)"></div>
    <div style="width:15%" ng-bind="user.create"></div>
    <div style="width:25%" ng-bind="showYFullTime(user.createTime)"></div>
</div>