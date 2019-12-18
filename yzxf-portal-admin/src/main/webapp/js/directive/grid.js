/**
 * 使用例子：(须注意先后顺序)
 *
 *
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    window.test = function () {
        console.log('xxx');
    }
    window.app.directive('grid', function () {
        return {
            restrict: 'AE',
            transclude: true,
            replace: true,
            scope: {
                id: '@',
                entity: '@',
                model: '@',
                isEdit: '=',
                excludeKeys: '@',
                includeKeys: '@',
                selectDataType: '@',
                selectValue: '@',//设置已选择数据的Id
                selectValueUrl: '@',//查询已选择数据的url
                contentTemp: '@'
            },
            templateUrl: '/temp/grid.html?_v=' + window.angular_temp_version,
            controller: function ($rootScope, $scope, $element, $http, $q, $compile, $popWindow, $templateCache) {
                if ($scope.id == null) {
                    $scope.id = 'grid_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }

                $scope.popWinId = $scope.id + '_popwin';
                var popType = null, __cellInputType = null;
                var keywords = $element.attr('keywords');

                $scope.formData = {};

                if ($scope.model != null && $scope.entity != null) {
                    $scope.actionPath = window.basePath + '/' + $scope.model + '/' + $scope.entity;
                }
                $scope.isEdit = $scope.isEdit == null ? false : $scope.isEdit;
                $scope.isQuickAdd = $element.attr('quickAdd') == 'true';
                $scope.data = {
                    items: []
                };
                $scope.headerLeft = 0;
                $scope.selectDataType = $scope.selectDataType ? $scope.selectDataType : 'more';// 选择方式，one：单选，more：多选，none：不能选
                //多选并且设置了selectValueUrl的情况下,拥有最高优先级.
                if ($scope.selectDataType == 'more' && !window.isEmpty($scope.selectValueUrl)) {
                    $http.get(window.basePath + $scope.selectValueUrl).success(function (re) {
                        $.each(re.content.items, function (k, v) {
                            $scope.selectIdMap || ($scope.selectIdMap = {});
                            $scope.selectIdMap[v['linkEntityId']] = true;
                        });
                    });
                } else {
                    if (!window.isEmpty($scope.selectValue)) {
                        var ids = $scope.selectValue.split(',');
                        if (ids instanceof Array) {
                            $.each(ids, function (k, v) {
                                $scope.selectIdMap || ($scope.selectIdMap = {});
                                $scope.selectIdMap[v] = true;
                            });
                        }
                    }
                }
                // 按钮显示控制
                $scope.showEditBtn = true;
                $scope.showShowTypeBtn = true;

                // 展示方式
                $scope.conTempList = [{
                    name: '网格',
                    value: '/temp/inc_grid.html'
                }, {
                    name: '表格',
                    value: '/temp/inc_grid_table.html'
                }, {
                    name: '大图标',
                    value: '/temp/inc_grid_icon_max.html'
                }, {
                    name: '小图标',
                    value: '/temp/inc_grid_icon_min.html'
                }];
                $scope.contentTemp = '/temp/inc_grid.html';

                /**
                 * 获取元数据
                 */
                $scope.genMDataAndQuery = function (reLoad) {
                    $rootScope.mdata || ($rootScope.mdata = {});
                    $scope.resetFilter();
                    if ($rootScope.mdata[$scope.actionPath] == null || reLoad) {
                        $http.get($scope.actionPath + '/getMData').success(function (re) {
                            $scope.isSyncTable = re.content.isSyncTable;
                            if ($scope.isSyncTable) {
                                $scope.syncData || ($scope.syncData = {});
                                $scope.syncData.entityVersion || ($scope.syncData.entityVersion = {});
                                if (re.content.entityVersion != null) {
                                    $scope.syncData.entityVersion = re.content.entityVersion;
                                }
                            }
                            $.each(re.content.items, function (k, v) {
                                v.inputType = window.isEmpty(v.inputType) ? 'input' : v.inputType;
                                if (v.name == 'sortNo') {//如果有排序号,默认使用排序号进行排序
                                    $scope.baseFilter.orderBy = 'sortNo';
                                    $scope.filter.orderBy = $scope.baseFilter.orderBy;
                                } else if (v.name == 'createTime' && $scope.baseFilter.orderBy !== 'sortNo') {//sortNo优先级最高
                                    $scope.baseFilter.orderBy = 'createTime';
                                }
                            });

                            $rootScope.mdata[$scope.actionPath] = re.content.items;
                            $scope.genMData();
                            $scope.query();
                        }).error(function () {
                        });
                    } else {
                        $scope.genMData();
                        $scope.query();
                    }
                }


                // 根据元数据产生列
                $scope.genMData = function () {
                    $scope.headerRow = [];
                    $scope.contentWidth = 0;

                    $scope.filter || ($scope.filter = {});
                    var excludeKeys = $scope.filter.excludeKeys;
                    var includeKeys = $scope.filter.includeKeys;
                    if (!window.isEmpty(excludeKeys)) {
                        excludeKeys = ',' + excludeKeys + ',';
                    }
                    var includeStr = '';
                    if (!window.isEmpty(includeKeys)) {
                        includeStr = ',' + includeKeys + ',';
                    }

                    var hideRowStr = window.localStorage.getItem($scope.id + '/hideRowStr');
                    var hideRowMap = {}, hideNum = 0;
                    if (!window.isEmpty(hideRowStr)) {
                        var hideRows = hideRowStr.split(',');
                        $.each(hideRows, function (kk, vv) {
                            hideRowMap[vv] = true;
                        })
                    }

                    var fieldMap = {};
                    angular.forEach($rootScope.mdata[$scope.actionPath], function (k) {
                        var ok = false;
                        if (!window.isEmpty(includeStr)) {
                            if (includeStr.indexOf(',' + k.name + ',') > -1) {
                                ok = true;
                            }
                        } else {
                            ok = !(!window.isEmpty(excludeKeys) && excludeKeys.indexOf(',' + k.name + ',') > -1);
                        }
                        if (ok) {
                            k.__width = 100;
                            if (k.name == 'name' || k.name == 'title' || k.name == 'desc' || k.name == 'chineseName') {
                                k.__width = 200;
                            }
                            if (k.inputType == 'dateTime') {
                                k.__width = 160;
                            }
                            if (k.inputTypeList != null) {
                                if (k.inputTypeList instanceof Array) {
                                    k.$$inputTypeList = k.inputTypeList;
                                } else if (k.inputTypeList.indexOf("[") == 0) {
                                    k.$$inputTypeList = JSON.parse(k.inputTypeList);
                                } else {
                                    var ss = k.inputTypeList.split(',');
                                    k.$$inputTypeList = [];
                                    $.each(ss, function (kk, vv) {
                                        k.$$inputTypeList.push({_id: vv, name: vv});
                                    })
                                }
                            }

                            k.type || (k.type = 'string');
                            k.inputType || (k.inputType = 'input');
                            k.inputType = k.type == 'boolean' ? 'boolean' : k.inputType;
                            k.$$isFileObj = k.inputType.startsWith('file');
                            k.$$isEntityObj = k.inputType.startsWith('linkEntity');
                            k.$$dateInput = k.inputType == 'date' || k.inputType == 'time' || k.inputType == 'dateTime';
                            k.$$numberInput = k.type == 'int' || k.type == 'long' || k.type == 'double';
                            k.$$name = window.isEmpty(k.title) ? k.name : k.title;

                            if (hideRowMap[k.name]) {
                                hideNum++;
                            }
                            k.$$selected = !hideRowMap[k.name];


                            $scope.headerRow.push(k);
                            fieldMap[k.name] = k;
                            $scope.contentWidth += k.__width;
                        }
                    });

                    $scope.selectedRowCount = $scope.headerRow.length - hideNum;

                    var includes = [];
                    $.each($scope.headerRow, function (k, v) {
                        includes.push(v.name);
                    });
                    $scope.baseFilter.includeKeys = includes.join(',');
                    if ($scope.filter != null) {
                        $scope.filter.includeKeys = $scope.baseFilter.includeKeys;
                    }
                }

                //切换添加或过滤
                $scope.fristRowAction = 'query';
                $scope.firstRow = function (type) {
                    if (type == 'add' && !$scope.isEdit) {
                        malert('请打开上方: "编辑模式" 按钮')
                        return;
                    }
                    if ($scope.fristRowAction == type) {
                        if ($scope.fristRowAction == 'add') {
                            $scope.saveData()
                        }
                    } else {
                        //$scope.clearQuickAddData();
                        malert('切换为: "' + (type == 'add' ? '快速添加' : '栏位过滤') + '" 模式')
                    }
                    $scope.fristRowAction = type;
                }
                $scope.filterList = function () {
                    if (!$scope.isEdit || $scope.fristRowAction == 'query') {
                        $scope.filter.____quickFilterMap = JSON.stringify($scope.formData);
                        //console.log('$scope.filter.____filterMap: ', $scope.filter.____filterMap);
                        $scope.query(true);
                    } else {

                    }
                }
                //显示日期控件
                $scope.showDate = function (rowIndex, cellIndex) {
                    if (rowIndex != null) {
                        $scope.__selectRow = rowIndex;
                    }
                    $scope.__selectCell = cellIndex;
                    popType = 'selectDateWin';
                    $popWindow.add($scope.popWinId, '选择日期', 300, 400, null, false, true);
                }
                // 切换编辑模式
                $scope.updateEditMode = function () {
                    $scope.isEdit = !$scope.isEdit;
                }
                // check input data
                $scope.checkInputData = function (field, item) {
                    // 服务器设置的字段不必校验
                    if (field.setByServer) {
                        return '';
                    }
                    item || (item = {});
                    var msg = '';
                    var data = item[field.name];
                    data = data == null ? '' : data;
                    // 1.检查是否为空
                    if (field.isNotNull && (data == null || data == '')) {
                        msg += '"' + field.title + '" 必须填写';
                        return msg;
                    }

                    // 数据为空，并且字段定义又允许为空，则不需要验证数据的位数
                    if (!((data == null || data == '') && !field.isNotNull)) {
                        // 2.检查长度是否在指定的范围
                        if (field.minLength != null && field.minLength > data.length) {
                            msg += '"' + field.title + '" 的长度最小为' + field.minLength + '位';
                            return msg;
                        }
                        if (field.maxLength != null && field.maxLength > 0 && field.maxLength < data.length) {
                            msg += '"' + field.title + '" 的长度最大为' + field.maxLength + '位';
                            return msg;
                        }
                    }
                    // 3.检查格式是否正确
                    var checkData = window.checkTypes[field.type];
                    if (checkData != null) {
                        if (!checkData.reg.test(data)) {
                            msg += '"' + field.title + '" 格式不正确[' + checkData.desc + ']!';
                            return msg;
                        }
                    }
                    return msg;
                }
                $scope.checkInput = function (field, item, showType) {
                    console.log('--data:' + $scope.formData[field.name])
                    field.__errMsg = $scope.checkInputData(field, item);
                    if (showType == 1 && field.__errMsg.length > 0) {
                        malert(field.__errMsg);
                        return false;
                    }
                    return true;
                }


                // 检查输入表单数据是否合法
                $scope.checkForm = function (item) {
                    var isOk = true;
                    $.each($scope.headerRow, function (k, v) {
                        var msg = $scope.checkInputData(v, item);
                        if (msg.length > 0) {
                            isOk = false;
                            console.log(msg);
                        }
                    });
                    return isOk;
                }
                // 获取快速添加样式
                $scope.getQuickAddClass = function (field, item) {
                    var className = field.type == 'long' || field.type == 'int' || field.type == 'double' ? 'num' : ''
                    field.__errMsg = $scope.checkInputData(field, item);
                    className += field.__errMsg.length > 0 ? ' err' : ' ok';
                    return className;
                }
                // 动态添加按钮
                $scope.addButtomAction = function (item) {
                    $scope.bottomActions || ($scope.bottomActions = []);
                    $scope.bottomActions.splice(0, 0, item);
                }
                // 底部按钮被点击
                $scope.clickBottomAction = function (item) {
                    if (item != null && item.click != null) {
                        item.click();
                    }
                }

                // 发送请求保存数据到服务器端
                $scope.saveData = function () {
                    if ($scope.formData == null) {
                        return;
                    }
                    // alert($scope.gridQuickAddForm);
                    // check form data
                    if (!$scope.checkForm($scope.formData)) {
                        malert('数据校验不通过，请检查！');
                        return;
                    }

                    var isAdd = false;
                    if ($scope.formData._id == null) {
                        $scope.formData._id = window.genUUID();
                        isAdd = true;
                    }

                    $http.put($scope.actionPath + '/save', $scope.formData).success(function (re) {
                        delete $scope.formData._id;
                        malert(isAdd ? '成功添加1条数据' : '保存成功');
                        if (isAdd) {
                            // 让数据显示在第一行，便于发现添加成功了
                            $scope.filter = $.extend($scope.filter, {
                                keywordValue: '',
                                orderBy: 'createTime',
                                orderByAsc: -1,
                                pageNo: 1
                            });
                        }
                        $scope.query();
                    });
                }
                // 清除快速添加行
                $scope.clearQuickAddData = function () {
                    $scope.formData = {};
                    if ($scope.baseFormData != null) {
                        $.extend(true, $scope.formData, $scope.baseFormData);
                    }
                    $scope.checkForm($scope.formData);
                }
                $scope.showSaveForm = function () {
                    // var elObj = angular.element('<pop-win win-pop="true" win-title="添加数据" win-width="700" win-height="500" >xxxx</pop-win>')
                    // angular.element('body').append(elObj);
                    // $(elObj).showCenter();
                    // var link = $compile(elObj);
                    // link($scope);
                    popType = 'addDataForm';
                    $popWindow.add($scope.popWinId, '添加数据', 550, 400, null, false, true);
                }
                $scope.showEditForm = function (item, index) {
                    popType = 'showEditForm';
                    $scope.__selectAll = true;
                    $scope.selectAll();
                    $scope.selectRow(item);
                    $scope.__selectRow = index;
                    $popWindow.add($scope.popWinId, '查看数据', 550, 400, null, false, true);
                }

                var fixDataRow = function (k, data) {
                    if ($scope.headerRow != null) {
                        //var n = {};
                        //$.extend(true, n, v);
                        $.each($scope.headerRow, function (kk, mdata) {
                            if (mdata == null) {
                                return true;
                            }
                            fixDataCell(mdata, data);
                        });
                        data.__selected = $scope.selectIdMap != null ? $scope.selectIdMap[data._id] : false;
                    }
                    return data;
                }
                var fixDataCell = function (mdata, data) {
                    var vobj = data[mdata.name];
                    var len = 0;
                    if (vobj != null) {
                        len = jQuery.type(vobj) === 'string' && !window.isEmpty(vobj) ? 1 : len;
                        len = jQuery.type(vobj) === 'array' && !window.isEmpty(vobj) ? vobj.length : len;
                    }

                    var str = vobj;
                    if (mdata.inputType == 'linkEntity') {
                        str = '浏览(' + len + ')...';
                    } else if (mdata.inputType == 'linkEntityMore' || mdata.inputType == 'fileMore') {
                        str = '浏览(' + (vobj == null ? 0 : vobj) + ')...';
                    } else if (mdata.inputType == 'file') {
                        str = '浏览(' + len + ')';
                    } else if (mdata.inputType.indexOf('htmlEdit') > -1) {
                        str = '编辑...';
                    } else if (mdata.inputType.indexOf('password') > -1) {
                        str = '重置...';
                    } else if (mdata.$$dateInput) {
                        str = window.showDateStr(mdata.inputType, vobj);
                    }

                    data['$$' + mdata.name] = str;
                }

                // 执行一次查询,查询必须顺序执行,增强性能
                $scope.queryLoading = false;
                $scope.query = function (isBackRun) {
                    if ($scope.actionPath == null) {
                        malert('actionPath is null');
                        return;
                    }
                    if ($scope.queryLoading) {
                        return;
                    }
                    $scope.queryLoading = true;
                    if ($scope.filter == null) {
                        $scope.resetFilter();
                    }
                    if ($scope.__keyword) {
                        $scope.filter.keywordValue = $scope.__keyword;
                    }
                    var url = $scope.actionPath + (!window.isEmpty($scope.queryUrl) ? $scope.queryUrl : '/query');
                    $http({
                        method: 'get',
                        url: url + mapToQueryString($scope.filter),
                        isBackRun: isBackRun
                    }).success(function (re) {
                        $scope.data = re.content;
                        $scope.__cellMdata = {};// cell编辑选中
                        $scope.data.__cellErrMsg = {};// cell编辑的错误信息
                        $scope.__cellData = {};// cell编辑临时数据
                        $.each($scope.data.items, function (k, v) {
                            fixDataRow(k, v);
                        });

                        // 分页处理
                        $scope.data.startPage = $scope.data.pageNo - 4;
                        $scope.data.startPage > 0 || ($scope.data.startPage = 1);
                        $scope.data.endPage = $scope.data.pageNo + 4;
                        var totalPage = $scope.data.totalPage;
                        $scope.data.endPage < totalPage || ($scope.data.endPage = totalPage);
                        $scope.data.pageNums = [];
                        for (var i = $scope.data.startPage; i <= $scope.data.endPage; i++) {
                            $scope.data.pageNums.push(i);
                        }
                        $scope.__selectAll = false;
                        $scope.filter.pageNo = $scope.data.pageNo;
                        $scope.filter.pageSize = $scope.data.pageSize;

                        $scope.queryLoading = false;
                    }).error(function () {
                        $scope.queryLoading = false;
                    });
                };
                // 单击栏目头排序
                $scope.orderByHeader = function (field) {
                    $scope.filter.orderBy = field.name;
                    $scope.filter.orderByAsc = $scope.filter.orderByAsc == 1 ? -1 : 1;
                    field.__orderbyClass = 'selected ';
                    field.__orderbyClass += $scope.filter.orderByAsc == 1 ? 'icon-arrow-down green' : 'icon-arrow-up green';
                    $scope.query();
                }
                //快速添加输入框,过滤数据




                $scope.deleteItem = function (index) {// 删除1行
                    if (!$scope.isEdit) {
                        malert('请打开上方: "编辑模式"')
                        return;
                    }

                    var data = $scope.data.items[index];
                    if (confirm('是否确认删除该条数据?')) {
                        var url = $scope.actionPath + '/del';
                        if ($scope.deleteUrl != null) {
                            url = $scope.deleteUrl;
                        }
                        $http.post(url, {
                            _id: data._id
                        }).success(function () {
                            malert('删除成功！');
                            $scope.query();
                        });
                    }
                }
                $scope.deleteMore = function () {
                    var ids = [];
                    $.each($scope.data.items, function (k, v) {
                        if (v.__selected) {
                            ids.push(v._id);
                        }
                    });
                    if (ids.length == 0) {
                        malert('请先选择要删除的数据！');
                        return;
                    }
                    var data = {
                        ids: ids
                    };
                    if (confirm('是否确认删除这' + ids.length + '条数据')) {
                        $http.post($scope.actionPath + '/deleteMore', data).success(function () {
                            malert('删除成功！');
                            $scope.query();
                        });
                    }
                }
                // 显示相关
                $scope.showCellClass = function (rowIndex, headerIndex) {
                    var data = $scope.data.items[rowIndex];
                    var mdata = $scope.headerRow[headerIndex];
                    var str = '';
                    if (mdata.type == 'boolean') {
                        str += data[mdata.name] ? 'icon-jianchacheck35 iconfont' : 'icon-addcollapse iconfont';
                    } else {
                        str += mdata.inputType;
                    }
                    if ($scope.__selectRow == rowIndex && $scope.__selectCell == headerIndex) {
                        str += ' selected';
                    }
                    return str;
                }
                $scope.clickCell = function (rowIndex, headerIndex) {
                    $scope.__selectRow = rowIndex;
                    $scope.__selectCell = headerIndex;

                    var data = $scope.data.items[rowIndex];
                    var mdata = $scope.headerRow[headerIndex];

                    if ((!$scope.isEdit && !mdata.$$isFileObj && !mdata.$$isEntityObj && mdata.inputType != 'htmlEdit') || mdata.readOnly || mdata.setByServer) {
                        return;
                    }

                    if (mdata.inputType == 'boolean') {
                        var newData = {};
                        $.extend(newData, data);
                        newData[mdata.name] = !data[mdata.name];
                        $http.put($scope.actionPath + '/save', newData).success(function (re) {
                            malert('保存成功');
                            data[mdata.name] = !data[mdata.name];
                        });
                    } else {
                        $scope.$$selectEditData = data[mdata.name];
                        if (mdata.inputType.indexOf('linkEntity') > -1) {
                            popType = 'linkEntityWin';
                            $popWindow.add($scope.popWinId, '修改 [' + mdata.title + ']', 700, 400);
                        } else if (mdata.inputType.indexOf('file') > -1) {
                            popType = 'uploadFileWin';
                            $popWindow.add($scope.popWinId, '修改 [' + mdata.title + ']', 700, 400, null, false, true);
                        } else if (mdata.inputType.indexOf('htmlEdit') > -1) {
                            popType = 'htmlEditWin';
                            $popWindow.add($scope.popWinId, '编辑富文本 [' + mdata.title + ']', 700, 400);
                        } else if (mdata.inputType.indexOf('password') > -1) {
                            if (confirm('是否确定进行密码重置?')) {
                                $http.post($scope.actionPath + '/resetPassword', {
                                    _id: data._id,
                                    name: mdata.name
                                }).success(function (re) {
                                    $scope.$$selectEditData = re.content.password;
                                    popType = 'resetPassword';
                                    $popWindow.add($scope.popWinId, '重置密码', 350, 200);
                                });
                            }
                        } else if (mdata.$$dateInput) {
                            $scope.showDate(rowIndex, headerIndex);
                        }
                    }
                }

                //选择行
                $scope.selectHeaderRow = function () {
                    popType = 'selectHeaderRow';
                    $popWindow.add($scope.popWinId, '选择栏位', 300, 450, null, false, true);
                }
                // 弹出窗口初始化成功的回调
                // var $scope.popWinId + '_tree' = $scope.popWinId + '_tree';
                // var $scope.popWinId + '_entityGrid' = $scope.popWinId + '_entityGrid';


                /* 接收消息处理 */
                // 弹出窗口初始化完成
                $scope.$on($scope.popWinId + '/init_end', function (event, obj) {
                    var mdata = $scope.headerRow[$scope.__selectCell];
                    var data = $scope.formData;
                    if ($scope.__selectRow != null) {
                        data = $scope.data.items[$scope.__selectRow];
                    }
                    if (popType == 'linkEntityWin') {//关联对象
                        var model = $scope.model, entity = null;
                        if (mdata._linkTable.startsWith('/')) {
                            var sp = mdata._linkTable.split('/');
                            model = sp[1];
                            entity = sp[2];
                        } else {
                            entity = mdata._linkTable;
                        }
                        var winData = {
                            id: $scope.popWinId,
                            model: model,
                            entity: entity,
                            inputType: mdata.inputType,
                            isTree: mdata.selectUi == 'tree'
                        };
                        if (mdata.inputType == 'linkEntityMore') {
                            if (data != null && !window.isEmpty(data._id != null)) {
                                winData.selectValueUrl = '/' + $scope.model + '/' + $scope.entity + '/findLinkTableData?entityField=' + mdata.name + '&entityId=' + data._id;
                            }
                        } else {
                            winData.selectValue = data[mdata.name];
                        }
                        window.reqUIAction($scope, $scope.popWinId + '/setData', winData);
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_form_selectEntity.html');
                        window.reqUIAction($scope, $scope.popWinId + '/addAction', {
                            name: 'ok',
                            title: '确定',
                            isHigh: true,
                            click: function () {
                                window.reqUIAction($scope, $scope.popWinId + '_entityGrid' + '/getData', 'selectIdMap', function (obj) {
                                    if (obj != null) {
                                        if (mdata.inputType == 'linkEntityMore') {
                                            var ids = [];
                                            $.each(obj, function (k, v) {
                                                if (obj[k]) {
                                                    ids.push(k);
                                                }
                                            });
                                            if (ids.length > 0) {
                                                var reqData = {
                                                    id: $scope.popWinId,
                                                    entityField: mdata.name,
                                                    isAdd: false,
                                                    entityId: data._id,
                                                    linkEntityIds: ids.join(',')
                                                };
                                                $http.post(window.basePath + '/' + $scope.model + '/' + $scope.entity + '/saveLinkTableData', reqData).success(function (re) {
                                                    data[mdata.name] = re.content.count;
                                                    fixDataCell(mdata, data);
                                                });
                                            }
                                        } else if (mdata.inputType == 'linkEntity') {
                                            $.each(obj, function (k, v) {
                                                if (obj[k]) {
                                                    $scope.$$selectEditData = k;
                                                    $scope.popWinSaveCell($scope.popWinId);
                                                }
                                            });
                                        }
                                    }
                                    window.reqUIAction($scope, $scope.popWinId + '/close');
                                });
                            }
                        });
                    } else if (popType == 'uploadFileWin') {//上传文件
                        window.reqUIAction($scope, $scope.popWinId + '/setData', {
                            entity: $scope.entity,
                            model: $scope.model,
                            entityField: mdata.name,
                            entityId: data._id,
                            inputType: mdata.inputType,
                            isEdit: $scope.isEdit,
                            value: data[mdata.name]
                        });
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_grid_file.html');
                    } else if (popType == 'htmlEditWin') {//html编辑器
                        var htmlEditId = $scope.popWinId + '_htmlEdit';
                        $scope.htmlEditId = htmlEditId;
                        window.reqUIAction($scope, $scope.popWinId + '/setData', {
                            htmlEditId: $scope.htmlEditId,
                            entity: $scope.entity,
                            model: $scope.model,
                            entityField: mdata.name,
                            entityId: data._id,
                            isEdit: $scope.isEdit
                        });
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_grid_htmlEditor.html');
                        window.reqUIAction($scope, $scope.popWinId + '/addAction', {
                            name: 'ok',
                            title: '确定',
                            isHigh: true,
                            click: function () {
                                window.reqUIAction($scope, htmlEditId + '/saveData', null, function (obj) {
                                    console.log('--html content:', obj);
                                    window.reqUIAction($scope, $scope.popWinId + '/close');
                                });
                            }
                        });
                    } else if (popType == 'selectDateWin') {//选择日期
                        window.reqUIAction($scope, $scope.popWinId + '/setData', {
                            entity: $scope.entity,
                            model: $scope.model,
                            name: mdata.name,
                            type: mdata.inputType,
                            value: data['$$' + mdata.name]
                        });
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_grid_selectDate.html');
                    } else if (popType == 'resetPassword') {//重置密码
                        //console.log($scope.$$selectEditData)
                        var contentStr = '<div style="padding:40px;text-align: center;">' +
                            '请牢记新密码: ' +
                            '<span class="high" style="font-size:35px;">' + $scope.$$selectEditData + '</span>' +
                            '</div>';
                        window.reqUIAction($scope, $scope.popWinId + '/setContent', contentStr);
                        window.reqUIAction($scope, $scope.popWinId + '/addAction', {
                            name: 'ok',
                            title: '确定',
                            isHigh: true,
                            click: function () {
                                window.reqUIAction($scope, $scope.popWinId + '/close');
                            }
                        });
                    } else if (popType == 'addDataForm') {//添加数据
                        window.reqUIAction($scope, $scope.popWinId + '/setData', {
                            entity: $scope.entity,
                            model: $scope.model,
                            isEdit: true,
                            hideTop: true,
                            hideDown: false,
                            formId: $scope.popWinId + '_form'
                        });
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_grid_saveForm.html');
                    } else if (popType == 'showEditForm') {//编辑数据
                        window.reqUIAction($scope, $scope.popWinId + '/setData', {
                            entity: $scope.entity,
                            model: $scope.model,
                            rowValue: $scope.data.items[$scope.__selectRow]._id,
                            hideTop: false,
                            hideDown: false,
                            isEdit: false,
                            formId: $scope.popWinId + '_form'
                        });
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_grid_saveForm.html');
                    } else if (popType == 'selectHeaderRow') {//选择栏位

                        window.reqUIAction($scope, $scope.popWinId + '/setData', {
                            headerRowObj: {
                                items: $scope.headerRow,
                                gridName: $scope.id,
                                selectedRowCount: $scope.selectedRowCount
                            },
                            selectAll: function (headerRowObj) {
                                //console.log('items', items);
                                if (headerRowObj.items.length > 0) {
                                    var vvv = !headerRowObj.items[0].$$selected;
                                    var hideRows = [];
                                    $.each(headerRowObj.items, function (k, v) {
                                        v.$$selected = vvv;
                                        if (!vvv) {
                                            hideRows.push(v.name);
                                        }
                                    });
                                    //$scope.headerRow = headerRowObj.items;
                                    headerRowObj.selectedRowCount = vvv ? headerRowObj.items.length : 0;
                                    $scope.selectedRowCount = headerRowObj.selectedRowCount;
                                    window.localStorage.setItem($scope.id + '/hideRowStr', hideRows.join(','));
                                }
                            },
                            selectItem: function (headerRowObj, index) {
                                headerRowObj.selectedRowCount = 0;
                                var hideRows = [];
                                $.each(headerRowObj.items, function (k, v) {
                                    if (k == index) {
                                        v.$$selected = !v.$$selected;
                                    }
                                    if (v.$$selected) {
                                        headerRowObj.selectedRowCount++
                                    } else {
                                        hideRows.push(v.name);
                                    }
                                });
                                $scope.selectedRowCount = headerRowObj.selectedRowCount;
                                window.localStorage.setItem($scope.id + '/hideRowStr', hideRows.join(','));
                            }
                        });
                        window.reqUIAction($scope, $scope.popWinId + '/setTemplate', '/temp/inc_grid_selectHeaderRow.html');
                    }
                });
                //form保存数据处理
                $scope.$on($scope.popWinId + '_form' + '/saveSuccess', function (event, obj) {
                    $scope.query();
                });

                //文件上传成功
                $scope.$on('/uploadSuccess', function (event, fileData) {
                    var mdata = $scope.headerRow[$scope.__selectCell];
                    var data = $scope.formData;
                    if ($scope.__selectRow != null) {
                        data = $scope.data.items[$scope.__selectRow];
                    }
                    if (data._id != null) {
                        $scope.__cellData[$scope.__selectRow + '_' + $scope.__selectCell] = mdata.inputType == 'file' ? fileData.fileId : (fileData.index + 1);
                        $scope.saveCellData($scope.__selectRow, $scope.__selectCell);
                    } else {
                        data[mdata.name] = fileData.fileId;
                        fixDataCell(mdata, data);
                    }
                });
                //文件上传失败
                $scope.$on('/uploadError', function (event, data) {
                    // $scope.data[data.entityField] = data.fileId;
                    console.log('文件真的失败FileId:', $scope.data);
                    console.log('文件真的失败了:', data);
                });
                //文件被删除
                $scope.$on('/delFileItem', function (event, fileData) {
                    var mdata = $scope.headerRow[$scope.__selectCell];
                    var data = $scope.formData;
                    if ($scope.__selectRow != null) {
                        data = $scope.data.items[$scope.__selectRow];
                    }
                    if (data != null && data._id != null) {
                        var v = data[mdata.name];
                        if (v != null) {
                            if (mdata.inputType == 'fileMore') {
                                try {
                                    var p = parseInt(v);
                                    if (p > 0) {
                                        p--;
                                        $scope.__cellData[$scope.__selectRow + '_' + $scope.__selectCell] = p;
                                        $scope.saveCellData($scope.__selectRow, $scope.__selectCell);
                                    }
                                } catch (e) {
                                }
                            } else if (mdata.inputType == 'file') {
                                $scope.__cellData[$scope.__selectRow + '_' + $scope.__selectCell] = '';
                                $scope.saveCellData($scope.__selectRow, $scope.__selectCell);
                            }
                        }
                    }
                });


                //设置日期
                $scope.$on('/setDateValue', function (event, dateData) {
                    var mdata = $scope.headerRow[$scope.__selectCell];
                    var data = $scope.formData;
                    if ($scope.__selectRow != null) {
                        data = $scope.data.items[$scope.__selectRow];
                    }
                    if (mdata == null || data == null) {
                        return;
                    }
                    if (('date_' + $scope.entity + '_' + mdata.name) == dateData.id) {
                        if (data._id != null) {
                            $scope.__cellData[$scope.__selectRow + '_' + $scope.__selectCell] = dateData.value;
                            $scope.saveCellData($scope.__selectRow, $scope.__selectCell);
                        } else {
                            data[mdata.name] = dateData.value;
                            fixDataCell(mdata, data);
                        }
                        window.reqUIAction($scope, $scope.popWinId + '/close');
                    }
                });

                $scope.$on($scope.popWinId + '_tree' + '/init_end', function (event, obj) {
                    console.log('tree is ok')
                    var treeId = event.name.split('/')[0];
                    window.reqUIAction($scope, treeId + '/itemExpended');
                    // 将选择的初始值发送给树
                    $scope.$$selectEditData || ($scope.$$selectEditData = []);
                    var sMap = {}
                    if ($scope.$$selectEditData != null && $scope.$$selectEditData.length > 0) {
                        $.each($scope.$$selectEditData, function (k, v) {
                            sMap[v._id] = true;
                        });
                    }

                    window.reqUIAction($scope, treeId + '/itemClicked');
                    //window.reqUIAction($scope, $scope.popWinId + '_tree' + '/loadTree', sMap);

                    //监听树相关的事件
                    $scope.$on(treeId + '/itemClicked_after', function (event, obj) {
                        var pid = obj._id;
                        window.reqUIAction($scope, $scope.popWinId + '_entityGrid' + '/setDataAndQuery', {
                            filter: {
                                _pid: pid
                            },
                            formData: {
                                pid: pid
                            }
                        });
                    });
                });

                //html编辑器加载完毕
                $scope.$on($scope.htmlEditId + '/init_end', function (event, obj) {
                    console.log('html Edit is ok');
                    // 将选择的初始值发送给编辑器
                    $scope.$$selectEditData || ($scope.$$selectEditData = '');
                    //console.log('save before:', $scope.$$selectEditData);
                    window.reqUIAction($scope, $scope.htmlEditId + '/setContent', $scope.$$selectEditData);
                });


                /** 消息监听结束 */
                    // 选择多行
                $scope.selectAll = function () {
                    if ($scope.selectDataType != 'more') {
                        return;
                    }
                    $scope.__selectAll = !$scope.__selectAll;
                    $scope.selectIdMap || ($scope.selectIdMap = {});
                    $.each($scope.data.items, function (k, v) {
                        v.__selected = $scope.__selectAll;
                        if (v.__selected) {
                            $scope.selectIdMap[v._id] = true;
                        } else {
                            delete $scope.selectIdMap[v._id];
                        }
                    });
                }
                // 选择的数据的事件监听
                $scope.selectRow = function (item) {
                    if ($scope.selectDataType == 'none') {
                        return;
                    }
                    $scope.selectIdMap || ($scope.selectIdMap = {});
                    if ($scope.selectDataType == 'one') {// 只能选择一个，所以清除其已经选择的
                        $.each($scope.data.items, function (k, v) {
                            if (v._id != item._id) {
                                v.__selected = false;
                            }
                        });
                        $scope.selectIdMap = {};
                    }
                    item.__selected = !item.__selected;
                    if (item.__selected) {
                        $scope.selectIdMap[item._id] = true;
                    } else {
                        delete $scope.selectIdMap[item._id];
                    }
                }
                $scope.setDataAndQuery = function (data) {
                    $scope.setData(data);
                    $scope.genMData();
                    $scope.query();
                }

                // 外部设置一个选取集合
                $scope.setSelecteIdMap = function (map) {
                    $scope.selectIdMap = map;
                }
                // 获取已经选取的数据，包括因分页而没有显示的
                $scope.getAllChecked = function () {
                    // 需要注意分页的原因造成数据丢失
                    return $scope.selectIdMap;
                }
                // 编辑一个格子
                $scope.editCell = function (rowIndex, headerIndex, $event) {
                    if (!$scope.isEdit) {
                        malert('请打开上方: "编辑模式" 按钮');
                        return;
                    }
                    var data = $scope.data.items[rowIndex];
                    var mdata = $scope.headerRow[headerIndex];
                    if (mdata.type == 'boolean' || mdata.readOnly || mdata.setByServer) {
                        return;
                    }
                    $scope.__cellMdata[rowIndex + '_' + headerIndex] = true;
                    $scope.__cellData[rowIndex + '_' + headerIndex] = data[mdata.name];
                }

                // 弹出窗口编辑后的cell数据保存
                $scope.popWinSaveCell = function (winId, callback) {
                    var rowIndex = $scope.__selectRow;
                    var headerIndex = $scope.__selectCell;
                    var data = $scope.data.items[rowIndex];
                    var mdata = $scope.headerRow[headerIndex];
                    //data[mdata.name] = $scope.$$selectEditData;
                    $scope.__cellData[rowIndex + '_' + headerIndex] = $scope.$$selectEditData;
                    $scope.saveCellData(rowIndex, headerIndex, null, callback);
                    delete $scope.$$selectEditData;
                    window.reqUIAction($scope, (winId ? winId : $scope.popWinId) + '/close');

                }

                //$scope.gridKeyup = function ($event) {
                //    console.log($event.keyCode);
                //}

                // 保存一个格子编辑的内容
                $scope.cancelSelect = function (rowIndex, headerIndex, $event) {
                    if ($event.keyCode != null) {
                        if ($event.keyCode == 27) {// 取消编辑
                            $scope.__cellMdata[rowIndex + '_' + headerIndex] = false;
                            return;
                        }
                    }
                }
                $scope.saveCellData = function (rowIndex, headerIndex, $event, callback) {
                    if ($event != null && $event.keyCode != null) {
                        // console.log(rowIndex + '_' + headerIndex + '_keycode:' + $event.keyCode)
                        if ($event.keyCode != null) {
                            if ($event.keyCode == 13) {

                            } else if ($event.keyCode == 27) {// 取消编辑
                                $scope.__cellMdata[rowIndex + '_' + headerIndex] = false;
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                    var data = $scope.data.items[rowIndex];
                    var mdata = $scope.headerRow[headerIndex];

                    var value = $scope.__cellData[rowIndex + '_' + headerIndex];
                    // 检查数据
                    var newData = {};
                    $.extend(newData, data);

                    if (data[mdata.name] == value) {
                        //未修改不发送保存请求
                        $scope.__cellMdata[rowIndex + '_' + headerIndex] = false;
                        //data[mdata.name] = $scope.__cellData[rowIndex + '_' + headerIndex];
                        fixDataRow(rowIndex, data);
                        delete $scope.__cellData[rowIndex + '_' + headerIndex];
                        // $scope.query();
                        //if (callback) {
                        //    callback(re);
                        //}
                        return;
                    }

                    newData[mdata.name] = value;
                    var errMsg = $scope.checkInputData(mdata, newData);
                    $scope.data.__cellErrMsg[rowIndex + '_' + headerIndex] = errMsg;
                    if (errMsg.length > 0) {
                        malert(errMsg);
                        return;
                    }
                    $http.put($scope.actionPath + '/save', newData).success(function (re) {
                        malert('保存成功');
                        $scope.__cellMdata[rowIndex + '_' + headerIndex] = false;
                        data[mdata.name] = $scope.__cellData[rowIndex + '_' + headerIndex];
                        fixDataRow(rowIndex, data);
                        delete $scope.__cellData[rowIndex + '_' + headerIndex];
                        // $scope.query();
                        if (callback) {
                            callback(re);
                        }
                    });
                }

                // 重置默认的查询器
                $scope.resetFilter = function () {
                    $scope.baseFilter = {
                        pageSize: 20,
                        orderByAsc: '-1',
                        keywords: keywords ? keywords : 'name',
                        includeKeys: $scope.includeKeys,
                        excludeKeys: $scope.excludeKeys
                    };
                    $scope.filter = {};
                    $.extend(true, $scope.filter, $scope.baseFilter);
                }

                $scope.scrollContent = function (el) {
                    var obj = $(el);
                    // console.log(obj);
                    // 处理滚动事件
                    if (obj.data('headerRow') == null) {
                        obj.data('headerRow', $element.find('.headerRow'));
                        obj.data('checkboxCon', $element.find('.checkboxCon'));
                    }
                    var headerRow = obj.data('headerRow');
                    var checkboxCon = obj.data('checkboxCon');
                    console.log();
                    $(el).scroll(function () {
                        headerRow.css('left', $(this).scrollLeft() * -1);
                        checkboxCon.css('top', $(this).scrollTop() * -1);
                    });
                }
                $scope.addTopBtn = function (item) {
                    $scope.topBtns || ($scope.topBtns = []);
                    $scope.topBtns.push(item);
                }

                // 第一次加载数据
                if ($scope.actionPath != null) {
                    $scope.genMDataAndQuery();
                }

                window.exportUiApi($scope, ['addTopBtn', 'setDataAndQuery', 'genMDataAndQuery', 'genMData', 'resetFilter', 'query', 'addButtomAction']);
                $rootScope.$broadcast($scope.id + '/init_end');

            },

            compile: function (element, attrs, transclude) {
                return function ($scope, element, attrs, ctrl, transclude) {
                };
            }
        }
    });

})(angular);