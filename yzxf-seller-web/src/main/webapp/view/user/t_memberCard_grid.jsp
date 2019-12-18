<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection">
    <div class="popTitle">换卡</div>
    <form ng-submit="exchangeCard(cardObj.phoneNumber,cardObj.oldCard,cardObj.newCard)" style="text-align: center;">
        <div class="lineH50px">
            <span>手机号:</span><input type="text" ng-model="cardObj.phoneNumber" placeholder="请输入完整的会员手机号">
        </div>
        <div class="lineH50px">
            <span>旧卡号:</span><input type="text" ng-model="cardObj.oldCard" placeholder="请输入旧卡卡号">
        </div>
        <div class="lineH50px">
            <span>新卡号:</span><input type="text" ng-model="cardObj.newCard" placeholder="请输入新卡卡号">
        </div>
        <div>
            <button class="btn1 bgOrange" type="submit">确定</button>
        </div>
    </form>
</div>
<div class="popSection flex2">
    <div class="popTitle">换卡记录</div>
    <div style="width:100%;padding:0">
        <div class="sectionTable">
            <div>
                <div style="width:20%">归属</div>
                <div style="width:20%">会员姓名</div>
                <div style="width:20%">新卡卡号</div>
                <div style="width:20%">旧卡卡号</div>
                <div style="width:20%">时间</div>
            </div>
            <div class="trBk" ng-repeat="exCl in exchangeCardList"
                ng-class="$$selectedItem._id==exCl._id?'selected':''">
                <div style="width:20%" ng-bind="exCl.name"></div>
                <div style="width:20%" ng-bind="exCl.realName"></div>
                <div style="width:20%" ng-bind="exCl.newCardNo"></div>
                <div style="width:20%" ng-bind="exCl.oldCardNo"></div>
                <div style="width:20%" ng-bind="showYFullTime(exCl.createTime)"></div>
            </div>
        </div>
        <div class="isNullBox" ng-show="isNullPage2">
            <div class="iconfont icon-meiyouneirong isNullIcon"></div>
            <div class="font25px colorGrayccc">没有数据</div>
        </div>

    </div>
</div>
<div class="sectionPage" ng-hide="isNullPage2" style="position: absolute;left: 0;bottom: -40px;width: 100%;margin: 0;">
    <div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex2!=1">上一页</div>
    <div ng-show="isFirstPage2">
        <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>
        <div class="fl lineH30px">......</div>
    </div>
    <div class="btn3" ng-repeat="page2 in pageList2" ng-bind="page2.num"
         ng-click="pageNumber(page2.num);pageCur(page2.num)"
         ng-class="pageIndex2==page2.num?'bgBlue tWhite':''"></div>
    <div ng-show="isLastPage2">
        <div class="fl lineH30px">......</div>
        <div class="btn3 fl marginLR5" ng-bind="totalPage2" ng-click="pageNumber(totalPage2)"></div>
    </div>
    <div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex2!=totalPage2">下一页</div>
    <div class="pageGo">
        <input type="text" placeholder="跳转" ng-model="pageGo2" />
        <button class="iconfont icon-right-1-copy" ng-click="pageNumber(pageGo2)"></button>
    </div>
</div>