<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="other_agreeAll_Ctrl" class="d_content title_section form_section">
    <div class="title titleRedBottom">
        相关协议
        <span class="icon-share01 iconfont titleRight whitefff"></span>
    </div>
    <div class="sectionMain">
        <div class="mainRow" ng-repeat="menu in menuList" ng-click="getMenu($index)">
            <div class="rowTitle" ng-bind="menu.title"></div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>

    <div class="hideMenu" ng-show="curMenu!=null" style="background:#fff;z-index:999;overflow:auto;">
        <div class="title titleRedBottom" style="position: fixed;top: 0;left: 0;">
            <span class="icon-left-1 iconfont titleBack whitefff" ng-click="closeMenu()"></span>
            {{curMenu.title}}
        </div>
        <div class="article overflowPC" style="height:630px;text-align: justify;margin-top: 50px;"
             ng-include="curMenu.include">
        </div>
    </div>
</div>