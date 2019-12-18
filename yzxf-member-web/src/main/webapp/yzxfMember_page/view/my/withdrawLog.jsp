<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .my_team_Ctrl .numCon {
        padding: 10px;
        background: #fff;
        /*box-shadow: 0 1px 1px #ccc*/
    }

    .my_team_Ctrl .itemCon {
        margin-top: 5px;
        box-shadow: 0 1px 1px #ccc;
        padding: 0 10px;
        background: #fff;
    }

    .my_team_Ctrl .itemCon > div div {
        height: 30px;
        line-height: 30px;
    }

    .my_team_Ctrl .itemTime {
        position: absolute;
        bottom: 0;
        right: 10px;
    }

    .my_team_Ctrl .numTeam {
        position: absolute;
        right: 10px;
        top: 5px;
        color: #888;
    }
</style>
<div ng-controller="my_withdrawLog_Ctrl" class="d_content form_section title_section my_team_Ctrl" style="padding-top: 50px;">
    <div class="title titleFixed">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        奖励积分
        <span class="titleManage iconfont icon-erweima1" style="font-size: 26px;"
              ng-click="getQrcode()" ng-if="$$myInfo.isBindCard"></span>
    </div>

    <div class="overflowPC">
        <div class="sectionMain" style="position: relative;box-shadow: 0 1px 1px #ccc;margin:0">
            <div class="mainRowSelect textCenter mainRowBottom10" style="margin: 8px;border: 0;float:none;">
            <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                  ng-bind="showDate(startDateTime)"></span>
                ——
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                      ng-bind="showDate(endDateTime)"></span>
            </div>
        </div>

        <div style="margin:15px">
            <span>共</span>
            <span class="high" ng-bind="dataPage.totalNum">0</span>笔，
            <span>累计提现</span>
            <span class="high" ng-bind="getMoney(dataPage.totalWithdraw)">0</span>分，
            <span>累计手续费</span>
            <span class="high" ng-bind="getMoney(dataPage.totalFee)">0</span>分
        </div>

        <div ng-repeat="item in team" class="itemCon">
            <div class="_flex-between" style="height:35px">
                <div ng-bind="'单号：'+item.orderNo"></div>
                <div class="notHigh" ng-bind="showDate(item.endTime)"></div>
            </div>
            <div class="_flex-between" style="height:35px">
                <div ng-bind="item.bankName"></div>
                <div ng-bind="item.bankId"></div>
            </div>
            <div class="_flex-between" style="height:35px">
                <div class="high" ng-bind="'转账积分：'+getMoney(item.withdrawMoney)"></div>
                <div class="notHigh" ng-bind="'手续费：'+getMoney(item.fee)"></div>
            </div>

        </div>
    </div>

    <div class="loadMore">
        <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
             ng-click="more()">加载更多...
        </div>
        <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
    </div>
</div>