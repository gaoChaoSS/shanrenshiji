<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_search_Ctrl" class="d_content form_section title_section store_panel">
    <div class="title titleRed">
        <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBack()"></span>
        <form ng-submit="querySearch();">
            <div id="indexRow">
                <span class="icon-fangdajing iconfont iconImg6" ng-click="querySearch()"></span>
                <input type="text" placeholder="请输入要搜索的词" name="keyWord" ng-model="keyWord"/>
                <button type="submit" style="display: none;">搜索</button>
            </div>
        </form>
    </div>
    <div class="overflowPC" id="storeScroll">
        <div class="sDaohang">
            <button ng-click="changgeShow('all')">全部({{isNullZero(totalNum)}})</button>
            <button ng-click="changgeShow('seller')">商家({{isNullZero(sellerSize)}})</button>
            <button ng-click="changgeShow('product')">商品({{isNullZero(productSize)}})</button>
        </div>
        <div class=projectInfo ng-repeat="item in resultList">
            <div class="itemInfo"
                 ng-click="goPage(item.type=='p'?'/store/commodity/goodsId/'+item._id:'/store/storeInfo/sellerId/'+item._id)">
                <img ng-src="{{iconImgUrl(getSellerIcon(item.icon,item.doorImg))}}" err-src="/yzxfMember_page/img/notImg02.jpg" style="width: 100%">
                <p>{{item.name}}</p>
            </div>
        </div>
        <div class="clearDiv"></div>
        <!--加载更多-->
        <div class="loadMore" ng-show="isSearch">
        <span style="font-size: 15px;color: #999;">共查询到&nbsp;<span
                style="color:#268BBF;font-size: 15px;">{{isNullZero(totalNumer)}}</span>&nbsp;条</span>
            <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>