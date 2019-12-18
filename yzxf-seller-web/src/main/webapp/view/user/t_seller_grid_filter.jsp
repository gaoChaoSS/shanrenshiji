<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">创建日期:</td>
        <td colspan="3">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">关键字:</td>
        <td>
            <input type="text" placeholder="用户,归属..." ng-model="filter.keywordValue"/>
        </td>
        <td class="headerTd">经营范围:</td>
        <td>
            <input type="text" ng-model="filter._operateType"/>
        </td>
    </tr>
    <tr>
        <td class="headerTd">状态:</td>
        <td>
            <select ng-model="filter._canUse">
                <option value="">------请选择------</option>
                <option value="true">有效</option>
                <option value="false">禁用</option>
            </select>
        </td>
        <td class="headerTd">商家手机:</td>
        <td>
            <input type="text" ng-model="filter._phone"/>
        </td>
    </tr>
</table>

