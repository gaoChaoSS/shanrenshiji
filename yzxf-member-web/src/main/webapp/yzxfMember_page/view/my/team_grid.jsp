<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="storeCon-main">
    <img ng-src="{{iconImgUrl(member.icon)}}" src="/yzxfSeller_page/img/notImg02.jpg"/>
    <div class="textEllipsis storeCon-title" ng-bind="getItemTitle(member)"></div>
    <div class="fl storeCon-info">
        <div ng-if="!isEmpty2(member.isBindCard)" ng-class="member.isBindCard?'num':'num'" style="background: #00aee2;color: #fff;">已激活</div>
        <div ng-if="!isEmpty2(member.isRealName) || member.isRealName" style="background: #00aee2;color: #fff;">已认证</div>
        <div ng-if="!member.canUse" class="tag-canUse">禁用</div>
        <div ng-if="!isEmpty2(member.sex)" ng-bind="member.sex"></div>
    </div>
</div>
<div class="storeCon-date">
    <span class="fl notHigh" style="margin-left: 15px;" ng-if="!isEmpty2(member.realName)" ng-bind="getRealName(member.realName)"></span>
    <span class="fr notHigh" ng-bind="'注册时间: '+showYFullTime(member.createTime)"></span>
</div>