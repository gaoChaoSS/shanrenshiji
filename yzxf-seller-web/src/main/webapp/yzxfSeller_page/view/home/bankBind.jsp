<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="home_bankBind_Ctrl" class="d_content title_section order_panel form_section index_page_globalDiv">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        绑定银行卡
    </div>

    <div class="overflowPC">
        <form ng-submit="bankBindApply()">
            <div class="mainRowTitle notHigh">请绑定账户本人的银行卡</div>
            <div class="sectionMain sectionMainNotMargin">
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">卡号</div>
                    <input class="rowInput" ng-model="bankId" placeholder="请输入银行卡号" ng-change="submitBtn()"/>
                </div>
            </div>
            <div class="mainRowTitle notHigh">请绑定账户本人的银行卡</div>
            <div class="sectionMain sectionMainNotMargin">
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">姓名</div>
                    <input class="rowInput" ng-model="bankUser" placeholder="请输入银行卡账户人姓名" ng-change="submitBtn()"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">手机号</div>
                    <input type="text" class="rowInput textSize15" ng-model="bankUserPhone"
                           ng-class="phoneNumber==''?'':(phoneError?'rowInputShort':'')"
                           ng-change="phoneCheck();submitBtn()" placeholder="请输入银行卡账户人的手机号"/>
                    <span class="mainRowRight errorRed mainRowRightErrorText textSize15"
                           ng-show="phoneNumber==''?'':phoneError">格式错误</span>
                </div>
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">身份证</div>
                    <input class="rowInput" ng-model="bankUserCardId" placeholder="请输入账户人的身份证号码" ng-change="submitBtn()"/>
                </div>
            </div>
            <button type="submit" class="submitBtn" ng-disabled="check">下一步</button>
        </form>
    </div>
</div>
