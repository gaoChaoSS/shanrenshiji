<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易扫码--%>
<div ng-controller="store_cashTransactionsScan_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('store/store')"></span>
        商家支付佣金
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <input type="text" class="cardIssuingInput" style="width:calc(100% - 120px);" ng-model="memberCard"
                   placeholder="请输入会员卡号/手机号/身份证号" ng-blur="memberCard!=null?getMemberInfo():''">
            <button class="cardIssuingButton" style="float: right;margin: 7px 10px 6px 0;width:auto">扫一扫</button>
        </div>
    </div>

    <div class="commentList" ng-show="memberCheck">
        <div class="commentMain paddingBottomNot">
            <div class="headAtAmong">
                <div class="circularPhoto">
                    <img src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(memberInfo.icon)}}"/>
                </div>
            </div>
            <div class="mainRow2 mainRowTop10">
                <div>
                    <div class="textTracking gray666">名称</div>
                </div>
                <div class="gray666"
                     ng-bind="': '+((memberInfo.realName==null||memberInfo.realName=='')?' 匿名':memberInfo.realName)"></div>
            </div>
            <div class="mainRow2 mainRowBottom10 gray666">
                <div>
                    <div class="textTracking gray666">账号</div>
                </div>
                <div class="gray666" ng-bind="': '+memberInfo.cardNo"></div>
            </div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">消费金额</div>
            <div class="rowInput">
                <input type="input" class="rowInput2" ng-model="money" style="width: 140px"
                       ng-change="getBrokerage()"/>
                x <span class="num" ng-bind=" integralRate +  '%'"></span>
                <span ng-bind="((brokerage!=null && brokerage!='') || brokerage==0)?' = ':''"></span>
                <span ng-bind="brokerage"></span>
            </div>
        </div>
    </div>

    <%--<div class="sectionMain" ng-show="memberIsPay">--%>
        <%--<div class="mainRow mainRowLast lineHeight100 textSize30 textCenter errorRed"--%>
             <%--ng-bind="'¥ '+brokerage">--%>
        <%--</div>--%>
    <%--</div>--%>
    <form ng-submit="tradeType()" ng-show="memberIsPay">
    <%--<form ng-submit="tradeType(selectPay)" ng-show="memberIsPay">--%>
        <div class="sectionMain">
            <%--<div class="mainRow" ng-click="selectPay=3">--%>
                <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                    <%--<span class="icon-cshy-rmb2 iconfont textSize22 deepRed"></span>--%>
                    <%--余额支付--%>
                <%--</div>--%>
                <%--<div ng-show="isShowRecharge=='SUCCESS'" class="iconfont mainRowRight limeGreen" ng-class="{3:'icon-103'}[selectPay]"></div>--%>
                <%--<div ng-show="isShowRecharge=='FAIL'" class="mainRowRight" ng-click="goPage('/home/deposit/type/seller')">--%>
                    <%--余额不足,去充值--%>
                    <%--<span class="icon-right-1-copy iconfont grayaaa"></span>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="mainRow" ng-click="selectPay=10" ng-show="isWechat">--%>
                <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                    <%--<span class="icon-iconfontweixin iconfont textSize22 limeGreen"></span>--%>
                    <%--微信支付--%>
                <%--</div>--%>
                <%--<div class="iconfont mainRowRight limeGreen" ng-class="{10:'icon-103'}[selectPay]"></div>--%>
            <%--</div>--%>

            <%--<div class="mainRow" ng-click="selectPay=4" ng-show="!isWechat">--%>
                <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                    <%--<span class="icon-zhifubao iconfont textSize22 dodgerBlue"></span>--%>
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
        </div>
        <button type="submit" class="submitBtn" ng-disabled="submitBtnCheck">确认支付</button>
    </form>
    <div class="hideMenu" ng-show="active">
        <div class="errorMain otherMain" ng-show="activeStatus==true">
            <div class="otherMainRow icon-zhengque1 iconfont textSize50 limeGreen"></div>
            <div class="otherMainRow textSize25">交易成功</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到主页'"></div>
        </div>
        <div class="errorMain otherMain" ng-show="activeStatus==false">
            <div class="otherMainRow icon-cuowu iconfont textSize50 orangeRed3"></div>
            <div class="otherMainRow textSize22" ng-bind="activeText"></div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到现金交易页面'"></div>
        </div>
    </div>

    <div id="payForm">

    </div>

    <%--<div class="hideMenu" ng-show="menuCheck">--%>
        <%--<div class="enterPsw">--%>
            <%--<div class="textCenter lineHeight50">支付密码</div>--%>
            <%--<div class="textCenter textSize30 lineHeight40" ng-bind="'¥ '+brokerage"></div>--%>
            <%--<div class="enterInput">--%>
                <%--<input maxlength="1" type="password" ng-model="pwd1"/>--%>
                <%--<input maxlength="1" type="password" ng-model="pwd2"/>--%>
                <%--<input maxlength="1" type="password" ng-model="pwd3"/>--%>
                <%--<input maxlength="1" type="password" ng-model="pwd4"/>--%>
                <%--<input maxlength="1" type="password" ng-model="pwd5"/>--%>
                <%--<input maxlength="1" type="password" ng-model="pwd6"/>--%>
            <%--</div>--%>
            <%--<div class="enterInputDes">请输入6位支付密码</div>--%>
            <%--<div class="selectDiv">--%>
                <%--<div style="border-left: 0;border-bottom: 0" ng-click="closePwd()">取消</div>--%>
                <%--<div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="tradeType()">确认</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="hideMenu" ng-show="isGoPagePwd">--%>
        <%--<div class="errorMain">--%>
            <%--<div class="errorMainRow">您还没有设置支付密码</div>--%>
            <%--<div class="errorMainRow">--%>
                <%--<div class="errorMainBtn black" ng-click="isGoPagePwd=false">取消</div>--%>
                <%--<div class="errorMainBtn black" ng-click="goPage('/home/paySetPassword/userType/seller')">去设置--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
</div>