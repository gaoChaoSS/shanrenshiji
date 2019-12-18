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
            <input type="text" placeholder="编号,姓名,手机..." ng-model="filter._search"/>
        </td>
    </tr>
</table>

