<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div id="layout_inc_mian" ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;position:relative;">
    <div style="background-color: #eee;overflow: hidden;height:35px;border-bottom: 1px solid #ccc;">
        <div class="fl" style="padding:4px;line-height: 24px">
            模块:
            <select ng-model="selectModel" ng-change="queryEntity(selectModel)" ng-class="selectModel?'high':''"
                    ng-options="item._id as item.$$name for item in modelList ">
            </select>
        </div>
        <div class="fl" style="padding:4px;;line-height: 24px" ng-show="selectModel!=null">
            <span class="notHigh">&gt;</span> 实体:
            <select ng-model="selectEntity" ng-change="changeEntity(selectEntity)"
                    ng-class="selectEntity?'high':''"
                    ng-options="item._id as item.$$name for item in entityList ">
            </select>
        </div>

    </div>
    <div style="position: absolute;top:35px;left:0;bottom:0;width:200px;overflow: auto;border-right: 1px solid #ccc;"
         ng-if="entityMap[selectEntity].tableType=='tree'">
        <div tree-view id="tree_left" tree-data="demo.tree" model="{{modelMap[selectModel].name}}"
             entity="{{entityMap[selectEntity].name}}" text-field="title"
             value-field='_id' select-type="none"></div>
    </div>
    <div style="position: absolute;top:35px;right:0;bottom:0;"
         ng-style="{left:entityMap[selectEntity].tableType=='tree'?200:0}">
        <div grid id="grid_right"
             ng-if="selectModel!=null&&selectEntity!=null"
             model="{{modelMap[selectModel].name}}"
             entity="{{entityMap[selectEntity].name}}"
             quick-add="true"
             exclude-keys="pid"
             is-edit="true"
             style="width: 100%; height: 100%;"></div>
    </div>
</div>