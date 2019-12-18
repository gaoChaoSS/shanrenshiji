<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="other_special_Ctrl" class="form_section title_section store_panel order_panel form_section">
    <div class="title titleRedBottom" style="margin-bottom: 0px;">
        <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBack()"></span>
        <span class="textSize18 whitefff" ng-bind="titleName"></span>
    </div>
    <div class="overflowPC" id="specialScroll">
        <div style="width:100%;background-color: #fff;height: 45px;padding: 10px 0">
            <div style="float: left;width: 50%;text-align: center;border-right: 1px solid #ccc;"
                ng-click="goPage('/store/operateType/operateUrl/operateSpecial')" ng-bind="selectedOperate"></div>
            <div style="float: right;width: 50%;text-align: center;">
                <span ng-click="saleCountMore=!saleCountMore;query()" ng-bind="saleCountMore?'销量&darr;':'销量&uarr;'"></span>
            </div>
        </div>
        <%--商品列表--%>
        <div ng-include="productInclude"></div>
    </div>
</div>