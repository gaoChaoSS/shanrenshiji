<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="profit_profit_Ctrl" style="width: 100%; height: 100%;">
    <div ng-click="Log=!Log" ng-bind="Log?'返回操作界面':'查看修改记录'" style="background: #1480B7;color:#FFF;border: 1px solid #DFDFDF;text-align: center;
    border-radius: 6px;z-index: 1;
    width: 100px;height: 30px;line-height: 30px;
    position: absolute;font-size: 15px;left: 0;top: 3px"></div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">利润比例</div>
        <div style="width: 600px;margin: 0 auto">
            <div class="height25 positionRe inputMargin20" style="overflow: hidden">
                <div class="" style="position:absolute;top:0;left: 0;bottom: 0;width:120px;"><img
                        src="../img/smallCircle.png" class="tFloat">
                    <div class=" tLineHight30 selectInputMarginL10">总利润比例</div>
                </div>
                <div class="" style="position: relative;margin:0 120px">
                    <div class="bgDFGray positionAb height25" style="top:0;left:0;bottom: 0;right: 0"></div>

                    <div class="positionAb height25" style="top:0;left:0;bottom: 0;right: 0">
                        <div style="height: 100%;background-color: coral;transition: 0.5s all"
                             ng-style="{width:totalRatio+'%'}"></div>
                    </div>
                </div>
                <div class="tFloatR tLineHight25 textAlignL" style="width: 90px" ng-bind="'比例: '+totalRatio+' %'"></div>
            </div>
            <form>
                <div class="height25 positionRe inputMargin20" style="overflow: hidden" ng-repeat="pl in profitList">
                    <div style="position:absolute;top:0;left: 0;bottom: 0;width:120px;"><img
                            src="../img/smallCircle.png" class="tFloat">
                        <div class=" tLineHight30 selectInputMarginL10" ng-bind="agentLevel(pl.level)"></div>
                    </div>
                    <div class="" style="position: relative;margin:0 120px">
                        <div class="bgDFGray positionAb height25" style="top:0;left:0;bottom: 0;right: 0"></div>
                        <div class="positionAb height25" style="top:0;left:0;bottom: 0;right: 0">
                            <div class="bgLightBlue" style="height: 100%;transition: 0.5s all"
                                 ng-style="{width:pl.profitRatio+'%'}"></div>
                        </div>
                    </div>
                    <div ng-show="!profitSaveOrModify" class=" tLineHight25 textAlignL"
                         style="position:absolute;top:0;right: -40px;bottom: 0;width:160px;padding:0 8px;">
                        比例: <span ng-bind="pl.profitRatio+'%'"></span></div>
                    <div ng-show="profitSaveOrModify" class=" tLineHight25 textAlignL"
                         style="position:absolute;top:0;right: -40px;bottom: 0;width:160px;padding:0 8px;">
                        比例: <input type="text" ng-model="pl.profitRatio" ng-blur="totalProfit()"
                                   style="width: 60px"/><span>%</span></div>
                </div>
            </form>
        </div>

        <button ng-show="!profitSaveOrModify" ng-click="profitSaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="profitSaveOrModify" ng-click="updateOrSaveRatio()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="profitSaveOrModify" ng-click="notUpdateRatio()"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">发卡比例</div>
        <div style="width: 600px;margin: 0 auto">
            <div class="height25 positionRe inputMargin20" style="overflow: hidden">
                <div class="" style="position:absolute;top:0;left: 0;bottom: 0;width:120px;"><img
                        src="../img/smallCircle.png" class="tFloat">
                    <div class=" tLineHight30 selectInputMarginL10">总利润比例</div>
                </div>
                <div class="" style="position: relative;margin:0 120px">
                    <div class="bgDFGray positionAb height25" style="top:0;left:0;bottom: 0;right: 0"></div>

                    <div class="positionAb height25" style="top:0;left:0;bottom: 0;right: 0">
                        <div style="height: 100%;background-color: coral;transition: 0.5s all"
                             ng-style="{width:totalRatioCard+'%'}"></div>
                    </div>
                </div>
                <div class="tFloatR tLineHight25 textAlignL" style="width: 90px" ng-bind="'比例: '+totalRatioCard+' %'"></div>
            </div>
            <form>
                <div class="height25 positionRe inputMargin20" style="overflow: hidden" ng-repeat="pl in profitListCard">
                    <div style="position:absolute;top:0;left: 0;bottom: 0;width:120px;"><img
                            src="../img/smallCircle.png" class="tFloat">
                        <div class=" tLineHight30 selectInputMarginL10" ng-bind="agentLevel(pl.levelCard)"></div>
                    </div>
                    <div class="" style="position: relative;margin:0 120px">
                        <div class="bgDFGray positionAb height25" style="top:0;left:0;bottom: 0;right: 0"></div>
                        <div class="positionAb height25" style="top:0;left:0;bottom: 0;right: 0">
                            <div class="bgLightBlue" style="height: 100%;transition: 0.5s all"
                                 ng-style="{width:pl.profitRatioCard+'%'}"></div>
                        </div>
                    </div>
                    <div ng-show="!profitSaveOrModifyCard" class=" tLineHight25 textAlignL"
                         style="position:absolute;top:0;right: -40px;bottom: 0;width:160px;padding:0 8px;">
                        比例: <span ng-bind="pl.profitRatioCard+'%'"></span></div>
                    <div ng-show="profitSaveOrModifyCard" class=" tLineHight25 textAlignL"
                         style="position:absolute;top:0;right: -40px;bottom: 0;width:160px;padding:0 8px;">
                        比例: <input type="text" ng-model="pl.profitRatioCard" ng-blur="totalProfitCard()"
                                   style="width: 60px"/><span>%</span></div>
                </div>
            </form>
        </div>

        <button ng-show="!profitSaveOrModifyCard" ng-click="profitSaveOrModifyCard=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="profitSaveOrModifyCard" ng-click="updateOrSaveRatioCard()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="profitSaveOrModifyCard" ng-click="notUpdateRatioCard()"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">充值分润比例</div>
        <div style="width: 600px;margin: 0 auto">
            <div class="height25 positionRe inputMargin20" style="overflow: hidden">
                <div class="" style="position:absolute;top:0;left: 0;bottom: 0;width:120px;"><img
                        src="../img/smallCircle.png" class="tFloat">
                    <div class=" tLineHight30 selectInputMarginL10">总利润比例</div>
                </div>
                <div class="" style="position: relative;margin:0 120px">
                    <div class="bgDFGray positionAb height25" style="top:0;left:0;bottom: 0;right: 0"></div>

                    <div class="positionAb height25" style="top:0;left:0;bottom: 0;right: 0">
                        <div style="height: 100%;background-color: coral;transition: 0.5s all"
                             ng-style="{width:totalRatioRecharge+'%'}"></div>
                    </div>
                </div>
                <div class="tFloatR tLineHight25 textAlignL" style="width: 90px" ng-bind="'比例: '+totalRatioRecharge+' %'"></div>
            </div>
            <form>
                <div class="height25 positionRe inputMargin20" style="overflow: hidden" ng-repeat="rec in recharge">
                    <div style="position:absolute;top:0;left: 0;bottom: 0;width:120px;"><img
                            src="../img/smallCircle.png" class="tFloat">
                        <div class=" tLineHight30 selectInputMarginL10" ng-bind="agentLevel(rec.level)"></div>
                    </div>
                    <div class="" style="position: relative;margin:0 120px">
                        <div class="bgDFGray positionAb height25" style="top:0;left:0;bottom: 0;right: 0"></div>
                        <div class="positionAb height25" style="top:0;left:0;bottom: 0;right: 0">
                            <div class="bgLightBlue" style="height: 100%;transition: 0.5s all"
                                 ng-style="{width:rec.profitRatio+'%'}"></div>
                        </div>
                    </div>
                    <div ng-show="!profitSaveOrModifyRecharge" class=" tLineHight25 textAlignL"
                         style="position:absolute;top:0;right: -40px;bottom: 0;width:160px;padding:0 8px;">
                        比例: <span ng-bind="rec.profitRatio+'%'"></span></div>
                    <div ng-show="profitSaveOrModifyRecharge" class=" tLineHight25 textAlignL"
                         style="position:absolute;top:0;right: -40px;bottom: 0;width:160px;padding:0 8px;">
                        比例: <input type="text" ng-model="rec.profitRatio" ng-blur="checkRatio('totalRatioRecharge','recharge')"
                                   style="width: 60px"/><span>%</span></div>
                </div>
            </form>
        </div>

        <button ng-show="!profitSaveOrModifyRecharge" ng-click="profitSaveOrModifyRecharge=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="profitSaveOrModifyRecharge" ng-click="updateOrSaveRatioRecharge()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="profitSaveOrModifyRecharge" ng-click="notUpdateRatioRecharge()"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">养老金投保上限</div>

        <div class="textAlignC positionRe" style="margin: 0 auto;width: 400px;">
            <img src="../img/bigCircle.png" alt="">
            <span class="textSize22 tOrange positionAb textAlignC" style="top: 65px;left: 102px;width: 200px"
                  ng-show="!pensionSaveOrModify" ng-bind="maxPensionMoney"></span>
            <input class="textSize22 tOrange positionAb rowInput4"
                   ng-show="pensionSaveOrModify" type="text" ng-model="maxPensionMoney"/>
            <div class="positionAb textSize18 tOrange" style="top: 100px;left: 165px;">当前上限</div>
        </div>
        <button ng-show="!pensionSaveOrModify" ng-click="pensionSaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="pensionSaveOrModify" ng-click="updateOrSaveMaxPension()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="pensionSaveOrModify" ng-click="notUpdateOrSaveMaxPension()"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">最低提现金额</div>

        <div class="textAlignC positionRe" style="margin: 0 auto;width: 400px;">
            <img src="../img/bigCircle.png" alt="">
            <span class="textSize22 tOrange positionAb textAlignC" style="top: 65px;left: 102px;width: 200px"
                  ng-show="!minSaveOrModify" ng-bind="minWithdrawalMoney"></span>
            <input class="textSize22 tOrange positionAb rowInput4"
                   ng-show="minSaveOrModify" type="text" ng-model="minWithdrawalMoney"/>
            <div class="positionAb textSize18 tOrange" style="top: 100px;left: 165px;">当前金额</div>
        </div>
        <button ng-show="!minSaveOrModify" ng-click="minSaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="minSaveOrModify" ng-click="updateOrSaveMin()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="minSaveOrModify" ng-click="notUpdateOrSaveMin()"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">提现手续费比例</div>

        <div class="textAlignC positionRe" style="margin: 0 auto;width: 400px;">
            <img src="../img/bigCircle.png" alt="">
            <span class="textSize22 tOrange positionAb textAlignC" style="top: 65px;left: 102px;width: 200px"
                  ng-show="!poundageSaveOrModify" ng-bind="poundageRatio+'%'"></span>
            <input class="textSize22 tOrange positionAb rowInput4"
                   ng-show="poundageSaveOrModify" type="text" ng-model="poundageRatio"/>
            <div class="positionAb textSize18 tOrange" style="top: 100px;left: 165px;">当前比例</div>
        </div>
        <button ng-show="!poundageSaveOrModify" ng-click="poundageSaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="poundageSaveOrModify" ng-click="updateOrSavePoundage()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="poundageSaveOrModify" ng-click="notUpdateOrSavePoundage()"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">会员充值分润总比例</div>

        <div class="textAlignC positionRe" style="margin: 0 auto;width: 400px;">
            <img src="../img/bigCircle.png" alt="">
            <span class="textSize22 tOrange positionAb textAlignC" style="top: 65px;left: 102px;width: 200px"
                  ng-show="!rechargeRatioSaveOrModify" ng-bind="rechargeRatio+'%'"></span>
            <input class="textSize22 tOrange positionAb rowInput4"
                   ng-show="rechargeRatioSaveOrModify" type="text" ng-model="rechargeRatio"/>
            <div class="positionAb textSize18 tOrange" style="top: 100px;left: 165px;">当前比例</div>
        </div>
        <button ng-show="!rechargeRatioSaveOrModify" ng-click="rechargeRatioSaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="rechargeRatioSaveOrModify" ng-click="updateRatio('rechargeRatio',rechargeRatio,6)"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="rechargeRatioSaveOrModify" ng-click="notUpdate('rechargeRatio')"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">会员充值赠送养老金比例</div>

        <div class="textAlignC positionRe" style="margin: 0 auto;width: 400px;">
            <img src="../img/bigCircle.png" alt="">
            <span class="textSize22 tOrange positionAb textAlignC" style="top: 65px;left: 102px;width: 200px"
                  ng-show="!rechargePensionRatioSaveOrModify" ng-bind="rechargePensionRatio+'%'"></span>
            <input class="textSize22 tOrange positionAb rowInput4"
                   ng-show="rechargePensionRatioSaveOrModify" type="text" ng-model="rechargePensionRatio"/>
            <div class="positionAb textSize18 tOrange" style="top: 100px;left: 165px;">当前比例</div>
        </div>
        <button ng-show="!rechargePensionRatioSaveOrModify" ng-click="rechargePensionRatioSaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="rechargePensionRatioSaveOrModify" ng-click="updateRatio('rechargePensionRatio',rechargePensionRatio,7)"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="rechargePensionRatioSaveOrModify" ng-click="notUpdate('rechargePensionRatio')"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div class="section1" ng-show="!Log">
        <div class="textSize22 textAlignC inputMargin20">会员激活金额</div>

        <div class="textAlignC positionRe" style="margin: 0 auto;width: 400px;">
            <img src="../img/bigCircle.png" alt="">
            <span class="textSize22 tOrange positionAb textAlignC" style="top: 65px;left: 102px;width: 200px"
                  ng-show="!activeMoneySaveOrModify" ng-bind="activeMoneyTemp"></span>
            <input class="textSize22 tOrange positionAb rowInput4"
                   ng-show="activeMoneySaveOrModify" type="text" ng-model="activeMoney"/>
            <div class="positionAb textSize18 tOrange" style="top: 100px;left: 165px;">当前金额</div>
        </div>
        <button ng-show="!activeMoneySaveOrModify" ng-click="activeMoneySaveOrModify=true"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">修改
        </button>
        <button ng-show="activeMoneySaveOrModify" ng-click="updateOrSaveActiveMoney()"
                class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30">保存
        </button>
        <button ng-show="activeMoneySaveOrModify" ng-click="notUpdate('activeMoney')"
                class="btn1 bgBlue borderBlue tFloatR selectInputMarginR30">取消
        </button>
    </div>
    <div ng-include="'/temp_new/grid.html'" class="h100B" ng-show="Log"></div>
</div>