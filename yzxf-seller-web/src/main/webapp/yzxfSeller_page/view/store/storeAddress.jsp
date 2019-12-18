<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeAddress_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div ng-show="userType=='seller'">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack"  ng-click="goPage('/store/sellerInfo')"></span>
            商家街道地址
            <span class="titleManage black textSize15" ng-click="updateSellerAddress()">保存</span>
        </div>
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <div class="mainRow">
            <textarea placeholder="填写您的街道地址(64字以内)" class="rowTextarea ng-pristine ng-untouched ng-valid"
                      ng-model="address"></textarea>
            </div>
        </div>
    </div>
    <div ng-show="userType=='factor'">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/factorInfo')"></span>
            服务站街道地址
            <span class="titleManage black textSize15" ng-click="updateFactorAddress()">保存</span>
        </div>
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <div class="mainRow">
            <textarea placeholder="填写您的街道地址(64字以内)" class="rowTextarea ng-pristine ng-untouched ng-valid"
                      ng-model="address"></textarea>
            </div>
        </div>
    </div>
</div>
