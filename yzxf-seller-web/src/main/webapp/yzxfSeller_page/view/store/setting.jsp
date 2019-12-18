<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="store_setting_Ctrl" class="d_content title_section form_section order_panel">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="showSet()?goPage('/store/store'):goPage('/home/index')"></span>
        设置
    </div>
    <div class="commentList">

        <div class="commentMain paddingBottomNot" ng-show="showSet()">
            <div class="headPortraitPanel">
                <div class="circularPhoto">
                    <img class="my_page_myHead_img" src="/yzxfSeller_page/img/notImg02.jpg"
                         ng-src="{{iconImgUrl(getSellerIcon(sellerInfo.icon,sellerInfo.doorImg))}}"/>
                    <input type="file" name="file" class="my_page_myHead_img"
                    onchange="angular.element(this).scope().uploadFile(this,'icon')"/>
                </div>
            </div>
            <div class="commentContent">
                <div class="overflowHidden textIndent30">
                    <span class="floatLeft textSize15 gray222" ng-bind="sellerInfo.name==null?'无':sellerInfo.name"></span>

                </div>
                <div class="overflowHidden lineHeight50 textIndent30 ">
                    <span class="gray222" ng-bind="'ID:'+sellerInfo._id"></span>
                </div>
            </div>
        </div>
        <div class="commentMain paddingBottomNot" ng-show="!showSet()">
            <div class="headPortraitPanel">
                <div class="circularPhoto">
                    <img class="my_page_myHead_img" src="/yzxfSeller_page/img/notImg02.jpg" ng-src="{{iconImgUrlFactor()}}"alt="userHead"/>
                    <input type="file" name="file" class="my_page_myHead_img"
                           onchange="angular.element(this).scope().uploadFile(this,'icon')"/>
                </div>
            </div>
            <div class="commentContent">
                <div class="overflowHidden textIndent30">
                    <span class="floatLeft textSize15 gray222" ng-bind="factorInfo.name==null?'无':factorInfo.name"></span>

                </div>
                <div class="overflowHidden lineHeight50 textIndent30 ">
                    <span class="gray222" ng-bind="'ID:'+factorInfo._id"></span>
                </div>
            </div>
        </div>

    </div>

    <div class="sectionMain">
        <div class="mainRow rowHeight50" ng-click="goPage('/store/sellerInfo')" ng-show="showSet()">
            <div class="rowTitle">
                <span class="icon-wxbmingxingdianpu iconfont"></span>
                <span class="rowTitleBtnText">店铺信息</span>
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow rowHeight50" ng-click="goPage('/home/factorInfo')" ng-show="!showSet()">
            <div class="rowTitle">
                <span class="icon-wxbmingxingdianpu iconfont"></span>
                <span class="rowTitleBtnText">服务站信息</span>
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <div class="mainRow rowHeight50" ng-click="goPage('/store/modifyPassword/userType/'+userType)">
            <div class="rowTitle">
                <span class="icon-mima iconfont"></span>
                <span class="rowTitleBtnText">密码修改</span>
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
        <%--<div class="mainRow rowHeight50">--%>
            <%--<div class="rowTitle">--%>
                <%--<span class="icon-i iconfont"></span>--%>
                <%--<span class="rowTitleBtnText">当前版本号</span>--%>
                <%--<span class="rowInput gray222 mainRowLeft10">V0.01</span>--%>
            <%--</div>--%>
            <%--<div class="icon-right-1-copy iconfont mainRowRight"></div>--%>
        <%--</div>--%>
    </div>

    <div class="sectionMain">
        <div class="mainRow rowHeight50" ng-click="goPage('/store/aboutUs')">
            <div class="rowTitle">
                <span class="icon-guanyu iconfont"></span>
                <span class="rowTitleBtnText">关于我们</span>
            </div>
            <div class="icon-right-1-copy iconfont mainRowRight"></div>
        </div>
    </div>

    <div class="BtnMain mainBtn">
        <div class="mainRow submitBtnTextCenter textSize18 bgBlue whitefff radius5" ng-click="logout()">
            退出登录
        </div>
    </div>
</div>