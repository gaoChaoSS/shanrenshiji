<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeEvent_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack black" ng-click="goBack()"></span>
        店铺活动
    </div>
    <div class="overflowPC" id="storeInfoScroll">
        <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span
                style="color:#268BBF;font-size: 15px;">{{totalActivityNumber}}</span>&nbsp;条</span>
        <div class="isNullBox" ng-show="eventList==null || eventList==''">
            <div class="iconfont icon-meiyouneirong"></div>没有内容
        </div>
        <div class="sectionMain sectionMainNotMargin mainRowTop10 eventDiv" ng-repeat="item in eventList">
            <div class="mainRowEvent">
                <div class="rowTitleEvent loessYellow">活动名称:</div>
                <div class="rowInputEvent" ng-bind="item.name"></div>
            </div>
            <div class="mainRowEvent">
                <div class="rowTitleEvent loessYellow">活动时间:</div>
                <div class="rowInputEvent" ng-bind="showDate(item.startTime)+' - '+showDate(item.endTime)"></div>
            </div>
            <div class="mainRowEvent">
                <div class="rowTitleEvent loessYellow">活动规则:</div>
                <div class="rowInputEvent lineHeight20 mainRowTop10" ng-bind="item.content"></div>
            </div>
            <div class="mainRowEvent">
                <div class="rowTitleEvent loessYellow">特殊说明:</div>
                <div class="rowInputEvent lineHeight20 mainRowTop10" ng-bind="item.explain"></div>
            </div>
            <div class="triangleEventBig" ng-class="colorCheck(item.$$goingNo)"></div>
            <%--<div class="triangleEventBig triangleEventBigCheck" ng-show="!item.$$goingNo"></div>--%>
            <div class="triangleEventSmall"></div>
            <div class="triangleEventText textSize12" ng-bind="item.$$goingCheck"></div>
            <button class="submitBtnEvent" ng-disabled="check" ng-show="item.$$goingNo!=2"
                    ng-click="closeEvent(item._id,item.isGoing)" ng-class="item.$$goingNo==1?'bgBlue':'bgYellow'">结束此活动</button>
            <button class="submitBtnEvent" style="background-color: #aaa" ng-disabled="check" ng-show="item.$$goingNo==2"
                    ng-click="delConfirm(item._id)">删除此活动</button>
            <div class="modifyBtnEvent" ng-show="item.$$goingNo==0" ng-click="goPage('store/addStoreEvent/eventId/'+item._id)">编辑</div>
        </div>

        <div class="loadMore">
            <div id="moreButton" ng-show="isActivityLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="getEventList()">加载更多...
            </div>
            <div ng-show="!isActivityLoadMore" style="font-size: 15px;color: #999;">没有更多</div>
        </div>
    </div>
    <div class="hideMenu" ng-show="menuCheck">
        <div class="enterPsw" style="height: 160px">
            <div class="textCenter lineHeight50" style="line-height: 130px;font-size: 25px">确定删除该活动吗</div>
            <div class="selectDiv">
                <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="deleteEvent(delId)">确认</div>
                <div style="border-left: 0;border-bottom: 0" ng-click="menuCheck=false">取消</div>
            </div>
        </div>
    </div>
    <div class="hideCommodityBtn bgWhite rowBorderTop" ng-click="goPage('/store/addStoreEvent')">
        <span class="icon-llalbumshopselectorcreate iconfont limeGreen textSize18"></span>
        <span class="notHigh">新增最新活动</span>
    </div>

</div>