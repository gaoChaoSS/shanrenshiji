<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeLegalPerson_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack"  ng-click="goPage('/store/sellerInfo')"></span>
        法人信息
        <span class="titleManage black textSize15" ng-click="isOK()">保存</span>
    </div>
    <div class="sectionMain sectionMainNotMargin mainRowTop5">
        <div class="mainRow">
            <input placeholder="填写法人信息" class="rowInput ng-pristine ng-untouched ng-valid" ng-model="legalPerson"/>
        </div>
    </div>
</div>
