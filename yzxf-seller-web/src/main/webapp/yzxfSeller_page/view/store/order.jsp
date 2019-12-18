<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_order_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="overflowPC" style="position: relative;">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack black" ng-click="goPage('/store/store')"></span>
            <span class="textSize18" ng-bind="selectedHead.name"></span>
            <%--<div ng-click="setPageCheck()" class="titleManage gray888"--%>
                 <%--ng-bind="pageCheck=='drawback'?'在线订单':'退款售后'"></div>--%>
        </div>
        <%--<div class="order-header">--%>
            <%--<div ng-repeat="head in headerList" ng-click="getOrderStatusList(head._id)">--%>
                <%--<div class="{{head.icon}} iconfont"--%>
                     <%--ng-class="selectedHead==head._id?'colorBlue':'gray444'"></div>--%>
                <%--<div class="topMenu-tap" ng-bind="head.count" ng-show="head.count!=null"></div>--%>
                <%--<div ng-class="selectedHead==head._id?'colorBlue':'gray444'" ng-bind="head.name"></div>--%>
            <%--</div>--%>
        <%--</div>--%>

        <div class="gray888 mainRowLeft10 lineHeight30">
            共查询到
            <span ng-bind="isNullZero(totalNumer)" class="dodgerBlue"></span> 条
        </div>

        <div class="order-model1" ng-repeat="order in orderList">
            <div class="order-title1">
                <div class="fl mainRowLeft10" ng-bind="order.memberName"></div>
                <div class="fr mainRowRight10 deepRed" ng-bind="getOrderStatus(order,$index)"></div>
            </div>
            <div class="order-info1" ng-repeat="product in order.productItems" style="min-height:70px;">
                <img src="{{iconImgUrl(product.icon)}}" style="width:50px">
                <div class="order-text1" style="margin-left:70px;">
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
            <div style="overflow: hidden;padding: 0 10px;">
                <div class="fl deepRed" ng-bind="'¥ '+getMoney(order.totalPrice)+'元'"></div>
                <div class="fr" ng-bind="showYFullTime(order.showStatusTime)"></div>
            </div>
            <div class="order-menu">
                <button class="bgWhite colorBlue borderBlue" ng-show="order.orderStatus==2" ng-click="goPage('/store/orderInfo/orderId/'+order._id+'/showModIndex/0')">发货</button>
                <button class="bgBlue whitefff" ng-show="order.orderStatus==6" ng-click="goPage('/store/orderInfo/orderId/'+order._id+'/showModIndex/4')">同意退货</button>
                <button class="bgWhite colorBlue borderBlue" ng-show="order.orderStatus==6" ng-click="goPage('/store/orderInfo/orderId/'+order._id+'/showModIndex/3')">拒绝退货</button>
                <button class="bgWhite colorBlue borderBlue"  ng-show="order.orderStatus==8" ng-click="setShowDraw(order._id)">确认收货</button>
                <button ng-click="goPage('/store/orderInfo/orderId/'+order._id)">详情</button>
            </div>
        </div>

        <div class="loadMore">
            <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
    <div class="hideMenu" ng-show="isShowDraw">
        <div class="errorMain">
            <div class="errorMainRow">是否确认收货</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="setShowDraw()">取消</div>
                <div class="errorMainBtn black" ng-click="submitDrawback()">确认</div>
            </div>
        </div>
    </div>
</div>