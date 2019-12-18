<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="mainRowLeft10 gray888 ">
    共查询到 <span ng-bind="isNullZero(totalGoodsNumber)" class="colorBlue"></span> 件商品
</div>

<div class="isNullBox" ng-show="goodsList==null || goodsList==''">
    <div class="iconfont icon-meiyouneirong"></div>
</div>

<div style="overflow: hidden;position: relative;background-color: #fff;margin-bottom: 10px;" ng-repeat="goods in goodsList"
     ng-click="goPage('/store/commodity/goodsId/'+goods._id+'/_id/'+goods.sellerId)">
    <img style="position: absolute;left:8px;top:15px;" class="iconProduct" err-src="/yzxfMember_page/img/notImg02.jpg"
         ng-src="{{iconImgUrl(goods.icon)}}"/>
    <div style="padding: 10px 0 0 120px;min-height: 120px;">
        <div class="introTitle textEllipsis" ng-bind="goods.name"></div>

        <div>
            <span class="iconSeller2">积</span>
            <span class="gray888 ng-binding" ng-bind="' 积分率: '+isNullZero(goods.integralRate)+'%'"></span>
        </div>
        <div>
            <%--<span class="iconSeller2">养老金</span>--%>
            <span class="gray888 ng-binding" ng-bind="'养老金:'+getPension(goods.salePrice,goods.integralRate)+'元'"></span>
        </div>
        <div class="mainRowTop10" ng-show="goods.oldPrice!=0 && goods.oldPrice!=null">
            <del ng-bind="'原价: '+isNullZero(goods.oldPrice)+'元'" class="gray888"></del>
        </div>
        <div class="gray888" ng-bind="'销量: '+isNullZero(goods.saleCount)"></div>
        <button class="button highL" style="position: absolute; right: 5px; bottom: 10px"
                ng-click="goPage('/store/commodity/goodsId/'+goods._id+'/_id/'+goods.sellerId)">立即抢购
        </button>
        <div ng-bind="'¥ '+isNullZero(goods.salePrice)" class="fr colorRed1"
             style="font-size: 25px;position: absolute; right: 10px; top: 32px"></div>
        <%--<div class="overflowH">--%>
            <%--<div class="tap1 fl" style="position:static;margin:0 2px">天天特价</div>--%>
            <%--<div class="tap1 fl" style="position:static;margin:0 2px">公益专区</div>--%>
        <%--</div>--%>
    </div>
</div>

<div class="loadMore" ng-show="totalGoodsNumber>0">
    <div id="moreGButton" ng-show="isGoodsLoadMore&&totalGoodsNumber>0" style="font-size: 15px;color: #999;"
         ng-click="moreGoods()">加载更多...
    </div>
    <div ng-show="!isGoodsLoadMore" style="font-size: 13px;color: #999;padding:8px;">没有更多了</div>
</div>