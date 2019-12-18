<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>


<div id="layout_inc_mian" ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;position: relative">
    <div style="overflow: auto;position: absolute;top:0;left:0;bottom: 0; width:200px;border-right: 1px solid #ccc;">
        <div tree-view id="tree_left" tree-data="demo.tree" model="{{model}}" entity="TestCase" text-field="name"
             value-field='_id' select-type="none"></div>
    </div>
    <div style="overflow: auto;position: absolute;top:0;right:0;bottom: 0; left:200px;padding:10px;background-color: #eee;">
        <div style="position: relative">
            <div style="padding:0 100px 0 25px;">
                <div ng-bind="selectItem.name"
                     style="font-weight: 600;font-size:16px;"></div>
            </div>
            <div ng-class="getTreeItemIcon(selectItem)"
                 style="position:absolute;top:0;bottom: 0;left:0;width:30px;line-height: 26px;font-size:18px;color: #888;"></div>
        </div>
        <div style="padding:2px 0;">
            <div ng-bind="selectItem.desc"
                 style="font-weight: 600;font-size:18px;padding-left: 20px;"></div>
        </div>

        <div style="padding: 25px 0 0 5px;color:#888;border-bottom:1px solid #bbb; " ng-if="selectItem.level<4">
            <span style="color:#888;font-weight: bold;">测试版本</span>
            列表
            <span class="high" ng-bind="selectItem.children.length"></span>
            条
        </div>

        <form ng-repeat="item in selectItem.children"
              style="display: block;border-bottom: 1px dashed #aaa;padding:8px 0;min-height: 45px;">
            <%--名字--%>
            <div style="position: relative;min-height: 26px;">
                <div style="padding:0 100px 0 20px;">
                    <div ng-bind="item.name" ng-show="!item.$$edit"
                         style="font-size: 15px;"></div>
                    <input ng-model="item.name" style="width:100%;font-size: 15px;" ng-blur="saveData(item)"
                           ng-keypress="keyAction(item,$event)"
                           ng-show="item.$$edit"/>
                </div>
                <div style="position: absolute;width:100px;top:0;right:0;bottom:0;">
                    <i class="fr btn icon-addcollapse iconfont high" title="删除" ng-click="delData(item,$index)"></i>
                    <i class="fr btn icon-edit iconfont  green" title="编辑" ng-class="item.$$edit?'selected':''"
                       ng-click="item.$$edit=!item.$$edit"></i>
                </div>
                <div class="icon-file-text2"
                     style="position:absolute;top:0;bottom: 0;left:0;width:30px;line-height: 20px;color: #888;"></div>
            </div>
            <div style="overflow: auto">
                <div class="fl notHigh">

                    覆盖度: <span class="high">156</span>/<span class="green">2333</span><span class="">(45%)</span>
                    big关闭: <span class="high">156</span>/<span class="green">2333</span><span class="">(45%)</span>
                </div>
                <div class="fr notHigh">
                    产品: <span class="">兴文小贷</span>
                    项目: <span class="">A项目</span>
                </div>
            </div>
            <div style="overflow: auto;padding: 6px 0;">
                <div class="fl" style="overflow: auto;width:33.33%">
                    <div class="fl notHigh">代码版本号:</div>
                    <div class="fl" ng-show="!item.$$edit">23432</div>
                    <div class="fl"><input ng-model="" style="width:100px;" ng-show="item.$$edit"/></div>
                </div>
                <div class="fl" style="overflow: auto;width:33.33%">
                    <div class="fl notHigh">对内版本号:</div>
                    <div class="fl" ng-show="!item.$$edit">23432</div>
                    <div class="fl"><input ng-model="" style="width:100px;" ng-show="item.$$edit"/></div>
                </div>
                <div class="fl" style="overflow: auto;width:33.33%">
                    <div class="fl notHigh">对外版本号:</div>
                    <div class="fl" ng-show="!item.$$edit">23432</div>
                    <div class="fl"><input ng-model="" style="width:100px;" ng-show="item.$$edit"/></div>
                </div>
            </div>
            <%--描述--%>
            <div style="padding:5px 0;">
                <div ng-bind="item.desc" ng-show="!item.$$edit" style="color:#888;padding-left: 20px;"></div>
                <textarea style="width:100%;color:#888;min-height: 7em;" ng-model="item.desc"
                          ng-blur="saveData(item)"
                          ng-show="item.$$edit"
                          placeholder="填写描述信息"></textarea>
            </div>
            <div style="overflow:auto;">
                <div class="fl notHigh">发布时间: <span>2016-11-12 12:33</span></div>
                <div class="fr btn">
                    <button style="background-color: transparent;">查看详情 <i class="iconfont icon-right"></i></button>
                </div>
            </div>
        </form>
        <div style="padding:10px 0;text-align: right;">
            <button class=" btn icon-add iconfont green" style="background-color: #eee;" title="添加"
                    ng-if="selectItem.level<4"
                    ng-click="addItem(selectItem)">添加
            </button>
        </div>


    </div>
</div>