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
    <h1 style="padding:15px 10px;text-align: center;font-size:16px;">支付记录</h1>
    <form ng-submit="queryList();" style="padding:5px 10px;display:block;">
        <table style="background-color: #eee;">
            <tr>
                <td style="background-color: #ddd;">时间:</td>
                <td>
                    <input ng-model="filter.startTime" style="width:110px;"/>
                    -
                    <input ng-model="filter.endTime" style="width:110px;"/>
                </td>
                <td style="background-color: #ddd;">金额:</td>
                <td>
                    <input ng-model="filter.startFee" style="width:80px;"/>
                    -
                    <input ng-model="filter.endFee" style="width:80px;"/>
                </td>
            </tr>
            <tr>
                <td style="background-color: #ddd;">支付方式:</td>
                <td><select ng-model="filter._payType"
                            ng-options="item._id as item.name for item in payTypeList"></select>
                </td>
                <td style="background-color: #ddd;">支付场景:</td>
                <td><select ng-model="filter._clientType"
                            ng-options="item._id as item.name for item in clientTypeList"></select></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;">商户:</td>
                <td><select ng-model="filter._sellerId"
                            ng-options="item._id as item.name for item in SellerList"></select></td>
                <td style="background-color: #ddd;">店铺:</td>
                <td><select ng-model="filter._storeId"
                            ng-options="item._id as item.name for item in StoreList"></select></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;">支付状态:</td>
                <td><select ng-model="filter._payStatus"
                            ng-options="item._id as item.name for item in payStatusList"></select></td>
                <td style="background-color: #ddd;"></td>
                <td></td>
            </tr>
            <tr>
                <td style="background-color: #ddd;"></td>
                <td colspan="3">
                    <button class="button" type="submit">查询</button>
                    <button class="button" type="button" ng-click="queryAll();">查询所有</button>
                </td>
            </tr>
        </table>
    </form>
    <div style="padding:5px 10px;">
        <div class="fl notHigh">共 <span class="high" ng-bind="dataPage.totalNum"></span> 条数据</div>
        <div class="fl"></div>
        <div class="clearDiv"></div>
    </div>
    <div style="padding:0 10px 10px;">
        <table style="background-color: #eee;">
            <tr style="font-weight:bold;text-align: center;background-color: #ddd;">
                <td style="width: 280px;">时间</td>
                <td style="width: 280px;">商户</td>
                <td style="width: 120px;">支付类型</td>
                <td style="width: 120px;">支付金额</td>
                <td style="width: 120px;">支付状态</td>
                <td style="width: 120px;">退款记录</td>
                <td style="width:70px;text-align: center">操作</td>
            </tr>
            <tr ng-repeat="item in dataPage.items">
                <td ng-bind="item.$$createTime"></td>
                <td ng-bind="item.$$sellerName+' - '+item.$$storeName+''"></td>
                <td ng-bind="item.$$payType+item.$$clientType"></td>
                <td class="green" ng-bind="item.totalFee"></td>
                <td ng-class="getStatusClass(item.payStatus)" ng-bind="item.$$payStatus"></td>
                <td></td>
                <td style="width:100px;text-align: center">
                    <button ng-if="item.payStatus=='SUCCESS'" class="button high" ng-click="payReturn(item)">
                        退款情况
                    </button>

                    <button ng-if="item.payStatus=='START'" class="button" ng-click="payQuery(item)">
                        支付结果
                    </button>


                </td>
            </tr>
        </table>
        <div class="pageCon" style="padding:10px 0;">
            <div class="fl">共 <span class="high" ng-bind="dataPage.totalNum"></span> 条数据</div>
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