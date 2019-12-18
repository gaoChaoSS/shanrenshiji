/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'couponVerification';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.getCouponInfo=function(){
            var url = window.basePath + '/crm/Coupon/getCouponUseTime';
            var data = {
                couponId:$rootScope.pathParams.couponId
            };
            $http.post(url,data).success(function(re){
                $scope.value = re.content;
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '卡券核销');
                $scope.getCouponInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);