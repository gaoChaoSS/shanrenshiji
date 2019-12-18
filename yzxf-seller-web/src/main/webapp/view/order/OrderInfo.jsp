<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div grid id="grid_main" model="{{model}}" entity="{{entity}}"
         quick-add="true"
         is-edit="true" style="width: 100%; height: 100%;"></div>
</div>