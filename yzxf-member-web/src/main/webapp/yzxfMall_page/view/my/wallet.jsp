<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="my_wallet_Ctrl">
    <%--头部模板--%>
    <div ng-include="mallHead"></div>
    <%--index导航--%>
    <div class="navigationDiv" ng-include="indexNavigation"></div>
    <%--中间内容--%>
    <div class="bodyWidth marginZAuto" style="height: 450px">
        <%--左边导航--%>
        <div class="floatL myPageLeftNavDiv" ng-include="myLeftNavigation"></div>
        <%--右边内容--%>
        <div class="floatL myPageRightContent">
            <div class="memberPHDiv">
                <div class="floatL memberMoney flex1">
                    <div class="floatL" style="margin-left: 20px;margin-right: 10px">我的余额</div>
                    <div class="floatL" style="color: #138bbe;font-size: 22px;margin:0 10px 0 0">¥</div>
                    <div class="floatL" style="color: #138bbe;font-size: 40px" ng-bind="wallet"></div>
                </div>
            </div>
            <div class="walletDiv">
                <div class="orderTitle">
                    <div class="floatL flex1" ng-click="changeUpType('1')" ng-class="topUpType=='1'?'clickBtn':''">余额充值</div>
                    <div class="floatL flex1" ng-click="changeUpType('2')" ng-class="topUpType=='2'?'clickBtn':''">替他人充值</div>
                    <div class="floatL flex1" ng-click="changeUpType('3')" ng-class="topUpType=='3'?'clickBtn':''">支付设置</div>
                </div>
                <div class="walletContent RePosition" ng-show="topUpType!=3">
                    <div ng-show="topUpType=='2'">
                        输入对方手机:
                        <input type="text" style="width: 200px;height: 40px;" ng-model="friendPhone">
                    </div>
                    <div class="marginTop15">
                        输入充值金额:
                        <input type="text" style="width: 200px;height: 40px;" ng-model="money">
                    </div>

                    <div class="overflowH" style="margin-left: 83px;">
                        <%--<div class="payTypeDiv" ng-show="topUpType==2" ng-click="payType=3" ng-class="payType==3?'borderPink':''" style="line-height: 48px;position: relative">--%>
                            <%--<img style="position: absolute;left: 20px;top: 7px" src="/yzxfMall_page/img/yue.png" alt="">--%>
                            <%--<span style="font-size: 20px;position: absolute;right: 20px;color:#FE5495">余额</span>--%>
                        <%--</div>--%>

                        <div class="payTypeDiv" ng-click="payType=4" ng-class="payType==4?'borderPink':''">
                            <img src="/yzxfMall_page/img/zhifubao.png" >
                        </div>
                        <div class="payTypeDiv" ng-click="payType=10" ng-class="payType==10?'borderPink':''">
                            <img src="/yzxfMall_page/img/weixin.png" >
                        </div>
                    </div>

                    <button class="topUpBtn" style="margin: 50px 105px;"
                            ng-click="topUpType==1?submitMy():getPwdWin()"
                            ng-disabled="submitCheck">确认充值</button>
                </div>

                <div class="walletContent RePosition" ng-show="topUpType==3">
                    <div class="orderTitle">
                        <div class="floatL flex1" ng-click="updateOrForgot='1'"
                             ng-class="updateOrForgot=='1'?'clickBtn':''">支付密码修改
                        </div>
                        <div class="floatL flex1" ng-click="updateOrForgot='2'"
                             ng-class="updateOrForgot=='2'?'clickBtn':''">支付密码找回
                        </div>
                    </div>
                    <div ng-show="updateOrForgot=='1'" style="height: 600px">
                        <div class="passwordDiv">旧密码:<input type="password" ng-model="oldPwd"></div>
                        <div class="passwordDiv">新密码:<input type="password" ng-model="newPwd"></div>
                        <div class="passwordDiv">确认密码:<input style="margin-left: 15px" type="password" ng-model="secondPwd"></div>
                        <button class="topUpBtn" style="margin-left: 135px;width: 302px" ng-click="modifyMyPayPwd()">
                            确认修改
                        </button>
                    </div>
                    <div ng-show="updateOrForgot=='2'" style="height: 600px">
                        <div style="color: #FE5495" class="passwordDiv">手机号:<input type="text" ng-model="phoneNumber">
                            <button class="topUpBtn" style="margin-left: 35px;width: 150px;height: 30px;font-size: 17px"
                                    ng-click="getPhoneCheckCode()">点击获取验证码
                            </button>
                        </div>

                        <div style="color: #FE5495" class="passwordDiv">验证码:<input type="text" ng-model="verification"></div>
                        <div class="passwordDiv">新密码:<input type="text" ng-model="newPassword"></div>
                        <button class="topUpBtn" style="margin-left: 135px;width: 302px" ng-click="payForgotPwd()">
                            确认修改
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="winCon flex1" ng-show="showWinConWallet && payType==3">
        <div class="enterPsw">
            <div class="textCenter lineH50px font20px">充值</div>
            <div class="textCenter textSize30 lineH40px" ng-bind="'¥ '+money"></div>
            <div class="enterInput">
                <input maxlength="1" type="password" ng-model="pwd1"/>
                <input maxlength="1" type="password" ng-model="pwd2"/>
                <input maxlength="1" type="password" ng-model="pwd3"/>
                <input maxlength="1" type="password" ng-model="pwd4"/>
                <input maxlength="1" type="password" ng-model="pwd5"/>
                <input maxlength="1" type="password" ng-model="pwd6"/>
            </div>
            <div class="enterInputDes">请输入6位支付密码</div>
            <div class="selectDiv">
                <div style="border-left: 0;border-bottom: 0" ng-click="changeWin()">取消</div>
                <div style="border-left: 0;border-right: 0;border-bottom: 0" ng-click="submitFriend()">确认</div>
            </div>
        </div>
        <div class="win-qrcode" ng-show="payType!=3">
            <div class="textCenter lineH50px font20px" ng-bind="payType"></div>
            <div class="textCenter textSize30 lineH40px" ng-bind="'¥ '+money"></div>
        </div>
    </div>

    <div class="winCon flex1" ng-show="isQrcode">
        <div class="win-qrcode flex4">
            <div class="iconfont icon-close btn-close1" ng-click="closeQrcode()"></div>
            <img ng-show="payType==4" src="/yzxfMall_page/img/zhifubao.png" />
            <img ng-show="payType==10" src="/yzxfMall_page/img/weixin.png" />
            <div class="qrcodeImg"></div>
        </div>
    </div>

    <div class="winCon flex1" ng-show="isSuccessWin">
        <div class="enterPsw">
            <div class="textCenter lineH50px font20px">支付结果</div>
            <div style="padding: 35px 0;">
                <div class="iconfont iconStatus1" ng-class="statusIcon"></div>
                <div class="btn4" ng-bind="statusText"></div>
            </div>
            <div class="closeText1" ng-click="closeSuccessWin()">关闭</div>
        </div>
    </div>

    <%--第一次设置支付密码--%>
    <div class="hideMenu overflowHidden flex1" ng-show="isPayPwd">
        <!--提醒白块-->
        <div class="win-order1" style="width: 400px;height: 400px">
            <%--标题--%>
            <div class="win-title1">设置支付密码</div>
            <form ng-submit="setPayPassword()">
                <div class="accountLoginName" style="margin: 40px auto;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " type="password" ng-model="firstPwd"  placeholder="请输入密码" maxlength="6"/>
                </div>
                <div class="accountLoginName" style="margin: 40px auto;">
                    <div class="floatL iconfont icon-mima"></div>
                    <input class="floatL " type="password" ng-model="secondPwd" placeholder="确认密码" maxlength="6"/>
                </div>
                <button class="topUpBtn" type="submit" style="margin-left: 39px;width: 322px;margin-top: 10px;border-radius: 5px">提交</button>
            </form>
        </div>
    </div>

    <%--底部模板--%>
    <div ng-include="mallBottom"></div>
    <div ng-include="'/yzxfMall_page/temp_new/okOrFailBox.html'"></div>
</div>