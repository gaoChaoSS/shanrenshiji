<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">创建日期:</td>
        <td colspan="3">
            <div ng-include="settlement_time"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">是否转账:</td>
        <td>
            <select ng-model="filter._isTransfer">
                <option value="">------请选择------</option>
                <option value="true">已转账</option>
                <option value="false">未转账</option>
            </select>
        </td>
        <td class="headerTd">用户:</td>
        <td>
            <input type="text" ng-model="filter._name"/>
        </td>
    </tr>
</table>