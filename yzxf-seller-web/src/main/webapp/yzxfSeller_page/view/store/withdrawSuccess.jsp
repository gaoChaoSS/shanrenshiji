<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%--现金交易--%>
<div ng-controller="store_withdrawSuccess_Ctrl" class="d_content title_section form_section">
    <div class="title">
        <span class="icon-left-1 iconfont titleBack" ng-click="goPageUrl()"></span>
        提现申请已提交
    </div>
    <div class="sectionMain" style="padding:50px 0;position: absolute;height: calc(100% - 56px);width: 100%;">
        <div class="iconfont iconImgText1 bgBlue icon-gengduo"></div>
        <div class="btnText2">后台管理员将会在一个工作日内处理</div>
    </div>
    <div class="btnText1 textSize16" ng-show="isSuccess!=0">
        <span ng-bind="countTimeNum"></span>秒后返回
        <span ng-click="goPageUrl()" class="dodgerBlue">我的账户</span>
    </div>
    <%--<div class="title bgBlue whitefff">--%>
        <%--提现详情--%>
    <%--</div>--%>
    <%--<div class="iconfont icon-zhengque1 textCenter textSize70 colorBlue mainRowTop20"></div>--%>
    <%--<div class="textCenter textSize20 mainRowBottom20">提现申请已提交</div>--%>
    <%--<div class="sectionMain">--%>
        <%--<div class="mainRow mainRowNotBorderBottom">--%>
            <%--<div class="gray333 textCenter textSize16" ng-bind="'等候后台处理'" style="line-height: 50px;"></div>--%>
        <%--</div>--%>
        <%--&lt;%&ndash;<div class="mainRow mainRowNotBorderBottom">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="rowTitle gray888">提现金额</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="mainRowRight gray333" ng-bind="'¥ 1878'"></div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<div class="mainRow mainRowNotBorderBottom">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="rowTitle gray888">手续费</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="mainRowRight gray333" ng-bind="'¥ 0.50'"></div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
    <%--</div>--%>
    <%--<input ng-show="type=='Seller'" type="submit" value="完成" class="submitBtn bgBlue3" ng-click="goPage('/store/storeAccount')"/>--%>
    <%--<input ng-show="type=='Factor'" type="submit" value="完成" class="submitBtn bgBlue3" ng-click="goPage('/home/cardIssuingAccount')"/>--%>
</div>