<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">日期:</td>
        <td colspan="3">
            <div ng-include="timeFilter"></div>
        </td>
    </tr>
    <tr>
        <td class="headerTd">记录类型:</td>
        <td>
            <select ng-model="filter._modifyType" style="border: none;background-color: #fff;" ng-change="selectModifyType()">
                <option value="">利润</option>
                <option value="0">发卡利润</option>
                <option value="2">养老金</option>
                <option value="3">最低提现额度</option>
                <option value="4">手续费比例</option>
                <option value="5">会员充值各级分润比例</option>
                <option value="6">会员充值总分润比例</option>
                <option value="7">会员充值赠送养老金比例</option>
                <option value="8">激活会员金额</option>
            </select>
        </td>
    </tr>
</table>