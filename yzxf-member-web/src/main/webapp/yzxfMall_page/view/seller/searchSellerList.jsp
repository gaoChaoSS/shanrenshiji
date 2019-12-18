<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="seller_searchSellerList_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--轮播图--%>
    <div class="navCarousel" ng-include="navCarousel"></div>

    <div class="bodyWidth marginAuto" style="overflow:hidden">
        <%--美食--%>
        <%--图标标题--%>
        <div class="isNullBox" ng-show="dataPage.sellerList==null || dataPage.sellerList==''">
            <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
            没有内容
        </div>
        <div class="sellerListDiv floatL" ng-repeat="s in dataPage.sellerList"
             ng-click="openPage('/seller/sellerInfo/sellerId/'+s._id)" style="margin: 20px 10px;">
            <%--商品图片--%>
            <div id="sellerTIm" style="background: url('{{iconImgUrl(getSellerIcon(s.icon,s.doorImg))}}') no-repeat;background-size: 100% 100%;border: 1px solid #DFDFDF;"></div>
            <%--名字--%>
            <div class="sellerListName">
                <div ng-bind="s.name" class="textEllipsis"></div>
                <div ng-bind="s.intro" class="textEllipsis"></div>
            </div>
            <div class="textEllipsis">
                <span class="iconfont icon-dizhi"></span>
                <span ng-bind="s.area+s.address"></span>
            </div>
            <div>
                <span class="sellerJiFenIcon">积</span>
                <span style="color: #ff0e0c" ng-bind="s.integralRate">1</span>
                <span style="color: #ff0e0c">%</span>
            </div>
        </div>
    </div>

    <form class="pageMain">
        <div class="sectionPage" ng-show="dataPage.totalNum>0">
            <div style="margin: 0 auto;">
                <div class="btn3" ng-click="pageNext1(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber1(1)"  ng-show="dataPage.pageNo>4"></div>
                <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                     ng-click="pageNumber1(i)"
                     ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                <div class="btn3" ng-bind="dataPage.totalPage"
                     ng-click="setPageNo1(dataPage.totalPage);"
                     ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                <div class="btn3" ng-click="pageNext1(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                    下一页
                </div>
                <div class="pageGo"
                     ng-show="dataPage.totalPage>1">
                    <input type="text" placeholder="跳转" ng-model="pageGo">
                    <button class="iconfont icon-right-1-copy" ng-click="pageGoFun1(pageGo)"></button>
                </div>
            </div>
        </div>
    </form>

    <%--底部模板--%>
    <div class="marginTop30" ng-include="mallBottom"></div>
</div>