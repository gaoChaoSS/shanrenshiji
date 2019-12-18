<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_cart_Ctrl" class="d_content form_section title_section" style="margin: 55px auto 0;">
    <div class="overflowPC">
        <div class="title titleRed titleFixed">
            购物车
            <span class="titleManage whitefff" ng-bind="isEdit?'完成':'编辑'" ng-click="initCart()"></span>
        </div>
        <div class="isNullBox" ng-show="cartList==null || cartList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="bgWhite overflowHidden mainRowTop10" ng-repeat="cart in cartList track by $index">
            <div class="product-top1">
                <div class="btn-circle1 floatLeft iconfont" style="margin: 10px 10px 0 5px;" ng-click="selectSeller($index,!cart.isSellerSelected)"
                     ng-class="cart.isSellerSelected?'btn-circle1-select icon-gou':''"></div>
                <span ng-bind="cart.sellerName" class="textSize16 gray666"
                      ng-click="goPage('/store/storeInfo/sellerId/'+cart.sellerId)"></span>
            </div>
            <div class="positionRelative" ng-repeat="product in cart.product track by $index">
                <div class="mod-select1">
                    <div class="btn-circle1 iconfont" ng-click="selectCart($parent.$index,$index)"
                         ng-class="product.isSelected?'btn-circle1-select icon-gou':''"></div>
                </div>
                <img err-src="/yzxfMember_page/img/notImg02.jpg" class="img-product1"
                     ng-click="goPage('/store/commodity/goodsId/'+product.productId+'/_id/'+product.sellerId)"
                     ng-src="{{iconImgUrl(product.productIcon)}}"/>
                <div style="padding:12px 8px 8px 120px;min-height: 95px;">
                    <div class="textSize16" ng-bind="product.productName"></div>
                    <div>
                        <span class="gray888 mainRowRight10" ng-repeat="spec in product.spec" ng-bind="spec.name + ':' + spec.items"></span>
                    </div>
                    <div class="orangeRed3" ng-bind="'¥ '+getMoney(product.price)"></div>
                </div>
                <div class="commodityMenuRight positionAbsolute" style="bottom:0;right:0">
                    <div class="commodityBtn" ng-click="setPayNum($parent.$index,$index,-1)">-</div>
                    <div class="resultsNum" ng-bind="product.count">1</div>
                    <div class="commodityBtn" ng-click="setPayNum($parent.$index,$index,1)">+</div>
                </div>
            </div>
        </div>
        <div class="mod-bottom1">
            <div class="bottom1-select" ng-click="selectAll()">
                <div class="btn-circle1 floatLeft iconfont" ng-class="isSelectedAll?'btn-circle1-select icon-gou':''"></div>
                <span>全选</span>
            </div>
            <div class="bottom1-money" ng-show="!isEdit">
                <div class="textSize18 gray333" ng-bind="'合计:¥'+getMoney(totalPrice)"></div>
                <%--<div class="textSize12 gray888"--%>
                     <%--ng-bind="'总额:¥'+getMoney(totalPrice-couponTotalPrice)+' 立减:¥'+getMoney(couponTotalPrice)"></div>--%>
            </div>
            <div class="bottom1-btn" ng-show="!isEdit" ng-click="payOrder()">去结算</div>
            <div class="button fr" style="margin:8px" ng-show="isEdit" ng-click="checkDelCart()">删除</div>
        </div>
    </div>
    <div class="hideMenu" ng-show="isDel">
        <div class="errorMain">
            <div class="errorMainRow">是否删除已选择的商品</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="isDel=false">取消</div>
                <div class="errorMainBtn black" ng-click="delCart()">确认</div>
            </div>
        </div>
    </div>
</div>