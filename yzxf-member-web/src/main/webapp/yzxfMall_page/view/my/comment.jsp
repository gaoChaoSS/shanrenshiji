<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_comment_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto" style="overflow: hidden">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" style="height: 382px;" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myOrderRightContent">
            <div class="isNullBox" ng-show="commentList==null || commentList==''">
                <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                没有内容
            </div>
            <div class="commentDiv" ng-repeat="com in commentList" ng-show="commentList!=null && commentList!=''">
                <div class="floatL" style="width:20%;text-align: left" ng-click="goPage('/seller/sellerInfo/sellerId/'+com.sellerId)">
                    <img class=" orderListImg" style="width: 100px;height: 100px" ng-src="{{iconImgUrl(com.sellerIcon)}}" alt="">
                    <div class=" orderGoodsName" style="margin-left: -5px;width: 110px;text-align: center;" ng-bind="com.sellerName">青年服饰</div>
                </div>
                <div class="floatL" style="width: 60%;text-align: left">
                    <div style="margin-top: 10px;font-size: 14px" ng-bind="showDate(com.createTime)">2017年03月17日16:50:15</div>
                    <div style="margin-top: 25px;font-size: 15px" ng-bind="com.commentContent">鞋子很好都干撒官方水电费回复感受到分公司鞋子很好都干撒官方水电费回复感受到分公司鞋子很好都干撒官方水电费回复感受到分公司 返回服饰电饭锅的方法更好  </div>
                </div>
                <div class="floatR" style="width: 20%;text-align: right">
                    <div style="margin-top: 10px;font-size: 15px">总体评价 <span style="color: #ff933c" ng-bind="starNo(com.serviceStar)"></span></div>
                    <%--<div style="margin-bottom: 20px;margin-top: 25px;font-size: 15px" ng-click="deleteComment()"><img style="margin-right: 10px" src="/yzxfMall_page/img/delete.png" alt="">删除</div>--%>
                </div>
            </div>
            <form class="pageMain">
                <div class="sectionPage" ng-show="dataPage.totalNum>0">
                    <div style="margin: 0 auto;">
                        <div class="btn3" ng-click="pageNext(-1)" ng-show="dataPage.pageNo>1">上一页</div>
                        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"  ng-show="dataPage.pageNo>4"></div>
                        <div ng-show="dataPage.pageNo>5" class="fl lineH30px">...</div>

                        <div class="btn3" ng-repeat="i in dataPage.$$pageList" ng-bind="i" ng-show="dataPage.totalPage>1"
                             ng-click="pageNumber(i)"
                             ng-class="dataPage.pageNo==i?'hoverBorder':''"></div>

                        <div ng-show="dataPage.pageNo<dataPage.totalPage-5" class="lineH30px">...</div>
                        <div class="btn3" ng-bind="dataPage.totalPage"
                             ng-click="setPageNo(dataPage.totalPage);"
                             ng-show="dataPage.pageNo<dataPage.totalPage-4"></div>
                        <div class="btn3" ng-click="pageNext(1)" ng-show="dataPage.pageNo<dataPage.totalPage">
                            下一页
                        </div>
                        <div class="pageGo"
                             ng-show="dataPage.totalPage>1">
                            <input type="text" placeholder="跳转" ng-model="pageGo">
                            <button class="iconfont icon-right-1-copy" ng-click="pageGoFun(pageGo)"></button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>