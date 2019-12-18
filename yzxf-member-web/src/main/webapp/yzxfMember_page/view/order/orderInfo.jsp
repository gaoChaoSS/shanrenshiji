<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="order_orderInfo_Ctrl" class="d_content title_section order_panel form_section">
    <div class="overflowPC">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack black" ng-click="goBack()"></span>
            订单详情
        </div>
        <div ng-if="showMod[1]">
            <div class="gray888 mainRowLeft10">快递信息</div>
            <div class="sectionMain sectionMainNotMargin">
                <div class="mainRow">
                    <div class="rowTitle">快递公司:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="order.express"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">快递单号:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="order.expressNo"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">发货时间:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(order.sendTime)"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus>=5">
                    <div class="rowTitle">收货时间:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(order.accountTime)"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus==100">
                    <div class="rowTitle">结单时间:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(order.endTime)"></div>
                </div>
            </div>
        </div>
        <div class="gray888 mainRowLeft10">订单产品信息</div>
        <div class="sectionMain sectionMainNotMargin">
            <div style="position: relative" ng-repeat="product in order.productItems track by $index">
                <div style="padding:8px;position: absolute;top:0;left:0;">
                    <img err-src="/yzxfMember_page/img/notImg02.jpg" style="width:80px;height: 80px;"
                         ng-click="goPage('/store/commodity/goodsId/'+product.productId)"
                         ng-src="{{iconImgUrl(product.icon)}}"/>
                </div>
                <div style="padding:5px 8px 8px 100px;min-height: 95px;">
                    <div>
                        <span ng-bind="product.name"></span>
                        <span class="gray888" ng-bind="' ( × '+product.count+' 件)'"></span>
                    </div>
                    <div>
                        <span class="gray888" ng-repeat="spec in product.selectSpec track by $index"
                              ng-bind="spec.name+' : '+spec.items+' '"></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="gray888 mainRowLeft10">订单信息</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow">
                <div class="rowTitle">订单金额:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.totalPrice+'元'"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">赠养老金:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.pensionMoney"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">支付方式:</div>
                <div class="rowInput mainRowLeft90" ng-bind="getPayType(order.payType)"></div>
            </div>
            <div class="mainRow" ng-show="order.bookingTime!=null">
                <div class="rowTitle">下单时间:</div>
                <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(order.bookingTime)"></div>
            </div>
        </div>
        <div class="gray888 mainRowLeft10" ng-show="order.couponId!=null && order.couponId!=''">卡券使用信息</div>
        <div class="sectionMain sectionMainNotMargin" ng-show="order.couponId!=null && order.couponId!=''">
            <div class="mainRow">
                <div class="rowTitle">序列号:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.couponId"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">卡券名称:</div>
                <div class="rowInput mainRowLeft90" ng-bind="coupon.name"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">使用条件:</div>
                <div class="rowInput mainRowLeft90" ng-bind="'满'+coupon.condition+'元立减'+coupon.value+'元'"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">实际折扣:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.couponPrice+'元'"></div>
            </div>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">有效时间:</div>--%>
                <%--<div class="rowInput mainRowLeft90" ng-bind="showYFullTime(coupon.startTime)+'-'+showYFullTime(coupon.endTime)"></div>--%>
            <%--</div>--%>
            <div class="mainRow">
                <div class="rowTitle">使用时间:</div>
                <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(couponLink.useTime)"></div>
            </div>
        </div>
        <div class="gray888 mainRowLeft10">买家信息</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow">
                <div class="rowTitle">会员卡号:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.memberCard"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">收货人名:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.sendContact"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">发货地址:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.sendAddress"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">邮政编码:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.sendPostcode"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">联系手机:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.sendContactPhone"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">买家留言:</div>
                <div class="rowInput mainRowLeft90" ng-bind="order.memberRemark"></div>
            </div>
        </div>

        <div ng-if="showMod[2]">
            <div class="gray888 mainRowLeft10">退货信息</div>
            <div class="sectionMain sectionMainNotMargin">
                <div class="mainRow" ng-show="order.orderStatus==9">
                    <div class="rowTitle">退货金额:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="order.returnPrice"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">申请时间:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(order.returnApplyTime)"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus>=9 && order.isReturn">
                    <div class="rowTitle">退货时间:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="showYFullTime(order.returnTime)"></div>
                </div>
                <div class="mainRow" ng-show="order.returnImg!=null && order.returnImg!=''">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        退货图片:
                    </div>
                    <img class="iconImg7" ng-show="order.returnImg!=null && order.returnImg!=''"
                         ng-src="/s_img/icon.jpg?_id={{order.returnImg}}&wh=650_0"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">退货理由:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="order.returnDesc"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus>=7 && !order.isReturn">
                    <div class="rowTitle">拒绝理由:</div>
                    <div class="rowInput mainRowLeft90" ng-bind="order.returnRefuse"></div>
                </div>
                <div ng-show="order.orderStatus>=7 && order.isReturn">
                    <div class="mainRow">
                        <div class="rowTitle">商家:</div>
                        <div class="rowInput mainRowLeft90" ng-bind="order.sellerName"></div>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle">收货人:</div>
                        <div class="rowInput mainRowLeft90" ng-bind="order.returnContact"></div>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle">联系手机:</div>
                        <div class="rowInput mainRowLeft90" ng-bind="order.returnPhone"></div>
                    </div>
                    <div class="mainRow" style="border-bottom: 1px solid #eee;">
                        <div class="rowTitle">收货地址:</div>
                        <div class="rowInput mainRowLeft90" ng-bind="order.returnAddress"></div>
                    </div>
                </div>
                <div ng-show="order.orderStatus>=8 && order.isReturn">
                    <div class="mainRow">
                        <div class="rowTitle">快递公司:</div>
                        <div class="rowInput mainRowLeft90" ng-bind="order.returnExpress"></div>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle">快递单号:</div>
                        <div class="rowInput mainRowLeft90" ng-bind="order.returnExpressNo"></div>
                    </div>
                </div>
            </div>
        </div>

        <button ng-if="order.orderStatus==7" class="submitBtn" ng-click="setShowMod(3)">填写发货信息</button>

        <div class="hideMenu" ng-if="showMod[3]">
            <div class="winCon-full">
                <div class="title">
                    <span class="icon-left-1 iconfont titleBack" ng-click="setShowMod(3)"></span>
                    发货信息
                </div>
                <div class="sectionMain">
                    <div class="mainRow">
                        <div class="rowTitle">快递公司:</div>
                        <input class="rowInput" ng-model="drawOrder.returnExpress"/>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle">快递单号:</div>
                        <input class="rowInput" ng-model="drawOrder.returnExpressNo"/>
                    </div>
                </div>
                <button class="submitBtn" ng-click="sendDrawbackOrder()">提交</button>
            </div>
        </div>
    </div>
</div>
