<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="popSection flex2">
    <div style="width:100%">
        <span>时间</span>
        <span>
            <span>
                <select ng-model="selectTime[0]" ng-options="year1.date for year1 in startYear"
                        ng-change="getTimeYear(0)">
                </select>
                <select ng-model="selectTime[1]" ng-options="month1.date for month1 in startMonth"
                        ng-show="selectedShow[0]">
                </select>
            </span>

            <button class="btn1 bgBlue" type="submit" ng-click="getMyIncomePage()">查询</button>
        </span>
    </div>
</div>
<div ng-include="'/view/account/t_agentIncomeInfo_grid.jsp'"></div>