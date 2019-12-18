<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div>
    <div style="width:20%">所属分类</div>
    <div style="width:40%">标题</div>
    <div style="width:20%">创建时间</div>
    <div style="width:20%">操作</div>
</div>
<div ng-repeat="newsList in dataPage.items" class="divBk"
    ng-class="dataPage.$$selectedItem._id==newsList._id?'selected':''"
    ng-click="dataPage.$$selectedItem=newsList;">
    <div style="width:20%" ng-bind="newsList.name"></div>
    <div style="width:40%" ng-bind="newsList.title"></div>
    <div style="width:20%" ng-bind="showYFullTime(newsList.createTime)"></div>
    <div style="width:20%;display:flex" class="flex1">
        <div class="fl btn1 bgBlue" ng-click="showInfo(newsList)">查看</div>
        <div class="fl btn1 bgBlue" ng-click="deleteNews(newsList._id)">删除</div>
    </div>
</div>