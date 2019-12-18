<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeIntroduction_Ctrl" class="d_content title_section form_section index_page_globalDiv order_panel">
    <form ng-show="userType=='seller'" ng-submit="submitSellerForm()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            商家简介
            <button type="submit" class="titleManage notHigh bgWhite">保存</button>
        </div>
        <div class="sectionMain textAreaMargin">
            <textarea placeholder="商家简介:不得超过64个字" class="rowTextarea" ng-model="storeInfo.intro"></textarea>
        </div>
    </form>
    <form ng-show="userType=='factor'" ng-submit="submitFactorForm()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            服务站简介
            <button type="submit" class="titleManage notHigh bgWhite">保存</button>
        </div>
        <div class="sectionMain textAreaMargin">
            <textarea placeholder="服务站简介:不得超过64个字" class="rowTextarea" ng-model="factorInfo.intro"></textarea>
        </div>
    </form>
</div>