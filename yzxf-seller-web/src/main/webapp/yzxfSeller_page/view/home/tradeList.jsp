<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_tradeList_Ctrl" class="d_content title_section form_section "
     style="overflow-x: hidden">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/index')"></span>
        会员交易记录列表
        <%--<span class="titleManage black textSize15 grayaaa">搜索</span>--%>
    </div>
    <div class="overflowPC">
        <%--<div ng-click="searchCheck=true" class="lineHeight50 textCenter sellerQuery" ng-show="!searchCheck">--%>
        <%--<span class="icon-fangdajing iconfont iconBig grayaaa"></span><span--%>
        <%--class="grayaaa textSize15">通过会员号或手机号搜索</span>--%>
        <%--</div>--%>
        <%--<input ng-show="searchCheck" ng-model="search" type="text" class="lineHeight50 sellerQuery textCenter"--%>
        <%--ng-blur="searchCheckFun()"/>--%>
        <div class="queryTitle">
            <div class="rowTitle3">会员卡号:</div>
            <input type="text" ng-model="memberCard" placeholder="请输入会员卡号查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">电话号码:</div>
            <input type="text" ng-model="memberPhone" placeholder="请输入电话号码查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">会员姓名:</div>
            <input type="text" ng-model="memberName" placeholder="请输入会员姓名查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">身份证:</div>
            <input type="text" ng-model="memberIdCard" placeholder="请输入身份证查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">交易日期:</div>
            <div class="mainRowLeft80">
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                      ng-bind="isAll?'&nbsp;':showDate(startDateTime)" style="margin:0 5px 0 0;min-width: 97px;"></span>—
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                      ng-bind="showDate(endDateTime)" style="margin:0;min-width: 97px"></span>
            </div>
        </div>
        <div class="clearDiv"></div>
        <div class="queryTitle">
            <div class="flTitle">
                <button ng-click="queryOrderList()">查询</button>
            </div>
            <div class="frTitle">
                <button ng-click="queryOrderListAll()">查询所有</button>
            </div>
        </div>
        <div class="clearDiv"></div>
        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totalNumber)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="orderList==null || orderList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="consumptionMain" ng-repeat="offline in orderList">
            <div class="consumptionRow">
                <span class="mainRowLeft10" ng-bind="offline.memberName"></span>
                <span class="grayaaa" ng-bind="'(会员号:'+offline.cardNo+')'"></span>
                <div class="consumptionRowRight" ng-bind="showYFullTime(offline.endTime)"></div>
            </div>
            <div class="consumptionRow rowDashedBottom">
                <span class="consumptionRowTitle">订单金额:<span
                        style="color:#268BBF;">{{isNullZero(offline.totalPrice)}}</span>元</span>
                <div class="consumptionRowRight" ng-show="offline.orderType!=7">
                    <span style="color:#444;">商家:</span>
                    <span style="color:#268BBF;" ng-bind="offline.name"></span>
                    <span style="color:#aaa;">(积分率:{{isNullZero(offline.orderIntegralRate)}}&#37;)</span>
                </div>
                <div class="consumptionRowRight" ng-show="offline.orderType==7">
                    会员卡激活
                </div>
            </div>
            <div class="consumptionRow ">
                <span class="consumptionRowTitle">支付方式:
                    <span class="deepBlue" ng-bind="getPayType(offline.payType)"></span>
                </span>
                <div class="consumptionRowRight errorRed" ng-bind="'提成金额 ¥ '+getMoney(offline.orderCash)"></div>
            </div>
        </div>
        <div class="loadMore" ng-show="totalNumber>0">
        <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span
                style="color:#268BBF;font-size: 15px;">{{isNullZero(totalNumber)}}</span>&nbsp;条</span>
            <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>