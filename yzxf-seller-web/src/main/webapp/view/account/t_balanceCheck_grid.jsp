<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div class="popSection flex2">
    <div class="popTitle">对账日期</div>
    <div style="width:100%">
        <span>对账日期:</span>
        <input type="date" ng-model="balanceData.date">
    </div>

</div>


<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fr btn1" ng-click="checkBalance()" >确定</button>
</div>