<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="seller_commodityList_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中部内容--%>
    <div class="bodyWidth marginZAuto">
        <%--分类div--%>
        <div class="commodityType">
            <div class="floatL flex1" style="width:0px;border: none;"></div>
            <div class="floatL flex1" style="width:0px;" ng-show="navGoodsType.length>=1"> </div>
            <div class="floatL flex1" ng-show="navGoodsType.length>=1" ng-bind="navGoodsType[0]" ng-click="getProductByOperate(0)">美食</div>
            <%--ng-click="goPage('/seller/commodityList/pOperate/'+)"--%>
            <div class="floatL flex1" ng-show="navGoodsType.length>=2"> > </div>
            <div class="floatL flex1" ng-show="navGoodsType.length>=2" ng-bind="navGoodsType[1]" ng-click="getProductByOperate(1)">美食</div>
        </div>
        <%--排序筛选--%>
        <div class="commodityUpOrDown">
            <div class="floatL flex1 " ng-click="isClick='0';queryCurrentList()" ng-class="isClick=='0'?'commodityHover':'bdTop'">积分率<span>⇩</span></div>
            <div class="floatL flex1 " ng-click="isClick='1';queryCurrentList()" ng-class="isClick=='1'?'commodityHover':'bdTop'">距离最近<span>⇩</span></div>
            <div class="floatL flex1 " ng-click="isClick='2';queryCurrentList()" ng-class="isClick=='2'?'commodityHover':'bdTop'">推荐商家<span>⇩</span></div>
            <div class="floatL flex1 " ng-click="isClick='3';queryCurrentList()" ng-class="isClick=='3'?'commodityHover':'bdTop'">销量<span>⇩</span></div>
            <div class="floatL flex1 " ng-click="isClick='4';queryCurrentList()" ng-class="isClick=='4'?'commodityHover':'bdTop'">评分<span>⇩</span></div>
        </div>
        <%--商品列表--%>
        <div class="commodityListDiv">
            <div class="isNullBox" ng-show="dataPage.items==null || dataPage.items==''">
                <div class="iconfont icon-meiyouneirong" style="font-size: 100px;"></div>
                没有内容
            </div>
            <div class="commodityDiv floatL" ng-repeat="g in dataPage.items" ng-click="openPage('/seller/commodityInfo/goodsId/'+g._id)">
                <%--商品图片--%>
                <div style="background: url('{{iconImgUrl(g.icon)}}') no-repeat;background-size: 100%"></div>
                <%--价格--%>
                <div>
                    <span style="color: #ff0e0c">¥<span style="font-size: 30px" ng-bind="getMoney(g.salePrice)"></span></span>
                    <del style="color: #929292;" ng-bind="'¥'+getMoney(g.oldPrice)"></del>
                </div>
                <%--商品名--%>
                <div class="flex1 textHightTwo" style="height:26px;line-height:26px" ng-bind="g.name">
                </div>
                <%--销量养老金--%>
                <div class="flex1 commodityTongji">
                    <div class="floatL" style="color: #929292;">总销量<span style="color:#B47E5C;" ng-bind="getMoney(g.saleCount)"></span></div>
                    <div class="floatL textEllipsis" style="color: #929292;">返养老金<span style="color: #138bbe"  ng-bind="'¥'+pension(g.integralRate,g.salePrice)"></span></div>
                </div>
            </div>
        </div>
        <div ng-include="'/yzxfMall_page/temp_new/pagingGrid.html'"></div>
    </div>
    <%--底部模板--%>
    <div class="marginTop30" ng-include="mallBottom"></div>

</div>