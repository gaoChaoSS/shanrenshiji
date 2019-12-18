<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <%--<tr>--%>
    <%--<td class="headerTd">创建日期:</td>--%>
    <%--<td colspan="3">--%>
    <%--<div ng-include="timeFilter"></div>--%>
    <%--</td>--%>
    <%--</tr>--%>
    <tr>
        <td class="headerTd">商户名:</td>
        <td>
            <input type="text" ng-model="filter._sellerName"/>
        </td>
        <td class="headerTd">商户ID:</td>
        <td>
            <input type="text" ng-model="filter._id"/>
        </td>
    </tr>
    <tr>
        <td class="headerTd">经营类别:</td>
        <td>
            <input type="text" ng-model="filter._operateType"/>
        </td>
        <td class="headerTd">商户推荐类型:</td>
        <td>
            <select ng-model="filter._recommendType" style="border: none;">
                <option value="">------请选择------</option>
                <option value="recommend">首页推荐</option>
                <option value="foodCourt">美食广场</option>
            </select>
        </td>
    </tr>
</table>

