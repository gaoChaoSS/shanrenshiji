(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeLegalPerson';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.errorOpen = false;

        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.storeInfo = re.content;
                $scope.legalPerson = $scope.storeInfo.legalPerson;
            });
        }
        $scope.isOK = function(){
            if (/[Α-￥]{2,10}/.test($scope.legalPerson)) {
                $scope.updateSellerAddress();
            }else {
                malert('请填写2~10位的中文姓名!');
            }
        }
        $scope.updateSellerAddress = function () {
            if (!window.isEmpty($scope.legalPerson)) {
                var url = window.basePath+'/account/Seller/updateSellerInfo';
                var data = {
                    field: 'legalPerson',
                    content: $scope.legalPerson
                };
                $http.post(url, data).success(function () {
                    malert("修改成功!");
                    $rootScope.goPage('/store/sellerInfo');
                });
            }else{
                malert('请填写2~10位的中文姓名!');
            }

        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '法人信息');
                $scope.getStoreInfo();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);