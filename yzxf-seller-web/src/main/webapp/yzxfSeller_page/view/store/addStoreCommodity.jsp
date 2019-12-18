<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="store_addStoreCommodity_Ctrl" class="d_content title_section form_section order_panel">
    <div class="overflowPC">
        <form ng-submit="submitBtn()">
            <div class="title">
                <span class="icon-left-1 iconfont titleBack" ng-click="goBack()"></span>
                <span ng-if="isAdd" class="textSize18">添加商品</span>
                <span ng-if="!isAdd" class="textSize18">修改商品</span>
                <button ng-click="save()" class="titleManage black textSize15 bgWhite">
                    <span>保存</span>
                </button>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">商品名称</div>
                    <input type="text" class="rowInput" ng-model="commodity.name" placeholder="请输入商品的名称"/>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">商品标签</div>
                    <input type="text" class="rowInput" ng-model="commodity.tag" placeholder="请输入商品的标签(20个字符以内)" maxlength="20"/>
                </div>
            </div>

            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">售价</div>
                    <input type="text" ng-model="commodity.salePrice" ng-blur="checkPrice()" class="rowInput"
                           style="float: left;width: auto;margin-right: 0;"
                           placeholder="请输入商品的售价"/>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">原价</div>
                    <input type="text" ng-model="commodity.oldPrice" ng-blur="checkPrice()" class="rowInput"
                           style="float: left;width: auto;margin-right: 0;"
                           placeholder="请输入商品的售价"/>
                </div>
            </div>

            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">商品类别</div>
                    <div ng-if="commodity.operateType!=null" ng-bind="commodity.operateType"
                         ng-click="selectProductType()" class="rowInput"></div>
                    <div class="rowInput" ng-if="commodity.operateType==null" ng-click="selectProductType()">请选择商品的类别
                    </div>
                </div>
            </div>

            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">商品规格</div>
                    <div class="rowInput" ng-click="addSpec()">添加新规格</div>
                    <div>
                        <div ng-repeat="specItems in commodity.spec" ng-bind="specItems.name"
                             ng-click="modifySpec($index)" class="btn1"></div>
                    </div>
                </div>
            </div>

            <div class="hideMenu" ng-show="showAddSpec">
                <div class="winCon-spec">
                    <div class="winCon-title">
                        <div class="icon-left-1 iconfont titleBack whitefff" ng-click="goBackWin()"></div>
                        商品规格
                        <div class="titleDel1" ng-click="isShowDelWin=true" ng-show="(modifySpecId!=null && modifySpecId!='') || modifySpecId==0">删除</div>
                    </div>
                    <input type="text" maxlength="10" ng-model="$$tempSpec.name" placeholder="请输入商品规格名称" class="tableTitle"
                        ng-blur="checkSpecName()">
                    <div class="delModel1">
                        <div class="iconfont icon-qingchu delBtn1" ng-repeat="modify in $$tempSpec.isModify"
                            ng-click="delTr($index)"></div>
                    </div>
                    <table class="winCon-table">
                        <tr class="titleTr1">
                            <td>种类</td>
                            <td>加价</td>
                        </tr>
                        <tr ng-repeat="tempItem in $$tempSpec.items">
                            <td>
                                <input type="text" maxlength="20" ng-model="tempItem"
                                       ng-focus="setFocus($index)"
                                       ng-blur="setBlur('items',tempItem,$index)"
                                       ng-class="$$tempSpec.isModify_items[$index].status?'td-modify':''">
                            </td>
                            <td>
                                <input type="text" maxlength="8" ng-model="$$tempSpec.addMoney[$index]"
                                       ng-focus="setFocus($index)"
                                       ng-blur="setBlur('addMoney',$$tempSpec.addMoney[$index],$index)"
                                       ng-class="$$tempSpec.isModify_addMoney[$index].status?'td-modify':''">
                            </td>
                        </tr>
                        <tr ng-show="$$tempSpec.items.length<10" style="border:none">
                            <td>
                                <input type="text" maxlength="20" ng-model="$$tempRow.items" placeholder="种类{{$$tempSpec.items.length+1}}(20个字符以内)">
                            </td>
                            <td>
                                <input type="text" maxlength="8" ng-model="$$tempRow.addMoney" placeholder="默认加价0元">
                            </td>
                        </tr>
                        <tr ng-show="$$tempSpec.items.length<10" class="winCon-addTr" ng-click="addSpecTrTemp()">
                            <td colspan="2">
                                <div class="iconfont icon-icontianjia01"></div>
                            </td>
                        </tr>
                    </table>
                    <button class="submitBtn" ng-bind="'添加新规格'" ng-show="isShowSubmit"
                            ng-click="submitSpec()"></button>
                </div>
            </div>

            <div class="hideMenu" ng-show="isShowDelWin==true">
                <div class="errorMain">
                    <div class="errorMainRow">是否删除?</div>
                    <div class="errorMainRow">
                        <div class="errorMainBtn black" ng-click="isShowDelWin=false">取消</div>
                        <div class="errorMainBtn black" ng-click="delSpec()">确认
                        </div>
                    </div>
                </div>
            </div>

            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle">商品介绍</div>
                <textarea ng-model="commodity.desc" class="rowInput" style="line-height: 20px;height: 100px;padding: 15px 0;"
                          placeholder="请输入商品的详情介绍"></textarea>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle" style="position: relative;float: left;margin-right: 10px;">封面图片</div>
                    <div class="button selectFileBtn">选择...</div>
                    <input type="file" name="file" class="uploadInput" style="top: 15px;left: 81px;"
                           onchange="angular.element(this).scope().uploadFile(this,'icon')">
                    <img class="commodityIcon" ng-if="commodity.icon!=null&&commodity.icon!=''"
                         ng-src='/s_img/icon.jpg?_id={{commodity.icon}}&wh=300_300'/>
                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle" style="position: relative;float: left;margin-right: 10px;">商品相册</div>
                    <div class="gray888 lineHeight50">(商品介绍/参数详情)</div>
                    <div class="rowFlex">
                        <div class="button selectFileBtn">选择...</div>
                        <input type="file" name="file" class="uploadInput"
                               onchange="angular.element(this).scope().uploadFile(this,'imgList')">
                        <div class="commodityIcon" ng-repeat="item in imgList">
                            <img ng-src='/s_img/icon.jpg?_id={{item.fileId}}&wh=300_300'/>
                            <div class="del iconfont icon-close" ng-click="delFileItem($index,'imgList')"></div>
                        </div>
                    </div>

                </div>
            </div>
            <div class="sectionMain">
                <div class="mainRow">
                    <div class="rowTitle" style="position: relative;float: left;margin-right: 10px;">商品缩略图</div>
                    <div class="gray888 lineHeight50">(商品详情页面预览图)</div>
                    <div class="rowFlex">
                        <div class="button selectFileBtn">选择...</div>
                        <input type="file" name="file" class="uploadInput"
                               onchange="angular.element(this).scope().uploadFile(this,'thumbnail')">
                        <div class="commodityIcon" ng-repeat="items in thumbnail">
                            <img ng-src='/s_img/icon.jpg?_id={{items.fileId}}&wh=300_300'/>
                            <div class="del iconfont icon-close" ng-click="delFileItem($index,'thumbnail')"></div>
                        </div>
                    </div>

                </div>
            </div>
        </form>
    </div>
</div>