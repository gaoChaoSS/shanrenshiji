<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-show="popWinTitle == '修改'?isModify:checkNext">
    <div class="popSection flex2" ng-show="popWinTitle=='修改' && !winMyInfoCheck">
        <div class="popTitle">权限管理</div>
        <div>
            <span>当前状态:</span>
            <span class="iconfont font25px"
                  ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"
                  ng-click="userInfo.canUse=!userInfo.canUse"></span>
        </div>
    </div>
    <div class="popSection flex2">
        <div class="popTitle">代理商资料</div>
        <div style="width:100%">
            <%--<span><span class="iconfont icon-xingbiaoon colorRed1"></span>商家名称:</span>--%>
            <span>代理商名称:</span>
            <input type="text" ng-model="userInfo.name"/>
        </div>
        <div style="width:100%">
            <span>联系人:</span>
            <input type="text" ng-model="userInfo.contactPerson"/>
        </div>
        <div style="width:100%">
            <span>联系手机:</span>
            <input type="text" ng-model="userInfo.phone"/>
        </div>
        <div style="width:100%">
            <span>所在区域:</span>
            <select ng-model="selectArea[0]" ng-options="area1.name for area1 in areaList[0]"
                    ng-change="getArea(selectArea[0]._id,2)"></select>
            <select ng-model="selectArea[1]" ng-options="area1.name for area1 in areaList[1]"
                    ng-change="getArea(selectArea[1]._id,3)"></select>
            <select ng-model="selectArea[2]" ng-options="area1.name for area1 in areaList[2]"
                    ng-change="getArea(selectArea[2]._id,4)"></select>
            <span>(全选)</span>
        </div>
        <div style="width:100%">
            <span>所在街道:</span>
            <input style="width: 470px;" type="text" ng-model="userInfo.address"/>
        </div>
    </div>
</div>
<div ng-show="popWinTitle == '修改'?isModify:!checkNext">
    <div class="popSection flex2">
        <div class="popTitle">银行账户</div>
        <div>
            <span style="width:100px">开户总行:</span>
            <select ng-model="selectBank" ng-options="bank as bank.name for bank in bankList"
                    ng-change="setBankName(selectBank)"></select>
        </div>
        <div>
            <span style="width:100px">开户支行:</span>
            <input type="text" ng-model="userInfo.bankName"/>
        </div>
        <div>
            <span style="width:100px">开户行省份:</span>
            <select ng-model="selectBankCity[0]" ng-options="bank0 as bank0.province for bank0 in bankCityList[0]"
                    ng-change="getBankCity('city',selectBankCity[0])"></select>
        </div>
        <div style="position:relative;overflow: initial;">
            <span style="width:100px">开户行城市:</span>
            <div style="width: 60%;position: relative;margin-left: 100px;">
                <input type="text" ng-model="userInfo.bankCity" ng-blur="blurBankCity()" ng-focus="blurBankCity()" style="width: 100%;"/>
                <div class="mod-select" ng-show="isShowBankCity && userInfo.bankCity!=null && userInfo.bankCity!=''">
                    <div ng-repeat="bankCity in bankCityList[1] | filter:userInfo.bankCity as showCity"
                         ng-bind="bankCity.city" ng-click="setBankCity(bankCity)"></div>
                </div>
            </div>
        </div>
        <div>
            <span style="width:100px">银行账号:</span>
            <input type="text" ng-model="userInfo.bankId"/>
        </div>
        <div>
            <span style="width:100px">户名:</span>
            <input type="text" ng-model="userInfo.bankUser"/>
        </div>
        <%--<div>--%>
            <%--<span style="width:100px">持卡人电话:</span>--%>
            <%--<input type="text" ng-model="userInfo.bankUserPhone"/>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<span style="width:100px">持卡人身份证:</span>--%>
            <%--<input type="text" ng-model="userInfo.bankUserCardId"/>--%>
        <%--</div>--%>
        <div style="min-height:70px;width:100%">
            <span style="min-width:100px">
                <div>银行卡正反面</div>
                <div>开户许可证:</div>
            </span>
            <div class="btn7">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'bankImg')"/>
            </div>
            <div class="imgMore1">
                <div ng-repeat="bankImgMore in imgMore.bankImg"
                     ng-show="bankImgMore!=null && bankImgMore!=''">
                    <img class="popImgMini" ng-src="{{iconImgUrl(bankImgMore)}}"
                         ng-click="showImgFun(bankImgMore)"/>
                    <div class="icon-cuowu iconfont btn8" ng-click="delFileItemMore('bankImg',$index)"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="popSection flex2">
        <div class="popTitle">证照信息</div>
        <div>
            <span>法人:</span>
            <input type="text" ng-model="userInfo.legalPerson"/>
        </div>
        <div>
            <span>法人身份证:</span>
            <input type="text" ng-model="userInfo.realCard"/>
        </div>
        <div style="min-height:70px">
            <span style="min-width:100px">营业许可证:</span>
            <div class="btn7" ng-show="userInfo.businessLicense==null || userInfo.businessLicense==''">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'businessLicense')"/>
            </div>
            <div class="iconHide1" ng-show="userInfo.businessLicense!=null && userInfo.businessLicense!=''">
                <img class="popImgMini" ng-show="userInfo.businessLicense!=null && userInfo.businessLicense!=''"
                     ng-src="{{iconImgUrl(userInfo.businessLicense)}}" ng-click="showImgFun(userInfo.businessLicense)"/>
                <div class="icon-cuowu iconfont btn8" ng-click="delFileItem('businessLicense')"></div>
            </div>
        </div>
        <div style="min-height:70px">
            <span style="min-width:100px">身份证正面照:</span>
            <div class="btn7" ng-show="userInfo.idCardImgFront==null || userInfo.idCardImgFront==''">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'idCardImgFront')"/>
            </div>
            <div class="iconHide1" ng-show="userInfo.idCardImgFront!=null && userInfo.idCardImgFront!=''">
                <img class="popImgMini" ng-src="{{iconImgUrl(userInfo.idCardImgFront)}}"
                     ng-click="showImgFun(userInfo.idCardImgFront)"/>
                <div class="icon-cuowu iconfont btn8" ng-click="delFileItem('idCardImgFront')"></div>
            </div>
        </div>
        <div style="min-height:70px">
            <span style="min-width:100px">身份证背面照:</span>
            <div class="btn7" ng-show="userInfo.idCardImgBack==null || userInfo.idCardImgBack==''">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'idCardImgBack')"/>
            </div>
            <div class="iconHide1" ng-show="userInfo.idCardImgBack!=null && userInfo.idCardImgBack!=''">
                <img class="popImgMini" ng-src="{{iconImgUrl(userInfo.idCardImgBack)}}"
                     ng-click="showImgFun(userInfo.idCardImgBack)"/>
                <div class="icon-cuowu iconfont btn8" ng-click="delFileItem('idCardImgBack')"></div>
            </div>
        </div>
        <div style="min-height:70px">
            <span style="min-width:100px">身份证手持照:</span>
            <div class="btn7" ng-show="userInfo.idCardImgHand==null || userInfo.idCardImgHand==''">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'idCardImgHand')"/>
            </div>
            <div class="iconHide1" ng-show="userInfo.idCardImgHand!=null && userInfo.idCardImgHand!=''">
                <img class="popImgMini" ng-src="{{iconImgUrl(userInfo.idCardImgHand)}}"
                     ng-click="showImgFun(userInfo.idCardImgHand)"/>
                <div class="icon-cuowu iconfont btn8" ng-click="delFileItem('idCardImgHand')"></div>
            </div>
        </div>
        <div style="min-height:70px;width:100%">
            <span style="min-width:100px">合同照片:</span>
            <div class="btn7">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'contractImg')"/>
            </div>
            <div class="imgMore1">
                <div ng-repeat="imgMore in imgMore.contractImg"
                     ng-show="imgMore!=null && imgMore!=''">
                    <img class="popImgMini" ng-src="{{iconImgUrl(imgMore)}}"
                         ng-click="showImgFun(imgMore)"/>
                    <div class="icon-cuowu iconfont btn8" ng-click="delFileItemMore('contractImg',$index)"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fl btn6" ng-click="save()" ng-show="popWinTitle != '修改'">保存草稿</button>
    <button class="fr btn1 bkColorYellow1" ng-click="checkCancel=true" ng-show="!checkNext">取消</button>
    <button class="fr btn1" ng-click="checkSubmit=true" ng-show="popWinTitle == '修改'?isModify:!checkNext">提交</button>
    <button class="fr btn1" ng-click="backBtn()" ng-show="popWinTitle == '修改'?!isModify:!checkNext">上一步</button>
    <button class="fr btn1" ng-click="nextFun()" ng-show="popWinTitle == '修改'?!isModify:checkNext">下一步</button>
</div>

<div class="winCon" ng-click="closeImgFun()" ng-show="showImg!=''">
    <img class="winConImg" ng-src="{{iconImgUrl(showImg)}}"/>
</div>

<div class="sectionHintBk" ng-show="checkCancel">
    <div class="sectionHint">
        <div class="lineH100px">是否退出编辑?</div>
        <div class="flex1">
            <button class="btn1" ng-click="checkCancel=false;closeWin()">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkCancel=false">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkSubmit">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm();checkSubmit=false">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkSubmit=false">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkSuccess">
    <div class="sectionHint">
        <div class="lineH100px">您的申请已提交</div>
        <div class="flex1">
            <button class="btn1" ng-click="closeWin();closeSuccess()">关闭</button>
        </div>
    </div>
</div>
