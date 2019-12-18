<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="home_factorInfo_Ctrl" class="d_content title_section form_section index_page_globalDiv ">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPage('/store/setting/userType/factor')"></span>
        服务站信息
    </div>
    <div class="overflowPC">
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <%-- ng-click="goPage('/store/updateName/userType/factor')"--%>
            <div class="mainRow rowHeight50">
                <div class="rowTitle">名称</div>
                <div class="rowInput textEllipsis" ng-bind="factorInfo.name"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
                <%-- ng-click="goPage('/store/storeIntroduction/userType/factor')"--%>
            <%--<div class="mainRow rowHeight50">--%>
                <%--<div class="rowTitle">简介</div>--%>
                <%--<div class="rowInput textEllipsis" ng-bind="factorInfo.intro"></div>--%>
                <%--&lt;%&ndash;<div class="icon-right-1-copy iconfont mainRowRight"></div>&ndash;%&gt;--%>
            <%--</div>--%>
            <%-- ng-click="goPage('/store/location/selectedArea/factorInfo')"--%>
            <div class="mainRow">
                <div class="rowTitle">区域</div>
                <div class="rowInput" ng-bind="factorInfo.area"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
                <%-- ng-click="goPage('/store/storeAddress/userType/factor')"--%>
            <div class="mainRow">
                <div class="rowTitle">街道地址</div>
                <div class="rowInput" ng-bind="factorInfo.address"></div>
                <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
            </div>
        </div>

        <%--<div class="sectionMain sectionMainNotMargin mainRowTop5">--%>
        <%--<div class="mainRow rowHeight50">--%>
        <%--<div class="rowTitle textSize15 widthPercent20H">交通信息</div>--%>
        <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
        <%--</div>--%>
        <%--</div>--%>

        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <div class="mainRow">
                <div class="rowTitle">银行卡号</div>
                <div class="rowInput" ng-bind="factorInfo.bankId"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">开户行</div>
                <div class="rowInput" ng-bind="factorInfo.bankName"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">户名</div>
                <div class="rowInput" ng-bind="factorInfo.bankUser"></div>
            </div>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">身份证</div>--%>
                <%--<div class="rowInput" ng-bind="factorInfo.bankUserCardId"></div>--%>
            <%--</div>--%>
            <%--<div class="mainRow">--%>
                <%--<div class="rowTitle">联系号码</div>--%>
                <%--<div class="rowInput" ng-bind="factorInfo.bankUserPhone"></div>--%>
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
        <div class="sectionMain sectionMainNotMargin mainRowTop5">
            <div class="mainRow">
                <div class="rowTitle">身份证</div>
                <div class="rowInput" ng-bind="factorInfo.realCard"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative">联系人身份证正面照:
                </div>
                <img class="iconImg7" ng-show="factorInfo.idCardImgFront!=null && factorInfo.idCardImgFront!=''"
                     ng-src="/s_img/icon.jpg?_id={{factorInfo.idCardImgFront}}&wh=650_0"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative">联系人身份证背面照:
                </div>
                <img class="iconImg7" ng-show="factorInfo.idCardImgBack!=null && factorInfo.idCardImgBack!=''"
                     ng-src="/s_img/icon.jpg?_id={{factorInfo.idCardImgBack}}&wh=650_0"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative">联系人身份证手持照:
                </div>
                <img class="iconImg7" ng-show="factorInfo.idCardImgHand!=null && factorInfo.idCardImgHand!=''"
                     ng-src="/s_img/icon.jpg?_id={{factorInfo.idCardImgHand}}&wh=650_0"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle positionRelative">
                    合同照片:
                </div>
                <div ng-repeat="imgMore in imgMore.contractImg">
                    <img class="iconImg7" ng-show="imgMore!=null && imgMore!=''"
                         ng-src="/s_img/icon.jpg?_id={{imgMore}}&wh=650_0"/>
                </div>
            </div>
        </div>
    </div>
</div>
