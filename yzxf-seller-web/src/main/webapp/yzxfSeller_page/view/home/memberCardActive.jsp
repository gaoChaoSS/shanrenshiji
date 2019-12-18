<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="home_memberCardActive_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack black" ng-click="goPage('/home/index')"></span>
        会员卡激活
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle textSize15" ng-class="phoneNumber==''?'':(phoneError?'errorRed':'')">
                电话:
            </div>
            <input type="text" class="rowInput textSize15" ng-model="phoneNumber"
                   ng-class="phoneNumber==''?'':(phoneError?'rowInputShort':'')"
                   ng-change="phoneCheck()" placeholder="请输入完整的会员手机号" ng-change="getMemberInfo()" ng-keyup="KeyCode()"/>
                <span class="mainRowRight errorRed mainRowRightErrorText textSize15"
                      ng-show="phoneNumber==''?'':phoneError">格式错误</span>
        </div>
    </div>
    <div class="sectionMain" ng-show="status==2">
        <div class="mainRow">
            <div class="rowTitle textSize15">
                验证码:
            </div>
            <input type="text" class="rowInput textSize15" ng-model="verification" placeholder="请输入短信验证码" />
        </div>
    </div>
    <button class="submitBtn" ng-show="status==2" ng-bind="isSend=='disabled'?countSendTime+'s后再获取验证码':'点击获取短信验证码'"
            ng-disabled="isSend" ng-click="countDown()"></button>
    <div class="sectionMain textCenter grayaaa textSize18" style="padding: 30px 0;" ng-show="status==1">
        该会员尚未实名认证,</br>
        请点击按钮前往实名认证
    </div>
    <div class="sectionMain textCenter grayaaa textSize18" style="padding: 30px 0;" ng-show="status==2">
        该手机号尚未注册,</br>
        请点击按钮一键注册,</br>
        账户密码稍后将会发送至您的手机
    </div>
    <div class="sectionMain textCenter grayaaa textSize18" style="padding: 30px 0;" ng-show="status==3">
        该会员身份信息完整,</br>
        请点击按钮绑定会员卡
    </div>
    <div class="sectionMain textCenter grayaaa textSize18" style="padding: 30px 0;" ng-show="status==4">
        该会员已经激活会员卡
    </div>
    <button class="submitBtn" ng-show="status==1" ng-click="goPage('/home/memberRealName/memberId/'+memberId)">前往实名认证
    </button>
    <button class="submitBtn" ng-show="status==2" ng-click="memberReg()"
            ng-disabled="(verification==null || verification=='')?'disabled':false">一键注册</button>
    <button class="submitBtn" ng-show="status==3" ng-click="goPage('/home/bindMemberCard/memberId/'+memberId)">绑定会员卡
    </button>


    <div style="position:fixed;bottom: 0;right:0;width:10px;height:10px" ng-click="setNotCode()"></div>
</div>