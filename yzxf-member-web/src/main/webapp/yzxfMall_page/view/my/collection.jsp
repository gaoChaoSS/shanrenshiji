<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_collection_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto" style="overflow: hidden">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" style="height: 382px;" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myOrderRightContent">
            <div class="orderTitle">
                <div class="floatL flex1" ng-click="goodsAsSeller='goods';clearFun();getCollectionList();" ng-class="goodsAsSeller=='goods'?'clickBtn':''">商品</div>
                <div class="floatL flex1" ng-click="goodsAsSeller='seller';clearFun();getSellerList();" ng-class="goodsAsSeller=='seller'?'clickBtn':''">店铺</div>
            </div>
            <div ng-show="goodsAsSeller=='goods'">
                <div class="isNullBox" ng-show="collectionList==null || collectionList==''">
                    <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                    没有内容
                </div>
                <div ng-show="collectionList!=null && collectionList!=''">
                    <table class="orderContent">
                        <tr class="orderTableTitle">
                            <td class="" style="width:35%;padding-left: 18px;">商品信息</td>
                            <td class="" style="width:20%;text-align: center">价格</td>
                            <td class="" style="width:20%;text-align: center">原价</td>
                            <td class="" style="width:25%;text-align: center">操作</td>
                        </tr>
                        <tr class="orderTableList" ng-repeat="goods in collectionList">
                            <td class="RePosition" style="width:35%;height: 120px;cursor:pointer" ng-click="goPage('/seller/commodityInfo/goodsId/'+goods.goodsId)">
                                <img class="AbPosition orderListImg" style="width: 100px;height: 100px" ng-src="{{iconImgUrl(goods.productIcon)}}" alt="">
                                <div class="AbPosition orderGoodsName" ng-bind="goods.name"></div>
                            </td>
                            <td class="" style="font-size:14px;height: 120px;width:20%;text-align: center" ng-bind="'¥'+isNullZero(goods.salePrice)"></td>
                            <td class="" style="font-size:14px;height: 120px;width:20%;text-align: center;color: #ff933c;" ng-bind="'¥'+isNullZero(goods.oldPrice)"></td>
                            <td class="" style="width:25%;text-align: center;height: 120px;">
                                <div style="margin-bottom: 20px;font-size: 15px;cursor:pointer;" ng-click="deleteCollection('goods',goods.goodsId)" ><img src="/yzxfMall_page/img/delete.png" alt="">删除</div>
                            </td>
                        </tr>
                    </table>
                </div>
                <form class="pageMain">
                    <div class="sectionPage" ng-show="dataPage.totalNum>0">
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
            </div>
            <div ng-show="goodsAsSeller=='seller'">
                <div class="isNullBox" ng-show="sellerList==null || sellerList==''">
                    <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                    没有内容
                </div>
                <div ng-show="sellerList!=null && sellerList!=''">
                    <table class="orderContent" ng-repeat="seller in sellerList">
                        <tr class="orderTableTitle">
                            <td class="" style="width:35%;padding-left: 18px;">店铺信息</td>
                            <td class="" style="width:18%;text-align: center">店名</td>
                            <td class="" style="width:17%;text-align: center">积分率</td>
                            <td class="" style="width:30%;text-align: center">操作</td>
                        </tr>
                        <tr class="orderTableList">
                            <td class="RePosition" style="width:35%;height: 120px;cursor:pointer" ng-click="goPage('/seller/sellerInfo/sellerId/'+seller.sellerId)">
                                <img class="AbPosition orderListImg" style="width: 100px;height: 100px"  ng-src="{{iconImgUrl(getSellerIcon(seller.icon,seller.doorImg))}}" alt="">
                                <div class="AbPosition orderGoodsName" ng-bind="seller.name"></div>
                            </td>
                            <td class="" style="font-size:14px;height: 120px;width:18%;text-align: center;color: #138bbe;" ng-bind="seller.sellerName"></td>
                            <td class="" style="font-size:14px;height: 120px;width:17%;text-align: center;color: #138bbe;" ng-bind="seller.integralRate+'%'"></td>
                            <td class="" style="width:30%;text-align: center;height: 120px;">
                                <div style="margin-bottom: 20px;font-size: 15px;cursor:pointer"  ng-click="deleteCollection('seller',seller.sellerId)"><img style="margin-right: 10px" src="/yzxfMall_page/img/delete.png" alt="">删除</div>
                            </td>
                        </tr>
                    </table>
                </div>
                <form class="pageMain">
                    <div class="sectionPage" ng-show="dataPage.totalNum>0">
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
                                <button class="iconfont icon-right-1-copy" ng-click="pageGoFunC(pageGo)"></button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

        </div>
    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>