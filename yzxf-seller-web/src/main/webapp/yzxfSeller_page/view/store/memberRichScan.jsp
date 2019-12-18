<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<form ng-controller="store_memberRichScan_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/store')"></span>
        会员扫码支付
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">账号</div>
            <input type="text" class="rowInput" placeholder="请输入会员卡号/手机号/身份证号" ng-model="memberCard"
                   ng-blur="getMemberInfo()"/>
        </div>
    </div>
    <div ng-show="memberIsPay">
        <div class="commentList" ng-show="memberCheck">
            <div class="commentMain paddingBottomNot">
                <div class="headAtAmong">
                    <div class="circularPhoto">
                        <img src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(memberInfo.icon)}}"/>
                    </div>
                </div>
                <div class="mainRow2 mainRowTop10" ng-show="memberInfo.realName!=null">
                    <div>
                        <div class="textTracking gray666">名称</div>
                    </div>
                    <div class="gray666" ng-bind="': '+memberInfo.realName"></div>
                </div>
                <div class="mainRow2 mainRowBottom10 gray666" ng-show="memberInfo.cardNo!=null">
                    <div>
                        <div class="textTracking gray666">账号</div>
                    </div>
                    <div class="gray666" ng-bind="': '+memberInfo.cardNo"></div>
                </div>

                <div class="mainRow2 mainRowTop10" ng-show="memberInfo.realName==null && memberInfo.cardNo==null && memberInfo.isFree">
                    <div style="text-align:center">免费会员</div>
                </div>
            </div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">金额</div>
            <div class="rowInput">
                <input type="text" class="rowInput2" placeholder="请输入会员消费金额"
                       ng-model="memberConsumption" style="width:140px;" ng-change="getBrokerage()"/>
                x <span class="num" ng-bind=" integralRate +  '%'"></span>
                <span ng-bind="((brokerage!=null && brokerage!='') || brokerage==0)?' = ':''"></span><span ng-bind="brokerage"></span>
            </div>
        </div>
    </div>
    <div class="sectionMain" ng-show="memberIsPay">
        <%--<div class="mainRow" ng-click="selectPay=10">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="icon-iconfontweixin iconfont textSize22 limeGreen"></span>--%>
                <%--微信支付--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{10:'icon-103'}[selectPay]"></div>--%>
        <%--</div>--%>
        <%--<div class="mainRow" ng-click="selectPay=4">--%>
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
        <%--<div class="mainRow" ng-click="selectPay=3" ng-show="sellerInfo.isOfflineBalance!=null--%>
            <%--&& sellerInfo.isOfflineBalance!='' && sellerInfo.isOfflineBalance">--%>
            <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                <%--<span class="iconfont2 icon2-zhifu textSize22 dodgerBlue"></span>--%>
                <%--发起订单,会员余额付款--%>
            <%--</div>--%>
            <%--<div class="iconfont mainRowRight limeGreen" ng-class="{3:'icon-103'}[selectPay]"></div>--%>
        <%--</div>--%>
    </div>
    <button type="submit" class="submitBtn" ng-disabled="submitBtnCheck" ng-click="produce2DCode()">下一步</button>
    <%--<button type="submit" class="submitBtn" ng-disabled="submitBtnCheck" ng-click="produce2DCode('memberPay')">发起订单,会员余额付款</button>--%>

    <div class="hideMenu" ng-if="isRepeat">
        <div class="errorMain">
            <div class="errorMainRow">已存在一个未支付的订单</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="setFlag('isRepeat')">取消</div>
                <div class="errorMainBtn black" ng-click="goPage('/store/depositSuccess/orderNo/'+orderTemp.orderNo+'/payType/3')">进入订单</div>
            </div>
        </div>
    </div>

    <div class="sectionMain" ng-show="s2DCode" style="text-align: center;">
        <img src="/yzxfSeller_page/img/2DCode.png" alt="" style="width: 70%;margin-top: 5px">
    </div>
</form>