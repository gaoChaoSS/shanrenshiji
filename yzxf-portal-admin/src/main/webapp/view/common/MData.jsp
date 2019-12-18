<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div id="layout_inc_mian" ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div layout>
        <div data-type="left" size="200" style="overflow: auto;">
            <div tree-view id="tree_left" tree-data="demo.tree" model="{{model}}" entity="{{entity}}" text-field="title"
                 value-field='_id' select-type="none"></div>
        </div>
        <div data-type="center">
            <div grid id="grid_right" model="{{model}}" entity="{{entity}}" is-edit="false" quick-add="true"
                 exclude-keys="pid,dbType" style="width: 100%; height: 100%;"></div>
        </div>
    </div>
</div>