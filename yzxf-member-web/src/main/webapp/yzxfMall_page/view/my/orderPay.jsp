<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_my_Ctrl">
    <div id="blur">
        <%--头部模板--%>
        <div ng-include="mallHead"></div>

        <!--确认支付-->
        <div style="width:1000px;margin:50px auto 100px">
            <div class="iconfont icon-close btn-close1" ng-click="sureOrder=false"></div>
            <div class="win-title1">确认订单</div>

            <div class="mod-address1 mod-gray1 flex3 pointer" ng-click="isShowAddress=!isShowAddress">
                <div>收货地址</div>
                <div class="mod-address2 flex3" style="flex:8">
                    <div ng-bind="defaultAddress.name"></div>
                    <div style="flex:6" ng-bind="defaultAddress.area+defaultAddress.address"></div>
                    <div ng-bind="defaultAddress.phone"></div>
                </div>
                <div>
                    选择 <span class="iconfont icon-down" style="font-size: 34px;position: absolute"></span>
                </div>
            </div>

            <div class="mod-addressList1" ng-show="isShowAddress">
                <div class="mod-address2 mod-gray1 flex3" ng-repeat="address in addressList"
                     ng-click="selectedAddress(address)">
                    <div ng-bind="address.name"></div>
                    <div ng-bind="address.area+address.address" style="flex:6"></div>
                    <div ng-bind="address.phone"></div>
                </div>
            </div>

            <table class="table-pay table-default marginTop30">
                <tr class="table-head">
                    <td style="width:10%">商品图片</td>
                    <td style="width:30%">商品名称</td>
                    <td style="width:40%">规格</td>
                    <td style="width:10%">数量</td>
                    <td style="width:10%">单价</td>
                </tr>
            </table>
            <table class="table-pay" ng-repeat="cart in cartList track by $index">
                <tr>
                    <td class="padding2 colorGray666 textEllipsis" style="max-width: 400px;"
                        ng-bind="cart.sellerName" colspan="2"></td>
                    <td class="padding2" colspan="3">
                        <div class="floatR">
                            <span class="iconfont icon-youhuiquan font18px colorBlue1"></span>
                            <select ng-model="selectedCoupon[$index]"
                                    ng-options="coupon.name for coupon in cart.couponList"
                                    ng-change="setCoupon($index,selectedCoupon[$index],true)"></select>
                        </div>
                    </td>
                </tr>
                <tr class="table-pay-item" ng-repeat="product in cart.product track by $index">
                    <td style="width:10%"><img src="{{iconImgUrl(product.productIcon)}}"></td>
                    <td style="width:30%" ng-bind="product.productName"></td>
                    <td style="width:40%">
                        <span ng-repeat="spec in product.spec track by $index"
                              ng-bind="spec.name+' : '+spec.items+' '"></span>
                    </td>
                    <td style="width:10%" ng-bind="product.count"></td>
                    <td style="width:10%" class="colorRed3" ng-bind="getMoney(product.unitPrice)"></td>
                </tr>
                <tr class="textRight table-pay-bottom">
                    <td colspan="5">
                        <span>
                            折扣 <span class="colorRed3 marginRight20" ng-bind="'¥ '+selectedCoupon[$index].value"></span>
                        </span>
                        <span>
                            小计 <span class="colorRed3" ng-bind="'¥ '+cart.sellerPrice"></span>
                        </span>
                    </td>
                </tr>
            </table>

            <%--<textarea class="mod-remark" placeholder="您可以在此留言(50字以内)" ng-model="memberRemark"></textarea>--%>

            <div class="mod-block1">
                <div>总计: <span class="colorRed3 font25px" ng-bind="'¥ '+totalPrice"></span></div>
                <div class="font16px">卡券折扣: <span class="colorRed3" ng-bind="'¥ '+totalCouponPrice"></span></div>
                <div class="font16px">赠送养老金: <span class="colorRed3" ng-bind="'¥ '+totalPension"></span></div>
            </div>

            <div class="overflowH marginTop15" style="clear:right">
                <div class="floatL lineH50px">支付方式:</div>
                <%--<div class="payTypeDiv marginLR20" ng-click="selectPay=3" ng-class="selectPay==3?'borderPink':''"--%>
                     <%--style="line-height: 45px;position: relative">--%>
                    <%--<img style="position: absolute;left: 20px;top: 7px" src="/yzxfMall_page/img/yue.png" alt="">--%>
                    <%--<span style="font-size: 21px;position: absolute;right: 20px">余额</span>--%>
                <%--</div>--%>
                <%--<div class="payTypeDiv marginLR20" ng-click="selectPay=4" ng-class="selectPay==4?'borderPink':''"><img--%>
                        <%--src="/yzxfMall_page/img/zhifubao.png" alt=""></div>--%>
                <%--<div class="payTypeDiv marginLR20" ng-click="selectPay=10" ng-class="selectPay==10?'borderPink':''"><img--%>
                        <%--src="/yzxfMall_page/img/weixin.png" alt=""></div>--%>
                <div class="payTypeDiv marginLR20" ng-click="selectPay=18" ng-class="selectPay==18?'borderPink':''"><img
                        src="/yzxfMall_page/img/gebank_logo.png" alt=""></div>
                <button style="width: 222px" class="topUpBtn floatR"
                        ng-click="getPwdWin()" ng-disabled="submitCheck">提交订单
                </button>
            </div>
        </div>

        <%--底部模板--%>
        <div ng-include="mallBottom"></div>
    </div>
    <div class="winCon flex1" ng-show="menuCheck">
        <div class="enterPsw">
            <div class="textCenter lineH50px font20px">余额支付</div>
            <div class="textCenter textSize30 lineH40px" ng-bind="'¥ '+totalPrice"></div>
            <div class="enterInput">
                <input maxlength="1" type="password" ng-model="pwd1"/>
                <input maxlength="1" type="password" ng-model="pwd2"/>
                <input maxlength="1" type="password" ng-model="pwd3"/>
                <input maxlength="1" type="password" ng-model="pwd4"/>
                <input maxlength="1" type="password" ng-model="pwd5"/>
                <input maxlength="1" type="password" ng-model="pwd6"/>
            </div>
            <div class="enterInputDes">请输入6位支付密码</div>
            <div class="selectDiv">
                <div style="border-left: 0;border-bottom: 0" ng-click="menuCheck=false;defaultBlur()">取消</div>
                <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="createOrder()">确认</div>
            </div>
        </div>
    </div>

    <div class="winCon flex1" ng-if="isSuccessWin">
        <div class="enterPsw">
            <div class="textCenter lineH50px font20px">已提交订单</div>
            <div style="padding: 35px 0;">
                <div class="iconfont iconStatus1" ng-class="statusIcon"></div>
                <div class="btn4" ng-bind="statusText"></div>
            </div>
            <div class="closeText1" ng-click="closeSuccessWin()">关闭</div>
        </div>
    </div>

    <div class="winCon flex1" ng-show="isQrcode">
        <div class="win-qrcode flex4">
            <div class="iconfont icon-close btn-close1" ng-click="closeQrcode()"></div>
            <%--<img ng-show="selectPay==4" src="/yzxfMall_page/img/zhifubao.png"/>--%>
            <%--<img ng-show="selectPay==10" src="/yzxfMall_page/img/weixin.png"/>--%>
            <img ng-show="selectPay==18" src="/yzxfMall_page/img/gebank_logo.png"/>
            <div class="qrcodeImg"></div>
        </div>
    </div>
    <div id="payForm"></div>
</div>