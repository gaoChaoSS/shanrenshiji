<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_updateName_Ctrl" class="d_content title_section form_section index_page_globalDiv order_panel">
    <form ng-show="userType=='seller'" ng-submit="submitSellerForm()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            商家名字
            <button type="submit" class="titleManage notHigh bgWhite">保存</button>
        </div>
        <div class="sectionMain textAreaMargin">
            <input placeholder="商家名字:不得超过20个字" class="rowInput ng-pristine ng-untouched ng-valid" ng-model="storeInfo.name"/>
        </div>
    </form>
    <form ng-show="userType=='factor'" ng-submit="submitFactorForm()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            服务站名字
            <button type="submit" class="titleManage notHigh bgWhite">保存</button>
        </div>
        <div class="sectionMain textAreaMargin">
            <input placeholder="服务站名字:不得超过20个字" class="rowInput ng-pristine ng-untouched ng-valid" ng-model="factorInfo.name"/>
        </div>
    </form>
</div>