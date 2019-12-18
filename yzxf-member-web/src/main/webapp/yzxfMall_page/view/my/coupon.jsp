<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_coupon_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myCouponDiv" ng-show="isHaveCoupon">
            <div class="couponInfo floatL RePosition" ng-repeat="cpl in couponList" ng-class="cpl.canUse==null||cpl.canUse==false?'couponInfoBGIUSE':'couponInfoBGIOK'">
                <div class="AbPosition">¥<span ng-bind="cpl.value"></span></div>
                <div class="AbPosition">卡券:<span ng-bind="cpl.couponName"></span></div>
                <div class="AbPosition">序列号:<span ng-bind="cpl.serial"></span></div>
                <div class="AbPosition">使用条件:<span ng-bind="'满'+cpl.condition+'可用'"></span></div>
                <div class="AbPosition">使用日期:<span ng-bind="showDate(cpl.startTime)+'至'+showDate(cpl.endTime)">2017-03-02至2017-03-20</span></div>
                <div class="AbPosition">店铺:<span ng-bind="isSellerName(cpl.sellerName,cpl.sellerId)">港仔文艺店</span></div>
            </div>
        </div>
            <form class="pageMain"
                  ng-submit="getCouponList();">
                <div class="sectionPage" ng-show="dataPage.totalNum>0">
                    <div style="margin: 0 auto;">
                        <div class="btn3" ng-click="pageNext(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"  ng-show="dataPage.pageNo>4"></div>
                        <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                        <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                             ng-click="pageNumber(i)"
                             ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                        <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                        <div class="btn3" ng-bind="dataPage.totalPage"
                             ng-click="setPageNo(dataPage.totalPage);"
                             ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                        <div class="btn3" ng-click="pageNext(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                            下一页
                        </div>
                        <div class="pageGo"
                             ng-show="dataPage.totalPage>1">
                            <input type="text" placeholder="跳转" ng-model="pageGo">
                            <button class="iconfont icon-right-1-copy" ng-click="pageGoFun(pageGo)"></button>
                        </div>
                    </div>
                </div>
            </form>
        <div class="floatL myCouponDiv" ng-show="!isHaveCoupon">
            <div class="noCouponDiv floatL flex1">
                <div style="background: url('/yzxfMall_page/img/noCoupon.png') no-repeat;width: 40%;height: 60%"></div>
            </div>
            <div class="noCouponDiv floatL">
                <div class="noCouponTxt RePosition">
                    <div class="AbPosition">您还没有任何优惠券哦</div>
                    <div class="AbPosition pointer" ng-click="goPage('/home/index')">去首页逛逛</div>
                </div>
                <div style="background: url('/yzxfMall_page/img/noCoupon1.png') no-repeat;width: 60%;height: 60%;border-top: 2px solid #DFDFDF;background-position-y: 30px; "></div>
            </div>
        </div>


    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>