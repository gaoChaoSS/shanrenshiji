(function (angular, undefined) {

    var model = 'my';
    var entity = 'coupon';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';
        $scope.myLeftNavigation = '/yzxfMall_page/temp_new/myLeftNavigation.html';
        $scope.isHaveCoupon=true;

        //获取卡券
        $scope.queryCurrentList = function () {
            if(!window.isEmpty($rootScope.dataPage)&&$scope.pageGo>$rootScope.dataPage.totalPage){
                return;
            }
            $scope.couponList = [];
            var url = window.basePath + '/crm/Coupon/queryMyCoupon?indexNum=' + $rootScope.indexNum + '&pageNo=' + $rootScope.pageNo + '&pageSize=' + $rootScope.pageSize;
            $http.get(url).success(function (re) {
                $scope.couponList=re.content.couponList;
                if(re.content.couponList.length==0){
                    $scope.isHaveCoupon=false;
                }
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

        $scope.isSellerName = function (sellerName, sellerId) {
            if (sellerId == '-1')
                return "普惠生活平台";
            else
                return sellerName;
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '我的卡券');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);
                $scope.indexTypeIsShow=false;
                $scope.titleText="coupon";
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $rootScope.pageSize = 9;
                $scope.isLoadMore = true;
                $scope.couponList = [];
                $scope.queryCurrentList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);