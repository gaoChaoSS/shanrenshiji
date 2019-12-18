<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="store_location_Ctrl" class="d_content title_section form_section" >
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        选择城市
        <span class="titleManage" ng-click="isGoPage()"
              ng-class="(locationId!=null)?'gray333':'grayaaa'">确定</span>
    </div>
    <div class="sectionLocation">
        <div class="colsLocation bgGrayccc">
            <div ng-repeat="province in list1" class="rowLocation" ng-class="province._id==id1?'bgGrayddd':'bgGrayccc'">
                <div ng-bind="province.name" ng-click="getLocation(province._id,2,province.name,province.value,province.pvalue)"
                     ng-class="province._id==id1?'locationSelected':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgGrayddd">
            <div ng-repeat="city in list2" class="rowLocation" ng-class="city._id==id2?'bgGrayeee':'bgGrayddd'">
                <div ng-bind="city.name" ng-click="getLocation(city._id,3,city.name,city.value,city.pvalue)"
                     ng-class="city._id==id2?'colorBlue':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgGrayeee">
            <div ng-repeat="county in list3" class="rowLocation" ng-class="county._id==id3?'bgWhite':'bgGrayeee'">
                <div ng-bind="county.name" ng-click="getLocation(county._id,4,county.name,county.value,county.pvalue)"
                     ng-class="county._id==id3?'colorBlue':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgWhite">
            <div ng-repeat="town in list4" class="rowLocation">
                <div ng-bind="town.name" ng-click="getIndex(town._id,town.name,town.value,town.pvalue)"
                     ng-class="town._id==id4?'colorBlue':'gray333'"></div>
            </div>
        </div>
    </div>
</div>
