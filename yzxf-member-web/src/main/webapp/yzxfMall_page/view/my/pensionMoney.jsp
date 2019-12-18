<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_pensionMoney_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto" style="height: 450px">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myPageRightContent">
            <div class="memberPHDiv" >
                <div class="floatL memberMoney flex1">
                    <div class="floatL" style="margin-left: 20px;margin-right: 10px">我的养老金</div>
                    <div class="floatL" style="color: #138bbe;font-size: 22px">¥</div>
                    <div class="floatL" style="color: #138bbe;font-size: 40px" ng-bind="getMoney(pensionMoney.pensionCount)">88</div>
                </div>
                <div class="floatR memberMoney flex1" style="border-left: 2px solid #dfdfdf">
                    <div class="floatL" style="margin-left: 5px;margin-right: 10px">未投保金额</div>
                    <div class="floatL" style="color: #138bbe;font-size: 22px">¥</div>
                    <div class="floatL" style="color: #138bbe;font-size: 40px" ng-bind="getMoney(pensionMoney.insureCount)">88</div>
                </div>
                <div class="floatR memberMoney flex1">
                    <div class="floatL" style="margin-right: 10px">已投保金额</div>
                    <div class="floatL" style="color: #138bbe;font-size: 22px">¥</div>
                    <div class="floatL" style="color: #138bbe;font-size: 40px" ng-bind="getMoney(pensionMoney.insureCountUse)">213</div>
                </div>
            </div>
            <%--交易记录--%>
            <div class="memberTradeDiv">
                <div class="tradeDiv">
                    <div class="floatL flex1 " ng-click="o2o='0';clearFun();queryCurrentList()" ng-class="o2o=='0'?'tradeTypeBtnClick':'tradeTypeBtn'">线上交易</div>
                    <div class="floatL flex1 " ng-click="o2o='1';clearFun();queryCurrentList()" ng-class="o2o=='1'?'tradeTypeBtnClick':'tradeTypeBtn'">线下交易</div>
                    <div class="floatR timeSelected">
                        时间筛选: <input type="date" ng-model="startDateTime" ng-change="getOrderList()"> 至 <input type="date" ng-model="endDateTime" ng-change="getOrderList()">
                    </div>
                </div>
                <div class="isNullBox" ng-show="orderList==null || orderList==''">
                    <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                    没有内容
                </div>
                <div ng-show="orderList!=null && orderList!=''">
                    <table class="orderContent">
                        <tr class="orderTableTitle">
                            <td class="" style="width:35%;margin-left: 18px">店铺信息</td>
                            <td class="" style="width:10%;text-align: center">积分率</td>
                            <td class="" style="width:10%;text-align: center">未投保</td>
                            <td class="" style="width:15%;text-align: center">消费金额</td>
                            <td class="" style="width:10%;text-align: center">养老金</td>
                            <td class="" style="width:20%;text-align: center">时间</td>
                        </tr>
                        <tr class="orderTableList" ng-repeat="order in orderList">
                            <td class="RePosition" style="width:35%;height: 120px;" ng-click="goPage('/seller/sellerInfo/sellerId/'+order.sellerId)">
                                <img class="AbPosition orderListImg" style="width: 100px;height: 100px" ng-src="{{iconImgUrl(getSellerIcon(order.sellerIcon,order.sellerDoorImg))}}" alt="">
                                <div class="AbPosition orderGoodsName pointer" ng-bind="order.name"></div>
                            </td>
                            <td class="" style="font-size:14px;height: 120px;width:10%;text-align: center;color: #148BBD;" ng-bind="order.integralRate+'%'"></td>
                            <td class="" style="font-size:14px;height: 120px;width:10%;text-align: center;" ng-bind="'¥'+getMoney(order.insureCount)"></td>
                            <td class="" style="font-size:14px;height: 120px;width:15%;text-align: center;color: #ff0e0c;" ng-bind="'¥'+getMoney(order.payMoney)"></td>
                            <td class="" style="font-size:14px;height: 120px;width:10%;text-align: center;color: #148BBD;" ng-bind="'¥'+getMoney(order.pensionMoney)"></td>
                            <td class="" style="width:20%;text-align: center;height: 120px;" ng-bind="showYFullTime(order.createTime)"></td>
                        </tr>
                    </table>
                </div>
                <form class="pageMain"
                      ng-submit="getOrderList();">
                    <div class="sectionPage" ng-show="dataPage.totalNum>0">
                        <div style="margin: 0 auto;">
                            <div class="btn3" ng-click="pageNext(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                            <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"  ng-show="dataPage.pageNo>4"></div>
                            <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                            <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                                 ng-click="pageNumber(i)"
                                 ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                            <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                            <div class="btn3" ng-bind="dataPage.totalPage"
                                 ng-click="setPageNo(dataPage.totalPage);"
                                 ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                            <div class="btn3" ng-click="pageNext(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                                下一页
                            </div>
                            <div class="pageGo"
                                 ng-show="dataPage.totalPage>1">
                                <input type="text" placeholder="跳转" ng-model="pageGo">
                                <button class="iconfont icon-right-1-copy" ng-click="pageGoFunC(pageGo)"></button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>