/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'cashPetitionSuccess';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '现金交易权限申请');
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

