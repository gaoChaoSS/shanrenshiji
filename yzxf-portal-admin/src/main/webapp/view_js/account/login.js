(function(angular, undefined) {
    var model = 'account';
    var entity = 'login';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.showLogin = true;
        $rootScope.doLogin = function() {
            if ($scope.login.loginName != null && $scope.login.loginName.length > 0) {
                setCookie('loginName', $scope.login.loginName);
            }
            var con = {
                loginName : $scope.login.loginName,
                deviceId : deviceId,
                password : $scope.login.password
            };
            if ($scope.loginFormType == 'reg') {
                con.name = $scope.login.name;
            }
            var msg = JSON.stringify({
                _id : genUUID(),
                actionPath : $scope.loginFormType == 'login' ? actionPathMap.login : actionPathMap.reg,
                content : con
            });
            console.log(con);
        }
    });
})(angular);
