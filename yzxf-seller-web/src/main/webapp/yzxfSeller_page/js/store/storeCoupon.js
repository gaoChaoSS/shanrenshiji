/**
 * Created by zq2014 on 16/12/26.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeCoupon';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.EventStatus = false;
        $scope.curDate = new Date().getTime();
        //获取卡券
        $scope.getCouponList = function () {
            var url = window.basePath + '/crm/Coupon/getStoreCoupon?indexNum=' + window.indexNum + "&pageNo=" + window.pageNo + "&pageSize=" + window.pageSize;
            $http.get(url).success(function (re) {
                $.each(re.content.couponList, function (k, v) {
                    $scope.couponList.push(v);
                })
                $scope.totalPage = re.content.totalPage;
                $scope.totalNumber = re.content.totalNum;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
                }
            })
        }
        $scope.iconImgUrlSeller = function () {
            return $scope.couponList != null && $scope.couponList.icon != null ? ('/s_img/icon.jpg?_id=' + $scope.couponList.icon + '&wh=300_300') : '/yzxfSeller_page/img/notImg02.jpg';
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

        $scope.delCoupon=function(_id,index){
            if(window.isEmpty(_id)){
                malert("尚未获取到卡券");
                return;
            }
            var url = window.basePath + '/crm/Coupon/delCoupon';
            $http.post(url,{_id:_id}).success(function () {
                $scope.couponList.splice(index,1);
                malert("删除成功");
            })
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '店铺卡券');
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.isLoadMore = true;
                $scope.checkRightName = false;
                $scope.couponList = [];
                $scope.addScrollEvent();
                $scope.getCouponList();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);