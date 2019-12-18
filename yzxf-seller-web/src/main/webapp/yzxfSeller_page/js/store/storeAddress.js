(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeAddress';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.errorOpen = false;

        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.storeInfo = re.content;
                $scope.area = $scope.storeInfo.area;
                $scope.address = $scope.storeInfo.address;
            });
        }
        $scope.getFactorInfo = function () {
            $scope.factorId = getCookie("_factor_id");
            var url = window.basePath + '/account/Factor/getFactorInfo?factorId=' + $scope.factorId;
            $http.get(url).success(function (re) {
                $rootScope.factorInfo = re.content;
                $scope.area = $scope.factorInfo.area;
                $scope.address = $scope.factorInfo.address;

            });
        }

        $scope.updateSellerAddress = function () {
            if (!window.isEmpty($scope.address)) {
                if (!window.isEmpty($rootScope.pathParams.userType)) {
                    var url = window.basePath;
                    if ($rootScope.pathParams.userType=="seller") {
                        url += '/account/Seller/updateSellerInfo';
                    } else if ($rootScope.pathParams.userType=="factor") {
                        url += '/account/Factor/updateFactorInfo';
                    }
                    var data = {
                        field: 'address',
                        content: $scope.address
                    };
                    $http.post(url, data).success(function () {
                        malert("修改成功!");
                        $rootScope.goBack();
                    });
                }
            } else {
                malert("请填写完整地址");
            }
        }
        $scope.updateFactorAddress = function () {
            if (!window.isEmpty($scope.address)) {
                var url = window.basePath + '/account/Factor/updateFactorInfo';
                var data = {
                    field: 'address',
                    content: $scope.address,
                    factorId: getCookie('_factor_id')
                };
                $http.post(url, data).success(function () {
                    malert("修改成功!");
                    $rootScope.goPage('/home/factorInfo');
                });
            } else {
                malert("请填写完整地址");
            }
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '店铺地址');
                $scope.userType = $rootScope.pathParams.userType;
                if ($scope.userType == 'seller') {
                    $scope.getStoreInfo();
                } else {
                    $scope.getFactorInfo();
                }

            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);