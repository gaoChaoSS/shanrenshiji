<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <div class="popTitle">初审资料</div>
    <div>
        <span>申请人:</span>
        <span ng-bind="verifyInfo.create"></span>
    </div>
    <div>
        <span>申请类型:</span>
        <span ng-bind="getOwnerType(verifyInfo.ownerType)"></span>
    </div>
    <div>
        <span>联系人:</span>
        <span ng-bind="userInfo.contactPerson"></span>
    </div>
    <div>
        <span>联系电话:</span>
        <span ng-bind="verifyInfo.ownerType=='Factor'?userInfo.mobile:userInfo.phone"></span>
    </div>
    <div style="width:100%;white-space: inherit">
        <span>当前区域:</span>
        <span ng-bind="userInfo.area"></span>
    </div>
</div>

<div class="winCon" ng-click="closeImgFun()" ng-show="showImg!=''">
    <img class="winConImg" ng-src="{{iconImgUrl(showImg)}}"/>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fr btn1" ng-click="checkBtnFun(1)">初审通过</button>
    <button class="fr btn1 bkColorYellow1" ng-click="checkBtnFun(2)">初审不通过</button>
</div>

<div class="sectionHintBk" ng-show="checkBtn[0]">
    <div class="sectionHint">
        <div class="lineH100px">是否退出审核?</div>
        <div class="flex1">
            <button class="btn1" ng-click="closeWin()">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(0)">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[1]">
    <div class="sectionHint">
        <div class="lineH100px">是否提交审核结果?</div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm(0.2)">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(1)">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[2]">
    <div class="sectionHint">
        <div class="lineH100px">请填写审批不通过的理由</div>
        <textarea class="textarea1" placeholder="请填写200字以内的审批不通过的理由,该信息将通过客户端反馈给用户" ng-model="userInfo.explain"></textarea>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm(0.3)">确定</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(2)">取消</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[3]">
    <div class="sectionHint">
        <div class="lineH100px">提交成功!</div>
        <div class="flex1">
            <button class="btn1 bkColorRed1" ng-click="closeWin()">确定</button>
        </div>
    </div>
</div>