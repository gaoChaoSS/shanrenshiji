<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="store_storeAccount_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/store')"></span>
        商家账户
    </div>
    <div class="overflowPC">

        <div class="mod-panel1">
            <%--<div>--%>
                <%--<span style="opacity: 0.8" class="textSize18">余额:</span>--%>
                <%--<span class="textSize30" ng-bind="sellerCashCount==null?0:getMoney(sellerCashCount)"></span>--%>
            <%--</div>--%>
            <div class="overflowHidden">
                <div class="fl" style="width:50%">
                    <div class="textSize20" ng-bind="getMoney(accountTotal.cashCount)"></div>
                    <div style="opacity: 0.8">余额</div>
                </div>
                <div class="fl" style="width:50%">
                    <div class="textSize20" ng-bind="getMoney(accountTotal.orderCashCount)"></div>
                    <div style="opacity: 0.8">总入账</div>
                </div>
                <%--<div class="fl" style="width:50%">--%>
                    <%--<div class="textSize20" ng-bind="getMoney(accountYesterday)"></div>--%>
                    <%--<div style="opacity: 0.8">昨日入账</div>--%>
                <%--</div>--%>
            </div>
        </div>
        <div class="mod-panel2">
            <div class="fl">
                <div class="iconfont icon-card fl textSize30"></div>
                <div class="fl" style="margin-left: 10px;" ng-bind="bankInfo.bankUser"></div>
            </div>
            <div class="fr">
                <div ng-bind="bankInfo.bankName+getBankCard(bankInfo.bankId)"></div>
            </div>
        </div>
        <div class="TitleHead" style="position: relative;" id="AccountsP">
            <span class="titleMoney textSize30 whitefff" ng-bind="'¥ '+getMoney(sellerAccountTotal)"></span>
            <%--折线图--%>
            <div id="Accounts"></div>
            <div class="rowText1">计算规则:每日交易额=线上购物+现金交易+会员扫码+互联网收款</div>
        </div>

        <div class="sectionMain">
            <div class="mainRow rowHeight50" ng-click="goPage('/home/deposit/type/seller/orderType/3')">
                <div class="rowTitle">充值</div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
            <div class="mainRow rowHeight50" ng-click="bankCheck?isSetPayPwd():goPage('/home/bankBind/userType/Seller')">
                <div class="rowTitle">提现</div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
            <div class="mainRow rowHeight50" ng-click="isSellerSetPayPwd()">
                <div class="rowTitle">修改支付密码</div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
            <div class="mainRow rowHeight50" ng-click="goPage('/store/payForgotPassword/userType/seller')">
                <div class="rowTitle">找回支付密码</div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
        </div>

        <div>
            <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                  ng-bind="showDate(startDateTime)"></span>
            至
            <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                  ng-bind="showDate(endDateTime)"></span>

            <select ng-model="selectTradeType" ng-change="changeSelect(selectTradeType);">
                <option value="">所有</option>
                <option value="0">会员扫码</option>
                <option value="1">现金交易</option>
                <option value="2">互联网收款</option>
                <option value="11">线上交易</option>
                <%--<option value="3">充值</option>--%>
                <%--<option value="9">提现</option>--%>
            </select>
        </div>
        <div style="padding: 0 10px">
            <span class="gray888" ng-show="totalNumer>0">
                共<span class="colorBlue" ng-bind="isNullZero(totalNumer)"></span>笔,
                交易<span class="colorBlue" ng-bind="getMoney(total.totalPrice)"></span>元,
                入账<span class="colorBlue" ng-bind="getMoney(total.totalIncomeOne)"></span>元,
                佣金<span class="colorBlue" ng-bind="getMoney(total.totalBrokerage)"></span>元
            </span>
        </div>
        <div class="isNullBox" ng-show="sellerTransactionList.length==0">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="sectionMain rowFlex" ng-repeat="l in sellerTransactionList">
            <div style="flex:1.5;line-height: 31px;margin-left: 20px">
                <span ng-bind="showDate(l.endTime)"></span><br>
                <span ng-bind="getStoreTradeType(l.orderType)"></span><br>
                <span ng-bind="getStorePayType(l.payType)"></span>
            </div>
            <div class="mainRowLeft10" style="margin-top:20px;flex:1" >
                <img class="circularPhoto" err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(l.memberIcon)}}"
                     ng-show="l.orderType=='0'||l.orderType=='1'||l.orderType=='11'"/>
                <img class="circularPhoto" err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(l.sellerIcon)}}"
                     ng-show="l.orderType=='3'||l.orderType=='9'||l.orderType=='2'" />
            </div>
            <div class="mainRowLeft10" style="flex:3" ng-show="l.orderType=='0'||l.orderType=='1'||l.orderType=='2'||l.orderType=='11'">
                <div class="lineHeight33">
                    <span ng-bind="'消费:'+getMoney(l.payMoney)"></span>
                    <span class="mainRowLeft10" ng-bind="'佣金:'+getMoney(l.brokerageCount)"></span>
                </div>
                <div class="lineHeight33" ng-show="l.orderType!='1'">
                    <span>入账:</span>
                    <span style="color: red" ng-bind="' +'+getMoney(l.orderCash-l.brokerageCount)"></span>
                </div>
                <div class="lineHeight33" ng-show="l.orderType!='2'">
                    <span ng-bind="l.realName"></span>
                    <span ng-bind="'('+l.mobile+')'"></span>
                </div>
            </div>
            <div class="mainRowLeft10" style="flex:3" ng-show="l.orderType=='9'">
                <div class="lineHeight33">
                    <span>-</span>
                    <span class="mainRowLeft10" ng-bind="getMoney(l.orderCash)"></span>
                </div>
                <div class="lineHeight33">
                    <span>手续费</span>
                    <span style="color: red" ng-bind="' - ' + getMoney(l.fee)"></span>
                </div>
            </div>
            <div class="mainRowLeft10" style="flex:3" ng-show="l.orderType=='3'">
                <div class="lineHeight33">
                    <span>+</span>
                    <span class="mainRowLeft10" ng-bind="getMoney(l.orderCash)"></span>
                </div>
                <div class="lineHeight33"><span>账户余额充值</span></div>
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