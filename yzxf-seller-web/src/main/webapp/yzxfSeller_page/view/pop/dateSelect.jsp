<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="pop_dateSelect_Ctrl">
    <div class="mark" onclick="window.history.back()"></div>
    <div class="dateCon" onclick="window.history.back()">
        <div class="dayList" ng-click="$event.stopPropagation();">
            <div style="text-align: center">
                <div class="fl cell" ng-click="changeDate('year',currentYear-1);">&lt;&lt;</div>
                <div class="fl cell" ng-click="changeDate('month',currentMonth-1);">&lt;</div>
                <div class="fl cell3">
                    <div class="fl citem" ng-click="changeShowType('year');"
                         ng-bind="currentYear+'年'"></div>
                    <div class="fl citem" style="width:50%;padding:6px 0;" ng-click="changeShowType('month');"
                         ng-bind="(currentMonth+1)+'月'"></div>
                </div>
                <div class="fl cell" ng-click="changeDate('month',currentMonth+1);">&gt;</div>
                <div class="fl cell" ng-click="changeDate('year',currentYear+1);">&gt;&gt;</div>
                <div class="clearDiv"></div>
            </div>
            <div style="background-color: #eee;" ng-show="showType=='year'">
                <div class="fl cell year" ng-repeat="item in years" ng-bind="item" ng-class="item==currentYear?'num':''"
                     ng-click="changeDate('year',item)"></div>
                <div class="clearDiv"></div>
            </div>
            <div style="background-color: #eee;" ng-show="showType=='month'">
                <div class="fl cell month" ng-repeat="item in monthTitleNames" ng-bind="item+'月'"
                     ng-class="item==currentMonth?'num':''"
                     ng-click="changeDate('month',item-1)"></div>
                <div class="clearDiv"></div>
            </div>
            <div class="fl item cell header" ng-repeat="item in weekTitleNames" ng-bind="item"></div>
            <div class="clearDiv"></div>
            <div class="fl item cell" ng-repeat="item in dateList" ng-class="item.className"
                 ng-click="selectDate(item);$event.stopPropagation();"
                 ng-bind="item.dayStr"></div>
            <div class="clearDiv"></div>
        </div>
    </div>
</div>