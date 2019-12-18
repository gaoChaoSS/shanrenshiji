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
        <td class="headerTd">手机号码:</td>
        <td>
            <input type="text" placeholder="手机号码..." ng-model="filter.mobile"/>
        </td>
        <td class="headerTd">用户名:</td>
        <td>
            <input type="text" placeholder="实名认证、用户名..." ng-model="filter.name"/>
        </td>
    </tr>
</table>

