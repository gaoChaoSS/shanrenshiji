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
        <td class="headerTd">会员:</td>
        <td>
            <input type="text" placeholder="请输入会员卡号或姓名" ng-model="filter._search"/>
        </td>
        <td class="headerTd" ng-if="entity=='pensionLog'">未投保金额:</td>
        <td ng-if="entity=='pensionLog'">
            <input type="text" placeholder="未投保金额" ng-model="filter._notInsure"/>
        </td>
        <td class="headerTd">投保状态:</td>
        <td>
            <select ng-options="item._id as item.title for item in ecpTypeList" ng-model="ecpItem.assignno"></select>
        </td>
    </tr>
</table>

