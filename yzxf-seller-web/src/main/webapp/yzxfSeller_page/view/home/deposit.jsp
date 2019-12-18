<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_deposit_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="isGoPage()"></span>
        余额充值
        <span class="titleManage notHigh" ng-click="goPage('/home/rechargeLog/orderType/'+pathParams.orderType+'/userType/'+getUserType(pathParams.type))">明细</span>
    </div>

    <div class="sectionMain">
        <div class="mainRow mainRowLast">
            <div class="rowTitle notHigh">充值金额:</div>
            <div class="rowInput">
                <input type="text" ng-model="money" ng-change="submitBtn()" placeholder="0"
                       class="inputBorder"/>
                <span class="notHigh"> 元</span>
            </div>
        </div>
        <div class="mainRow mainRowLast mainRowBtnList" ng-click="submitBtn()">
            <button ng-click="money=5">5 元</button>
            <button ng-click="money=10">10 元</button>
            <button ng-click="money=20">20 元</button>
            <button ng-click="money=50">50 元</button>
            <button ng-click="money=100">100 元</button>
            <button ng-click="money=300">300 元</button>
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
        <%--<div class="mainRow" ng-click="selectPay=4" ng-show="!isWechat && isMobile">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-iconfontweixin iconfont textSize18 limeGreen"></span>--%>
                <%--仅支持微信内支付--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>

    <input type="button" value="确认充值" class="submitBtn submitBtnBlue" ng-disabled="submitCheck" ng-click="submitOrder()"/>

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
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到账户中心'"></div>
        </div>
        <div class="errorMain otherMain" ng-show="!activeStatus">
            <div class="otherMainRow icon-cuowu iconfont textSize50 orangeRed3"></div>
            <div class="otherMainRow textSize22">充值失败请稍后再试</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后请重新尝试'"></div>
        </div>
    </div>
</div>