<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">名称:</td>
        <td colspan="3">
            <input type="text" ng-model="filter._name" />
        </td>
    </tr>
</table>