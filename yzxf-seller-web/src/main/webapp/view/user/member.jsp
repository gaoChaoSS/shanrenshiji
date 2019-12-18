<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">

    <div ng-include="'/temp_new/grid.html'" class="h100B"></div>
    <%--<div ng-show="!checkInfo" class="positionRe">
        <div class="section1">
            <span class="tLineHight45 selectInputMarginL30 tGry888 textSize15">会员详情</span>
            <button class="btn1 bgOrange borderOrange tFloatR selectInputMarginR30" ng-click="checkInfo=!checkInfo">
                返回
            </button>
        </div>
        <div class="headPhoto1">
            <div class="headPhotoHover1">
                <img class="headIcon" ng-src="{{iconImgUrl(memberInfo.icon)}}">
                <img class="bottomIcon" ng-show="!isRenzheng" class="positionAb" src="../img/renzheng1.png">
                <img class="bottomIcon" ng-show="isRenzheng" class="positionAb" src="../img/renzheng2.png">
                <div class="headText">
                    <span class="font16px" ng-bind="memberInfo.realName==null?'未实名认证':memberInfo.realName"></span>
                    <span class="font16px" ng-bind="memberInfo.isRealName?'已认证':'未认证'"></span>
                </div>
            </div>
        </div>
        <div class="section1 sectionUserInfo marginTop70 paddingTop100">
            <div>
                <span style="font-size: 23px;color:#FF78CE" class="icon-huiyuanshengri iconfont"></span>
                <span class="textSize18 selectInputMarginL10 tBlue"
                      ng-bind="memberInfo.idCard==null?'未绑定生日':memberInfo.idCard.substring(6,14)"> </span>
            </div>
            <div>
                <span style="font-size: 23px;color:#AD70FF" class="icon-dianhua iconfont"></span>
                <span class="textSize18 selectInputMarginL10 tBlue" ng-bind="memberInfo.mobile"></span>
            </div>
            <div>
                <span style="font-size: 23px;color:#6393FF" class="icon-youxiang iconfont"></span>
                <span class="textSize18 selectInputMarginL10 tBlue"
                      ng-bind="memberInfo.email==null?'未填写邮箱':memberInfo.email"></span>
            </div>
            <div>
                <span style="font-size: 23px;color:#46C8FF" class="icon-llcouponmapmark iconfont"></span>
                <span class="textSize18 selectInputMarginL10 tBlue"
                      ng-bind="memberInfo.realArea==null?'未填写地址':memberInfo.realArea"></span>
            </div>
            <div>
                <span style="font-size: 23px;color:#128BBD" class="icon-zhengjianguanli iconfont"></span>
                <span class="textSize18 selectInputMarginL10 tBlue">二代身份证</span>
            </div>
            <div>
                <span style="font-size: 23px;color:#FFB257" class="icon-zhengjianguanli iconfont"></span>
                <span class="textSize18 selectInputMarginL10 tBlue"
                      ng-bind="memberInfo.idCard==null?'未绑定身份证':memberInfo.idCard"></span>
            </div>
        </div>
        <div class="section1">
            <div class="sectionTitle">
                <div class="fl rowTitle1">养老投入金额情况</div>
            </div>
            <div class="sectionChart flex1" style="padding-bottom:0">
                <div class="roundItem positionR" style="min-width:auto">
                    <div id="myStat" data-dimension="150" data-total="150" data-part="30"
                         data-info="New Clients"
                         data-width="4" data-fontsize="15" data-percent="17.5" data-fgcolor="#ED5050"
                         data-bgcolor="#DBDBDB" data-fill="#fff"></div>
                    <div class="touruMoney">
                        <div>已投:<span class="tBlue" ng-bind="memberInfo.insureCountUse.toFixed(2)"></span></div>
                        <div>可投:<span class="tBlue" ng-bind="memberInfo.insureCount.toFixed(2)"></span></div>
                    </div>
                </div>
                <div ng-show="0" class="roundItem positionR" style="min-width:auto">
                    <div id="myStat1" data-dimension="150" data-total="150" data-part="30"
                         data-info="New Clients"
                         data-width="4" data-fontsize="15" data-percent="17.5" data-fgcolor="#E58619"
                         data-bgcolor="#DBDBDB" data-fill="#fff"></div>
                    <div class="touruMoney">
                        <div>已投:<span class="tBlue">30</span></div>
                        <div>可投:<span class="tBlue">120</span></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="section1">
            <div class="sectionTitle">
                <div class="fl rowTitle1">会员余额</div>
            </div>
            <div class="cardBk1">
                <div ng-bind="'卡号 : '+memberInfo.cardNo"></div>
                <div ng-bind="'余额 : '+memberInfo.balanceMoney.toFixed(2)"></div>
            </div>
        </div>
        <div class="section1">
            <table class="sectionTable">
                <tr>
                    <td style="width: 16%">交易时间</td>
                    <td style="width: 16%">支付方式</td>
                    <td style="width: 16%">养老金提成</td>
                    <td style="width: 16%">交易类型</td>
                    <td style="width: 16%">商户名</td>
                    <td style="width: 16%">消费金额</td>
                </tr>
                <tr ng-repeat="l in memberAccountList">
                    <td style="width: 16%"
                        ng-bind="showYFullTime(l.createTime)==null?'':showYFullTime(l.createTime)"></td>
                    <td style="width: 16%" ng-bind="payType(l.payType)==null?'':payType(l.payType)"></td>
                    <td style="width: 16%" ng-bind="l.pensionTrade.toFixed(2)==null?'':l.pensionTrade.toFixed(2)"></td>
                    <td style="width: 16%" ng-bind="tradeType(l.tradeType)==null?'':tradeType(l.tradeType)"></td>
                    <td style="width: 16%" ng-bind="l.sellerName==null?'':l.sellerName"></td>
                    <td style="width: 16%" ng-bind="l.orderCash.toFixed(2)==null?'':l.orderCash.toFixed(2)"></td>
                </tr>
            </table>
            <div class="isNullBox" ng-show="totalPageInfo<1 || totalPageInfo==null">
                <div class="iconfont icon-meiyouneirong isNullIcon"></div>
                <div class="font25px colorGrayccc">没有数据</div>
            </div>
            <div class="sectionPage" ng-hide="totalPageInfo<1 || totalPageInfo==null">
                <div class="btn2" ng-click="pageNextInfo(-1)" ng-show="pageIndexInfo!=1">上一页</div>
                <div ng-show="isFirstPageInfo">
                    <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumberInfo(1)"></div>
                    <div class="fl lineH30px">......</div>
                </div>
                <div class="btn3" ng-repeat="pageInfo in pageListInfo" ng-bind="pageInfo.num"
                     ng-click="pageNumberInfo(pageInfo.num);pageCurInfo(pageInfo.num)"
                     ng-class="pageIndexInfo==pageInfo.num?'bgBlue tWhite':''"></div>
                <div ng-show="isLastPageInfo">
                    <div class="fl lineH30px">......</div>
                    <div class="btn3 fl marginLR5" ng-bind="totalPageInfo"
                         ng-click="pageNumberInfo(totalPageInfo)"></div>
                </div>
                <div class="btn2" ng-click="pageNextInfo(1)" ng-show="pageIndexInfo!=totalPageInfo">下一页</div>
            </div>
        </div>
        --%>
</div>









