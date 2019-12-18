<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="home_surplusMemberCard_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/index')"></span>
        未激活会员卡号段
    </div>
    <div class="overflowPC">
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
                <div class="mainRow">
                    <div class="floatLeft">
                        <div class="rowTitle widthPercent20">起始卡号</div>
                        <div class="rowInput black" ng-bind="cardNO(item.startCardNo)"></div>
                    </div>
                    <div class="floatLeft">
                        <div class="rowTitle widthPercent20">终止卡号</div>
                        <div class="rowInput black" ng-bind="cardNO(item.endCardNo)"></div>
                    </div>
                    <div class="floatLeft">
                        <div class="rowTitle widthPercent20">数量</div>
                        <div class="rowInput black" ng-bind="item.cardNum"></div>
                    </div>
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