<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="ti">
    <div style="margin-right: 15px;" class="icon-stop2 fl remote">远程添加</div>
    <div style="margin-right: 15px;" class="icon-stop2 fl local">本地添加</div>
    <div style="margin-right: 15px;" class="icon-stop2 fl update">修改</div>
</div>
<form class="mDataForm" ng-submit="saveData()">
    <div class="itemTr headerCon" ng-style="{width:allWidth}">
        <div class="headerTd1 textNowrap headerTd1" style="width:50px;"></div>
        <div class="headerTd1 textNowrap" ng-style="{width:item.width}" ng-repeat="item in mDataDef"
             ng-if="isShowField(item)"
             ng-bind="item.title">
        </div>
        <div class="headerTd1 textNowrap headerTd1" style="width:60px;">操作</div>
        <div class="headerTd1 textNowrap headerTd1" style="width:100px;"></div>
    </div>

    <%--添加一条--%>
    <div class="itemTr" ng-style="{width:allWidth}">
        <div class="headerTd1 textNowrap headerTd1" style="width:50px;"></div>
        <div class="headerTd1 textNowrap" ng-style="{width:item.width}" ng-repeat="item in mDataDef"
             ng-if="isShowField(item)">
            <input ng-if="item.inputType=='input'" ng-model="newData[item.name]"
                   placeholder="{{'输入'+item.title}}" ng-mouseover="selectText($event)"/>
            <input ng-if="item.inputType=='number'" type="number" ng-model="newData[item.name]"
                   placeholder="{{'输入'+item.title}}" ng-mouseover="selectText($event)"/>
            <button ng-if="item.inputType=='boolean'" type="button" class="button iconfont icon-jianchacheck35"
                    ng-click="newData[item.name]=!newData[item.name]" style="font-size: 15px;"
                    ng-class="newData[item.name]?'green':'notHigh'"
                    title="{{'设置:'+item.title}}"></button>
        </div>
        <div class="textNowrap headerTd1" style="text-align: center;width:60px;"></div>
        <div class="textNowrap headerTd1" style="text-align: center;width:100px;">
            <button class="button green iconfont icon-add" title="添加"></button>
        </div>
    </div>


    <div class="itemTr" ng-repeat="pitem in result.localList"
         ng-style="{width:allWidth}">

        <div class="textNowrap" style="width:50px;" ng-class="getItemClass(pitem)">
            <span ng-bind="!pitem.$$remoteAdd||pitem.$$syncShow?'本地:':'远程:'"></span>
        </div>

        <div class="notHigh" ng-if="pitem.$$syncShow&&pitem.$$remoteAdd" ng-style="{width:allWidth-50-60-100}"
             style="background-color: #eee;">不存在
        </div>
        <div class="textNowrap" ng-class="item.inputType"
             ng-style="{width:item.width,backgroundColor:pitem.$$syncShow?'#eee':''}"
             ng-repeat="item in mDataDef"
             ng-dblclick="pitem['$$old_'+item.name]=pitem[item.name];isEditObj[pitem._id+'_'+item.name]=true;"
             ng-if="isShowField(item)&&!(pitem.$$remoteAdd&&pitem.$$syncShow)"
             ng-include="'/view_temp/inputEdit.jsp'">
        </div>

        <div class="textNowrap" style="text-align: center;width:60px;"
             ng-style="{backgroundColor:pitem.$$syncShow?'#eee':''}">
            <button type="button"
                    title="同步" ng-click="pitem.$$syncShow=!pitem.$$syncShow;showSyncCon(pitem)"
                    ng-class="getItemClass(pitem)+(!pitem.$$syncShow?' icon-right':' icon-unfold')"
                    class="button iconfont"></button>
        </div>
        <div class="textNowrap" ng-style="{backgroundColor:pitem.$$syncShow?'#eee':''}"
             style="text-align: center;width:100px;">
            <button class="button high iconfont icon-addcollapse" title="删除本地数据" type="button"
                    ng-if="pitem.$$syncShow&&!pitem.$$remoteAdd"
                    ng-click="delItem(pitem)"></button>

            <button class="button green icon-upload" title="上传本地到远程" type="button" ng-click="upload(pitem)"
                    ng-if="pitem.$$syncShow&&(pitem.$$isUpdate||pitem.$$localAdd)"></button>

        </div>

        <%--显示远程数据,用于对比--%>
        <div class="headerTd1 textNowrap notHigh" style="width:50px;border-left:1px solid #ccc;"
             ng-if="pitem.$$syncShow">远程:
        </div>

        <div class="notHigh" ng-if="pitem.$$syncShow&&pitem.$$localAdd" ng-style="{width:allWidth-50-60-100}"
             style="background-color: #eee;">不存在
        </div>

        <div class="textNowrap" ng-repeat="item in mDataDef"
             ng-if="isShowField(item)&&pitem.$$syncShow&&!pitem.$$localAdd"
             ng-style="{width:item.width,backgroundColor:remoteMap[pitem._id][item.name]!=pitem[item.name]?'#fdf0eb':'#eee'}">
            <span ng-bind="remoteMap[pitem._id][item.name]"></span>
        </div>

        <div class="textNowrap" style="text-align: center;width:60px;background-color: #eee;"
             ng-if="pitem.$$syncShow">
        </div>
        <div class="textNowrap" style="text-align: center;width:100px;background-color: #eee;"
             ng-if="pitem.$$syncShow">
            <button class="button high iconfont icon-addcollapse" title="删除远程数据" type="button"
                    ng-if="!pitem.$$localAdd"
                    ng-click="delRemoveItem(pitem)"></button>
            <button class="button green icon-download" title="下载远程到本地" type="button" ng-click="download(pitem)"
                    ng-if="pitem.$$syncShow&&(pitem.$$isUpdate||pitem.$$remoteAdd)"></button>
        </div>
    </div>
</form>