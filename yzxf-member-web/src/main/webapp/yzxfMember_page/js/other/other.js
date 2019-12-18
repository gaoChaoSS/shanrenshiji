(function (angular, undefined) {

    var model = 'other';
    var entity = 'other';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '更多');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = true;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);