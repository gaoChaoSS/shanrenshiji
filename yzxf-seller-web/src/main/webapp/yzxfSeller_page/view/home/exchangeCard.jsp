<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="home_exchangeCard_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack black" ng-click="goPage('/home/index')"></span>
        换卡
    </div>
    <div class="overflowPC">
        <form ng-submit="exchangeCard()" ng-show="status=2">
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="mainRow">
                        <div class="rowTitle textSize15">
                            电话:
                        </div>
                        <input type="text" class="rowInput textSize15" ng-model="phoneNumber" placeholder="请输入完整的会员手机号"/>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle textSize15">
                            旧卡号:
                        </div>
                        <input type="text" class="rowInput textSize15" ng-model="oldCard" placeholder="请输入旧卡卡号"/>
                    </div>
                    <div class="mainRow">
                        <div class="rowTitle textSize15">
                            新卡号:
                        </div>
                        <input type="text" class="rowInput textSize15" ng-model="newCard" placeholder="请输入新卡卡号"/>
                    </div>
                </div>

            </div>
            <div style="text-align: center;padding-top: 30px;">
                <button style="background: #138bbe;" class="cardIssuingButton" type="submit">确定换卡</button>
            </div>
        </form>

        <div class="mainRow">
            <div class="rowTitle widthPercent20 textSize15 lineHeight50">换卡记录</div>
        </div>

        <div class="mainRowLeft10 gray888">
            共查询到 <span ng-bind="isNullZero(totalNumber)" class="colorBlue"></span> 条记录
        </div>
        <div class="isNullBox" ng-show="exchangeCardList==null || exchangeCardList==''">
            <div class="iconfont icon-meiyouneirong"></div>
            没有内容
        </div>
        <div class="mainRowTop10" ng-repeat="item in exchangeCardList">
            <div class="bgWhite paddingBottomNot borderBottomGray">
                <div class="mainRow mainRowNotBorderBottom">
                    <div class="rowTitle widthPercent20">用户名 : </div>
                    <div class="rowInput black"
                         ng-bind="(item.realName==null||item.realName=='')?'匿名':item.realName"></div>

                </div>
                <div class="mainRow">
                    <div class="rowTitle widthPercent20">旧卡号 : </div>
                    <div class="rowInput black" ng-bind="item.oldCardNo" style="padding-left: 20px;"></div>
                    <div class="rowTitle widthPercent20">新卡号 : </div>
                    <div class="rowInput black" ng-bind="item.newCardNo" style="padding-left: 20px;"></div>
                </div>
            </div>
            <div class="overflowHidden lineHeight50 textIndent30 textRight bgWhite">
                <span class="mainRowRight10">换卡时间 : </span>
                <span class="mainRowRight10" ng-bind="showYFullTime(item.createTime)"></span>
            </div>
        </div>
        <div class="loadMore">
            <div id="moreButton" ng-show="isLoadMore" style="font-size: 15px;color: #999;"
                 ng-click="more()">加载更多...
            </div>
            <div ng-show="!isLoadMore" style="font-size: 15px;color: #999;">没有更多了</div>
        </div>
    </div>
</div>