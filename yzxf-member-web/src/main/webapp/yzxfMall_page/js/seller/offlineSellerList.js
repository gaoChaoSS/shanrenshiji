(function (angular, undefined) {

    var model = 'seller';
    var entity = 'offlineSellerList';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.navCarousel = '/yzxfMall_page/temp_new/navCarousel.html';
        $scope.indexMain = '/yzxfMall_page/temp_new/indexMain.html';

        $scope.indexTypeIsShow = true;

        $scope.imgList = [
            "/yzxfMall_page/img/banner1.jpg",
            "/yzxfMall_page/img/banner2.jpg",
            "/yzxfMall_page/img/banner3.jpg"
        ]

        $scope.imgCarousel = function () {
            if(!window.isEmpty($scope.timeCount)){
                $interval.cancel($scope.timeCount);
            }
            var count = $scope.imgList.length;
            $scope.timeCount = $interval(function () {
                if ($scope.imgCur + 1 >= count) {
                    $scope.imgCur = 0;
                } else {
                    $scope.imgCur++;
                }
                if ($location.path().indexOf("offlineSellerList") ==-1) {
                    $interval.cancel($scope.timeCount);
                }
            }, 5000);
        }
        $scope.selectImg = function (index) {
            $scope.imgCur = index;
            $interval.cancel($scope.timeCount);
            $scope.imgCarousel();
        }
        $scope.mouseNavUp=function(index){
            $scope.txtOrImg=index;
        }
        $scope.mouseNavDown=function(){
            $scope.txtOrImg=-1;
        }
        $(".goTop").click(function(){
            window.scrollTo(0,0);
        });
        $(window).scroll(function(event){
            var wScrollY = window.scrollY; // 当前滚动条位置
            if (wScrollY < 480) {
                $('.indexLeftNavDiv').hide();
                $('.indexRightNavDiv').hide();
            }else{
                $('.indexLeftNavDiv').show()
                $('.indexRightNavDiv').show();
            }
        });
        $scope.queryCurrentList = function () {
            var url = window.basePath + "/account/Seller/queryTypeForMall";
            $http.get(url).success(function (re) {
                $scope.operate = re.content.items;
                var selectedValue = '';
                $scope.totalNumCount=0;
                for(var i = 0 ;i<$scope.operate.length;i++){
                    if($scope.operate[i].level==1){
                        selectedValue="_"+$scope.operate[i].value+"_";
                    }else{
                        selectedValue=$scope.operate[i].pvalue+"_"+$scope.operate[i].value+"_";
                    }
                    $scope.getSellerList(selectedValue,i);
                }
            })
        }
        //获取商家列表
        $scope.getSellerList = function (selectedValue,index) {
            if(!window.isEmpty($rootScope.pathParams.selectedIndex)){
                $scope.selectedIndex=$rootScope.pathParams.selectedIndex;
                $rootScope.pathParams.selectedIndex="";
                $location.path("/seller/offlineSellerList");
            }
            if ($location.path() != '/seller/offlineSellerList') {
                return;
            }

            if ($rootScope['$$storeArea'] != null) {
                $scope.locationName = $rootScope['$$storeArea'].locationSelected;
                $scope.selectedValue = $rootScope['$$storeArea'].locationAreaValue;
                $scope.selectedPValue = $rootScope['$$storeArea'].locationAreaPValue;
            }
            var url = window.basePath + "/account/Seller/querySellerList?selectedIndex=2";
            url += "&selectType=" + selectedValue;
            if (!window.isEmpty($rootScope.showArea) && !window.isEmpty($rootScope.showArea.areaValue)) {
                url += "&areaValue=" + $rootScope.showArea.areaValue;
            }
            url+= "&indexNum=0&pageNo=1&pageSize=10";
            $http.get(url).success(function (re) {
                $scope.operate[index].sellerList=re.content.sellerList;
                $scope.totalNumCount+= parseInt($rootScope.isNullZero(re.content.totalNum));
            });
            //$rootScope.clearLocation();

        }



        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '推荐商家');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                $('.indexLeftNavDiv').hide();
                $('.indexRightNavDiv').hide();
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 5;
                $scope.sellerList = [];
                $rootScope.pType = '';
                $scope.imgCur = 0;
                $scope.imgCarousel();
                $scope.curLocation="offlineSellerList";
                $scope.queryCurrentList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);