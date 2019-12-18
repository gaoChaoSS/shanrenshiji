(function (angular, undefined) {

    var model = 'store';
    var entity = 'storePhone';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.errorOpen = false;

        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.storeInfo = re.content;
                $scope.phone = $scope.storeInfo.phone;
            });
        }
        $scope.isOK = function(){
            if (/^1[34578]{1}\d{9}$/.test($scope.phone)) {
                $scope.updateSellerAddress();
            }else {
                malert('请填写正确的手机号!');
            }
        }
        $scope.updateSellerAddress = function () {
            if (!window.isEmpty($scope.phone)) {
                    var url = window.basePath+'/account/Seller/updateSellerInfo';
                    var data = {
                        field: 'phone',
                        content: $scope.phone
                    };
                    $http.post(url, data).success(function () {
                        malert("修改成功!");
                        $rootScope.goPage('/store/sellerInfo');
                    });
                }else{
                malert('请填写您的手机号!');
            }

        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '店铺联系方式');
                $scope.userType = $rootScope.pathParams.userType;
                    $scope.getStoreInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);