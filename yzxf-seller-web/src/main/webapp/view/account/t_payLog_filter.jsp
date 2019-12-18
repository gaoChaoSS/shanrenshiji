<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">日期:</td>
        <td colspan="5">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">支付人:</td>
        <td>
            <input type="text" ng-model="filter._payName"/>
        </td>
        <td class="headerTd">支付类型:</td>
        <td>
            <select ng-model="filter._payType">
                <option value="">------请选择------</option>
                <%--<option value="3">余额支付</option>--%>
                <option value="4">支付宝</option>
                <option value="10">微信</option>
            </select>
        </td>
        <td class="headerTd">支付结果:</td>
        <td>
            <select ng-model="filter._payStatus">
                <option value="">------请选择------</option>
                <option value="SUCCESS">成功</option>
                <option value="FAIL">失败</option>
                <option value="START">未支付</option>
                <option value="payReturn">退款</option>
            </select>
        </td>
    </tr>
</table>