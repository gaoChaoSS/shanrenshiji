<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_forgetPassword_Ctrl">
    <%--头部--%>
    <div class="account_HeadDiv">
        <div class="floatL account_HeadDivImg" ng-click="goPage('/home/index')">
            <img src="/yzxfMall_page/img/logo1.png" style="width: 130px;"/>
        </div>
        <div class="floatL account_typeTitle">找回密码</div>
        <div class="floatR account_rightTypeTitle" ng-click="goPage('/account/login')">登录</div>
        <div class="floatR account_rightTxt">想起密码?立刻登录</div>
    </div>
    <%--中间内容--%>
    <div class="marginZAuto accountContentDiv" style="width: 1366px;height: 550px;margin: 0 auto">
        <div class=" floatL accountBk"></div>
        <div  ng-show="!isForget" class=" floatL accountFromDiv" style="height: 460px;margin-top: 40px">
            <div class="flex1">设置新密码</div>
            <form ng-submit="forgotPwd()">
                <div class="accountLoginName">
                    <div class="floatL iconfont icon-shouji"></div>
                    <input class="floatL " ng-model="phoneNumber" type="text"  placeholder="请输入您的手机号" ng-change="phoneCheck();"/>
                </div>
                <div style="position: relative">
                    <div class="accountLoginName" style="margin-top: 25px;width: 45%;margin-left: 39px">
                        <div class="floatL iconfont icon-shimingrenzheng" style="width: 47px;"></div>
                        <input style="width: 100px" ng-model="verification" class="floatL " type="text"  placeholder="请输入验证码"/>
                    </div>
                    <div class="flex1" style="width: 32%;height: 37px;position: absolute;right: 39px;top: 0;background: #138bbe;color: #FFF;font-size: 14px"
                         ng-disabled="pCheck" ng-click="getPhoneCheckCode()" ng-bind="codeDiv?countdown+'s后再获取验证码':'点击获取验证码'">获取验证码</div>
                </div>
                <div class="accountLoginName" style="margin-top: 25px;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " ng-model="newPassword" type="password"  placeholder="请输入密码"/>
                </div>
                <div class="accountLoginName" style="margin-top: 25px;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " ng-model="confirmPassword" type="password"  placeholder="确认密码"/>
                </div>
                <button class="topUpBtn" style="margin-left: 39px;width: 322px;margin-top: 25px;border-radius: 5px" type="submit">完成</button>
            </form>
        </div>
        <div ng-show="isForget" class=" floatL forgetPwdDiv">
            <div class="flex1">
                <img ng-show="modifyIsOk=='OK'" src="/yzxfMall_page/img/yirenzheng.png" alt="">
                <img ng-show="modifyIsOk=='NO'" src="/yzxfMall_page/img/weirenzheng.png" alt="">
            </div>
            <div>
                <span ng-show="modifyIsOk=='OK'">恭喜您!密码找回成功!</span>
                <span ng-show="modifyIsOk=='NO'">很遗憾!密码找回失败!</span>
            </div>
            <div>
                <span ng-show="modifyIsOk=='OK'" ng-click="goPage('/account/login')">立即登录</span>
                <span ng-show="modifyIsOk=='NO'" ng-click="isForget=!isForget">重新找回</span>
            </div>
        </div>
    </div>
    <%--底部模板--%>
    <div ng-include="accountBottom" style="clear: left"></div>
</div>