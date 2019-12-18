<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div ng-include="'/temp_new/grid.html'" class="h100B"></div>
<%--<div class="section5">--%>
        <%--<div class="sectionTitle">--%>
            <%--<div class="fl rowTitle1">交易流水--%>
                <%--<span class="colorGray888" ng-bind=" '(共'+totalNumber+'条记录)'"></span>--%>
            <%--</div>--%>
            <%--<div class="fr">--%>
                <%--<div class="btn1 fl bgOrange" ng-click="clearSelectData()">清除条件</div>--%>
                <%--<div class="btn1 fl bgBlue" ng-click="getOrderList()">查询</div>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="sectionQuery flex2">--%>
            <%--<div class="width100B">--%>
                <%--<span>归属:</span>--%>
                <%--<select class="h40px minW100px" ng-model="selectArea[0]" ng-options="area1.name for area1 in areaList[0]" ng-change="getLocation(selectArea[0].areaValue,2)"></select>--%>
                <%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[1]" ng-options="area1.name for area1 in areaList[1]" ng-change="getLocation(selectArea[1].areaValue,3)"></select>--%>
                <%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[2]" ng-options="area1.name for area1 in areaList[2]" ng-change="getLocation(selectArea[2].areaValue,4)"></select>--%>
                <%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[3]" ng-options="area1.name for area1 in areaList[3]" ng-change="getLocation(selectArea[3].areaValue,5)"></select>--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<span>日期:</span>--%>
                <%--<div ng-include="timeFilter"></div>--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<span>用户</span>--%>
                <%--<input type="text" class="inputQuery width300px" placeholder="请输入会员/商家的编号或手机号" ng-model="search">--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<span>交易类型</span>--%>
                <%--<select class="inputQuery" ng-model="selectTrade">--%>
                    <%--<option value="0">所有</option>--%>
                    <%--<option value="1">转账</option>--%>
                    <%--<option value="2">充值</option>--%>
                    <%--<option value="3">线上交易</option>--%>
                    <%--<option value="4">现金交易</option>--%>
                    <%--<option value="5">会员扫码</option>--%>
                <%--</select>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="sectionAction">--%>
            <%--<div ng-click="showDataInfo()">查看</div>--%>
        <%--</div>--%>

        <%--<table class="sectionTable">--%>
            <%--&lt;%&ndash;<thead>&ndash;%&gt;--%>
                <%--<tr>--%>
                    <%--<td style="width:15%">归属</td>--%>
                    <%--<td style="width:15%">订单编号</td>--%>
                    <%--<td style="width:10%">交易类型</td>--%>
                    <%--<td style="width:10%">会员卡号</td>--%>
                    <%--<td style="width:10%">商家</td>--%>
                    <%--<td style="width:5% ">积分率</td>--%>
                    <%--<td style="width:10%">交易金额</td>--%>
                    <%--<td style="width:10%">佣金</td>--%>
                    <%--<td style="width:15%">时间</td>--%>
                <%--</tr>--%>
            <%--&lt;%&ndash;</thead>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<tbody>&ndash;%&gt;--%>
                <%--<tr class="trBk" ng-repeat="order in orderList"--%>
                    <%--ng-click="selectOrder(order)"--%>
                    <%--ng-class="$$selectedItem._id==order._id?'selected':''">--%>
                    <%--<td style="width:15%" ng-bind="order.belongArea"></td>--%>
                    <%--<td style="width:15%" ng-bind="order.orderNo"></td>--%>
                    <%--<td style="width:10%" ng-bind="getTradeType(order.logTradeType)"></td>--%>
                    <%--<td style="width:10%" ng-bind="order.cardNo"></td>--%>
                    <%--<td style="width:10%" ng-bind="order.sellerName"></td>--%>
                    <%--<td style="width: 5%;color: #FED326" ng-bind="isNullNumber(order.score)+' %'"></td>--%>
                    <%--<td style="width:10%;color: red;" ng-bind="order.payMoney.toFixed(2)"></td>--%>
                    <%--<td style="width:10%;color: deepskyblue" ng-bind="order.brokerageCount.toFixed(2)"></td>--%>
                    <%--<td style="width:15%" ng-bind="showYFullTime(order.orderCreateTime)"></td>--%>
                <%--</tr>--%>
            <%--&lt;%&ndash;</tbody>&ndash;%&gt;--%>
        <%--</table>--%>
        <%--<div class="isNullBox" ng-show="isNullPage">--%>
            <%--<div class="iconfont icon-meiyouneirong isNullIcon"></div>--%>
            <%--<div class="font25px colorGrayccc">没有数据</div>--%>
        <%--</div>--%>

        <%--<div class="sectionPage" ng-hide="isNullPage">--%>
            <%--<div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex!=1">上一页</div>--%>
            <%--<div ng-show="isFirstPage">--%>
                <%--<div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>--%>
                <%--<div class="fl lineH30px">......</div>--%>
            <%--</div>--%>
            <%--<div class="btn3" ng-repeat="page in pageList" ng-bind="page.num"--%>
                 <%--ng-click="pageNumber(page.num);pageCur(page.num)"--%>
                 <%--ng-class="pageIndex==page.num?'bgBlue tWhite':''"></div>--%>
            <%--<div ng-show="isLastPage">--%>
                <%--<div class="fl lineH30px">......</div>--%>
                <%--<div class="btn3 fl marginLR5" ng-bind="totalPage" ng-click="pageNumber(totalPage)"></div>--%>
            <%--</div>--%>
            <%--<div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex!=totalPage">下一页</div>--%>
            <%--<div class="pageGo">--%>
                <%--<input type="text" placeholder="跳转" ng-model="pageGo" />--%>
                <%--<button class="iconfont icon-right-1-copy" ng-click="pageNumber(pageGo)"></button>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--&lt;%&ndash;<div ng-show="isOrderInfo">&ndash;%&gt;--%>
        <%--&lt;%&ndash;<div class="section1">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div style="margin-left: 60px;font-size: 18px">佣金分配情况</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<div class="levelsProfitDiv tFloat" style="border: 1px solid #ECA330;background-color: #ECA330">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitSmallDiv">1.5</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitName">平台</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<div class="levelsProfitDiv tFloat" style="border: 1px solid #9969E8;background-color: #9969E8">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitSmallDiv">0.5</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitName">省级代理商</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<div class="levelsProfitDiv tFloat" style="border: 1px solid #19A2E2;background-color: #19A2E2">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitSmallDiv">1.5</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitName">市级代理商</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<div class="levelsProfitDiv tFloat" style="border: 1px solid #865044;background-color: #865044">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitSmallDiv">7.25</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitName">县级代理商</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<div class="levelsProfitDiv tFloat" style="border: 1px solid #B58E77;background-color: #B58E77">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitSmallDiv">0.15</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="levelsProfitName">服务站</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<div class="section1">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<table class="sectionTable">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<tr>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 12%;text-align: right">订单编号:</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 88%;text-align: left">商品名称</td>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<tr>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%">商品图片</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%">商品名称</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%">商品数量</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%">商品单价</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%">商品总价</td>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<tr ng-repeat="">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%"></td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%"></td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%"></td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%"></td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width: 20%"></td>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</table>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
    <%--<div class="winCon" ng-show="showPopWin">--%>
        <%--<div class="content">--%>
            <%--<div class="contentTitle" ng-click="showPopWin=false;" ng-bind="popWinTitle"></div>--%>
            <%--<div class="close iconfont icon-plus" ng-click="closePopWin()"></div>--%>
            <%--<div class="include">--%>
                <%--<div class="popSection">--%>
                    <%--<div class="popTitle">归属机构:</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span>归属机构:</span>--%>
                        <%--<span ng-bind="$$selectedItem.belongArea"></span>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="popSection flex2">--%>
                    <%--<div class="popTitle">利润分配</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span>订单编号:</span>--%>
                        <%--<span ng-bind="$$selectedItem.orderNo"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span>平台利润:</span>--%>
                        <%--<span class="colorRed2" ng-bind="$$agentLogList[0].orderCash+'元'"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span ng-bind="$$agentLogList[1].name+' (省级)利润:'"></span>--%>
                        <%--<span class="colorRed2" ng-bind="$$agentLogList[1].orderCash+'元'"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span ng-bind="$$agentLogList[2].name+' (市级)利润:'"></span>--%>
                        <%--<span class="colorRed2" ng-bind="$$agentLogList[2].orderCash+'元'"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span ng-bind="$$agentLogList[3].name+' (县级)利润:'"></span>--%>
                        <%--<span class="colorRed2" ng-bind="$$agentLogList[3].orderCash+'元'"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span ng-bind="$$factorLog.name+' (发卡点)利润:'"></span>--%>
                        <%--<span class="colorRed2" ng-bind="$$factorLog.orderCash+'元'"></span>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="popSection flex2">--%>
                    <%--<div class="popTitle">会员资料</div>--%>
                    <%--<div>--%>
                        <%--<span>会员姓名:</span>--%>
                        <%--<span ng-bind="$$selectedItem.memberName"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>当前状态:</span>--%>
                        <%--<span ng-bind="$$selectedItem.memberCanUse?'有效':'禁用'"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>手机号码:</span>--%>
                        <%--<span ng-bind="$$selectedItem.memberMobile"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>身份证号:</span>--%>
                        <%--<span ng-bind="$$selectedItem.memberIdCard"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>常用邮箱:</span>--%>
                        <%--<span ng-bind="$$selectedItem.memberEmail"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span>所在城市:</span>--%>
                        <%--<span ng-bind="$$selectedItem.memberRealArea+$$selectedItem.memberRealAddress"></span>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="popSection flex2">--%>
                    <%--<div class="popTitle">商家资料</div>--%>
                    <%--<div>--%>
                        <%--<span>商家名称:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerName"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>当前状态:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerCanUse?'有效':'禁用'"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>积分率:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerIntegralRate"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>联系人:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerContactPerson"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>联系电话:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerMobile"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>客服电话:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerServerPhone"></span>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span>经营范围:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerOperateType"></span>--%>
                    <%--</div>--%>
                    <%--<div style="width:100%">--%>
                        <%--<span>当前地址:</span>--%>
                        <%--<span ng-bind="$$selectedItem.sellerArea+$$selectedItem.sellerAddress"></span>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
</div>
