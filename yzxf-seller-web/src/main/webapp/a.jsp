<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="phonecat">
<head>
    <%
        pageContext.setAttribute("serverVersion", "0.978");
    %>
    <script>
        window.angular_temp_version = ${serverVersion};
    </script>

    <script charset="utf-8" src="https://map.qq.com/api/js?v=2.exp"></script>

    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="0">
    <%--<meta name="viewport"--%>
          <%--content="width=device-width, initial-scale=1.0, user-scalable=no"/>--%>

    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1"/>
    <link rel="stylesheet" href="./css/i_common.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/icomoon/style.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/iconfont/iconfont.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/aliFontTwo/iconfont.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_layout.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_date.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_file.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_form.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_popWin.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_grid.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/i_htmlEditor.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/admin/index.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/admin/general.css?_v=${serverVersion}"/>
    <link rel="stylesheet" href="./css/admin/eventInfo.css?_v=${serverVersion}"/>
    <link rel="stylesheet" type="text/css" href="/css/aliFontTwo/iconfont.css?_v=${serverVersion}"/>
</head>
<body ng-controller="allBodyCtrl" ng-mousedown="clickAllBody();">
<!-- 过渡层 -->
<div id="allLoadingCon" class="loadingAllCon"
     style="background-color: rgb(255, 255, 255); position: fixed; z-index: 10000; top: 0; left: 0; right: 0; bottom: 0; text-align: center; padding-top: 100px;">
    <!-- img src="/member_page/img/logo.png" -->
    <div class="title" style="color: #888; text-shadow: 0 0 2px #888;" ng-bind="site.title">加载中...</div>
</div>

<div class="loginMark" ng-show="showLogin"
     style="position: absolute; z-index: 1111; top: 0; left: 0; right: 0; bottom: 0; text-align: center; padding-top: 100px;">
</div>
<div id="loginCon" class="loadingAllCon" ng-show="showLogin"
     style=" position: absolute; z-index: 1112; top: 0; left: 0; right: 0; bottom: 0; text-align: center; padding-top: 100px;">
    <!-- img src="/member_page/img/logo.png" -->
    <form class="mainForm" ng-submit="doLogin();" style="">
        <div style="padding-bottom: 20px;font-size:18px;">
            <img src="/yzxfSeller_page/img/logoIndex.png" style="width: 150px;"/>
        </div>
        <div class="row" ng-if="loginFormType=='reg'">
            <div class="field notHigh">姓 名:</div>
            <div class="input">
                <input ng-model="login.name" required="required"/>
            </div>
        </div>
        <div class="row">
            <div class="field" style="color:#fff">用户名:</div>
            <div class="input">
                <input ng-model="login.loginName" required="required" style="width:158px;"/>
            </div>
        </div>
        <div class="row">
            <div class="field" style="color:#fff">密 码:</div>
            <div class="input">
                <input type="password" ng-model="login.password" required="required" style="width:158px;"/>
            </div>
        </div>
        <div class="row" style="padding-top: 10px;">
            <div class="field notHigh"></div>
            <div class="input">
                <button type="submit" class="button "
                        style="width:155px;background-color:#1480b7;color:#fff;border:0;height:30px;">
                    {{loginFormType=='reg'?'注册新用户':'登录'}}
                </button>
            </div>
        </div>
    </form>
</div>
<div class="layout" id="layout_all_main" ng-show="!$scope.showLogin">
    <div class="topCon">
        <%--<div class="fl siteName index_top" ng-click="goPage2('#/home/index')">--%>
        <%--{{site.name}}1--%>
        <%--</div>--%>
        <img class="fl siteName index_top" src="yzxfSeller_page/img/logoIndex.png" ng-click="goPage2(goPageDefault)"/>
        <div class="fr frSpan navBtn" style="text-align: right;">
            <span style="color:#fff;" ng-bind="agent.name" ng-click="modifyMyInfo()"></span>
            <span ng-bind="getAgentLevel(agent.level)" ng-show="agent.level!=null && agent.level!=''"
                  style="background: #fff;border-radius: 5px;color: #138bbe;font-size: 12px;padding: 2px 2px;"></span>
            <span style="color:#fff;" ng-click="showPwdWindow()">密码修改</span>
            <span class="iconfont icon-youxiang win1-hover" style="font-size: 25px;position:relative;padding: 10px 0;"
                  ng-click="goPage2('#/pending/pending')" ng-show="agent.adminType==0">
                <div class="iconImg1" ng-show="pendTotal>0"></div>
                <div class="win1" ng-show="pendTotal>0">
                    <div>您有
                        <span ng-bind="pendTotal" class="fontNum1 font20px"></span>
                        条新的申请
                    </div>
                    <div ng-show="userPend.sellerCountSecond>0">
                        <span class="fontNum1" ng-bind="userPend.sellerCountSecond"></span>
                        <span> 条商户复审审核申请</span>
                    </div>
                    <div ng-show="userPend.factorCountSecond>0">
                        <span class="fontNum1" ng-bind="userPend.factorCountSecond"></span>
                        <span> 条发卡点复审审核申请</span>
                    </div>
                    <div ng-show="userPend.agentCountSecond>0">
                        <span class="fontNum1" ng-bind="userPend.agentCountSecond"></span>
                        <span> 条代理商复审审核申请</span>
                    </div>
                    <%--<div ng-show="withdrawPend.withdrawCount>0">--%>
                    <%--<span class="fontNum1" ng-bind="withdrawPend.withdrawCount"></span>--%>
                    <%--<span> 条提现申请</span>--%>
                    <%--</div>--%>
                </div>
                <div class="win1" ng-show="pendTotal==0">
                    <div>您最近暂无新的申请请求</div>
                </div>
            </span>
            <span class="button high" ng-click="doLogout()">退出</span>
        </div>

    </div>
    <div class="left" style="width:190px;bottom:0;top:50px;left:0;background: #fff;border-right: 1px solid #ccc;"
         id="leftMenu">
        <div class="item menuLeft" ng-repeat="item in menus" ng-hide="item.hide">
            <div class="title" ng-click="clickLeftMenu(item);clickLeftSelected($index)"
                 ng-class="menus.__selectMenuId==item._id&&menus.__openMenu?'open':''"
                 style="font-size: 16px;height: 36px;line-height: 36px;border-bottom: 1px solid #ccc; background-color: #efefef;">
                <span class="iconfont notHigh" ng-class="item.icon"></span>
                <span ng-bind="item.name"></span>
                <span class="nextCon iconfont " ng-class="leftSelected==$index?'nextCon2 icon-iconfontarrows':'icon-right-1-copy'"></span>
            </div>
            <div class="items menuLeftChild"
                 ng-show="menus.__selectMenuId==item._id&&menus.__openMenu&&(!citem.adminAccesss||(citem.adminAccesss&&agent.level==1))"
                 ng-repeat="citem in item.items "
                 ng-click="doMenuAction(citem,citem.url==null?(item._id+'/'+citem._id):citem.url)">
                <a href="javascript:void(0)" style="text-decoration: none"
                   ng-click="doMenuAction(citem,citem.url==null?(item._id+'/'+citem._id):citem.url)"
                   ng-class="citem.__selected?'selected':''">{{citem.name}}</a>
            </div>
        </div>
    </div>
    <div class="center center2">
        <div ng-view style="width: 100%; height: 100%;">
        </div>
    </div>

    <div ng-show="pwdWindow" class="winCon">
        <div class="content" ng-class="winCheck?'content2':'content1'">
            <div class="contentTitle">密码修改</div>
            <div class="close1" ng-click="closePwdWin()">
                <div class="close1-icon iconfont icon-plus"></div>
            </div>
            <div class="close1 iconfont icon-daifahuo" style="right:40px;" ng-click="winCheck=!winCheck"></div>
            <div class="include">
                <div class="popSection flex2">
                    <div class="popTitle">密码修改</div>
                    <form ng-submit="modifyPwd(oldPwd,newPwd,newSPwd)" style="width: 100%;text-align: center">
                        <div style="width:100%;float: left;" class="lineH40px">
                            <span>旧密码:</span>
                            <input type="text" ng-model="oldPwd" placeholder="请输入旧密码"/>
                        </div>
                        <div style="width:100%;margin: 0 auto;float: left" class="lineH40px">
                            <span>新密码:</span>
                            <input type="text" ng-model="newPwd" placeholder="请输入新密码"/>
                        </div>
                        <div style="width:100%" class="lineH40px">
                            <span>确认密码:</span>
                            <input type="text" ng-model="newSPwd" placeholder="请再次输入新密码"/>
                        </div>
                        <div style="width: 100%;padding: 10px 0">
                            <div style="width:25%;margin: 0 auto">
                                <button class="btn1 bgBlue" type="submit">提交</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%--不显示,临时放置鼠标悬停位置的td的文字,用来为td标签的提示框做文字的长度判断--%>
<span style="display:none;font-size:13px" id="checkTdWidth"></span>

<div class="winCon" ng-show="showMyInfo">
    <div class="content" ng-class="winCheck?'content2':'content1'">
        <div class="contentTitle">当前登录用户信息</div>
        <div class="close1" ng-click="closePopWin()">
            <div class="close1-icon iconfont icon-plus"></div>
        </div>
        <div class="close1 iconfont icon-daifahuo" style="right:40px;" ng-click="winCheck=!winCheck"></div>
        <div class="include" ng-include="winMyInfo"></div>
    </div>
</div>

<div class="sectionHintBk" ng-show="tool.isShow" style="z-index:9999">
    <div class="sectionHint">
        <div class="lineH50px textCenter colorBlue1" ng-bind="tool.title"></div>
        <div class="lineH50px" style="font-size:14px" ng-bind="tool.desc">请核对订单详细信息</div>
        <div class="flex1">
            <button class="btn1" ng-click="tool.exec()">确认</button>
            <button class="btn1 bkColorRed1" ng-click="tool.cancel()">取消</button>
        </div>
    </div>
</div>

<!-- pop window list -->
<%--<%@include file="./temp/i_popwinList.jsp"%>--%>
<div class="mark" ng-repeat="item in popWindows"
<%--ng-click="item.close();" --%>
     ng-style="{zIndex:$index+10}"></div>
<div pop-win index="{{$index}}"
     id="{{item.name}}"
     ng-style="{zIndex:$index+10,width:item.winWidth,height:winHeight}"
     hide-top="{{item.hideTop}}"
     hide-down="{{item.hideDown}}"
     is-pop="true"
     win-width="{{item.width}}"
     win-height="{{item.height}}"
     win-title="{{item.title}}"
     win-temp="{{item.temp}}"
     ng-repeat="item in popWindows"></div>

<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/angular${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/getBrowserInfo.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/angular-route${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/script${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/extend-1.0.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/init_websocket.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/circliful.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="./a_app.js"></script>

<script type="text/javascript" src="./js/directive/layout.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/base.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/date.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/file.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/form.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/popWin.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/tree.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/directive/htmlEditor.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/profit/highcharts.js?_v=${serverVersion}"></script>

<script type="text/javascript" src="./js_common/filterTime.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js_common/grid_temp.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/user/t_seller_modify_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/user/t_agent_modify_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/user/t_factor_modify_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/user/t_member_modify_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/account/t_income_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/account/t_trade_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/insure/t_insure_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/user/t_member_team_grid.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/user/t_seller_gpayBindECP.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./view_js/account/t_trade_balanceCheck_grid.js?_v=${serverVersion}"></script>

</body>
</html>