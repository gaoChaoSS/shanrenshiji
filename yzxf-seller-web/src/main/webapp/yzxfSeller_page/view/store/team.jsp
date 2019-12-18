<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="store_team_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/store')"></span>
        {{titleName}}
        <span class="titleManage black textSize15 grayaaa" ng-click="goPage('/store/teamOrder')">收益</span>
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
</div>