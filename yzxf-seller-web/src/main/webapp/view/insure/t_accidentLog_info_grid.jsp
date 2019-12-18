<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection" ng-show="userInfo.cardNo!=null && userInfo.cardNo!=''">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%">
        <span>归属机构:</span>
        <span ng-bind="agentNameAll"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle" ng-bind="titleCheck"></div>
    <div>
        <span>投保编号:</span>
        <span ng-bind="orderInfo.insureNO" ng-if="!isNullCheck(dataPage.$$selectedItem.insureNO)"></span>
        <input type="text" ng-model="orderInfo.insureNO" ng-if="isNullCheck(dataPage.$$selectedItem.insureNO)"/>
    </div>
    <div>
        <span>投保公司:</span>
        <span ng-bind="orderInfo.company" ng-if="!isNullCheck(dataPage.$$selectedItem.company)"></span>
        <input type="text" ng-model="orderInfo.company" ng-if="isNullCheck(dataPage.$$selectedItem.company)"/>
    </div>
    <div>
        <span>投保时间:</span>
        <span ng-bind="showYFullTime(dataPage.$$selectedItem.createTime)"></span>
    </div>
    <div>
        <span>投保金额:</span>
        <span ng-bind="dataPage.$$selectedItem.money+'元'"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">会员信息</div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="userInfo.realName"></span>
    </div>
    <div ng-show="!adminCheck">
        <span>当前状态:</span>
        <span class="iconfont font25px" ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></span>
    </div>
    <div>
        <span>手机号码:</span>
        <span ng-bind="userInfo.mobile"></span>
    </div>
    <div>
        <span>身份证号:</span>
        <span ng-bind="userInfo.idCard"></span>
    </div>
    <div>
        <span>常用邮箱:</span>
        <span ng-bind="userInfo.email"></span>
    </div>
    <div style="width:100%">
        <span>身份证居住地:</span>
        <span ng-bind="userInfo.realArea+userInfo.realAddress"></span>
    </div>
    <div style="width:100%" ng-show="userInfo.area!=null || userInfo.address!=null">
        <span>当前居住地:</span>
        <span ng-bind="(userInfo.area==null?'':userInfo.area)+(userInfo.address==null?'':userInfo.address)"></span>
    </div>
</div>
<%--管理员--%>
<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-if="agent.level==1 && titleCheck=='请补全投保单'">
    <button class="fr btn1" ng-click="checkWin()">提交</button>
</div>
<div class="sectionHintBk" ng-show="checkSubmit">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="setAccidentLog();checkWin()">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkWin()">否</button>
        </div>
    </div>
</div>

