(function (angular, undefined) {
    var model = 'account';
    var entity = 'bind';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $interval, $element, $compile) {

        var mobileReg = /^1[34578]\d{9}$/;
        $scope.checkInput = function () {
            if (mobileReg.test($scope.bindData.phone) && !window.isEmpty($scope.bindData.smsCode)) {
                $scope.checkBtn = false;
            } else {
                $scope.checkBtn = 'disabled';
            }
        };

        $scope.verText = "获取短信验证码";
        $scope.getVer = function () {
            if (window.isEmpty($scope.bindData.phone) || !mobileReg.test($scope.bindData.phone)) {
                malert("请输入有效的手机号码");
                return;
            }
            $scope.checkVer = 'disabled';
            var t = new Date();
            var c = t.getTime() % 5 + '' + t.getTime() % 7 + '' + t.getTime() % 9 + '' + t.getTime() + '' + t.getSeconds() % 5 + '' + t.getSeconds() % 7 + '' + t.getSeconds() % 9;
            var data = {
                loginName: $scope.bindData.phone,
                type: "reg",
                c: c
            }
            $http.put(window.basePath + '/common/Sms/getCheckCode', data).success(function (re) {
                malert("已发送短信验证码!");
                var timeCount = 60;
                $scope.setTime = $interval(function () {
                    timeCount--;
                    $scope.verText = timeCount + "秒后重新发送";
                    if (timeCount < 0) {
                        $interval.cancel($scope.setTime);
                        $scope.checkVer = false;
                        $scope.verText = "获取短信验证码";
                    }
                }, 1000);
            }).error(function (re) {
                malert(re.responseJSON.content.errMsg);
                $scope.checkVer = false;
                $interval.cancel($scope.setTime);
            });
        }

        $scope.login=function(){
            if(window.isEmpty($scope.bindData.phone)){
                malert("请输入手机号码");
                return;
            }
            if(window.isEmpty($scope.bindData.password)){
                malert("请输入密码");
                return;
            }
            $scope.doBind();
        }

        $scope.doBind = function () {
            $http.post(window.basePath + '/account/User/codeLogin', $scope.bindData).success(function(re){
                setCookie('___MEMBER_TOKEN', re.content.token);
                setCookie('_member_loginName', re.content.loginName);
                setCookie('_member_mobile', re.content.mobile);
                setCookie('_member_icon', re.content.icon);
                setCookie('_member_id', re.content._id);
                setCookie('lastLoginType', $scope.selectedText);
                setCookie('isBindWechat', true);
                $rootScope.myInfo = re.content;
                $rootScope.notLogin = false;
                $rootScope.isBindWechat = getCookie('isBindWechat');
                malert("绑定成功");
                if(window.isEmpty($rootScope.pathParams.sellerId)){
                    $rootScope.goPage('/my/my');
                }else{
                    $rootScope.goPage('/my/payByFixedQrCode/sellerId/'+$rootScope.pathParams.sellerId);
                }
                if ($rootScope.pageActionMap[$rootScope.currentPageUrl].onResume != null) {
                    $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
                }
            });
        }

        $scope.getMember=function(){
            if(window.isEmpty($scope.bindData.phone)){
                return;
            }
            $http.get(window.basePath + '/crm/Member/getMemberInfo?mobile='+ $scope.bindData.phone).success(function(re){
                if(re.content==null || re.content.items==null){
                    $scope.isMember=false;
                }else{
                    $scope.isMember=true;
                    $scope.memberInfo = re.content.items[0];
                }
            });
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '关联账号');
                $scope.isHideTitle = false;
                $scope.isMember = false;
                $scope.checkBtn = 'disabled';
                $scope.verText = "获取短信验证码";

                // var dataStr = getCookie("_oauth_data");
                // if(!window.isEmpty(dataStr)) {
                    // $rootScope.goPage("/my/my")
                // } else {
                //     $scope.bindData = JSON.parse(dataStr);
                // }
                $scope.bindData={
                    phone:'',
                    realName:'',
                    cardNo:'',
                    password:'',
                    openId:$rootScope.pathParams.openId
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentUrl].onResume();

    });
})(angular);