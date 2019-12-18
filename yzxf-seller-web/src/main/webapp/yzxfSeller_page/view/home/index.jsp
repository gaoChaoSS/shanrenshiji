<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_index_Ctrl" class="d_content title_section form_section " style="overflow-x: hidden">
    <div ng-if="showType=='loading'" class="hideMenu loadingClass">加载中...</div>

    <div class="hideMenu" ng-if="showType=='notAccess'">
        <div class="errorMain">
            <div class="errorMainRow">您还没有开通服务站功能</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="goPage('/store/store')">取消</div>
                <div class="errorMainBtn black" ng-click="goPage('/store/factorApply')">去开通</div>
            </div>
        </div>
    </div>
    <div class="overflowPC">
        <div ng-if="showType=='ok'">
            <div class="store_page_storeHead"
                 style="background-image:url('/yzxfSeller_page/img/bkSellerLogin.png');background-size: 100%;"></div>
            <div class="store_page_Head">
                <div class="store_page_Head_left iconfont icon-left-1" ng-click="goPage('/store/store')">商家</div>
                <%--头像--%>
                <div class="my_page_myHead_div" style="position: relative;" ng-click="goPage('/store/setting/userType/factor')">
                    <img class="my_page_myHead_img" src="/yzxfSeller_page/img/notImg02.jpg"
                         ng-src="{{iconImgUrl()}}"
                         alt="userHead">
                </div>
                <div class="icon-shezhi iconfont store_page_Head_right"
                     ng-click="goPage('/store/setting/userType/factor')"></div>

                <div class="my_page_myHead_loginReg">
                    <div class="my_page_myHead_loginRegText textSize18"
                         ng-bind="factorInfo.name==null?'无':factorInfo.name"></div>
                </div>
            </div>

            <div class="page-narrow1">
                <div class="sectionMain sectionWallet sectionMainNotMargin">
                    <div class="sectionWalletRow" ng-click="goPage('/home/memberCardActive')">
                        <div><span class="icon-shimingrenzheng iconfont textSize25"></span></div>
                        <div>会员激活</div>
                    </div>
                    <div class="sectionWalletRow" ng-click="goPage('/home/cardRecords')">
                        <div><span class="icon-card iconfont textSize30"></span></div>
                        <div>发卡记录</div>
                    </div>
                </div>

                <div class="mod-title2 mainRowTop10">
                    <div class="fl">其他功能</div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-click="goPage('/home/tradeList')">
                        <div class="iconfont2 icon2-jiaoyi" style="color:#F9A037"></div>
                        <div>会员交易记录</div>
                    </div>
                    <div ng-click="goPage('/home/exchangeCard')">
                        <div class="iconfont2 icon2-replace" style="color:#3D9AEE"></div>
                        <div>换卡</div>
                    </div>
                    <div ng-click="goPage('/home/surplusMemberCard')">
                        <div class="iconfont2 icon2-phonedateicon21" style="color:#3D9AEE;font-size: 30px;"></div>
                        <div>未激活号段</div>
                    </div>
                    <div ng-click="goPage('/home/cardIssuingAccount')">
                        <div class="iconfont2 icon2-qianbao2" style="color:#FA957A"></div>
                        <div>服务站账户</div>
                    </div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-click="goPage('/home/belongStore')">
                        <div class="iconfont icon-shangjia" style="color:#3E9AEE;font-size: 30px;"></div>
                        <div>归属商家</div>
                    </div>
                    <div ng-click="goPage('/home/belongMember')">
                        <div class="iconfont icon-wodeshixin" style="color:#3E9AEE;font-size: 30px;"></div>
                        <div>归属会员</div>
                    </div>
                    <div ng-click="goPage('/store/store')">
                        <div class="iconfont icon-shangjia" style="color:#ff0000;font-size: 30px;"></div>
                        <div>我的店铺</div>
                    </div>
                    <div></div>
                </div>
            </div>

            <%--<div class="sectionMain">--%>
                <%--<div class="mainRow lineHeight50" ng-click="goPage('/home/tradeList')">--%>
                        <%--<div class="rowTitle rowTitleBtn black">--%>
                            <%--<img src="/yzxfSeller_page/img/tradeList.png">--%>
                            <%--&nbsp;&nbsp;会员交易记录--%>
                        <%--</div>--%>
                    <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
                <%--</div>--%>
                <%--<div class="mainRow lineHeight50" ng-click="goPage('/home/cardIssuingAccount')">--%>
                    <%--<div class="rowTitle rowTitleBtn black">--%>
                        <%--<img src="/yzxfSeller_page/img/storeAccount.png">--%>
                        <%--&nbsp;&nbsp;服务站账户--%>
                    <%--</div>--%>
                    <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
                <%--</div>--%>
                <%--<div class="mainRow lineHeight50" ng-click="goPage('/home/exchangeCard')">--%>
                    <%--<div class="rowTitle rowTitleBtn black">--%>
                        <%--<img src="/yzxfSeller_page/img/exchangeCard.png">--%>
                        <%--&nbsp;&nbsp;换卡--%>
                    <%--</div>--%>
                    <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
                <%--</div>--%>
                <%--<div class="mainRow lineHeight50" ng-click="goPage('/home/surplusMemberCard')">--%>
                    <%--<div class="rowTitle rowTitleBtn black">--%>
                        <%--<img src="/yzxfSeller_page/img/notActiveCard.png">--%>
                        <%--&nbsp;&nbsp;未激活会员卡号段--%>
                    <%--</div>--%>
                    <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
                <%--</div>--%>

            <%--</div>--%>

            <div class="hideMenu" ng-show="saoyisao">
                <div class="errorMain">
                    <div class="errorMainRow">将访问您的相机</div>
                    <div class="errorMainRow">
                        <div class="errorMainBtn black" ng-click="saoyisao=false">取消</div>
                        <div class="errorMainBtn black">确定</div>
                    </div>
                </div>
            </div>

        </div>

    </div>


</div>
