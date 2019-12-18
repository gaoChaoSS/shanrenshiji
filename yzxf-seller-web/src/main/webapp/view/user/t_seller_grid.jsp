<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:15%">归属</div>
    <div style="width:10%">ID</div>
    <div style="width:15%">名称</div>
    <div style="width:10%">联系方式</div>
    <div style="width:10%">状态</div>
    <div style="width:15%">地址</div>
    <div style="width:10%">经营范围</div>
    <div style="width:15%">创建时间</div>
</div>
<div ng-repeat="seller in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==seller._id?'selected':''"
    ng-click="dataPage.$$selectedItem=seller;">
    <div style="width:15%" ng-bind="seller.belongArea"></div>
    <div style="width:10%" ng-bind="seller._id"></div>
    <div style="width:15%" ng-bind="seller.name=='null'?'':seller.name"></div>
    <div style="width:10%" ng-bind="seller.phone"></div>
    <div style="width:10%" ng-bind="seller.canUse?'有效':'禁用'" ng-class="seller.canUse?'colorBlue1':'colorRed2'"></div>
    <div style="width:15%" ng-bind="isNullText(seller.area)+isNullText(seller.address)"></div>
    <div style="width:10%" ng-bind="seller.operateType"></div>
    <div style="width:15%" ng-bind="showYFullTime(seller.createTime)"></div>
</div>
