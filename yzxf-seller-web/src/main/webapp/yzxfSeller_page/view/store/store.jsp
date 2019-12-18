<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_store_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="overflowPC" style="position: relative;">
        <div ng-if="showType=='loading'" class="hideMenu loadingClass"></div>

        <div class="hideMenu" ng-if="showType=='notAccess'">
            <div class="errorMain">
                <div class="errorMainRow">您还没有开通商户功能</div>
                <div class="errorMainRow">
                    <div class="errorMainBtn black" ng-click="goPage('/home/index')">取消</div>
                    <div class="errorMainBtn black" ng-click="goPage('/store/storeApply')">去开通
                    </div>
                </div>
            </div>
        </div>

        <div ng-if="showType=='ok'">
            <div class="store_page_storeHead"
                 style="background-image:url('/yzxfSeller_page/img/bkSellerLogin.png');background-size: 100%;"></div>
            <div class="store_page_Head">
                <div class="store_page_Head_left iconfont icon-left-1" ng-click="goPage('/home/index')">服务站</div>
                <%--头像--%>
                <div class="my_page_myHead_div" style="position: relative;"
                     ng-click="goPage('/store/setting/userType/seller')">
                    <img class="my_page_myHead_img" src="/yzxfSeller_page/img/notImg02.jpg"
                         ng-src="{{iconImgUrl(getSellerIcon(loginInfo.icon,loginInfo.doorImg))}}"
                         alt="userHead">
                    <%--<input type="file" name="file" style="position: absolute;width: 100px; height: 100px;"--%>
                    <%--onchange="angular.element(this).scope().uploadFile(this,'icon')"/>--%>
                </div>
                <div class="icon-shezhi iconfont store_page_Head_right"
                     ng-click="goPage('/store/setting/userType/seller')"></div>

                <div class="my_page_myHead_loginReg">
                    <div class="my_page_myHead_loginRegText textSize18"
                         ng-bind="loginInfo.name==null?'无':loginInfo.name"></div>
                    <div class="my_page_myHead_loginRegText textSize12"
                         ng-bind="'积分率 '+loginInfo.integralRate +' %'"></div>
                </div>
            </div>

            <div class="page-narrow1">
                <div class="sectionMain sectionWallet sectionMainNotMargin">
                    <div class="sectionWalletRow" ng-click="goPage('/store/memberRichScan')">
                        <div><span class="icon2-saoma iconfont2 textSize25"></span></div>
                        <div>会员扫码</div>
                    </div>
                    <div class="sectionWalletRow" ng-click="goPage('/store/otherRichScan')">
                        <div><span class="icon2-saoma iconfont2 textSize30"></span></div>
                        <div>互联网收款</div>
                    </div>
                    <div class="sectionWalletRow" ng-click="goPage('/store/cashTransactionsScan')">
                        <div><span class="icon2-xianjindai iconfont2 textSize30"></span></div>
                        <div>现金交易</div>
                    </div>
                </div>

                <div class="mod-title2 mainRowTop10" ng-click="goPage('/store/order/headerIndex/-1')">
                    <div class="fl">在线订单管理</div>
                    <div class="fr">
                        查看所有
                        <span class="icon-right-1-copy iconfont gray888"></span>
                    </div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-repeat="head in headerList" ng-click="goPage('/store/order/headerIndex/'+$index)">
                        <div class="{{head.icon}} iconfont2" style="color:#138bbe;font-size: 30px;"></div>
                        <div class="topMenu-tap2" ng-bind="head.count" ng-show="head.count!=null"></div>
                        <div ng-bind="head.name"></div>
                    </div>
                </div>

                <div class="mod-title2 mainRowTop10" ng-click="goPage('/store/order/pageCheck/drawback/headerIndex/-1')">
                    <div class="fl">退款售后管理</div>
                    <div class="fr">
                        查看所有
                        <span class="icon-right-1-copy iconfont gray888"></span>
                    </div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-repeat="head2 in headerList2" ng-click="goPage('/store/order/pageCheck/drawback/headerIndex/'+$index)">
                        <div class="{{head2.icon}} iconfont2" style="color:#F9506C;font-size: 30px;"></div>
                        <div class="topMenu-tap2" style="border:1px solid #F9506C;color:#F9506C"
                             ng-bind="head2.count" ng-show="head2.count!=null"></div>
                        <div ng-bind="head2.name"></div>
                    </div>
                </div>

                <div class="mod-title2 mainRowTop10">
                    <div class="fl">其他功能</div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-click="goPage('/store/storeCommodity')">
                        <div class="iconfont2 icon2-shangpin" style="color:#F9A037"></div>
                        <div>店铺商品</div>
                    </div>
                    <div ng-click="goPage('/store/storeEvent')">
                        <div class="iconfont2 icon2-tejia" style="color:#3D9AEE"></div>
                        <div>店铺活动</div>
                    </div>
                    <div ng-click="goPage('/store/storeCoupon')">
                        <div class="iconfont2 icon2-3" style="color:#3D9AEE"></div>
                        <div>店铺卡券</div>
                    </div>
                    <div ng-click="goPage('/store/storeAccount')">
                        <div class="iconfont2 icon2-qianbao2" style="color:#FA957A"></div>
                        <div>商家账户</div>
                    </div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-click="goPage('/store/useCoupon')">
                        <div class="iconfont2 icon2-qiaquanhexiao" style="color:#F9506C;font-size: 34px;"></div>
                        <div>卡券核销</div>
                    </div>
                    <div ng-click="goPage('/store/queryTransaction')">
                        <div class="iconfont2 icon2-iconfontxinxichaxun" style="color:#8994FF"></div>
                        <div>交易查询</div>
                    </div>
                    <%--<div ng-click="goPage('/home/belongMember/userType/Seller')">--%>
                        <%--<div class="iconfont icon-wodeshixin" style="color:#3E9AEE;font-size: 30px;"></div>--%>
                        <%--<div>归属会员</div>--%>
                    <%--</div>--%>
                    <div ng-click="goRelate()">
                        <div class="iconfont2 icon2-k" style="color:#15A887;font-size: 25px;"></div>
                        <div>快易帮</div>
                    </div>
                    <div ng-click="getQrcode()">
                        <div class="iconfont2 icon2-ccgl-yundansaomiao-2" style="color:#3E9AEE"></div>
                        <div>收银码</div>
                    </div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-click="goPage('/store/teamOrder')">
                        <div class="iconfont2 icon2-jiaoyi" style="color:#F9A037"></div>
                        <div>我的红包</div>
                    </div>
                    <div ng-click="goPage('/store/team')">
                        <div class="iconfont icon-friends" style="color:#cb00d8"></div>
                        <div>团队成员</div>
                    </div>
                    <div></div>
                    <div></div>
                </div>
                <div class="topMenu sectionMainNotMargin" style="padding: 20px 0">
                    <div ng-click="goMyMember()" ng-show="loginInfo.member">
                        <div class="iconfont2 icon2-replace" style="color:#3E9AEE"></div>
                        <div>会员中心</div>
                    </div>
                    <div></div>
                    <div></div>
                    <div></div>
                </div>
            </div>


            <div class="hideMenu" ng-show="remind">
                <div class="errorMain otherMain">
                    <div class="icon-close iconfont closeStoreBtn" ng-click="remind=false"></div>
                    <div class="otherMainRow"><img src="/yzxfSeller_page/img/wenxintixing.png" alt=""></div>
                    <div class="otherMainRow">
                        <span class="textSize16">您可以通过扫描用户的二维码</span>
                        <span class="textSize16">来进行手动养老积分输入哦~</span>
                    </div>
                </div>
            </div>


        </div>
    </div>

    <style>
        #qrcode img{
            display:block;
            width:110px;
            height:110px;
        }
        .qrcode-item{
            transform: translate(18%,18%);
        }

        .qrcode-title{
            text-align: center;
            width: 300%;
            line-height: 1;
            position: absolute;
            left: -100%;
        }

        @Media screen and (min-width:750px){
            #qrcode img{
                width:140px;
                height:140px;
            }
            .qrcode-item{
                transform: translate(20%,20%);
            }
        }
    </style>

    <div class="hideMenu" style="background:#000" ng-show="showFixedQrCode" ng-click="setShowFixedQrCode()">
        <div class="bg" style="background-image:url('/yzxfSeller_page/img/store_scan_code.jpeg');
            background-repeat: no-repeat;
            background-size: 100% auto;
            background-position: center center;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;"></div>
        <div style="position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            z-index: 1;
            display: flex;
            justify-content: center;
            align-items: center;">
            <div class="qrcode-item" >
                <div id="qrcode"></div>
                <div class="qrcode-title" ng-bind="loginInfo.name"></div>
            </div>
        </div>
        <%--<div class="receipt-top">--%>
            <%--<div>--%>
                <%--<div class="iconfont2 icon2-z-alipay"></div>--%>
                <%--<div>--%>
                    <%--<div>支 付 宝</div>--%>
                    <%--<div> A L I P A Y </div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<div class="iconfont2 icon2-wechat"></div>--%>
                <%--<div>--%>
                    <%--<div>微信支付</div>--%>
                    <%--<div>WechatPay</div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="receipt-middle">--%>
            <%--<div class="receipt-middle-qrcode">--%>
                <%--<div>--%>
                    <%--<div id="qrcode"></div>--%>
                    <%--<div class="qrcode-title" ng-bind="loginInfo.name"></div>--%>
                <%--</div>--%>
                <%--<img src="../img/receipt_1.png" />--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<div style="font-size: 2.4rem;">扫一扫支付</div>--%>
                <%--<div style="font-size:1.2rem;letter-spacing: 2px;">SCAN PAYMENT</div>--%>
                <%--<div style="font-size:1.2rem;letter-spacing: 2px;color: #666;">(支持信用卡付款)</div>--%>
                <%--<div style="line-height: 50px;font-size: 1.5rem;transform: scaleX(1.2);color: #C7000A;">支付获赠养老金</div>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="receipt-bottom">--%>
            <%--<img src="../img/receipt_3.png" style="margin-left: 5%;border-radius: 5px;"/>--%>
            <%--<div>--%>
                <%--<img src="../img/receipt_4.png" style="margin: 40px 10px;"/>--%>
                <%--<img src="../img/receipt_2.png" style="margin-left: 10%;--%>
                    <%--border-radius: 5px;--%>
                    <%--position: absolute;--%>
                    <%--right: 5px;--%>
                    <%--top: 4px;"/>--%>
            <%--</div>--%>
        <%--</div>--%>

    </div>
</div>