<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="seller_commodityInfo_Ctrl" id="commodityInfo_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <div class="commodityNav">
        <span>美食</span>><span ng-bind="goodsInfo.name"></span>
    </div>
    <%--中部内容--%>
    <div class="bodyWidth marginZAuto">
        <%--商品信息--%>
        <div class="commodityInfoDiv">
            <%--商品图片--%>
            <div class="commodityInfoImg floatL">
                <%--上面大图--%>
                <div class="fangdaImg">
                    <img ng-src="{{iconImgUrl(showImg)}}" alt="">
                </div>
                <%--缩略图--%>
                <div class="floatL suolueImg" ng-repeat="col in thumbnail" ng-mouseenter="fangdaImg(col.icon)" ng-mouseleave="leaveImg()">
                    <img ng-src="{{iconImgUrl(col.icon)}}" alt="">
                </div>
            </div>
            <%--商品规格颜色--%>
            <div class="commodityInfoTxt floatL">
                <div>
                    <%--商品名--%>
                    <div ng-bind="goodsInfo.name"></div>
                    <%--小标题--%>
                    <div ng-bind="goodsInfo.tag"></div>
                    <%--金额--%>
                    <div>
                        <span style="color: #ff0e0c;font-size: 50px;margin-right: 30px" ng-bind="'¥'+goodsInfo.salePrice"></span>
                        <del style="color: #888;font-size:20px" ng-bind="'¥'+goodsInfo.oldPrice"></del>
                    </div>
                    <%--配送--%>
                    <div>
                        <div class="floatL">配送至 : </div>
                        <div class="floatL" ng-bind="showArea.name"></div>
                        <div class="floatL" style="font-size: 12px;padding: 2px 4px;background:#138bbe">免运费</div>
                    </div>
                    <%--促销--%>
                    <%--<div>--%>
                        <%--<div class="floatL">促销:</div>--%>
                        <%--<div class="floatL">满促</div>--%>
                        <%--<div class="floatL">满99元即送99元代金卷,限赠一次,数量有限,先到先得</div>--%>
                    <%--</div>--%>
                </div>
                <%--中部带虚线--%>
                <div>
                    <div class="floatL">
                        <span>销量:</span>
                        <span ng-bind="isNullZero(goodsInfo.saleCount)"></span>
                    </div>
                    <%--<div class="floatL">--%>
                        <%--<span>赠送养老金:</span>--%>
                        <%--<span>¥5</span>--%>
                    <%--</div>--%>
                </div>
                <%--底部规格颜色数量--%>
                <div>
                    <div class="btn-spec1" ng-repeat="specList in goodsInfo.spec">
                        <div class="floatL" ng-bind="specList.name+': '"></div>
                        <div class="floatL commodityBtnListBtn itemNormal"
                             ng-repeat="itemsList in specList.items"
                             ng-class="selectItems[$parent.$index].items==itemsList?'selected':''"
                             ng-bind="itemsList"
                             ng-click="setSelectedItems($parent.$index,$index)">
                        </div>
                    </div>

                    <%--数量--%>
                    <div class="btn-spec1">
                        <div class="floatL">数量: </div>
                        <div class="floatL btn-count1">
                            <div class="floatL commodityBtn" ng-click="commodityBtn(-1)">-</div>
                            <div class="floatL resultsNum" ng-bind="resultsNum">1</div>
                            <div class="floatL commodityBtn" ng-click="commodityBtn(1)">+</div>
                        </div>
                    </div>
                    <button class="topUpBtn" style="margin-top: 20px;margin-left: 45px;border-radius: 5px;" ng-click="saveCart()">加入购物车</button>
                    <div style="margin-top: 20px;">
                        <span class="iconfont colorRed3 pointer" ng-class="isCollection?'icon-tubiao2223':'icon-xihuan'"
                              ng-click="addOrDelGoodsCollection()" style="margin-left: 45px;margin-right: 10px;"></span>
                        <span class="pointer" style="margin-right: 30px" ng-click="addOrDelGoodsCollection()">收藏</span>
                        <span class="iconfont icon-wxbmingxingdianpu pointer" style="margin-right: 10px;color: #FA548E"
                              ng-click="goPage('/seller/sellerInfo/sellerId/'+goodsInfo.sellerId)"></span>
                        <span class="pointer" ng-click="goPage('/seller/sellerInfo/sellerId/'+goodsInfo.sellerId)">进入店铺</span>
                    </div>
                </div>
            </div>
        </div>
        <%--详情/评论--%>
        <div class="sellerTitle" style="background-color: #F6F6F6;height: 60px;margin:20px auto;">
            <div class="floatL flex1" ng-click="infoAsComment='info'" ng-class="infoAsComment=='info'?'clickDiv':''">商品参数</div>
            <%--<div class="floatL flex1" ng-click="infoAsComment='comment';getGoodsComment();" ng-class="infoAsComment=='comment'?'clickDiv':''">所有评价</div>--%>
        </div>

        <%--商品详情--%>
        <div class="commodityParameter" ng-show="infoAsComment=='info'">
            <%--&lt;%&ndash;参数信息&ndash;%&gt;--%>
            <%--<div class="commodityParameterTxt">--%>
                <%--<div style="margin-left: 3%;margin-top: 20px;margin-bottom: 20px;color: #b3b3b3;">产品参数:</div>--%>
                <%--<div>--%>
                    <%--<div class="floatL" ng-repeat="pl in parameterList"><span ng-bind="pl.name"></span><span ng-bind="pl.content"></span></div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--商品大图--%>
            <div>
                <div ng-repeat="cdl in imgList" style="width: 100%;margin-top: -2px;"><img style="width: 100%" src="{{iconImg(cdl.icon)}}" alt=""></div>
            </div>
        </div>
        <div class="iconfont icon-meiyouneirong" ng-show="imgList==null || imgList.length==0"
             style="text-align:center;height:500px;line-height:500px;color:#ccc;font-size:200px">

        </div>
        <%--<div class="commentListDiv" ng-show="infoAsComment=='comment'">--%>
            <%--<div class="isNullBox" ng-show="goodsComment==null || goodsComment==''">--%>
                <%--<div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>--%>
                <%--没有内容--%>
            <%--</div>--%>
            <%--<div class="commentList" ng-repeat="com in goodsComment">--%>
                <%--<div class="floatL">--%>
                    <%--<img src="{{iconImgUrl(com.icon)}}">--%>
                    <%--<div ng-bind="com.mobile"></div>--%>
                <%--</div>--%>
                <%--<div class="floatL">--%>
                    <%--<div ng-bind="showDate(com.createTime)"></div>--%>
                    <%--<div ng-bind="com.name"></div>--%>
                <%--</div>--%>
                <%--<div class="floatL">--%>
                    <%--<div>--%>
                        <%--<span>总体评价</span>--%>
                        <%--<span ng-bind="starNo(com.serviceStar)"></span>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>
    <%--底部模板--%>
    <div class="marginTop30" ng-include="mallBottom"></div>
</div>