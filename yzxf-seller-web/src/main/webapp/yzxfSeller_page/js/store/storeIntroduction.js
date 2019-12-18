(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeIntroduction';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //获取商家信息
        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.storeInfo = re.content;
            });
        }
        //获取发卡点信息
        $scope.getFactorInfo = function () {
            var url = window.basePath + '/account/Factor/getFactorInfo';
            $http.get(url).success(function (re) {
                $scope.factorInfo = re.content;
            });
        }
        $scope.submitSellerForm = function () {
            if (!window.isEmpty($scope.storeInfo.intro)) {
                var url = window.basePath + '/account/Seller/updateSellerInfo';
                var data = {
                    field:'intro',
                    content: $scope.storeInfo.intro
                };
                $http.post(url, data).success(function () {
                    malert("修改成功");
                    $rootScope.goBack();
                })
            }else{
                malert("请填写商家简介");
            }
        }
        $scope.submitFactorForm = function () {
            if (!window.isEmpty($scope.factorInfo.intro)) {
                var url = window.basePath + '/account/Factor/updateFactorInfo';
                var data = {
                    field:'intro',
                    content: $scope.factorInfo.intro,
                    factorId:getCookie('_factor_id')
                };
                $http.post(url, data).success(function () {
                    malert("修改成功");
                    $rootScope.goBack();
                })
            }else{
                malert("请填写服务中心简介");
            }
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商家简介');
                $scope.userType=$rootScope.pathParams.userType;
                if($scope.userType=='seller'){
                    $scope.getStoreInfo();
                }else{
                    $scope.getFactorInfo();
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);