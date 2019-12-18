(function (angular, undefined) {

    var model = 'my';
    var entity = 'coupon';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.couponId = "";
        //获取卡券
        $scope.getCouponList = function () {
            var url = window.basePath;
            if (window.isEmpty($rootScope.pathParams.sellerId)) {
                url += '/crm/Coupon/queryMyCoupon?indexNum=' + window.indexNum + '&pageNo=' + window.pageNo + '&pageSize=' + window.pageSize;
            } else {
                url += '/crm/Coupon/queryStoreCoupon?sellerId=' + $rootScope.pathParams.sellerId + '&indexNum=' + window.indexNum + '&pageNo=' + window.pageNo + '&pageSize=' + window.pageSize;
            }
            $http.get(url).success(function (re) {
                $.each(re.content.couponList, function (k, v) {
                    $scope.couponList.push(v);
                })
                $scope.totalPage = re.content.totalPage;
                $scope.totalNumber = re.content.totalNum;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
                if (!window.isEmpty(getCookie("___MEMBER_TOKEN")) && !window.isEmpty($rootScope.pathParams.sellerId)) {
                    $scope.getStoreCouponReceive();
                }
            })
        }
        $scope.scrollEvent = function () {
            if ($('#moreButton').size() == 0) {
                return;
            }
            var loadNextPage = function () {
                window.stopScroll--;
                if (window.stopScroll > 0) {
                    return;
                }
                if ($scope.totalPage <= window.pageNo) {
                    return;
                }
                $scope.more();
            }
            if ($('#moreButton').offset().top < ($(window).scrollTop() + $(window).height())) {
                window.stopScroll || (window.stopScroll = 0);
                window.stopScroll++;
                loadNextPage.delay(0.5);
            }
        }

        $scope.isSellerName = function (sellerName, sellerId) {
            if (sellerId == '-1')
                return "普惠生活平台";
            else
                return sellerName;
        }

        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('.overflowPC').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.more = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getCouponList();
        };
        //获取会员在该店铺已经领取的卡券
        $scope.getStoreCouponReceive = function () {
            var url = window.basePath + '/crm/Coupon/queryStoreCouponReceive?sellerId=' + $rootScope.pathParams.sellerId;
            $http.get(url).success(function (re) {
                $scope.couponReceiveList = re.content.items;
                $scope.getList();
            })
        }

        //是否领取卡券面板
        $scope.receivce = function (isReceivce, id) {
            if (!isReceivce && !window.isEmpty($rootScope.pathParams.sellerId)) {
                $scope.receivcePanel = true;
                $scope.couponId = id;
            }
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
            //if (count == $scope.totalNumber) {
            //    malert("暂无可领取卡券");
            //    //if (!window.isEmpty(getCookie("___MEMBER_TOKEN"))) {
            //    //    malert("您已领取完该商家的卡券");
            //    //}else{
            //    //    malert("该商店暂无卡券");
            //    //}
            //}

        }

        //领取卡券
        $scope.addMemberCoupon = function () {
            if (!window.isEmpty($scope.couponId)) {
                var url = window.basePath + '/crm/Coupon/addMemberCoupon?couponId=' + $scope.couponId;
                $http.get(url).success(function () {
                    $scope.receivcePanel = false;
                    $scope.getStoreCouponReceive();
                })
            }
        }

        $scope.getQrcode=function(couponNo){
            $scope.setShowQrCode();
            var qr = qrcode(10, 'H');
            qr.addData(couponNo);
            qr.make();
            $("#showQrCode").html(qr.createImgTag());
            var width = $(window).width()-50+'px';
            $("#showQrCode>img").css({
                'width':width,
                'height':width
            })
        }

        $scope.setShowQrCode=function(){
            $scope.showQrCode = !$scope.showQrCode;
        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                $scope.pageName = "";
                if (window.isEmpty($rootScope.pathParams.sellerId)) {
                    $scope.pageName = "我的卡券";
                } else {
                    $scope.pageName = "商家卡券";
                }
                $scope.showQrCode=false;
                window.setWindowTitle($rootScope, $scope.pageName);
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.couponList = [];
                $scope.getCouponList();
                $scope.addScrollEvent();

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