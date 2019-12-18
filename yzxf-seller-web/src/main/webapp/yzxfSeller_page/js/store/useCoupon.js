/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'useCoupon';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.submitBtn = function () {
            if (!window.isEmpty($scope.couponNo)) {
                $scope.updateCouponSerial();
            } else {
                malert('请输入卡券');
            }
        }

        $scope.updateCouponSerial = function () {
            var url = window.basePath + '/crm/Coupon/updateCouponSerial';
            var date={
                couponNo:$scope.couponNo
            }
            $http.post(url,date).success(function(re){
                $rootScope.goPage('/store/couponVerification/couponId/'+re.content.couponId);
            });
        }
        $scope.queryUseCouponList = function(){
            var url = window.basePath + '/crm/Coupon/queryUseCouponList?indexNum=' + window.indexNum +'&pageNo='+window.pageNo+'&pageSize='+window.pageSize;
            $http.get(url).success(function(re){
                $.each(re.content.useCouponList, function (k, v) {
                    $scope.useCouponList.push(v);
                })
                $scope.totalPage = re.content.totalPage;
                $scope.totalNumber = re.content.totalNum;
                if ($scope.totalPage != 0 && $scope.totalPage <= window.pageNo) {
                    $scope.isLoadMore = false;
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

        $scope.addScrollEvent = function () {
            $('.overflowPC').unbind('scroll');
            $(window).unbind('scroll');
            $('#overflowCoupon').on('scroll', $scope.scrollEvent);
            $(window).on('scroll', $scope.scrollEvent);
        }

        $scope.more = function () {
            window.indexNum++;
            window.pageNo++;
            $scope.queryUseCouponList();
        };

        // $scope.scanQrCode=function(){
        //     // if(!window.isWechat()){
        //     //     return;
        //     // }
        //     wx.scanQRCode({
        //         needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
        //         scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
        //         success: function (res) {
        //             // 当needResult 为 1 时，扫码返回的结果
        //             // if(res.needResult==1){
        //             //     $scope.couponNo = res.resultStr;
        //             // }
        //             $scope.couponNo = res.resultStr;
        //         },
        //         fail:function(res){
        //             alert(JSON.stringify(res))
        //         }
        //     });
        // }

        // $scope.initWechat=function(){
        //     wx.config({
        //         debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        //         appId: '', // 必填，公众号的唯一标识
        //         timestamp: '', // 必填，生成签名的时间戳
        //         nonceStr: '', // 必填，生成签名的随机串
        //         signature: '',// 必填，签名
        //         jsApiList: [] // 必填，需要使用的JS接口列表
        //     });
        // }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '使用卡券');
                $scope.useCouponList = [];
                $scope.isLoadMore = true;
                window.indexNum = 0;
                window.pageNo = 1;
                window.pageSize = 8;
                $scope.queryUseCouponList();
                $scope.addScrollEvent();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);


