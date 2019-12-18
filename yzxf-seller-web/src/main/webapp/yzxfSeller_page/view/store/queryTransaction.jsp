<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_queryTransaction_Ctrl"
     class="d_content title_section form_section index_page_globalDiv order_panel ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        交易查询
        <span class="titleManage black textSize15 grayaaa">搜索</span>
    </div>
    <div class="overflowPC">
        <input ng-model="search" placeholder="通过会员号或手机号搜索" type="text" class="lineHeight50 sellerQuery textCenter"
               ng-blur="searchCheckFun()"/>

        <div class="sectionMain">
            <div class="mainRow" ng-click="showLoginSelect=!showLoginSelect">
                <div class="rowTitle">交易方式:</div>
                <span class="rowInput notHigh lineHeight50" ng-bind="selectedText"></span>
                <span class="icon-iconfontarrows iconfont mainRowRight"></span>
            </div>
            <div ng-show="showLoginSelect" ng-click="showLoginSelect=false">
                <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='线上交易';getOrderList()">线上交易
                    <span ng-show="selectedText=='线上交易'" class="icon-jianchacheck35 iconfont mainRowSelected"></span>
                </div>
                <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='线下交易';getOrderList()">线下交易
                    <span ng-show="selectedText=='线下交易'" class="icon-jianchacheck35 iconfont mainRowSelected"></span>
                </div>
            </div>
        </div>

        <div class="sectionMain">
            <div>
                <div class="lineHeight40 textCenter">选择日期</div>
                <div class="lineHeight40 textCenter mainRowBottom10">
                    <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                          ng-bind="showDate(startDateTime)"></span>
                    ——
                    <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                          ng-bind="showDate(endDateTime)"></span>
                </div>
            </div>
        </div>

        <div class="mainRow">
            <div class="rowTitle widthPercent20 textSize15 lineHeight50">交易列表</div>
        </div>
        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totalNumer)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="orderList==null || orderList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>

        <div class="order-model1" ng-repeat="item in orderList" ng-show="selectedText=='线上交易'"
             ng-click="goPage('/store/orderInfo/orderId/'+item.orderId)">
            <div class="order-title1">
                <div class="fl mainRowLeft10" ng-bind="item.realName"></div>
                <div class="fl mainRowLeft10 gray888" ng-bind="'(会员号:'+item.cardNo+')'"></div>
                <div class="fr mainRowRight10 deepRed" ng-bind="getOrderStatus(item,$index)"></div>
            </div>
            <div class="order-info1">
                <img src="{{iconImgUrl(item.goodsIcon)}}">
                <div class="order-text1">
                    <div>
                        <span ng-bind="item.goodsName"></span>
                        <span class="gray888" ng-bind="' ( × '+item.count+')'"></span>
                    </div>
                    <div>订单金额: <span ng-bind="item.totalPrice+'元'" class="deepRed"></span></div>
                    <div ng-bind="item.showStatusTime"></div>
                    <div ng-show="item.express!=null" ng-bind="item.express+': '+item.expressNo"></div>
                </div>
            </div>
        </div>
        <div class="consumptionMain" ng-repeat="offline in orderList" ng-show="selectedText=='线下交易'">
            <div class="consumptionRow">
                <span class="mainRowLeft10" ng-bind="offline.realName"></span>
                <span class="grayaaa" ng-bind="offline.cardNo==null?'非会员':'(会员号:'+offline.cardNo+')'"></span>
                <div class="consumptionRowRight" ng-bind="showYFullTime(offline.endTime)"></div>
            </div>
            <div class="consumptionRow rowDashedBottom">
                <span class="consumptionRowTitle" style="color:blue;width: 150px;"
                      ng-bind="'平台佣金: '+getMoney(offline.brokerageCount)+' 元'"></span>
                <div class="consumptionRowRight" ng-show="offline.cardNo!=null" ng-bind="'(积分率: '+isNullZero(offline.score)+' %)'"></div>
            </div>
            <div class="consumptionRow ">
                <span class="consumptionRowTitle">会员支付方式:
                    <span class="deepBlue" ng-bind="getPayType(offline.payType)"></span>
                </span>
                <div class="consumptionRowRight errorRed" ng-bind="getOrderType(offline.orderType)+' ¥ '+getMoney(offline.payMoney)"></div>
            </div>
        </div>
        <div class="loadMore">
            <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span
                    style="color:#268BBF;font-size: 15px;">{{isNullZero(totalNumer)}}</span>&nbsp;条</span>
                <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                     ng-click="more()">加载更多...
                </div>
                <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
            </div>
        </div>
</div>