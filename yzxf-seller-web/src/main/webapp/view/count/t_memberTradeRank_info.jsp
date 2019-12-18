<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%">
        <span>归属机构:</span>
        <span ng-bind="agentNameAll"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">总交易额</div>
    <div>
        <span>线上交易总额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.cashOnlineCount)"></span>
    </div>
    <div>
        <span>线下交易总额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.cashOfflineCount)"></span>
    </div>
    <div>
        <span>总交易额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.totalConsume)"></span>
    </div>
    <div>
        <span>充值总额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.rechargeCount)"></span>
    </div>
    <div>
        <span>目前余额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.cashCount)"></span>
    </div>
    <div>
        <span>已使用余额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.cashCountUse)"></span>
    </div>
</div>
<%--<div class="popSection" ng-show="dataPage.$$selectedItem.isTransfer">--%>
    <%--<div class="popTitle">转账银行信息</div>--%>
    <%--<div>--%>
        <%--<span>银行账号:</span>--%>
        <%--<span ng-bind="dataPage.$$selectedItem.bankId"></span>--%>
    <%--</div>--%>
    <%--<div>--%>
        <%--<span>开户行:</span>--%>
        <%--<span ng-bind="dataPage.$$selectedItem.bankName"></span>--%>
    <%--</div>--%>
    <%--<div>--%>
        <%--<span>银行持卡人:</span>--%>
        <%--<span ng-bind="dataPage.$$selectedItem.bankUser"></span>--%>
    <%--</div>--%>
    <%--<div>--%>
        <%--<span>转账日期:</span>--%>
        <%--<span ng-bind="showYFullTime(dataPage.$$selectedItem.transferTime)"></span>--%>
    <%--</div>--%>
<%--</div>--%>

<div class="popSection flex2">
    <div class="popTitle">交易流水</div>
    <div class="popTitle2">
        <div ng-click="setCashOrder(false)" ng-class="isCashOrder==false?'popTitle2-selected':''">所有订单</div>
        <div ng-click="setCashOrder(true)" ng-class="isCashOrder==true?'popTitle2-selected':''">余额订单</div>
    </div>

    <div style="width:100%;padding:0">
        <table class="sectionTable">
            <tr>
                <td ng-style="isCashOrder?{width:'15%'}:{width:'20%'}">订单编号</td>
                <td style="width:15%">交易类型</td>
                <td style="width:10%">支付方式</td>
                <td style="width:15%">付款人</td>
                <td style="width:15%">收款人</td>
                <td style="width:10%" ng-show="isCashOrder">余额</td>
                <td ng-style="isCashOrder?{width:'10%'}:{width:'15%'}">交易金额</td>
                <td style="width:15%">时间</td>
            </tr>
            <tr ng-repeat="order in orderList2" class="trBk">
                <td ng-style="isCashOrder?{width:'15%'}:{width:'20%'}" ng-bind="order.orderNo"></td>
                <td style="width:15%" ng-bind="getTradeType(order.orderType)"></td>
                <td style="width:10%" ng-bind="getPayType(order.payType)"></td>
                <td style="width:15%" ng-bind="getNameByPay($index,order.nameMember,order.nameSeller,order.nameFactor,order.orderType)"></td>
                <td style="width:15%" ng-bind="getNameByAcq($index,order.nameMember,order.nameMemberAcq,order.nameSeller,order.nameFactor,order.orderType)"></td>
                <td style="width:10%;color: red;" ng-show="isCashOrder" ng-bind="getMoney(order.cashCount)"></td>
                <td style="color: red;" ng-style="isCashOrder?{width:'10%'}:{width:'15%'}" ng-bind="getMoney(order.payMoney)"></td>
                <td style="width:15%" ng-bind="showYFullTime(order.orderCreateTime)"></td>
            </tr>
        </table>
        <div class="isNullBox" ng-show="isNullPage2">
            <div class="iconfont icon-meiyouneirong isNullIcon"></div>
            <div class="font25px colorGrayccc">没有数据</div>
        </div>
    </div>
</div>

<div class="sectionPage" ng-class="winCheck?'bottom0':''" ng-hide="isNullPage2" style="position: absolute;left: 0;bottom: -40px;width: 100%;margin: 0;">
    <div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex2!=1">上一页</div>
    <div ng-show="isFirstPage2">
        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>
        <div class="fl lineH30px">......</div>
    </div>
    <div class="btn3" ng-repeat="page2 in pageList2" ng-bind="page2.num"
         ng-click="pageNumber(page2.num);pageCur(page2.num)"
         ng-class="pageIndex2==page2.num?'bgBlue tWhite':''"></div>
    <div ng-show="isLastPage">
        <div class="fl lineH30px">......</div>
        <div class="btn3 fl marginLR5" ng-bind="totalPage2" ng-click="pageNumber(totalPage2)"></div>
    </div>
    <div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex2!=totalPage2">下一页</div>
    <div class="pageGo">
        <input type="text" placeholder="跳转" ng-model="pageGo2" />
        <button class="iconfont icon-right-1-copy" ng-click="pageNumber(pageGo2)"></button>
    </div>
</div>
