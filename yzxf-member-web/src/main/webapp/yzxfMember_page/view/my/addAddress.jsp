<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="my_addAddress_Ctrl" class="d_content title_section form_section">
    <form ng-submit="submitForm()">
        <div class="title">
            <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
            我的收货地址
            <span class="titleManage notHigh" ng-click="submitForm()"  ng-class="check?'':'notHigh'">完成</span>
        </div>

            <div class="mainRowTitle notHigh">联系人</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow">
                <div class="rowTitle">姓名:</div>
                <input type="text" class="rowInput" placeholder="请填写收货人姓名" ng-model="consignee" ng-change="nextBtn()"/>
            </div>
            <div class="mainRow mainRowSelect">
                <div class="rowInputCheckMain">
                    <div class="rowInputCheck" ng-click="checkMan=true">
                        <span ng-class="{true:'icon-checkbox-checked',false:'icon-checkbox-unchecked'}[checkMan]"></span>男士
                    </div>
                    <div class="rowInputCheck" ng-click="checkMan=false">
                        <span ng-class="{true:'icon-checkbox-unchecked',false:'icon-checkbox-checked'}[checkMan]"></span>女士
                    </div>
                </div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">电话:</div>
                <input type="text" class="rowInput" placeholder="请填写收货人的联系号码" ng-model="phoneNumber" ng-change="nextBtn()"/>
            </div>
        </div>
        <div class="mainRowTitle notHigh">收货地址</div>
        <div class="sectionMain sectionMainNotMargin">
            <div class="mainRow" ng-click="goPage('home/location/selectedArea/addAddressArea')">
                <div class="rowTitle">所在区域</div>
                <div class="rowInput" ng-bind="area"></div>
                <div class="icon-right-1-copy iconfont mainRowRight"></div>
            </div>
            <div class="mainRow">
                <div class="rowTitle">街道地址:</div>
                <input type="text" class="rowInput" placeholder="请填写街道地址" ng-model="address" ng-change="nextBtn()"/>
            </div>
            <div class="mainRow">
                <div class="rowTitle">邮政编码:</div>
                <input type="text" class="rowInput" placeholder="请填写邮政编码" ng-model="postalcode" ng-change="nextBtn()"/>
            </div>
        </div>
    </form>
</div>