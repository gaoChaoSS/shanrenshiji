(function (angular, undefined) {
    window.app.register.controller('store_storeInfo_Ctrl', function ($rootScope, $scope, $location, $http, $interval, $element, $compile) {
        $scope.titleCheck = 1;
        //店铺ID
        $scope.sellerId = $rootScope.pathParams.sellerId;
        $scope.redIcon = false;
        $scope.isGoCoupon = false;

        $scope.productInclude="/yzxfMember_page/view/store/i_product.jsp";

        $scope.formatMobile=function(mobile){
            return mobile.substr(0, 3) + "****" + mobile.substr(7)
        }

        //卡券红点闪烁
        $scope.redIconCheck = function () {
            if (!window.isEmpty($scope.isGoCoupon) && $scope.isGoCoupon) {
                var time = $interval(function () {
                    $scope.redIcon = !$scope.redIcon;
                    if (!$scope.isGoCoupon) {
                        $interval.cancel(time);
                    }
                    if($location.path().substr(0,16)!='/store/storeInfo'){
                        $interval.cancel(time);
                    }
                }, 500);
            } else {
                $scope.redIcon = false;
            }
        }

        //店铺信息
        $scope.getStoreInfo = function () {
            var url = window.basePath + '/account/StoreInfo/getStoreInfoById?sellerId=' + $scope.sellerId;
            $http.get(url).success(function (re) {
                $scope.sellerInfo = re.content.sellerInfo;
                $scope.isCollection = re.content.isCollection;
                $scope.isCoupon = re.content.isCoupon;

                //处理商家背景图片
                if(window.isEmpty($scope.sellerInfo.icon) && window.isEmpty($scope.sellerInfo.doorImg)){
                    $scope.showBk="background:#ccc";
                }else{
                    $scope.showBk="background-image:url('"+$rootScope.iconImg($rootScope.getSellerIcon($scope.sellerInfo.icon,$scope.sellerInfo.doorImg))+"')";
                }

                $scope.getCouponList();
            })
        }
        //商品信息
        $scope.getGoodsList = function (type) {
            $scope.scrollType = 'goods';
            if (type == 'frist') {
                $scope.goodsList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isGoodsLoadMore = true;
            }
            var url = window.basePath + '/account/StoreInfo/getGoodsInfoById?sellerId=' + $scope.sellerId + "&indexNum=" + window.indexNum + "&pageNo=" + window.pageNo + "&pageSize=" + window.pageSize;
            $http.get(url).success(function (re) {
                $.each(re.content.goodsList, function (k, v) {
                    $scope.goodsList.push(v);
                })
                $scope.totalGoodsPage = re.content.totalPage;
                $scope.totalGoodsNumber = re.content.totalNum;
                if ($scope.totalGoodsPage != 0 && $scope.totalGoodsPage <= window.pageNo) {
                    $scope.isGoodsLoadMore = false;
                }
            })
        }
        //星级评价
        $scope.starNo = function (number) {
            var str = "";
            for (var no = 0; no < number; no++) {
                str += '★';
            }
            return str;
        };

        $scope.getOrderStatus=function(status){
            if(status>=6 && status<9){
                return '已申请退货';
            }else if(status==9){
                return '已退货';
            }
        };

        //店铺评价
        $scope.getStoreCommentList = function (type) {
            if (type == 'frist') {
                $scope.commentList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isCommentLoadMore = true;
            }
            $scope.scrollType = 'comment';
            var url = window.basePath + '/account/StoreInfo/getStoreCommentList?sellerId=' + $scope.sellerId + "&indexNum=" + window.indexNum + "&pageNo=" + window.pageNo + "&pageSize=" + window.pageSize;
            ;
            $http.get(url).success(function (re) {
                $.each(re.content.commentList, function (k, v) {
                    $scope.commentList.push(v);
                })
                $scope.totalCommentPage = re.content.totalPage;
                $scope.totalCommentNumber = re.content.totalNum;
                if ($scope.totalCommentPage != 0 && $scope.totalCommentPage <= window.pageNo) {
                    $scope.isCommentLoadMore = false;
                }
            })
        }
        $scope.scrollEvent = function () {
            var id = "";
            var totalPage = 0;
            if ($scope.scrollType == 'goods') {
                id = "moreGButton";
                totalPage = $scope.totalGoodsPage;
            } else if ($scope.scrollType == 'comment') {
                id = "moreCButton";
                totalPage = $scope.totalCommentPage;
            }
            else if ($scope.scrollType == 'Activity') {
                id = "moreAButton";
                totalPage = $scope.totalActivityPage;
            }
            if ($('#' + id).size() == 0) {
                return;
            }
            var loadNextPage = function () {
                window.stopScroll--;
                if (window.stopScroll > 0) {
                    return;
                }
                if (totalPage <= window.pageNo) {
                    return;
                }
                $("#" + id).click();
            }
            if ($('#' + id).offset().top < ($(window).scrollTop() + $(window).height())) {
                window.stopScroll || (window.stopScroll = 0);
                window.stopScroll++;
                loadNextPage.delay(0.5);
            }
        }

        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('#storeInfoScroll').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.moreGoods = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getGoodsList();
        }
        $scope.moreComment = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getStoreCommentList();
        }
        $scope.moreActivity = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.getActivity();
        }
        $scope.curDate = new Date().getTime();
        //获取活动
        $scope.getActivity = function (type) {
            if (type == 'frist') {
                $scope.eventList = [];
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isActivityLoadMore = true;
            }
            $scope.scrollType = 'Activity';
            var url = '/s_user/api/account/Seller/getStoreEvent?sellerId=' + $scope.sellerId + "&indexNum=" + window.indexNum + "&pageNo=" + window.pageNo + "&pageSize=" + window.pageSize+"&isGoing=true";
            $http.get(url).success(function (re) {
                $.each(re.content.eventList, function (k, v) {
                    $scope.eventList.push(v);
                })
                $scope.totalActivityPage = re.content.totalPage;
                $scope.totalActivityNumber = re.content.totalNum;
                if ($scope.totalActivityPage != 0 && $scope.totalActivityPage <= window.pageNo) {
                    $scope.isActivityLoadMore = false;
                }
                for (var index in $scope.eventList) {
                    if (!$scope.eventList[index].isGoing || $scope.eventList[index].endTime < $scope.curDate) {
                        $scope.eventList[index].$$goingCheck = '已结束';
                        $scope.eventList[index].$$goingNo = 2;
                    } else {
                        if ($scope.eventList[index].startTime > $scope.curDate) {
                            $scope.eventList[index].$$goingCheck = '未开始';
                            $scope.eventList[index].$$goingNo = 0;
                        } else {
                            $scope.eventList[index].$$goingCheck = '进行中';
                            $scope.eventList[index].$$goingNo = 1;
                        }
                    }
                }
            })
        }
        $scope.getIsEvent = function(){
            var url = '/s_user/api/account/Seller/getStoreEvent?sellerId=' + $scope.sellerId + "&indexNum=" + window.indexNum + "&pageNo=" + window.pageNo + "&pageSize=" + window.pageSize+"&isGoing=true";
            $http.get(url).success(function (re) {
              if(re.content.eventList.length>0){
                  $scope.sellerEventIcon=true;
              }
            })
        }
        //颜色判断
        $scope.colorCheck = function (num) {
            if (num == 0) {
                return 'triangleEventBig borderTopYellow';
            } else if (num == 1) {
                return 'triangleEventBig borderTopRed';
            } else if (num == 2) {
                return 'triangleEventBig borderTopGray';
            }
        }
        //添加或删除收藏店铺
        $scope.addOrDelStoreCollection = function () {
            var url = window.basePath + '/crm/MemberCollection/addOrDelStoreCollection?sellerId=' + $scope.sellerId;
            $http.get(url).success(function () {
                $scope.isCollection = !$scope.isCollection;
            })
        }

        $scope.isGoPage = function (id) {
            $rootScope.goPage('/my/coupon/sellerId/' + id);
            //if ($scope.isCoupon && !window.isEmpty(getCookie("___MEMBER_TOKEN"))) {
            //    if ($scope.isGoCoupon) {
            //        $rootScope.goPage('/my/coupon/sellerId/' + id);
            //    } else {
            //        malert("该商店暂无卡券!");
            //    }
            //} else {
            //    malert("该商店暂无卡券");
            //}

        }

        //获取所有卡券
        $scope.getCouponList = function () {
            var url = window.basePath;
            if (window.isEmpty($scope.sellerId)) {
                url += '/crm/Coupon/queryMyCoupon';
            } else {
                url += '/crm/Coupon/queryStoreCoupon?sellerId=' + $scope.sellerId + '&indexNum=' + window.indexNum + '&pageNo=' + window.pageNo + '&pageSize=' + window.pageSize;
            }
            $http.get(url).success(function (re) {
                $.each(re.content.couponList, function (k, v) {
                    $scope.couponList.push(v);
                })
                $scope.totalPage = re.content.totalPage;
                $scope.totalNumberCoupon = re.content.totalNum;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
                if (!window.isEmpty(getCookie("___MEMBER_TOKEN")) && !window.isEmpty($rootScope.pathParams.sellerId)) {
                    $scope.getStoreCouponReceive();
                }
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
            if ($scope.couponReceiveList.length == $scope.totalNumberCoupon) {//相等说明没有卡券可领取
                $scope.isGoCoupon = false;
                $scope.redIcon = false;
            } else {
                $scope.isGoCoupon = true;
                $scope.redIcon = true;
            }
            $scope.redIconCheck();
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '商家店面');
                //$rootScope.windowTitleHide = true;
                $rootScope.isLoginPage = true;
                $scope.isCollection = false;
                $scope.isGoCoupon = false;
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.isGoodsLoadMore = true;
                $scope.sellerEventIcon = false;
                $scope.couponList = [];
                $scope.goodsList = [];
                $scope.getStoreInfo();
                $scope.addScrollEvent();
                $scope.getGoodsList();
                $scope.getIsEvent();


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