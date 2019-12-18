<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection" ng-show="userInfo.cardNo!=null && userInfo.cardNo!=''">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%">
        <span>归属机构:</span>
        <span ng-bind="getBelongArea(agentNameAll,userInfo.cardNo)"></span>
    </div>
</div>
<div class="popSection flex2" ng-show="adminCheck">
    <div class="popTitle">权限管理</div>
    <div>
        <span>修改状态:</span>
            <span class="iconfont font25px"
                  ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"
                  ng-click="userInfo.canUse=!userInfo.canUse"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">会员信息</div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="userInfo.realName"></span>
    </div>
    <div>
        <span>会员性别:</span>
        <span ng-bind="getSex(userInfo.sex)"></span>
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
        <span>常用邮箱:</span>
        <span ng-bind="userInfo.email"></span>
    </div>
    <div>
        <span>身份证号:</span>
        <span ng-bind="userInfo.idCard"></span>
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
<div class="popSection flex2">
    <div class="popTitle">会员账户</div>
    <div>
        <span>线上交易总额:</span>
        <span ng-bind="getMoney(memberAccount.cashOnlineCount)"></span>
    </div>
    <div>
        <span>线下交易总额:</span>
        <span ng-bind="getMoney(memberAccount.cashOfflineCount)"></span>
    </div>
    <div>
        <span>总交易额:</span>
        <span ng-bind="getMoney(memberAccount.totalConsume)"></span>
    </div>
    <div>
        <span>充值总额:</span>
        <span ng-bind="getMoney(memberAccount.rechargeCount)"></span>
    </div>
    <div>
        <span>目前余额:</span>
        <span ng-bind="getMoney(memberAccount.cashCount)"></span>
    </div>
    <div>
        <span>可提现余额:</span>
        <span ng-bind="getMoney(memberAccount.canWithdrawMoney)"></span>
    </div>
    <div>
        <span>已使用余额:</span>
        <span ng-bind="getMoney(memberAccount.cashCountUse)"></span>
    </div>
    <div>
        <span>意外已投总和:</span>
        <span ng-bind="getMoney(memberAccount.accidentCount)"></span>
    </div>
    <div>
        <span>养老已投总和:</span>
        <span ng-bind="getMoney(memberAccount.insureCountUse)"></span>
    </div>
    <div>
        <span>养老可投总和:</span>
        <span ng-bind="getMoney(memberAccount.insureCount)"></span>
    </div>
</div>
<div class="popSection">
    <div class="popTitle">实体卡</div>
    <div>
        <span>绑定实体卡:</span>
        <span ng-bind="userInfo.cardNo" ng-if="userInfo.cardNo!=null"></span>
        <span class="notHigh" ng-if="userInfo.cardNo==null">未绑定</span>
    </div>
    <div style="width: 50%;float: left">
        <span>激活卡日期:</span>
        <span ng-bind="showYFullTime(activeTime)" ng-if="activeTime!=null"></span>
        <span class="notHigh" ng-if="activeTime==null">暂无</span>
    </div>
    <div style="width: 50%;float: left">
        <span>更换卡日期:</span>
        <span ng-bind="showYFullTime(exchangeTime)" ng-if="exchangeTime!=null"></span>
        <span class="notHigh" ng-if="exchangeTime==null">尚未换卡</span>
    </div>
</div>
<%--管理员--%>
<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-show="adminCheck">
    <button class="fr btn1" ng-click="checkSubmit=true">提交</button>
</div>
<div class="sectionHintBk" ng-show="checkSubmit">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="setMemberCanUse();checkSubmit=false">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkSubmit=false">否</button>
        </div>
    </div>
</div>

