<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div ng-include="'/temp_new/grid.html'" class="h100B"></div>
    <%--<div class="section1">--%>
        <%--<div class="sectionTitle">--%>
            <%--<div class="fl rowTitle1">代理商管理--%>
                <%--<span class="colorGray888" ng-bind=" '(共'+totalNumber+'条记录)'"></span>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="sectionQuery flex2">--%>
            <%--<div>--%>
                <%--<span>开始时间</span>--%>
                <%--<input type="date" class="inputQuery" ng-model="startDate"/>--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<span>结束时间</span>--%>
                <%--<input type="date" class="inputQuery" ng-model="endDate"/>--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<span>代理商</span>--%>
                <%--<input type="text" class="inputQuery width300px" placeholder="请输入代理商名称" ng-model="search">--%>
            <%--</div>--%>
            <%--<div>--%>
                <%--<span>审核状态</span>--%>
                <%--<select class="inputQuery" ng-model="selectCheck">--%>
                    <%--<option value="0">所有</option>--%>
                    <%--<option value="1">已通过</option>--%>
                    <%--<option value="2">未通过</option>--%>
                <%--</select>--%>
            <%--</div>--%>
            <%--<div class="btn1" ng-click="pageNumber(1)">查询</div>--%>
        <%--</div>--%>

        <%--<table class="sectionTable">--%>
            <%--<tr>--%>
                <%--<td style="width:5% " ng-show="modifyCheck"></td>--%>
                <%--<td style="width:15%">名称</td>--%>
                <%--<td style="width:10%">状态</td>--%>
                <%--<td style="width:10%">联系方式</td>--%>
                <%--<td style="width:10%">联系人</td>--%>
                <%--<td style="width:20%">所在地区</td>--%>
                <%--<td style="width:15%">申请时间</td>--%>
                <%--<td style="width:15%">创建时间</td>--%>
            <%--</tr>--%>
            <%--<tr ng-repeat="agent in orderList" class="trBk" ng-click="modifyCheckedFun($index)"--%>
                <%--ng-class="modifyList[$index].check?'bkColorRed2':''" ng-show="!modifyList[$index].modifyDel">--%>
                <%--<td style="width:5% " ng-show="modifyCheck"><input type="checkbox" ng-checked="modifyList[$index].check" class="font25px"></td>--%>
                <%--<td style="width:15%" ng-bind="agent.name"></td>--%>
                <%--<td style="width:10%" ng-bind="canUseCheck(modifyList[$index].modifyCanUse)" ng-class="modifyList[$index].modifyCanUse?'textColor':'colorRed2'"></td>--%>
                <%--<td style="width:10%" ng-bind="agent.phone"></td>--%>
                <%--<td style="width:10%" ng-bind="agent.contactPerson"></td>--%>
                <%--<td style="width:20%" ng-bind="agent.area"></td>--%>
                <%--<td style="width:15%" ng-bind="showYFullTime(agent.applyTime)"></td>--%>
                <%--<td style="width:15%" ng-bind="showYFullTime(agent.createTime)"></td>--%>
            <%--</tr>--%>
        <%--</table>--%>

        <%--<div class="isNullBox" ng-show="isNullPage">--%>
            <%--<div class="iconfont icon-meiyouneirong isNullIcon"></div>--%>
            <%--<div class="font25px colorGrayccc">没有数据</div>--%>
        <%--</div>--%>

        <%--<div class="selectTableCon">--%>
            <%--<div class="fl lineH50px marginLeft25" ng-show="modifyCheck" ng-click="checkedAll=!checkedAll;modifyCheckedFun(checkedAll)">--%>
                <%--<input type="checkbox" class="font25px" ng-checked="checkedAll">--%>
                <%--<span ng-bind="'全选'"></span>--%>
            <%--</div>--%>
            <%--<div class="fr">--%>
                <%--<div class="lineH50px" ng-show="!modifyCheck">--%>
                    <%--<button class="queryBtn1" ng-click="modifyCheck=true;getModifyList()"--%>
                            <%--ng-class="modifyCheck?'bkColorRed1':''"--%>
                            <%--ng-show="totalPage!=0">审核模式</button>--%>
                    <%--&lt;%&ndash;<button class="queryBtn1" ng-click="delBtnCheck=!delBtnCheck;modifyCheck=true;getModifyList()"&ndash;%&gt;--%>
                            <%--&lt;%&ndash;ng-class="delBtnCheck?'bkColorRed1':''"&ndash;%&gt;--%>
                            <%--&lt;%&ndash;ng-show="totalPage!=0">删除模式</button>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<button class="queryBtn1" ng-click="goPageAddAgent()">新增</button>&ndash;%&gt;--%>
                <%--</div>--%>
                <%--<div class="lineH50px" ng-show="modifyCheck">--%>
                    <%--<button class="queryBtn1 bkColorYellow1" ng-click="modifyCanUseFun(true)">审核通过</button>--%>
                    <%--<button class="queryBtn1 bkColorYellow1" ng-click="modifyCanUseFun(false)">审核不通过</button>--%>
                    <%--<button class="queryBtn1 bkColorRed1" ng-click="cancelBtn()">取消</button>--%>
                    <%--<button class="queryBtn1" ng-click="submitModifyList()">保存</button>--%>
                <%--</div>--%>
                <%--&lt;%&ndash;<div class="lineH50px" ng-show="delBtnCheck">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<button class="queryBtn1 bkColorYellow1" ng-click="modifyDelFun(true)">删除</button>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<button class="queryBtn1 bkColorRed1" ng-click="cancelBtn()">取消</button>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<button class="queryBtn1" ng-click="submitDelList()">保存</button>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<div class="sectionPage" ng-hide="isNullPage" ng-show="!modifyCheck">--%>
            <%--<div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex!=1">上一页</div>--%>
            <%--<div ng-show="isFirstPage">--%>
                <%--<div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>--%>
                <%--<div class="fl lineH30px">......</div>--%>
            <%--</div>--%>
            <%--<div class="btn3" ng-repeat="page in pageList" ng-bind="page.num"--%>
                 <%--ng-click="pageNumber(page.num);pageCur(page.num)"--%>
                 <%--ng-class="pageIndex==page.num?'bgBlue tWhite':''"></div>--%>
            <%--<div ng-show="isLastPage">--%>
                <%--<div class="fl lineH30px">......</div>--%>
                <%--<div class="btn3 fl marginLR5" ng-bind="totalPage" ng-click="pageNumber(totalPage)"></div>--%>
            <%--</div>--%>
            <%--<div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex!=totalPage">下一页</div>--%>
        <%--</div>--%>
    <%--</div>--%>
</div>