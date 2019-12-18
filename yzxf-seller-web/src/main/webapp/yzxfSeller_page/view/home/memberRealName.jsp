<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_memberRealName_Ctrl" class="form_section d_content title_section ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        实名认证
    </div>
    <form ng-submit="submitForm()">
        <div class="sectionMain" >
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">真实姓名:</div>
                <input type="text" class="rowInput" ng-model="realName" placeholder="请输入您的姓名" ng-change="submitBtn()"/>
            </div>
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">会员性别:</div>
                <div class="rowInput" style="padding:0">
                    <div class="rowInputCheck" ng-click="sex=1">
                        <span ng-class="sex==1?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>男
                    </div>
                    <div class="rowInputCheck" ng-click="sex=2">
                        <span ng-class="sex==2?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>女
                    </div>
                </div>
            </div>
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle" ng-class="cardNumber==''?'':(cardError?'errorRed':'')">身份证号:</div>
                <input type="text" ng-model="cardNumber" class="rowInput" ng-class="cardNumber==''?'':(cardError?'rowInputShort':'')" placeholder="请输入您的身份证号码" ng-change="cardErrorFuc();submitBtn()"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="cardNumber==''?'':cardError">格式错误</span>
            </div>
            <div class="mainRow">
                <div class="rowTitle" ng-class="myEmail==''?'':(emailError?'errorRed':'')">电子邮箱:</div>
                <input type="text" ng-model="myEmail" class="rowInput" ng-class="myEmail==''?'':(emailError?'rowInputShort':'')" placeholder="(选填)请输入有效的邮箱" ng-change="emailErrorFuc();submitBtn()"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="myEmail==''?'':emailError">格式错误</span>
            </div>
            <div class="mainRow" ng-click="goPage('/store/location/selectedArea/realNameArea')">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">身份证区域</div>
                <div class="rowInput" ng-bind="area" style="margin-left: 100px;"></div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">身份证街道:</div>
                <input class="rowInput" ng-model="address" ng-change="submitBtn()" style="margin-left: 100px;"/>
            </div>
        </div>
        <input type="submit" value="提交认证" class="submitBtn" ng-disabled="check"/>
    </form>
</div>