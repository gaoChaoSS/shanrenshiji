<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="help_help_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
        <%--中间内容--%>
        <div class="bodyWidth marginZAuto" style="overflow: hidden;">
            <%--左边导航--%>
            <div class="floatL helpPageLeftNavDiv">
                <div class="helpNavRowDiv" ng-repeat="menu in menuList">
                    <span><span class="iconLingxing">◆ </span><span ng-bind="menu.name"></span></span>
                    <ul>
                        <li class="helpNavLi" ng-repeat="menu2 in menu.items" ng-bind="menu2.name"
                            ng-class="selectedHelpMenu._id===menu2._id?'helpNavLiClick':''"
                            ng-click="selectPage(menu2)"></li>
                    </ul>
                </div>
            </div>
            <%--右边内容--%>
            <div class="floatR helpPageRightContent">
                <div class="helpContentTitle flex1" ng-bind="curArt.title"></div>
                <div class="helpContentText">
                    <div class="contentPText" ng-repeat="content in curArt.contents">
                        <div class="contentT" ng-if="content.type===0" ng-bind="content.desc"></div>
                        <img ng-if="content.type===1" src="{{iconImg(content.desc)}}" style="display:block;margin:0 auto"/>
                    </div>
                </div>
            </div>
        </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
</div>