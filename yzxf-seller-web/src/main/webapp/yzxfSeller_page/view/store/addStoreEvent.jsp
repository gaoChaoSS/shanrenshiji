<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_addStoreEvent_Ctrl" class="d_content title_section form_section order_panel">
    <form ng-submit="submitBtn()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            <span ng-bind="titleEvent" class="textSize18">添加活动</span>
            <button type="submit" class="titleManage black textSize15 bgWhite">保存</button>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">活动名称</div>
                <input type="text" class="rowInput" ng-model="name" placeholder="请输入此次活动的名称"/>
            </div>
        </div>
        <div class="sectionMain">
            <div>
                <div class="lineHeight40 textCenter">选择日期</div>
                <div class="lineHeight40 textCenter mainRowBottom10">
                    <span class="squareBtn" ng-click="selectDateAction(startDateTime,'startDate')"
                          ng-bind="showDate(startDateTime)+' 00:00'"></span>
                    ——
                    <span class="squareBtn" ng-click="selectDateAction(endDateTime,'endDate')"
                          ng-bind="showDate(endDateTime)+' 23:59'"></span>
                </div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">优惠规则</div>
                <input type="text" ng-model="content" class="rowInput" placeholder="请输入此次活动的规则"/>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">特殊说明</div>
                <input type="text" ng-model="explain" class="rowInput" placeholder="说明"/>
            </div>
        </div>
    </form>
</div>