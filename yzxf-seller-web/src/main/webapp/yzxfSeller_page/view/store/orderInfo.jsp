<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_orderInfo_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="overflowPC" style="position: relative;">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack black" ng-click="goBack()"></span>
            订单详情
        </div>
        <div class="gray888 mainRowLeft10 lineHeight50">订单产品信息</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="order-info1" ng-repeat="product in order.productItems">
                <img src="{{iconImgUrl(product.icon)}}">
                <div class="order-text1">
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
        <div class="gray888 mainRowLeft10 lineHeight50">订单信息</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow">
                <div class="rowTitle">订单金额:</div>
                <div class="rowInput" ng-bind="order.totalPrice+'元'"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">支付方式:</div>
                <div class="rowInput" ng-bind="getPayType(order.payType)"></div>
            </div>
            <div class="mainRow" ng-show="order.bookingTime!=null">
                <div class="rowTitle">下单时间:</div>
                <div class="rowInput" ng-bind="showYFullTime(order.bookingTime)"></div>
            </div>
        </div>
        <div class="gray888 mainRowLeft10 lineHeight50" ng-show="order.couponId!=null && order.couponId!=''">卡券使用信息</div>
        <div class="sectionMain sectionMainNotMargin" ng-show="order.couponId!=null && order.couponId!=''">
            <div class="mainRow">
                <div class="rowTitle">序列号:</div>
                <div class="rowInput" ng-bind="order.couponId"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">卡券名称:</div>
                <div class="rowInput" ng-bind="coupon.name"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">使用条件:</div>
                <div class="rowInput" ng-bind="'满'+coupon.condition+'元立减'+coupon.value+'元'"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">实际折扣:</div>
                <div class="rowInput" ng-bind="order.couponPrice+'元'"></div>
            </div>
            <%--<div class="mainRow">--%>
            <%--<div class="rowTitle">有效时间:</div>--%>
            <%--<div class="rowInput mainRowLeft90" ng-bind="showYFullTime(coupon.startTime)+'-'+showYFullTime(coupon.endTime)"></div>--%>
            <%--</div>--%>
            <div class="mainRow">
                <div class="rowTitle">使用时间:</div>
                <div class="rowInput" ng-bind="showYFullTime(couponLink.useTime)"></div>
            </div>
        </div>
        <div class="gray888 mainRowLeft10 lineHeight50">买家信息</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow">
                <div class="rowTitle">会员卡号:</div>
                <div class="rowInput" ng-bind="order.memberCard"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">收货人名:</div>
                <div class="rowInput" ng-bind="order.sendContact"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">发货地址:</div>
                <div class="rowInput" ng-bind="order.sendAddress"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">邮政编码:</div>
                <div class="rowInput" ng-bind="order.sendPostcode"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">联系手机:</div>
                <div class="rowInput" ng-bind="order.sendContactPhone"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">买家留言:</div>
                <div class="rowInput" ng-bind="order.memberRemark"></div>
            </div>
        </div>
        <div ng-if="showMod[1]">
            <div class="gray888 mainRowLeft10 lineHeight50">快递信息</div>
            <div class="sectionMain sectionMainNotMargin">
                <div class="mainRow">
                    <div class="rowTitle">快递公司:</div>
                    <div class="rowInput" ng-bind="order.express"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">快递单号:</div>
                    <div class="rowInput" ng-bind="order.expressNo"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">发货时间:</div>
                    <div class="rowInput" ng-bind="order.sendTime?showYFullTime(order.sendTime):''"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus>=5">
                    <div class="rowTitle">收货时间:</div>
                    <div class="rowInput" ng-bind="order.accountTime?showYFullTime(order.accountTime):''"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus==100">
                    <div class="rowTitle">结单时间:</div>
                    <div class="rowInput" ng-bind="order.endTime?showYFullTime(order.endTime):''"></div>
                </div>
                <%--<div class="mainRow">--%>
                    <%--<div class="rowTitle">运费:</div>--%>
                    <%--<div class="rowInput" ng-bind="order.freight+'元'"></div>--%>
                <%--</div>--%>
            </div>
        </div>
        <div ng-if="showMod[2]">
            <div class="gray888 mainRowLeft10 lineHeight50">退货信息</div>
            <div class="sectionMain sectionMainNotMargin">
                <div class="mainRow" ng-show="order.orderStatus==9">
                    <div class="rowTitle">退货金额:</div>
                    <div class="rowInput" ng-bind="order.returnPrice"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">申请时间:</div>
                    <div class="rowInput" ng-bind="showYFullTime(order.returnApplyTime)"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus>=9 && order.isReturn">
                    <div class="rowTitle">退货时间:</div>
                    <div class="rowInput" ng-bind="showYFullTime(order.returnTime)"></div>
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
                    <div class="rowInput" ng-bind="order.returnDesc"></div>
                </div>
                <div class="mainRow" ng-show="order.orderStatus>=7 && !order.isReturn">
                    <div class="rowTitle">拒绝理由:</div>
                    <div class="rowInput" ng-bind="order.returnRefuse"></div>
                </div>
                <div ng-show="order.orderStatus>=7 && order.isReturn">
                    <div class="mainRow">
                        <div class="rowTitle">收货人:</div>
                        <div class="rowInput" ng-bind="order.returnContact"></div>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle">联系手机:</div>
                        <div class="rowInput" ng-bind="order.returnPhone"></div>
                    </div>
                    <div class="mainRow" style="border-bottom: 1px solid #eee;">
                        <div class="rowTitle">收货地址:</div>
                        <div class="rowInput" ng-bind="order.returnAddress"></div>
                    </div>
                </div>
                <div ng-show="order.orderStatus>=8 && order.isReturn">
                    <div class="mainRow">
                        <div class="rowTitle">快递公司:</div>
                        <div class="rowInput" ng-bind="order.returnExpress"></div>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle">快递单号:</div>
                        <div class="rowInput" ng-bind="order.returnExpressNo"></div>
                    </div>
                </div>
            </div>
            <div class="flex1" style="padding: 0 20px;">
                <button type="button" class="submitBtn" ng-show="order.orderStatus==6" ng-click="setShowMod(4)">同意退货</button>
                <button type="button" class="submitBtn bgRed" ng-show="order.orderStatus==6" ng-click="setShowMod(3)">拒绝退货</button>
            </div>
        </div>

        <button ng-if="order.orderStatus==2" class="submitBtn" ng-click="setShowMod(0)">填写发货信息</button>
        <button ng-if="order.orderStatus==2" class="submitBtn bgRed" ng-click="notStockReturn()">库存无货，退款给会员</button>
        <%--<button ng-if="order.orderStatus==8" class="submitBtn" ng-click="submitDrawback()">[退货]确认收货</button>--%>
    </div>

    <div class="hideMenu" ng-show="showMod[0]">
        <div class="winCon-spec bgGrayeee">
            <div class="title">
                <span class="icon-left-1 iconfont titleBack black" ng-click="setShowMod(0)"></span>
                发货信息
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">快递公司:</div>
                    <input class="rowInput" ng-model="tempData.express"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">快递单号:</div>
                    <input class="rowInput" ng-model="tempData.expressNo"/>
                </div>
            </div>
            <button type="button" class="submitBtn" ng-click="getSubmit()">确认发货</button>
        </div>
    </div>

    <div class="hideMenu" ng-show="showMod[3]">
        <div class="winCon-spec bgGrayeee">
            <div class="title">
                <span class="icon-left-1 iconfont titleBack black" ng-click="setShowMod(3)"></span>
                拒绝退货理由
            </div>
            <div class="gray888 mainRowLeft10 lineHeight50">拒绝退货理由</div>
            <textarea ng-model="drawOrder.returnRefuse" maxlength="255" placeholder="请填写拒绝理由(255个字符长度以内)"
                      class="rowTextarea"></textarea>
            <button type="button" class="submitBtn" ng-click="drawback(false)" ng-disabled="isDrawbackBtn">提交</button>
        </div>
    </div>

    <div class="hideMenu" ng-show="showMod[4]">
        <div class="winCon-spec bgGrayeee">
            <div class="title">
                <span class="icon-left-1 iconfont titleBack black" ng-click="setShowMod(4)"></span>
                确认收货地址
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">收货人:</div>
                    <input class="rowInput" ng-model="drawOrder.returnContact" maxlength="10"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">联系手机:</div>
                    <input class="rowInput" ng-model="drawOrder.returnPhone"/>
                </div>
            </div>
            <div class="gray888 mainRowLeft10 lineHeight50">退货收货地址</div>
            <textarea ng-model="drawOrder.returnAddress" maxlength="300" placeholder="请填写退货的收货地址(300个字符长度以内)"
                      class="rowTextarea"></textarea>

            <div class="gray888 mainRowLeft10 lineHeight25">备注:您填写的退货信息将展示给会员,会员将根据您提供的地址信息退还商品</div>
            <button type="button" class="submitBtn" ng-click="drawback(true)" ng-disabled="isDrawbackBtn">提交</button>
        </div>
    </div>
</div>