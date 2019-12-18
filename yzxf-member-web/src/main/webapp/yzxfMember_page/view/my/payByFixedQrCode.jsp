<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_payByFixedQrCode_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/my')"></span>
        扫固定收银牌
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">订单信息:</div>
            <div class="rowInput textRight" ng-bind="seller.name+'-线下付款'"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">付款金额:</div>
            <input class="rowInput" ng-model="totalPrice" placeholder="请输入付款金额" style="text-align: right;"/>
        </div>
        <div class="mainRow" ng-show="notLogin">
            <div class="rowTitle">手机号码:</div>
            <input class="rowInput" ng-model="mobile" placeholder="手机号码" style="text-align: right;"/>
        </div>
    </div>

    <div class="sectionMain">
        <%--<div class="mainRow" ng-click="payType=3">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-cshy-rmb2 iconfont textSize22 deepRed"></span>--%>
                <%--余额支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{3:'icon-103'}[payType]"></div>--%>
        <%--</div>--%>
        <%--<div class="mainRow" ng-click="payType=10" ng-show="isWechat">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-iconfontweixin iconfont textSize18 limeGreen"></span>--%>
                <%--微信支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{10:'icon-103'}[payType]"></div>--%>
        <%--</div>--%>

        <%--<div class="mainRow" ng-click="payType=4" ng-show="!isWechat">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-zhifubao iconfont textSize18 dodgerBlue"></span>--%>
                <%--支付宝支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{4:'icon-103'}[payType]"></div>--%>
        <%--</div>--%>

        <div class="mainRow" ng-click="payType=18">
            <div class="rowTitle rowTitleBtn notHigh">
                <img src="/yzxfMall_page/img/gebank_logo.png" alt="">
                贵商行支付
            </div>
            <div class="iconfont mainRowRight limeGreen" ng-class="{18:'icon-103'}[payType]"></div>
        </div>
    </div>

    <button class="submitBtn" ng-click="getPwdWin()">确认付款</button>

    <div id="payForm">

    </div>

    <div class="hideMenu" ng-show="menuCheck">
        <div class="enterPsw">
            <div class="textCenter lineHeight50" ng-bind="'付款给 '+seller.name"></div>
            <div class="textCenter textSize30 lineHeight40" ng-bind="'¥ '+totalPrice"></div>
            <div class="enterInput">
                <input maxlength="1" type="tel" ng-model="pwd1"/>
                <input maxlength="1" type="tel" ng-model="pwd2"/>
                <input maxlength="1" type="tel" ng-model="pwd3"/>
                <input maxlength="1" type="tel" ng-model="pwd4"/>
                <input maxlength="1" type="tel" ng-model="pwd5"/>
                <input maxlength="1" type="tel" ng-model="pwd6"/>
            </div>
            <div class="enterInputDes">请输入6位支付密码</div>
            <div class="selectDiv">
                <div style="border-left: 0;border-bottom: 0" ng-click="menuCheck=false">取消</div>
                <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="submitOrder()">确认</div>
            </div>
        </div>
    </div>
    <div class="hideMenu" style="background:#fff" ng-show="isShowBind">
        <div class="lineHeight100 colorGray888 textCenter textSize20">请先绑定微信登录</div>
        <div class="lineHeight50 colorGray888 textCenter">正在跳转...</div>
    </div>
</div>