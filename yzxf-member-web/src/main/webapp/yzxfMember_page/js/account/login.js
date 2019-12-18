(function (angular, undefined) {
    var model = "account";
    var entity = "login";


    window.app.register.controller('account_login_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //页面事件处理
        $scope.showLoginSelect = false;
        $scope.selectedText = getCookie("lastLoginType");
        $scope.selectedText = window.isEmpty($scope.selectedText) ? '手机号' : $scope.selectedText;
        $scope.errorLoginPanel = false;
        $scope.selectPhone = getCookie("selectPhone");
        $scope.selectNumber = getCookie("selectNumber");
        $scope.selectCard = getCookie("selectCard");
        $scope.pwd =getCookie("pwd");
        var chk ='disabled';
        if((!window.isEmpty($scope.selectPhone)
            || !window.isEmpty($scope.selectNumber)
            || !window.isEmpty($scope.selectCard))
            && !window.isEmpty($scope.pwd)
        ){
            chk = false;
        }
        $scope.check = chk;
        //是否能点击登录按钮判断
        $scope.loginBtnColor = function () {
            $scope.check = 'disabled';
            if ($scope.selectedText == '手机号') {
                if (/^1[3456789]{1}\d{9}$/.test($scope.selectPhone) && !window.isEmpty($scope.pwd)) {
                    $scope.check = false;
                }
            } else if ($scope.selectedText == '卡号') {
                if (!window.isEmpty($scope.selectNumber) && !window.isEmpty($scope.pwd)) {
                    $scope.check = false;
                }
            } else if ($scope.selectedText == '身份证号') {
                if (/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.selectCard) && !window.isEmpty($scope.pwd)) {
                    $scope.check = false;
                }
            }

        }
        $scope.loginPwd = function () {
            if (!/[0-9a-zA-Z]([^\s\u4e00-\u9fa5]*[0-9a-zA-Z])?/.test($scope.pwd)) {
                $scope.pwd = null;
            }
        }
        $scope.goPageC = function () {
            $rootScope.goPage('/home/index/s/' + new Date().getTime());
        }

        $scope.submitForm = function () {
            $scope.loginBtnColor();

            window.localStorage.setItem("selectPhone", $scope.selectPhone);
            var data = {
                deviceId: genUUID(),
                password: $scope.pwd
            };
            if ($scope.selectedText == '手机号') {
                data.userNameType = 'mobile';
                data.mobile = $scope.selectPhone;
                setCookie('selectPhone', $scope.selectPhone,null,7);
                setCookie('pwd', $scope.pwd,null,7);
            } else if ($scope.selectedText == '卡号') {
                data.userNameType = 'cardNo';
                data.cardNo = $scope.selectNumber;
                setCookie('selectNumber', $scope.selectNumber,null,7);
                setCookie('pwd', $scope.pwd,null,7);
            } else if ($scope.selectedText == '身份证号') {
                data.userNameType = 'idCard';
                data.idCard = $scope.selectCard;
                setCookie('selectCard', $scope.selectCard,null,7);
                setCookie('pwd', $scope.pwd,null,7);
            }
            $scope.loginFun(data);
        };

        $scope.loginFun = function(data){
            var url = window.basePath + '/crm/Member/loginUserPass';
            $http.post(url, data).success(function (re) {
                if ($("#rememberPwd").is(':checked')) {
                    setCookie('___MEMBER_TOKEN', re.content.token,null,7);
                    setCookie('_member_loginName', re.content.loginName,null,7);
                    setCookie('_member_mobile', re.content.mobile,null,7);
                    setCookie('_member_icon', re.content.icon,null,7);
                    setCookie('_member_id', re.content._id,null,7);
                    setCookie('lastLoginType', $scope.selectedText,null,7);
                } else {
                    setCookie('___MEMBER_TOKEN', re.content.token);
                    setCookie('_member_loginName', re.content.loginName);
                    setCookie('_member_mobile', re.content.mobile);
                    setCookie('_member_icon', re.content.icon);
                    setCookie('_member_id', re.content._id);
                    setCookie('lastLoginType', $scope.selectedText);
                }
                deleteCookie("_other_id");
                $rootScope.myInfo = re.content;
                $rootScope.notLogin = false;
                //$rootScope.goPage('/home/index');
                if ($rootScope.pageActionMap[$rootScope.currentPageUrl].onResume != null) {
                    $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
                }else{
                    // if(!window.isEmpty($rootScope.pathParams.returnPayByFixedQrCode) && !window.isEmpty($rootScope.pathParams.shareId)){
                    //     $rootScope.goPage("/my/payByFixedQrCode/sellerId/"+$rootScope.pathParams.shareId);
                    // }else
                    if(window.isWechat()){
                        $rootScope.goPage('/other/other');
                    }else{
                        $rootScope.goPage('/home/index');
                    }
                }
            });
        }

        $scope.goBackCheck=function(){
            if(window.isWechat()){
                $rootScope.goPage('/other/other');
            }else{
                $rootScope.goPage('/home/index');
            }
        };

        $scope.checkAutoLogin = function(){
            if(!window.isEmpty(getCookie("_member_id"))){
                $rootScope.goPage('/my/my');
            }else{
                if(!$rootScope.pathParams.val){
                    return;
                }
                $scope.userType = 'member';

                var data = JSON.parse(decodeURIComponent($rootScope.pathParams.val));
                $scope.loginFun(data);
            }
        };

        $scope.checkAutoLogin();

        //$rootScope.pageActionMap[$rootScope.currentPageUrl] = {
        //    onResume: function () {
        //        window.setWindowTitle($rootScope, '登录');
        //        //$rootScope.windowTitleHide = true;
        //        $rootScope.showTop = false;
        //
        //        if (window.isEmpty($scope.selectPhone)) {
        //            $scope.selectPhone = getCookie('selectPhone');
        //            if($scope.selectPhone=='null'){
        //                $scope.selectPhone==null;
        //            }
        //        }
        //        if (window.isEmpty($scope.selectNumber)) {
        //            $scope.selectNumber = getCookie('selectNumber');
        //            console.log($scope.selectNumber);
        //            if($scope.selectNumber=='null'){
        //                $scope.selectNumber==null;
        //            }
        //        }
        //        if (window.isEmpty($scope.selectCard)) {
        //            $scope.selectCard = getCookie('selectCard');
        //            if($scope.selectCard=='null'){
        //                $scope.selectCard==null;
        //            }
        //        }
        //    }
        //}
        //$rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });

})(angular);