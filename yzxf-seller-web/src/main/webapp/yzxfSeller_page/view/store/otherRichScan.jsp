<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<form ng-controller="store_otherRichScan_Ctrl" class="d_content title_section form_section order_panel"
      ng-submit="produce2DCode()">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/store')"></span>
        互联网收款
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">金额</div>
            <div class="rowInput">
                <input type="text" class="rowInput2" placeholder="请输入会员消费金额"
                       ng-model="memberConsumption" style="width:140px;" ng-change="getBrokerage()"/>
                <%--x <span class="num" ng-bind=" integralRate +  '%'"></span>--%>
                <%--<span ng-bind="((brokerage!=null && brokerage!='') || brokerage==0)?' = ':''"></span><span ng-bind="brokerage"></span>--%>
            </div>
        </div>
    </div>
    <form ng-submit="produce2DCode(selectPay)" ng-show="memberIsPay">
        <div class="sectionMain">
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
        </div>
        <button type="submit" class="submitBtn" ng-disabled="submitBtnCheck">下一步</button>
    </form>
</form>