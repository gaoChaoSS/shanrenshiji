<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="seller_sellerInfo_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中部内容--%>
    <div class="bodyWidth marginZAuto">
        <%--商家信息--%>
        <div class="sellerInfoDiv">
            <div class="floatL">
                <img ng-src="{{iconImgUrl(getSellerIcon(sellerInfo.icon,sellerInfo.doorImg))}}" alt="">
            </div>
            <div class="floatL">
                <div>
                    <span class="" ng-bind="sellerInfo.name"></span>
                    <span class="sellerJiFenIcon font14px">积</span>
                    <span class="font14px gray888" ng-bind="sellerInfo.integralRate+'%'"></span>
                </div>
                <div style="margin-top: 10px;color: #888;line-height: 25px;padding-bottom: 40px;" ng-bind="sellerInfo.intro"></div>
                <div style="position: absolute;bottom: 20px;color:#888" ng-bind="'地址: '+sellerInfo.area+sellerInfo.address"></div>
            </div>
            <div class="floatL RePosition">
                <div class="iconfont AbPosition" ng-class="isCollection?'icon-tubiao2223 pinkColor':'icon-xihuan pinkColor'" ng-click="addOrDelStoreCollection()"></div>
                <div class="AbPosition">收藏商家</div>
            </div>
            <div style="clear: left">
                <div ng-repeat="event in eventList"><button ng-bind="event.name">满促</button><span class="pinkColor" ng-bind="event.content"></span></div>
            </div>
        </div>
        <%--优惠券列表--%>
        <div class="positionR" ng-show="couponTotalPage!=0">
            <div class="iconfont icon-left-1 btn-left1" ng-click="setCouponPageNo(-1)" ng-show="couponPageNo>1"></div>
            <div class="sellerCouponDiv flex3">
                <div class="sellerCouponInfo RePosition floatL" ng-repeat="c in couponList">
                    <div class="flex1 AbPosition">
                        <span>¥</span>
                        <span style="font-size: 30px;margin: 0 4px;" ng-bind="c.value"></span>
                        <span style="font-size: 22px;">优惠券</span>
                    </div>
                    <div class="flex1 AbPosition pointer" ng-show="!c.$$isReceivce" ng-click="addMemberCoupon(c._id)">立即领取</div>
                    <div class="flex1 AbPosition" ng-show="c.$$isReceivce" >已领取</div>
                    <div class="flex1 AbPosition">(满<span ng-bind="c.condition"></span>使用)</div>
                </div>
            </div>
            <div class="iconfont icon-right-1-copy btn-left1" style="left:auto;right:0"
                 ng-click="setCouponPageNo(1)" ng-show="couponPageNo<couponTotalPage"></div>
        </div>

        <%--切换按钮--%>
        <div class="sellerTitle" style="background-color: #F6F6F6;height: 50px;margin-bottom: 20px;">
            <div class="pointer floatL flex1" ng-click="goodsAsComment='goods';clearFun();getGoodsList()" ng-class="goodsAsComment=='goods'?'clickDiv':''">商品</div>
            <div class="pointer floatL flex1" ng-click="goodsAsComment='comment';clearFun();getStoreCommentList()" ng-class="goodsAsComment=='comment'?'clickDiv':''">评价</div>
        </div>
        <%--商品列表--%>
        <div class="commodityListDiv" ng-show="goodsAsComment=='goods'">
            <div class="isNullBox" ng-show="goodsList==null || goodsList==''">
                <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                没有内容
            </div>
            <div class="commodityDiv floatL" ng-repeat="g in goodsList"  ng-click="goPage('/seller/commodityInfo/goodsId/'+g._id)">
                <%--商品图片--%>
                <div style="background: url({{iconImgUrl(g.icon)}}) no-repeat;background-size: 100% 95%;"></div>
                <%--价格--%>
                <div>
                    <div class="floatL" style="color: #ff0e0c;font-size: 30px" ng-bind="'¥'+getMoney(g.salePrice)"></div>
                    <del class="floatR" style="color: #929292;margin-top: 14px;" ng-bind="'¥'+getMoney(g.oldPrice)"></del>
                </div>
                <%--商品名--%>
                <div class="flex1 textHightTwo" style="margin: 5px 0px;height: auto;" ng-bind="g.name">
                </div>
                <%--销量养老金--%>
                <div class="flex1 commodityTongji">
                    <div class="floatL" style="color: #929292;">总销量<span style="color:#B47E5C;" ng-bind="getMoney(g.saleCount)"></span></div>
                    <div class="floatL" style="color: #929292;">养老金<span style="color: #ff0e0c" ng-bind="'¥'+pension(sellerInfo.integralRate,g.salePrice)"></span></div>
                </div>
            </div>

        </div>
            <form class="pageMain"
                  ng-submit="getGoodsList();">
                <div class="sectionPage" ng-show="dataPage.totalNum>0&&goodsAsComment=='goods'">
                    <div style="margin: 0 auto;">
                        <div class="btn3" ng-click="pageNextC(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumberC(1)"  ng-show="dataPage.pageNo>4"></div>
                        <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                        <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                             ng-click="pageNumberC(i)"
                             ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                        <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                        <div class="btn3" ng-bind="dataPage.totalPage"
                             ng-click="setPageNoC(dataPage.totalPage);"
                             ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                        <div class="btn3" ng-click="pageNextC(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                            下一页
                        </div>
                        <div class="pageGo"
                             ng-show="dataPage.totalPage>1">
                            <input type="text" placeholder="跳转" ng-model="pageGo">
                            <button class="iconfont icon-right-1-copy" ng-click="pageGoFunC(pageGo)"></button>
                        </div>
                    </div>
                </div>
            </form>
        <%--评论列表--%>
        <div class="commentListDiv" ng-show="goodsAsComment=='comment'">
            <div class="isNullBox" ng-show="commentList==null || commentList==''">
                <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                没有内容
            </div>
            <div class="commentList" ng-repeat="cl in commentList">
                <div class="floatL">
                    <img ng-src="{{iconImgUrl(cl.memberIcon)}}" alt="">
                    <div ng-bind="formatMobile(cl.mobile)"></div>
                </div>
                <div class="floatL">
                    <div ng-bind="showDate(cl.createTime)"></div>
                    <div ng-bind="cl.storeComment"></div>
                </div>
                <div class="floatL">
                    <div>
                        <span>总体评价</span>
                        <span ng-bind="starNo(cl.serviceStar)"></span>
                    </div>
                </div>
            </div>
        </div>
            <form class="pageMain"
                  ng-submit="getStoreCommentList();">
                <div class="sectionPage" ng-show="dataPage.totalNum>0&&goodsAsComment=='comment'">
                    <div style="margin: 0 auto;">
                        <div class="btn3" ng-click="pageNextS(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumberS(1)"  ng-show="dataPage.pageNo>4"></div>
                        <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                        <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                             ng-click="pageNumberS(i)"
                             ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                        <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                        <div class="btn3" ng-bind="dataPage.totalPage"
                             ng-click="setPageNoS(dataPage.totalPage);"
                             ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                        <div class="btn3" ng-click="pageNextS(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                            下一页
                        </div>
                        <div class="pageGo"
                             ng-show="dataPage.totalPage>1">
                            <input type="text" placeholder="跳转" ng-model="pageGo">
                            <button class="iconfont icon-right-1-copy" ng-click="pageGoFunS(pageGo)"></button>
                        </div>
                    </div>
                </div>
            </form>
    </div>
    <%--底部模板--%>
    <div class="marginTop30" ng-include="mallBottom"></div>
</div>