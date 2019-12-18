<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div>
    <div style="width:10%">关联账号</div>
    <div style="width:20%">会员</div>
    <div style="width:20%">商家</div>
    <div style="width:20%">服务站</div>
    <div style="width:20%">代理商</div>
    <div style="width:10%">创建日期</div>
</div>
<div ng-repeat="user in dataPage.items" class="trBk"
     ng-class="dataPage.$$selectedItem.userId==user.userId?'selected':''"
     ng-click="dataPage.$$selectedItem=user;$event.stopPropagation()" >
    <div style="width:10%" ng-bind="user.userId"></div>
    <div style="width:20%" ng-bind="!isEmpty2(user.memberMobile)?user.memberMobile:''+(!isEmpty2(user.memberName)?'('+user.memberName+')':'')"></div>
    <div style="width:20%" ng-bind="user.sellerName"></div>
    <div style="width:20%" ng-bind="user.factorName"></div>
    <div style="width:20%" ng-bind="user.agentName"></div>
    <div style="width:10%" ng-bind="showYFullTime(user.createTime)"></div>
</div>