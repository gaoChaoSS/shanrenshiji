<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_realName_Ctrl" class="form_section d_content title_section ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        实名认证
    </div>
    <form ng-submit="submitForm()" ng-show="isRealName==false">
        <div class="sectionMain" >
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">真实姓名:</div>
                <input type="text" class="rowInput" ng-model="realName" placeholder="请输入您的姓名" ng-change="submitBtn()"/>
            </div>
            <%--<div class="mainRow mainRowSelect">--%>
                <%--<div class="rowInputCheckMain">--%>
                    <%--<div class="rowInputCheck" ng-click="sex=0">--%>
                        <%--<span ng-class="sex==0?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>男--%>
                    <%--</div>--%>
                    <%--<div class="rowInputCheck" ng-click="sex=1">--%>
                        <%--<span ng-class="sex==1?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>女--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle" ng-class="cardNumber==''?'':(cardError?'errorRed':'')">身份证号:</div>
                <input type="text" ng-model="cardNumber" class="rowInput" ng-class="cardNumber==''?'':(cardError?'rowInputShort':'')" placeholder="请输入您的身份证号码" ng-change="cardErrorFuc();submitBtn()"/>
                <span class="mainRowRight errorRed mainRowRightErrorText" ng-show="cardNumber==''?'':cardError"  style="right: 15px">格式错误</span>
            </div>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle" ng-class="myEmail==''?'':(emailError?'errorRed':'')">电子邮箱:</div>--%>
                <%--<input type="text" ng-model="myEmail" class="rowInput" ng-class="myEmail==''?'':(emailError?'rowInputShort':'')" placeholder="(选填)请输入有效的邮箱" ng-change="emailErrorFuc();submitBtn()"/>--%>
                <%--<span class="mainRowRight errorRed mainRowRightErrorText" ng-show="myEmail==''?'':emailError"  style="right: 15px">格式错误</span>--%>
            <%--</div>--%>
            <div class="mainRow" ng-click="goPage('/home/location/selectedArea/realNameArea')">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">身份证区域</div>
                <div class="rowInput" ng-bind="area"></div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
            <div class="mainRow">
                <div class="mainRowLeft orangeRed3">*</div>
                <div class="rowTitle">身份证街道:</div>
                <input class="rowInput" ng-model="address" ng-change="submitBtn()"/>
            </div>
        </div>
        <input type="submit" value="提交认证" class="submitBtn" ng-disabled="check"/>
    </form>

    <div class="sectionMain textCenter" ng-show="isRealName==true" style="padding:30px 0;color: green">
        尊敬的会员您好:您已经实名认证!
    </div>
    <div class="sectionMain" ng-show="isRealName==true">
        <div class="mainRow">
            <div class="rowTitle notHigh">真实姓名:</div>
            <div class="rowInput" ng-bind="memberInfo.realName"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle notHigh">会员性别:</div>
            <div class="rowInput" ng-bind="getSex(memberInfo.sex)"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle notHigh">身份证号:</div>
            <div class="rowInput" ng-bind="getIdCard(memberInfo.idCard)"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle notHigh">电子邮箱:</div>
            <div class="rowInput" ng-bind="memberInfo.email"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle notHigh">所在地区:</div>
            <div class="rowInput" ng-bind="memberInfo.realArea"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle notHigh">所在街道:</div>
            <div class="rowInput" ng-bind="memberInfo.realAddress"></div>
        </div>
    </div>
</div>