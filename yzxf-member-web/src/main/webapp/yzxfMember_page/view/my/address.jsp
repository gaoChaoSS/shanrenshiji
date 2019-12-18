<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_address_Ctrl" class="d_content form_section title_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="pathParams.goBack==1?goBack():goPage('/my/my')"></span>
        我的收货地址
        <span class="titleManage notHigh" ng-click="manageBtn()" ng-bind="check?'完成':'管理'"></span>
    </div>
    <div class="overflowPC">
        <div class="sectionMain3" ng-repeat="itemAddress in queryAddress">
            <div class="addressModifyBtn flex1 " ng-show="check && !delCheck" ng-click="delCheckFun()">
                <div class="widthPercent100 colorRed1 icon-cuowu iconfont textSize25"></div>
            </div>
            <div class="mainRowLeft10" ng-class="check?'flex5':'flex1'">
                <div class="lineHeight25 gray888" ng-bind="isNullText(itemAddress.area)+isNullText(itemAddress.address)"></div>
                <div class="lineHeight25">
                    <span class="gray888" ng-bind="isNullText(itemAddress.name)+' '+isNullText(itemAddress.gender)+' '+isNullText(itemAddress.phone)"></span>
                </div>
                <div class="lineHeight25" ng-click="setDefaultAddress(itemAddress._id)">
                    <span class="icon-103 iconfont mainRowRight20" ng-class="itemAddress.defaultAddress?'limeGreen':'gray888'"></span>
                    <span class="gray666" ng-bind="itemAddress.defaultAddress?'默认地址':'设为默认地址'"></span>
                </div>
            </div>
            <div class="addressModifyBtn flex1" ng-show="check && !delCheck" ng-click="clearArea();goPage('/my/addAddress/addressId/'+itemAddress._id)">
                <div class="widthPercent100 loessYellow icon-xiugai iconfont"></div>
            </div>
            <div class="addressModifyBtn flex1 bgRed" ng-show="delCheck" ng-click="delAddress(itemAddress._id)">
                <div class="widthPercent100 whitefff">删除</div>
            </div>
        </div>
        <div class="hideCommodityBtn bgWhite rowBorderTop" ng-click="clearArea();goPage('/my/addAddress')">
            <span class="icon-llalbumshopselectorcreate iconfont limeGreen textSize18"></span>
            <span class="notHigh">新增收货地址</span>
        </div>
    </div>
</div>