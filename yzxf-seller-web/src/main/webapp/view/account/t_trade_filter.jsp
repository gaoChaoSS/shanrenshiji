<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">创建日期:</td>
        <td>
            <div ng-include="timeFilter"></div>
        </td>
        <td class="headerTd">查询方式:</td>
        <td>
            <select ng-model="filter._queryType">
                <option value="">按创建时间查询</option>
                <option value="endTime">按结算时间查询</option>
            </select>
        </td>
        <td class="headerTd">支付方式:</td>
        <td>
            <select ng-model="filter._payType">
                <option value="">所有</option>
                <option value="3">余额</option>
                <option value="4">支付宝</option>
                <option value="10">微信</option>
                <option value="6">现金</option>
                <option value="16">养老金</option>
            </select>
        </td>
    </tr>
    <tr>
        <td class="headerTd">关键字:</td>
        <td>
            <input type="text" placeholder="用户,ID,手机,身份证号.." ng-model="filter._search"/>
        </td>
        <td class="headerTd">订单编号:</td>
        <td>
            <input type="text" placeholder="订单编号" ng-model="filter._orderNo"/>
        </td>
        <td class="headerTd">交易类型:</td>
        <td>
            <select ng-model="filter._orderType">
                <option value="">所有</option>
                <option value="0">会员扫码</option>
                <option value="1">现金交易</option>
                <option value="2">互联网收款</option>
                <option value="3" ng-show="agent.level==1">商家充值</option>
                <option value="4" ng-show="agent.level==1">服务站充值</option>
                <option value="5" ng-show="agent.level==1">会员充值</option>
                <option value="6" ng-show="agent.level==1">会员代充值</option>
                <option value="7">服务站激活会员卡</option>
                <option value="8">会员端激活会员卡</option>
                <option value="13">养老金激活会员卡</option>
                <option value="9" ng-show="agent.level==1">商家提现</option>
                <option value="10" ng-show="agent.level==1">服务站提现</option>
                <option value="11">会员在线购买</option>
                <option value="12">快易帮现金收款</option>
                <option value="14" ng-show="agent.level==1">会员提现</option>
            </select>
        </td>
    </tr>
</table>

