<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .input1{
        overflow:hidden;
        margin: 10px 0;
        border:2px solid #1480B7;
        border-radius:5px;

    }
    .input1>*{
        float:left;
        height:40px;
        line-height:40px;
    }
    .input1>.input1-title{
        width:150px;
        text-align:center;
        background:#1480B7;
        color:#fff;
    }
    .input1>.input1-val{
        border:none;
        outline:none;
        border-radius:0;
        width: calc(100% - 150px);
        font-weight: bolder;
        text-align:center;
        color:#888;
    }
    .input1>.input1-val-edit{
        box-shadow: 0 0 2px 1px #1480b773 inset;
        color:#333;
    }
    .inputCon1{
        position: absolute;
        top: 0;
        right: 10px;
        width: 230px;
    }
    .container{
        margin-right: 250px;
        height:400px
    }

    @media screen and (max-width: 1100px) {
        .inputCon1{
            position:static;
            width:100%;
        }
        .container{
            margin:0;
        }
    }
</style>

<div ng-controller="profit_parameter_Ctrl" style="width: 100%; height: 100%;position: relative;">
    <div class="sectionTitle">
        <div class="rowTitle1">参数配置</div>
        <div style="position:absolute;left:100px;top:0;line-height: 37px;color:#aaa">说明：图表无法显示小数点后第2位，只是显示问题，并不影响使用</div>
    </div>
    <div class="leftMenu">
        <div ng-repeat="menu in menuList" class="menuList" ng-click="setPage($index)">
            <div class="menuRow">
                <div class="menuRowTitle">
                    <span ng-bind="menu.typeTitle"></span>
                </div>
            </div>
        </div>
    </div>

    <div class="main-art">
        <div class="art-middle">
            <div class="art-title">
                <div class="art-title-name" ng-bind="curPage.typeTitle"></div>
            </div>
            <div class="art-content" ng-show="curPage.$$isList">
                <div class="container"></div>
                <div class="inputCon1">
                    <div ng-repeat="items in curData" class="input1">
                        <div class="input1-title" ng-bind="items.title"></div>
                        <input class="input1-val input1-val-edit" type="text" ng-model="items.val" ng-if="isEdit">
                        <div class="input1-val" ng-show="!isEdit" ng-bind="items.val+isNullText(items.unit)"></div>
                    </div>
                </div>
            </div>

            <div class="art-content" ng-show="!curPage.$$isList">
                <div class="input1">
                    <div class="input1-title" ng-bind="curData.title"></div>
                    <input class="input1-val input1-val-edit" type="text" ng-model="curData.val" ng-if="isEdit">
                    <div class="input1-val" ng-if="!isEdit" ng-bind="curData.val+isNullText(curData.unit)"></div>
                </div>
                <div class="container-line" style="margin: 50px 0 0 0;" ng-show="lineData.length>1"></div>
            </div>
        </div>
    </div>

    <div class="art-bottom">
        <button class="btn1 fr" ng-if="isEdit" ng-click="saveParameter()">保存</button>
        <button class="btn1 fr" ng-if="isEdit" ng-click="cancelParameter()">取消</button>
        <button class="btn1 fr" ng-if="!isEdit" ng-click="setStatus('isEdit')">编辑</button>
    </div>
</div>