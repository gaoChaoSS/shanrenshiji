<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div class="sectionTitle">
        <div class="rowTitle1">商城帮助中心</div>
    </div>
    <div class="leftMenu">
        <div class="input-add">
            <input type="text" placeholder="新增目录" ng-model="addMenuName[0]">
            <button ng-click="addMenu(parentMenu)">新增</button>
        </div>
        <div ng-repeat="menu in menuList" ng-init="outIndex=$index" class="menuList">
            <div class="menuRow">
                <div class="menuRowTitle" ng-click="openParent($index)">
                    <span ng-bind="menu.name"></span>
                    <div class="menu-arrows">
                        <div class="iconfont icon-fanhuidingbu"
                              ng-style="{transform:menu.status==-1?'rotate(90deg)':'rotate(180deg)'}"></div>
                    </div>
                </div>
                <div class="menu1-btn" ng-show="menu.status===0">
                    <button ng-click="setMenuBtn($index,1)">添加</button>
                    <button ng-click="delMenu(menu._id,$index)">删除</button>
                </div>
                <form ng-submit="addMenu(menu,$index)" ng-show="menu.status==1"
                      class="menuRow-add" ng-style="{height:menu.status==1?30:0}">
                    <span>● </span>
                    <input type="text" ng-model="addMenuName[1]"
                           ng-blur="addMenu(menu,$index)" placeholder="请输入名称">
                </form>
                <div style="transition:0.5s all;overflow:hidden"
                     ng-style="{height:menu.status==-1?0:menu.items.length*30}">
                    <div class="menuRowTitle2 textEllipsis" ng-repeat="menu2 in menu.items" ng-bind="'● '+menu2.name"
                        ng-click="showArt(menu2,outIndex,$index)"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="main-art">
        <div class="art-middle" ng-show="curArt.status===0">
            <div class="art-title">
                <div class="art-title-name" ng-bind="curArt.title"></div>
                <div class="art-title-tap">
                    <span ng-bind="'编辑时间：'+showYFullTime(curArt.updateTime)"></span>
                    <%--<span ng-bind="'是否公开：'+(curArt.canUse?'√':'×')"></span>--%>
                </div>
            </div>
            <div class="art-content">
                <div ng-repeat="item in curArt.contents" style="padding:10px">
                    <div class="art-text" ng-if="item.type===0" ng-bind="item.desc"></div>
                    <img class="art-img" ng-if="item.type===1 && item.desc!=null && item.desc!=''" src="{{iconImg(item.desc)}}" />
                </div>
            </div>
        </div>

        <div class="art-middle" ng-show="curArt!=null && curArt.status===1">
            <div class="art-title">
                <input type="text" ng-model="curArt.title">
                <%--<div class="art-title-tap">--%>
                    <%--<span>--%>
                        <%--是否公开--%>
                        <%--<span ng-click="curArt.canUse=true">--%>
                            <%--<span ng-class="curArt.canUse?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>是--%>
                        <%--</span>--%>
                        <%--<span ng-click="curArt.canUse=false">--%>
                            <%--<span ng-class="!curArt.canUse?'icon-checkbox-checked':'icon-checkbox-unchecked'" style="color: #138bbe;"></span>否--%>
                        <%--</span>--%>
                    <%--</span>--%>
                <%--</div>--%>
            </div>
            <div class="art-content">
                <div ng-repeat="cur in curArt.contents" class="art-content-row">
                    <div contenteditable="true" class="art-edit" ng-if="cur.type===0" ng-bind="cur.desc"></div>
                    <div ng-if="cur.type===1" class="main-art-add-img">
                        <span ng-show="cur.desc==null || cur.desc==''">添加图片</span>
                        <div style="display:none" class="contentsIndex" ng-bind="$index"></div>
                        <input type="file" name="file" class="btnUpload3"
                               onchange="angular.element(this).scope().uploadFile(this,$(this).prev('.contentsIndex').text())"/>
                        <img class="art-img" ng-if="cur.desc!=null && cur.desc!=''" src="{{iconImg(cur.desc)}}" />
                    </div>
                    <div class="art-content-btn" ng-click="delContent($index)">×</div>
                </div>
                <div style="display:flex">
                    <div class="main-art-add" style="margin: 20px;" ng-click="addContents(0)">添加文字</div>
                    <div class="main-art-add" style="margin: 20px;" ng-click="addContents(1)">添加图片</div>
                </div>
            </div>
        </div>

        <div class="art-bottom" ng-show="curArt!=null && curArt!=''">
            <button class="btn1" ng-show="curArt.status===0" ng-click="curArt.status=1">编辑</button>
            <button class="btn1" ng-show="curArt.status===1" ng-click="getWin(1)">保存</button>
            <button class="btn1" ng-show="curArt.status===1" ng-click="getWin(0)">取消</button>
            <button class="btn1" ng-click="getWin(2)">删除</button>
        </div>
    </div>
    <div class="sectionHintBk" ng-show="curWin.isShow">
        <div class="sectionHint">
            <div class="lineH100px" ng-bind="curWin.title">是否退出编辑?</div>
            <div class="flex1">
                <button class="btn1" ng-click="curWin.action()">是</button>
                <button class="btn1 bkColorRed1" ng-click="curWin.isShow=false">否</button>
            </div>
        </div>
    </div>
</div>
