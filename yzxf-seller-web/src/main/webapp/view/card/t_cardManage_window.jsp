<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<%--<div class="popSection flex2">--%>
    <%--<div class="popTitle">特殊号段信息</div>--%>
    <%--<div style="width:100%;padding-left: 32px;" ng-show="adminSpecial!=null && adminSpecial.length>0">--%>
        <%--<div><span style="color: #555;font-weight: 600;">剩余卡号段:</span></div>--%>
        <%--<div ng-repeat="adminSl in adminSpecial">--%>
            <%--<span ng-bind="'起止号码:'+cardNO(adminSl.startCardNo)+'~'"></span>--%>
            <%--<span ng-bind="cardNO(adminSl.endCardNo)"></span>--%>
            <%--<span ng-bind="'数量:'+adminSl.cardNum"></span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div style="width:100%;padding-left: 32px;" ng-show="adminSpecial.length==0">--%>
        <%--<div style="text-align: center">!请设置特殊号段!且只能设置一次</div>--%>
        <%--<form ng-submit="setStartSpecial(startCardNoS,endCardNoS)" style="width: 100%">--%>
            <%--<div style="width: 100%">--%>
                <%--<div style="width:50%;float: left;padding-left: 70px">--%>
                    <%--<span>起始卡号:</span>--%>
                    <%--<input type="text" ng-model="startCardNoS" placeholder="例如:000001"/>--%>
                <%--</div>--%>
                <%--<div style="width:50%;margin: 0 auto;float: left">--%>
                    <%--<span>终止卡号:</span>--%>
                    <%--<input type="text" ng-model="endCardNoS" placeholder="例如:999999" ng-blur="countCardTotalS(startCardNoS,endCardNoS)"/>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div style="width: 25%;padding: 10px 0;margin: 0 auto;overflow: hidden">--%>
                <%--<div style="width:100%">--%>
                    <%--<span>数量:</span>--%>
                    <%--<span ng-bind="cardTotalS"></span>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div style="width: 100%;padding: 10px 0">--%>
                <%--<div style="width:25%;margin: 0 auto">--%>
                    <%--<button class="btn1 bgBlue" type="submit">提交</button>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</form>--%>
    <%--</div>--%>
<%--</div>--%>

<div class="popSection flex2" ng-show="adminLevel=='1'">
    <div class="popTitle">设置新的号段</div>
    <div style="width:100%">
        <span>发卡方:</span>
        <span ng-bind="agent.name"></span>
    </div>
    <div style="width:100%">
        <span>请选择地区:</span>
        <select ng-model="addressCode"  ng-change="selectAddressCode(addressCode)">
            <option ng-repeat="al in addressList" value="{{al.v}}">{{al.name}}</option>
        </select>
    </div>
    <div style="width:100%">
        <span>起始卡号:</span>
        <span>
            <%--<span style="display:inline-block;line-height: 22px;background-color: #ddd;text-align: center;width: 45px" ng-bind="addressCodeT==''?'地区码':addressCodeT"></span>--%>
            <input type="text" style="width: 300px;" ng-model="cardObj.startCardNo" placeholder="请输入16位起始卡号" maxlength="16"
                   ng-change="countCardTotal(cardObj.startCardNo,cardObj.endCardNo)"/>
        </span>
    </div>
    <div style="width: 100%">
        <span>终止卡号:</span>
        <span>
            <!--<span style="display:inline-block;line-height: 22px;background-color: #ddd;text-align: center;width: 45px" ng-bind="addressCodeT==''?'地区码':addressCodeT"></span>-->
            <input type="text" style="width: 300px;" ng-model="cardObj.endCardNo" placeholder="请输入16位终止卡号" maxlength="16"
                   ng-change="countCardTotal(cardObj.startCardNo,cardObj.endCardNo)"/>
        </span>
    </div>
    <div style="width: 100%">
        <span>数量:</span>
        <span ng-bind="cardTotal"></span>
    </div>
    <div style="width: 100%;padding: 10px 0">
        <div style="width:25%;margin: 0 auto">
            <button class="btn1 bgBlue" type="submit" ng-click="setStartCardField(cardObj.startCardNo,cardObj.endCardNo)">提交</button>
        </div>
    </div>
</div>

<div class="popSection flex2">
    <div class="popTitle">剩余卡信息</div>

    <div class="sectionTable" style="width:100%;padding: 0;">
        <div>
            <div style="width:40%">起始号段</div>
            <div style="width:40%">结束号段</div>
            <div style="width:20%">数量</div>
        </div>
        <div ng-repeat="adminCfl in adminCardFieldList">
            <div style="width:40%" ng-bind="adminCfl.startCardNo"></div>
            <div style="width:40%" ng-bind="adminCfl.endCardNo"></div>
            <div style="width:20%" ng-bind="adminCfl.cardNum"></div>
        </div>
    </div>
    <div class="isNullBox" ng-show="adminCardFieldList==null || adminCardFieldList.length==0" style="margin: 60px auto;">
        <div class="iconfont icon-meiyouneirong isNullIcon"></div>
        <div class="font25px colorGrayccc">没有数据</div>
    </div>
</div>