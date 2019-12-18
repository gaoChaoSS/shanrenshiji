<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="store_qrCode_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span ng-show="memberType=='member'" class="icon-left-1 iconfont titleBack"
              ng-click="goPage('/store/memberRichScan')"></span>
        <span ng-show="memberType=='other'" class="icon-left-1 iconfont titleBack"
              ng-click="goPage('/store/otherRichScan')"></span>
        {{getTitle()}}支付
    </div>
    <div class="sectionMain" ng-show="memberType=='member'">
        <div class="mainRow">
            <div class="rowTitle" ng-bind="'账号:'+memberCard"></div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow mainRowLast lineHeight100 textSize30 textCenter errorRed"
             ng-bind="'¥ '+money">
        </div>
    </div>
    <div class="sectionMain qrcodeContentCon" style="text-align: center;"   data-ng-init="memberType == 'member'?getMemberInfo():genQrCode();">
    </div>
    <%--<button type="button" class="submitBtn" ng-click="getSubmit()">检查客户是否支付完成</button>--%>
    <div class="hideMenu" ng-show="active">
        <div class="errorMain otherMain" ng-show="activeStatus==true">
            <div class="otherMainRow icon-zhengque1 iconfont textSize50 limeGreen"></div>
            <div class="otherMainRow textSize25">交易成功</div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到主页'"></div>
        </div>
        <div class="errorMain otherMain" ng-show="activeStatus==false">
            <div class="otherMainRow icon-cuowu iconfont textSize50 orangeRed3"></div>
            <div class="otherMainRow textSize22" ng-bind="activeText"></div>
            <div class="otherMainRow grayaaa textSize13" ng-bind="countdown+' 秒后返回到现金交易页面'"></div>
        </div>
    </div>
</div>