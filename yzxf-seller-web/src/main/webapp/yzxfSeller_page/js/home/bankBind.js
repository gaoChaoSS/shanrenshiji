/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'home';
    var entity = 'bankBind';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.check = 'disabled';
        $scope.phoneCheck = function () {
            if (/^1[34578]{1}\d{9}$/.test($scope.bankUserPhone)) {
                $scope.phoneError = false;
            } else {
                $scope.phoneError = true;
            }
        }
        //是否能点击提交按钮判断
        $scope.submitBtn = function () {
            $scope.check = 'disabled';
            if (/^1[34578]{1}\d{9}$/.test($scope.bankUserPhone) && !window.isEmpty($scope.bankId) &&
                !window.isEmpty($scope.bankUser) &&
                /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.bankUserCardId)){
                $scope.check = false;
            }
        }

        //提交绑定申请
        $scope.bankBindApply = function(){
            var userId="";
            if($rootScope.pathParams.userType==null || $rootScope.pathParams.userType==""){
                malert("网络出错,请返回重新绑定");
                return;
            }else{
                if($rootScope.pathParams.userType=="Factor"){
                    userId=getCookie('_factor_id');
                }else if($rootScope.pathParams.userType=="Seller"){
                    userId=getCookie('_seller_id');
                }
            }
            if(!/^(\d{16}|\d{19})$/.test($scope.bankId)){
                malert("银行卡号错误");
                return;
            }
            if(!/^1[3456789]{1}\d{9}$/.test($scope.bankUserPhone)){
                malert("手机号码错误");
                return;
            }
            if(!/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/.test($scope.bankUserCardId)){
                malert("身份证错误!");
                return;
            }

            var url = window.basePath + '/account/Factor/bankBindApply';
            var data={
                bankId:$scope.bankId,
                bankUser:$scope.bankUser,
                bankUserPhone:$scope.bankUserPhone,
                bankUserCardId:$scope.bankUserCardId,
                bankName:'中国建设银行(德源支行)',
                userType:$rootScope.pathParams.userType,
                userId:userId
            }
            $http.post(url,data).success(function(re){
                if(re.content.isOK){
                    $rootScope.goPage('/home/bindOver/bindOver/Ok/userType/'+$rootScope.pathParams.userType);
                }else{
                    $rootScope.goPage('/home/bindOver/bindOver/No/userType/'+$rootScope.pathParams.userType);
                }
            })
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '绑定银行卡');
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);