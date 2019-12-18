<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="popSection flex2">
    <div class="popTitle">会员信息</div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="insureInfo.realName"></span>
    </div>
    <div>
        <span>会员卡号:</span>
        <span ng-bind="insureInfo.cardNo"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">投保信息</div>
    <div style="width:100%">
        <span>交易流水号:</span>
        <span ng-bind="insureInfo.transSeq"></span>
    </div>
    <div style="width:100%">
        <span>订单号:</span>
        <span ng-bind="insureInfo.contSerialNumber"></span>
    </div>
    <div style="width:100%">
        <span>保单号:</span>
        <span ng-bind="insureInfo.contNo"></span>
    </div>
    <div>
        <span>套餐产品编码:</span>
        <span ng-bind="'1802'"></span>
    </div>
    <div>
        <span>总保费:</span>
        <span ng-bind="insureInfo.prem+'元'"></span>
    </div>
    <div>
        <span>投保人:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.AppntName"></span>
    </div>
    <div>
        <span>投保状态:</span>
        <span ng-bind="getStatus(insureInfo.status)"></span>
    </div>
    <div ng-if="insureInfo.status!='end' && insureInfo.returnFlag=='fail'">
        <span>投保人:</span>
        <span ng-bind="insureInfo.desc"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">被保人信息</div>
    <div>
        <span>姓名:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.InsuredName"></span>
    </div>
    <div>
        <span>性别:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.InsuredSex=='1'?'男':'女'"></span>
    </div>
    <div>
        <span>身份证号码:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.InsuredIDNo"></span>
    </div>
    <div>
        <span>生日:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.InsuredBirthday"></span>
    </div>
    <div>
        <span>手机号码:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.InsuredMobile"></span>
    </div>
    <div style="width:100%">
        <span>地址:</span>
        <span ng-bind="insureInfo.applyItem.TransData.InputData.InsuredAddress"></span>
    </div>
</div>
<div class="sectionPage" ng-class="winCheck?'bottom0':''" ng-hide="isNullPage2" style="position: absolute;left: 0;bottom: -40px;width: 100%;margin: 0;">
    <div class="btn3" ng-click="$$filter.$$queryType=$$filter.$$queryTypeLast">返回</div>
</div>
