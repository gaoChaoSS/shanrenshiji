<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="store_sellerInfo_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/setting/userType/seller')"></span>
        商户信息
    </div>
    <div class="overflowPC">
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <%--ng-click="goPage('/store/updateName/userType/seller')"--%>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">商家名字</div>
                <div class="rowInput textEllipsis" ng-bind="storeInfo.name"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
            <%--ng-click="goPage('/store/storeIntroduction/userType/seller')"--%>
            <div class="mainRow">
                <div class="rowTitle">商家简介</div>
                <div class="rowInput" style="line-height: 25px;padding: 14px 0;" ng-bind="storeInfo.intro"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
                <%-- ng-click="goPage('/store/storePhone')"--%>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">联系方式</div>
                <div class="rowInput textEllipsis" ng-bind="storeInfo.phone"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
                <%-- ng-click="goPage('/store/storeServerPhone')"--%>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">客服电话</div>
                <div class="rowInput textEllipsis" ng-bind="storeInfo.serverPhone"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
        </div>

        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <%-- ng-click="goPage('/store/operateType')"--%>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">经营范围</div>
                <div class="rowInput textEllipsis" ng-bind="storeInfo.operateType"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
                <%-- ng-click="goPage('/store/storeBusiness/openTime/'+storeInfo.openTime+'_'+storeInfo.closeTime+'/openWeek/'+storeInfo.openWeek)"--%>
            <div class="mainRow" style="overflow: hidden">
                <div class="rowTitle">营业时间</div>
                <div class="rowInput" ng-bind="businessTime"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
        </div>


        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <%--<div class="mainRow rowHeight50">--%>
            <%--<div class="rowTitle textSize15 widthPercent20H">商户信息</div>--%>
            <%--<div class="rowInput rowInputSI black textSize15 textEllipsis" ng-bind="storeInfo.sellerInfo"></div>--%>
            <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            <%--</div>--%>
            <%--<div class="mainRow rowHeight50">--%>
            <%--<div class="rowTitle textSize15 widthPercent20H">商家折扣</div>--%>
            <%--<div class="rowInput rowInputSI black textSize15 textEllipsis" ng-bind="storeInfo.discount"></div>--%>
            <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            <%--</div>--%>
                <%-- ng-click="goPage('/store/location/selectedArea/sellerInfo')"--%>
            <div class="mainRow">
                <div class="rowTitle">商家区域</div>
                <div class="rowInput" ng-bind="storeInfo.area"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
                <%-- ng-click="goPage('/store/storeAddress/userType/seller')"--%>
            <div class="mainRow">
                <div class="rowTitle">街道地址</div>
                <div class="rowInput" ng-bind="storeInfo.address"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
            <div class="mainRow" ng-click="initMap()">
                <div class="rowTitle">经纬度</div>
                <div class="rowInput">
                    <div ng-bind="storeInfo.longitude"></div>
                    <div ng-bind="storeInfo.latitude"></div>
                </div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
        </div>

        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <div class="mainRow">
                <div class="rowTitle">银行卡号</div>
                <div class="rowInput" ng-bind="storeInfo.bankId"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">开户行</div>
                <div class="rowInput" ng-bind="storeInfo.bankName"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">户名</div>
                <div class="rowInput" ng-bind="storeInfo.bankUser"></div>
            </div>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">身份证</div>--%>
                <%--<div class="rowInput" ng-bind="storeInfo.bankUserCardId"></div>--%>
            <%--</div>--%>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">联系号码</div>--%>
                <%--<div class="rowInput" ng-bind="storeInfo.bankUserPhone"></div>--%>
            <%--</div>--%>
            <div class="mainRow">
                <div class="rowTitle positionRelative">
                    银行卡正反面/开户许可证:
                </div>
                <div ng-repeat="imgMore2 in imgMore.bankImg">
                    <img class="iconImg7" ng-show="imgMore2!=null && imgMore2!=''"
                         ng-src="/s_img/icon.jpg?_id={{imgMore2}}&wh=650_0"/>
                </div>
            </div>
        </div>

        <%--<div class="sectionMain sectionMainNotMargin mainRowTop5">--%>
            <%--<div class="mainRow rowHeight50">--%>
                <%--<div class="rowTitle textSize15 widthPercent20H">交通信息</div>--%>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            <%--</div>--%>
        <%--</div>--%>
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <%-- ng-click="goPage('/store/storeLegalPerson')"--%>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">法人信息</div>
                <div class="rowInput textEllipsis" ng-bind="storeInfo.legalPerson"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">身份证</div>
                <div class="rowInput textEllipsis" ng-bind="storeInfo.realCard"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative"> 营业执照:</div>
                <img class="iconImg7"  ng-show="storeInfo.businessLicense!=null && storeInfo.businessLicense!=''"
                     ng-src="/s_img/icon.jpg?_id={{storeInfo.businessLicense}}&wh=650_0"/>
            </div>

            <div class="mainRow">
                <div class="rowTitle positionRelative">法人身份证正面照:</div>
                <img class="iconImg7"  ng-show="storeInfo.idCardImgFront!=null && storeInfo.idCardImgFront!=''"
                     ng-src="/s_img/icon.jpg?_id={{storeInfo.idCardImgFront}}&wh=650_0"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative">法人身份证背面照:</div>
                <img class="iconImg7"  ng-show="storeInfo.idCardImgBack!=null && storeInfo.idCardImgBack!=''"
                     ng-src="/s_img/icon.jpg?_id={{storeInfo.idCardImgBack}}&wh=650_0"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative">法人身份证手持照:</div>
                <img class="iconImg7"  ng-show="storeInfo.idCardImgHand!=null && storeInfo.idCardImgHand!=''"
                     ng-src="/s_img/icon.jpg?_id={{storeInfo.idCardImgHand}}&wh=650_0"/>
            </div>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle positionRelative">--%>
                    <%--合同照片:--%>
                <%--</div>--%>
                <%--<div ng-repeat="imgMore in imgMore.contractImg">--%>
                    <%--<img class="iconImg7" ng-show="imgMore!=null && imgMore!=''"--%>
                         <%--ng-src="/s_img/icon.jpg?_id={{imgMore}}&wh=650_0"/>--%>
                <%--</div>--%>
            <%--</div>--%>
            <div class="mainRow">
                <div class="rowTitle positionRelative">
                    店铺门头/收银照:
                </div>
                <div ng-repeat="doorImg in imgMore.doorImg">
                    <img class="iconImg7" ng-show="doorImg!=null && doorImg!=''"
                         ng-src="/s_img/icon.jpg?_id={{doorImg}}&wh=650_0"/>
                </div>
            </div>
        </div>
    </div>
    <div ng-show="showMap" class="hideMenu" style="background:#fff">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff" ng-click="showMap=false"></span>
            位置经纬度
        </div>
        <div id="container" style="height:500px;margin-top: 10px;"></div>
    </div>
</div>