/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'sellerInfo';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //获取商家信息
        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/Seller/querySeller';
            $http.get(url).success(function (re) {
                $scope.storeInfo = re.content;
                //设置营业时间
                $scope.businessTime='';
                if(!window.isEmpty($scope.storeInfo.openWeek)){
                    $scope.businessTime+= '周'+$scope.storeInfo.openWeek;
                }
                if(!window.isEmpty($scope.storeInfo.openTime)){
                    $scope.businessTime+= ' '+$scope.storeInfo.openTime+':00 - '+$scope.storeInfo.closeTime+':00';
                }
                //临时存放
                $scope.imgMore={
                    bankImg: $scope.storeInfo.bankImg.split("_"),
                    contractImg:$scope.storeInfo.contractImg.split("_"),
                    doorImg:$scope.storeInfo.doorImg.split("_"),
                };
            });
        };

        //初始化地图
        $scope.initMap = function () {
            $scope.showMap=true;
            if(window.isEmpty($scope.storeInfo.latitude) || window.isEmpty($scope.storeInfo.longitude)){
                return;
            }
            $scope.center = new qq.maps.LatLng($scope.storeInfo.latitude, $scope.storeInfo.longitude);
            $scope.map = new qq.maps.Map(document.getElementById("container"), {
                center: $scope.center,
                zoom: 18,
            });
            var marker = new qq.maps.Marker({
                position: $scope.center,
                map: $scope.map
            });
            $scope.markerCluster = new qq.maps.MarkerCluster({
                map: $scope.map,
                minimumClusterSize: 2, //默认2
                markers: [],
                zoomOnClick: true, //默认为true
                gridSize: 60, //默认60
                averageCenter: true, //默认false
                maxZoom: 16 //默认18
            });
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商户信息');
                $scope.getStoreInfo();
                $scope.showMap=false;
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
    //当图片加载失败时,显示的404图片
    window.app.register.directive('errSrc', function () {
        return {
            link: function (scope, element, attrs) {
                element.bind('error', function () {
                    if (attrs.src != attrs.errSrc) {
                        attrs.$set('src', attrs.errSrc);
                    }
                });
            }
        }
    });
})(angular);