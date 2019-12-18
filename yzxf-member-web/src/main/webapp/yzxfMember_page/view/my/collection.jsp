<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_collection_Ctrl" class="d_content title_section order_panel form_section">
    <div class="title mainRowBottom10" id="morePTop">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        <div class="titleTwo">
            <span class="titleText titleTextTwo" ng-class="{true:'titleTextCheckRed'}[titleCheck]"
                  ng-click="getCollectionList('first')">商品</span>
            <span class="titleText titleTextTwo" ng-class="{false:'titleTextCheckRed'}[titleCheck]"
                  ng-click="getSellerList('first')">商家</span>
        </div>
    </div>
    <div class="overflowPC">
        <div ng-show="titleCheck==true" id="goodsCollection">
            <div class="mainRowLeft10 gray888" ng-hide="notShowSellerNum">
                共查询到 <span ng-bind="isNullZero(totalNumer)" class="colorBlue"></span> 条记录
            </div>
            <div class="isNullBox" ng-show="collectionList==null || collectionList==''">
                <div class="iconfont icon-meiyouneirong"></div>
                没有内容
            </div>
            <div class="commodity" ng-repeat="itemGoods in collectionList"
                 ng-click="goPage('/store/commodity/goodsId/'+itemGoods.entityId+'/_id/'+itemGoods.sellerId)">
                <div class="commodityInfo">
                    <div class="commodityImg">
                        <div>
                            <img err-src="/yzxfMember_page/img/notImg02.jpg"
                                 ng-src="{{iconImgUrl(itemGoods.productIcon)}}">
                        </div>
                    </div>
                    <div class="commodityText">
                        <div class="textHightTwo gray333" ng-bind="itemGoods.name"></div>
                        <div>
                            <del class="grayaaa" ng-bind="'原价 '+isNullZero(itemGoods.oldPrice)+' 元'"></del>
                            <div class="grayaaa">现价 <span class="textSize18 errorRed"
                                                          ng-bind="isNullZero(itemGoods.salePrice)+' 元'"></span></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="loadMore" ng-show="totalNumer>0">
                <div id="morePButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                     ng-click="moreGoods()">加载更多...
                </div>
                <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
            </div>
        </div>

        <div ng-show="titleCheck==false">
            <div class="mainRowLeft10 gray888">
                共查询到 <span ng-bind="isNullZero(totalSNumer)" class="colorBlue"></span> 条记录
            </div>
            <div class="isNullBox" ng-show="sellerList==null || sellerList==''">
                <div class="iconfont icon-meiyouneirong"></div>
                没有内容
            </div>
            <div class="sectionMain submitBtnNotMargin store_panel">
                <div class="sectionSeller" ng-repeat="item in sellerList"
                     ng-click="goPage('/store/storeInfo/sellerId/'+item.sellerId)">
                    <img class="iconSeller1" err-src="/yzxfMember_page/img/notImg02.jpg"
                         ng-src="{{iconImgUrl(getSellerIcon(item.icon,item.doorImg))}}"
                         ng-click="goPage('/store/storeInfo/sellerId/'+item.sellerId)"/>
                    <div class="sellerIntro">
                        <div class="introTitle textEllipsis" ng-bind="isNullText2(item.sellerName)"
                             ng-click="goPage('/store/storeInfo/sellerId/'+item.sellerId)"></div>
                        <div class="icon-lianxi01 iconfont iconSeller3"></div>
                        <div>
                            <div>
                                <span class="iconSeller2">积</span>
                                <span class="gray888" ng-bind="' 积分率: '+isNullZero(item.integralRate)+'%'"></span>
                            </div>
                            <div class="sellerDesc textEllipsis" ng-bind="'商家描述: '+isNullText2(item.intro)"
                                 class="gray888"></div>
                        </div>
                        <div class="sellerAddress textEllipsis"
                             ng-bind="'地址: '+isNullText2((item.area==null?'':item.area)+(item.address==null?'':item.address))"></div>
                    </div>
                </div>
            </div>
            <div class="loadMore" ng-show="totalSNumer>0">
                <div id="moreButton" ng-show="isSLoadMore&&totalSNumer>0" style="font-size: 15px;color: #999;"
                     ng-click="more()">加载更多...
                </div>
                <div ng-show="!isSLoadMore" style="font-size: 13px;color: #999;padding:8px ;">没有更多了</div>
            </div>
        </div>
    </div>
</div>