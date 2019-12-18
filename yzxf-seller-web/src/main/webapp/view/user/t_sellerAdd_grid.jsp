<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-show="popWinTitle == '修改'?isModify:checkNext">
    <div class="popSection" ng-if="agent.level==4 || popWinTitle=='新增'">
        <div class="popTitle" style="margin-top:0">归属</div>
        <div style="width:100%">
            <div class="belongArea1" ng-include="'/temp_new/agent_select.jsp'"></div>
        </div>
        <div style="width:100%" ng-show="false">{{userInfo.belongAreaValue=agentSelectValue[0]}}</div>
    </div>
    <div class="popSection" ng-if="agent.level==1 && popWinTitle == '修改'">
        <div class="popTitle" style="margin-top:0">归属</div>
        <div style="width:100%;white-space: inherit">
            <span>归属机构:</span>
            <span ng-bind="agentNameAll"></span>
        </div>
    </div>
    <div class="popSection flex2">
        <div class="popTitle">商家资料</div>
        <div>
            <span>商家名称:</span>
            <input type="text" ng-model="userInfo.name"/>
        </div>
        <div>
            <span>积分率:</span>
            <input type="text" ng-model="userInfo.integralRate"/> %
        </div>
        <div>
            <span>联系人:</span>
            <input type="text" ng-model="userInfo.contactPerson"/>
        </div>
        <div>
            <span>联系手机:</span>
            <input type="text" ng-model="userInfo.phone"/>
        </div>
        <div>
            <span>电子邮箱:</span>
            <input type="text" ng-model="userInfo.email"/>
        </div>
        <div>
            <span>客服电话:</span>
            <input type="text" ng-model="userInfo.serverPhone"/>
        </div>
        <div style="width:100%">
            <span>营业星期:</span>
            <span ng-bind="userInfo.openWeek==null || userInfo.openWeek==''?'':'周'+userInfo.openWeek"></span>
            <span class="btn6" style="padding: 5px 20px;margin:0" ng-click="checkWeekWin()">选择</span>
            <div class="winCon" ng-show="showWeek">
                <div class="selectWeek">
                    <div>
                        <div class="contentTitle2">选择星期</div>
                        <div class="close iconfont icon-plus" ng-click="checkWeekWin()"></div>
                    </div>
                    <div ng-repeat="week in weekList" class="contentText1"
                         ng-click="week.check=!week.check">
                        <div ng-bind="week.name"></div>
                        <div class="iconfont icon-103" ng-show="week.check"></div>
                    </div>
                    <div class="contentBtn">
                        <div class="bkColorBlue1" ng-click="setOpenWeek()">确定</div>
                        <div style="background:#aaa" ng-click="cancelWeek()">取消</div>
                    </div>
                </div>
            </div>
        </div>
        <div style="width:100%">
            <span>营业时间:</span>
            <div class="flex2">
                <div class="btn5 iconfont icon-qingchu colorRed2" style="margin: 3px 0 0 0;"
                     ng-click="timeBtn(-1,true)"></div>
                <div class="btn5 iconfont icon-icontianjia01 colorGreen1" ng-click="timeBtn(1,true)"></div>
                <div class="btn6">
                    <span ng-bind="userInfo.openTime+':00 ~ '"></span>
                    <span ng-bind="userInfo.closeTime+':00'"></span>
                </div>
                <div class="btn5 iconfont icon-icontianjia01 colorGreen1" ng-click="timeBtn(1,false)"></div>
                <div class="btn5 iconfont icon-qingchu colorRed2" style="margin: 3px 0 0 0;"
                     ng-click="timeBtn(-1,false)"></div>
            </div>
        </div>
        <div style="width:100%">
            <span>经营范围:</span>
            <select ng-model="selectOperate[0]" ng-options="operate1.name for operate1 in operateList[0]"
                    ng-change="getOperateType(selectOperate[0]._id,2)"></select>
            <select ng-model="selectOperate[1]" ng-options="operate2.name for operate2 in operateList[1]"
                    ng-change="getOperateType(selectOperate[1]._id,3)"></select>
            <select ng-model="selectOperate[2]" ng-options="operate3.name for operate3 in operateList[2]"
                    ng-change="getOperateType(selectOperate[2]._id,4)"></select>
            <span>(可不全选)</span>
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
        <div style="width:100%">
            <span>商家简介:</span>
            <input style="width: 470px;" type="text" ng-model="userInfo.intro"/>
        </div>
        <div>
            <span>商家经度:</span>
            <span ng-bind="userInfo.longitude"></span>
        </div>
        <div>
            <span>商家纬度:</span>
            <span ng-bind="userInfo.latitude"></span>
        </div>
        <div class="popTitle2" style="width:100%">
            <div ng-click="getLatAndLong()">获取经纬度坐标</div>
        </div>
        <div id="container" style="width: 80%;height: 500px;border:1px solid #ccc;margin: 0 auto;"></div>
    </div>

    <div class="popSection flex2">
        <div class="popTitle">权限管理</div>
        <div ng-show="popWinTitle=='修改'">
            <span style="width:120px">状态:</span>
            <span class="iconfont font25px" ng-init="userInfo.canUse=''"
                  ng-class="userInfo.canUse?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"
                  ng-click="userInfo.canUse=!userInfo.canUse"></span>
        </div>
        <%--<div>--%>
            <%--<span style="width:120px">现金交易权限:</span>--%>
            <%--<span class="iconfont font25px"--%>
              <%--ng-class="userInfo.isMoneyTransaction?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"--%>
              <%--ng-click="userInfo.isMoneyTransaction=!userInfo.isMoneyTransaction"></span>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<span style="width:120px">卡券核销权限:</span>--%>
            <%--<span class="iconfont font25px"--%>
              <%--ng-class="userInfo.isCouponVerification?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"--%>
              <%--ng-click="userInfo.isCouponVerification=!userInfo.isCouponVerification"></span>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<span style="width:120px">首页是否推荐:</span>--%>
            <%--<span class="iconfont font25px"--%>
              <%--ng-class="userInfo.isRecommend?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"--%>
              <%--ng-click="userInfo.isRecommend=!userInfo.isRecommend"></span>--%>
        <%--</div>--%>
        <%--<div>--%>
            <%--<span style="width:120px">是否支持在线支付:</span>--%>
            <%--<span class="iconfont font25px"--%>
              <%--ng-class="userInfo.isOnlinePay?'icon-zhengque1 colorGreen1':'icon-cuowu colorRed1'"--%>
              <%--ng-click="userInfo.isOnlinePay=!userInfo.isOnlinePay"></span>--%>
        <%--</div>--%>
    </div>
</div>
<div ng-show="popWinTitle == '修改'?isModify:!checkNext">
    <div class="popSection flex2">
        <div class="popTitle">银行账户</div>
        <div>
            <span style="width:100px">银行账号:</span>
            <input type="text" ng-model="userInfo.bankId"/>
        </div>
        <div>
            <span style="width:100px">开户行:</span>
            <input type="text" ng-model="userInfo.bankName"/>
        </div>
        <div>
            <span style="width:100px">开户行地址:</span>
            <input type="text" ng-model="userInfo.bankAddress"/>
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
                <div>银行卡</div>
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
            <span style="min-width:100px">法人:</span>
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
        <div style="min-height:70px" ng-show="userInfo.bankUser != userInfo.legalPerson">
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
        <%--<div style="min-height:70px;width:100%">--%>
            <%--<span style="min-width:100px">合同照片:</span>--%>
            <%--<div class="btn7">上传--%>
                <%--<input type="file" name="file" class="btnUpload3"--%>
                       <%--onchange="angular.element(this).scope().uploadFile(this,'contractImg')"/>--%>
            <%--</div>--%>
            <%--<div class="imgMore1">--%>
                <%--<div ng-repeat="imgMore in imgMore.contractImg"--%>
                     <%--ng-show="imgMore!=null && imgMore!=''">--%>
                    <%--<img class="popImgMini" ng-src="{{iconImgUrl(imgMore)}}"--%>
                         <%--ng-click="showImgFun(imgMore)"/>--%>
                    <%--<div class="icon-cuowu iconfont btn8" ng-click="delFileItemMore('contractImg',$index)"></div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
        <div style="min-height:70px;width:100%">
            <span style="min-width:100px">店铺门头</span>
            <div class="btn7">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile(this,'doorImg')"/>
            </div>
            <div class="imgMore1">
                <div ng-repeat="doorImg in imgMore.doorImg"
                     ng-show="doorImg!=null && doorImg!=''">
                    <img class="popImgMini" ng-src="{{iconImgUrl(doorImg)}}"
                         ng-click="showImgFun(doorImg)"/>
                    <div class="icon-cuowu iconfont btn8" ng-click="delFileItemMore('doorImg',$index)"></div>
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
            <button class="btn1" ng-click="checkCancel=false;showPopWin=false;">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkCancel=false">否</button>
        </div>
    </div>
</div>
<div class="sectionHintBk" ng-show="checkSubmit">
    <div class="sectionHint">
        <div class="lineH100px">是否提交?</div>
        <div class="flex1">
            <button class="btn1" ng-click="submitForm();closePopWin;checkSubmit=false">是</button>
            <button class="btn1 bkColorRed1" ng-click="checkSubmit=false">否</button>
        </div>
    </div>
</div>
