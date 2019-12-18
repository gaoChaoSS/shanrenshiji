/**
 * 使用例子：(须注意先后顺序) <layout> <top size="30">Joey</top> <top size="50">Joey</top> <bottom size="10"></bottom> <bottom size="30"></bottom> <left size="150"></left> <left size="150"></left>
 * 
 * <right size="150"></right> <right size="150"></right> <center></center> </layout>
 * 
 * @param angular
 * @param undefined
 */
(function(angular, undefined) {
    "use strict";
    window.app.directive('layout', function() {
        return {
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : false,
            controller : function($scope, $element) {
                if ($element.attr('id') == null) {
                    $element.attr('id', 'layout_' + getRandom(100000000));
                }
                $scope.id = $element.attr('id');
                this.id = $scope.id;
            },
            template : '<div class="layout" ng-transclude></div>',
            // scope : {},
            link : function($scope, element, attrs, tabsCtrl) {
                $scope.indexMap = {};
                $scope.children = {};
                $scope.typeMap = {};
                // 构建布局对象
                $.each(element.children(), function(k, v) {
                    var type = $(v).attr('data-type');
                    var size = parseInt($(v).attr('size'));
                    var id = $(v).attr('id');
                    if (id == null || id == '') {
                        id = 'layout_' + type + '_' + getRandom(100000000);
                        $(v).attr('id', id);
                    }
                    $(v).addClass(type);
                    $scope.indexMap[type] || ($scope.indexMap[type] = 0);
                    $scope.children[id] = {
                        id : id,
                        type : type,
                        size : size,
                        index : ++$scope.indexMap[type]
                    };

                    $scope.typeMap[type] || ($scope.typeMap[type] = [])
                    $scope.typeMap[type].push($scope.children[id]);
                });
                // 执行布局运算
                $scope.reSize = function(data) {
                    console.log(data);
                    console.log(data.id, data.size, $scope.children[data.id]);
                    $scope.children[data.id].size = data.size;
                    $scope.setAllPos();
                }
                $scope.setAllPos = function() {
                    var types = 'top,bottom,left,right,center';
                    var poz = {
                        top : 0,
                        left : 0,
                        right : 0,
                        bottom : 0
                    };
                    $.each(types.split(','), function(k, v) {
                        if ($scope.typeMap[v]) {
                            $.each($scope.typeMap[v], function(kk, obj) {
                                var data = {};
                                if (v == 'top' || v == 'bottom') {
                                    data.height = obj.size;
                                    data.left = 0;
                                    data.right = 0;
                                    data[v] = poz[v];
                                    poz[v] += obj.size;
                                } else if (v == 'left' || v == 'right') {
                                    data.width = obj.size;
                                    data.top = poz.top;
                                    data.bottom = poz.bottom;
                                    data[v] = poz[v];
                                    poz[v] += obj.size;
                                } else if (v == 'center') {
                                    $.each([ 'top', 'bottom', 'left', 'right' ], function(kkk, vvv) {
                                        poz[vvv] || (poz[vvv] = 0);
                                        data[vvv] = poz[vvv];
                                    });
                                }
                                $('#' + obj.id).css(data);
                            });
                        }
                    });
                }
                $scope.setAllPos();
                exportUiApi($scope, [ 'reSize' ]);
            }
        }
    })
})(angular);