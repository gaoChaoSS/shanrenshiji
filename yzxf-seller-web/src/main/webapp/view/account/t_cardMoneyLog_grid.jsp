<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:25%">归属</div>
    <div style="width:25%">会员卡号</div>
    <div style="width:25%">会员姓名</div>
    <div style="width:25%">激活时间</div>
</div>
<div ng-repeat="card in dataPage.items"
    class="trBk" ng-class="dataPage.$$selectedItem._id==card._id?'selected':''"
    ng-click="dataPage.$$selectedItem=card">
    <div style="width:25%" ng-bind="card.factorName==null?'平台':card.factorName"></div>
    <div style="width:25%" ng-bind="card.memberCardId"></div>
    <div style="width:25%" ng-bind="card.memberName"></div>
    <div style="width:25%" ng-bind="showYFullTime(card.activeTime)"></div>
</div>