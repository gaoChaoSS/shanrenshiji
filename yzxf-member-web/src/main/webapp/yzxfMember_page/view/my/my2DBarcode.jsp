<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="my_my2DBarcode_Ctrl" class="d_content title_section form_section">
    <%--<div class="overflowPC">--%>
    <div class="bk-hidden">
        <div class="bk-blur1" style="{{showBk}}"></div>
    </div>
    <div class="sectionMain2 textShadow1">
        <div class="icon-left-1 iconfont titleBack whitefff" ng-click="goPage('/my/my')"></div>
        <div class="textCenter whitefff lineHeight50 textSize20">个人信息</div>
        <img class="headImg" err-src="/yzxfMember_page/img/notImg02.jpg"
             ng-src="{{iconImgUrl(memberInfo.icon)}}"/>
        <input type="file" name="file" class="headImg"
               onchange="angular.element(this).scope().uploadFile(this)"/>
        <div style="position:relative;margin: 20px 0 0 120px;">
            <div ng-bind="isNullText2(memberInfo.realName)" class="textEllipsis whitefff"></div>
            <div>
                <span class="iconfont2 icon2-huiyuan whitefff" ng-show="renZheng"></span>
                <span class="whitefff" ng-class="renZheng?'':'grayccc'" ng-bind="iconText"></span>
            </div>
            <div ng-click="getQrcode()" style="position: absolute;top: 0;right: 10%;">
                <span class="iconfont icon-erweima textSize20 whitefff"></span>
            </div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">会员号:</div>
            <div class="rowInput" ng-bind="cardCheck()"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">身份证:</div>
            <div class="rowInput" ng-bind="isNullText2(memberInfo.idCard)"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">身份证居住地:</div>
            <div class="rowInput" ng-bind="isNullText2(memberInfo.realArea)+isNullText2(memberInfo.realAddress)"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">电话号码:</div>
            <div class="rowInput" ng-bind="isNullText2(memberInfo.mobile)"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">性别:</div>
            <div class="rowInput" ng-bind="getSex(memberInfo.sex)" ng-show="!sexCheck"></div>
            <div class="icon-shenqing iconfont mainRowRight colorBlue"
                 ng-click="sexCheck=!sexCheck;!sexCheck?updateSex():''"></div>
            <div class="rowInput" ng-show="sexCheck">
                <div class="rowInputCheck" ng-click="memberInfo.sex=1">
                    <span ng-class="memberInfo.sex==1?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>男
                </div>
                <div class="rowInputCheck" ng-click="memberInfo.sex=2">
                    <span ng-class="memberInfo.sex==2?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>女
                </div>
            </div>
        </div>
    </div>
    <div class="sectionMain">
        <div class="mainRow">
            <div class="rowTitle">所在地区:</div>
            <div class="rowInput" ng-bind="isNullText2(memberInfo.area)"></div>
            <div class="icon-shenqing iconfont mainRowRight colorBlue"
                 ng-click="clearStoreLocation();goPage('/home/location/selectedArea/myInfoAddress')"></div>
        </div>
        <div class="mainRow">
            <div class="rowTitle">街道地址:</div>
            <div class="rowInput textEllipsis" style="width: 60%" ng-bind="isNullText2(address)" ng-show="!addressCheck"></div>
            <form ng-submit="updateAddress()">
                <input type="text" class="rowInput textEllipsis" style="border:1px solid #ccc;width: 60%"
                       ng-model="address" ng-blur="addressCheck=false;updateAddress()" ng-show="addressCheck"/>
                <div class="icon-shenqing iconfont mainRowRight colorBlue"
                     ng-click="addressCheck=!addressCheck;!addressCheck?updateAddress():''"></div>
            </form>
        </div>
        <div class="mainRow">
            <div class="rowTitle">邮箱地址:</div>
            <div class="rowInput textEllipsis" style="width: 60%" ng-bind="isNullText2(email)" ng-show="!emaliCheck"></div>
            <form ng-submit="updateAddress()">
                <input type="text" class="rowInput textEllipsis" style="border:1px solid #ccc;width: 60%"
                       ng-model="email" ng-blur="emaliCheck=false;updateEmail()" ng-show="emaliCheck"/>
                <div class="icon-shenqing iconfont mainRowRight colorBlue"
                     ng-click="emaliCheck=!emaliCheck;!emaliCheck?updateEmail():''"></div>
            </form>
        </div>
    </div>
    <%--</div>--%>

    <div class="hideMenu flexMod1" ng-show="myCard" ng-click="myCard=false">
        <div class="windowPanel1">
            <img class="headImg2" err-src="/yzxfMember_page/img/notImg02.jpg"
                 ng-src="{{iconImgUrl(memberInfo.icon)}}"/>
            <div class="mainRow3">
                <div ng-bind="isNullText2(memberInfo.realName)" class="textEllipsis"></div>
                <div class="mainRowTop35">
                    <div class="iconfont2 icon2-huiyuan fl lineHeight20"></div>
                    <div class="mainRowLeft20" ng-class="renZheng?'':'grayccc'" ng-bind="iconText"></div>
                </div>
            </div>
            <div class="qrcode"></div>
            <div class="textCenter mainRowTop10">扫描上方二维码图案</div>
            <div class="textCenter">查看我的信息</div>
        </div>
    </div>
</div>
