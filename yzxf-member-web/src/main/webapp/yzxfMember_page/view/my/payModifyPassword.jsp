<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_payModifyPassword_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/paySetting')"></span>
        支付密码修改
    </div>
    <form ng-submit="modifyMyPayPwd()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">旧密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="oldPwd" ng-change="nextBtn()" placeholder="请输入原来的支付密码"/>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-class="newPwd==''?'':(pwdLengthError?'errorRed':'')">新密码:</div>
                <input type="{{showPassword?'text':'password'}}" ng-model="newPwd" ng-change="nextBtn()" class="rowInput" ng-class="newPwd==''?'':(pwdLengthError?'rowInputShort':'')" placeholder="请输入新的支付密码"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="newPwd==''?false:pwdLengthError">只能6位</span>
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
        <input type="submit" value="完成" class="submitBtn submitBtnBlue" ng-disabled="check" ng-click="nextBtn()"/>
    </form>
    <div class="hideMenu" ng-show="isOK">
        <div class="errorMain">
            <div class="errorMainRow">密码修改成功</div>
            <div class="errorMainRow" ng-click="goPage('/my/paySetting')">确定</div>
        </div>
    </div>
</div>