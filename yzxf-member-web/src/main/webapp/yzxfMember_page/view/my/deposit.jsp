<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_deposit_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/wallet')"></span>
        购买积分
    </div>

    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle rowTitleBtn notHigh textCenter" style="width:100%;margin: 0;" ng-bind="'购买积分赠送'+rechargePensionRatio+'养老金'"></div>
        </div>
    </div>

    <div class="sectionMain">
        <div class="mainRow mainRowLast">
            <div class="rowTitle notHigh">购买积分:</div>
            <div class="rowInput">
                <input type="text" ng-model="money" ng-change="submitBtn()" placeholder="选择购买积分"
                       class="inputBorder" disabled="disabled"/>
                <span class="notHigh"> 元（1积分 = 1元）</span>
            </div>
        </div>
        <div class="mainRow mainRowLast mainRowBtnList" ng-click="submitBtn()">
            <button ng-click="money=10">10 积分</button>
            <button ng-click="money=50">50 积分</button>
            <button ng-click="money=100">100 积分</button>
            <button ng-click="money=200">200 积分</button>
            <button ng-click="money=500">500 积分</button>
            <button ng-click="money=1000">1000 积分</button>
        </div>
    </div>

    <div class="sectionMain">
        <%--<div class="mainRow" ng-click="selectPay=10" ng-show="isWechat || !isMobile">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-iconfontweixin iconfont textSize18 limeGreen"></span>--%>
                <%--微信支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{10:'icon-103'}[selectPay]"></div>--%>
        <%--</div>--%>
        <%--<div class="mainRow" ng-click="selectPay=4" ng-show="!isWechat">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-zhifubao iconfont textSize18 dodgerBlue"></span>--%>
                <%--支付宝支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{4:'icon-103'}[selectPay]"></div>--%>
        <%--</div>--%>
        <div class="mainRow" ng-click="selectPay=18">
            <div class="rowTitle rowTitleBtn notHigh">
                <img src="/yzxfMall_page/img/gebank_logo.png" alt="">
                贵商行支付
            </div>
            <div class="iconfont mainRowRight limeGreen" ng-class="{18:'icon-103'}[selectPay]"></div>
        </div>
        <%--<div class="mainRow" ng-click="selectPay=10" ng-show="!isWechat && isMobile">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-iconfontweixin iconfont textSize18 limeGreen"></span>--%>
                <%--仅支持微信内支付--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>

    <input type="submit" value="确认购买" class="submitBtn" ng-disabled="check" ng-click="checkAccount()"/>
    <div id="payForm">

    </div>

    <div ng-show="isShowQrCodePage" class="hideMenu" style="background:#fff">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            {{selectPay==4?'支付宝':'微信'}}支付
        </div>
        <div class="sectionMain">
            <div class="mainRow mainRowLast lineHeight100 textSize30 textCenter errorRed"
                 ng-bind="'¥ '+order.totalPrice">
            </div>
        </div>
        <div class="sectionMain qrcodeContentCon" style="text-align:center"></div>
    </div>

    <div class="hideMenu" ng-show="active" style="z-index:99">
        <div class="errorMain otherMain" ng-show="activeStatus">
            <div class="otherMainRow icon-zhengque1 iconfont textSize50 limeGreen"></div>
            <div class="otherMainRow textSize25">充值成功</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到我的积分'"></div>
        </div>
        <div class="errorMain otherMain" ng-show="!activeStatus">
            <div class="otherMainRow icon-cuowu iconfont textSize50 orangeRed3"></div>
            <div class="otherMainRow textSize22">充值失败请稍后再试</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后请重新尝试'"></div>
        </div>
    </div>
</div>