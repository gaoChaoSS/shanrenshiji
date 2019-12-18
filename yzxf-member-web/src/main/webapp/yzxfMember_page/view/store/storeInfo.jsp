<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_storeInfo_Ctrl" class="d_content store_panel title_section order_panel form_section">
    <div class="overflowPC positionRelative" id="storeInfoScroll">
        <%--页面头部--%>
        <div class="bk-hidden">
            <div class="bk-blur1" style="{{showBk}}"></div>
        </div>
        <div class="storeInfoHead textShadow1">
            <%--返回图标--%>
            <div class="icon-left-1 iconfont storeBack" ng-click="goBack()"></div>
            <%--商户logo--%>
            <div class="storeInfoHeadLogo" ng-click="goPage('/store/storeIntro/sellerId/'+sellerId)">
                <img err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(getSellerIcon(sellerInfo.icon,sellerInfo.doorImg))}}"/>
            </div>
            <%--商户名字和简介--%>
            <div class="storeInfoHeadTitle" ng-click="goPage('/store/storeIntro/sellerId/'+sellerId)">
                <div class="storeName" ng-bind="sellerInfo.name"></div>
                <div class="textHightTwo" ng-bind="sellerInfo.intro"></div>
                <%--<div ng-show="sellerInfo.isOnlinePay" style="color:#ddd">--%>
                    <%--<span class="icon-fu iconfont storeHeadIcon colorBlue2"></span>--%>
                    <%--该商家支持在线支付--%>
                <%--</div>--%>
                <%--<div ng-show="!sellerInfo.isOnlinePay" style="color:#ddd">--%>
                    <%--<span class="icon-fu iconfont storeHeadIcon grayccc"></span>--%>
                    <%--该商家不支持在线支付--%>
                <%--</div>--%>
            </div>
            <%--优惠券和收藏--%>
            <div class="storeInfoHeadRightIcon">
                <div class="positionRelative" ng-click="isGoPage(sellerInfo._id)">
                    <div class="iconImg3" ng-show="redIcon"></div>
                    <div class="icon-youhuiquan iconfont storeIconRight whitefff"></div>
                </div>

                <div class="iconfont storeIconRight"
                     ng-class="isCollection?'icon-tubiao2223 errorRed':'icon-xihuan whitefff'"
                     ng-click="addOrDelStoreCollection()"></div>
            </div>
            <div class="clearDiv"></div>
        </div>
        <%--页面标题--%>
        <div class="title">
            <div class="titleThree">
            <span class="titleText titleTextTwo" ng-class="{1:'titleTextCheckRed'}[titleCheck]"
                  ng-click="titleCheck=1;getGoodsList('frist');">商品</span>
            <span class="titleText titleTextTwo" ng-class="{2:'titleTextCheckRed'}[titleCheck]"
                  ng-click="titleCheck=2;getStoreCommentList('frist')">评论</span>
            <span class="titleText titleTextTwo" style="position: relative" ng-class="{3:'titleTextCheckRed'}[titleCheck]"
                  ng-click="titleCheck=3;getActivity('frist')">商家活动<div style="top:0px;right: 5px" class="iconImg3" ng-show="sellerEventIcon"></div></span>
            </div>
        </div>

        <%--店内商品--%>
        <div ng-show="titleCheck==1">
            <div ng-include="productInclude"></div>
        </div>
        <%--店内评论--%>
        <div ng-show="titleCheck==2">
            <div class="mainRowLeft10 gray888">
                <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span style="color:#268BBF;font-size: 15px;">{{isNullZero(totalCommentNumber)}}</span>&nbsp;条</span>
            </div>
            <div class="isNullBox" ng-show="commentList==null || commentList==''">
                <div class="iconfont icon-meiyouneirong"></div>
                没有内容
            </div>
            <div class="commentList" ng-repeat="comment in commentList">
                <div class="commentMain">
                    <div class="headPortraitPanel">
                        <div class="circularPhoto">
                            <img err-src="/yzxfMember_page/img/notImg02.jpg"
                                 ng-src="{{iconImgUrl(comment.memberIcon)}}"/>
                        </div>
                    </div>
                    <div class="commentContent">
                        <div class="overflowHidden">
                            <%--用户名--%>
                            <span class="floatLeft" ng-bind="formatMobile(comment.mobile)"></span>
                            <%--评论时间--%>
                            <span class="floatRight mainRowRight10 notHigh"
                                  ng-bind="showDate(comment.createTime)"></span>
                        </div>
                        <div class="overflowHidden lineHeight30 positionRelative">
                            <span class="notHigh">星级:<span class="littleYellow" ng-bind="starNo(comment.serviceStar)"></span></span>
                            <span class="tap1" ng-show="comment.orderStatus!=5 && comment.orderStatus!=100"
                                  ng-bind="getOrderStatus(comment.orderStatus)"></span>
                        </div>
                        <%--评论内容--%>
                        <div class="overflowHidden" ng-bind="comment.storeComment">
                        </div>
                    </div>
                </div>
            </div>
            <div class="loadMore" ng-show="totalCommentNumber>0">
                <div id="moreCButton" ng-show="isCommentLoadMore&&totalCommentNumber>0"
                     style="font-size: 15px;color: #999;"
                     ng-click="moreComment()">加载更多...
                </div>
                <div ng-show="!isCommentLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
            </div>
        </div>
        <%--商家--%>
        <div ng-show="titleCheck==3">
            <%--<div class="mainRowTitleStore notHigh">附近销量较好的商家</div>--%>
            <div class="mainRowLeft10 gray888">
                <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span style="color:#268BBF;font-size: 15px;">{{isNullZero(totalActivityNumber)}}</span>&nbsp;条</span>
            </div>
            <div class="isNullBox" ng-show="eventList==null || eventList==''">
                <div class="iconfont icon-meiyouneirong"></div>
                没有内容
            </div>
            <div class="sectionMain sectionMainNotMargin mainRowTop10 eventDiv" ng-repeat="item in eventList">
                <div class="mainRowEvent">
                    <div class="rowTitleEvent loessYellow">活动名称:</div>
                    <div class="rowInputEvent" ng-bind="item.name"></div>
                </div>
                <div class="mainRowEvent">
                    <div class="rowTitleEvent loessYellow">活动时间:</div>
                    <div class="rowInputEvent"
                         ng-bind="showDate(item.startTime)+' - '+showDate(item.endTime)"></div>
                </div>
                <div class="mainRowEvent">
                    <div class="rowTitleEvent loessYellow">活动规则:</div>
                    <div class="rowInputEvent lineHeight20 mainRowTop10" ng-bind="item.content"></div>
                </div>
                <div class="mainRowEvent" ng-show="item.explain!=null && item.explain!=''">
                    <div class="rowTitleEvent loessYellow">特殊说明:</div>
                    <div class="rowInputEvent lineHeight20 mainRowTop10" ng-bind="item.explain"></div>
                </div>
                <div class="triangleEventBig" ng-class="colorCheck(item.$$goingNo)"></div>
                <%--<div class="triangleEventBig triangleEventBigCheck" ng-show="!item.$$goingNo"></div>--%>
                <div class="triangleEventSmall"></div>
                <div class="triangleEventText textSize12" ng-bind="item.$$goingCheck"></div>
            </div>
            <div class="loadMore" ng-show="totalActivityNumber>0">
                <div id="moreAButton" ng-show="isActivityLoadMore&&totalActivityNumber>0"
                     style="font-size: 15px;color: #999;"
                     ng-click="moreActivity()">加载更多...
                </div>
                <div ng-show="!isActivityLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
            </div>
            <%--<div class="storeLists" ng-repeat="store in storeList track by $index">--%>
            <%--&lt;%&ndash;商家图标区域&ndash;%&gt;--%>
            <%--<div class="storeIcon">--%>
            <%--<div class="storeNotIcon">--%>
            <%--<img ng-src="{{store.storeImg}}" err-src="/yzxfMember_page/img/notImg02.jpg">--%>
            <%--</div>--%>

            <%--</div>--%>
            <%--&lt;%&ndash;商家详细信息区域&ndash;%&gt;--%>
            <%--<div class="storeInfo">--%>
            <%--<div class="storeName">--%>
            <%--&lt;%&ndash;店铺名&ndash;%&gt;--%>
            <%--<span ng-bind="store.storeName"></span>--%>
            <%--&lt;%&ndash;积分率&ndash;%&gt;--%>
            <%--<span ng-bind="store.integral"></span>--%>
            <%--</div>--%>
            <%--<div class="storeDescribe">--%>
            <%--<span class="storeDescribeTitle">商家描述:</span>--%>
            <%--<span class="storeDescribeText" ng-bind="store.describe"></span>--%>
            <%--</div>--%>
            <%--<div class="storeDescribe storeDescribeAddress">--%>
            <%--地址:<span ng-bind="store.address"></span>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--&lt;%&ndash;一键拨号&ndash;%&gt;--%>
            <%--<div class="storePhone">--%>
            <%--<div class="storePhoneIcon icon-lianxi01 iconfont"></div>--%>
            <%--<div class="storePhoneText">一键拨号</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--<div class="titleStoreAddress">--%>
            <%--本商家所处位置--%>
            <%--</div>--%>
            <%--<div class="storeAddressImg">--%>
            <%--<img src="/yzxfMember_page/img/ditu.png" alt="">--%>
            <%--</div>--%>
        </div>
    </div>
</div>