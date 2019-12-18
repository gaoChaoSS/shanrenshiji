<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">

    <div class="section1">
        <div class="sectionTitle">
            <div class="fl rowTitle1">商家审核
                <span class="colorGray888" ng-bind=" '(共'+totalNumber+'条记录)'"></span>
            </div>
        </div>

        <div class="sectionQuery flex2">
            <div>
                <span>商家名称</span>
                <input type="text" class="inputQuery" ng-model="name">
            </div>
            <div>
                <span>状态</span>
                <select class="inputQuery" ng-model="status">
                    <option value="0">所有</option>
                    <option value="1">审核通过</option>
                    <option value="2">审核不通过</option>
                </select>
            </div>
            <div>
                <div class="btn1" ng-click="querySeller()">查询</div>
                <div class="btn1" >账号分配</div>
            </div>
        </div>
        <table class="sectionTable">
            <tr>
                <td style="width:5% " ng-show="modifyCheck"></td>
                <td style="width:15%">名称</td>
                <td style="width:10%">状态</td>
                <td style="width:15%">联系人</td>
                <td style="width:15%">联系电话</td>
                <td style="width:25%">所在地区</td>
                <td style="width:15%">申请时间</td>
            </tr>
            <tr ng-repeat="seller in orderList" class="trBk" ng-click="modifyCheckedFun($index)"
                ng-class="modifyList[$index].check?'bkColorRed2':''" ng-show="!modifyList[$index].modifyDel">
                <td style="width:5% " ng-show="modifyCheck"><input type="checkbox" ng-checked="modifyList[$index].check" class="font25px"></td>
                <td style="width:15%" ng-bind="(seller.name==null||seller.name=='')?'匿名':seller.name"> </td>
                <td style="width:10%" ng-bind="canUseCheck(modifyList[$index].modifyCanUse)" ng-class="modifyList[$index].modifyCanUse?'textColor':'colorRed2'"></td>
                <td style="width:15%" ng-bind="(seller.sellerNo==null||seller.sellerNo=='')?'无':seller.sellerNo"></td>
                <td style="width:15%" ng-bind="seller.phone"></td>
                <td style="width:25%" ng-bind="seller.area"></td>
                <td style="width:15%" ng-bind="showYFullTime(seller.applyTime)"></td>
            </tr>
        </table>

        <div class="isNullBox" ng-show="isNullPage">
            <div class="iconfont icon-meiyouneirong isNullIcon"></div>
            <div class="font25px colorGrayccc">没有数据</div>
        </div>

        <div class="selectTableCon">
            <div class="fl lineH50px marginLeft25" ng-show="modifyCheck" ng-click="checkedAll=!checkedAll;modifyCheckedFun(checkedAll)">
                <input type="checkbox" class="font25px" ng-checked="checkedAll">
                <span ng-bind="'全选'"></span>
            </div>
            <div class="fr">
                <div class="lineH50px" ng-show="!modifyCheck">
                    <button class="queryBtn1" ng-click="modifyCheck=true;getModifyList()"
                            ng-class="modifyCheck?'bkColorRed1':''"
                            ng-show="totalPage!=0">审核模式</button>
                </div>
                <div class="lineH50px" ng-show="modifyCheck">
                    <button class="queryBtn1 bkColorYellow1" ng-click="modifyCanUseFun(true)">审核通过</button>
                    <button class="queryBtn1 bkColorYellow1" ng-click="modifyCanUseFun(false)">审核不通过</button>
                    <button class="queryBtn1 bkColorRed1" ng-click="cancelBtn()">取消</button>
                    <button class="queryBtn1" ng-click="submitModifyList()">保存</button>
                </div>
            </div>
        </div>

        <div class="sectionPage" ng-hide="totalPage<1 || totalPage==null">
            <div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex!=1">上一页</div>
            <div ng-show="isFirstPage">
                <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>
                <div class="fl lineH30px">......</div>
            </div>
            <div class="btn3" ng-repeat="page in pageList" ng-bind="page.num"
                 ng-click="pageNumber(page.num);pageCur(page.num)"
                 ng-class="pageIndex==page.num?'bgBlue tWhite':''"></div>
            <div ng-show="isLastPage">
                <div class="fl lineH30px">......</div>
                <div class="btn3 fl marginLR5" ng-bind="totalPage" ng-click="pageNumber(totalPage)"></div>
            </div>
            <div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex!=totalPage">下一页</div>
        </div>

    </div>
</div>
</div>