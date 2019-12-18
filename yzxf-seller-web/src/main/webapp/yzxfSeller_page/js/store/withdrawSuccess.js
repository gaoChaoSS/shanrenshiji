/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'withdrawSuccess';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location,$interval, $http, $element, $compile) {

        $scope.goPageFun=function(){
            $scope.countTimeNum=5;
            var goPageTime=$interval(function () {
                $scope.countTimeNum--;
                if($scope.countTimeNum==0){
                    $interval.cancel(goPageTime);
                    $scope.goPageUrl();
                }
                if ($location.path().indexOf("withdrawSuccess") == -1) {
                    $interval.cancel(goPageTime);
                }
            }, 1000);
        }

        $scope.goPageUrl=function(){
            if($scope.type=='Seller'){
                $rootScope.goPage("/store/storeAccount");
            }else{
                $rootScope.goPage("/home/cardIssuingAccount");
            }
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '提现详情');
                $scope.type=$rootScope.pathParams.type;
                $scope.goPageFun();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);


