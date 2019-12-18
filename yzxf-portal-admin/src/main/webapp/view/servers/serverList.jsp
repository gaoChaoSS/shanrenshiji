<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .serverList > .item {
        font-size: 50px;
        text-shadow: 0 0 4px #ddd;
        color: #888;
        padding: 20px;
        margin: 10px;
        border-radius: 6px;
        text-align: center;
    }

    .serverList > .item > .title {
        padding: 5px;
        font-size: 15px;
    }

    .serverList > .item.online {
        color: green;
    }

    .serverList > .item:hover {
        background-color: #eee;
    }

    .showInfo > .title {
        padding: 15px 3px 5px;
        font-weight: bold;
    }

    .keyTitle {
        padding: 8px 0 2px;
        margin: 0 10px;
        border-bottom: 1px solid #ddd;
        font-size: 18px;
    }
</style>

<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div style="padding:5px;background-color: #eee;border-bottom: 1px solid #ccc;overflow: hidden">
        <div class="fl">
            <span ng-if="conType=='list'">服务器列表</span>
            <span ng-if="conType=='info'"><a href="javascript:void(0);" ng-click="showList();">服务器列表</a></span>
            <span ng-if="conType=='info'">&gt;</span>
            <span ng-if="conType=='info'" class="notHigh" ng-bind="info.$$title"></span>
        </div>
        <div class="fr" ng-if="conType=='info'">
            <span ng-class="info.online?'green':'high'" ng-bind="info.online?'已连接':'已断开'"></span>
            <span class="button icon-loop2"> 刷新</span>
        </div>
    </div>
    <div ng-show="conType=='list'">
        <div>
            <div class="keyTitle">中央服务器</div>
            <div class="serverList">
                <div class="fl item icon-database online" ng-click="showInfo(commonServer);">
                    <div class="title" ng-bind="commonServer.name"></div>
                    <div ng-bind="commonServer.host+':'+commonServer.port"></div>
                </div>
                <div class="clearDiv"></div>
            </div>
        </div>

        <div ng-repeat="(key,value) in serverMap">
            <div ng-bind="key"
                 style="" class="keyTitle"></div>
            <div class="serverList">
                <div class="fl item icon-database" ng-click="showInfo(item);" ng-class="item.online?'online':''"
                     ng-repeat="item in value">
                    <div class="title" ng-bind="item.name"></div>
                    <div ng-bind="item.host+':'+item.port"></div>
                </div>
                <div class="clearDiv"></div>
            </div>
        </div>
    </div>
    <div ng-show="conType=='loading'" class="notHigh" style="padding:40px;text-align: center">
        加载中...
    </div>
    <div class="showInfo" ng-show="conType=='info'" style="padding:6px;">
        <div class="title">基本信息</div>
        <table>
            <tr>
                <td class="hearderTd1" style="width:150px;">类型</td>
                <td ng-bind="info.type"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;">名称</td>
                <td ng-bind="info.name"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;">服务器地址</td>
                <td ng-bind="info.host+' : '+info.port"></td>
            </tr>
        </table>
        <div class="title" ng-if="info.type=='service'">数据库信息</div>
        <table ng-if="info.type=='service'">
            <tr>
                <td class="hearderTd1" style="width:150px;">数据库地址</td>
                <td ng-bind="info.db.jdbcUrl"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;">驱动程序</td>
                <td ng-bind="info.db.driverClass"></td>
            </tr>

            <tr>
                <td class="hearderTd1" style="width:150px;vertical-align: top">数据库表</td>
                <td>
                    <div class="button iconfont" style="width:80px;text-align: center;"
                         ng-click="tablesShow=!tablesShow"
                         ng-bind="(tablesShow?'隐藏':'显示')"
                         ng-class="!tablesShow?'icon-right':'icon-unfold'"></div>
                    <table ng-repeat="t in info.entityTable" style="margin: 10px 0;" ng-if="tablesShow">
                        <tr style="background-color: #eee;">
                            <td class="icon-table" style="font-weight: bold;font-size:16px;padding:8px 2px;"
                                ng-bind="t.name"></td>
                            <td style="font-weight: bold" ng-bind="t.title"></td>
                            <td ng-bind="t.sellerOwner?'商家关联表':''"></td>
                            <td ng-bind="t.memberOwner?'会员关联表':''"></td>
                            <td></td>
                        </tr>
                        <tr style="background-color: #eee;text-align: center">
                            <td>字段</td>
                            <td>标题</td>
                            <td>类型</td>
                            <td>最小长度</td>
                            <td>最大长度</td>
                        </tr>
                        <tr ng-repeat="item in info.entityTableFieldList[t.name]">
                            <td class="icon-list" ng-bind="item.name"></td>
                            <td ng-bind="item.title">标题</td>
                            <td ng-bind="item.type">类型</td>
                            <td class="green" ng-bind="item.minLength">最小长度</td>
                            <td class="green" ng-bind="item.maxLength">最大长度</td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>

        <%--<div class="title">API信息</div>--%>
        <%--<table>--%>
        <%--</table>--%>
        <div class="title"> 连接信息</div>
        <table>
            <tr ng-if="info.name!='common'">
                <td class="hearderTd1" style="width:150px;">中央服务器</td>
                <td class="icon-database" ng-bind="info.commonServer"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;">Redis服务器
                </td>
                <td class="icon-database" ng-bind="info.redis"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;"><i class="icon-arrow-right2 notHigh"></i> 连接的模块
                </td>
                <td>
                    <div ng-repeat="(key,item) in info.linkTo"
                         style="padding:4px 8px;margin: 4px 0;background-color: #eee;border-radius: 4px;overflow: auto">
                        <div class="icon-database fl" style="font-weight: bold;padding:6px;" ng-bind="key"></div>
                        <div class="fl green" style="padding:5px;" ng-bind="item"></div>
                        <div class="fl" style="padding:0 8px;">
                            <button class="button">详情 &gt;</button>
                        </div>
                    </div>

                </td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;"><i class="icon-arrow-left2 notHigh"></i> 被连接的模块
                </td>
                <td style="overflow:auto;">
                    <div ng-repeat="(key,item) in info.linkMe"
                         style="padding:4px 8px;margin: 4px 0;background-color: #eee;border-radius: 4px;overflow: auto">
                        <div class="icon-database fl" style="font-weight: bold;padding:6px;" ng-bind="key"></div>
                        <div class="fl" style="padding:0 8px;" ng-repeat="hostItem in item">
                            <span class="green" ng-bind="hostItem.host+':'+hostItem.port"></span>
                            <button class="button">详情 &gt;</button>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>