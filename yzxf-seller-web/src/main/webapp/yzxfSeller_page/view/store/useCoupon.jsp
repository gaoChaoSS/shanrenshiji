<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易扫码--%>
<div ng-controller="store_useCoupon_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        卡券核销
    </div>
    <div class="overflowPC" id="overflowCoupon">
        <form ng-submit="submitBtn()">
            <div class="commentList">
                <div class="mainRow">
                    <input type="text" class="lineHeight50 mainRowLeft0" style="width:100%;padding:0" placeholder="请输入卡券序列号"
                           ng-model="couponNo"/>
                    <%--<div ng-show="!isWechat" ng-click="scanQrCode()"--%>
                            <%--style="position:absolute;right:20px;top:10px;height:30px;line-height:30px;padding:0 20px;background:#eee;border-radius:30px;color: #888;">扫一扫</div>--%>
                </div>
            </div>

            <button type="submit" class="submitBtn">确认使用</button>
        </form>
        <div class="mainRow">
            <div class="rowTitle widthPercent20 textSize15 lineHeight50">核销记录</div>
        </div>
        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totalNumber)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="useCouponList==null || useCouponList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="mainRowTop10" ng-repeat="item in useCouponList">
            <div class="bgWhite paddingBottomNot borderBottomGray">
                <div class="mainRow mainRowNotBorderBottom">
                    <div class="rowTitle widthPercent20">用户名 :</div>
                    <div class="rowInput black"
                         ng-bind="(item.realName==null||item.realName=='')?'匿名':item.realName"></div>

                </div>
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">卡券序列号 :</div>
                    <div class="rowInput black" ng-bind="item.serial" style="padding-left: 20px;"></div>
                </div>
            </div>
            <div class="overflowHidden lineHeight50 textIndent30 textRight bgWhite">
                <span class="mainRowRight10">使用时间 : </span>
                <span class="mainRowRight10" ng-bind="showYFullTime(item.useTime)"></span>
            </div>
        </div>
        <div class="loadMore">
            <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>