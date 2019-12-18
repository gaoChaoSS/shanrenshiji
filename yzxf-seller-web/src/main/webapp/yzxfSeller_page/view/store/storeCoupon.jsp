<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeCoupon_Ctrl"
     class="d_content title_section form_section index_page_globalDiv order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack black" ng-click="goBack()"></span>
        店铺卡券
        <span class="titleManage notHigh" ng-click="checkRightName=!checkRightName"  ng-bind="checkRightName?'完成':'删除'"></span>
    </div>

    <div class="overflowPC" style="margin-bottom: 100px;">
        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totalNumber)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="couponList==null || couponList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="couponList" ng-repeat="item in couponList">
            <div class="couponBk">
                <div class="couponImg floatLeft">
                    <div class="circularPhoto borderBlue">
                        <img err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(item.icon)}}"/>
                    </div>
                </div>
                <div class="couponInfo floatLeft">
                    <div class="textEllipsis grayaaa" ng-bind="item.name"></div>
                    <div class="textEllipsis grayaaa" ng-bind="'商家:'+item.sellerName"
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
                    <div class="textSize12" ng-bind="'满'+ isNullZero(item.condition) +'可用'"></div>
                </div>
                <div class="receivceWidth floatLeft" ng-click="receivce(item.$$isReceivce,item._id)"
                     ng-bind="item.isOverdue?'已过期':'有效期'" ng-show="!checkRightName"
                     ng-class="item.isOverdue?'receivceTrue':'receivceFalse'"></div>
                <div class="receivceWidth floatLeft receivceFalse" ng-click="delCoupon(item._id,$index)"
                      ng-show="checkRightName" style="line-height:25px;">点击删除</div>
            </div>
        </div>
        <div class="loadMore">
            <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
    <div class="hideCommodityBtn bgWhite rowBorderTop" ng-click="goPage('/store/addStoreCoupon')" ng-show="!checkRightName">
        <span class="icon-llalbumshopselectorcreate iconfont limeGreen textSize18"></span>
        <span class="notHigh">发布卡券</span>
    </div>

</div>