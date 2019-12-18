(function (angular, undefined) {
    var model = "store";
    var entity = "modifyPassword";
    window.app.register.controller('store_modifyPassword_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.check = 'disabled';
        //完成按钮判断
        $scope.nextBtn = function () {
            $scope.check = 'disabled';
            if (!window.isEmpty($scope.oldPwd) && /^.{6,}$/.test($scope.firstPwd)
                && !window.isEmpty($scope.secondPwd) && ($scope.firstPwd == $scope.secondPwd)) {
                $scope.check = false;
            }
            //两次密码是否相同
            if ($scope.firstPwd == $scope.secondPwd) {
                $scope.errorColor = false;
                $scope.errorText = false;
            } else {
                $scope.errorColor = true;
                $scope.errorText = true;
            }
            //密码是否大于6位
            if (/^.{6,}$/.test($scope.firstPwd)) {
                $scope.pwdLengthError = false;
            } else {
                $scope.pwdLengthError = true;
            }
            if (/^\s*$/.test($scope.firstPwd)) {
                malert('密码开头不能输入空格');
                $scope.pwd = null;
            }
            if (/^\s*$/.test($scope.secondPwd)) {
                malert('密码开头不能输入空格');
                $scope.pwd = null;
            }
        }
        $scope.modifyPwd = function () {
            var url = window.basePath + '/account/User/modifyPassWord';
            var data = {
                userId: getCookie("_user_id"),
                oldPwd: $scope.oldPwd,
                firstPwd: $scope.firstPwd,
                secondPwd: $scope.secondPwd
            };
            $http.post(url, data).success(function (re) {
                $scope.oldPwd = null;
                $scope.firstPwd = null;
                $scope.secondPwd = null;
                $scope.isOK = true;
            })

        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '修改密码');
                $scope.userType = $rootScope.pathParams.userType;
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);