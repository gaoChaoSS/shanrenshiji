(function(angular, undefined) {
    var model = "account";
    var entity = "forgotPassword";
    window.app.register.controller('account_forgotPassword_Ctrl', function($rootScope, $scope, $location, $http, $element, $compile) {

        //下一步按钮判断
        $scope.submitBtn=function(){
            if(/^1[34578]{1}\d{9}$/.test($scope.phoneNumber) && !window.isEmpty($scope.verification)
                && /^.{6,16}$/.test($scope.newPassword)){
                $scope.check = false;
            }
            if (/^.{6,16}$/.test($scope.newPassword)) {
                $scope.pwdLengthError = false;
            } else {
                $scope.pwdLengthError = true;
            }
        }
        $scope.forgotPwd = function (){
            var url = window.basePath +'/crm/Member/forgotPassword';
            var data = {
                loginName:$scope.phoneNumber,
                newPassword:$scope.newPassword,
                verification:$scope.verification
            }
            $http.put(url,data).success(function(){
                malert('密码找回成功!请登录');
                $rootScope.goPage('/account/login');
            })

        }

        $scope.phoneCheck = function (){
            if (/^1[34578]{1}\d{9}$/.test($scope.phoneNumber)){
                $scope.pCheck = false;
            }else{
                $scope.pCheck = 'disabled';
            }
        }
        $scope.getPhoneCheckCode = function(){
            if($scope.pCheck||window.isEmpty($scope.phoneNumber)){
                return;
            }
            var url = window.basePath + '/crm/Member/getMobileIsReg?mobile='+$scope.phoneNumber;
            $http.get(url).success(function(re){
                if(re.content._id==null){
                    malert('抱歉!该账号尚未注册,请先注册');
                }else {
                    var url1 = window.basePath +'/common/Sms/getCheckCode';
                    var data1 = {
                        loginName:+$scope.phoneNumber,
                        type:'change_password'
                    }
                    $http.put(url1,data1).success(function(){
                        malert('获取验证码成功!');
                        $scope.codeDiv=true;
                        $scope.pCheck=false;
                        $scope.countDown();
                    })
                }
            })
        }
        //倒计时
        $scope.countDown = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                $scope.pCheck=false;
                $scope.codeDiv=false;
                $scope.countdown = 60;
            } else {
                $scope.pCheck = 'disabled';
                (function () {
                    $rootScope.$apply($scope.countDown);
                }).delay(1);
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '支付密码找回');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = false;
                $scope.codeDiv = false;
                $scope.check = 'disabled';
                $scope.pCheck = 'disabled';
                $scope.countdown=60;
                $scope.phoneNumber="";
                $scope.newPassword="";
                $scope.verification="";
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);