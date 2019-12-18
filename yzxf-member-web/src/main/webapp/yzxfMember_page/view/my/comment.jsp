<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_comment_Ctrl" class="d_content title_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        我的评价
    </div>
    <div class="overflowPC">
        <div class="mainRowLeft10 gray888">
            <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span style="color:#268BBF;font-size: 15px;">{{isNullZero(totalNumer)}}</span>&nbsp;条</span>
        </div>
        <div class="isNullBox" ng-show="commentList==null || commentList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="commentList" ng-repeat="item in commentList">
            <div class="commentStore" ng-click="goPage('store/storeInfo/sellerId/'+item.sellerId)">
                <span class="icon-wxbmingxingdianpu iconfont grayaaa"></span>
                <span class="grayaaa" ng-bind="item.sellerName"></span>
            </div>
            <div class="commentMain">
                <div class="headPortraitPanel">
                    <div class="circularPhoto">
                        <img err-src="/yzxfMember_page/img/notImg02.jpg" ng-src="{{iconImgUrl(item.memberIcon)}}"/>
                    </div>
                </div>
                <div class="commentContent">
                    <div class="overflowHidden">
                        <span class="floatLeft" ng-bind="item.memberName"></span>
                        <span class="floatRight mainRowRight10 notHigh" ng-bind="showDate(item.createTime)"></span>
                    </div>
                    <div class="overflowHidden lineHeight30">
                        <span class="notHigh">星级:<span class="commentContentGrade"
                                                       ng-bind="starNo(item.serviceStar)"></span></span>
                        <%--<span class="notHigh" ng-bind="'评分: '+item.score"></span>--%>
                    </div>
                    <div class="overflowHidden gray888" ng-bind="item.commentContent">
                    </div>
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