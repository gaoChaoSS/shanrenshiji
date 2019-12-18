<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="my_settings_Ctrl" class="d_content form_section title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/my')"></span>
        设置
    </div>
    <div class="sectionMain">
        <div class="mainRow" ng-click="goPage('/my/modifyPassword')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-mima iconfont iconBig"></span>
                密码修改
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <%--<div class="mainRow" ng-click="goPage('/account/forgotPassword')">--%>
            <%--<div class="rowTitle rowTitleBtn">--%>
                <%--<span class="icon-mima1 iconfont iconBig"></span>--%>
                <%--密码找回--%>
            <%--</div>--%>
            <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
        <%--</div>--%>
        <div class="mainRow" ng-click="goPage('/my/realName')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-shimingrenzheng iconfont iconBig"></span>
                实名认证
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goBindWechat()" ng-show="isBindWechat">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-shimingrenzheng iconfont iconBig"></span>
                微信绑定
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow" ng-click="goPage('/my/agreement')">
            <div class="rowTitle rowTitleBtn">
                <span class="icon-icon0732xieyi iconfont iconBig"></span>
                协议展示
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>
    <div class="sectionMain mainBottom">
        <div class="mainRow submitBtnTextCenter textSize18 deepRed" ng-click="logout()">
            退出登录
        </div>
    </div>
</div>