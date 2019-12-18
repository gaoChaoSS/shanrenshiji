<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">日期:</td>
        <td colspan="3">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">代理商等级:</td>
        <td>
            <select ng-model="filter._agentLevel" ng-init="filter._agentLevel=''" style="border: none;background-color: #fff;">
                <option value="">所有</option>
                <option value="One">省级</option>
                <option value="Two">市级</option>
                <option value="Three">县级</option>
            </select>
        </td>
        <td class="headerTd">代理商名:</td>
        <td colspan="3">
            <input type="text" ng-model="filter._agentName"/>
        </td>
    </tr>
</table>