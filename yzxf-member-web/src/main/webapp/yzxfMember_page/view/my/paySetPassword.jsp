<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_paySetPassword_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/wallet')"></span>
        请先设置支付密码
    </div>
    <div class="gray888 mainRowLeft10">请输入6位数字组成的密码</div>
    <form ng-submit="setPayPassword()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">新密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput " ng-model="firstPwd" ng-class="pwdLengthError?'rowInputShort':''" ng-blur="nextBtn()" placeholder="请输入新密码" maxlength="6"/>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-class="{true:'errorColor'}[errorColor]">确认密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="secondPwd" ng-change="nextCheck()" ng-blur="sureNextBtn()" maxlength="6"
                       ng-class="secondPwd==''?'rowInput':(errorColor?'rowInput rowInputShort ':'rowInput')" placeholder="请再次输入新密码"/>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
        </div>
        <button type="submit" class="submitBtn submitBtnBlue" ng-disabled="check" ng-click="nextBtn()" ng-bind="checkValue">完成</button>
    </form>
    <div class="hideMenu" ng-show="isOK">
        <div class="errorMain">
            <div class="errorMainRow">密码设置成功</div>
            <div class="errorMainRow" ng-click="goPage('/my/wallet')">确定</div>
        </div>
    </div>
</div>