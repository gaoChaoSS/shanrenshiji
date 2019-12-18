<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeIntro_Ctrl" class="d_content title_section form_section">
    <div class="overflowPC positionRelative" style="overflow-x: hidden;">
        <div class="bk-hidden">
            <div class="bk-blur1" style="{{showBk}}"></div>
        </div>
        <span id="sellerNameText" style="display:none"
              ng-bind="isNullText2(intro.name)+'('+isNullText2(intro.operateType)+')'"></span>
        <div class="sectionMain2 textShadow1">
            <div class="icon-left-1 iconfont titleBack whitefff" ng-click="goBack()"></div>
            <div class="textCenter whitefff lineHeight50 textSize20">商家信息</div>
            <img class="headImg" err-src="/yzxfMember_page/img/notImg02.jpg"
                 ng-src="{{iconImgUrl(getSellerIcon(intro.icon,intro.doorImg))}}"/>
            <div class="mainRow2" style="width: calc(100% - 105px);left: 105px;">
                <div ng-show="!isScrollSellerName" ng-bind="isNullText2(intro.name)+'('+isNullText2(intro.operateType)+')'"
                         class="textEllipsis whitefff" style="flex:3"></div>
                <marquee ng-show="isScrollSellerName" ng-bind="isNullText2(intro.name)+'('+isNullText2(intro.operateType)+')'"
                         id="marquee" onMouseOut="this.start()" onMouseOver="this.stop()"
                         class="whitefff" style="flex:3"></marquee>
                <div ng-click="getQrcode()">
                    <span class="iconfont icon-erweima textSize20 whitefff"></span>
                </div>
            </div>
            <div class="mainRow2">
                <div style="flex:2">
                    <span class="icon-fu iconfont iconImg textSize20 " ng-class="intro.isOnlinePay?'whitefff':'grayccc'"></span>
                    <span class="mainRowLeft20 whitefff" ng-class="intro.isOfflineBalance?'whitefff':'grayccc'" ng-bind="'该商家'+(intro.isOfflineBalance?'':'不')+'支持余额付款'"></span>
                </div>
                <div class="whitefff" ng-bind="'积分率:'+(intro.integralRate==null?'暂无':intro.integralRate+' %')"></div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow" ng-click="showDoor=true">
                <div class="rowTitle">商家相册</div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">地址:</div>
                <div class="rowInput" style="line-height: 20px;padding:15px 0" ng-click="goMap(intro.address)"
                     ng-bind="isNullText2(isNullText(intro.area)+isNullText(intro.address))"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">电话号码:</div>
                <div class="rowInput" ng-bind="isNullText2(intro.phone)"></div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">营业时间:</div>
                <div class="rowInput" ng-bind="isNullText2(businessTime)"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">商家简介:</div>
                <div class="rowInput" ng-bind="isNullText2(intro.intro)" style="line-height: 20px;padding:15px 0"></div>
            </div>
        </div>

        <div class="hideMenu overflowHidden flexMod1" ng-show="myCard" ng-click="myCard=false">
            <div class="windowPanel1">
                <img class="headImg2" err-src="/yzxfMember_page/img/notImg02.jpg"
                     ng-src="{{iconImgUrl(getSellerIcon(intro.icon,intro.doorImg))}}"/>
                <div class="mainRow3">
                    <div ng-bind="isNullText2(intro.name)" class="textEllipsis"></div>
                    <div class="mainRowTop10 gray888" ng-bind="'积分率:'+(intro.integralRate==null?'暂无':intro.integralRate+' %')"></div>
                    <div class="gray888" ng-bind="isNullText2(intro.operateType)"></div>
                </div>
                <div class="qrcode"></div>
                <div class="textCenter mainRowTop10">扫描上方二维码图案</div>
                <div class="textCenter">查看商家信息</div>
            </div>
        </div>
    </div>

    <div ng-show="showMap" class="hideMenu" style="background:#fff;overflow: auto;">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff" ng-click="showMap=false"></span>
            地图
        </div>
        <div id="container" style="height:500px;margin-top: 10px;"></div>
    </div>
    <div ng-show="showDoor" class="hideMenu" style="background:#fff;overflow: auto;">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff" ng-click="showDoor=false"></span>
            店内照片
        </div>
        <img ng-repeat="doorImg in doorImgList" ng-src="{{iconImg(doorImg)}}"
                style="width:100%;margin:5px 0"/>
    </div>
</div>
