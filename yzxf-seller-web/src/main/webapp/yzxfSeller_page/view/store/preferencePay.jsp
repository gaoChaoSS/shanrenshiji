<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_preferencePay_Ctrl"
     class="d_content title_section form_section index_page_globalDiv order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack black" ng-click="goBack()"></span>
        优惠买单
    </div>
    <div class="overflowPC">
        <%--<div class="payTuoyuan bgWhite"></div>--%>
        <div class="pay2DCodeDiv bgWhite">
            <div class="success textSize16">扫我有惊喜</div>
            <div class="pay2DCodeDivImg">
                <img src="/yzxfSeller_page/img/2DCode.png" alt="">
            </div>
        </div>
        <div class="mainRowTitlePay errorRed textCenter">/&nbsp;&nbsp;最近活动&nbsp;&nbsp;/</div>
        <%--活动列表--%>
            <div class="isNullBox" ng-show="eventList==null || eventList==''">
                <div class="iconfont icon-meiyouneirong"></div>
                没有内容
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
                <div class="rowTitleEvent loessYellow">活动内容:</div>
                <div class="rowInputEvent lineHeight20 mainRowTop10" ng-bind="item.content"></div>
            </div>
            <div class="triangleEventBig" ng-class="colorCheck(item.$$goingNo)"></div>
            <%--<div class="triangleEventBig triangleEventBigCheck" ng-show="!item.$$goingNo"></div>--%>
            <div class="triangleEventSmall"></div>
            <div class="triangleEventText textSize12">进行中</div>
        </div>
    </div>


</div>