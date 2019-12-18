<%@ page import="java.util.Date" %>
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="account_login_Ctrl" class="form_section d_content title_section loginPanel">
    <div class="hide1"></div>
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBackCheck()"></span>
        账户登录
        <span class="titleRight2 notHigh" ng-click="goPage('/account/reg')">注册</span>
    </div>
    <form ng-submit="submitForm()">
        <div class="sectionMain">
            <div class="mainRow" ng-click="showLoginSelect=!showLoginSelect">
                <div class="rowTitle">登录方式:</div>
                <span ng-bind="selectedText" class="rowInput notHigh"></span>
                <span class="icon-iconfontarrows iconfont mainRowRight"></span>
            </div>
            <div class="mainRow rowHeightAuto" ng-show="showLoginSelect" ng-click="showLoginSelect=false">
                <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='手机号';loginBtnColor()">手机号
                    <span ng-show="selectedText=='手机号'" class="icon-jianchacheck35 iconfont mainRowSelected"></span>
                </div>
                <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='卡号';loginBtnColor()">卡号
                    <span ng-show="selectedText=='卡号'" class="icon-jianchacheck35 iconfont mainRowSelected"></span>
                </div>
                <div class="mainRowSelect mainRowSelectOption" ng-click="selectedText='身份证号';loginBtnColor()">身份证号
                    <span ng-show="selectedText=='身份证号'" class="icon-jianchacheck35 iconfont mainRowSelected"></span>
                </div>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-bind="selectedText+':'"></div>
                <input type="text" class="rowInput" placeholder="请输入{{selectedText}}" ng-model="selectPhone"
                       ng-show="selectedText=='手机号'" ng-change="loginBtnColor()"/>
                <input type="text" class="rowInput" placeholder="请输入{{selectedText}}" ng-model="selectNumber"
                       ng-show="selectedText=='卡号'" ng-change="loginBtnColor()"/>
                <input type="text" class="rowInput" placeholder="请输入{{selectedText}}" ng-model="selectCard"
                       ng-show="selectedText=='身份证号'" ng-change="loginBtnColor()"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle">密码:</div>
                <input id="pwd" type="{{showPassword?'text':'password'}}" class="rowInput" ng-model="pwd" placeholder="请输入密码" ng-change="loginPwd();loginBtnColor()"/>
                <span class="mainRowRight" ng-class="!showPassword?'icon-eye-blocked':'icon-eye dodgerBlue'" ng-click="showPassword=!showPassword"></span>
            </div>
            <div class="mainRow">
                <div class="rowTitle">
                    <input type="checkbox" id="noAccount">
                    <label for="noAccount" class="rememberPwdLabel">未注册只需输入手机号码</label>
                </div>
            </div>
        </div>
        <button type="submit" class="submitBtn submitBtnRed" ng-disabled="check">登录</button>
        <div class="mainBottom">
            <div class="floatLeft" ng-click="goPage('/account/forgotPassword')">找回密码</div>
            <div class="floatRight">
                <input type="checkbox" id="rememberPwd">
                <label for="rememberPwd" class="rememberPwdLabel">记住账户密码</label>
            </div>
        </div>
    </form>

    <div class="hideMenu" ng-show="errorLoginPanel">
        <div class="errorMain">
            <div class="errorMainRow">用户名或密码不正确</div>
            <div class="errorMainRow">
                <div class="errorMainBtn dodgerBlue" ng-click="errorLoginPanel=false">确认</div>
                <div class="errorMainBtn errorRed" ng-click="goPage('/account/forgotPassword')">找回密码</div>
            </div>
        </div>
    </div>
</div>