<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div id="layout_inc_mian" ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;position: relative">
    <div style="overflow: auto;position: absolute;top:0;left:0;bottom: 0; width:200px;border-right: 1px solid #ccc;">
        <div tree-view id="tree_left" tree-data="demo.tree" model="{{model}}" entity="{{entity}}" text-field="name"
             value-field='_id' select-type="none"></div>
    </div>
    <div style="overflow: auto;position: absolute;top:0;right:0;bottom: 0; left:200px;padding:10px;background-color: #eee;">
        <div style="position: relative">
            <div style="padding:0 100px 0 25px;">
                <div ng-bind="selectItem.name" ng-show="!selectItem.$$edit"
                     style="font-weight: 600;font-size:16px;"></div>
                <input ng-model="selectItem.name" style="font-weight: 600;font-size:16px;width:100%;"
                       ng-blur="saveData(selectItem)"
                       ng-show="selectItem.$$edit"/>
            </div>
            <div style="position: absolute;width:100px;top:0;right:0;bottom:0;">
                <%--<i class="fr btn icon-addcollapse iconfont high" title="删除" ng-click=""></i>--%>
                <i class="fr btn icon-edit iconfont  green" title="编辑" ng-class="item.$$edit?'selected':''"
                   ng-if="selectItem.pid!=null&&selectItem.pid!=''"
                   ng-click="selectItem.$$edit=!selectItem.$$edit"></i>
            </div>
            <div ng-class="getTreeItemIcon(selectItem)"
                 style="position:absolute;top:0;bottom: 0;left:0;width:30px;line-height: 26px;font-size:18px;color: #888;"></div>
        </div>
        <div style="padding:2px 0;">
            <div class="notHigh" ng-bind="selectItem.desc" ng-show="!selectItem.$$edit"
                 style="padding-left: 20px;"></div>
            <textarea style="width:100%;color:#888;min-height: 7em;" ng-model="selectItem.desc"
                      ng-blur="saveData(selectItem)"
                      ng-show="selectItem.$$edit"
                      placeholder="填写描述信息"></textarea>
        </div>

        <div style="padding: 25px 0 0 5px;color:#888;border-bottom:1px solid #bbb; " ng-if="selectItem.level<4">
            <span style="color:#888;font-weight: bold;" ng-bind="typeName[selectItem.level+1]"></span>
            列表
            <span class="high" ng-bind="selectItem.children.length"></span>
            条
        </div>

        <form ng-repeat="item in selectItem.children"
              style="display: block;border-bottom: 1px dashed #aaa;padding:8px 0;min-height: 45px;">
            <div style="position: relative">
                <div style="padding:0 100px 0 20px;">
                    <div ng-bind="item.name" ng-show="!item.$$edit"
                         style=""></div>
                    <input ng-model="item.name" style="width:100%;" ng-blur="saveData(item)"
                           ng-keypress="keyAction(item,$event)"
                           ng-show="item.$$edit"/>
                </div>
                <div style="position: absolute;width:100px;top:0;right:0;bottom:0;">
                    <i class="fr btn icon-addcollapse iconfont high" title="删除" ng-click="delData(item,$index)"></i>
                    <i class="fr btn icon-edit iconfont  green" title="编辑" ng-class="item.$$edit?'selected':''"
                       ng-click="item.$$edit=!item.$$edit"></i>
                </div>
                <div ng-class="getTreeItemIcon(item)"
                     style="position:absolute;top:0;bottom: 0;left:0;width:30px;line-height: 20px;color: #888;"></div>
            </div>


            <div style="padding:5px 0;">
                <div ng-bind="item.desc" ng-show="!item.$$edit" style="color:#888;padding-left: 20px;"></div>
                <textarea style="width:100%;color:#888;min-height: 7em;" ng-model="item.desc"
                          ng-blur="saveData(item)"
                          ng-show="item.$$edit"
                          placeholder="填写描述信息"></textarea>
            </div>
        </form>
        <div style="padding:10px 0;text-align: right;">
            <button class=" btn icon-add iconfont green" style="background-color: #eee;" title="添加一条数据"
                    ng-if="selectItem.level<4"
                    ng-click="addItem(selectItem)">添加一条
                <span class="green" ng-bind="typeName[selectItem.level+1]"></span>
            </button>
        </div>


        <div style="padding: 25px 0 0 5px;color:#888;border-bottom:1px solid #bbb; " ng-if="selectItem.level==4">
            测试版本
            <span class="high" ng-bind="selectItem.children.length"></span>
            条
        </div>
    </div>
</div>