(function (angular, undefined) {

    var model = 'account';
    var entity = 'reg';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.accountBottom = '/yzxfMall_page/temp_new/accountBottom.html';
        initTypeGrid($rootScope, $scope, $http , $interval , $location);

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
                    var url1 = window.basePath +'/common/Sms/getCheckCode';
                    var data1 = {
                        loginName:+$scope.phoneNumber,
                        type:'reg'
                    }
                    $http.put(url1,data1).success(function(){
                        malert('获取验证码成功!');
                        $scope.codeDiv=true;
                        $scope.pCheck=false;
                        $scope.countDown();
                    })
                }else {
                    malert('抱歉!该账号已经注册');
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
        $scope.submitForm = function () {
            if(!$scope.isOK){
                malert('请阅读并接受<<普惠生活>>用户协议!');
                return;
            }
            if(/^1[3456789]{1}\d{9}$/.test($scope.phoneNumber)){
                window.localStorage.setItem("selectPhone", $scope.selectPhone);
                var data = {
                    deviceId: genUUID(),
                    mobile:$scope.phoneNumber,
                    verification:$scope.verification,
                    password: $scope.firstPwd,
                    secondPwd: $scope.secondPwd
                };
                var url = window.basePath + '/crm/Member/register';
                $http.post(url, data).success(function () {
                    malert("注册成功!");
                    setCookie('selectPhone',$scope.phoneNumber);
                    $rootScope.goPage('/account/login/date/' + new Date().getTime());
                });
            }else{
                malert("请输入正确的手机号码!");
            }
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '注册');
                $scope.memberIsRealName=true;
                $scope.phoneNumber = null;
                $scope.firstPwd = null;
                $scope.secondPwd = null;
                $scope.verification = null;
                $scope.isOK = false;
                $scope.countdown=60;
                $scope.codeDiv = false;
                $scope.pCheck = 'disabled';
                $scope.phoneNumber="";
                $scope.verification="";
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);