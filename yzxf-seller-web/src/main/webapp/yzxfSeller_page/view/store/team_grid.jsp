<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="storeCon-main">
    <img ng-src="{{iconImgUrl(user.icon)}}" src="/yzxfSeller_page/img/notImg02.jpg"/>
    <div class="textEllipsis storeCon-title">
        <span ng-if="!isEmpty2(user.name)" ng-bind="user.name"></span>
        <span ng-if="!isEmpty2(user.cardNo)" ng-bind="user.cardNo"></span>
        <span ng-if="!isEmpty2(user.mobile)" ng-class="isEmpty2(user.cardNo)?'':'notHigh'" ng-bind="' ('+user.mobile+')'"></span>
    </div>
    <div class="fl storeCon-info">
        <div ng-if="!isEmpty2(user.integralRate)" ng-bind="'积分率 '+user.integralRate+' %'"></div>
        <div ng-if="!isEmpty2(user.operateType)" ng-bind="user.operateType"></div>
        <div ng-if="!isEmpty2(user.isBindCard)" ng-class="user.isBindCard?'num':'num'" style="background: #00aee2;color: #fff;">已激活</div>
        <div ng-if="!isEmpty2(user.isRealName) || user.isRealName" style="background: #00aee2;color: #fff;">已认证</div>
        <div ng-if="!user.canUse" class="tag-canUse" style="background:orangered;color:#fff">禁用</div>
        <div ng-if="!isEmpty2(user.sex)" ng-bind="user.sex"></div>
    </div>
</div>
<div class="storeCon-address">
    <span class="notHigh" ng-if="!isEmpty2(user.phone)" ng-bind="'联系号码: '+user.phone"></span>
    <span class="notHigh" style="margin-left: 20px;" ng-if="!isEmpty2(user.contactPerson)" ng-bind="' 联系人: '+user.contactPerson+''"></span>
</div>
<div class="storeCon-address notHigh" ng-if="!isEmpty2(user.realArea) || !isEmpty2(user.realAddress)"
    ng-bind="'地址: '+user.realArea+user.realAddress"></div>
<div class="storeCon-address notHigh" ng-if="entityPage=='belongStore' && (!isEmpty2(user.area) || !isEmpty2(user.address))"
    ng-bind="'地址: '+user.area+user.address"></div>
<div class="storeCon-date">
    <span class="fl notHigh" style="margin-left: 10px;" ng-if="!isEmpty2(user.realName)" ng-bind="user.realName"></span>
    <span class="fr notHigh" ng-bind="'注册时间: '+showYFullTime(user.createTime)"></span>
</div>


<%--<div class="storeCon-main">--%>
    <%--<img ng-src="{{iconImgUrl(seller.icon)}}" src="/yzxfSeller_page/img/notImg02.jpg"/>--%>
    <%--<div class="textEllipsis storeCon-title" ng-bind="seller.name"></div>--%>
    <%--<div class="fl storeCon-info">--%>
        <%--<div class="storeCon-tag">--%>
            <%--<div ng-bind="'积分率 '+seller.integralRate+' %'"></div>--%>
            <%--<div ng-bind="seller.operateType"></div>--%>
            <%--<div ng-if="!seller.canUse" class="tag-canUse">禁用</div>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<span style="margin-right:10px;color: #888;" ng-bind="seller.phone"></span>--%>
            <%--<span style="color: #888;" ng-bind="seller.contactPerson"></span>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>
<%--<div class="storeCon-address" ng-bind="'地址: '+seller.area+seller.address"></div>--%>
<%--<div class="storeCon-date" ng-bind="showYFullTime(seller.createTime)"></div>--%>