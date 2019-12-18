<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div>
    <div style="width:15%">商户编号</div>
    <div style="width:20%">商户名</div>
    <div style="width:20%">经营类别</div>
    <div style="width:15%">首页推荐</div>
    <div style="width:15%">美食广场</div>
    <div style="width:15%" ng-show="agent.level==1">积分支付</div>
</div>
<div ng-repeat="seller in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==seller._id?'selected':''"
    ng-click="dataPage.$$selectedItem=seller;">
    <div style="width:15%" ng-bind="seller._id"></div>
    <div style="width:20%" ng-bind="seller.name"></div>
    <div style="width:20%" ng-bind="seller.operateType"></div>
    <div style="width:15%" class="iconfont" ng-click="changeCommodityType(seller._id,seller.isRecommend,'isRecommend')" ng-class="seller.isRecommend?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
    <div style="width:15%" class="iconfont" ng-click="changeCommodityType(seller._id,seller.foodCourt,'foodCourt')" ng-class="seller.foodCourt?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
    <div style="width:15%" class="iconfont" ng-show="agent.level==1"
        ng-click="changeCommodityType(seller._id,seller.isOfflineBalance,'isOfflineBalance')"
        ng-class="seller.isOfflineBalance?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></div>
</div>