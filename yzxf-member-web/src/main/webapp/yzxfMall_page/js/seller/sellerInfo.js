(function (angular, undefined) {

    var model = 'seller';
    var entity = 'sellerInfo';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $interval, $location, $http, $element, $compile) {
        $scope.mallHead = '/yzxfMall_page/temp_new/mallHead.html';
        $scope.indexNavigation = '/yzxfMall_page/temp_new/navigation.html';
        $scope.mallBottom = '/yzxfMall_page/temp_new/mallBottom.html';

        $scope.isClick='XX';
        $scope.isCollection=false;
        $scope.goodsAsComment='goods';

        $scope.clearFun=function(){
            $rootScope.indexNum = 0;
            $rootScope.pageNo = 1;
            $rootScope.pageSize = 10;
        }
        $scope.pageNumberC = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getGoodsList();

        }
        $scope.pageNextC = function (num) {
            $rootScope.pageNo += num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getGoodsList();
        }
        $scope.pageGoFunC = function (num) {
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            if(num>$rootScope.dataPage.totalPage){
                malert('跳转页面超过上限!');
            }else{
                $scope.getGoodsList();
            }
        }
        $scope.setPageNoC = function (num){
            $rootScope.pageNo=num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getGoodsList();
        }
        $scope.pageNumberS = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getStoreCommentList();

        }
        $scope.pageNextS = function (num) {
            $rootScope.pageNo += num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getStoreCommentList();
        }
        $scope.pageGoFunS = function (num) {
            $rootScope.pageNo = num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            if(num>$rootScope.dataPage.totalPage){
                malert('跳转页面超过上限!');
            }else{
                $scope.getStoreCommentList();
            }
        }
        $scope.setPageNoS = function (num){
            $rootScope.pageNo=num;
            $rootScope.indexNum = $rootScope.pageNo-1;
            $scope.getStoreCommentList();
        }


        $scope.hoverUp=function(index){
            $scope.isGoodsHover=index;
        }
        $scope.hoverDown=function(){
            $scope.isGoodsHover='';
        }
        $scope.formatMobile=function(mobile){
            return mobile.substr(0, 3) + "****" + mobile.substr(7)
        }
        //商品信息
        $scope.getGoodsList = function () {
                $scope.goodsList = [];
            var url = window.basePath + '/account/StoreInfo/getGoodsInfoById?sellerId=' + $scope.sellerId + "&indexNum=" + $rootScope.indexNum + "&pageNo=" + $rootScope.pageNo + "&pageSize=" + $rootScope.pageSize;
            $http.get(url).success(function (re) {
                $.each(re.content.goodsList, function (k, v) {
                    $scope.goodsList.push(v);
                })
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
        $scope.pension = function(integralRate,salePrice){
            return $rootScope.getMoney(integralRate*salePrice/100/2);
        }

        //店铺评价
        $scope.getStoreCommentList = function (type) {
                $scope.commentList = [];
            $scope.scrollType = 'comment';
            var url = window.basePath + '/account/StoreInfo/getStoreCommentList?sellerId=' + $scope.sellerId + "&indexNum=" + $rootScope.indexNum + "&pageNo=" + $rootScope.pageNo + "&pageSize=" + $rootScope.pageSize;
            ;
            $http.get(url).success(function (re) {
                $.each(re.content.commentList, function (k, v) {
                    $scope.commentList.push(v);
                })
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
        //店铺信息
        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/StoreInfo/getStoreInfoById?sellerId=' + $scope.sellerId;
            $http.get(url).success(function (re) {
                $scope.sellerInfo = re.content.sellerInfo;
                $scope.isCollection = re.content.isCollection;
                $scope.isCoupon = re.content.isCoupon;
            })
        }

        //星级评价
        $scope.starNo = function (number) {
            var str = "";
            for (var no = 0; no < number; no++) {
                str += '★';
            }
            return str;
        }

        $scope.setCouponPageNo=function(num){
            $scope.couponPageNo+=num;
            if($scope.couponPageNo<1 || $scope.couponPageNo>$scope.couponTotalPage){
                $scope.couponPageNo-=num;
                return;
            }
            $scope.getCouponList();
        };

        //获取所有卡券
        $scope.getCouponList = function () {
            var url = window.basePath +'/crm/Coupon/queryStoreCoupon?sellerId=' + $scope.sellerId + '&pageNo='+$scope.couponPageNo+'&pageSize=5';
            $http.get(url).success(function (re) {
                $scope.couponList=re.content.couponList;
                $scope.couponTotalPage=re.content.totalPage;
                //$.each(re.content.couponList, function (k, v) {
                //    $scope.couponList.push(v);
                //})
                //$scope.totalPage = re.content.totalPage;
                //$scope.totalNumberCoupon = re.content.totalNum;
                //if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                //    $scope.isLoadMore = false;
                //}
                if (!window.isEmpty(getCookie("___MEMBER_TOKEN"))) {
                    $scope.getStoreCouponReceive();
                }
            })
        }
        //添加或删除收藏店铺
        $scope.addOrDelStoreCollection = function () {
            var url = window.basePath + '/crm/MemberCollection/addOrDelStoreCollection?sellerId=' + $scope.sellerId;
            $http.get(url).success(function () {
                $scope.isCollection = !$scope.isCollection;
            })
        }
        //获取会员在该店铺已经领取的卡券
        $scope.getStoreCouponReceive = function () {
            var url = window.basePath + '/crm/Coupon/queryStoreCouponReceive?sellerId=' + $scope.sellerId;
            $http.get(url).success(function (re) {
                $scope.couponReceiveList = re.content.items;
                $scope.getList();
            })
        }

        //判断会员在该店铺已经领取的卡券
        $scope.getList = function () {
            var count = 0;
            //var count2 = 0;
            var receMap = {};
            for (var index=0;index<$scope.couponReceiveList.length;index++) {
                var _id = $scope.couponReceiveList[index]._id;
                receMap[_id] = true;
                count++;
            }
            for (var index2=0;index2<$scope.couponList.length;index2++) {
                var isR = receMap[$scope.couponList[index2]._id];
                $scope.couponList[index2].$$isReceivce = isR == null ? false : isR;
                //count2++;
            }
        }
        //暂时当前店铺最新活动
        $scope.getSellerEventList=function(){
            var url = '/s_user/api/account/Seller/getStoreEvent?sellerId=' + $scope.sellerId + "&indexNum=0&pageNo=1&pageSize=2 &isGoing=true";
            $http.get(url).success(function (re) {
                $.each(re.content.eventList, function (k, v) {
                    $scope.eventList.push(v);
                })
            })
        }
        //领取卡券
        $scope.addMemberCoupon = function (id) {
            if (!window.isEmpty(id)) {
                var url = window.basePath + '/crm/Coupon/addMemberCoupon?couponId=' + id;
                $http.get(url).success(function () {
                    $scope.getStoreCouponReceive();
                    malert('领取成功!');
                })
            }
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商家详情');
                initTypeGrid($rootScope, $scope, $http , $interval , $location);

                $scope.sellerId = $rootScope.pathParams.sellerId;
                //$scope.sellerId = 'S-000006';
                $rootScope.indexNum = 0;
                $rootScope.pageNo = 1;
                $rootScope.pageSize = 10;
                $scope.couponPageNo=1;
                $scope.couponList = [];
                $scope.goodsList = [];
                $scope.getStoreInfo();
                $scope.getGoodsList();
                $scope.getCouponList();
                if (!window.isEmpty(getCookie("___MEMBER_TOKEN"))) {
                    $scope.getStoreCouponReceive();
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);