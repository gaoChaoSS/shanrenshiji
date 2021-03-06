<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_orderPay_Ctrl" class="d_content form_section title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        账户管理费
    </div>

    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">卡号:</div>
            <input type="text"  class="rowInput" ng-model="cardNo" ng-change="nextBtn()" placeholder="请输入激活号码"/>
        </div>
    </div>

    <div class="sectionMain" style="padding: 10px 20px;">
        <div style="padding: 10px 0;">激活会员专享:</div>
        <p style="margin-left: 20px">
            1. 获得商店的专享优惠.<br>
            2. 可享受消费积分养老金.<br>
        </p>
    </div>

    <div class="sectionMain">
        <div class="mainRow mainRowLast lineHeight100 textSize30 textCenter errorRed" ng-bind="'¥ '+activeMoney">
        </div>
    </div>
    <div class="sectionMain">
        <%--<div class="mainRow" ng-click="selectPay=3">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-cshy-rmb2 iconfont textSize22 deepRed"></span>--%>
                <%--余额支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{3:'icon-103'}[selectPay]"></div>--%>
        <%--</div>--%>
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
        <div class="mainRow" ng-click="selectPay=18" ng-show="!isMobile">
            <div class="rowTitle rowTitleBtn notHigh">
                <img src="/yzxfMall_page/img/gebank_logo.png" alt="">
                贵商行支付
            </div>
            <div class="iconfont mainRowRight limeGreen" ng-class="{18:'icon-103'}[selectPay]"></div>
        </div>
    </div>

    <input type="submit" value="确认" class="submitBtn" ng-click="getPwdWin()"/>

    <div class="hideMenu" ng-show="menuCheck">
        <div class="enterPsw">
            <div class="textCenter lineHeight50">激活会员卡</div>
            <div class="textCenter textSize30 lineHeight40" ng-bind="'¥ '+activeMoney"></div>
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
            <div class="otherMainRow textSize25">激活成功</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到我的钱包'"></div>
        </div>
        <div class="errorMain otherMain" ng-show="!activeStatus">
            <div class="otherMainRow icon-cuowu iconfont textSize50 orangeRed3"></div>
            <div class="otherMainRow textSize22">激活失败请稍后再试</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后请重新尝试'"></div>
        </div>
    </div>

    <div class="hideMenu" ng-show="isSuccess" style="z-index:99">
        <div class="errorMain otherMain" style="height: auto;">
            <div class="otherMainRow icon-zhengque1 iconfont textSize50 limeGreen"></div>
            <div class="otherMainRow textSize25">激活成功</div>
            <div class="otherMainRow" style="height: auto">请前往个人信息查看会员卡号</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countTimeNum+' 秒后返回到我的主页'"></div>
        </div>
    </div>
</div>