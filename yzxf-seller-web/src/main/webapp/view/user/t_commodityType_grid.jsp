<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div>
    <div style="width:20%">店铺</div>
    <div style="width:20%">商品名</div>
    <div style="width:10%">商品类别</div>
    <div style="width:10%">销量</div>
    <div style="width:10%">商城:特价商品</div>
    <div style="width:10%">热门商品</div>
    <div style="width:10%">商城:公益商品</div>
    <div style="width:10%">会员:置顶热门商品</div>
</div>
<div ng-repeat="commodity in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==commodity._id?'selected':''"
    ng-click="dataPage.$$selectedItem=commodity;">
    <div style="width:20%" ng-bind="commodity.sellerName"></div>
    <div style="width:20%" ng-bind="commodity.commodityName"></div>
    <div style="width:10%" ng-bind="commodity.operateType"></div>
    <div style="width:10%" ng-bind="commodity.saleCount"></div>
    <div style="width:10%" class="iconfont" ng-click="changeCommodityType(commodity._id,commodity.te,'tejia')" ng-class="commodity.te?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
    <div style="width:10%" class="iconfont" ng-click="changeCommodityType(commodity._id,commodity.hot,'remen')" ng-class="commodity.hot?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
    <div style="width:10%" class="iconfont" ng-click="changeCommodityType(commodity._id,commodity.gongyi,'gongyi')" ng-class="commodity.gongyi?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
    <div style="width:10%" class="iconfont" ng-click="changeCommodityType(commodity._id,commodity.isIndexCommodity,'isIndexCommodity')" ng-class="commodity.isIndexCommodity?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
</div>