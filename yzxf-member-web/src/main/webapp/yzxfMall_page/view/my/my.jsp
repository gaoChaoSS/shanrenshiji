<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_my_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto" style="height: 450px">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myPageRightContent">
            <%--头像余额部分--%>
            <div class="memberInfoDiv">
                <div class="floatL memberHeadDiv">
                    <img class="floatL memberHeadImg" ng-src="{{iconImgUrl(myInfo.icon)}}" alt="">
                </div>
                <div class="floatL memberHeadName" ng-bind="loginAfterName"></div>
                <div class="floatL memberIsReal">
                    <span class="iconfont2 icon2-huiyuan" ng-class="memberIsRealName?'colorBlue1':'colorGray888'"></span>
                    <span ng-show="memberIsRealName" style="color: #138bbe">已认证</span>
                    <span ng-show="!memberIsRealName" style="color: #949292">未认证</span>
                </div>
                <div class="floatR memberMoney flex1" style="border-left: 2px solid #dfdfdf">
                    <div class="floatL" style="margin-left: 5px;margin-right: 10px">我的养老金</div>
                    <div class="floatL" style="color: #138bbe;font-size: 22px">¥</div>
                    <div class="floatL" style="color: #138bbe;font-size: 40px" ng-bind="isNullZero(pensionMoney)">88</div>
                </div>
                <div class="floatR memberMoney flex1">
                    <div class="floatL" style="margin-right: 10px">我的余额</div>
                    <div class="floatL" style="color: #138bbe;font-size: 22px">¥</div>
                    <div class="floatL" style="color: #138bbe;font-size: 40px" ng-bind="isNullZero(wallet)">213</div>
                </div>
            </div>
            <%--基本资料--%>
            <div class="MemberInfo">
                <div class="MemberInfoTitle">基本资料</div>
                <div class="MemberInfoContent">
                    <div><span>会员号 : </span><span ng-bind="isNullText2(myInfo.cardNo)"></span></div>
                    <div><span>身份证 : </span><span ng-bind="isNullText2(myInfo.idCard)"></span></div>
                    <div><span>手机号 : </span><span ng-bind="isNullText2(myInfo.mobile)"></span></div>
                    <div><span>地址 : </span><span ng-bind="isNullText2(myInfo.realArea+myInfo.realAddress)"></span></div>
                </div>
            </div>
        </div>
    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>