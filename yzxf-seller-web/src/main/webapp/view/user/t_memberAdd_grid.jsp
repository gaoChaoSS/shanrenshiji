<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection" ng-show="allInfo.cardNo!=null && allInfo.cardNo!=''">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%;white-space: normal;">
        <span>归属机构:</span>
        <span ng-bind="getBelongArea(agentNameAll,allInfo.cardNo)"></span>
    </div>
</div>
<div class="popSection flex2" ng-show="adminCheck">
    <div class="popTitle">权限管理</div>
    <div>
        <span>可用状态:</span>
        <span class="iconfont font25px"
              ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"
              ng-click="userInfo.canUse=!userInfo.canUse"></span>
    </div>
    <%--<div>--%>
        <%--<span>实名认证:</span>--%>
        <%--<span class="iconfont font25px"--%>
              <%--ng-class="userInfo.isRealName?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"--%>
              <%--ng-click="userInfo.isRealName=!userInfo.isRealName"></span>--%>
    <%--</div>--%>
</div>
<div class="popSection flex2">
    <div class="popTitle">会员信息</div>
    <div style="width:100%">
        <span>手机号码:</span>
        <%--<span ng-bind="allInfo.mobile"></span>--%>
        <input type="text" ng-model="userInfo.mobile"/>
    </div>
    <div style="width:100%">
        <span>会员姓名:</span>
        <input type="text" ng-model="userInfo.realName"/>
    </div>
    <div style="width:100%">
        <span>会员性别:</span>
        <span ng-click="userInfo.sex='1'">
            <span ng-class="userInfo.sex==='1'?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>男
        </span>
        <span ng-click="userInfo.sex='2'">
            <span ng-class="userInfo.sex==='2'?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>女
        </span>
    </div>
    <div style="width:100%">
        <span>常用邮箱:</span>
        <input type="text" ng-model="userInfo.email"/>
    </div>
    <div style="width:100%">
        <span>身份证号:</span>
        <input type="text" ng-model="userInfo.idCard"/>
    </div>
    <div style="width:100%">
        <span>证件地区:</span>
        <select ng-model="selectArea[0]" ng-options="area1.name for area1 in areaList[0]"
                ng-change="getArea(selectArea[0]._id,2)"></select>
        <select ng-model="selectArea[1]" ng-options="area1.name for area1 in areaList[1]"
                ng-change="getArea(selectArea[1]._id,3)"></select>
        <select ng-model="selectArea[2]" ng-options="area1.name for area1 in areaList[2]"
                ng-change="getArea(selectArea[2]._id,4)"></select>
    </div>
    <div style="width:100%">
        <span>证件街道:</span>
        <input type="text" ng-model="userInfo.realAddress"/>
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
            <button class="btn1" ng-click="submitForm();checkSubmit=false">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkSubmit=false">否</button>
        </div>
    </div>
</div>