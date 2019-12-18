<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_storeCommodity_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack black" ng-click="goPage('/store/store')"></span>
        商品
    </div>
    <div class="overflowPC">
        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totleNum)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="commoditys.length==0">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="commodity {{$index}}" ng-repeat="item in commoditys track by $index">
            <a href="store/addStoreCommodity/_id/{{item._id}}">
                <div class="commodityImg">
                    <img class="my_page_myHead_img" err-src="/yzxfMember_page/img/notImg02.jpg"
                         ng-src="{{iconImgUrl(item.icon)}}">
                </div>
                <div class="commodityInfo">
                    <div ng-bind="item.name"></div>
                    <div>售价: <span ng-bind="item.salePrice+'元'"></span></div>
                </div>
                <div ng-show="item.saleCount!=null && !isDel" class="btn-sale1">
                    销量: <span ng-bind="item.saleCount" class="gray888"></span>
                </div>
            </a>
            <div class="btn-del1 iconfont icon-close flex1" ng-click="delConfirm($index, item._id)" ng-class="isDel?'btn-del2':''"></div>
        </div>
        <div class="loadMore" style="text-align: center">
            <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
    <div class="hideMenu" ng-show="menuCheck">
        <div class="errorMain">
            <div class="errorMainRow">确定删除该商品吗?</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="del(delIndex,delId)">确认</div>
                <div class="errorMainBtn black" ng-click="menuCheck=false">取消
                </div>
            </div>
        </div>
    </div>


    <div class="hideCommodityBtn bgWhite rowBorderTop" style="width: 50%;"
         ng-click="goPage('/store/addStoreCommodity')">
        <span class="icon-llalbumshopselectorcreate iconfont limeGreen textSize18"></span>
        <span class="notHigh">新增商品</span>
    </div>
    <div class="hideCommodityBtn bgWhite rowBorderTop" ng-show="!isDel"
         style="width: 50%;margin-left: 50%;" ng-click="showDel()">
        <span class="icon-close iconfont limeGreen textSize18"></span>
        <span class="notHigh">删除商品</span>
    </div>
    <div class="hideCommodityBtn bgWhite rowBorderTop" ng-show="isDel"
         style="width: 50%;margin-left: 50%;background-color: #138bbe" ng-click="showDel()">
        <span class="notHigh" style="color: #fff">保存</span>
    </div>

</div>