(function (angular, undefined) {

    var model = 'seller';
    var entity = 'searchSellerList';
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
                $('.indexLeftNavDiv').show();
                $('.indexRightNavDiv').show();
            }
        });

        $scope.pageNumber1 = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSearch();

        }
        $scope.pageNext1 = function (num) {
            $rootScope.pageNo = parseInt(num)+parseInt($rootScope.pageNo);
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSearch();
        }
        $scope.pageGoFun1 = function (num) {
            if(window.isEmpty(num) || num>$rootScope.dataPage.totalPage){
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSearch();
        }
        $scope.setPageNo1 = function (num){
            $rootScope.pageNo=num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getSearch();
        }

        $scope.getSearch = function(){
            if(window.isEmpty($rootScope.searchSOP) || window.isEmpty($rootScope.pageNo)){
                return;
            }
            var url = window.basePath + "/account/Seller/querySellerList?searchSOP="+$rootScope.searchSOP+"&pageNo="+$rootScope.pageNo+"&pageSize=25";
            $http.get(url).success(function(re){
                $rootScope.dataPage = re.content;
                //$scope.filter.pageNo = $scope.dataPage.pageNo;
                $rootScope.dataPage.$$pageList = [];
                var start = $rootScope.dataPage.pageNo - 3;
                var end = $rootScope.dataPage.pageNo + 4;

                start = start < 1 ? 1 : start;
                end = end > $rootScope.dataPage.totalPage ? $rootScope.dataPage.totalPage : end;

                for (var i = start; i <= end; i++) {
                    $rootScope.dataPage.$$pageList.push(i);
                }
            })
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '搜索商家');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $rootScope.pageSize = 25;
                $scope.sellerList = [];
                $scope.imgCur = 0;
                $scope.imgCarousel();
                $scope.getSearch();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);