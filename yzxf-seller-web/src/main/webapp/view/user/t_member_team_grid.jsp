<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popTitle2">
    <%--<div ng-repeat="child in orderList" ng-bind="child.nameSeller" ng-click="setSelectedItem(child)"--%>
         <%--ng-class="child._id==dataPage.$$selectedItem._id?'popTitle2-selected':''"></div>--%>
    <div class="subIndex===0?'popTitle2-selected':''" ng-click="checkSubPage(0)">会员</div>
    <div class="subIndex===1?'popTitle2-selected':''" ng-click="checkSubPage(1)">交易记录</div>
</div>

<div class="popSection flex2">
    <div class="popTitle">收益信息</div>
    <div>
        <span>团队总收益:</span>
        <span ng-bind="teamCount.total+' 元 ('+teamCount.totalNum+'笔交易)'"></span>
    </div>
    <div>
        <span>当月收益:</span>
        <span ng-bind="teamCount.month+' 元 ('+teamCount.monthNum+'笔交易)'"></span>
    </div>
    <div>
        <span>今日收益:</span>
        <span ng-bind="teamCount.day+' 元 ('+teamCount.dayNum+'笔交易)'"></span>
    </div>
</div>

<div ng-show="subIndex===0">
    <div class="popSection flex2" ng-if="entity=='Member'">
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

    <div class="popSection flex2" ng-show="teamList.up!=null && !jquery.isEmptyObject(teamList.up)" ng-if="entity=='Member'">
        <div class="popTitle">上级分享会员(推荐人)</div>
        <div>
            <span>真实姓名</span>
            <span ng-bind="teamList.up.realName"></span>
        </div>
        <div>
            <span>会员卡号</span>
            <span ng-bind="teamList.up.cardNo"></span>
        </div>
        <div>
            <span>身份证号码</span>
            <span ng-bind="teamList.up.idCard"></span>
        </div>
        <div>
            <span>手机号码</span>
            <span ng-bind="teamList.up.mobile"></span>
        </div>
        <div>
            <span>注册时间</span>
            <span ng-bind="showYFullTime(teamList.up.createTime)"></span>
        </div>
    </div>

    <div class="popSection flex2">
        <div class="popTitle">分享会员</div>
        <div style="width:100%;padding:0">
            <div class="sectionTable">
                <div>
                    <div style="width:25%">会员卡号</div>
                    <div style="width:15%">真实姓名</div>
                    <div style="width:20%">身份证号码</div>
                    <div style="width:20%">手机号码</div>
                    <div style="width:20%">注册时间</div>
                </div>
                <div class="trBk" ng-repeat="order in page.items"
                     ng-class="page.$$selectedItem.teamId==order.teamId?'selected':''"
                     ng-click="page.$$selectedItem=order;">
                    <div style="width:25%" ng-bind="order.cardNo"></div>
                    <div style="width:15%" ng-bind="order.realName"></div>
                    <div style="width:20%" ng-bind="order.idCard"></div>
                    <div style="width:20%" ng-bind="order.mobile"></div>
                    <div style="width:20%" ng-bind="showYFullTime(order.createTime)"></div>
                </div>
            </div>
            <div class="isNullBox" ng-show="isNullPage2">
                <div class="iconfont icon-meiyouneirong isNullIcon"></div>
                <div class="font25px colorGrayccc">没有数据</div>
            </div>
        </div>
    </div>
</div>

<div ng-show="subIndex===1">
    <div class="popSection flex2">
        <div class="popTitle">交易记录</div>
        <div style="width:100%;padding:0">
            <div class="sectionTable">
                <div>
                    <div style="width:20%">订单号码</div>
                    <div style="width:20%">收益金额</div>
                    <div style="width:20%">会员手机</div>
                    <div style="width:20%">真实姓名</div>
                    <div style="width:20%">下单时间</div>
                </div>
                <div class="trBk" ng-repeat="order in page.items"
                     ng-class="page.$$selectedItem.orderNo==order.orderNo?'selected':''"
                     ng-click="page.$$selectedItem=order;">
                    <div style="width:20%" ng-bind="order.orderNo"></div>
                    <div style="width:20%" ng-bind="order.orderCash"></div>
                    <div style="width:20%" ng-bind="order.mobile"></div>
                    <div style="width:20%" ng-bind="order.realName"></div>
                    <div style="width:20%" ng-bind="showYFullTime(order.createTime)"></div>
                </div>
            </div>
            <div class="isNullBox" ng-show="isNullPage2">
                <div class="iconfont icon-meiyouneirong isNullIcon"></div>
                <div class="font25px colorGrayccc">没有数据</div>
            </div>
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