<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:20%">用户</div>
    <div style="width:10%">用户类型</div>
    <div style="width:15%">审批阶段</div>
    <div style="width:20%">提交人</div>
    <div style="width:20%">审核人</div>
    <%--<td style="width:20%">审批说明</td>--%>
    <div style="width:15%">审核时间</div>
</div>
<div ng-repeat="user in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==user._id?'selected':''"
    ng-click="dataPage.$$selectedItem=user;">
    <div style="width:20%" ng-bind="user.owner"></div>
    <div style="width:10%" ng-bind="getOwnerType(user.ownerType)"></div>
    <div style="width:15%" ng-bind="getStatus(user.status)" ng-class="getStatusClass(user.status)"></div>
    <div style="width:20%" ng-bind="user.create"></div>
    <div style="width:20%" ng-bind="getStatusData(user.status,user.verifierFirst,user.verifier)"></div>
    <!--<td style="width:20%" ng-bind="getExplain(user.explain)"></td>-->
    <div style="width:15%" ng-bind="getStatusData(user.status,showYFullTime(user.verifierFirstTime),showYFullTime(user.verifierTime))"></div>
</div>