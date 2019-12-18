/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'home';
    var entity = 'bindOver';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //倒计时
        $scope.countDown = function () {
            $scope.countdown--;
            if ($scope.countdown == 0) {
                $scope.goPageUrl();
            } else {
                (function () {
                    $rootScope.$apply($scope.countDown);
                }).delay(1);
            }
        }
        $scope.goPageUrl=function(){
            if($rootScope.pathParams.userType=="Factor"){
                $rootScope.goPage('/home/index');
            }else{
                $rootScope.goPage('/store/storeAccount');
            }
        }
        $scope.goPageUrl2=function(){
            if($rootScope.pathParams.userType=="Factor"){
                $rootScope.goPage('/home/cardIssuingAccount');
            }else{
                $rootScope.goPage('/store/store');
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '绑定完成');
                if($rootScope.pathParams.bindOver=='Ok'){
                    $scope.bindOver = true;
                    $scope.countDown();
                }else{
                    $scope.bindOver = false;
                }
                $scope.countdown = 5;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);