(function (angular, undefined) {

    var model = 'my';
    var entity = 'settings';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.logout = function () {
            deleteCookie('___MEMBER_TOKEN');
            deleteCookie('_member_loginName');
            deleteCookie('loginName');
            deleteCookie('_member_mobile');
            deleteCookie('_member_icon');
            deleteCookie('lastLoginType');
            deleteCookie('_member_id');
            $rootScope.myInfo = null;
            $rootScope.goPage('/home/index');
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '设置');
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
