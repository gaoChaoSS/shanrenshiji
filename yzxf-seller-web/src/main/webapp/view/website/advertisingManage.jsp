<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <%--官网首页BANNER--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">官网首页BANNER</div>
        </div>
        <div class="flex1">
            <div class="sectionImgDiv" ng-repeat="wsb in webSiteBanner" ng-mouseenter="wsbIsShow=wsb._id" ng-mouseleave="wsbIsShow=''">
                <img src="{{iconImg(wsb.icon)}}" alt="">
                <div class="blockDiv" ng-show="wsbIsShow==wsb._id" ng-click="setWebsiteValue('website');changeDivFun(wsb,wsb.icon,'CommodityTypeManage','icon','webSiteBanner',$index,5)">
                    <div class="iconfont icon-shuaxin blockDivIcon"></div>
                    <div class="blockDivText">更换广告图片</div>
                </div>
            </div>
        </div>
        <div class="advertisingTixing">提示:请上传1920(宽)X610(高)像素的图片</div>
    </div>
    <%--官网每个页面顶部的背景图--%>
    <div class="advertisingDiv">
        <div class="sectionTitle" style="border: none;">
            <div class="fl rowTitle1" style="color: #148BBF">官网页面顶部背景图</div>
        </div>
        <div>
            <div class="sectionImgDiv" ng-repeat="top1 in webSiteTop" ng-mouseenter="wsbIsShow=top1._id" ng-mouseleave="wsbIsShow=''">
                <img src="{{iconImg(top1.icon)}}" alt="">
                <div class="blockDiv" ng-show="wsbIsShow==top1._id" ng-click="setWebsiteValue('websiteTop');changeDivFun(top1,top1.icon,'CommodityTypeManage','icon','webSiteTop',$index,6)">
                    <div class="iconfont icon-shuaxin blockDivIcon"></div>
                    <div class="blockDivText">更换图片</div>
                </div>
                <div class="title-bottom" ng-bind="top1.entityId"></div>
            </div>
        </div>
        <div class="advertisingTixing" style="clear:left">提示:请上传1920(宽)X400(高)像素的图片</div>
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









