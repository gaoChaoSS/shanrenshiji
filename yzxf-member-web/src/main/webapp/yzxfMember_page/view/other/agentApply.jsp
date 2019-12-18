<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="other_agentApply_Ctrl" class="d_content form_section title_section">
    <div class="overflowPC">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBackFun()"></span>
            代理商申请
        </div>
        <form ng-submit="firstSubmit()" ng-show="userPending.status==0 || userPending.status==0.3">
            <div class="sectionMain">
                <div ng-show="userPending.status==0" style="padding:10px 20px;">
                    注:尊敬的会员,在您提交代理商申请后,我们的工作人员会在7日内给您答复!
                </div>
                <div ng-show="userPending.status==0.3" style="padding:10px 20px;">
                    对不起,您的代理商申请未通过审核,审批不通过原因:{{userPending.explain}}
                </div>
            </div>

            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        联系人:
                    </div>
                    <input type="text" ng-model="userInfo.contactPerson" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-phone iconfont iconBig"></span>
                        联系手机:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.phone"/>
                </div>
                <div class="mainRow" ng-click="goPage('/home/location/selectedArea/agentApplyArea')">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span>所在地区</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
            </div>

            <button type="submit" class="submitBtn" ng-bind="userPending.status==0.3?'重新提交申请':'申请'"></button>
        </form>

        <div ng-show="userPending.status==0.1">
            <div class="sectionMain textCenter" style="padding:30px 0">
                尊敬的会员:您的代理商资格正在审核中!
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-icon1 iconfont iconBig"></span> 联系人:</div>
                    <div class="rowInput" ng-bind="userInfo.contactPerson"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-phone iconfont iconBig"></span> 联系手机:</div>
                    <div class="rowInput" ng-bind="userInfo.phone"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi1 iconfont iconBig"></span> 所在地区:</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                </div>
            </div>
        </div>

        <form ng-submit="submitForm()" ng-show="userPending.status==0.2 || userPending.status==3">
            <div class="sectionMain">
                <div ng-show="userPending.status==0.2" style="padding:10px 20px;">
                    尊敬的会员:您的代理商申请初步审核已通过,请进一步完善资料!
                </div>
                <div ng-show="userPending.status==3" style="padding:10px 20px;">
                    对不起,您的代理商申请未通过审核,审批不通过原因:{{userPending.explain}}
                </div>
                <div ng-click="upSample=true" class="submitBtn"
                     style="width: 50%;font-size: 15px;text-align: center;line-height: 42px" >点击查看上传图片规范</div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-card iconfont iconBig"></span>
                        代理商名称:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.name"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        联系人:
                    </div>
                    <input type="text" ng-model="userInfo.contactPerson" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-phone iconfont iconBig"></span>
                        联系手机:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.phone"/>
                </div>

                <div class="mainRow" ng-click="goPage('/home/location/selectedArea/agentApplyArea')">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span>所在地区</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span>所在街道:</div>
                    <textarea class="rowTextarea2" ng-model="userInfo.address" placeholder="填写所在街道"
                              style="height:100px;"></textarea>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-cash iconfont iconBig"></span>
                        开户总行:
                    </div>
                    <div class="rowInput" ng-bind="userInfo.bankType" ng-click="setShowPage(0)" style="height: 50px;"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight" ng-click="setShowPage(0)"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-cash iconfont iconBig"></span>
                        开户支行:
                    </div>
                    <input type="text" ng-model="userInfo.bankName" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-dizhi1 iconfont iconBig"></span>
                        支行城市:
                    </div>
                    <div class="rowInput" ng-bind="userInfo.bankCity" ng-click="setShowPage(1)" style="height: 50px;"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight" ng-click="setShowPage(1)"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        银行账号:
                    </div>
                    <input type="text" ng-model="userInfo.bankId" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        户名:
                    </div>
                    <input type="text" ng-model="userInfo.bankUser" class="rowInput"/>
                </div>
                <%--<div class="mainRow">--%>
                    <%--<div class="rowTitle">--%>
                        <%--<span class="icon-shouji iconfont iconBig"></span>--%>
                        <%--持卡人电话:--%>
                    <%--</div>--%>
                    <%--<input type="text" ng-model="userInfo.bankUserPhone" class="rowInput"/>--%>
                <%--</div>--%>
                <%--<div class="mainRow">--%>
                    <%--<div class="rowTitle">--%>
                        <%--<span class="icon-shimingrenzheng1 iconfont iconBig"></span>--%>
                        <%--持卡人身份证:--%>
                    <%--</div>--%>
                    <%--<input type="text" ng-model="userInfo.bankUserCardId" class="rowInput"/>--%>
                <%--</div>--%>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        银行卡正反面/开户许可证:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('bankImg')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'bankImg')"/>
                        <div ng-repeat="imgMore1 in imgMore.bankImg">
                            <div class="floatLeft positionRelative"
                                 ng-show="imgMore1!=null && imgMore1!=''">
                                <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{imgMore1}}&wh=300_300"/>
                                <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItemMore('bankImg',$index)"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人:
                    </div>
                    <input type="text" ng-model="userInfo.legalPerson" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证:
                    </div>
                    <input type="text" ng-model="userInfo.realCard" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span>
                        营业执照:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('businessLicense')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'businessLicense')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.businessLicense!=null && userInfo.businessLicense!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.businessLicense}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('businessLicense')"></div>
                        </div>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证正面照:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('idCardImgFront')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'idCardImgFront')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.idCardImgFront!=null && userInfo.idCardImgFront!=''">
                            <img class="iconImg4"
                                 ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgFront}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('idCardImgFront')"></div>
                        </div>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证背面照:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('idCardImgBack')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'idCardImgBack')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.idCardImgBack!=null && userInfo.idCardImgBack!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgBack}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('idCardImgBack')"></div>
                        </div>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证手持照:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('idCardImgHand')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'idCardImgHand')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.idCardImgHand!=null && userInfo.idCardImgHand!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgHand}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('idCardImgHand')"></div>
                        </div>
                    </div>
                </div>
            </div>
            <button type="submit" class="submitBtn" ng-bind="userPending.status==3?'重新提交复审':'提交复审'"></button>
        </form>
        <div ng-show="userPending.status==1 || userPending.status==2">
            <div class="sectionMain textCenter" ng-show="userPending.status==1" style="padding:30px 0">
                尊敬的会员您好:您的代理商资格正在审核中!
            </div>
            <div class="sectionMain textCenter" ng-show="userPending.status==2" style="padding:30px 0">
                尊敬的会员您好:恭喜您,您的代理商申请已通过!
            </div>
            <div class="sectionMain">
                <div class="mainRow" ng-show="userPending.status==2">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 登录名:</div>
                    <div class="rowInput" ng-bind="userInfo.loginName"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 代理商名称:</div>
                    <div class="rowInput" ng-bind="userInfo.name"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-icon1 iconfont iconBig"></span> 联系人:</div>
                    <div class="rowInput" ng-bind="userInfo.contactPerson"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-phone iconfont iconBig"></span> 联系手机:</div>
                    <div class="rowInput" ng-bind="userInfo.phone"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi1 iconfont iconBig"></span> 所在地区:</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span> 所在街道:</div>
                    <div class="rowInput" ng-bind="userInfo.address"></div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-svgmoban56 iconfont iconBig"></span> 银行账号:</div>
                    <div class="rowInput" ng-bind="userInfo.bankId"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-cash iconfont iconBig"></span> 开户总行:</div>
                    <div class="rowInput" ng-bind="userInfo.bankType"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-cash iconfont iconBig"></span> 开户支行:</div>
                    <div class="rowInput" ng-bind="userInfo.bankName"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span> 开户支行城市:</div>
                    <div class="rowInput" ng-bind="userInfo.bankProvince+' '+userInfo.bankCity"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-icon1 iconfont iconBig"></span> 户名:</div>
                    <div class="rowInput" ng-bind="userInfo.bankUser"></div>
                </div>
                <%--<div class="mainRow">--%>
                    <%--<div class="rowTitle"><span class="icon-shouji iconfont iconBig"></span> 持卡人电话:</div>--%>
                    <%--<div class="rowInput" ng-bind="userInfo.bankUserPhone"></div>--%>
                <%--</div>--%>
                <%--<div class="mainRow">--%>
                    <%--<div class="rowTitle"><span class="icon-shimingrenzheng1 iconfont iconBig"></span> 持卡人身份证:</div>--%>
                    <%--<div class="rowInput" ng-bind="userInfo.bankUserCardId"></div>--%>
                <%--</div>--%>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        银行卡正反面/开户许可证:
                    </div>
                    <div ng-repeat="imgMore2 in imgMore.bankImg">
                        <img class="iconImg7" ng-show="imgMore2!=null && imgMore2!=''"
                             ng-src="/s_img/icon.jpg?_id={{imgMore2}}&wh=650_0"/>
                    </div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-shenfenzheng iconfont iconBig"></span> 法人:</div>
                    <div class="rowInput" ng-bind="userInfo.legalPerson"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-shenfenzheng iconfont iconBig"></span> 法人身份证:</div>
                    <div class="rowInput" ng-bind="userInfo.realCard"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span>
                        营业执照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.businessLicense!=null && userInfo.businessLicense!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.businessLicense}}&wh=300_300"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        身份证正面照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.idCardImgFront!=null && userInfo.idCardImgFront!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgFront}}&wh=300_300"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        身份证背面照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.idCardImgBack!=null && userInfo.idCardImgBack!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgBack}}&wh=300_300"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        身份证手持照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.idCardImgHand!=null && userInfo.idCardImgHand!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgHand}}&wh=300_300"/>
                </div>
                <div class="mainRow" ng-show="userPending.status==2">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        合同照片:
                    </div>
                    <div ng-repeat="imgMore3 in imgMore.contractImg">
                        <img class="iconImg7" ng-show="imgMore3!=null && imgMore3!=''"
                             ng-src="/s_img/icon.jpg?_id={{imgMore3}}&wh=650_0"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="hideMenu" ng-show="upSample" ng-click="upSample=false">
            <div class="errorMain memberMain" style="height: 500px;border-radius: 0px">
                <div class="errorMainRow content" style="height: 100%;overflow: auto;text-align: left;padding: 6px 10px;">
                    <img style="width:100%" src="/yzxfMember_page/img/shili.jpg" alt="">
                </div>
            </div>
        </div>

        <button type="submit" class="submitBtn" ng-show="userPending.status!=0.2 && userInfo.creatorType=='other'"
                ng-bind="'再次申请'" ng-click="setOtherId();checkPendType()"></button>
    </div>

    <div ng-show="showPage[0]" class="hideMenu" style="background:#fff;overflow-y:auto">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff"  ng-click="setShowPage(0)"></span>
            开户总行
        </div>
        <div class="overflowPC">
            <div class="mainRow lineHeight50" style="padding: 0 5%;"
                 ng-repeat="bank in bankList" ng-bind="bank.name"
                 ng-click="userInfo.bankType=bank.name;userInfo.bankTypeValue=bank.bankId;setShowPage(0)"></div>
        </div>
    </div>
    <div ng-show="showPage[1]" class="hideMenu" style="background:#fff;overflow-y:auto;overflow-x: hidden;">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff"  ng-click="setShowPage(1)"></span>
            支行城市
        </div>

        <div class="search1" style="padding: 0 20px;">
            <input type="text" ng-model="keyword" placeholder="关键字搜索"
                   style="width: 100%;border-bottom: 1px solid #ccc;border-radius: 0;">
        </div>
        <div class="sectionLocation sectionLocation3">
            <div class="colsLocation" style="height: 100%;">
                <div ng-repeat="p in bankCityList[0] | filter:keyword as show" class="rowLocation">
                    <div ng-bind="p.province" ng-click="getBankCity('city',p)"
                         ng-class="p.provinceValue==selectBankCity[0].provinceValue?'locationSelected':'gray333'"></div>
                </div>
            </div>
            <div class="colsLocation" style="height: 100%;border-left: 1px solid #eee;"
                 ng-show="selectBankCity[0].provinceValue!=null && selectBankCity[0].provinceValue!=''">
                <div ng-repeat="city in bankCityList[1] | filter:keyword as showCity" class="rowLocation">
                    <div ng-bind="city.city" ng-class="city.cityValue==userInfo.bankCityValue?'locationSelected':'gray333'"
                         ng-click="userInfo.bankCity=city.city;userInfo.bankCityValue=city.cityValue;setShowPage(1)"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="hideMenu" style="z-index: 999;" ng-show="showUploadName!=null && showUploadName!=''">
        <div class="hide-bottom">
            <button>
                拍照
                <input type="file" name="file" capture="camera" accept="image/*"
                       onchange="angular.element(this).scope().uploadFile(this)"/>
            </button>
            <button>
                相册
                <input type="file" name="file"
                       onchange="angular.element(this).scope().uploadFile(this)"/>
            </button>
            <button ng-click="closeUpload()">取消</button>
        </div>
    </div>


<%--<div ng-show="showMap" class="hideMenu" style="background:#fff">--%>
        <%--<div class="title titleRedBottom">--%>
            <%--<span class="icon-left-1 iconfont titleBack whitefff" ng-click="showMap=false"></span>--%>
            <%--位置经纬度--%>
        <%--</div>--%>
        <%--<div id="container" style="height:500px;margin-top: 10px;"></div>--%>
    <%--</div>--%>
</div>