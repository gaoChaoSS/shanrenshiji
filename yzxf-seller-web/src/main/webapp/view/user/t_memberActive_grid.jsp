<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%">
        <div class="belongArea1" ng-include="'/temp_new/agent_select.jsp'"></div>
    </div>
</div>

<div class="popSection">
    <div class="popTitle" style="margin-top:0">卡号</div>
    <div style="width:100%">
        <span>卡号:</span>
        <input type="text" ng-model="userInfo.cardNo"/>
        <%--<span ></span>--%>
    </div>
</div>

<div class="popSection flex2">
    <div class="popTitle">会员信息</div>
    <div style="width:100%">
        <span>手机号码:</span>
        <span ng-bind="userInfo.mobile"></span>
    </div>
    <div style="width:100%">
        <span>会员姓名:</span>
        <span ng-bind="userInfo.realName"></span>
    </div>
</div>

<%--管理员--%>
<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fr btn1" ng-click="checkSubmit=true">提交</button>
</div>
<div class="sectionHintBk" ng-show="checkSubmit">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="activeMember();checkSubmit=false">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkSubmit=false">否</button>
        </div>
    </div>
</div>