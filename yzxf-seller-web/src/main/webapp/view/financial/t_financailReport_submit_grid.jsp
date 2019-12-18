<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <div style="width:100%;padding:0">
        <div class="sectionTable">
            <div>
                <div style="width:20%">代理商</div>
                <div style="width:20%">开户总行</div>
                <div style="width:20%">开户支行</div>
                <div style="width:20%">支行城市</div>
                <div style="width:10%">户名</div>
                <div style="width:10%">入账金额</div>
            </div>
            <div ng-repeat="order in submitList" class="trBk" ng-show="order.$$selectedItem">
                <div style="width:20%" ng-bind="order.name"></div>
                <div style="width:20%" ng-bind="order.bankType"></div>
                <div style="width:20%" ng-bind="order.bankName"></div>
                <div style="width:20%" ng-bind="isNullText(order.bankProvince) +' '+isNullText(order.bankCity)"></div>
                <div style="width:10%" ng-bind="order.bankUser"></div>
                <div style="width:10%;color: red;" ng-bind="getMoney(order.incomeAccount)"></div>
            </div>
        </div>
    </div>
</div>
<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fr btn1" ng-click="setFlag('isSubmit')">提交</button>
</div>
<%--<div class="sectionPage" ng-hide="isNullPage2" style="position: absolute;left: 0;bottom: -40px;width: 100%;margin: 0;">--%>
    <%--<div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex2!=1">上一页</div>--%>
    <%--<div ng-show="isFirstPage2">--%>
        <%--<div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>--%>
        <%--<div class="fl lineH30px">......</div>--%>
    <%--</div>--%>
    <%--<div class="btn3" ng-repeat="page2 in pageList2" ng-bind="page2.num"--%>
         <%--ng-click="pageNumber(page2.num);pageCur(page2.num)"--%>
         <%--ng-class="pageIndex2==page2.num?'bgBlue tWhite':''"></div>--%>
    <%--<div ng-show="isLastPage2">--%>
        <%--<div class="fl lineH30px">......</div>--%>
        <%--<div class="btn3 fl marginLR5" ng-bind="totalPage2" ng-click="pageNumber(totalPage2)"></div>--%>
    <%--</div>--%>
    <%--<div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex2!=totalPage2">下一页</div>--%>
    <%--<div class="pageGo">--%>
        <%--<input type="text" placeholder="跳转" ng-model="pageGo2" />--%>
        <%--<button class="iconfont icon-right-1-copy" ng-click="pageNumber(pageGo2)"></button>--%>
    <%--</div>--%>
<%--</div>--%>
