<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:15%">类型</div>
    <div style="width:15%">所属城市</div>
    <div style="width:20%">名称</div>
    <div style="width:10%">联系方式</div>
    <div style="width:40%">地址</div>
    <div style="width:15%">创建时间</div>
</div>
<div ng-repeat="branch in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==branch._id?'selected':''"
    ng-click="dataPage.$$selectedItem=branch;">
    <div style="width:15%" ng-bind="branch.pid==-1?'城市':'分支机构'"></div>
    <div style="width:15%" ng-bind="branch.belongCity"></div>
    <div style="width:20%" ng-bind="branch.name"></div>
    <div style="width:10%" ng-bind="branch.mobile"></div>
    <div style="width:40%" ng-bind="branch.address"></div>
    <div style="width:15%" ng-bind="showYFullTime(branch.createTime)"></div>
</div>
