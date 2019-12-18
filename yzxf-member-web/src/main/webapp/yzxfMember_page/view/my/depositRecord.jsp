<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_depositRecord_Ctrl" class="d_content form_section title_section">
    <div class="overflowPC">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/my/wallet')"></span>
        购买记录
    </div>
        <div class="mainRowLeft10 gray888">
            <div class="notHigh">共查询到 <span ng-bind="isNullZero(totalNumber)" class="colorBlue ng-binding">49</span> 条记录
            </div>
        </div>
        <div ng-repeat="item in record" class="sectionMain4">
            <div class="recordTime">
                <div ng-bind="getRecordDate(showYFullTime(item.createTime),0)" class="gray888"></div>
                <div ng-bind="getRecordDate(showYFullTime(item.createTime),1)" class="gray888"></div>
            </div>
            <img ng-src="{{iconImgUrl(item.icon)}}"/>
            <div class="recordText">
                <div>
                    <span ng-bind="'¥'+item.orderCash" class="fl textSize16"></span>
                <span class="fr">
                    <span class="iconfont textSize18" ng-class="getPayTypeIcon(item.payType)"></span>
                    <span ng-bind="getPayType(item.payType)" class="gray888"></span>
                </span>
                </div>
                <div>
                    <span ng-bind="item.realName"></span>
                    <span ng-bind="'('+item.mobile+')'" class="gray888"></span>
                </div>
            </div>
        </div>

        <div class="loadMore" ng-show="totalNumber>0">
            <div id="moreButton" ng-show="isLoadMore && totalNumber>0" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>