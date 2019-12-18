<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="popSection flex2" ng-if="$$filter.$$queryType==='memberAll'">
    <div class="popTitle">会员信息</div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="dataPage.$$selectedItem.realName"></span>
    </div>
    <div>
        <span>会员卡号:</span>
        <span ng-bind="dataPage.$$selectedItem.cardNo"></span>
    </div>
</div>
<div ng-if="$$filter.$$queryType!=='logId'">
    <div class="popSection flex2">
        <div class="popTitle">投保记录</div>
        <div style="width:100%;padding:0">
            <div class="sectionTable">
                <div>
                    <div style="width:15%">保单号</div>
                    <%--<div style="width:15%">投保订单号</div>--%>
                    <div style="width:15%">交易流水号</div>
                    <div style="width:15%">交易类型</div>
                    <div style="width:10%">投保金额</div>
                    <div style="width:10%">流程</div>
                    <div style="width:10%">状态</div>
                    <div style="width:10%">会员</div>
                    <div style="width:15% ">时间</div>
                </div>
                <div class="trBk" ng-repeat="order in page.items"
                     ng-class="page.$$selectedItem._id==order._id?'selected':''"
                     ng-click="page.$$selectedItem=order;"
                     ng-dblclick="showInsureInfo(order._id)">
                    <div style="width:15%" ng-bind="order.contNo"></div>
                    <div style="width:15%" ng-bind="order.transSeq"></div>
                    <div style="width:15%" ng-bind="getOrderType(order.orderType)"></div>
                    <div style="width:10%;color: red;" ng-bind="getMoney(order.prem)+'元'"></div>
                    <div style="width:10%" ng-bind="getStatus(order.status)"></div>
                    <div style="width:10%" ng-bind="getReturnFlag(order.returnFlag)"></div>
                    <div style="width:10%" ng-bind="order.realName"></div>
                    <div style="width:15%" ng-bind="showYFullTime(order.createTime)"></div>
                </div>
            </div>
            <div class="isNullBox" ng-show="isNullPage2">
                <div class="iconfont icon-meiyouneirong isNullIcon"></div>
                <div class="font25px colorGrayccc">没有数据</div>
            </div>
        </div>
    </div>

    <div class="sectionPage" ng-class="winCheck?'bottom0':''" ng-hide="isNullPage2" style="position: absolute;left: 0;bottom: -40px;width: 100%;margin: 0;">
        <div class="btn3" ng-click="pageNext2(-1)" ng-show="pageIndex2!=1">上一页</div>
        <div ng-show="isFirstPage2">
            <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber2(1)"></div>
            <div class="fl lineH30px">......</div>
        </div>
        <div class="btn3" ng-repeat="page2 in pageList2" ng-bind="page2.num"
             ng-click="pageNumber2(page2.num);pageCur2(page2.num)"
             ng-class="pageIndex2==page2.num?'bgBlue tWhite':''"></div>
        <div ng-show="isLastPage2">
            <div class="fl lineH30px">......</div>
            <div class="btn3 fl marginLR5" ng-bind="totalPage2" ng-click="pageNumber2(totalPage2)"></div>
        </div>
        <div class="btn3" ng-click="pageNext2(1)" ng-show="pageIndex2!=totalPage2">下一页</div>
        <div class="pageGo">
            <input type="text" placeholder="跳转" ng-model="pageGo2" />
            <button class="iconfont icon-right-1-copy" ng-click="pageNumber2(pageGo2)"></button>
        </div>
    </div>
</div>

<div ng-if="$$filter.$$queryType==='logId'" ng-include="'/view/insure/t_insure_info_grid.jsp'"></div>