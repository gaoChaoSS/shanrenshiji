<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>--%>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">创建日期:</td>
        <td colspan="3">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
</table>

<div style="margin-top: 15px;width: 50px;display: block;text-align: center" class="btn1 bgBlue" ng-click="showAddInfo()">新增</div>