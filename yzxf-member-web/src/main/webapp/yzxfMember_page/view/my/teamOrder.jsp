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
<div ng-controller="my_teamOrder_Ctrl" class="d_content form_section title_section my_team_Ctrl" style="padding-top: 50px;">
    <div class="title titleFixed">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        我的红包
        <span class="titleManage iconfont icon-erweima1" style="font-size: 26px;"
              ng-click="getQrcode()" ng-if="$$myInfo.isBindCard"></span>
    </div>

    <div class="overflowPC">
        <div style="padding-top:10px;background:#fff;position:relative">
            <div class="numCon">
                <div class="num textCenter" style="line-height: 1;font-size: 30px;"
                     ng-bind="'¥ '+getMoney(countItem.total)"></div>
                <div class="notHigh textCenter" ng-bind="'累计红包 ('+countItem.totalNum+'笔)'"></div>
            </div>
            <div class="_flex3">
                <div class="numCon">
                    <div class="num textCenter" style="line-height: 1;font-size: 22px;"
                         ng-bind="'¥ '+getMoney(countItem.month)"></div>
                    <div class="notHigh textCenter" ng-bind="'当月奖励红包 ('+isNullZero(countItem.monthNum)+'笔)'"></div>
                </div>
                <div class="numCon">
                    <div class="num textCenter" style="line-height: 1;font-size: 22px;"
                         ng-bind="'¥ '+getMoney(countItem.day)"></div>
                    <div class="notHigh textCenter" ng-bind="'今日奖励红包 ('+isNullZero(countItem.dayNum)+'笔)'"></div>
                </div>
            </div>
            <div class="numTeam" ng-click="goPage('/my/withdraw')" style="left: 10px;right:auto;">红包提现</div>
            <div class="numTeam" ng-click="goPage('/my/team')">团队成员</div>
        </div>

        <div class="sectionMain" style="position: relative;box-shadow: 0 1px 1px #ccc;margin:0">
            <div class="mainRowSelect textCenter mainRowBottom10" style="margin: 8px;border: 0;float:none;">
            <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                  ng-bind="showDate(startDateTime)"></span>
                ——
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                      ng-bind="showDate(endDateTime)"></span>
            </div>
        </div>

        <div ng-repeat="item in team" class="itemCon _flex5" style="align-items: center;position:relative">
            <img class="mod-circleImg1" style="width: 50px;height: 50px;margin: 10px 15px;" ng-src="{{iconImgUrl(item.icon)}}">
            <div>
                <div>
                    <span ng-bind="getMobile(item.mobile)"></span>
                    <span class="notHigh" ng-if="!isEmpty2(item.realName)" ng-bind="'( '+getRealName(item.realName)+' )'"></span>
                    <%--<span class="notHigh" ng-bind="item.orderNo"></span>--%>
                </div>
                <div class="high" ng-bind="'¥ '+getMoney(item.orderCash)"></div>
                <div class="notHigh itemTime" ng-bind="showDate(item.createTime)"></div>
            </div>
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