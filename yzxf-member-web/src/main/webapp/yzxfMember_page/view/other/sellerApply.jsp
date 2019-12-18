<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="other_sellerApply_Ctrl" class="d_content form_section title_section">
    <div class="title titleRedBottom">
        <span class="icon-left-1 iconfont titleBack whitefff" ng-click="goBackFun()"></span>
        商户申请
    </div>
    <div ng-include="'/yzxfMember_page/view/other/agree.jsp'"></div>
    <div class="overflowPC">
        <form ng-submit="firstSubmit()" ng-show="userPending.status==0 || userPending.status==0.3">
            <div class="sectionMain">
                <div ng-show="userPending.status==0" style="padding:10px 20px;">
                    注:尊敬的会员,在您提交商户申请后,我们的工作人员会在7日内给您答复!
                </div>
                <div ng-show="userPending.status==0.3" style="padding:10px 20px;">
                    对不起,您的商户申请未通过审核,审批不通过原因:{{userPending.explain}}
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
                <div class="mainRow" ng-click="goPage('/home/location/selectedArea/sellerApplyArea')">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span>所在地区</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
            </div>

            <button type="submit" class="submitBtn" ng-bind="userPending.status==0.3?'重新申请':'申请'"></button>
        </form>

        <div ng-show="userPending.status==0.1">
            <div class="sectionMain textCenter" style="padding:30px 0">
                尊敬的会员:您的商户资格正在审核中!
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
                    注:尊敬的会员,在您提交申请后,我们的工作人员会在7日内给您答复!
                </div>
                <div ng-show="userPending.status==3" style="padding:10px 20px;">
                    对不起,您的申请未通过审核,审批不通过原因:{{userPending.explain}}
                </div>
                <div ng-click="upSample=true" class="submitBtn submitBtnRed"
                     style="width: 30%;font-size: 15px;text-align: center;line-height: 42px;margin: 20px 10%;float:left" >上传图片规范</div>
                <div ng-click="setScopeFlag('showAgreePage')" class="submitBtn submitBtnRed"
                     style="width: 30%;font-size: 15px;text-align: center;line-height: 42px;margin: 20px 10%;float:left" >服务协议</div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-card iconfont iconBig"></span>
                        商户名:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.name"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-card iconfont iconBig"></span>
                        商户简称:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.merchantsAbbreviation" placeholder="不可超过四个字">
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-card iconfont iconBig"></span>
                        商户类型:
                    </div>
                    <div class="rowInput" >
                        <select ng-options="item._id4 as item.title4 for item in merchantsTypeList" ng-model="userInfo.merchantsType" ></select>
                    </div>
                </div>
                <div class="mainRow" >
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        商户证件类型:
                    </div>
                    <div class="rowInput" >
                        <select ng-options="item._id6 as item.title6 for item in companyCertificateTypeList" ng-model="userInfo.companyCertificateType"></select>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-card iconfont iconBig"></span>
                        线下线上标志:
                    </div>
                    <div class="rowInput" >
                        <select  ng-options="item._id3 as item.title3 for item in markList" ng-model="userInfo.mark"></select>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-cshy-rmb2 iconfont iconBig"></span>
                        积分率(%):
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.integralRate"/>
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
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-daifahuo iconfont iconBig"></span>
                        电子邮箱:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.email"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-daifahuo iconfont iconBig"></span>
                        支付方式:
                    </div>
                    <div class="rowInput" >
                        <input type="checkbox"  ng-model="userInfo.Alipay" />支付宝
                        <input type="checkbox" ng-model="userInfo.Wechat" />微信
                        <input type="checkbox" ng-model="userInfo.Unionpay"/>银联
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-daifahuo iconfont iconBig"></span>
                        注册资本(万):
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.registeredCapital"/>
                </div>
            </div>
            <div class="sectionMain">
                <%--<div class="mainRow" ng-click="userInfo.isOnlinePay=!userInfo.isOnlinePay">--%>
                <%--<div class="rowTitle">--%>
                <%--<span class="icon-fu iconfont iconBig"></span>--%>
                <%--在线支付:--%>
                <%--</div>--%>
                <%--<span class="iconfont rowInput textSize20"--%>
                <%--ng-class="userInfo.isOnlinePay?'icon-zhengque1 limeGreen':'icon-cuowu deepRed'"></span>--%>
                <%--</div>--%>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-phone iconfont iconBig"></span>
                        客服电话:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.serverPhone"/>
                </div>
                <div class="mainRow" ng-click="showWeek=true">
                    <div class="rowTitle">
                        <span class="icon-shijian iconfont iconBig"></span>
                        营业星期
                    </div>
                    <div class="rowInput" ng-bind="userInfo.openWeek"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="winCon" ng-show="showWeek">
                    <div class="selectWeek">
                        <div>
                            <div class="contentTitle2">营业星期</div>
                            <div class="close iconfont icon-plus" ng-click="closeWeek()"></div>
                        </div>
                        <div ng-repeat="week in weekList" class="contentText1"
                             ng-click="week.check=!week.check">
                            <div ng-bind="week.name"></div>
                            <div class="iconfont icon-103" ng-show="week.check"></div>
                        </div>
                        <div class="contentBtn">
                            <div class="bgBblue2" ng-click="setOpenWeek()">确定</div>
                            <div class="bgGrayaaa" ng-click="cancelWeek()">取消</div>
                        </div>
                    </div>
                </div>
                <div class="mainRow" ng-click="showOpenTime=true">
                    <div class="rowTitle">
                        <span class="icon-shijian iconfont iconBig"></span>
                        营业时间
                    </div>
                    <div class="rowInput">
                        <span ng-bind="userInfo.openTime+':00 ~ '"></span>
                        <span ng-bind="userInfo.closeTime+':00'"></span>
                    </div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="winCon" ng-show="showOpenTime">
                    <div class="selectWeek">
                        <div>
                            <div class="contentTitle2">营业时间</div>
                            <div class="close iconfont icon-plus" ng-click="showOpenTime=false"></div>
                        </div>
                        <div class="btn2">
                            <div class="iconfont icon-icon19" ng-click="timeBtn(-1,true)"></div>
                            <div class="iconfont icon-icon19" ng-click="timeBtn(-1,false)"></div>
                        </div>
                        <div class="textCenter">
                            <span class="textSize20" ng-bind="userInfo.openTime+':00 ~ '"></span>
                            <span class="textSize20" ng-bind="userInfo.closeTime+':00'"></span>
                        </div>
                        <div class="btn2">
                            <div class="iconfont icon-iconfontarrows" ng-click="timeBtn(1,true)"></div>
                            <div class="iconfont icon-iconfontarrows" ng-click="timeBtn(1,false)"></div>
                        </div>
                    </div>
                </div>
                <div class="mainRow" ng-click="goPage('store/operateType/operateUrl/sellerApply')">
                    <div class="rowTitle"><span class="icon-wxbmingxingdianpu iconfont iconBig"></span> 经营范围</div>
                    <div class="rowInput" ng-bind="userInfo.operateType"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="mainRow" ng-click="goPage('/home/location/selectedArea/sellerApplyArea')">
                    <div class="rowTitle"><span class="icon-dizhi1 iconfont iconBig"></span> 所在地区</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span> 所在街道:</div>
                    <textarea class="rowTextarea2" ng-model="userInfo.address" placeholder="填写所在街道"
                              style="height:100px;"></textarea>
                </div>
                <div class="mainRow" ng-click="getLatAndLong()">
                    <div class="rowTitle"><span class="icon-dizhi1 iconfont iconBig"></span> 点击获取经纬度</div>
                    <div class="rowInput" style="margin-left: 150px;">
                        <div ng-bind="userInfo.longitude"></div>
                        <div ng-bind="userInfo.latitude"></div>
                    </div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-icon0732xieyi iconfont iconBig"></span> 商家简介:</div>
                    <textarea class="rowTextarea2" ng-model="userInfo.intro" placeholder="填写商家简介(可不填)"
                              style="height:100px;"></textarea>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        银行账号:
                    </div>
                    <input type="text" ng-model="userInfo.bankId" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        账户类型:
                    </div>
                    <div class="rowInput" >
                        <select ng-options="item._id7 as item.title7 for item in accountTypeList" ng-model="userInfo.accountType"></select>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        开户人类型:
                    </div>
                    <div class="rowInput" >
                        <select ng-options="item._id8 as item.title8 for item in contactTypeList" ng-model="userInfo.contactType"></select>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-cash iconfont iconBig"></span>
                        开户行:
                    </div>
                    <input type="text" ng-model="userInfo.bankName" class="rowInput" placeholder="例:中国人民银行青龙县支行"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-dizhi iconfont iconBig"></span>
                        开户行地址:
                    </div>
                    <input type="text" ng-model="userInfo.bankAddress" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-icon1 iconfont iconBig"></span>
                        户名:
                    </div>
                    <input type="text" ng-model="userInfo.bankUser" class="rowInput" ng-change="checkBankUser()"/>
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
            <div class="sectionMain" ng-show="userInfo.contactType == '2'">
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 代理人授权书照片:</div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('authphoto')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'authphoto')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.authphoto!=null && userInfo.authphoto!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.authphoto}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('authphoto')"></div>
                        </div>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 代理人证件正面照:</div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('agentphotof')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'agentphotof')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.agentphotof!=null && userInfo.agentphotof!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.agentphotof}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('agentphotof')"></div>
                        </div>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 代理人证件背面照:</div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('agentphotob')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'agentphotob')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.agentphotob!=null && userInfo.agentphotob!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.agentphotob}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('agentphotob')"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="sectionMain" ng-show="userInfo.merchantsType != '0'">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-daifahuo iconfont iconBig"></span>
                        证件注册地址:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.registeredAddress"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        商户证件号码:
                    </div>
                    <input type="text" class="rowInput" ng-model="userInfo.companyIdNumber">
                </div>

                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 营业执照:</div>
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
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-svgmoban56 iconfont iconBig"></span>
                        法人:
                    </div>
                    <input type="text" ng-model="userInfo.legalPerson" class="rowInput" ng-change="checkBankUser()"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle">
                        <span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证:
                    </div>
                    <input type="text" ng-model="userInfo.realCard" class="rowInput"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-icon0732xieyi iconfont iconBig"></span> 法人证件有效期至:</div>
                    <div style="padding-left: 160px;padding-top: 5px">
                        <input type="text" ng-model="userInfo.lpcertval"  placeholder="例:2039-02-08"/>
                        <button type="button" style="width: 50px" ng-click="changeLpcertval()">长期</button>
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
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgFront}}&wh=300_300"/>
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
                        法人手持身份证照片:
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
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        店铺门头照:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('doorImg')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'doorImg')"/>
                        <div ng-repeat="doorImg in imgMore.doorImg">
                            <div class="floatLeft positionRelative"
                                 ng-show="doorImg!=null && doorImg!=''">
                                <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{doorImg}}&wh=300_300"/>
                                <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItemMore('doorImg',$index)"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        经营场所照:
                    </div>
                    <div class="rowInput" style="width: 100%;margin-left:0">
                        <div class="rowBox" ng-click="showUpload('sitephoto')">选择…</div>
                        <input type="file" name="file" class="rowBox" ng-if="!isMobile"
                               onchange="angular.element(this).scope().uploadFile(this,'sitephoto')"/>
                        <div class="floatLeft positionRelative"
                             ng-show="userInfo.sitephoto!=null && userInfo.sitephoto!=''">
                            <img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{userInfo.sitephoto}}&wh=300_300"/>
                            <div class="icon-cuowu iconfont iconImg5" ng-click="delFileItem('sitephoto')"></div>
                        </div>
                    </div>
                </div>

                <%--<div class="mainRow">--%>
                <%--<div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>--%>
                <%--合同照片:--%>
                <%--</div>--%>
                <%--<div class="rowInput" style="width: 100%;margin-left:0">--%>
                <%--<div class="rowBox">选择…</div>--%>
                <%--<input type="file" name="file" class="rowBox"--%>
                <%--onchange="angular.element(this).scope().uploadFile(this,'contractImg')"/>--%>
                <%--<div ng-repeat="imgMore in userInfo.contractImg">--%>
                <%--<div class="floatLeft positionRelative" ng-show="imgMore.fileId!=null && imgMore.fileId!=''">--%>
                <%--<img class="iconImg4" ng-src="/s_img/icon.jpg?_id={{imgMore.fileId}}&wh=300_300"/>--%>
                <%--<div class="icon-cuowu iconfont iconImg5" ng-click="delFileItemMore($index)"></div>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--</div>--%>
            </div>
            <button type="submit" class="submitBtn" ng-bind="userPending.status==3?'重新提交复审':'提交复审'"></button>
        </form>

        <div ng-show="userPending.status==1 || userPending.status==2 || userPending.status==4">
            <div class="sectionMain textCenter" ng-show="userPending.status==1 || userPending.status==4" style="padding:30px 0">
                尊敬的会员您好:您的商户资格正在审核中!
            </div>
            <div class="sectionMain textCenter" ng-show="userPending.status==2" style="padding:30px 0">
                尊敬的会员您好:恭喜您,您的商户申请已通过!
            </div>
            <div class="sectionMain">
                <div class="mainRow" ng-show="userPending.status==2">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 登录名:</div>
                    <div class="rowInput" ng-bind="userInfo.loginName"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 商户名:</div>
                    <div class="rowInput" ng-bind="userInfo.name"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 商户简称:</div>
                    <div class="rowInput" ng-bind="userInfo.merchantsAbbreviation"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 商户类型:</div>
                    <div class="rowInput" ng-bind="userInfo.merchantsType"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 商户证件类型:</div>
                    <div class="rowInput" ng-bind="userInfo.companyCertificateType"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-card iconfont iconBig"></span> 线上线下标志:</div>
                    <div class="rowInput" ng-bind="userInfo.mark"></div>
                </div>

                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-cshy-rmb2 iconfont iconBig"></span> 积分率:</div>
                    <div class="rowInput" ng-bind="userInfo.integralRate+'%'"></div>
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
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 电子邮箱:</div>
                    <div class="rowInput" ng-bind="userInfo.email"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 支付方式:</div>
                    <div class="rowInput" ng-bind="userInfo.paytype"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 注册资本(万):</div>
                    <div class="rowInput" ng-bind="userInfo.registeredCapital"></div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-phone iconfont iconBig"></span> 客服电话:</div>
                    <div class="rowInput" ng-bind="userInfo.serverPhone"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-shijian iconfont iconBig"></span> 营业星期:</div>
                    <div class="rowInput" ng-bind="'周'+userInfo.openWeek"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-shijian iconfont iconBig"></span> 营业时间:</div>
                    <div class="rowInput" ng-bind="userInfo.openTime+'~'+userInfo.closeTime"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-phone iconfont iconBig"></span> 经营范围:</div>
                    <div class="rowInput" ng-bind="userInfo.operateType"></div>
                </div>

                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi1 iconfont iconBig"></span> 所在地区:</div>
                    <div class="rowInput" ng-bind="userInfo.area"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span> 所在街道:</div>
                    <div class="rowInput" ng-bind="userInfo.address"></div>
                </div>
                <div class="mainRow" ng-click="initMap('noClick')">
                    <div class="rowTitle"><span class="icon-dizhi1 iconfont iconBig"></span> 经纬度</div>
                    <div class="rowInput">
                        <div ng-bind="userInfo.longitude"></div>
                        <div ng-bind="userInfo.latitude"></div>
                    </div>
                    <div class="icon-right-1-copy iconfont mainRowRight"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-icon0732xieyi iconfont iconBig"></span> 商家简介:</div>
                    <div class="rowInput" ng-bind="userInfo.intro"></div>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-svgmoban56 iconfont iconBig"></span> 银行账号:</div>
                    <div class="rowInput" ng-bind="userInfo.bankId"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-svgmoban56 iconfont iconBig"></span> 账户类型:</div>
                    <div class="rowInput" ng-bind="userInfo.accountType"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-svgmoban56 iconfont iconBig"></span> 开户人类型:</div>
                    <div class="rowInput" ng-bind="userInfo.contactType"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-cash iconfont iconBig"></span> 开户行:</div>
                    <div class="rowInput" ng-bind="userInfo.bankName"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-dizhi iconfont iconBig"></span> 开户行地址:</div>
                    <div class="rowInput" ng-bind="userInfo.bankAddress"></div>
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
            <div class="sectionMain" ng-show="userInfo.contactType == '代理人'">
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 代理人授权书:</div>
                    <img class="iconImg7" ng-show="userInfo.authphoto!=null && userInfo.authphoto!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.authphoto}}&wh=650_0"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 代理人证件正面照:</div>
                    <img class="iconImg7" ng-show="userInfo.agentphotof!=null && userInfo.agentphotof!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.agentphotof}}&wh=650_0"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 代理人证件背面照:</div>
                    <img class="iconImg7" ng-show="userInfo.agentphotob!=null && userInfo.agentphotob!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.agentphotob}}&wh=650_0"/>
                </div>
            </div>
            <div class="sectionMain" ng-show="userInfo.merchantsType !='小微商户'">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 证件注册地址:</div>
                    <div class="rowInput" ng-bind="userInfo.registeredAddress"></div>
                </div>

                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 商户证件号码:</div>
                    <div class="rowInput" ng-bind="userInfo.companyIdNumber"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-zhifu iconfont iconBig"></span> 营业执照:</div>
                    <img class="iconImg7" ng-show="userInfo.businessLicense!=null && userInfo.businessLicense!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.businessLicense}}&wh=650_0"/>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-cash iconfont iconBig"></span> 法人:</div>
                    <div class="rowInput" ng-bind="userInfo.legalPerson"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-shenfenzheng iconfont iconBig"></span> 法人身份证:</div>
                    <div class="rowInput" ng-bind="userInfo.realCard"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle"><span class="icon-daifahuo iconfont iconBig"></span> 证件有效期:</div>
                    <div class="rowInput" ng-bind="userInfo.lpcertval"></div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证正面照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.idCardImgFront!=null && userInfo.idCardImgFront!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgFront}}&wh=650_0"/>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证背面照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.idCardImgBack!=null && userInfo.idCardImgBack!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgBack}}&wh=650_0"/>
                </div>
                <div class="mainRow" ng-show="!isNeedIdCardImgHand">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        法人身份证手持照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.idCardImgHand!=null && userInfo.idCardImgHand!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.idCardImgHand}}&wh=650_0"/>
                </div>
                <%--<div class="mainRow" ng-show="userPending.status==2">--%>
                    <%--<div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>--%>
                        <%--合同照片:--%>
                    <%--</div>--%>
                    <%--<div ng-repeat="imgMore3 in imgMore.contractImg">--%>
                        <%--<img class="iconImg7" ng-show="imgMore3!=null && imgMore3!=''"--%>
                             <%--ng-src="/s_img/icon.jpg?_id={{imgMore3}}&wh=650_0"/>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        店铺门头照:
                    </div>
                    <div ng-repeat="doorImg2 in imgMore.doorImg">
                        <img class="iconImg7" ng-show="doorImg2!=null && doorImg2!=''"
                             ng-src="/s_img/icon.jpg?_id={{doorImg2}}&wh=650_0"/>
                    </div>
                </div>
                <div class="mainRow">
                    <div class="rowTitle positionRelative"><span class="icon-shenfenzheng iconfont iconBig"></span>
                        经营场所照:
                    </div>
                    <img class="iconImg7" ng-show="userInfo.sitephoto!=null && userInfo.sitephoto!=''"
                         ng-src="/s_img/icon.jpg?_id={{userInfo.sitephoto}}&wh=650_0"/>
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


    <div ng-show="showMap" class="hideMenu" style="background:#fff">
        <div class="title titleRedBottom">
            <span class="icon-left-1 iconfont titleBack whitefff" ng-click="showMap=false"></span>
            位置经纬度
        </div>
        <div id="container" style="height:500px;margin-top: 10px;"></div>
        <button ng-click="showMap=false" class="submitBtn">确定</button>
    </div>
</div>