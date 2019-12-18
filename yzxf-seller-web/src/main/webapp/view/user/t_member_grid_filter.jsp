<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">创建日期:</td>
        <td colspan="5">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">姓名:</td>
        <td>
            <input type="text" ng-model="filter._realName"/>
        </td>
        <td class="headerTd">身份证:</td>
        <td>
            <input type="text" ng-model="filter._idCard"/>
        </td>
        <td class="headerTd">归属:</td>
        <td>
            <input type="text" ng-model="filter._belongArea"/>
        </td>
    </tr>
    <tr>
        <td class="headerTd">会员卡号:</td>
        <td>
            <input type="text" ng-model="filter._cardNo"/>
        </td>
        <td class="headerTd">会员手机:</td>
        <td>
            <input type="text" ng-model="filter._mobile"/>
        </td>
    </tr>
    <tr>
        <td class="headerTd">状态:</td>
        <td>
            <select ng-model="filter._canUse">
                <option value="">------请选择------</option>
                <option value="true">有效</option>
                <option value="false">禁用</option>
            </select>
        </td>
        <td class="headerTd">实名认证:</td>
        <td>
            <select ng-model="filter._isRealName" style="border: none;">
                <option value="">------请选择------</option>
                <option value="true">已认证</option>
                <option value="false">未认证</option>
            </select>
        </td>
    </tr>
</table>

