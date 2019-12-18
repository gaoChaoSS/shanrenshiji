<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_order_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto" style="overflow: hidden">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" style="height: 382px;" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myOrderRightContent" style="position:relative;margin-top: 70px;border-top: 3px solid #ccc;">
            <div class="mod-title1">
                <div ng-class="pageCheck!='drawback'?'mod-title1-select':''" ng-click="setPageCheck('')">我的订单</div>
                <div ng-class="pageCheck=='drawback'?'mod-title1-select':''" ng-click="setPageCheck('drawback')">退款售后</div>
            </div>
            <div class="orderTitle">
                <div class="floatL flex1" ng-repeat="header in headerList" ng-click="getOrderStatusList(header._id)"
                     ng-class="selectedHead==header._id?'clickBtn':''" ng-bind="header.name"></div>
            </div>
            <%--<div class="isNullBox" ng-show="queryMyOrder==null || queryMyOrder==''">--%>
                <%--<div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>--%>
                <%--没有内容--%>
            <%--</div>--%>
            <%--<table class="orderContent"  ng-show="queryMyOrder!=null && queryMyOrder!=''">--%>
                <%--<tr class="orderTableTitle">--%>
                    <%--<td class="" style="width:35%;text-indent:2em;">订单信息</td>--%>
                    <%--<td class="" style="width:10%;text-align: center">状态</td>--%>
                    <%--<td class="" style="width:10%;text-align: center">总计</td>--%>
                    <%--<td class="" style="width:15%;text-align: center">获得养老金</td>--%>
                    <%--<td class="" style="width:15%;text-align: center">详情</td>--%>
                    <%--<td class="" style="width:15%;text-align: center">操作</td>--%>
                <%--</tr>--%>
                <%--<tr class="orderTableList"  ng-repeat="item in queryMyOrder">--%>
                    <%--<td class="RePosition" style="width:35%;">--%>
                        <%--<img ng-show="item.orderType==11" class="AbPosition orderListImg" ng-click="goPage('/seller/commodityInfo/goodsId/'+item.productItems[0].productId)" ng-src="{{iconImgUrl(item.productItems[0].icon)}}" alt="">--%>
                        <%--<img ng-show="item.orderType==1||item.orderType==0" class="AbPosition orderListImg" ng-src="{{iconImgUrl(item.sellerIcon)}}" alt="">--%>
                        <%--<div ng-show="item.orderType==11" class="AbPosition orderGoodsName" ng-bind="item.productItems[0].name"></div>--%>
                        <%--<div ng-show="item.orderType==1||item.orderType==0" class="AbPosition orderGoodsName" ng-bind="item.sellerName"></div>--%>
                        <%--<div class="AbPosition orderGoodsSeller" ng-click="goPage('/seller/sellerInfo/sellerId/'+item.sellerId)">商家信息</div>--%>
                    <%--</td>--%>
                    <%--<td style="font-size:14px;width:10%;text-align: center" ng-bind="orderStatus(item.orderStatus)"></td>--%>
                    <%--<td style="font-size:14px;width:10%;text-align: center;color: #ff0e0c;" ng-bind="'¥'+getMoney(item.totalPrice)"></td>--%>
                    <%--<td style="font-size:14px;width:15%;text-align: center;color: #ff0e0c;" ng-bind="'¥'+getMoney(item.pensionMoney)"></td>--%>
                    <%--<td ng-show="item.orderType==11" style="font-size:14px;width:15%;text-align: center;color: #ff0e0c;"><span class="pointer" ng-click="goPage('/my/orderInfo/orderId/'+item._id)">订单详情</span></td>--%>
                    <%--<td ng-show="item.orderType==1||item.orderType==0" style="font-size:14px;width:15%;text-align: center;color: #ff0e0c;">线下交易</td>--%>
                    <%--<td style="width:15%;text-align: center;">--%>
                        <%--<div style="margin-bottom: 20px;font-size: 15px;cursor:pointer" ng-click="delOrder(item._id)" ng-show="item.orderType==11 && item.orderStatus==1">--%>
                            <%--<img style="margin-right: 10px" src="/yzxfMall_page/img/delete.png" alt="">删除订单--%>
                        <%--</div>--%>
                        <%--<div style="font-size: 15px;cursor:pointer" ng-click="goComment(item)" ng-show="((item.orderStatus>=5 && item.orderStatus<=9) || item.orderStatus==100)&&!item.isComment"><img style="margin-right: 10px" src="/yzxfMall_page/img/goComment.png" alt="">去评价</div>--%>
                        <%--<div><button class="sureTake" ng-show="item.orderType==11 && item.orderStatus==4" ng-click="submitPass(item._id)" >确认收货</button></div>--%>
                    <%--</td>--%>
                <%--</tr>--%>
            <%--</table>--%>
            <table class="table-pay table-default marginTop30">
                <tr class="table-head">
                    <td style="width:10%">商品图片</td>
                    <td style="width:30%">商品名称</td>
                    <td style="width:40%">规格</td>
                    <td style="width:10%">数量</td>
                    <td style="width:10%">价格</td>
                </tr>
            </table>
            <table class="table-pay" ng-repeat="order in queryMyOrder track by $index" style="width:calc(100% - 20px);margin:0 auto 20px">
                <tr>
                    <td colspan="2">
                        <span ng-bind="showYFullTime(order.showStatusTime)" style="font: 16px tahoma;"></span>
                        <%--<span class="colorGray999" ng-bind="'订单编号:'+order.orderNo"></span>--%>
                        <span class="colorRed2 marginLeft15" ng-bind="getOrderStatus(order,$index)"></span>
                    </td>
                    <td>
                        <span class="padding2 colorGray666 pointer colorHover1" ng-bind="order.sellerName"
                              ng-click="goPage('/seller/sellerInfo/sellerId/'+order.sellerId)"></span>
                    </td>
                    <td class="padding2" colspan="2">
                        <div class="floatR" ng-show="order.couponName!=null && order.couponName!=''">
                            <span class="iconfont icon-youhuiquan font18px colorRed1"></span>
                            <span ng-bind="order.couponName"></span>
                        </div>
                    </td>
                </tr>
                <tr class="table-pay-item" ng-show="order.orderType==1 || order.orderType==0">
                    <td style="width:10%" class="pointer">
                        <img src="{{iconImgUrl(getSellerIcon(order.sellerIcon,order.sellerDoorImg))}}"
                             ng-click="goPage('/seller/sellerInfo/sellerId/'+order.sellerId)">
                    </td>
                    <td style="width:30%">线下实体店消费</td>
                    <td style="width:40%" ng-bind="order.orderType=='0'?'会员扫码':'现金交易'"></td>
                    <td style="width:10%"></td>
                    <td style="width:10%"></td>
                </tr>
                <tr class="table-pay-item" ng-repeat="product in order.productItems track by $index" ng-show="order.orderType==11">
                    <td style="width:10%" class="pointer">
                        <img src="{{iconImgUrl(product.icon)}}"
                             ng-click="goPage('/seller/commodityInfo/goodsId/'+product.productId)">
                    </td>
                    <td style="width:30%" ng-bind="product.name"></td>
                    <td style="width:40%">
                        <span ng-repeat="spec in product.selectSpec track by $index"
                              ng-bind="spec.name+' : '+spec.items+' '"></span>
                    </td>
                    <td style="width:10%" ng-bind="product.count"></td>
                    <td style="width:10%" class="colorRed3" ng-bind="getMoney(product.price)"></td>
                </tr>
                <tr class="textRight table-pay-bottom">
                    <td colspan="5">
                        <button class="table-pay-btn1" ng-show="order.orderType==11" ng-click="queryInfo(order)">订单详情</button>
                        <button class="table-pay-btn1" ng-show="order.orderType==11 && order.orderStatus==1" ng-click="delOrder(order)" >删除订单</button>
                        <button class="table-pay-btn1" ng-show="order.orderType!=11 && order.orderStatus==1" ng-click="delOrderByOffline(order)" >删除订单</button>
                        <button class="table-pay-btn1" ng-show="((order.orderStatus>=5 && order.orderStatus<=9) || order.orderStatus==100)&&!order.isComment"
                                ng-click="goComment(order)" >去评价</button>
                        <button class="table-pay-btn1" ng-show="order.orderType==11 && order.orderStatus==4" ng-click="submitPass(order._id)" >确认收货</button>
                        <button class="table-pay-btn1" ng-show="order.orderType==11 && order.orderStatus==5" ng-click="setShowMod(0,order)">申请退款</button>
                        <button class="table-pay-btn1" ng-show="order.orderType==11 && order.orderStatus==7" ng-click="setShowMod(1,order)">填写发货信息</button>

                        <span ng-show="order.couponPrice!=null && order.couponPrice!='' && order.couponPrice!=0">
                            卡券折扣 <span class="colorRed3" ng-bind="'¥ '+getMoney(order.couponPrice)"></span>
                        </span>
                        <span>
                            养老金 <span class="colorRed3" ng-bind="'¥ '+getMoney(order.pensionMoney)"></span>
                        </span>
                        <span>
                            总计 <span class="colorRed3 marginRight20" ng-bind="'¥ '+getMoney(order.totalPrice)"></span>
                        </span>
                    </td>
                </tr>
            </table>
        </div>
            <form class="pageMain">
                <div class="sectionPage" ng-show="dataPage.totalNum>0">
                    <div style="margin: 0 auto;">
                        <div class="btn3" ng-click="pageNextO(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumberO(1)"  ng-show="dataPage.pageNo>4"></div>
                        <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                        <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                             ng-click="pageNumberO(i)"
                             ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                        <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                        <div class="btn3" ng-bind="dataPage.totalPage"
                             ng-click="setPageNo0(dataPage.totalPage);"
                             ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                        <div class="btn3" ng-click="pageNextO(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                            下一页
                        </div>
                        <div class="pageGo"
                             ng-show="dataPage.totalPage>1">
                            <input type="text" placeholder="跳转" ng-model="pageGo">
                            <button class="iconfont icon-right-1-copy" ng-click="pageGoFunO(pageGo)"></button>
                        </div>
                    </div>
                </div>
            </form>
    </div>
    <%--订单评价--%>
    <div class="hideMenu overflowHidden" ng-show="commentBox" style="display: flex;align-items: center;justify-content: center;">
        <!--评价-->
        <div style="width: 800px;height: 590px;margin: 0 auto;background-color: #fff;position: relative">
            <div style="position: absolute;right: -35px;top:-35px;color: #fff;font-size: 23px" class="iconfont icon-close" ng-click="commentBox=false"></div>
            <%--标题--%>
            <div style="width: 90%;margin:0 auto;text-align: center;height:50px;line-height: 50px;font-size: 20px;border-bottom: 1px solid #DFDFDF">评价</div>
            <%--内容--%>
            <div style="width: 90%;margin:25px auto;height: 120px;text-align:center;overflow: hidden;" ng-show="commentBoxInfo.orderType!=0&&commentBoxInfo.orderType!=1">
                <div class="floatL" style="width: 20%;line-height: 120px;color: #777">商品信息 : </div>
                <div class="floatL" style="width: 16%"><img style="width: 100%;height: 100%" src="{{iconImgUrl(commentBoxInfo.productItems[0].icon)}}" alt=""></div>
                <div class="floatL" style="width: 60%;line-height: 60px;text-align: left;padding-left: 25px">
                    <div style="" ng-bind="commentBoxInfo.productItems[0].name"></div>
                    <div style="color: red" ng-bind="'¥ '+commentBoxInfo.totalPrice"></div>
                </div>
            </div>
            <div style="width: 90%;margin:25px auto;height: 120px;text-align:center;overflow: hidden;" ng-show="commentBoxInfo.orderType==0||commentBoxInfo.orderType==1">
                <div class="floatL" style="width: 20%;line-height: 120px;color: #777">商家信息 : </div>
                <div class="floatL" style="width: 16%"><img style="width: 100%;height: 100%" src="{{iconImgUrl(commentBoxInfo.sellerIcon)}}" alt=""></div>
                <div class="floatL" style="width: 60%;line-height: 60px;text-align: left;padding-left: 25px">
                    <div style="" ng-bind="commentBoxInfo.sellerName"></div>
                    <div style="color: red"></div>
                </div>
            </div>
            <div style="width: 90%;margin:25px auto;height: 70px;line-height:70px;text-align:center;overflow: hidden;">
                <div class="floatL" style="width: 20%;line-height: 70px;color: #777" ng-show="commentBoxInfo.orderType!=0&&commentBoxInfo.orderType!=1">商品评分 : </div>
                <div class="floatL" style="width: 20%;line-height: 70px;color: #777" ng-show="commentBoxInfo.orderType==0||commentBoxInfo.orderType==1">商家评分 : </div>
                <div class="floatL" style="width: 80%;text-align: left">
                    <span class="pointer" style="margin-right: 10px;font-size: 22px" ng-repeat="itemStar in starList"
                    ng-click="startCheck(itemStar.id)" ng-class="starGrade>=itemStar.id?'loessYellow':'gray888'">★</span>
                </div>
            </div>
            <div style="width: 90%;margin:25px auto;text-align:center;height: 155px;overflow: hidden;">
                <div class="floatL" style="width: 20%;line-height: 30px;color: #777" ng-show="commentBoxInfo.orderType!=0&&commentBoxInfo.orderType!=1">商品评价 : </div>
                <div class="floatL" style="width: 20%;line-height: 30px;color: #777" ng-show="commentBoxInfo.orderType==0||commentBoxInfo.orderType==1">商家评价 : </div>
                <div class="floatL" style="width: 80%;text-align: left;">
                    <textarea ng-model="commentText" style="width: 90%;resize: none;height: 100px;padding:2%" name="" id="" cols="30" rows="10"></textarea>
                </div>
            </div>
            <%--提交按钮--%>
            <div style="width: 90%;margin:20px auto;text-align: right;line-height: 90px">
                <button style="width: 130px;border-radius: 7px;font-size: 17px" class="topUpBtn" ng-click="submitComment()">提交评价</button>
            </div>
            <%--提交结果--%>
            <div class="commentStatusBox bkColorBlue1" ng-show="isSuccess">
                提交成功!<span ng-bind="countTimeNum+' 秒后关闭'"></span>
            </div>
        </div>
    </div>

    <div class="hideMenu flex1" ng-show="showMod[0]">
        <div class="mod-win1">
            <div class="iconfont icon-close btn-close2" ng-click="setShowMod(0)"></div>
            <div class="win1-title1">申请理由</div>
            <textarea ng-model="returnDesc" placeholder="退货理由(255个字符以内,不支持emoji表情)"></textarea>
            <button ng-click="returnDescSubmit()">提交</button>
        </div>
    </div>

    <div class="hideMenu flex1" ng-show="showMod[1]">
        <div class="mod-win1">
            <div class="iconfont icon-close btn-close2" ng-click="setShowMod(1)"></div>
            <div class="win1-title1">填写退款发货信息</div>
            <input type="text" ng-model="drawOrder.returnExpress" placeholder="填写快递公司">
            <input type="text" ng-model="drawOrder.returnExpressNo" placeholder="填写快递单号">
            <button ng-click="sendDrawbackOrder()">提交</button>
        </div>
    </div>

    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>