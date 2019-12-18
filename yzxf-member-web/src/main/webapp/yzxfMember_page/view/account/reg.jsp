<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_reg_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBackCheck()"></span>
        会员注册
        <%--<span class="titleRight2 notHigh" ng-click="goPage('/account/login')">登录</span>--%>
        <%--<span class="titleManage iconfont2 icon2-ccgl-yundansaomiao-2" style="font-size: 26px;" ng-click="scanQrCode()"></span>--%>
    </div>
    <div ng-if="showShare" class="shareCon">
        <div class="notHigh positionAbsolute">邀请人</div>
        <div class="positionAbsolute" style="right:10px">
            <div class="iconfont2 icon2-huiyuan" style="font-size:15px" ng-if="pathParams.shareType==='Member'"
                 ng-class="share.isRealName?'dodgerBlue':'notHigh'" ng-bind="share.isRealName?' 已认证':' 未认证'"></div>
            <div class="iconfont icon-duigou" style="font-size:15px;margin-top: 5px;" ng-if="pathParams.shareType==='Member'"
                 ng-class="share.isBindCard?'dodgerBlue':'notHigh'" ng-bind="share.isBindCard?' 已激活':' 未激活'"></div>
            <div class="iconfont icon-duigou high" ng-if="!share.canUse" style="font-size:15px;margin-top: 5px;">禁用</div>
        </div>
        <div>
            <img class="mod-circleImg1" style="margin:0 auto 10px;display:block" ng-src="{{iconImgUrl(myInfo.icon)}}">
            <div style="margin:0 auto" class="_flex1">
                <span ng-bind="pathParams.shareType==='Member'?getMobile(share.mobile):share.name"></span>
                <span class="notHigh" ng-if="pathParams.shareType==='Member' && !isEmpty2(share.realName)" ng-bind="' ( '+getRealName(share.realName)+' )'"></span>
            </div>
        </div>
    </div>

    <form ng-submit="submitForm()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">手机号:</div>
                <input type="text" class="rowInput rowInputShort" placeholder="请输入手机号码" ng-model="phoneNumber" ng-change="nextBtn()"/>
                <%--<span class="mainRowRight sendMessage">点击获取验证码</span>--%>
            </div>
            <div class="mainRow">
                <div class="rowTitle">验证码:</div>
                <input type="text" class="rowInput" ng-model="verification" ng-change="nextBtn()"/>
            </div>
        </div>
        <div style="text-align: center;padding: 5px 0;background-color: #FF9900" class="submitBtn submitBtnRed" ng-disabled="pCheck" ng-click="getPhoneCheckCode()" ng-bind="codeDiv?countdown+'s后再获取验证码':'点击获取验证码'"></div>
        <div class="sectionMain">
            <div class="mainRow" ng-show="memberType==1">
                <div class="rowTitle">密码:</div>
                <input type="{{showPassword?'text':'password'}}" ng-model="firstPwd" ng-change="nextBtn()" ng-blur="nextBtn()" class="rowInput" placeholder="请输入密码"/>
                <%--<span class="mainRowRight errorRed mainRowRightErrorText" ng-show="firstPwd==''?false:pwdLengthError">6~16位</span>--%>
                <span class="{{isOpenEye}} mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow" ng-show="memberType==1">
                <div class="rowTitle" ng-class="{true:'errorColor'}[errorColor]">确认密码:</div>
                <input type="{{showPassword?'text':'password'}}" ng-model="secondPwd" ng-change="nextBtn()" ng-blur="nextBtn()" class="rowInput" ng-class="firstPwd==secondPwd?'':(pwdLengthError?'rowInputShort':'')" placeholder="请再次输入密码"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="errorText">密码不一致</span>
                <span ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" class="mainRowRight" ng-click="showPassword=!showPassword"></span>
            </div>
        </div>
        <button type="submit" class="submitBtn submitBtnRed" ng-disabled="check">下一步</button>
    </form>

    <%--<div style="position:fixed;bottom:15px;width:100%;text-align:center;left: 0;"--%>
         <%--ng-click="goPage('/account/login/returnData/'+pathParams.returnData)">已有账号，去<span class="colorBlue">登录</span></div>--%>

    <div style="position:fixed;left: 0;bottom:0;width:10px;height:10px" ng-click="setShareSeller()"></div>
    <div style="position:fixed;bottom: 0;right:0;width:10px;height:10px" ng-click="setNotCode()"></div>
</div>