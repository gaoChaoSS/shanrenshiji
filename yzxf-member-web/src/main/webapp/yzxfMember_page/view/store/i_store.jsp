<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="mainRowLeft10 gray888" ng-hide="notShowSellerNum">
    共查询到 <span ng-bind="isNullZero(totalNum)" class="colorBlue"></span> 家商铺
</div>

<div class="isNullBox" ng-show="sellerList==null || sellerList==''">
    <div class="iconfont icon-meiyouneirong"></div>
</div>

<div class="sectionSeller" ng-repeat="item in sellerList track by $index">
    <img class="iconSeller1" style="max-width: 80px;"
         err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(getSellerIcon(item.icon,item.doorImg))}}"
         ng-click="goPage('/store/storeInfo/sellerId/'+item._id)"/>
    <div class="iconfont icon-dizhi location1" ng-bind="getDistance(item.distance)" ng-show="item.distance!=null"></div>
    <div class="sellerIntro">
        <div class="introTitle textEllipsis"  style="padding: 6px 0;"
             ng-click="goPage('/store/storeInfo/sellerId/'+item._id)">
            <span class="textSize16" ng-bind="isNullText2(item.name)" ng-click="goPage('/store/storeInfo/sellerId/'+item._id)"></span>
            <span class="gray888" ng-bind="(item.countOrder==null && item.countOrder!=0)?'':' (销量 '+isNullZero(item.countOrder)+')'"></span>
            <span class="gray888" ng-bind="item.countStar==null?'':' (评分 '+getMoney(isNullZero(item.countStar))+')'"></span>
        </div>
        <a href="tel:{{item.phone}}"><div class="icon-lianxi01 iconfont iconSeller3"></div></a>
        <div>
            <div>
                <span class="iconSeller2">积</span>
                <span class="gray888" ng-bind="' 积分率: '+isNullZero(item.integralRate)+'%'"></span>
            </div>
            <div class="sellerDesc textEllipsis" ng-bind="'商家类型: '+isNullText2(item.operateType)" class="gray888"></div>
        </div>
        <div class="sellerAddress textHightTwo" ng-click="goMap(locationName,item.address,item.latitude,item.longitude)"
             ng-bind="'地址: '+formatAddress(isNullText2((item.area==null?'':item.area)+(item.address==null?'':item.address)))"></div>
    </div>
</div>

<!--加载更多-->
<div class="loadMore">
    <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
         ng-click="more()"> 加载更多...
    </div>
    <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多商家</div>
</div>
