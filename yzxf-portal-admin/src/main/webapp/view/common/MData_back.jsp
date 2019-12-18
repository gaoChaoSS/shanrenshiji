<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<style>

    .itemTr > div > input {
        width: 100%;
    }

    .itemTr > div > select {
        width: 100%;
        border: 1px solid #ccc;
        background-color: #eee;
        padding: 4px 0;
        height: 30px;
    }

    .itemTr > div.headerTd1 {
        text-align: center;
        background-color: #eee;
        font-weight: bold;
    }

    .mDataForm > .itemTr:first-child {
        border-top: 1px solid #ccc;
    }

    .itemTr {
        overflow: auto;
    }

    .itemTr > div {
        background-color: #fff;
        float: left;
        padding: 0px 4px;
        border-right: 1px solid #ccc;
        border-bottom: 1px solid #ccc;
        min-height: 39px;
        line-height: 38px;
    }

    .itemTr > div:first-child {
        border-left: 1px solid #ccc;
    }

    .itemTr > div.remoteAdd, .remoteAdd {
        /*color: #ef5f2a;*/
        background-color: #fda888;
    }

    .itemTr > div.localAdd, .localAdd {
        /*color: #489557;*/
        background-color: #d4f9db;
    }

    .itemTr > div.isUpdate, .isUpdate {
        /*color: #7c27a1;*/
        background-color: #ebdaf2;
    }

    .itemTr > div.boolean {
        text-align: center;
    }

    .ti {
        overflow: auto;
        padding: 4px 0 8px;
    }

    .ti > div {
        margin-right: 5px;
        padding: 4px;
    }

    div.remote {
        color: #ef5f2a;
    }

    div.local {
        color: #489557;
    }

    div.update {
        color: #7c27a1;
    }

    .button.iconfont {
        border: 1px solid;
        text-align: center;
    }
</style>

<div id="layout_inc_mian" ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;position: relative">
    <div style="overflow: auto;position: absolute;top:0;left:0;bottom: 0; width:200px;border-right: 1px solid #ccc;">
        <div tree-view id="tree_left" tree-data="demo.tree" model="{{model}}" entity="{{entity}}" text-field="title"
             value-field='_id' select-type="none"></div>
    </div>
    <div style="overflow: auto;position: absolute;top:0;right:0;bottom: 0; left:200px;padding:8px;background-color: #eee;"
         ng-include="'/view_temp/grid.jsp'">
        <%--<div grid id="grid_right" model="{{model}}" entity="{{entity}}" quick-add="true" exclude-keys="pid,dbType" style="width: 100%; height: 100%;"></div>--%>
    </div>
</div>