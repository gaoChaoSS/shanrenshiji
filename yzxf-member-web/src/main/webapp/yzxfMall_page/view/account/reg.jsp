<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_reg_Ctrl">
    <%--头部--%>
    <div class="account_HeadDiv">
        <div class="floatL account_HeadDivImg" ng-click="goPage('/home/index')">
            <img src="/yzxfMall_page/img/logo1.png" style="width: 130px;"/>
        </div>
        <div class="floatL account_typeTitle pointer">注册</div>
        <div class="floatR account_rightTypeTitle pointer"  ng-click="goPage('/account/login')">登录</div>
        <div class="floatR account_rightTxt">已有普惠生活账户,立刻登录</div>
    </div>
    <%--中间内容--%>
    <div class="marginZAuto accountContentDiv" style="width: 100%;height: 550px;">
        <div class=" floatL accountBk"></div>
        <form ng-submit="submitForm()">
            <div class=" floatL accountFromDiv" style="height: 460px;margin-top: 40px">
                <div class="flex1">注册账号</div>
                <div class="accountLoginName">
                    <div class="floatL iconfont icon-shouji"></div>
                    <input class="floatL " type="text" ng-model="phoneNumber"  placeholder="请输入您的手机号" ng-change="phoneCheck()"/>
                </div>
                <div style="position: relative">
                    <div class="accountLoginName" style="margin-top: 25px;width: 50%;margin-left: 39px">
                        <div class="floatL iconfont icon-shimingrenzheng" style="width: 47px;"></div>
                        <input style="width: 100px" ng-model="verification" class="floatL " type="text"  placeholder="请输入验证码"/>
                    </div>
                    <div style="font-size: 12px;" class="flex1 getCodeBtn" ng-disabled="pCheck" ng-click="getPhoneCheckCode()" ng-bind="codeDiv?countdown+'s后再获取验证码':'点击获取验证码'">获取验证码</div>
                </div>
                <div class="accountLoginName" style="margin-top: 25px;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " type="password" ng-model="firstPwd"  placeholder="请输入密码"/>
                </div>
                <div class="accountLoginName" style="margin-top: 25px;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " type="password" ng-model="secondPwd" placeholder="确认密码"/>
                </div>
                <div class="accountLoginBottom">
                    <div class="floatL"><input ng-model="isOK" name="isOK" value="1" type="checkbox"/>阅读并接受<span class="pointer colorBlue1">《普惠生活用户协议》</span></div>
                </div>
                <button class="topUpBtn" type="submit" style="margin-left: 39px;width: 322px;margin-top: 10px;border-radius: 5px">注册</button>
            </div>
        </form>
    </div>
    <%--底部模板--%>
    <div ng-include="accountBottom" style="clear: left"></div>
</div>