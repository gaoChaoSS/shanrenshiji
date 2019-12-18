<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<span>归属:</span>

<span ng-repeat="pitem in agent.$$parent">
    <span style="color:#116c9b" ng-bind="pitem.name"></span>
    <span class="notHigh">&gt;</span>
</span>

<span>
    <span style="color:#116c9b" ng-bind="agent.name"></span>
    <span class="notHigh">&gt;</span>
</span>

<select class="h40px minW100px" ng-model="agentSelectValue[$index]" ng-repeat="item in agentChildList"
        style="margin-right: 10px;"
        ng-options="aitem.areaValue as aitem.name for aitem in item"
        ng-change="getAgentChildren(agentSelectValue[$index],$index+1)">
</select>

<%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[1]"--%>
<%--ng-options="area1.name for area1 in areaList[1]"--%>
<%--ng-change="getLocation(selectArea[1].areaValue,3)">--%>
<%--</select>--%>

<%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[1]"--%>
<%--ng-options="area1.name for area1 in areaList[1]"--%>
<%--ng-change="getLocation(selectArea[1].areaValue,3)">--%>
<%--</select>--%>
<%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[2]"--%>
<%--ng-options="area1.name for area1 in areaList[2]"--%>
<%--ng-change="getLocation(selectArea[2].areaValue,4)">--%>
<%--</select>--%>
<%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[3]"--%>
<%--ng-show="selectArea[2].areaValue!=''"--%>
<%--ng-options="area1.name for area1 in areaList[3]"--%>
<%--ng-change="getLocation(selectArea[3].areaValue,5)">--%>
<%--</select>--%>