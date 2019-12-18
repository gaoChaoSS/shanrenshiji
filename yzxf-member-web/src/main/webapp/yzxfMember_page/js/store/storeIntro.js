(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeIntro';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location,$timeout, $http, $element, $compile) {
        $scope.getSellerIntro = function(){
            var url = window.basePath + '/account/StoreInfo/getSellerIntroById?sellerId='+$rootScope.pathParams.sellerId;
            $http.get(url).success(function (re) {
                $scope.intro = re.content;
                if(!window.isEmpty($scope.intro.openWeek)){
                    $scope.businessTime= '周'+$scope.intro.openWeek+' '+$scope.intro.openTime+':00 - '+$scope.intro.closeTime+':00';
                }else{
                    $scope.businessTime='暂无';
                }
                //处理商家背景图片
                if(window.isEmpty($scope.intro.icon) && window.isEmpty($scope.intro.doorImg)){
                    $scope.showBk="background:#ccc";
                }else{
                    $scope.showBk="background-image:url('"+$rootScope.iconImg($rootScope.getSellerIcon($scope.intro.icon,$scope.intro.doorImg))+"')";
                }
                if(!window.isEmpty($scope.intro.doorImg)){
                    $scope.doorImgList = $scope.intro.doorImg.split("_");
                }
                $scope.initMap();
                $scope.setSellerNameScroll();
            });
        };

        $scope.setSellerNameScroll=function(){
            var text = $("#sellerNameText").text();
            if($("#sellerNameText").text()=='暂无(暂无)'){
                $timeout(function(){
                    $scope.setSellerNameScroll();
                },1000);
                return;
            }
            var max=285;
            var textWidth = $("#sellerNameText").css("width");
            textWidth = parseInt(textWidth.substring(0,textWidth.length-2));
            if(window.isMobile()){
                var winWidth = $(window).width();
                max = (winWidth - 105) * 0.75;
            }
            $scope.isScrollSellerName=textWidth>max;
            if($scope.isScrollSellerName){
                $("#marquee").start();
            }
        };

        $scope.getQrcode=function(){
            $scope.myCard=!$scope.myCard;
            var qr = qrcode(10, 'H');
            qr.addData($location.absUrl().split("yzxfMember")[0]+"yzxfMember/store/storeInfo/sellerId/"+$rootScope.pathParams.sellerId);
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").addClass("iconImg2");
        };

        //初始化地图
        $scope.goMap = function (address) {
            // if(window.isEmpty($scope.intro.latitude) || window.isEmpty($scope.intro.longitude)){
            //     return;
            // }
            // $scope.center = new qq.maps.LatLng($scope.intro.latitude, $scope.intro.longitude);
            // $scope.map = new qq.maps.Map(document.getElementById("container"), {
            //     center: $scope.center,
            //     zoom: 18,
            // });
            // var marker = new qq.maps.Marker({
            //     position: $scope.center,
            //     map: $scope.map
            // });
            // $scope.markerCluster = new qq.maps.MarkerCluster({
            //     map: $scope.map,
            //     minimumClusterSize: 2, //默认2
            //     markers: [],
            //     zoomOnClick: true, //默认为true
            //     gridSize: 60, //默认60
            //     averageCenter: true, //默认false
            //     maxZoom: 16 //默认18
            // });

            window.initWeChatConfig($rootScope, $scope, $http, function () {
                wx.openLocation({
                    latitude: $scope.intro.latitude,//目的地latitude
                    longitude: $scope.intro.longitude,//目的地longitude
                    name: address,
                    address:address,
                    scale: 15//地图缩放大小，可根据情况具体调整
                });

                wx.error(function (res) {
                    alert(JSON.stringify(res));
                });
            });
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商家简介');
                $rootScope.isLoginPage = true;
                $scope.getSellerIntro();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
    //当图片加载失败时,显示的404图片
    window.app.register.directive('errSrc', function() {
        return {
            link: function(scope, element, attrs) {
                element.bind('error', function() {
                    if (attrs.src != attrs.errSrc) {
                        attrs.$set('src', attrs.errSrc);
                    }
                });
            }
        }

    });
})(angular);