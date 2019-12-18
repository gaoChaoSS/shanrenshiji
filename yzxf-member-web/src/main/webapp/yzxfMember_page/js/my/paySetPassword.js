(function(angular, undefined) {
    var model = "my";
    var entity = "paySetPassword";
    window.app.register.controller('my_paySetPassword_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope.isIndex =false;
        $scope.errorColor=false;
        $scope.checkValue = '完成';

        $scope.check = 'disabled';
        //完成按钮判断
        $scope.nextBtn=function(){
            //密码是否6位
            if(/^\d{6}$/.test($scope.firstPwd)){
                $scope.pwdLengthError=false;
            }else{
                malert('请输入6位数字密码');
            }
        }
        $scope.sureNextBtn = function (){
            //两次密码是否相同
            if($scope.firstPwd==$scope.secondPwd){
                $scope.errorColor=false;
                $scope.errorText=false;
            }else{
                malert('两次密码不相同');
            }
        }
        $scope.nextCheck = function (){
            $scope.check = 'disabled';
            if(/^\d{6}$/.test($scope.firstPwd)
                && !window.isEmpty($scope.secondPwd)
                && ($scope.firstPwd==$scope.secondPwd)){
                $scope.check = false;
                $scope.checkValue = '完成';
            }else{
                $scope.checkValue = '密码验证不通过';
            }
        }
        $scope.setPayPassword = function(){
            $scope.nextBtn();
            var url = window.basePath + '/crm/Member/setPayPassword';
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
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);