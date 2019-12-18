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
<div ng-repeat="factor in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==factor._id?'selected':''"
    ng-click="dataPage.$$selectedItem=factor;">
    <div style="width:15%" ng-bind="factor.belongArea"></div>
    <div style="width:15%" ng-bind="factor.name"></div>
    <div style="width:10%" ng-bind="factor.canUse?'有效':'禁用'" ng-class="factor.canUse?'colorBlue1':'colorRed2'"></div>
    <div style="width:10%" ng-bind="factor.contactPerson"></div>
    <div style="width:10%" ng-bind="factor.mobile"></div>
    <div style="width:25%" ng-bind="isNullText(factor.area)+isNullText(factor.address)"></div>
    <div style="width:15%" ng-bind="showYFullTime(factor.createTime)"></div>
</div>
