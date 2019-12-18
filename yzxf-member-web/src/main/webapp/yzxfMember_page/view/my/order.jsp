<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_order_Ctrl" class="d_content form_section title_section order_panel">
    <div class="title titleRed">
        <span class="icon-left-1 iconfont titleBack titleRed" ng-click="goPage('/my/my')"></span>
        <span class="textSize18 whitefff" ng-bind="pageCheck=='drawback'?'退款售后管理':selectedHead.name"></span>
        <%--<div ng-click="setPageCheck()" class="titleManage whitefff"--%>
             <%--ng-bind="pageCheck=='drawback'?'我的订单':'退款售后'"></div>--%>
    </div>

    <%--顶部导航栏--%>
    <div class="topMenu" style="padding:0;" ng-show="pageCheck=='drawback'">
        <div ng-repeat="head in headerList" ng-click="getOrderStatusList(head._id)">
            <div class="{{head.icon}} iconfont"
                 ng-class="selectedHead._id==head._id?'colorBlue':'gray444'"></div>
            <div class="topMenu-tap" ng-bind="head.count" ng-show="head.count!=null"></div>
            <div ng-class="selectedHead._id==head._id?'colorBlue':'gray444'" ng-bind="head.name"></div>
        </div>
    </div>

    <%--商品订单信息--%>
    <div class="overflowPC">
        <div class="mainRowLeft10 gray888">
            <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span style="color:#268BBF;font-size: 15px;">{{isNullZero(totalNumer)}}</span>&nbsp;条</span>
        </div>
        <div class="isNullBox" ng-show="orderList==null || orderList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div style="background-color: #fff;overflow: hidden;margin-bottom: 12px;" ng-repeat="item in orderList">
            <div style="margin:4px 12px;border-bottom: 1px dashed #eee;">
                <span ng-bind="item.sellerName" class="colorBlue"
                      ng-click="goPage('/store/storeInfo/sellerId/'+item.sellerId)"></span>
                <span class="fr notHigh" ng-bind="getOrderStatus(item,$index)"></span>
            </div>
            <div style="position: relative" ng-show="item.orderType==11" ng-repeat="product in item.productItems">
                <div style="padding:8px;position: absolute;top:0;left:0;">
                    <img err-src="/yzxfMember_page/img/notImg02.jpg" style="width:50px;height: 50px;"
                         ng-click="goPage('/store/commodity/goodsId/'+product.productId+'/_id/'+item.sellerId)"
                         ng-src="{{iconImgUrl(product.icon)}}"/>
                </div>
                <div style="padding:5px 8px 8px 80px;min-height: 70px;">
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
            <div ng-show="item.orderType==11" class="overflowHidden" style="padding: 0 10px;">
                <div class="notHigh fl">
                    <span class="deepRed textSize16" ng-bind="'¥ '+getMoney(item.totalPrice)"></span>
                    <span class="textSize12" ng-show="item.orderStatus==100">
                        <span class="textSize12">(养老金:¥</span>
                        <span class="textSize12 deepRed" ng-bind="getMoney(item.pensionMoney)"></span>
                        <span class="textSize12">)</span>
                    </span>
                </div>
                <%--<div class="notHigh" ng-show="item.express!=null" ng-bind="item.express+':'+item.expressNo"></div>--%>
                <div class="notHigh fr" ng-bind="showYFullTime(item.showStatusTime)"></div>
            </div>
            <div style="position: relative" ng-show="item.orderType!=11">
                <div style="padding:8px;position: absolute;top:0px;left:0;">
                    <img err-src="/yzxfMember_page/img/notImg02.jpg" style="width:80px;height: 80px;"
                         ng-click="goPage('/store/storeInfo/sellerId/'+item.sellerId)"
                         ng-src="{{iconImgUrl(getSellerIcon(item.sellerIcon,item.sellerDoorImg))}}"/>
                </div>
                <div style="padding:12px 8px 8px 100px;min-height: 95px;">
                    <div class="notHigh">线下交易: <span ng-bind="item.totalPrice"></span> 元
                    </div>
                    <div class="notHigh">养老金:
                        <span style="color:orangered" ng-bind="getMoney(item.pensionMoney)"></span> 元
                    </div>
                    <div class="notHigh" ng-bind="'创建时间:'+showYFullTime(item.createTime)"></div>
                </div>
            </div>
            <div style="overflow:hidden;padding:4px 15px;">
                <button ng-bind="item.isComment?'已评价':'去评价'" class="button fl highL"
                        ng-disabled="item.isComment?'disabled':false"
                        ng-show="(item.orderStatus>=5 && item.orderStatus<=9) || item.orderStatus==100"
                        ng-click="isGoPage(item.isComment,item.orderNo,item._id,item.sellerId)"></button>

                <button class="button fr" ng-show="item.orderType==11 && item.orderStatus!=1" ng-click="queryInfo(item)" >详情</button>
                <button class="button fr" ng-show="item.orderType==11 && item.orderStatus==1" ng-click="queryInfo(item)" >去支付</button>
                <button class="button fr" ng-show="item.orderType==11 && item.orderStatus==4" ng-click="setPassId(item._id)" >确认收货</button>
                <button class="button fr" ng-show="item.orderType==11 && item.orderStatus==1" ng-click="delOrder(item)">删除</button>
                <button class="button fr" ng-show="item.orderType!=11 && item.orderStatus==1" ng-click="delOrderByOffline(item)">删除</button>
                <button class="button fr" ng-show="item.orderType==11 && item.orderStatus==5" ng-click="applyDrawback(item)">申请退款</button>
                <button class="button fr" ng-show="item.orderType==11 && item.orderStatus==7" ng-click="goPage('order/orderInfo/orderId/'+item._id+'/showModIndex/3')">填写发货信息</button>
            </div>
        </div>
        <div class="hideMenu" ng-show="isShowPass">
            <div class="errorMain">
                <div class="errorMainRow">是否确认收货</div>
                <div class="errorMainRow">
                    <div class="errorMainBtn black" ng-click="isShowPass=false">取消</div>
                    <div class="errorMainBtn black" ng-click="submitPass()">确认</div>
                </div>
            </div>
        </div>
        <div class="loadMore" ng-show="totalNumer>0">
            <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>