<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="my_team_Ctrl" class="d_content form_section title_section" style="padding-top: 50px;">
    <div class="title titleFixed">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        {{titleName}}
        <span class="titleManage iconfont icon-erweima1" style="font-size: 26px;"
              ng-click="getQrcode()" ng-if="$$myInfo.isBindCard"></span>
    </div>

    <div ng-if="!isEmpty2(member)">
        <div class="notHigh" style="margin-left:10px">我的邀请人</div>
        <div class="storeCon" ng-include="'/yzxfMember_page/view/my/team_grid.jsp'"></div>
    </div>

    <div style="padding:0 10px;overflow:hidden">
        <span class="fl notHigh">我的分享人</span>
        <span class="fr notHigh" ng-click="goPage('/my/teamOrder')">查看奖励积分</span>
    </div>
    <div class="overflowPC" >
        <div class="storeCon" ng-repeat="member in memberList track by $index">
            <div ng-include="'/yzxfMember_page/view/my/team_grid.jsp'"></div>
        </div>
    </div>
    <div class="loadMore">
        <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
             ng-click="more()">加载更多...
        </div>
        <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
    </div>

    <div class="hideMenu _flex1 bgWhite" ng-show="showCode">
        <div class="title titleFixed">
            <span class="icon-left-1 iconfont titleBack" ng-click="showCode=false"></span>
            团队分享二维码
        </div>
        <div class="share-text">新用户通过团队二维码注册，需通过微信打开国联普惠会员注册页面，点击右上角扫一扫按钮扫码注册</div>
        <div class="qrcode" style="width:80%"></div>
    </div>
</div>