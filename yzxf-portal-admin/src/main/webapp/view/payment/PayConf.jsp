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
</style>

<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <h1 style="padding:15px 10px;text-align: center">支付账号配置记录</h1>
    <div style="padding:5px 10px;">
        <div class="fl notHigh">共 <span class="high" ng-bind="dataPage.totalNum"></span> 条数据</div>
        <div class="fl"></div>
        <button class="fr button" style="padding:5px 10px;" ng-click="addItem();">+ 添加记录</button>
        <div class="clearDiv"></div>
    </div>
    <div style="padding:0 10px 10px;">
        <table style="background-color: #eee;">
            <tr style="font-weight:bold;text-align: center;background-color: #ddd;">
                <td style="width: 280px;">商户id</td>
                <td style="width: 120px;">支付类型</td>
                <td>内容</td>
                <td style="width:70px;text-align: center">操作</td>
            </tr>
            <tr ng-repeat="item in dataPage.items">
                <td><input ng-model="item.sellerId" style="width:99%;" placeholder="请填写商户Id" ng-blur="saveItem(item);"/>
                </td>
                <td><select ng-model="item.payType" ng-options="sitem._id as sitem.name for sitem in payTypeList"
                            ng-change="saveItem(item);"></select></td>
                <td>
                    <button class=" button" ng-click="addContent(item)">+ 添加值</button>
                    <table>
                        <tr class="valueList" ng-repeat="citem in item.$$content">
                            <td style="width:100px;"><input placeholder="请填写Key" ng-model="citem.name"
                                                            ng-blur="saveItem(item);"/>
                            </td>
                            <td><textarea placeholder="请填写Value" ng-model="citem.value"
                                          ng-blur="saveItem(item);"></textarea></td>
                            <td style="width:200px;"><textarea placeholder="请填写描述" ng-model="citem.desc"
                                                               ng-blur="saveItem(item);"></textarea></td>
                            <td style="width:60px;">
                                <button class="button high" ng-click="deleteValue($index)" ng-blur="saveItem(item);">删除
                                </button>
                            </td>
                            <div class="clearDiv"></div>
                        </tr>
                    </table>
                </td>
                <td style="width:70px;text-align: center">
                    <button class="button high" ng-click="deleteItem($index)">删除</button>
                </td>
            </tr>
        </table>
    </div>
</div>