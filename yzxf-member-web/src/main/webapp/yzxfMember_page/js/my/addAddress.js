(function (angular, undefined) {

    var model = 'my';
    var entity = 'addAddress';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.check = false;
        $scope.checkMan = true;

        $scope.area="";
        $scope.areaValue="";

        //完成按钮判断
        $scope.nextBtn = function () {
            $scope.check = false;
            if (/^1[34578]{1}\d{9}$/.test($scope.phoneNumber) && !window.isEmpty($scope.consignee)
                && !window.isEmpty($scope.address) && !window.isEmpty($scope.postalcode)) {
                $scope.check = true;
            }
        }
        $scope.getAddressInfo = function () {
            if (!window.isEmpty($rootScope.pathParams.addressId)) {
                var url = window.basePath + '/crm/MemberAddress/getMemberAddress?_id=' + $rootScope.pathParams.addressId;
                $http.get(url).success(function (re) {

                    if($rootScope['$$addAddressArea']!=null){
                        //re.content.items[0].area=null;
                        //$scope.area=null;
                        $scope.area=$rootScope['$$addAddressArea'].locationArea;
                        $scope.areaValue=$rootScope['$$addAddressArea'].locationAllValue;
                    }else{
                        $scope.consignee = re.content.items[0].name;
                        $scope.checkMan = (re.content.items[0].gender == '男士');
                        $scope.phoneNumber = re.content.items[0].phone;
                        $scope.address = re.content.items[0].address;
                        $scope.postalcode = re.content.items[0].postcode;
                        $scope.area = re.content.items[0].area;
                    }
                })
            }
            //alert($scope.area+","+$scope.areaValue);
            if($rootScope['$$addAddressArea']!=null){
                $scope.area=$rootScope['$$addAddressArea'].locationArea;
                $scope.areaValue=$rootScope['$$addAddressArea'].locationAllValue;
                //alert($scope.area+","+$scope.areaValue);
            }
        }
        $scope.submitForm = function () {
            $scope.nextBtn();
            var url = window.basePath + '/crm/MemberAddress/addAddress';
            var data = {
                consignee: $scope.consignee,
                gender: $scope.checkMan ? '男士' : '女士',
                phone: $scope.phoneNumber,
                address: $scope.address,
                area: $scope.area,
                areaValue: $scope.areaValue,
                postalcode: $scope.postalcode
            };
            if ($rootScope.pathParams.addressId) {
                //data.push("_id", $rootScope.pathParams.addressId);
                data._id=$rootScope.pathParams.addressId;
            }
            $http.post(url, data).success(function () {
                $rootScope.goBack();
            })
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                //$rootScope.windowTitle = '个人中心';
                window.setWindowTitle($rootScope, '新建收货地址');
                $scope.getAddressInfo();

            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);