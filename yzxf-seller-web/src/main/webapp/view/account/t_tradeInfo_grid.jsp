<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popTitle2" ng-show="orderList!=null && orderList.length>1">
    <div ng-repeat="child in orderList" ng-bind="child.nameSeller" ng-click="setSelectedItem(child)"
         ng-class="child._id==dataPage.$$selectedItem._id?'popTitle2-selected':''"></div>
</div>

<div class="popSection flex2" ng-if="!jQuery.isEmptyObject($$team) && !isInitBill">
    <div class="popTitle" style="margin-top:0">利润分配</div>
    <div style="width:100%">
        <span>订单编号:</span>
        <span ng-bind="dataPage.$$selectedItem.orderNo"></span>
    </div>

    <div class="sectionTable" style="width:100%">
        <div>
            <div style="width:80%">名称</div>
            <div style="width:20%">金额(元)</div>
        </div>
        <div ng-repeat="t in $$team" ng-show="isShow(t.level,agent.level)">
            <div style="width:80%" ng-bind="t.name==null?t.mobile:t.name"></div>
            <div style="width:20%" ng-bind="getMoney(t.orderCash)" class="colorRed2"></div>
        </div>
    </div>
</div>

<%--<div class="popSection flex2" ng-if="modelList.card && !isInitBill">--%>
    <%--<div class="popTitle" style="margin-top:0">发卡利润分配</div>--%>
    <%--<div class="sectionTable" style="width:100%">--%>
        <%--<div>--%>
            <%--<div style="width:20%">类型</div>--%>
            <%--<div style="width:60%">名称</div>--%>
            <%--<div style="width:20%">金额(元)</div>--%>
        <%--</div>--%>
        <%--<div ng-repeat="agentLog in $$agentLog" ng-show="agentLog.agentName!=null&&agent.level<=agentLog.level">--%>
            <%--<div style="width:20%" ng-bind="agentLevelNum(agentLog.level)"></div>--%>
            <%--<div style="width:60%" ng-bind="agentLog.agentName"></div>--%>
            <%--<div style="width:20%" ng-bind="getMoney(agentLog.orderCash)" class="colorRed2"></div>--%>
        <%--</div>--%>
        <%--<div ng-show="factor[0].orderCash!=null">--%>
            <%--<div style="width:20%" ng-bind="'服务站'"></div>--%>
            <%--<div style="width:60%" ng-bind="factor[0].factorName"></div>--%>
            <%--<div style="width:20%" ng-bind="factor[0].orderCash==null?'无':getMoney(factor[0].orderCash)" class="colorRed2"></div>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<div style="width:20%" ng-bind="'会员(投保)'"></div>--%>
            <%--<div style="width:60%" ng-bind="userList[0].realName"></div>--%>
            <%--<div style="width:20%" ng-bind="'0'" class="colorRed2"></div>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>
<%--<div class="popSection flex2" ng-if="modelList.recharge && !isInitBill">--%>
    <%--<div class="popTitle" style="margin-top:0">充值利润分配</div>--%>

    <%--<div class="sectionTable">--%>
        <%--<div>--%>
            <%--<div style="width:20%">类型</div>--%>
            <%--<div style="width:60%">名称</div>--%>
            <%--<div style="width:20%">金额(元)</div>--%>
        <%--</div>--%>
        <%--<div ng-repeat="agentLog in $$agentLog" ng-show="agentLog.agentName!=null&&agent.level<=agentLog.level">--%>
            <%--<div style="width:20%" ng-bind="agentLevelNum(agentLog.level)"></div>--%>
            <%--<div style="width:60%" ng-bind="agentLog.agentName"></div>--%>
            <%--<div style="width:20%" ng-bind="getMoney(agentLog.orderCash)" class="colorRed2"></div>--%>
        <%--</div>--%>
        <%--<div ng-show="factor[0].orderCash!=null">--%>
            <%--<div style="width:20%" ng-bind="'服务站'"></div>--%>
            <%--<div style="width:60%" ng-bind="factor[0].factorName"></div>--%>
            <%--<div style="width:20%" ng-bind="factor[0].orderCash==null?'无':getMoney(factor[0].orderCash)" class="colorRed2"></div>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<div style="width:20%" ng-bind="'会员(养老金)'"></div>--%>
            <%--<div style="width:60%" ng-bind="userList[0].realName"></div>--%>
            <%--<div style="width:20%" ng-bind="getMoney(dataPage.$$selectedItem.pensionMoney)" class="colorRed2"></div>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>

<div class="popSection flex2" ng-show="entityTitle != '会员激活记录'">
    <div class="popTitle">支付情况</div>
    <div>
        <span>订单编号:</span>
        <span ng-bind="dataPage.$$selectedItem.orderNo"></span>
    </div>
    <div>
        <span>订单类型:</span>
        <span ng-bind="getTradeType(dataPage.$$selectedItem.orderType)"></span>
    </div>
    <div>
        <span>订单状态:</span>
        <span ng-bind="getOrderStatus(dataPage.$$selectedItem.orderStatus)" class="colorRed1"></span>
    </div>
    <div>
        <span>支付方式:</span>
        <span ng-bind="getPayType(dataPage.$$selectedItem.payType)"></span>
    </div>
    <div ng-show="orderItem.orderStatus>=2">
        <span>下单时间:</span>
        <span ng-bind="showYFullTime(orderItem.bookingTime)"></span>
    </div>
    <div ng-show="orderItem.orderStatus==100">
        <span>结单时间:</span>
        <span ng-bind="showYFullTime(orderItem.endTime)"></span>
    </div>
    <div>
        <span>付款人:</span>
        <span ng-bind="dataPage.$$selectedItem.payName"></span>
    </div>
    <div>
        <span>收款人:</span>
        <span ng-bind="dataPage.$$selectedItem.acqName"></span>
    </div>
    <div>
        <span>交易金额:</span>
        <span ng-bind="getMoney(dataPage.$$selectedItem.payMoney)+'元'"></span>
    </div>
    <div ng-show="dataPage.$$selectedItem.orderStatus==100 && (dataPage.$$selectedItem.orderType==2 || dataPage.$$selectedItem.orderType==12)">
        <span>退款操作:</span>
        <button class="btn1" ng-click="drawback()">退款</button>
    </div>
</div>

<div class="popSection flex2" ng-show="orderItem!=null">
    <div class="popTitle">订单产品信息</div>
    <div class="popSection flex2 popSection-mod" ng-repeat="product in orderItem.productItems">
        <div style="width:100%;white-space: normal;">
            <span>商品名称:</span>
            <span ng-bind="product.name"></span>
        </div>
        <div>
            <span>购买数量:</span>
            <span ng-bind="product.count"></span>
        </div>
        <div>
            <span>产品价格:</span>
            <span ng-bind="product.price"></span>
        </div>
        <div style="width:100%;white-space: normal;">
            <span>产品规格:</span>
        <span class="colorGray888" ng-repeat="spec in product.selectSpec"
              ng-bind="getSpec(spec)"></span>
        </div>
    </div>
</div>
<div class="popSection flex2" ng-show="orderItem.couponId!=null && orderItem.couponId!=''">
    <div class="popTitle">卡券使用信息</div>
    <div>
        <span>卡券序列号:</span>
        <span ng-bind="orderItem.couponId"></span>
    </div>
    <div>
        <span>卡券名称:</span>
        <span ng-bind="coupon.name"></span>
    </div>
    <div>
        <span>使用条件:</span>
        <span ng-bind="'满'+coupon.condition+'元立减'+coupon.value+'元'"></span>
    </div>
    <div>
        <span>实际折扣:</span>
        <span ng-bind="orderItem.couponPrice+'元'"></span>
    </div>
    <div style="width:100%">
        <span>有效时间:</span>
        <span ng-bind="showYFullTime(coupon.startTime)+' - '+showYFullTime(coupon.endTime)"></span>
    </div>
    <div>
        <span>使用时间:</span>
        <span ng-bind="showYFullTime(couponLink.useTime)"></span>
    </div>
</div>
<div class="popSection flex2" ng-show="orderItem!=null">
    <div class="popTitle">送货信息</div>
    <div ng-show="orderItem.express!=null">
        <span>快递公司:</span>
        <span ng-bind="orderItem.express"></span>
    </div>
    <div ng-show="orderItem.expressNo!=null">
        <span>快递单号:</span>
        <span ng-bind="orderItem.expressNo"></span>
    </div>
    <div ng-show="orderItem.orderStatus>=4">
        <span>送货时间:</span>
        <span ng-bind="showYFullTime(orderItem.sendTime)"></span>
    </div>
    <div ng-show="orderItem.orderStatus>=5">
        <span>收货时间:</span>
        <span ng-bind="showYFullTime(orderItem.accountTime)"></span>
    </div>
    <div>
        <span>收货人:</span>
        <span ng-bind="orderItem.sendContact"></span>
    </div>
    <div>
        <span>收货人手机:</span>
        <span ng-bind="orderItem.sendContactPhone"></span>
    </div>
    <div>
        <span>邮政编码:</span>
        <span ng-bind="orderItem.sendPostcode"></span>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>收货地址:</span>
        <span ng-bind="orderItem.sendAddress"></span>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>买家留言:</span>
        <span ng-bind="orderItem.memberRemark"></span>
    </div>
</div>

<div class="popSection flex2" ng-show="orderItem.orderType>=6 && orderItem.isApplyReturn!=null && orderItem.isApplyReturn">
    <div class="popTitle">退货信息</div>
    <div>
        <span>申请退货时间:</span>
        <span ng-bind="showYFullTime(orderItem.returnApplyTime)"></span>
    </div>
    <div ng-show="orderItem.returnRefuse==null && orderItem.orderStatus>=9">
        <span>退货结单时间:</span>
        <span ng-bind="showYFullTime(orderItem.returnTime)"></span>
    </div>
    <div ng-show="orderItem.orderStatus>6">
        <span>商家是否接受:</span>
        <span ng-bind="orderItem.returnRefuse==null?'已接受':'已拒绝'"></span>
    </div>
    <div ng-show="orderItem.returnExpress!=null">
        <span>快递公司:</span>
        <span ng-bind="orderItem.returnExpress"></span>
    </div>
    <div ng-show="orderItem.returnExpressNo!=null">
        <span>快递单号:</span>
        <span ng-bind="orderItem.returnExpressNo"></span>
    </div>
    <div ng-show="orderItem.isReturn && orderItem.orderStatus>=8">
        <span>收货人:</span>
        <span ng-bind="orderItem.returnContact"></span>
    </div>
    <div ng-show="orderItem.isReturn && orderItem.orderStatus>=8">
        <span>收货人手机:</span>
        <span ng-bind="orderItem.returnPhone"></span>
    </div>
    <div ng-show="orderItem.isReturn && orderItem.orderStatus>=8" style="width:100%;white-space: normal;">
        <span>收货地址:</span>
        <span ng-bind="orderItem.returnAddress"></span>
    </div>
    <div style="width:100%;white-space: normal;min-height:70px" ng-show="orderItem.return!=null && orderItem.return!=''">
        <span style="min-width:100px">会员退货图片:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(orderItem.returnImg)}}"
             ng-click="showImgFun(orderItem.returnImg)"/>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>会员退货留言:</span>
        <span ng-bind="orderItem.returnDesc"></span>
    </div>
    <div ng-show="orderItem.returnRefuse!=null" style="width:100%;white-space: normal;">
        <span>商家拒绝退货:</span>
        <span ng-bind="orderItem.returnRefuse"></span>
    </div>
</div>

<div class="popSection flex2" ng-show="modelList.member">
    <div class="popTitle">会员资料</div>
    <div style="width:100%;white-space: normal;">
        <span>归属:</span>
        <span ng-bind="userList[0].belongArea"></span>
    </div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="userList[0].realName"></span>
    </div>
    <div>
        <span>当前状态:</span>
        <span ng-bind="userList[0].canUse?'有效':'禁用'"></span>
    </div>
    <div>
        <span>手机号码:</span>
        <span ng-bind="userList[0].mobile"></span>
    </div>
    <div>
        <span>身份证号:</span>
        <span ng-bind="userList[0].idCard"></span>
    </div>
    <div style="width:100%">
        <span>身份证居住地:</span>
        <span ng-bind="userList[0].realArea+userList[0].realAddress"></span>
    </div>
    <div>
        <span>常用邮箱:</span>
        <span ng-bind="userList[0].email"></span>
    </div>
</div>
<div class="popSection flex2" ng-show="modelList.friend">
    <div class="popTitle">朋友资料</div>
    <div style="width:100%;white-space: normal;">
        <span>归属:</span>
        <span ng-bind="userList[3].belongArea"></span>
    </div>
    <div>
        <span>会员姓名:</span>
        <span ng-bind="userList[3].realName"></span>
    </div>
    <div>
        <span>当前状态:</span>
        <span ng-bind="userList[3].canUse?'有效':'禁用'"></span>
    </div>
    <div>
        <span>手机号码:</span>
        <span ng-bind="userList[3].mobile"></span>
    </div>
    <div>
        <span>身份证号:</span>
        <span ng-bind="userList[3].idCard"></span>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>身份证居住地:</span>
        <span ng-bind="userList[3].realArea+userList[3].realAddress"></span>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>常用邮箱:</span>
        <span ng-bind="userList[3].email"></span>
    </div>
</div>
<div class="popSection flex2" ng-show="modelList.seller">
    <div class="popTitle">商家资料</div>
    <div style="width:100%;white-space: normal;">
        <span>归属:</span>
        <span ng-bind="userList[1].belongArea"></span>
    </div>
    <div>
        <span>商家名称:</span>
        <span ng-bind="userList[1].name"></span>
    </div>
    <div>
        <span>当前状态:</span>
        <span ng-bind="userList[1].canUse?'有效':'禁用'"></span>
    </div>
    <div ng-show="dataPage.$$selectedItem.score!=0">
        <span>订单积分率:</span>
        <span ng-bind="dataPage.$$selectedItem.score+'%'"></span>
    </div>
    <div>
        <span>联系人:</span>
        <span ng-bind="userList[1].contactPerson"></span>
    </div>
    <div>
        <span>联系电话:</span>
        <span ng-bind="userList[1].phone"></span>
    </div>
    <div>
        <span>客服电话:</span>
        <span ng-bind="userList[1].serverPhone"></span>
    </div>
    <div>
        <span>经营范围:</span>
        <span ng-bind="userList[1].operateType"></span>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>当前地址:</span>
        <span ng-bind="userList[1].area+userList[1].address"></span>
    </div>
</div>
<div class="popSection flex2" ng-show="modelList.factor">
    <div class="popTitle">服务站资料</div>
    <div style="width:100%;white-space: normal;">
        <span>归属:</span>
        <span ng-bind="userList[2].belongArea"></span>
    </div>
    <div>
        <span>服务站名称:</span>
        <span ng-bind="userList[2].name"></span>
    </div>
    <div>
        <span>当前状态:</span>
        <span ng-bind="userList[2].canUse?'有效':'禁用'"></span>
    </div>
    <div>
        <span>联系人:</span>
        <span ng-bind="userList[2].contactPerson"></span>
    </div>
    <div>
        <span>联系电话:</span>
        <span ng-bind="userList[2].mobile"></span>
    </div>
    <div style="width:100%;white-space: normal;">
        <span>当前地址:</span>
        <span ng-bind="userList[2].area+userList[2].address"></span>
    </div>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-show="checkShowBtn()">
    <button class="fr btn1" ng-click="checkBtnFun(0)">一键补单</button>
    <button class="fr btn1" ng-click="checkBtnFun(2)">纠正单号</button>
</div>

<div class="sectionHintBk" ng-show="checkBtn[0]">
    <div class="sectionHint">
        <div class="lineH100px textCenter">是否确认补单?</div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm()">确认</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(-1)">取消</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[1]">
    <div class="sectionHint">
        <div class="lineH50px textCenter colorBlue1">补单成功</div>
        <div class="lineH50px" style="font-size:14px">请核对订单详细信息</div>
        <div class="flex1">
            <button class="btn1 bkColorRed1" ng-click="closeWin()">确定</button>
        </div>
    </div>
</div>

<div class="sectionHintBk" ng-show="checkBtn[2]">
    <div class="sectionHint" style="min-width: 500px;">
        <div class="textCenter">输入第三方订单号码</div>
        <div>
            <input type="text" ng-model="selectedPay.divId" style="margin: 50px 0;width: 100%;border: 1px solid #ccc;">
        </div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm()">确认</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(-1)">取消</button>
        </div>
    </div>
</div>

<div class="winCon" ng-click="closeImgFun()" ng-show="showImg!=''">
    <img class="winConImg" ng-src="{{iconImgUrl(showImg)}}"/>
</div>