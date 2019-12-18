<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_belongMember_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/index')"></span>
        归属会员
        <span class="titleManage iconfont icon-erweima1" style="font-size: 26px;"
              ng-click="getQrcode()"></span>
    </div>
    <div class="overflowPC">
        <div class="storeCon" ng-repeat="user in memberList track by $index">
            <div ng-include="'/yzxfSeller_page/view/store/team_grid.jsp'"></div>
        </div>
    </div>
    <div class="loadMore">
        <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
             ng-click="more()">加载更多...
        </div>
        <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
    </div>

    <div class="hideMenu flex1 flex4 bgWhite" ng-show="showCode" style="z-index:99">
        <div class="title title-fixed">
            <span class="icon-left-1 iconfont titleBack" ng-click="showCode=false"></span>
            分享二维码
        </div>
        <div style="position: absolute;top: 60px;padding: 10px;color: #888;">新用户通过二维码注册，需通过微信打开国联普惠会员注册页面，点击右上角扫一扫按钮扫码注册</div>
        <div class="qrcode" style="width:80%"></div>
    </div>
</div>
