<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div ng-class="entityTitle=='商家月收益结算'?'width15B':'width30B'">归属</div>
    <div style="width:15%" ng-bind="entity=='agentIncome'?'代理商':(entity=='factorIncome'?'服务站':'商家')"></div>
    <div style="width:15%" ng-show="entity=='sellerIncome'">交易总额{{totalOrderCash}}</div>
    <div style="width:15%">收益总额{{totalIncomeAccount}}</div>
    <div style="width:15%">消费笔数</div>
    <div style="width:10%">是否转账</div>
    <div style="width:15%">时间</div>

</div>
<div ng-repeat="agent in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==agent._id?'selected':''"
    ng-click="dataPage.$$selectedItem=agent">
    <div ng-bind="agent.belongArea" ng-class="entityTitle=='商家月收益结算'?'width15B':'width30B'"></div>
    <div style="width:15%" ng-bind="agent.name"></div>
    <div style="width:15%" ng-bind="getMoney(agent.orderCash)" ng-show="entityTitle=='商家月收益结算'"></div>
    <div style="width:15%" ng-bind="getMoney(agent.incomeAccount)"></div>
    <div style="width:15%" ng-bind="agent.orderCount"></div>
    <div style="width:10%" ng-bind="getIsTransfer(agent.isTransfer)"></div>
    <div style="width:15%" ng-bind="getAccountMonth(agent.month)"></div>
</div>