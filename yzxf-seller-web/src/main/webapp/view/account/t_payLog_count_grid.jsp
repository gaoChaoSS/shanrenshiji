<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <%--<div class="popTitle">支付金额统计</div>--%>
    <div style="width:100%">
        <span>时间:</span>
        <span>
            <input type="date" style="width:150px" ng-model="countFilter.startTime" ng-change="checkTime()"/>至
            <input type="date" style="width:150px" ng-model="countFilter.endTime" ng-change="checkTime()"/>
            <button class="btn1" ng-click="getCountPay()">查询</button>
        </span>
    </div>
</div>

<%--<div>--%>
    <%--<div ng-repeat="count in countItem" class="order1">--%>
        <%--<div ng-bind="count.title"></div>--%>
        <%--<div ng-repeat="li in count.list">--%>
            <%--<div ng-bind="li.name"></div>--%>
            <%--<div ng-bind="li.count"></div>--%>
            <%--<div ng-bind="li.totalPrice"></div>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>

<div class="popSection flex2" ng-repeat="count in countItem">
    <div class="popTitle" ng-bind="count.title+' 总入账：'+getMoney(count.totalPrice)+'元'+'，总笔数：'+isNullNum(count.count)"></div>
    <div class="sectionTable">
        <div>
            <div style="width:40%">订单类型</div>
            <div style="width:40%">数量</div>
            <div style="width:20%">平台入账总额</div>
        </div>
        <div ng-repeat="li in count.list">
            <div style="width:40%" ng-bind="li.name"></div>
            <div style="width:40%" ng-bind="li.count"></div>
            <div style="width:20%" ng-bind="getMoney(li.totalPrice)"></div>
        </div>
    </div>
</div>


