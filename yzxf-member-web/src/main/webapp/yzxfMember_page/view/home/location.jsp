<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="home_location_Ctrl" class="d_content title_section form_section" >
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBackFun()"></span>
        {{(locationName==null||locationName=='')?'选择城市':locationName}}
        <span class="titleManage" ng-click="isGoPage()"
              ng-class="(locationId!=null)?'gray333':'grayaaa'" ng-bind="selectBtn()"></span>
    </div>
    <div ng-show="pathParams.selectedArea=='storeArea' && areaName[1]!=null && areaName[1]!=''" class="search1">
        <input type="text" ng-model="keyword" placeholder="输入街道名称(64个字符长度以内)">
        <button ng-click="search(true)">搜索街道</button>
        <button ng-click="showCity=true" ng-class="showCity?'disabledBtn1':''"
                ng-disabled="showCity?'disabled':''">选择城市</button>
    </div>
    <div ng-show="!showCity">
        <div ng-repeat="searchItem in searchList" class="searchItem"
             ng-click="goPageByLat(searchItem)">
            <div ng-bind="searchItem.title"></div>
            <div ng-bind="searchItem.address"></div>
        </div>
    </div>

    <div class="sectionLocation" ng-class="(pathParams.selectedArea=='storeArea' && areaName[1]!=null && areaName[1]!='')?'sectionLocation2':''" ng-show="showCity">
        <div class="colsLocation bgGrayccc">
            <div ng-repeat="province in list1" class="rowLocation" ng-class="province.code==areaId[0]?'bgGrayddd':'bgGrayccc'">
                <div ng-bind="province.name" ng-click="getLocation(province.code,'02',province.parentCode,province.name,province.id,province.value,province.pValue)"
                     ng-class="province.code==areaId[0]?'locationSelected':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgGrayddd">
            <div ng-repeat="city in list2" class="rowLocation" ng-class="city.code==areaId[1]?'bgGrayeee':'bgGrayddd'">
                <div ng-bind="city.name" ng-click="getLocation(city.code,'03',city.parentCode,city.name,city.id,city.value,city.pValue)"
                     ng-class="city.code==areaId[1]?'colorBlue':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgGrayeee">
            <div ng-repeat="county in list3" class="rowLocation" ng-class="county.code==areaId[2]?'bgWhite':'bgGrayeee'">
                <div ng-bind="county.name" ng-click="getLocation(county.code,'04',county.parentCode,county.name,county.id,county.value,county.pValue)"
                     ng-class="county.code==areaId[2]?'colorBlue':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgWhite">
            <div ng-repeat="town in list4" class="rowLocation">
                <div ng-bind="town.name" ng-click="getLocation(town.code,'05',town.parentCode,town.name,town.id,town.value,town.pValue)"
                     ng-class="town._id==areaId[3]?'colorBlue':'gray333'"></div>
            </div>
        </div>
    </div>
</div>
