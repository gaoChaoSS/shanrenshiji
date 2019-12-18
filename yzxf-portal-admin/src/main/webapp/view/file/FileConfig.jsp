<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<style>
</style>
<div id="layout_inc_mian" ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div class="keyTitle">
        基本信息
        <button ng-click="reInitIndex()" class="button">刷新</button>
    </div>
    <div style="padding: 2px 10px;">
        <table>
            <tr>
                <td class="hearderTd1" style="width:150px;">当前块序号:</td>
                <td ng-bind="dirInfo.blockNo"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;">当前块写入位置:</td>
                <td ng-bind="dirInfo.position"></td>
            </tr>
            <tr>
                <td class="hearderTd1" style="width:150px;">文件块</td>
                <td>
                    <div class="fl" style="background-color: #eee;border-radius: 4px;padding:2px 5px;"
                         ng-repeat="item in dirInfo.blockList" ng-bind="item"></div>
                </td>
            </tr>
        </table>
    </div>
    <div class="keyTitle">文件列表 <span class="notHigh">(共 <span class="high"
                                                              ng-bind="dirInfo.fileIdMapSize"></span> 项)</span></div>
    <div ng-repeat="(key,item) in dirInfo.fileIdMap"
         style="background-color: #eee;padding: 4px;margin: 6px 10px;border-radius: 4px;overflow:hidden;">
        <div class="fl">
            <a target="_blank" href="/s_img/icon.jpg?_id={{key}}">
                <img ng-src="/s_img/icon.jpg?_id={{key}}"
                     style="max-width: 80px;max-height: 80px;border:1px solid #ccc;padding:2px;"/>
            </a>
        </div>
        <div class="fl" ng-bind="key" style="padding:8px;"></div>
    </div>
    <div class="notHigh" style="padding:10px;">注: 最多显示100项</div>
</div>