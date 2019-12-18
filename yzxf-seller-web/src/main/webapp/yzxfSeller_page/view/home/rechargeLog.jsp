<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_rechargeLog_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        {{pageTitle}}明细
    </div>

    <div class="overflowPC">
        <div>
        <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
              ng-bind="showDate(startDateTime)"></span>
            至
            <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                  ng-bind="showDate(endDateTime)"></span>

            <%--<select ng-model="selectTradeType" ng-change="changeSelect(selectTradeType);">--%>
            <%--<option value="">所有</option>--%>
            <%--<option value="0">会员扫码</option>--%>
            <%--<option value="1">现金交易</option>--%>
            <%--<option value="2">互联网收款</option>--%>
            <%--<option value="11">线上交易</option>--%>
            <%--&lt;%&ndash;<option value="3">充值</option>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<option value="9">提现</option>&ndash;%&gt;--%>
            <%--</select>--%>
        </div>
        <div style="padding: 0 10px">
            <span class="gray888" ng-show="totalNumer>0">
                共<span class="colorBlue" ng-bind="isNullZero(totalNumer)"></span>笔,
                {{pageTitle}}总额<span class="colorBlue" ng-bind="getMoney(total.totalPrice)"></span>元
            </span>
        </div>
        <div class="isNullBox" ng-show="order.length==0">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="sectionMain rowFlex" ng-repeat="l in order">
            <div style="flex:1.5;line-height: 31px;margin-left: 20px">
                <span ng-bind="showDate(l.endTime)" style="line-height: 87px;"></span><br>
                <%--<span ng-bind="getStoreTradeType(l.oTradeType)"></span><br>--%>
            </div>
            <div class="mainRowLeft10" style="margin-top:20px;flex:1" >
                <img class="circularPhoto" err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(pathParams.userType=='Seller'?l.sellerIcon:l.factorIcon)}}" />
            </div>
            <div class="mainRowLeft10" style="flex:3;padding: 10px 0;" ng-show="l.orderType=='9' || l.orderType=='10'">
                <div class="lineHeight33">
                    <span>-</span>
                    <span class="mainRowLeft10" ng-bind="getMoney(l.orderCash)"></span>
                </div>
                <div class="lineHeight33">
                    <span>手续费</span>
                    <span style="color: red" ng-bind="' - ' + getMoney(l.fee)"></span>
                </div>
                <span ng-bind="getStorePayType(l.payType)"></span>
            </div>
            <div class="mainRowLeft10" style="flex:3;padding: 10px 0;" ng-show="l.orderType=='3' || l.orderType=='4'">
                <div class="lineHeight33">
                    <span>+</span>
                    <span class="mainRowLeft10" ng-bind="getMoney(l.orderCash)"></span>
                </div>
                <div class="lineHeight33" ng-bind="getStorePayType(l.payType)"></div>
            </div>
        </div>
        <div class="loadMore">
            <div id="moreButton" ng-show="isLoadMore&&totalNumer>0"
                 style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>