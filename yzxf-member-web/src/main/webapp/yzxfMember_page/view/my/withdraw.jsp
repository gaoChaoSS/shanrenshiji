<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="my_withdraw_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        积分转现
        <span class="titleManage notHigh" ng-click="goPage('/my/withdrawLog')">明细</span>
    </div>
    <form ng-submit="tixian()">
        <div class="sectionMain4" style="width: 95%;margin: 10px auto;border-radius: 10px;">
            <div class="mainRow mainRowNotBorderBottom">
                <div class="rowTitle widthPercent40">到账银行卡</div>
                <div class="rowInput mainRowLeftB40 colorBlue">
                    <span ng-bind="bankInfo.bankName+'('+bankInfo.bankId+')'" ng-show="bankInfo.bankId!=null" style="margin-right:20px"></span>
                    <span ng-click="goPage('/my/setBank')" class="colorRed1" >去设置</span>
                </div>
            </div>
            <div class="mainRow mainRowNotBorderBottom rowMinHeight20">
                <div class="rowInput mainRowLeftB40 lineHeight20 grayaaa" ng-bind="'提到银行卡,手续费'+poundageRatio+'%'"></div>
            </div>
            <div class="mainRow mainRowNotBorderBottom">
                <div class="rowTitle widthPercent40">可转现积分</div>
                <div class="rowInput mainRowLeftB40 colorBlue" ng-bind="getMoney(wallet.canWithdrawMoney)+'分'"></div>
            </div>
            <div class="mainRow mainRowNotBorderBottom">
                <div class="rowTitle widthPercent40">积分转现到银行卡</div>
            </div>
            <div class="mainRow">
                <div style="position: absolute;top: 5px;left: 10px;font-size: 16px;">¥</div>
                <input type="text" id="inputMoney" style="width: 100%;height: 50px;line-height: 50px;font-size: 30px;text-indent: 20px;"
                       ng-model="money" placeholder="请输入转现积分"
                       ng-change="getMoneyCharge()"  maxlength="7">
            </div>
            <div class="mainRow mainRowNotBorderBottom">
                <div class="rowTitle widthPercent40 grayaaa" ng-bind="'扣除'+moneyCharge+'分手续费'"></div>
            </div>
        </div>
        <%--<div class="textCenter lineHeight50 grayaaa">两小时内到账</div>--%>
        <input type="submit" value="转现" class="submitBtn bgBlue3" style="margin:0 auto"/>
    </form>
    <div class="hideMenu" ng-show="menuCheck">
        <div class="enterPsw">
            <div class="textCenter lineHeight50">转现</div>
            <div class="textCenter textSize30 lineHeight40" ng-bind="'¥ '+money"></div>
            <div class="enterInput">
                <input maxlength="1" type="password" ng-model="pwd1"/>
                <input maxlength="1" type="password" ng-model="pwd2"/>
                <input maxlength="1" type="password" ng-model="pwd3"/>
                <input maxlength="1" type="password" ng-model="pwd4"/>
                <input maxlength="1" type="password" ng-model="pwd5"/>
                <input maxlength="1" type="password" ng-model="pwd6"/>
            </div>
            <div class="enterInputDes">请输入6位支付密码</div>
            <div class="selectDiv">
                <div style="border-left: 0;border-bottom: 0" ng-click="menuCheck=false">取消</div>
                <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="Withdrawal()">确认</div>
            </div>
        </div>
    </div>
</div>