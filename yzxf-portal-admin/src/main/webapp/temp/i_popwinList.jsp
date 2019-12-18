<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<!-- pop window list -->
<div class="mark" ng-repeat="item in popWindows" ng-click="item.close();" ng-style="{zIndex:$index+10}"></div>
<div pop-win index="{{$index}}" id="{{item.name}}" ng-style="{zIndex:$index+10,width:item.winWidth,height:winHeight}"
     is-pop="true" ng-repeat="item in popWindows" win-width="{{item.width}}"
     win-height="{{item.height}}" win-title="{{item.title}}"></div>