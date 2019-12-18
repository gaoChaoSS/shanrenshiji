<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_bind_Ctrl" class="form_section d_content title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBackCheck()"></span>
        微信绑定
        <span class="titleRight2 notHigh" ng-click="goPage('/account/login')">登录</span>
    </div>
    <form ng-submit="submitForm()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">手机号:</div>
                <input type="text" class="rowInput" placeholder="请输入手机号码" ng-model="bindData.phone"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle">密码:</div>
                <input type="password" class="rowInput" ng-model="bindData.password"/>
            </div>
            <%--<div class="mainRow" ng-show="isMember && memberInfo.realName!=null && memberInfo.realName!=''">--%>
                <%--<div class="rowTitle">会员:</div>--%>
                <%--<div class="rowInput" ng-bind="memberInfo.realName"></div>--%>
            <%--</div>--%>
            <%--<div class="mainRow" ng-show="isMember && memberInfo.cardNo!=null && memberInfo.cardNo!=''">--%>
                <%--<div class="rowTitle">卡号:</div>--%>
                <%--<div class="rowInput" ng-bind="memberInfo.cardNo"></div>--%>
            <%--</div>--%>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">验证码:</div>--%>
                <%--<input type="text" class="rowInput" style="width: calc(100% - 230px);"--%>
                       <%--ng-model="bindData.smsCode" ng-change="checkInput()"/>--%>
                <%--<div class="submitBtn-small" ng-bind="verText" ng-disabled="isMember?checkVer:'disabled'" ng-click="getVer()"></div>--%>
            <%--</div>--%>
        </div>
        <div style="text-align: center;line-height:45px;" class="submitBtn"
             <%--ng-disabled="isMember?checkBtn:'disabled'"--%>
             ng-click="login()">确认关联</div>
    </form>
</div>