<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" >
<head>
    <%
        pageContext.setAttribute("serverVersion", "0.21");
    %>
    <script>
        window.angular_temp_version = ${serverVersion};
    </script>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1"/>

    <style>
        *{padding:0;margin:0}
        *, *:before, *:after {
            -moz-box-sizing: border-box;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
        }
        a{text-decoration: none}
        .fr{float:right}
        .fl{float:left}
        .hd{overflow:hidden}
        .center{text-align: center}
        .cursor{cursor:default!important;}
        .nav{
            position:fixed;
            top:0;
            left:0;
            width:100%;
            height:40px;
            padding:0 50px;
            background:#555;
            z-index:1;
        }
        .nav div{
            float:left;
            padding:0 30px;
            height:40px;
            line-height:40px;
            color:#fff;
            font-size:16px;
            transition:0.5s all;
            cursor:pointer;
        }
        .nav div:hover{
            background:#777;
        }
        .nav div:first-child{
            cursor:default;
        }
        .nav div:first-child:hover{
            background:#555;
        }
        .navSelected{
            background:#888;
        }

        .leftNav{
            position:absolute;
            top:40px;
            left:0;
            width:200px;
            height:calc(100% - 40px);
            overflow-x:hidden;
            /*overflow-y:auto;*/
            background:#eee;
            overflow-y:visible;
        }
        .leftNav-title{
            text-align:center;
            height:30px;
            line-height:30px;
            color:#888;
        }
        .leftNav-item,.leftNav-item2{
            display:block;
            width:200px;
            height:25px;
            line-height:25px;
            background:#555;
            color:#fff;
            font-size:13px;
            margin:1px 0;
            padding:0 5px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            cursor:pointer;
            transition:0.5s all;
        }
        .leftNav-item:hover{
            background:#222;
        }
        .leftNav-item2:hover{
            background:#555;
        }
        .leftNav-item2{
            height:20px;
            line-height:20px;
            margin:1px 0;
            padding:0 5px 0 10px;
            background:#888;
        }


        .main{
            position:absolute;
            top:40px;
            left:200px;
            width:calc(100% - 200px);
            height:calc(100% - 40px);
            overflow-x:hidden;
            overflow-y:auto;
        }
        .page{
            padding:10px;
        }
        .mainModel{margin-top:100px}
        table{
            border-collapse: collapse;
            border: none;
            margin: 10px auto;
            font-size:13px;
        }
        table,td{
            border:1px solid #ccc;
        }
        td{
            padding:5px 10px;
            color:#666;
            line-height: 20px;
        }
        table tr{background:#fff}
        table tr:first-child{background:#ddd;}
        table tr:first-child td{
            font-size:16px;
            text-align:center;
        }
        td input{
            width: 200px;
            height: 25px;
            border: none;
        }
        .tbody{
            transition:0.5s all;
            cursor:pointer;
        }
        .tbody:hover{
            background:#eee;
        }
        .title2,.desc,.title1{
            display:block;
            text-align:center;
        }
        .title2{
            font-size:20px;
            font-weight: bolder;
            margin-top:20px;
            color: #666;
        }
        .desc{
            font-size:14px;
            color:#666;
            margin-top:5px;
        }

        .mod-center{
            width:900px;
            margin:0 auto;
        }

        .title1{
            font-size:30px;
            font-weight: bolder;
            color: #444;
        }
        .row{
            padding:10px 0;
        }
        .row input{
            width: 200px;
            height: 30px;
            border: none;
            border-bottom: 1px solid #888;
            margin: 0 10px;
        }
        .radio{
            width:20px;
            height:20px;
            line-height:20px;
            color:#fff;
            text-align:center;
            font-size:16px;
            border-radius:5px;
            background:#888;
            display:inline-block;
            cursor:pointer;
            transition:0.5s all;
        }
        .radio-select{
            background:#33a356;
        }
        .textarea-border{
            width: 80%;
            max-width: 780px;
            min-width: 300px;
            height: 100px;
            margin: 0 10px;
            border-radius: 5px;
            border: 1px solid #888;
        }
        .textarea-noBorder{
            width: 100%;
            height: 100%;
            border: none;
            padding: 10px;
            resize: none;
            outline: none;
        }
        .btn1{
            border: 1px solid #888;
            height: 30px;
            line-height: 30px;
            padding: 0 10px;
            border-radius: 5px;
            cursor: pointer;
            margin: 0 10px;
            transition:0.5s all;
            color: #555;
            background: #ccc;
        }
        .btn1:hover{
            color:#222;
            background: #bbb;
            box-shadow:0 0 3px 1px #ccc;
        }
        .btn-close{
            position: absolute;
            top: 5px;
            right: 10px;
            padding: 5px;
            /*border: 1px solid #fff;*/
            border-radius: 50%;
            transition: 1s all;
            font-size: 20px;
            color: #fff;
            cursor: pointer;
            z-index: 99;
        }
        .btn-close:hover{
            transform:rotate(360deg);
        }
        .mod-text{
            position:relative;
            margin: 20px auto;
            width: 880px;
            background: #eee;
            border-radius: 5px;
        }
        .mod-text-shelter{
            position: absolute;
            top: 0;
            left: 0;
            z-index: 1;
            width: 100%;
            height: 100%;
            background: rgba(160, 160, 160, 0.8);
            text-align: center;
            color: #eee;
            cursor:pointer;
            transition:0.5s all;
        }
        .mod-text-shelter:hover{
            background: rgba(120, 120, 120, 0.8);
            color: #fff;
        }
        .shelter,.shelter-onload{
            position:fixed;
            top:0;
            left:0;
            width:100%;
            height:100%;
            background: rgba(160, 160, 160, 0.8);
            z-index: 99;
        }
        .shelter-onload{
            background:#fff;
            text-align:center;
            font-size:30px;
            color:#ccc;
        }
        .flex-justify{
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .win1{
            position: relative;
            width: 100%;
            height: 100%;
            overflow: auto;
            background: #fff;
            padding: 50px;
        }
    </style>
</head>
<body ng-app="app" ng-controller="order">
<div class="nav">
    <div>普惠生活开发手册</div>
    <div ng-repeat="nav in menu track by $index" ng-bind="nav.name" ng-class="nav.isShow?'navSelected':''" ng-click="setMenu(nav._id)"></div>
</div>

<div class="leftNav">
    <div class="leftNav-title">目录</div>
    <a class="leftNav-item" ng-show="menu[0].isShow" ng-repeat="l0 in menu[0].items track by $index" ng-bind="l0.entityName+'('+l0.title+')'" href="{{'#0_'+l0.entityName}}"></a>
    <div ng-show="menu[1].isShow" ng-repeat="l1 in menu[1].items track by $index">
        <div class="leftNav-item" ng-bind="l1.name+'('+l1.title+')'" ng-click="setSelectApi(l1.name)"></div>
        <div ng-repeat="l1_action in l1.actionItem track by $index" ng-show="selectAction==l1.name">
            <a class="leftNav-item2" ng-repeat="l1_api in l1_action.apiItem" ng-bind="'/'+l1_action.name+l1_api.name" href="{{'#1_'+l1_api.no}}"></a>
        </div>
    </div>
</div>

<div class="main">
    <%--元数据--%>
    <div class="page" ng-show="menu[0].isShow">
        <div class="mainModel" ng-repeat="order in menu[0].items track by $index">
            <a class="title2" ng-bind="order.entityName+'('+order.title+')'" name="{{'0_'+order.entityName}}"></a>
            <div class="desc" ng-bind="order.desc"></div>
            <table>
                <tr>
                    <td>字段名</td>
                    <td>字段类型</td>
                    <td>标题</td>
                    <td>描述</td>
                </tr>
                <tr class="tbody" ng-repeat="item in order.orderItem track by $index">
                    <td ng-bind="item.name"></td>
                    <td ng-bind="isNullString(item.type)"></td>
                    <td ng-bind="item.title"></td>
                    <td ng-bind="item.desc"></td>
                </tr>
            </table>
        </div>
    </div>

    <%--API--%>
    <div class="page" ng-show="menu[1].isShow">
        <div class="mainModel" ng-repeat="mod in menu[1].items" ng-show="mod.actionItem.length>0">
            <div class="title1" ng-bind="mod.title+'('+mod.name+')'"></div>
            <div ng-repeat="action in mod.actionItem track by $index" ng-show="action.apiItem.length>0">
                <div class="title2" ng-bind="action.name+'('+action.title+')'"></div>
                <table>
                    <tr>
                        <td>序号</td>
                        <td>类型</td>
                        <td>模块</td>
                        <td>action</td>
                        <td>API</td>
                        <td>标题</td>
                        <td>描述</td>
                    </tr>
                    <tr class="tbody" ng-repeat="api in action.apiItem track by $index" ng-click="setShowApiWin(api)">
                        <td ><a name="{{'1_'+api.no}}" ng-bind="api.no"></a></td>
                        <td ng-bind="api.apiType"></td>
                        <td ng-bind="action.pid"></td>
                        <td ng-bind="action.name"></td>
                        <td ng-bind="api.name"></td>
                        <td ng-bind="api.title"></td>
                        <td ng-bind="isObject(api.desc)"></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="page" ng-show="menu[2].isShow">

    </div>

    <%--小工具--%>
    <div class="page" ng-show="menu[3].isShow">
        <div class="mod-center">
            <div class="row">
                参数: <input type="text" ng-model="param.param" style="margin-right:20px">
                是否必须: <div class="radio" ng-class="param.need?'radio-select':''" ng-bind="param.need?'√':'×'" ng-click="param.need=!param.need"></div>
            </div>
            <div class="hd" style="padding: 5px 0;">
                <div class="fl">描述:</div>
                <textarea class="fl textarea-border" ng-model="param.desc"></textarea>
            </div>
            <div style="margin: 5px 0 0 160px;">
                <button class="btn1" ng-click="addParam()">添加参数</button>
                <button class="btn1" ng-click="addReturn()">添加返回值</button>
                <button class="btn1" ng-click="addPage()">添加分页</button>
                <button class="btn1" ng-click="createString()">生成字符串</button>
                <button class="btn1" ng-click="initParamsList('clear')">清空表格</button>
            </div>
        </div>
        <div class="mod-text" ng-click="copyText()" ng-show="stringJson!=null && stringJson!=''">
            <textarea class="textarea-noBorder" ng-bind="stringJson" id="stringJson"></textarea>
            <div class="mod-text-shelter flex-justify">点击复制文本</div>
        </div>

        <div class="title2">接口参数</div>
        <table>
            <tr>
                <td>参数</td>
                <td>是否必须</td>
                <td>描述</td>
                <td>操作</td>
            </tr>
            <tr ng-repeat="pItem in paramsList.params track by $index">
                <td><input type="text" ng-model="pItem.param"></td>
                <td class="center"><div class="radio" ng-class="pItem.need=='是'?'radio-select':''" ng-bind="pItem.need=='是'?'√':'×'" ng-click="pItem.need=='是'?pItem.need='否':pItem.need='是'"></div></td>
                <td><input type="text" ng-model="pItem.desc"></td>
                <td><button class="btn1" ng-click="delItem('params',$index)">删除</button></td>
            </tr>
        </table>

        <div class="title2">返回字段</div>
        <table>
            <tr>
                <td>返回值</td>
                <td>描述</td>
                <td>操作</td>
            </tr>
            <tr ng-repeat="re in paramsList.return track by $index">
                <td><input type="text" ng-model="re.param"></td>
                <td><input type="text" ng-model="re.desc"></td>
                <td><button class="btn1" ng-click="delItem('return',$index)">删除</button></td>
            </tr>
        </table>
    </div>
</div>

<%--API接口/返回字段详情页面--%>
<div class="shelter" ng-show="isShowApi">
    <div class="win1">
        <div class="nav">
            <div ng-bind="showApi.name+'('+showApi.title+')'"></div>
            <span class="btn-close" ng-click="closeApiWin()">×</span>
        </div>

        <div class="title2">接口参数</div>
        <table ng-show="showApi.desc.params!=null && showApi.desc.params.length>0">
            <tr>
                <td>参数</td>
                <td>是否必须</td>
                <td>描述</td>
            </tr>
            <tr class="tbody cursor" ng-repeat="showItem in showApi.desc.params track by $index">
                <td ng-bind="showItem.param"></td>
                <td class="center"><div class="radio" ng-class="showItem.need=='是'?'radio-select':''" ng-bind="showItem.need=='是'?'√':'×'"></div></td>
                <td ng-bind="showItem.desc"></td>
            </tr>
        </table>
        <div class="title2" ng-show="showApi.desc.params==null || showApi.desc.params.length==0">(无)</div>

        <div class="title2">返回字段</div>
        <table ng-show="showApi.desc.return!=null && showApi.desc.return.length>0">
            <tr>
                <td>返回值</td>
                <td>描述</td>
            </tr>
            <tr class="tbody cursor" ng-repeat="showRe in showApi.desc.return track by $index">
                <td ng-bind="showRe.param"></td>
                <td>
                    <span ng-bind="showRe.desc"></span>
                    <div ng-hide="showRe.mdata==null || showRe.mdata.length==0">
                        <div class="title2" ng-bind="showRe.mdata.entityName+'('+showRe.mdata.title+')'"></div>
                        <table>
                            <tr>
                                <td>字段名</td>
                                <td>字段类型</td>
                                <td>标题</td>
                                <td>描述</td>
                            </tr>
                            <tr class="tbody cursor" ng-repeat="mdataItem in showRe.mdata.orderItem track by $index">
                                <td ng-bind="mdataItem.name"></td>
                                <td ng-bind="isNullString(mdataItem.type)"></td>
                                <td ng-bind="mdataItem.title"></td>
                                <td ng-bind="mdataItem.desc"></td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
        <div class="title2" ng-show="showApi.desc.return==null || showApi.desc.return.length==0">(无)</div>
    </div>
</div>

<%--初始化页面遮挡,不存在onloadShelter变量--%>
<div class="shelter-onload flex-justify" ng-show="onloadShelter">
    加载中...
</div>

<script type="text/javascript" src="/js/lib/jquery-2.1.3${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="./js/lib/angular${js_min }.js?_v=${serverVersion}"></script>
<script type="text/javascript" src="createOrder.js?_v=${serverVersion}"></script>
<%--<script type="text/javascript" src="js/jquery-2.1.3.min.js"></script>--%>
<%--<script type="text/javascript" src="js/angular.min.js"></script>--%>
<%--<script type="text/javascript" src="js/createOrder.js"></script>--%>
</body>
</html>