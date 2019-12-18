<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="content">
    <div class="popSection flex2">
        <div style="width:100%">
            <span>所属分类:</span>
            <select ng-model="descListJson.selectedAl"
                    ng-options="al._id as al.name for al in articleList"></select>
        </div>
        <div style="min-height:70px">
            <span style="min-width:100px">封面图片:</span>
            <div class="btn7" ng-show="changeImg==null || changeImg==''">上传
                <input type="file" name="file" class="btnUpload3"
                       onchange="angular.element(this).scope().uploadFile1(this)"/>
            </div>
            <div class="iconHide1" ng-show="changeImg!=null && changeImg!=''">
                <img class="popImgMini" ng-src="{{iconImg(changeImg)}}"
                     ng-click="showImgFun(changeImg)"/>
                <div class="icon-cuowu iconfont btn8" ng-click="delFileItem('changeImg')"></div>
            </div>
        </div>
        <div style="width:100%">
            <span>标题:</span>
            <input type="text" ng-model="descListJson.title">
        </div>
        <div style="width:100%">
            <span>来源:</span>
            <input type="text" ng-model="descListJson.source">
        </div>
        <div style="width:100%">
            <span>描述:</span>
            <input type="text" ng-model="descListJson.desc">
        </div>
        <div style="width:100%">
            <span>内容:</span>
            <div id="description" class="descDiv commonValue" style="margin: 0;border: 1px solid #eee;width: 80%;">
                <%--ng-repeat="item in descList"--%>
                <div class="desc" ng-repeat="item in descListJson.contents">
                    <div class="button pointer" style="position: relative">
                        <div title="添加文本" ng-click="addText('descList')" class="icon-paste"></div>
                        <div style="position:relative" class="marginTop10">
                            <div class="icon-images"></div>
                            <input type="file" name="file" title="添加图片"
                                   class="uploadMod"
                                   ng-click="setUploadIndex($index)"
                                   onchange="angular.element(this).scope().newsUpload(this)"/>
                        </div>
                        <div title="删除" ng-click="removeText($index,'descList')" ng-if="$index>0"
                                class="iconfont icon-close marginTop10"></div>

                    </div>
                    <div class="text">
                            <textarea onfocus="autoTextArea(this)" index="desc_{{$index}}"
                                      ng-click="initTextarea($event)" class="value"
                                      style="height: 60px;line-height: 20px; width: 100%;border:none;"
                                      ng-model="item.desc"></textarea>
                        <!--存放图片的-->
                        <div class="iconHide1" style="position:relative;top:0;left:10px;margin-bottom:10px;" ng-show="item.img!=null && item.img!=''">
                            <img class="popImgMini" ng-src='/s_img/icon.jpg?_id={{item.img}}' ng-if="item.img!=null&&item.img!=''"
                                 ng-click="showImgFun(item.img)"/>
                            <div class="icon-cuowu iconfont btn8" ng-click="item.img=''"></div>
                        </div>

                    </div>
                    <div class="clearDiv"></div>
                </div>

            </div>
        </div>
    </div>

    <%--<div class="item">--%>
        <%--<div class="fieldName" style="text-align: center">内容</div>--%>
        <%--<div id="description" class="descDiv commonValue" style="margin: 0;border: 1px solid #000;">--%>
            <%--&lt;%&ndash;ng-repeat="item in descList"&ndash;%&gt;--%>
            <%--<div class="desc" ng-repeat="item in descListJson.contents">--%>
                <%--<div class="button" style="position: relative">--%>
                    <%--<button title="添加文本" ng-click="addText('descList')" type="button"--%>
                            <%--class="icon-paste"></button>--%>
                    <%--<div class="icon-images" style="z-index: 2;position: absolute;left: 7px"></div>--%>
                    <%--<input type="file" name="file" title="添加图片"--%>
                           <%--style="position: relative; overflow: hidden;z-index: 3;height: 10px;border: none;"--%>
                           <%--ng-click="setUploadIndex($index)" ;--%>
                           <%--onchange="angular.element(this).scope().newsUpload(this)"/>--%>
                    <%--<button title="删除" ng-click="removeText($index,'descList')" ng-if="$index>0"--%>
                            <%--type="button"--%>
                            <%--class="iconfont icon-close"></button>--%>

                <%--</div>--%>
                <%--<div class="text">--%>
                            <%--<textarea onfocus="autoTextArea(this)" index="desc_{{$index}}"--%>
                                      <%--ng-click="initTextarea($event)" class="value"--%>
                                      <%--style="height: 60px;line-height: 19px; width: 100%;border: 1px solid #dfdfdf;"--%>
                                      <%--ng-model="item.desc"></textarea>--%>
                    <%--<!--存放图片的-->--%>
                    <%--<img class="commodityIcon" ng-if="item.img!=null&&item.img!=''"--%>
                         <%--ng-src='/s_img/icon.jpg?_id={{item.img}}'/>--%>

                <%--</div>--%>
                <%--<div class="clearDiv"></div>--%>
            <%--</div>--%>

        <%--</div>--%>
        <%--<div class="clearDiv"></div>--%>
    <%--</div>--%>
</div>
<div class="popSectionPage" ng-class="winCheck?'bottom0':''">
    <button class="fr btn1 bgBlue" ng-click="saveNews(title,selectedAl)">保存</button>
</div>
<div class="winCon" ng-click="closeImgFun()" ng-show="showImg!=''">
    <img class="winConImg" ng-src="{{iconImg(showImg)}}"/>
</div>
