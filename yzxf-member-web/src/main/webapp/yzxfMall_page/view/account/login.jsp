<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_login_Ctrl">
    <%--头部--%>
    <div class="account_HeadDiv">
        <div class="floatL account_HeadDivImg" ng-click="goPage('/home/index')">
            <img src="/yzxfMall_page/img/logo1.png" style="width: 130px;"/>
        </div>
        <div class="floatL account_typeTitle">登录</div>
        <div class="floatR account_rightTypeTitle pointer" ng-click="goPage('/account/reg')">注册</div>
        <div class="floatR account_rightTxt">还没有普惠生活账户?</div>
    </div>
    <%--中间内容--%>
    <div class="marginZAuto accountContentDiv" style="width: 100%;height: 550px;">
        <div class=" floatL accountBk"></div>
        <form ng-submit="submitForm()">
            <div class=" floatL accountFromDiv">
                <div class="flex1">登录账号</div>
                <div class="accountLoginName">
                    <div class="floatL iconfont icon-user"></div>
                    <input class="floatL" ng-model="loginName" type="text"  placeholder="请输入您的手机号,会员号,或者身份证号"/>
                </div>
                <div class="loginUserType">
                    <div><input ng-model="userType" value="1" type="radio" ng-checked="true">手机号</div>
                    <div><input ng-model="userType" value="2" type="radio">会员号</div>
                    <div><input ng-model="userType" value="3" type="radio">身份证号</div>
                </div>
                <div class="accountLoginName" style="margin-top: 25px;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " type="password" ng-model="pwd" placeholder="请输入您的密码"/>
                </div>
                <div class="accountLoginBottom">
                    <%--<div class="floatL"><input name="userType" type="checkbox" id="rememberPwd"/>下次自动登录</div>--%>
                    <div class="floatR pointer" ng-click="goPage('/account/forgetPassword')">忘记密码?</div>
                </div>
                <button type="submit" class="topUpBtn" style="margin-left: 39px;width: 322px;margin-top: 10px;border-radius: 5px">立即登录</button>
            </div>
        </form>
    </div>
    <%--底部模板--%>
    <div ng-include="accountBottom" style="clear: left"></div>
</div>