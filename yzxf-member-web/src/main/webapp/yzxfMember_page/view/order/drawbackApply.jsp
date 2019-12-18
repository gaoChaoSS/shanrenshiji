<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="order_drawbackApply_Ctrl" class="d_content form_section title_section order_panel">
    <div class="title titleRed">
        <span class="icon-left-1 iconfont titleBack titleRed" ng-click="goPage('/my/order')"></span>
        申请退货
    </div>
    <div class="overflowPC">
        <div class="sectionMain">
            <div style="margin:4px 12px;border-bottom: 1px dashed #eee;">
                <span ng-bind="order.sellerName" class="colorBlue"
                      ng-click="goPage('/store/storeInfo/sellerId/'+order.sellerId)"></span>
                <span class="fr notHigh" ng-bind="orderStatus(order.orderStatus)"></span>
            </div>
            <div style="position: relative" ng-show="order.orderType==11" ng-repeat="product in order.productItems">
                <div style="padding:8px;position: absolute;top:0;left:0;">
                    <img err-src="/yzxfMember_page/img/notImg02.jpg" style="width:50px;height: 50px;"
                         ng-click="goPage('/store/commodity/goodsId/'+product.productId+'/_id/'+order.sellerId)"
                         ng-src="{{iconImgUrl(product.icon)}}"/>
                </div>
                <div style="padding:5px 8px 8px 80px;min-height: 70px;">
                    <div>
                        <span ng-bind="product.name"></span>
                        <span class="gray888" ng-bind="' ( × '+product.count+' 件)'"></span>
                    </div>
                    <div>
                <span class="gray888" ng-repeat="spec in product.selectSpec track by $index"
                      ng-bind="spec.name+' : '+spec.items+' '"></span>
                    </div>
                </div>
            </div>
            <div class="overflowHidden" style="padding: 0 10px;">
                <div class="notHigh fl">
                    <span class="deepRed textSize16" ng-bind="'¥ '+getMoney(order.totalPrice)"></span>
                </div>
                <div class="notHigh fr" ng-bind="showYFullTime(order.createTime)"></div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                    上传图片:
                </div>
                <div class="rowInput" style="width: calc(100% - 100px);margin-left:0">
                    <div class="rowBox">选择…</div>
                    <input type="file" name="file" class="rowBox"
                           onchange="angular.element(this).scope().uploadFile(this)"/>
                    <div class="floatLeft positionRelative"
                         ng-show="returnImg!=null && returnImg!=''">
                        <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{returnImg}}&wh=300_300"/>
                        <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem()"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="sectionMain">
            <div class="mainRow rowHeightAuto">
            <textarea placeholder="退货理由(255个字符以内,不支持emoji表情)" class="rowTextarea"
                      ng-model="returnDesc" ng-change="submitCheck()" maxlength="255"></textarea>
            </div>
        </div>
        <button type="submit" class="hideCommodityBtn bgBlue whitefff"
                ng-disabled="btnCheck" ng-click="submitForm()">提交申请
        </button>
    </div>
</div>