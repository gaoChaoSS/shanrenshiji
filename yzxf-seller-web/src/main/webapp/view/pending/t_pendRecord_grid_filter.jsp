<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">审核日期:</td>
        <td colspan="5">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">被审核用户:</td>
        <td>
            <input type="text" ng-model="filter._owner"/>
        </td>
        <td class="headerTd">提交用户:</td>
        <td>
            <input type="text" ng-model="filter._create"/>
        </td>
        <td class="headerTd">审核用户:</td>
        <td>
            <input type="text" ng-model="filter._verifier"/>
        </td>
    </tr>
    <tr>
        <td class="headerTd">状态:</td>
        <td>
            <select ng-model="filter._status">
                <option value="">------请选择------</option>
                <%--<option value="0.2">初审通过</option>--%>
                <%--<option value="0.3">初审不通过</option>--%>
                <option value="2">复审通过</option>
                <option value="3">复审不通过</option>
            </select>
        </td>
        <td class="headerTd" ng-show="agent.level==1 || agent.level==4">用户类型:</td>
        <td ng-show="agent.level==1 || agent.level==4">
            <select ng-model="filter._ownerType">
                <option value="">------请选择------</option>
                <option value="seller">商户</option>
                <option value="agent" ng-show="agent.level!=4">代理商</option>
                <option value="factor">发卡点</option>
            </select>
        </td>
    </tr>
</table>

