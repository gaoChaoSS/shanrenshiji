<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<span ng-if="!isEditObj[pitem._id+'_'+item.name]&&item.inputType!='boolean'"
      ng-bind="pitem[item.name]"></span>
<input ng-if="isEditObj[pitem._id+'_'+item.name]&&item.inputType=='input'"
       ng-model="pitem[item.name]"
       ng-blur="saveData(pitem,item.name);"
       ng-keyup="inputKeyUp($event,pitem,item.name)"
       ng-mouseover="selectText($event)"/>

<input ng-if="isEditObj[pitem._id+'_'+item.name]&&item.inputType=='number'" type="number"
       ng-model="pitem[item.name]"
       ng-blur="saveData(pitem,item.name);"
       ng-keypress="inputKeyUp($event,pitem,item.name)"
       ng-mouseover="selectText($event)"/>

<select ng-if="isEditObj[pitem._id+'_'+item.name]&&item.inputType=='select'"
        ng-model="pitem[item.name]"
        ng-change="saveData(pitem,item.name);"
        ng-options="vv for vv in item.inputTypeList">
</select>

<button ng-show="item.inputType=='boolean'" type="button"
        class="button iconfont icon-jianchacheck35"
        ng-click="pitem[item.name]=!pitem[item.name];saveData(pitem,item.name);"
        style="font-size: 15px;"
        ng-class="pitem[item.name]?'green':'notHigh'"
        title="{{'设置:'+item.title}}"></button>