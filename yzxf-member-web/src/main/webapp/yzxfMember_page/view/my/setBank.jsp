<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_setBank_Ctrl" class="d_content form_section title_section">
    <div class="title titleRedBottom">
        <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBack()"></span>
        设置提现银行卡
    </div>
    <div class="overflowPC">
        <form ng-submit="submitForm()" >
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        银行账号:
                    </div>
                    <input type="text" ng-model="userInfo.bankId" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-cash iconfont iconBig"></span>
                        开户行:
                    </div>
                    <input type="text" ng-model="userInfo.bankName" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-dizhi iconfont iconBig"></span>
                        开户行地址:
                    </div>
                    <input type="text" ng-model="userInfo.bankAddress" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        户名:
                    </div>
                    <input type="text" ng-model="userInfo.bankUser" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        持卡人身份证:
                    </div>
                    <input type="text" ng-model="userInfo.bankUserCardId" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        持卡人手机:
                    </div>
                    <input type="text" ng-model="userInfo.bankUserPhone" class="rowInput"/>
                </div>
            </div>

            <button type="submit" class="submitBtn">提交</button>
        </form>
    </div>
</div>