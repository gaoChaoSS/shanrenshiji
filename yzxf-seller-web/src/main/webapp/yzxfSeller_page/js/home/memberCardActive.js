(function (angular, undefined) {

    var model = 'home';
    var entity = 'memberCardActive';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {
        //倒计时
        $scope.countDown = function () {
            if (!/^1[3456789]{1}\d{9}$/.test($scope.phoneNumber)) {
                malert("请输入正确的手机号码");
                return;
            }
            $scope.getCode();
            $scope.isSend='disabled';
            $scope.countSendTime=60;
            var isSendTime=$interval(function () {
                $scope.countSendTime--;
                if($scope.countSendTime==-1 || $location.path().indexOf("memberCardActive") == -1){
                    $interval.cancel(isSendTime);
                    $scope.isSend=false;
                    $scope.countSendTime=60;
                }
            }, 1000);
        }

        $scope.getCode=function(){
            var url = window.basePath +'/common/Sms/getCheckCode';
            var data = {
                loginName:$scope.phoneNumber,
                type:'reg'
            }
            $http.put(url,data).success(function(){
                malert("已发送短信验证码");
            })
        }
        //电话验证
        $scope.phoneCheck = function () {
            if (/^1[34578]{1}\d{9}$/.test($scope.phoneNumber)) {
                $scope.phoneError = false;
                $scope.getMemberInfo();
            } else {
                $scope.phoneError = true;
            }
        }
        //回车响应事件
        $scope.KeyCode = function(){
            var currKey = 0, e = e || event;
            currKey = e.keyCode || e.which || e.charCode;
            if(currKey==13) {
                $scope.phoneCheck();
            }
        }
        //通过手机号获取账户信息
        $scope.getMemberInfo = function () {
            if(!window.isEmpty($scope.phoneNumber)){
                var url = window.basePath + '/crm/Member/getMemberInfo?mobile=' + $scope.phoneNumber;
                $http.get(url).success(function (re) {
                    if(re.content.items.length != 0 && !window.isEmpty(re.content.items[0].cardNo)) {
                        $scope.status = 4;
                    }else if (re.content.items.length != 0 && re.content.items[0].isRealName == true) {
                        $scope.memberId = re.content.items[0]._id;
                        $scope.status = 3;
                        setCookie("_active_memberId",$scope.memberId);
                    } else if (re.content.items.length != 0 && (re.content.items[0].isRealName == false || re.content.items[0].isRealName == null)) {
                        $scope.memberId = re.content.items[0]._id;
                        $scope.status = 1;
                        setCookie("_active_memberId",$scope.memberId);
                    } else{
                        $scope.status = 2;
                    }
                })
            }else{
                malert('请输入会员手机号');
            }

        }
        //一键注册
        $scope.memberReg = function() {
            if (/^1[34578]{1}\d{9}$/.test($scope.phoneNumber)) {
                var url = window.basePath + '/crm/Member/register';
                var data = {
                    mobile: $scope.phoneNumber,
                    autoReg: 'Yes',
                    deviceId: genUUID(),
                    verification:$scope.verification
                };
                data['notCode'] = $scope.notCode;
                $http.post(url, data).success(function (re) {
                    malert('注册成功,请输入真实信息进行实名认证!');
                    setCookie("_active_memberId",re.content.items[0]._id);
                    $rootScope.goPage('/home/memberRealName/memberId/' + re.content.items[0]._id);
                })
            }else{
                malert('请输入正确的电话号码');
            }
        }

        $scope.setNotCode = function(){
            if($scope.countNotCode!==3){
                $scope.countNotCode++;
                return;
            }
            malert("开启调试");
            $scope.notCode = true;
            $scope.check = false;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '会员卡激活');
                $scope.countdown = 4;
                $scope.countNotCode = 0;
                $scope.phoneNumber = null;
                $scope.status = null;
                $scope.isSend=false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);