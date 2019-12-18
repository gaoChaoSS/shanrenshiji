<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="home_bindOver_Ctrl" class="d_content title_section order_panel form_section index_page_globalDiv">
    <div class="title textSize22">
        <span class="icon-left-1 iconfont titleBack textSize15" ng-click="goPageUrl2()">返回账户</span>
        绑定完成
    </div>
    <div class="sectionMain textCenter" ng-show="bindOver">
        <div class="icon-zhengque1 iconfont textSize30 limeGreen mainRowTop20"></div>
        <div class="textSize18">绑定成功!</div>
        <div class="textSize15 grayaaa">绑定成功后可在账户页面查看查看详情</div>
        <div class="textSize15 grayaaa" ng-bind="countdown+' 秒后返回首页或者点击下方按钮立刻返回到首页'"></div>
        <div class="textSize18 dodgerBlue" ng-click="goPageUrl()" style="margin: 15px 0;">立刻返回</div>
    </div>
    <div class="sectionMain textCenter" ng-show="!bindOver">
        <div class="icon-cuowu iconfont textSize30 orangeRed3 mainRowTop20"></div>
        <div class="textSize18">绑定失败!</div>
        <div class="textSize15 grayaaa">也许是网络出问题了,重新试一试吧~</div>
        <div class="textSize18 dodgerBlue" ng-click="goPage('home/bankBind/userType/'+pathParams.userType)" style="margin: 15px 0;">重新绑定</div>
    </div>
    <div class="sectionMain">
        <img ng-src="/yzxfSeller_page/img/bindOver.jpg" alt="" style="width: 100%;">
    </div>
</div>
