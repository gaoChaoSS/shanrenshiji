<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .valueList {
        padding: 6px;
        border-radius: 4px;
        background-color: #ddd;
        margin: 5px 0;
    }

    .valueList input, .valueList textarea {
        min-height: 45px;
        width: 99%;
    }

    input, select {
        color: red;
    }
</style>

<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div style="padding:5px 10px;">
        <table style="background-color: #eee;">
            <tr>
                <td style="background-color: #ddd;width:100px;">支付时间:</td>
                <td ng-bind="payInfo.$$createTime"></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;width:100px;">支付单号:</td>
                <td ng-bind="payInfo._id"></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;width:100px;">商户:</td>
                <td ng-bind="payInfo.$$sellerName+' - '+payInfo.$$storeName+''"></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;width:100px;">支付方式:</td>
                <td ng-bind="payInfo.$$payType+payInfo.$$clientType"></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;width:100px;">支付金额:</td>
                <td class="green" ng-bind="payInfo.totalFee"></td>
            </tr>
        </table>
    </div>
    <form ng-submit="submitReq();" style="padding:5px 10px;display:block;">
        <table style="background-color: #eee;">
            <tr>
                <td style="background-color: #ddd;width:100px;">退款金额:</td>
                <td>
                    <input id="returnAmount" ng-model="returnAmount" style="width:110px;font-size:16px;" class="high"
                           required/>
                    <button class="button highM" type="submit">提交申请</button>
                </td>
            </tr>
        </table>
    </form>
    <div style="padding:5px 10px;">
        <div class="fl notHigh">共 <span class="high" ng-bind="dataPage.totalNum"></span> 条退款记录</div>
        <div class="fl"></div>
        <div class="clearDiv"></div>
    </div>
    <div style="padding:0 10px 10px;" ng-if="dataPage.totalNum>0">
        <table style="background-color: #eee;">
            <tr style="font-weight:bold;text-align: center;background-color: #ddd;">
                <td style="width: 280px;">退款时间</td>
                <td style="width: 120px;">退款金额</td>
                <td style="width: 120px;">退款状态</td>
                <td style="width: 120px;">数据详情</td>
                <td style="width: 120px;">操作</td>
            </tr>
            <tr ng-repeat="item in dataPage.items">
                <td ng-bind="item.$$createTime"></td>
                <td class="green" ng-bind="item.returnAmount"></td>
                <td ng-class="getStatusClass(item.returnStatus)" ng-bind="item.$$returnStatus"></td>
                <td ng-bind="item.returnResultMap"></td>
                <td>
                    <button class="button" ng-click="returnQuery(item)">查询退款情况</button>
                </td>
            </tr>
        </table>
        <div class="pageCon" style="padding:10px 0;">
            <div class="fl">共 <span class="high" ng-bind="dataPage.totalNum"></span> 条退款记录</div>
            <div class="fl button" ng-if="dataPage.$$start>1" ng-click="filter.pageNo=1;queryList();">1</div>
            <div class="fl" style="padding:0 6px;" ng-if="dataPage.$$start>1"> ...</div>
            <div class="fl button" ng-class="item==dataPage.pageNo?'current':''"
                 ng-click="filter.pageNo=item;queryList();"
                 ng-repeat="item in dataPage.$$pages"
                 ng-bind="item"></div>
            <div class="fl" style="padding:0 6px;" ng-if="dataPage.totalPage>dataPage.$$end"> ...</div>
            <div class="fl button" ng-click="filter.pageNo=dataPage.totalPage;queryList();"
                 ng-bind="dataPage.totalPage"
                 ng-if="dataPage.totalPage>dataPage.$$end"></div>
            <div class="clearDiv"></div>
        </div>
    </div>
</div>