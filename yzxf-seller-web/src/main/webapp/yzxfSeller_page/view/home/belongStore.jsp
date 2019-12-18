<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_belongStore_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/home/index')"></span>
        归属商家
    </div>
    <div class="overflowPC">
        <div class="storeCon" ng-repeat="user in sellerList track by $index">
            <div ng-include="'/yzxfSeller_page/view/store/team_grid.jsp'"></div>
            <%--<div class="storeCon-main">--%>
                <%--<img ng-src="{{iconImgUrl(seller.icon)}}" src="/yzxfSeller_page/img/notImg02.jpg"/>--%>
                <%--<div class="textEllipsis storeCon-title" ng-bind="seller.name"></div>--%>
                <%--<div class="fl storeCon-info">--%>
                    <%--<div class="storeCon-tag">--%>
                        <%--<div ng-bind="'积分率 '+seller.integralRate+' %'"></div>--%>
                        <%--<div ng-bind="seller.operateType"></div>--%>
                        <%--<div ng-if="!seller.canUse" class="tag-canUse">禁用</div>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<span style="margin-right:10px;color: #888;" ng-bind="seller.phone"></span>--%>
                        <%--<span style="color: #888;" ng-bind="seller.contactPerson"></span>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="storeCon-address" ng-bind="'地址: '+seller.area+seller.address"></div>--%>
            <%--<div class="storeCon-date" ng-bind="showYFullTime(seller.createTime)"></div>--%>
        </div>
    </div>
    <div class="loadMore">
        <div id="moreButton" ng-show="isLoadMore&&totalNumer>0" style="font-size: 15px;color: #999;"
             ng-click="more()">加载更多...
        </div>
        <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
    </div>
</div>
