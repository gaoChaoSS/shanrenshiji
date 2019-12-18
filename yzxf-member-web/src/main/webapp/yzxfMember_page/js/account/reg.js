(function (angular, undefined) {
    var model = "account";
    var entity = "reg";
    window.app.register.controller('account_reg_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {
        $rootScope.isIndex = false;
        $rootScope.isLoginPage = true;
        $scope.memberType=1;
        $scope.setShareSeller = function(){
            if($scope.countShareSeller!==3){
                $scope.countShareSeller++;
                return;
            }
            $rootScope.goPage('/account/reg/shareId/F-000056/shareType/Factor/returnPayByFixedQrCode/1')
        };

        $scope.getShare=function(){
            var url = window.basePath;
            if(window.isEmpty($rootScope.pathParams.shareId)){
                $scope.showShare = false;
                return;
            }
            if(window.isEmpty($rootScope.pathParams.shareType)){
                $rootScope.pathParams.shareType = "Member";
            }
            if($rootScope.pathParams.shareType === "Member"){
                url+='/crm/Member/getMyInfoById2?_id='+$rootScope.pathParams.shareId;
            }else if($rootScope.pathParams.shareType === "Seller"){
                url+='/account/Seller/getSellerById?_id='+$scope.pathParams.shareId;
            }else if($rootScope.pathParams.shareType === "Factor"){
                url+='/account/Factor/getFactorBaseById?_id='+$scope.pathParams.shareId;
            }else{
                return;
            }
            $http.get(url).success(function(re){
                $scope.share = re.content;
                $scope.showShare = !jQuery.isEmptyObject($scope.share);
            })
        }

        //手机号码是否正确
        $scope.mobileCheck = function(){
            if (!/^1[3456789]{1}\d{9}$/.test($scope.phoneNumber)) {
                $scope.check = true;
                malert('手机号码格式不正确!');
            }
        }

        //下一步按钮判断
        $scope.nextBtn = function () {
            $scope.phoneCheck();
            //两次密码是否相同
            if ($scope.firstPwd == $scope.secondPwd) {
                $scope.errorColor = false;
                $scope.errorText = false;
            } else {
                $scope.errorColor = true;
                $scope.errorText = true;
            }
            //密码是否大于6位
            if (/^.{6,16}$/.test($scope.firstPwd)) {
                $scope.pwdLengthError = false;
            } else {
                $scope.pwdLengthError = true;
            }
            if(/^\s*$/.test($scope.firstPwd)){
                malert('密码开头不能输入空格');
                $scope.firstPwd = null;
            }
            if(/^\s*$/.test($scope.secondPwd)){
                malert('密码开头不能输入空格');
                $scope.secondPwd = null;
            }
            if (/^1[3456789]{1}\d{9}$/.test($scope.phoneNumber) && !window.isEmpty($scope.verification) && $scope.isSendMessage
                && /^.{6,16}$/.test($scope.firstPwd) && !window.isEmpty($scope.secondPwd) && ($scope.firstPwd == $scope.secondPwd)) {
                $scope.check = false;
            }else{
                $scope.check = 'disabled';
            }
            if($scope.memberType==2){
                $scope.check = false;
            }
        }
        $scope.phoneCheck = function (){
            if($scope.isSendMessage){
                return;
            }
            if (/^1[3456789]{1}\d{9}$/.test($scope.phoneNumber)){
                $scope.pCheck = false;
            }else{
                $scope.pCheck = 'disabled';
            }
        }
        $scope.getPhoneCheckCode = function(){
            if($scope.pCheck||window.isEmpty($scope.phoneNumber)){
                return;
            }
            $scope.pCheck = 'disabled';
            var url = window.basePath + '/crm/Member/getMobileIsReg?mobile='+$scope.phoneNumber;
            $http.get(url).success(function(re){
                if(re.content.memberType!="3"){
                    $scope.memberType=re.content.memberType;
                    var url1 = window.basePath +'/common/Sms/getCheckCode';
                    var data1 = {
                        loginName:$scope.phoneNumber,
                        type:'reg'
                    }
                    $http.put(url1,data1).success(function(){
                        malert('获取验证码成功!');
                        $scope.codeDiv=true;
                        //$scope.pCheck=false;
                        $scope.isSendMessage=true;
                        $scope.countDown();
                    })
                }else {
                    $scope.pCheck = false;
                    malert('抱歉!该账号已经注册且已拥有归属');
                }
                // if(re.content._id==null){
                // var url1 = window.basePath +'/common/Sms/getCheckCode';
                // var data1 = {
                //     loginName:$scope.phoneNumber,
                //     type:'reg'
                // }
                // $http.put(url1,data1).success(function(){
                //     malert('获取验证码成功!');
                //     $scope.codeDiv=true;
                //     //$scope.pCheck=false;
                //     $scope.isSendMessage=true;
                //     $scope.countDown();
                // })
                // }else {
                //     $scope.pCheck = false;
                //     malert('抱歉!该账号已经注册');
                // }

            })
        }
        //倒计时
        $scope.countDown = function () {
            //$scope.countdown--;
            //if ($scope.countdown == 0) {
            //    $scope.pCheck=false;
            //    $scope.codeDiv=false;
            //    $scope.countdown = 60;
            //} else {
            //    $scope.pCheck = 'disabled';
            //    (function () {
            //        $rootScope.$apply($scope.countDown);
            //    }).delay(1);
            //}
            $scope.countdown = 60;
            $scope.pCheck = 'disabled';
            var goPageTime=$interval(function () {
                $scope.countdown--;
                if($scope.countdown==0){
                    $interval.cancel(goPageTime);
                    $scope.pCheck = false;
                    $scope.codeDiv=false;
                    $scope.countdown = 60;
                }
                if ($location.path().indexOf("reg") == -1) {
                    $interval.cancel(goPageTime);
                    $scope.pCheck = false;
                }
            }, 1000);
        }

        $scope.submitForm = function () {
            if(/^1[3456789]{1}\d{9}$/.test($scope.phoneNumber)){
                window.localStorage.setItem("selectPhone", $scope.selectPhone);
                var data = {
                    deviceId: genUUID(),
                    mobile:$scope.phoneNumber,
                    verification:$scope.verification,
                    password: $scope.firstPwd,
                    secondPwd: $scope.secondPwd,
                    memberType: $scope.memberType,
                };
                if($scope.showShare){
                    data['shareId'] = $scope.share._id;
                    data['shareType'] = $rootScope.pathParams.shareType;
                    if(data['shareType']==='Factor'){
                        data['autoReg']='Yes';
                    }
                }
                if($scope.notCode){
                    data['notCode'] = $scope.notCode;
                }
                var url = window.basePath + '/crm/Member/register';
                $http.post(url, data).success(function (re) {
                    if(re.content.memberType=='1'){
                        malert("注册成功!");
                        setCookie('selectPhone',$scope.phoneNumber);
                        url = '/account/login/date/' + new Date().getTime();
                        // if(!window.isEmpty($rootScope.pathParams.returnPayByFixedQrCode)){
                        //     url+='/shareId/'+$rootScope.pathParams.shareId + '/shareType/Seller/returnPayByFixedQrCode/'+$rootScope.pathParams.returnPayByFixedQrCode;
                        // }
                        $scope.goPage(url);
                    }else if(re.content.memberType=='2'){
                        malert("修改归属成功!");
                        $rootScope.goPage('/home/index');
                    }

                });
            }else{
                malert("请输入正确的手机号码!");
            }
        };

        $scope.goBackCheck=function(){
            if(window.isWechat()){
                $rootScope.goPage('/other/other');
            }else{
                $rootScope.goPage('/home/index');
            }
        };

        $scope.setNotCode = function(){
            if($scope.countNotCode!==3){
                $scope.countNotCode++;
                return;
            }
            $scope.notCode = true;
            $scope.check = false;
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '注册');
                $scope.phoneNumber = null;
                $scope.firstPwd = null;
                $scope.secondPwd = null;
                $scope.verification = null;
                $scope.countdown=60;
                $scope.codeDiv = false;
                $scope.isSendMessage = false;
                $scope.check = 'disabled';
                $scope.pCheck = 'disabled';
                $scope.phoneNumber="";
                $scope.verification="";
                $scope.countNotCode=0;
                $scope.countShareSeller=0;
                if(!window.isEmpty(getCookie("_member_id"))){
                    $rootScope.goPage('/my/my');
                }
                $scope.getShare();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);