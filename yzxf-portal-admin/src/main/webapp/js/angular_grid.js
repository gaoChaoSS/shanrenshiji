(function(angular, undefined) {
    // test data
    var grid_test_header = [ {
        _id : 'xxxx',
        name : 'name',
        title : '名称',
        desc : '',
        type : 'string',
        minLength : 3,
        maxLength : 10,
        canUpdate : true
    }, {
        _id : '234',
        name : 'phone',
        title : '手机',
        desc : '',
        type : 'mobile',
        isNull : false,
        canUpdate : true
    }, {
        _id : '2345',
        name : 'email',
        title : '邮件',
        desc : '',
        type : 'email',
        canUpdate : true
    }, {
        _id : 'xxxx1',
        name : 'desc',
        title : '描述',
        inputType : 'textarea',
        desc : '',
        type : 'string',
        minLength : 0,
        maxLength : 4,
        canUpdate : true,
        rowHide : true,
        reg : ''
    }, {
        _id : 'xxxx33',
        name : 'chu',
        title : '年龄',
        desc : '',
        type : 'int',
        isNull : false,
        min : 10,
        max : 100,
        canUpdate : true,
        reg : ''
    }, {
        _id : 'xxxx2',
        name : 'start',
        title : '开始日期',
        inputType : 'date',
        desc : '',
        type : 'time',
        min : '2015-10-04',
        max : '2015-12-01',
        canUpdate : true,
        reg : ''
    }, {
        _id : 'yyy',
        name : 'createTime',
        title : '创建时间',
        desc : '',
        type : 'time',
        setByServer : true,
        reg : ''
    }, {
        _id : 'xxxx333',
        name : 'dataStatus',
        title : '状态',
        desc : '',
        type : 'boolean',
        isNull : false,
        canUpdate : true,
        reg : ''
    } ];

    window.gridCtrl = function($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        // 动态构建新加数据行
        $scope.grid.actionPath = window.baseApiPath + '/' + $scope.grid.modelName + '/' + $scope.grid.entityName;

        $scope.grid.actions = {
            init : function() {
                if ($scope.grid.isQuickAdd) {
                    $scope.grid.actions.initQuickAdd();
                }
                $scope.grid.actions.resetFilter();
            },
            initQuickAdd : function() {
                // 初始化添加行
                var newElObj = $element.find('.newDataRow');
                newElObj.append('<td></td>');
                $.each($scope.grid.headerRow, function(k, v) {
                    var mName = 'formData.' + v.name;
                    var str = '<td>'
                    if (!v.setByServer) {
                        str += _genInputStr(v);
                    }
                    str += '</td>';
                    newElObj.append(str);
                });
                newElObj.append('<td></td>');
                var aStr = '';
                aStr += '<td ng-show="grid.isEditMode" style="text-align: center;" >';
                aStr += '<i class="icon-plus green fl" style="padding:4px;margin:0 2px;" ng-click="grid.actions.quickAddData();" title="添加一条记录"></i>';
                aStr += '<i class="icon-cross2 highlight fl" style="padding:4px;margin:0 2px;" ng-click="grid.actions.clearQuickData();"></i>';
                aStr += '</td>';
                newElObj.append(aStr);

                var link = $compile(newElObj.contents());
                link($scope);
            },
            showSaveForm : function() {
                var winStr = $templateCache.get('popWin.html');
                var all = $element.find(document).append(winStr)
                var winObj = all.find('.popWin');
                var link = $compile(winObj.contents());
                link($scope);
            },

            resetFilter : function() {
                $scope.grid.baseFilter || ($scope.grid.baseFilter = {});
                $scope.grid.filter = {};
                $.extend(true, $scope.grid.filter, $scope.grid.baseFilter);
            },
            getMData : function(callback) {// 获取元数据
                $http.get($scope.grid.actionPath + '/getMData').success(function(re) {
                    $scope.grid.header = re.content.items;
                    $scope.grid.headerRow = $.grep($scope.grid.header, function(item, i) {
                        var b = item.rowHide == null || item.rowHide == false;// 服务器端设置的是否显示在行的配置
                        if ($scope.grid.fieldShowRow != null) {
                            b = (',' + $scope.grid.fieldShowRow + ',').indexOf(',' + item.name + ',') > -1;
                        }
                        return b;
                    });
                    callback();
                });
            },
            query : function() {
                $http.get($scope.grid.actionPath + '/query' + mapToQueryString($scope.grid.filter)).success(function(re) {
                    $scope.grid.data = re.content;
                    // 分页处理
                    $scope.grid.data.startPage = $scope.grid.data.pageNo - 4;
                    $scope.grid.data.startPage > 0 || ($scope.grid.data.startPage = 1);
                    $scope.grid.data.endPage = $scope.grid.data.pageNo + 4;
                    var totalPage = $scope.grid.data.totalPage;
                    $scope.grid.data.endPage < totalPage || ($scope.grid.data.endPage = totalPage);
                    $scope.grid.data.pageNums = [];
                    for (var i = $scope.grid.data.startPage; i <= $scope.grid.data.endPage; i++) {
                        $scope.grid.data.pageNums.push(i);
                    }
                });
            },
            quickAddData : function() {// 快速添加一条数据
                if ($scope.grid.isTree) {
                    $rootScope.formData.pid = $scope.tree.__selecteTreeItem;
                }
                $rootScope.saveData($scope.grid.actionPath, $rootScope.formData, function() {
                    delete $rootScope.formData._id;
                    $scope.grid.actions.query();
                });
            },
            clearQuickData : function() {
                $rootScope.formData = {};
            },
            saveRowData : function() {// 编辑1行
                $rootScope.saveData($scope.grid.actionPath, $rootScope.formData, function() {
                    $rootScope.formData = {};
                    $scope.grid.actions.query();
                });
            },
            deleteItem : function(index) {// 删除1行
                var data = $scope.grid.data.items[index];
                if (confirm('是否确认删除该条数据?')) {
                    $http['delete']($scope.grid.actionPath + '/' + data._id).success(function() {
                        malert('删除成功！');
                        $scope.grid.actions.query();
                    });
                }
            },
            selectAll : function() {// 选择多行
                $scope.grid.__selectAll = !$scope.grid.__selectAll;
                $.each($scope.grid.data.items, function(k, v) {
                    v.__selected = $scope.grid.__selectAll;
                });
            },
            // 删除多行
            deleteMore : function() {
                var ids = [];
                $.each($scope.grid.data.items, function(k, v) {
                    if (v.__selected) {
                        ids.push(v._id);
                    }
                });
                if (ids.length == 0) {
                    malert('请先选择要删除的数据！');
                    return;
                }

                var data = {
                    ids : ids
                };
                $http.post($scope.grid.actionPath + '/deleteMore', data).success(function() {
                    malert('删除成功！');
                    $scope.grid.actions.query();
                });
            },

            showCellClass : function(rowIndex, headerIndex) {// 根据不同的数据类型显示不同的样式
                var data = $scope.grid.data.items[rowIndex];
                var mdata = $scope.grid.headerRow[headerIndex];
                if (mdata.type == 'boolean') {
                    var str = data[mdata.name] ? 'icon-jianchacheck35 iconfont' : 'icon-cross';
                    return str + ($scope.grid.isEditMode ? ' stateBoolen' : '');
                }
                return '';
            },
            showCell : function(rowIndex, headerIndex) {// 根据不同的数据类型显示不同的值
                var data = $scope.grid.data.items[rowIndex];
                var mdata = $scope.grid.headerRow[headerIndex];
                if (mdata.type == 'boolean') {
                    return '';
                }
                return data[mdata.name];
            },
            clickCell : function(rowIndex, headerIndex) {
                var data = $scope.grid.data.items[rowIndex];
                var mdata = $scope.grid.headerRow[headerIndex];
                if (mdata.type == 'boolean') {
                    data[mdata.name] = !data[mdata.name];
                    $rootScope.saveData($scope.grid.actionPath, data, function() {
                        $scope.grid.actions.query();
                    });
                }
            },
            // 关于 treeGridView
            clickTreeItem : function(item) {
                item.__selected = true;
                $scope.__selecteTreeItem = item._id;
                $scope.grid.filter._pid = item._id;
                $scope.grid.actions.query();
            },
            openTreeItem : function(item) {
                item.__isOpen = !item.__isOpen;
                if (item.__isOpen) {
                    $http.get($scope.grid.actionPath + '/query?pageSize=500&_pid=' + item._id).success(function(re) {
                        item.items = re.content.items;
                    });
                }
            }
        }
        // 通过调取元数据开始
        $scope.grid.actions.getMData(function() {
            $scope.grid.actions.init();
            if ($scope.grid.isTree) {
                // $element.find('.rootItem').click();
                $scope.tree = {
                    modelName : $scope.grid.modelName,
                    entityName : $scope.grid.entityName,
                    name : $scope.grid.treeName,
                    data : [ {
                        name : '菜单',
                        _id : '-1'
                    } ]
                };
                window.treeCtrl(actionPath, $scope.tree, $http);
                // 重写点击item方法
                var _clickTreeItem = $scope.tree.actions.clickTreeItem;
                $scope.tree.actions.clickTreeItem = function(item) {
                    _clickTreeItem(item);
                    $scope.grid.filter._pid = item._id;
                    $scope.grid.actions.query();
                }

                $scope.treeList = $scope.tree.data;
                $scope.tree.actions.selectRoot();

            } else {
                $scope.grid.actions.query();
            }
        });
    }

})(angular);