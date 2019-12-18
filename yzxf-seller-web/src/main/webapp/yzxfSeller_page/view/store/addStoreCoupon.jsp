<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_addStoreCoupon_Ctrl" class="d_content title_section form_section order_panel">
    <form ng-submit="submitBtn()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            <span class="textSize18">创建卡券</span>
            <button type="submit" class="titleManage black textSize15 bgWhite">发布</button>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">卡券名称</div>
                <input type="text" class="rowInput" ng-model="name" placeholder="请输入卡券名称"/>
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
                <div class="rowTitle">使用规则</div>
                <div class="rowInput">
                    满
                    <input type="text" class="rowInput2" ng-model="condition"/>
                    元抵扣
                    <input type="text" class="rowInput2" ng-model="value"/>元
                </div>
                <%--<input type="text" ng-model="condition" class="rowInput" placeholder="满多少元抵扣"/>--%>
            </div>
        </div>
        <%--<div class="sectionMain">--%>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">抵扣金额</div>--%>
                <%--<input type="text" ng-model="value" class="rowInput" placeholder="抵扣金额"/>--%>
            <%--</div>--%>
        <%--</div>--%>
    </form>
</div>