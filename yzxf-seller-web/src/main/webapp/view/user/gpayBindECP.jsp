<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popTitle2">
    <div class="subIndex===0?'popTitle2-selected':''" ng-click="checkSubPage(0)">绑定</div>
    <div class="subIndex===1?'popTitle2-selected':''" ng-click="checkSubPage(1)">解绑</div>
</div>


<div ng-show="subIndex===0">
    <div class="popSection flex2">
        <div class="popTitle">商家信息</div>
        <div>
            <span>商家名称:</span>
            <span ng-bind="dataPage.$$selectedItem.name"></span>
        </div>
        <div>
            <span>签约文件:</span>
            <input type="text" ng-model="ecpItem.filename"/>
        </div>
        <div>
            <span>分润方式:</span>
            <select ng-options="item._id as item.title for item in assignnoList" ng-model="ecpItem.assignno"></select>
        </div>
        <div>
            <span>分润率:</span>
            <input type="number" ng-model="ecpItem.rate"/>
        </div>
    </div>

</div>

<div ng-show="subIndex===1">
    <div class="popSection flex2">
        <div class="popTitle">商家信息</div>
        <div>
            <span>商家名称:</span>
            <span ng-bind="dataPage.$$selectedItem.name"></span>
        </div>
    </div>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fr btn1" ng-click="submitDataEcp()" ng-show="subIndex===0">提交</button>
    <button class="fr btn1" ng-click="unbindDataEcp()" ng-show="subIndex===1">解绑</button>
</div>