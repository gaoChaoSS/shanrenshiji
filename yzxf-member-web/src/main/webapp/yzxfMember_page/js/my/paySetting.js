(function (angular, undefined) {

    var model = 'my';
    var entity = 'paySetting';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                //$rootScope.windowTitle = '个人中心';
                window.setWindowTitle($rootScope, '支付设置');
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);