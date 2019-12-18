<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="user_agentInfo_Ctrl" style="width: 100%; height: 100%;">
    <div class="section1">
        <span class="tLineHight45 selectInputMarginL30 tGry888 textSize15">代理商信息</span>
        <button class="btn1 fr" ng-click="goBack()">返回</button>
        <button class="btn1 bkColorGreen1 fr" ng-click="modifyCheck=true" ng-show="!agent.canUse">审核通过</button>
        <button class="btn1 bgOrange fr" ng-click="modifyCheck=true" ng-show="agent.canUse">审核不通过</button>
    </div>
    <div class="section1 sectionUserInfo">
        <div><span>代理商:</span><span ng-bind="agent.name"></span></div>
        <div><span>联系人:</span><span ng-bind="agent.contactPerson"></span></div>
        <div><span>身份证:</span><span ng-bind="agent.realCard"></span></div>
        <div><span>申请时间:</span><span ng-bind="agent.applyTime"></span></div>
        <div><span>联系号码:</span><span ng-bind="agent.phone"></span></div>
        <div><span>所在地址:</span><span ng-bind="agent.area+agent.address"></span></div>
    </div>

    <div class="section1 sectionImg">
        <div class="sectionTitle1">身份证正面照</div>
        <img class="marginLeft140" ng-src="{{iconImgUrl(agent.idCardImgFront)}}" />
    </div>
    <div class="section1 sectionImg">
        <div class="sectionTitle1">身份证背面照</div>
        <img class="marginLeft140" ng-src="{{iconImgUrl(agent.idCardImgBack)}}" />
    </div>
    <div class="section1 sectionImg">
        <div class="sectionTitle1">手持身份证照</div>
        <img class="marginLeft140" ng-src="{{iconImgUrl(agent.idCardImgHand)}}" />
    </div>
    <div class="section1 sectionImg">
        <div class="sectionTitle1">营业执照</div>
        <img class="marginLeft140" ng-src="{{iconImgUrl(agent.businessLicense)}}" />
    </div>

    <div class="sectionHintBk" ng-show="modifyCheck">
        <div class="sectionHint">
            <div class="lineH100px" ng-show="!agent.canUse">是否启用 <span class="colorBlue1" ng-bind="agent.name"></span> 代理商?</div>
            <div class="lineH100px" ng-show="agent.canUse"><span class="colorBlue1" ng-bind="agent.name"></span> 代理商已经通过审核,是否禁用?</div>
            <div class="flex1">
                <button class="btn1" ng-click="submitModify()">是</button>
                <button class="btn1 bkColorRed1" ng-click="modifyCheck=false">否</button>
            </div>
        </div>
    </div>
</div>
