(function (angular, undefined) {

    var model = 'my';
    var entity = 'setBank';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.initUser = function () {
            $scope.userInfo = {
                bankId: "",
                bankName: "",
                bankUser: "",
                bankUserCardId: "",
                bankUserPhone: "",
                bankAddress:""
            };

            var url = window.basePath+"/crm/Member/getBankInfo";
            $http.get(url).success(function(re){
                $scope.userInfo = re.content;
            })
        };

        $scope.submitForm = function(){
            if(!/^[0-9]{16,19}$/.test($scope.userInfo.bankId)){
                malert("请输入正确的银行账号!");
                return;
            }
            if(window.isEmpty($scope.userInfo.bankName)){
                malert("请输入开户行!");
                return;
            }
            if(!window.isEmpty($scope.userInfo.bankName) && $scope.userInfo.bankAddress.length>200){
                malert("请输入正确的开户行地址（200个字符长度以内）");
                return;
            }
            if(!/^[\u4E00-\u9FA5]{2,64}$/.test($scope.userInfo.bankUser)){
                malert("请填写2~64位中文汉字之间的户名!");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.userInfo.bankUserCardId)){
               malert("请输入正确的持卡人身份证号码!");
               return;
            }
            if(!/^1[34578]{1}\d{9}$/.test($scope.userInfo.bankUserPhone)) {
                malert('请输入正确的持卡人手机!');
                return;
            }
            var url = window.basePath + "/crm/Member/saveBankInfo";
            $http.post(url, $scope.userInfo).success(function () {
                malert("提交成功");
                $rootScope.goBack();
            });
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '设置提现银行卡');
                $scope.initUser();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);
