<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <%--会员首页BANNER--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">会员版首页BANNER{{mbIsShow}}</div>
        </div>
        <div class="flex1">
            <div class="sectionImgDiv" ng-repeat="mb in memberBanner" ng-mouseenter="mbIsShow=mb._id" ng-mouseleave="mbIsShow=''">
                <img src="{{iconImg(mb.icon)}}" alt="" >
                <div class="blockDiv" ng-show="mbIsShow==mb._id" ng-click="changeDivFun(mb,mb.icon,'CommodityTypeManage','icon','memberBanner',$index,1)">
                    <div class="iconfont icon-shuaxin blockDivIcon"></div>
                    <div class="blockDivText">更换广告图片</div>
                </div>
            </div>
            <%--<div class="sectionImgDiv">--%>
            <%--<img src="" alt="">--%>
            <%--</div>--%>
            <%--<div class="sectionImgDiv">--%>
            <%--<img src="" alt="">--%>
            <%--</div>--%>
        </div>
        <div class="advertisingTixing">提示:请上传750(宽)X234(高)像素的图片</div>
    </div>
    <%--会员首页图标--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">会员版首页图标</div>
        </div>
        <div class="flex1" style="width: 700px;flex-wrap: wrap;margin: 0 auto">
            <div class="sectionIconDiv" ng-repeat="iIconO in indexIconOne" ng-mouseenter="iIconIsShow=iIconO._id" ng-mouseleave="iIconIsShow=''">
                <img src="{{iconImg(iIconO.img)}}" alt="">
                <div class="sectionIconTitle" ng-bind="iIconO.name">火锅</div>
                <div class="iconBlockDiv" ng-show="iIconIsShow==iIconO._id" ng-click="changeDivFun(iIconO,iIconO.img,'OperateType','img','indexIconOne',$index,'','isNav')">
                    <div class="iconfont icon-shuaxin iconBlockDivIcon"></div>
                    <div class="iconBlockDivText">更换图标</div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传88(宽)X88(高)像素的图片</div>
    </div>
    <%--商城首页图标--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">商城首页图标</div>
        </div>
        <div class="flex1" style="width: 800px;flex-wrap: wrap;margin: 0 auto">
            <div class="sectionIconDiv flex1" ng-repeat="mIconO in mallIndexIconOne" ng-mouseenter="mIconIsShow=mIconO._id" ng-mouseleave="mIconIsShow=''">
                <img src="{{iconImg(mIconO.cMallImg)}}" alt="" style="width: 50px;height: auto;">
                <div class="sectionIconTitle" ng-bind="mIconO.name">火锅</div>
                <div class="iconBlockDiv" ng-show="mIconIsShow==mIconO._id" ng-click="isSetColor('top');changeDivFun(mIconO,mIconO.cMallImg,'OperateType','cMallImg','mallIndexIconOne',$index,'','mallIsNav');">
                    <div class="iconfont icon-shuaxin iconBlockDivIcon"></div>
                    <div class="iconBlockDivText">更换图标</div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传24(宽)X24(高)像素的图片</div>
    </div>
    <%--商城首页左侧导航图标--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">商城首页左侧导航图标</div>
        </div>
        <div class="flex1" style="width: 800px;flex-wrap: wrap;margin: 0 auto">
            <div class="sectionIconDiv flex1" ng-repeat="mIconT in mallIndexIconOne" ng-mouseenter="mIconTIsShow=mIconT._id" ng-mouseleave="mIconTIsShow=''">
                <img src="{{iconImg(mIconT.mallImg)}}" alt="" style="width: auto;height: auto;">
                <div class="sectionIconTitle" ng-bind="mIconT.name">火锅</div>
                <div class="iconBlockDiv" ng-show="mIconTIsShow==mIconT._id"  ng-click="isSetColor('left');changeDivFun(mIconT,mIconT.mallImg,'OperateType','mallImg','mallIndexIconOne',$index,'','mallIsNav');">
                    <div class="iconfont icon-shuaxin iconBlockDivIcon"></div>
                    <div class="iconBlockDivText">更换图标</div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传24(宽)X24(高)像素的图片</div>
    </div>
    <%--商城首页BANNER--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">商城首页BANNER</div>
        </div>
        <div class="flex1">
            <div class="sectionImgDiv" ng-repeat="mllb in mallBanner" ng-mouseenter="mllbIsShow=mllb._id" ng-mouseleave="mllbIsShow=''">
                <img src="{{iconImg(mllb.icon)}}" alt="">
                <div class="blockDiv" ng-show="mllbIsShow==mllb._id" ng-click="changeDivFun(mllb,mllb.icon,'CommodityTypeManage','icon','mallBanner',$index,2)">
                    <div class="iconfont icon-shuaxin blockDivIcon"></div>
                    <div class="blockDivText">更换广告图片</div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传1920(宽)X500(高)像素的图片</div>
    </div>
    <%--商城首页限时特价广告--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">商城首页Banner广告</div>
        </div>
        <div class="flex1">
            <div class="sectionImgDiv" ng-repeat="ma in mallAdvertising" style="width: 250px;height: 240px" ng-mouseenter="maIsShow=ma._id" ng-mouseleave="maIsShow=''">
                <img src="{{iconImg(ma.icon)}}" alt="">
                <div class="blockDiv" style="padding-top: 45px" ng-show="maIsShow==ma._id" ng-click="changeDivFun(ma,ma.icon,'CommodityTypeManage','icon','mallAdvertising',$index,3)">
                    <div class="iconfont icon-shuaxin blockDivIcon"></div>
                    <div class="blockDivText">更换广告图片</div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传250(宽)X240(高)像素的图片</div>
    </div>
    <%--商城首页长条广告--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">商城首页长条广告</div>
        </div>
        <div class="flex1">
            <div class="flex1" style="flex-wrap: wrap;margin: 0 auto;">
                <div class="sectionImgDiv" ng-repeat="mr in mallRectangle" style="height:110px" ng-mouseenter="mrIsShow=mr._id" ng-mouseleave="mrIsShow=''">
                    <img src="{{iconImg(mr.icon)}}" alt="">
                    <div class="blockDiv" ng-show="mrIsShow==mr._id" ng-click="changeDivFun(mr,mr.icon,'CommodityTypeManage','icon','mallRectangle',$index,4);">
                        <div class="iconfont icon-shuaxin blockDivIcon" style="margin-top: 0"></div>
                        <div class="blockDivText">更换广告图片</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传1200(宽)X110(高)像素的图片</div>
    </div>

    <%--更换图片--%>
    <div class="winCon" ng-show="changeBox" style="display: flex;align-items: center;justify-content: center;">
        <!--更换图片-->
        <div style="width: 680px;height: 400px;margin: 0 auto;background-color: #fff;position: relative">
            <%--图片--%>
            <div class="changeDivLeft flex1">
                <div class="flex1">
                    <img style="max-width: 278px" src="{{iconImg(changeImg)}}" alt="">
                    <div>上传图片</div>
                </div>
                <input type="file" name="file"
                       onchange="angular.element(this).scope().uploadFile(this)"/>
            </div>
            <%--广告数据--%>
            <div class="changeDivRight" ng-show="changeType=='advertising'">
                <%--链接类型--%>
                <div>
                    请选择广告关联类型 :
                    <select ng-model="associatedType" style="width: 120px;background-color: #fff">
                        <option value="">商家</option>
                        <option value="ProductInfo">商品</option>
                        <option value="link">网页链接</option>
                    </select>
                </div>
                <div>
                    请输入关联ID :
                    <input type="text" ng-model="associatedId">
                </div>
                <div style="position: absolute;bottom: 60px;right:145px;">
                    <bottom class="btn1 bgBlue" ng-click="updateAdvertising()">确定</bottom>
                </div>
            </div>
            <%--官网轮播图--%>
            <div class="changeDivRight" ng-show="changeType=='website'">
                <%--链接类型--%>
                <div>
                    请输入关联网页地址 :
                    <input type="text" ng-model="webAddress">
                </div>
                <div style="position: absolute;bottom: 60px;right:145px;">
                    <bottom class="btn1 bgBlue" ng-click="updateWebSiteImg()">确定</bottom>
                </div>
            </div>
            <%--图标数据--%>
            <div class="changeDivRight" ng-show="changeType=='icon'">
                <div>
                    请选择图标类型 :
                    <select ng-model="selectOperate[0]" ng-options="operate1.name for operate1 in operateList[0]"
                            ng-change="getOperateType(selectOperate[0]._id,2)" style="background: #fff;display: block;margin: 10px auto;"></select>
                    <select ng-model="selectOperate[1]" ng-options="operate2.name for operate2 in operateList[1]"
                            ng-change="getOperateType(selectOperate[1]._id,3)" style="background: #fff;display: block;margin: 10px auto;"></select>
                    <select ng-model="selectOperate[2]" ng-options="operate3.name for operate3 in operateList[2]"
                            ng-change="getOperateType(selectOperate[2]._id,4)" style="background: #fff;display: block;margin: 10px auto;"></select>
                </div>
                <div ng-show="uploadData.scope=='mallIndexIconOne'&&colorLocation=='top'">
                    请选择背景颜色:
                    <input type="color" ng-model="selectedColor">
                </div>
                <div style="position: absolute;bottom: 60px;right:145px;">
                    <bottom class="btn1 bgBlue" ng-click="updateIcon()">确定</bottom>
                </div>
            </div>
            <bottom class="btn1 bgBlue" style="position: absolute;bottom: 10px;"
                    ng-click="updateWebSiteImg()" ng-show="isWebSite=='websiteTop'">确定</bottom>
            <%--关闭图标--%>
            <div class="iconfont icon-close" style="position:absolute; right: 5px;top: 5px;font-size: 20px"
                 ng-click="changeBox=false;isWebSite='';webAddress=''"></div>
        </div>
    </div>

</div>









