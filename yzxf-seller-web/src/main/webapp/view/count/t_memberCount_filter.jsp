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
        <td class="headerTd">归属:</td>
        <td>
            <input type="text" ng-model="filter._belongArea"/>
        </td>
        <td class="headerTd">会员数量:</td>
        <td>
            <input type="text" ng-model="filter._num"/>
            <select ng-model="filter._numUpDn">
                <option value="">以上</option>
                <option value="down">以下</option>
            </select>
            <select ng-model="filter._numOrder">
                <option value="">降序</option>
                <option value="asc">升序</option>
            </select>
        </td>
    </tr>
</table>