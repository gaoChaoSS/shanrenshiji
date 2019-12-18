<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--<div class="popSection">--%>
    <%--<div class="popTitle" style="margin-top:0">归属</div>--%>
    <%--<div style="width:100%">--%>
        <%--<span>归属机构:</span>--%>
        <%--<span ng-bind="agentNameAll"></span>--%>
    <%--</div>--%>
<%--</div>--%>
<%--<div class="popSection flex2">--%>
    <%--<div class="popTitle">详细信息</div>--%>
    <%--<div>--%>
        <%--<span ng-show="agentInfoObj.level!=5">代理商名:</span>--%>
        <%--<span ng-show="agentInfoObj.level==5">服务站名:</span>--%>
        <%--<span ng-bind="agentInfoObj.name"></span>--%>
    <%--</div>--%>
    <%--<div>--%>
        <%--<span>联系方式:</span>--%>
        <%--<span ng-show="agentInfoObj.level!=5" ng-bind="agentInfoObj.phone"></span>--%>
        <%--<span ng-show="agentInfoObj.level==5" ng-bind="agentInfoObj.mobile"></span>--%>
    <%--</div>--%>
    <%--<div>--%>
        <%--<span>创建日期:</span>--%>
        <%--<span ng-bind="showYFullTime(agentInfoObj.createTime)"></span>--%>
    <%--</div>--%>
    <%--<div style="width:100%">--%>
        <%--<span>地址:</span>--%>
        <%--<span ng-bind="agentInfoObj.address"></span>--%>
    <%--</div>--%>
<%--</div>--%>
<div class="popSection flex2">
    <div class="popTitle">实体卡回收</div>
    <div style="width: 100%">
        <span>回收方:</span>
        <span ng-bind="adminAgent.name"></span>
    </div>
    <div style="width: 100%">
        <span>被回收方:</span>
        <span ng-bind="agentInfoObj.name"></span>
    </div>
    <div style="width: 100%">
        <span>起始卡号:</span>
        <input type="text" style="width: 300px;" ng-model="cardObj.startCardNo" placeholder="请输入16位起始卡号"/>
    </div>
    <div style="width:100%">
        <span>终止卡号:</span>
        <input type="text" style="width: 300px;" ng-model="cardObj.endCardNo" placeholder="请输入16位终止卡号"
               ng-blur="countCardTotal(cardObj.startCardNo,cardObj.endCardNo)"/>
    </div>
    <div style="width:100%">
        <span>数量:</span>
        <span ng-bind="cardTotal"></span>
    </div>
    <div style="width: 100%;padding: 10px 0">
        <div style="width:25%;margin: 0 auto">
            <button class="btn1 bgBlue" type="submit" ng-click="cardRecycle(cardObj.startCardNo,cardObj.endCardNo)">提交</button>
        </div>
    </div>
</div>

<div class="popSection flex2">
    <div class="popTitle">剩余卡信息</div>
    <table class="sectionTable">
        <tr>
            <td style="width:40%">起始号段</td>
            <td style="width:40%">结束号段</td>
            <td style="width:20%">数量</td>
        </tr>
        <tr ng-repeat="adminCfl in cardFieldList">
            <td style="width:40%" ng-bind="adminCfl.startCardNo"></td>
            <td style="width:40%" ng-bind="adminCfl.endCardNo"></td>
            <td style="width:20%" ng-bind="adminCfl.cardNum"></td>
        </tr>
    </table>
    <div class="isNullBox" ng-show="cardFieldList==null || cardFieldList.length==0" style="margin: 60px auto;">
        <div class="iconfont icon-meiyouneirong isNullIcon"></div>
        <div class="font25px colorGrayccc">没有数据</div>
    </div>
</div>