<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">修改日期:</td>
        <td colspan="3">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">类型:</td>
        <td>
            <select ng-options="type.type as type.typeTitle for type in typeList" ng-model="filter.type"></select>
        </td>
    </tr>
</table>