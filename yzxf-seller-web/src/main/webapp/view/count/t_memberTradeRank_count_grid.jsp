<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="fl" style="width:160px;padding:0 20px;text-align:right;">
    <div ng-repeat="nav in countList" ng-bind="nav.key" style="height:50px;line-height:50px;margin:10px 0"></div>
</div>

<div class="fl" style="width:calc(100% - 200px)">
    <div ng-repeat="val in countList | orderBy:'scaleNum':true" style="position:relative;height:50px;background:#ccc;margin:10px 0">
        <div style="background:#138bbe;height:50px;" ng-style="{width:val.scaleNum+'%'}"></div>
        <div style="position:absolute;top:0;left:0;width:100%;line-height:50px;text-align:center;color:#fff;"
             ng-bind="val.scaleNum+'% / '+val.num+'元'"></div>
    </div>
</div>