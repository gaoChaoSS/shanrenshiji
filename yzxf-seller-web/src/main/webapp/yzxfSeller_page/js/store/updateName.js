(function (angular, undefined) {

    var model = 'store';
    var entity = 'updateName';
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
            $scope.factorId = getCookie("_factor_id");
            var url = window.basePath + '/account/Factor/getFactorInfo?factorId='+$scope.factorId;
            $http.get(url).success(function (re) {
                $scope.factorInfo = re.content;
            });
        }
        $scope.submitSellerForm = function () {
            if (!window.isEmpty($scope.storeInfo.name)) {
                var url = window.basePath + '/account/Seller/updateSellerInfo';
                var data = {
                    field:'name',
                    content: $scope.storeInfo.name
                };
                $http.post(url, data).success(function () {
                    malert("修改成功");
                    $rootScope.goBack();
                })
            }else{
                malert("请填写商家名字");
            }
        }
        $scope.submitFactorForm = function () {
            if (!window.isEmpty($scope.factorInfo.name)) {
                var url = window.basePath + '/account/Factor/updateFactorInfo';
                var data = {
                    field:'name',
                    content: $scope.factorInfo.name,
                    factorId:getCookie('_factor_id')
                };
                $http.post(url, data).success(function () {
                    malert("修改成功");
                    $rootScope.goBack();
                })
            }else{
                malert("请填写服务中心名字");
            }
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {

                $scope.userType=$rootScope.pathParams.userType;
                if($scope.userType=='seller'){
                    window.setWindowTitle($rootScope, '商家名字');
                    $scope.getStoreInfo();
                }else{
                    window.setWindowTitle($rootScope, '服务中心名字');
                    $scope.getFactorInfo();
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);