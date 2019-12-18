<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="home_cardRecords_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/index')"></span>
        发卡记录
    </div>
    <div class="overflowPC">
        <div class="queryTitle">
            <div class="rowTitle3">会员卡号:</div>
            <input type="text" ng-model="memberCard" placeholder="请输入会员卡号查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">电话号码:</div>
            <input type="text" ng-model="memberPhone" placeholder="请输入电话号码查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">会员姓名:</div>
            <input type="text" ng-model="memberName" placeholder="请输入会员姓名查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">身份证:</div>
            <input type="text" ng-model="memberIdCard" placeholder="请输入身份证查询"/>
        </div>
        <div class="queryTitle">
            <div class="rowTitle3">激活日期:</div>
            <div class="mainRowLeft80">
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/startDate/value/'+startDateTime)"
                      ng-bind="isAll?'&nbsp;':showDate(startDateTime)" style="margin:0 5px 0 0;min-width: 97px;"></span>—
                <span class="squareBtn" ng-click="goPage('/pop/dateSelect/type/endDate/value/'+endDateTime)"
                      ng-bind="showDate(endDateTime)" style="margin:0;min-width: 97px;"></span>
            </div>
        </div>
        <div class="clearDiv"></div>
        <div class="queryTitle">
            <div class="flTitle">
                <button ng-click="getCardList()">查询</button>
            </div>
            <div class="frTitle">
                <button ng-click="getCardListAll()">查询所有</button>
            </div>
        </div>
        <div class="clearDiv"></div>
        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totalNumer)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="CardLogList==null || CardLogList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="mainRowTop10" ng-repeat="item in CardLogList">
            <div class="bgWhite paddingBottomNot borderBottomGray">
                <div class="mainRow mainRowNotBorderBottom">
                    <div class="rowTitle widthPercent20">用户名</div>
                    <div class="rowInput black"
                         ng-bind="(item.realName==null||item.realName=='')?'匿名':item.realName"></div>

                </div>
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">卡号</div>
                    <div class="rowInput black" ng-bind="item.memberCardId"></div>
                </div>
            </div>
            <div class="overflowHidden lineHeight50 textIndent30 textRight bgWhite">
                <span class="mainRowRight10" ng-bind="showYFullTime(item.createTime)"></span>
                <span class="iconfont icon-fa textSize25 colorBlue mainRowRight20"></span>
            </div>
        </div>
        <div class="loadMore">
            <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>