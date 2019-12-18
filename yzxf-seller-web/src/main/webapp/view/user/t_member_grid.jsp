<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div>
    <div style="width:25%">归属</div>
    <div style="width:10%">会员姓名</div>
    <div style="width:10%">状态</div>
    <div style="width:10%">实名认证</div>
    <div style="width:15%">会员卡</div>
    <div style="width:15%">会员手机</div>
    <div style="width:15%">创建日期</div>
</div>
<div ng-repeat="member in dataPage.items" class="trBk"
    ng-class="dataPage.$$selectedItem._id==member._id?'selected':''"
    ng-click="dataPage.$$selectedItem=member;$event.stopPropagation()" >
    <div style="width:25%" ng-bind="getBelongArea(member.belongArea,member.cardNo)"></div>
    <div style="width:10%" ng-bind="member.realName"></div>
    <div style="width:10%" ng-bind="member.canUse?'有效':'禁用'" ng-class="member.canUse?'colorBlue1':'colorRed2'"></div>
    <div style="width:10%" ng-bind="member.isRealName?'已认证':'未认证'" ng-class="member.isRealName?'colorBlue1':'colorRed2'"></div>
    <div style="width:15%" ng-bind="member.cardNo"></div>
    <div style="width:15%" ng-bind="member.mobile"></div>
    <div style="width:15%" ng-bind="showYFullTime(member.createTime)"></div>
</div>