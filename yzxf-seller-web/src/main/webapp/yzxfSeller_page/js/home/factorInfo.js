/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'home';
    var entity = 'factorInfo';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.getFactorInfo = function () {
            var url = window.basePath + '/account/Factor/getFactorInfo';
            $http.get(url).success(function (re) {
                $scope.factorInfo = re.content;
                //临时存放
                $scope.imgMore={
                    bankImg: $scope.factorInfo.bankImg.split("_"),
                    contractImg:$scope.factorInfo.contractImg.split("_")
                };
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '服务站信息');
                $scope.getFactorInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);