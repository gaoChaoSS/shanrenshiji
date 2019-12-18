<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>



<div ng-controller="order_orderComment_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
        订单评价
    </div>
    <form ng-submit="submitForm()">
        <div class="sectionMain">
            <div class="mainRow">
                <div class="rowTitle widthPercent20">订单编号</div>
                <div class="rowInput" ng-bind="pathParams.orderNo"></div>
            </div>

            <div class="mainRow">
                <div class="rowTitle widthPercent20">星级评价</div>
                <div class="rowInput">
                    <span class="mainRowRight10 gray888 textSize22" ng-repeat="itemStar in starList"
                          ng-click="startCheck(itemStar.id)" ng-class="starGrade>=itemStar.id?'loessYellow':'gray888'">★</span>
                </div>

            </div>
            <div class="mainRow rowHeightAuto">
                <textarea placeholder="分享您的购物心得(200字以内,不支持emoji表情)" class="rowTextarea"
                          ng-model="commentText" ng-change="submitCheck()"></textarea>
            </div>
        </div>
        <button type="submit" class="hideCommodityBtn bgBlue whitefff" ng-disabled="btnCheck">提交评价</button>
    </form>
</div>