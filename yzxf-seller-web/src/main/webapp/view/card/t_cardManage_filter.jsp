<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">日期:</td>
        <td colspan="5">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">代理商等级:</td>
        <td>
            <select ng-model="filter._agentLevel" ng-init="filter._agentLevel=''">
                <option value="">所有</option>
                <option value="2">省级</option>
                <option value="3">市级</option>
                <option value="4">县级</option>
            </select>
        </td>
        <td class="headerTd">代理商名:</td>
        <td>
            <input type="text" ng-model="filter._agentName"/>
        </td>
        <td class="headerTd">卡号:</td>
        <td>
            <input type="text" ng-model="filter._cardNo"/>
        </td>
        <td class="headerTd" ng-show="entity=='cardLog'">类型:</td>
        <td ng-show="entity=='cardLog'">
            <select ng-model="filter._type" ng-init="filter._type=''">
                <option value="">分配卡</option>
                <option value="2">回收卡</option>
            </select>
        </td>
    </tr>
</table>