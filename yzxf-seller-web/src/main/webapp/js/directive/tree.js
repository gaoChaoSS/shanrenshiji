/**
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    window.app.directive('treeView', function () {
        return {

            restrict: 'AE',
            transclude: true,
            replace: true,
            templateUrl: '/temp/tree.html',
            scope: {
                id: '@',
                selectType: '@',
                textField: '@',
                itemIcon: '&',
                itemTemplateUrl: '@',
                model: '@',
                entity: '@',
                loadAll: '@'// 是否是一次加载所有，对于少量数据可以
            },
            controller: function ($rootScope, $scope, $http, $element) {
                $scope.textField || ($scope.textField = '$$name');
                if ($scope.id == null) {
                    $scope.id = 'tree_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }
                // alert($scope.treeData);
                $scope.treeData || ($scope.treeData = [{
                    _id: '-1',
                    pid: '',
                    name: '所有数据',
                    level: -1,
                    title: '所有数据',
                    $$name: '所有数据'
                }]);

                $scope.filter = {
                    pageSize: 500
                };

                $scope.actionPath = window.basePath + '/' + $scope.model + '/' + $scope.entity;
                var _setChildren = function (item, map) {
                    item.children || (item.children = []);
                    $.each(map, function (k, v) {
                        if (v.pid == item._id) {
                            _setChildren(v, map);
                            item.children.push(v);
                            // delete map[v._id];
                        }
                    });
                }

                $scope.selectIdMap = {};
                // 根据id选择1组节点
                $scope.loadTree = function (selected) {
                    if ($scope.loadAll == 'true') {
                        var url = $scope.actionPath + '/query';
                        if ($scope.queryUrl != null) {
                            url = $scope.queryUrl;
                        }
                        $http.get(url + mapToQueryString($scope.filter)).success(function (re) {
                            var list = re.content.items;
                            var map = {}, selectedMap = {};
                            $.each(selected, function (k, v) {
                                selectedMap[v._id] = true;
                            });
                            $.each(list, function (k, v) {
                                v.$$isChecked = selectedMap[v._id];
                                if (window.isEmpty(v.title)) {
                                    v.$$name = v.name;
                                } else {
                                    v.$$name = v.title + '(' + v.name + ')';
                                }
                                map[v._id] = v;
                            });
                            _setChildren($scope.treeData[0], map);
                        });
                    } else {

                        // 通过服务器获取一个节点的所有父亲,从性能的角度考虑，无论选择了多少id只默认选中第一个id的
                        var index = 0;
                        var firstNodeId = null;
                        $.each(selected, function (k, v) {
                            if (index++ == 0) {
                                firstNodeId = k;
                                return false;
                            }
                        });
                        $scope.selectIdMap = selected;
                        if (firstNodeId != null) {
                            $scope.extendItemById(firstNodeId);
                        }
                    }
                }
                /**
                 * 传入一个ID然后一直向上展开到改节点所在的父节点
                 */
                $scope.extendItemById = function (itemId) {
                    var url = $scope.actionPath + '/queryTreeParents';
                    if ($scope.queryUrl != null) {
                        url = $scope.queryUrl;
                    }
                    var data = {
                        _id: itemId
                    };
                    $http.get(url + mapToQueryString(data)).success(function (re) {
                        console.log('queryTreeParents:', re.content.items);
                        var _selectItem = null;
                        var actions = [];// 队列的方式请求执行
                        $.each(re.content.items, function (k, v) {
                            actions.push(function (next) {
                                console.log('----data', k, ',', _selectItem);
                                $scope.itemExpended(_selectItem, null, true, function (item) {
                                    $.each(item.children, function (kk, vv) {
                                        console.log('xx: ', v, vv._id);
                                        if (v == vv._id) {
                                            _selectItem = vv;
                                        }
                                    });
                                    // console.log('----data', k, ',', _selectItem);
                                    next();
                                });
                            });
                        });
                        actions.push(function (next) {
                            $scope.itemExpended(_selectItem, null, true);
                        });

                        $($element).queue('init', actions);
                        $($element).dequeue('init');
                    });
                }

                var _loadChildren = function (item, callback) {
                    if (item.$$isExpend && $scope.model != null && $scope.entity != null && $scope.loadAll != 'true') { // 注意指定了才异步加载
                        var url = $scope.actionPath + '/query';
                        if ($scope.queryUrl != null) {
                            url = $scope.queryUrl;
                        }
                        $scope.filter._pid = item._id;
                        $http.get(url + mapToQueryString($scope.filter)).success(function (re) {
                            item.children = re.content.items;
                            $.each(item.children, function (k, v) {
                                v.$$isChecked = $scope.selectIdMap[v._id];
                                if (v.$$isChecked) {
                                    _selectItemMap[v._id] = v;
                                }
                                if (window.isEmpty(v.title)) {
                                    v.$$name = v.name;
                                } else {
                                    v.$$name = v.title + '(' + v.name + ')';
                                }
                            });
                            if (callback != null) {
                                callback(item);
                            }
                        });
                    }
                }
                $scope.itemOpen = function (item) {
                    $scope.itemExpended(item, null, true);
                }
                // 节点被展开或合拢,如果是展开就刷新数据
                $scope.itemExpended = function (item, $event, isExpend, callback) {
                    if (item == null) {// 则展开根节点
                        item = $scope.treeData[0];
                    }
                    if (isExpend != null) {
                        item.$$isExpend = isExpend;
                    } else {
                        item.$$isExpend = !item.$$isExpend;
                    }
                    if (item.$$isExpend) {
                        _loadChildren(item, callback);
                    }
                    if ($event) {
                        $event.stopPropagation();
                    }
                };
                // 获取节点的icon
                $scope.getItemIcon = function (item) {
                    var re = $scope['itemIcon']({
                        $item: item
                    });
                    if (re == null) {
                        return item.$$isExpend ? 'icon-folder-open' : 'icon-folder';
                    }
                    return re;
                };


                $scope.getTreeItemIcon = function (item) {
                    if (item.pid == '') {
                        return 'icon-home2';
                    } else {
                        if (item.level == 0) {
                            return 'icon-stack'
                        } else if (item.level == 1) {
                            return 'icon-drawer'
                        } else if (item.level == 2) {
                            return 'icon-table2'
                        } else if (item.level == 3) {
                            return 'icon-menu'
                        } else if (item.level == 4) {
                            return 'icon-list2'
                        }
                    }
                    return null;
                }

                $scope.isLeaf = function (item) {
                    var noChild = !item.children || !item.children.length;
                    if (!noChild) {
                        $.each(item.children, function (k, v) {
                            v.parent = item;
                        });
                    }
                    return noChild;
                };
                $scope.itemClicked = function (item, $event) {
                    if (item == null) {// 则展开根节点
                        item = $scope.treeData[0];
                    }
                    $scope.$$selectedId = item._id;
                    if ($scope.selectType != 'none') {
                        $scope.itemCheckedChanged(item);
                    }

                    // 告诉父被点击,可以方便的被父亲ctrl截获并处理
                    $rootScope.$broadcast($scope.id + '/itemClicked_after', item);
                }
                var _updateChildCheck = function (item) {
                    var child = item.children;
                    if (child != null && child.length > 0) {
                        $.each(child, function (k, v) {
                            v.$$isChecked = item.$$isChecked;
                            if (v.$$isChecked) {
                                $scope.selectIdMap[v._id] = true;
                            } else {
                                delete $scope.selectIdMap[v._id];
                            }
                            _updateChildCheck(v);
                        });
                    }
                }
                var _selectItemMap = {};
                $scope.itemCheckedChanged = function (item, $event) {
                    item.$$isChecked = !item.$$isChecked;
                    if ($scope.selectType == 'tree') {
                        if (item.$$isChecked) {
                            var p = item.parent;
                            while (p != null && !p.$$isChecked) {
                                p.$$isChecked = true;
                                $scope.selectIdMap[p._id] = true;

                                p = p.parent;
                            }
                            $scope.selectIdMap[item._id] = true;
                        } else {
                            delete $scope.selectIdMap[item._id];
                        }

                        _updateChildCheck(item);
                        //alert(Object.keys($scope.selectIdMap).length);
                    } else if ($scope.selectType == 'more') {
                        if (item.$$isChecked) {
                            _selectItemMap[item._id] = item;
                            $scope.selectIdMap[item._id] = item.$$isChecked;
                        } else {
                            delete _selectItemMap[item._id];
                            delete $scope.selectIdMap[item._id];
                        }
                    } else if ($scope.selectType == 'one') {
                        if (item.$$isChecked) {
                            if ($scope.selectIdMap != null) {
                                $.each($scope.selectIdMap, function (k, v) {
                                    if (_selectItemMap[k] != null) {
                                        _selectItemMap[k].$$isChecked = false;
                                    }
                                    delete $scope.selectIdMap[k];
                                });
                            }
                            _selectItemMap[item._id] = item;
                            $scope.selectIdMap[item._id] = true;
                        } else {
                            delete _selectItemMap[item._id];
                            delete $scope.selectIdMap[item._id];
                        }
                    }
                }

                $scope.warpCallback = function (callback, item, $event) {
                    $scope.$$selectedId = item._id;
                    ($scope[callback] || angular.noop)({
                        $item: item,
                        $event: $event
                    });
                };
                var msgPath = '/' + $scope.model + '/' + $scope.entity + '/tree_selectNode';
                $scope.$on(msgPath, function (event, obj) {
                    // console.log('recive msg:' + msgPath + ',' + obj);
                    var item = null;
                    $.each($scope.treeData, function (k, v) {
                        if (obj._id == v._id) {
                            item = v;
                            return false;
                        }
                    });
                    // $scope.warpCallback('itemClicked', item);
                    // $scope.itemExpended(item);
                });
                exportUiApi($scope, ['itemOpen', 'loadTree', 'itemClicked', 'itemExpended', 'itemCheckedChanged', 'getAllChecked']);
                $rootScope.$broadcast($scope.id + '/init_end');
            },
            compile: function (element, attrs, transclude) {
                return function ($scope, element, attrs, ctrl, transclude) {

                };
            }
        }
    });

})(angular);