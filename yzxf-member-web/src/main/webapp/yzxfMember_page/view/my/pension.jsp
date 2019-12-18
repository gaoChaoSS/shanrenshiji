<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_pension_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        我的养老金
    </div>
    <div class="overflowPC">
        <div class="sectionMain">
            <div class="pensionMain">
                <div class="floatRight icon-shuaxin iconfont textSize30 mainRowRight10 mainRowTop10 deepBlue"
                     ng-click="refresh()"></div>
                <div class="textCenter gray666 clearRight lineHeight50">养老金总额</div>
                <div class="textCenter">
                    <span class="textSize30 lineHeight50 mainRowLeft25"
                          ng-bind="getMoney(pensionMoney.pensionCount)"></span>元
                </div>
            </div>
            <div class="textCenter lineHeight30 gray888 mainRowLeft100N positionRelative" ng-if="myInfo.isBindCard">
                已投保金额
                <span class="mainRowLeft25 gray888 positionAbsolute lineHeight30"
                      ng-bind="getMoney(pensionMoney.insureCountUse)+' 元'"></span>
            </div>
            <div class="textCenter lineHeight30 gray888 mainRowLeft100N positionRelative">
                <span class="notHigh" ng-bind="myInfo.isBindCard?'未投保金额':'消费奖励金'"></span>
                <span class="mainRowLeft25 gray888 positionAbsolute lineHeight30"
                      ng-bind="getMoney(pensionMoney.insureCount)+' 元'"></span>
            </div>
            <div class="textCenter lineHeight30 grayaaa textSize12" ng-show="showText!=null && showText!=''">
                <%--注: 保单的情况会通过邮件通知--%>
                <span ng-bind="showText" ng-click="goPage(goPageText)" class="colorBlue"></span>
            </div>
            <div class="sectionMain" style="border-top: 1px solid #eee;">
                <div class="mainRow" ng-click="showLoginSelect=!showLoginSelect">
                    <div class="rowTitle">交易方式:</div>
                    <span class="rowInput notHigh" ng-bind="selectedText"></span>
                    <span class="icon-down iconfont mainRowRight textSize30"></span>
                </div>
                <div ng-show="showLoginSelect" ng-click="showLoginSelect=false">
                    <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='线下交易';getOrderList()">线下交易
                        <span ng-show="selectedText=='线下交易'"
                              class="icon-zhengque iconfont mainRowSelected limeGreen"></span>
                    </div>
                    <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='线上交易';getOrderList()">线上交易
                        <span ng-show="selectedText=='线上交易'"
                              class="icon-zhengque iconfont mainRowSelected limeGreen"></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="sectionMain" style="margin:0;position: relative">
            <div class="rowTitle" style="position: static;text-align: center">选择日期:</div>
            <div class="mainRowSelect textCenter mainRowBottom10" style="margin: 8px;border: 0;float:none;">
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                      ng-bind="showDate(startDateTime)"></span>
                ——
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                      ng-bind="showDate(endDateTime)"></span>
            </div>
        </div>
        <div class="mainRowLeft10 gray888" style="overflow: hidden;padding-top:20px;">
            <div class="fl">明细列表:</div>
            <div class="fr notHigh">共查询到 <span ng-bind="isNullZero(totalNumer)" class="colorBlue"></span> 条记录
            </div>
        </div>
        <div class="isNullBox" ng-show="orderList==null || orderList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="consumptionMain" ng-repeat="items in orderList">
            <div class="consumptionRow">
                <span class="consumptionRowTitle" style="font-weight: bold;width:auto;" ng-bind="items.name"></span>
                &nbsp;
                <%--<span class="grayaaa" ng-bind="' (积分率:'+isNullZero(items.score)+'%)'"></span>--%>
                <div class="consumptionRowRight" ng-bind="showYFullTime(items.endTime)"></div>
            </div>
            <div class="consumptionRow rowDashedBottom">
                <span class="consumptionRowTitle" ng-show="items.orderType!=7 && items.orderType!=8"
                      ng-bind="'养老金: '+getMoney(items.pensionMoney)+'元'" style="width: calc(100% - 150px);"></span>
                <span class="consumptionRowTitle" ng-show="items.orderType==7 || items.orderType==8"
                      ng-bind="myInfo.belongArea" style="width: calc(100% - 150px);"></span>
                <div class="consumptionRowRight" ng-bind="getOrderType(items.orderType)"></div>
            </div>
            <div class="consumptionRow ">
                <div class="consumptionRowLeft" ng-show="items.orderType!=7 && items.orderType!=8"
                     ng-bind="isInsureFun(items.isInsure,items.insureCount,items.insureCountUse)"
                     ng-class="items.isInsure?'colorBlue':'grayaaa'"></div>
                <div class="consumptionRowLeft grayaaa" ng-show="items.orderType==7 || items.orderType==8"
                     ng-bind="'赠送人生意外险一份，保险生效日期以保险公司短信为准'"></div>
                <div class="consumptionRowRight errorRed" ng-bind="'¥ '+getMoney(items.payMoney)"></div>
            </div>
        </div>
        <div class="loadMore" ng-show="totalNumer>0">
            <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>

