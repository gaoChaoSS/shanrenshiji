<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="financial_financailReport_Ctrl" style="width: 100%; height: 100%;">
    <div ng-include="'/temp_new/grid.html'" class="h100B"></div>
    <div class="sectionHintBk" ng-show="isSubmit" style="z-index: 21;">
        <div class="sectionHint">
            <div class="lineH100px">是否确认提交?</div>
            <div class="flex1">
                <button class="btn1" ng-click="transferSubmit()">是</button>
                <button class="btn1 bkColorRed1" ng-click="closeSubmit()">否</button>
            </div>
        </div>
    </div>
    <div class="sectionHintBk" ng-show="submitSuccess" style="z-index: 21;">
        <div class="sectionHint" style="max-width: 900px;min-width: 500px;background: #fff;">
            <div class="sectionTable">
                <div>
                    <div style="width:50%">代理商</div>
                    <div style="width:50%">结果</div>
                </div>
                <div ng-repeat="re in transferList | orderBy:'status':true" class="trBk">
                    <div style="width:50%" ng-bind="re.name"></div>
                    <div style="width:50%" ng-bind="re.status"></div>
                </div>
            </div>
            <%--<div class="textCenter colorGreen1" ng-show="successList.length>0">提交成功</div>--%>
            <%--<div class="rowText2" ng-repeat="success1 in successList" ng-bind="success1"></div>--%>

            <%--<div class="textCenter colorRed2" ng-show="failList.length>0">提交失败</div>--%>
            <%--<div class="textCenter" style="font-size:14px" ng-show="failList.length>0">(未获取到银行卡)</div>--%>
            <%--<div class="rowText2"  ng-repeat="fail1 in failList" ng-bind="fail1"></div>--%>

            <div class="flex1">
                <button class="btn1" ng-click="closeSubmit()">关闭</button>
            </div>
        </div>
    </div>
</div>