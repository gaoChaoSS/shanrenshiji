<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="order_orderConfirmation_Ctrl" class="d_content commentPanel form_section title_section order_panel">
    <div class="overflowPC">
        <%--头部标题--%>
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            订单确认
            <span class="titleManage notHigh"></span>
        </div>
        <%--收货地址--%>
        <div class="sectionMain" style="position: relative;" ng-click="getAddressList()">
            <%--<div class="addressModifyBtn colorBlue icon-dizhi iconfont"></div>--%>
            <div class="floatLeft mainRowLeft10" style="width:85%;min-height: 50px;">
                <div class="lineHeight25 gray666" ng-bind="memberAddress.area+memberAddress.address"></div>
                <div class="lineHeight25">
                    <span class="gray888" ng-bind="memberAddress.name"></span>
                    <span class="gray888" ng-bind="memberAddress.gender"></span>
                    <span class="gray888" ng-bind="memberAddress.phone"></span>
                </div>
            </div>
            <div class="btn5 grayaaa icon-right-1-copy iconfont"></div>
        </div>
        <%--商品缩略图.品名.价格--%>
        <div class="sectionMain mainRowBottom10 mainRowLeft10" ng-repeat="cart in cartList track by $index">
            <div class="overflowH" style="padding:0 10px">
                <div class="fl lineHeight30" ng-bind="cart.sellerName"></div>
                <div class="fr lineHeight30" ng-click="showCouponPage($index)">
                    <span class="iconfont icon-youhuiquan textSize20 colorRed1"></span>
                    <span class="gray888" ng-bind="selectedCoupon[$index].name"></span>
                </div>
            </div>
            <div class="commodityInfo" ng-repeat="product in cart.product track by $index">
                <div class="commodityImg">
                    <div>
                        <img ng-src="{{iconImgUrl(product.productIcon)}}" err-src="/yzxfMember_page/img/notImg02.jpg">
                    </div>
                </div>
                <div class="commodityText">
                    <div style="margin-bottom:20px">
                        <div ng-bind="product.productName" style="color: #666;font-weight: 900;font-size: 16px;line-height:16px"></div>
                        <div>
                            <span class="grayaaa" ng-repeat="spec in product.spec" ng-bind="spec.name+':'+spec.items+' '"></span>
                        </div>
                    </div>
                    <div>
                        <span class="orangeRed">¥ <span ng-bind="product.unitPrice" class="orangeRed"></span></span>
                        <span class="gray888">× <span ng-bind="product.count" class="gray888"></span></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle">卡券折扣</div>
                <div class="mainRowRight">
                    <span class="errorRed">  ¥</span>
                    <span class="errorRed" ng-bind="totalCouponPrice"></span>
                </div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">获赠养老金</div>
                <div class="mainRowRight errorRed">
                    <span class="errorRed">  ¥</span>
                    <span class="errorRed" ng-bind="totalPension">  </span>
                </div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">总计</div>
                <div class="mainRowRight">
                    <span class="errorRed">  ¥</span>
                    <span class="errorRed textSize25" ng-bind="totalPrice"></span>
                </div>
            </div>
        </div>

        <div class="sectionMain" ng-show="isShowRemark">
            <div class="mainRow">
                <div class="mainRow rowHeightAuto">
                    <textarea placeholder="买家留言(50字以内)" class="rowTextarea" ng-model="memberRemark" style="height: 80px;" maxlength="50"></textarea>
                </div>
            </div>
        </div>

        <div class="sectionMain">
            <div class="mainRow" ng-click="selectPay=3">
                <div class="rowTitle rowTitleBtn notHigh">
                    <span class="icon-cshy-rmb2 iconfont textSize22 deepRed"></span>
                    消费积分兑换
                </div>
                <div class="iconfont mainRowRight limeGreen" ng-class="{3:'icon-103'}[selectPay]"></div>
            </div>

            <%--<div class="mainRow" ng-click="selectPay=10" ng-show="isWechat">--%>
                <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                    <%--<span class="icon-iconfontweixin iconfont textSize18 limeGreen"></span>--%>
                    <%--微信支付--%>
                <%--</div>--%>
                <%--<div class="iconfont mainRowRight limeGreen" ng-class="{10:'icon-103'}[selectPay]"></div>--%>
            <%--</div>--%>

            <%--<div class="mainRow" ng-click="selectPay=4" ng-show="!isWechat">--%>
                <%--<div class="rowTitle rowTitleBtn notHigh">--%>
                    <%--<span class="icon-zhifubao iconfont textSize18 dodgerBlue"></span>--%>
                    <%--支付宝支付--%>
                <%--</div>--%>
                <%--<div class="iconfont mainRowRight limeGreen" ng-class="{4:'icon-103'}[selectPay]"></div>--%>
            <%--</div>--%>

            <div class="mainRow" ng-click="selectPay=18">
                <div class="rowTitle rowTitleBtn notHigh"><img
                        src="/yzxfMall_page/img/gebank_logo.png" alt="">
                    贵商行支付
                </div>
                <div class="iconfont mainRowRight limeGreen" ng-class="{18:'icon-103'}[selectPay]"></div>
            </div>
        </div>

        <div class="hideMenu" ng-show="menuCheck">
            <div class="enterPsw">
                <div class="textCenter lineHeight50">消费积分兑换</div>
                <div class="textCenter textSize30 lineHeight40" ng-bind="'¥ '+totalPrice"></div>
                <div class="enterInput">
                    <input maxlength="1" type="tel" ng-model="pwd1"/>
                    <input maxlength="1" type="tel" ng-model="pwd2"/>
                    <input maxlength="1" type="tel" ng-model="pwd3"/>
                    <input maxlength="1" type="tel" ng-model="pwd4"/>
                    <input maxlength="1" type="tel" ng-model="pwd5"/>
                    <input maxlength="1" type="tel" ng-model="pwd6"/>
                </div>
                <div class="enterInputDes">请输入6位支付密码</div>
                <div class="selectDiv">
                    <div style="border-left: 0;border-bottom: 0" ng-click="menuCheck=false">取消</div>
                    <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="createOrder()" ng-disabled="submitCheck">确认</div>
                </div>
            </div>
        </div>

        <div class="hideMenu" ng-show="isSuccess">
            <div class="errorMain otherMain">
                <div class="otherMainRow icon-zhengque1 iconfont textSize50 limeGreen"></div>
                <div class="otherMainRow textSize25">已提交订单</div>
                <div class="otherMainRow grayaaa textSize13" ng-bind="countTimeNum+' 秒后返回到我的主页'"></div>
            </div>
        </div>

        <%--底部按钮--%>
        <div class="fixedBottom rowBorderTop">
            <div class="fixedBottomBtn textSize18 bgBblue2" ng-click="getPwdWin()" ng-disabled="submitCheck">提交订单</div>
        </div>
    </div>

    <%--卡券--%>
    <div class="hideMenu" ng-show="isShowCoupon">
        <div class="winCon-full">
            <div class="title">
                <span class="icon-left-1 iconfont titleBack" ng-click="isShowCoupon=false"></span>
                <span class="textSize18">选择卡券</span>
                <span class="titleManage notHigh" ng-click="setCoupon(selectedSeller.index,'',true)">不选择卡券</span>
            </div>
            <div class="couponList" ng-repeat="coupon in showCoupon track by $index" ng-click="setCoupon(selectedSeller.index,coupon,true)">
                <div class="couponBk">
                    <div class="couponImg floatLeft">
                        <div class="circularPhoto borderRed">
                            <img err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(coupon.sellerIcon)}}"/>
                        </div>
                    </div>
                    <div class="couponInfo floatLeft">
                        <div class="textEllipsis " style="font-size:16px;" ng-bind="coupon.name"></div>
                        <div class="textEllipsis gray888" ng-bind="'商家:'+selectedSeller.name"></div>
                        <div class="couponTime notHigh couponInfoLast" ng-bind="'有效期: '+showDate(coupon.startTime)+' 至 '+showDate(coupon.endTime)"></div>
                    </div>
                    <div class="couponSemiCircle floatLeft">
                        <div></div>
                        <div></div>
                    </div>
                    <div class="couponMoney floatLeft">
                        <div class="textSize18" ng-bind="'¥ '+ isNullZero(coupon.value)"></div>
                        <div class="textSize12" ng-bind="'满'+ isNullZero(coupon.condition) +'可用'"></div>
                    </div>
                    <div class="receivceWidth floatLeft receivceFalse">可使用</div>
                </div>
            </div>
        </div>
    </div>

    <div class="hideMenu" ng-show="isShowAddress">
        <div class="winCon-full">
            <div class="title">
                <span class="icon-left-1 iconfont titleBack" ng-click="isShowAddress=false"></span>
                选择收货地址
            </div>
            <div class="sectionMain3" ng-repeat="itemAddress in addressList track by $index" ng-click="selectedAddress(itemAddress)">
                <div class="mainRowLeft10" ng-class="check?'flex5':'flex1'">
                    <div class="lineHeight25 gray888" ng-bind="isNullText(itemAddress.area)+isNullText(itemAddress.address)"></div>
                    <div class="lineHeight25">
                        <span class="gray888" ng-bind="isNullText(itemAddress.name)+' '+isNullText(itemAddress.gender)+' '+isNullText(itemAddress.phone)"></span>
                    </div>
                    <div class="lineHeight25" ng-show="itemAddress.defaultAddress">
                        <span class="icon-103 iconfont mainRowRight20" ng-class="itemAddress.defaultAddress?'limeGreen':''"></span>
                        <span class="gray666" ng-bind="itemAddress.defaultAddress?'默认地址':''"></span>
                    </div>
                </div>
            </div>
            <div class="hideCommodityBtn bgWhite rowBorderTop" ng-click="goPage('/my/addAddress')">
                <span class="icon-llalbumshopselectorcreate iconfont limeGreen textSize18"></span>
                <span class="notHigh">新增收货地址</span>
            </div>
        </div>
    </div>

    <div id="payForm">

    </div>
</div>
