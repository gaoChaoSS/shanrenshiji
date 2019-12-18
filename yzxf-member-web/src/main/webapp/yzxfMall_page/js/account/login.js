(function (angular, undefined) {

    var model = 'account';
    var entity = 'login';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.accountBottom = '/yzxfMall_page/temp_new/accountBottom.html';
        initTypeGrid($rootScope, $scope, $http , $interval , $location);


        $scope.submitForm = function () {
            if ($scope.userType == '1') {
                if (!/^1[34578]{1}\d{9}$/.test($scope.loginName) && window.isEmpty($scope.pwd)) {
                    malert('请输入正确的手机号和密码');
                    return;
                }
            } else if ($scope.userType == '2') {
                if (window.isEmpty($scope.loginName) && window.isEmpty($scope.pwd)) {
                    malert('请输入正确的会员卡号和密码');
                    return;
                }
            } else if ($scope.userType == '3') {
                if (!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.loginName) && window.isEmpty($scope.pwd)) {
                    malert('请输入正确的身份证号和密码');
                    return;
                }
            }
            var data = {
                deviceId: genUUID(),
                password: $scope.pwd
            };
            if ($scope.userType == '1') {
                data.userNameType = 'mobile';
                data.mobile = $scope.loginName;
                setCookie('selectPhone', $scope.loginName);
            } else if ($scope.userType == '2') {
                data.userNameType = 'cardNo';
                data.cardNo = $scope.loginName;
                setCookie('selectNumber', $scope.loginName);
            } else if ($scope.userType == '3') {
                data.userNameType = 'idCard';
                data.idCard = $scope.loginName;
                setCookie('selectCard', $scope.loginName);
            }

            //记住密码

            var url = window.basePath + '/crm/Member/loginUserPass';
            $http.post(url, data).success(function (re) {
                setCookie('___MEMBER_TOKEN', re.content.token, null, 60*3);
                setCookie('_member_loginName', re.content.loginName, null, 60*3);
                setCookie('_member_mobile', re.content.mobile, null, 60*3);
                setCookie('_member_icon', re.content.icon, null, 60*3);
                setCookie('_member_id', re.content._id, null, 60*3);
                setCookie('lastLoginType', $scope.selectedText, null, 60*3);
                $rootScope.myInfo = re.content;
                $rootScope.notLogin = false;
                $scope.loginAfterGetMemberInfo();
                $rootScope.goPage('/home/index');
            });

        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '登录');
                $scope.memberIsRealName=true;
                $scope.userType='1';
                $scope.loginName='';
                $scope.pwd='';
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);