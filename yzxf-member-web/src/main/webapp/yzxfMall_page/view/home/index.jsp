<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_index_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead" id="head"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--轮播图--%>
    <div class="navCarousel" ng-include="navCarousel" style="z-index: 1"></div>

    <div class="bodyWidth marginAuto" id="mainBody" ng-include="indexMain"></div>

    <%--底部模板--%>
    <div class="marginTop30" ng-include="mallBottom"></div>

    <%--左边定位导航--%>
    <div class="indexLeftNavDiv" id="indexLeftNavDiv" ng-class="getNavClass()">
        <div class="iconfont icon-fanhuidingbu navBtn-mini" id="hideLeftNavBtn"></div>
        <a ng-repeat="mdl in operate" href="{{'#nav'+mdl.name}}"
           ng-mouseenter="mouseNavUp($index)" ng-mouseleave="mouseNavDown()">
            <div ng-show="txtOrImg!=$index"><img src="{{iconImg(mdl.mallImg)}}" alt=""></div>
            <div ng-show="txtOrImg==$index" ng-bind="mdl.name" class="navBtn-Text"></div>
        </a>
    </div>
    <%--右边定位二维码--%>
        <%--<div class="indexRightNavDiv">--%>
            <%--<div ng-show="!txtIsHover" class="indexRightNavTopN iconfont icon-icon19" style="font-size: 22px;" ng-mouseenter="goBackTop()"></div>--%>
            <%--<div class="indexRightNavTopY" ng-show="txtIsHover" ng-mouseleave="goBackTopNo()">--%>
                <%--<div style="margin-top: -5px"  class="goTop"><a>返回</a></div>--%>
                <%--<div style="margin-top: -2px;" class="goTop"><a>顶部</a></div>--%>
            <%--</div>--%>
            <%--<div class="indexRightNav2DCN" ng-show="!isHover" ng-mouseenter="codeClick()"><img src="/yzxfMall_page/img/2DCode01.png" alt=""></div>--%>
            <%--<div class="indexRightNav2DCY" ng-show="isHover" ng-mouseleave="codeClickNo()"><img src="/yzxfMall_page/img/2DCode02.png" alt=""></div>--%>
            <%--<div class="indexErweima" ng-show="isHover">--%>
                <%--<img src="/yzxfMall_page/img/2Dcode.png" alt="">--%>
                <%--<div>下载APP</div>--%>
            <%--</div>--%>
        <%--</div>--%>
</div>