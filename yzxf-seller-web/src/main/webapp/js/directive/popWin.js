/**
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";

    $(window).resize(function () {
        // console.log($('.popWin[win-pop="true"]').is(':visible'))
        if ($('.popWin[is-pop="true"]').is(':visible')) {
            $('.popWin[is-pop="true"]').each(function (k, v) {
                if (!$(v).hasClass('max')) {
                    $(v).showCenter();
                }
            });
        }
    });
    window.app.directive('popWin', function () {
        return {
            restrict: 'AE',
            transclude: true,
            replace: true,
            scope: {
                id: '@',
                index: '@',
                isPop: '@',
                hideTop: '@',
                hideDown: '@',
                winWidth: '@',
                winHeight: '@',
                maxWin: '&',
                close: '&'
            },

            controller: function ($scope, $element, $compile, $popWindow) {
                console.log($scope.id, $element.attr('id'))
                if ($scope.id == null) {
                    $scope.id = 'win_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }
                $scope.hideTopCon = $scope.hideTop == 'true';
                $scope.hideDownCon = $scope.hideDown == 'true';
                // $scope.id = $element.attr('id');
                //$scope.hideTop = $scope.hideTop != 'false';
                //$scope.hideDown = $scope.hideDown != 'false';
                //console.log('hideTop:', $scope.hideTop);
                //console.log('hideDown:', $scope.hideDown);

                $scope.close = function () {
                    // alert('call - colose')
                    if ($scope.isPop == 'true') {
                        var mIndex = parseInt($element.css('zIndex')) - 1;
                        //console.log(mIndex);
                        angular.element('#mark_' + mIndex).remove();
                    }
                    $popWindow.closeWinInx($scope.index);
                }
                $scope.setContent = function (conentStr) {
                    var el = $element.find('.windowContent');
                    console.log('conentStr:' + conentStr);
                    el.empty().append(conentStr);
                    $compile(el.contents())($scope);
                }
                $scope.setTemplate = function (tempUrl) {
                    $scope.contentTempUrl = tempUrl;
                }
                // 窗口的按钮
                $scope.actions || ($scope.actions = [{
                    name: 'cancel',
                    title: '取消',
                    click: $scope.close
                }]);
                $scope.addAction = function (item) {
                    console.log('--' + $scope.actions)
                    $scope.actions.splice(0, 0, item);
                }
                $scope.clickAction = function (item) {
                    if (item.click) {
                        item.click();
                    }
                }
                $scope.maxWin = function () {
                    $scope.$$isMax = !$scope.$$isMax;
                }

                if ($scope.winWidth != null && $scope.winHeight != null) {
                    $element.css({
                        width: $scope.winWidth,
                        height: $scope.winHeight
                    });
                    $element.showCenter($scope.winWidth, $scope.winHeight).show();
                }
                $scope.setData = function (data) {
                    $scope[data.key] = data.value;
                }
                exportUiApi($scope, ['close', 'setContent', 'addAction', 'setData', 'setTemplate']);
            },
            //template: '<div class="popWin" ng-class="$$isMax?\'max\':\'\'"></div>',
            templateUrl: '/temp/popWin.html?_v=' + window.angular_temp_version,
            compile: function (element, attrs, transclude, $scope) {
                // var clone = element.children().clone();

                // element.find('.center').append(clone);

                return function ($scope, $element, attrs, ctrl, transclude) {
                    $scope.title = attrs.winTitle;
                    window.zIndex || (window.zIndex = 0);
                    $scope.zIndex = window.zIndex;
                    //如果有设置模板,就加载js
                    if (attrs.winTemp != null && attrs.winTemp != '') {
                        var dependencies = ['/view_js/' + attrs.winTemp + '.js?v=' + window.angular_temp_version];
                        $script(dependencies, function () {
                            $scope.$parent.$apply(function () {
                                $scope.contentTempUrl = '/view/' + attrs.winTemp + '.jsp?v=' + window.angular_temp_version;
                            });
                        });
                    }
                    // scope.content = element.html();
                    $scope.$root.$broadcast($scope.id + '/init_end');
                };
            }
        }
    });

})(angular);


