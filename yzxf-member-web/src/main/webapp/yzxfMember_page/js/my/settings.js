(function (angular, undefined) {

    var model = 'my';
    var entity = 'settings';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.logout = function () {
            deleteCookie('___MEMBER_TOKEN');
            deleteCookie('_member_loginName');
            deleteCookie('loginName');
            deleteCookie('_member_mobile');
            deleteCookie('_member_icon');
            deleteCookie('lastLoginType');
            deleteCookie('_member_id');
            deleteCookie('_other_id');
            $rootScope.myInfo = null;
            $rootScope.goPage('/account/login');
        }
        $scope.isLogin = function (){
            $scope.isUserLogin = getCookie('___MEMBER_TOKEN');
            if($scope.isUserLogin==null||window.isEmpty($scope.isUserLogin)){
                $rootScope.goPage('/account/login');
            }
        }

        $scope.goBindWechat=function(){
            var url = window.basePath + '/account/Oauth/start?' +
                'type='+(window.isMobile()?'wechatMobile':'wechatPc')+
                '&deviceId='+genUUID();
            $http.get(url).success(function (re) {
                window.location.href=re.content.sendUrl;
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.isLogin();
                window.setWindowTitle($rootScope, '设置');
                //$rootScope.windowTitleHide = true;
                $rootScope.isIndex = false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);
