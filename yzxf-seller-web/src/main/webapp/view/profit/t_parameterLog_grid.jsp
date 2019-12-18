<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div style="width:25%">类型</div>
    <div style="width:25%">名称</div>
    <div style="width:25%">值</div>
    <div style="width:15%">修改日期</div>
</div>
<div ng-repeat="data in dataPage.items" class="trBk"
     ng-class="dataPage.$$selectedItem._id==data._id?'selected':''"
     ng-click="dataPage.$$selectedItem=data;$event.stopPropagation()" >
    <div style="width:25%" ng-bind="data.typeTitle"></div>
    <div style="width:25%" ng-bind="data.title"></div>
    <div style="width:25%">
        <span ng-bind="getMoney(data.oldVal)+isNullText(data.unit)"></span>
        <span style="display: inline-block;transform: rotate(90deg);" class="iconfont icon-fanhuidingbu colorGreen1"></span>
        <span ng-bind="getMoney(data.val)+isNullText(data.unit)"></span>
    </div>
    <div style="width:25%" ng-bind="showYFullTime(data.createTime)"></div>
</div>