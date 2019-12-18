<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <div class="popTitle">分支机构</div>
    <div style="width:100%;">
        <span>类型:</span>
        <select ng-model="selectType.name" ng-options="type as type for type in typeList" ng-show="isAdd && !isModify"></select>
        <span ng-show="!isAdd || isModify" ng-bind="selectType.name"></span>
    </div>
    <div style="width:100%;" ng-show="selectType.name=='分支机构'">
        <span>归属城市:</span>
        <select ng-model="selectCity._id" ng-options="city._id as city.name for city in cityList" ng-show="isAdd"></select>
        <span ng-bind="userInfo.belongCity" ng-show="!isAdd"></span>
    </div>
    <div style="width:100%;">
        <span>名称:</span>
        <input type="text" ng-model="userInfo.name" ng-show="isAdd" maxlength="100"/>
        <span ng-bind="userInfo.name" ng-show="!isAdd"></span>
    </div>
    <div style="width:100%;" ng-show="selectType.name=='分支机构'">
        <span>联系方式:</span>
        <input type="text" ng-model="userInfo.mobile" ng-show="isAdd" maxlength="20"/>
        <span ng-bind="userInfo.mobile" ng-show="!isAdd"></span>
    </div>
    <div style="width:100%;white-space: inherit" ng-show="selectType.name=='分支机构'">
        <span>地址:</span>
        <input type="text" ng-model="userInfo.address" ng-show="isAdd" maxlength="300" style="width: 80%;"/>
        <span ng-bind="userInfo.address" ng-show="!isAdd"></span>
    </div>
    <div ng-show="!isAdd">
        <span>创建时间:</span>
        <span ng-bind="showYFullTime(userInfo.createTime)"></span>
    </div>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-hide="hideSubmit">
    <button class="fr btn1" ng-click="checkBtnFun(1)">提交</button>
</div>

<div class="sectionHintBk" ng-show="checkBtn[0]">
    <div class="sectionHint">
        <div class="lineH100px">是否退出修改?</div>
        <div class="flex1">
            <button class="btn1" ng-click="closeWin()">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(0)">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[1]">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="modifyBranches()">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(1)">否</button>
        </div>
    </div>
</div>

<div class="sectionHintBk" ng-show="checkBtn[2]">
    <div class="sectionHint">
        <div class="lineH100px">操作成功!</div>
        <div class="flex1">
            <button class="btn1 bkColorRed1" ng-click="closeWin()">确定</button>
        </div>
    </div>
</div>
