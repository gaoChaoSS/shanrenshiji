<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<tr>
    <td style="width:50%" ng-show="dataPage.items[0].modifyType==2">养老金上限</td>
    <td style="width:50%" ng-show="dataPage.items[0].modifyType==3">最低提现额度</td>
    <td style="width:50%" ng-show="dataPage.items[0].modifyType==4">手续费比例</td>
    <td style="width:50%" ng-show="dataPage.items[0].modifyType==6">会员充值总分润比例</td>
    <td style="width:50%" ng-show="dataPage.items[0].modifyType==7">会员充值赠送养老金比例</td>
    <td style="width:50%" ng-show="dataPage.items[0].modifyType==8">会员激活金额</td>
    <td style="width:50%">修改时间</td>
</tr>
<tr ng-repeat="pension in dataPage.items" class="trBk">
    <td style="width:50%;color: red;" ng-bind="pension.pensionLog"></td>
    <td style="width:50%" ng-bind="showYFullTime(pension.createTime)"></td>
</tr>
