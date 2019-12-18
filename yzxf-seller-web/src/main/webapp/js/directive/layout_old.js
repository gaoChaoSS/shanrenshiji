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
            controller : [ "$scope", "$element", function($scope, $element) {
                if ($element.attr('id') == null) {
                    $element.attr('id', 'layout_' + getRandom(100000000));
                }
                $scope.layout = {
                    top : 0,
                    left : 0,
                    bottom : 0,
                    right : 0
                };
                var centerDiv = $element.find('.center');
                this.top = function(size) {
                    $scope.layout.top += parseInt(size);
                    return $scope.layout;
                };
                this.left = function(size) {
                    $scope.layout.left += parseInt(size);
                    return $scope.layout;
                };
                this.right = function(size) {
                    $scope.layout.right += parseInt(size);
                    return $scope.layout;

                };
                this.bottom = function(size) {
                    $scope.layout.bottom += parseInt(size);
                    return $scope.layout;

                };
                this.getLayout = function() {
                    return $scope.layout;
                }
            } ],
            template : '<div class="layout" ng-transclude></div>',
            // scope : {},
            link : function($scope, element, attrs, tabsCtrl) {

            }
        }
    }).directive('center', function() {
        return {
            require : '^layout',
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : {},
            template : '<div class="center" style="top:{{top}}px;left:{{left}}px;right:{{right}}px;bottom:{{bottom}}px;" ng-transclude></div>',
            link : function(scope, element, attrs, tabsCtrl) {
                // tabsCtrl.top(attrs.size)
                var p = tabsCtrl.getLayout();
                scope.left = p.left;
                scope.right = p.right;
                scope.top = p.top;
                scope.bottom = p.bottom;
            }
        }
    }).directive('top', function() {
        return {
            require : '^layout',
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : {},
            template : '<div class="top" style="top:{{top}}px;height:{{size}}px" ng-transclude></div>',
            link : function(scope, element, attrs, tabsCtrl) {
                // tabsCtrl.top(attrs.size)
                var p = tabsCtrl.getLayout();
                scope.top = p.top;
                scope.size = attrs.size;
                p = tabsCtrl.top(scope.size);
            }
        }
    }).directive('left', function() {
        return {
            require : '^layout',
            restrict : 'AE',
            transclude : true,
            replace : true,
            template : '<div class="left" style="top:{{top}}px;left:{{left}}px;width:{{size}}px;bottom:{{bottom}}px" ng-transclude></div>',
            scope : false,
            controller : function($scope) {
                this.setBottom = function(size) {
                    $scope.bottom = size;
                }
            },
            link : function(scope, element, attrs, tabsCtrl) {
                var p = tabsCtrl.getLayout();
                scope.top = p.top;
                scope.left = p.left;
                scope.size = attrs.size;
                scope.bottom = p.bottom;
                p = tabsCtrl.left(scope.size);
            }
        }
    }).directive('right', function() {
        return {
            require : '^layout',
            restrict : 'AE',
            transclude : true,
            replace : true,
            template : '<div class="right" style="top:{{top}}px;right:{{right}}px;width:{{size}}px;bottom:{{bottom}}px" ng-transclude></div>',
            scope : {},
            link : function(scope, element, attrs, tabsCtrl) {
                var p = tabsCtrl.getLayout();
                scope.top = p.top;
                scope.left = p.left;
                scope.right = p.right;
                scope.size = attrs.size;
                scope.bottom = p.bottom;
                p = tabsCtrl.right(scope.size);
            }
        }
    }).directive('bottom', function() {
        return {
            require : '^layout',
            restrict : 'AE',
            transclude : true,
            replace : true,
            scope : {},
            template : '<div class="bottom" style="height:{{size}}px;bottom:{{bottom}}px" ng-transclude></div>',
            // scope : {},
            link : function(scope, element, attrs, tabsCtrl) {
                var p = tabsCtrl.getLayout();
                scope.size = attrs.size;
                scope.bottom = p.bottom;
                p = tabsCtrl.bottom(scope.size);
            }
        }
    });
})(angular);