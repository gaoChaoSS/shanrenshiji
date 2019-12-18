/**
 * 对list进行封装，使其有更多的基础功能
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    window.app.directive('listView', function () {
        return {

            restrict: 'AE',
            transclude: true,
            replace: true,
            templateUrl: '/front_temp/list.html?_=' + window.angular_temp_version,
            scope: {
                id: '@',
                model: '@',
                entity: '@',
                items: '=',
                selectType: '@',// 选择方式,one:单选，more：多选，none：无法选择，默认为none
                baseTemplate: '@',// 基本的模板
                baseClass: '@',// 基本的class样式
                baseStyle: '@'// 基本的style样式
            },
            controller: function ($rootScope, $scope, $http, $element) {
                $scope.textField || ($scope.textField = 'name');
                if ($scope.id == null) {
                    $scope.id = 'list_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }

                var _genClass = function (v) {
                    $scope.baseClass || ($scope.baseClass = '');
                    var c = v['class'] ? v['class'] : $scope.baseClass;
                    c += v.$$selected ? " selected" : "";
                    return c;
                }
                var _fixData = function (list) {
                    $.each(list, function (k, v) {
                        $scope.$$hasAction = v.click || $scope.baseClick;
                        v.$$class = _genClass(v);
                        v.$$style = v['style'] ? v['style'] : $scope.baseStyle;
                        v.$$template = v['template'] ? v['template'] : $scope.baseTemplate;
                        if (v.$$template == null) {
                            v.$$template = '/front_temp/list_item_base.html?_=' + window.angular_temp_version
                        }
                        v.$$iconClass = v.iconClass ? v.iconClass : "icon-menu";
                    });
                    return list;
                }

                $scope.content = {};
                console.log("list data: ", $scope.items);
                if ($scope.items != null) {
                    $scope.content.items = _fixData($scope.items);
                }
                // 分两种情况:
                // 1：外部直接设置list，
                // 2：通过api从服务器获取数据，从服务器获取数据需要实现：滚动自动加载，加载中状态，等功能

                console.log('baseClick:', $scope.baseClick);


                $scope.filter = {
                    pageSize: 20
                };
                $scope.clickItem = function (event, index) {
                    var item = $scope.content.items[index];
                    console.log('click item:', item, index);

                    if ($scope.selectType == 'one') {
                        if ($scope.$$selecteItem != null) {
                            $scope.$$selecteItem.$$selected = false;
                            $scope.$$selecteItem.$$class = _genClass(item);
                        } else {//可能存在初始化选中的情况
                            $.each($scope.content.items, function (k, v) {
                                if (v.$$selected) {
                                    v.$$selected = false;
                                    v.$$class = _genClass(v);
                                    return false;
                                }
                            });
                        }
                        item.$$selected = true;
                        item.$$class = _genClass(item);
                        $scope.$$selecteItem = item;
                    } else if ($scope.selectType == 'more') {
                        item.$$selected = !item.$$selected;
                        item.$$class = _genClass(item);
                    }
                    $scope.$emit($scope.id + '/clickItemAction', item);
                }
                $scope.initAndQuery = function (obj) {
                    $.extend(true, $scope, obj);
                    query();
                }
                $scope.query = function (isAdd) {
                    if ($scope.model != null && $scope.entity != null) {
                        $scope.actionPath = window.basePath + '/' + $scope.model + '/' + $scope.entity;
                        var url = $scope.actionPath + '/query';
                        $http.get(url + mapToQueryString($scope.filter)).success(function (re) {
                            if (isAdd) {
                                $scope.content.items.addAll(_fixData(re.content.items));// 封装数据
                                $scope.content.totalNum = re.content.totalNum;
                            } else {
                                $scope.content = re.content;
                                $scope.content.items = _fixData($scope.content.items);// 封装数据
                            }
                            $scope.filter.pageSize = re.content.pageSize;
                            $scope.filter.pageNo = re.content.pageNo;
                        });
                    } else {
                        // malert('没有定义查询的 actionPath');
                    }
                }
                $scope.queryMore = function () {
                    $scope.filter.pageNo || ($scope.filter.pageNo = 1);
                    $scope.filter.pageNo++;
                    $scope.query(true);
                }



                exportUiApi($scope, ['query', 'initAndQuery', 'queryMore', 'clickItem']);
                $rootScope.$broadcast($scope.id + '/init_end');


            },
            compile: function (element, attrs, transclude) {
                return function ($scope, element, attrs, ctrl, transclude) {
                    $scope.query();
                };
            }
        }
    });

})(angular);