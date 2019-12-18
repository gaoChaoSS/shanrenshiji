<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div ng-include="'/temp_new/grid.html'" class="h100B"></div>

    <div class="sectionHintBk" ng-show="isDel">
        <div class="sectionHint">
            <div class="lineH100px">是否删除此草稿?</div>
            <div class="flex1">
                <button class="btn1" ng-click="delSubmit()">是</button>
                <button class="btn1 bkColorRed1" ng-click="closeDel()">否</button>
            </div>
        </div>
    </div>
    <div class="sectionHintBk" ng-show="delSuccess">
        <div class="sectionHint">
            <div class="lineH100px">删除成功</div>
            <div class="flex1">
                <button class="btn1" ng-click="closeDel()">关闭</button>
            </div>
        </div>
    </div>
</div>