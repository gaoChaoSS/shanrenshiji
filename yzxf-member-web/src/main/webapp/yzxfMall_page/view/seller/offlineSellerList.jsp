<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="seller_offlineSellerList_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--轮播图--%>
    <div class="navCarousel" ng-include="navCarousel"></div>

    <div class="bodyWidth marginAuto">
        <div class="isNullBox" ng-show="totalNumCount==0">
            <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
            没有内容
        </div>
        <%--切换按钮--%>
        <%--<div class="sellerTitle" style="height: 50px;margin: 20px 0;border-bottom: 1px solid #DFDFDF;">--%>
            <%--&lt;%&ndash;ng-class="goodsAsComment=='goods'?'clickDiv':''&ndash;%&gt;--%>
            <%--<div class="floatL flex1" style="width: 120px;margin-left: 15px;font-size: 13px;color: #ababab" ><span class="icon-dizhi iconfont" style="margin-right: 15px;color: red"></span>全部地区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >武侯区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >青羊区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >金牛区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >锦江区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >郫都区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >温江区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >高新区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >龙泉驿区</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >大邑县</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >金堂县</div>--%>
            <%--<div class="floatL flex1" style="width: 80px;margin-left: 15px;font-size: 13px;" >高新西区</div>--%>
        <%--</div>--%>
        <%--美食--%>
        <div class="offTypeDiv" style="padding: 20px;" ng-repeat="mod in operate" id="{{'seller'+mod.name}}" ng-show="mod.sellerList!=''&&mod.sellerList!=null">
            <%--图标标题--%>
            <div>
                <div class="floatL"><img src="{{iconImg(mod.cMallImg)}}" alt=""></div>
                <div class="floatL" ng-bind="mod.name"></div>
            </div>
            <div class="isNullBox" ng-show="mod.sellerList==null || mod.sellerList==''">
                <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                没有内容
            </div>
            <div class="sellerListDiv floatL" ng-repeat="s in mod.sellerList" ng-click="openPage('/seller/sellerInfo/sellerId/'+s._id)">
                <%--商品图片--%>
                <div id="sellerTIm" style="background: url('{{iconImgUrl(getSellerIcon(s.icon,s.doorImg))}}') no-repeat;background-size: 100% 100%;border: 1px solid #DFDFDF;"></div>
                <%--名字--%>
                <div class="sellerListName">
                    <div class="textEllipsis" ng-bind="s.name"></div>
                    <div class="textEllipsis" ng-bind="s.intro"></div>
                </div>
                <div class="textEllipsis">
                    <span class="iconfont icon-dizhi"></span>
                    <span ng-bind="s.area+s.address"></span>
                </div>
                <div>
                    <span class="sellerJiFenIcon">积</span>
                    <span style="color: #138bbe" ng-bind="s.integralRate">1</span>
                    <span style="color: #138bbe">%</span>
                </div>
            </div>
        </div>
    </div>

    <%--底部模板--%>
    <div class="marginTop30" ng-include="mallBottom"></div>
    <%--&lt;%&ndash;左边定位导航&ndash;%&gt;--%>
    <%--<div class="indexLeftNavDiv">--%>
        <%--<a ng-repeat="mdl in operate" href="{{'#seller'+mdl.name}}"--%>
           <%--ng-mouseenter="mouseNavUp($index)" ng-mouseleave="mouseNavDown()" >--%>
            <%--<div ng-show="txtOrImg!=$index"><img src="{{iconImg(mdl.mallImg)}}" alt=""></div>--%>
            <%--<div ng-show="txtOrImg==$index" ng-bind="mdl.name"></div>--%>
        <%--</a>--%>
    <%--</div>--%>
</div>