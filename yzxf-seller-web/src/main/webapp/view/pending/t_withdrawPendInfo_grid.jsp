<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<%--<div class="popSection" ng-show="dataPage.$$selectedItem.explain!=null && dataPage.$$selectedItem.explain!=''">--%>
    <%--<div class="popTitle" style="margin-top:0">审批说明</div>--%>
    <%--<div style="width:100%;white-space: inherit">--%>
        <%--<span>说明:</span>--%>
        <%--<span ng-bind="dataPage.$$selectedItem.explain"></span>--%>
    <%--</div>--%>
<%--</div>--%>
<div class="popSection" ng-show="agentNameAll!='' && agentNameAll!=null">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%;white-space: inherit">
        <span>归属机构:</span>
        <span ng-bind="agentNameAll"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">银行凭证</div>
    <div style="width:100%">
        <span>银行凭证码:</span>
        <span ng-bind="dataPage.$$selectedItem.voucher" ng-show="entityTitle=='提现记录'"></span>
        <input type="text" ng-model="dataPage.$$selectedItem.voucher" ng-show="entityTitle=='提现申请'">
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">提现信息</div>
    <div>
        <span>申请时间:</span>
        <span ng-bind="showYFullTime(dataPage.$$selectedItem.createTime)"></span>
    </div>
    <div>
        <span>状态:</span>
        <span ng-bind="getStatus(dataPage.$$selectedItem.status)"></span>
    </div>
    <div>
        <span>提现金额:</span>
        <span ng-bind="dataPage.$$selectedItem.withdrawMoney+'元'"></span>
    </div>
    <div>
        <span>手续费:</span>
        <span ng-bind="dataPage.$$selectedItem.fee+'元'"></span>
    </div>
    <div>
        <span>总额:</span>
        <span ng-bind="dataPage.$$selectedItem.withdrawMoney+dataPage.$$selectedItem.fee+'元'"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">银行卡信息</div>
    <div>
        <span>银行账号:</span>
        <span ng-bind="dataPage.$$selectedItem.bankId"></span>
    </div>
    <div>
        <span>开户行:</span>
        <span ng-bind="dataPage.$$selectedItem.bankName"></span>
    </div>
    <div>
        <span>银行持卡人:</span>
        <span ng-bind="dataPage.$$selectedItem.bankUser"></span>
    </div>
    <div>
        <span>持卡人电话:</span>
        <span ng-bind="dataPage.$$selectedItem.bankUserPhone"></span>
    </div>
    <div>
        <span>持卡人身份证:</span>
        <span ng-bind="dataPage.$$selectedItem.bankUserCardId"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">用户资料</div>
    <div>
        <span>用户名称:</span>
        <span ng-bind="userInfo.name"></span>
    </div>
    <div>
        <span>联系人:</span>
        <span ng-bind="userInfo.contactPerson"></span>
    </div>
    <div>
        <span>联系电话:</span>
        <span ng-bind="userInfo.phone==null?userInfo.mobile:userInfo.phone"></span>
    </div>
    <div>
        <span>身份证:</span>
        <span ng-bind="userInfo.realCard"></span>
    </div>
    <div style="width:100%;white-space: inherit">
        <span>当前地址:</span>
        <span ng-bind="userInfo.area+userInfo.address"></span>
    </div>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-show="entityTitle == '提现申请'">
    <button class="fr btn1" ng-click="checkBtnFun(0)">受理</button>
</div>

<div class="sectionHintBk" ng-show="checkBtn[0]">
    <div class="sectionHint">
        <div class="lineH100px textCenter">是否受理该提现?</div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm()">确认受理</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(2)">取消</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[1]">
    <div class="sectionHint">
        <div class="lineH50px textCenter colorBlue1">受理成功</div>
        <div class="lineH50px" style="font-size:14px">请在3个工作日内完成银行提现</div>
        <div class="flex1">
            <button class="btn1 bkColorRed1" ng-click="closeWin()">确定</button>
        </div>
    </div>
</div>