<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_setting_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myCouponDiv">
            <div class="orderTitle">
                <div class="floatL flex1" ng-click="settingType='1'" ng-class="settingType=='1'?'clickBtn':''">收货地址</div>
                <div class="floatL flex1" ng-click="settingType='2'" ng-class="settingType=='2'?'clickBtn':''">密码修改</div>
                <div class="floatL flex1" ng-click="settingType='3';isRealName()" ng-class="settingType=='3'?'clickBtn':''">实名认证</div>
            </div>
            <div ng-show="settingType=='1'">
                <div class="addressList"  ng-repeat="itemAddress in queryAddress">
                    <div class="addressDiv floatL RePosition" ng-mouseenter="addressMouseUp($index)" ng-mouseleave="addressMouseDown()">
                        <div class="addressText" ng-bind="isNullText(itemAddress.area)+isNullText(itemAddress.address)"></div>
                        <div>
                            <span ng-bind="itemAddress.name"></span>
                            <span ng-bind="itemAddress.gender"></span>
                            <span ng-bind="itemAddress.phone"></span>
                        </div>
                        <div class="addressBtn" ng-show="addListIsHover==$index">
                            <button ng-click="setDefaultAddress(itemAddress._id)" ng-show="itemAddress.defaultAddress==false||itemAddress.defaultAddress==null||itemAddress.defaultAddress==''">设为默认</button>
                            <span ng-click="openUpdate(itemAddress._id)">修改</span>
                            <span ng-click="delAddress(itemAddress._id)">删除</span>
                        </div>
                    </div>
                    <div class="floatL" ng-show="itemAddress.defaultAddress">默认地址</div>
                </div>
                <div style="width: 90%;margin: 25px auto"  ng-click="initOpen()">
                    <span class="iconfont icon-plus colorBlue1 pointer"> 新增收货地址</span>
                </div>
            </div>
            <div ng-show="settingType=='2'" style="height: 600px">
                <div style="width: 90%;margin: 25px auto">
                    <span class="colorBlue1"> 登录密码修改</span>
                </div>
                <form ng-submit="modifyPwd()">
                    <div class="passwordDiv">旧密码:<input type="text" ng-model="oldPwd"></div>
                    <div class="passwordDiv">新密码:<input type="text" ng-model="firstPwd"></div>
                    <div class="passwordDiv">确认密码:<input style="margin-left: 15px" type="text" ng-model="secondPwd"></div>
                    <button class="topUpBtn" style="margin-left: 135px;width: 302px">确认修改</button>
                </form>
            </div>
            <div ng-show="settingType=='3'" style="height: 600px">
                <div style="width: 90%;margin: 20px auto;height: 100px" ng-show="!memberIsRealName">
                    <div class="floatL"><img src="/yzxfMall_page/img/weirenzheng.png" alt=""></div>
                    <div class="floatL" style="margin-top: 25px;margin-left: 20px;font-size: 20px;">抱歉!您还未实名认证!</div>
                </div>
                <div style="width: 90%;margin: 20px auto;height: 100px" ng-show="memberIsRealName">
                    <div class="floatL"><img src="/yzxfMall_page/img/yirenzheng.png" alt=""></div>
                    <div class="floatL" style="margin-top: 25px;margin-left: 20px;font-size: 20px;">恭喜!您已实名认证!</div>
                </div>
                <div style="width: 90%;margin: 25px auto">
                    <span ng-show="!memberIsRealName" class="colorBlue1">请填写资料立刻认证</span>
                    <span ng-show="memberIsRealName" class="colorBlue1">认证资料</span>
                </div>
                <div ng-show="!memberIsRealName">
                    <form ng-submit="submitForm()">
                        <div class="passwordDiv">所在区域:
                            <select ng-model="selectArea[0]" ng-options="area1.name for area1 in areaListEd[0]"
                                    ng-change="getArea(selectArea[0]._id,2)"></select>
                            <select ng-model="selectArea[1]" ng-options="area1.name for area1 in areaListEd[1]"
                                    ng-change="getArea(selectArea[1]._id,3)"></select>
                            <select ng-model="selectArea[2]" ng-options="area1.name for area1 in areaListEd[2]"
                                    ng-change="getArea(selectArea[2]._id,4)"></select>
                            <%--<input style="width:105px" type="text" placeholder="--省份">--%>
                            <%--<input style="width:105px" type="text" placeholder="--城市">--%>
                            <%--<input style="width:105px" type="text" placeholder="--区域">--%>
                        </div>
                        <div class="passwordDiv">街道地址:<input ng-model="address" style="width: 400px" type="text"></div>
                        <div class="passwordDiv">真实姓名:<input ng-model="realName" type="text"></div>
                        <div class="passwordDiv">身份证号:<input ng-model="cardNumber" type="text"></div>
                        <div class="passwordDiv">电子邮箱:<input ng-model="myEmail" type="text"></div>
                        <button class="topUpBtn" type="submit" style="margin-left: 150px;width: 302px">确认提交</button>
                    </form>
                </div>
                <div ng-show="memberIsRealName">
                    <div class="passwordDiv">所在区域:
                        <input style="width:185px;border: none;font-size: 15px;" type="text" readOnly="true" placeholder="{{memberInfo.realArea}}">
                    </div>
                    <div class="passwordDiv">街道地址:<input readOnly="true" style="border: none;font-size: 15px;width: 400px" type="text" placeholder="{{memberInfo.realArea+memberInfo.realAddress}}"></div>
                    <div class="passwordDiv">真实姓名:<input readOnly="true" style="border: none;font-size: 15px;" type="text"  placeholder="{{memberInfo.realName}}"></div>
                    <div class="passwordDiv">身份证号:<input readOnly="true" style="border: none;font-size: 15px;" type="text"  placeholder="{{memberInfo.idCard}}"></div>
                    <div class="passwordDiv">电子邮箱:<input readOnly="true" style="border: none;font-size: 15px;" type="text"  placeholder="{{isNullText2(memberInfo.email)}}"></div>
                </div>
            </div>
        </div>

    </div>
    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
    <%--透明遮罩层--%>
    <div class="hideMenu" ng-show="addTakeAdd">
        <div class="addNewTakesDiv">
            <form ng-submit="submitTakeAddress()">
                <div style="width: 90%;margin: 35px auto">
                    <span class="colorBlue1">新增收货地址</span>
                </div>
                <div class="passwordDiv">所在区域:
                    <select ng-model="selectArea[0]" ng-options="area1.name for area1 in areaListEd[0]"
                            ng-change="getArea(selectArea[0]._id,2)"></select>
                    <select ng-model="selectArea[1]" ng-options="area1.name for area1 in areaListEd[1]"
                            ng-change="getArea(selectArea[1]._id,3)"></select>
                    <select ng-model="selectArea[2]" ng-options="area1.name for area1 in areaListEd[2]"
                            ng-change="getArea(selectArea[2]._id,4)"></select>
                </div>
                <div class="passwordDiv">街道地址:<input ng-model="address" style="width: 400px" type="text"></div>
                <div class="passwordDiv">邮政编码:<input ng-model="postalcode" type="text"></div>
                <div class="passwordDiv">姓名:<input style="margin-left: 62px" ng-model="consignee" type="text"></div>
                <div class="passwordDiv">电话:<input style="margin-left: 62px" ng-model="phoneNumber" type="text"></div>
                <div class="passwordDiv">性别:
                    <select ng-model="checkMan" style="margin-left: 58px;width: 150px">
                        <option value="">请选择收货人性别</option>
                        <option value="男士">男士</option>
                        <option value="女士">女士</option>
                    </select>
                </div>
                <button class="topUpBtn" type="submit" style="margin-left: 150px;width: 152px;border-radius: 5px">确认</button>
                <span class="topUpBtn colorBlue1" style="width: 152px;margin-left: 70px;background: #fff;" ng-click="addTakeAdd=false">取消</span>
            </form>
        </div>
    </div>
    <%--透明遮罩层--%>
    <div class="hideMenu" ng-show="updateTakeAdd">
        <div class="addNewTakesDiv">
            <form ng-submit="updateAddress(addressId)">
                <div style="width: 90%;margin: 35px auto">
                    <span class="colorBlue1">修改收货地址</span>
                </div>
                <div class="passwordDiv">所在区域:
                    <select ng-model="selectArea[0]" ng-options="area1.name for area1 in areaListEd[0]"
                            ng-change="getArea(selectArea[0]._id,2)"></select>
                    <select ng-model="selectArea[1]" ng-options="area1.name for area1 in areaListEd[1]"
                            ng-change="getArea(selectArea[1]._id,3)"></select>
                    <select ng-model="selectArea[2]" ng-options="area1.name for area1 in areaListEd[2]"
                            ng-change="getArea(selectArea[2]._id,4)"></select>
                </div>
                <div class="passwordDiv">街道地址:<input ng-model="Uaddress" style="width: 400px" type="text"></div>
                <div class="passwordDiv">邮政编码:<input ng-model="Upostalcode" type="text"></div>
                <div class="passwordDiv">姓名:<input style="margin-left: 62px" ng-model="Uconsignee" type="text"></div>
                <div class="passwordDiv">电话:<input style="margin-left: 62px" ng-model="UphoneNumber" type="text"></div>
                <div class="passwordDiv">性别:
                    <select ng-model="UcheckMan" style="margin-left: 58px;width: 150px">
                        <option value="">请选择收货人性别</option>
                        <option value="男士">男士</option>
                        <option value="女士">女士</option>
                    </select>
                </div>
                <button class="topUpBtn" type="submit" style="margin-left: 150px;width: 152px;border-radius: 5px">确认</button>
                <span class="topUpBtn colorBlue1" style="width: 152px;margin-left: 70px;background: #fff;" ng-click="updateTakeAdd=false">取消</span>
            </form>
        </div>
    </div>
    <div ng-include="'/yzxfMall_page/temp_new/okOrFailBox.html'"></div>
</div>
