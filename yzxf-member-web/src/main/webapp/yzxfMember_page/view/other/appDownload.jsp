<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="other_appDownload_Ctrl" class="d_content title_section form_section">
    <div class="title titleRedBottom">
        <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBackFun()"></span>
        {{selectedItem.name}}下载
        <span class="icon-share01 iconfont titleRight whitefff" ng-click="errorLoginPanel=true"></span>
    </div>
    <div class="sectionMain" ng-show="showMenu">
        <div class="mainRow" ng-repeat="item in appList"
             ng-click="setQrcode($index)">
            <div class="rowTitle rowTitleBtn" ng-bind="item.name+'下载'"></div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>

    <div ng-show="!showMenu">
        <div class="lineHeight30 textCenter">扫一扫下载</div>
        <div class="qrcode"></div>
        <div class="lineHeight50 textCenter" ng-click="goUrl(selectedItem.url)">
            <span>点击下载</span>
            <span class="colorRed1" ng-bind="selectedItem.name"></span>
        </div>
    </div>
</div>