<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="agent_agentAdd_Ctrl" style="width: 100%; height: 100%;">
    <div class="section1">
        <span class="tLineHight45 selectInputMarginL30 tGry888 textSize15">新增代理商</span>
        <button class="btn1 fr bkColorGreen1" ng-click="nextFun()" ng-show="!nextCheck">下一步</button>
        <button class="btn1 fr bkColorGreen1" ng-click="submitForm()" ng-show="nextCheck">提交</button>
        <button class="btn1 fr bkColorYellow1" ng-click="nextCheck=false" ng-show="nextCheck">上一步</button>
        <button class="btn1 fr" ng-click="cancelCheck=true">取消</button>
    </div>
    <div class="section1 sectionUserInfo2" ng-show="!nextCheck">
        <div>
            <span>代理商:</span>
            <input type="text" ng-model="agentInfo.name"/>
        </div>
        <div>
            <span>所在区域:</span>
            <select ng-model="select1" ng-options="area1.name for area1 in areaList[0]" ng-change="getLocation(select1._id,2)" class="marginLeft140"></select>
            <select ng-model="select2" ng-options="area1.name for area1 in areaList[1]" ng-change="getLocation(select2._id,3)"></select>
            <select ng-model="select3" ng-options="area1.name for area1 in areaList[2]" ng-change="getLocation(select2._id,4)"></select>
        </div>
        <div>
            <span>所在街道:</span>
            <input type="text" ng-model="agentInfo.address" />
        </div>
        <div>
            <span>联系人:</span>
            <input type="text" ng-model="agentInfo.contactPerson" />
        </div>
        <div>
            <span>身份证:</span>
            <input type="text" ng-model="agentInfo.realCard" />
        </div>
        <div>
            <span>联系号码:</span>
            <input type="text" ng-model="agentInfo.phone" />
        </div>
    </div>

    <div ng-show="nextCheck">
        <div class="section1 sectionImg">
            <div class="sectionTitle1">营业执照</div>
            <div class="marginLeft140 positionR" ng-hide="agentInfo.businessLicense==null || agentInfo.businessLicense==''">
                <img ng-src="/s_img/icon.jpg?_id={{agentInfo.businessLicense}}&wh=300_300" />
                <div class="iconfont icon-guanbi btnClose1" ng-click="delFileItem('businessLicense')"></div>
            </div>
            <div class="marginLeft140 btnUpload1" ng-show="agentInfo.businessLicense==null || agentInfo.businessLicense==''">
                <input type="file" name="file" class="btnUpload2" onchange="angular.element(this).scope().uploadFile(this,'businessLicense')"/>
                点击上传图片
            </div>
        </div>
        <div class="section1 sectionImg">
            <div class="sectionTitle1">身份证正面照</div>
            <div class="marginLeft140 positionR" ng-hide="agentInfo.idCardImgFront==null || agentInfo.idCardImgFront==''">
                <img ng-src="/s_img/icon.jpg?_id={{agentInfo.idCardImgFront}}&wh=300_300" />
                <div class="iconfont icon-guanbi btnClose1" ng-click="delFileItem('idCardImgFront')"></div>
            </div>
            <div class="marginLeft140 btnUpload1" ng-show="agentInfo.idCardImgFront==null || agentInfo.idCardImgFront==''">
                <input type="file" name="file" class="btnUpload2" onchange="angular.element(this).scope().uploadFile(this,'idCardImgFront')"/>
                点击上传图片
            </div>
        </div>
        <div class="section1 sectionImg">
            <div class="sectionTitle1">身份证背面照</div>
            <div class="marginLeft140 positionR" ng-hide="agentInfo.idCardImgBack==null || agentInfo.idCardImgBack==''">
                <img ng-src="/s_img/icon.jpg?_id={{agentInfo.idCardImgBack}}&wh=300_300" />
                <div class="iconfont icon-guanbi btnClose1" ng-click="delFileItem('idCardImgBack')"></div>
            </div>
            <div class="marginLeft140 btnUpload1" ng-show="agentInfo.idCardImgBack==null || agentInfo.idCardImgBack==''">
                <input type="file" name="file" class="btnUpload2" onchange="angular.element(this).scope().uploadFile(this,'idCardImgBack')"/>
                点击上传图片
            </div>
        </div>
        <div class="section1 sectionImg">
            <div class="sectionTitle1">手持身份证照</div>
            <div class="marginLeft140 positionR" ng-hide="agentInfo.idCardImgHand==null || agentInfo.idCardImgHand==''">
                <img ng-src="/s_img/icon.jpg?_id={{agentInfo.idCardImgHand}}&wh=300_300" />
                <div class="iconfont icon-guanbi btnClose1" ng-click="delFileItem('idCardImgHand')"></div>
            </div>
            <div class="marginLeft140 btnUpload1" ng-show="agentInfo.idCardImgHand==null || agentInfo.idCardImgHand==''">
                <input type="file" name="file" class="btnUpload2" onchange="angular.element(this).scope().uploadFile(this,'idCardImgHand')"/>
                点击上传图片
            </div>
        </div>
    </div>

    <div class="sectionHintBk" ng-show="cancelCheck">
        <div class="sectionHint">
            <div class="lineH100px">是否退出编辑?</div>
            <div class="flex1">
                <button class="btn1" ng-click="cancelBtn()">是</button>
                <button class="btn1 bkColorRed1" ng-click="cancelCheck=false">否</button>
            </div>
        </div>
    </div>
</div>
