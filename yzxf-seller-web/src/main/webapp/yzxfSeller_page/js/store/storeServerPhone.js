(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeServerPhone';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.errorOpen = false;

        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.storeInfo = re.content;
                $scope.serverPhone = $scope.storeInfo.serverPhone;
            });
        }
        $scope.isOK = function(){
            if (/(^(\d{2,4}[-_－—]?)?\d{3,8}([-_－—]?\d{3,8})?([-_－—]?\d{1,7})?$)|(^0?1[35]\d{9}$)/.test($scope.serverPhone)) {
                $scope.updateSellerAddress();
            }else {
                malert('请填写正确的号码!');
            }
        }
        $scope.updateSellerAddress = function () {
            if (!window.isEmpty($scope.serverPhone)) {
                var url = window.basePath+'/account/Seller/updateSellerInfo';
                var data = {
                    field: 'serverPhone',
                    content: $scope.serverPhone
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
                window.setWindowTitle($rootScope, '客服电话');
                $scope.getStoreInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);