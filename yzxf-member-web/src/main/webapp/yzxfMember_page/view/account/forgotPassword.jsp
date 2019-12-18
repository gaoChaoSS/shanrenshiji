<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_forgotPassword_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/account/login')"></span>
        密码找回
    </div>
    <form ng-submit="forgotPwd()">
        <div class="mainRowTitle notHigh">短信验证</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow">
                <div class="rowTitle">手机号:</div>
                <input type="text" class="rowInput rowInputShort" placeholder="请输入手机号码" ng-model="phoneNumber" ng-change="phoneCheck();submitBtn()" />
            </div>
            <div class="mainRow">
                <div class="rowTitle">验证码:</div>
                <input type="text" class="rowInput" ng-model="verification" ng-change="submitBtn()"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle">新密码:</div>
                <input type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="newPassword" ng-change="submitBtn()"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="firstPwd==''?false:pwdLengthError">6~16位</span>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
        </div>
        <div style="text-align: center;padding: 5px 0;background-color: #FF9900" class="submitBtn submitBtnRed" ng-disabled="pCheck" ng-click="getPhoneCheckCode()" ng-bind="codeDiv?countdown+'s后再获取验证码':'点击获取验证码'"></div>
        <%--<div class="mainRowTitle notHigh">邮箱验证</div>--%>
        <%--<div class="sectionMain sectionMainNotMargin">--%>
        <%--<div class="mainRow">--%>
        <%--<div class="rowTitle" ng-class="myEmail==''?'rowTitle':(emailError?'rowTitle errorRed':'rowTitle')">电子邮箱:</div>--%>
        <%--<input type="text" class="rowInput" ng-model="myEmail" ng-class="myEmail==''?'rowInput':(emailError?'rowInput rowInputShort':'rowInput')" placeholder="请输入有效的邮箱" ng-change="cardErrorFuc();submitBtn()"/>--%>
        <%--<div class="mainRowRight mainRowRightErrorText errorRed" ng-show="myEmail==''?false:emailError">邮箱格式错误</div>--%>
        <%--</div>--%>
        <%--</div>--%>

        <button type="submit" class="submitBtn submitBtnRed" ng-disabled="check">完成</button>
    </form>
</div>