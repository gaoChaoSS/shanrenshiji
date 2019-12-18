<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">审核日期:</td>
        <td colspan="3">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">被审核用户:</td>
        <td>
            <input type="text" ng-model="filter._owner"/>
        </td>
        <td class="headerTd" ng-show="agent.level==1 || agent.level==4">用户类型:</td>
        <td ng-show="agent.level==1 || agent.level==4">
            <select ng-model="filter._ownerType">
                <option value="">------请选择------</option>
                <option value="Seller">商户</option>
                <option value="Agent" ng-show="agent.level!=4">代理商</option>
                <option value="Factor">服务站</option>
            </select>
        </td>
    </tr>
</table>

