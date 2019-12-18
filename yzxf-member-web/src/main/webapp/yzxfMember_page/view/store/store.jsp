<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<script charset="utf-8" src="https://map.qq.com/api/js?v=2.exp"></script>

<div ng-controller="store_store_Ctrl" class="d_content title_section store_panel">
    <div class="title titleRed">
        <span class="titleAddress titleLeft widthPercent40 textEllipsis"
              ng-click="goPage('/home/location/locationQuery/1/selectedArea/storeArea')">
            <span class="icon-dizhi iconfont"></span>
            <span ng-bind="locationName==null || locationName==''?'地址':locationName"></span>
        </span>
        商家
    </div>

    <%--商家条件筛选按钮--%>
    <div class="storeSelect" style="border-bottom: 1px solid #eee;">
        <div class="storeSelectBtn" ng-click="goPage('store/operateType/operateUrl/operateStore')">
            <span ng-bind="selectType" ng-class="selectType!='经营范围'?'storeSelected':'gray888'"></span>
            <%--<span class="iconfont icon-right-1-copy storeSelectBtnIcon"></span>--%>
        </div>
        <div class="storeSelectBtn" ng-click="orderCheck=!orderCheck" ng-bind="orderName">排序方式</div>
    </div>
    <div ng-show="orderCheck" class="optionMenu">
        <div class="overflowHidden mainRowMargin10">
            <div ng-repeat="type in typeList" class="optionBtn" ng-click="selectedCheck(type.index,type.name)"
                 ng-class="selectedIndex==type.index?'optionBtn2':''" ng-bind="type.name"></div>
        </div>
        <%--<div class="hidePanel1" ng-show="orderCheck" ng-click="revokeOrderCheck()"></div>--%>
    </div>
    <div class="overflowPC" id="storeScroll">
        <div class="areaStore" ng-show="foodCourList!=null && foodCourList.length!=0">
            <div class="areaTitle">美食广场</div>
            <div class="flItem" ng-repeat="food in foodCourList"
                 ng-click="goPage('/store/storeInfo/sellerId/'+food._id)">
                <div style="position:relative" ng-style="food.distance==null?{marginBottom:'20px'}:{marginBottom:0}">
                    <img style="display: block;margin: 0 auto;width: auto;"
                         err-src="/yzxfMember_page/img/notImg02.jpg"
                         ng-src="{{iconImgUrl(getSellerIcon(food.icon,food.doorImg))}}">
                    <div class="juliCon" ng-show="food.distance!=null" ng-bind="getDistance(food.distance)"></div>
                </div>
                <div class="descCon"><p ng-bind="isNullText2(food.name)"></p></div>
            </div>
            <div class="clearDiv"></div>
        </div>
        <%--商家列表--%>
        <div ng-include="storeInclude"></div>
    </div>
</div>