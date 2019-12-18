<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_commodity_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        <div class="titleTwo">
            <span class="titleText titleTextTwo" style="width: 40%">商品详情</span>
        </div>
    </div>
    <div class="overflowPC" style="padding-bottom: 100px;">
        <div ng-show="titleCheck==1">
            <div class="sectionMain">
                <div class="mainRow sectionMainImg">
                    <div class="iconfont icon-left-1 img-btn1" ng-click="setSelectIcon(-1)"
                         ng-show="thumbnail!=null && thumbnail.length>1 && selectedIcon.index!=thumbnail[0].index"></div>
                    <div class="img-animation1">
                        <img ng-repeat="tImg in thumbnail" ng-show="selectedIcon.index==tImg.index"
                             ng-src="{{iconImg(tImg.icon)}}" ng-class="getAnimation(tImg)"
                             err-src="/yzxfMember_page/img/notImg02.jpg"/>
                    </div>
                    <div class="iconfont icon-right-1-copy img-btn1" style="right:0" ng-click="setSelectIcon(1)"
                         ng-show="thumbnail!=null && thumbnail.length>1 && selectedIcon.index!=thumbnail[thumbnail.length-1].index"></div>
                </div>
                <div class="mainRow">
                    <div class="lineHeight50 textEllipsis" style="font-size: 17px;font-weight: bold;padding:0 8px;width: 90%;"
                         ng-bind="goodsInfo.name"></div>
                    <div class="mainRowLeft10">
                        <span class="titleTextMarginRight grayaaa">会员价</span>
                        <span class="errorRed titleTextMarginRight mainRowLeft10" style="font-size: 18px;"
                              ng-bind="' ¥ '+isNullZero(goodsInfo.salePrice)"></span>
                        <span class="errorRed" ng-bind="'('+(goodsInfo.te?'特价':'直供')+')'"></span>
                    </div>
                    <div class="mainRowLeft10">
                        <span class="titleTextMarginRight grayaaa">市场价</span>
                        <span class="grayaaa mainRowLeft10" ng-bind="' ¥ '+isNullZero(goodsInfo.oldPrice)"></span>
                        <span class="grayaaa fr" style="margin-right: 15px;"
                              ng-bind="'养老金: '+getPension(goodsInfo.salePrice,sellerInfo.integralRate)+'元'"></span>
                    </div>
                    <div class="mainRowLeft10" style="position:relative">
                        <span class="titleTextMarginRight grayaaa">运费</span>
                        <span class="grayaaa mainRowLeft10" ng-bind="' ¥ '+isNullZero(goodsInfo.freight)" ng-show="isNullZero(goodsInfo.freight)!=0"></span>
                        <span class="mainRowRight lineHeight20" ng-bind="'总销量: '+isNullZero(goodsInfo.saleCount)"></span>
                    </div>
                    <div class="storeInfoHeadRightIcon storeAbTopRight">
                        <div class="iconfont storeIconRight" style="position: absolute;top: -10px;right: 15px;"
                             ng-class="isCollection?'icon-tubiao2223 errorRed':'icon-xihuan grayccc'"
                             ng-click="addOrDelGoodsCollection()"></div>
                    </div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow" ng-click="hideMenuCheck=1">
                    <div class="rowTitle">请选择规格</div>
                    <div class="mainRowRight icon-gengduo iconfont"></div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">商品简介:</div>
                    <div class="rowInput" ng-bind="goodsInfo.desc"
                         style="line-height: 20px;padding:15px 0;width: calc(100% - 100px);margin-left: 100px;"></div>
                </div>
            </div>
            <%--<div class="sectionMain">--%>
                <%--<div class="mainRow" ng-click="errorFun=true">--%>
                <%--&lt;%&ndash;<div class="mainRow" ng-click="hideMenuCheck=2">&ndash;%&gt;--%>
                    <%--<div class="rowTitle">--%>
                        <%--满 <span class="errorRed" ng-bind="isNullZero(goodsInfo.pinkage)">  </span> 包邮 ·--%>
                        <%--<span ng-bind="isNullZero(goodsInfo.refund)==0?'不支持退货':goodsInfo.refund+'天退货'"></span>--%>
                    <%--</div>--%>
                    <%--<div class="mainRowRight icon-gengduo iconfont"></div>--%>
                <%--</div>--%>
                <%--<div class="mainRow" ng-click="errorFun=true">--%>
                <%--&lt;%&ndash;<div class="mainRow" ng-click="hideMenuCheck=3">&ndash;%&gt;--%>
                    <%--<div class="rowTitle rowTitleShort textEllipsis"--%>
                         <%--ng-bind="'描述: '+(goodsInfo.desc==null)?'暂无描述':goodsInfo.desc"></div>--%>
                    <%--<div class="mainRowRight icon-gengduo iconfont"></div>--%>
                <%--</div>--%>
            <%--</div>--%>

            <div class="mod-bottom1" style="bottom:0">
                <div class="mod-rowIcon">
                    <div ng-click="goPage('/store/storeInfo/sellerId/'+goodsInfo.sellerId)">
                        <div class="iconfont icon-wxbmingxingdianpu"></div>
                        <div>商家</div>
                    </div>
                    <div ng-click="addOrDelGoodsCollection()">
                        <div class="iconfont" ng-class="isCollection?'icon-tubiao2223 errorRed':'icon-xihuan gray666'"></div>
                        <div>收藏</div>
                    </div>
                    <div ng-click="goPage('/my/cart')">
                        <div class="iconfont2 icon2-gouwuche1"></div>
                        <div>购物车</div>
                    </div>
                </div>
                <div class="bottom1-btn" style="width:40%" ng-click="hideMenuCheck=1">加入购物车</div>
            </div>

        </div>
        <div class="hideMenu" ng-show="errorFun">
            <div class="errorMain">
                <div class="errorMainRow">对不起,该功能暂未开放</div>
                <div class="errorMainRow" ng-click="errorFun=false">确定</div>
            </div>
        </div>
        <%--详情页面--%>
        <div ng-show="titleCheck==2">
            <div>
                <img ng-repeat="item in imgList" class="imgList"
                     ng-src='/s_img/icon.jpg?_id={{item.fileId}}&wh=300_300'/>
            </div>
        </div>

        <%--评论页面--%>
        <%--<div ng-show="titleCheck==3">--%>
        <%--<div class="commentList" ng-repeat="itemComment in goodsComment">--%>
        <%--<div class="commentMain">--%>
        <%--<div class="headPortraitPanel">--%>
        <%--<div class="circularPhoto">--%>
        <%--<img ng-src="/yzxfMember_page/img/store01.jpg" err-src="/yzxfMember_page/img/notImg01.jpg">--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--<div class="commentContent">--%>
        <%--<div class="overflowHidden">--%>
        <%--<span class="floatLeft" ng-bind="itemComment.mobile"></span>--%>
        <%--<span class="floatRight mainRowRight10 notHigh" ng-bind="showDate(itemComment.createTime)"></span>--%>
        <%--</div>--%>
        <%--<div class="overflowHidden lineHeight30">--%>
        <%--<span class="notHigh">星级:<span class="commentContentGrade" ng-bind="starNo(itemComment.serviceStar)"></span></span>--%>
        <%--<span class="notHigh" ng-bind="'评分: '+itemComment.score"></span>--%>
        <%--</div>--%>
        <%--<div class="overflowHidden gray888" ng-bind="itemComment.name">--%>

        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>

        <%--选择颜色规格--%>
        <div class="hideMenu" ng-show="hideMenuCheck==1">
            <div class="hideTransparent" ng-click="hideMenuCheck=0"></div>
            <div class="hideCommodityMenu">
                <div ng-repeat="specList in goodsInfo.spec">
                    <div class="mainRowTitle notHigh" ng-bind="specList.name"></div>
                    <div class="commodityBtnList mainRow rowHeightAuto">
                        <div ng-repeat="itemsList in specList.items">
                            <button class="commodityBtnListBtn itemNormal"
                                    ng-class="selectItems[$parent.$index].items==itemsList?'selected':''"
                                    ng-bind="itemsList"
                            <%--ng-disabled="itemColor.check==0"--%>
                                    ng-click="setSelectedItems($parent.$index,$index)">
                            </button>
                        </div>
                    </div>
                </div>
                <div class="rowTitle">购买数量</div>
                <div class="commodityMenuRight">
                    <div class="commodityBtn" ng-click="commodityBtn(-1)">-</div>
                    <div class="resultsNum" ng-bind="resultsNum">1</div>
                    <div class="commodityBtn" ng-click="commodityBtn(1)">+</div>
                </div>
                <div ng-show="totalMoney!=''" class="row1">
                    <span>总价: </span>
                    <span ng-bind="getMoney(totalMoney)+'元'" class="deepRed"></span>

                </div>
                <div ng-show="totalMoney!=''" class="row1" style="margin: 0 0 0 10px;">
                    <span>获赠养老金: </span>
                    <span ng-bind="getMoney(pensionMoney)+'元'" class="deepRed"></span>
                </div>
            </div>
            <button class="hideCommodityBtn bgBlue2 whitefff"
                    ng-disabled="(totalMoney==null || totalMoney =='' || totalMoney<=0)?'disabled':false"
                    ng-click="titleCheck = 1;hideMenuCheck = 0;saveCart()">加入购物车
                    <%--ng-click="titleCheck = 1;hideMenuCheck = 0;goPage('/order/orderConfirmation/productId/'+goodsInfo._id)">加入购物车--%>
            </button>
        </div>

        <%--活动菜单--%>
        <%--<div class="hideMenu" ng-show="hideMenuCheck==2">--%>
            <%--<div class="hideTransparent" ng-click="hideMenuCheck=0"></div>--%>
            <%--<div class="hideCommodityMenu">--%>
                <%--<div class="mainRowTitle notHigh">--%>
                    <%--<span class="iconfont icon-cuxiao deepRed textSize22"></span>--%>
                    <%--活动期间满30则可享有一定优惠--%>
                <%--</div>--%>
                <%--<div class="mainRowTitle notHigh">--%>
                    <%--<span class="iconfont icon-cuxiao deepRed textSize22"></span>--%>
                    <%--活动期间满60打8折--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<button class="hideCommodityBtn bgBlue2 whitefff" ng-click="hideMenuCheck=0">返回</button>--%>
        <%--</div>--%>

        <%--描述--%>
        <%--<div class="hideMenu" ng-show="hideMenuCheck==3">--%>
            <%--<div class="hideTransparent" ng-click="hideMenuCheck=0"></div>--%>
            <%--<div class="hideCommodityMenu">--%>
                <%--<div class="mainRowTitle notHigh" ng-bind="'描述: '+(goodsInfo.desc==null)?'暂无描述':goodsInfo.desc">--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<button class="hideCommodityBtn bgBlue2 whitefff" ng-click="hideMenuCheck=0">返回</button>--%>
        <%--</div>--%>

        <div class="sectionMain" style="font-size:0">
            <img ng-repeat="img in imgList"
                 ng-src="{{iconImg(img.icon)}}" class="widthPercent100"
                 err-src="/yzxfMember_page/img/notImg02.jpg"/>
        </div>
    </div>
</div>
