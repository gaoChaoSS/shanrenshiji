<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_coupon_Ctrl" class="d_content form_section title_section order_panel ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        <span ng-bind="pageName" class="textSize18"></span>
    </div>
    <div class="mainRowLeft10 gray888">
        共查询到 <span ng-bind="isNullZero(totalNumber)" class="colorBlue"></span> 条记录
    </div>
    <div class="isNullBox" ng-show="couponList==null || couponList==''">
        <div class="iconfont icon-meiyouneirong"></div>
        没有内容
    </div>
    <div class="overflowPC">
        <div ng-show="pathParams.sellerId!=null" class="couponList" ng-repeat="item in couponList">
            <div class="couponBk">
                <div class="couponImg floatLeft">
                    <div class="circularPhoto borderRed">
                        <img err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(item.sellerIcon)}}"/>
                    </div>
                </div>
                <div class="couponInfo floatLeft">
                    <div class="textEllipsis " style="font-size:16px;" ng-bind="item.couponName"></div>
                    <div class="textEllipsis " ng-bind="'商家:'+isSellerName(item.sellerName,item.sellerId)"
                         ng-show="pathParams.sellerId==null"></div>
                    <div class="couponTime notHigh" ng-class="pathParams.sellerId==null?'textSize12':'couponInfoLast'"
                         ng-bind="'有效期: '+showDate(item.startTime)+' 至 '+showDate(item.endTime)"></div>
                </div>
                <div class="couponSemiCircle floatLeft">
                    <div></div>
                    <div></div>
                </div>
                <div class="couponMoney floatLeft">
                    <div class="textSize18" ng-bind="'¥ '+ isNullZero(item.value)"></div>
                    <div class="textSize12" ng-bind="'满'+ isNullZero(item.condition) +'可用'"></div>
                </div>
                <div class="receivceWidth floatLeft" ng-click="receivce(item.$$isReceivce,item._id)"
                     ng-bind="pathParams.sellerId==null?'可使用':(item.$$isReceivce)?'已领取':'可领取'"
                     ng-class="item.$$isReceivce?'receivceTrue':'receivceFalse'"></div>
            </div>
        </div>
        <div ng-show="pathParams.sellerId==null" class="couponList" ng-repeat="item in couponList">
            <div class="couponBk">
                <div class="couponImg floatLeft">
                    <div class="circularPhoto borderRed" ng-click="goPage('/store/storeInfo/sellerId/'+item.sellerId)">
                        <img err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(item.sellerIcon)}}"/>
                    </div>
                    <div class="iconfont2 icon2-ccgl-yundansaomiao-2" style="text-align: center;font-size: 20px;color:#666"
                         ng-click="getQrcode(item.serial)"></div>
                </div>
                <div class="couponInfo floatLeft">
                    <div class="textEllipsis gray666 textSize16" ng-bind="item.couponName"></div>
                    <div class="textEllipsis grayaaa" ng-bind="'商家: '+isSellerName(item.sellerName,item.sellerId)"
                         ng-show="pathParams.sellerId==null" style="width: 130%;"></div>
                    <div class="textEllipsis grayaaa" ng-bind="'序号: '+item.serial" style="width:210px;"
                         ng-show="pathParams.sellerId==null"></div>
                    <div class="couponTime grayaaa" ng-class="pathParams.sellerId==null?'textSize12':'couponInfoLast'"
                         ng-bind="'有效期: '+showDate(item.startTime)+' 至 '+showDate(item.endTime)"></div>
                </div>
                <div class="couponSemiCircle floatLeft">
                    <div></div>
                    <div></div>
                </div>
                <div class="couponMoney floatLeft">
                    <div class="textSize18" ng-bind="'¥ '+ isNullZero(item.value)"></div>
                    <div class="textSize12" style="color:#000;" ng-bind="'满'+ isNullZero(item.condition) +'可用'"></div>
                </div>
                <div class="receivceWidth floatLeft" ng-click="receivce(item.$$isReceivce,item._id)"
                     ng-bind="item.canUse==null||item.canUse==false?'已使用':item.isOverdue?'已过期':'可使用'"
                     ng-class="item.canUse==null||item.canUse==false?'receivceTrue':item.isOverdue?'receivceTrue':'receivceFalse'"></div>
            </div>
        </div>
        <div class="loadMore" ng-show="totalNumber>0">
            <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
        <%--<div id="more" ng-click="more()" style="text-align: center;height: 30px; cursor: pointer;">加载更多</div>--%>
        <div class="hideMenu" ng-show="receivcePanel">
            <div class="errorMain">
                <div class="errorMainRow">是否领取</div>
                <div class="errorMainRow">
                    <div class="errorMainBtn dodgerBlue" ng-click="addMemberCoupon()">领取</div>
                    <div class="errorMainBtn errorRed" ng-click="receivcePanel=false">取消</div>
                </div>
            </div>
        </div>
    </div>

    <div ng-show="showQrCode" id="showQrCode" ng-click="setShowQrCode()"
         style="position:fixed;top:0;left:0;width:100%;height:100%;background:#fff;display: flex;align-items: center;justify-content: center;">
        <div></div>
    </div>
</div>