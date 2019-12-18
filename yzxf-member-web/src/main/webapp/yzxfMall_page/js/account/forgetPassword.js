(function (angular, undefined) {

    var model = 'account';
    var entity = 'forgetPassword';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.accountBottom = '/yzxfMall_page/temp_new/accountBottom.html';
        initTypeGrid($rootScope, $scope, $http , $interval , $location);
        $scope.forgotPwd = function (){
            if(window.isEmpty($scope.phoneNumber)){
                malert('请输入手机号!');
                return;
            }
            if($scope.confirmPassword!=$scope.newPassword){
                malert('两次密码输入不一致!');
                return;
            }
            if(window.isEmpty($scope.newPassword)){
                malert('请输入密码!');
                return;
            }
            if(window.isEmpty($scope.confirmPassword)){
                malert('请确认密码!');
                return;
            }
            var url = window.basePath +'/crm/Member/forgotPassword';
            var data = {
                loginName:$scope.phoneNumber,
                newPassword:$scope.newPassword,
                verification:$scope.verification,
                confirmPassword:$scope.confirmPassword,
                forgetType:'mall'
            }
            $http.put(url,data).success(function(){
                $scope.modifyIsOk = 'OK';
                $scope.isForget=true;
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
                malert('请输入正确的手机号!');
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
        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '找回密码');
                $scope.modifyIsOk='NO';
                $scope.isForget=false;
                $scope.countdown=60;
                $scope.codeDiv = false;
                $scope.pCheck = 'disabled';
                $scope.phoneNumber="";
                $scope.newPassword="";
                $scope.verification="";
                $scope.confirmPassword="";
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);