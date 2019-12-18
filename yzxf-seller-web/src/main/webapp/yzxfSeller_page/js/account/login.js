(function (angular, undefined) {
    var model = "account";
    var entity = "login";

    window.app.register.controller('account_login_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.submitBtn = function () {
            if (!window.isEmpty($scope.sellerName) && !window.isEmpty($scope.password)) {
                return true;
            } else {
                return false;
            }
        }
        $scope.submitForm = function () {
            var str;
            if($scope.sellerName.indexOf("@")==-1){
                str = $scope.sellerName.substring(0,1);
            }else{
                str = $scope.sellerName.split('@')[1].substring(0,1);
            }
            if(str=='S'){
                $scope.userType='seller';
            }else if(str=='F'){
                $scope.userType='factor';
            }
            if (!$scope.submitBtn()) {
                malert("用户名或密码不能为空");
            } else if ($scope.userType == '请选择您的账号类型') {
                malert("用户名或密码错误");
            } else {
                window.localStorage.setItem("selectPhone", $scope.selectPhone);
                $scope.loginFun({
                    deviceId: genUUID(),
                    loginName: $scope.sellerName,
                    password: $scope.password,
                    userType: $scope.userType
                });
            }
        }

        $scope.loginFun = function(data){
            var url = window.basePath + '/account/User/login';
            $http.post(url, data).success(function (re) {
                setCookie('___USER_TOKEN', re.content.token);
                setCookie('_user_name', re.content.name);
                setCookie('_user_icon', re.content.icon);
                setCookie('_user_id', re.content._id);
                setCookie('_seller_id', re.content.sellerId);
                setCookie('_factor_id', re.content.factorId);
                setCookie('_loginName_', $scope.sellerName);
                setCookie('_pwd_', $scope.password);
                setCookie('_userType_', $scope.userType);
                $rootScope.notLogin = false;
                if ($scope.userType == 'seller') {
                    if (re.content.isSellerAdmin == true) {
                        $scope.goPageLoginAfter('/store/store');
                    } else if (re.content.isSellerAdmin == false || re.content.isSellerAdmin == '') {
                        malert("账户没有权限");
                        return false;
                    }
                } else if ($scope.userType == 'factor') {
                    if (re.content.isFactorAdmin == true) {
                        $scope.goPageLoginAfter('/home/index');
                    } else if (re.content.isFactorAdmin == false && re.content.isFactorAdmin == '') {
                        malert("账户没有权限");
                        return false;
                    }
                }
            });
        };

        $scope.goPageLoginAfter = function(page){
            window.location.href=window.location.href.split("yzxfSeller")[0]+'yzxfSeller'+page;
        }

        $scope.checkAutoLogin = function(){
            if(!isEmpty(getCookie('_seller_id'))){
                $rootScope.goPage("/store/store");
            }else{
                if(!$rootScope.pathParams.val){
                    return;
                }
                $scope.userType = 'seller';

                var data = JSON.parse(decodeURIComponent($rootScope.pathParams.val));
                $scope.loginFun(data);
            }
        };


        $scope.sellerName = getCookie('_loginName_');
        $scope.password = getCookie('_pwd_');
        $scope.userType = getCookie('_userType_');
        $scope.userType = '请选择您的账号类型';

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '登录');
                $rootScope.isLoginPage=true;

                $scope.checkAutoLogin();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });

})(angular);