<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_modifyPassword_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/setting/userType/'+userType)"></span>
        密码修改
    </div>
    <form ng-submit="modifyPwd()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">旧密码:</div>
                <input type="{{showPassword?'text':'password'}}"  class="rowInput" ng-model="oldPwd" ng-change="nextBtn()" placeholder="请输入旧密码"/>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle">新密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="firstPwd" class="sendMessageInput" ng-change="nextBtn()" placeholder="请输入新密码"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="pwdLengthError">6位~16位</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-class="{true:'errorColor'}[errorColor]">确认密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="secondPwd" ng-change="nextBtn()"
                       ng-class="secondPwd==''?'rowInput':(errorColor?'rowInputShort rowInput':'rowInput')" placeholder="请再次输入新密码"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="errorText">密码不一致</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
        </div>
        <button type="submit" class="submitBtn" ng-disabled="check" ng-click="nextBtn()">完成</button>
    </form>
    <div class="hideMenu" ng-show="isOK">
        <div class="errorMain">
            <div class="errorMainRow">密码修改成功</div>
            <div class="errorMainRow" ng-click="goPage('/store/setting/userType/'+userType)">确定</div>
        </div>
    </div>
</div>