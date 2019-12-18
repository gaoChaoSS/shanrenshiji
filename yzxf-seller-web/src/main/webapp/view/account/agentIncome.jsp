<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div ng-include="'/temp_new/grid.html'" class="h100B"></div>

    <%--<div ng-show="checkCardManage">--%>
        <%--<div class="section1">--%>
            <%--<div class="sectionTitle">--%>
                <%--<div class="fl rowTitle1">代理商收益结算</div>--%>
                <%--<div class="fr">--%>
                    <%--<div class="btn1 fl bgOrange" ng-click="clearSellectData()">清除条件</div>--%>
                    <%--<div class="btn1 fl bgBlue" ng-click="criteriaSelect()">查询</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="sectionQuery flex2">--%>
                <%--<div class="width100B">--%>
                    <%--<span>归属:</span>--%>
                    <%--<select class="h40px minW100px" ng-model="selectArea[0]" ng-options="area1.name for area1 in areaList[0]" ng-change="getLocation(selectArea[0].areaValue,2)"></select>--%>
                    <%--<select class="marginLeft15 h40px minW100px" ng-model="selectArea[1]" ng-options="area1.name for area1 in areaList[1]" ng-change="getLocation(selectArea[1].areaValue,3)"></select>--%>
                    <%--&lt;%&ndash;<select class="marginLeft15 h40px minW100px" ng-model="selectArea[2]" ng-options="area1.name for area1 in areaList[2]" ng-change="getLocation(selectArea[2].areaValue,4)"></select>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<select class="marginLeft15 h40px minW100px" ng-model="selectArea[3]" ng-options="area1.name for area1 in areaList[3]" ng-change="getLocation(selectArea[3].areaValue,5)"></select>&ndash;%&gt;--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>日期:</span>--%>
                    <%--<div ng-include="timeFilter"></div>--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>代理商ID</span>--%>
                    <%--<input type="text" class="inputQuery width300px" placeholder="请输入完整的代理商编号" ng-model="filter.agentId">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>代理商名</span>--%>
                    <%--<input type="text" class="inputQuery width300px" placeholder="请输入代理商名" ng-model="filter.agentName">--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
        <%--<div class="section1">--%>
            <%--<table class="sectionTable">--%>
                <%--<tr>--%>
                    <%--<td style="width:20%">归属</td>--%>
                    <%--<td style="width:20%">代理商</td>--%>
                    <%--<td style="width:20%">交易金额</td>--%>
                    <%--<td style="width:20%">消费笔数</td>--%>
                    <%--<td style="width:20%">月份</td>--%>
                <%--</tr>--%>
                <%--<tr ng-repeat="aTList in agentTradeList">--%>
                    <%--<td style="width:20%" ng-bind="aTList.belongName"></td>--%>
                    <%--<td style="width:20%" ng-bind="aTList.name"></td>--%>
                    <%--<td style="width:20%" ng-bind="aTList.orderCash"></td>--%>
                    <%--<td style="width:20%" ng-bind="aTList.orderCount"></td>--%>
                    <%--<td style="width:20%" ng-bind="aTList.tradeNum"></td>--%>
                <%--</tr>--%>
            <%--</table>--%>
            <%--&lt;%&ndash;<table class="sectionTable">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<tr>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width:40%">代理商名</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width:30%">提成总金额</td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width:30%">消费笔数</td>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<tr ng-repeat="aTList in agentTradeList">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width:40%" ng-bind="aTList.name"></td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width:30%" ng-bind="aTList.pushMoney"></td>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<td style="width:30%" ng-bind="aTList.tradeNum"></td>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</table>&ndash;%&gt;--%>
            <%--<div class="isNullBox" ng-show="totalPage<1 || totalPage==null">--%>
                <%--<div class="iconfont icon-meiyouneirong isNullIcon"></div>--%>
                <%--<div class="font25px colorGrayccc">没有数据</div>--%>
            <%--</div>--%>
            <%--<div class="sectionPage" ng-hide="totalPage<1 || totalPage==null">--%>
                <%--<div class="btn2" ng-click="pageNext(-1,isCriteria)" ng-show="pageIndex!=1">上一页</div>--%>
                <%--<div ng-show="isFirstPage">--%>
                    <%--<div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1,isCriteria)"></div>--%>
                    <%--<div class="fl lineH30px">......</div>--%>
                <%--</div>--%>
                <%--<div class="btn3" ng-repeat="page in pageList" ng-bind="page.num"--%>
                     <%--ng-click="pageNumber(page.num,isCriteria);pageCur(page.num)"--%>
                     <%--ng-class="pageIndex==page.num?'bgBlue tWhite':''"></div>--%>
                <%--<div ng-show="isLastPage">--%>
                    <%--<div class="fl lineH30px">......</div>--%>
                    <%--<div class="btn3 fl marginLR5" ng-bind="totalPage" ng-click="pageNumber(totalPage,isCriteria)"></div>--%>
                <%--</div>--%>
                <%--<div class="btn2" ng-click="pageNext(1,isCriteria)" ng-show="pageIndex!=totalPage">下一页</div>--%>
                <%--<div class="pageGo">--%>
                    <%--<input type="text" placeholder="跳转" ng-model="pageGo">--%>
                    <%--<div class="iconfont icon-right-1-copy" ng-click="pageNumber(pageGo)"></div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
</div>