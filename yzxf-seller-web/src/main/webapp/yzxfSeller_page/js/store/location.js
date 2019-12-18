(function (angular, undefined) {

    var model = 'store';
    var entity = 'location';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        var init = function () {
            //地区ID
            $scope.locationId = null;
            $scope.locationName = null;
            //地区名字
            $scope.name1 = "";
            $scope.name2 = "";
            $scope.name3 = "";
            $scope.name4 = "";

            //地区value
            $scope.areaValue = "";

            //地区pvalue
            $scope.pValue = "";

            //地区集合
            $scope.list1 = null;
            $scope.list2 = null;
            $scope.list3 = null;
            $scope.list4 = null;

            $scope.id1 = null;
            $scope.id2 = null;
            $scope.id3 = null;
            $scope.id4 = null;
            //是否有四级选项
            //$scope.isList4 = false;
        }
        //获取地理位置
        $scope.getLocation = function (_id, type, name, value, pValue) {
            var url = window.basePath + '/crm/Member/getLocation?pid=' + _id;
            $http.get(url).success(function (re) {
                $scope.areaValue = value;
                $scope.pValue = pValue;
                $scope.locationId = _id;
                $scope.locationName = name;

                if (type == 1) {
                    $scope.list1 = re.content.items;
                }
                if (type == 2) {
                    $scope.list2 = re.content.items;
                    $scope.id1 = _id;
                    $scope.name1 = name;

                    $scope.list3 = null;
                    $scope.list4 = null;

                    $scope.id2 = null;
                    $scope.id3 = null;
                    $scope.id4 = null;

                    $scope.name2 = "";
                    $scope.name3 = "";
                    $scope.name4 = "";
                }
                if (type == 3) {
                    $scope.list3 = re.content.items;
                    $scope.id2 = _id;
                    $scope.name2 = name;

                    $scope.list4 = null;

                    $scope.id3 = null;
                    $scope.id4 = null;

                    $scope.name3 = "";
                    $scope.name4 = "";
                }
                if (type == 4) {
                    $scope.list4 = re.content.items;
                    //if ($scope.list4.length > 0) {
                    //    $scope.isList4 = true;
                    //} else {
                    //    $scope.isList4 = false;
                    //}
                    $scope.id3 = _id;
                    $scope.name3 = name;

                    $scope.name4 = "";

                    $scope.id4 = null;
                }
            })
        }
        $scope.getIndex = function (_id, name, value, pValue) {
            $scope.id4 = _id;
            $scope.name4 = name;
            $scope.areaValue = value;
            $scope.pValue = pValue;

            $scope.locationId = _id;
            $scope.locationName = name;
        }
        $scope.isGoPage = function () {
            var area = $scope.name1 + $scope.name2 + $scope.name3 + $scope.name4;
            if (window.isEmpty(area)) {
                malert("请选择地址!");
                return;
            }

            if (window.isEmpty($scope.name1) || window.isEmpty($scope.name2) ||
                window.isEmpty($scope.name3)) {
                malert("请至少选择三级地址");
                return;
            }

            if (window.isEmpty($scope.pValue)) {
                $scope.areaValue = "_" + $scope.areaValue + "_";
            } else {
                $scope.areaValue = $scope.pValue + "_" + $scope.areaValue + "_";
            }
            if(!window.isEmpty($rootScope.pathParams.userApply)){
                $rootScope['$$' + $rootScope.pathParams.userApply] = {
                    'locationSelected': $scope.locationName,
                    'locationArea': area,
                    'locationAllValue': $scope.areaValue//保存地址value时使用
                };
                $rootScope.goBack();
            }else if (!window.isEmpty($rootScope.pathParams.selectedArea)) {
                if($rootScope.pathParams.selectedArea == 'realNameArea'){
                    $rootScope['$$'+$rootScope.pathParams.selectedArea]={
                        locationArea:area,
                        locationAreaValue:$scope.areaValue
                    }
                    $rootScope.goBack();
                    return;
                }
                var url = window.basePath;
                if ($rootScope.pathParams.selectedArea == 'sellerInfo') {//商家
                    url += '/account/Seller/updateSellerArea';
                } else if ($rootScope.pathParams.selectedArea == 'factorInfo') {//发卡点地址
                    url += '/account/Factor/updateFactorArea?factorId=' + getCookie("_factor_id");
                }
                var data = {
                    area: area,
                    areaValue: $scope.areaValue
                };
                $http.post(url, data).success(function () {
                    malert("保存成功!");
                    $rootScope.goBack();
                });
            }


        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '选择城市');
                $rootScope.isIndex = false;

                init();

                $scope.getLocation(-1, 1);
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);