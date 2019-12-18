<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div class="popSection flex2">
    <div class="popTitle" style="margin-top:0">商品信息</div>
    <div>
        <span>商品名:</span>
        <span ng-bind="goodsInfo.name"></span>
    </div>
    <div >
        <span>原价:</span>
        <span ng-bind="'¥ '+goodsInfo.oldPrice"></span>
    </div>
    <div >
        <span>现价:</span>
        <span ng-bind="'¥ '+goodsInfo.salePrice"></span>
    </div>
    <div >
        <span>销量:</span>
        <span ng-bind="dataPage.$$selectedItem.saleCount"></span>
    </div>
    <div >
        <span>归属:</span>
        <span ng-bind="dataPage.$$selectedItem.sellerName"></span>
    </div>
    <div style="width:100%;white-space: inherit">
        <span>商品编号:</span>
        <span ng-bind="goodsInfo._id"></span>
    </div>
    <%--<div style="width:100%;white-space: inherit">--%>
        <%--<span>规格:</span>--%>
        <%--<div ng-repeat="spec in goodsInfo.spec" ng-bind="'【'+spec.name+' : '+spec.items+'】'"></div>--%>
    <%--</div>--%>
</div>
<div class="popSection flex2">
    <div class="popTitle" style="margin-top:0">商品规格</div>
    <div ng-repeat="spec in goodsInfo.spec" style="width:100%;white-space: inherit">
        <span ng-bind="spec.name+':'"></span>
        <span ng-bind="spec.items"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">商品信息</div>
    <div style="min-height:70px">
        <span style="min-width:100px">商品封面:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(goodsInfo.icon)}}"
             ng-click="showImgFun(goodsInfo.icon)"/>
    </div>
</div>

<div class="winCon" ng-click="closeImgFun()" ng-show="showImg!=''">
    <img class="winConImg" ng-src="{{iconImgUrl(showImg)}}"/>
</div>
