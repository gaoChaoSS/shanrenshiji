<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="store_operateType_Ctrl" class="d_content title_section form_section" >
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        选择类别
        <span class="titleManage"
              ng-class="operateName!=null?'gray333':'grayaaa'"
              ng-click="isGoPage()"
              ng-bind="operateName!=null?'确定':'所有类别'"></span>
    </div>
    <div class="sectionLocation">
        <div class="colsLocation bgGrayddd">
            <div ng-repeat="first in firstList" class="rowLocation" ng-class="first.code==firstId?'bgGrayeee':'bgGrayddd'">
                <div ng-bind="first.name" ng-click="getOperate(first.code,2,first.parentCode,first.name)"
                     ng-class="first.code==firstId?'locationSelected':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgGrayeee">
            <div ng-repeat="second in secondList" class="rowLocation" ng-class="second.code==secondId?'bgWhite':'bgGrayeee'">
                <div ng-bind="second.name" ng-click="getOperate(second.code,3,second.parentCode,second.name)"
                     ng-class="second.code==secondId?'colorBlue':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgWhite">
            <div ng-repeat="third in thirdList" class="rowLocation">
                <div ng-bind="third.name" ng-click="getOperate(third.code,4,third.parentCode,third.name)"
                     ng-class="third.code==thirdId?'colorBlue':'gray333'"></div>
            </div>
        </div>
    </div>
</div>
