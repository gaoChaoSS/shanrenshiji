<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection" ng-show="dataPage.$$selectedItem.explain!=null && dataPage.$$selectedItem.explain!=''
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
    <div ng-show="entityTitle == '服务站管理'">
        <span>密码:</span>
        <button class="btn1" ng-click="resetUserPwd()">重置</button>
    </div>
</div>
<div class="popSection flex2" ng-show="pendingBtnCheck">
    <div class="popTitle">权限管理</div>
    <div>
        <span>更改状态:</span>
            <span class="iconfont font25px"
                  ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"
                  ng-click="userInfo.canUse=!userInfo.canUse"></span>
    </div>
</div>
<div class="popSection flex2">
    <div class="popTitle">服务站资料</div>
    <div>
        <span>服务站名称:</span>
        <span ng-bind="userInfo.name"></span>
    </div>
    <div ng-show="entityTitle=='待复审'">
        <span>当前状态:</span>
        <span ng-bind="userCanUse?'可用':'禁用'"></span>
        <%--<span class="iconfont font25px" ng-class="userCanUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"></span>--%>
    </div>
    <div ng-show="entity!='UserPending'">
        <span>创建时间:</span>
        <span ng-bind="showYFullTime(userInfo.createTime)"></span>
    </div>
    <div>
        <span>联系人:</span>
        <span ng-bind="userInfo.contactPerson"></span>
    </div>
    <div>
        <span>联系电话:</span>
        <span ng-bind="userInfo.mobile"></span>
    </div>
    <div style="width:100%;white-space: inherit">
        <span>当前地址:</span>
        <span ng-bind="address"></span>
    </div>
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
            <div>银行卡正反面</div>
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
    <div style="width:100%">
        <span>身份证:</span>
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
    <div style="min-height:70px">
        <span style="min-width:100px">身份证手持照:</span>
        <img class="popImgMini" src="/img/bindBankCard2.png" ng-src="{{iconImgUrl(userInfo.idCardImgHand)}}"
             ng-click="showImgFun(userInfo.idCardImgHand)"/>
    </div>
    <div style="min-height:70px;width:100%" ng-show="!isShowContract">
        <span style="min-width:100px">合同照片:</span>
        <div class="imgMore1 marginLeft100">
            <div ng-repeat="imgMore2 in imgMore.contractImg">
                <img class="popImgMini" ng-src="{{iconImgUrl(imgMore2)}}"
                     ng-click="showImgFun(imgMore2)"/>
            </div>
        </div>
    </div>
    <div style="min-height:70px;width:100%" ng-show="isShowContract">
        <span style="min-width:100px">合同照片:</span>
        <div class="btn7">上传
            <input type="file" name="file" class="btnUpload3"
                   onchange="angular.element(this).scope().uploadFile(this,'contractImg')"/>
        </div>
        <div class="imgMore1">
            <div ng-repeat="imgMore3 in imgMore.contractImg"
                 ng-show="imgMore3!=null && imgMore3!=''">
                <img class="popImgMini" ng-src="{{iconImgUrl(imgMore3)}}"
                     ng-click="showImgFun(imgMore3)"/>
                <div class="icon-cuowu iconfont btn8" ng-click="delFileItemMore('contractImg',$index)"></div>
            </div>
        </div>
    </div>
</div>

<div class="popSection flex2" ng-show="entity!='UserPending'">
    <div class="popTitle">持卡信息</div>
    <div style="width:100%">
        <span>总剩余卡量:</span>
        <span ng-bind="userInfo.surplusCardNum"></span>
    </div>

    <div class="sectionTable" style="width:100%">
        <div>
            <div style="width:40%">起始号段</div>
            <div style="width:40%">结束号段</div>
            <div style="width:20%">数量</div>
        </div>
        <div ng-repeat="card in cardList">
            <div style="width:40%" ng-bind="card.startCardNo"></div>
            <div style="width:40%" ng-bind="card.endCardNo"></div>
            <div style="width:20%" ng-bind="card.cardNum"></div>
        </div>
    </div>
    <div class="isNullBox" ng-show="cardList==null || cardList.length==0" style="margin: 60px auto;">
        <div class="iconfont icon-meiyouneirong isNullIcon"></div>
        <div class="font25px colorGrayccc">没有数据</div>
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
<div class="popSectionPage" ng-class="winCheck?'bottom0':''" ng-show="pendingBtnCheck && entityTitle == '服务站管理'">
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