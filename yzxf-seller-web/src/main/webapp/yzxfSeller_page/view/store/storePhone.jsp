<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storePhone_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack"  ng-click="goPage('/store/sellerInfo')"></span>
            商家联系方式
            <span class="titleManage black textSize15" ng-click="isOK()">保存</span>
        </div>
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <div class="mainRow">
            <input placeholder="填写您的联系电话" class="rowInput ng-pristine ng-untouched ng-valid" ng-model="phone"/>
            </div>
        </div>
</div>
