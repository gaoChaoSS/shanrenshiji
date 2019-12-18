(function(angular, undefined) {
    var model = "home";
    var entity = "paySetPassword";
    window.app.register.controller(model+'_'+entity+'_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope.isIndex =false;
        $scope.errorColor=false;

        $scope.check = 'disabled';
        //完成按钮判断
        $scope.nextBtn=function(){
            $scope.check = 'disabled';
            if(/^[0-9]{6}$/.test($scope.firstPwd)
                && !window.isEmpty($scope.secondPwd)
                && ($scope.firstPwd==$scope.secondPwd)){
                $scope.check = false;
            }
            //两次密码是否相同
            if($scope.firstPwd==$scope.secondPwd){
                $scope.errorColor=false;
                $scope.errorText=false;
            }else{
                $scope.errorColor=true;
                $scope.errorText=true;
            }
            //密码是否6位
            if(/^[0-9]{6}$/.test($scope.firstPwd)){
                $scope.pwdLengthError=false;
            }else{
                $scope.pwdLengthError=true;
            }
        }
        $scope.setFactorPayPassword = function(){
            $scope.nextBtn();
            var url = window.basePath + '/account/Factor/setPayPassword';
            var data={
                firstPwd: $scope.firstPwd,
                secondPwd: $scope.secondPwd
            };
            $http.post(url,data).success(function (re) {
                $scope.firstPwd = null;
                $scope.secondPwd = null;
                $scope.isOK = true;
            })
        }
        $scope.setSellerPayPassword = function(){
            $scope.nextBtn();
            var url = window.basePath + '/account/Seller/setPayPassword';
            var data={
                firstPwd: $scope.firstPwd,
                secondPwd: $scope.secondPwd
            };
            $http.post(url,data).success(function (re) {
                $scope.firstPwd = null;
                $scope.secondPwd = null;
                $scope.isOK = true;
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '设置支付密码');
                $scope.userType = $rootScope.pathParams.userType;
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);