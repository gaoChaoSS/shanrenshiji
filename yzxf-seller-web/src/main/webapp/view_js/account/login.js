(function (angular, undefined) {
    var model = 'account';
    var entity = 'login';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.showLogin = true;
        $scope.login || ($scope.login = {});
        $scope.login.loginName = getCookie('loginName');
    });
})(angular);
