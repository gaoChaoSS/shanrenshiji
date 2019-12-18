/**
 * 使用例子：(须注意先后顺序)
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    window.app.directive('myform', function () {
        return {
            restrict: 'AE',
            transclude: true,
            replace: true,
            scope: {
                id: '@',
                entity: '@',
                model: '@',
                rowValue: '@',
                isEditStr: '@',
                hideTop: '@',
                hideDown: '@',
                excludeKeys: '@',
                includeKeys: '@',
                contentTemp: '@'
            },
            templateUrl: '/temp/myform.html?_v=' + window.angular_temp_version,
            controller: function ($rootScope, $scope, $element, $http, $q, $compile, $popWindow, $templateCache) {
                if (window.isEmpty($scope.id)) {
                    $scope.id = 'form_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }
                $scope.hideTopCon = $scope.hideTop == 'true';
                $scope.hideDownCon = $scope.hideDown == 'true';
                $scope.isEdit = $scope.isEditStr == 'true';

                var popWinId = $scope.id + '_popWin';

                if ($scope.model != null && $scope.entity != null) {
                    $scope.actionPath = window.basePath + '/' + $scope.model + '/' + $scope.entity;
                }

                $scope.setNewData = function () {
                    $scope.data = {$$add: true, _id: genUUID()};
                    //向下属的控件,如file,发送命令
                    if ($scope.headerRow != null) {
                        $.each($scope.headerRow, function (k, v) {
                            if (v.inputType == 'file' || v.inputType == 'fileMore') {
                                $scope.$broadcast('file_' + $scope.entity + '_' + v.name, {action: 'setNewData'});
                            }
                        });
                    }
                }

                $scope.data || ($scope.data = {});
                if (!window.isEmpty($scope.rowValue)) {
                    $scope.data._id = $scope.rowValue;
                } else {
                    $scope.setNewData();
                }


                /**
                 * 获取元数据
                 */
                $scope.genMDataAndQuery = function (obj, reLoad, callback) {
                    if (obj != null) {
                        $scope.setOptions(obj);
                    }
                    $rootScope.mdata || ($rootScope.mdata = {});
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
                            });

                            $rootScope.mdata[$scope.actionPath] = re.content.items;
                            if (callback) {
                                callback();
                            }
                        }).error(function () {
                            if (callback) {
                                callback();
                            }
                        });
                    } else {
                        if (callback) {
                            callback();
                        }
                    }
                }
                $scope.fixMData = function () {
                    $scope.headerRow = [];
                    var fieldMap = {};
                    var excludeKeys = $scope.excludeKeys;
                    var includeKeys = $scope.includeKeys;

                    if (excludeKeys != null) {
                        excludeKeys = ',' + excludeKeys + ',';
                    }
                    var includeStr = '';
                    if (includeKeys != null && includeKeys != '') {
                        includeStr = ',' + includeKeys + ',';
                    }

                    angular.forEach($rootScope.mdata[$scope.actionPath], function (k) {
                        // TODO 对要显示的字段进行处理
                        var ok = false;

                        if (includeStr != null && includeStr != '') {
                            if (includeStr.indexOf(',' + k.name + ',') > -1) {
                                ok = true;
                            }
                        } else {
                            if (excludeKeys != null && excludeKeys.indexOf(',' + k.name + ',') > -1) {
                                ok = false;
                            } else {
                                ok = true;
                            }
                        }


                        if (ok) {
                            //createTime和_id默认为只读
                            if (k.name == '_id' || k.name == 'createTime' || k.setByServer) {
                                k.readOnly = true;
                            }
                            k.type || (k.type = 'string');
                            k.inputType || (k.inputType = 'input');
                            k.__width = 100;
                            if (k.name == 'name' || k.name == 'desc' || k.name == 'chineseName') {
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

                            k.maxLength = k.maxLength ? k.maxLength : 64;
                            k.minLength = k.minLength ? k.minLength : 0;
                            if (k.isNotNull && k.minLength == 0) {
                                k.minLength = 1;
                            }
                            k.inputType = k.type == 'boolean' ? 'boolean' : k.inputType;
                            k.$$dateInput = k.inputType == 'date' || k.inputType == 'time' || k.inputType == 'dateTime';
                            k.$$numberInput = k.type == 'int' || k.type == 'long' || k.type == 'double';
                            k.$$isFileObj = k.inputType.startsWith('file');
                            k.$$isEntityObj = k.inputType.startsWith('linkEntity');
                            k.$$title = window.isEmpty(k.title) ? k.name : k.title;

                            $scope.headerRow.push(k);
                            fieldMap[k.name] = k;
                            $scope.contentWidth += k.__width;
                        }
                    });
                    //如果设置了包含,只显示包含的数据
                    if (includeKeys != null && includeStr != '') {
                        $scope.headerRow.length = 0;
                        $scope.contentWidth = 0;
                        $.each(includeKeys.split(','), function (k, v) {
                            var h = fieldMap[v];
                            $scope.headerRow.push(h);
                            $scope.contentWidth += h.__width ? h.__width : 100;
                        })
                    }
                }

                var fixDataField = function (mData, value) {
                    if (mData.$$dateInput) {
                        value['$$' + mData.name] = window.showDateStr(mData.inputType, value[mData.name]);
                    } else if (mData.inputType == 'boolean') {
                        value['$$' + mData.name] = ' ';
                    } else if (mData.inputType == 'password') {
                        value['$$' + mData.name] = '* * * * * *';
                    } else if (mData.inputType == 'htmlEdit') {
                        value['$$' + mData.name] = '查看...';
                    } else if (mData.inputType == 'linkEntity') {
                        var id = value[mData.name];
                        if (!window.isEmpty(id) && !window.isEmpty(mData._linkTable)) {
                            var model = $scope.model;
                            var entity = null;
                            if (mData._linkTable.startsWith('/')) {
                                var ss = mData._linkTable.split('/');
                                model = ss[1];
                                entity = ss[2];
                            } else {
                                entity = mData._linkTable;
                            }
                            var url = window.basePath + '/' + model + '/' + entity + '/show?_id=' + id;
                            $http.get(url).success(function (re) {
                                value['$$' + mData.name] = re.content;
                            });
                        }
                    } else if (mData.inputType == 'linkEntityMore') {
                        //var id = value[v.name];
                        if (!window.isEmpty(mData._linkTable)) {
                            var url = $scope.actionPath + '/findLinkTableData?entityField=' + mData.name + '&entityId=' + value._id;
                            $http.get(url).success(function (re) {
                                $.each(re.content.items, function (k, v) {
                                    v.$$name = window.isEmpty(v.title) ? v.name : v.title;
                                })
                                value['$$' + mData.name] = re.content;
                            });
                        }
                    }
                }
                var fixData = function (value) {
                    value || (value = {});
                    $.each($scope.headerRow, function (k, v) {
                        fixDataField(v, value);
                    });
                    return value;
                }

                $scope.delLinkItem = function (mdata, index) {
                    var items = $scope.data['$$' + mdata.name].items;
                    var url = $scope.actionPath + '/delLinkTableDataByd?_id=' + items[index].$$linkTableId;
                    $http.post(url).success(function (re) {
                        items.splice(index, 1);
                    });
                }

                $scope.queryData = function () {
                    if (!$scope.data.$$add) {
                        $http.get($scope.actionPath + "/show?_id=" + $scope.data._id).success(function (re) {
                            if ($scope.headerRow == null) {
                                $scope.fixMData();
                            }
                            $scope.data = fixData(re.content);
                            $rootScope.$broadcast($scope.id + '/querySuccess');
                            //$.each($scope.headerRow, function (k, v) {
                            //    if (v.inputType == 'file') {
                            //        window.reqUIAction($scope, 'file_' + $scope.entity + '_' + v.name + '/setData', {fileId: $scope.data[v.name]});
                            //    }
                            //})
                        });
                    } else {
                        if ($scope.headerRow == null) {
                            $scope.fixMData();
                        }
                    }
                }

                $scope.checkError = function (item) {
                    var d = $scope.data[item.name];
                    if (item.minLength > 0 && window.isEmpty(d)) {
                        return true;
                    }
                    return false;
                }
                $scope.showClass = function (item) {
                    if (item.name == 'name' || item.name == 'title') {
                        return 'bold';
                    }
                    if (item.$$showDesc && $scope.checkError(item)) {
                        return ' err';
                    }
                    if (item.inputType == 'boolean') {
                        var d = $scope.data[item.name];
                        d = d == null ? false : d;
                        return d ? 'iconfont green icon-jianchacheck35' : 'iconfont high icon-addcollapse';
                    }
                    if (item.$$numberInput) {
                        return 'num';
                    }
                }
                $scope.showData = function (item) {
                    var d = $scope.data[item.name];
                    if (!window.isEmpty(d)) {
                        if (item.inputType == 'dateTime') {
                            return new Date(parseInt(d)).showDateTime();
                        }
                    } else {
                        return d;
                    }
                }

                $scope.saveData = function () {
                    $.each($scope.headerRow, function (k, v) {
                        v.$$showDesc = true;
                    })
                    if ($scope[$scope.id].$invalid) {
                        malert('数据不完整,请检查!');
                        return;
                    }
                    $http.put($scope.actionPath + "/save", $scope.data).success(function (re) {
                        malert('操作成功!');
                        $scope.data = fixData(re.content);
                        $rootScope.$broadcast($scope.id + '/saveSuccess');
                    })
                }
                $scope.showDate = function (item) {
                    $scope.$$showDate = item.name;
                    $scope.$$selectMData = item;
                }
                $scope.showSelectEntityWin = function (item) {
                    $scope.selectMData = item;
                    $popWindow.add(popWinId, '选择 [' + item.$$title + ']', 700, 400, null, false, false);
                }
                $scope.showHtmlEdit = function (item) {
                    $scope.selectMData = item;
                    $popWindow.add(popWinId, '编辑 [' + item.$$title + ']', 700, 400, null, false, false);
                }

                $scope.focusInput = function (item) {
                    $scope.focusName = item.name;
                    item.$$showDesc = true;
                }
                $scope.blurInput = function (item) {
                    $scope.focusName = null;
                }

                $scope.resetPassword = function (name) {
                    if ($scope.$$add) {
                        return;
                    }
                    $http.post($scope.actionPath + '/resetPassword', {
                        _id: $scope.data._id,
                        name: name
                    }).success(function (re) {
                        $scope.data['$$pass_' + name] = re.content.password;
                    });
                }

                //$scope.data = {name: 'test', title: '标题'};

                //弹出窗口加载成功
                $scope.$on(popWinId + '/init_end', function (event, obj) {
                    var mData = $scope.selectMData;
                    if (mData.inputType == 'htmlEdit') {
                        var htmlEditId = popWinId + '_htmlEdit';
                        $scope.htmlEditId = htmlEditId;
                        window.reqUIAction($scope, popWinId + '/setData', {
                            htmlEditId: $scope.htmlEditId,
                            entity: $scope.entity,
                            model: $scope.model,
                            entityField: mData.name,
                            entityId: $scope.data._id,
                            isEdit: $scope.isEdit
                        });
                        window.reqUIAction($scope, popWinId + '/setTemplate', '/temp/inc_grid_htmlEditor.html');
                        window.reqUIAction($scope, popWinId + '/addAction', {
                            name: 'ok',
                            title: '确定',
                            isHigh: true,
                            click: function () {
                                window.reqUIAction($scope, htmlEditId + '/saveData', null, function (obj) {
                                    console.log('--html content:', obj);
                                    window.reqUIAction($scope, popWinId + '/close');
                                });
                            }
                        });
                    } else if (mData.inputType.startsWith('linkEntity')) {
                        var model = $scope.model;
                        var entity = null;
                        if (mData._linkTable.startsWith('/')) {
                            var sp = mData._linkTable.split('/');
                            model = sp[1];
                            entity = sp[2];
                        } else {
                            entity = mData._linkTable;
                        }
                        var winData = {
                            model: model,
                            entity: entity,
                            inputType: mData.inputType,
                            isTree: mData.selectUi == 'tree'
                        };
                        if (mData.inputType == 'linkEntityMore') {
                            if ($scope.data != null && !window.isEmpty($scope.data._id != null)) {
                                winData.selectValueUrl = '/' + $scope.model + '/' + $scope.entity + '/findLinkTableData?entityField=' + mData.name + '&entityId=' + $scope.data._id;
                            }
                        } else {
                            winData.selectValue = $scope.data[mData.name];
                        }
                        window.reqUIAction($scope, popWinId + '/setData', winData);

                        window.reqUIAction($scope, popWinId + '/setTemplate', '/temp/inc_form_selectEntity.html');
                        window.reqUIAction($scope, popWinId + '/addAction', {
                            name: 'ok',
                            title: '确定',
                            isHigh: true,
                            click: function () {
                                window.reqUIAction($scope, $scope.id + '_popWin_entityGrid' + '/getData', 'selectIdMap', function (obj) {
                                    if (obj != null) {
                                        if (mData.inputType == 'linkEntityMore') {
                                            var ids = [];
                                            $.each(obj, function (k, v) {
                                                if (obj[k]) {
                                                    ids.push(k);
                                                }
                                            });
                                            if (ids.length > 0) {
                                                var data = {
                                                    entityField: mData.name,
                                                    isAdd: false,
                                                    entityId: $scope.data._id,
                                                    linkEntityIds: ids.join(',')
                                                };
                                                $http.post(window.basePath + '/' + $scope.model + '/' + $scope.entity + '/saveLinkTableData', data).success(function (k, v) {
                                                    fixDataField(mData, $scope.data);
                                                });
                                            }
                                        } else if (mData.inputType == 'linkEntity') {
                                            $.each(obj, function (k, v) {
                                                if (obj[k]) {
                                                    $scope.data[mData.name] = k;
                                                    $scope.saveData();
                                                }
                                            });
                                        }
                                    }
                                    window.reqUIAction($scope, popWinId + '/close');
                                });
                            }
                        });
                    }
                });

                //文件上传成功
                $scope.$on('/uploadSuccess', function (event, fileData) {
                    $scope.data[fileData.entityField] = fileData.fileId;
                    $scope.saveData();
                });
                $scope.$on('/uploadError', function (event, data) {
                    //$scope.data[data.entityField] = data.fileId;
                    console.log('文件真的失败FileId:', $scope.data);
                    console.log('文件真的失败了:', data);
                });
                $scope.$on('/delFileItem', function (event, fileData) {
                    $scope.data[fileData.entityField] = null;
                    $scope.saveData();
                })

                //隐藏日历
                $scope.$on('/allMenuHide', function (event, data) {
                    $scope.$$showDate = null;
                    $scope.$$selectMData = null;
                });
                //设置日期
                $scope.$on('/setDateValue', function (event, dateData) {
                    // malert(dateData.value);
                    if (('date_' + $scope.entity + '_' + dateData.name) == dateData.id) {
                        $scope.data[dateData.name] = dateData.value;
                        var v = $scope.$$selectMData;
                        $scope.data['$$' + v.name] = window.showDateStr(v.inputType, $scope.data[v.name]);

                        $scope.$$showDate = null;
                        $scope.$$selectMData = null;
                    }
                });

                $scope.genMDataAndQuery(null, false, $scope.queryData);
                window.exportUiApi($scope, ['setOptions']);
                $rootScope.$broadcast($scope.id + '/init_end');
            },

            compile: function (element, attrs, transclude) {
                return function ($scope, element, attrs, ctrl, transclude) {
                };
            }
        }
    });

})(angular);