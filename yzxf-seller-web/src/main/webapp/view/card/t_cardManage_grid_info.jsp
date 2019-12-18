<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <div class="popTitle">实体卡分配</div>
    <div style="width: 100%">
        <span>发货方:</span>
        <span ng-bind="agent.name"></span>
    </div>
    <div style="width: 100%">
        <span>收货方:</span>
        <select ng-model="tempCard.name" ng-options="al as al.name for al in agentLowerList"></select>
    </div>
    <div style="width: 100%">
        <span>起始卡号:</span>
        <input type="text" style="width: 300px;" ng-model="tempCard.startCardNo" placeholder="请输入16位起始卡号" maxlength="16"
               ng-change="countCardTotal(tempCard.startCardNo,tempCard.endCardNo)"/>
    </div>
    <div style="width:100%">
        <span>终止卡号:</span>
        <input type="text" style="width: 300px;" ng-model="tempCard.endCardNo" placeholder="请输入16位终止卡号" maxlength="16"
               ng-change="countCardTotal(tempCard.startCardNo,tempCard.endCardNo)"/>
    </div>
    <div style="width:100%">
        <span>数量:</span>
        <span ng-bind="cardTotal"></span>
    </div>
    <div style="width: 100%;padding: 10px 0">
        <div style="width:25%;margin: 0 auto">
            <button class="btn1 bgBlue" type="submit" ng-click="allocationCard()">提交</button>
        </div>
    </div>
</div>

<div class="popSection flex2">
    <div class="popTitle">持有卡信息</div>
    <div class="sectionTable" style="width:100%;padding: 0;">
        <div>
            <div style="width:40%">起始号段</div>
            <div style="width:40%">结束号段</div>
            <div style="width:20%">数量</div>
        </div>
        <div ng-repeat="card in cardFieldList">
            <div style="width:40%" ng-bind="card.startCardNo"></div>
            <div style="width:40%" ng-bind="card.endCardNo"></div>
            <div style="width:20%" ng-bind="card.cardNum"></div>
        </div>
    </div>
    <div style="width:100%">
        <div class="isNullBox" ng-show="cardFieldList==null || cardFieldList.length==0" style="margin: 60px auto;">
            <div class="iconfont icon-meiyouneirong isNullIcon"></div>
            <div class="font25px colorGrayccc">没有数据</div>
        </div>
    </div>
</div>