<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%">
        <span>归属机构:</span>
        <span ng-bind="agentNameAll"></span>
    </div>
</div>
<div class="popSection">
    <div class="popTitle">月结算</div>
    <div ng-show="dataPage.$$selectedItem.userType=='Seller'">
        <span>总交易额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.orderCash)"></span>
    </div>
    <div>
        <span>总收益额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.incomeAccount)"></span>
    </div>
</div>
<div class="popSection" ng-show="dataPage.$$selectedItem.isTransfer">
    <div class="popTitle">转账银行信息</div>
    <div>
        <span>银行账号:</span>
        <span ng-bind="dataPage.$$selectedItem.bankId"></span>
    </div>
    <div>
        <span>开户行:</span>
        <span ng-bind="dataPage.$$selectedItem.bankName"></span>
    </div>
    <div>
        <span>银行持卡人:</span>
        <span ng-bind="dataPage.$$selectedItem.bankUser"></span>
    </div>
    <div>
        <span>转账日期:</span>
        <span ng-bind="showYFullTime(dataPage.$$selectedItem.transferTime)"></span>
    </div>
</div>
<div class="popSection">
    <div class="popTitle">交易流水信息</div>
    <div>
        <span>是否到账:</span>
        <span ng-bind="fyList.isSuccess?'到账':'未到账'"></span>
    </div>
    <div>
        <span>状态:</span>
        <span ng-bind="fyList.result"></span>
    </div>
    <div>
        <span>备注:</span>
        <span ng-bind="fyList.reason"></span>
    </div>
    <div ng-show="fyList.transferMoney!=null && fyList.transferMoney!=''">
        <span>实际转账金额:</span>
        <span ng-bind="fyList.transferMoney"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">交易流水</div>
    <div style="width:100%;padding:0">
        <div class="sectionTable">
            <div>
                <div style="width:20%">归属</div>
                <div style="width:15%">订单编号</div>
                <div style="width:10%">交易类型</div>
                <div style="width:15%">付款人</div>
                <div style="width:15%">收款人</div>
                <div style="width:10%">交易金额</div>
                <div style="width:15%">时间</div>
            </div>
            <div ng-repeat="order in orderList2" class="trBk">
                <div style="width:20%" ng-bind="getBelongArea(order.belongMember,order.belongSeller,order.belongFactor,order.cardNo,order.orderType)"></div>
                <div style="width:15%" ng-bind="order.orderNo"></div>
                <div style="width:10%" ng-bind="getTradeType(order.orderType)"></div>
                <div style="width:15%" ng-bind="getNameByPay($index,order.nameMember,order.nameSeller,order.nameFactor,order.orderType)"></div>
                <div style="width:15%" ng-bind="getNameByAcq($index,order.nameMember,order.nameMemberAcq,order.nameSeller,order.nameFactor,order.orderType)"></div>
                <div style="width:10%;color: red;" ng-bind="getMoney(order.payMoney)"></div>
                <div style="width:15%" ng-bind="showYFullTime(order.orderCreateTime)"></div>
            </div>
        </div>
        <div class="isNullBox" ng-show="isNullPage2">
            <div class="iconfont icon-meiyouneirong isNullIcon"></div>
            <div class="font25px colorGrayccc">没有数据</div>
        </div>

    </div>
</div>
<div class="sectionPage" ng-hide="isNullPage2" style="position: absolute;left: 0;bottom: -40px;width: 100%;margin: 0;">
    <div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex2!=1">上一页</div>
    <div ng-show="isFirstPage2">
        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>
        <div class="fl lineH30px">......</div>
    </div>
    <div class="btn3" ng-repeat="page2 in pageList2" ng-bind="page2.num"
         ng-click="pageNumber(page2.num);pageCur(page2.num)"
         ng-class="pageIndex2==page2.num?'bgBlue tWhite':''"></div>
    <div ng-show="isLastPage2">
        <div class="fl lineH30px">......</div>
        <div class="btn3 fl marginLR5" ng-bind="totalPage2" ng-click="pageNumber(totalPage2)"></div>
    </div>
    <div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex2!=totalPage2">下一页</div>
    <div class="pageGo">
        <input type="text" placeholder="跳转" ng-model="pageGo2" />
        <button class="iconfont icon-right-1-copy" ng-click="pageNumber(pageGo2)"></button>
    </div>
</div>
