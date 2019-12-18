<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="other_other_Ctrl" class="d_content title_section form_section">
    <div class="title titleRedBottom">
        更多
        <span class="icon-share01 iconfont titleRight whitefff" ng-click="errorLoginPanel=true"></span>
    </div>

    <div class="sectionMain">
        <div class="mainRow" ng-click="goPage('/other/sellerApply')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/sellerApply.png">
                商户申请
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goPage('/other/factorApply')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/factorApply.png">
                服务站申请
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goPage('/other/agentApply')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/agentApply.png">
                代理商申请
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>

    <div class="sectionMain">
        <div class="mainRow" ng-click="goPage('/other/special')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/special.png">
                天天特价
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goPage('/other/commonweal')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/commonweal.png">
                公益专区
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goPage('/other/special/productType/hot')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/special.png">
                热门商品
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow" ng-click="goPage('/other/aboutUs')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/aboutUs.png">
                关于我们
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goPage('/other/agreeAll')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/aboutUs.png">
                相关协议
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow" ng-click="goPage('/other/appDownload')">
            <div class="rowTitle rowTitleBtn">
                <img src="/yzxfMember_page/img/aboutUs.png">
                APP下载
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>
    <div class="hideMenu" ng-show="errorLoginPanel">
        <div class="errorMain otherMain">
            <div class="otherMainRow textSize22">分享至</div>
            <div class="otherMainRow">
                <div class="icon-iconfontweixin iconfont limeGreen"></div>
                <div class="icon-friends iconfont"></div>
                <div class="icon-iconfontqq iconfont skyBlue"></div>
                <div class="icon-qqkongjian iconfont littleYellow"></div>
            </div>
            <div class="errorMainRow" ng-click="errorLoginPanel=false">
                <span class="errorMainBtn errorRed textSize22">取消</span>
            </div>
        </div>
    </div>
</div>