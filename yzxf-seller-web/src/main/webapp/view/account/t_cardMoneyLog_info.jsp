<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <div class="popTitle" style="margin-top:0">利润分配</div>
    <div style="width:100%" ng-repeat="agentLog in $$agentLog" ng-show="agentLog.agentName!=null&&agent.level<=agentLog.level">
        <span ng-bind="agentLog.agentName+'('+agentLevelNum(agentLog.level)+')'+'利润:'"></span>
        <span class="colorRed2" ng-bind="getMoney(agentLog.orderCash)+'元'"></span>
        <%--<span>平台利润:</span>--%>
        <%--<span class="colorRed2" ng-bind="agent[0].orderCash==null?'无':getMoney(agent[0].orderCash)+'元'"></span>--%>
    </div>
    <%--<div style="width:100%" ng-show="agent[1].orderCash!=null">--%>
        <%--<span ng-bind="agent[1].agentName+' (省级)利润:'"></span>--%>
        <%--<span class="colorRed2" ng-bind="agent[1].orderCash==null?'无':getMoney(agent[1].orderCash)+'元'"></span>--%>
    <%--</div>--%>
    <%--<div style="width:100%" ng-show="agent[2].orderCash!=null">--%>
        <%--<span ng-bind="agent[2].agentName+' (市级)利润:'"></span>--%>
        <%--<span class="colorRed2" ng-bind="agent[2].orderCash==null?'无':getMoney(agent[2].orderCash)+'元'"></span>--%>
    <%--</div>--%>
    <%--<div style="width:100%" ng-show="agent[3].orderCash!=null">--%>
        <%--<span ng-bind="agent[3].agentName+' (县级)利润:'"></span>--%>
        <%--<span class="colorRed2" ng-bind="agent[3].orderCash==null?'无':getMoney(agent[3].orderCash)+'元'"></span>--%>
    <%--</div>--%>
    <div style="width:100%" ng-show="factor[0].orderCash!=null">
        <span ng-bind="factor[0].factorName+' (服务站)利润:'"></span>
        <span class="colorRed2" ng-bind="factor[0].orderCash==null?'无':getMoney(factor[0].orderCash)+'元'"></span>
    </div>
    <%--<div style="width:100%">--%>
        <%--<span ng-bind="'(会员)投保金额:'"></span>--%>
        <%--<span class="colorRed2" ng-bind="'0 元'"></span>--%>
    <%--</div>--%>
</div>
<div class="popSection flex2">
    <div class="popTitle">会员资料</div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="member[0].memberName"></span>
    </div>
    <div>
        <span>当前状态:</span>
        <span ng-bind="member[0].memberCanUse?'有效':'禁用'"></span>
    </div>
    <div>
        <span>手机号码:</span>
        <span ng-bind="member[0].memberMobile"></span>
    </div>
    <div>
        <span>身份证号:</span>
        <span ng-bind="member[0].memberIdCard"></span>
    </div>
    <div>
        <span>常用邮箱:</span>
        <span ng-bind="member[0].memberEmail"></span>
    </div>
    <div style="width:100%">
        <span>所在城市:</span>
        <span ng-bind="member[0].memberRealArea+member[0].memberRealAddress"></span>
    </div>
</div>