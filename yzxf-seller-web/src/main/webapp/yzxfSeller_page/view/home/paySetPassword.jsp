<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_paySetPassword_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span ng-show="userType=='factor'" class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/cardIssuingAccount')"></span>
        <span ng-show="userType=='seller'" class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/storeAccount')"></span>
        请先设置支付密码
    </div>
    <div class="gray888 mainRowLeft10 lineHeight50">请输入6位数字组成的密码</div>
    <form ng-show="userType=='factor'" ng-submit="setFactorPayPassword()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">新密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput " ng-model="firstPwd" ng-class="pwdLengthError?'rowInputShort':''" ng-change="nextBtn()" placeholder="请输入新密码" maxlength="6"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="pwdLengthError">只能6位</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-class="{true:'errorColor'}[errorColor]">确认密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="secondPwd" ng-change="nextBtn()"
                       ng-class="secondPwd==''?'rowInput':(errorColor?'rowInput rowInputShort ':'rowInput')" placeholder="请再次输入新密码" maxlength="6"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="errorText">密码不一致</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
        </div>
        <button type="submit" class="submitBtn" ng-disabled="check" ng-click="nextBtn()">完成</button>
    </form>
    <form ng-show="userType=='seller'" ng-submit="setSellerPayPassword()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">新密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput " ng-model="firstPwd" ng-class="pwdLengthError?'rowInputShort':''" ng-change="nextBtn()" placeholder="请输入新密码" maxlength="6"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="pwdLengthError">只能6位</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-class="{true:'errorColor'}[errorColor]">确认密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="secondPwd" ng-change="nextBtn()"
                       ng-class="secondPwd==''?'rowInput':(errorColor?'rowInput rowInputShort ':'rowInput')" placeholder="请再次输入新密码" maxlength="6"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="errorText">密码不一致</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
        </div>
        <button type="submit" class="submitBtn" ng-disabled="check" ng-click="nextBtn()">完成</button>
    </form>
    <div class="hideMenu" ng-show="isOK">
        <div class="errorMain">
            <div class="errorMainRow">密码设置成功</div>
            <div class="errorMainRow" ng-click="goBack()">确定</div>
            <%--<div ng-show="userType=='factor'" class="errorMainRow" ng-click="goPage('/home/cardIssuingAccount')">确定</div>--%>
            <%--<div ng-show="userType=='seller'" class="errorMainRow" ng-click="goPage('/store/storeAccount')">确定</div>--%>
        </div>
    </div>
</div>