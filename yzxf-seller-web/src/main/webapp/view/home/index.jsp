<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div class="section5 padding30">
        <div class="sectionTitle2">
            <div class="rowTitle2">
                欢迎登录普惠生活平台管理系统
            </div>
        </div>
        <div class="sectionText1">
            <div>1.所有页面打开时，要先点击【查询】，即可显示该页面的内容。</div>
            <div>2.点击【重置】按钮，所有的筛选条件会被清空。</div>
            <div>3.上传图片要求：图片需小于2M，jpg格式。</div>
            <%--<div>4.30分钟无操作自动下线</div>--%>
        </div>
    </div>
    <%--<div class="section1">--%>
        <%--<div class="overflowH">--%>
            <%--<div class="fl btnList width500px">--%>
                <%--<div ng-click="selectedDate1=1;getCountData()" ng-class="selectedDate1==1?'selectedBlue':''">今日</div>--%>
                <%--<div ng-click="selectedDate1=2;getCountData()" ng-class="selectedDate1==2?'selectedBlue':''">昨日</div>--%>
                <%--<div ng-click="selectedDate1=3;getCountData()" ng-class="selectedDate1==3?'selectedBlue':''">本月</div>--%>
                <%--<div ng-click="selectedDate1=4;getCountData()" ng-class="selectedDate1==4?'selectedBlue':''">上月</div>--%>
            <%--</div>--%>
            <%--&lt;%&ndash;<div class="fr">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<span class="font16px">归属</span>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<input type="text" class="inputQuery">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<button class="btn1">查询</button>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--</div>--%>
        <%--<div class="sectionChart">--%>
            <%--<div class="roundItem" id="roundItem1"></div>--%>
            <%--<div class="roundItem" id="roundItem2"></div>--%>
            <%--<div class="roundItem" id="roundItem3"></div>--%>
            <%--<div class="roundItem" id="roundItem4"></div>--%>
            <%--<div class="roundItem" id="roundItem5"></div>--%>
            <%--<div class="roundItem" id="roundItem6"></div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<div class="flex5">--%>
        <%--<div class="section4">--%>
            <%--<div class="sectionTitle">--%>
                <%--<div class="fl rowTitle1">会员消费排行</div>--%>
                <%--<div class="fr btnList2">--%>
                    <%--<div ng-click="selectedDate2=1" ng-class="selectedDate2==1?'selectedBlue':''">今日</div>--%>
                    <%--<div ng-click="selectedDate2=2" ng-class="selectedDate2==2?'selectedBlue':''">昨日</div>--%>
                    <%--<div ng-click="selectedDate2=3" ng-class="selectedDate2==3?'selectedBlue':''">本月</div>--%>
                    <%--<div ng-click="selectedDate2=4" ng-class="selectedDate2==4?'selectedBlue':''">上月</div>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<div class="sectionQuery flex2">--%>
                <%--<div>--%>
                    <%--<span>开始时间</span>--%>
                    <%--<input type="date" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>结束时间</span>--%>
                    <%--<input type="date" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>归属</span>--%>
                    <%--<input type="text" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div class="btn1">查询</div>--%>
            <%--</div>--%>

            <%--<table class="sectionTable">--%>
                <%--<tr>--%>
                    <%--<td style="width:30%">头像</td>--%>
                    <%--<td style="width:30%">会员姓名</td>--%>
                    <%--<td style="width:39%">消费金额</td>--%>
                <%--</tr>--%>
                <%--<tr class="trBk" ng-repeat="member in memberInfo">--%>
                    <%--<td style="width:30%" ng-bind="member.icon"></td>--%>
                    <%--<td style="width:30%" ng-bind="member.name"></td>--%>
                    <%--<td style="width:39%" ng-bind="member.money"></td>--%>
                <%--</tr>--%>
            <%--</table>--%>
            <%--<div class="isNullBox" ng-show="isNullPageM">--%>
                <%--<div class="iconfont icon-meiyouneirong isNullIcon"></div>--%>
                <%--<div class="font25px colorGrayccc">没有数据</div>--%>
            <%--</div>--%>

            <%--<div class="sectionPage" ng-hide="isNullPage">--%>
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

        <%--<div class="section4">--%>
            <%--<div class="sectionTitle">--%>
                <%--<div class="fl rowTitle1">商家排行榜</div>--%>
                <%--<div class="fr btnList2">--%>
                    <%--<div ng-click="selectedDate3=1" ng-class="selectedDate3==1?'selectedBlue':''">今日</div>--%>
                    <%--<div ng-click="selectedDate3=2" ng-class="selectedDate3==2?'selectedBlue':''">昨日</div>--%>
                    <%--<div ng-click="selectedDate3=3" ng-class="selectedDate3==3?'selectedBlue':''">本月</div>--%>
                    <%--<div ng-click="selectedDate3=4" ng-class="selectedDate3==4?'selectedBlue':''">上月</div>--%>
                <%--</div>--%>
            <%--</div>--%>

            <%--<div class="sectionQuery flex2">--%>
                <%--<div>--%>
                    <%--<span>开始时间</span>--%>
                    <%--<input type="date" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>结束时间</span>--%>
                    <%--<input type="date" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>归属</span>--%>
                    <%--<input type="text" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>商户号</span>--%>
                    <%--<input type="text" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div>--%>
                    <%--<span>分类</span>--%>
                    <%--<input type="text" class="inputQuery">--%>
                <%--</div>--%>
                <%--<div class="btn1">查询</div>--%>
            <%--</div>--%>

            <%--<table class="sectionTable">--%>
                <%--<table class="sectionTable">--%>
                    <%--<tr>--%>
                        <%--<td style="width:30%">头像</td>--%>
                        <%--<td style="width:30%">会员姓名</td>--%>
                        <%--<td style="width:39%">消费金额</td>--%>
                    <%--</tr>--%>
                    <%--<tr class="trBk" ng-repeat="member in memberInfo">--%>
                        <%--<td style="width:30%" ng-bind="member.icon"></td>--%>
                        <%--<td style="width:30%" ng-bind="member.name"></td>--%>
                        <%--<td style="width:39%" ng-bind="member.money"></td>--%>
                    <%--</tr>--%>
                <%--</table>--%>
            <%--</table>--%>

            <%--<div class="isNullBox" ng-show="isNullPage">--%>
                <%--<div class="iconfont icon-meiyouneirong isNullIcon"></div>--%>
                <%--<div class="font25px colorGrayccc">没有数据</div>--%>
            <%--</div>--%>

            <%--<div class="sectionPage">--%>
                <%--<div class="lineH30px">首页</div>--%>
                <%--<div class="btn2">上一页</div>--%>
                <%--<div class="btn3">1</div>--%>
                <%--<div class="btn3">2</div>--%>
                <%--<div class="btn3">3</div>--%>
                <%--<div class="btn2">下一页</div>--%>
                <%--<div class="lineH30px">尾页</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
</div>