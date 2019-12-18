<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_my_Ctrl" class="d_content form_section title_section">

    <div class="page-top1">
        <div class="icon2-saoma iconfont2 page-top1-right" style="left:4%;right:auto;line-height: 1;" ng-click="scanQrCode()"></div>
        <div class="icon-shezhi iconfont page-top1-right" ng-click="goPage('/my/settings')"></div>
    </div>
    <div class="page-narrow1">
        <div class="mod-up1">
            <img class="mod-circleImg1" err-src="/yzxfMember_page/img/notImg02.jpg"
                 ng-src="{{iconImgUrl(myInfo.icon)}}" ng-click="goPage('my/my2DBarcode')">
            <div class="mod-up1-text" ng-bind="loginName"></div>
            <div class="mod-up1-right" ng-click="goPage('/my/realName')">
                <span class="iconfont2 icon2-huiyuan" ng-show="myInfo.isRealName"></span>
                <span ng-bind="myInfo.isRealName?'已实名认证':'未实名认证'"></span>
                <span class="icon-right-1-copy iconfont"></span>
            </div>
            <div class="mod-up2-text" ng-bind="cardNo"></div>
            <div class="mod-up2-right">
                <span class="iconfont2 icon2-huiyuan" ng-show="myInfo.isBindCard"></span>
                <span ng-bind="myInfo.cardNo?'已激活养老金补充卡':'未激活养老金补充卡'"></span>
            </div>
        </div>

        <div class="mod-title2 mainRowTop20"  ng-show="showText!=null && showText!=''">
            <div class="textCenter lineHeight30 grayaaa textSize12">
                <%--注: 保单的情况会通过邮件通知--%>
                <span ng-bind="showText" ng-click="goPage(goPageText)" class="colorBlue"></span>
            </div>
        </div>

        <div class="mod-title2 mainRowTop20" ng-click="goPage('/my/order/headerIndex/-1')">
            <div class="fl">我的订单</div>
            <div class="fr">
                查看所有
                <span class="icon-right-1-copy iconfont gray888"></span>
            </div>
        </div>
        <div class="topMenu topMenu5">
            <div ng-repeat="head in headerList" ng-click="goPage('/my/order/headerIndex/'+$index)">
                <div class="{{head.icon}} iconfont2" style="color:#138bbe;font-size: 30px;"></div>
                <div class="topMenu-tap2" ng-bind="head.count" ng-show="head.count!=null"></div>
                <div ng-bind="head.name"></div>
            </div>
        </div>

        <div class="mod-title2 mainRowTop20">
            <div class="fl">其他功能</div>
        </div>
        <div class="topMenu">
            <%--<div ng-click="goPage('/my/payBySeller')">--%>
                <%--<div class="iconfont2 icon2-zhifu" style="color:#3E9AEE"></div>--%>
                <%--<div>余额付款</div>--%>
            <%--</div>--%>
            <div ng-repeat="item in otherList" ng-click="item.fun?item.fun():goPage(item.page)">
                <div ng-class="item.iconfont" ng-style="{'color':item.color}"></div>
                <div ng-bind="item.name"></div>
            </div>
        </div>
    </div>

    <%--<div class="hideMenu" ng-show="errorLoginPanel">--%>
        <%--<div class="errorMain">--%>
            <%--<div class="errorMainRow">将访问你的相机</div>--%>
            <%--<div class="errorMainRow">--%>
                <%--<div class="errorMainBtn black" ng-click="errorLoginPanel=false">取消</div>--%>
                <%--<div class="errorMainBtn black">确定</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div class="hideMenu" ng-show="errorPension">
        <div class="errorMain">
            <div class="errorMainRow">您没有绑定会员卡,请绑定</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="errorPension=false">取消</div>
                <div class="errorMainBtn black" ng-click="memberProtocol=true">前往绑定</div>
            </div>
        </div>
    </div>
    <div class="hideMenu" ng-show="memberProtocol">
        <div class="errorMain memberMain">
            <div class="errorMainRow">会员协议</div>

            <div class="errorMainRow content" style="height: 299px;
    overflow: auto;
    text-align: left;
    padding: 6px 10px;">
                <p>1. 会员有权根据本协议的约定，以及普惠生活养老平台网站上发布的相关规则在普惠生活养老平台上询商品信息、
                    订购具体商品、查询物流信息、提供或接受物流服务、发表使用体验、参与品讨论、物流体验、
                    参加普惠生活养老平台网站的有关活动，以及使用普惠生活养老平台提供的其他服务。
                </p>
                <p> 2. 会员应当保证在出售/购买商品、
                    提供/接受物流运输服务过程中遵守诚实信用原则。
                    不扰乱网上交易的正常秩序。 </p>
                <p> 3. 会员同意严格遵守以下义务：</br>
                    ①不得传输或发表：煽动抗拒、破坏宪法和法律、行政法规实施的言论，煽动颠覆国家政权。</br>
                    推翻社会主义制度的言论，煽动分裂国家、破坏国家统一的言论，煽动民族仇恨、民族歧视、破坏民族团结的言论。</br>
                    ②不得利用普惠生活养老平台从事洗钱、窃取商业秘密、窃取个人信息等违法犯罪活动.</br>
                    ③不得捏造或者歪曲事实，散步谣言，扰乱社会秩序。</br>
                    ④不得传输或发表任何封建迷信、邪教、淫秽、色情、赌博、暴力、恐怖、教唆犯罪等不文明的信息资料。</br>
                    ⑤不得公然侮辱他人或者捏造事实诽谤他人或者进行其他恶意攻击。</br>
                    ⑥其他违反宪法和法律、行政法规规定的。</br>
                </p>
                <p>
                    4. 未经普惠生活养老平台书面同意，会员不得在普惠生活养老平台网站上发布任何形式的广告。</br></p>
                <p>
                    5. 遵守普惠生活养老平台制定的规则。</br>
                </p>
            </div>
            <div class="errorMainRow rowBorderTop">
                <div class="errorMainBtn black" ng-click="memberProtocol=false;errorPension=false">返回</div>
                <div class="errorMainBtn black" ng-click="errorPension=false;getIsRealName()">同意</div>
            </div>
        </div>
    </div>
    <div class="hideMenu overflowHidden flexMod1" ng-show="myCard" ng-click="myCard=false">
        <div class="windowPanel1">
            <img class="headImg2" err-src="/yzxfMember_page/img/notImg02.jpg"
                 ng-src="{{iconImgUrl(myInfo.icon)}}"/>
            <div class="mainRow3">
                <div ng-bind="isNullText2(myInfo.realName)" class="textEllipsis"></div>
                <div class="mainRowTop35">
                    <div class="iconfont2 icon2-huiyuan fl lineHeight20"></div>
                    <div class="mainRowLeft20" ng-class="isShowPensionTap?'':'grayccc'" ng-bind="iconText"></div>
                </div>
            </div>
            <div class="qrcode"></div>
            <div class="textCenter mainRowTop10">扫描上方二维码图案</div>
            <div class="textCenter">查看我的信息</div>
        </div>
    </div>
</div>