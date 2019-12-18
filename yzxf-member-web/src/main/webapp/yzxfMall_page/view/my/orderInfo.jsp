<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_orderInfo_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth orderInfoDiv">
        <div class="orderInfoTitle">
            <div class="floatL" style="font-size: 30px;">订单详情</div>
            <div class="floatR pointer" style="font-size: 20px;color: #ff0e0c" ng-click="goPage('/my/order')">返回订单</div>
        </div>
        <div class="orderInfoTable">
            <div class="tableTrGray overflowH"><span style="color: #ff0e0c;font-size: 18px;">订单详情</span></div>
            <div  class="tableTdWhite">
                <div class="floatL" style="width: 33%"><span class="tdText">订单编号: <span ng-bind="orderInfo.orderNo"></span></span></div>
                <div class="floatL colorRed1" style="width: 33%"><span class="tdText">订单状态: <span ng-bind="getOrderStatus()"></span></span></div>
                <div class="floatL" style="width: 33%"><span class="tdText">支付方式: <span ng-bind="getPayType(orderInfo.payType)"></span></span></div>
                <div class="floatL" style="width: 33%" ng-show="orderInfo.orderStatus>=2"><span class="tdText">下单时间: <span ng-bind="showYFullTime(orderInfo.createTime)"></span></span></div>
                <div class="floatL" style="width: 33%" ng-show="orderInfo.orderStatus>=4"><span class="tdText">送货时间: <span ng-bind="showYFullTime(orderInfo.sendTime)"></span></span></div>
                <div class="floatL" style="width: 33%" ng-show="orderInfo.orderStatus>=5"><span class="tdText">收货时间: <span ng-bind="showYFullTime(orderInfo.accountTime)"></span></span></div>
                <div class="floatL" style="width: 33%" ng-show="orderInfo.orderStatus>=100"><span class="tdText">结单时间: <span ng-bind="showYFullTime(orderInfo.endTime)"></span></span></div>
            </div>
            <div class="tableTrGray overflowH" ng-show="orderInfo.orderStatus>=6"><span>退货信息</span></div>
            <div  class="tableTdWhite" ng-show="orderInfo.orderStatus>=6">
                <div class="floatL"  style="width: 33%"><span class="tdText">商家: <span ng-bind="orderInfo.sellerName"></span></span></div>
                <div class="floatL" style="width: 33%" ng-show="orderInfo.orderStatus>=6"><span class="tdText">申请退货时间: <span ng-bind="showYFullTime(orderInfo.returnApplyTime)"></span></span></div>
                <div class="floatL" style="width: 33%" ng-show="orderInfo.orderStatus>=9 && orderInfo.isReturn"><span class="tdText">退货时间: <span ng-bind="showYFullTime(orderInfo.returnTime)"></span></span></div>
                <div class="floatL colorRed3" style="width: 33%" ng-show="orderInfo.orderStatus==9"><span class="tdText">退货金额: <span ng-bind="orderInfo.returnPrice"></span></span></div>
                <div class="floatL" style="width: 100%"><span class="tdText">退货理由: <span ng-bind="orderInfo.returnDesc"></span></span></div>
                <div class="floatL" style="width: 100%" ng-show="orderInfo.orderStatus>=7 && !orderInfo.isReturn"><span class="tdText">拒绝退货理由: <span ng-bind="orderInfo.returnRefuse"></span></span></div>

                <div ng-show="orderInfo.orderStatus>=7 && orderInfo.isReturn">
                    <div class="floatL"  style="width: 33%"><span class="tdText">收货人: <span ng-bind="orderInfo.returnContact"></span></span></div>
                    <div class="floatL"  style="width: 33%"><span class="tdText">联系电话: <span ng-bind="orderInfo.returnPhone"></span></span></div>
                    <div class="floatL"  style="width: 100%"><span class="tdText">收货地址: <span ng-bind="orderInfo.returnAddress"></span></span></div>
                </div>
                <div ng-show="orderInfo.orderStatus>=8 && orderInfo.isReturn">
                    <div class="floatL"  style="width: 50%"><span class="tdText">快递公司: <span ng-bind="orderInfo.returnExpress"></span></span></div>
                    <div class="floatL"  style="width: 50%"><span class="tdText">快递单号: <span ng-bind="orderInfo.returnExpressNo"></span></span></div>
                </div>
            </div>
            <div class="tableTrGray overflowH"><span>发货信息</span></div>
            <div  class="tableTdWhite">
                <div class="floatL"  style="width: 15%"><span class="tdText">收货人: <span ng-bind="orderInfo.sendContact"></span></span></div>
                <div class="floatL"  style="width: 55%;text-align: center"><span class="tdText">收货地址: <span ng-bind="orderInfo.sendAddress"></span></span></div>
                <div class="floatL"  style="width: 30%"><span class="tdText">联系电话: <span ng-bind="orderInfo.sendContactPhone"></span></span></div>
            </div>
            <div class="tableTrGray overflowH"><span>金额情况</span></div>
            <div class="tableTdWhite">
                <div class="floatL" style="width: 33%"><span class="tdText">订单金额: <span ng-bind="'¥ '+orderInfo.totalPrice"></span></span></div>
                <div class="floatL colorRed3" style="width: 33%"><span class="tdText">实付: <span ng-bind="'¥ '+orderInfo.totalPrice"></span></span></div>
                <div class="floatL colorRed3" style="width: 33%"><span class="tdText">获得养老金: <span ng-bind="orderInfo.pensionMoney"></span></span></div>
            </div>
            <div class="tableTrGray overflowH" ng-show="orderInfo.couponId!=null && orderInfo.couponId!=''"><span>优惠券使用情况</span></div>
            <div class="tableTdWhite" ng-show="orderInfo.couponId!=null && orderInfo.couponId!=''">
                <div class="floatL" style="width: 50%"><span class="tdText">序列号: <span ng-bind="orderInfo.couponId"></span></span></div>
                <div class="floatL colorRed3" style="width: 50%"><span class="tdText">卡券名称: <span ng-bind="coupon.name"></span></span></div>
                </div>
            <div class="tableTdWhite" ng-show="orderInfo.couponId!=null && orderInfo.couponId!=''">
                <div class="floatL colorRed3" style="width: 50%"><span class="tdText">使用条件: <span ng-bind="'满'+coupon.condition+'元立减'+coupon.value+'元'"></span></span></div>
                <div class="floatL colorRed3" style="width: 50%"><span class="tdText">实际折扣: <span ng-bind="orderInfo.couponPrice+'元'"></span></span></div>
            </div>
        </div>
        <div class="orderInfoTable">
            <div class="tableTrGray overflowH" style="height: 40px;line-height: 40px">
                <div class="floatL" style="width: 10%;text-align: center;font-size: 15px">商品图片</div>
                <div class="floatL" style="width: 30%;text-align: center;font-size: 15px">商品名称</div>
                <div class="floatL" style="width: 10%;text-align: center;font-size: 15px">数量</div>
                <div class="floatL" style="width: 50%;text-align: center;font-size: 15px">规格</div>
            </div>
            <div class="tableTdWhite" style="height: 50px;padding: 10px 0;border-top: 1px solid #DFDFDF"
                ng-repeat="product in orderInfo.productItems">
                <div class="floatL" style="width: 10%;text-align: center;height: 50px;" ng-click="goPage('/seller/commodityInfo/goodsId/'+product.productId)"><img style="width: 40px" src="{{iconImgUrl(product.icon)}}" alt=""></div>
                <div class="floatL textEllipsis" style="width: 30%;text-align: center;margin-top: -15px;font-size: 15px" ng-bind="product.name"></div>
                <div class="floatL" style="width: 10%;text-align: center;line-height: 50px;" ng-bind="product.count">1</div>
                <div class="floatL textEllipsis" style="width: 50%;text-align: center;line-height: 50px;">
                    <span ng-repeat="spec in product.selectSpec" ng-bind="'【'+spec.name+' : '+spec.items+'】'"></span>
                </div>
            </div>
        </div>
    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>