<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection" ng-show="dataPage.$$selectedItem.status!=null && dataPage.$$selectedItem.explain!=''
            && (dataPage.$$selectedItem.status==0.3 || dataPage.$$selectedItem.status==3)">
    <div class="popTitle" style="margin-top:0">审批说明</div>
    <div style="width:100%;white-space: inherit">
        <span ng-bind="dataPage.$$selectedItem.status==3?'复审说明:':'初审说明:'"></span>
        <span ng-bind="dataPage.$$selectedItem.explain"></span>
    </div>
</div>
<div class="popSection" ng-show="agentNameAll!='' && agentNameAll!=null">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%;white-space: inherit">
        <span>归属机构:</span>
        <span ng-bind="agentNameAll"></span>
    </div>
</div>
<div class="popSection" ng-show="pendingBtnCheck && (agentNameAll=='' || agentNameAll==null)">
    <div class="popTitle" style="margin-top:0">归属</div>
    <div style="width:100%">
        <div class="belongArea1" ng-include="'/temp_new/agent_select.jsp'"></div>
    </div>
</div>
<div class="popSection flex2" ng-show="userInfo.loginName!=null && userInfo.loginName!=''">
    <div class="popTitle">账号信息</div>
    <div>
        <span>登录名:</span>
        <span ng-bind="userInfo.loginName"></span>
    </div>
    <div ng-show="entityTitle == '商家管理'">
        <span>密码:</span>
        <button class="btn1" ng-click="resetUserPwd()">重置</button>
    </div>
    <div>
        <span>状态:</span>
        <span class="iconfont font25px"
              ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"
              ng-click="pendingBtnCheck?(userInfo.canUse=!userInfo.canUse):''"></span>
    </div>
    <div style="width:100%">
        <span>关联账号:</span>
        <span ng-show="relateStore.relateStoreId==null || relateStore.relateStoreId==''">
            <span class="colorGray888">尚未关联快易帮店铺</span>
            <button class="btn1" ng-click="goRelate()">前往快易帮关联</button>
        </span>
        <span ng-show="relateStore.relateStoreId!=null && relateStore.relateStoreId!=''"
              ng-bind="'快易帮店铺ID：'+relateStore.relateStoreId">
        </span>
    </div>
</div>
<%--<div class="popSection flex2">--%>
    <%--<div class="popTitle">权限管理</div>--%>
    <%--<div>--%>
        <%--<span style="width:120px">状态:</span>--%>
        <%--<span class="iconfont font25px"--%>
              <%--ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"--%>
              <%--ng-click="pendingBtnCheck?(userInfo.canUse=!userInfo.canUse):''"></span>--%>
    <%--</div>--%>
    <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<span style="width:120px">首页是否推荐:</span>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<span class="iconfont font25px" ng-class="userInfo.isRecommend?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"&ndash;%&gt;--%>
              <%--&lt;%&ndash;ng-click="pendingBtnCheck?(userInfo.isRecommend=!userInfo.isRecommend):''"></span>&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<span style="width:120px">是否支持在线支付:</span>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<span class="iconfont font25px" ng-class="userInfo.isOnlinePay?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"&ndash;%&gt;--%>
              <%--&lt;%&ndash;ng-click="pendingBtnCheck?(userInfo.isOnlinePay=!userInfo.isOnlinePay):''"></span>&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
<%--</div>--%>
<div class="popSection flex2">
    <div class="popTitle">商家资料</div>
    <div style="width:100%">
        <span>商家名称:</span>
        <span ng-bind="userInfo.name"></span>
    </div>
    <div ng-show="pendingBtnCheck">
        <span>当前状态:</span>
        <span ng-bind="userCanUse?'可用':'禁用'"></span>
        <%--<span class="iconfont font25px" ng-class="userCanUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></span>--%>
    </div>
    <div>
        <span>积分率:</span>
        <span ng-bind="integralRate"></span>
    </div>
    <div>
        <span>联系人:</span>
        <span ng-bind="userInfo.contactPerson"></span>
    </div>
    <div>
        <span>联系电话:</span>
        <span ng-bind="userInfo.phone"></span>
    </div>
    <div>
        <span>电子邮箱:</span>
        <span ng-bind="userInfo.email"></span>
    </div>
    <div>
        <span>客服电话:</span>
        <span ng-bind="userInfo.serverPhone"></span>
    </div>
    <div>
        <span>经营范围:</span>
        <span ng-bind="userInfo.operateType"></span>
    </div>
    <div style="width:100%">
        <span>营业时间:</span>
        <span ng-bind="openWeek"></span>
    </div>
    <div style="width:100%;white-space: inherit">
        <span>当前地址:</span>
        <span ng-bind="address"></span>
    </div>
    <div style="width:100%;white-space: inherit">
        <span>商家简介:</span>
        <span ng-bind="userInfo.intro"></span>
    </div>
    <div>
        <span>商家经度:</span>
        <span ng-bind="userInfo.longitude"></span>
    </div>
    <div>
        <span>商家纬度:</span>
        <span ng-bind="userInfo.latitude"></span>
    </div>
    <div id="container" style="width: 80%;height: 500px;border:1px solid #ccc;margin: 0 auto;"></div>
</div>
<div class="popSection flex2">
    <div class="popTitle">银行账户</div>
    <div>
        <span>银行账号:</span>
        <span ng-bind="userInfo.bankId"></span>
    </div>
    <div>
        <span>开户行:</span>
        <span ng-bind="userInfo.bankName"></span>
    </div>
    <div>
        <span>开户行地址:</span>
        <span ng-bind="userInfo.bankAddress"></span>
    </div>
    <div>
        <span>户名:</span>
        <span ng-bind="userInfo.bankUser"></span>
    </div>
    <%--<div>--%>
        <%--<span>持卡人电话:</span>--%>
        <%--<span ng-bind="userInfo.bankUserPhone"></span>--%>
    <%--</div>--%>
    <%--<div>--%>
        <%--<span>持卡人身份证:</span>--%>
        <%--<span ng-bind="userInfo.bankUserCardId"></span>--%>
    <%--</div>--%>
    <div style="min-height:70px;width:100%">
        <span style="min-width:100px">
            <div>银行卡</div>
            <div>开户许可证:</div>
        </span>
        <div class="imgMore1 marginLeft100">
            <div ng-repeat="imgMore1 in imgMore.bankImg">
                <img class="popImgMini" ng-src="{{iconImgUrl(imgMore1)}}"
                     ng-click="showImgFun(imgMore1)"/>
            </div>
        </div>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">证照信息</div>
    <div>
        <span style="min-width:100px">法人:</span>
        <span ng-bind="userInfo.legalPerson"></span>
    </div>
    <div>
        <span>法人身份证:</span>
        <span ng-bind="userInfo.realCard"></span>
    </div>
    <div style="min-height:70px">
        <span style="min-width:100px">身份证正面照:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(userInfo.idCardImgFront)}}"
             ng-click="showImgFun(userInfo.idCardImgFront)"/>
    </div>
    <div style="min-height:70px">
        <span style="min-width:100px">身份证背面照:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(userInfo.idCardImgBack)}}"
             ng-click="showImgFun(userInfo.idCardImgBack)"/>
    </div>
    <div style="min-height:70px" ng-show="userInfo.bankUser!=userInfo.legalPerson">
        <span style="min-width:100px">身份证手持照:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(userInfo.idCardImgHand)}}"
             ng-click="showImgFun(userInfo.idCardImgHand)"/>
    </div>
    <div style="min-height:70px">
        <span style="min-width:100px">营业许可证:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(userInfo.businessLicense)}}"
             ng-click="showImgFun(userInfo.businessLicense)"/>
    </div>
    <%--<div style="min-height:70px;width:100%" ng-show="!isShowContract">--%>
        <%--<span style="min-width:100px">合同照片:</span>--%>
        <%--<div class="imgMore1 marginLeft100">--%>
            <%--<div ng-repeat="imgMore2 in imgMore.contractImg">--%>
                <%--<img class="popImgMini" ng-src="{{iconImgUrl(imgMore2)}}"--%>
                     <%--ng-click="showImgFun(imgMore2)"/>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div style="min-height:70px;width:100%" ng-show="isShowContract">--%>
        <%--<span style="min-width:100px">合同照片:</span>--%>
        <%--<div class="btn7">上传--%>
            <%--<input type="file" name="file" class="btnUpload3"--%>
                   <%--onchange="angular.element(this).scope().uploadFile(this,'contractImg')"/>--%>
        <%--</div>--%>
        <%--<div class="imgMore1">--%>
            <%--<div ng-repeat="imgMore3 in imgMore.contractImg"--%>
                 <%--ng-show="imgMore3!=null && imgMore3!=''">--%>
                <%--<img class="popImgMini" ng-src="{{iconImgUrl(imgMore3)}}"--%>
                     <%--ng-click="showImgFun(imgMore3)"/>--%>
                <%--<div class="icon-cuowu iconfont btn8" ng-click="delFileItemMore('contractImg',$index)"></div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div style="min-height:70px;width:100%">
        <span style="min-width:100px">店铺门头照</span>
        <div class="imgMore1 marginLeft100">
            <div ng-repeat="doorImg in imgMore.doorImg">
                <img class="popImgMini" ng-src="{{iconImgUrl(doorImg)}}"
                     ng-click="showImgFun(doorImg)"/>
            </div>
        </div>
    </div>
</div>

<div class="winCon" ng-click="closeImgFun()" ng-show="showImg!=''">
    <img class="winConImg" ng-src="{{iconImgUrl(showImg)}}"/>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-show="pendingBtnCheck && entityTitle == '待复审'">
    <%--<button class="fl btn6" ng-click="checkBtnFun(0)">退出</button>--%>
    <button class="fr btn1" ng-click="checkBtnFun(1)">审批通过</button>
    <button class="fr btn1 bkColorYellow1" ng-click="checkBtnFun(2)">审批不通过</button>
</div>

<div class="sectionHintBk" ng-show="checkBtn[0]">
    <div class="sectionHint">
        <div class="lineH100px">是否退出审核?</div>
        <div class="flex1">
            <button class="btn1" ng-click="closeWin()">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(0)">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[1]">
    <div class="sectionHint">
        <div class="lineH100px">是否提交审核结果?</div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm(2)">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(1)">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[2]">
    <div class="sectionHint">
        <div class="lineH100px">请填写审批不通过的理由</div>
        <textarea class="textarea1" placeholder="请填写200字以内的审批不通过的理由,该信息将通过客户端反馈给用户" ng-model="userInfo.explain"></textarea>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm(3)">确定</button>
            <button class="btn1 bkColorRed1" ng-click="checkBtnFun(2)">取消</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkBtn[3]">
    <div class="sectionHint">
        <div class="lineH100px">审核成功!</div>
        <div class="flex1">
            <button class="btn1 bkColorRed1" ng-click="closeWin()">确定</button>
        </div>
    </div>
</div>

<%--管理员--%>
<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-show="pendingBtnCheck && entityTitle == '商家管理'">
    <button class="fr btn1" ng-click="checkSubmit=true">提交</button>
</div>
<div class="sectionHintBk" ng-show="checkSubmit">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="modifyCanUse();checkSubmit=false">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkSubmit=false">否</button>
        </div>
    </div>
</div>

<%--重置密码--%>
<div class="sectionHintBk" ng-show="loginPwd!=null && loginPwd!=''">
    <div class="sectionHint">
        <div class="lineH50px textCenter">新密码</div>
        <div class="lineH50px textCenter colorBlue1" ng-bind="loginPwd"></div>
        <div class="flex1">
            <button class="btn1" ng-click="clearPwd()">确定</button>
        </div>
    </div>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-if="showPendCheck">
    <button class="fr btn1 bkColorYellow1" ng-click="submitModifyCheck()">转为草稿</button>
</div>
<div class="sectionHintBk" ng-show="pendRecordSubmitCheck">
    <div class="sectionHint">
        <div class="lineH30px textCenter colorBlue1">是否转为草稿?</div>
        <div class="lineH100px" ng-bind="showPendText"></div>
        <div class="flex1">
            <button class="btn1" ng-click="submitModify();submitModifyCheck()">是</button>
            <button class="btn1 bkColorRed1" ng-click="submitModifyCheck()">否</button>
        </div>
    </div>
</div>