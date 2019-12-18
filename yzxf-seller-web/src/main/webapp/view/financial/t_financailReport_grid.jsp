<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:5% " class="tdSelect1" ng-click="selectAll()" ng-bind="selectThName"></div>
    <div style="width:30%">归属</div>
    <div style="width:20%">代理商</div>
    <div style="width:20%">收益总额</div>
    <div style="width:10%">是否转账</div>
    <%--<td style="width:15%">是否到账</td>--%>
    <div style="width:20%">时间</div>

</div>
<div ng-repeat="agent in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==agent._id?'selected':''"
    ng-click="dataPage.$$selectedItem=agent">
    <div style="width:5%">
        <input type="checkbox" ng-model="submitList[$index].$$selectedItem"
                 ng-show="submitList[$index].isTransfer==null || !submitList[$index].isTransfer">
    </div>
    <div style="width:30%" ng-bind="agent.belongArea"></div>
    <div style="width:20%" ng-bind="agent.name"></div>
    <div style="width:20%" ng-bind="getMoney(agent.incomeAccount)"></div>
    <div style="width:10%" ng-bind="getIsTransfer(agent.isTransfer)"></div>
    <%--<td style="width:15%" ng-bind="agent.isSuccess?'到账':'未到账'"></td>--%>
    <div style="width:20%" ng-bind="getAccountMonth(agent.month)"></div>
</div>