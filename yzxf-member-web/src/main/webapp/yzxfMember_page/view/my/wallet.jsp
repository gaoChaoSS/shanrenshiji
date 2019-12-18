<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_wallet_Ctrl" class="d_content form_section title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/my')"></span>
        消费积分
    </div>

    <div style="padding: 30px;background-color: #fff;margin-top: 10px;text-align: center">
        <div>
            <span class="notHigh">当前积分:</span>
            <span class=" high" style="padding:2px;font-size:30px;"
                  ng-bind="getMoney(wallet.cashCount)"></span>
            <span class="notHigh">元</span>
        </div>
        <div>
            <span class="notHigh">包含可提现红包:</span>
            <span class=" high" style="padding:2px;font-size:30px;"
                  ng-bind="getMoney(wallet.canWithdrawMoney)"></span>
            <span class="notHigh">元</span>
        </div>
    </div>
    <%--<div class="sectionWalletRow">--%>
    <%--<div><span class="textSize22" ng-bind="isNullZero(wallet.redPaperCount)"></span><span--%>
    <%--class="notHigh">元</span></div>--%>
    <%--<div><span class="iconfont icon-12 orangeRed textSize30"></span><span>现金红包</span></div>--%>
    <%--</div>--%>

    <div class="sectionMain" style="margin: 0;border-top: 1px solid #ddd;">
        <div class="mainRow" ng-click="checkAccount()">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-cshy-rmb2 iconfont iconBig"></span>
                购买积分
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="isMemberPayPwd('/my/depositFriend')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-pengyou iconfont iconBig"></span>
                代购积分
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="isMemberPayPwd('/my/depositRecord')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-cshy-rmb2 iconfont iconBig"></span>
                购买记录
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="isMemberPayPwd('/my/withdraw')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-cshy-rmb2 iconfont iconBig"></span>
                积分转账
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="isMemberPayPwd('/my/withdrawLog')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-cshy-rmb2 iconfont iconBig"></span>
                转账记录
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="isMemberPayPwd('/my/paySetting')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-shezhi iconfont iconBig"></span>
                支付设置
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-iconwolxwm iconfont iconBig"></span>
                联系客服
            </div>
            <div class="mainRowRight mainRowRightGreenBtn">
                <span class="icon-lianxi01 iconfont iconBig limeGreen"></span>
                <a href="tel:4001199687" class="notHigh">一键拨号</a>
            </div>
        </div>
        <%--<div class="mainRow">--%>
            <%--<div class="rowTitle rowTitleBtn">--%>
                <%--<span class="icon-guanyu iconfont iconBig"></span>--%>
                <%--关于我的积分--%>
            <%--</div>--%>
            <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
        <%--</div>--%>
    </div>
</div>