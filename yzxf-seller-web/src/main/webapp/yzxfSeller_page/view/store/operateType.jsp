<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="store_operateType_Ctrl" class="d_content title_section form_section" >
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        选择经营范围
        <span class="titleManage" ng-click="isGoPage()"
              ng-class="(operateId!=null && operateId!=-1)?'gray333':'grayaaa'">确定</span>
    </div>
    <div class="sectionLocation">
        <div class="colsLocation bgGrayddd">
            <div ng-repeat="first in firstList" class="rowLocation" ng-class="first._id==firstId?'bgGrayeee':'bgGrayddd'">
                <div ng-bind="first.name" ng-click="getOperate(first._id,2,first.name,first.value,first.pvalue)"
                     ng-class="first._id==firstId?'locationSelected':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgGrayeee">
            <div ng-repeat="second in secondList" class="rowLocation" ng-class="second._id==secondId?'bgWhite':'bgGrayeee'">
                <div ng-bind="second.name" ng-click="getOperate(second._id,3,second.name,second.value,second.pvalue)"
                     ng-class="second._id==secondId?'colorBlue':'gray333'"></div>
            </div>
        </div>
        <div class="colsLocation bgWhite">
            <div ng-repeat="third in thirdList" class="rowLocation">
                <div ng-bind="third.name" ng-click="getOperate(third._id,4,third.name,third.value,third.pvalue)"
                     ng-class="third._id==thirdId?'colorBlue':'gray333'"></div>
            </div>
        </div>
    </div>
</div>
