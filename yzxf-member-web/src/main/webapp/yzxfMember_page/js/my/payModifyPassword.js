(function(angular, undefined) {
    var model = "my";
    var entity = "payModifyPassword";
    window.app.register.controller('my_payModifyPassword_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.check = 'disabled';
        //完成按钮判断
        $scope.nextBtn=function(){
            $scope.check = 'disabled';
            if(!window.isEmpty($scope.oldPwd) && !window.isEmpty($scope.newPwd) && /^[0-9]{6}$/.test($scope.newPwd)
                && ($scope.newPwd==$scope.secondPwd)){
                $scope.check = false;
            }
            //两次密码是否相同
            if($scope.newPwd==$scope.secondPwd){
                $scope.errorColor=false;
                $scope.errorText=false;
            }else{
                $scope.errorColor=true;
                $scope.errorText=true;
            }
            //密码是否大于6位
            if(/^[0-9]{6}$/.test($scope.newPwd)){
                $scope.pwdLengthError=false;
            }else{
                $scope.pwdLengthError=true;
            }
        }

        $scope.modifyMyPayPwd = function(){
            var url = window.basePath + '/crm/Member/modifyMyPayPwd';
            var data={
                oldPwd: $scope.oldPwd,
                firstPwd: $scope.newPwd,
                secondPwd: $scope.secondPwd
            };
            $http.post(url,data).success(function (re) {
                $scope.oldPwd = null;
                $scope.newPwd = null;
                $scope.secondPwd = null;
                $scope.isOK = true;
            })

        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '支付密码修改');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);